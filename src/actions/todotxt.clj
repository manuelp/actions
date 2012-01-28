(ns actions.todotxt
  (:require [clojure.string :as str]))

(defn format-tags [tags]
  (str/join " " (map #(str "+" (name %)) tags)))

(defn format-contexts [ctxs]
  (str/join " " (map #(str "@" %) ctxs)))

(defn format-todotxt [action]
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
