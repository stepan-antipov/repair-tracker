(ns tg-bot.core
  (:gen-class)
  (:require
   [cheshire.core :as json]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.util.response :refer [response]]
   [ring.middleware.reload :refer [wrap-reload]]
   [tg-bot.handlers.handle-info :refer [handle-info]]
   [tg-bot.handlers.handle-start :refer [handle-start]]))


(defn handle-message [message] 
  (let [text (get message :text)
        chat-id (get-in message [:chat :id])]
    (when (= text "/start")
      (handle-start chat-id))))


(defn handle-callback [callback-query] 
  (let [message (:message callback-query) 
        data (:data callback-query)
        chat-id (-> message :chat :id)] 
    (case data
        "/info" (handle-info chat-id)
        ;"/worksheet" (handle-worksheet chat-id)
        ;"/search_worksheet" (handle-search chat-id)
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

      (println payload)
      (println callback_query)

      (response "OK"))

    (response "Метод не поддерживается 405")))



(defn app [request]
  (case (:uri request)
    "/webhook" (handle-webhook request)
    (response "Not found 404")))



(def wrapped-app (-> #'app wrap-reload)) ;; TODO fix before prod



(defn -main []
  (println "Сервер запущен на порту 4000")
  (run-jetty wrapped-app {:port 4000}))



;; {:update_id 239340005, :callback_query {:id 5842050594249827690, :from {:id 1360208400, :is_bot false, :first_name Vadik, :last_name alfavir.orig, :language_code ru}, :message {:message_id 97, :from {:id 8185482677, :is_bot true, :first_name RepairTracker, :username RepairTrackerBot}, :chat {:id -1002288796045, :title Архив 2.0, :type supergroup}, :date 1740323023, :text Тут будет текст, который пользователь бота будет видеть впервые, а также краткое описание для кнопок в клавиатуре, :reply_markup {:inline_keyboard [[{:text 📌 Информация, :callback_data /info} {:text 📞 Проверить клиента, :callback_data /check_client}] [{:text ✏️ Редактировать анкету, :callback_data /edit_worksheet} {:text 🔎 Найти анкету, :callback_data /search_worksheet}] [{:text 📝 Создать анкету, :callback_data /worksheet}]]}}, :chat_instance -57937855276544147
;; 31, :data /info}} 



;; {:update_id 239340014, :message {:message_id 103, :from {:id 914014303, :is_bot false, :first_name Степан, :last_name Антипов, :username clojure_sith, :language_code ru}, :chat {:id -1002288796045, :title Архив 2.0, :type supergroup}, :date 1740335489, :text /start, :entities [{:offset 0, :length 6, :type bot_command}]}}
