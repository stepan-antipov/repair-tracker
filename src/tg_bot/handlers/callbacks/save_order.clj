(ns tg-bot.handlers.callbacks.save-order 
  (:require
   [tg-bot.db.repositories :refer [save-order]]
   [tg-bot.methods.remove-inline-keyboard :refer [remove-inline-keyboard]]
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.state :refer [chat-state]]))



(defn save-order-flow [{:keys [chat-id order message-id]}]
  (println "Order: " order "\n Chat id: " chat-id)
  (remove-inline-keyboard {:chat-id chat-id
                           :message-id message-id})
  (save-order {:chat-id chat-id
               :order order})
  (swap! chat-state dissoc chat-id)
  (send-telegram-message {:chat-id chat-id
                          :message "Анкета успешно сохранена!"}))


