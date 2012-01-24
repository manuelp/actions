(ns actions.core)
(use '[clojure.string :only (join)])

(defn write-data [tasks file-name]
  (spit file-name tasks))

(defn read-data [file-name]
  (let [data (slurp file-name)]
    (if (not (empty? data))
      (read-string data)
      [])))

(defn save-actions [actions]
  (write-data actions "actions.data"))

(defn load-actions []
  (read-data "actions.data"))

(defn format-tags [tags]
  (join \, (map (fn [sym]
                  (name sym))
                tags)))

(defn format-action [action]
  (if (:done action)
    (str (action :id)  ": [x] " (action :description) " (" (format-tags (action :tags)) ")")
    (str (action :id) ": [ ] " (action :description) " (" (format-tags (action :tags)) ")")))

(defn print-actions [actions]
  (println (join \newline (map format-action (sort-by #(:description %) (filter (fn [a] (not (a :done))) actions))))))

(defn next-id [actions]
  (if (empty? actions)
    1
    (inc (apply max (map #(:id %) actions)))))

(defn add-action [description tags actions]
  (conj actions {:id (next-id actions) :description description :tags tags :done false}))

(defn mark-done [action]
  (assoc action :done true))

(defn change-description [action new-desc]
  (assoc action :description new-desc))

(defn finish-action [id actions]
  (vec (map (fn [a]
         (if (= (:id a) id)
           (mark-done a)
           a))
       actions)))

(defn edit-action [id new-desc actions]
  (vec (map (fn [a]
         (if (= (:id a) id)
           (change-description a new-desc)
           a))
       actions)))

(print-actions (load-actions))
