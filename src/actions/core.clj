(ns actions.core)

(defn write-data [tasks file-name]
  (spit file-name tasks))

(defn read-data [file-name]
  (read-string (slurp file-name)))
