(ns iridule.data-test
  (:require [clojure.test :refer :all]
            [iridule.data :refer [delimiter?]]
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
