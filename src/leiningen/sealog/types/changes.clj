(ns leiningen.sealog.types.changes
  "Specs and functions for changelogs as described by [Keep A Changelog](https://keepachangelog.com/en/1.0.0/)."
  (:require [clojure.spec.alpha :as spec]
            [leiningen.sealog.types.common :as types]
            [spec-tools.core :as st]))


;; Specs for changelog types
(spec/def ::change-note
  (st/spec
    {:type        :string
     :spec        ::types/text
     :description "A single line of text describing a change."}))


(def added
  "A symbolic keyword representing the added change type."
  :added)


(spec/def ::added
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes representing new features."}))


(def changed
  "A symbolic keyword representing the changed change type."
  :changed)


(spec/def ::changed
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes representing changes to existing features."}))


(def deprecated
  "A symbolic keyword representing the deprecated change type."
  :deprecated)


(spec/def ::deprecated
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for soon-to-be removed features."}))


(def removed
  "A symbolic keyword representing the removed change type."
  :removed)


(spec/def ::removed
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for removed features."}))


(def fixed
  "A symbolic keyword representing the fixed change type."
  :fixed)


(spec/def ::fixed
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for bug fixes."}))


(def security
  "A symbolic keyword representing the security change type."
  :security)


(spec/def ::security
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for security fixes."}))


(def ^:const change-types
  "The set of supported change types."
  #{added changed deprecated removed fixed security})


(spec/def ::changes
  (st/spec
    {:type        :map
     :spec        (spec/keys :opt-un [::added
                                      ::changed
                                      ::deprecated
                                      ::removed
                                      ::fixed
                                      ::security])
     :description "A map of change types to vectors of change notes."}))
