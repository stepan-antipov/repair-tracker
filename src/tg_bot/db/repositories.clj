(ns tg-bot.db.repositories 
  (:require
   [clojure.java.jdbc :as jdbc]
   [tg-bot.db.credentials :refer [db]]
   [tg-bot.db.queries :refer [client-id-by-phone-number insert-client
                              insert-order insert-photo order-by-id]]))



(defn create-order [{:keys [chat-id order]}]
  (println "Saving to db")
  (let [{:keys [phone-model phone-number client-name diagnosis file-id]} order]
    
    (jdbc/with-db-transaction [tx db]

      ;; CLIENTS
      (jdbc/execute! tx (insert-client {:phone-number phone-number
                                        :name client-name}))

      (let [client (first (jdbc/query tx (client-id-by-phone-number phone-number)))
            client-id (:id client)]

        (println (str "Transaction: client-id " client-id)) 
        
        ;; ORDERS
        (let [order (first (jdbc/query tx (insert-order {:client-id client-id
                                                         :chat-id chat-id 
                                                         :phone-model phone-model
                                                         :client-name client-name
                                                         :diagnosis diagnosis})))
              order-id (:id order)]

          (println (str "Transactin: order_id " order-id))

          ;;PHOTOS
          (jdbc/execute! tx (insert-photo {:file-id file-id
                                           :order-id order-id}))
          order-id)))))



(defn get-order-by-id [{:keys [chat-id id]}]
  (println "\nOrder by id from db \n" chat-id " " id) 

  (let [int-id (Integer/parseInt id)
        order (first (jdbc/query db (order-by-id {:chat-id chat-id 
                                                  :id int-id})))]
    (println "Order by id: " order)
    order))


 


 

;; {:id 3, :client_id 10, :chat_id -1002288796045, :phone_model 1, :client_name 3, :diagnosis 4, :status nil, :created_at #inst "2025-03-23T09:25:11.894315000-00:00"}
