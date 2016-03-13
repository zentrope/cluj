(defproject zentrope.com/cluj "0.4.1"
  :description
  "bundle clojure libs to use for shell scripting"

  :url "https://githib.com/zentrope/cluj"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/core.async "0.2.374"]
   [org.clojure/data.json "0.2.6"]
   [org.clojure/tools.cli "0.3.3"]
   [org.clojure/tools.nrepl "0.2.12"]

   [org.clojure/core.logic "0.8.10"]
   [clj-time "0.11.0"]
   [com.cemerick/pomegranate "0.3.0" :exclusions [org.codehaus.plexus/plexus-utils]]
   [http-kit "2.2.0-alpha1"]
   [me.raynes/fs "1.4.6"]
   [pandect "0.5.4"]
   [org.codehaus.plexus/plexus-utils "3.0.22"]
   [cider/cider-nrepl "0.11.0"]
   [com.stuartsierra/component "0.3.1"]
   [com.jcraft/jsch "0.1.53"]
   [javax.mail/mail "1.5.0-b01"]
   [hiccup "1.0.5"]
   [com.datomic/datomic-free "0.9.5350" :exclusions [org.slf4j/jcl-over-slf4j
                                                     org.slf4j/jul-to-slf4j
                                                     org.slf4j/log4j-over-slf4j
                                                     org.slf4j/slf4j-log4j12]]]

  :main nil

  :aliases {"updates" ["ancient" ":all" ":check-clojure" ":plugins" ":allow-qualified"]}

  :clean-targets ^{:protect false}
  ["resources"       ;; bug in leiningen, auto-creates these dirs if not present
   "dev-resources"
   "test"
   :target-path]

  :min-lein-version "2.6.1"

  :profiles
  {:uberjar {:aot :all}
   :dev {:dependencies
         [[org.clojure/tools.nrepl "0.2.12"]]

         :plugins
         [[lein-ancient "0.6.8"]]}})
