(ns tg-bot.db.repositories 
  (:require
   [clojure.java.jdbc :as jdbc]
   [tg-bot.db.credentials :refer [db]]
   [tg-bot.db.queries :refer [insert-client]]))



(defn save-order [{:keys [chat-id order]}]
  (println "Saving to db")
  (let [{:keys [phone-model phone-number client-name diagnosis photo]} order]
    
    (jdbc/execute! db (insert-client {:phone-number phone-number
                                       :name client-name}))))


