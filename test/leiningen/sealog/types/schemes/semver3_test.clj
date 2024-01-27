(ns leiningen.sealog.types.schemes.semver3-test
  (:require [clojure.spec.alpha :as spec]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :as check.test]
            [clojure.test.check.properties :as prop]
            [leiningen.sealog.types.schemes.semver3 :as sut]))


(defn generatable?
  "Attempts to generate a value for spec and returns true if it succeeds."
  {:added  "1.3"
   :no-doc true}
  [spec]
  (try
    (every? #(spec/valid? spec %) (gen/sample (spec/gen spec)))
    (catch Exception e
      (println (str "Failed to generate a value for spec: " spec))
      (println e)
      false)))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (generatable? ::sut/bump-type))
    (is (generatable? ::sut/major))
    (is (generatable? ::sut/minor))
    (is (generatable? ::sut/patch))
    (is (generatable? ::sut/version))))


(deftest compare-versions-test
  (testing "compare-versions returns 1 if `v1` is a newer version than `v2`"
    (is (= 1 (sut/compare-versions {:major 1 :minor 2 :patch 3}
                                   {:major 1 :minor 2 :patch 2}))
        "Patch version is greater in v1")
    (is (= 1 (sut/compare-versions {:major 1 :minor 3 :patch 2}
                                   {:major 1 :minor 2 :patch 2}))
        "Minor version is greater in v1")
    (is (= 1 (sut/compare-versions {:major 2 :minor 2 :patch 2}
                                   {:major 1 :minor 2 :patch 2}))
        "Major version is greater in v1"))
  (testing "compare-versions returns -1 if `v1` is an older version than `v2`"
    (is (= -1 (sut/compare-versions {:major 1 :minor 2 :patch 2}
                                    {:major 1 :minor 2 :patch 3}))
        "Patch version is greater in v2")
    (is (= -1 (sut/compare-versions {:major 1 :minor 2 :patch 2}
                                    {:major 1 :minor 3 :patch 2}))
        "Minor version is greater in v2")
    (is (= -1 (sut/compare-versions {:major 1 :minor 2 :patch 2}
                                    {:major 2 :minor 2 :patch 2}))
        "Major version is greater in v2"))
  (testing "compare-versions returns 0 if `v1` is the same version as `v2`"
    (is (= 0 (sut/compare-versions {:major 1 :minor 2 :patch 3}
                                   {:major 1 :minor 2 :patch 3}))
        "All version numbers are equal")))


(deftest sort-versions-*-test
  (testing "sort-versions sorts a sequence of versions from oldest to newest"
    (is (= []
           (sut/sort-versions-ascending [])
           (sut/sort-versions-descending [])))
    (is (= [{:major 1 :minor 2 :patch 1}
            {:major 1 :minor 2 :patch 2}
            {:major 1 :minor 2 :patch 3}]
           (sut/sort-versions-ascending [{:major 1 :minor 2 :patch 2}
                                         {:major 1 :minor 2 :patch 1}
                                         {:major 1 :minor 2 :patch 3}])
           (reverse (sut/sort-versions-descending [{:major 1 :minor 2 :patch 2}
                                                   {:major 1 :minor 2 :patch 1}
                                                   {:major 1 :minor 2 :patch 3}]))))))


(deftest render-version-test
  (testing "render-version renders a version as a string"
    (is (= "1.2.3" (sut/render-version {:major 1 :minor 2 :patch 3})))))


(deftest parse-version-test
  (testing "parse-version parses a version string into a version map"
    (is (= {:major 1 :minor 2 :patch 3}
           (sut/parse-version "1.2.3")))))


(deftest bump-test
  (testing "Bumps the version according to the bump type"
    (is (= {:major 1 :minor 2 :patch 4}
           (sut/bump {:major 1 :minor 2 :patch 3} :patch)))
    (is (= {:major 1 :minor 3 :patch 0}
           (sut/bump {:major 1 :minor 2 :patch 3} :minor)))
    (is (= {:major 2 :minor 0 :patch 0}
           (sut/bump {:major 1 :minor 2 :patch 3} :major)))))


;; Property-based tests

(declare compare-versions-range)

(check.test/defspec
  compare-versions-range 100
  (prop/for-all
    [v1 (spec/gen ::sut/version)
     v2 (spec/gen ::sut/version)]
    (#{-1 0 1} (sut/compare-versions v1 v2))))


(check.test/defspec
  sort-ascending-idempotentcy-test 100
  (prop/for-all
    [versions (spec/gen (spec/coll-of ::sut/version))]
    (= (sut/sort-versions-ascending versions)
       (sut/sort-versions-ascending (sut/sort-versions-ascending versions)))))


(check.test/defspec
  sort-descending-idempotentcy-test 100
  (prop/for-all
    [versions (spec/gen (spec/coll-of ::sut/version))]
    (= (sut/sort-versions-descending versions)
       (sut/sort-versions-descending (sut/sort-versions-descending versions)))))


(check.test/defspec
  sort-reversal-test 100
  (prop/for-all
    [versions (spec/gen (spec/coll-of ::sut/version))]
    (= (sut/sort-versions-ascending versions)
       (reverse (sut/sort-versions-descending versions)))))


(check.test/defspec
  render-reversal-test 100
  (prop/for-all
    [version (spec/gen ::sut/version)]
    (= version
       (sut/parse-version (sut/render-version version)))))


(check.test/defspec
  bump-ordering-test 100
  (prop/for-all
    [version (spec/gen ::sut/version)
     bump-type (spec/gen ::sut/bump-type)]
    (= 1 (sut/compare-versions (sut/bump version bump-type) version))))
