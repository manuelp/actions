(ns actions.console
  (:require [clojure.string :as str]))

(defn format-tags [tags]
  (str/join \, (map (fn [sym] (name sym)) tags)))

(defn format-contexts [ctxs]
  (str/join \, (map #(str "@" %) ctxs)))

(defn format-action [action]
  (str (action :id)
       (if (contains? action :priority)
         (str " (" (action :priority) ") ")
         " ")
       (action :description)
       " (" (format-tags (action :tags)) ")"
       " (" (format-contexts (action :contexts)) ")"))

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
