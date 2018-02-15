(ns always-agent-poc.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [always-agent-poc.events :as events]
            [always-agent-poc.subs :as subs]))

(defn my-data-component []
  [:div
   [:div
    (pr-str @(re-frame.subs/subscribe [:subs/db]))]
   [:div
    (pr-str @(re-frame.subs/subscribe [:subs/my-data]))]
   [:div
    (pr-str @(re-frame.subs/subscribe [:subs/ajax-data]))]
   [:div
    (pr-str @(re-frame.subs/subscribe [:subs/ajax-data-tst]))]])

(defn button-component-0 []
  [:button
   {:on-click #(re-frame/dispatch [:events/initialize-db])}
   "Click me 0"])

(defn button-component []
  [:button
   {:on-click #(re-frame/dispatch [:events/my-event-1 99])}
   "Click me A"])

(defn button-component-2 []
  [:button
   {:on-click #(re-frame/dispatch [:events/ajax])}
   "Click me B"])

(defn button-component-3 []
  [:button
   {:on-click #(re-frame/dispatch [:events/ajax-tst])}
   "Click me C"])

(defn button-component-4 []
  [:button
   {:on-click #(re-frame/dispatch [:events/deal-destinations])}
   "Click me D"])

(defn profile-image-component [src]
  (when @src
    [:div
     [:img {:src @src
            :style {:position :relative
                    :display :block
                    :width 181
                    :height 175
                    :border-radius "2px 2px 0 0"}}]
     [:div {:style {:font-style :italic
                    :position :absolute
                    :bottom 270
                    :box-sizing :border-box
                    :width 181
                    :padding "5px 5px"
                    :color "#fff"
                    :background "rgba(57, 83, 108, 0.9)"}}
      "Your Agent Tony"]]))

(defn gallery-image-component [src]
  (when @src
    [:div
     [:img {:src @src}]]))

(defn deal-component []
  (when (seq @(re-frame/subscribe [:subs/deals]))
    [:div {:style {:clear :both :margin 20}}
     [:h2 "Featured Destinations"]
     [:h5 "Check out the following"]
     [:div.row-fluid
      (doall
       (for [deal @(re-frame/subscribe [:subs/deals])]
         ^{:key (:cityId deal)}
         [:span {:style {:margin 2}}
          [:a {:href (str "http://thoragency.localhost" (:dealUrl deal))}
           [:img {:src (str "http://thoragency.localhost" (:imageUrl deal))
                  :width 350
                  :height 350}]]]))]]))

(defn main-panel []
  (let [name (re-frame/subscribe [:subs/name])]
    [:div.container
     [my-data-component]
     [:div
      [button-component-0]]
     [:div
      [button-component]]
     [:div
      [button-component-2]]
     [:div
      [button-component-3]]
     [:div
      [button-component-4]]
     [:div {:style {:background (str "url(" @(re-frame/subscribe [:subs/gallery]) ")")
                    :position :relative
                    :width "100%"
                    :height 450
                    :margin 10
                    :background-position "center"
                    :background-size "cover"
                    :background-repeat "no-repeat"}}
      [:div {:style {:float :left}}
       [profile-image-component (re-frame/subscribe [:subs/photo])]]]
     #_[gallery-image-component (re-frame/subscribe [:subs/gallery])]
     [deal-component]
     #_[:div.row
      [:div.col-sm "Hello from " @name]
      [:div.col-sm "More content"]
      [:div.col-sm "Still more content"]
      [:div#my-id.col-sm {:style {:color :red}} "Still more content"]]]))
