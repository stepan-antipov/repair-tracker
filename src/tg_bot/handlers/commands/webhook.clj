(ns tg-bot.handlers.commands.webhook 
  (:require
   [cheshire.core :as json]
   [ring.util.response :refer [response]]
   [tg-bot.core :refer [wrapped-handlers]]))


(defn handle-webhook [request] 
  (if (= (:request-method request) :post)

    (let [payload (json/parse-string (slurp (:body request)) true)
          {:keys [message callback_query]} payload]
      
      (wrapped-handlers {:message message 
                         :callback-query callback_query}) 

      (println "Payload:  " payload "\n")
      (println "Callback query " callback_query)

      (response "OK"))

    (response "Метод не поддерживается 405")))
