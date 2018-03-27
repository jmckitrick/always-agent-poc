# Always Agent Proof-Of-Concept

A SPA written in ClojureScript.

Uses [re-frame][1] to drive an Always-Agent configuration page.

## Design considerations

There are at least 3 ways to integrate existing React components into ClojureScript:

1. CLJSJS
1. 'Double Bundle'
1. Shadow-CLJS

The first method is done completely with CLJS,
while the other two methods use `package.json`
and other NPM/JS tools.

### CLJSJS

[CLJSJS][2] wraps common JS libraries into CLJS components.
This approach is simple, but if the library or version
we want is not available, unless we want to try creating
a CLJSJS module, this method will not work.

Advantage: Simple and seamless to use, no JS needed.


### 'Double Bundle'

The ['Double Bundle'][2] approach uses Webpack to build the
NPM dependencies into one JS file, and uses standard CLJS tools
to build the app code as a separate file.
Both are referenced in the HTML, with the NPM bundle first.
There are some drawbacks.

1. Must create synthetic namespaces for some CLJSJS dependencies.
1. Must use `get` on global object to access the object and use it.
1. Advanced compilation might not work yet.

Advantage: Uses standard tools for JS and CLJS: NPM and cljs-build, respectively.


### Shadow-CLJS

[Shadow-CLJS][4] is a NPM tool for compiling CLJS
using existing ClojureScript and JS tools. It does a better
job of handling external libraries, integrating with
and even replacing other tools. But there are tradeoffs.

1. A new tool is introduced into the build chain.
1. A `shadow-cljs.edn` configuration file is needed.
1. An unusual quoted `require` statement is needed.

Advantages: One tool handles JS and CLJS, including dependencies.


## Development

Method | Additional Required Tools | Source Annotation | Development Endpoint
------ | ------------------------- | ----------------- | --------------------
CLJSJS | None                      | `_CLJSJS_`        | localhost:3449
Double Bundle | NPM, Webpack | `_DOUBLE_`              | localhost:3449
Shadow CLJS | NPM, Shadow-CLJS | `_SHADOW_`            | localhost:8080/shadow.html

### CLJSJS

No special instructions. See below for Figwheel startup.


### Double Bundle

```bash
npm i
npm run-script build
```

### Running application in Figwheel (development) mode (Methods 1 & 2):

```bash
lein clean
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).


### Running in development mode (Method 3):

### Shadow-CLJS

```bash
$ shadow-cljs watch
```

The command above gives a HUD similar to Figwheel.

Browse to http://localhost:8080/shadow.html

As you edit source code, changes are automatically pushed to the browser.
You will see the Shadow-CLJS spinner when this happens. Errors will
also be displayed in the browser and the console, just like Figwheel.


### CIDER/EMACS

CIDER will connect to Figwheel apps out of the box with `cider-jack-in-clojurescript`.

Shadow-CLJS will connect as well, with just a bit more work:

Put this in your Emacs config file:

```lisp
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

*NB: This step may no longer be necessary with current CIDER*

Now start a `watch` build (see above), navigate to a CLJS file, and connect:

To connect to older CIDER:

1. Run `cider-connect` in emacs.
1. Respond when prompted to enter the hostname and port.
1. In the CIDER REPL when it is ready, run this command:

```
(shadow.cljs.devtools.api/repl :app)
```

## TODO

- try devcards with the shadow build
- research re-frame subscription derefs
 and determine where best to place them when passing to components

[1]: https://github.com/Day8/re-frame
[2]: https://cljsjs.github.io
[3]: https://github.com/pesterhazy/double-bundle
[4]: https://shadow-cljs.github.io/docs/UsersGuide.html
