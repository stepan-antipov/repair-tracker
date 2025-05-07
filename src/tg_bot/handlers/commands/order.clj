(ns tg-bot.handlers.commands.order
  (:require
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.methods.send-photo :refer [send-photo]]
   [tg-bot.state :refer [chat-state]]
   [tg-bot.ui.keyboards :refer [save-or-cancel-keyboard]]
   [tg-bot.ui.messages :refer [form-order-message]]))



(defn handle-order [chat-id]
  (swap! chat-state assoc chat-id {:state :phone-model 
                                   :order {}})
  (send-telegram-message {:chat-id chat-id
                          :message "Введите модель телефона"}))


(defn update-chat-state-and-send-message [{:keys [chat-id state-key next-state-key message text]}]
  (swap! chat-state update-in [chat-id :order] assoc state-key text)
  (swap! chat-state assoc-in [chat-id :state] next-state-key)
  (send-telegram-message {:chat-id chat-id
                          :message message}))




(defn process-order [{:keys [chat-id file-id state-key]}]
  (swap! chat-state update-in [chat-id :order] assoc state-key file-id) ;; photo
  (let [{:keys [order]} (get @chat-state chat-id)
        {:keys [phone-model phone-number client-name diagnosis]} order] 
    (send-photo {:chat-id chat-id 
                 :file-id file-id})
    (send-telegram-message {:chat-id chat-id
                            :message (form-order-message 
                                       {:phone-model phone-model
                                        :phone-number phone-number
                                        :client-name client-name
                                        :diagnosis diagnosis
                                        :file-id file-id})
                            :keyboard save-or-cancel-keyboard})))



(defn handle-chat-input [{:keys [chat-id text file-id action]}] 
  (println (str "State " @chat-state)) 

  (case action
    :phone-model 
    (update-chat-state-and-send-message {:chat-id chat-id
                                         :state-key :phone-model
                                         :next-state-key :phone-number
                                         :message "Введите номер телефона клиента"
                                         :text text})

    :phone-number
    (update-chat-state-and-send-message {:chat-id chat-id
                                         :state-key :phone-number
                                         :next-state-key :client-name
                                         :message "Введите имя клиента"
                                         :text text})

    :client-name 
    (update-chat-state-and-send-message {:chat-id chat-id
                                         :state-key :client-name
                                         :next-state-key :diagnosis
                                         :message "Результаты диагностики + На что договорились"
                                         :text text})

    :diagnosis
    (update-chat-state-and-send-message {:chat-id chat-id
                                         :state-key :diagnosis
                                         :next-state-key :file-id
                                         :message "Пришлите фото устройства"
                                         :text text})

    :file-id
    (process-order {:chat-id chat-id
                    :file-id file-id 
                    :state-key :file-id})
    (println "что-то пошло не так")))
