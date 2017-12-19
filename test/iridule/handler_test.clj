(ns iridule.handler-test
  (:require [clojure.test :refer :all]
            [iridule.core :refer [handler app-middleware index]]
            [iridule.data :refer :all]
            [iridule.handler :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :refer :all]))


(def test-records ["Sawayn Sam F Orange 2033-11-21"
                   "Lindgren Kelly F Red 2014-06-24"
                   "Parisian Sarai M Red 2042-12-12"
                   "Kub Dee M Indigo 2039-03-16"
                   "Okuneva Godfrey M Red 1975-01-09"])

(def gender-sorted [{:lastname "Lindgren" :firstname "Kelly" :gender "F"
                     :fav-color "Red" :birthdate "6/24/2014"}
                    {:lastname "Sawayn" :firstname "Sam" :gender "F"
                     :fav-color "Orange" :birthdate "11/21/2033"}
                    {:lastname "Kub" :firstname "Dee" :gender "M"
                     :fav-color "Indigo" :birthdate "3/16/2039"}
                    {:lastname "Okuneva" :firstname "Godfrey" :gender "M"
                     :fav-color "Red" :birthdate "1/9/1975"}
                    {:lastname "Parisian" :firstname "Sarai" :gender "M"
                     :fav-color "Red" :birthdate "12/12/2042"}])

(def birthdate-sorted [{:lastname "Okuneva" :firstname "Godfrey" :gender "M"
                        :fav-color "Red" :birthdate "1/9/1975"}
                       {:lastname "Lindgren" :firstname "Kelly" :gender "F"
                        :fav-color "Red" :birthdate "6/24/2014"}
                       {:lastname "Sawayn" :firstname "Sam" :gender "F"
                        :fav-color "Orange" :birthdate "11/21/2033"}
                       {:lastname "Kub" :firstname "Dee" :gender "M"
                        :fav-color "Indigo" :birthdate "3/16/2039"}
                       {:lastname "Parisian" :firstname "Sarai" :gender "M"
                        :fav-color "Red" :birthdate "12/12/2042"}])

(def name-sorted [{:lastname "Sawayn" :firstname "Sam" :gender "F"
                   :fav-color "Orange" :birthdate "11/21/2033"}
                  {:lastname "Parisian" :firstname "Sarai" :gender "M"
                   :fav-color "Red" :birthdate "12/12/2042"}
                  {:lastname "Okuneva" :firstname "Godfrey" :gender "M"
                   :fav-color "Red" :birthdate "1/9/1975"}
                  {:lastname "Lindgren" :firstname "Kelly" :gender "F"
                   :fav-color "Red" :birthdate "6/24/2014"}
                  {:lastname "Kub" :firstname "Dee" :gender "M"
                   :fav-color "Indigo" :birthdate "3/16/2039"}])

(deftest handler-test
  (doseq [line test-records]
    (handler (-> (mock/request :post "/records")
                 (mock/json-body {:line line}))))

  (testing "/records/gender endpoint renders records sorted by gender"
    (is (= (handler (mock/request :get "/records/gender"))
           {:status  200
            :headers {"Content-Type" "application/json; charset=utf-8"}
            :body (generate-string {:records gender-sorted})})))

  (testing "/records/birthdate endpoint renders records sorted by birthdate"
    (is (= (handler (mock/request :get "/records/birthdate"))
           {:status  200
            :headers {"Content-Type" "application/json; charset=utf-8"}
            :body (generate-string {:records birthdate-sorted})})))

  (testing "/records/name endpoint renders records sorted by last name (desc)"
    (is (= (handler (mock/request :get "/records/name"))
           {:status  200
            :headers {"Content-Type" "application/json; charset=utf-8"}
            :body (generate-string {:records name-sorted})}))))
