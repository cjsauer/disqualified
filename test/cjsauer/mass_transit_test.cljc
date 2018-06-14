(ns cjsauer.mass-transit-test
  (:require [cjsauer.mass-transit :as sut]
            [clojure.spec.alpha :as s]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :as t :include-macros true])))

(s/def :human/name string?)
(s/def :human/age number?)
(s/def ::human (s/keys :req [:human/name
                             :human/age]))
(s/def ::unq-human (s/keys :req-un [:human/name
                                    :human/age]))

(def human {:human/age 42
            :human/name "Jeff"
            :other 12})

(def unq-human {:age 42
                :name "Jeff"
                :other 12})

(t/deftest round-trip
  (t/is (= unq-human
           (-> unq-human (sut/qualify-map ::human) sut/unqualify-map)))
  (t/is (= human
           (-> human sut/unqualify-map (sut/qualify-map ::human))))
  )
