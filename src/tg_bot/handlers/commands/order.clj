(ns tg-bot.handlers.commands.order
  (:require 
   [tg-bot.methods.send-message :refer [send-telegram-message]]
   [tg-bot.state :refer [chat-state]]))



(def inline-keyboard
  {:inline_keyboard
   [[{:text "✅ Сохранить" :callback_data "/save_order"} 
     {:text "❌ Сброс" :callback_data "/cancel_order"}]]})



(defn form-order-message [{:keys [phone-model phone-number client-name diagnosis photo]}]
  (format 
    "Ваша анкета:\n\n📱 %s\n\n☎️ %s\n\n🙍‍♂️ %s\n\n🛠️ %s\n\n📷 %s" 
    phone-model phone-number client-name diagnosis photo))



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




(defn process-order [{:keys [chat-id text state-key]}]
  (swap! chat-state update-in [chat-id :order] assoc state-key text) ;; photo
  (let [{:keys [order]} (get @chat-state chat-id)
        {:keys [phone-model phone-number client-name diagnosis photo]} order] 
    (send-telegram-message {:chat-id chat-id
                            :message (form-order-message 
                                       {:phone-model phone-model
                                        :phone-number phone-number
                                        :client-name client-name
                                        :diagnosis diagnosis
                                        :photo photo})
                            :keyboard inline-keyboard})))



(defn handle-chat-input [{:keys [chat-id text]}] 
  (println (str "State " @chat-state)) 

  (case (get-in @chat-state [chat-id :state])
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
                                         :next-state-key :photo
                                         :message "Пришлите фото устройства"
                                         :text text})

    :photo
    (process-order {:chat-id chat-id
                    :text text 
                    :state-key :photo})
    (println "что-то пошло не так")))
