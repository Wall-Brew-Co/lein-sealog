(ns leiningen.sealog.types.changelog
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [com.wallbrew.spoon.core :as spoon]
            [leiningen.sealog.types.changes :as changes]
            [leiningen.sealog.types.common :as types]
            [leiningen.sealog.types.schemes.semver3 :as semver3]
            [spec-tools.core :as st])
  (:import (java.time Instant)))


(defn distinct-versions?
  "Returns true if the given changelog has distinct versions."
  [changelog]
  (let [versions (map :version changelog)]
    (or (empty? versions)
        (apply distinct? versions))))


(defn same-version-type?
  "Returns true if the given changelog has the same version type."
  [changelog]
  (let [version-types (map :version-type changelog)]
    (or (empty? version-types)
        (apply = version-types))))


;; Specs for changelog entires
(spec/def ::version
  (st/spec
    {:type        :map
     :spec        (spec/or :semver3 ::semver3/version)
     :description "A single line of text describing a change."}))


(spec/def ::version-type
  (st/spec
    {:type        :keyword
     :spec        ::types/scheme
     :description "The type of version number."}))


(spec/def ::timestamp
  (st/spec
    {:spec        inst?
     :description "The time the changelog entry was written."}))


(spec/def ::entry
  (st/spec
    {:type        :map
     :spec        (spec/keys :req-un [::version
                                      ::version-type
                                      ::changes/changes
                                      ;; ::timestamp
                                      ])
     :description "The changes for a single version."}))


(spec/def ::changelog
  (st/spec
    {:type        :vector
     :spec        (spec/and (spec/coll-of ::entry :distinct true)
                            distinct-versions?
                            same-version-type?)
     :description "A vector of changelog entries."}))


(defn compare-changelog-versions
  "Compare two changelog versions: `v1` and `v2`."
  [v1 v2]
  (let [v1-type (:version-type v1)
        v2-type (:version-type v2)]
    (if (= v1-type v2-type)
      (case v1-type
        :semver3 (semver3/compare-versions (:version v1) (:version v2)))
      (throw (ex-info "Cannot compare changelog versions of different types."
                      {:v1-type v1-type
                       :v2-type v2-type})))))


(defn sort-changelog-ascending
  "Sort a changelog in ascending order."
  [changelog-sequence]
  (sort compare-changelog-versions changelog-sequence))


(defn sort-changelog-descending
  "Sort a changelog in descending order."
  [changelog-sequence]
  (sort (comp - compare-changelog-versions) changelog-sequence))


(defn max-version
  "Return the maximum version in a changelog."
  [changelog-sequence]
  (assert (seq changelog-sequence))
  (->> changelog-sequence
       sort-changelog-descending
       first))


(defn bump
  "Create a new changelog entry with a bumped version number."
  [changelog-sequence bump-type]
  (let [maximum-version (max-version changelog-sequence)
        version-number  (:version maximum-version)
        bumped-version  (case (:version-type maximum-version)
                          :semver3 (semver3/bump version-number bump-type))]
    {:version      bumped-version
     :version-type (:version-type maximum-version)
     :changes      {changes/added      []
                    changes/changed    []
                    changes/deprecated []
                    changes/removed    []
                    changes/fixed      []
                    changes/security   []}
     :timestamp    (Instant/now)}))


(defn initialize
  "Create a new changelog entry for the initial version."
  [version-type]
  {:version      (case version-type
                   :semver3 (semver3/initialize))
   :version-type version-type
   :changes      {changes/added      []
                  changes/changed    []
                  changes/deprecated []
                  changes/removed    []
                  changes/fixed      []
                  changes/security   []}
   :timestamp    (Instant/now)})


(defn render-version
  "Render a single version as a human-readable string."
  [{:keys [version version-type] :as _change}]
  (case version-type
    :semver3 (semver3/render-version version)))


(defn render-filename
  "Render a changelog-entry's filename."
  [changelog-entry]
  (let [version (render-version changelog-entry)]
    (str (str/replace version #"\." "-") ".edn")))


(defn insert
  "Insert a note of a specified change type into the most current change file."
  [changelog-entry change-type change-notes]
  (let [change-keyword (keyword change-type)]
    (update-in changelog-entry [:changes change-keyword] spoon/concatv change-notes)))
