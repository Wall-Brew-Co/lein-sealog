(ns leiningen.sealog.types.changes
  "Specs and functions for changelogs as described by [Keep A Changelog](https://keepachangelog.com/en/1.0.0/)."
  (:require [clojure.spec.alpha :as s]
            [leiningen.sealog.types.common :as types]
            [spec-tools.core :as st]))


;; Specs for changelog types
(s/def ::change-note
  (st/spec
    {:type        :string
     :spec        ::types/text
     :description "A single line of text describing a change."}))


(s/def ::added
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes representing new features."}))


(s/def ::changed
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes representing changes to existing features."}))


(s/def ::deprecated
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes for soon-to-be removed features."}))


(s/def ::removed
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes for removed features."}))


(s/def ::fixed
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes for bug fixes."}))


(s/def ::security
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes for security fixes."}))


(s/def ::misc
  (st/spec
    {:type        :vector
     :spec        (s/coll-of ::change-note :distinct true)
     :description "A vector of change notes for miscellaneous notifications"}))


(s/def ::changes
  (st/spec
    {:type        :map
     :spec        (s/keys :opt-un [::added
                                   ::changed
                                   ::deprecated
                                   ::removed
                                   ::fixed
                                   ::security ::misc])
     :description "A map of change types to vectors of change notes."}))
