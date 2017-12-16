(ns iridule.core
  (:gen-class))


(defn -main [& files]
  (doseq [file files]
    (println file)))
