(defproject iridule "0.1.0-SNAPSHOT"
  :description "Experiments with Guava, ring, marginalia"
  :url "https://github.com/rboyd/iridule"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.google.guava/guava "23.0"]
                 [clj-time "0.14.2"]
                 [compojure "1.6.0"]
                 [ring-server "0.5.0"]
                 [ring/ring-json "0.4.0"]]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/tools.nrepl "0.2.12"]
                                  [org.clojure/data.generators "0.1.2"]
                                  [faker "0.3.2"]
                                  [ring/ring-mock "0.3.2"]
                                  [cheshire "5.8.0"]]}}
  :aliases {"docs" ["marg"
                    "-ddoc"
                    "-firidule.html"
                    "src/iridule/core.clj"
                    "src/iridule/data.clj"
                    "src/iridule/handler.clj"]}
  :main iridule.core)
