(ns maria.ns-utils
  (:require [maria.eval :as e]))

(defn builtin-ns? [s]
  (and (not= s 'maria.user)
       (re-find #"^(?:re-view|maria|cljs|re-db|clojure)" (name s))))

(defn analyzer-ns [c-state ns]
  (get-in c-state [:cljs.analyzer/namespaces ns]))

(defn user-namespaces [c-state]
  (->> (keys (:cljs.analyzer/namespaces c-state))
       (filter (complement builtin-ns?))))

(defn add-$macros-suffix [sym]
  (symbol (str sym "$macros")))

(defn elide-quote [x]
  (cond-> x
          (and (seq? x) (= 'quote (first x))) (second)))

(defn resolve-sym
  "Resolve a symbol into fully qualified name. Returns vector of [namespace, name] as symbols."
  [c-state c-env sym]
  (let [n (:name (e/resolve-var c-state c-env sym))]
    (mapv symbol [(namespace n) (name n)])))

(defn resolve-var
  "Simplified resolve-var fn, looks up `def` in compiler state."
  [c-state c-env sym]
  (let [[namespace name] (resolve-sym c-state c-env sym)]
    (or (get-in @c-state [:cljs.analyzer/namespaces namespace :defs name])
        (get-in @c-state [:cljs.analyzer/namespaces (add-$macros-suffix namespace) :defs name]))))