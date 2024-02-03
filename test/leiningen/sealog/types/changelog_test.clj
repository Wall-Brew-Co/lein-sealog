(ns leiningen.sealog.types.changelog-test
  (:require [clojure.spec.alpha :as spec]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :as check.test]
            [clojure.test.check.properties :as prop]
            [leiningen.sealog.test-util :as test-util]
            [leiningen.sealog.types.changelog :as sut]))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (test-util/generatable? ::sut/version))
    (is (test-util/generatable? ::sut/version-type))
    (is (test-util/generatable? ::sut/timestamp))
    (is (test-util/generatable? ::sut/entry))
    (is (test-util/generatable? ::sut/changelog))))


;; Static tests

(deftest distinct-versions?-test
  (testing "Returns true if the given changelog has distinct versions."
    (is (sut/distinct-versions? [{:version {:major 1 :minor 2 :patch 3}}
                                 {:version {:major 1 :minor 2 :patch 4}}]))
    (is (sut/distinct-versions? [])
        "This function is vacuously true for empty changelogs.")
    (is (not (sut/distinct-versions? [{:version {:major 1 :minor 2 :patch 3}}
                                      {:version {:major 1 :minor 2 :patch 3}}])))))


(deftest same-version-type?-test
  (testing "Returns true if the given changelog has the same version type."
    (is (sut/same-version-type? [{:version-type :semver3}
                                 {:version-type :semver3}]))
    (is (sut/same-version-type? [])
        "This function is vacuously true for empty changelogs.")
    (is (not (sut/same-version-type? [{:version-type :semver3}
                                      {:version-type :semver4}])))))


(deftest compare-changelog-versions-test
  (testing "compare-changelog-versions returns 1 if `v1` is a newer version than `v2`"
    (is (= 1 (sut/compare-changelog-versions {:version      {:major 1
                                                             :minor 2
                                                             :patch 3}
                                              :version-type :semver3}
                                             {:version      {:major 1
                                                             :minor 2
                                                             :patch 2}
                                              :version-type :semver3}))
        "Patch version is greater in v1")
    (is (= 1 (sut/compare-changelog-versions {:version      {:major 1
                                                             :minor 3
                                                             :patch 2}
                                              :version-type :semver3}
                                             {:version      {:major 1
                                                             :minor 2
                                                             :patch 2}
                                              :version-type :semver3}))
        "Minor version is greater in v1")
    (is (= 1 (sut/compare-changelog-versions {:version      {:major 2
                                                             :minor 2
                                                             :patch 2}
                                              :version-type :semver3}
                                             {:version      {:major 1
                                                             :minor 2
                                                             :patch 2}
                                              :version-type :semver3}))
        "Major version is greater in v1"))
  (testing "compare-changelog-versions returns -1 if `v1` is an older version than `v2`"
    (is (= -1 (sut/compare-changelog-versions {:version      {:major 1
                                                              :minor 2
                                                              :patch 3}
                                               :version-type :semver3}
                                              {:version      {:major 1
                                                              :minor 2
                                                              :patch 4}
                                               :version-type :semver3}))
        "Patch version is greater in v2")
    (is (= -1 (sut/compare-changelog-versions {:version      {:major 1
                                                              :minor 3
                                                              :patch 2}
                                               :version-type :semver3}
                                              {:version      {:major 1
                                                              :minor 4
                                                              :patch 2}
                                               :version-type :semver3}))
        "Minor version is greater in v2")
    (is (= -1 (sut/compare-changelog-versions {:version      {:major 2
                                                              :minor 2
                                                              :patch 2}
                                               :version-type :semver3}
                                              {:version      {:major 3
                                                              :minor 2
                                                              :patch 2}
                                               :version-type :semver3}))
        "Major version is greater in v2"))
  (testing "compare-changelog-versions returns 0 if `v1` is the same version as `v2`"
    (is (= 0 (sut/compare-changelog-versions {:version      {:major 1
                                                             :minor 2
                                                             :patch 3}
                                              :version-type :semver3}
                                             {:version      {:major 1
                                                             :minor 2
                                                             :patch 3}
                                              :version-type :semver3}))
        "Patch version is equal in v1 and v2")))


(deftest sort-changelog-ascending-test
  (testing "sort-changelog-ascending sorts the changelog in ascending order"
    (is (= [{:version      {:major 1
                            :minor 2
                            :patch 3}
             :version-type :semver3}
            {:version      {:major 1
                            :minor 2
                            :patch 4}
             :version-type :semver3}]
           (sut/sort-changelog-ascending [{:version      {:major 1
                                                          :minor 2
                                                          :patch 4}
                                           :version-type :semver3}
                                          {:version      {:major 1
                                                          :minor 2
                                                          :patch 3}
                                           :version-type :semver3}])))
    (is (= [] (sut/sort-changelog-ascending [])))
    (is (= [{:version      {:major 1
                            :minor 2
                            :patch 3}
             :version-type :semver3}
            {:version      {:major 1
                            :minor 2
                            :patch 4}
             :version-type :semver3}
            {:version      {:major 1
                            :minor 2
                            :patch 5}
             :version-type :semver3}]
           (sut/sort-changelog-ascending [{:version      {:major 1
                                                          :minor 2
                                                          :patch 5}
                                           :version-type :semver3}
                                          {:version      {:major 1
                                                          :minor 2
                                                          :patch 4}
                                           :version-type :semver3}
                                          {:version      {:major 1
                                                          :minor 2
                                                          :patch 3}
                                           :version-type :semver3}])))))


(deftest sort-changelog-descending-test
  (testing "sort-changeog-descending sorts the changelog in descending order"
    (is (= [{:version      {:major 1
                            :minor 2
                            :patch 4}
             :version-type :semver3}
            {:version      {:major 1
                            :minor 2
                            :patch 3}
             :version-type :semver3}]
           (sut/sort-changelog-descending [{:version      {:major 1
                                                           :minor 2
                                                           :patch 4}
                                            :version-type :semver3}
                                           {:version      {:major 1
                                                           :minor 2
                                                           :patch 3}
                                            :version-type :semver3}])))
    (is (= [] (sut/sort-changelog-descending [])))
    (is (= [{:version      {:major 1
                            :minor 2
                            :patch 5}
             :version-type :semver3}
            {:version      {:major 1
                            :minor 2
                            :patch 4}
             :version-type :semver3}
            {:version      {:major 1
                            :minor 2
                            :patch 3}
             :version-type :semver3}]
           (sut/sort-changelog-descending [{:version      {:major 1
                                                           :minor 2
                                                           :patch 5}
                                            :version-type :semver3}
                                           {:version      {:major 1
                                                           :minor 2
                                                           :patch 4}
                                            :version-type :semver3}
                                           {:version      {:major 1
                                                           :minor 2
                                                           :patch 3}
                                            :version-type :semver3}])))))


;; Property-based tests

(declare compare-changelog-versions-range
         sort-changelog-ascending-idempotentcy-test
         sort-descending-idempotentcy-test
         sort-reversal-test)


(check.test/defspec
  compare-changelog-versions-range 100
  (prop/for-all
    [v1 (spec/gen ::sut/entry)
     v2 (spec/gen ::sut/entry)]
    (#{-1 0 1} (sut/compare-changelog-versions v1 v2))))


(check.test/defspec
  sort-changelog-ascending-idempotentcy-test 100
  (prop/for-all
    [entrys (spec/gen (spec/coll-of ::sut/entry))]
    (= (sut/sort-changelog-ascending entrys)
       (sut/sort-changelog-ascending (sut/sort-changelog-ascending entrys)))))


(check.test/defspec
  sort-descending-idempotentcy-test 100
  (prop/for-all
    [entrys (spec/gen (spec/coll-of ::sut/entry))]
    (= (sut/sort-changelog-descending entrys)
       (sut/sort-changelog-descending (sut/sort-changelog-descending entrys)))))


(check.test/defspec
  sort-reversal-test 100
  (prop/for-all
    [entrys (spec/gen (spec/coll-of ::sut/entry))]
    (= (-> entrys sut/sort-changelog-ascending first :version)
       (-> entrys sut/sort-changelog-descending last :version))))
