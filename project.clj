(defproject actions "1.0.3-SNAPTSHOT"
  :description "A simple actions manager with the todo.txt plain text format."
  :url "https://github.com/manuelp/actions"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-swank "1.4.4"]]
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [bronsa/colorize "0.1.2"]]
  :dev-dependencies [[lein-marginalia "0.7.0"]]
  :main actions.console)
