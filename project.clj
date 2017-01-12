(defproject zentrope.com/cluj "6"

  :description
  "bundle clojure libs to use for shell scripting"

  :url "https://githib.com/zentrope/cluj"

  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojure              "1.9.0-alpha14"]
   [org.clojure/core.async           "0.2.395"]
   [org.clojure/data.json            "0.2.6"]
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
   [com.stuartsierra/component       "0.3.2"]
   [com.hierynomus/sshj              "0.19.1"]
   [net.sf.expectit/expectit-core    "0.8.2"]
   [javax.mail/mail                  "1.5.0-b01"]
   [hiccup                           "1.0.5"]
   [com.datomic/datomic-free         "0.9.5544" :exclusions [org.slf4j/jcl-over-slf4j
                                                             org.slf4j/log4j-over-slf4j
                                                             org.slf4j/jul-to-slf4j
                                                             org.slf4j/slf4j-log4j12
                                                             org.slf4j/slf4j-api]]]

  :main nil

  :aliases {"updates" ["ancient" ":all" ":check-clojure" ":plugins" ":allow-qualified"]}

  :min-lein-version "2.7.1"

  :profiles
  {:uberjar {:aot :all}
   :dev     {:dependencies
             [[org.clojure/tools.nrepl "0.2.12"]]
             :plugins
             [[lein-ancient "0.6.10"]]}})
