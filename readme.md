# Cluj

Clojure bundled with a lot of handy "shell scripting" libs. Put the
uberjar and script on your path and you can have lots of handy
functionality for shell-scripting, or whatever.

## status

[Version 6](https://github.com/zentrope/cluj/releases/tag/v6)

You can get the release on the [releases](https://github.com/zentrope/cluj/releases)  page.

* Download the jar

* Download the script

* Put both the jar and the script on your `$PATH` and `chmod u+x
  cluj` the script. It expects the jar in the same directory as the
  script itself, or in `~/.cluj`.

Someday, the `cluj` script should self-install, and the jar itself
should contain some code to update itself, but that day isn't today.

## bundled libs

All of these are mashed together into an uberjar, the "main" of which
is the Clojure REPL.


```clojure
[[org.clojure/clojure              "1.9.0-alpha14"]
 [org.clojure/core.async           "0.2.395"]
 [org.clojure/data.json            "0.2.6"]
 [org.clojure/math.combinatorics   "0.1.4"]
 [org.clojure/tools.cli            "0.3.5"]
 [org.clojure/tools.nrepl          "0.2.12"]
 [org.clojure/tools.logging        "0.3.1"]
 [org.clojure/core.logic           "0.8.11"]
 [ch.qos.logback/logback-classic   "1.1.8"]
 [clj-time                         "0.13.0"]
 [com.cemerick/pomegranate         "0.3.1" :exclusions [org.codehaus.plexus/plexus-utils]]
 [org.codehaus.plexus/plexus-utils "3.0.24"]
 [http-kit                         "2.3.0-alpha1"]
 [me.raynes/fs                     "1.4.6"]
 [pandect                          "0.6.1"]
 [cider/cider-nrepl                "0.14.0" :exclusions [org.tcrawley/dynapath]]
 [integrant                        "0.1.5"]
 [integrant/repl                   "0.1.0"]
 [com.hierynomus/sshj              "0.19.1"]
 [net.sf.expectit/expectit-core    "0.8.2"]
 [javax.mail/mail                  "1.5.0-b01"]
 [hiccup                           "1.0.5"]
 [org.clojure/java.jdbc            "0.7.0-alpha1"]
 [org.postgresql/postgresql        "9.4.1212"]
 [com.h2database/h2                "1.4.193"]
 [mysql/mysql-connector-java       "6.0.5"]
 [org.xerial/sqlite-jdbc           "3.16.1"]
 [com.datomic/datomic-free         "0.9.5544" :exclusions [org.slf4j/jcl-over-slf4j
                                                           org.slf4j/log4j-over-slf4j
                                                           org.slf4j/jul-to-slf4j
                                                           org.slf4j/slf4j-log4j12
                                                           org.slf4j/slf4j-api]]]
```

## example script

Imagine you want to use a shell script to build your Clojure app
rather than a full blown system. The hardest part of that system is
resolving dependencies. =Cluj= includes =pomegranate=, so you can role
your own resolver:

```clojure
#!/usr/bin/env cluj

(require '[clojure.string :as s])
(require '[cemerick.pomegranate.aether :as a])

(def deps
  '[[org.clojure/clojure "1.8.0"]
    [me.raynes/fs "1.4.6"]
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
```

If the `cluj` script is on your `$PATH`, for instance, in `~/bin`, you
can run this script as:

    cluj deps.clj

Given that the first line is `!/usr/bin/env cluj` you can set the
execute permission on the script and run it directly:

    chmod u+x deps.clj
    ./deps.clj

again, as long as `cluj` is on your `$PATH`. With a few changes
(removing the "jar =>" print stuff), you could do something like:

    export CLASSPATH=`deps.clj`

In this way, you could implement a build system in `bash`.

## Change Log

### version 6 (snapshot)

* [ ] Use stub technique to avoid AOT.
* [ ] Add a simple emacs binding cider repl example thing.
* [x] Don't start up `cider/repl` when not invoking interactive repl
* [x] Add default `cider/repl` on `64001` when cluj repl invoked.
* [x] Updated all libraries to most recent versions.
* [x] Add `scripts/install` to install locally in `~/bin`.
* [x] Changing to incremental version number rather than semantic bs.
* [x] Removed plumatic schema in favor of Clojure 1.9 `clojure.spec`.
* [x] Removed component.
* [x] Added integrant and integrant/repl.
* [x] Added jdbc with mysql, postgres, h2 and sql-lite drivers.
* [x] Added `clojure/math.combinatorics`.

### version 0.5.0

* Added prismatic/plumatic schema.
* Added clojure.tools.logging.
* Added logback.
* Updated pomegranate version.

### version 0.4.1

* Added sshj.
* Added expectit.
* Added javamail
* Added hiccup
* Updated datomic to 0.9.5350
* Updated cider/nrepl "0.11.0"
* Updated http-kit "2.2.0-alpha1"

### version 0.4.0

* Updated to Clojure 1.8
* Added core.logic.
* Added Stuart Sierra's component library.

### version 0.3.0

* Updated to Clojure 1.8-RC4

### version 0.2.0

* Added datomic-free
* Added nrepl + cider
* Added a simple repl server example
* Added a datomic work session

### version 0.1.0

* Initial version
