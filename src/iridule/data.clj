(ns iridule.data
  (:require [clojure.string :refer [index-of split]]
            [clj-time.format :refer [parse unparse formatter formatters]])
  (:import [com.google.common.collect TreeMultimap Multimaps]
           [java.util Comparator]))


(defn line->delimiter
  "Detects and returns the delimiter used in the given line. We must support CSV,
  pipe-delimited, and space-delimited records."
  [line]
  (cond
    (index-of line "|") " \\| "
    (index-of line ",") ", "
    :else " "))

;; We use [clj-time][1] a Clojure wrapper around [Joda Time][2], which provides
;; utility functions to parse birthdates and render them in a preferred format.
;;
;; [1]: https://github.com/clj-time/clj-time
;; [2]: http://www.joda.org/joda-time

(defn parse-date
  "Parses YYYY-MM-DD string into Joda DateTime."
  [date-str]
  (parse (formatters :year-month-day) date-str))

(def custom-formatter (formatter "M/d/yyyy"))

(defn render-date
  "Renders M/D/YYYY string from a Joda DateTime."
  [date]
  (unparse custom-formatter date))

(defn line->kv
  "Converts a single line into a key-value pair for later indexing. First splits
  the line at the given delimiter (regex), converts to map with known keys,
  parses birthdate into instant, and finally applies extract-key-fn to select the
  relevant keys for desired index."
  [extract-key-fn delim line]
  (let [vals (-> (zipmap [:lastname :firstname :gender :fav-color :birthdate]
                         (split line delim))
                 (update :birthdate parse-date))]
    [(extract-key-fn vals) vals]))

;; This map declares how keys will be extracted from records (after having
;; been parsed from string to maps). In case of birthdate and lastname indices
;; this is straightforward, but for gender-lastname, we construct something of
;; a composite key this way.
(def index->extract-fn [[:gender-lastname
                         #(identity [(:gender %) (:lastname %)])]
                        [:birthdate :birthdate]
                        [:lastname :lastname]])

(defn create-multimap!
  "Creates a TreeMultimap, having the property that entries are maintained in
  order (using the supplied comparator) on insertion."
  [^Comparator key-comparator]
  (Multimaps/synchronizedSortedSetMultimap
   (TreeMultimap/create key-comparator (constantly 1))))

(defn index-record!
  "Wraps line->kv and puts entries in the TreeMultimap."
  [tm extract-key-fn delim line]
  (let [[k v] (line->kv extract-key-fn delim line)]
      (.put tm k v)))
