# actions
This project is a little to-do list manager written in [Clojure](http://clojure.org). It's designed as:

- A CLI interface to manage a to-do list in a flat [todo.txt](https://github.com/ginatrapani/todo.txt-cli/wiki/The-Todo.txt-Format) file format.
- An API to manage to-do items.

## Usage

Just run the jar file and start to manage your *todo.txt* file. Use the **h** command to get a short help with a list of all possible commands. Note that **you need to have a todo.txt file in the directory where you run the jar file**.

## How to compile
Simply use [Leiningen](https://github.com/technomancy/leiningen) to create a "batteries included" JAR file:

```
lein uberjar
```

## License

Copyright (C) 2012 Manuel Paccagnella

Distributed under the Eclipse Public License, the same as Clojure.
