(ns leiningen.sealog.types.changes-test
  (:require [clojure.test :refer :all]
            [leiningen.sealog.test-util :as test-util]
            [leiningen.sealog.types.changes :as sut]))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (test-util/generatable? ::sut/change-note))
    (is (test-util/generatable? ::sut/added))
    (is (test-util/generatable? ::sut/changed))
    (is (test-util/generatable? ::sut/deprecated))
    (is (test-util/generatable? ::sut/removed))
    (is (test-util/generatable? ::sut/fixed))
    (is (test-util/generatable? ::sut/security))
    (is (test-util/generatable? ::sut/misc))
    (is (test-util/generatable? ::sut/changes))))
