(ns actions.core
  (:require :reload [actions.spitfile :as out]
            [actions.todotxt :as todotxt]))

(defn save-actions [actions]
  (todotxt/write-data actions "todo.txt"))

(defn load-actions []
  (todotxt/read-data "todo.txt"))

(defn next-id [actions]
  (if (empty? actions)
    1
    (inc (apply max (map #(:id %) actions)))))

(defn new-action
  ([description]
     {:done false
      :description description
      :priority nil
      :tags []
      :contexts []})
  ([description priority tags contexts]
     {:done false
      :description description
      :priority priority
      :tags tags
      :contexts contexts}))

(defn add-action
  ([actions description]
     (conj actions (assoc (new-action description) :id (next-id actions))))
  ([actions description priority tags contexts]
     (conj actions (assoc (new-action description priority tags contexts)
                     :id (next-id actions)))))

(defn today []
  (. (new java.text.SimpleDateFormat "yyyy-MM-dd") format (new java.util.Date)))

(defn mark-done [action]
  (assoc action :done true :doneDate (today)))

(defn deprioritize-action [action]
  (dissoc action :priority))

(defn deprioritize [id actions]
  (map #(if (= (:id %) id)
          (deprioritize-action %)
          %)
       actions))

(defn change-description [action new-desc]
  (assoc action :description new-desc))

(defn finish-action [id actions]
  (map (fn [a]
         (if (= (:id a) id)
           (mark-done a)
           a))
       actions))

(defn edit-action [id new-desc actions]
  (map (fn [a]
         (if (= (:id a) id)
           (change-description a new-desc)
           a))
       actions))

(defn remove-action [id actions]
  (filter #(not (= id (% :id))) actions))

(todotxt/print-actions (load-actions))

