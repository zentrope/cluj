(ns cluj.stub
  ;; This stub is the only AOT compiled part of the system, even
  ;; when running in an uberjar context.
  (:gen-class))

(defn -main
  [& args]
  (require 'cluj.core)
  (apply (resolve 'cluj.core/-main) args))
