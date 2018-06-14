(defproject cjsauer/disqualified "0.1.1-SNAPSHOT"
  :description "Tiny Clojure(Script) library for converting between qualified
  and unqualified keyword maps using clojure.spec definitions"
  :url "http://github.com/cjsauer/disqualified"
  :scm {:name "git"
        :url "http://github.com/cjsauer/disqualified"}
  :dependencies []
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :test {:source-paths ["test"]}})
