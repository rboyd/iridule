(ns iridule.core
  (:require [iridule.data :refer [line->delimiter create-multimap! render-date
                                  index-record!]])
  (:gen-class))


(def index (atom {:gender-lastname (create-multimap! compare)
                  :birthdate (create-multimap! compare)
                  :lastname (create-multimap! (comp - compare))}))

(defn render-index! [k-idx]
  (doseq [v (.values (get @index k-idx))]
    (->> (update v :birthdate render-date)
         vals
         (clojure.string/join ",")
         println)))

(defn -main [& files]
  ; read files specified from cmdline, parse and index
  (doseq [file files]
    (with-open [rdr (clojure.java.io/reader file)]
      (let [lines (line-seq rdr)
            first-line (first lines)
            delim (re-pattern (line->delimiter first-line))]
        (doseq [line lines]
          (doseq [[k-idx extract-fn] [[:gender-lastname
                                       #(identity [(:gender %) (:lastname %)])]
                                      [:birthdate :birthdate]
                                      [:lastname :lastname]]]
            (index-record! (get @index k-idx) extract-fn delim line))))))

  ; render output(s)
  (doall (map-indexed #(do (println "Output" (inc %1))
                           (render-index! %2))
                      [:gender-lastname :birthdate :lastname])))
