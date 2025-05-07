(ns tg-bot.handlers.callbacks.create-order 
  (:require
   [tg-bot.db.repositories :refer [create-order]]
   [tg-bot.methods.remove-inline-keyboard :refer [remove-inline-keyboard]]
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.state :refer [chat-state]]))



(defn create-order-flow [{:keys [chat-id order message-id]}]
  (println "Order: " order "\n Chat id: " chat-id)
  (remove-inline-keyboard {:chat-id chat-id
                           :message-id message-id})
  (try 
    (if-let [order-id (create-order {:chat-id chat-id
                                    :order order})]
     (do 
       (swap! chat-state dissoc chat-id)
       (send-telegram-message {:chat-id chat-id
                               :message (str "Анкета успешно сохранена!\n
ID Анкеты: " order-id)}))
     (send-telegram-message {:chat-id chat-id
                             :message "Ошибка при сохранении анкеты"}))
    (catch Exception e
      (println "Error creating order:" (.getMessage e))
      (send-telegram-message {:chat-id chat-id
                            :message "Произошла ошибка при сохранении анкеты"}))))
