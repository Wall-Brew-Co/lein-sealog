(ns leiningen.sealog.impl-test
  (:require [clojure.spec.alpha :as spec]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :as check.test]
            [clojure.test.check.properties :as prop]
            [leiningen.sealog.impl :as sut]
            [leiningen.sealog.types.changelog :as changelog]))


(deftest concatv-test
  (testing "Concatv returns a vector"
    (is (= [] (sut/concatv))
        "Like concat, concatv returns an empty vector when given no arguments")
    (is (= [1 2 3] (sut/concatv (list 1 2 3)))
        "Concatv casts single collevtions to a vector")
    (is (= [1 2 3] (sut/concatv [1 2 3]))
        "Concatv casts single collevtions to a vector")
    (is (= [1 2 3 4 5 6] (sut/concatv [1 2 3] [4 5 6]))
        "Concatv concatenates multiple collections into a vector")
    (is (= [1 2 3 4 5 6] (sut/concatv [1 2 3] (list 4 5 6)))
        "Concatv concatenates multiple collections into a vector")))


(deftest static-text-tests
  (testing "render-preamble returns a collection of strings"
    (is (every? string? (sut/render-preamble))))
  (testing "render-footer returns a collection of strings"
    (is (every? string? (sut/render-footer)))))


;; Property-based tests

(declare render-changes-sequence-of-strings
         render-changelog-string)

(check.test/defspec
  render-changes-sequence-of-strings 100
  (prop/for-all
    [changes (spec/gen ::changelog/changelog)]
    (every? string? (sut/render-changes changes))))


(check.test/defspec
  render-changelog-string 100
  (prop/for-all
    [changes (spec/gen ::changelog/changelog)]
    (string? (sut/render-changelog changes))))
