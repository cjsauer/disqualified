(ns cjsauer.mass-transit-clj.core
  (:require [clojure.set :refer [map-invert]]
            [clojure.spec.alpha :as s]))

(s/def ::keys-vec (s/coll-of keyword? :kind vector?))
(s/def ::keys-spec-sym #(= % 'keys))
(s/def ::keys-spec
  (s/cat :sym ::keys-spec-sym
         :rel keyword?
         :keys ::keys-vec))

(s/def :human/name string?)
(s/def :human/age number?)
(s/def ::human (s/keys :req [:human/name
                             :human/age]))
(s/def ::unq-human (s/keys :req-un [:human/name
                                    :human/age]))

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
  [q-spec m & [overrides]]
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

(comment
  (def human {:human/age 42
              :human/name "Jeff"})
  (def unq-human {:age 42
                  :name "Jeff"
                  :other 12})

  (keys-spec-keys ::human)
  (keys-spec-keys ::unq-human)

  (unqualify-map human)

  (map stripped human)

  (qualify-map ::human unq-human)
  (qualify-map ::human unq-human {:name :user/name
                                  :age  :person/age})

  )
