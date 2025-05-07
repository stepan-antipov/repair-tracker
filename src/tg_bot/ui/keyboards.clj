(ns tg-bot.ui.keyboards)



(def start-keyboard
  {:inline_keyboard
   [[{:text "🔎 Найти анкету" :callback_data "/search_order"}] 
    ;;  {:text "📞 Проверить клиента" :callback_data "/check_client"}
    [;; {:text "✏️ Редактировать анкету" :callback_data "/edit_order"}
     {:text "📌 Информация" :callback_data "/info"}]
    [{:text "📝 Создать анкету" :callback_data "/order"}]]})


(def save-or-cancel-keyboard
  {:inline_keyboard
   [[{:text "✅ Сохранить" :callback_data "/create_order"} 
     {:text "❌ Сброс" :callback_data "/cancel_order"}]]})


(def search-keyboard
  {:inline_keyboard
   [[{:text "ID заказа" :callback_data "/order_by_id"}] ;; in progress
    ;; [{:text "Модель телефона" :callback_data "/order_by_model"}]
    ;; [{:text "Номер клиента" :callback_data "/order_by_phone"}]
    ;; [{:text "Имя клиента" :callback_data "/order_by_name"}]
    ;; [{:text "Начать поиск" :callback_data "/confirm_search"}]
    ]})


