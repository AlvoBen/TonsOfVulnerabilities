﻿/** vim: et:ts=4:sw=4:sts=4
 * @license RequireJS 2.1.8 Copyright (c) 2010-2012, The Dojo Foundation All Rights Reserved.
 * Available via the MIT or new BSD license.
 * see: http://github.com/jrburke/requirejs for details
 */

var requirejs, require, define;
(function(global) {
    var req, s, head, baseElement, dataMain, src, interactiveScript, currentlyAddingScript, mainScript, subPath, version = '2.1.8',
        commentRegExp = /(\/\*([\s\S]*?)\*\/|([^:]|^)\/\/(.*)$)/mg,
        cjsRequireRegExp = /[^.]\s*require\s*\(\s*["']([^'"\s]+)["']\s*\)/g,
        jsSuffixRegExp = /\.js$/,
        currDirRegExp = /^\.\//,
        op = Object.prototype,
        ostring = op.toString,
        hasOwn = op.hasOwnProperty,
        ap = Array.prototype,
        apsp = ap.splice,
        isBrowser = !! (typeof window !== 'undefined' && navigator && window.document),
        isWebWorker = !isBrowser && typeof importScripts !== 'undefined',
        readyRegExp = isBrowser && navigator.platform === 'PLAYSTATION 3' ? /^complete$/ : /^(complete|loaded)$/,
        defContextName = '_',
        isOpera = typeof opera !== 'undefined' && opera.toString() === '[object Opera]',
        contexts = {}, cfg = {}, globalDefQueue = [],
        useInteractive = false;

    function isFunction(i) {
        return ostring.call(i) === '[object Function]'
    }
    function isArray(i) {
        return ostring.call(i) === '[object Array]'
    }
    function each(a, f) {
        if (a) {
            var i;
            for (i = 0; i < a.length; i += 1) {
                if (a[i] && f(a[i], i, a)) {
                    break
                }
            }
        }
    }
    function eachReverse(a, f) {
        if (a) {
            var i;
            for (i = a.length - 1; i > -1; i -= 1) {
                if (a[i] && f(a[i], i, a)) {
                    break
                }
            }
        }
    }
    function hasProp(o, p) {
        return hasOwn.call(o, p)
    }
    function getOwn(o, p) {
        return hasProp(o, p) && o[p]
    }
    function eachProp(o, f) {
        var p;
        for (p in o) {
            if (hasProp(o, p)) {
                if (f(o[p], p)) {
                    break
                }
            }
        }
    }
    function mixin(t, a, f, d) {
        if (a) {
            eachProp(a, function(v, p) {
                if (f || !hasProp(t, p)) {
                    if (d && typeof v !== 'string') {
                        if (!t[p]) {
                            t[p] = {}
                        }
                        mixin(t[p], v, f, d)
                    } else {
                        t[p] = v
                    }
                }
            })
        }
        return t
    }
    function bind(o, f) {
        return function() {
            return f.apply(o, arguments)
        }
    }
    function scripts() {
        return document.getElementsByTagName('script')
    }
    function defaultOnError(e) {
        throw e
    }
    function getGlobal(v) {
        if (!v) {
            return v
        }
        var g = global;
        each(v.split('.'), function(p) {
            g = g[p]
        });
        return g
    }
    function makeError(i, m, a, r) {
        var e = new Error(m + '\nhttp://requirejs.org/docs/errors.html#' + i);
        e.requireType = i;
        e.requireModules = r;
        if (a) {
            e.originalError = a
        }
        return e
    }
    if (typeof define !== 'undefined') {
        return
    }
    if (typeof requirejs !== 'undefined') {
        if (isFunction(requirejs)) {
            return
        }
        cfg = requirejs;
        requirejs = undefined
    }
    if (typeof require !== 'undefined' && !isFunction(require)) {
        cfg = require;
        require = undefined
    }
    function newContext(a) {
        var b, M, d, h, f, g = {
            waitSeconds: 7,
            baseUrl: './',
            paths: {},
            pkgs: {},
            shim: {},
            config: {}
        }, r = {}, k = {}, u = {}, l = [],
            m = {}, n = {}, o = 1,
            p = 1;

        function t(c) {
            var i, e;
            for (i = 0; c[i]; i += 1) {
                e = c[i];
                if (e === '.') {
                    c.splice(i, 1);
                    i -= 1
                } else if (e === '..') {
                    if (i === 1 && (c[2] === '..' || c[0] === '..')) {
                        break
                    } else if (i > 0) {
                        c.splice(i - 1, 2);
                        i -= 2
                    }
                }
            }
        }
        function q(c, e, K) {
            var L, N, O, P, i, j, Q, R, S, T, U, V = e && e.split('/'),
                W = V,
                X = g.map,
                Y = X && X['*'];
            if (c && c.charAt(0) === '.') {
                if (e) {
                    if (getOwn(g.pkgs, e)) {
                        W = V = [e]
                    } else {
                        W = V.slice(0, V.length - 1)
                    }
                    c = W.concat(c.split('/'));
                    t(c);
                    N = getOwn(g.pkgs, (L = c[0]));
                    c = c.join('/');
                    if (N && c === L + '/' + N.main) {
                        c = L
                    }
                } else if (c.indexOf('./') === 0) {
                    c = c.substring(2)
                }
            }
            if (K && X && (V || Y)) {
                P = c.split('/');
                for (i = P.length; i > 0; i -= 1) {
                    Q = P.slice(0, i).join('/');
                    if (V) {
                        for (j = V.length; j > 0; j -= 1) {
                            O = getOwn(X, V.slice(0, j).join('/'));
                            if (O) {
                                O = getOwn(O, Q);
                                if (O) {
                                    R = O;
                                    S = i;
                                    break
                                }
                            }
                        }
                    }
                    if (R) {
                        break
                    }
                    if (!T && Y && getOwn(Y, Q)) {
                        T = getOwn(Y, Q);
                        U = i
                    }
                }
                if (!R && T) {
                    R = T;
                    S = U
                }
                if (R) {
                    P.splice(0, S, R);
                    c = P.join('/')
                }
            }
            return c
        }
        function v(c) {
            if (isBrowser) {
                each(scripts(), function(e) {
                    if (e.getAttribute('data-requiremodule') === c && e.getAttribute('data-requirecontext') === d.contextName) {
                        e.parentNode.removeChild(e);
                        return true
                    }
                })
            }
        }
        function w(i) {
            var c = getOwn(g.paths, i);
            if (c && isArray(c) && c.length > 1) {
                v(i);
                c.shift();
                d.require.undef(i);
                d.require([i]);
                return true
            }
        }
        function x(c) {
            var e, i = c ? c.indexOf('!') : -1;
            if (i > -1) {
                e = c.substring(0, i);
                c = c.substring(i + 1, c.length)
            }
            return [e, c]
        }
        function y(c, e, i, j) {
            var K, L, N, O, P = null,
                Q = e ? e.name : null,
                R = c,
                S = true,
                T = '';
            if (!c) {
                S = false;
                c = '_@r' + (o += 1)
            }
            O = x(c);
            P = O[0];
            c = O[1];
            if (P) {
                P = q(P, Q, j);
                L = getOwn(m, P)
            }
            if (c) {
                if (P) {
                    if (L && L.normalize) {
                        T = L.normalize(c, function(c) {
                            return q(c, Q, j)
                        })
                    } else {
                        T = q(c, Q, j)
                    }
                } else {
                    T = q(c, Q, j);
                    O = x(T);
                    P = O[0];
                    T = O[1];
                    i = true;
                    K = d.nameToUrl(T)
                }
            }
            N = P && !L && !i ? '_unnormalized' + (p += 1) : '';
            return {
                prefix: P,
                name: T,
                parentMap: e,
                unnormalized: !! N,
                url: K,
                originalName: R,
                isDefine: S,
                id: (P ? P + '!' + T : T) + N
            }
        }
        function z(c) {
            var i = c.id,
                e = getOwn(r, i);
            if (!e) {
                e = r[i] = new d.Module(c)
            }
            return e
        }
        function A(c, e, i) {
            var j = c.id,
                K = getOwn(r, j);
            if (hasProp(m, j) && (!K || K.defineEmitComplete)) {
                if (e === 'defined') {
                    i(m[j])
                }
            } else {
                K = z(c);
                if (K.error && e === 'error') {
                    i(K.error)
                } else {
                    K.on(e, i)
                }
            }
        }
        function B(e, c) {
            var i = e.requireModules,
                j = false;
            if (c) {
                c(e)
            } else {
                each(i, function(K) {
                    var L = getOwn(r, K);
                    if (L) {
                        L.error = e;
                        if (L.events.error) {
                            j = true;
                            L.emit('error', e)
                        }
                    }
                });
                if (!j) {
                    req.onError(e)
                }
            }
        }
        function C() {
            if (globalDefQueue.length) {
                apsp.apply(l, [l.length - 1, 0].concat(globalDefQueue));
                globalDefQueue = []
            }
        }
        h = {
            'require': function(c) {
                if (c.require) {
                    return c.require
                } else {
                    return (c.require = d.makeRequire(c.map))
                }
            },
            'exports': function(c) {
                c.usingExports = true;
                if (c.map.isDefine) {
                    if (c.exports) {
                        return c.exports
                    } else {
                        return (c.exports = m[c.map.id] = {})
                    }
                }
            },
            'module': function(e) {
                if (e.module) {
                    return e.module
                } else {
                    return (e.module = {
                        id: e.map.id,
                        uri: e.map.url,
                        config: function() {
                            var c, i = getOwn(g.pkgs, e.map.id);
                            c = i ? getOwn(g.config, e.map.id + '/' + i.main) : getOwn(g.config, e.map.id);
                            return c || {}
                        },
                        exports: m[e.map.id]
                    })
                }
            }
        };

        function D(i) {
            delete r[i];
            delete k[i]
        }
        function E(c, e, j) {
            var K = c.map.id;
            if (c.error) {
                c.emit('error', c.error)
            } else {
                e[K] = true;
                each(c.depMaps, function(L, i) {
                    var N = L.id,
                        O = getOwn(r, N);
                    if (O && !c.depMatched[i] && !j[N]) {
                        if (getOwn(e, N)) {
                            c.defineDep(i, m[N]);
                            c.check()
                        } else {
                            E(O, e, j)
                        }
                    }
                });
                j[K] = true
            }
        }
        function F() {
            var c, e, i, j, K = g.waitSeconds * 1000,
                L = K && (d.startTime + K) < new Date().getTime(),
                N = [],
                O = [],
                P = false,
                Q = true;
            if (b) {
                return
            }
            b = true;
            eachProp(k, function(R) {
                c = R.map;
                e = c.id;
                if (!R.enabled) {
                    return
                }
                if (!c.isDefine) {
                    O.push(R)
                }
                if (!R.error) {
                    if (!R.inited && L) {
                        if (w(e)) {
                            j = true;
                            P = true
                        } else {
                            N.push(e);
                            v(e)
                        }
                    } else if (!R.inited && R.fetched && c.isDefine) {
                        P = true;
                        if (!c.prefix) {
                            return (Q = false)
                        }
                    }
                }
            });
            if (L && N.length) {
                i = makeError('timeout', 'Load timeout for modules: ' + N, null, N);
                i.contextName = d.contextName;
                return B(i)
            }
            if (Q) {
                each(O, function(R) {
                    E(R, {}, {})
                })
            }
            if ((!L || j) && P) {
                if ((isBrowser || isWebWorker) && !f) {
                    f = setTimeout(function() {
                        f = 0;
                        F()
                    }, 50)
                }
            }
            b = false
        }
        M = function(c) {
            this.events = getOwn(u, c.id) || {};
            this.map = c;
            this.shim = getOwn(g.shim, c.id);
            this.depExports = [];
            this.depMaps = [];
            this.depMatched = [];
            this.pluginMaps = {};
            this.depCount = 0
        };
        M.prototype = {
            init: function(c, e, i, j) {
                j = j || {};
                if (this.inited) {
                    return
                }
                this.factory = e;
                if (i) {
                    this.on('error', i)
                } else if (this.events.error) {
                    i = bind(this, function(K) {
                        this.emit('error', K)
                    })
                }
                this.depMaps = c && c.slice(0);
                this.errback = i;
                this.inited = true;
                this.ignore = j.ignore;
                if (j.enabled || this.enabled) {
                    this.enable()
                } else {
                    this.check()
                }
            },
            defineDep: function(i, c) {
                if (!this.depMatched[i]) {
                    this.depMatched[i] = true;
                    this.depCount -= 1;
                    this.depExports[i] = c
                }
            },
            fetch: function() {
                if (this.fetched) {
                    return
                }
                this.fetched = true;
                d.startTime = (new Date()).getTime();
                var c = this.map;
                if (this.shim) {
                    d.makeRequire(this.map, {
                        enableBuildCallback: true
                    })(this.shim.deps || [], bind(this, function() {
                        return c.prefix ? this.callPlugin() : this.load()
                    }))
                } else {
                    return c.prefix ? this.callPlugin() : this.load()
                }
            },
            load: function() {
                var c = this.map.url;
                if (!n[c]) {
                    n[c] = true;
                    d.load(this.map.id, c)
                }
            },
            check: function() {
                if (!this.enabled || this.enabling) {
                    return
                }
                var c, i, j = this.map.id,
                    K = this.depExports,
                    L = this.exports,
                    N = this.factory;
                if (!this.inited) {
                    this.fetch()
                } else if (this.error) {
                    this.emit('error', this.error)
                } else if (!this.defining) {
                    this.defining = true;
                    if (this.depCount < 1 && !this.defined) {
                        if (isFunction(N)) {
                            if ((this.events.error && this.map.isDefine) || req.onError !== defaultOnError) {
                                try {
                                    L = d.execCb(j, N, K, L)
                                } catch (e) {
                                    c = e
                                }
                            } else {
                                L = d.execCb(j, N, K, L)
                            }
                            if (this.map.isDefine) {
                                i = this.module;
                                if (i && i.exports !== undefined && i.exports !== this.exports) {
                                    L = i.exports
                                } else if (L === undefined && this.usingExports) {
                                    L = this.exports
                                }
                            }
                            if (c) {
                                c.requireMap = this.map;
                                c.requireModules = this.map.isDefine ? [this.map.id] : null;
                                c.requireType = this.map.isDefine ? 'define' : 'require';
                                return B((this.error = c))
                            }
                        } else {
                            L = N
                        }
                        this.exports = L;
                        if (this.map.isDefine && !this.ignore) {
                            m[j] = L;
                            if (req.onResourceLoad) {
                                req.onResourceLoad(d, this.map, this.depMaps)
                            }
                        }
                        D(j);
                        this.defined = true
                    }
                    this.defining = false;
                    if (this.defined && !this.defineEmitted) {
                        this.defineEmitted = true;
                        this.emit('defined', this.exports);
                        this.defineEmitComplete = true
                    }
                }
            },
            callPlugin: function() {
                var c = this.map,
                    i = c.id,
                    j = y(c.prefix);
                this.depMaps.push(j);
                A(j, 'defined', bind(this, function(K) {
                    var L, N, O, P = this.map.name,
                        Q = this.map.parentMap ? this.map.parentMap.name : null,
                        R = d.makeRequire(c.parentMap, {
                            enableBuildCallback: true
                        });
                    if (this.map.unnormalized) {
                        if (K.normalize) {
                            P = K.normalize(P, function(P) {
                                return q(P, Q, true)
                            }) || ''
                        }
                        N = y(c.prefix + '!' + P, this.map.parentMap);
                        A(N, 'defined', bind(this, function(e) {
                            this.init([], function() {
                                return e
                            }, null, {
                                enabled: true,
                                ignore: true
                            })
                        }));
                        O = getOwn(r, N.id);
                        if (O) {
                            this.depMaps.push(N);
                            if (this.events.error) {
                                O.on('error', bind(this, function(e) {
                                    this.emit('error', e)
                                }))
                            }
                            O.enable()
                        }
                        return
                    }
                    L = bind(this, function(e) {
                        this.init([], function() {
                            return e
                        }, null, {
                            enabled: true
                        })
                    });
                    L.error = bind(this, function(e) {
                        this.inited = true;
                        this.error = e;
                        e.requireModules = [i];
                        eachProp(r, function(S) {
                            if (S.map.id.indexOf(i + '_unnormalized') === 0) {
                                D(S.map.id)
                            }
                        });
                        B(e)
                    });
                    L.fromText = bind(this, function(S, T) {
                        var U = c.name,
                            V = y(U),
                            W = useInteractive;
                        if (T) {
                            S = T
                        }
                        if (W) {
                            useInteractive = false
                        }
                        z(V);
                        if (hasProp(g.config, i)) {
                            g.config[U] = g.config[i]
                        }
                        try {
                            req.exec(S)
                        } catch (e) {
                            return B(makeError('fromtexteval', 'fromText eval for ' + i + ' failed: ' + e, e, [i]))
                        }
                        if (W) {
                            useInteractive = true
                        }
                        this.depMaps.push(V);
                        d.completeLoad(U);
                        R([U], L)
                    });
                    K.load(c.name, R, L, g)
                }));
                d.enable(j, this);
                this.pluginMaps[j.id] = j
            },
            enable: function() {
                k[this.map.id] = this;
                this.enabled = true;
                this.enabling = true;
                each(this.depMaps, bind(this, function(c, i) {
                    var e, j, K;
                    if (typeof c === 'string') {
                        c = y(c, (this.map.isDefine ? this.map : this.map.parentMap), false, !this.skipMap);
                        this.depMaps[i] = c;
                        K = getOwn(h, c.id);
                        if (K) {
                            this.depExports[i] = K(this);
                            return
                        }
                        this.depCount += 1;
                        A(c, 'defined', bind(this, function(L) {
                            this.defineDep(i, L);
                            this.check()
                        }));
                        if (this.errback) {
                            A(c, 'error', bind(this, this.errback))
                        }
                    }
                    e = c.id;
                    j = r[e];
                    if (!hasProp(h, e) && j && !j.enabled) {
                        d.enable(c, this)
                    }
                }));
                eachProp(this.pluginMaps, bind(this, function(c) {
                    var e = getOwn(r, c.id);
                    if (e && !e.enabled) {
                        d.enable(c, this)
                    }
                }));
                this.enabling = false;
                this.check()
            },
            on: function(c, e) {
                var i = this.events[c];
                if (!i) {
                    i = this.events[c] = []
                }
                i.push(e)
            },
            emit: function(c, e) {
                each(this.events[c], function(i) {
                    i(e)
                });
                if (c === 'error') {
                    delete this.events[c]
                }
            }
        };

        function G(c) {
            if (!hasProp(m, c[0])) {
                z(y(c[0], null, true)).init(c[1], c[2])
            }
        }
        function H(c, e, i, j) {
            if (c.detachEvent && !isOpera) {
                if (j) {
                    c.detachEvent(j, e)
                }
            } else {
                c.removeEventListener(i, e, false)
            }
        }
        function I(e) {
            var c = e.currentTarget || e.srcElement;
            H(c, d.onScriptLoad, 'load', 'onreadystatechange');
            H(c, d.onScriptError, 'error');
            return {
                node: c,
                id: c && c.getAttribute('data-requiremodule')
            }
        }
        function J() {
            var c;
            C();
            while (l.length) {
                c = l.shift();
                if (c[0] === null) {
                    return B(makeError('mismatch', 'Mismatched anonymous define() module: ' + c[c.length - 1]))
                } else {
                    G(c)
                }
            }
        }
        d = {
            config: g,
            contextName: a,
            registry: r,
            defined: m,
            urlFetched: n,
            defQueue: l,
            Module: M,
            makeModuleMap: y,
            nextTick: req.nextTick,
            onError: B,
            configure: function(c) {
                if (c.baseUrl) {
                    if (c.baseUrl.charAt(c.baseUrl.length - 1) !== '/') {
                        c.baseUrl += '/'
                    }
                }
                var e = g.pkgs,
                    i = g.shim,
                    j = {
                        paths: true,
                        config: true,
                        map: true
                    };
                eachProp(c, function(K, L) {
                    if (j[L]) {
                        if (L === 'map') {
                            if (!g.map) {
                                g.map = {}
                            }
                            mixin(g[L], K, true, true)
                        } else {
                            mixin(g[L], K, true)
                        }
                    } else {
                        g[L] = K
                    }
                });
                if (c.shim) {
                    eachProp(c.shim, function(K, L) {
                        if (isArray(K)) {
                            K = {
                                deps: K
                            }
                        }
                        if ((K.exports || K.init) && !K.exportsFn) {
                            K.exportsFn = d.makeShimExports(K)
                        }
                        i[L] = K
                    });
                    g.shim = i
                }
                if (c.packages) {
                    each(c.packages, function(K) {
                        var L;
                        K = typeof K === 'string' ? {
                            name: K
                        } : K;
                        L = K.location;
                        e[K.name] = {
                            name: K.name,
                            location: L || K.name,
                            main: (K.main || 'main').replace(currDirRegExp, '').replace(jsSuffixRegExp, '')
                        }
                    });
                    g.pkgs = e
                }
                eachProp(r, function(K, L) {
                    if (!K.inited && !K.map.unnormalized) {
                        K.map = y(L)
                    }
                });
                if (c.deps || c.callback) {
                    d.require(c.deps || [], c.callback)
                }
            },
            makeShimExports: function(c) {
                function e() {
                    var i;
                    if (c.init) {
                        i = c.init.apply(global, arguments)
                    }
                    return i || (c.exports && getGlobal(c.exports))
                }
                return e
            },
            makeRequire: function(c, e) {
                e = e || {};

                function i(j, K, L) {
                    var N, O, P;
                    if (e.enableBuildCallback && K && isFunction(K)) {
                        K.__requireJsBuild = true
                    }
                    if (typeof j === 'string') {
                        if (isFunction(K)) {
                            return B(makeError('requireargs', 'Invalid require call'), L)
                        }
                        if (c && hasProp(h, j)) {
                            return h[j](r[c.id])
                        }
                        if (req.get) {
                            return req.get(d, j, c, i)
                        }
                        O = y(j, c, false, true);
                        N = O.id;
                        if (!hasProp(m, N)) {
                            return B(makeError('notloaded', 'Module name "' + N + '" has not been loaded yet for context: ' + a + (c ? '' : '. Use require([])')))
                        }
                        return m[N]
                    }
                    J();
                    d.nextTick(function() {
                        J();
                        P = z(y(null, c));
                        P.skipMap = e.skipMap;
                        P.init(j, K, L, {
                            enabled: true
                        });
                        F()
                    });
                    return i
                }
                mixin(i, {
                    isBrowser: isBrowser,
                    toUrl: function(j) {
                        var K, L = j.lastIndexOf('.'),
                            N = j.split('/')[0],
                            O = N === '.' || N === '..';
                        if (L !== -1 && (!O || L > 1)) {
                            K = j.substring(L, j.length);
                            j = j.substring(0, L)
                        }
                        return d.nameToUrl(q(j, c && c.id, true), K, true)
                    },
                    defined: function(j) {
                        return hasProp(m, y(j, c, false, true).id)
                    },
                    specified: function(j) {
                        j = y(j, c, false, true).id;
                        return hasProp(m, j) || hasProp(r, j)
                    }
                });
                if (!c) {
                    i.undef = function(j) {
                        C();
                        var K = y(j, c, true),
                            L = getOwn(r, j);
                        delete m[j];
                        delete n[K.url];
                        delete u[j];
                        if (L) {
                            if (L.events.defined) {
                                u[j] = L.events
                            }
                            D(j)
                        }
                    }
                }
                return i
            },
            enable: function(c) {
                var e = getOwn(r, c.id);
                if (e) {
                    z(c).enable()
                }
            },
            completeLoad: function(c) {
                var e, i, j, K = getOwn(g.shim, c) || {}, L = K.exports;
                C();
                while (l.length) {
                    i = l.shift();
                    if (i[0] === null) {
                        i[0] = c;
                        if (e) {
                            break
                        }
                        e = true
                    } else if (i[0] === c) {
                        e = true
                    }
                    G(i)
                }
                j = getOwn(r, c);
                if (!e && !hasProp(m, c) && j && !j.inited) {
                    if (g.enforceDefine && (!L || !getGlobal(L))) {
                        if (w(c)) {
                            return
                        } else {
                            return B(makeError('nodefine', 'No define call for ' + c, null, [c]))
                        }
                    } else {
                        G([c, (K.deps || []), K.exportsFn])
                    }
                }
                F()
            },
            nameToUrl: function(c, e, j) {
                var K, L, N, O, P, i, Q, R, S;
                if (req.jsExtRegExp.test(c)) {
                    R = c + (e || '')
                } else {
                    K = g.paths;
                    L = g.pkgs;
                    P = c.split('/');
                    for (i = P.length; i > 0; i -= 1) {
                        Q = P.slice(0, i).join('/');
                        N = getOwn(L, Q);
                        S = getOwn(K, Q);
                        if (S) {
                            if (isArray(S)) {
                                S = S[0]
                            }
                            P.splice(0, i, S);
                            break
                        } else if (N) {
                            if (c === N.name) {
                                O = N.location + '/' + N.main
                            } else {
                                O = N.location
                            }
                            P.splice(0, i, O);
                            break
                        }
                    }
                    R = P.join('/');
                    R += (e || (/\?/.test(R) || j ? '' : '.js'));
                    R = (R.charAt(0) === '/' || R.match(/^[\w\+\.\-]+:/) ? '' : g.baseUrl) + R
                }
                return g.urlArgs ? R + ((R.indexOf('?') === -1 ? '?' : '&') + g.urlArgs) : R
            },
            load: function(i, c) {
                req.load(d, i, c)
            },
            execCb: function(c, e, i, j) {
                return e.apply(j, i)
            },
            onScriptLoad: function(e) {
                if (e.type === 'load' || (readyRegExp.test((e.currentTarget || e.srcElement).readyState))) {
                    interactiveScript = null;
                    var c = I(e);
                    d.completeLoad(c.id)
                }
            },
            onScriptError: function(e) {
                var c = I(e);
                if (!w(c.id)) {
                    return B(makeError('scripterror', 'Script error for: ' + c.id, e, [c.id]))
                }
            }
        };
        d.require = d.makeRequire();
        return d
    }
    req = requirejs = function(d, c, e, o) {
        var a, b, f = defContextName;
        if (!isArray(d) && typeof d !== 'string') {
            b = d;
            if (isArray(c)) {
                d = c;
                c = e;
                e = o
            } else {
                d = []
            }
        }
        if (b && b.context) {
            f = b.context
        }
        a = getOwn(contexts, f);
        if (!a) {
            a = contexts[f] = req.s.newContext(f)
        }
        if (b) {
            a.configure(b)
        }
        return a.require(d, c, e)
    };
    req.config = function(c) {
        return req(c)
    };
    req.nextTick = typeof setTimeout !== 'undefined' ? function(f) {
        setTimeout(f, 4)
    } : function(f) {
        f()
    };
    if (!require) {
        require = req
    }
    req.version = version;
    req.jsExtRegExp = /^\/|:|\?|\.js$/;
    req.isBrowser = isBrowser;
    s = req.s = {
        contexts: contexts,
        newContext: newContext
    };
    req({});
    each(['toUrl', 'undef', 'defined', 'specified'], function(p) {
        req[p] = function() {
            var c = contexts[defContextName];
            return c.require[p].apply(c, arguments)
        }
    });
    if (isBrowser) {
        head = s.head = document.getElementsByTagName('head')[0];
        baseElement = document.getElementsByTagName('base')[0];
        if (baseElement) {
            head = s.head = baseElement.parentNode
        }
    }
    req.onError = defaultOnError;
    req.createNode = function(c, m, u) {
        var n = c.xhtml ? document.createElementNS('http://www.w3.org/1999/xhtml', 'html:script') : document.createElement('script');
        n.type = c.scriptType || 'text/javascript';
        n.charset = 'utf-8';
        n.async = true;
        return n
    };
    req.load = function(c, m, u) {
        var a = (c && c.config) || {}, n;
        if (isBrowser) {
            n = req.createNode(a, m, u);
            n.setAttribute('data-requirecontext', c.contextName);
            n.setAttribute('data-requiremodule', m);
            if (n.attachEvent && !(n.attachEvent.toString && n.attachEvent.toString().indexOf('[native code') < 0) && !isOpera) {
                useInteractive = true;
                n.attachEvent('onreadystatechange', c.onScriptLoad)
            } else {
                n.addEventListener('load', c.onScriptLoad, false);
                n.addEventListener('error', c.onScriptError, false)
            }
            n.src = u;
            currentlyAddingScript = n;
            if (baseElement) {
                head.insertBefore(n, baseElement)
            } else {
                head.appendChild(n)
            }
            currentlyAddingScript = null;
            return n
        } else if (isWebWorker) {
            try {
                importScripts(u);
                c.completeLoad(m)
            } catch (e) {
                c.onError(makeError('importscripts', 'importScripts failed for ' + m + ' at ' + u, e, [m]))
            }
        }
    };

    function getInteractiveScript() {
        if (interactiveScript && interactiveScript.readyState === 'interactive') {
            return interactiveScript
        }
        eachReverse(scripts(), function(a) {
            if (a.readyState === 'interactive') {
                return (interactiveScript = a)
            }
        });
        return interactiveScript
    }
    if (isBrowser) {
        eachReverse(scripts(), function(a) {
            if (!head) {
                head = a.parentNode
            }
            dataMain = a.getAttribute('data-main');
            if (dataMain) {
                mainScript = dataMain;
                if (!cfg.baseUrl) {
                    src = mainScript.split('/');
                    mainScript = src.pop();
                    subPath = src.length ? src.join('/') + '/' : './';
                    cfg.baseUrl = subPath
                }
                mainScript = mainScript.replace(jsSuffixRegExp, '');
                if (req.jsExtRegExp.test(mainScript)) {
                    mainScript = dataMain
                }
                cfg.deps = cfg.deps ? cfg.deps.concat(mainScript) : [mainScript];
                return true
            }
        })
    }
    define = function(n, d, c) {
        var a, b;
        if (typeof n !== 'string') {
            c = d;
            d = n;
            n = null
        }
        if (!isArray(d)) {
            c = d;
            d = null
        }
        if (!d && isFunction(c)) {
            d = [];
            if (c.length) {
                c.toString().replace(commentRegExp, '').replace(cjsRequireRegExp, function(m, e) {
                    d.push(e)
                });
                d = (c.length === 1 ? ['require'] : ['require', 'exports', 'module']).concat(d)
            }
        }
        if (useInteractive) {
            a = currentlyAddingScript || getInteractiveScript();
            if (a) {
                if (!n) {
                    n = a.getAttribute('data-requiremodule')
                }
                b = contexts[a.getAttribute('data-requirecontext')]
            }
        }(b ? b.defQueue : globalDefQueue).push([n, d, c])
    };
    define.amd = {
        jQuery: true
    };
    req.exec = function(text) {
        return eval(text)
    };
    req(cfg)
}(this));