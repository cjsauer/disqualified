# disqualified

[![Clojars Project](https://img.shields.io/clojars/v/cjsauer/disqualified.svg)](https://clojars.org/cjsauer/disqualified)

Tiny Clojure(Script) library for conversion between qualified and unqualified
keyword maps using clojure.spec definitions.

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

;; Strip namespaces off all keyword keys
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

### Limitations

`qualify-map` only works for `s/keys` spec definitions. It is not useful for
`s/map-of` spec definitions in which the target keywords can not be inferred.

`qualify-map` uses the keys specified inside of the `:req` vector given to
`s/keys` to infer target keywords. This means that it can conflict with the
optionality semantics (`:opt`) of `s/keys`. To work around this, you may want to
create a separate spec definition specifcally for qualification (e.g. `(s/def
::human-qualifier (s/keys :req [...]))`). This conflation of key selection and
key optionality will hopefully be fixed in "spec 2", as hinted by Rich Hickey in
his talk [Maybe Not][https://www.youtube.com/watch?v=YR5WdGrpoug].
