(ns tg-bot.handlers.commands.start
  (:require
   [tg-bot.methods.send-message :refer [send-telegram-message]]))


(def inline-keyboard
  {:inline_keyboard
   [;; [{:text "🔎 Найти анкету" :callback_data "/search_order"} 
    ;;  {:text "📞 Проверить клиента" :callback_data "/check_client"}]
    [;; {:text "✏️ Редактировать анкету" :callback_data "/edit_order"}
     {:text "📌 Информация" :callback_data "/info"}]
    [{:text "📝 Создать анкету" :callback_data "/order"}]]})


(defn handle-start [chat-id] 
  (println "\n\n Send start \n\n")
  (send-telegram-message {:chat-id chat-id 
                          :message "Выберите опцию"
                          :keyboard inline-keyboard}))
