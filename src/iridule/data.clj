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

(defn lines->kv [delim lines]
  (for [line lines
        :let [vals (split line delim)]]
          [(first vals) vals]))

(defn create-multimap!
  "Creates a TreeMultimap, having the property that entries are maintained in order (using the supplies comparator) on insertion."
  [^Comparator key-comparator]
  (TreeMultimap/create key-comparator (constantly 1)))

(defn index-records! [tm delim lines]
  (doseq [[k v] (lines->kv (re-pattern delim) lines)]
    (.put tm k v)))
