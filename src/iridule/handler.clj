(ns iridule.handler
  (:require [iridule.data :refer [render-date index-record! index->extract-fn
                                  line->delimiter]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response]]))

(defn records [app-state index]
  (->> (.values (get app-state index))
       (map #(update % :birthdate render-date))))

(defroutes app
  (POST "/records" [:as request]
        (let [line (get-in request [:params :line])
              delim (re-pattern (line->delimiter line))]
          (doseq [[k-idx extract-fn] index->extract-fn]
            (index-record! (get @(:app-state request) k-idx)
                           extract-fn delim line)))
        (response {:status "ok"}))
  (GET "/records/gender" [:as request]
       (response {:records (records @(:app-state request) :gender-lastname)}))
  (GET "/records/birthdate" [:as request]
       (response {:records (records @(:app-state request) :birthdate)}))
  (GET "/records/name" [:as request]
       (response {:records (records @(:app-state request) :lastname)}))
  (route/not-found "<h1>Page not found</h1>"))
