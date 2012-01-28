(ns actions.core
  (:require :reload [actions.spitfile :as out]
            [actions.todotxt :as todotxt]))

(defn save-actions [actions]
  (out/write-data (vec actions) "actions.data"))

(defn load-actions []
  (out/read-data "actions.data"))

(defn next-id [actions]
  (if (empty? actions)
    1
    (inc (apply max (map #(:id %) actions)))))

(defn add-action
  ([actions description]
     (conj actions {:id (next-id actions)
                    :description description
                    :priority nil
                    :tags []
                    :contexts []
                    :done false}))
  ([actions description priority tags contexts]
     (conj actions {:id (next-id actions)
                    :description description
                    :priority priority
                    :tags tags
                    :contexts contexts
                    :done false})))

(defn mark-done [action]
  (assoc action :done true))

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
