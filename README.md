# disqualified

[![Clojars Project](https://img.shields.io/clojars/v/cjsauer/disqualified.svg)](https://clojars.org/cjsauer/disqualified)

Tiny Clojure(Script) library for conversion between qualified and unqualified
keyword maps.

### Setup

Leiningen/boot:

```
[cjsauer/disqualified "0.1.0"]
```

tools.deps:

```
cjsauer/disqualified {:mvn/version "0.1.0"}
```

### Usage

```Clojure
(ns my-project.core
  (:require [cjsauer.disqualified :refer [qualify-map unqualify-map]]))

;; Define some specs for humans
(s/def :human/name string?)
(s/def :human/age number?)
(s/def ::human (s/keys :req [:human/name
                             :human/age]))

(def human {:human/age 42
            :human/name "Jeff"
            :other 12})

;; Strip namespaces off all keywords
(unqualify-map human)
;; {:age 42, :name "Jeff", :other 12}

(def unq-human {:age 42
                :name "Brad"
                :other 12})

;; Qualify the keys of the map using the given spec as the guide.
;; Notice how keywords not in the spec are simply ignored.
(qualify-map unq-human ::human)
;; {:human/age 42, :human/name "Brad", :other 12} 

;; An overrides map can be provided to customize behavior
(qualify-map unq-human ::human {:other :something/else})
;;{:human/age 42, :human/name "Brad", :something/else 12}
```

Note that `(qualify-map)` only works for `(s/keys)` spec definitions. It is not useful
for `(s/map-of)` spec definitions in which the target keywords can not be inferred.
