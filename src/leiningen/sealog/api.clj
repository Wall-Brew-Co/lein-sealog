(ns leiningen.sealog.api
  "The public API for sealog.

   This namespace contains the public functions that are called by the leiningen"
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.impl :as impl]
            [leiningen.sealog.types.changelog :as changelog]
            [leiningen.sealog.types.commands :as commands]))


(defn init
  "Create a new changelog directory."
  [_opts]
  (let [configuration (impl/load-config!)]
    (if (impl/sealog-configured?)
      (main/info "Sealog configuration found.")
      (impl/configure! configuration))
    (if (impl/sealog-initialized? configuration)
      (main/info "Sealog is already initialized!")
      (impl/init! configuration))))


(defn render-changelog
  "Render the changelog to the target file."
  [opts]
  (let [configuration    (impl/load-config!)
        filepath         (or (first opts) (:changelog-filename configuration))
        changelog        (impl/load-changelog-entry-directory! configuration)
        sorted-changelog (changelog/sort-changelog-descending changelog)
        changes          (impl/render-changelog sorted-changelog)]
    (impl/write-file! filepath changes)
    (main/info (format "Wrote changelog to: %s" filepath))))


(defn bump-version
  "Create a new changelog entry for the version bump."
  [opts]
  (let [configuration       (impl/load-config!)
        changelog-directory (:changelog-entry-directory configuration)
        bump-type           (keyword (or (first opts) "patch"))
        changelog           (impl/load-changelog-entry-directory! configuration)
        new-entry           (update (changelog/bump changelog bump-type) :timestamp str)
        new-file            (str changelog-directory (changelog/render-filename new-entry))]
    (impl/write-edn-file! new-file new-entry configuration)
    (println (format "Created new changelog entry: %s" new-file))))


(defn insert-entry
  "Insert a note of a specified change type into the most current change file."
  [opts]
  (let [change-type  (first opts)
        change-notes (rest opts)]
    (if (commands/valid-insert-command? change-type change-notes)
      (let [configuration           (impl/load-config!)
            changelog-directory     (:changelog-entry-directory configuration)
            changelog               (impl/load-changelog-entry-directory! configuration)
            latest-changelog-entry  (changelog/max-version changelog)
            updated-changelog-entry (changelog/insert latest-changelog-entry change-type change-notes)
            updated-entry-filename  (str changelog-directory (changelog/render-filename updated-changelog-entry))]
        (impl/write-edn-file! updated-entry-filename updated-changelog-entry configuration)
        (println (format "Updated changelog entries: %s" updated-entry-filename)))
      (System/exit 1))))

(defn display-version
  "Display information about the current version."
  [project opts]
  (let [version-source (first opts)]
    (if (commands/valid-version-command? version-source)
      (let [configuration          (impl/load-config!)
            changelog              (impl/load-changelog-entry-directory! configuration)
            latest-changelog-entry (changelog/max-version changelog)
            sealog-version         (changelog/render-version latest-changelog-entry)
            leiningen-version      (:version project)]
        (case version-source
          "project.clj" (main/info leiningen-version)
          "sealog"      (main/info sealog-version)
          (do (main/info (str "project.clj: " leiningen-version))
              (main/info (str "sealog: " sealog-version)))))
      (System/exit 1))))
