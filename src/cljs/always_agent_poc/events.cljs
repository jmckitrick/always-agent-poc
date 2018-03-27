(ns always-agent-poc.events
  (:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [re-frame.core :as rf]
   [always-agent-poc.db :as db]
   [goog.object :as g]))

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
 :subs/agent-name
 (fn [db]
   (:agent-name db)))

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
   {:db (-> db
            (dissoc :gallery-fail)
            (assoc :gallery-loading? true))
    :http-xhrio {:method :get
                 :uri "http://thoragency.localhost/admin/rest/profile/gallery"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:events/good-gallery]
                 :on-failure [:events/bad-gallery]}}))

(rf/reg-event-db
 :events/good-gallery
 (fn [db [_ data]]
   (js/console.log "Successful gallery" data)
   (assoc db :gallery data :gallery-loading? false)))

(rf/reg-event-db
 :events/bad-gallery
 (fn [db [_ data]]
   (js/console.log "Failure to get gallery" data)
   (-> db
       (dissoc :gallery)
       (assoc :gallery-fail true :gallery-loading? false))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; blob functions from Igor and StackOverflow
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def blob-builder (or
                   (g/get js/window "BlobBuilder")
                   (g/get js/window "WebKitBlobBuilder")
                   (g/get js/window "MozBlobBuilder")
                   (g/get js/window "MSBlobBuilder")))
(def has-blob-constructor (and js/Blob
                               (try (do (js/Blob.) true)
                                    (catch ExceptionInfo e
                                      false))))
(def has-array-buffer-view-support (and has-blob-constructor
                                        js/Uint8Array
                                        (try (= (.-size (js/Blob. [(js/Uint8Array. 100)])))
                                             (catch ExceptionInfo e
                                               false))))

(def data-uri-re #"^data:((.*?)(;charset=.*?)?)(;base64)?,")

(defn data-uri->blob [uri]
  ;; "data:image/png;base64,foobar"
  ;; "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg'><filter ….2525 0 0 0.2525 0.2525 0.2525 0 0 0 0 0 1 0'/></filter></svg>#grayscale"
  ;; (str "data:image/gif;base64,R0lGODlhEAAQAMQAAORHHOVSKudfOulrSOp3WOyDZu6QdvCchPGolfO0o/XBs/fNwfjZ0frl3/zy7////"
  ;;      "wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAkAABAALAAAAAAQABAAAAVVICSOZGlCQA"
  ;;      "osJ6mu7fiyZeKqNKToQGDsM8hBADgUXoGAiqhSvp5QAnQKGIgUhwFUYLCVDFCrKUE1lBavAViFIDlTImbKC5Gm2hB0SlBCBMQiB0UjIQA7")
  (when-let [matches (re-find data-uri-re uri)]
    (js/console.log "Matched uri")
    (let [media-type (if (matches 2)
                       (matches 1)
                       (str "text/plain" + (or (matches 3) ";charset=US-ASCII")))
          is-base64 (seq (matches 4))
          data-string (get (.split uri ",") 1)
          byte-string (if is-base64
                        (js/atob data-string)
                        (js/decodeURIComponent data-string))
          mime-string (-> uri
                          (.split ",")
                          (get 0)
                          (.split ":")
                          (get 1)
                          (.split ";")
                          (get 0))
          ab (js/ArrayBuffer. (.-length byte-string))
          ia (js/Uint8Array. ab)]
      (js/console.log "Doing aset")
      (doseq [i (range (.-length byte-string))]
        (aset ia i (.charCodeAt byte-string i)))
      (if has-blob-constructor
        (js/Blob. [(if has-array-buffer-view-support
                     ia ab)] {:type mime-string})
        (let [bb (blob-builder.)]
          (.append bb ab)
          (.getBlob bb mime-string))))))

(rf/reg-event-fx
 :events/good-gallery-add
 (fn [{:keys [db] :as cofx}]
   (js/console.log "Successful gallery add")
   {:db db
    :dispatch [:events/load-gallery-data]}))

(rf/reg-event-fx
 :events/save-gallery-photo
 (fn [{:keys [db]} [_ data]]
   (js/console.log "Save gallery photo")
   (let [form-data (js/FormData.)
         blob (data-uri->blob data)]
     (when blob
       (js/console.log "Save gallery photo data" blob)
       (.append form-data "gallery-photo" blob)
       {:http-xhrio {:method :post
                     :uri "http://thoragency.localhost/admin/rest/profile/gallery"
                     :timeout 16000
                     :body form-data
                     :response-format (ajax/text-response-format)
                     :on-success [:events/good-gallery-add]
                     :on-failure [:events/bad-ajax]}}))))


(rf/reg-event-db
 :events/select-image
 (fn [db [_ index]]
   (js/console.log "Select image" index)
   (when-let [selected-image (get (:gallery db) index)]
     (assoc db :selected-image selected-image))))

(rf/reg-event-fx
 :events/good-gallery-delete
 (fn [{:keys [db]} _]
   (js/console.log "Successful gallery delete")
   {:db (dissoc db :selected-image)
    :dispatch [:events/load-gallery-data]}))

(rf/reg-sub
 :subs/gallery-image-selected
 (fn [db]
   (get-in db [:selected-image :original])))

(rf/reg-event-fx
 :events/delete-gallery-item
 (fn [{:keys [db]}]
   (when-let [item (:selected-image db)]
     (js/console.log "Delete gallery item" item)
     (let [enc-item (js/encodeURIComponent (:key item))]
       (js/console.log "Delete gallery encoded item" enc-item)
       {:db (assoc db :gallery-loading? true)
        :http-xhrio {:method :delete
                     :uri (str
                           "http://thoragency.localhost/admin/rest/profile/gallery/" enc-item)
                     ;;:with-credentials true
                     :timeout 60000
                     :format (ajax/text-request-format)
                     ;;:response-format (ajax/json-response-format {:keywords? true})
                     :response-format (ajax/text-response-format)
                     :on-success [:events/good-gallery-delete]
                     :on-failure [:events/bad-ajax]}}))))
