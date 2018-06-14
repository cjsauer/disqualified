(ns cjsauer.mass-transit
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
  "Converts all qualified keywords of map m to unqualified keywords by stripping
  off the namespace."
  [m]
  (into {} (map strip-kv m)))

(defn qualify-map
  "Converts all unqualified keywords of map m to qualified keywords using
  q-spec, a clojure.spec keys definition, as a guide.
  Optionally takes a map of overrides, which is a map of unqualified keys
  to their desired qualified keys."
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

(comment
  ;; Define some specs for humans
  (s/def :human/name string?)
  (s/def :human/age number?)
  (s/def ::human (s/keys :req [:human/name
                               :human/age]))

  (def human {:human/age 42
              :human/name "Jeff"
              :other 12})

  (unqualify-map human)
  ;; {:age 42, :name "Jeff", :other 12}

  (def unq-human {:age 42
                  :name "Brad"
                  :other 12})

  ;; Qualify the keys of the map using the given spec as the guide.
  ;; Notice how unknown keywords are ignored.
  (qualify-map unq-human ::human)
  ;; {:human/age 42, :human/name "Brad", :other 12} 

  ;; An overrides map can be provided to customize behavior
  (qualify-map unq-human ::human {:other :something/else})
  ;;{:human/age 42, :human/name "Brad", :something/else 12}

  )
