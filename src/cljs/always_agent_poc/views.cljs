(ns always-agent-poc.views
  (:require [re-frame.core :as re-frame]
            [always-agent-poc.subs :as subs]
            ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div "Hello from " @name]))
