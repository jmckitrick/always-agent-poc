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

(def my-atom (atom [1 2 3]))
#_
(defcard my-first-card
  (sab/html [:h1 "Devcards!"]))
#_
(deftest my-first-test
  "## Here is a test"
  (cljs.test/testing "context 1"
    (cljs.test/is (= 0 0))
    (cljs.test/is (= 1 1))
    (cljs.test/is (= 1 2))
    (cljs.test/is (= 2 2))))
#_
(defcard my-next-card
  "## Here is some markdown."
  @my-atom)
#_
(defcard another-card
  "## Try reagent"
  (dc/reagent views/button-component))
#_
(defcard another-card-1
  "## Try reagent"
  (dc/reagent views/button-component-0))
#_
(defcard another-card-2
  "## Try reagent"
  (dc/reagent views/my-data-component))
#_
(defcard-rg another-card-rg
  "## Try reagent"
  views/my-data-component
  nil
  {:inspect-data true
   :history true})

;;(defonce my-image (atom "https://clojure.org/images/clojure-logo-120b.png"))
(defonce my-image (atom "http://loremflickr.com/200/200/face,closeup/all"))
(defonce my-bg (atom "http://loremflickr.com/800/400/travel/all"))

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

(defonce my-deal (atom {:imageUrl @my-image :dealUrl "#"}))

(defcard-rg deal-component-card
  "## Deal component"
  [views/deal-component @my-deal]
  my-deal
  {:inspect-data true
   ;;:hidden true
   })

(defcard-rg edit-icon-component-card
  "## Edit icon component"
  [views/edit-icon-component -20 -20 1 false])

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
