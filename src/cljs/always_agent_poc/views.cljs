(ns always-agent-poc.views
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [always-agent-poc.events :as events]
            [always-agent-poc.subs :as subs]
            [goog.object :as g]
            ;; This is the shadow-cljs method of npm module import.
            ;; They are not necessary with the double bundle method.
            ["react-avatar-editor" :as react-avatar-editor]
            ["react-image-gallery" :as react-image-gallery]))

(defn my-data-component []
  [:div
   [:div
    (pr-str @(rf/subscribe [:subs/db]))]
   [:div
    (pr-str @(rf/subscribe [:subs/my-data]))]
   [:div
    (pr-str @(rf/subscribe [:subs/ajax-data]))]
   [:div
    (pr-str @(rf/subscribe [:subs/ajax-data-tst]))]])

(defn button-component-0 []
  [:button
   {:on-click #(rf/dispatch [:events/initialize-db])}
   "Reset app db"])

(defn button-component []
  [:button
   {:on-click #(rf/dispatch [:events/my-event-1 99])}
   "Click me A"])

(defn button-component-2 []
  [:button
   {:on-click #(rf/dispatch [:events/ajax])}
   "Click me B"])

(defn button-component-3 []
  [:button
   {:on-click #(rf/dispatch [:events/ajax-tst])}
   "Load Always Agent Info"])

(defn button-component-4 []
  [:button
   {:on-click #(rf/dispatch [:events/deal-destinations])}
   "Load Deal Destinations"])

(defn toggle-edit [a target]
  (swap! a not)
  (rf/dispatch [:events/edit-me target]))

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
    (rf/dispatch [:events/update-name name])))

(defn edit-text-component []
  (let [txt (reagent/atom @(rf/subscribe [:subs/name]))]
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
      (let [[edit? target] @(rf/subscribe [:subs/edit-target])]
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
       (let [[edit target] @(rf/subscribe [:subs/edit-target])]
         (if (and
              (= true edit)
              (= :name target))
           [edit-text-component]
           (str "Your agent " @(rf/subscribe [:subs/agent-name]))))
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
  (when (seq @(rf/subscribe [:subs/deals]))
    [:div {:style {:clear :both :margin 20}}
     [:h2 "Featured Destinations"]
     [:h5 "Check out the following"]
     [:div.row-fluid
      (doall
       (for [deal @(rf/subscribe [:subs/deals])]
         ^{:key (:cityId deal)}
         [deal-component deal]))]]))

(defn agent-component [gallery-pic profile-pic edit-target]
  (when (and @gallery-pic @profile-pic)
    (let [[edit? target] @edit-target]
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
       [profile-image-component profile-pic]])))

(defn inline-editor [text on-change]
  (let [s (reagent/atom {:text text})]
    (fn [text on-change]
      (if (:editing? @s)
        [:form {:on-submit #(do
                              (.preventDefault %)
                              (swap! s dissoc :editing?)
                              (when on-change
                                (on-change (:text @s))))}
         [:input {:type :text :value (:text @s)
                  :on-change #(swap! s assoc
                                     :text (-> % .-target .-value))}]
         [:button "Save"]
         [:button {:on-click #(do (.preventDefault %)
                                  (swap! s dissoc :editing?))}
          "Cancel"]]
        [:span {:on-click #(swap! s assoc
                                  :editing? true
                                  :text text)}
         text [:sup "\u270e"]]))))

(defn bio-component [bio-tagline bio-text]
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
      [inline-editor @bio-tagline #(rf/dispatch [:events/tagline %])]]]
    [:p
     [inline-editor @bio-text #(rf/dispatch [:events/bio-text %])]]]])

(defn handle-file-change [e]
  (let [reader (js/FileReader.)
        file (aget (.-files (.-target e)) 0)]
    (js/console.log "Reader:" reader)
    (js/console.log "e:" e)
    (js/console.log "e.target:" (.-target e))
    (js/console.log "Files:" (.-files (.-target e)))
    (js/console.log "File:" (aget (.-files (.-target e)) 0))
    #_(set! (.-onload reader) #(js/console.log "Got file" e))
    #_(set! (.-onload reader) #(js/console.log "Got file" (.-target e)))
    (set! (.-onerror reader) #(js/console.log "Got file error" (.-target %)))
    #_(set! (.-onload reader) #(js/console.log "Got file" (.-result (.-target %))))
    (set! (.-onload reader) #(rf/dispatch [:events/file-url (.-result (.-target %))]))
    (js/console.log "Reader:" reader)
    (js/console.log "Reader onload:" (.-onload reader))
    (js/console.log "Reader onerror:" (.-onerror reader))
    (.readAsDataURL reader file)))

(defn gallery-delete []
  (js/console.log "Gallery delete!")
  (rf/dispatch [:events/delete-gallery-item]))

(defn avatar-editor-ex [editor-atom]
  "Show an avatar editor used as an image uploader.
For shadow-cljs:
[avatar-editor react-avatar-editor]
For double bundle:
[avatar-editor (g/get js/window \"ReactAvatarEditor\")]
NB: Unlike some npm components (react-image-gallery, for example)
this component is not found under 'default' property."
  (let [avatar-editor react-avatar-editor]
    [:div.user-avatar-container
     {:style {:position :relative
              ;;:border "1px solid black"
              ;; :height 250
              ;; :width 250
              :height 100
              :width 350
              }}
     [:div
      (when @(rf/subscribe [:subs/gallery-image-selected])
        [:span.fas.fa-trash {:style {:color "#CCC"
                                     :padding 10
                                     :position :absolute
                                     :top -8
                                     :right 50
                                     :width 65
                                     :height 55
                                     :font-size "3em"
                                     :display :inline-block}
                             :on-click #(gallery-delete)}])
      [:span.fas.fa-camera {:style {:color "#CCC"
                                    :padding 10
                                    :position :absolute
                                    :top -10
                                    :right 0
                                    :width 65
                                    :height 55
                                    :font-size "3em"
                                    :display :inline-block
                                    }}]
      [:input {:type :file :accept "image/*"
               :style {:position :absolute
                       :top 0
                       :right 0
                       :width 65
                       :height 55
                       :opacity 0.0
                       :cursor :pointer}
               :on-change #(handle-file-change %)}]]
     [:> avatar-editor {:image (or @(rf/subscribe [:subs/file-data])
                                   @(rf/subscribe [:subs/gallery-image-selected])
                                   @(rf/subscribe [:subs/photo])
                                   "http://loremflickr.com/200/200/face,closeup/all")
                        ;; :width 250
                        ;; :height 250
                        :height 100
                        :width 350
                        :border 0
                        :color [255 255 255 0.6]
                        :scale 1
                        :rotate 0
                        :onImageReady #(js/console.log "Ready!")
                        :onMouseUp #(js/console.log "Mouse up")
                        :ref #(do
                                (js/console.log "Set avatar editor atom to" %)
                                (reset! editor-atom %))}]]))

(defn select-image [e index]
  #_(js/console.log "Select image" index)
  (rf/dispatch [:events/select-image index]))

(defn image-gallery []
  "Show an image gallery.
For shadow-cljs:
[image-gallery react-image-gallery]
For double bundle:
[image-gallery (g/get js/window \"ReactImageGallery\")]
NB: Some npm components (react-image-gallery, for example)
are found under 'default' property."
  (let [image-gallery react-image-gallery]
    (js/console.log "Loading???" @(rf/subscribe [:subs/gallery-loading?]))
    [:div
     (if @(rf/subscribe [:subs/gallery-loading?])
       [:i.fas.fa-spinner.fa-spin.fa-8x]
       (if (empty? @(rf/subscribe [:subs/gallery-data]))
         [:i.fas.fa-exclamation-circle.fa-4x]
         [:> (.-default image-gallery) {:items @(rf/subscribe [:subs/gallery-data])
                                        ;;:renderItem #()
                                        :onThumbnailClick select-image
                                        :showFullscreenButton false
                                        :showPlayButton false}]))]))

(defn image-gallery-ex []
  (reagent/create-class
   {:reagent-render
    (fn []
      [image-gallery])
    :component-did-mount
    (fn []
      (js/console.log "Gallery mounted!")
      (rf/dispatch [:events/load-gallery-data]))}))

(defn save-gallery-photo [editor-atom]
  (js/console.log "Save gallery photo")
  (when-let [editor @editor-atom]
    (let [canvas (.getImage editor)
          data (.toDataURL canvas)]
      #_(js/console.log "Canvas" canvas)
      #_(js/console.log "Data" data)
      (rf/dispatch [:events/save-gallery-photo data]))))

(defn button-component-5 [editor-atom]
  [:button
   {:on-click #(save-gallery-photo editor-atom)}
   "Click to save gallery photo"])

(defn button-component-6 []
  [:button
   {:on-click #(rf/dispatch [:events/load-gallery-data])}
   "Load gallery"])

(defn button-delete-item []
  [:button
   {:on-click #(rf/dispatch [:events/delete-gallery-item])}
   "Delete selected item from gallery"])

(defn main-panel []
  (js/console.log "react-avatar-editor" react-avatar-editor)
  (js/console.dir "react-avatar-editor" react-avatar-editor)
  (js/console.log "react-image-gallery" react-image-gallery)
  (js/console.dir "react-image-gallery" react-image-gallery)
  [:div.container
   [:div
    [:div
     [button-component-0]]
    #_[:div
     [button-component]]
    #_[:div
     [button-component-2]]
    [:div
     [button-component-3]]
    [:div
     [button-component-4]]
    [:div
     [my-data-component]]
    [:br]]
   [agent-component
    (rf/subscribe [:subs/gallery])
    (rf/subscribe [:subs/photo])
    (rf/subscribe [:subs/edit-target])]
   [:br]
   [bio-component
    (rf/subscribe [:subs/tagline])
    (rf/subscribe [:subs/bio-text])]
   [deals-component]
   (let [editor-atom (atom nil)]
     [:div
      [avatar-editor-ex editor-atom]
      [:div
       [button-component-5 editor-atom]]])
   [:br]
   [image-gallery-ex]
   [:br]
   [button-component-6]
   [button-delete-item]])
