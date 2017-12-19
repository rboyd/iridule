(ns iridule.core
  (:require [iridule.data :refer [line->delimiter create-multimap! render-date
                                  index-record! index->extract-fn]]
            [ring.server.standalone :refer [serve]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [iridule.handler :refer [app]])
  (:gen-class))


; application state
(def index (atom {:gender-lastname (create-multimap! compare)
                  :birthdate (create-multimap! compare)
                  :lastname (create-multimap! (comp - compare))}))

(defn render-index!
  "Print the values in a given index (specified by keyword)."
  [k-idx]
  (doseq [v (.values (get @index k-idx))]
    (->> (update v :birthdate render-date)
         vals
         (clojure.string/join ",")
         println)))

(defn index!
  "Given a list of files and a destination index, perform the indexing."
  [files [k-idx extract-fn]]
  (doseq [file files]
    (with-open [rdr (clojure.java.io/reader file)]
      (let [lines (line-seq rdr)
            first-line (first lines)
            delim (re-pattern (line->delimiter first-line))]
        (doseq [line lines]
          (index-record! (get @index k-idx) extract-fn delim line))))))

(defn app-middleware
  "Make index available to ring handler in :app-state"
  [f state]
  (fn [request]
    (f (assoc request :app-state state))))

(def handler (-> app
                 (app-middleware index)
                 wrap-keyword-params
                 wrap-json-response
                 wrap-json-params))

(defn -main [& files]
  ; read files specified from cmdline, parse and index
  ; parallelized/one core per index (as multimap is not thread-safe)
  (doall (pmap (partial index! files) index->extract-fn))

  ; render output(s)
  (doall (map-indexed #(do (println "Output" (inc %1))
                           (render-index! %2))
                      [:gender-lastname :birthdate :lastname]))

  ; start web server
  (serve handler {:port 3000})
  (shutdown-agents))
