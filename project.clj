(defproject com.wallbrew/lein-sealog "1.0.2"
  :description "A Leiningen plugin for managing your changelog."
  :url "https://github.com/Wall-Brew-Co/common-beer-format"
  :license {:name         "MIT"
            :url          "https://opensource.org/licenses/MIT"
            :distribution :repo
            :comments     "Same-as all Wall-Brew projects"}
  :scm {:name "git"
        :url  "https://github.com/Wall-Brew-Co/lein-sealog"}
  :dependencies [[java-time-literals/java-time-literals "2018-04-06"]
                 [metosin/spec-tools "0.10.5"]
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/core.match "1.0.1"]
                 [org.clojure/test.check "1.1.1"]]
  :plugins [[com.wallbrew/lein-sealog "1.0.2"]
            [lein-project-version "0.1.0"]]
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_user
                                    :password      :env/clojars_pass
                                    :sign-releases false}]]
  :deploy-branches ["master"]
  :eval-in-leiningen true)
