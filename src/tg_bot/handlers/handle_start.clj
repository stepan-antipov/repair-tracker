(ns tg-bot.handlers.handle-start
  (:require
   [tg-bot.methods.send-message :refer [send-telegram-message]]))


(def inline-keyboard
  {:inline_keyboard
   [[{:text "🔎 Найти анкету" :callback_data "/search_worksheet"} 
     {:text "📞 Проверить клиента" :callback_data "/check_client"}]
    [{:text "✏️ Редактировать анкету" :callback_data "/edit_worksheet"}
     {:text "📌 Информация" :callback_data "/info"}]
    [{:text "📝 Создать анкету" :callback_data "/worksheet"}]]})


(defn handle-start [chat-id] 
  (println "\n\n Send start \n\n")
  (send-telegram-message {:chat-id chat-id 
                          :message "Выберите опцию"
                          :keyboard inline-keyboard}))
