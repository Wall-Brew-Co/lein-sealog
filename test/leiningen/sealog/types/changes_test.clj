(ns leiningen.sealog.types.changes-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer [deftest is testing]]
            [leiningen.sealog.types.changes :as sut]))


(defn generatable?
  "Attempts to generate a value for spec and returns true if it succeeds."
  {:added  "1.3"
   :no-doc true}
  [spec]
  (try
    (every? #(s/valid? spec %) (gen/sample (s/gen spec)))
    (catch Exception e
      (println (str "Failed to generate a value for spec: " spec))
      (println e)
      false)))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (generatable? ::sut/change-note))
    (is (generatable? ::sut/added))
    (is (generatable? ::sut/changed))
    (is (generatable? ::sut/deprecated))
    (is (generatable? ::sut/removed))
    (is (generatable? ::sut/fixed))
    (is (generatable? ::sut/security))
    (is (generatable? ::sut/misc))
    (is (generatable? ::sut/changes))))
