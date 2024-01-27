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


(spec/def ::added
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes representing new features."}))


(spec/def ::changed
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes representing changes to existing features."}))


(spec/def ::deprecated
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for soon-to-be removed features."}))


(spec/def ::removed
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for removed features."}))


(spec/def ::fixed
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for bug fixes."}))


(spec/def ::security
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for security fixes."}))


(spec/def ::misc
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::change-note :distinct true)
     :description "A vector of change notes for miscellaneous notifications"}))


(spec/def ::changes
  (st/spec
    {:type        :map
     :spec        (spec/keys :opt-un [::added
                                      ::changed
                                      ::deprecated
                                      ::removed
                                      ::fixed
                                      ::security ::misc])
     :description "A map of change types to vectors of change notes."}))
