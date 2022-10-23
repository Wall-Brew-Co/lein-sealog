(ns leiningen.sealog.api
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.config :as config]
            [leiningen.sealog.impl :as impl]
            [leiningen.sealog.types.changelog :as changelog]))


(defn init
  "Create a new changelog directory."
  [_opts]
  (if (impl/sealog-initialized?)
    (main/info "Sealog is already initialized!")
    (impl/init!)))


(defn render-changelog
  "Render the changelog to the target file."
  [opts]
  (let [filepath         (or (first opts) config/changelog-filename)
        changelog        (impl/load-changelog-directory!)
        sorted-changelog (changelog/sort-changelog-descending changelog)
        changes          (impl/render-changelog sorted-changelog)]
    (spit filepath changes)
    (main/info (format "Wrote changelog to: %s" filepath))))


(defn bump-version
  "Create a new changelog entry for the version bump."
  [opts]
  (let [bump-type (keyword (or (first opts) "patch"))
        changelog (impl/load-changelog-directory!)
        new-entry (update (changelog/bump changelog bump-type) :timestamp str)
        new-file  (str config/changelog-directory (changelog/render-filename new-entry))]
    (spit new-file new-entry)
    (println (format "Created new changelog entry: %s" new-file))))
