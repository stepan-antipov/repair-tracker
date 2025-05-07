(ns tg-bot.methods.send-photo
  (:require 
   [clj-http.client :as client]
   [tg-bot.config :refer [telegram-token]]))



(defn send-photo [{:keys [chat-id file-id]}] 
  (let [url (str "https://api.telegram.org/bot" telegram-token "/sendPhoto") 
        params {:chat_id chat-id
                :photo file-id}]
    (client/post url {:form-params params})))


