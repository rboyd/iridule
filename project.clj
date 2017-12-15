(defproject iridule "0.1.0-SNAPSHOT"
  :description "Experiments with Guava, ring, marginalia"
  :url "https://github.com/rboyd/iridule"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.google.guava/guava "23.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.nrepl "0.2.12"]
                                  [org.clojure/data.generators "0.1.2"]
                                  [faker "0.3.2"]]}})
