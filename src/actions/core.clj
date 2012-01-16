(ns actions.core)

(defn write-data [tasks file-name]
  (spit file-name tasks))

(defn read-data [file-name]
  (read-string (slurp file-name)))

;(def actions (ref []))

(defn new-action [description tags]
  (dosync (alter actions conj {:description description :tags tags :done false})))

(defn save-actions []
  (write-data @actions "actions.data"))

(defn load-actions []
  (dosync (ref-set actions (read-data "actions.data"))))

(defn format-tags [tags]
  (loop [res (name (first tags)) t (rest tags)]
    (if (empty? (rest t))
      res
      (recur (str res "," (name (first t))) (rest t)))))

(defn format-action [action]
  (if (:done action)
    (str "[x] " (:description action) " (" (format-tags action ")"))
    (str "[ ] " (:description action) " (" (format-tags action ")"))))
