(ns tg-bot.handlers.commands.search-orders
  (:require
   [tg-bot.db.repositories :refer [get-order-by-id]]
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.methods.send-photo :refer [send-photo]]
   [tg-bot.state :refer [chat-state]]
   [tg-bot.ui.keyboards :refer [search-keyboard]]
   [tg-bot.ui.messages :refer [form-order-message]]))



;; (defn form-order-message [{:keys [phone-model phone-number client-name diagnosis]}]
;;   (format 
;;     "Ваша анкета:\n\n📱 %s\n\n☎️ %s\n\n🙍‍♂️ %s\n\n🛠️ %s" 
;;     phone-model phone-number client-name diagnosis))



;; (defn update-search-orders-state-and-send-message [{:keys [chat-id state-key next-state-key message text]}]
;;   (swap! chat-state update-in [chat-id :order] assoc state-key text)
;;   (swap! chat-state assoc-in [chat-id :state] next-state-key)
;;   (send-telegram-message {:chat-id chat-id
;;                           :message message}))



;; MAIN FUNCTION

(defn handle-search-orders [chat-id]
  (swap! chat-state assoc chat-id {:state :order-search 
                                   :search-params {}})
  (send-telegram-message {:chat-id chat-id
                          :message "Выберите параметры для поиска:"
                          :keyboard search-keyboard}))



(defn handle-order-by-id [chat-id]
  (swap! chat-state assoc-in [chat-id :state] :order-by-id)
  (send-telegram-message {:chat-id chat-id
                          :message "Введите ID заказа:"}))



;; (defn orders-by-phone [chat-id]
;;   (swap! chat-state assoc-in [chat-id :state] :orders-by-phone)
;;   (send-telegram-message {:chat-id chat-id
;;                           :message "Введите телефон клиента:"}))


;; (defn orders-by-model [chat-id]
;;   (swap! chat-state assoc-in [chat-id :state] :orders-by-model)
;;   (send-telegram-message {:chat-id chat-id
;;                           :message "Введите модель устройства:"})) 


;; (defn orders-by-phone [chat-id]
;;   (swap! chat-state assoc-in [chat-id :state] :orders-by-name)
;;   (send-telegram-message {:chat-id chat-id
;;                           :message "Введите имя клиента:"}))


;; (defn update-search-orders-state-and-send-message [{:keys [chat-id state-key next-state-key message text]}] 
;;   ;;(swap! chat-state assoc-in [chat-id :state] next-state-key)
;;   (swap! chat-state update-in [chat-id :search-params] assoc state-key text)
;;   (send-telegram-message {:chat-id chat-id
;;                           :message message}))




(defn process-order-by-id [{:keys [chat-id id]}]


  ;; TODO тут нужна грамонтая обработка состояния 
  (swap! chat-state assoc chat-id {:state nil})

  (let [{:keys [id phone_model diagnosis created_at name phone_number file_id]}
        (get-order-by-id {:chat-id chat-id 
                               :id id})] 
    

    (send-telegram-message {:chat-id chat-id
                            :message (form-order-message 
                                       {:phone-model phone_model
                                        :phone-number phone_number
                                        :client-name name
                                        :diagnosis diagnosis})})
    (send-photo {:chat-id chat-id 
                 :file-id file_id})))

;; TODO добавить а form-order дату и ID







(defn handle-order-search-input [{:keys [chat-id text action]}] 
  (println (str "Search order state " @chat-state)) 

  (case action
    :order-by-id
    
    (process-order-by-id {:chat-id chat-id
                          :state-key :order-by-id 
                          :id text})

    (println "что-то пошло не так")))

