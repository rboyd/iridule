(ns iridule.data-test
  (:require [clojure.test :refer :all]
            [iridule.data :refer [delimiter? create-multimap! index-records!]]
            [faker.name :refer [last-name first-name]]
            [clojure.data.generators :refer [date]]))


(defn gen-row [delimiter]
  (clojure.string/join delimiter [(last-name)
                                  (first-name)
                                  (rand-nth ["M" "F"])
                                  (rand-nth ["Red" "Orange" "Yellow"
                                             "Green" "Blue" "Indigo" "Violet"])
                                  (date)]))

(deftest detect-test
  (doseq [delim [" | " ", " " "]]
    (testing (str "Detects '" delim "' as the row's delimiter") 
      (is (= delim (delimiter? (gen-row delim)))))))

(deftest maintains-order-test
  (testing "multimap with maintains order with last name desc comparator."
    (let [lines  ["Lovelace, Ada, F, Violet, 1815/12/10"
                  "Armstrong, Neil, M, Red, 1930/8/5"
                  "Cantor, Georg, M, Blue, 1845/3/3"
                  "Armstrong, Neil, M, Red, 1930/8/5"]
          db (create-multimap! (comp - #(compare (first %1) (first %2))))]
      (index-records! db ", " lines)
      (is (= ["Lovelace" "Cantor" "Armstrong" "Armstrong"] (map first (.values db)))))))
