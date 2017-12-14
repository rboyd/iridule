(ns iridule.data
  (:require [clojure.string :refer [index-of]]))

(defn delimiter?
  "Detects the delimiter used in supplied row."
  [row]
  (cond
    (index-of row "|") " | "
    (index-of row ",") ", "
    :else " "))
