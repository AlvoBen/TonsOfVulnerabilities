﻿/*!
 * jQuery JavaScript Library v1.7.1
 * http://jquery.com/
 *
 * Copyright 2011, John Resig
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 * Copyright 2011, The Dojo Foundation
 * Released under the MIT, BSD, and GPL Licenses.
 *
 * Date: Mon Nov 21 21:11:03 2011 -0500
 */

(function(w, u) {
    var d = w.document,
        g = w.navigator,
        h = w.location;
    var Q = (function() {
        var Q = function(i, j) {
            return new Q.fn.init(i, j, r)
        }, _ = w.jQuery,
            a = w.$,
            r, q = /^(?:[^#<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)/,
            b = /\S/,
            t = /^\s+/,
            c = /\s+$/,
            f = /^<(\w+)\s*\/?>(?:<\/\1>)?$/,
            m = /^[\],:{}\s]*$/,
            n = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,
            o = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
            p = /(?:^|:|,)(?:\s*\[)+/g,
            s = /(webkit)[ \/]([\w.]+)/,
            v = /(opera)(?:.*version)?[ \/]([\w.]+)/,
            R2 = /(msie) ([\w.]+)/,
            S2 = /(mozilla)(?:.*? rv:([\w.]+))?/,
            T2 = /-([a-z]|[0-9])/ig,
            U2 = /^-ms-/,
            V2 = function(i, l) {
                return (l + "").toUpperCase()
            }, W2 = g.userAgent,
            X2, Y2, Z2, $2 = Object.prototype.toString,
            _2 = Object.prototype.hasOwnProperty,
            a3 = Array.prototype.push,
            g1 = Array.prototype.slice,
            b3 = String.prototype.trim,
            c3 = Array.prototype.indexOf,
            d3 = {};
        Q.fn = Q.prototype = {
            constructor: Q,
            init: function(i, j, r) {
                var k, l, f3, g3;
                if (!i) {
                    return this
                }
                if (i.nodeType) {
                    this.context = this[0] = i;
                    this.length = 1;
                    return this
                }
                if (i === "body" && !j && d.body) {
                    this.context = d;
                    this[0] = d.body;
                    this.selector = i;
                    this.length = 1;
                    return this
                }
                if (typeof i === "string") {
                    if (i.charAt(0) === "<" && i.charAt(i.length - 1) === ">" && i.length >= 3) {
                        k = [null, i, null]
                    } else {
                        k = q.exec(i)
                    }
                    if (k && (k[1] || !j)) {
                        if (k[1]) {
                            j = j instanceof Q ? j[0] : j;
                            g3 = (j ? j.ownerDocument || j : d);
                            f3 = f.exec(i);
                            if (f3) {
                                if (Q.isPlainObject(j)) {
                                    i = [d.createElement(f3[1])];
                                    Q.fn.attr.call(i, j, true)
                                } else {
                                    i = [g3.createElement(f3[1])]
                                }
                            } else {
                                f3 = Q.buildFragment([k[1]], [g3]);
                                i = (f3.cacheable ? Q.clone(f3.fragment) : f3.fragment).childNodes
                            }
                            return Q.merge(this, i)
                        } else {
                            l = d.getElementById(k[2]);
                            if (l && l.parentNode) {
                                if (l.id !== k[2]) {
                                    return r.find(i)
                                }
                                this.length = 1;
                                this[0] = l
                            }
                            this.context = d;
                            this.selector = i;
                            return this
                        }
                    } else if (!j || j.jquery) {
                        return (j || r).find(i)
                    } else {
                        return this.constructor(j).find(i)
                    }
                } else if (Q.isFunction(i)) {
                    return r.ready(i)
                }
                if (i.selector !== u) {
                    this.selector = i.selector;
                    this.context = i.context
                }
                return Q.makeArray(i, this)
            },
            selector: "",
            jquery: "1.7.1",
            length: 0,
            size: function() {
                return this.length
            },
            toArray: function() {
                return g1.call(this, 0)
            },
            get: function(i) {
                return i == null ? this.toArray() : (i < 0 ? this[this.length + i] : this[i])
            },
            pushStack: function(i, j, k) {
                var l = this.constructor();
                if (Q.isArray(i)) {
                    a3.apply(l, i)
                } else {
                    Q.merge(l, i)
                }
                l.prevObject = this;
                l.context = this.context;
                if (j === "find") {
                    l.selector = this.selector + (this.selector ? " " : "") + k
                } else if (j) {
                    l.selector = this.selector + "." + j + "(" + k + ")"
                }
                return l
            },
            each: function(i, j) {
                return Q.each(this, i, j)
            },
            ready: function(i) {
                Q.bindReady();
                Y2.add(i);
                return this
            },
            eq: function(i) {
                i = +i;
                return i === -1 ? this.slice(i) : this.slice(i, i + 1)
            },
            first: function() {
                return this.eq(0)
            },
            last: function() {
                return this.eq(-1)
            },
            slice: function() {
                return this.pushStack(g1.apply(this, arguments), "slice", g1.call(arguments).join(","))
            },
            map: function(j) {
                return this.pushStack(Q.map(this, function(k, i) {
                    return j.call(k, i, k)
                }))
            },
            end: function() {
                return this.prevObject || this.constructor(null)
            },
            push: a3,
            sort: [].sort,
            splice: [].splice
        };
        Q.fn.init.prototype = Q.fn;
        Q.extend = Q.fn.extend = function() {
            var j, k, l, f3, g3, h3, i3 = arguments[0] || {}, i = 1,
                j3 = arguments.length,
                k3 = false;
            if (typeof i3 === "boolean") {
                k3 = i3;
                i3 = arguments[1] || {};
                i = 2
            }
            if (typeof i3 !== "object" && !Q.isFunction(i3)) {
                i3 = {}
            }
            if (j3 === i) {
                i3 = this;
                --i
            }
            for (; i < j3; i++) {
                if ((j = arguments[i]) != null) {
                    for (k in j) {
                        l = i3[k];
                        f3 = j[k];
                        if (i3 === f3) {
                            continue
                        }
                        if (k3 && f3 && (Q.isPlainObject(f3) || (g3 = Q.isArray(f3)))) {
                            if (g3) {
                                g3 = false;
                                h3 = l && Q.isArray(l) ? l : []
                            } else {
                                h3 = l && Q.isPlainObject(l) ? l : {}
                            }
                            i3[k] = Q.extend(k3, h3, f3)
                        } else if (f3 !== u) {
                            i3[k] = f3
                        }
                    }
                }
            }
            return i3
        };
        Q.extend({
            noConflict: function(i) {
                if (w.$ === Q) {
                    w.$ = a
                }
                if (i && w.jQuery === Q) {
                    w.jQuery = _
                }
                return Q
            },
            isReady: false,
            readyWait: 1,
            holdReady: function(i) {
                if (i) {
                    Q.readyWait++
                } else {
                    Q.ready(true)
                }
            },
            ready: function(i) {
                if ((i === true && !--Q.readyWait) || (i !== true && !Q.isReady)) {
                    if (!d.body) {
                        return setTimeout(Q.ready, 1)
                    }
                    Q.isReady = true;
                    if (i !== true && --Q.readyWait > 0) {
                        return
                    }
                    Y2.fireWith(d, [Q]);
                    if (Q.fn.trigger) {
                        Q(d).trigger("ready").off("ready")
                    }
                }
            },
            bindReady: function() {
                if (Y2) {
                    return
                }
                Y2 = Q.Callbacks("once memory");
                if (d.readyState === "complete") {
                    return setTimeout(Q.ready, 1)
                }
                if (d.addEventListener) {
                    d.addEventListener("DOMContentLoaded", Z2, false);
                    w.addEventListener("load", Q.ready, false)
                } else if (d.attachEvent) {
                    d.attachEvent("onreadystatechange", Z2);
                    w.attachEvent("onload", Q.ready);
                    var i = false;
                    try {
                        i = w.frameElement == null
                    } catch (e) {}
                    if (d.documentElement.doScroll && i) {
                        e3()
                    }
                }
            },
            isFunction: function(i) {
                return Q.type(i) === "function"
            },
            isArray: Array.isArray || function(i) {
                return Q.type(i) === "array"
            },
            isWindow: function(i) {
                return i && typeof i === "object" && "setInterval" in i
            },
            isNumeric: function(i) {
                return !isNaN(parseFloat(i)) && isFinite(i)
            },
            type: function(i) {
                return i == null ? String(i) : d3[$2.call(i)] || "object"
            },
            isPlainObject: function(i) {
                if (!i || Q.type(i) !== "object" || i.nodeType || Q.isWindow(i)) {
                    return false
                }
                try {
                    if (i.constructor && !_2.call(i, "constructor") && !_2.call(i.constructor.prototype, "isPrototypeOf")) {
                        return false
                    }
                } catch (e) {
                    return false
                }
                var k;
                for (k in i) {}
                return k === u || _2.call(i, k)
            },
            isEmptyObject: function(i) {
                for (var j in i) {
                    return false
                }
                return true
            },
            error: function(i) {
                throw new Error(i)
            },
            parseJSON: function(i) {
                if (typeof i !== "string" || !i) {
                    return null
                }
                i = Q.trim(i);
                if (w.JSON && w.JSON.parse) {
                    return w.JSON.parse(i)
                }
                if (m.test(i.replace(n, "@").replace(o, "]").replace(p, ""))) {
                    return (new Function("return " + i))()
                }
                Q.error("Invalid JSON: " + i)
            },
            parseXML: function(i) {
                var j, k;
                try {
                    if (w.DOMParser) {
                        k = new DOMParser();
                        j = k.parseFromString(i, "text/xml")
                    } else {
                        j = new ActiveXObject("Microsoft.XMLDOM");
                        j.async = "false";
                        j.loadXML(i)
                    }
                } catch (e) {
                    j = u
                }
                if (!j || !j.documentElement || j.getElementsByTagName("parsererror").length) {
                    Q.error("Invalid XML: " + i)
                }
                return j
            },
            noop: function() {},
            globalEval: function(i) {
                if (i && b.test(i)) {
                    (w.execScript || function(i) {
                        w["eval"].call(w, i)
                    })(i)
                }
            },
            camelCase: function(i) {
                return i.replace(U2, "ms-").replace(T2, V2)
            },
            nodeName: function(i, j) {
                return i.nodeName && i.nodeName.toUpperCase() === j.toUpperCase()
            },
            each: function(j, k, l) {
                var f3, i = 0,
                    g3 = j.length,
                    h3 = g3 === u || Q.isFunction(j);
                if (l) {
                    if (h3) {
                        for (f3 in j) {
                            if (k.apply(j[f3], l) === false) {
                                break
                            }
                        }
                    } else {
                        for (; i < g3;) {
                            if (k.apply(j[i++], l) === false) {
                                break
                            }
                        }
                    }
                } else {
                    if (h3) {
                        for (f3 in j) {
                            if (k.call(j[f3], f3, j[f3]) === false) {
                                break
                            }
                        }
                    } else {
                        for (; i < g3;) {
                            if (k.call(j[i], i, j[i++]) === false) {
                                break
                            }
                        }
                    }
                }
                return j
            },
            trim: b3 ? function(i) {
                return i == null ? "" : b3.call(i)
            } : function(i) {
                return i == null ? "" : i.toString().replace(t, "").replace(c, "")
            },
            makeArray: function(i, j) {
                var k = j || [];
                if (i != null) {
                    var l = Q.type(i);
                    if (i.length == null || l === "string" || l === "function" || l === "regexp" || Q.isWindow(i)) {
                        a3.call(k, i)
                    } else {
                        Q.merge(k, i)
                    }
                }
                return k
            },
            inArray: function(j, k, i) {
                var l;
                if (k) {
                    if (c3) {
                        return c3.call(k, j, i)
                    }
                    l = k.length;
                    i = i ? i < 0 ? Math.max(0, l + i) : i : 0;
                    for (; i < l; i++) {
                        if (i in k && k[i] === j) {
                            return i
                        }
                    }
                }
                return -1
            },
            merge: function(k, f3) {
                var i = k.length,
                    j = 0;
                if (typeof f3.length === "number") {
                    for (var l = f3.length; j < l; j++) {
                        k[i++] = f3[j]
                    }
                } else {
                    while (f3[j] !== u) {
                        k[i++] = f3[j++]
                    }
                }
                k.length = i;
                return k
            },
            grep: function(j, k, l) {
                var f3 = [],
                    g3;
                l = !! l;
                for (var i = 0, h3 = j.length; i < h3; i++) {
                    g3 = !! k(j[i], i);
                    if (l !== g3) {
                        f3.push(j[i])
                    }
                }
                return f3
            },
            map: function(j, k, l) {
                var f3, g3, h3 = [],
                    i = 0,
                    i3 = j.length,
                    j3 = j instanceof Q || i3 !== u && typeof i3 === "number" && ((i3 > 0 && j[0] && j[i3 - 1]) || i3 === 0 || Q.isArray(j));
                if (j3) {
                    for (; i < i3; i++) {
                        f3 = k(j[i], i, l);
                        if (f3 != null) {
                            h3[h3.length] = f3
                        }
                    }
                } else {
                    for (g3 in j) {
                        f3 = k(j[g3], g3, l);
                        if (f3 != null) {
                            h3[h3.length] = f3
                        }
                    }
                }
                return h3.concat.apply([], h3)
            },
            guid: 1,
            proxy: function(i, j) {
                if (typeof j === "string") {
                    var k = i[j];
                    j = i;
                    i = k
                }
                if (!Q.isFunction(i)) {
                    return u
                }
                var l = g1.call(arguments, 2),
                    f3 = function() {
                        return i.apply(j, l.concat(g1.call(arguments)))
                    };
                f3.guid = i.guid = i.guid || f3.guid || Q.guid++;
                return f3
            },
            access: function(j, l, f3, g3, fn, i3) {
                var j3 = j.length;
                if (typeof l === "object") {
                    for (var k in l) {
                        Q.access(j, k, l[k], g3, fn, f3)
                    }
                    return j
                }
                if (f3 !== u) {
                    g3 = !i3 && g3 && Q.isFunction(f3);
                    for (var i = 0; i < j3; i++) {
                        fn(j[i], l, g3 ? f3.call(j[i], i, fn(j[i], l)) : f3, i3)
                    }
                    return j
                }
                return j3 ? fn(j[0], l) : u
            },
            now: function() {
                return (new Date()).getTime()
            },
            uaMatch: function(i) {
                i = i.toLowerCase();
                var j = s.exec(i) || v.exec(i) || R2.exec(i) || i.indexOf("compatible") < 0 && S2.exec(i) || [];
                return {
                    browser: j[1] || "",
                    version: j[2] || "0"
                }
            },
            sub: function() {
                function j(k, l) {
                    return new j.fn.init(k, l)
                }
                Q.extend(true, j, this);
                j.superclass = this;
                j.fn = j.prototype = this();
                j.fn.constructor = j;
                j.sub = this.sub;
                j.fn.init = function init(k, l) {
                    if (l && l instanceof Q && !(l instanceof j)) {
                        l = j(l)
                    }
                    return Q.fn.init.call(this, k, l, i)
                };
                j.fn.init.prototype = j.fn;
                var i = j(d);
                return j
            },
            browser: {}
        });
        Q.each("Boolean Number String Function Array Date RegExp Object".split(" "), function(i, j) {
            d3["[object " + j + "]"] = j.toLowerCase()
        });
        X2 = Q.uaMatch(W2);
        if (X2.browser) {
            Q.browser[X2.browser] = true;
            Q.browser.version = X2.version
        }
        if (Q.browser.webkit) {
            Q.browser.safari = true
        }
        if (b.test("\xA0")) {
            t = /^[\s\xA0]+/;
            c = /[\s\xA0]+$/
        }
        r = Q(d);
        if (d.addEventListener) {
            Z2 = function() {
                d.removeEventListener("DOMContentLoaded", Z2, false);
                Q.ready()
            }
        } else if (d.attachEvent) {
            Z2 = function() {
                if (d.readyState === "complete") {
                    d.detachEvent("onreadystatechange", Z2);
                    Q.ready()
                }
            }
        }
        function e3() {
            if (Q.isReady) {
                return
            }
            try {
                d.documentElement.doScroll("left")
            } catch (e) {
                setTimeout(e3, 1);
                return
            }
            Q.ready()
        }
        return Q
    })();
    var x = {};

    function y(f) {
        var o = x[f] = {}, i, l;
        f = f.split(/\s+/);
        for (i = 0, l = f.length; i < l; i++) {
            o[f[i]] = true
        }
        return o
    }
    Q.Callbacks = function(f) {
        f = f ? (x[f] || y(f)) : {};
        var l = [],
            s = [],
            m, a, b, c, j, k = function(p) {
                var i, q, r, t, v;
                for (i = 0, q = p.length; i < q; i++) {
                    r = p[i];
                    t = Q.type(r);
                    if (t === "array") {
                        k(r)
                    } else if (t === "function") {
                        if (!f.unique || !o.has(r)) {
                            l.push(r)
                        }
                    }
                }
            }, n = function(i, p) {
                p = p || [];
                m = !f.memory || [i, p];
                a = true;
                j = b || 0;
                b = 0;
                c = l.length;
                for (; l && j < c; j++) {
                    if (l[j].apply(i, p) === false && f.stopOnFalse) {
                        m = true;
                        break
                    }
                }
                a = false;
                if (l) {
                    if (!f.once) {
                        if (s && s.length) {
                            m = s.shift();
                            o.fireWith(m[0], m[1])
                        }
                    } else if (m === true) {
                        o.disable()
                    } else {
                        l = []
                    }
                }
            }, o = {
                add: function() {
                    if (l) {
                        var i = l.length;
                        k(arguments);
                        if (a) {
                            c = l.length
                        } else if (m && m !== true) {
                            b = i;
                            n(m[0], m[1])
                        }
                    }
                    return this
                },
                remove: function() {
                    if (l) {
                        var p = arguments,
                            q = 0,
                            r = p.length;
                        for (; q < r; q++) {
                            for (var i = 0; i < l.length; i++) {
                                if (p[q] === l[i]) {
                                    if (a) {
                                        if (i <= c) {
                                            c--;
                                            if (i <= j) {
                                                j--
                                            }
                                        }
                                    }
                                    l.splice(i--, 1);
                                    if (f.unique) {
                                        break
                                    }
                                }
                            }
                        }
                    }
                    return this
                },
                has: function(p) {
                    if (l) {
                        var i = 0,
                            q = l.length;
                        for (; i < q; i++) {
                            if (p === l[i]) {
                                return true
                            }
                        }
                    }
                    return false
                },
                empty: function() {
                    l = [];
                    return this
                },
                disable: function() {
                    l = s = m = u;
                    return this
                },
                disabled: function() {
                    return !l
                },
                lock: function() {
                    s = u;
                    if (!m || m === true) {
                        o.disable()
                    }
                    return this
                },
                locked: function() {
                    return !s
                },
                fireWith: function(i, p) {
                    if (s) {
                        if (a) {
                            if (!f.once) {
                                s.push([i, p])
                            }
                        } else if (!(f.once && m)) {
                            n(i, p)
                        }
                    }
                    return this
                },
                fire: function() {
                    o.fireWith(this, arguments);
                    return this
                },
                fired: function() {
                    return !!m
                }
            };
        return o
    };
    var z = [].slice;
    Q.extend({
        Deferred: function(f) {
            var a = Q.Callbacks("once memory"),
                b = Q.Callbacks("once memory"),
                p = Q.Callbacks("memory"),
                s = "pending",
                l = {
                    resolve: a,
                    reject: b,
                    notify: p
                }, c = {
                    done: a.add,
                    fail: b.add,
                    progress: p.add,
                    state: function() {
                        return s
                    },
                    isResolved: a.fired,
                    isRejected: b.fired,
                    then: function(j, m, n) {
                        i.done(j).fail(m).progress(n);
                        return this
                    },
                    always: function() {
                        i.done.apply(i, arguments).fail.apply(i, arguments);
                        return this
                    },
                    pipe: function(j, m, n) {
                        return Q.Deferred(function(o) {
                            Q.each({
                                done: [j, "resolve"],
                                fail: [m, "reject"],
                                progress: [n, "notify"]
                            }, function(q, r) {
                                var t = r[0],
                                    v = r[1],
                                    _;
                                if (Q.isFunction(t)) {
                                    i[q](function() {
                                        _ = t.apply(this, arguments);
                                        if (_ && Q.isFunction(_.promise)) {
                                            _.promise().then(o.resolve, o.reject, o.notify)
                                        } else {
                                            o[v + "With"](this === i ? o : this, [_])
                                        }
                                    })
                                } else {
                                    i[q](o[v])
                                }
                            })
                        }).promise()
                    },
                    promise: function(o) {
                        if (o == null) {
                            o = c
                        } else {
                            for (var k in c) {
                                o[k] = c[k]
                            }
                        }
                        return o
                    }
                }, i = c.promise({}),
                k;
            for (k in l) {
                i[k] = l[k].fire;
                i[k + "With"] = l[k].fireWith
            }
            i.done(function() {
                s = "resolved"
            }, b.disable, p.lock).fail(function() {
                s = "rejected"
            }, a.disable, p.lock);
            if (f) {
                f.call(i, i)
            }
            return i
        },
        when: function(f) {
            var a = z.call(arguments, 0),
                i = 0,
                l = a.length,
                p = new Array(l),
                c = l,
                b = l,
                j = l <= 1 && f && Q.isFunction(f.promise) ? f : Q.Deferred(),
                k = j.promise();

            function r(i) {
                return function(v) {
                    a[i] = arguments.length > 1 ? z.call(arguments, 0) : v;
                    if (!(--c)) {
                        j.resolveWith(j, a)
                    }
                }
            }
            function m(i) {
                return function(v) {
                    p[i] = arguments.length > 1 ? z.call(arguments, 0) : v;
                    j.notifyWith(k, p)
                }
            }
            if (l > 1) {
                for (; i < l; i++) {
                    if (a[i] && a[i].promise && Q.isFunction(a[i].promise)) {
                        a[i].promise().then(r(i), j.reject, m(i))
                    } else {
                        --c
                    }
                }
                if (!c) {
                    j.resolveWith(j, a)
                }
            } else if (j !== f) {
                j.resolveWith(j, l ? [f] : [])
            }
            return k
        }
    });
    Q.support = (function() {
        var s, b, a, c, o, f, m, j, t, k, l, i, n, p = d.createElement("div"),
            q = d.documentElement;
        p.setAttribute("className", "t");
        p.innerHTML = "   <link/><table></table><a href='/a' style='top:1px;float:left;opacity:.55;'>a</a><input type='checkbox'/>";
        b = p.getElementsByTagName("*");
        a = p.getElementsByTagName("a")[0];
        if (!b || !b.length || !a) {
            return {}
        }
        c = d.createElement("select");
        o = c.appendChild(d.createElement("option"));
        f = p.getElementsByTagName("input")[0];
        s = {
            leadingWhitespace: (p.firstChild.nodeType === 3),
            tbody: !p.getElementsByTagName("tbody").length,
            htmlSerialize: !! p.getElementsByTagName("link").length,
            style: /top/.test(a.getAttribute("style")),
            hrefNormalized: (a.getAttribute("href") === "/a"),
            opacity: /^0.55/.test(a.style.opacity),
            cssFloat: !! a.style.cssFloat,
            checkOn: (f.value === "on"),
            optSelected: o.selected,
            getSetAttribute: p.className !== "t",
            enctype: !! d.createElement("form").enctype,
            html5Clone: d.createElement("nav").cloneNode(true).outerHTML !== "<:nav></:nav>",
            submitBubbles: true,
            changeBubbles: true,
            focusinBubbles: false,
            deleteExpando: true,
            noCloneEvent: true,
            inlineBlockNeedsLayout: false,
            shrinkWrapBlocks: false,
            reliableMarginRight: true
        };
        f.checked = true;
        s.noCloneChecked = f.cloneNode(true).checked;
        c.disabled = true;
        s.optDisabled = !o.disabled;
        try {
            delete p.test
        } catch (e) {
            s.deleteExpando = false
        }
        if (!p.addEventListener && p.attachEvent && p.fireEvent) {
            p.attachEvent("onclick", function() {
                s.noCloneEvent = false
            });
            p.cloneNode(true).fireEvent("onclick")
        }
        f = d.createElement("input");
        f.value = "t";
        f.setAttribute("type", "radio");
        s.radioValue = f.value === "t";
        f.setAttribute("checked", "checked");
        p.appendChild(f);
        j = d.createDocumentFragment();
        j.appendChild(p.lastChild);
        s.checkClone = j.cloneNode(true).cloneNode(true).lastChild.checked;
        s.appendChecked = f.checked;
        j.removeChild(f);
        j.appendChild(p);
        p.innerHTML = "";
        if (w.getComputedStyle) {
            m = d.createElement("div");
            m.style.width = "0";
            m.style.marginRight = "0";
            p.style.width = "2px";
            p.appendChild(m);
            s.reliableMarginRight = (parseInt((w.getComputedStyle(m, null) || {
                marginRight: 0
            }).marginRight, 10) || 0) === 0
        }
        if (p.attachEvent) {
            for (i in {
                submit: 1,
                change: 1,
                focusin: 1
            }) {
                l = "on" + i;
                n = (l in p);
                if (!n) {
                    p.setAttribute(l, "return;");
                    n = (typeof p[l] === "function")
                }
                s[i + "Bubbles"] = n
            }
        }
        j.removeChild(p);
        j = c = o = m = p = f = null;
        Q(function() {
            var r, v, _, R2, td, T2, U2, V2, vb, X2, Y2, Z2 = d.getElementsByTagName("body")[0];
            if (!Z2) {
                return
            }
            U2 = 1;
            V2 = "position:absolute;top:0;left:0;width:1px;height:1px;margin:0;";
            vb = "visibility:hidden;border:0;";
            X2 = "style='" + V2 + "border:5px solid #000;padding:0;'";
            Y2 = "<div " + X2 + "><div></div></div>" + "<table " + X2 + " cellpadding='0' cellspacing='0'>" + "<tr><td></td></tr></table>";
            r = d.createElement("div");
            r.style.cssText = vb + "width:0;height:0;position:static;top:0;margin-top:" + U2 + "px";
            Z2.insertBefore(r, Z2.firstChild);
            p = d.createElement("div");
            r.appendChild(p);
            p.innerHTML = "<table><tr><td style='padding:0;border:0;display:none'></td><td>t</td></tr></table>";
            t = p.getElementsByTagName("td");
            n = (t[0].offsetHeight === 0);
            t[0].style.display = "";
            t[1].style.display = "none";
            s.reliableHiddenOffsets = n && (t[0].offsetHeight === 0);
            p.innerHTML = "";
            p.style.width = p.style.paddingLeft = "1px";
            Q.boxModel = s.boxModel = p.offsetWidth === 2;
            if (typeof p.style.zoom !== "undefined") {
                p.style.display = "inline";
                p.style.zoom = 1;
                s.inlineBlockNeedsLayout = (p.offsetWidth === 2);
                p.style.display = "";
                p.innerHTML = "<div style='width:4px;'></div>";
                s.shrinkWrapBlocks = (p.offsetWidth !== 2)
            }
            p.style.cssText = V2 + vb;
            p.innerHTML = Y2;
            v = p.firstChild;
            _ = v.firstChild;
            td = v.nextSibling.firstChild.firstChild;
            T2 = {
                doesNotAddBorder: (_.offsetTop !== 5),
                doesAddBorderForTableAndCells: (td.offsetTop === 5)
            };
            _.style.position = "fixed";
            _.style.top = "20px";
            T2.fixedPosition = (_.offsetTop === 20 || _.offsetTop === 15);
            _.style.position = _.style.top = "";
            v.style.overflow = "hidden";
            v.style.position = "relative";
            T2.subtractsBorderForOverflowNotVisible = (_.offsetTop === -5);
            T2.doesNotIncludeMarginInBodyOffset = (Z2.offsetTop !== U2);
            Z2.removeChild(r);
            p = r = null;
            Q.extend(s, T2)
        });
        return s
    })();
    var A = /^(?:\{.*\}|\[.*\])$/,
        B = /([A-Z])/g;
    Q.extend({
        cache: {},
        uuid: 0,
        expando: "jQuery" + (Q.fn.jquery + Math.random()).replace(/\D/g, ""),
        noData: {
            "embed": true,
            "object": "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",
            "applet": true
        },
        hasData: function(a) {
            a = a.nodeType ? Q.cache[a[Q.expando]] : a[Q.expando];
            return !!a && !D(a)
        },
        data: function(a, n, b, p) {
            if (!Q.acceptData(a)) {
                return
            }
            var c, t, r, i = Q.expando,
                f = typeof n === "string",
                j = a.nodeType,
                k = j ? Q.cache : a,
                l = j ? a[i] : a[i] && i,
                m = n === "events";
            if ((!l || !k[l] || (!m && !p && !k[l].data)) && f && b === u) {
                return
            }
            if (!l) {
                if (j) {
                    a[i] = l = ++Q.uuid
                } else {
                    l = i
                }
            }
            if (!k[l]) {
                k[l] = {};
                if (!j) {
                    k[l].toJSON = Q.noop
                }
            }
            if (typeof n === "object" || typeof n === "function") {
                if (p) {
                    k[l] = Q.extend(k[l], n)
                } else {
                    k[l].data = Q.extend(k[l].data, n)
                }
            }
            c = t = k[l];
            if (!p) {
                if (!t.data) {
                    t.data = {}
                }
                t = t.data
            }
            if (b !== u) {
                t[Q.camelCase(n)] = b
            }
            if (m && !t[n]) {
                return c.events
            }
            if (f) {
                r = t[n];
                if (r == null) {
                    r = t[Q.camelCase(n)]
                }
            } else {
                r = t
            }
            return r
        },
        removeData: function(a, n, p) {
            if (!Q.acceptData(a)) {
                return
            }
            var t, i, l, b = Q.expando,
                c = a.nodeType,
                f = c ? Q.cache : a,
                j = c ? a[b] : b;
            if (!f[j]) {
                return
            }
            if (n) {
                t = p ? f[j] : f[j].data;
                if (t) {
                    if (!Q.isArray(n)) {
                        if (n in t) {
                            n = [n]
                        } else {
                            n = Q.camelCase(n);
                            if (n in t) {
                                n = [n]
                            } else {
                                n = n.split(" ")
                            }
                        }
                    }
                    for (i = 0, l = n.length; i < l; i++) {
                        delete t[n[i]]
                    }
                    if (!(p ? D : Q.isEmptyObject)(t)) {
                        return
                    }
                }
            }
            if (!p) {
                delete f[j].data;
                if (!D(f[j])) {
                    return
                }
            }
            if (Q.support.deleteExpando || !f.setInterval) {
                delete f[j]
            } else {
                f[j] = null
            }
            if (c) {
                if (Q.support.deleteExpando) {
                    delete a[b]
                } else if (a.removeAttribute) {
                    a.removeAttribute(b)
                } else {
                    a[b] = null
                }
            }
        },
        _data: function(a, n, b) {
            return Q.data(a, n, b, true)
        },
        acceptData: function(a) {
            if (a.nodeName) {
                var m = Q.noData[a.nodeName.toLowerCase()];
                if (m) {
                    return !(m === true || a.getAttribute("classid") !== m)
                }
            }
            return true
        }
    });
    Q.fn.extend({
        data: function(k, v) {
            var p, a, n, b = null;
            if (typeof k === "undefined") {
                if (this.length) {
                    b = Q.data(this[0]);
                    if (this[0].nodeType === 1 && !Q._data(this[0], "parsedAttrs")) {
                        a = this[0].attributes;
                        for (var i = 0, l = a.length; i < l; i++) {
                            n = a[i].name;
                            if (n.indexOf("data-") === 0) {
                                n = Q.camelCase(n.substring(5));
                                C(this[0], n, b[n])
                            }
                        }
                        Q._data(this[0], "parsedAttrs", true)
                    }
                }
                return b
            } else if (typeof k === "object") {
                return this.each(function() {
                    Q.data(this, k)
                })
            }
            p = k.split(".");
            p[1] = p[1] ? "." + p[1] : "";
            if (v === u) {
                b = this.triggerHandler("getData" + p[1] + "!", [p[0]]);
                if (b === u && this.length) {
                    b = Q.data(this[0], k);
                    b = C(this[0], k, b)
                }
                return b === u && p[1] ? this.data(p[0]) : b
            } else {
                return this.each(function() {
                    var s = Q(this),
                        c = [p[0], v];
                    s.triggerHandler("setData" + p[1] + "!", c);
                    Q.data(this, k, v);
                    s.triggerHandler("changeData" + p[1] + "!", c)
                })
            }
        },
        removeData: function(k) {
            return this.each(function() {
                Q.removeData(this, k)
            })
        }
    });

    function C(a, k, b) {
        if (b === u && a.nodeType === 1) {
            var n = "data-" + k.replace(B, "-$1").toLowerCase();
            b = a.getAttribute(n);
            if (typeof b === "string") {
                try {
                    b = b === "true" ? true : b === "false" ? false : b === "null" ? null : Q.isNumeric(b) ? parseFloat(b) : A.test(b) ? Q.parseJSON(b) : b
                } catch (e) {}
                Q.data(a, k, b)
            } else {
                b = u
            }
        }
        return b
    }
    function D(o) {
        for (var n in o) {
            if (n === "data" && Q.isEmptyObject(o[n])) {
                continue
            }
            if (n !== "toJSON") {
                return false
            }
        }
        return true
    }
    function E(a, t, s) {
        var b = t + "defer",
            q = t + "queue",
            m = t + "mark",
            c = Q._data(a, b);
        if (c && (s === "queue" || !Q._data(a, q)) && (s === "mark" || !Q._data(a, m))) {
            setTimeout(function() {
                if (!Q._data(a, q) && !Q._data(a, m)) {
                    Q.removeData(a, b, true);
                    c.fire()
                }
            }, 0)
        }
    }
    Q.extend({
        _mark: function(a, t) {
            if (a) {
                t = (t || "fx") + "mark";
                Q._data(a, t, (Q._data(a, t) || 0) + 1)
            }
        },
        _unmark: function(f, a, t) {
            if (f !== true) {
                t = a;
                a = f;
                f = false
            }
            if (a) {
                t = t || "fx";
                var k = t + "mark",
                    c = f ? 0 : ((Q._data(a, k) || 1) - 1);
                if (c) {
                    Q._data(a, k, c)
                } else {
                    Q.removeData(a, k, true);
                    E(a, t, "mark")
                }
            }
        },
        queue: function(a, t, b) {
            var q;
            if (a) {
                t = (t || "fx") + "queue";
                q = Q._data(a, t);
                if (b) {
                    if (!q || Q.isArray(b)) {
                        q = Q._data(a, t, Q.makeArray(b))
                    } else {
                        q.push(b)
                    }
                }
                return q || []
            }
        },
        dequeue: function(a, t) {
            t = t || "fx";
            var q = Q.queue(a, t),
                f = q.shift(),
                b = {};
            if (f === "inprogress") {
                f = q.shift()
            }
            if (f) {
                if (t === "fx") {
                    q.unshift("inprogress")
                }
                Q._data(a, t + ".run", b);
                f.call(a, function() {
                    Q.dequeue(a, t)
                }, b)
            }
            if (!q.length) {
                Q.removeData(a, t + "queue " + t + ".run", true);
                E(a, t, "queue")
            }
        }
    });
    Q.fn.extend({
        queue: function(t, a) {
            if (typeof t !== "string") {
                a = t;
                t = "fx"
            }
            if (a === u) {
                return Q.queue(this[0], t)
            }
            return this.each(function() {
                var q = Q.queue(this, t, a);
                if (t === "fx" && q[0] !== "inprogress") {
                    Q.dequeue(this, t)
                }
            })
        },
        dequeue: function(t) {
            return this.each(function() {
                Q.dequeue(this, t)
            })
        },
        delay: function(t, a) {
            t = Q.fx ? Q.fx.speeds[t] || t : t;
            a = a || "fx";
            return this.queue(a, function(n, b) {
                var c = setTimeout(n, t);
                b.stop = function() {
                    clearTimeout(c)
                }
            })
        },
        clearQueue: function(t) {
            return this.queue(t || "fx", [])
        },
        promise: function(t, o) {
            if (typeof t !== "string") {
                o = t;
                t = u
            }
            t = t || "fx";
            var a = Q.Deferred(),
                b = this,
                i = b.length,
                c = 1,
                f = t + "defer",
                q = t + "queue",
                m = t + "mark",
                j;

            function r() {
                if (!(--c)) {
                    a.resolveWith(b, [b])
                }
            }
            while (i--) {
                if ((j = Q.data(b[i], f, u, true) || (Q.data(b[i], q, u, true) || Q.data(b[i], m, u, true)) && Q.data(b[i], f, Q.Callbacks("once memory"), true))) {
                    c++;
                    j.add(r)
                }
            }
            r();
            return a.promise()
        }
    });
    var F = /[\n\t\r]/g,
        G = /\s+/,
        H = /\r/g,
        I = /^(?:button|input)$/i,
        J = /^(?:button|input|object|select|textarea)$/i,
        K = /^a(?:rea)?$/i,
        L = /^(?:autofocus|autoplay|async|checked|controls|defer|disabled|hidden|loop|multiple|open|readonly|required|scoped|selected)$/i,
        M = Q.support.getSetAttribute,
        N, O, P;
    Q.fn.extend({
        attr: function(n, v) {
            return Q.access(this, n, v, true, Q.attr)
        },
        removeAttr: function(n) {
            return this.each(function() {
                Q.removeAttr(this, n)
            })
        },
        prop: function(n, v) {
            return Q.access(this, n, v, true, Q.prop)
        },
        removeProp: function(n) {
            n = Q.propFix[n] || n;
            return this.each(function() {
                try {
                    this[n] = u;
                    delete this[n]
                } catch (e) {}
            })
        },
        addClass: function(v) {
            var a, i, l, b, s, c, f;
            if (Q.isFunction(v)) {
                return this.each(function(j) {
                    Q(this).addClass(v.call(this, j, this.className))
                })
            }
            if (v && typeof v === "string") {
                a = v.split(G);
                for (i = 0, l = this.length; i < l; i++) {
                    b = this[i];
                    if (b.nodeType === 1) {
                        if (!b.className && a.length === 1) {
                            b.className = v
                        } else {
                            s = " " + b.className + " ";
                            for (c = 0, f = a.length; c < f; c++) {
                                if (!~s.indexOf(" " + a[c] + " ")) {
                                    s += a[c] + " "
                                }
                            }
                            b.className = Q.trim(s)
                        }
                    }
                }
            }
            return this
        },
        removeClass: function(v) {
            var a, i, l, b, f, c, k;
            if (Q.isFunction(v)) {
                return this.each(function(j) {
                    Q(this).removeClass(v.call(this, j, this.className))
                })
            }
            if ((v && typeof v === "string") || v === u) {
                a = (v || "").split(G);
                for (i = 0, l = this.length; i < l; i++) {
                    b = this[i];
                    if (b.nodeType === 1 && b.className) {
                        if (v) {
                            f = (" " + b.className + " ").replace(F, " ");
                            for (c = 0, k = a.length; c < k; c++) {
                                f = f.replace(" " + a[c] + " ", " ")
                            }
                            b.className = Q.trim(f)
                        } else {
                            b.className = ""
                        }
                    }
                }
            }
            return this
        },
        toggleClass: function(v, s) {
            var t = typeof v,
                a = typeof s === "boolean";
            if (Q.isFunction(v)) {
                return this.each(function(i) {
                    Q(this).toggleClass(v.call(this, i, this.className, s), s)
                })
            }
            return this.each(function() {
                if (t === "string") {
                    var c, i = 0,
                        b = Q(this),
                        f = s,
                        j = v.split(G);
                    while ((c = j[i++])) {
                        f = a ? f : !b.hasClass(c);
                        b[f ? "addClass" : "removeClass"](c)
                    }
                } else if (t === "undefined" || t === "boolean") {
                    if (this.className) {
                        Q._data(this, "__className__", this.className)
                    }
                    this.className = this.className || v === false ? "" : Q._data(this, "__className__") || ""
                }
            })
        },
        hasClass: function(s) {
            var c = " " + s + " ",
                i = 0,
                l = this.length;
            for (; i < l; i++) {
                if (this[i].nodeType === 1 && (" " + this[i].className + " ").replace(F, " ").indexOf(c) > -1) {
                    return true
                }
            }
            return false
        },
        val: function(v) {
            var a, r, b, c = this[0];
            if (!arguments.length) {
                if (c) {
                    a = Q.valHooks[c.nodeName.toLowerCase()] || Q.valHooks[c.type];
                    if (a && "get" in a && (r = a.get(c, "value")) !== u) {
                        return r
                    }
                    r = c.value;
                    return typeof r === "string" ? r.replace(H, "") : r == null ? "" : r
                }
                return
            }
            b = Q.isFunction(v);
            return this.each(function(i) {
                var s = Q(this),
                    f;
                if (this.nodeType !== 1) {
                    return
                }
                if (b) {
                    f = v.call(this, i, s.val())
                } else {
                    f = v
                }
                if (f == null) {
                    f = ""
                } else if (typeof f === "number") {
                    f += ""
                } else if (Q.isArray(f)) {
                    f = Q.map(f, function(v) {
                        return v == null ? "" : v + ""
                    })
                }
                a = Q.valHooks[this.nodeName.toLowerCase()] || Q.valHooks[this.type];
                if (!a || !("set" in a) || a.set(this, f, "value") === u) {
                    this.value = f
                }
            })
        }
    });
    Q.extend({
        valHooks: {
            option: {
                get: function(a) {
                    var v = a.attributes.value;
                    return !v || v.specified ? a.value : a.text
                }
            },
            select: {
                get: function(a) {
                    var v, i, m, o, b = a.selectedIndex,
                        c = [],
                        f = a.options,
                        j = a.type === "select-one";
                    if (b < 0) {
                        return null
                    }
                    i = j ? b : 0;
                    m = j ? b + 1 : f.length;
                    for (; i < m; i++) {
                        o = f[i];
                        if (o.selected && (Q.support.optDisabled ? !o.disabled : o.getAttribute("disabled") === null) && (!o.parentNode.disabled || !Q.nodeName(o.parentNode, "optgroup"))) {
                            v = Q(o).val();
                            if (j) {
                                return v
                            }
                            c.push(v)
                        }
                    }
                    if (j && !c.length && f.length) {
                        return Q(f[b]).val()
                    }
                    return c
                },
                set: function(a, v) {
                    var b = Q.makeArray(v);
                    Q(a).find("option").each(function() {
                        this.selected = Q.inArray(Q(this).val(), b) >= 0
                    });
                    if (!b.length) {
                        a.selectedIndex = -1
                    }
                    return b
                }
            }
        },
        attrFn: {
            val: true,
            css: true,
            html: true,
            text: true,
            data: true,
            width: true,
            height: true,
            offset: true
        },
        attr: function(a, n, v, p) {
            var r, b, c, f = a.nodeType;
            if (!a || f === 3 || f === 8 || f === 2) {
                return
            }
            if (p && n in Q.attrFn) {
                return Q(a)[n](v)
            }
            if (typeof a.getAttribute === "undefined") {
                return Q.prop(a, n, v)
            }
            c = f !== 1 || !Q.isXMLDoc(a);
            if (c) {
                n = n.toLowerCase();
                b = Q.attrHooks[n] || (L.test(n) ? O : N)
            }
            if (v !== u) {
                if (v === null) {
                    Q.removeAttr(a, n);
                    return
                } else if (b && "set" in b && c && (r = b.set(a, v, n)) !== u) {
                    return r
                } else {
                    a.setAttribute(n, "" + v);
                    return v
                }
            } else if (b && "get" in b && c && (r = b.get(a, n)) !== null) {
                return r
            } else {
                r = a.getAttribute(n);
                return r === null ? u : r
            }
        },
        removeAttr: function(a, v) {
            var p, b, n, l, i = 0;
            if (v && a.nodeType === 1) {
                b = v.toLowerCase().split(G);
                l = b.length;
                for (; i < l; i++) {
                    n = b[i];
                    if (n) {
                        p = Q.propFix[n] || n;
                        Q.attr(a, n, "");
                        a.removeAttribute(M ? n : p);
                        if (L.test(n) && p in a) {
                            a[p] = false
                        }
                    }
                }
            }
        },
        attrHooks: {
            type: {
                set: function(a, v) {
                    if (I.test(a.nodeName) && a.parentNode) {
                        Q.error("type property can't be changed")
                    } else if (!Q.support.radioValue && v === "radio" && Q.nodeName(a, "input")) {
                        var b = a.value;
                        a.setAttribute("type", v);
                        if (b) {
                            a.value = b
                        }
                        return v
                    }
                }
            },
            value: {
                get: function(a, n) {
                    if (N && Q.nodeName(a, "button")) {
                        return N.get(a, n)
                    }
                    return n in a ? a.value : null
                },
                set: function(a, v, n) {
                    if (N && Q.nodeName(a, "button")) {
                        return N.set(a, v, n)
                    }
                    a.value = v
                }
            }
        },
        propFix: {
            tabindex: "tabIndex",
            readonly: "readOnly",
            "for": "htmlFor",
            "class": "className",
            maxlength: "maxLength",
            cellspacing: "cellSpacing",
            cellpadding: "cellPadding",
            rowspan: "rowSpan",
            colspan: "colSpan",
            usemap: "useMap",
            frameborder: "frameBorder",
            contenteditable: "contentEditable"
        },
        prop: function(a, n, v) {
            var r, b, c, f = a.nodeType;
            if (!a || f === 3 || f === 8 || f === 2) {
                return
            }
            c = f !== 1 || !Q.isXMLDoc(a);
            if (c) {
                n = Q.propFix[n] || n;
                b = Q.propHooks[n]
            }
            if (v !== u) {
                if (b && "set" in b && (r = b.set(a, v, n)) !== u) {
                    return r
                } else {
                    return (a[n] = v)
                }
            } else {
                if (b && "get" in b && (r = b.get(a, n)) !== null) {
                    return r
                } else {
                    return a[n]
                }
            }
        },
        propHooks: {
            tabIndex: {
                get: function(a) {
                    var b = a.getAttributeNode("tabindex");
                    return b && b.specified ? parseInt(b.value, 10) : J.test(a.nodeName) || K.test(a.nodeName) && a.href ? 0 : u
                }
            }
        }
    });
    Q.attrHooks.tabindex = Q.propHooks.tabIndex;
    O = {
        get: function(a, n) {
            var b, p = Q.prop(a, n);
            return p === true || typeof p !== "boolean" && (b = a.getAttributeNode(n)) && b.nodeValue !== false ? n.toLowerCase() : u
        },
        set: function(a, v, n) {
            var p;
            if (v === false) {
                Q.removeAttr(a, n)
            } else {
                p = Q.propFix[n] || n;
                if (p in a) {
                    a[p] = true
                }
                a.setAttribute(n, n.toLowerCase())
            }
            return n
        }
    };
    if (!M) {
        P = {
            name: true,
            id: true
        };
        N = Q.valHooks.button = {
            get: function(a, n) {
                var r;
                r = a.getAttributeNode(n);
                return r && (P[n] ? r.nodeValue !== "" : r.specified) ? r.nodeValue : u
            },
            set: function(a, v, n) {
                var r = a.getAttributeNode(n);
                if (!r) {
                    r = d.createAttribute(n);
                    a.setAttributeNode(r)
                }
                return (r.nodeValue = v + "")
            }
        };
        Q.attrHooks.tabindex.set = N.set;
        Q.each(["width", "height"], function(i, n) {
            Q.attrHooks[n] = Q.extend(Q.attrHooks[n], {
                set: function(a, v) {
                    if (v === "") {
                        a.setAttribute(n, "auto");
                        return v
                    }
                }
            })
        });
        Q.attrHooks.contenteditable = {
            get: N.get,
            set: function(a, v, n) {
                if (v === "") {
                    v = "false"
                }
                N.set(a, v, n)
            }
        }
    }
    if (!Q.support.hrefNormalized) {
        Q.each(["href", "src", "width", "height"], function(i, n) {
            Q.attrHooks[n] = Q.extend(Q.attrHooks[n], {
                get: function(a) {
                    var r = a.getAttribute(n, 2);
                    return r === null ? u : r
                }
            })
        })
    }
    if (!Q.support.style) {
        Q.attrHooks.style = {
            get: function(a) {
                return a.style.cssText.toLowerCase() || u
            },
            set: function(a, v) {
                return (a.style.cssText = "" + v)
            }
        }
    }
    if (!Q.support.optSelected) {
        Q.propHooks.selected = Q.extend(Q.propHooks.selected, {
            get: function(a) {
                var p = a.parentNode;
                if (p) {
                    p.selectedIndex;
                    if (p.parentNode) {
                        p.parentNode.selectedIndex
                    }
                }
                return null
            }
        })
    }
    if (!Q.support.enctype) {
        Q.propFix.enctype = "encoding"
    }
    if (!Q.support.checkOn) {
        Q.each(["radio", "checkbox"], function() {
            Q.valHooks[this] = {
                get: function(a) {
                    return a.getAttribute("value") === null ? "on" : a.value
                }
            }
        })
    }
    Q.each(["radio", "checkbox"], function() {
        Q.valHooks[this] = Q.extend(Q.valHooks[this], {
            set: function(a, v) {
                if (Q.isArray(v)) {
                    return (a.checked = Q.inArray(Q(a).val(), v) >= 0)
                }
            }
        })
    });
    var R = /^(?:textarea|input|select)$/i,
        S = /^([^\.]*)?(?:\.(.+))?$/,
        T = /\bhover(\.\S+)?\b/,
        U = /^key/,
        V = /^(?:mouse|contextmenu)|click/,
        W = /^(?:focusinfocus|focusoutblur)$/,
        X = /^(\w*)(?:#([\w\-]+))?(?:\.([\w\-]+))?$/,
        Y = function(s) {
            var q = X.exec(s);
            if (q) {
                q[1] = (q[1] || "").toLowerCase();
                q[3] = q[3] && new RegExp("(?:^|\\s)" + q[3] + "(?:\\s|$)")
            }
            return q
        }, Z = function(a, m) {
            var b = a.attributes || {};
            return ((!m[1] || a.nodeName.toLowerCase() === m[1]) && (!m[2] || (b.id || {}).value === m[2]) && (!m[3] || m[3].test((b["class"] || {}).value)))
        }, $ = function(a) {
            return Q.event.special.hover ? a : a.replace(T, "mouseenter$1 mouseleave$1")
        };
    Q.event = {
        add: function(a, b, c, f, s) {
            var i, j, k, t, l, m, n, o, p, q, r, v;
            if (a.nodeType === 3 || a.nodeType === 8 || !b || !c || !(i = Q._data(a))) {
                return
            }
            if (c.handler) {
                p = c;
                c = p.handler
            }
            if (!c.guid) {
                c.guid = Q.guid++
            }
            k = i.events;
            if (!k) {
                i.events = k = {}
            }
            j = i.handle;
            if (!j) {
                i.handle = j = function(e) {
                    return typeof Q !== "undefined" && (!e || Q.event.triggered !== e.type) ? Q.event.dispatch.apply(j.elem, arguments) : u
                };
                j.elem = a
            }
            b = Q.trim($(b)).split(" ");
            for (t = 0; t < b.length; t++) {
                l = S.exec(b[t]) || [];
                m = l[1];
                n = (l[2] || "").split(".").sort();
                v = Q.event.special[m] || {};
                m = (s ? v.delegateType : v.bindType) || m;
                v = Q.event.special[m] || {};
                o = Q.extend({
                    type: m,
                    origType: l[1],
                    data: f,
                    handler: c,
                    guid: c.guid,
                    selector: s,
                    quick: Y(s),
                    namespace: n.join(".")
                }, p);
                r = k[m];
                if (!r) {
                    r = k[m] = [];
                    r.delegateCount = 0;
                    if (!v.setup || v.setup.call(a, f, n, j) === false) {
                        if (a.addEventListener) {
                            a.addEventListener(m, j, false)
                        } else if (a.attachEvent) {
                            a.attachEvent("on" + m, j)
                        }
                    }
                }
                if (v.add) {
                    v.add.call(a, o);
                    if (!o.handler.guid) {
                        o.handler.guid = c.guid
                    }
                }
                if (s) {
                    r.splice(r.delegateCount++, 0, o)
                } else {
                    r.push(o)
                }
                Q.event.global[m] = true
            }
            a = null
        },
        global: {},
        remove: function(a, b, c, s, m) {
            var f = Q.hasData(a) && Q._data(a),
                t, i, k, o, n, l, j, p, q, r, v, _;
            if (!f || !(p = f.events)) {
                return
            }
            b = Q.trim($(b || "")).split(" ");
            for (t = 0; t < b.length; t++) {
                i = S.exec(b[t]) || [];
                k = o = i[1];
                n = i[2];
                if (!k) {
                    for (k in p) {
                        Q.event.remove(a, k + b[t], c, s, true)
                    }
                    continue
                }
                q = Q.event.special[k] || {};
                k = (s ? q.delegateType : q.bindType) || k;
                v = p[k] || [];
                l = v.length;
                n = n ? new RegExp("(^|\\.)" + n.split(".").sort().join("\\.(?:.*\\.)?") + "(\\.|$)") : null;
                for (j = 0; j < v.length; j++) {
                    _ = v[j];
                    if ((m || o === _.origType) && (!c || c.guid === _.guid) && (!n || n.test(_.namespace)) && (!s || s === _.selector || s === "**" && _.selector)) {
                        v.splice(j--, 1);
                        if (_.selector) {
                            v.delegateCount--
                        }
                        if (q.remove) {
                            q.remove.call(a, _)
                        }
                    }
                }
                if (v.length === 0 && l !== v.length) {
                    if (!q.teardown || q.teardown.call(a, n) === false) {
                        Q.removeEvent(a, k, f.handle)
                    }
                    delete p[k]
                }
            }
            if (Q.isEmptyObject(p)) {
                r = f.handle;
                if (r) {
                    r.elem = null
                }
                Q.removeData(a, ["events", "handle"], true)
            }
        },
        customEvent: {
            "getData": true,
            "setData": true,
            "changeData": true
        },
        trigger: function(a, b, c, o) {
            if (c && (c.nodeType === 3 || c.nodeType === 8)) {
                return
            }
            var t = a.type || a,
                n = [],
                f, j, i, k, l, m, s, p, q, r;
            if (W.test(t + Q.event.triggered)) {
                return
            }
            if (t.indexOf("!") >= 0) {
                t = t.slice(0, -1);
                j = true
            }
            if (t.indexOf(".") >= 0) {
                n = t.split(".");
                t = n.shift();
                n.sort()
            }
            if ((!c || Q.event.customEvent[t]) && !Q.event.global[t]) {
                return
            }
            a = typeof a === "object" ? a[Q.expando] ? a : new Q.Event(t, a) : new Q.Event(t);
            a.type = t;
            a.isTrigger = true;
            a.exclusive = j;
            a.namespace = n.join(".");
            a.namespace_re = a.namespace ? new RegExp("(^|\\.)" + n.join("\\.(?:.*\\.)?") + "(\\.|$)") : null;
            m = t.indexOf(":") < 0 ? "on" + t : "";
            if (!c) {
                f = Q.cache;
                for (i in f) {
                    if (f[i].events && f[i].events[t]) {
                        Q.event.trigger(a, b, f[i].handle.elem, true)
                    }
                }
                return
            }
            a.result = u;
            if (!a.target) {
                a.target = c
            }
            b = b != null ? Q.makeArray(b) : [];
            b.unshift(a);
            s = Q.event.special[t] || {};
            if (s.trigger && s.trigger.apply(c, b) === false) {
                return
            }
            q = [
                [c, s.bindType || t]
            ];
            if (!o && !s.noBubble && !Q.isWindow(c)) {
                r = s.delegateType || t;
                k = W.test(r + t) ? c : c.parentNode;
                l = null;
                for (; k; k = k.parentNode) {
                    q.push([k, r]);
                    l = k
                }
                if (l && l === c.ownerDocument) {
                    q.push([l.defaultView || l.parentWindow || w, r])
                }
            }
            for (i = 0; i < q.length && !a.isPropagationStopped(); i++) {
                k = q[i][0];
                a.type = q[i][1];
                p = (Q._data(k, "events") || {})[a.type] && Q._data(k, "handle");
                if (p) {
                    p.apply(k, b)
                }
                p = m && k[m];
                if (p && Q.acceptData(k) && p.apply(k, b) === false) {
                    a.preventDefault()
                }
            }
            a.type = t;
            if (!o && !a.isDefaultPrevented()) {
                if ((!s._default || s._default.apply(c.ownerDocument, b) === false) && !(t === "click" && Q.nodeName(c, "a")) && Q.acceptData(c)) {
                    if (m && c[t] && ((t !== "focus" && t !== "blur") || a.target.offsetWidth !== 0) && !Q.isWindow(c)) {
                        l = c[m];
                        if (l) {
                            c[m] = null
                        }
                        Q.event.triggered = t;
                        c[t]();
                        Q.event.triggered = u;
                        if (l) {
                            c[m] = l
                        }
                    }
                }
            }
            return a.result
        },
        dispatch: function(a) {
            a = Q.event.fix(a || w.event);
            var b = ((Q._data(this, "events") || {})[a.type] || []),
                c = b.delegateCount,
                f = [].slice.call(arguments, 0),
                r = !a.exclusive && !a.namespace,
                k = [],
                i, j, l, m, n, s, o, p, q, t, v;
            f[0] = a;
            a.delegateTarget = this;
            if (c && !a.target.disabled && !(a.button && a.type === "click")) {
                m = Q(this);
                m.context = this.ownerDocument || this;
                for (l = a.target; l != this; l = l.parentNode || this) {
                    s = {};
                    p = [];
                    m[0] = l;
                    for (i = 0; i < c; i++) {
                        q = b[i];
                        t = q.selector;
                        if (s[t] === u) {
                            s[t] = (q.quick ? Z(l, q.quick) : m.is(t))
                        }
                        if (s[t]) {
                            p.push(q)
                        }
                    }
                    if (p.length) {
                        k.push({
                            elem: l,
                            matches: p
                        })
                    }
                }
            }
            if (b.length > c) {
                k.push({
                    elem: this,
                    matches: b.slice(c)
                })
            }
            for (i = 0; i < k.length && !a.isPropagationStopped(); i++) {
                o = k[i];
                a.currentTarget = o.elem;
                for (j = 0; j < o.matches.length && !a.isImmediatePropagationStopped(); j++) {
                    q = o.matches[j];
                    if (r || (!a.namespace && !q.namespace) || a.namespace_re && a.namespace_re.test(q.namespace)) {
                        a.data = q.data;
                        a.handleObj = q;
                        n = ((Q.event.special[q.origType] || {}).handle || q.handler).apply(o.elem, f);
                        if (n !== u) {
                            a.result = n;
                            if (n === false) {
                                a.preventDefault();
                                a.stopPropagation()
                            }
                        }
                    }
                }
            }
            return a.result
        },
        props: "attrChange attrName relatedNode srcElement altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),
        fixHooks: {},
        keyHooks: {
            props: "char charCode key keyCode".split(" "),
            filter: function(a, o) {
                if (a.which == null) {
                    a.which = o.charCode != null ? o.charCode : o.keyCode
                }
                return a
            }
        },
        mouseHooks: {
            props: "button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
            filter: function(a, o) {
                var b, c, f, i = o.button,
                    j = o.fromElement;
                if (a.pageX == null && o.clientX != null) {
                    b = a.target.ownerDocument || d;
                    c = b.documentElement;
                    f = b.body;
                    a.pageX = o.clientX + (c && c.scrollLeft || f && f.scrollLeft || 0) - (c && c.clientLeft || f && f.clientLeft || 0);
                    a.pageY = o.clientY + (c && c.scrollTop || f && f.scrollTop || 0) - (c && c.clientTop || f && f.clientTop || 0)
                }
                if (!a.relatedTarget && j) {
                    a.relatedTarget = j === a.target ? o.toElement : j
                }
                if (!a.which && i !== u) {
                    a.which = (i & 1 ? 1 : (i & 2 ? 3 : (i & 4 ? 2 : 0)))
                }
                return a
            }
        },
        fix: function(a) {
            if (a[Q.expando]) {
                return a
            }
            var i, p, o = a,
                f = Q.event.fixHooks[a.type] || {}, c = f.props ? this.props.concat(f.props) : this.props;
            a = Q.Event(o);
            for (i = c.length; i;) {
                p = c[--i];
                a[p] = o[p]
            }
            if (!a.target) {
                a.target = o.srcElement || d
            }
            if (a.target.nodeType === 3) {
                a.target = a.target.parentNode
            }
            if (a.metaKey === u) {
                a.metaKey = a.ctrlKey
            }
            return f.filter ? f.filter(a, o) : a
        },
        special: {
            ready: {
                setup: Q.bindReady
            },
            load: {
                noBubble: true
            },
            focus: {
                delegateType: "focusin"
            },
            blur: {
                delegateType: "focusout"
            },
            beforeunload: {
                setup: function(a, n, b) {
                    if (Q.isWindow(this)) {
                        this.onbeforeunload = b
                    }
                },
                teardown: function(n, a) {
                    if (this.onbeforeunload === a) {
                        this.onbeforeunload = null
                    }
                }
            }
        },
        simulate: function(t, a, b, c) {
            var e = Q.extend(new Q.Event(), b, {
                type: t,
                isSimulated: true,
                originalEvent: {}
            });
            if (c) {
                Q.event.trigger(e, null, a)
            } else {
                Q.event.dispatch.call(a, e)
            }
            if (e.isDefaultPrevented()) {
                b.preventDefault()
            }
        }
    };
    Q.event.handle = Q.event.dispatch;
    Q.removeEvent = d.removeEventListener ? function(a, t, b) {
        if (a.removeEventListener) {
            a.removeEventListener(t, b, false)
        }
    } : function(a, t, b) {
        if (a.detachEvent) {
            a.detachEvent("on" + t, b)
        }
    };
    Q.Event = function(s, p) {
        if (!(this instanceof Q.Event)) {
            return new Q.Event(s, p)
        }
        if (s && s.type) {
            this.originalEvent = s;
            this.type = s.type;
            this.isDefaultPrevented = (s.defaultPrevented || s.returnValue === false || s.getPreventDefault && s.getPreventDefault()) ? b1 : a1
        } else {
            this.type = s
        }
        if (p) {
            Q.extend(this, p)
        }
        this.timeStamp = s && s.timeStamp || Q.now();
        this[Q.expando] = true
    };

    function a1() {
        return false
    }
    function b1() {
        return true
    }
    Q.Event.prototype = {
        preventDefault: function() {
            this.isDefaultPrevented = b1;
            var e = this.originalEvent;
            if (!e) {
                return
            }
            if (e.preventDefault) {
                e.preventDefault()
            } else {
                e.returnValue = false
            }
        },
        stopPropagation: function() {
            this.isPropagationStopped = b1;
            var e = this.originalEvent;
            if (!e) {
                return
            }
            if (e.stopPropagation) {
                e.stopPropagation()
            }
            e.cancelBubble = true
        },
        stopImmediatePropagation: function() {
            this.isImmediatePropagationStopped = b1;
            this.stopPropagation()
        },
        isDefaultPrevented: a1,
        isPropagationStopped: a1,
        isImmediatePropagationStopped: a1
    };
    Q.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function(o, f) {
        Q.event.special[o] = {
            delegateType: f,
            bindType: f,
            handle: function(a) {
                var t = this,
                    r = a.relatedTarget,
                    b = a.handleObj,
                    s = b.selector,
                    c;
                if (!r || (r !== t && !Q.contains(t, r))) {
                    a.type = b.origType;
                    c = b.handler.apply(this, arguments);
                    a.type = f
                }
                return c
            }
        }
    });
    if (!Q.support.submitBubbles) {
        Q.event.special.submit = {
            setup: function() {
                if (Q.nodeName(this, "form")) {
                    return false
                }
                Q.event.add(this, "click._submit keypress._submit", function(e) {
                    var a = e.target,
                        f = Q.nodeName(a, "input") || Q.nodeName(a, "button") ? a.form : u;
                    if (f && !f._submit_attached) {
                        Q.event.add(f, "submit._submit", function(b) {
                            if (this.parentNode && !b.isTrigger) {
                                Q.event.simulate("submit", this.parentNode, b, true)
                            }
                        });
                        f._submit_attached = true
                    }
                })
            },
            teardown: function() {
                if (Q.nodeName(this, "form")) {
                    return false
                }
                Q.event.remove(this, "._submit")
            }
        }
    }
    if (!Q.support.changeBubbles) {
        Q.event.special.change = {
            setup: function() {
                if (R.test(this.nodeName)) {
                    if (this.type === "checkbox" || this.type === "radio") {
                        Q.event.add(this, "propertychange._change", function(a) {
                            if (a.originalEvent.propertyName === "checked") {
                                this._just_changed = true
                            }
                        });
                        Q.event.add(this, "click._change", function(a) {
                            if (this._just_changed && !a.isTrigger) {
                                this._just_changed = false;
                                Q.event.simulate("change", this, a, true)
                            }
                        })
                    }
                    return false
                }
                Q.event.add(this, "beforeactivate._change", function(e) {
                    var a = e.target;
                    if (R.test(a.nodeName) && !a._change_attached) {
                        Q.event.add(a, "change._change", function(b) {
                            if (this.parentNode && !b.isSimulated && !b.isTrigger) {
                                Q.event.simulate("change", this.parentNode, b, true)
                            }
                        });
                        a._change_attached = true
                    }
                })
            },
            handle: function(a) {
                var b = a.target;
                if (this !== b || a.isSimulated || a.isTrigger || (b.type !== "radio" && b.type !== "checkbox")) {
                    return a.handleObj.handler.apply(this, arguments)
                }
            },
            teardown: function() {
                Q.event.remove(this, "._change");
                return R.test(this.nodeName)
            }
        }
    }
    if (!Q.support.focusinBubbles) {
        Q.each({
            focus: "focusin",
            blur: "focusout"
        }, function(o, f) {
            var a = 0,
                b = function(c) {
                    Q.event.simulate(f, c.target, Q.event.fix(c), true)
                };
            Q.event.special[f] = {
                setup: function() {
                    if (a++ === 0) {
                        d.addEventListener(o, b, true)
                    }
                },
                teardown: function() {
                    if (--a === 0) {
                        d.removeEventListener(o, b, true)
                    }
                }
            }
        })
    }
    Q.fn.extend({
        on: function(t, s, a, f, o) {
            var b, c;
            if (typeof t === "object") {
                if (typeof s !== "string") {
                    a = s;
                    s = u
                }
                for (c in t) {
                    this.on(c, s, a, t[c], o)
                }
                return this
            }
            if (a == null && f == null) {
                f = s;
                a = s = u
            } else if (f == null) {
                if (typeof s === "string") {
                    f = a;
                    a = u
                } else {
                    f = a;
                    a = s;
                    s = u
                }
            }
            if (f === false) {
                f = a1
            } else if (!f) {
                return this
            }
            if (o === 1) {
                b = f;
                f = function(i) {
                    Q().off(i);
                    return b.apply(this, arguments)
                };
                f.guid = b.guid || (b.guid = Q.guid++)
            }
            return this.each(function() {
                Q.event.add(this, t, f, a, s)
            })
        },
        one: function(t, s, a, f) {
            return this.on.call(this, t, s, a, f, 1)
        },
        off: function(t, s, f) {
            if (t && t.preventDefault && t.handleObj) {
                var a = t.handleObj;
                Q(t.delegateTarget).off(a.namespace ? a.type + "." + a.namespace : a.type, a.selector, a.handler);
                return this
            }
            if (typeof t === "object") {
                for (var b in t) {
                    this.off(b, s, t[b])
                }
                return this
            }
            if (s === false || typeof s === "function") {
                f = s;
                s = u
            }
            if (f === false) {
                f = a1
            }
            return this.each(function() {
                Q.event.remove(this, t, f, s)
            })
        },
        bind: function(t, a, f) {
            return this.on(t, null, a, f)
        },
        unbind: function(t, f) {
            return this.off(t, null, f)
        },
        live: function(t, a, f) {
            Q(this.context).on(t, this.selector, a, f);
            return this
        },
        die: function(t, f) {
            Q(this.context).off(t, this.selector || "**", f);
            return this
        },
        delegate: function(s, t, a, f) {
            return this.on(t, s, a, f)
        },
        undelegate: function(s, t, f) {
            return arguments.length == 1 ? this.off(s, "**") : this.off(t, s, f)
        },
        trigger: function(t, a) {
            return this.each(function() {
                Q.event.trigger(t, a, this)
            })
        },
        triggerHandler: function(t, a) {
            if (this[0]) {
                return Q.event.trigger(t, a, this[0], true)
            }
        },
        toggle: function(f) {
            var a = arguments,
                b = f.guid || Q.guid++,
                i = 0,
                t = function(c) {
                    var l = (Q._data(this, "lastToggle" + f.guid) || 0) % i;
                    Q._data(this, "lastToggle" + f.guid, l + 1);
                    c.preventDefault();
                    return a[l].apply(this, arguments) || false
                };
            t.guid = b;
            while (i < a.length) {
                a[i++].guid = b
            }
            return this.click(t)
        },
        hover: function(o, f) {
            return this.mouseenter(o).mouseleave(f || o)
        }
    });
    Q.each(("blur focus focusin focusout load resize scroll unload click dblclick " + "mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave " + "change select submit keydown keypress keyup error contextmenu").split(" "), function(i, n) {
        Q.fn[n] = function(a, f) {
            if (f == null) {
                f = a;
                a = null
            }
            return arguments.length > 0 ? this.on(n, null, a, f) : this.trigger(n)
        };
        if (Q.attrFn) {
            Q.attrFn[n] = true
        }
        if (U.test(n)) {
            Q.event.fixHooks[n] = Q.event.keyHooks
        }
        if (V.test(n)) {
            Q.event.fixHooks[n] = Q.event.mouseHooks
        }
    });

    /*!
     * Sizzle CSS Selector Engine
     *  Copyright 2011, The Dojo Foundation
     *  Released under the MIT, BSD, and GPL Licenses.
     *  More information: http://sizzlejs.com/
     */

    (function() {
        var c = /((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^\[\]]*\]|['"][^'"]*['"]|[^\[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?((?:.|\r|\n)*)/g,
            f = "sizcache" + (Math.random() + '').replace('.', ''),
            k = 0,
            t = Object.prototype.toString,
            n = false,
            o = true,
            r = /\\/g,
            p = /\r\n/g,
            q = /\W/;
        [0, 0].sort(function() {
            o = false;
            return 0
        });
        var s = function(a, b, j, l) {
            j = j || [];
            b = b || d;
            var $2 = b;
            if (b.nodeType !== 1 && b.nodeType !== 9) {
                return []
            }
            if (!a || typeof a !== "string") {
                return j
            }
            var m, _2, a3, b3, c3, d3, e3, i, f3 = true,
                g3 = s.isXML(b),
                h3 = [],
                i3 = a;
            do {
                c.exec("");
                m = c.exec(i3);
                if (m) {
                    i3 = m[3];
                    h3.push(m[1]);
                    if (m[2]) {
                        b3 = m[3];
                        break
                    }
                }
            } while (m);
            if (h3.length > 1 && R2.exec(a)) {
                if (h3.length === 2 && _.relative[h3[0]]) {
                    _2 = Z2(h3[0] + h3[1], b, l)
                } else {
                    _2 = _.relative[h3[0]] ? [b] : s(h3.shift(), b);
                    while (h3.length) {
                        a = h3.shift();
                        if (_.relative[a]) {
                            a += h3.shift()
                        }
                        _2 = Z2(a, _2, l)
                    }
                }
            } else {
                if (!l && h3.length > 1 && b.nodeType === 9 && !g3 && _.match.ID.test(h3[0]) && !_.match.ID.test(h3[h3.length - 1])) {
                    c3 = s.find(h3.shift(), b, g3);
                    b = c3.expr ? s.filter(c3.expr, c3.set)[0] : c3.set[0]
                }
                if (b) {
                    c3 = l ? {
                        expr: h3.pop(),
                        set: U2(l)
                    } : s.find(h3.pop(), h3.length === 1 && (h3[0] === "~" || h3[0] === "+") && b.parentNode ? b.parentNode : b, g3);
                    _2 = c3.expr ? s.filter(c3.expr, c3.set) : c3.set;
                    if (h3.length > 0) {
                        a3 = U2(_2)
                    } else {
                        f3 = false
                    }
                    while (h3.length) {
                        d3 = h3.pop();
                        e3 = d3;
                        if (!_.relative[d3]) {
                            d3 = ""
                        } else {
                            e3 = h3.pop()
                        }
                        if (e3 == null) {
                            e3 = b
                        }
                        _.relative[d3](a3, e3, g3)
                    }
                } else {
                    a3 = h3 = []
                }
            }
            if (!a3) {
                a3 = _2
            }
            if (!a3) {
                s.error(d3 || a)
            }
            if (t.call(a3) === "[object Array]") {
                if (!f3) {
                    j.push.apply(j, a3)
                } else if (b && b.nodeType === 1) {
                    for (i = 0; a3[i] != null; i++) {
                        if (a3[i] && (a3[i] === true || a3[i].nodeType === 1 && s.contains(b, a3[i]))) {
                            j.push(_2[i])
                        }
                    }
                } else {
                    for (i = 0; a3[i] != null; i++) {
                        if (a3[i] && a3[i].nodeType === 1) {
                            j.push(_2[i])
                        }
                    }
                }
            } else {
                U2(a3, j)
            }
            if (b3) {
                s(b3, $2, j, l);
                s.uniqueSort(j)
            }
            return j
        };
        s.uniqueSort = function(a) {
            if (V2) {
                n = o;
                a.sort(V2);
                if (n) {
                    for (var i = 1; i < a.length; i++) {
                        if (a[i] === a[i - 1]) {
                            a.splice(i--, 1)
                        }
                    }
                }
            }
            return a
        };
        s.matches = function(a, b) {
            return s(a, null, null, b)
        };
        s.matchesSelector = function(a, b) {
            return s(b, null, null, [a]).length > 0
        };
        s.find = function(a, b, j) {
            var l, i, m, $2, T2, _2;
            if (!a) {
                return []
            }
            for (i = 0, m = _.order.length; i < m; i++) {
                T2 = _.order[i];
                if (($2 = _.leftMatch[T2].exec(a))) {
                    _2 = $2[1];
                    $2.splice(1, 1);
                    if (_2.substr(_2.length - 1) !== "\\") {
                        $2[1] = ($2[1] || "").replace(r, "");
                        l = _.find[T2]($2, b, j);
                        if (l != null) {
                            a = a.replace(_.match[T2], "");
                            break
                        }
                    }
                }
            }
            if (!l) {
                l = typeof b.getElementsByTagName !== "undefined" ? b.getElementsByTagName("*") : []
            }
            return {
                set: l,
                expr: a
            }
        };
        s.filter = function(a, b, j, l) {
            var m, $2, T2, _2, a3, b3, c3, i, d3, e3 = a,
                f3 = [],
                g3 = b,
                h3 = b && b[0] && s.isXML(b[0]);
            while (a && b.length) {
                for (T2 in _.filter) {
                    if ((m = _.leftMatch[T2].exec(a)) != null && m[2]) {
                        b3 = _.filter[T2];
                        c3 = m[1];
                        $2 = false;
                        m.splice(1, 1);
                        if (c3.substr(c3.length - 1) === "\\") {
                            continue
                        }
                        if (g3 === f3) {
                            f3 = []
                        }
                        if (_.preFilter[T2]) {
                            m = _.preFilter[T2](m, g3, j, f3, l, h3);
                            if (!m) {
                                $2 = _2 = true
                            } else if (m === true) {
                                continue
                            }
                        }
                        if (m) {
                            for (i = 0;
                            (a3 = g3[i]) != null; i++) {
                                if (a3) {
                                    _2 = b3(a3, m, i, g3);
                                    d3 = l ^ _2;
                                    if (j && _2 != null) {
                                        if (d3) {
                                            $2 = true
                                        } else {
                                            g3[i] = false
                                        }
                                    } else if (d3) {
                                        f3.push(a3);
                                        $2 = true
                                    }
                                }
                            }
                        }
                        if (_2 !== u) {
                            if (!j) {
                                g3 = f3
                            }
                            a = a.replace(_.match[T2], "");
                            if (!$2) {
                                return []
                            }
                            break
                        }
                    }
                }
                if (a === e3) {
                    if ($2 == null) {
                        s.error(a)
                    } else {
                        break
                    }
                }
                e3 = a
            }
            return g3
        };
        s.error = function(m) {
            throw new Error("Syntax error, unrecognized expression: " + m)
        };
        var v = s.getText = function(a) {
            var i, b, j = a.nodeType,
                l = "";
            if (j) {
                if (j === 1 || j === 9) {
                    if (typeof a.textContent === 'string') {
                        return a.textContent
                    } else if (typeof a.innerText === 'string') {
                        return a.innerText.replace(p, '')
                    } else {
                        for (a = a.firstChild; a; a = a.nextSibling) {
                            l += v(a)
                        }
                    }
                } else if (j === 3 || j === 4) {
                    return a.nodeValue
                }
            } else {
                for (i = 0;
                (b = a[i]); i++) {
                    if (b.nodeType !== 8) {
                        l += v(b)
                    }
                }
            }
            return l
        };
        var _ = s.selectors = {
            order: ["ID", "NAME", "TAG"],
            match: {
                ID: /#((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,
                CLASS: /\.((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,
                NAME: /\[name=['"]*((?:[\w\u00c0-\uFFFF\-]|\\.)+)['"]*\]/,
                ATTR: /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?=)\s*(?:(['"])(.*?)\3|(#?(?:[\w\u00c0-\uFFFF\-]|\\.)*)|)|)\s*\]/,
                TAG: /^((?:[\w\u00c0-\uFFFF\*\-]|\\.)+)/,
                CHILD: /:(only|nth|last|first)-child(?:\(\s*(even|odd|(?:[+\-]?\d+|(?:[+\-]?\d*)?n\s*(?:[+\-]\s*\d+)?))\s*\))?/,
                POS: /:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^\-]|$)/,
                PSEUDO: /:((?:[\w\u00c0-\uFFFF\-]|\\.)+)(?:\((['"]?)((?:\([^\)]+\)|[^\(\)]*)+)\2\))?/
            },
            leftMatch: {},
            attrMap: {
                "class": "className",
                "for": "htmlFor"
            },
            attrHandle: {
                href: function(a) {
                    return a.getAttribute("href")
                },
                type: function(a) {
                    return a.getAttribute("type")
                }
            },
            relative: {
                "+": function(a, b) {
                    var j = typeof b === "string",
                        m = j && !q.test(b),
                        $2 = j && !m;
                    if (m) {
                        b = b.toLowerCase()
                    }
                    for (var i = 0, l = a.length, _2; i < l; i++) {
                        if ((_2 = a[i])) {
                            while ((_2 = _2.previousSibling) && _2.nodeType !== 1) {}
                            a[i] = $2 || _2 && _2.nodeName.toLowerCase() === b ? _2 || false : _2 === b
                        }
                    }
                    if ($2) {
                        s.filter(b, a, true)
                    }
                },
                ">": function(a, b) {
                    var j, m = typeof b === "string",
                        i = 0,
                        l = a.length;
                    if (m && !q.test(b)) {
                        b = b.toLowerCase();
                        for (; i < l; i++) {
                            j = a[i];
                            if (j) {
                                var $2 = j.parentNode;
                                a[i] = $2.nodeName.toLowerCase() === b ? $2 : false
                            }
                        }
                    } else {
                        for (; i < l; i++) {
                            j = a[i];
                            if (j) {
                                a[i] = m ? j.parentNode : j.parentNode === b
                            }
                        }
                        if (m) {
                            s.filter(b, a, true)
                        }
                    }
                },
                "": function(a, b, i) {
                    var j, l = k++,
                        m = Y2;
                    if (typeof b === "string" && !q.test(b)) {
                        b = b.toLowerCase();
                        j = b;
                        m = X2
                    }
                    m("parentNode", b, l, a, j, i)
                },
                "~": function(a, b, i) {
                    var j, l = k++,
                        m = Y2;
                    if (typeof b === "string" && !q.test(b)) {
                        b = b.toLowerCase();
                        j = b;
                        m = X2
                    }
                    m("previousSibling", b, l, a, j, i)
                }
            },
            find: {
                ID: function(a, b, i) {
                    if (typeof b.getElementById !== "undefined" && !i) {
                        var m = b.getElementById(a[1]);
                        return m && m.parentNode ? [m] : []
                    }
                },
                NAME: function(m, a) {
                    if (typeof a.getElementsByName !== "undefined") {
                        var b = [],
                            j = a.getElementsByName(m[1]);
                        for (var i = 0, l = j.length; i < l; i++) {
                            if (j[i].getAttribute("name") === m[1]) {
                                b.push(j[i])
                            }
                        }
                        return b.length === 0 ? null : b
                    }
                },
                TAG: function(m, a) {
                    if (typeof a.getElementsByTagName !== "undefined") {
                        return a.getElementsByTagName(m[1])
                    }
                }
            },
            preFilter: {
                CLASS: function(m, a, b, j, l, $2) {
                    m = " " + m[1].replace(r, "") + " ";
                    if ($2) {
                        return m
                    }
                    for (var i = 0, _2;
                    (_2 = a[i]) != null; i++) {
                        if (_2) {
                            if (l ^ (_2.className && (" " + _2.className + " ").replace(/[\t\n\r]/g, " ").indexOf(m) >= 0)) {
                                if (!b) {
                                    j.push(_2)
                                }
                            } else if (b) {
                                a[i] = false
                            }
                        }
                    }
                    return false
                },
                ID: function(m) {
                    return m[1].replace(r, "")
                },
                TAG: function(m, a) {
                    return m[1].replace(r, "").toLowerCase()
                },
                CHILD: function(m) {
                    if (m[1] === "nth") {
                        if (!m[2]) {
                            s.error(m[0])
                        }
                        m[2] = m[2].replace(/^\+|\s*/g, '');
                        var a = /(-?)(\d*)(?:n([+\-]?\d*))?/.exec(m[2] === "even" && "2n" || m[2] === "odd" && "2n+1" || !/\D/.test(m[2]) && "0n+" + m[2] || m[2]);
                        m[2] = (a[1] + (a[2] || 1)) - 0;
                        m[3] = a[3] - 0
                    } else if (m[2]) {
                        s.error(m[0])
                    }
                    m[0] = k++;
                    return m
                },
                ATTR: function(m, a, i, b, j, l) {
                    var $2 = m[1] = m[1].replace(r, "");
                    if (!l && _.attrMap[$2]) {
                        m[1] = _.attrMap[$2]
                    }
                    m[4] = (m[4] || m[5] || "").replace(r, "");
                    if (m[2] === "~=") {
                        m[4] = " " + m[4] + " "
                    }
                    return m
                },
                PSEUDO: function(m, a, i, b, j) {
                    if (m[1] === "not") {
                        if ((c.exec(m[3]) || "").length > 1 || /^\w/.test(m[3])) {
                            m[3] = s(m[3], null, null, a)
                        } else {
                            var l = s.filter(m[3], a, i, true ^ j);
                            if (!i) {
                                b.push.apply(b, l)
                            }
                            return false
                        }
                    } else if (_.match.POS.test(m[0]) || _.match.CHILD.test(m[0])) {
                        return true
                    }
                    return m
                },
                POS: function(m) {
                    m.unshift(true);
                    return m
                }
            },
            filters: {
                enabled: function(a) {
                    return a.disabled === false && a.type !== "hidden"
                },
                disabled: function(a) {
                    return a.disabled === true
                },
                checked: function(a) {
                    return a.checked === true
                },
                selected: function(a) {
                    if (a.parentNode) {
                        a.parentNode.selectedIndex
                    }
                    return a.selected === true
                },
                parent: function(a) {
                    return !!a.firstChild
                },
                empty: function(a) {
                    return !a.firstChild
                },
                has: function(a, i, m) {
                    return !!s(m[3], a).length
                },
                header: function(a) {
                    return (/h\d/i).test(a.nodeName)
                },
                text: function(a) {
                    var b = a.getAttribute("type"),
                        T2 = a.type;
                    return a.nodeName.toLowerCase() === "input" && "text" === T2 && (b === T2 || b === null)
                },
                radio: function(a) {
                    return a.nodeName.toLowerCase() === "input" && "radio" === a.type
                },
                checkbox: function(a) {
                    return a.nodeName.toLowerCase() === "input" && "checkbox" === a.type
                },
                file: function(a) {
                    return a.nodeName.toLowerCase() === "input" && "file" === a.type
                },
                password: function(a) {
                    return a.nodeName.toLowerCase() === "input" && "password" === a.type
                },
                submit: function(a) {
                    var b = a.nodeName.toLowerCase();
                    return (b === "input" || b === "button") && "submit" === a.type
                },
                image: function(a) {
                    return a.nodeName.toLowerCase() === "input" && "image" === a.type
                },
                reset: function(a) {
                    var b = a.nodeName.toLowerCase();
                    return (b === "input" || b === "button") && "reset" === a.type
                },
                button: function(a) {
                    var b = a.nodeName.toLowerCase();
                    return b === "input" && "button" === a.type || b === "button"
                },
                input: function(a) {
                    return (/input|select|textarea|button/i).test(a.nodeName)
                },
                focus: function(a) {
                    return a === a.ownerDocument.activeElement
                }
            },
            setFilters: {
                first: function(a, i) {
                    return i === 0
                },
                last: function(a, i, m, b) {
                    return i === b.length - 1
                },
                even: function(a, i) {
                    return i % 2 === 0
                },
                odd: function(a, i) {
                    return i % 2 === 1
                },
                lt: function(a, i, m) {
                    return i < m[3] - 0
                },
                gt: function(a, i, m) {
                    return i > m[3] - 0
                },
                nth: function(a, i, m) {
                    return m[3] - 0 === i
                },
                eq: function(a, i, m) {
                    return m[3] - 0 === i
                }
            },
            filter: {
                PSEUDO: function(a, m, i, b) {
                    var $2 = m[1],
                        _2 = _.filters[$2];
                    if (_2) {
                        return _2(a, i, m, b)
                    } else if ($2 === "contains") {
                        return (a.textContent || a.innerText || v([a]) || "").indexOf(m[3]) >= 0
                    } else if ($2 === "not") {
                        var a3 = m[3];
                        for (var j = 0, l = a3.length; j < l; j++) {
                            if (a3[j] === a) {
                                return false
                            }
                        }
                        return true
                    } else {
                        s.error($2)
                    }
                },
                CHILD: function(a, m) {
                    var b, l, i, j, $2, _2, a3, T2 = m[1],
                        b3 = a;
                    switch (T2) {
                        case "only":
                        case "first":
                            while ((b3 = b3.previousSibling)) {
                                if (b3.nodeType === 1) {
                                    return false
                                }
                            }
                            if (T2 === "first") {
                                return true
                            }
                            b3 = a;
                        case "last":
                            while ((b3 = b3.nextSibling)) {
                                if (b3.nodeType === 1) {
                                    return false
                                }
                            }
                            return true;
                        case "nth":
                            b = m[2];
                            l = m[3];
                            if (b === 1 && l === 0) {
                                return true
                            }
                            i = m[0];
                            j = a.parentNode;
                            if (j && (j[f] !== i || !a.nodeIndex)) {
                                _2 = 0;
                                for (b3 = j.firstChild; b3; b3 = b3.nextSibling) {
                                    if (b3.nodeType === 1) {
                                        b3.nodeIndex = ++_2
                                    }
                                }
                                j[f] = i
                            }
                            a3 = a.nodeIndex - l;
                            if (b === 0) {
                                return a3 === 0
                            } else {
                                return (a3 % b === 0 && a3 / b >= 0)
                            }
                    }
                },
                ID: function(a, m) {
                    return a.nodeType === 1 && a.getAttribute("id") === m
                },
                TAG: function(a, m) {
                    return (m === "*" && a.nodeType === 1) || !! a.nodeName && a.nodeName.toLowerCase() === m
                },
                CLASS: function(a, m) {
                    return (" " + (a.className || a.getAttribute("class")) + " ").indexOf(m) > -1
                },
                ATTR: function(a, m) {
                    var b = m[1],
                        i = s.attr ? s.attr(a, b) : _.attrHandle[b] ? _.attrHandle[b](a) : a[b] != null ? a[b] : a.getAttribute(b),
                        j = i + "",
                        T2 = m[2],
                        l = m[4];
                    return i == null ? T2 === "!=" : !T2 && s.attr ? i != null : T2 === "=" ? j === l : T2 === "*=" ? j.indexOf(l) >= 0 : T2 === "~=" ? (" " + j + " ").indexOf(l) >= 0 : !l ? j && i !== false : T2 === "!=" ? j !== l : T2 === "^=" ? j.indexOf(l) === 0 : T2 === "$=" ? j.substr(j.length - l.length) === l : T2 === "|=" ? j === l || j.substr(0, l.length + 1) === l + "-" : false
                },
                POS: function(a, m, i, b) {
                    var j = m[2],
                        l = _.setFilters[j];
                    if (l) {
                        return l(a, i, m, b)
                    }
                }
            }
        };
        var R2 = _.match.POS,
            S2 = function(a, b) {
                return "\\" + (b - 0 + 1)
            };
        for (var T2 in _.match) {
            _.match[T2] = new RegExp(_.match[T2].source + (/(?![^\[]*\])(?![^\(]*\))/.source));
            _.leftMatch[T2] = new RegExp(/(^(?:.|\r|\n)*?)/.source + _.match[T2].source.replace(/\\(\d+)/g, S2))
        }
        var U2 = function(a, b) {
            a = Array.prototype.slice.call(a, 0);
            if (b) {
                b.push.apply(b, a);
                return b
            }
            return a
        };
        try {
            Array.prototype.slice.call(d.documentElement.childNodes, 0)[0].nodeType
        } catch (e) {
            U2 = function(a, b) {
                var i = 0,
                    j = b || [];
                if (t.call(a) === "[object Array]") {
                    Array.prototype.push.apply(j, a)
                } else {
                    if (typeof a.length === "number") {
                        for (var l = a.length; i < l; i++) {
                            j.push(a[i])
                        }
                    } else {
                        for (; a[i]; i++) {
                            j.push(a[i])
                        }
                    }
                }
                return j
            }
        }
        var V2, W2;
        if (d.documentElement.compareDocumentPosition) {
            V2 = function(a, b) {
                if (a === b) {
                    n = true;
                    return 0
                }
                if (!a.compareDocumentPosition || !b.compareDocumentPosition) {
                    return a.compareDocumentPosition ? -1 : 1
                }
                return a.compareDocumentPosition(b) & 4 ? -1 : 1
            }
        } else {
            V2 = function(a, b) {
                if (a === b) {
                    n = true;
                    return 0
                } else if (a.sourceIndex && b.sourceIndex) {
                    return a.sourceIndex - b.sourceIndex
                }
                var j, l, m = [],
                    bp = [],
                    $2 = a.parentNode,
                    _2 = b.parentNode,
                    a3 = $2;
                if ($2 === _2) {
                    return W2(a, b)
                } else if (!$2) {
                    return -1
                } else if (!_2) {
                    return 1
                }
                while (a3) {
                    m.unshift(a3);
                    a3 = a3.parentNode
                }
                a3 = _2;
                while (a3) {
                    bp.unshift(a3);
                    a3 = a3.parentNode
                }
                j = m.length;
                l = bp.length;
                for (var i = 0; i < j && i < l; i++) {
                    if (m[i] !== bp[i]) {
                        return W2(m[i], bp[i])
                    }
                }
                return i === j ? W2(a, bp[i], -1) : W2(m[i], b, 1)
            };
            W2 = function(a, b, i) {
                if (a === b) {
                    return i
                }
                var j = a.nextSibling;
                while (j) {
                    if (j === b) {
                        return -1
                    }
                    j = j.nextSibling
                }
                return 1
            }
        }(function() {
            var a = d.createElement("div"),
                i = "script" + (new Date()).getTime(),
                B1 = d.documentElement;
            a.innerHTML = "<a name='" + i + "'/>";
            B1.insertBefore(a, B1.firstChild);
            if (d.getElementById(i)) {
                _.find.ID = function(b, j, l) {
                    if (typeof j.getElementById !== "undefined" && !l) {
                        var m = j.getElementById(b[1]);
                        return m ? m.id === b[1] || typeof m.getAttributeNode !== "undefined" && m.getAttributeNode("id").nodeValue === b[1] ? [m] : u : []
                    }
                };
                _.filter.ID = function(b, m) {
                    var j = typeof b.getAttributeNode !== "undefined" && b.getAttributeNode("id");
                    return b.nodeType === 1 && j && j.nodeValue === m
                }
            }
            B1.removeChild(a);
            B1 = a = null
        })();
        (function() {
            var a = d.createElement("div");
            a.appendChild(d.createComment(""));
            if (a.getElementsByTagName("*").length > 0) {
                _.find.TAG = function(m, b) {
                    var j = b.getElementsByTagName(m[1]);
                    if (m[1] === "*") {
                        var l = [];
                        for (var i = 0; j[i]; i++) {
                            if (j[i].nodeType === 1) {
                                l.push(j[i])
                            }
                        }
                        j = l
                    }
                    return j
                }
            }
            a.innerHTML = "<a href='#'></a>";
            if (a.firstChild && typeof a.firstChild.getAttribute !== "undefined" && a.firstChild.getAttribute("href") !== "#") {
                _.attrHandle.href = function(b) {
                    return b.getAttribute("href", 2)
                }
            }
            a = null
        })();
        if (d.querySelectorAll) {
            (function() {
                var a = s,
                    b = d.createElement("div"),
                    i = "__sizzle__";
                b.innerHTML = "<p class='TEST'></p>";
                if (b.querySelectorAll && b.querySelectorAll(".TEST").length === 0) {
                    return
                }
                s = function(l, m, $2, _2) {
                    m = m || d;
                    if (!_2 && !s.isXML(m)) {
                        var a3 = /^(\w+$)|^\.([\w\-]+$)|^#([\w\-]+$)/.exec(l);
                        if (a3 && (m.nodeType === 1 || m.nodeType === 9)) {
                            if (a3[1]) {
                                return U2(m.getElementsByTagName(l), $2)
                            } else if (a3[2] && _.find.CLASS && m.getElementsByClassName) {
                                return U2(m.getElementsByClassName(a3[2]), $2)
                            }
                        }
                        if (m.nodeType === 9) {
                            if (l === "body" && m.body) {
                                return U2([m.body], $2)
                            } else if (a3 && a3[3]) {
                                var b3 = m.getElementById(a3[3]);
                                if (b3 && b3.parentNode) {
                                    if (b3.id === a3[3]) {
                                        return U2([b3], $2)
                                    }
                                } else {
                                    return U2([], $2)
                                }
                            }
                            try {
                                return U2(m.querySelectorAll(l), $2)
                            } catch (c3) {}
                        } else if (m.nodeType === 1 && m.nodeName.toLowerCase() !== "object") {
                            var d3 = m,
                                e3 = m.getAttribute("id"),
                                f3 = e3 || i,
                                g3 = m.parentNode,
                                h3 = /^\s*[+~]/.test(l);
                            if (!e3) {
                                m.setAttribute("id", f3)
                            } else {
                                f3 = f3.replace(/'/g, "\\$&")
                            }
                            if (h3 && g3) {
                                m = m.parentNode
                            }
                            try {
                                if (!h3 || g3) {
                                    return U2(m.querySelectorAll("[id='" + f3 + "'] " + l), $2)
                                }
                            } catch (i3) {} finally {
                                if (!e3) {
                                    d3.removeAttribute("id")
                                }
                            }
                        }
                    }
                    return a(l, m, $2, _2)
                };
                for (var j in a) {
                    s[j] = a[j]
                }
                b = null
            })()
        }(function() {
            var a = d.documentElement,
                m = a.matchesSelector || a.mozMatchesSelector || a.webkitMatchesSelector || a.msMatchesSelector;
            if (m) {
                var b = !m.call(d.createElement("div"), "div"),
                    i = false;
                try {
                    m.call(d.documentElement, "[test!='']:sizzle")
                } catch (j) {
                    i = true
                }
                s.matchesSelector = function(l, $2) {
                    $2 = $2.replace(/\=\s*([^'"\]]*)\s*\]/g, "='$1']");
                    if (!s.isXML(l)) {
                        try {
                            if (i || !_.match.PSEUDO.test($2) && !/!=/.test($2)) {
                                var _2 = m.call(l, $2);
                                if (_2 || !b || l.document && l.document.nodeType !== 11) {
                                    return _2
                                }
                            }
                        } catch (e) {}
                    }
                    return s($2, null, null, [l]).length > 0
                }
            }
        })();
        (function() {
            var a = d.createElement("div");
            a.innerHTML = "<div class='test e'></div><div class='test'></div>";
            if (!a.getElementsByClassName || a.getElementsByClassName("e").length === 0) {
                return
            }
            a.lastChild.className = "e";
            if (a.getElementsByClassName("e").length === 1) {
                return
            }
            _.order.splice(1, 0, "CLASS");
            _.find.CLASS = function(m, b, i) {
                if (typeof b.getElementsByClassName !== "undefined" && !i) {
                    return b.getElementsByClassName(m[1])
                }
            };
            a = null
        })();

        function X2(a, b, j, m, $2, _2) {
            for (var i = 0, l = m.length; i < l; i++) {
                var a3 = m[i];
                if (a3) {
                    var b3 = false;
                    a3 = a3[a];
                    while (a3) {
                        if (a3[f] === j) {
                            b3 = m[a3.sizset];
                            break
                        }
                        if (a3.nodeType === 1 && !_2) {
                            a3[f] = j;
                            a3.sizset = i
                        }
                        if (a3.nodeName.toLowerCase() === b) {
                            b3 = a3;
                            break
                        }
                        a3 = a3[a]
                    }
                    m[i] = b3
                }
            }
        }
        function Y2(a, b, j, m, $2, _2) {
            for (var i = 0, l = m.length; i < l; i++) {
                var a3 = m[i];
                if (a3) {
                    var b3 = false;
                    a3 = a3[a];
                    while (a3) {
                        if (a3[f] === j) {
                            b3 = m[a3.sizset];
                            break
                        }
                        if (a3.nodeType === 1) {
                            if (!_2) {
                                a3[f] = j;
                                a3.sizset = i
                            }
                            if (typeof b !== "string") {
                                if (a3 === b) {
                                    b3 = true;
                                    break
                                }
                            } else if (s.filter(b, [a3]).length > 0) {
                                b3 = a3;
                                break
                            }
                        }
                        a3 = a3[a]
                    }
                    m[i] = b3
                }
            }
        }
        if (d.documentElement.contains) {
            s.contains = function(a, b) {
                return a !== b && (a.contains ? a.contains(b) : true)
            }
        } else if (d.documentElement.compareDocumentPosition) {
            s.contains = function(a, b) {
                return !!(a.compareDocumentPosition(b) & 16)
            }
        } else {
            s.contains = function() {
                return false
            }
        }
        s.isXML = function(a) {
            var b = (a ? a.ownerDocument || a : 0).documentElement;
            return b ? b.nodeName !== "HTML" : false
        };
        var Z2 = function(a, b, j) {
            var m, $2 = [],
                _2 = "",
                B1 = b.nodeType ? [b] : b;
            while ((m = _.match.PSEUDO.exec(a))) {
                _2 += m[0];
                a = a.replace(_.match.PSEUDO, "")
            }
            a = _.relative[a] ? a + "*" : a;
            for (var i = 0, l = B1.length; i < l; i++) {
                s(a, B1[i], $2, j)
            }
            return s.filter(_2, $2)
        };
        s.attr = Q.attr;
        s.selectors.attrMap = {};
        Q.find = s;
        Q.expr = s.selectors;
        Q.expr[":"] = Q.expr.filters;
        Q.unique = s.uniqueSort;
        Q.text = s.getText;
        Q.isXMLDoc = s.isXML;
        Q.contains = s.contains
    })();
    var c1 = /Until$/,
        d1 = /^(?:parents|prevUntil|prevAll)/,
        e1 = /,/,
        f1 = /^.[^:#\[\.,]*$/,
        g1 = Array.prototype.slice,
        h1 = Q.expr.match.POS,
        i1 = {
            children: true,
            contents: true,
            next: true,
            prev: true
        };
    Q.fn.extend({
        find: function(s) {
            var a = this,
                i, l;
            if (typeof s !== "string") {
                return Q(s).filter(function() {
                    for (i = 0, l = a.length; i < l; i++) {
                        if (Q.contains(a[i], this)) {
                            return true
                        }
                    }
                })
            }
            var b = this.pushStack("", "find", s),
                c, n, r;
            for (i = 0, l = this.length; i < l; i++) {
                c = b.length;
                Q.find(s, this[i], b);
                if (i > 0) {
                    for (n = c; n < b.length; n++) {
                        for (r = 0; r < c; r++) {
                            if (b[r] === b[n]) {
                                b.splice(n--, 1);
                                break
                            }
                        }
                    }
                }
            }
            return b
        },
        has: function(t) {
            var a = Q(t);
            return this.filter(function() {
                for (var i = 0, l = a.length; i < l; i++) {
                    if (Q.contains(this, a[i])) {
                        return true
                    }
                }
            })
        },
        not: function(s) {
            return this.pushStack(k1(this, s, false), "not", s)
        },
        filter: function(s) {
            return this.pushStack(k1(this, s, true), "filter", s)
        },
        is: function(s) {
            return !!s && (typeof s === "string" ? h1.test(s) ? Q(s, this.context).index(this[0]) >= 0 : Q.filter(s, this).length > 0 : this.filter(s).length > 0)
        },
        closest: function(s, c) {
            var r = [],
                i, l, a = this[0];
            if (Q.isArray(s)) {
                var b = 1;
                while (a && a.ownerDocument && a !== c) {
                    for (i = 0; i < s.length; i++) {
                        if (Q(a).is(s[i])) {
                            r.push({
                                selector: s[i],
                                elem: a,
                                level: b
                            })
                        }
                    }
                    a = a.parentNode;
                    b++
                }
                return r
            }
            var p = h1.test(s) || typeof s !== "string" ? Q(s, c || this.context) : 0;
            for (i = 0, l = this.length; i < l; i++) {
                a = this[i];
                while (a) {
                    if (p ? p.index(a) > -1 : Q.find.matchesSelector(a, s)) {
                        r.push(a);
                        break
                    } else {
                        a = a.parentNode;
                        if (!a || !a.ownerDocument || a === c || a.nodeType === 11) {
                            break
                        }
                    }
                }
            }
            r = r.length > 1 ? Q.unique(r) : r;
            return this.pushStack(r, "closest", s)
        },
        index: function(a) {
            if (!a) {
                return (this[0] && this[0].parentNode) ? this.prevAll().length : -1
            }
            if (typeof a === "string") {
                return Q.inArray(this[0], Q(a))
            }
            return Q.inArray(a.jquery ? a[0] : a, this)
        },
        add: function(s, c) {
            var a = typeof s === "string" ? Q(s, c) : Q.makeArray(s && s.nodeType ? [s] : s),
                b = Q.merge(this.get(), a);
            return this.pushStack(j1(a[0]) || j1(b[0]) ? b : Q.unique(b))
        },
        andSelf: function() {
            return this.add(this.prevObject)
        }
    });

    function j1(n) {
        return !n || !n.parentNode || n.parentNode.nodeType === 11
    }
    Q.each({
        parent: function(a) {
            var p = a.parentNode;
            return p && p.nodeType !== 11 ? p : null
        },
        parents: function(a) {
            return Q.dir(a, "parentNode")
        },
        parentsUntil: function(a, i, b) {
            return Q.dir(a, "parentNode", b)
        },
        next: function(a) {
            return Q.nth(a, 2, "nextSibling")
        },
        prev: function(a) {
            return Q.nth(a, 2, "previousSibling")
        },
        nextAll: function(a) {
            return Q.dir(a, "nextSibling")
        },
        prevAll: function(a) {
            return Q.dir(a, "previousSibling")
        },
        nextUntil: function(a, i, b) {
            return Q.dir(a, "nextSibling", b)
        },
        prevUntil: function(a, i, b) {
            return Q.dir(a, "previousSibling", b)
        },
        siblings: function(a) {
            return Q.sibling(a.parentNode.firstChild, a)
        },
        children: function(a) {
            return Q.sibling(a.firstChild)
        },
        contents: function(a) {
            return Q.nodeName(a, "iframe") ? a.contentDocument || a.contentWindow.document : Q.makeArray(a.childNodes)
        }
    }, function(n, f) {
        Q.fn[n] = function(a, s) {
            var r = Q.map(this, f, a);
            if (!c1.test(n)) {
                s = a
            }
            if (s && typeof s === "string") {
                r = Q.filter(s, r)
            }
            r = this.length > 1 && !i1[n] ? Q.unique(r) : r;
            if ((this.length > 1 || e1.test(s)) && d1.test(n)) {
                r = r.reverse()
            }
            return this.pushStack(r, n, g1.call(arguments).join(","))
        }
    });
    Q.extend({
        filter: function(a, b, n) {
            if (n) {
                a = ":not(" + a + ")"
            }
            return b.length === 1 ? Q.find.matchesSelector(b[0], a) ? [b[0]] : [] : Q.find.matches(a, b)
        },
        dir: function(a, b, c) {
            var m = [],
                f = a[b];
            while (f && f.nodeType !== 9 && (c === u || f.nodeType !== 1 || !Q(f).is(c))) {
                if (f.nodeType === 1) {
                    m.push(f)
                }
                f = f[b]
            }
            return m
        },
        nth: function(c, r, a, b) {
            r = r || 1;
            var n = 0;
            for (; c; c = c[a]) {
                if (c.nodeType === 1 && ++n === r) {
                    break
                }
            }
            return c
        },
        sibling: function(n, a) {
            var r = [];
            for (; n; n = n.nextSibling) {
                if (n.nodeType === 1 && n !== a) {
                    r.push(n)
                }
            }
            return r
        }
    });

    function k1(a, q, k) {
        q = q || 0;
        if (Q.isFunction(q)) {
            return Q.grep(a, function(b, i) {
                var r = !! q.call(b, i, b);
                return r === k
            })
        } else if (q.nodeType) {
            return Q.grep(a, function(b, i) {
                return (b === q) === k
            })
        } else if (typeof q === "string") {
            var f = Q.grep(a, function(b) {
                return b.nodeType === 1
            });
            if (f1.test(q)) {
                return Q.filter(q, f, !k)
            } else {
                q = Q.filter(q, f)
            }
        }
        return Q.grep(a, function(b, i) {
            return (Q.inArray(b, q) >= 0) === k
        })
    }
    function l1(d) {
        var l = m1.split("|"),
            s = d.createDocumentFragment();
        if (s.createElement) {
            while (l.length) {
                s.createElement(l.pop())
            }
        }
        return s
    }
    var m1 = "abbr|article|aside|audio|canvas|datalist|details|figcaption|figure|footer|" + "header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",
        n1 = / jQuery\d+="(?:\d+|null)"/g,
        o1 = /^\s+/,
        p1 = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/ig,
        q1 = /<([\w:]+)/,
        r1 = /<tbody/i,
        s1 = /<|&#?\w+;/,
        t1 = /<(?:script|style)/i,
        u1 = /<(?:script|object|embed|option|style)/i,
        v1 = new RegExp("<(?:" + m1 + ")", "i"),
        w1 = /checked\s*(?:[^=]|=\s*.checked.)/i,
        x1 = /\/(java|ecma)script/i,
        y1 = /^\s*<!(?:\[CDATA\[|\-\-)/,
        z1 = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            legend: [1, "<fieldset>", "</fieldset>"],
            thead: [1, "<table>", "</table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
            area: [1, "<map>", "</map>"],
            _default: [0, "", ""]
        }, A1 = l1(d);
    z1.optgroup = z1.option;
    z1.tbody = z1.tfoot = z1.colgroup = z1.caption = z1.thead;
    z1.th = z1.td;
    if (!Q.support.htmlSerialize) {
        z1._default = [1, "div<div>", "</div>"]
    }
    Q.fn.extend({
        text: function(t) {
            if (Q.isFunction(t)) {
                return this.each(function(i) {
                    var s = Q(this);
                    s.text(t.call(this, i, s.text()))
                })
            }
            if (typeof t !== "object" && t !== u) {
                return this.empty().append((this[0] && this[0].ownerDocument || d).createTextNode(t))
            }
            return Q.text(this)
        },
        wrapAll: function(a) {
            if (Q.isFunction(a)) {
                return this.each(function(i) {
                    Q(this).wrapAll(a.call(this, i))
                })
            }
            if (this[0]) {
                var b = Q(a, this[0].ownerDocument).eq(0).clone(true);
                if (this[0].parentNode) {
                    b.insertBefore(this[0])
                }
                b.map(function() {
                    var c = this;
                    while (c.firstChild && c.firstChild.nodeType === 1) {
                        c = c.firstChild
                    }
                    return c
                }).append(this)
            }
            return this
        },
        wrapInner: function(a) {
            if (Q.isFunction(a)) {
                return this.each(function(i) {
                    Q(this).wrapInner(a.call(this, i))
                })
            }
            return this.each(function() {
                var s = Q(this),
                    c = s.contents();
                if (c.length) {
                    c.wrapAll(a)
                } else {
                    s.append(a)
                }
            })
        },
        wrap: function(a) {
            var b = Q.isFunction(a);
            return this.each(function(i) {
                Q(this).wrapAll(b ? a.call(this, i) : a)
            })
        },
        unwrap: function() {
            return this.parent().each(function() {
                if (!Q.nodeName(this, "body")) {
                    Q(this).replaceWith(this.childNodes)
                }
            }).end()
        },
        append: function() {
            return this.domManip(arguments, true, function(a) {
                if (this.nodeType === 1) {
                    this.appendChild(a)
                }
            })
        },
        prepend: function() {
            return this.domManip(arguments, true, function(a) {
                if (this.nodeType === 1) {
                    this.insertBefore(a, this.firstChild)
                }
            })
        },
        before: function() {
            if (this[0] && this[0].parentNode) {
                return this.domManip(arguments, false, function(a) {
                    this.parentNode.insertBefore(a, this)
                })
            } else if (arguments.length) {
                var s = Q.clean(arguments);
                s.push.apply(s, this.toArray());
                return this.pushStack(s, "before", arguments)
            }
        },
        after: function() {
            if (this[0] && this[0].parentNode) {
                return this.domManip(arguments, false, function(a) {
                    this.parentNode.insertBefore(a, this.nextSibling)
                })
            } else if (arguments.length) {
                var s = this.pushStack(this, "after", arguments);
                s.push.apply(s, Q.clean(arguments));
                return s
            }
        },
        remove: function(s, k) {
            for (var i = 0, a;
            (a = this[i]) != null; i++) {
                if (!s || Q.filter(s, [a]).length) {
                    if (!k && a.nodeType === 1) {
                        Q.cleanData(a.getElementsByTagName("*"));
                        Q.cleanData([a])
                    }
                    if (a.parentNode) {
                        a.parentNode.removeChild(a)
                    }
                }
            }
            return this
        },
        empty: function() {
            for (var i = 0, a;
            (a = this[i]) != null; i++) {
                if (a.nodeType === 1) {
                    Q.cleanData(a.getElementsByTagName("*"))
                }
                while (a.firstChild) {
                    a.removeChild(a.firstChild)
                }
            }
            return this
        },
        clone: function(a, b) {
            a = a == null ? false : a;
            b = b == null ? a : b;
            return this.map(function() {
                return Q.clone(this, a, b)
            })
        },
        html: function(v) {
            if (v === u) {
                return this[0] && this[0].nodeType === 1 ? this[0].innerHTML.replace(n1, "") : null
            } else if (typeof v === "string" && !t1.test(v) && (Q.support.leadingWhitespace || !o1.test(v)) && !z1[(q1.exec(v) || ["", ""])[1].toLowerCase()]) {
                v = v.replace(p1, "<$1></$2>");
                try {
                    for (var i = 0, l = this.length; i < l; i++) {
                        if (this[i].nodeType === 1) {
                            Q.cleanData(this[i].getElementsByTagName("*"));
                            this[i].innerHTML = v
                        }
                    }
                } catch (e) {
                    this.empty().append(v)
                }
            } else if (Q.isFunction(v)) {
                this.each(function(i) {
                    var s = Q(this);
                    s.html(v.call(this, i, s.html()))
                })
            } else {
                this.empty().append(v)
            }
            return this
        },
        replaceWith: function(v) {
            if (this[0] && this[0].parentNode) {
                if (Q.isFunction(v)) {
                    return this.each(function(i) {
                        var s = Q(this),
                            o = s.html();
                        s.replaceWith(v.call(this, i, o))
                    })
                }
                if (typeof v !== "string") {
                    v = Q(v).detach()
                }
                return this.each(function() {
                    var n = this.nextSibling,
                        p = this.parentNode;
                    Q(this).remove();
                    if (n) {
                        Q(n).before(v)
                    } else {
                        Q(p).append(v)
                    }
                })
            } else {
                return this.length ? this.pushStack(Q(Q.isFunction(v) ? v() : v), "replaceWith", v) : this
            }
        },
        detach: function(s) {
            return this.remove(s, true)
        },
        domManip: function(a, t, c) {
            var r, f, b, p, v = a[0],
                s = [];
            if (!Q.support.checkClone && arguments.length === 3 && typeof v === "string" && w1.test(v)) {
                return this.each(function() {
                    Q(this).domManip(a, t, c, true)
                })
            }
            if (Q.isFunction(v)) {
                return this.each(function(i) {
                    var k = Q(this);
                    a[0] = v.call(this, i, t ? k.html() : u);
                    k.domManip(a, t, c)
                })
            }
            if (this[0]) {
                p = v && v.parentNode;
                if (Q.support.parentNode && p && p.nodeType === 11 && p.childNodes.length === this.length) {
                    r = {
                        fragment: p
                    }
                } else {
                    r = Q.buildFragment(a, this, s)
                }
                b = r.fragment;
                if (b.childNodes.length === 1) {
                    f = b = b.firstChild
                } else {
                    f = b.firstChild
                }
                if (f) {
                    t = t && Q.nodeName(f, "tr");
                    for (var i = 0, l = this.length, j = l - 1; i < l; i++) {
                        c.call(t ? B1(this[i], f) : this[i], r.cacheable || (l > 1 && i < j) ? Q.clone(b, true, true) : b)
                    }
                }
                if (s.length) {
                    Q.each(s, I1)
                }
            }
            return this
        }
    });

    function B1(a, c) {
        return Q.nodeName(a, "table") ? (a.getElementsByTagName("tbody")[0] || a.appendChild(a.ownerDocument.createElement("tbody"))) : a
    }
    function C1(s, a) {
        if (a.nodeType !== 1 || !Q.hasData(s)) {
            return
        }
        var t, i, l, o = Q._data(s),
            c = Q._data(a, o),
            b = o.events;
        if (b) {
            delete c.handle;
            c.events = {};
            for (t in b) {
                for (i = 0, l = b[t].length; i < l; i++) {
                    Q.event.add(a, t + (b[t][i].namespace ? "." : "") + b[t][i].namespace, b[t][i], b[t][i].data)
                }
            }
        }
        if (c.data) {
            c.data = Q.extend({}, c.data)
        }
    }
    function D1(s, a) {
        var n;
        if (a.nodeType !== 1) {
            return
        }
        if (a.clearAttributes) {
            a.clearAttributes()
        }
        if (a.mergeAttributes) {
            a.mergeAttributes(s)
        }
        n = a.nodeName.toLowerCase();
        if (n === "object") {
            a.outerHTML = s.outerHTML
        } else if (n === "input" && (s.type === "checkbox" || s.type === "radio")) {
            if (s.checked) {
                a.defaultChecked = a.checked = s.checked
            }
            if (a.value !== s.value) {
                a.value = s.value
            }
        } else if (n === "option") {
            a.selected = s.defaultSelected
        } else if (n === "input" || n === "textarea") {
            a.defaultValue = s.defaultValue
        }
        a.removeAttribute(Q.expando)
    }
    Q.buildFragment = function(a, n, s) {
        var f, c, b, i, j = a[0];
        if (n && n[0]) {
            i = n[0].ownerDocument || n[0]
        }
        if (!i.createDocumentFragment) {
            i = d
        }
        if (a.length === 1 && typeof j === "string" && j.length < 512 && i === d && j.charAt(0) === "<" && !u1.test(j) && (Q.support.checkClone || !w1.test(j)) && (Q.support.html5Clone || !v1.test(j))) {
            c = true;
            b = Q.fragments[j];
            if (b && b !== 1) {
                f = b
            }
        }
        if (!f) {
            f = i.createDocumentFragment();
            Q.clean(a, i, f, s)
        }
        if (c) {
            Q.fragments[j] = b ? f : 1
        }
        return {
            fragment: f,
            cacheable: c
        }
    };
    Q.fragments = {};
    Q.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function(n, o) {
        Q.fn[n] = function(s) {
            var r = [],
                a = Q(s),
                p = this.length === 1 && this[0].parentNode;
            if (p && p.nodeType === 11 && p.childNodes.length === 1 && a.length === 1) {
                a[o](this[0]);
                return this
            } else {
                for (var i = 0, l = a.length; i < l; i++) {
                    var b = (i > 0 ? this.clone(true) : this).get();
                    Q(a[i])[o](b);
                    r = r.concat(b)
                }
                return this.pushStack(r, n, a.selector)
            }
        }
    });

    function E1(a) {
        if (typeof a.getElementsByTagName !== "undefined") {
            return a.getElementsByTagName("*")
        } else if (typeof a.querySelectorAll !== "undefined") {
            return a.querySelectorAll("*")
        } else {
            return []
        }
    }
    function F1(a) {
        if (a.type === "checkbox" || a.type === "radio") {
            a.defaultChecked = a.checked
        }
    }
    function G1(a) {
        var n = (a.nodeName || "").toLowerCase();
        if (n === "input") {
            F1(a)
        } else if (n !== "script" && typeof a.getElementsByTagName !== "undefined") {
            Q.grep(a.getElementsByTagName("input"), F1)
        }
    }
    function H1(a) {
        var b = d.createElement("div");
        A1.appendChild(b);
        b.innerHTML = a.outerHTML;
        return b.firstChild
    }
    Q.extend({
        clone: function(a, b, c) {
            var s, f, i, j = Q.support.html5Clone || !v1.test("<" + a.nodeName) ? a.cloneNode(true) : H1(a);
            if ((!Q.support.noCloneEvent || !Q.support.noCloneChecked) && (a.nodeType === 1 || a.nodeType === 11) && !Q.isXMLDoc(a)) {
                D1(a, j);
                s = E1(a);
                f = E1(j);
                for (i = 0; s[i]; ++i) {
                    if (f[i]) {
                        D1(s[i], f[i])
                    }
                }
            }
            if (b) {
                C1(a, j);
                if (c) {
                    s = E1(a);
                    f = E1(j);
                    for (i = 0; s[i]; ++i) {
                        C1(s[i], f[i])
                    }
                }
            }
            s = f = null;
            return j
        },
        clean: function(a, c, f, s) {
            var b;
            c = c || d;
            if (typeof c.createElement === "undefined") {
                c = c.ownerDocument || c[0] && c[0].ownerDocument || d
            }
            var r = [],
                j;
            for (var i = 0, k;
            (k = a[i]) != null; i++) {
                if (typeof k === "number") {
                    k += ""
                }
                if (!k) {
                    continue
                }
                if (typeof k === "string") {
                    if (!s1.test(k)) {
                        k = c.createTextNode(k)
                    } else {
                        k = k.replace(p1, "<$1></$2>");
                        var t = (q1.exec(k) || ["", ""])[1].toLowerCase(),
                            l = z1[t] || z1._default,
                            m = l[0],
                            n = c.createElement("div");
                        if (c === d) {
                            A1.appendChild(n)
                        } else {
                            l1(c).appendChild(n)
                        }
                        n.innerHTML = l[1] + k + l[2];
                        while (m--) {
                            n = n.lastChild
                        }
                        if (!Q.support.tbody) {
                            var o = r1.test(k),
                                p = t === "table" && !o ? n.firstChild && n.firstChild.childNodes : l[1] === "<table>" && !o ? n.childNodes : [];
                            for (j = p.length - 1; j >= 0; --j) {
                                if (Q.nodeName(p[j], "tbody") && !p[j].childNodes.length) {
                                    p[j].parentNode.removeChild(p[j])
                                }
                            }
                        }
                        if (!Q.support.leadingWhitespace && o1.test(k)) {
                            n.insertBefore(c.createTextNode(o1.exec(k)[0]), n.firstChild)
                        }
                        k = n.childNodes
                    }
                }
                var q;
                if (!Q.support.appendChecked) {
                    if (k[0] && typeof(q = k.length) === "number") {
                        for (j = 0; j < q; j++) {
                            G1(k[j])
                        }
                    } else {
                        G1(k)
                    }
                }
                if (k.nodeType) {
                    r.push(k)
                } else {
                    r = Q.merge(r, k)
                }
            }
            if (f) {
                b = function(k) {
                    return !k.type || x1.test(k.type)
                };
                for (i = 0; r[i]; i++) {
                    if (s && Q.nodeName(r[i], "script") && (!r[i].type || r[i].type.toLowerCase() === "text/javascript")) {
                        s.push(r[i].parentNode ? r[i].parentNode.removeChild(r[i]) : r[i])
                    } else {
                        if (r[i].nodeType === 1) {
                            var v = Q.grep(r[i].getElementsByTagName("script"), b);
                            r.splice.apply(r, [i + 1, 0].concat(v))
                        }
                        f.appendChild(r[i])
                    }
                }
            }
            return r
        },
        cleanData: function(a) {
            var b, c, f = Q.cache,
                s = Q.event.special,
                j = Q.support.deleteExpando;
            for (var i = 0, k;
            (k = a[i]) != null; i++) {
                if (k.nodeName && Q.noData[k.nodeName.toLowerCase()]) {
                    continue
                }
                c = k[Q.expando];
                if (c) {
                    b = f[c];
                    if (b && b.events) {
                        for (var t in b.events) {
                            if (s[t]) {
                                Q.event.remove(k, t)
                            } else {
                                Q.removeEvent(k, t, b.handle)
                            }
                        }
                        if (b.handle) {
                            b.handle.elem = null
                        }
                    }
                    if (j) {
                        delete k[Q.expando]
                    } else if (k.removeAttribute) {
                        k.removeAttribute(Q.expando)
                    }
                    delete f[c]
                }
            }
        }
    });

    function I1(i, a) {
        if (a.src) {
            Q.ajax({
                url: a.src,
                async :false,
                dataType: "script"
            })
        } else {
            Q.globalEval((a.text || a.textContent || a.innerHTML || "").replace(y1, "/*$0*/"))
        }
        if (a.parentNode) {
            a.parentNode.removeChild(a)
        }
    }
    var J1 = /alpha\([^)]*\)/i,
        K1 = /opacity=([^)]*)/,
        L1 = /([A-Z]|^ms)/g,
        M1 = /^-?\d+(?:px)?$/i,
        N1 = /^-?\d/,
        O1 = /^([\-+])=([\-+.\de]+)/,
        P1 = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        }, Q1 = ["Left", "Right"],
        R1 = ["Top", "Bottom"],
        S1, T1, U1;
    Q.fn.css = function(n, v) {
        if (arguments.length === 2 && v === u) {
            return this
        }
        return Q.access(this, n, v, true, function(a, n, v) {
            return v !== u ? Q.style(a, n, v) : Q.css(a, n)
        })
    };
    Q.extend({
        cssHooks: {
            opacity: {
                get: function(a, c) {
                    if (c) {
                        var r = S1(a, "opacity", "opacity");
                        return r === "" ? "1" : r
                    } else {
                        return a.style.opacity
                    }
                }
            }
        },
        cssNumber: {
            "fillOpacity": true,
            "fontWeight": true,
            "lineHeight": true,
            "opacity": true,
            "orphans": true,
            "widows": true,
            "zIndex": true,
            "zoom": true
        },
        cssProps: {
            "float": Q.support.cssFloat ? "cssFloat" : "styleFloat"
        },
        style: function(a, n, v, b) {
            if (!a || a.nodeType === 3 || a.nodeType === 8 || !a.style) {
                return
            }
            var r, t, o = Q.camelCase(n),
                s = a.style,
                c = Q.cssHooks[o];
            n = Q.cssProps[o] || o;
            if (v !== u) {
                t = typeof v;
                if (t === "string" && (r = O1.exec(v))) {
                    v = (+(r[1] + 1) * +r[2]) + parseFloat(Q.css(a, n));
                    t = "number"
                }
                if (v == null || t === "number" && isNaN(v)) {
                    return
                }
                if (t === "number" && !Q.cssNumber[o]) {
                    v += "px"
                }
                if (!c || !("set" in c) || (v = c.set(a, v)) !== u) {
                    try {
                        s[n] = v
                    } catch (e) {}
                }
            } else {
                if (c && "get" in c && (r = c.get(a, false, b)) !== u) {
                    return r
                }
                return s[n]
            }
        },
        css: function(a, n, b) {
            var r, c;
            n = Q.camelCase(n);
            c = Q.cssHooks[n];
            n = Q.cssProps[n] || n;
            if (n === "cssFloat") {
                n = "float"
            }
            if (c && "get" in c && (r = c.get(a, true, b)) !== u) {
                return r
            } else if (S1) {
                return S1(a, n)
            }
        },
        swap: function(a, o, c) {
            var b = {};
            for (var n in o) {
                b[n] = a.style[n];
                a.style[n] = o[n]
            }
            c.call(a);
            for (n in o) {
                a.style[n] = b[n]
            }
        }
    });
    Q.curCSS = Q.css;
    Q.each(["height", "width"], function(i, n) {
        Q.cssHooks[n] = {
            get: function(a, c, b) {
                var v;
                if (c) {
                    if (a.offsetWidth !== 0) {
                        return V1(a, n, b)
                    } else {
                        Q.swap(a, P1, function() {
                            v = V1(a, n, b)
                        })
                    }
                    return v
                }
            },
            set: function(a, v) {
                if (M1.test(v)) {
                    v = parseFloat(v);
                    if (v >= 0) {
                        return v + "px"
                    }
                } else {
                    return v
                }
            }
        }
    });
    if (!Q.support.opacity) {
        Q.cssHooks.opacity = {
            get: function(a, c) {
                return K1.test((c && a.currentStyle ? a.currentStyle.filter : a.style.filter) || "") ? (parseFloat(RegExp.$1) / 100) + "" : c ? "1" : ""
            },
            set: function(a, v) {
                var s = a.style,
                    U1 = a.currentStyle,
                    o = Q.isNumeric(v) ? "alpha(opacity=" + v * 100 + ")" : "",
                    f = U1 && U1.filter || s.filter || "";
                s.zoom = 1;
                if (v >= 1 && Q.trim(f.replace(J1, "")) === "") {
                    s.removeAttribute("filter");
                    if (U1 && !U1.filter) {
                        return
                    }
                }
                s.filter = J1.test(f) ? f.replace(J1, o) : f + " " + o
            }
        }
    }
    Q(function() {
        if (!Q.support.reliableMarginRight) {
            Q.cssHooks.marginRight = {
                get: function(a, c) {
                    var r;
                    Q.swap(a, {
                        "display": "inline-block"
                    }, function() {
                        if (c) {
                            r = S1(a, "margin-right", "marginRight")
                        } else {
                            r = a.style.marginRight
                        }
                    });
                    return r
                }
            }
        }
    });
    if (d.defaultView && d.defaultView.getComputedStyle) {
        T1 = function(a, n) {
            var r, b, c;
            n = n.replace(L1, "-$1").toLowerCase();
            if ((b = a.ownerDocument.defaultView) && (c = b.getComputedStyle(a, null))) {
                r = c.getPropertyValue(n);
                if (r === "" && !Q.contains(a.ownerDocument.documentElement, a)) {
                    r = Q.style(a, n)
                }
            }
            return r
        }
    }
    if (d.documentElement.currentStyle) {
        U1 = function(a, n) {
            var l, r, b, c = a.currentStyle && a.currentStyle[n],
                s = a.style;
            if (c === null && s && (b = s[n])) {
                c = b
            }
            if (!M1.test(c) && N1.test(c)) {
                l = s.left;
                r = a.runtimeStyle && a.runtimeStyle.left;
                if (r) {
                    a.runtimeStyle.left = a.currentStyle.left
                }
                s.left = n === "fontSize" ? "1em" : (c || 0);
                c = s.pixelLeft + "px";
                s.left = l;
                if (r) {
                    a.runtimeStyle.left = r
                }
            }
            return c === "" ? "auto" : c
        }
    }
    S1 = T1 || U1;

    function V1(a, n, b) {
        var v = n === "width" ? a.offsetWidth : a.offsetHeight,
            c = n === "width" ? Q1 : R1,
            i = 0,
            l = c.length;
        if (v > 0) {
            if (b !== "border") {
                for (; i < l; i++) {
                    if (!b) {
                        v -= parseFloat(Q.css(a, "padding" + c[i])) || 0
                    }
                    if (b === "margin") {
                        v += parseFloat(Q.css(a, b + c[i])) || 0
                    } else {
                        v -= parseFloat(Q.css(a, "border" + c[i] + "Width")) || 0
                    }
                }
            }
            return v + "px"
        }
        v = S1(a, n, n);
        if (v < 0 || v == null) {
            v = a.style[n] || 0
        }
        v = parseFloat(v) || 0;
        if (b) {
            for (; i < l; i++) {
                v += parseFloat(Q.css(a, "padding" + c[i])) || 0;
                if (b !== "padding") {
                    v += parseFloat(Q.css(a, "border" + c[i] + "Width")) || 0
                }
                if (b === "margin") {
                    v += parseFloat(Q.css(a, b + c[i])) || 0
                }
            }
        }
        return v + "px"
    }
    if (Q.expr && Q.expr.filters) {
        Q.expr.filters.hidden = function(a) {
            var b = a.offsetWidth,
                c = a.offsetHeight;
            return (b === 0 && c === 0) || (!Q.support.reliableHiddenOffsets && ((a.style && a.style.display) || Q.css(a, "display")) === "none")
        };
        Q.expr.filters.visible = function(a) {
            return !Q.expr.filters.hidden(a)
        }
    }
    var W1 = /%20/g,
        X1 = /\[\]$/,
        Y1 = /\r?\n/g,
        Z1 = /#.*$/,
        $1 = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg,
        _1 = /^(?:color|date|datetime|datetime-local|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i,
        a2 = /^(?:about|app|app\-storage|.+\-extension|file|res|widget):$/,
        b2 = /^(?:GET|HEAD)$/,
        c2 = /^\/\//,
        d2 = /\?/,
        e2 = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
        f2 = /^(?:select|textarea)/i,
        g2 = /\s+/,
        h2 = /([?&])_=[^&]*/,
        i2 = /^([\w\+\.\-]+:)(?:\/\/([^\/?#:]*)(?::(\d+))?)?/,
        j2 = Q.fn.load,
        k2 = {}, l2 = {}, m2, n2, o2 = ["*/"] + ["*"];
    try {
        m2 = h.href
    } catch (e) {
        m2 = d.createElement("a");
        m2.href = "";
        m2 = m2.href
    }
    n2 = i2.exec(m2.toLowerCase()) || [];

    function p2(s) {
        return function(a, f) {
            if (typeof a !== "string") {
                f = a;
                a = "*"
            }
            if (Q.isFunction(f)) {
                var b = a.toLowerCase().split(g2),
                    i = 0,
                    l = b.length,
                    c, j, p;
                for (; i < l; i++) {
                    c = b[i];
                    p = /^\+/.test(c);
                    if (p) {
                        c = c.substr(1) || "*"
                    }
                    j = s[c] = s[c] || [];
                    j[p ? "unshift" : "push"](f)
                }
            }
        }
    }
    function q2(s, o, a, j, b, c) {
        b = b || o.dataTypes[0];
        c = c || {};
        c[b] = true;
        var l = s[b],
            i = 0,
            f = l ? l.length : 0,
            k = (s === k2),
            m;
        for (; i < f && (k || !m); i++) {
            m = l[i](o, a, j);
            if (typeof m === "string") {
                if (!k || c[m]) {
                    m = u
                } else {
                    o.dataTypes.unshift(m);
                    m = q2(s, o, a, j, m, c)
                }
            }
        }
        if ((k || !m) && !c["*"]) {
            m = q2(s, o, a, j, "*", c)
        }
        return m
    }
    function r2(t, s) {
        var k, a, f = Q.ajaxSettings.flatOptions || {};
        for (k in s) {
            if (s[k] !== u) {
                (f[k] ? t : (a || (a = {})))[k] = s[k]
            }
        }
        if (a) {
            Q.extend(true, t, a)
        }
    }
    Q.fn.extend({
        load: function(a, p, c) {
            if (typeof a !== "string" && j2) {
                return j2.apply(this, arguments)
            } else if (!this.length) {
                return this
            }
            var o = a.indexOf(" ");
            if (o >= 0) {
                var s = a.slice(o, a.length);
                a = a.slice(0, o)
            }
            var t = "GET";
            if (p) {
                if (Q.isFunction(p)) {
                    c = p;
                    p = u
                } else if (typeof p === "object") {
                    p = Q.param(p, Q.ajaxSettings.traditional);
                    t = "POST"
                }
            }
            var b = this;
            Q.ajax({
                url: a,
                type: t,
                dataType: "html",
                data: p,
                complete: function(j, f, i) {
                    i = j.responseText;
                    if (j.isResolved()) {
                        j.done(function(r) {
                            i = r
                        });
                        b.html(s ? Q("<div>").append(i.replace(e2, "")).find(s) : i)
                    }
                    if (c) {
                        b.each(c, [i, f, j])
                    }
                }
            });
            return this
        },
        serialize: function() {
            return Q.param(this.serializeArray())
        },
        serializeArray: function() {
            return this.map(function() {
                return this.elements ? Q.makeArray(this.elements) : this
            }).filter(function() {
                return this.name && !this.disabled && (this.checked || f2.test(this.nodeName) || _1.test(this.type))
            }).map(function(i, a) {
                var v = Q(this).val();
                return v == null ? null : Q.isArray(v) ? Q.map(v, function(v, i) {
                    return {
                        name: a.name,
                        value: v.replace(Y1, "\r\n")
                    }
                }) : {
                    name: a.name,
                    value: v.replace(Y1, "\r\n")
                }
            }).get()
        }
    });
    Q.each("ajaxStart ajaxStop ajaxComplete ajaxError ajaxSuccess ajaxSend".split(" "), function(i, o) {
        Q.fn[o] = function(f) {
            return this.on(o, f)
        }
    });
    Q.each(["get", "post"], function(i, m) {
        Q[m] = function(a, b, c, t) {
            if (Q.isFunction(b)) {
                t = t || c;
                c = b;
                b = u
            }
            return Q.ajax({
                type: m,
                url: a,
                data: b,
                success: c,
                dataType: t
            })
        }
    });
    Q.extend({
        getScript: function(a, c) {
            return Q.get(a, u, c, "script")
        },
        getJSON: function(a, b, c) {
            return Q.get(a, b, c, "json")
        },
        ajaxSetup: function(t, s) {
            if (s) {
                r2(t, Q.ajaxSettings)
            } else {
                s = t;
                t = Q.ajaxSettings
            }
            r2(t, s);
            return t
        },
        ajaxSettings: {
            url: m2,
            isLocal: a2.test(n2[1]),
            global: true,
            type: "GET",
            contentType: "application/x-www-form-urlencoded",
            processData: true,
            async :true,
            accepts: {
                xml: "application/xml, text/xml",
                html: "text/html",
                text: "text/plain",
                json: "application/json, text/javascript",
                "*": o2
            },
            contents: {
                xml: /xml/,
                html: /html/,
                json: /json/
            },
            responseFields: {
                xml: "responseXML",
                text: "responseText"
            },
            converters: {
                "* text": w.String,
                "text html": true,
                "text json": Q.parseJSON,
                "text xml": Q.parseXML
            },
            flatOptions: {
                context: true,
                url: true
            }
        },
        ajaxPrefilter: p2(k2),
        ajaxTransport: p2(l2),
        ajax: function(a, o) {
            if (typeof a === "object") {
                o = a;
                a = u
            }
            o = o || {};
            var s = Q.ajaxSetup({}, o),
                c = s.context || s,
                b = c !== s && (c.nodeType || c instanceof Q) ? Q(c) : Q.event,
                f = Q.Deferred(),
                j = Q.Callbacks("once memory"),
                k = s.statusCode || {}, l, r = {}, m = {}, n, p, t, q, v, _ = 0,
                R2, i, S2 = {
                    readyState: 0,
                    setRequestHeader: function(U2, W2) {
                        if (!_) {
                            var X2 = U2.toLowerCase();
                            U2 = m[X2] = m[X2] || U2;
                            r[U2] = W2
                        }
                        return this
                    },
                    getAllResponseHeaders: function() {
                        return _ === 2 ? n : null
                    },
                    getResponseHeader: function(U2) {
                        var W2;
                        if (_ === 2) {
                            if (!p) {
                                p = {};
                                while ((W2 = $1.exec(n))) {
                                    p[W2[1].toLowerCase()] = W2[2]
                                }
                            }
                            W2 = p[U2.toLowerCase()]
                        }
                        return W2 === u ? null : W2
                    },
                    overrideMimeType: function(U2) {
                        if (!_) {
                            s.mimeType = U2
                        }
                        return this
                    },
                    abort: function(U2) {
                        U2 = U2 || "abort";
                        if (t) {
                            t.abort(U2)
                        }
                        T2(0, U2);
                        return this
                    }
                };

            function T2(U2, W2, X2, Y2) {
                if (_ === 2) {
                    return
                }
                _ = 2;
                if (q) {
                    clearTimeout(q)
                }
                t = u;
                n = Y2 || "";
                S2.readyState = U2 > 0 ? 4 : 0;
                var Z2, $2, _2, a3 = W2,
                    b3 = X2 ? t2(s, S2, X2) : u,
                    c3, d3;
                if (U2 >= 200 && U2 < 300 || U2 === 304) {
                    if (s.ifModified) {
                        if ((c3 = S2.getResponseHeader("Last-Modified"))) {
                            Q.lastModified[l] = c3
                        }
                        if ((d3 = S2.getResponseHeader("Etag"))) {
                            Q.etag[l] = d3
                        }
                    }
                    if (U2 === 304) {
                        a3 = "notmodified";
                        Z2 = true
                    } else {
                        try {
                            $2 = u2(s, b3);
                            a3 = "success";
                            Z2 = true
                        } catch (e) {
                            a3 = "parsererror";
                            _2 = e
                        }
                    }
                } else {
                    _2 = a3;
                    if (!a3 || U2) {
                        a3 = "error";
                        if (U2 < 0) {
                            U2 = 0
                        }
                    }
                }
                S2.status = U2;
                S2.statusText = "" + (W2 || a3);
                if (Z2) {
                    f.resolveWith(c, [$2, a3, S2])
                } else {
                    f.rejectWith(c, [S2, a3, _2])
                }
                S2.statusCode(k);
                k = u;
                if (R2) {
                    b.trigger("ajax" + (Z2 ? "Success" : "Error"), [S2, s, Z2 ? $2 : _2])
                }
                j.fireWith(c, [S2, a3]);
                if (R2) {
                    b.trigger("ajaxComplete", [S2, s]);
                    if (!(--Q.active)) {
                        Q.event.trigger("ajaxStop")
                    }
                }
            }
            f.promise(S2);
            S2.success = S2.done;
            S2.error = S2.fail;
            S2.complete = j.add;
            S2.statusCode = function(U2) {
                if (U2) {
                    var W2;
                    if (_ < 2) {
                        for (W2 in U2) {
                            k[W2] = [k[W2], U2[W2]]
                        }
                    } else {
                        W2 = U2[S2.status];
                        S2.then(W2, W2)
                    }
                }
                return this
            };
            s.url = ((a || s.url) + "").replace(Z1, "").replace(c2, n2[1] + "//");
            s.dataTypes = Q.trim(s.dataType || "*").toLowerCase().split(g2);
            if (s.crossDomain == null) {
                v = i2.exec(s.url.toLowerCase());
                s.crossDomain = !! (v && (v[1] != n2[1] || v[2] != n2[2] || (v[3] || (v[1] === "http:" ? 80 : 443)) != (n2[3] || (n2[1] === "http:" ? 80 : 443))))
            }
            if (s.data && s.processData && typeof s.data !== "string") {
                s.data = Q.param(s.data, s.traditional)
            }
            q2(k2, s, o, S2);
            if (_ === 2) {
                return false
            }
            R2 = s.global;
            s.type = s.type.toUpperCase();
            s.hasContent = !b2.test(s.type);
            if (R2 && Q.active++ === 0) {
                Q.event.trigger("ajaxStart")
            }
            if (!s.hasContent) {
                if (s.data) {
                    s.url += (d2.test(s.url) ? "&" : "?") + s.data;
                    delete s.data
                }
                l = s.url;
                if (s.cache === false) {
                    var ts = Q.now(),
                        V2 = s.url.replace(h2, "$1_=" + ts);
                    s.url = V2 + ((V2 === s.url) ? (d2.test(s.url) ? "&" : "?") + "_=" + ts : "")
                }
            }
            if (s.data && s.hasContent && s.contentType !== false || o.contentType) {
                S2.setRequestHeader("Content-Type", s.contentType)
            }
            if (s.ifModified) {
                l = l || s.url;
                if (Q.lastModified[l]) {
                    S2.setRequestHeader("If-Modified-Since", Q.lastModified[l])
                }
                if (Q.etag[l]) {
                    S2.setRequestHeader("If-None-Match", Q.etag[l])
                }
            }
            S2.setRequestHeader("Accept", s.dataTypes[0] && s.accepts[s.dataTypes[0]] ? s.accepts[s.dataTypes[0]] + (s.dataTypes[0] !== "*" ? ", " + o2 + "; q=0.01" : "") : s.accepts["*"]);
            for (i in s.headers) {
                S2.setRequestHeader(i, s.headers[i])
            }
            if (s.beforeSend && (s.beforeSend.call(c, S2, s) === false || _ === 2)) {
                S2.abort();
                return false
            }
            for (i in {
                success: 1,
                error: 1,
                complete: 1
            }) {
                S2[i](s[i])
            }
            t = q2(l2, s, o, S2);
            if (!t) {
                T2(-1, "No Transport")
            } else {
                S2.readyState = 1;
                if (R2) {
                    b.trigger("ajaxSend", [S2, s])
                }
                if (s.async &&s.timeout > 0) {
                    q = setTimeout(function() {
                        S2.abort("timeout")
                    }, s.timeout)
                }
                try {
                    _ = 1;
                    t.send(r, T2)
                } catch (e) {
                    if (_ < 2) {
                        T2(-1, e)
                    } else {
                        throw e
                    }
                }
            }
            return S2
        },
        param: function(a, t) {
            var s = [],
                b = function(k, v) {
                    v = Q.isFunction(v) ? v() : v;
                    s[s.length] = encodeURIComponent(k) + "=" + encodeURIComponent(v)
                };
            if (t === u) {
                t = Q.ajaxSettings.traditional
            }
            if (Q.isArray(a) || (a.jquery && !Q.isPlainObject(a))) {
                Q.each(a, function() {
                    b(this.name, this.value)
                })
            } else {
                for (var p in a) {
                    s2(p, a[p], t, b)
                }
            }
            return s.join("&").replace(W1, "+")
        }
    });

    function s2(p, o, t, a) {
        if (Q.isArray(o)) {
            Q.each(o, function(i, v) {
                if (t || X1.test(p)) {
                    a(p, v)
                } else {
                    s2(p + "[" + (typeof v === "object" || Q.isArray(v) ? i : "") + "]", v, t, a)
                }
            })
        } else if (!t && o != null && typeof o === "object") {
            for (var n in o) {
                s2(p + "[" + n + "]", o[n], t, a)
            }
        } else {
            a(p, o)
        }
    }
    Q.extend({
        active: 0,
        lastModified: {},
        etag: {}
    });

    function t2(s, j, r) {
        var c = s.contents,
            a = s.dataTypes,
            b = s.responseFields,
            f, t, i, k;
        for (t in b) {
            if (t in r) {
                j[b[t]] = r[t]
            }
        }
        while (a[0] === "*") {
            a.shift();
            if (f === u) {
                f = s.mimeType || j.getResponseHeader("content-type")
            }
        }
        if (f) {
            for (t in c) {
                if (c[t] && c[t].test(f)) {
                    a.unshift(t);
                    break
                }
            }
        }
        if (a[0] in r) {
            i = a[0]
        } else {
            for (t in r) {
                if (!a[0] || s.converters[t + " " + a[0]]) {
                    i = t;
                    break
                }
                if (!k) {
                    k = t
                }
            }
            i = i || k
        }
        if (i) {
            if (i !== a[0]) {
                a.unshift(i)
            }
            return r[i]
        }
    }
    function u2(s, r) {
        if (s.dataFilter) {
            r = s.dataFilter(r, s.dataType)
        }
        var a = s.dataTypes,
            c = {}, i, k, l = a.length,
            t, b = a[0],
            p, f, j, m, n;
        for (i = 1; i < l; i++) {
            if (i === 1) {
                for (k in s.converters) {
                    if (typeof k === "string") {
                        c[k.toLowerCase()] = s.converters[k]
                    }
                }
            }
            p = b;
            b = a[i];
            if (b === "*") {
                b = p
            } else if (p !== "*" && p !== b) {
                f = p + " " + b;
                j = c[f] || c["* " + b];
                if (!j) {
                    n = u;
                    for (m in c) {
                        t = m.split(" ");
                        if (t[0] === p || t[0] === "*") {
                            n = c[t[1] + " " + b];
                            if (n) {
                                m = c[m];
                                if (m === true) {
                                    j = n
                                } else if (n === true) {
                                    j = m
                                }
                                break
                            }
                        }
                    }
                }
                if (!(j || n)) {
                    Q.error("No conversion from " + f.replace(" ", " to "))
                }
                if (j !== true) {
                    r = j ? j(r) : n(m(r))
                }
            }
        }
        return r
    }
    var v2 = Q.now(),
        w2 = /(\=)\?(&|$)|\?\?/i;
    Q.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function() {
            return Q.expando + "_" + (v2++)
        }
    });
    Q.ajaxPrefilter("json jsonp", function(s, o, j) {
        var i = s.contentType === "application/x-www-form-urlencoded" && (typeof s.data === "string");
        if (s.dataTypes[0] === "jsonp" || s.jsonp !== false && (w2.test(s.url) || i && w2.test(s.data))) {
            var r, a = s.jsonpCallback = Q.isFunction(s.jsonpCallback) ? s.jsonpCallback() : s.jsonpCallback,
                p = w[a],
                b = s.url,
                c = s.data,
                f = "$1" + a + "$2";
            if (s.jsonp !== false) {
                b = b.replace(w2, f);
                if (s.url === b) {
                    if (i) {
                        c = c.replace(w2, f)
                    }
                    if (s.data === c) {
                        b += (/\?/.test(b) ? "&" : "?") + s.jsonp + "=" + a
                    }
                }
            }
            s.url = b;
            s.data = c;
            w[a] = function(k) {
                r = [k]
            };
            j.always(function() {
                w[a] = p;
                if (r && Q.isFunction(p)) {
                    w[a](r[0])
                }
            });
            s.converters["script json"] = function() {
                if (!r) {
                    Q.error(a + " was not called")
                }
                return r[0]
            };
            s.dataTypes[0] = "json";
            return "script"
        }
    });
    Q.ajaxSetup({
        accepts: {
            script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
        },
        contents: {
            script: /javascript|ecmascript/
        },
        converters: {
            "text script": function(t) {
                Q.globalEval(t);
                return t
            }
        }
    });
    Q.ajaxPrefilter("script", function(s) {
        if (s.cache === u) {
            s.cache = false
        }
        if (s.crossDomain) {
            s.type = "GET";
            s.global = false
        }
    });
    Q.ajaxTransport("script", function(s) {
        if (s.crossDomain) {
            var a, b = d.head || d.getElementsByTagName("head")[0] || d.documentElement;
            return {
                send: function(_, c) {
                    a = d.createElement("script");
                    a.async = "async";
                    if (s.scriptCharset) {
                        a.charset = s.scriptCharset
                    }
                    a.src = s.url;
                    a.onload = a.onreadystatechange = function(_, i) {
                        if (i || !a.readyState || /loaded|complete/.test(a.readyState)) {
                            a.onload = a.onreadystatechange = null;
                            if (b && a.parentNode) {
                                b.removeChild(a)
                            }
                            a = u;
                            if (!i) {
                                c(200, "success")
                            }
                        }
                    };
                    b.insertBefore(a, b.firstChild)
                },
                abort: function() {
                    if (a) {
                        a.onload(0, 1)
                    }
                }
            }
        }
    });
    var x2 = w.ActiveXObject ? function() {
            for (var k in z2) {
                z2[k](0, 1)
            }
        } : false,
        y2 = 0,
        z2;

    function A2() {
        try {
            return new w.XMLHttpRequest()
        } catch (e) {}
    }
    function B2() {
        try {
            return new w.ActiveXObject("Microsoft.XMLHTTP")
        } catch (e) {}
    }
    Q.ajaxSettings.xhr = w.ActiveXObject ? function() {
        return !this.isLocal && A2() || B2()
    } : A2;
    (function(a) {
        Q.extend(Q.support, {
            ajax: !! a,
            cors: !! a && ("withCredentials" in a)
        })
    })(Q.ajaxSettings.xhr());
    if (Q.support.ajax) {
        Q.ajaxTransport(function(s) {
            if (!s.crossDomain || Q.support.cors) {
                var c;
                return {
                    send: function(a, b) {
                        var f = s.xhr(),
                            j, i;
                        if (s.username) {
                            f.open(s.type, s.url, s.async, s.username, s.password)
                        } else {
                            f.open(s.type, s.url, s.async)
                        }
                        if (s.xhrFields) {
                            for (i in s.xhrFields) {
                                f[i] = s.xhrFields[i]
                            }
                        }
                        if (s.mimeType && f.overrideMimeType) {
                            f.overrideMimeType(s.mimeType)
                        }
                        if (!s.crossDomain && !a["X-Requested-With"]) {
                            a["X-Requested-With"] = "XMLHttpRequest"
                        }
                        try {
                            for (i in a) {
                                f.setRequestHeader(i, a[i])
                            }
                        } catch (_) {}
                        f.send((s.hasContent && s.data) || null);
                        c = function(_, k) {
                            var l, m, r, n, o;
                            try {
                                if (c && (k || f.readyState === 4)) {
                                    c = u;
                                    if (j) {
                                        f.onreadystatechange = Q.noop;
                                        if (x2) {
                                            delete z2[j]
                                        }
                                    }
                                    if (k) {
                                        if (f.readyState !== 4) {
                                            f.abort()
                                        }
                                    } else {
                                        l = f.status;
                                        r = f.getAllResponseHeaders();
                                        n = {};
                                        o = f.responseXML;
                                        if (o && o.documentElement) {
                                            n.xml = o
                                        }
                                        n.text = f.responseText;
                                        try {
                                            m = f.statusText
                                        } catch (e) {
                                            m = ""
                                        }
                                        if (!l && s.isLocal && !s.crossDomain) {
                                            l = n.text ? 200 : 404
                                        } else if (l === 1223) {
                                            l = 204
                                        }
                                    }
                                }
                            } catch (p) {
                                if (!k) {
                                    b(-1, p)
                                }
                            }
                            if (n) {
                                b(l, m, n, r)
                            }
                        };
                        if (!s.async ||f.readyState === 4) {
                            c()
                        } else {
                            j = ++y2;
                            if (x2) {
                                if (!z2) {
                                    z2 = {};
                                    Q(w).unload(x2)
                                }
                                z2[j] = c
                            }
                            f.onreadystatechange = c
                        }
                    },
                    abort: function() {
                        if (c) {
                            c(0, 1)
                        }
                    }
                }
            }
        })
    }
    var C2 = {}, D2, E2, F2 = /^(?:toggle|show|hide)$/,
        G2 = /^([+\-]=)?([\d+.\-]+)([a-z%]*)$/i,
        H2, I2 = [
            ["height", "marginTop", "marginBottom", "paddingTop", "paddingBottom"],
            ["width", "marginLeft", "marginRight", "paddingLeft", "paddingRight"],
            ["opacity"]
        ],
        J2;
    Q.fn.extend({
        show: function(s, a, c) {
            var b, f;
            if (s || s === 0) {
                return this.animate(M2("show", 3), s, a, c)
            } else {
                for (var i = 0, j = this.length; i < j; i++) {
                    b = this[i];
                    if (b.style) {
                        f = b.style.display;
                        if (!Q._data(b, "olddisplay") && f === "none") {
                            f = b.style.display = ""
                        }
                        if (f === "" && Q.css(b, "display") === "none") {
                            Q._data(b, "olddisplay", N2(b.nodeName))
                        }
                    }
                }
                for (i = 0; i < j; i++) {
                    b = this[i];
                    if (b.style) {
                        f = b.style.display;
                        if (f === "" || f === "none") {
                            b.style.display = Q._data(b, "olddisplay") || ""
                        }
                    }
                }
                return this
            }
        },
        hide: function(s, a, c) {
            if (s || s === 0) {
                return this.animate(M2("hide", 3), s, a, c)
            } else {
                var b, f, i = 0,
                    j = this.length;
                for (; i < j; i++) {
                    b = this[i];
                    if (b.style) {
                        f = Q.css(b, "display");
                        if (f !== "none" && !Q._data(b, "olddisplay")) {
                            Q._data(b, "olddisplay", f)
                        }
                    }
                }
                for (i = 0; i < j; i++) {
                    if (this[i].style) {
                        this[i].style.display = "none"
                    }
                }
                return this
            }
        },
        _toggle: Q.fn.toggle,
        toggle: function(f, a, c) {
            var b = typeof f === "boolean";
            if (Q.isFunction(f) && Q.isFunction(a)) {
                this._toggle.apply(this, arguments)
            } else if (f == null || b) {
                this.each(function() {
                    var s = b ? f : Q(this).is(":hidden");
                    Q(this)[s ? "show" : "hide"]()
                })
            } else {
                this.animate(M2("toggle", 3), f, a, c)
            }
            return this
        },
        fadeTo: function(s, t, a, c) {
            return this.filter(":hidden").css("opacity", 0).show().end().animate({
                opacity: t
            }, s, a, c)
        },
        animate: function(a, s, b, c) {
            var o = Q.speed(s, b, c);
            if (Q.isEmptyObject(a)) {
                return this.each(o.complete, [false])
            }
            a = Q.extend({}, a);

            function f() {
                if (o.queue === false) {
                    Q._mark(this)
                }
                var i = Q.extend({}, o),
                    j = this.nodeType === 1,
                    k = j && Q(this).is(":hidden"),
                    n, v, p, e, l, m, q, r, t;
                i.animatedProperties = {};
                for (p in a) {
                    n = Q.camelCase(p);
                    if (p !== n) {
                        a[n] = a[p];
                        delete a[p]
                    }
                    v = a[n];
                    if (Q.isArray(v)) {
                        i.animatedProperties[n] = v[1];
                        v = a[n] = v[0]
                    } else {
                        i.animatedProperties[n] = i.specialEasing && i.specialEasing[n] || i.easing || 'swing'
                    }
                    if (v === "hide" && k || v === "show" && !k) {
                        return i.complete.call(this)
                    }
                    if (j && (n === "height" || n === "width")) {
                        i.overflow = [this.style.overflow, this.style.overflowX, this.style.overflowY];
                        if (Q.css(this, "display") === "inline" && Q.css(this, "float") === "none") {
                            if (!Q.support.inlineBlockNeedsLayout || N2(this.nodeName) === "inline") {
                                this.style.display = "inline-block"
                            } else {
                                this.style.zoom = 1
                            }
                        }
                    }
                }
                if (i.overflow != null) {
                    this.style.overflow = "hidden"
                }
                for (p in a) {
                    e = new Q.fx(this, i, p);
                    v = a[p];
                    if (F2.test(v)) {
                        t = Q._data(this, "toggle" + p) || (v === "toggle" ? k ? "show" : "hide" : 0);
                        if (t) {
                            Q._data(this, "toggle" + p, t === "show" ? "hide" : "show");
                            e[t]()
                        } else {
                            e[v]()
                        }
                    } else {
                        l = G2.exec(v);
                        m = e.cur();
                        if (l) {
                            q = parseFloat(l[2]);
                            r = l[3] || (Q.cssNumber[p] ? "" : "px");
                            if (r !== "px") {
                                Q.style(this, p, (q || 1) + r);
                                m = ((q || 1) / e.cur()) * m;
                                Q.style(this, p, m + r)
                            }
                            if (l[1]) {
                                q = ((l[1] === "-=" ? -1 : 1) * q) + m
                            }
                            e.custom(m, q, r)
                        } else {
                            e.custom(m, v, "")
                        }
                    }
                }
                return true
            }
            return o.queue === false ? this.each(f) : this.queue(o.queue, f)
        },
        stop: function(t, c, a) {
            if (typeof t !== "string") {
                a = c;
                c = t;
                t = u
            }
            if (c && t !== false) {
                this.queue(t || "fx", [])
            }
            return this.each(function() {
                var i, b = false,
                    f = Q.timers,
                    j = Q._data(this);
                if (!a) {
                    Q._unmark(true, this)
                }
                function s(k, j, i) {
                    var l = j[i];
                    Q.removeData(k, i, true);
                    l.stop(a)
                }
                if (t == null) {
                    for (i in j) {
                        if (j[i] && j[i].stop && i.indexOf(".run") === i.length - 4) {
                            s(this, j, i)
                        }
                    }
                } else if (j[i = t + ".run"] && j[i].stop) {
                    s(this, j, i)
                }
                for (i = f.length; i--;) {
                    if (f[i].elem === this && (t == null || f[i].queue === t)) {
                        if (a) {
                            f[i](true)
                        } else {
                            f[i].saveState()
                        }
                        b = true;
                        f.splice(i, 1)
                    }
                }
                if (!(a && b)) {
                    Q.dequeue(this, t)
                }
            })
        }
    });

    function K2() {
        setTimeout(L2, 0);
        return (J2 = Q.now())
    }
    function L2() {
        J2 = u
    }
    function M2(t, n) {
        var o = {};
        Q.each(I2.concat.apply([], I2.slice(0, n)), function() {
            o[this] = t
        });
        return o
    }
    Q.each({
        slideDown: M2("show", 1),
        slideUp: M2("hide", 1),
        slideToggle: M2("toggle", 1),
        fadeIn: {
            opacity: "show"
        },
        fadeOut: {
            opacity: "hide"
        },
        fadeToggle: {
            opacity: "toggle"
        }
    }, function(n, p) {
        Q.fn[n] = function(s, a, c) {
            return this.animate(p, s, a, c)
        }
    });
    Q.extend({
        speed: function(s, a, f) {
            var o = s && typeof s === "object" ? Q.extend({}, s) : {
                complete: f || !f && a || Q.isFunction(s) && s,
                duration: s,
                easing: f && a || a && !Q.isFunction(a) && a
            };
            o.duration = Q.fx.off ? 0 : typeof o.duration === "number" ? o.duration : o.duration in Q.fx.speeds ? Q.fx.speeds[o.duration] : Q.fx.speeds._default;
            if (o.queue == null || o.queue === true) {
                o.queue = "fx"
            }
            o.old = o.complete;
            o.complete = function(n) {
                if (Q.isFunction(o.old)) {
                    o.old.call(this)
                }
                if (o.queue) {
                    Q.dequeue(this, o.queue)
                } else if (n !== false) {
                    Q._unmark(this)
                }
            };
            return o
        },
        easing: {
            linear: function(p, n, f, a) {
                return f + a * p
            },
            swing: function(p, n, f, a) {
                return ((-Math.cos(p * Math.PI) / 2) + 0.5) * a + f
            }
        },
        timers: [],
        fx: function(a, o, p) {
            this.options = o;
            this.elem = a;
            this.prop = p;
            o.orig = o.orig || {}
        }
    });
    Q.fx.prototype = {
        update: function() {
            if (this.options.step) {
                this.options.step.call(this.elem, this.now, this)
            }(Q.fx.step[this.prop] || Q.fx.step._default)(this)
        },
        cur: function() {
            if (this.elem[this.prop] != null && (!this.elem.style || this.elem.style[this.prop] == null)) {
                return this.elem[this.prop]
            }
            var p, r = Q.css(this.elem, this.prop);
            return isNaN(p = parseFloat(r)) ? !r || r === "auto" ? 0 : r : p
        },
        custom: function(f, a, b) {
            var s = this,
                c = Q.fx;
            this.startTime = J2 || K2();
            this.end = a;
            this.now = this.start = f;
            this.pos = this.state = 0;
            this.unit = b || this.unit || (Q.cssNumber[this.prop] ? "" : "px");

            function t(i) {
                return s.step(i)
            }
            t.queue = this.options.queue;
            t.elem = this.elem;
            t.saveState = function() {
                if (s.options.hide && Q._data(s.elem, "fxshow" + s.prop) === u) {
                    Q._data(s.elem, "fxshow" + s.prop, s.start)
                }
            };
            if (t() && Q.timers.push(t) && !H2) {
                H2 = setInterval(c.tick, c.interval)
            }
        },
        show: function() {
            var a = Q._data(this.elem, "fxshow" + this.prop);
            this.options.orig[this.prop] = a || Q.style(this.elem, this.prop);
            this.options.show = true;
            if (a !== u) {
                this.custom(this.cur(), a)
            } else {
                this.custom(this.prop === "width" || this.prop === "height" ? 1 : 0, this.cur())
            }
            Q(this.elem).show()
        },
        hide: function() {
            this.options.orig[this.prop] = Q._data(this.elem, "fxshow" + this.prop) || Q.style(this.elem, this.prop);
            this.options.hide = true;
            this.custom(this.cur(), 0)
        },
        step: function(a) {
            var p, n, c, t = J2 || K2(),
                b = true,
                f = this.elem,
                o = this.options;
            if (a || t >= o.duration + this.startTime) {
                this.now = this.end;
                this.pos = this.state = 1;
                this.update();
                o.animatedProperties[this.prop] = true;
                for (p in o.animatedProperties) {
                    if (o.animatedProperties[p] !== true) {
                        b = false
                    }
                }
                if (b) {
                    if (o.overflow != null && !Q.support.shrinkWrapBlocks) {
                        Q.each(["", "X", "Y"], function(i, v) {
                            f.style["overflow" + v] = o.overflow[i]
                        })
                    }
                    if (o.hide) {
                        Q(f).hide()
                    }
                    if (o.hide || o.show) {
                        for (p in o.animatedProperties) {
                            Q.style(f, p, o.orig[p]);
                            Q.removeData(f, "fxshow" + p, true);
                            Q.removeData(f, "toggle" + p, true)
                        }
                    }
                    c = o.complete;
                    if (c) {
                        o.complete = false;
                        c.call(f)
                    }
                }
                return false
            } else {
                if (o.duration == Infinity) {
                    this.now = t
                } else {
                    n = t - this.startTime;
                    this.state = n / o.duration;
                    this.pos = Q.easing[o.animatedProperties[this.prop]](this.state, n, 0, 1, o.duration);
                    this.now = this.start + ((this.end - this.start) * this.pos)
                }
                this.update()
            }
            return true
        }
    };
    Q.extend(Q.fx, {
        tick: function() {
            var t, a = Q.timers,
                i = 0;
            for (; i < a.length; i++) {
                t = a[i];
                if (!t() && a[i] === t) {
                    a.splice(i--, 1)
                }
            }
            if (!a.length) {
                Q.fx.stop()
            }
        },
        interval: 13,
        stop: function() {
            clearInterval(H2);
            H2 = null
        },
        speeds: {
            slow: 600,
            fast: 200,
            _default: 400
        },
        step: {
            opacity: function(f) {
                Q.style(f.elem, "opacity", f.now)
            },
            _default: function(f) {
                if (f.elem.style && f.elem.style[f.prop] != null) {
                    f.elem.style[f.prop] = f.now + f.unit
                } else {
                    f.elem[f.prop] = f.now
                }
            }
        }
    });
    Q.each(["width", "height"], function(i, p) {
        Q.fx.step[p] = function(f) {
            Q.style(f.elem, p, Math.max(0, f.now) + f.unit)
        }
    });
    if (Q.expr && Q.expr.filters) {
        Q.expr.filters.animated = function(a) {
            return Q.grep(Q.timers, function(f) {
                return a === f.elem
            }).length
        }
    }
    function N2(n) {
        if (!C2[n]) {
            var b = d.body,
                a = Q("<" + n + ">").appendTo(b),
                c = a.css("display");
            a.remove();
            if (c === "none" || c === "") {
                if (!D2) {
                    D2 = d.createElement("iframe");
                    D2.frameBorder = D2.width = D2.height = 0
                }
                b.appendChild(D2);
                if (!E2 || !D2.createElement) {
                    E2 = (D2.contentWindow || D2.contentDocument).document;
                    E2.write((d.compatMode === "CSS1Compat" ? "<!doctype html>" : "") + "<html><body>");
                    E2.close()
                }
                a = E2.createElement(n);
                E2.body.appendChild(a);
                c = Q.css(a, "display");
                b.removeChild(D2)
            }
            C2[n] = c
        }
        return C2[n]
    }
    var O2 = /^t(?:able|d|h)$/i,
        P2 = /^(?:body|html)$/i;
    if ("getBoundingClientRect" in d.documentElement) {
        Q.fn.offset = function(o) {
            var a = this[0],
                b;
            if (o) {
                return this.each(function(i) {
                    Q.offset.setOffset(this, o, i)
                })
            }
            if (!a || !a.ownerDocument) {
                return null
            }
            if (a === a.ownerDocument.body) {
                return Q.offset.bodyOffset(a)
            }
            try {
                b = a.getBoundingClientRect()
            } catch (e) {}
            var c = a.ownerDocument,
                f = c.documentElement;
            if (!b || !Q.contains(f, a)) {
                return b ? {
                    top: b.top,
                    left: b.left
                } : {
                    top: 0,
                    left: 0
                }
            }
            var j = c.body,
                k = Q2(c),
                l = f.clientTop || j.clientTop || 0,
                m = f.clientLeft || j.clientLeft || 0,
                s = k.pageYOffset || Q.support.boxModel && f.scrollTop || j.scrollTop,
                n = k.pageXOffset || Q.support.boxModel && f.scrollLeft || j.scrollLeft,
                t = b.top + s - l,
                p = b.left + n - m;
            return {
                top: t,
                left: p
            }
        }
    } else {
        Q.fn.offset = function(o) {
            var a = this[0];
            if (o) {
                return this.each(function(i) {
                    Q.offset.setOffset(this, o, i)
                })
            }
            if (!a || !a.ownerDocument) {
                return null
            }
            if (a === a.ownerDocument.body) {
                return Q.offset.bodyOffset(a)
            }
            var c, b = a.offsetParent,
                p = a,
                f = a.ownerDocument,
                j = f.documentElement,
                k = f.body,
                l = f.defaultView,
                m = l ? l.getComputedStyle(a, null) : a.currentStyle,
                t = a.offsetTop,
                n = a.offsetLeft;
            while ((a = a.parentNode) && a !== k && a !== j) {
                if (Q.support.fixedPosition && m.position === "fixed") {
                    break
                }
                c = l ? l.getComputedStyle(a, null) : a.currentStyle;
                t -= a.scrollTop;
                n -= a.scrollLeft;
                if (a === b) {
                    t += a.offsetTop;
                    n += a.offsetLeft;
                    if (Q.support.doesNotAddBorder && !(Q.support.doesAddBorderForTableAndCells && O2.test(a.nodeName))) {
                        t += parseFloat(c.borderTopWidth) || 0;
                        n += parseFloat(c.borderLeftWidth) || 0
                    }
                    p = b;
                    b = a.offsetParent
                }
                if (Q.support.subtractsBorderForOverflowNotVisible && c.overflow !== "visible") {
                    t += parseFloat(c.borderTopWidth) || 0;
                    n += parseFloat(c.borderLeftWidth) || 0
                }
                m = c
            }
            if (m.position === "relative" || m.position === "static") {
                t += k.offsetTop;
                n += k.offsetLeft
            }
            if (Q.support.fixedPosition && m.position === "fixed") {
                t += Math.max(j.scrollTop, k.scrollTop);
                n += Math.max(j.scrollLeft, k.scrollLeft)
            }
            return {
                top: t,
                left: n
            }
        }
    }
    Q.offset = {
        bodyOffset: function(b) {
            var t = b.offsetTop,
                l = b.offsetLeft;
            if (Q.support.doesNotIncludeMarginInBodyOffset) {
                t += parseFloat(Q.css(b, "marginTop")) || 0;
                l += parseFloat(Q.css(b, "marginLeft")) || 0
            }
            return {
                top: t,
                left: l
            }
        },
        setOffset: function(a, o, i) {
            var p = Q.css(a, "position");
            if (p === "static") {
                a.style.position = "relative"
            }
            var c = Q(a),
                b = c.offset(),
                f = Q.css(a, "top"),
                j = Q.css(a, "left"),
                k = (p === "absolute" || p === "fixed") && Q.inArray("auto", [f, j]) > -1,
                l = {}, m = {}, n, q;
            if (k) {
                m = c.position();
                n = m.top;
                q = m.left
            } else {
                n = parseFloat(f) || 0;
                q = parseFloat(j) || 0
            }
            if (Q.isFunction(o)) {
                o = o.call(a, i, b)
            }
            if (o.top != null) {
                l.top = (o.top - b.top) + n
            }
            if (o.left != null) {
                l.left = (o.left - b.left) + q
            }
            if ("using" in o) {
                o.using.call(a, l)
            } else {
                c.css(l)
            }
        }
    };
    Q.fn.extend({
        position: function() {
            if (!this[0]) {
                return null
            }
            var a = this[0],
                o = this.offsetParent(),
                b = this.offset(),
                p = P2.test(o[0].nodeName) ? {
                    top: 0,
                    left: 0
                } : o.offset();
            b.top -= parseFloat(Q.css(a, "marginTop")) || 0;
            b.left -= parseFloat(Q.css(a, "marginLeft")) || 0;
            p.top += parseFloat(Q.css(o[0], "borderTopWidth")) || 0;
            p.left += parseFloat(Q.css(o[0], "borderLeftWidth")) || 0;
            return {
                top: b.top - p.top,
                left: b.left - p.left
            }
        },
        offsetParent: function() {
            return this.map(function() {
                var o = this.offsetParent || d.body;
                while (o && (!P2.test(o.nodeName) && Q.css(o, "position") === "static")) {
                    o = o.offsetParent
                }
                return o
            })
        }
    });
    Q.each(["Left", "Top"], function(i, n) {
        var m = "scroll" + n;
        Q.fn[m] = function(v) {
            var a, b;
            if (v === u) {
                a = this[0];
                if (!a) {
                    return null
                }
                b = Q2(a);
                return b ? ("pageXOffset" in b) ? b[i ? "pageYOffset" : "pageXOffset"] : Q.support.boxModel && b.document.documentElement[m] || b.document.body[m] : a[m]
            }
            return this.each(function() {
                b = Q2(this);
                if (b) {
                    b.scrollTo(!i ? v : Q(b).scrollLeft(), i ? v : Q(b).scrollTop())
                } else {
                    this[m] = v
                }
            })
        }
    });

    function Q2(a) {
        return Q.isWindow(a) ? a : a.nodeType === 9 ? a.defaultView || a.parentWindow : false
    }
    Q.each(["Height", "Width"], function(i, n) {
        var t = n.toLowerCase();
        Q.fn["inner" + n] = function() {
            var a = this[0];
            return a ? a.style ? parseFloat(Q.css(a, t, "padding")) : this[t]() : null
        };
        Q.fn["outer" + n] = function(m) {
            var a = this[0];
            return a ? a.style ? parseFloat(Q.css(a, t, m ? "margin" : "border")) : this[t]() : null
        };
        Q.fn[t] = function(s) {
            var a = this[0];
            if (!a) {
                return s == null ? null : this
            }
            if (Q.isFunction(s)) {
                return this.each(function(i) {
                    var f = Q(this);
                    f[t](s.call(this, i, f[t]()))
                })
            }
            if (Q.isWindow(a)) {
                var b = a.document.documentElement["client" + n],
                    c = a.document.body;
                return a.document.compatMode === "CSS1Compat" && b || c && c["client" + n] || b
            } else if (a.nodeType === 9) {
                return Math.max(a.documentElement["client" + n], a.body["scroll" + n], a.documentElement["scroll" + n], a.body["offset" + n], a.documentElement["offset" + n])
            } else if (s === u) {
                var o = Q.css(a, t),
                    r = parseFloat(o);
                return Q.isNumeric(r) ? r : o
            } else {
                return this.css(t, typeof s === "string" ? s : s + "px")
            }
        }
    });
    w.jQuery = w.$ = Q;
    if (typeof define === "function" && define.amd && define.amd.jQuery) {
        define("jquery", [], function() {
            return Q
        })
    }
})(window);