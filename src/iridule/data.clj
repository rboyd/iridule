(ns iridule.data
  (:require [clojure.string :refer [index-of split]]
            [clj-time.format :refer [parse unparse formatter formatters]])
  (:import [com.google.common.collect TreeMultimap]
           [java.util Comparator]))


(defn delimiter?
  "Detects the delimiter used in the supplied line."
  [line]
  (cond
    (index-of line "|") " | "
    (index-of line ",") ", "
    :else " "))

(def custom-formatter (formatter "M/d/yyyy"))

(defn parse-date
  "Converts from YYYY-MM-DD to M/D/YYYY."
  [date-str]
  (unparse custom-formatter
           (parse (formatters :year-month-day) date-str)))

(defn line->kv [extract-key-fn delim line]
  "Converts a single line into a key-value pair for later indexing. First splits
  the line at the given delimiter (regex), converts to map with known keys, and
  finally appliesextract-key-fn to select the relevant keys for desired index."
  (let [vals (zipmap [:lastname :firstname :gender :fav-color :birthdate]
                     (split line delim))]
    [(extract-key-fn vals) vals]))

(defn create-multimap!
  "Creates a TreeMultimap, having the property that entries are maintained in
  order (using the supplied comparator) on insertion."
  [^Comparator key-comparator]
  (TreeMultimap/create key-comparator (constantly 1)))

(defn index-records! [tm extract-key-fn delim lines]
  (doseq [line lines]
    (let [[k v] (line->kv extract-key-fn delim line)]
      (.put tm k (update v :birthdate parse-date)))))
