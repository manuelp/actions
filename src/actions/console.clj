(ns actions.console
  (:use [actions.core])
  (:require [clojure.string :as s]
            [actions.todotxt :as todotxt])
  (:import java.lang.Integer))

(defn read-command [prompt]
  (do (println prompt))
  (s/split (read-line) #"\s"))

(declare print-help)

(def valid-commands {
                     "a" [(fn [& words] (save-actions
                                        (add-action
                                         (load-actions)
                                         (s/join \  words))))
                          "Add a new action to the list."]
                     "e" [(fn [& words]
                            (save-actions
                             (edit-action (. Integer parseInt (first words))
                                          (s/join \  (rest words))
                                          (load-actions))))
                          "Edit and existing action."]
                     "rm" [print-help "Remove an existing actions from the list by id."]
                     "p" [print-help "Set the priority of an existing action."]
                     "dp" [print-help "Remove priority from an action."]
                     "do" [print-help "Mark an existing action as done with today as completion date."]
                     "h" [print-help "Print this help."]
                     "ls" [(fn [] (todotxt/print-actions (load-actions)))
                           "Print a list of the actions to do on the list."]
                     })

(defn print-help []
  (println (s/join \newline (map #(str (key %) ": " (first (rest (val %)))) valid-commands))))

(defn command-loop []
  (let [cmd (read-command "Gimme a command, please.")]
    (if (= "q" (first cmd))
      (println "Bye!")
      (do
        (apply (first (valid-commands (first cmd))) (vec (rest cmd)))
        (recur)))))

(command-loop)
