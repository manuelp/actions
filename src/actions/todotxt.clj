(ns actions.todotxt
  (:require [clojure.string :as s]
   [clojure.java.io :as io]))

(defn format-id [id]
  (if (< id 10)
    (str \0 id)
    id))

(defn format-action [action]
  (str (format-id (action :id)) " " (action :description)))

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
        action {:id nextid
                :description str
                :tags (filter project? tokens)
                :contexts (filter context? tokens)}]
    (if (= "x" (first tokens))
      (assoc action :done true
             :doneDate (first (rest tokens))
             :priority (take-priority (first (rest (rest tokens)))))
      (assoc action :done false
             :priority (take-priority (first tokens))))))

(defn read-actions [lines]
  (loop [rem lines res [] nextid 1]
    (if (empty? rem)
      res
      (recur (rest rem) (conj res (read-action (first rem) nextid)) (inc nextid)))))

(defn write-data [tasks file-name]
  (spit file-name (s/join \newline (map #(get % :description) tasks))))

(defn read-data [file-name]
  (with-open [rdr (io/reader file-name)]
    (read-actions (filter (complement empty?) (line-seq rdr)))))
