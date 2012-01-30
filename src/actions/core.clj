(ns actions.core
  (:require :reload [actions.spitfile :as out]
            [actions.todotxt :as todotxt]))

(defn save-actions
  "Save actions to the 'todo.txt' file."
  [actions]
  (todotxt/write-data actions "todo.txt"))

(defn load-actions
  "Load actions from the 'todo.txt' file."
  []
  (todotxt/read-data "todo.txt"))

(defn next-id
  "Get the next free id based on how much actions there are (num actions + 1)."
  [actions]
  (if (empty? actions)
    1
    (inc (apply max (map #(:id %) actions)))))

(defn new-action
  "Create a new hash-map that represents a new action (without id)."
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
  "Add a new action to the list."
  ([actions description]
     (conj actions (assoc (new-action description) :id (next-id actions))))
  ([actions description priority tags contexts]
     (conj actions (assoc (new-action description priority tags contexts)
                     :id (next-id actions)))))

(defn today
  "Get today's date in the 'yyyy-MM-dd' format."
  []
  (. (new java.text.SimpleDateFormat "yyyy-MM-dd") format (new java.util.Date)))

(defn mark-done
  "Mark the action as done, compiling the :doneDate property."
  [action]
  (assoc action :done true :doneDate (today)))

(defn finish-action
  "Mark the action of the list with the given id as done."
  [id actions]
  (map (fn [a]
         (if (= (:id a) id)
           (mark-done a)
           a))
       actions))

(defn deprioritize-action
  "Remove priority from an action."
  [action]
  (dissoc action :priority))

(defn deprioritize
  "Remove priority from the action in the list with the given id."
  [id actions]
  (map #(if (= (:id %) id)
          (deprioritize-action %)
          %)
       actions))

(defn change-description
  "Returns a new action that is a copy of the given one with a different description."
  [action new-desc]
  (assoc action :description new-desc))

(defn edit-action
  "Substitute the description of the action in the list with the given id."
  [id new-desc actions]
  (map (fn [a]
         (if (= (:id a) id)
           (change-description a new-desc)
           a))
       actions))

(defn remove-action
  "Remove from the list the action with the given id."
  [id actions]
  (filter #(not (= id (% :id))) actions))

(todotxt/print-actions (load-actions))

