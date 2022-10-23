(ns leiningen.sealog.types.schemes.semver3
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [spec-tools.core :as st]))


;; Specs for semver-3 version types

(def ^:const bump-types
  "The types of version bumps that are supported by this scheme."
  #{:major :minor :patch})


(s/def ::bump-type
  (st/spec
    {:type        :keyword
     :spec        bump-types
     :gen         #(s/gen bump-types)
     :description "The type of version bump to perform. Valid values are: :major, :minor, :patch."}))


(s/def ::major
  (st/spec
    {:type        :long
     :spec        nat-int?
     :description "The major version number."}))


(s/def ::minor
  (st/spec
    {:type        :long
     :spec        nat-int?
     :description "The minor version number."}))


(s/def ::patch
  (st/spec
    {:type        :long
     :spec        nat-int?
     :description "The patch version number."}))


(s/def ::version
  (st/spec
    {:type        :map
     :spec        (s/keys :req-un [::major ::minor ::patch])
     :description "A map containing the major, minor, and patch version numbers."}))


(defn compare-versions
  "Compare two semver-3 versions: `v1` and `v2`."
  [v1 v2]
  (let [s1-major (:major v1)
        s1-minor (:minor v1)
        s1-patch (:patch v1)
        s2-major (:major v2)
        s2-minor (:minor v2)
        s2-patch (:patch v2)]
    (cond
      (> s1-major s2-major) 1
      (< s1-major s2-major) -1
      (> s1-minor s2-minor) 1
      (< s1-minor s2-minor) -1
      (> s1-patch s2-patch) 1
      (< s1-patch s2-patch) -1
      :else 0)))


(defn sort-versions-ascending
  "Sort a sequence of semver-3 versions into ascending order."
  [version-sequence]
  (sort compare-versions version-sequence))


(defn sort-versions-descending
  "Sort a sequence of semver-3 versions into descending order."
  [version-sequence]
  (sort (comp - compare-versions) version-sequence))


(defn render-version
  "Render a semver-3 version as a human-readable string."
  [{:keys [major minor patch]}]
  (str/join "." [major minor patch]))


(defn parse-version
  "Parse a semver-3 version from a string."
  [version-string]
  (let [version-parts (str/split version-string #"\.")
        major         (Long/parseLong (nth version-parts 0))
        minor         (Long/parseLong (nth version-parts 1))
        patch         (Long/parseLong (nth version-parts 2))]
    {:major major
     :minor minor
     :patch patch}))


(defn bump
  "Bump a semver-3 version by a given type."
  [{:keys [major minor patch]} bump-type]
  (case bump-type
    :major {:major (inc major)
            :minor 0
            :patch 0}
    :minor {:major major
            :minor (inc minor)
            :patch 0}
    :patch {:major major
            :minor minor
            :patch (inc patch)}))


(defn initialize
  "Initialize a semver-3 version."
  []
  {:major 1
   :minor 0
   :patch 0})
