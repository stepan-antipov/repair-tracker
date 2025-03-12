(ns tg-bot.db.queries)


(defn insert-client [{:keys [phone-number name]}]
  (format "INSERT INTO clients (phone_number, name) VALUES (%s, %s)" phone-number name))
