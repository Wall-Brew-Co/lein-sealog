(ns leiningen.sealog.types.config
  (:require [clojure.spec.alpha :as spec]
            [leiningen.sealog.types.common :as types]
            [spec-tools.core :as st]))


(def ^:const config-file
  "The default name of the configuration file."
  ".sealog/config.edn")


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


(spec/def ::config
  (st/spec
    {:type        :map
     :spec        (spec/keys :opt-un [::changelog-filename
                                      ::changelog-entry-directory
                                      ::version-scheme])
     :description "The configuration for Sealog."}))


(def default-config
  "The default configuration for Sealog."
  {:changelog-filename        "CHANGELOG.md"
   :changelog-entry-directory ".sealog/changes/"
   :version-scheme            :semver3})
