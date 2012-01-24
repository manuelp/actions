(ns actions.console
  (:require [clojure.string :only join]))

(defn format-tags [tags]
  (join \, (map (fn [sym]
                  (name sym))
                tags)))

(defn format-action [action]
  (if (:done action)
    (str (action :id)  ": [x] " (action :description) " (" (format-tags (action :tags)) ")")
    (str (action :id) ": [ ] " (action :description) " (" (format-tags (action :tags)) ")")))

; TODO: Reader macro in place of explicit fn
(defn print-actions [actions]
  (println (join \newline (map format-action
                               (sort-by #(:description %)
                                        (filter (fn [a] (not (a :done))) actions))))))
