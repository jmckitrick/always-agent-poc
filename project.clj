(defproject always-agent-poc "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [day8.re-frame/http-fx "0.1.5"]
                 [cljs-ajax "0.7.3"]
                 [devcards "0.2.4"]
                 [sablono "0.8.3"]
                 [org.webjars/bootstrap "4.0.0"]]

  ;; _DOUBLE_
  ;; Exclude these dependencies, replacing them with
  ;; empty namespaces that mirror the structure.
  :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-ancient "0.6.15"]
            [lein-externs "0.1.6"]]

  :min-lein-version "2.7.1"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.9"]
                   [figwheel-sidecar "0.5.15"]
                   [com.cemerick/piggieback "0.2.2"]
                   [day8.re-frame/re-frame-10x "0.2.0"]]

    :plugins      [[lein-figwheel "0.5.15"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs" "src"]
     :figwheel     {:on-jsload "always-agent-poc.core/mount-root"}
     :compiler     {:main                 always-agent-poc.core
                    :output-to            "resources/public/js/app.js"
                    :output-dir           "resources/public/js"
                    :asset-path           "js"
                    :source-map-timestamp true
                    :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                    :preloads             [devtools.preload day8.re-frame-10x.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}

    {:id           "devcards"
     :source-paths ["src/cljs"]
     :figwheel     {:devcards true}
     :compiler     {:main                 always-agent-poc.core
                    :output-to            "resources/public/js/devcards_app.js"
                    :output-dir           "resources/public/js/devcards_out"
                    :asset-path           "js/devcards_out"
                    :source-map-timestamp true}}

    {:id           "min"
     :source-paths ["src/cljs" "src"]
     :compiler     {:main            always-agent-poc.core
                    :output-to       "resources/public/js/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
