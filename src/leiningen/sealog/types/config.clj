(ns leiningen.sealog.types.config
  (:require [clojure.spec.alpha :as spec]
            [leiningen.sealog.types.common :as types]
            [spec-tools.core :as st]))


(def ^:const config-file
  "The default name of the configuration file."
  ".sealog/config.edn")


(def ^:const backup-config-file
  "The secondary name of the configuration file."
  ".wallbrew/sealog/config.edn")


(spec/def ::changelog-filename
  (st/spec
    {:type        :string
     :spec        ::types/text
     :description "The name of the rendered Changelog file."}))


(spec/def ::changelog-entry-directory
  (st/spec
    {:type        :string
     :spec        ::types/text
     :description "The name of the directory containing the changelog entries."}))


(spec/def ::version-scheme
  (st/spec
    {:type        :keyword
     :gen         #(spec/gen types/scheme-set)
     :spec        ::types/scheme
     :description "The versioning scheme to use."}))


(spec/def ::pretty-print-edn?
  (st/spec
    {:type        :boolean
     :description "Whether or not to pretty print the EDN files."}))

(spec/def ::remove-commas-in-change-files?
  (st/spec
   {:type :boolean
    :description "Wether or not the map delimiter commas inserted by clojure.pprint should be removed when creating a new change file."}))


(spec/def ::config
  (st/spec
    {:type        :map
     :spec        (spec/keys :opt-un [::changelog-filename
                                      ::changelog-entry-directory
                                      ::version-scheme
                                      ::pretty-print-edn?
                                      ::remove-commas-in-change-files?])
     :description "The configuration for Sealog."}))


(def default-config
  "The default configuration for Sealog."
  {:changelog-filename             "CHANGELOG.md"
   :changelog-entry-directory      ".sealog/changes/"
   :version-scheme                 :semver3
   :pretty-print-edn?              false
   :remove-commas-in-change-files? false})
