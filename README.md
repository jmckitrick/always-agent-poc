# always-agent-poc

A [re-frame][1] application to show an Always-Agent configuration page.

## Development Mode

### Double Bundle

```
npm i
npm run-script build
```

### Start Cider from Emacs:

Put this in your Emacs config file:

NB: This may no longer be necessary with current CIDER

```
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL:

`cider-jack-in-clojurescript` or (`C-c M-J`)

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### [For shadow-cljs][2]

Use shadow to build the app

$ shadow-cljs compile

OR

$ shadow-cljs watch

#### To connect in emacs

First, run `cider-connect`

Then when the REPL is ready:

```
(shadow.cljs.devtools.api/repl :app)
```

## TODO

- try devcards with the shadow build
- research re-frame subscription derefs
 and determine where best to place them when passing to components

[1]: https://github.com/Day8/re-frame
[2]: https://shadow-cljs.github.io/docs/UsersGuide.html
