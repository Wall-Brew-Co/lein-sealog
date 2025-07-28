(defproject com.wallbrew/lein-sealog "1.9.0"
  :description "A Leiningen plugin for managing your changelog."
  :url "https://github.com/Wall-Brew-Co/lein-sealog"
  :license {:name         "MIT"
            :url          "https://opensource.org/licenses/MIT"
            :distribution :repo
            :comments     "Same-as all Wall-Brew projects"}
  :scm {:name "git"
        :url  "https://github.com/Wall-Brew-Co/lein-sealog"}
  :pom-addition [:organization
                 [:name "Wall Brew Co."]
                 [:url "https://wallbrew.com"]]
  :dependencies [[com.wallbrew/spoon "1.5.0"]
                 [java-time-literals/java-time-literals "2018-04-06"]
                 [metosin/spec-tools "0.10.7"]
                 [org.clojure/clojure "1.12.1"]]
  :plugins [[com.github.clj-kondo/lein-clj-kondo "2025.07.28"]
            [com.wallbrew/bouncer "1.2.0"]
            [com.wallbrew/lein-sealog "1.9.0"]
            [mvxcvi/cljstyle "0.17.642"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.1"]]}}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_user
                                    :password      :env/clojars_pass
                                    :sign-releases false}]]
  :deploy-branches ["master"]
  :eval-in-leiningen true)
