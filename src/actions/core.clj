(ns actions.core)
(use '[clojure.string :only (join)])

(defn write-data [tasks file-name]
  (spit file-name tasks))

(defn read-data [file-name]
  (let [data (slurp file-name)]
    (if (not (empty? data))
      (read-string data)
      [])))

(def actions (ref []))

(defn next-id []
  (if (empty? @actions)
    1
    (inc (apply max (map #(:id %) @actions)))))

(defn new-action [description tags]
  (dosync (alter actions conj {:id (next-id) :description description :tags tags :done false})))

(defn save-actions []
  (write-data @actions "actions.data"))

(defn load-actions []
  (dosync (ref-set actions (read-data "actions.data"))))

(load-actions)

(defn format-tags [tags]
  (join \, (map (fn [sym]
                  (name sym))
                tags)))

(defn format-action [action]
  (if (:done action)
    (str (action :id)  ": [x] " (action :description) " (" (format-tags (action :tags)) ")")
    (str (action :id) ": [ ] " (action :description) " (" (format-tags (action :tags)) ")")))

(defn print-actions []
  (println (join \newline (map format-action (sort-by #(:description %) @actions)))))

(defn mark-done [id]
  (dosync (ref-set actions (map (fn [a]
                                  (if (= (:id a) 1)
                                    (assoc a :done true)
                                    a))
                                @actions))))

;; Test
(print-actions)
