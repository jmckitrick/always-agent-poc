(ns always-agent-poc.views
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [always-agent-poc.events :as events]
            [always-agent-poc.subs :as subs]
            [goog.object :as g]
            #_[ImageGallery]
            #_[AvatarEditor]
            #_[AvatarEditor2]
            #_[webpack.bundle]
            #_[react-image-gallery :as gallery-1]
            #_[react-avatar-editor :as editor-1]
            #_["react-avatar-editor" :as editor-2]
            #_["react-image-gallery" :as gallery-2]))

(defonce edit (reagent/atom false))

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

(defn toggle-edit [a target]
  (swap! a not)
  (re-frame/dispatch [:events/edit-me target]))

(defn edit-icon-component [top right size target]
  (let [edit? (reagent/atom false)]
    (fn [top right size target]
      (let [style {:style
                   {:position :absolute
                    :top top
                    :right right}}]
        [:div {:style {:font-size (if (= size :medium) "2em" "3em")
                       :color :blue}
               :on-click #(toggle-edit edit? target)}
         (if-not @edit?
           [:i.far.fa-edit style]
           [:i.far.fa-save style])]))))

(defn update-text [txt el]
  (let [name (-> el .-target .-value)]
    (reset! txt name)
    (re-frame/dispatch [:events/update-name name])))

(defn edit-text-component []
  (let [txt (reagent/atom @(re-frame/subscribe [:subs/name]))]
    (fn []
      [:input
       {:type :text
        :size 20
        :value @txt
        :on-change #(update-text txt %)}])))

(defn profile-image-component [src]
  (when @src
    [:div.profile-section {:style {:float :left}}
     [:div.profile-image
      {:style {:position :relative
               :display :block
               :width 181
               :height 175
               ;:border-radius "2px 2px 2px 2px"
               }}
      (let [[edit? target] @(re-frame/subscribe [:subs/edit-target])]
        [:img {:style {:max-width "100%"
                       :box-sizing :border-box
                       :border (if (and edit? (= :profile target))
                                 "2px solid blue" "2px solid transparent")}
               :src @src}])
      [edit-icon-component -20 -20 :medium :profile]
      [:div {:style {:font-style :italic
                     :position :absolute
                     :bottom 0
                     :box-sizing :border-box
                     :width "100%"
                     :padding "5px 7px"
                     :color "#fff"
                     :background "rgba(57, 83, 108, 0.9)"}}
       (let [[edit target] @(re-frame/subscribe [:subs/edit-target])]
         (if (and
              (= true edit)
              (= :name target))
           [edit-text-component]
           "Your agent Tony Stark"))
       [edit-icon-component -20 -20 :medium :name]]]]))

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
  (let [[edit? target] @(re-frame/subscribe [:subs/edit-target])]
    [:div.profile-row
     {:style {:background (str "url(" @gallery-pic ")")
              :position :relative
              :width "100%"
              :height 450
                                        ;:margin 10
              :background-position "center"
              :background-size "cover"
              :background-repeat "repeat"
              :border (if (and edit? (= :gallery target))
                        "2px solid blue" "2px solid transparent")}}
     [edit-icon-component -30 -30 :large :gallery]
     #_[:i.fas.fa-edit {:style {:position :absolute
                                ;;:position :relative
                                        ;:float :right
                                ;;:top "5%";"85%"
                                :top -30
                                :right -30
                                ;;:left 200
                                :font-size "3em" :color :blue}}]
     [profile-image-component profile-pic]]))

(defn bio-component [bio]
  [:div.bio.noTwid {:style {:width "100%"
                            :padding-top 39
                            :float :none
                            :overflow :hidden
                            :display :block}}
   [:div.agent-contact {:style {:float :right
                                :width "30%"
                                :margin-top 58
                                :margin-bottom 20
                                :word-wrap :break-word
                                :color "#274e6f"
                                :max-width 290
                                :border "1px solid #d4d4d4"
                                :box-shadow "0 1px 3px -1px rgba(0, 0, 0, 0.44)"
                                :position :relative}}
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
          :href "tel:770-555-1212"}
      [:i.fas.fa-phone {:style {:display :inline-block
                                :box-sizing :border-box
                                :margin-right 7
                                :text-align :left}}]
      [:span {:style {:display :inline-block
                      :box-sizing :border-box
                      :margin-right 7
                      :text-align :left}}
       "Phone"]]
     [:a {:style {:display :block
                  :margin-top 8
                  :margin-bottom 7
                  :color "#274e6f"}
          :href "#"}
      [:i.far.fa-envelope {:style {:display :inline-block
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
    [:span.social {:style {:padding "5px 10px"
                           :border-top "1px solid #d4d4d4"
                           :display :inherit}}
     [:a {:href "https://www.facebook.com" :style {:margin-right 15}}
      [:i.fab.fa-facebook]]
     [:a {:href "https://twitter.com" :style {:margin-right 15}}
      [:i.fab.fa-twitter]]
     [:a {:href "https://www.linkedin.com" :style {:margin-right 15}}
      [:i.fab.fa-linkedin-in]]
     [:a {:href "https://plus.google.com" :style {:margin-right 15}}
      [:i.fab.fa-google-plus-g]]]]
   [:div.bio-section {:style {:width "63%"}}
    [:h3 "About your agent"]
    [:strong {:style {:font-size 15
                      :font-style :italic
                      :display :block
                      :margin-bottom 18
                      :color "#2a5e8d"}}
     [:div {:style {:position :relative}}
      "Been there, loved it!"
      [edit-icon-component 0 100 :medium :tagline]]]
    [:p "Here is my bio with a bunch of details"]]])

(def gallery-adapter (reagent/adapt-react-class "react-image-gallery"))
(def gallery-adapted (reagent/adapt-react-class "ImageGallery"))

(defn experimental-stuff []
  (let [;;avatar-editor (aget js/window "deps" "react-avatar-editor")
        avatar-editor (g/get js/window "ReactAvatarEditor")
        ;;image-gallery (aget js/window "deps" "react-image-gallery")
        image-gallery (g/get js/window "ReactImageGallery")
        ]
    [:div
     "Editor"
     [:> avatar-editor {:image "http://loremflickr.com/200/200/face,closeup/all"
                        :width 200
                        :height 200
                        :border 50
                        :color [255 255 255 0.6]
                        :scale 1
                        :rotate 0
                        :onImageReady #(js/console.log "Ready!")
                        :onMouseUp #(js/console.log "Mouse up")
                        }]
     "Gallery 0"
     [:> (.-default image-gallery) {:items [{:original "http://lorempixel.com/1000/600/nature/1/"
                                             :thumbnail "http://lorempixel.com/250/150/nature/1/"}
                                            {:original "http://lorempixel.com/1000/600/nature/2/"
                                             :thumbnail "http://lorempixel.com/250/150/nature/2/"}
                                            {:original "http://lorempixel.com/1000/600/nature/3/"
                                             :thumbnail "http://lorempixel.com/250/150/nature/3/"}]
                                    :renderItem #()
                                    :showFullscreenButton false
                                    :showPlayButton false}]
     #_"Gallery 1"
     #_[gallery-1]
     #_[gallery-adapter {}]
     #_"Gallery 2"
     #_[gallery-adapted {}]
     #_(react/createElement "react-image-gallery")
     #_(react/createElement "react-avatar-editor")]))

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
     [:br]
     #_[agent-component
      (re-frame/subscribe [:subs/gallery])
      (re-frame/subscribe [:subs/photo])]
     #_[bio-component {}]
     #_[deals-component]
     #_[:div.row
      [:div.col-sm "Hello from " @name]
      [:div.col-sm "More content"]
      [:div.col-sm "Still more content"]
        [:div#my-id.col-sm {:style {:color :red}} "Still more content"]]
     [experimental-stuff]]))
