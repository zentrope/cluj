#!/usr/bin/env cluj

(ns clojure.playground)

(require '[clojure.tools.nrepl.server :refer [start-server stop-server]])
(require '[cider.nrepl :refer [cider-nrepl-handler]])

(defn sum
  [x & xs]
  (apply + x xs))

(defn mult
  [x & xs]
  (apply * x xs))

(defn minus
  [x & xs]
  (apply - x xs))

(defn- hook-shutdown!
  [^java.lang.Runnable f]
  (doto (Runtime/getRuntime)
    (.addShutdownHook (Thread. f))))

(defn main
  []
  (let [server (start-server :port 7777 :handler cider-nrepl-handler)
        lock (promise)]
    (println "REPL on localhost:7777, ^C to shutdown.")
    (hook-shutdown! #(do (println "Shutting down")
                         (stop-server server)
                         (deliver lock :release)))
    @lock
    (System/exit 0)))

(main)
