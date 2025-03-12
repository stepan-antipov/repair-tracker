(ns tg-bot.db.credentials 
  (:require [tg-bot.config :refer [dbname dbtype host user password]]))

;; config for protgresql database connection

(def db {:dbtype dbtype
         :dbname dbname
         :host host
         :user user
         :password password})
