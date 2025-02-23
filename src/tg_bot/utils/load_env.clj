(ns tg-bot.utils.load-env
  (:require [clojure.string :as string]))

(defn load-env [file]
  (->> 
    (slurp file)
    (string/split-lines)
    (map #(string/split % #"=" 2))
    (into {})))

(def env (load-env ".env")) 

