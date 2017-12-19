(ns iridule.data
  (:require [clojure.string :refer [index-of split]]
            [clj-time.format :refer [parse unparse formatter formatters]])
  (:import [com.google.common.collect TreeMultimap]
           [java.util Comparator]))


(def index->extract-fn [[:gender-lastname
                         #(identity [(:gender %) (:lastname %)])]
                        [:birthdate :birthdate]
                        [:lastname :lastname]])

(defn line->delimiter
  "Detects and returns the delimiter used in the given line."
  [line]
  (cond
    (index-of line "|") " \\| "
    (index-of line ",") ", "
    :else " "))

(defn parse-date
  "Parses YYYY-MM-DD string into Joda DateTime."
  [date-str]
  (parse (formatters :year-month-day) date-str))

(def custom-formatter (formatter "M/d/yyyy"))

(defn render-date
  "Renders M/D/YYYY string from a Joda DateTime."
  [date]
  (unparse custom-formatter date))

(defn line->kv [extract-key-fn delim line]
  "Converts a single line into a key-value pair for later indexing. First splits
  the line at the given delimiter (regex), converts to map with known keys,
  parses birthdate into instant, and finally applies extract-key-fn to select the
  relevant keys for desired index."
  (let [vals (-> (zipmap [:lastname :firstname :gender :fav-color :birthdate]
                         (split line delim))
                 (update :birthdate parse-date))]
    [(extract-key-fn vals) vals]))

(defn create-multimap!
  "Creates a TreeMultimap, having the property that entries are maintained in
  order (using the supplied comparator) on insertion."
  [^Comparator key-comparator]
  (TreeMultimap/create key-comparator (constantly 1)))

(defn index-record! [tm extract-key-fn delim line]
  (let [[k v] (line->kv extract-key-fn delim line)]
      (.put tm k v)))
