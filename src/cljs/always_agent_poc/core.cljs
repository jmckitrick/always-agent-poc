(ns always-agent-poc.core
  (:require-macros
   [devcards.core :as dc :refer [defcard defcard-rg deftest]]
   [cljs.test :as t :refer [async is testing]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [always-agent-poc.events :as events]
            [always-agent-poc.views :as views]
            [always-agent-poc.config :as config]
            [sablono.core :as sab]
            [devcards.core]))

#_(defonce my-image (atom "https://clojure.org/images/clojure-logo-120b.png"))
(defonce my-image (atom "http://loremflickr.com/200/200/face,closeup/all"))
(defonce my-bg (atom "http://loremflickr.com/800/400/travel/all"))
(defonce my-bg2 (atom "http://loremflickr.com/350/350/travel/all"))

(defcard-rg agent-component-card
  "## Agent component"
  [views/agent-component my-bg my-image]
  my-bg
  {:inspect-data true
   :history true
   ;;:hidden true
   })

(defcard-rg bio-component-card
  "## Bio component"
  [views/bio-component]
  {})

(defonce my-deal (atom {:imageUrl @my-bg2 :dealUrl "#"}))

(defcard-rg deal-component-card
  "## Deal component"
  [views/deal-component @my-deal]
  my-deal
  {:inspect-data true
   ;;:hidden true
   })

(defcard-rg edit-text-card
  "## Edit text"
  [views/edit-text-component])

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:events/initialize-db])
  (dev-setup)
  (mount-root))
