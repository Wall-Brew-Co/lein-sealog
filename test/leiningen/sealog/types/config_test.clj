(ns leiningen.sealog.types.config-test
  (:require [clojure.test :refer :all]
            [com.wallbrew.spoon.spec :as spoon.spec]
            [leiningen.sealog.test-util :as test-util]
            [leiningen.sealog.types.config :as sut]))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (test-util/generatable? ::sut/changelog-filename))
    (is (test-util/generatable? ::sut/changelog-entry-directory))
    (is (test-util/generatable? ::sut/version-scheme))
    (is (test-util/generatable? ::sut/config))))


(deftest default-scheme-assertion-test
  (testing "The default configuration is a valid config"
    (is (spoon.spec/test-valid? ::sut/config sut/default-config))))
