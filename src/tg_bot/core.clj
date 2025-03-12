(ns tg-bot.core
  (:gen-class)
  (:require
   [cheshire.core :as json]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.util.response :refer [response]]
   [tg-bot.db.init-tables :as init-tables]
   [tg-bot.handlers.callbacks.cancel-order :refer [cancel-order-flow]]
   [tg-bot.handlers.callbacks.save-order :refer [save-order-flow]]
   [tg-bot.handlers.commands.info :refer [handle-info]]
   [tg-bot.handlers.commands.order :refer [handle-chat-input handle-order]]
   [tg-bot.handlers.commands.start :refer [handle-start]]
   [tg-bot.state :refer [chat-state]]))


(defn handle-message [message] 
  (let [text (get message :text)
        chat-id (get-in message [:chat :id])
        state? (boolean (get @chat-state chat-id))]
    (println "Chat ID:" chat-id)
    (println (str "Chat state" @chat-state))
    (println (str "Inside the chat:" (get @chat-state chat-id)))
    (cond (= text "/start")
          (handle-start chat-id)
          state?
          (handle-chat-input {:chat-id chat-id 
                              :text text}))))


(defn handle-callback [callback-query] 
  (let [{:keys [message data]} callback-query 
        chat-id (-> message :chat :id)
        {:keys [order]} (get @chat-state chat-id)
        message-id (:message_id message)] 
    (case data
      "/info" (handle-info chat-id)
      "/order" (handle-order chat-id)
      "/save_order" (save-order-flow {:chat-id chat-id
                                      :order order
                                      :message-id message-id})
      "/cancel_order" (cancel-order-flow {:chat-id chat-id
                                          :message-id message-id})
      
     ;"/search_order" (handle-search chat-id)
      )))



(defn wrapped-handlers [{:keys [message callback-query]}] 
  (if callback-query 
    (handle-callback callback-query)
    (handle-message message)))


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



(defn app [request]
  (println "Request: " request "\n")
  (case (:uri request)
    "/webhook" (handle-webhook request)
    (response "Not found 404")))



(def wrapped-app (-> #'app wrap-reload)) ;; TODO fix before prod



(defn -main [& args]
  (println "Сервер запущен на порту 4000")
  (if (= (first args) "init-tables")
    (init-tables/-main)
    (run-jetty wrapped-app {:port 4000})))
