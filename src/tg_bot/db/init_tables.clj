(ns tg-bot.db.init-tables 
  (:require
   [clojure.java.jdbc :as jdbc]
   [tg-bot.db.credentials :refer [db]]))


(defn create-table-clients []
  (jdbc/execute! db
    "CREATE TABLE IF NOT EXISTS clients (
      id SERIAL PRIMARY KEY,  
      phone_number VARCHAR(15) NOT NULL,
      name VARCHAR(100),
      created_at timestamptz DEFAULT NOW() NOT NULL)"))



(defn create-table-orders []
  (jdbc/execute! db
   "CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    client_id INTEGER REFERENCES clients(id) ON DELETE CASCADE,
    chat_id BIGINT NOT NULL, 
    phone_model TEXT NOT NULL, 
    client_name TEXT NOT NULL,
    diagnosis TEXT NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"))


(defn create-table-photo []
  (jdbc/execute! db
   "CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(id) ON DELETE CASCADE, 
    file_id VARCHAR(255) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"))



(defn -main []
  (println 'init-tables)
  (create-table-clients)
  (create-table-orders)
  (create-table-photo))
