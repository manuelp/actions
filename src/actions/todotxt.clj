(ns actions.todotxt
  (:require [clojure.string :as s]
   [clojure.java.io :as io]))

(defn format-id [id]
  (if (< id 10)
    (str \0 id)
    id))

(defn format-action [{:keys [id priority description]}]
  (str (format-id id) " " (if priority (str "(" priority ") ")) description))

(defn take-with-priority [actions]
  (filter #(not (nil? (:priority %))) actions))

(defn take-without-priority [actions]
  (filter #(nil? (:priority %)) actions))

(defn sort-actions [actions]
  (let [with-p (take-with-priority actions)
        without-p (take-without-priority actions)]
    (concat (sort-by #(vec (map % [:priority :description])) with-p)
            (sort-by :description without-p))))

(defn format-actions [actions]
  (s/join \newline
            (map format-action (sort-actions (filter #(not (% :done)) actions)))))

(defn print-actions [actions]
  (println (format-actions actions)))

(defn project? [str]
  (= \+ (first str)))

(defn context? [str]
  (= \@ (first str)))

(defn priority? [str]
  (and (= 3 (count str))
       (= \( (first str))
       (= \) (first (rest (rest str))))))

(defn take-priority [token]
  (if (priority? token)
    (first (rest token))))

(defn read-action
  "Create an action from a string read using the todo.txt format."
  [str nextid]
  (let [tokens (s/split str #"\s")
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

(defn read-actions [lines]
  (loop [rem lines res [] nextid 1]
    (if (empty? rem)
      res
      (recur (rest rem) (conj res (read-action (first rem) nextid)) (inc nextid)))))

(defn write-action [task]
  (str (if (:done task) (str "x " (:doneDate task) " "))
       (if (:priority task) (str "(" (:priority task) ") "))
       (:description task)))

(defn write-data [tasks file-name]
  (spit file-name (s/join \newline (map write-action tasks))))

(defn read-data [file-name]
  (with-open [rdr (io/reader file-name)]
    (read-actions (filter (complement empty?) (line-seq rdr)))))
