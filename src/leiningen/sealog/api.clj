(ns leiningen.sealog.api
  "The public API for sealog.

   This namespace contains the public functions that are called by the leiningen"
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.impl :as impl]
            [leiningen.sealog.impl.io :as io]
            [leiningen.sealog.types.changelog :as changelog]
            [leiningen.sealog.types.commands :as commands]))


(defn init
  "Create a new changelog directory."
  [project _options]
  (let [configuration (impl/load-config! project)]
    (if (impl/sealog-configured? project)
      (main/info "Sealog configuration found.")
      (impl/configure! configuration))
    (if (impl/sealog-initialized? configuration)
      (main/info "Sealog is already initialized!")
      (impl/init! configuration))))


(defn render-changelog
  "Render the changelog to the target file."
  [project options]
  (let [configuration    (impl/load-config! project)
        filepath         (or (first options) (:changelog-filename configuration))
        changelog        (impl/load-changelog-entry-directory! configuration)
        sorted-changelog (changelog/sort-changelog-descending changelog)
        changes          (impl/render-changelog sorted-changelog)]
    (io/write-file! filepath changes)
    (main/info (format "Wrote changelog to: %s" filepath))))


(defn bump-version
  "Create a new changelog entry for the version bump."
  [project options]
  (let [configuration       (impl/load-config! project)
        changelog-directory (:changelog-entry-directory configuration)
        bump-type           (keyword (or (first options) "patch"))
        changelog           (impl/load-changelog-entry-directory! configuration)
        new-entry           (update (changelog/bump changelog bump-type) :timestamp str)
        new-file            (str changelog-directory (changelog/render-filename new-entry))]
    (io/write-edn-file! new-file new-entry configuration)
    (println (format "Created new changelog entry: %s" new-file))))


(defn insert-entry
  "Insert a note of a specified change type into the most current change file."
  [project options]
  (let [change-type  (first options)
        change-notes (rest options)]
    (if (commands/valid-insert-command? change-type change-notes)
      (let [configuration           (impl/load-config! project)
            changelog-directory     (:changelog-entry-directory configuration)
            changelog               (impl/load-changelog-entry-directory! configuration)
            latest-changelog-entry  (changelog/max-version changelog)
            updated-changelog-entry (changelog/insert latest-changelog-entry change-type change-notes)
            updated-entry-filename  (str changelog-directory (changelog/render-filename updated-changelog-entry))]
        (io/write-edn-file! updated-entry-filename updated-changelog-entry configuration)
        (main/info (format "Updated changelog entries: %s" updated-entry-filename)))
      (main/exit 1))))


(defn display-version
  "Display information about the current version."
  [project options]
  (let [version-source (first options)]
    (if (commands/valid-version-command? version-source)
      (let [configuration          (impl/load-config! project)
            changelog              (impl/load-changelog-entry-directory! configuration)
            latest-changelog-entry (changelog/max-version changelog)
            sealog-version         (changelog/render-version latest-changelog-entry)
            leiningen-version      (:version project)]
        (case version-source
          "project.clj" (main/info leiningen-version)
          "sealog"      (main/info sealog-version)
          (do (main/info (str "project.clj: " leiningen-version))
              (main/info (str "sealog: " sealog-version)))))
      (main/exit 1))))


(defn check
  "Check the current configuration, changelog entries, and the current project version."
  [project _opts]
  (if (impl/valid-configuration? project)
    (let [configuration                (impl/load-config! project)
          some-changelog-entries?      (impl/changelog-entry-directory-is-not-empty? configuration)
          all-changelog-entries-valid? (impl/changelog-directory-only-contains-valid-files? configuration)
          same-version-type?           (impl/all-changelog-entries-use-same-version-type? configuration)
          distinct-versions?           (impl/all-changelog-entries-have-distinct-versions? configuration)]
      (if (and some-changelog-entries?
               all-changelog-entries-valid?
               same-version-type?
               distinct-versions?)
        (let [versions-match?     (impl/project-version-matches-latest-changelog-entry? project configuration)
              changelog-rendered? (impl/rendered-changelog-contains-all-changelog-entries? configuration)]
          (if (and versions-match? changelog-rendered?)
            (main/info "All checks passed")
            (main/exit 1)))
        (main/exit 1)))
    (main/exit 1)))
