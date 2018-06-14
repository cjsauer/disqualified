(ns cjsauer.mass-transit-clj.core
  (:require [clojure.set :refer [map-invert]]
            [clojure.spec.alpha :as s]))

(s/def ::keys-vec (s/coll-of keyword? :kind vector?))
(s/def ::keys-spec-sym #(= % 'keys))
(s/def ::keys-spec
  (s/cat :sym ::keys-spec-sym
         :rel keyword?
         :keys ::keys-vec))

(defn- keys-spec-keys
  [spec]
  (let [conformed (s/conform ::keys-spec (s/describe spec))]
    (when (not= conformed ::s/invalid)
      (:keys conformed))))

(defn- strip
  [k]
  (-> k name keyword))

(defn- strip-kv
  [[k v]]
  [(strip k) v])

(defn unqualify-map
  [m]
  (into {} (map strip-kv m)))

(defn qualify-map
  [m q-spec & [overrides]]
  (let [q-keys (keys-spec-keys q-spec)
        q->unq (map-invert
                (zipmap q-keys (map strip q-keys)))
        lift-key (fn [k]
                   (or (get overrides k)
                       (q->unq k)
                       k))
        translate-kv (fn [[k v]]
                       [(lift-key k) v])]
    (into {} (map translate-kv m))))
