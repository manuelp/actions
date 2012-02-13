(ns actions.core
  (:require :reload [actions.spitfile :as out]
            [actions.todotxt :as todotxt]
            [clojure.string :as s]))

(defn save-actions
  "Save actions to the 'todo.txt' file."
  [actions]
  (todotxt/write-data actions "todo.txt"))

(defn load-actions
  "Load actions from the 'todo.txt' file."
  []
  (todotxt/read-data "todo.txt"))

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

(defn next-id
  "Get the next free id based on how much actions there are (num actions + 1)."
  [actions]
  (if (empty? actions)
    1
    (inc (apply max (map #(:id %) actions)))))

(defn add-action
  "Add a new action to the list."
  ([actions description]
     (conj actions (assoc (new-action description) :id (next-id actions))))
  ([actions description priority tags contexts]
     (conj actions (assoc (new-action description priority tags contexts)
                     :id (next-id actions)))))

(defn finish-action
  "Mark the action of the list with the given id as done."
  [id actions]
  (letfn [(today [] (. (new java.text.SimpleDateFormat "yyyy-MM-dd")
                       format (new java.util.Date)))
          (mark-done [action]
            (assoc action :done true :doneDate (today)))]
    (map (fn [a]
           (if (= (:id a) id)
             (mark-done a)
             a))
         actions)))

(defn deprioritize
  "Remove priority from the action in the list with the given id."
  [id actions]
  (letfn [(deprioritize-action [action]
            (dissoc action :priority))]
    (map #(if (= (:id %) id)
            (deprioritize-action %)
            %)
         actions)))

(defn add-priority
  "Add or change priority of an action."
  [id priority actions]
  (letfn [(set-priority [p action] (assoc action :priority p))]
    (map #(if (= (:id %) id)
            (set-priority priority %)
            %)
         actions)))

(defn edit-action
  "Substitute the description of the action in the list with the given id."
  [id new-desc actions]
  (letfn [(change-description [action new-desc]
            (assoc action :description new-desc))]
    (map (fn [a]
           (if (= (:id a) id)
             (change-description a new-desc)
             a))
         actions)))

(defn remove-action
  "Remove from the list the action with the given id."
  [id actions]
  (filter #(not (= id (% :id))) actions))

;; Here for quick tests in the REPL
;;(todotxt/print-actions (load-actions))
