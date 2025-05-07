(ns tg-bot.core
  (:gen-class)
  (:require
   [cheshire.core :as json]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.util.response :refer [response]]
   [tg-bot.db.init-tables :as init-tables]
   [tg-bot.handlers.callbacks.cancel-order :refer [cancel-order-flow]]
   [tg-bot.handlers.callbacks.create-order :refer [create-order-flow]]
   [tg-bot.handlers.commands.info :refer [handle-info]]
   [tg-bot.handlers.commands.order :refer [handle-chat-input handle-order]]
   [tg-bot.handlers.commands.search-orders :refer [handle-order-by-id
                                                   handle-order-search-input
                                                   handle-search-orders]]
   [tg-bot.handlers.commands.start :refer [handle-start]]
   [tg-bot.state :refer [chat-state]]))


(defn handle-message [message] 
  (let [text (get message :text)
        file-id (get-in message [:photo 1 :file_id])
        chat-id (get-in message [:chat :id])
        action (get-in @chat-state [chat-id :state])]
    (println "USER MESSAGE :" message "\n")
    (println "Chat ID:" chat-id "\n")
    (println (str "Chat state" @chat-state "\n"))
    
    (println (str "File-id:   " file-id "\n"))
    (cond (= text "/start")
          (handle-start chat-id)
          (contains? #{:phone-model :phone-number :client-name :diagnosis :file-id} action)
          (handle-chat-input {:chat-id chat-id 
                              :text text
                              :file-id file-id
                              :action action})
          (contains? #{:order-by-id} action)
          (handle-order-search-input {:chat-id chat-id 
                                      :text text 
                                      :action action})
          )))


(defn handle-callback [callback-query] 
  (let [{:keys [message data]} callback-query 
        chat-id (-> message :chat :id)
        {:keys [order]} (get @chat-state chat-id)
        message-id (:message_id message)] 
    (case data
      "/info" (handle-info chat-id)
      "/order" (handle-order chat-id)
      "/create_order" (create-order-flow {:chat-id chat-id
                                          :order order
                                          :message-id message-id})
      "/cancel_order" (cancel-order-flow {:chat-id chat-id
                                          :message-id message-id})
      
      "/search_order" (handle-search-orders chat-id)
      "/order_by_id" (handle-order-by-id chat-id)
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


;; ;; {:update_id 239340238, 
;; :message {:message_id 433, :from {:id 914014303, :is_bot false, :first_name Степан, :last_name Антипов, :username clojure_sith, :language_code ru}, 
;;           :chat {:id -1002288796045, :title Архив 2.0, :type supergroup}, :date 1741719442, 
;;           :photo [{:file_id AgACAgIAAyEFAASIbEGNAAIBsWfQh5KAqpgKAVV5vMrKDfQbOMQ4AAK48jEbr12ISq4mjUOmLMLCAQADAgADcwADNgQ, :file_unique_id AQADuPIxG69diEp4, :file_size 3325, :width 90, :height 90} {:file_id AgACAgIAAyEFAASIbEGNAAIBsWfQh5KAqpgKAVV5vMrKDfQbOMQ4AAK48jEbr12ISq4mjUOmLMLCAQADAgADbQADNgQ, :file_unique_id AQADuPIxG69diEpy, :file_size 45442, :width 320, :height 320} {:file_id AgACAgIAAyEFAASIbEGNAAIBsWfQh5KAqpgKAVV5vMrKDfQbOMQ4AAK48jEbr12ISq4mjUOmLMLCAQADAgADeAADNgQ, :file_unique_id AQADuPIxG69diEp9, :file_size 86112, :width 640, :height 640}]}}
