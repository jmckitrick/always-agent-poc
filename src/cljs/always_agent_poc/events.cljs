(ns always-agent-poc.events
  (:require [re-frame.core :as re-frame]
            [always-agent-poc.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))
