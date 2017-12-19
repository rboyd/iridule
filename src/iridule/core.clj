;; *The iridule–when, beautiful and strange,*  
;; *In a bright sky above a mountain range*  
;; *One opal cloudlet in an oval form*  
;; *Reflects the rainbow of a thunderstorm*  
;; *Which in a distant valley has been staged*  
;; *For we are most artistically caged.*
;;
;; *- Vladimir Nabokov*
(ns iridule.core
  (:require [iridule.data :refer [line->delimiter create-multimap! render-date
                                  index-record! index->extract-fn]]
            [ring.server.standalone :refer [serve]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [iridule.handler :refer [app]])
  (:gen-class))


;; The application state. We use three separate indices:
;;
;;   * :gender-lastname - sorted by gender (females before males) then by last
;;       name ascending
;;   * :birthdate - sorted by birth date, ascending
;;   * :lastname - sorted by last name, descending
;;
;; These indices are Google Guava [TreeMultimap][1]s under the hood. They
;; are maintained in sorted state, paying the cost of the sort on insert instead
;; of each time the user wants to display the items within.
;;
;; The parameter (a Comparator) supplied to create-multimap! determines its sort order.
;;
;; [1]: https://google.github.io/guava/releases/23.0/api/docs/com/google/common/collect/TreeMultimap.html
(def index (atom {:gender-lastname (create-multimap! compare)
                  :birthdate (create-multimap! compare)
                  :lastname (create-multimap! (comp - compare))}))

(defn render-index!
  "Print the values in a given index (specified by keyword) to stdout."
  [k-idx]
  (let [tm (get @index k-idx)]
    (locking tm
      (doseq [v (.values tm)]
        (->> (update v :birthdate render-date)
             vals
             (clojure.string/join ",")
             println)))))

(defn index!
  "Given a list of files along with a destination index and extraction function,
  perform the indexing."
  [files [k-idx extract-fn]]
  (doseq [file files]
    (with-open [rdr (clojure.java.io/reader file)]
      (let [lines (line-seq rdr)
            first-line (first lines)
            delim (re-pattern (line->delimiter first-line))]
        (doseq [line lines]
          (index-record! (get @index k-idx) extract-fn delim line))))))

(defn app-middleware
  "Make indices available to ring handler in :app-state"
  [f state]
  (fn [request]
    (f (assoc request :app-state state))))

;; Our ring handler, with some ceremony to accept and return json.
(def handler (-> app
                 (app-middleware index)
                 wrap-keyword-params
                 wrap-json-response
                 wrap-json-params))

;; The entrypoint to our program.
;;   * Accepts any number of filenames as command line args
;;   * Indexes the lines in each
;;   * Prints the records from each index to stdout
;;   * Starts a ring server to expose our HTTP API
(defn -main [& files]
  ; read files specified from cmdline, parse and index. (parallelized)
  (doall (pmap (partial index! files) index->extract-fn))

  ; render output(s)
  (doall (map-indexed #(do (println "Output" (inc %1))
                           (render-index! %2))
                      [:gender-lastname :birthdate :lastname]))

  ; start web server
  (binding [*out* *err*]
    (serve handler {:port 3000}))
  (shutdown-agents))
