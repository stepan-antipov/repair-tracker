(ns tg-bot.methods.send-message
  (:require
   [cheshire.core :as json]
   [clj-http.client :as client]
   [tg-bot.config :refer [telegram-token]]))



(defn send-telegram-message [{:keys [chat-id message keyboard]}] 
  (let [url (str "https://api.telegram.org/bot" telegram-token "/sendMessage") 
        default-params {:chat_id chat-id
                        :text message}
        params (if keyboard 
                 (assoc default-params :reply_markup (json/generate-string keyboard))
                 default-params)]
    (client/post url {:form-params params})))


