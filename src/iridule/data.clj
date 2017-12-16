(ns iridule.data
  (:require [clojure.string :refer [index-of split]])
  (:import [com.google.common.collect TreeMultimap]
           [java.util Comparator]))


(defn delimiter?
  "Detects the delimiter used in supplied row."
  [row]
  (cond
    (index-of row "|") " | "
    (index-of row ",") ", "
    :else " "))

(defn lines->kv [extract-fn delim lines]
  (for [line lines
        :let [vals (zipmap [:lastname :firstname :gender :fav-color :birthdate]
                           (split line delim))]]
          [(extract-fn vals) vals]))

(defn create-multimap!
  "Creates a TreeMultimap, having the property that entries are maintained in order (using
  the supplied comparator) on insertion."
  [^Comparator key-comparator]
  (TreeMultimap/create key-comparator (constantly 1)))

(defn index-records! [tm extract-fn delim lines]
  (doseq [[k v] (lines->kv extract-fn (re-pattern delim) lines)]
    (.put tm k v)))
