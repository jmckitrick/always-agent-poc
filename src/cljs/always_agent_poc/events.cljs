(ns always-agent-poc.events
  (:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [re-frame.core :as rf]
   [always-agent-poc.db :as db]))

(comment
  (set! db/default-db {}))

(rf/reg-event-db
 :events/initialize-db
 (fn  [_ _]
   (js/console.log "Clear db")
   db/default-db))

(rf/reg-sub
 :subs/my-data
 (fn [db]
   (:my-event-arg db)))

(rf/reg-event-db
 :events/my-event-1
 (fn [db [_ arg1]]
   (js/console.log "Got my-event-1!" arg1)
   (assoc db :my-event-arg arg1)))

(rf/reg-sub
 :subs/name
 (fn [db]
   (:name db)))

(rf/reg-event-db
 :events/good-ajax
 (fn [db [_ data]]
   (js/console.log "Got good ajax")
   (assoc db :ajax-data data :spinner false)))

(rf/reg-event-fx
 :events/bad-ajax
 (fn [db [_ data]]
   (js/console.log "Got bad ajax" data)
   db))

(rf/reg-event-fx
 :events/ajax
 (fn [{:keys [db]} _]
   (js/console.log "Get ajax")
   {:db (assoc db :spinner true)
    :http-xhrio {:method :get
                 :uri "http://ip.jsontest.com/"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? :true})
                 :on-success [:events/good-ajax]
                 :on-failure [:events/bad-ajax]}}))

(rf/reg-sub
 :subs/ajax-data
 (fn [db]
   (:ajax-data db)))

(rf/reg-event-fx
 :events/ajax-tst
 (fn [{:keys [db]} _]
   (js/console.log "Get ajax tst")
   {:db (assoc db :spinner true)
    :http-xhrio {:method :get
                 :uri "http://thoragency.localhost/agent/tony/alwaysAgentInfo.json"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? :true})
                 :on-success [:events/good-ajax-tst]
                 :on-failure [:events/bad-ajax]}}))

(rf/reg-event-db
 :events/good-ajax-tst
 (fn [db [_ data]]
   (js/console.log "Got good ajax tst")
   (assoc db :ajax-data-tst data :spinner false)))

(rf/reg-sub
 :subs/ajax-data-tst
 (fn [db]
   (:ajax-data-tst db)))

(rf/reg-sub
 :subs/db
 (fn [db]
   (js/console.log "db" db)
   db))

(rf/reg-sub
 :subs/photo
 (fn []
   (rf/subscribe [:subs/ajax-data-tst]))
 (fn [data]
   (js/console.log "photoUrl" (:photoUrl data))
   (when (:photoUrl data)
     (str "http://thoragency.localhost" (:photoUrl data)))))

(rf/reg-sub
 :subs/gallery
 (fn []
   (rf/subscribe [:subs/ajax-data-tst]))
 (fn [data]
   (js/console.log "gallery" (:gallery data))
   (:gallery data)))

(rf/reg-event-db
 :events/good-deals
 (fn [db [_ data]]
   (js/console.log "Got good deals" data)
   (assoc db :deals data :spinner false)))

(rf/reg-event-fx
 :events/deal-destinations
 (fn [{:keys [db]} _]
   (js/console.log "Get deals")
   {:db (assoc db :spinner true)
    :http-xhrio {:method :get
                 :uri "http://thoragency.localhost/web-services/subsite-deals"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:events/good-deals]
                 :on-failure [:events/bad-ajax]}}))

(rf/reg-sub
 :subs/deals
 (fn [db]
   (get-in db [:deals :destinations])))

(rf/reg-event-db
 :events/edit-me
 (fn [db [_ target]]
   (js/console.log "Edit" target)
   (assoc db :edit (not (:edit db)) :target target)))

(rf/reg-sub
 :subs/edit-target
 (fn [db]
   [(:edit db) (:target db)]))

(rf/reg-event-db
 :events/update-name
 (fn [db [_ name]]
   (assoc db :name name)))

(rf/reg-sub
 :subs/name
 (fn [db]
   (:name db)))

(rf/reg-event-fx
 :events/load-gallery-data
 (fn [{:keys [db]} _]
   (js/console.log "Get gallery data...")
   {:db (assoc db :gallery-loading? true)
    :http-xhrio {:method :get
                 :uri "http://thoragency.localhost/admin/rest/profile/gallery"
                 ;;:with-credentials true
                 :timeout 60000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:events/good-gallery]
                 :on-failure [:events/bad-ajax]}}))

(rf/reg-event-db
 :events/good-gallery
 (fn [db [_ data]]
   (js/console.log "Successful gallery" data)
   (assoc db :gallery data :gallery-loading? false)))

(rf/reg-sub
 :subs/gallery-loading?
 (fn [db]
   (js/console.log "Sub gallery loading?")
   (:gallery-loading? db)))

(rf/reg-sub
 :subs/gallery-data
 (fn [db]
   (js/console.log "Sub gallery data")
   (:gallery db)))

(rf/reg-event-db
 :events/file-url
 (fn [db [_ data]]
   (assoc db :file-data data)))

(rf/reg-sub
 :subs/file-data
 (fn [db]
   #_(js/console.log "File data" (:file-data db))
   (:file-data db)))

(rf/reg-sub
 :subs/bio-text
 (fn [db]
   (:bio-text db)))

(rf/reg-event-db
 :events/bio-text
 (fn [db [_ text]]
   (assoc db :bio-text text)))

(rf/reg-sub
 :subs/tagline
 (fn [db]
   (:tagline db)))

(rf/reg-event-db
 :events/tagline
 (fn [db [_ tagline]]
   (assoc db :tagline tagline)))

(defn data-uri->blob [uri]
  ;; "data:image/png;base64,foobar"
  (let [byte-string (js/atob (get (.split uri ",") 1))
        mime-string (-> uri
                        (.split ",")
                        (get 0)
                        (.split ":")
                        (get 1)
                        (.split ";")
                        (get 0))
        ab (js/ArrayBuffer. (.-length byte-string))
        ia (js/Uint8Array. ab)]
    (doseq [i (range (.-length byte-string))]
      (aset ia i (.charCodeAt byte-string i)))
    (js/Blob. [ab] {:type mime-string})))

(rf/reg-event-db
 :events/good-gallery-add
 (fn [db]
   (js/console.log "Successful gallery add")
   db))

(rf/reg-event-fx
 :events/save-gallery-photo
 (fn [{:keys [db]} [_ data]]
   (js/console.log "Save gallery photo")
   (let [form-data (js/FormData.)
         blob (data-uri->blob data)]
     (js/console.log "Save gallery photo data" blob)
     (.append form-data "gallery-photo" blob)
     {:http-xhrio {:method :post
                   :uri "http://thoragency.localhost/admin/rest/profile/gallery"
                   :timeout 16000
                   :body form-data
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success [:events/good-gallery-add]
                   :on-failure [:events/bad-ajax]}})))
