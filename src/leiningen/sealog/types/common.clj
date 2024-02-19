(ns leiningen.sealog.types.common
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [spec-tools.core :as st]))


(def not-blank?
  "A predicate that returns true if the given string is not blank."
  (complement str/blank?))


(spec/def ::text
  (st/spec
    {:type        :string
     :spec        (spec/and string? not-blank?)
     :description "A single line of non-blank text."}))


(def ^:const schemes
  "The types of version bumps that are supported by sealog."
  {:semver3 {:title "Semantic Versioning"
             :link  "https://semver.org/spec/v2.0.0.html"}})


(def ^:const scheme-set
  "The set of supported version bump types."
  (set (keys schemes)))


(spec/def ::scheme
  (st/spec
    {:type        :keyword
     :gen         #(spec/gen scheme-set)
     :spec        scheme-set
     :description "The type of version number."}))
