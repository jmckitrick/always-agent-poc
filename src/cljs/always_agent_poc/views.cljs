(ns always-agent-poc.views
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]])
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
    [:div.profile-section {:style {:float :left}}
     [:div.profile-image
      {:style {:position :relative
               :display :block
               :width 181
               :height 175
               :border-radius "2px 2px 0 0"}}
      [:img {:style {:max-width "100%"}
             :src @src}]
      [:div {:style {:font-size "3em" :color :blue}
             :on-click #(re-frame/dispatch [:events/edit-me])}
       [:i.fas.fa-edit {:style {:position :absolute
                                :bottom "85%"
                                :right -20}}]]
      [:div {:style {:font-style :italic
                     :position :absolute
                     :bottom 0
                     :box-sizing :border-box
                     :width "100%"
                     :padding "5px 7px"
                     :color "#fff"
                     :background "rgba(57, 83, 108, 0.9)"}}
       "Your agent Tony Stark"
       [:div {:style {:font-size "2em" :color :blue}}
        [:i.fas.fa-edit {:style {:position :absolute
                                 :bottom "50%"
                                 :right -20
                                 }}]]]]]))

(defn deal-component [{:keys [dealUrl imageUrl] :as deal}]
  (let [url (if (re-find #"^http" imageUrl)
              imageUrl
              (str "http://thoragency.localhost" imageUrl))]
    [:span {:style {:margin 2}}
     [:a {:href dealUrl}
      [:img {:src url
             :width 350
             :height 350}]]]))

(defn deals-component []
  (when (seq @(re-frame/subscribe [:subs/deals]))
    [:div {:style {:clear :both :margin 20}}
     [:h2 "Featured Destinations"]
     [:h5 "Check out the following"]
     [:div.row-fluid
      (doall
       (for [deal @(re-frame/subscribe [:subs/deals])]
         ^{:key (:cityId deal)}
         [deal-component deal]))]]))

(defn agent-component [gallery-pic profile-pic]
  [:div.profile-row
   {:style {:background (str "url(" @gallery-pic ")")
            :position :relative
            ;:width "100%"
            :height 450
            ;:margin 10
            ;:background-position "center"
            :background-size "cover"
            ;:background-repeat "no-repeat"
            }}
   [:i.fas.fa-edit {:style {:position :absolute
                            ;;:position :relative
                            ;:float :right
                            ;;:top "5%";"85%"
                            :top -30
                            :right -30
                            ;;:left 200
                            :font-size "3em" :color :blue}}]
   [profile-image-component profile-pic]])

(defn bio-component [bio]
  [:div {:style {:width "100%"
                 :padding-top 39}}
   [:div.agent-contact {:style {:float :right
                                :width "30%"
                                :margin-top 58
                                :max-width 290
                                :border "1px solid #d4d4d4"
                                :box-shadow "0 1px 3px -1px rgba(0, 0, 0, 0.44)"}}
    [:strong {:style {:font-size 15
                      :font-style :italic
                      :font-weight 400
                      :display :block
                      :margin "10px 0 0 10px"
                      :color "#2a5e8d"}}
     "Contacts & Social Networks"]
    [:address {:style {:margin-bottom 0
                       :padding "4px 10px 9px"
                       :display :block
                       :font-style :normal}}
     [:a {:style {:display :block
                  :margin-top 8
                  :margin-bottom 7
                  :color "#274e6f"}
          :href "#"}
      [:i.fa.fa-envelope {:style {:display :inline-block
                                   :box-sizing :border-box
                                   :margin-right 7
                                   :text-align :left}}]
      [:span {:style {:display :inline-block
                      :box-sizing :border-box
                      :margin-right 7
                      :text-align :left}}
       "Email"]
      [:span {:style {:display :inline-block
                      :box-sizing :border-box
                      :text-align :left}}
       "tony.stark@tstllc.net"]]]
    ]
   [:div.bio-section {:style {:width "63%"}}
    [:h3 "About your agent"]
    [:strong {:style {:font-size 15
                      :font-style :italic
                      :display :block
                      :margin-bottom 18
                      :color "#2a5e8d"}}
     "Been there, loved it!"]
    [:p "Here is my bio with a bunch of details"]]])

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
     [agent-component
      (re-frame/subscribe [:subs/gallery])
      (re-frame/subscribe [:subs/photo])]
     [bio-component {}]
     [deals-component]
     #_[:div.row
      [:div.col-sm "Hello from " @name]
      [:div.col-sm "More content"]
      [:div.col-sm "Still more content"]
      [:div#my-id.col-sm {:style {:color :red}} "Still more content"]]]))
