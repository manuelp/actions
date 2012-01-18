(ns actions.core)
(use '[clojure.string :only (join)])

(defn write-data [tasks file-name]
  (spit file-name tasks))

(defn read-data [file-name]
  (read-string (slurp file-name)))

(def actions (ref []))

(defn new-action [description tags]
  (dosync (alter actions conj {:description description :tags tags :done false})))

(defn save-actions []
  (write-data @actions "actions.data"))

(defn load-actions []
  (dosync (ref-set actions (read-data "actions.data"))))

(defn format-tags [tags]
  (join \, (map (fn [sym]
                         (name sym))
                       tags)))

(defn format-action [action]
  (if (:done action)
    (str "[x] " (:description action) " (" (format-tags action ")"))
    (str "[ ] " (:description action) " (" (format-tags action ")"))))
