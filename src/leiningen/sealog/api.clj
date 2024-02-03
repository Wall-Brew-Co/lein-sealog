(ns leiningen.sealog.api
  "The public API for sealog.

   This namespace contains the public functions that are called by the leiningen"
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.impl :as impl]
            [leiningen.sealog.types.changelog :as changelog]))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}


(defn init
  "Create a new changelog directory."
  [_opts]
  (let [configuration (impl/load-config!)]
    (if (impl/sealog-initialized? configuration)
      (main/info "Sealog is already initialized!")
      (impl/init! configuration))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}


(defn render-changelog
  "Render the changelog to the target file."
  [opts]
  (let [configuration    (impl/load-config!)
        filepath         (or (first opts) (:changelog-filename configuration))
        changelog        (impl/load-changelog-entry-directory! configuration)
        sorted-changelog (changelog/sort-changelog-descending changelog)
        changes          (impl/render-changelog sorted-changelog)]
    (spit filepath changes)
    (main/info (format "Wrote changelog to: %s" filepath))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}


(defn bump-version
  "Create a new changelog entry for the version bump."
  [opts]
  (let [configuration       (impl/load-config!)
        changelog-directory (:changelog-entry-directory configuration)
        bump-type           (keyword (or (first opts) "patch"))
        changelog           (impl/load-changelog-entry-directory! configuration)
        new-entry           (update (changelog/bump changelog bump-type) :timestamp str)
        new-file            (str changelog-directory (changelog/render-filename new-entry))]
    (spit new-file new-entry)
    (println (format "Created new changelog entry: %s" new-file))))
