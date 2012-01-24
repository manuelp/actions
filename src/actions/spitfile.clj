(ns actions.spitfile)

(defn write-data [tasks file-name]
  (spit file-name tasks))

(defn read-data [file-name]
  (let [data (slurp file-name)]
    (if (not (empty? data))
      (read-string data)
      [])))
