(ns tg-bot.state)


;; {:chat-id {:state :phone-model or :order-search 
;;            :orders {...} 
;;            :search-params {...} }


(def chat-state (atom {})) 
