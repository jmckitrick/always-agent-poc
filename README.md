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

### 'Double Bundle'

The ['Double Bundle'][2] approach uses Webpack to build the
NPM dependencies into one JS file, and uses standard CLJS tools
to build the app code as a separate file.
Both are referenced in the HTML, with the NPM bundle first.
There are some drawbacks.

1. Must create synthetic namespaces for some CLJSJS dependencies.
1. Must use `get` on global object to access the object and use it.
1. Advanced compilation might not work yet.

### Shadow-CLJS

[Shadow-CLJS][4] is a NPM tool for compiling CLJS
using existing ClojureScript and JS tools. It does a better
job of handling external libraries, integrating with
and even replacing other tools. But there are tradeoffs.

1. A new tool is introduced into the build chain.
1. An unusual quoted `require` statement is needed.

## Development

Method | Additional Required Tools | Source Annotation
------ | ------------------------- | -----------------
CLJSJS | None                      | `_CLJSJS_`
Double Bundle | NPM, Webpack | `_DOUBLE_`
Shadow CLJS | NPM, Shadow-CLJS | `_SHADOW_`

### CLJSJS

No special instructions. See below for Figwheel startup.

### Double Bundle

```bash
npm i
npm run-script build
```

### Shadow-CLJS

Use Shadow-CLJS to build the app:

```bash
$ shadow-cljs compile
```

OR

```bash
$ shadow-cljs watch
```

### Run application in development mode (all methods):

```bash
lein clean
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### CIDER/EMACS

Put this in your Emacs config file:

```lisp
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

*NB: This step may no longer be necessary with current CIDER*

Now start a Figwheel REPL (see above), navigate to a CLJS file, and connect:

`cider-jack-in-clojurescript` or (`C-c M-J`)

To connect to older Shadow-CLJS:

1. Run `cider-connect` in emacs.
2. In the REPL when it is ready:

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
