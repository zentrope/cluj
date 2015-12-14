#!/usr/bin/env cluj

;; I'd like to package up code for deployment on FreeBSD without
;; having to build on FreeBSD. A package is just a tarball with some
;; metadata files and the actual files to be deployed. Part of the
;; metadata requires that the size of the files be calculated as well
;; as sha256 hashes for each file.

;; So, here's an example of that. So much easier that BASH. ;)

(require '[me.raynes.fs :as fs])
(require '[pandect.algo.sha256 :refer [sha256-file]])
(require '[clojure.data.json :as json])

(defn halt!
  [code reason]
  (println "error: " reason)
  (System/exit code))

(def the-pkg
  {:name "my-app"
   :origin "java/thing"
   :version "0.1.0_4"
   :comment "A java server"
   :maintainer "me@vanity.com"
   :www "http://github.com"
   :prefix "/"
   :flatsize 0
   :desc "An amazing server."
   :abi "FreeBSD:10:amd64"
   :arch "freebsd:10:x86:64"
   :deps {:openjdk8 {:origin "java/openjdk8" :version "8.60.24"}}
   :files {}
   :scripts {}})

(defn size-of
  [dir]
  (->> (fs/find-files* dir fs/file?)
       (reduce (fn [count file] (+ count (.length file))) 0)))

(defn plist
  [dir]
  (->> (fs/find-files* dir fs/file?)
       (map #(assoc {} :file %))
       (map #(assoc % :sha256 (sha256-file (:file %))))
       (reduce (fn [a v]
                 (assoc a (.getAbsolutePath (:file v)) (str "1$" (:sha256 v)))) {})))

(defn validate!
  [dir]
  (when (nil? dir)
    (halt! 1 "Must supply directory as first argument"))

  (when-not (fs/directory? dir)
    (halt! 2 (str "The file " dir " ought to be a directory."))))

(let [dir (first *command-line-args*)]
  (validate! dir)
  (let [manifest (-> the-pkg
                     (assoc :files (plist dir))
                     (assoc :flatsize (size-of dir)))]
    (println (json/write-str manifest))))
