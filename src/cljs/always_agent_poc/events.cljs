(ns always-agent-poc.events
  (:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [re-frame.core :as re-frame]
   [always-agent-poc.db :as db]))

(comment
  (set! db/default-db {}))

(re-frame/reg-event-db
 :events/initialize-db
 (fn  [_ _]
   (js/console.log "Clear db")
   db/default-db))

(re-frame/reg-sub
 :subs/my-data
 (fn [db]
   (:my-event-arg db)))

(re-frame/reg-event-db
 :events/my-event-1
 (fn [db [_ arg1]]
   (js/console.log "Got my-event-1!" arg1)
   (assoc db :my-event-arg arg1)))

(re-frame/reg-sub
 :subs/name
 (fn [db]
   (:name db)))

(re-frame/reg-event-db
 :events/good-ajax
 (fn [db [_ data]]
   (js/console.log "Got good ajax")
   (assoc db :ajax-data data :spinner false)))

(re-frame/reg-event-fx
 :events/bad-ajax
 (fn [db [_ data]]
   (js/console.log "Got bad ajax" data)
   db))

(re-frame/reg-event-fx
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

(re-frame/reg-sub
 :subs/ajax-data
 (fn [db]
   (:ajax-data db)))

(re-frame/reg-event-fx
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

(re-frame/reg-event-db
 :events/good-ajax-tst
 (fn [db [_ data]]
   (js/console.log "Got good ajax tst")
   (assoc db :ajax-data-tst data :spinner false)))

(re-frame/reg-sub
 :subs/ajax-data-tst
 (fn [db]
   (:ajax-data-tst db)))

(re-frame/reg-sub
 :subs/db
 (fn [db]
   (js/console.log "db" db)
   db))

(re-frame/reg-sub
 :subs/photo
 (fn []
   (re-frame/subscribe [:subs/ajax-data-tst]))
 (fn [data]
   (js/console.log "photoUrl" (:photoUrl data))
   (when (:photoUrl data)
     (str "http://thoragency.localhost" (:photoUrl data)))))

(re-frame/reg-sub
 :subs/gallery
 (fn []
   (re-frame/subscribe [:subs/ajax-data-tst]))
 (fn [data]
   (js/console.log "gallery" (:gallery data))
   (:gallery data)))

(re-frame/reg-event-db
 :events/good-deals
 (fn [db [_ data]]
   (js/console.log "Got good deals" data)
   (assoc db :deals data :spinner false)))

(re-frame/reg-event-fx
 :events/deal-destinations
 (fn [{:keys [db]} _]
   (js/console.log "Get deals")
   {:db (assoc db :spinner true)
    :http-xhrio {:method :get
                 :uri "http://thoragency.localhost/web-services/subsite-deals"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? :true})
                 :on-success [:events/good-deals]
                 :on-failure [:events/bad-ajax]}}))

(re-frame/reg-sub
 :subs/deals
 (fn [db]
   (get-in db [:deals :destinations])))

(re-frame/reg-event-db
 :events/edit-me
 (fn [db [_ target]]
   (js/console.log "Edit" target)
   (assoc db :edit (not (:edit db)) :target target)))

(re-frame/reg-sub
 :subs/edit-target
 (fn [db]
   [(:edit db) (:target db)]))
