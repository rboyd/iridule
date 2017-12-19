(ns iridule.data-test
  (:require [clojure.test :refer :all]
            [iridule.data :refer [line->delimiter create-multimap! index-record!
                                  parse-date render-date line->kv]]
            [faker.name :refer [last-name first-name]]
            [clojure.data.generators :refer [date]]
            [clj-time.coerce :refer [from-date]]
            [clj-time.format :refer [unparse formatters]]))


(defn gen-row [delimiter]
  "Generates a fake (random) row."
  (clojure.string/join delimiter [(last-name)
                                  (first-name)
                                  (rand-nth ["M" "F"])
                                  (rand-nth ["Red" "Orange" "Yellow"
                                             "Green" "Blue" "Indigo" "Violet"])
                                  (unparse (formatters :year-month-day)
                                           (from-date (date)))]))

(deftest detect-test
  (let [delimiter-map {" | ", " \\| "
                       ", ",  ", "
                       " ",   " "}]
    (doseq [delim (keys delimiter-map)]
      (testing (str "Detects '" delim "' as the row's delimiter")
        (is (= (get delimiter-map delim) (line->delimiter (gen-row delim))))))))

(deftest maintains-order-test
  (let [lines ["Lovelace, Ada, F, Violet, 1815-12-10"
               "Armstrong, Neil, M, Red, 1930-08-05"
               "Cantor, Georg, M, Blue, 1845-03-03"
               "Armstrong, Neil, M, Red, 1930-08-05"]]

    (testing "multimap maintains order with last name desc index"
      (let [db (create-multimap! (comp - compare))]
        (doseq [line lines]
          (index-record! db :lastname #", " line))
        (is (= ["Lovelace" "Cantor" "Armstrong" "Armstrong"]
               (map :lastname (.values db))))))

    (testing "multimap maintains order with gender asc / last name asc index"
      (let [db (create-multimap! compare)]
        (doseq [line lines]
          (index-record! db #(identity [(:gender %) (:lastname %)]) #", " line))
        (is (= ["Lovelace" "Armstrong" "Armstrong" "Cantor"]
               (map :lastname (.values db))))))))

(deftest parse-and-render-date-test
  (testing "YYYY-MM-DD is parsed and rendered as  M/D/YYYY"
    (is (= "8/5/1930" (render-date (parse-date "1930-08-05"))))))

(deftest line->kv-test
  (testing "parses line into expected key/value pair"
    (is (= ["Lovelace" {:lastname "Lovelace"
                        :firstname "Ada"
                        :gender "F"
                        :fav-color "Violet"
                        :birthdate (parse-date "1815-12-10")}]
           (line->kv :lastname #", " "Lovelace, Ada, F, Violet, 1815-12-10")))))
