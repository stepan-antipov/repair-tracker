(ns tg-bot.methods.remove-inline-keyboard
  (:require 
   [clj-http.client :as client]
   [tg-bot.config :refer [telegram-token]]))


(defn remove-inline-keyboard [{:keys [chat-id message-id]}]
  (let [url (str "https://api.telegram.org/bot" telegram-token "/editMessageReplyMarkup")
        params {:chat_id chat-id
                :message_id message-id
                :reply_markup {:inline_keyboard []}}]
    (client/post url {:form-params params
                      :content-type :json})))

