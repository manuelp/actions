(ns actions.console
  (:require [clojure.string :as str]))

(defn format-tags [tags]
  (str/join \, (map (fn [sym]
                  (name sym))
                tags)))

(defn format-action [action]
  (str (action :id) " (" (action :priority) ") " (action :description) " (" (format-tags (action :tags)) ")"))

(defn print-actions [actions]
  (println (str/join \newline
                 (map format-action
                      (sort-by #(:description %)
                               (sort-by #(:priority %)
                                        (filter #(not (% :done)) actions)))))))
