(ns tg-bot.db.queries)


(defn insert-client [{:keys [phone-number name]}]
  ["INSERT INTO clients (phone_number, name) VALUES (?, ?)" phone-number name])


(defn insert-order [{:keys [client-id chat-id phone-model client-name diagnosis]}]  
  ["INSERT INTO orders (client_id, chat_id, phone_model, client_name, diagnosis) 
VALUES (?, ?, ?, ?, ?)
 RETURNING id" client-id chat-id phone-model client-name diagnosis]) 

;; TODO НЕВЕРНО сохраняется client_name, его тут быть не должно. Client должен получаться из таблицы clients по client-id 


(defn insert-photo [{:keys [order-id file-id]}]
  ["INSERT INTO photos (order_id, file_id) VALUES (?, ?)" order-id file-id])



(defn client-id-by-phone-number [phone-number]
  ["SELECT id FROM clients WHERE phone_number = ?" phone-number])



(defn order-by-id [{:keys [chat-id id]}] 
  ["SELECT orders.id, orders.phone_model, orders.diagnosis, orders.created_at,  clients.name, clients.phone_number, file_id  
    FROM orders
    INNER JOIN clients ON orders.client_id = clients.id 
    INNER JOIN photos ON orders.id = photos.order_id
    WHERE  orders.chat_id = ? AND orders.id = ?
    LIMIT 1" chat-id id])
