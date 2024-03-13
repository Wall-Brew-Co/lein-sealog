(ns leiningen.sealog.types.commands-test
  (:require [clojure.test :refer :all]
            [leiningen.sealog.test-util :as test-util]
            [leiningen.sealog.types.changes :as changes]
            [leiningen.sealog.types.commands :as sut]))

(deftest change-types-test
  (testing "The insert types are the same as the change types"
    (is (= (set (map keyword sut/insert-types)) changes/change-types))
    (is (every? string? sut/insert-types))))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (test-util/generatable? ::sut/insert-change-type))
    (is (test-util/generatable? ::sut/insert-change-notes))))

(deftest valid-insert-command?-test
  (testing "The insert command is valid if the change type and notes are valid"
    (is (true? (sut/valid-insert-command? "added" ["A new feature"])))
    (is (true? (sut/valid-insert-command? "changed" ["An existing feature"])))
    (is (true? (sut/valid-insert-command? "deprecated" ["A soon-to-be removed feature" "Or is it?"])))
    (is (true? (sut/valid-insert-command? "removed" ["A removed feature"]))))
  (testing "The insert command is invalid if the change type is invalid"
    (is (false? (sut/valid-insert-command? "invalid" ["A new feature"])))
    (is (false? (sut/valid-insert-command? "added" [])))
    (is (false? (sut/valid-insert-command? "added" [""])))
    (is (false? (sut/valid-insert-command? "invalid" ["A new feature" ""])))))
