(ns tg-bot.handlers.callbacks.cancel-order 
  (:require
   [tg-bot.methods.remove-inline-keyboard :refer [remove-inline-keyboard]]
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.state :refer [chat-state]]))


(defn cancel-order-flow [{:keys [chat-id message-id]}] 
  (remove-inline-keyboard {:chat-id chat-id
                           :message-id message-id})
  (swap! chat-state dissoc chat-id)
  (send-telegram-message {:chat-id chat-id
                          :message "Вы отменили заполнение анкеты"}))
