(ns actions.todotxt
  (:use [colorize.core])
  (:require [clojure.string :as s]
            [clojure.java.io :as io]))

(defn sort-actions [actions]
  (let [with-p (filter #(not (nil? (:priority %))) actions)
        without-p (filter #(nil? (:priority %)) actions)]
    (concat (sort-by #(vec (map % [:priority :description])) with-p)
            (sort-by :description without-p))))

(defn colorize-action [string priority]
  (cond (= priority \A) (red string)
        (= priority \B) (yellow string)
        (= priority \C) (blue string)
        (= priority \D) (green string)
        :default (cyan string)))

(defn format-actions [actions]
  (letfn [(format-id [id]
            (if (< id 10)
              (str \0 id)
              id))
          (format-action [{:keys [id priority description]}]
            (let [res (str (format-id id) " " (if priority (str "(" priority ") ")) description)]
              (if priority
                (colorize-action res priority)
                res)))]
    (s/join \newline
           (map format-action (sort-actions (filter #(not (% :done)) actions))))))

(defn print-actions [actions]
  (println (format-actions actions)))

(defn read-action
  "Create an action from a string read using the todo.txt format."
  [str nextid]
  (let [project? (fn [str] (= \+ (first str)))
        context? (fn [str] (= \@ (first str)))
        priority? (fn [str] (and (= 3 (count str))
                                (= \( (first str))
                                (= \) (first (rest (rest str))))))
        take-priority (fn [token]
                        (if (priority? token)
                          (first (rest token))))
        tokens (s/split str #"\s")
        is-done? (fn [tokens] (= "x" (first tokens)))
        action {:id nextid
                :tags (filter project? tokens)
                :contexts (filter context? tokens)}]
    (assoc action
      :done (is-done? tokens)
      :doneDate (if (is-done? tokens)
                  (first (rest tokens)))
      :priority (if (is-done? tokens)
                  (take-priority (first (rest (rest tokens))))
                  (take-priority (first tokens)))
      :description (s/join \  (remove priority? (if (is-done? tokens)
                                                  (rest (rest tokens))
                                                  tokens))))))

(defn read-actions
  "Create action structures from strings in the todo.txt format."
  [lines]
  (loop [rem lines res [] nextid 1]
    (if (empty? rem)
      res
      (recur (rest rem) (conj res (read-action (first rem) nextid)) (inc nextid)))))

(defn read-data
  "Read todo.txt actions from file."
  [file-name]
  (with-open [rdr (io/reader file-name)]
    (read-actions (filter (complement empty?) (line-seq rdr)))))

(defn write-action
  "Serialize actions into todo.txt strings."
  [task]
  (str (if (:done task) (str "x " (:doneDate task) " "))
       (if (:priority task) (str "(" (:priority task) ") "))
       (:description task)))

(defn write-data
  "Write todo.txt strings to a file."
  [tasks file-name]
  (spit file-name (s/join \newline (map write-action tasks))))
