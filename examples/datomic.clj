#!/usr/bin/env cluj
;;-----------------------------------------------------------------------------
;; Let's pretend I want to do some sort of "survey" thing and use
;; datomic to store the survey templates and the data resulting from
;; the survey.
;;
;; In this example, I created a repl (invoked at the bottom) and
;; started it up with "cluj datomic.clj" which started up a REPL. In
;; Emacs, I invoked cider-connect to start workgin with it, then added
;; everything, evaluating as I went along.
;;
;; I image this might be an interesting way to develop a complicate
;; script. Just put a REPL in it, work out the details, then delete
;; the REPL code when you're done.
;;-----------------------------------------------------------------------------
;;

(require '[datomic.api :as d])
(require '[clojure.tools.nrepl.server :refer [start-server stop-server]])
(require '[cider.nrepl :refer [cider-nrepl-handler]])
(require '[clojure.pprint :refer [pprint]])
(require '[clojure.walk :refer [postwalk]])

;;-----------------------------------------------------------------------------

(defn repl
  []
  (let [server (start-server :port 7777 :handler cider-nrepl-handler)
        lock (promise)]
    (println "REPL on localhost:7777, ^C to shutdown.")
    (doto (Runtime/getRuntime)
      (.addShutdownHook (Thread. #(do (println "Shutting down")
                                      (stop-server server)
                                      (deliver lock :release)))))
    @lock
    (System/exit 0)))

;;-----------------------------------------------------------------------------
;; Some functions to make generating Datomic attribute definitions
;; tractable.
;; -----------------------------------------------------------------------------

(defn tempid
  []
  (d/tempid :db.part/user))

(defn enum
  [key]
  {:db/id (tempid) :db/ident key})

(defn part
  [key doc]
  {:db/doc doc
   :db/ident key
   :db/id (d/tempid :db.part/db)
   :db.install/_partition :db.part/db})

(defn attr
  [ident type cardinality doc]
  {:db/doc doc
   :db/ident ident
   :db/valueType (keyword "db.type" (name type))
   :db/cardinality (keyword "db.cardinality" (name cardinality))
   :db/id (d/tempid :db.part/db)
   :db.install/_attribute :db.part/db})

(defn pkey
  [& args]
  (assoc (apply attr args) :db/unique :db.unique/identity))

(defn uniq
  [& args]
  (assoc (apply attr args) :db/unique :db.unique/value))

(defn lstr
  [& args]
  (assoc (apply attr args) :db/isComponent true))

;;-----------------------------------------------------------------------------

(defn mk-attributes []
  [(pkey :program/id :uuid :one "Id")
   (attr :program/name :string :one "Name of the program")
   (attr :program/surveys :ref :many "Surveys")

   (pkey :survey/id :uuid :one "Id")
   (attr :survey/name :string :one "Name")
   (lstr :survey/questions :ref :many "Questions")

   (enum :question.type/text)
   (enum :question.type/radio)
   (enum :question.type/checkbox)
   (enum :question.type/matrix)

   (pkey :question/id        :uuid    :one  "Id")
   (attr :question/type      :ref     :one  "Type")
   (attr :question/context   :string  :one  "Question text")
   (attr :question/aspects   :ref     :many "Question(s)")
   (lstr :question/options   :ref     :many "Choices per aspect")
   (lstr :question/answers   :ref     :many "The answers")

   (attr :aspect/text        :string  :one  "Aspect of the q")

   (pkey :option/order       :long    :one "The presention order")
   (attr :option/text        :string  :one "The text of the option")

   (pkey :answer/id          :uuid    :one  "Id")
   (attr :answer/user        :string  :one  "Who answered (should be ref)")
   (attr :answer/date        :instant :one  "Date answered")
   (attr :answer/results     :ref     :many "Results (choices)")

   (pkey :result/id          :uuid    :one  "Result id")
   (attr :result/aspect      :string  :one  "The question")
   (attr :result/answer      :string  :one  "The answer")])

(defn seed-data
  []
  (let [[pid sid qid1] (repeatedly tempid)]
    [{:db/id pid
      :program/id (d/squuid)
      :program/name "A program"
      :program/surveys [sid]}
     {:db/id sid
      :survey/id (d/squuid)
      :survey/name "A survey"
      :survey/questions [qid1]}
     {:db/id qid1
      :question/type :question.type/radio
      ;; needless for "radio", but used for "matrix"
      :question/context "Company size?"
      :question/aspects [{:db/id (tempid)
                          :aspect/text "Company size?"}]
      :question/options [{:db/id (tempid)
                          :option/order 1
                          :option/text "1-10"}
                         {:db/id (tempid)
                          :option/order 2
                          :option/text "11-100"}]
      :question/answers [{:db/id (tempid)
                          :answer/id (d/squuid)
                          :answer/user "clarice@lambs.net"
                          :answer/date (java.util.Date.)
                          :answer/results [{:db/id (tempid)
                                            :result/id (d/squuid)
                                            :result/aspect "Company size?"
                                            :result/answer "1-10"}]}]}]))

;;-----------------------------------------------------------------------------

(def uri "datomic:mem://testdb")
(d/create-database uri)
(def conn (atom (d/connect uri)))

(defn recreate
  []
  (d/delete-database uri)
  (d/create-database uri)
  (reset! conn (d/connect uri))
  @(d/transact @conn (mk-attributes))
  @(d/transact @conn (seed-data)))

;;-----------------------------------------------------------------------------

(defn de-refs
  "Replace [:db/ident :value] pairs with just :value."
  [m]
  (postwalk #(if-let [v (:db/ident %)] v %) m))

(defn de-ns
  "Remove the namespace part of a keyword."
  [m]
  (postwalk #(if (keyword? %) (keyword (name %)) %) m))

(defn db
  [conn]
  (d/db conn))

(def pull-pattern
  '[:program/id
    :program/name
    {:program/surveys
     [:survey/id
      :survey/name
      {:survey/questions
       [:question/id
        :question/context
        {:question/aspects [:aspect/name]}
        {:question/options [:option/order :option/text]}
        {:question/answers [:answer/user
                            :answer/date
                            {:answer/results [:result/aspect
                                              :result/answer]}]}]}]}])

(defn find-programs
  [db]
  (-> (d/q '{:find [[(pull ?e pat) ...]]
             :in [$ pat]
             :where [[?e :program/id]]} db pull-pattern)
      de-refs
      de-ns))


(comment
  ;;
  ;; Result of:
  ;;   (find-programs (db @conn))
  ;;
  [{:id #uuid "566fbc33-2988-4468-b180-00d494af5263",
   :name "A program",
   :surveys
   [{:id #uuid "566fbc33-73a8-4892-b202-23bfe05cfb15",
     :name "A survey",
     :questions
     [{:context "Company size?",
       :options [{:order 1,
                  :text "1-10"}
                 {:order 2,
                  :text "11-100"}],
       :answers
       [{:user "clarice@lambs.net",
         :date #inst "2015-12-15T07:07:31.324-00:00",
         :results [{:aspect "Company size?",
                    :answer "1-10"}]}]}]}]}]
  ;;
  ;; So, given a program, I can get its surveys with a pull-pattern
  ;; that pulls just the survey (not any answers), or given a survey,
  ;; pull answers, etc, etc. Hm.
  ;;
  )



;;-----------------------------------------------------------------------------

(defonce r (repl))
