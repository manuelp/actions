(ns actions.todotxt
  (:require [clojure.string :as str]
            [actions.core :as core]))

(defn format-tags [tags]
  (str/join " " (map #(str "+" (name %)) tags)))

(defn format-contexts [ctxs]
  (str/join " " (map #(str "@" %) ctxs)))

(defn format-action [action]
  (str (action :id) " "
       (if (action :done) "x ")
       (if (action :priority)
         (str "(" (action :priority) ") "))
       (action :description)
       (if (not (empty? (action :tags)))
         (str " " (format-tags (action :tags))))
       (if (not (empty? (action :contexts)))
         (str " " (format-contexts (action :contexts))))))

(defn take-with-priority [actions]
  (filter #(not (nil? (:priority %))) actions))

(defn take-without-priority [actions]
  (filter #(nil? (:priority %)) actions))

(defn sort-actions [actions]
  (let [with-p (take-with-priority actions)
        without-p (take-without-priority actions)]
    (concat (sort-by #(vec (map % [:priority :description])) with-p)
            (sort-by :description without-p))))

(defn print-actions [actions]
  (println (str/join \newline
                 (map format-action (sort-actions (filter #(not (% :done)) actions))))))

(defn project? [str]
  (= \+ (first str)))

(defn context? [str]
  (= \@ (first str)))

(defn priority? [str]
  (and (= 3 (count str))
       (= \( (first str))
       (= \) (first (rest (rest str))))))

(defn take-priority [s]
  (first (rest s)))

(defn read-action
  "Create an action from a string read using the todo.txt format."
  [actions str]
  (let [tokens (str/split str #"\s")]
    (core/add-action actions
                     str
                     (if (priority? (first tokens))
                       (take-priority (first tokens)))
                     (filter project? tokens)
                     (filter context? tokens))))
