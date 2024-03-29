(defproject com.wallbrew/lein-sealog "1.3.0"
  :description "A Leiningen plugin for managing your changelog."
  :url "https://github.com/Wall-Brew-Co/common-beer-format"
  :license {:name         "MIT"
            :url          "https://opensource.org/licenses/MIT"
            :distribution :repo
            :comments     "Same-as all Wall-Brew projects"}
  :scm {:name "git"
        :url  "https://github.com/Wall-Brew-Co/lein-sealog"}
  :dependencies [[com.wallbrew/spoon "1.2.2"]
                 [java-time-literals/java-time-literals "2018-04-06"]
                 [metosin/spec-tools "0.10.6"]
                 [org.clojure/clojure "1.11.2"]
                 [org.clojure/core.match "1.1.0"]
                 [org.clojure/test.check "1.1.1"]]
  :plugins [[com.github.clj-kondo/lein-clj-kondo "2024.03.13"]
            [com.wallbrew/lein-sealog "1.3.0"]
            [lein-cljsbuild "1.1.8"]
            [lein-project-version "0.1.0"]
            [mvxcvi/cljstyle "0.16.630"]
            [ns-sort "1.0.3"]]
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_user
                                    :password      :env/clojars_pass
                                    :sign-releases false}]]
  :deploy-branches ["master"]
  :eval-in-leiningen true)
