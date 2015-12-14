(ns cluj.core
  (:require [clojure.string :as s]
            [cemerick.pomegranate.aether :as a])
  (:gen-class))

(def repos
  {"java.net" "http://download.java.net/maven/2"
   "central" "http://repo1.maven.org/maven2/"
   "clojars" "http://clojars.org/repo"
   "sonatype" "http://oss.sonatype.org/content/repositories/releases"})

(defn classpath
  "Given a typical lein style dependency vector, return a classpath."
  ([spec]
   (classpath spec repos))
  ([spec repos]
   (let [sep (System/getProperty "path.separator")
         deps (a/resolve-dependencies :coordinates spec :repositories repos)
         files (a/dependency-files deps)]
     (s/join sep (map #(.getAbsolutePath %) files)))))

(def whatever
  ["This might do something in the future like"
   "print out the list of included libs and links"
   "to API documentation."])

(defn -main
  [& args]
  (println (s/join "\n" whatever)))
