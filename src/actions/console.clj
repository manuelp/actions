(ns actions.console
  (:use [actions.core])
  (:require [clojure.string :as s]
            [actions.todotxt :as todotxt])
  (:import java.lang.Integer)
  (:gen-class))

(defn read-command [prompt]
  (do (println prompt))
  (s/split (read-line) #"\s"))

(defn parse-int [s]
  (. Integer parseInt s))

(declare print-help)

(defn filter-actions [actions words]
  (letfn [(contains-chunk? [chunk desc]
            (not (nil? (re-seq (re-pattern (str ".*" chunk ".*")) desc))))
          (matches-chunkes? [chunkes desc]
            (reduce #(and %1 %2) (map #(contains-chunk? % desc) chunkes)))]
    (if (not (nil? words))
      (filter #(matches-chunkes? words (:description %)) actions)
      actions)))

(def valid-commands {
                     "a" ["Add a new action to the list."
                          (fn [& words] (save-actions
                                        (add-action
                                         (load-actions)
                                         (s/join \  words))))]
                     "e" ["Edit and existing action."
                          (fn [& words]
                            (save-actions
                             (edit-action (parse-int (first words))
                                          (s/join \  (rest words))
                                          (load-actions))))]
                     "rm" ["Remove an existing actions from the list by id."
                           (fn [& words]
                             (save-actions (remove-action (parse-int (first words))
                                                          (load-actions))))]
                     "p" ["Set the priority of an existing action."
                          (fn [id priority]
                            (save-actions (add-priority (parse-int id) priority (load-actions))))]
                     "dp" ["Remove priority from an action."
                           (fn [id]
                             (save-actions (deprioritize (parse-int id) (load-actions))))]
                     "do" ["Mark an existing action as done with today as completion date."
                           (fn [id] (save-actions (finish-action (parse-int id) (load-actions))))]
                     "h" ["Print this help." print-help]
                     "ls" ["Print a list of the actions to do on the list."
                           (fn [& words] (todotxt/print-actions (filter-actions (load-actions) words)))]
                     })

(defn print-help []
  (println (s/join \newline (map #(str (key %) ": " (first (val %))) valid-commands))))

(defn command-loop []
  (let [cmd (read-command "Gimme a command, please.")]
    (if (= "q" (first cmd))
      (println "Bye!")
      (do
        (apply (first (rest (valid-commands (first cmd)))) (vec (rest cmd)))
        (recur)))))

;;(command-loop)

(defn -main []
  (command-loop))

