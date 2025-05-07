(ns tg-bot.handlers.commands.start
  (:require
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.ui.keyboards :refer [start-keyboard]]))



(defn handle-start [chat-id] 
  (println "\n\n Send start \n\n")
  (send-telegram-message {:chat-id chat-id 
                          :message "Выберите опцию"
                          :keyboard start-keyboard}))
