(ns leiningen.sealog.types.config-test
  (:require [clojure.test :refer :all]
            [leiningen.sealog.test-util :as test-util]
            [leiningen.sealog.types.config :as sut]))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (test-util/generatable? ::sut/changelog-filename))
    (is (test-util/generatable? ::sut/changelog-entry-directory))
    (is (test-util/generatable? ::sut/default-scheme))
    (is (test-util/generatable? ::sut/config))))
