#!/usr/bin/env cluj

(require '[cluj.core :as cluj])
(require '[clojure.string :as s])
(require '[cemerick.pomegranate.aether :as a])
(require '[clojure.pprint :refer (pprint)])

(def deps
  '[[org.clojure/clojure "1.8.0-RC3"]
    [me.raynes/fs "1.4.6" :excluse [org.clojure/clojure]]
    [http-kit "2.1.19"]])

(def repos
  {"central" {:url "http://repo1.maven.org/maven2/"}
   "sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"}
   "java.net" {:url "http://download.java.net/maven/2"}
   "clojars" {:url "http://clojars.org/repo"}})

(defn jar-list
  [deps repos]
  (-> (a/resolve-dependencies :coordinates deps :repositories repos)
      (a/dependency-files)))

(defn class-path
  [deps repos]
  (->> (jar-list deps repos)
       (map #(.getAbsolutePath %))
       (s/join (System/getProperty "path.separator"))))

;; Print path to each jar
(doseq [jar (sort (jar-list deps repos))]
  (println "jar =>" (.getAbsolutePath jar)))

;; Dump the classpath
(println "classpath:")
(println (class-path deps repos))
