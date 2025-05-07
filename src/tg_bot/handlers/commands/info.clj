(ns tg-bot.handlers.commands.info
  (:require
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.ui.messages :refer [info-message]]))



(defn handle-info [chat-id] 
  (println "\n\n Send info \n\n")
  (send-telegram-message {:chat-id chat-id
                          :message info-message}))
