(ns cluj.core
  (:require
   [clojure.tools.nrepl.server :refer [start-server stop-server]]
   [cider.nrepl :refer [cider-nrepl-handler]])
  (:gen-class))

(defn- hook-shutdown!
  [^java.lang.Runnable f]
  (doto (Runtime/getRuntime)
    (.addShutdownHook (Thread. f))))

(def port 64001)

;;-----------------------------------------------------------------------------

(defn new-repl
  []
  {:port port :handler cider-nrepl-handler})

(defn start-repl
  [repl]
  (assoc repl :server (start-server :port (:port repl) :handler (:handler repl))))

(defn stop-repl
  [repl]
  (when-let [s (:server repl)]
    (stop-server s))
  (dissoc repl :server))

(defn system
  []
  (try
    (let [lock (promise)
          r (start-repl (new-repl))]
      (hook-shutdown! #(do (stop-repl r)
                           (deliver lock :release)))
      @lock)
    (catch Throwable t
      (println "ERROR:" t))))

;;-----------------------------------------------------------------------------

(defn -main
  [& args]
  (println (format "Socket-based cider/repl available on port %s." port))
  (doto (Thread. system)
    (.start))
  (apply clojure.main/main args))
