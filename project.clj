(defproject lein-sealog "1.0.0"
  :description "A Leiningen plugin for managing your changelog."
  :url "https://github.com/Wall-Brew-Co/common-beer-format"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :scm {:name "git"
        :url  "https://github.com/Wall-Brew-Co/sealog"}
  :dependencies [[java-time-literals/java-time-literals "2018-04-06"]
                 [metosin/spec-tools "0.10.5"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/core.match "1.0.0"]
                 [org.clojure/test.check "1.1.1"]]
  :eval-in-leiningen true)
