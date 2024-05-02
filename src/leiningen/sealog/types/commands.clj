(ns leiningen.sealog.types.commands
  "Specs that define the commands for sealog and their options."
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [leiningen.core.main :as main]
            [leiningen.sealog.types.changes :as changes]
            [spec-tools.core :as st]))


(def ^:const insert-types
  "The allowed insert types for the insert command."
  (set (map name changes/change-types)))


(spec/def ::insert-change-type
  (st/spec
    {:type        :string
     :spec        insert-types
     :gen        #(spec/gen insert-types)
     :description "The type of change to insert a note for."}))


(spec/def ::insert-change-notes
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::changes/change-note :distinct true :min-count 1)
     :description "A single line of text describing the change."}))


(defn valid-insert-command?
  "Validate the options for the insert command.
   If the options are valid, return true.
   Otherwise, print an error message and return false."
  [change-type change-notes]
  (if (spec/valid? ::insert-change-type change-type)
    (if (spec/valid? ::insert-change-notes change-notes)
      true
      (do (main/warn "Invalid change notes: " change-notes
                     "\nChange notes must be a non-empty vector of distinct strings.")
          false))
    (do (main/warn "Invalid change type: " change-type
                   "\nValid change types: " (str/join ", " insert-types))
        false)))


(def valid-version-sources
  "The allowed sources for the version command."
  #{"project.clj" "sealog"})


(spec/def ::version-source
  (st/spec
    {:type        :string
     :spec        valid-version-sources
     :gen         #(spec/gen valid-version-sources)
     :description "The source of truth for the current version number."}))


(defn valid-version-command?
  "Validate the options for the version command.
   If the options are valid, return true.
   Otherwise, print an error message and return false."
  [version-source]
  (if (or (nil? version-source)
          (spec/valid? ::version-source version-source))
    true
    (do (main/warn "Invalid source type: " version-source
                   "\nValid source types: " (str/join ", " valid-version-sources))
        false)))
