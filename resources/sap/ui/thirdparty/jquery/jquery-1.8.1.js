﻿/*!
 * jQuery JavaScript Library v1.8.1
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2012 jQuery Foundation and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: Thu Aug 30 2012 17:17:22 GMT-0400 (Eastern Daylight Time)
 */

(function(w, u) {
    var d, g, h = w.document,
        k = w.location,
        q = w.navigator,
        x = w.jQuery,
        y = w.$,
        z = Array.prototype.push,
        A = Array.prototype.slice,
        B = Array.prototype.indexOf,
        C = Object.prototype.toString,
        D = Object.prototype.hasOwnProperty,
        E = String.prototype.trim,
        Q = function(s, c) {
            return new Q.fn.init(s, c, d)
        }, F = /[\-+]?(?:\d*\.|)\d+(?:[eE][\-+]?\d+|)/.source,
        G = /\S/,
        H = /\s+/,
        I = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,
        J = /^(?:[^#<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)/,
        K = /^<(\w+)\s*\/?>(?:<\/\1>|)$/,
        L = /^[\],:{}\s]*$/,
        M = /(?:^|:|,)(?:\s*\[)+/g,
        N = /\\(?:["\\\/bfnrt]|u[\da-fA-F]{4})/g,
        O = /"[^"\\\r\n]*"|true|false|null|-?(?:\d\d*\.|)\d+(?:[eE][\-+]?\d+|)/g,
        P = /^-ms-/,
        R = /-([\da-z])/gi,
        S = function(a, l) {
            return (l + "").toUpperCase()
        }, T = function() {
            if (h.addEventListener) {
                h.removeEventListener("DOMContentLoaded", T, false);
                Q.ready()
            } else if (h.readyState === "complete") {
                h.detachEvent("onreadystatechange", T);
                Q.ready()
            }
        }, U = {};
    Q.fn = Q.prototype = {
        constructor: Q,
        init: function(s, c, d) {
            var m, a, r, b;
            if (!s) {
                return this
            }
            if (s.nodeType) {
                this.context = this[0] = s;
                this.length = 1;
                return this
            }
            if (typeof s === "string") {
                if (s.charAt(0) === "<" && s.charAt(s.length - 1) === ">" && s.length >= 3) {
                    m = [null, s, null]
                } else {
                    m = J.exec(s)
                }
                if (m && (m[1] || !c)) {
                    if (m[1]) {
                        c = c instanceof Q ? c[0] : c;
                        b = (c && c.nodeType ? c.ownerDocument || c : h);
                        s = Q.parseHTML(m[1], b, true);
                        if (K.test(m[1]) && Q.isPlainObject(c)) {
                            this.attr.call(s, c, true)
                        }
                        return Q.merge(this, s)
                    } else {
                        a = h.getElementById(m[2]);
                        if (a && a.parentNode) {
                            if (a.id !== m[2]) {
                                return d.find(s)
                            }
                            this.length = 1;
                            this[0] = a
                        }
                        this.context = h;
                        this.selector = s;
                        return this
                    }
                } else if (!c || c.jquery) {
                    return (c || d).find(s)
                } else {
                    return this.constructor(c).find(s)
                }
            } else if (Q.isFunction(s)) {
                return d.ready(s)
            }
            if (s.selector !== u) {
                this.selector = s.selector;
                this.context = s.context
            }
            return Q.makeArray(s, this)
        },
        selector: "",
        jquery: "1.8.1",
        length: 0,
        size: function() {
            return this.length
        },
        toArray: function() {
            return A.call(this)
        },
        get: function(n) {
            return n == null ? this.toArray() : (n < 0 ? this[this.length + n] : this[n])
        },
        pushStack: function(a, n, s) {
            var r = Q.merge(this.constructor(), a);
            r.prevObject = this;
            r.context = this.context;
            if (n === "find") {
                r.selector = this.selector + (this.selector ? " " : "") + s
            } else if (n) {
                r.selector = this.selector + "." + n + "(" + s + ")"
            }
            return r
        },
        each: function(c, a) {
            return Q.each(this, c, a)
        },
        ready: function(f) {
            Q.ready.promise().done(f);
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
            return this.pushStack(A.apply(this, arguments), "slice", A.call(arguments).join(","))
        },
        map: function(c) {
            return this.pushStack(Q.map(this, function(a, i) {
                return c.call(a, i, a)
            }))
        },
        end: function() {
            return this.prevObject || this.constructor(null)
        },
        push: z,
        sort: [].sort,
        splice: [].splice
    };
    Q.fn.init.prototype = Q.fn;
    Q.extend = Q.fn.extend = function() {
        var o, n, s, c, a, b, t = arguments[0] || {}, i = 1,
            l = arguments.length,
            f = false;
        if (typeof t === "boolean") {
            f = t;
            t = arguments[1] || {};
            i = 2
        }
        if (typeof t !== "object" && !Q.isFunction(t)) {
            t = {}
        }
        if (l === i) {
            t = this;
            --i
        }
        for (; i < l; i++) {
            if ((o = arguments[i]) != null) {
                for (n in o) {
                    s = t[n];
                    c = o[n];
                    if (t === c) {
                        continue
                    }
                    if (f && c && (Q.isPlainObject(c) || (a = Q.isArray(c)))) {
                        if (a) {
                            a = false;
                            b = s && Q.isArray(s) ? s : []
                        } else {
                            b = s && Q.isPlainObject(s) ? s : {}
                        }
                        t[n] = Q.extend(f, b, c)
                    } else if (c !== u) {
                        t[n] = c
                    }
                }
            }
        }
        return t
    };
    Q.extend({
        noConflict: function(a) {
            if (w.$ === Q) {
                w.$ = y
            }
            if (a && w.jQuery === Q) {
                w.jQuery = x
            }
            return Q
        },
        isReady: false,
        readyWait: 1,
        holdReady: function(a) {
            if (a) {
                Q.readyWait++
            } else {
                Q.ready(true)
            }
        },
        ready: function(a) {
            if (a === true ? --Q.readyWait : Q.isReady) {
                return
            }
            if (!h.body) {
                return setTimeout(Q.ready, 1)
            }
            Q.isReady = true;
            if (a !== true && --Q.readyWait > 0) {
                return
            }
            g.resolveWith(h, [Q]);
            if (Q.fn.trigger) {
                Q(h).trigger("ready").off("ready")
            }
        },
        isFunction: function(o) {
            return Q.type(o) === "function"
        },
        isArray: Array.isArray || function(o) {
            return Q.type(o) === "array"
        },
        isWindow: function(o) {
            return o != null && o == o.window
        },
        isNumeric: function(o) {
            return !isNaN(parseFloat(o)) && isFinite(o)
        },
        type: function(o) {
            return o == null ? String(o) : U[C.call(o)] || "object"
        },
        isPlainObject: function(o) {
            if (!o || Q.type(o) !== "object" || o.nodeType || Q.isWindow(o)) {
                return false
            }
            try {
                if (o.constructor && !D.call(o, "constructor") && !D.call(o.constructor.prototype, "isPrototypeOf")) {
                    return false
                }
            } catch (e) {
                return false
            }
            var a;
            for (a in o) {}
            return a === u || D.call(o, a)
        },
        isEmptyObject: function(o) {
            var n;
            for (n in o) {
                return false
            }
            return true
        },
        error: function(m) {
            throw new Error(m)
        },
        parseHTML: function(a, c, s) {
            var p;
            if (!a || typeof a !== "string") {
                return null
            }
            if (typeof c === "boolean") {
                s = c;
                c = 0
            }
            c = c || h;
            if ((p = K.exec(a))) {
                return [c.createElement(p[1])]
            }
            p = Q.buildFragment([a], c, s ? null : []);
            return Q.merge([], (p.cacheable ? Q.clone(p.fragment) : p.fragment).childNodes)
        },
        parseJSON: function(a) {
            if (!a || typeof a !== "string") {
                return null
            }
            a = Q.trim(a);
            if (w.JSON && w.JSON.parse) {
                return w.JSON.parse(a)
            }
            if (L.test(a.replace(N, "@").replace(O, "]").replace(M, ""))) {
                return (new Function("return " + a))()
            }
            Q.error("Invalid JSON: " + a)
        },
        parseXML: function(a) {
            var b, t;
            if (!a || typeof a !== "string") {
                return null
            }
            try {
                if (w.DOMParser) {
                    t = new DOMParser();
                    b = t.parseFromString(a, "text/xml")
                } else {
                    b = new ActiveXObject("Microsoft.XMLDOM");
                    b.async = "false";
                    b.loadXML(a)
                }
            } catch (e) {
                b = u
            }
            if (!b || !b.documentElement || b.getElementsByTagName("parsererror").length) {
                Q.error("Invalid XML: " + a)
            }
            return b
        },
        noop: function() {},
        globalEval: function(a) {
            if (a && G.test(a)) {
                (w.execScript || function(a) {
                    w["eval"].call(w, a)
                })(a)
            }
        },
        camelCase: function(s) {
            return s.replace(P, "ms-").replace(R, S)
        },
        nodeName: function(a, n) {
            return a.nodeName && a.nodeName.toUpperCase() === n.toUpperCase()
        },
        each: function(o, c, a) {
            var n, i = 0,
                l = o.length,
                b = l === u || Q.isFunction(o);
            if (a) {
                if (b) {
                    for (n in o) {
                        if (c.apply(o[n], a) === false) {
                            break
                        }
                    }
                } else {
                    for (; i < l;) {
                        if (c.apply(o[i++], a) === false) {
                            break
                        }
                    }
                }
            } else {
                if (b) {
                    for (n in o) {
                        if (c.call(o[n], n, o[n]) === false) {
                            break
                        }
                    }
                } else {
                    for (; i < l;) {
                        if (c.call(o[i], i, o[i++]) === false) {
                            break
                        }
                    }
                }
            }
            return o
        },
        trim: E && !E.call("\uFEFF\xA0") ? function(t) {
            return t == null ? "" : E.call(t)
        } : function(t) {
            return t == null ? "" : t.toString().replace(I, "")
        },
        makeArray: function(a, r) {
            var t, b = r || [];
            if (a != null) {
                t = Q.type(a);
                if (a.length == null || t === "string" || t === "function" || t === "regexp" || Q.isWindow(a)) {
                    z.call(b, a)
                } else {
                    Q.merge(b, a)
                }
            }
            return b
        },
        inArray: function(a, b, i) {
            var l;
            if (b) {
                if (B) {
                    return B.call(b, a, i)
                }
                l = b.length;
                i = i ? i < 0 ? Math.max(0, l + i) : i : 0;
                for (; i < l; i++) {
                    if (i in b && b[i] === a) {
                        return i
                    }
                }
            }
            return -1
        },
        merge: function(f, s) {
            var l = s.length,
                i = f.length,
                j = 0;
            if (typeof l === "number") {
                for (; j < l; j++) {
                    f[i++] = s[j]
                }
            } else {
                while (s[j] !== u) {
                    f[i++] = s[j++]
                }
            }
            f.length = i;
            return f
        },
        grep: function(a, c, b) {
            var r, f = [],
                i = 0,
                l = a.length;
            b = !! b;
            for (; i < l; i++) {
                r = !! c(a[i], i);
                if (b !== r) {
                    f.push(a[i])
                }
            }
            return f
        },
        map: function(a, c, b) {
            var v, f, r = [],
                i = 0,
                l = a.length,
                j = a instanceof Q || l !== u && typeof l === "number" && ((l > 0 && a[0] && a[l - 1]) || l === 0 || Q.isArray(a));
            if (j) {
                for (; i < l; i++) {
                    v = c(a[i], i, b);
                    if (v != null) {
                        r[r.length] = v
                    }
                }
            } else {
                for (f in a) {
                    v = c(a[f], f, b);
                    if (v != null) {
                        r[r.length] = v
                    }
                }
            }
            return r.concat.apply([], r)
        },
        guid: 1,
        proxy: function(f, c) {
            var t, a, p;
            if (typeof c === "string") {
                t = f[c];
                c = f;
                f = t
            }
            if (!Q.isFunction(f)) {
                return u
            }
            a = A.call(arguments, 2);
            p = function() {
                return f.apply(c, a.concat(A.call(arguments)))
            };
            p.guid = f.guid = f.guid || p.guid || Q.guid++;
            return p
        },
        access: function(a, f, b, v, c, j, p) {
            var l, m = b == null,
                i = 0,
                n = a.length;
            if (b && typeof b === "object") {
                for (i in b) {
                    Q.access(a, f, i, b[i], 1, j, v)
                }
                c = 1
            } else if (v !== u) {
                l = p === u && Q.isFunction(v);
                if (m) {
                    if (l) {
                        l = f;
                        f = function(o, b, v) {
                            return l.call(Q(o), v)
                        }
                    } else {
                        f.call(a, v);
                        f = null
                    }
                }
                if (f) {
                    for (; i < n; i++) {
                        f(a[i], b, l ? v.call(a[i], i, f(a[i], b)) : v, p)
                    }
                }
                c = 1
            }
            return c ? a : m ? f.call(a) : n ? f(a[0], b) : j
        },
        now: function() {
            return (new Date()).getTime()
        }
    });
    Q.ready.promise = function(o) {
        if (!g) {
            g = Q.Deferred();
            if (h.readyState === "complete") {
                setTimeout(Q.ready, 1)
            } else if (h.addEventListener) {
                h.addEventListener("DOMContentLoaded", T, false);
                w.addEventListener("load", Q.ready, false)
            } else {
                h.attachEvent("onreadystatechange", T);
                w.attachEvent("onload", Q.ready);
                var t = false;
                try {
                    t = w.frameElement == null && h.documentElement
                } catch (e) {}
                if (t && t.doScroll) {
                    (function doScrollCheck() {
                        if (!Q.isReady) {
                            try {
                                t.doScroll("left")
                            } catch (e) {
                                return setTimeout(doScrollCheck, 50)
                            }
                            Q.ready()
                        }
                    })()
                }
            }
        }
        return g.promise(o)
    };
    Q.each("Boolean Number String Function Array Date RegExp Object".split(" "), function(i, n) {
        U["[object " + n + "]"] = n.toLowerCase()
    });
    d = Q(h);
    var V = {};

    function W(o) {
        var a = V[o] = {};
        Q.each(o.split(H), function(_, f) {
            a[f] = true
        });
        return a
    }
    Q.Callbacks = function(o) {
        o = typeof o === "string" ? (V[o] || W(o)) : Q.extend({}, o);
        var m, f, a, b, c, i, l = [],
            s = !o.once && [],
            j = function(p) {
                m = o.memory && p;
                f = true;
                i = b || 0;
                b = 0;
                c = l.length;
                a = true;
                for (; l && i < c; i++) {
                    if (l[i].apply(p[0], p[1]) === false && o.stopOnFalse) {
                        m = false;
                        break
                    }
                }
                a = false;
                if (l) {
                    if (s) {
                        if (s.length) {
                            j(s.shift())
                        }
                    } else if (m) {
                        l = []
                    } else {
                        n.disable()
                    }
                }
            }, n = {
                add: function() {
                    if (l) {
                        var p = l.length;
                        (function add(r) {
                            Q.each(r, function(_, t) {
                                var v = Q.type(t);
                                if (v === "function" && (!o.unique || !n.has(t))) {
                                    l.push(t)
                                } else if (t && t.length && v !== "string") {
                                    add(t)
                                }
                            })
                        })(arguments);
                        if (a) {
                            c = l.length
                        } else if (m) {
                            b = p;
                            j(m)
                        }
                    }
                    return this
                },
                remove: function() {
                    if (l) {
                        Q.each(arguments, function(_, p) {
                            var r;
                            while ((r = Q.inArray(p, l, r)) > -1) {
                                l.splice(r, 1);
                                if (a) {
                                    if (r <= c) {
                                        c--
                                    }
                                    if (r <= i) {
                                        i--
                                    }
                                }
                            }
                        })
                    }
                    return this
                },
                has: function(p) {
                    return Q.inArray(p, l) > -1
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
                    if (!m) {
                        n.disable()
                    }
                    return this
                },
                locked: function() {
                    return !s
                },
                fireWith: function(p, r) {
                    r = r || [];
                    r = [p, r.slice ? r.slice() : r];
                    if (l && (!f || s)) {
                        if (a) {
                            s.push(r)
                        } else {
                            j(r)
                        }
                    }
                    return this
                },
                fire: function() {
                    n.fireWith(this, arguments);
                    return this
                },
                fired: function() {
                    return !!f
                }
            };
        return n
    };
    Q.extend({
        Deferred: function(f) {
            var t = [
                ["resolve", "done", Q.Callbacks("once memory"), "resolved"],
                ["reject", "fail", Q.Callbacks("once memory"), "rejected"],
                ["notify", "progress", Q.Callbacks("memory")]
            ],
                s = "pending",
                p = {
                    state: function() {
                        return s
                    },
                    always: function() {
                        a.done(arguments).fail(arguments);
                        return this
                    },
                    then: function() {
                        var b = arguments;
                        return Q.Deferred(function(n) {
                            Q.each(t, function(i, c) {
                                var j = c[0],
                                    l = b[i];
                                a[c[1]](Q.isFunction(l) ? function() {
                                    var r = l.apply(this, arguments);
                                    if (r && Q.isFunction(r.promise)) {
                                        r.promise().done(n.resolve).fail(n.reject).progress(n.notify)
                                    } else {
                                        n[j + "With"](this === a ? n : this, [r])
                                    }
                                } : n[j])
                            });
                            b = null
                        }).promise()
                    },
                    promise: function(o) {
                        return typeof o === "object" ? Q.extend(o, p) : p
                    }
                }, a = {};
            p.pipe = p.then;
            Q.each(t, function(i, b) {
                var l = b[2],
                    c = b[3];
                p[b[1]] = l.add;
                if (c) {
                    l.add(function() {
                        s = c
                    }, t[i ^ 1][2].disable, t[2][2].lock)
                }
                a[b[0]] = l.fire;
                a[b[0] + "With"] = l.fireWith
            });
            p.promise(a);
            if (f) {
                f.call(a, a)
            }
            return a
        },
        when: function(s) {
            var i = 0,
                r = A.call(arguments),
                l = r.length,
                a = l !== 1 || (s && Q.isFunction(s.promise)) ? l : 0,
                b = a === 1 ? s : Q.Deferred(),
                c = function(i, m, v) {
                    return function(n) {
                        m[i] = this;
                        v[i] = arguments.length > 1 ? A.call(arguments) : n;
                        if (v === p) {
                            b.notifyWith(m, v)
                        } else if (!(--a)) {
                            b.resolveWith(m, v)
                        }
                    }
                }, p, f, j;
            if (l > 1) {
                p = new Array(l);
                f = new Array(l);
                j = new Array(l);
                for (; i < l; i++) {
                    if (r[i] && Q.isFunction(r[i].promise)) {
                        r[i].promise().done(c(i, j, r)).fail(b.reject).progress(c(i, f, p))
                    } else {
                        --a
                    }
                }
            }
            if (!a) {
                b.resolveWith(j, r)
            }
            return b.promise()
        }
    });
    Q.support = (function() {
        var s, b, a, c, o, f, j, l, i, m, n, p = h.createElement("div");
        p.setAttribute("className", "t");
        p.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";
        b = p.getElementsByTagName("*");
        a = p.getElementsByTagName("a")[0];
        a.style.cssText = "top:1px;float:left;opacity:.5";
        if (!b || !b.length || !a) {
            return {}
        }
        c = h.createElement("select");
        o = c.appendChild(h.createElement("option"));
        f = p.getElementsByTagName("input")[0];
        s = {
            leadingWhitespace: (p.firstChild.nodeType === 3),
            tbody: !p.getElementsByTagName("tbody").length,
            htmlSerialize: !! p.getElementsByTagName("link").length,
            style: /top/.test(a.getAttribute("style")),
            hrefNormalized: (a.getAttribute("href") === "/a"),
            opacity: /^0.5/.test(a.style.opacity),
            cssFloat: !! a.style.cssFloat,
            checkOn: (f.value === "on"),
            optSelected: o.selected,
            getSetAttribute: p.className !== "t",
            enctype: !! h.createElement("form").enctype,
            html5Clone: h.createElement("nav").cloneNode(true).outerHTML !== "<:nav></:nav>",
            boxModel: (h.compatMode === "CSS1Compat"),
            submitBubbles: true,
            changeBubbles: true,
            focusinBubbles: false,
            deleteExpando: true,
            noCloneEvent: true,
            inlineBlockNeedsLayout: false,
            shrinkWrapBlocks: false,
            reliableMarginRight: true,
            boxSizingReliable: true,
            pixelPosition: false
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
            p.attachEvent("onclick", n = function() {
                s.noCloneEvent = false
            });
            p.cloneNode(true).fireEvent("onclick");
            p.detachEvent("onclick", n)
        }
        f = h.createElement("input");
        f.value = "t";
        f.setAttribute("type", "radio");
        s.radioValue = f.value === "t";
        f.setAttribute("checked", "checked");
        f.setAttribute("name", "t");
        p.appendChild(f);
        j = h.createDocumentFragment();
        j.appendChild(p.lastChild);
        s.checkClone = j.cloneNode(true).cloneNode(true).lastChild.checked;
        s.appendChecked = f.checked;
        j.removeChild(f);
        j.appendChild(p);
        if (p.attachEvent) {
            for (i in {
                submit: true,
                change: true,
                focusin: true
            }) {
                l = "on" + i;
                m = (l in p);
                if (!m) {
                    p.setAttribute(l, "return;");
                    m = (typeof p[l] === "function")
                }
                s[i + "Bubbles"] = m
            }
        }
        Q(function() {
            var r, p, t, v, _ = "padding:0;margin:0;border:0;display:block;overflow:hidden;",
                r3 = h.getElementsByTagName("body")[0];
            if (!r3) {
                return
            }
            r = h.createElement("div");
            r.style.cssText = "visibility:hidden;border:0;width:0;height:0;position:static;top:0;margin-top:1px";
            r3.insertBefore(r, r3.firstChild);
            p = h.createElement("div");
            r.appendChild(p);
            p.innerHTML = "<table><tr><td></td><td>t</td></tr></table>";
            t = p.getElementsByTagName("td");
            t[0].style.cssText = "padding:0;margin:0;border:0;display:none";
            m = (t[0].offsetHeight === 0);
            t[0].style.display = "";
            t[1].style.display = "none";
            s.reliableHiddenOffsets = m && (t[0].offsetHeight === 0);
            p.innerHTML = "";
            p.style.cssText = "box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box;padding:1px;border:1px;display:block;width:4px;margin-top:1%;position:absolute;top:1%;";
            s.boxSizing = (p.offsetWidth === 4);
            s.doesNotIncludeMarginInBodyOffset = (r3.offsetTop !== 1);
            if (w.getComputedStyle) {
                s.pixelPosition = (w.getComputedStyle(p, null) || {}).top !== "1%";
                s.boxSizingReliable = (w.getComputedStyle(p, null) || {
                    width: "4px"
                }).width === "4px";
                v = h.createElement("div");
                v.style.cssText = p.style.cssText = _;
                v.style.marginRight = v.style.width = "0";
                p.style.width = "1px";
                p.appendChild(v);
                s.reliableMarginRight = !parseFloat((w.getComputedStyle(v, null) || {}).marginRight)
            }
            if (typeof p.style.zoom !== "undefined") {
                p.innerHTML = "";
                p.style.cssText = _ + "width:1px;padding:1px;display:inline;zoom:1";
                s.inlineBlockNeedsLayout = (p.offsetWidth === 3);
                p.style.display = "block";
                p.style.overflow = "visible";
                p.innerHTML = "<div></div>";
                p.firstChild.style.width = "5px";
                s.shrinkWrapBlocks = (p.offsetWidth !== 3);
                r.style.zoom = 1
            }
            r3.removeChild(r);
            r = p = t = v = null
        });
        j.removeChild(p);
        b = a = c = o = f = j = p = null;
        return s
    })();
    var X = /(?:\{[\s\S]*\}|\[[\s\S]*\])$/,
        Y = /([A-Z])/g;
    Q.extend({
        cache: {},
        deletedIds: [],
        uuid: 0,
        expando: "jQuery" + (Q.fn.jquery + Math.random()).replace(/\D/g, ""),
        noData: {
            "embed": true,
            "object": "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",
            "applet": true
        },
        hasData: function(a) {
            a = a.nodeType ? Q.cache[a[Q.expando]] : a[Q.expando];
            return !!a && !$(a)
        },
        data: function(a, n, b, p) {
            if (!Q.acceptData(a)) {
                return
            }
            var t, r, i = Q.expando,
                c = typeof n === "string",
                f = a.nodeType,
                j = f ? Q.cache : a,
                l = f ? a[i] : a[i] && i;
            if ((!l || !j[l] || (!p && !j[l].data)) && c && b === u) {
                return
            }
            if (!l) {
                if (f) {
                    a[i] = l = Q.deletedIds.pop() || ++Q.uuid
                } else {
                    l = i
                }
            }
            if (!j[l]) {
                j[l] = {};
                if (!f) {
                    j[l].toJSON = Q.noop
                }
            }
            if (typeof n === "object" || typeof n === "function") {
                if (p) {
                    j[l] = Q.extend(j[l], n)
                } else {
                    j[l].data = Q.extend(j[l].data, n)
                }
            }
            t = j[l];
            if (!p) {
                if (!t.data) {
                    t.data = {}
                }
                t = t.data
            }
            if (b !== u) {
                t[Q.camelCase(n)] = b
            }
            if (c) {
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
            var t, i, l, b = a.nodeType,
                c = b ? Q.cache : a,
                f = b ? a[Q.expando] : Q.expando;
            if (!c[f]) {
                return
            }
            if (n) {
                t = p ? c[f] : c[f].data;
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
                    if (!(p ? $ : Q.isEmptyObject)(t)) {
                        return
                    }
                }
            }
            if (!p) {
                delete c[f].data;
                if (!$(c[f])) {
                    return
                }
            }
            if (b) {
                Q.cleanData([a], true)
            } else if (Q.support.deleteExpando || c != c.window) {
                delete c[f]
            } else {
                c[f] = null
            }
        },
        _data: function(a, n, b) {
            return Q.data(a, n, b, true)
        },
        acceptData: function(a) {
            var n = a.nodeName && Q.noData[a.nodeName.toLowerCase()];
            return !n || n !== true && a.getAttribute("classid") === n
        }
    });
    Q.fn.extend({
        data: function(a, v) {
            var p, b, c, n, l, f = this[0],
                i = 0,
                j = null;
            if (a === u) {
                if (this.length) {
                    j = Q.data(f);
                    if (f.nodeType === 1 && !Q._data(f, "parsedAttrs")) {
                        c = f.attributes;
                        for (l = c.length; i < l; i++) {
                            n = c[i].name;
                            if (n.indexOf("data-") === 0) {
                                n = Q.camelCase(n.substring(5));
                                Z(f, n, j[n])
                            }
                        }
                        Q._data(f, "parsedAttrs", true)
                    }
                }
                return j
            }
            if (typeof a === "object") {
                return this.each(function() {
                    Q.data(this, a)
                })
            }
            p = a.split(".", 2);
            p[1] = p[1] ? "." + p[1] : "";
            b = p[1] + "!";
            return Q.access(this, function(v) {
                if (v === u) {
                    j = this.triggerHandler("getData" + b, [p[0]]);
                    if (j === u && f) {
                        j = Q.data(f, a);
                        j = Z(f, a, j)
                    }
                    return j === u && p[1] ? this.data(p[0]) : j
                }
                p[1] = v;
                this.each(function() {
                    var s = Q(this);
                    s.triggerHandler("setData" + b, p);
                    Q.data(this, a, v);
                    s.triggerHandler("changeData" + b, p)
                })
            }, null, v, arguments.length > 1, null, false)
        },
        removeData: function(a) {
            return this.each(function() {
                Q.removeData(this, a)
            })
        }
    });

    function Z(a, b, c) {
        if (c === u && a.nodeType === 1) {
            var n = "data-" + b.replace(Y, "-$1").toLowerCase();
            c = a.getAttribute(n);
            if (typeof c === "string") {
                try {
                    c = c === "true" ? true : c === "false" ? false : c === "null" ? null : +c + "" === c ? +c : X.test(c) ? Q.parseJSON(c) : c
                } catch (e) {}
                Q.data(a, b, c)
            } else {
                c = u
            }
        }
        return c
    }
    function $(o) {
        var n;
        for (n in o) {
            if (n === "data" && Q.isEmptyObject(o[n])) {
                continue
            }
            if (n !== "toJSON") {
                return false
            }
        }
        return true
    }
    Q.extend({
        queue: function(a, t, b) {
            var c;
            if (a) {
                t = (t || "fx") + "queue";
                c = Q._data(a, t);
                if (b) {
                    if (!c || Q.isArray(b)) {
                        c = Q._data(a, t, Q.makeArray(b))
                    } else {
                        c.push(b)
                    }
                }
                return c || []
            }
        },
        dequeue: function(a, t) {
            t = t || "fx";
            var b = Q.queue(a, t),
                s = b.length,
                f = b.shift(),
                c = Q._queueHooks(a, t),
                n = function() {
                    Q.dequeue(a, t)
                };
            if (f === "inprogress") {
                f = b.shift();
                s--
            }
            if (f) {
                if (t === "fx") {
                    b.unshift("inprogress")
                }
                delete c.stop;
                f.call(a, n, c)
            }
            if (!s && c) {
                c.empty.fire()
            }
        },
        _queueHooks: function(a, t) {
            var b = t + "queueHooks";
            return Q._data(a, b) || Q._data(a, b, {
                empty: Q.Callbacks("once memory").add(function() {
                    Q.removeData(a, t + "queue", true);
                    Q.removeData(a, b, true)
                })
            })
        }
    });
    Q.fn.extend({
        queue: function(t, a) {
            var s = 2;
            if (typeof t !== "string") {
                a = t;
                t = "fx";
                s--
            }
            if (arguments.length < s) {
                return Q.queue(this[0], t)
            }
            return a === u ? this : this.each(function() {
                var b = Q.queue(this, t, a);
                Q._queueHooks(this, t);
                if (t === "fx" && b[0] !== "inprogress") {
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
            var a, c = 1,
                b = Q.Deferred(),
                f = this,
                i = this.length,
                r = function() {
                    if (!(--c)) {
                        b.resolveWith(f, [f])
                    }
                };
            if (typeof t !== "string") {
                o = t;
                t = u
            }
            t = t || "fx";
            while (i--) {
                a = Q._data(f[i], t + "queueHooks");
                if (a && a.empty) {
                    c++;
                    a.empty.add(r)
                }
            }
            r();
            return b.promise(o)
        }
    });
    var a1, b1, c1, d1 = /[\t\r\n]/g,
        e1 = /\r/g,
        f1 = /^(?:button|input)$/i,
        g1 = /^(?:button|input|object|select|textarea)$/i,
        h1 = /^a(?:rea|)$/i,
        i1 = /^(?:autofocus|autoplay|async|checked|controls|defer|disabled|hidden|loop|multiple|open|readonly|required|scoped|selected)$/i,
        j1 = Q.support.getSetAttribute;
    Q.fn.extend({
        attr: function(n, v) {
            return Q.access(this, Q.attr, n, v, arguments.length > 1)
        },
        removeAttr: function(n) {
            return this.each(function() {
                Q.removeAttr(this, n)
            })
        },
        prop: function(n, v) {
            return Q.access(this, Q.prop, n, v, arguments.length > 1)
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
                a = v.split(H);
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
            var r, a, b, c, f, i, l;
            if (Q.isFunction(v)) {
                return this.each(function(j) {
                    Q(this).removeClass(v.call(this, j, this.className))
                })
            }
            if ((v && typeof v === "string") || v === u) {
                r = (v || "").split(H);
                for (i = 0, l = this.length; i < l; i++) {
                    b = this[i];
                    if (b.nodeType === 1 && b.className) {
                        a = (" " + b.className + " ").replace(d1, " ");
                        for (c = 0, f = r.length; c < f; c++) {
                            while (a.indexOf(" " + r[c] + " ") > -1) {
                                a = a.replace(" " + r[c] + " ", " ")
                            }
                        }
                        b.className = v ? Q.trim(a) : ""
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
                        j = v.split(H);
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
                if (this[i].nodeType === 1 && (" " + this[i].className + " ").replace(d1, " ").indexOf(c) > -1) {
                    return true
                }
            }
            return false
        },
        val: function(v) {
            var a, r, b, c = this[0];
            if (!arguments.length) {
                if (c) {
                    a = Q.valHooks[c.type] || Q.valHooks[c.nodeName.toLowerCase()];
                    if (a && "get" in a && (r = a.get(c, "value")) !== u) {
                        return r
                    }
                    r = c.value;
                    return typeof r === "string" ? r.replace(e1, "") : r == null ? "" : r
                }
                return
            }
            b = Q.isFunction(v);
            return this.each(function(i) {
                var f, s = Q(this);
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
                a = Q.valHooks[this.type] || Q.valHooks[this.nodeName.toLowerCase()];
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
        attrFn: {},
        attr: function(a, n, v, p) {
            var r, b, c, f = a.nodeType;
            if (!a || f === 3 || f === 8 || f === 2) {
                return
            }
            if (p && Q.isFunction(Q.fn[n])) {
                return Q(a)[n](v)
            }
            if (typeof a.getAttribute === "undefined") {
                return Q.prop(a, n, v)
            }
            c = f !== 1 || !Q.isXMLDoc(a);
            if (c) {
                n = n.toLowerCase();
                b = Q.attrHooks[n] || (i1.test(n) ? b1 : a1)
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
            var p, b, n, c, i = 0;
            if (v && a.nodeType === 1) {
                b = v.split(H);
                for (; i < b.length; i++) {
                    n = b[i];
                    if (n) {
                        p = Q.propFix[n] || n;
                        c = i1.test(n);
                        if (!c) {
                            Q.attr(a, n, "")
                        }
                        a.removeAttribute(j1 ? n : p);
                        if (c && p in a) {
                            a[p] = false
                        }
                    }
                }
            }
        },
        attrHooks: {
            type: {
                set: function(a, v) {
                    if (f1.test(a.nodeName) && a.parentNode) {
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
                    if (a1 && Q.nodeName(a, "button")) {
                        return a1.get(a, n)
                    }
                    return n in a ? a.value : null
                },
                set: function(a, v, n) {
                    if (a1 && Q.nodeName(a, "button")) {
                        return a1.set(a, v, n)
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
                    return b && b.specified ? parseInt(b.value, 10) : g1.test(a.nodeName) || h1.test(a.nodeName) && a.href ? 0 : u
                }
            }
        }
    });
    b1 = {
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
    if (!j1) {
        c1 = {
            name: true,
            id: true,
            coords: true
        };
        a1 = Q.valHooks.button = {
            get: function(a, n) {
                var r;
                r = a.getAttributeNode(n);
                return r && (c1[n] ? r.value !== "" : r.specified) ? r.value : u
            },
            set: function(a, v, n) {
                var r = a.getAttributeNode(n);
                if (!r) {
                    r = h.createAttribute(n);
                    a.setAttributeNode(r)
                }
                return (r.value = v + "")
            }
        };
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
            get: a1.get,
            set: function(a, v, n) {
                if (v === "") {
                    v = "false"
                }
                a1.set(a, v, n)
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
    var k1 = /^(?:textarea|input|select)$/i,
        l1 = /^([^\.]*|)(?:\.(.+)|)$/,
        m1 = /(?:^|\s)hover(\.\S+|)\b/,
        n1 = /^key/,
        o1 = /^(?:mouse|contextmenu)|click/,
        p1 = /^(?:focusinfocus|focusoutblur)$/,
        q1 = function(a) {
            return Q.event.special.hover ? a : a.replace(m1, "mouseenter$1 mouseleave$1")
        };
    Q.event = {
        add: function(a, b, c, f, s) {
            var i, j, l, t, m, n, o, p, r, v, _;
            if (a.nodeType === 3 || a.nodeType === 8 || !b || !c || !(i = Q._data(a))) {
                return
            }
            if (c.handler) {
                r = c;
                c = r.handler;
                s = r.selector
            }
            if (!c.guid) {
                c.guid = Q.guid++
            }
            l = i.events;
            if (!l) {
                i.events = l = {}
            }
            j = i.handle;
            if (!j) {
                i.handle = j = function(e) {
                    return typeof Q !== "undefined" && (!e || Q.event.triggered !== e.type) ? Q.event.dispatch.apply(j.elem, arguments) : u
                };
                j.elem = a
            }
            b = Q.trim(q1(b)).split(" ");
            for (t = 0; t < b.length; t++) {
                m = l1.exec(b[t]) || [];
                n = m[1];
                o = (m[2] || "").split(".").sort();
                _ = Q.event.special[n] || {};
                n = (s ? _.delegateType : _.bindType) || n;
                _ = Q.event.special[n] || {};
                p = Q.extend({
                    type: n,
                    origType: m[1],
                    data: f,
                    handler: c,
                    guid: c.guid,
                    selector: s,
                    namespace: o.join(".")
                }, r);
                v = l[n];
                if (!v) {
                    v = l[n] = [];
                    v.delegateCount = 0;
                    if (!_.setup || _.setup.call(a, f, o, j) === false) {
                        if (a.addEventListener) {
                            a.addEventListener(n, j, false)
                        } else if (a.attachEvent) {
                            a.attachEvent("on" + n, j)
                        }
                    }
                }
                if (_.add) {
                    _.add.call(a, p);
                    if (!p.handler.guid) {
                        p.handler.guid = c.guid
                    }
                }
                if (s) {
                    v.splice(v.delegateCount++, 0, p)
                } else {
                    v.push(p)
                }
                Q.event.global[n] = true
            }
            a = null
        },
        global: {},
        remove: function(a, b, c, s, m) {
            var t, f, i, o, n, l, j, p, r, v, _, r3 = Q.hasData(a) && Q._data(a);
            if (!r3 || !(p = r3.events)) {
                return
            }
            b = Q.trim(q1(b || "")).split(" ");
            for (t = 0; t < b.length; t++) {
                f = l1.exec(b[t]) || [];
                i = o = f[1];
                n = f[2];
                if (!i) {
                    for (i in p) {
                        Q.event.remove(a, i + b[t], c, s, true)
                    }
                    continue
                }
                r = Q.event.special[i] || {};
                i = (s ? r.delegateType : r.bindType) || i;
                v = p[i] || [];
                l = v.length;
                n = n ? new RegExp("(^|\\.)" + n.split(".").sort().join("\\.(?:.*\\.|)") + "(\\.|$)") : null;
                for (j = 0; j < v.length; j++) {
                    _ = v[j];
                    if ((m || o === _.origType) && (!c || c.guid === _.guid) && (!n || n.test(_.namespace)) && (!s || s === _.selector || s === "**" && _.selector)) {
                        v.splice(j--, 1);
                        if (_.selector) {
                            v.delegateCount--
                        }
                        if (r.remove) {
                            r.remove.call(a, _)
                        }
                    }
                }
                if (v.length === 0 && l !== v.length) {
                    if (!r.teardown || r.teardown.call(a, n, r3.handle) === false) {
                        Q.removeEvent(a, i, r3.handle)
                    }
                    delete p[i]
                }
            }
            if (Q.isEmptyObject(p)) {
                delete r3.handle;
                Q.removeData(a, "events", true)
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
            var f, j, i, l, m, n, s, p, r, t, v = a.type || a,
                _ = [];
            if (p1.test(v + Q.event.triggered)) {
                return
            }
            if (v.indexOf("!") >= 0) {
                v = v.slice(0, -1);
                j = true
            }
            if (v.indexOf(".") >= 0) {
                _ = v.split(".");
                v = _.shift();
                _.sort()
            }
            if ((!c || Q.event.customEvent[v]) && !Q.event.global[v]) {
                return
            }
            a = typeof a === "object" ? a[Q.expando] ? a : new Q.Event(v, a) : new Q.Event(v);
            a.type = v;
            a.isTrigger = true;
            a.exclusive = j;
            a.namespace = _.join(".");
            a.namespace_re = a.namespace ? new RegExp("(^|\\.)" + _.join("\\.(?:.*\\.|)") + "(\\.|$)") : null;
            n = v.indexOf(":") < 0 ? "on" + v : "";
            if (!c) {
                f = Q.cache;
                for (i in f) {
                    if (f[i].events && f[i].events[v]) {
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
            s = Q.event.special[v] || {};
            if (s.trigger && s.trigger.apply(c, b) === false) {
                return
            }
            r = [
                [c, s.bindType || v]
            ];
            if (!o && !s.noBubble && !Q.isWindow(c)) {
                t = s.delegateType || v;
                l = p1.test(t + v) ? c : c.parentNode;
                for (m = c; l; l = l.parentNode) {
                    r.push([l, t]);
                    m = l
                }
                if (m === (c.ownerDocument || h)) {
                    r.push([m.defaultView || m.parentWindow || w, t])
                }
            }
            for (i = 0; i < r.length && !a.isPropagationStopped(); i++) {
                l = r[i][0];
                a.type = r[i][1];
                p = (Q._data(l, "events") || {})[a.type] && Q._data(l, "handle");
                if (p) {
                    p.apply(l, b)
                }
                p = n && l[n];
                if (p && Q.acceptData(l) && p.apply(l, b) === false) {
                    a.preventDefault()
                }
            }
            a.type = v;
            if (!o && !a.isDefaultPrevented()) {
                if ((!s._default || s._default.apply(c.ownerDocument, b) === false) && !(v === "click" && Q.nodeName(c, "a")) && Q.acceptData(c)) {
                    if (n && c[v] && ((v !== "focus" && v !== "blur") || a.target.offsetWidth !== 0) && !Q.isWindow(c)) {
                        m = c[n];
                        if (m) {
                            c[n] = null
                        }
                        Q.event.triggered = v;
                        c[v]();
                        Q.event.triggered = u;
                        if (m) {
                            c[n] = m
                        }
                    }
                }
            }
            return a.result
        },
        dispatch: function(a) {
            a = Q.event.fix(a || w.event);
            var i, j, c, r, s, m, b, f, l, n, o = ((Q._data(this, "events") || {})[a.type] || []),
                p = o.delegateCount,
                t = [].slice.call(arguments),
                v = !a.exclusive && !a.namespace,
                _ = Q.event.special[a.type] || {}, r3 = [];
            t[0] = a;
            a.delegateTarget = this;
            if (_.preDispatch && _.preDispatch.call(this, a) === false) {
                return
            }
            if (p && !(a.button && a.type === "click")) {
                for (c = a.target; c != this; c = c.parentNode || this) {
                    if (c.disabled !== true || a.type !== "click") {
                        s = {};
                        b = [];
                        for (i = 0; i < p; i++) {
                            f = o[i];
                            l = f.selector;
                            if (s[l] === u) {
                                s[l] = Q(l, this).index(c) >= 0
                            }
                            if (s[l]) {
                                b.push(f)
                            }
                        }
                        if (b.length) {
                            r3.push({
                                elem: c,
                                matches: b
                            })
                        }
                    }
                }
            }
            if (o.length > p) {
                r3.push({
                    elem: this,
                    matches: o.slice(p)
                })
            }
            for (i = 0; i < r3.length && !a.isPropagationStopped(); i++) {
                m = r3[i];
                a.currentTarget = m.elem;
                for (j = 0; j < m.matches.length && !a.isImmediatePropagationStopped(); j++) {
                    f = m.matches[j];
                    if (v || (!a.namespace && !f.namespace) || a.namespace_re && a.namespace_re.test(f.namespace)) {
                        a.data = f.data;
                        a.handleObj = f;
                        r = ((Q.event.special[f.origType] || {}).handle || f.handler).apply(m.elem, t);
                        if (r !== u) {
                            a.result = r;
                            if (r === false) {
                                a.preventDefault();
                                a.stopPropagation()
                            }
                        }
                    }
                }
            }
            if (_.postDispatch) {
                _.postDispatch.call(this, a)
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
                    b = a.target.ownerDocument || h;
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
                a.target = o.srcElement || h
            }
            if (a.target.nodeType === 3) {
                a.target = a.target.parentNode
            }
            a.metaKey = !! a.metaKey;
            return f.filter ? f.filter(a, o) : a
        },
        special: {
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
    Q.removeEvent = h.removeEventListener ? function(a, t, b) {
        if (a.removeEventListener) {
            a.removeEventListener(t, b, false)
        }
    } : function(a, t, b) {
        var n = "on" + t;
        if (a.detachEvent) {
            if (typeof a[n] === "undefined") {
                a[n] = null
            }
            a.detachEvent(n, b)
        }
    };
    Q.Event = function(s, p) {
        if (!(this instanceof Q.Event)) {
            return new Q.Event(s, p)
        }
        if (s && s.type) {
            this.originalEvent = s;
            this.type = s.type;
            this.isDefaultPrevented = (s.defaultPrevented || s.returnValue === false || s.getPreventDefault && s.getPreventDefault()) ? s1 : r1
        } else {
            this.type = s
        }
        if (p) {
            Q.extend(this, p)
        }
        this.timeStamp = s && s.timeStamp || Q.now();
        this[Q.expando] = true
    };

    function r1() {
        return false
    }
    function s1() {
        return true
    }
    Q.Event.prototype = {
        preventDefault: function() {
            this.isDefaultPrevented = s1;
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
            this.isPropagationStopped = s1;
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
            this.isImmediatePropagationStopped = s1;
            this.stopPropagation()
        },
        isDefaultPrevented: r1,
        isPropagationStopped: r1,
        isImmediatePropagationStopped: r1
    };
    Q.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function(o, f) {
        Q.event.special[o] = {
            delegateType: f,
            bindType: f,
            handle: function(a) {
                var r, t = this,
                    b = a.relatedTarget,
                    c = a.handleObj,
                    s = c.selector;
                if (!b || (b !== t && !Q.contains(t, b))) {
                    a.type = c.origType;
                    r = c.handler.apply(this, arguments);
                    a.type = f
                }
                return r
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
                    if (f && !Q._data(f, "_submit_attached")) {
                        Q.event.add(f, "submit._submit", function(b) {
                            b._submit_bubble = true
                        });
                        Q._data(f, "_submit_attached", true)
                    }
                })
            },
            postDispatch: function(a) {
                if (a._submit_bubble) {
                    delete a._submit_bubble;
                    if (this.parentNode && !a.isTrigger) {
                        Q.event.simulate("submit", this.parentNode, a, true)
                    }
                }
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
                if (k1.test(this.nodeName)) {
                    if (this.type === "checkbox" || this.type === "radio") {
                        Q.event.add(this, "propertychange._change", function(a) {
                            if (a.originalEvent.propertyName === "checked") {
                                this._just_changed = true
                            }
                        });
                        Q.event.add(this, "click._change", function(a) {
                            if (this._just_changed && !a.isTrigger) {
                                this._just_changed = false
                            }
                            Q.event.simulate("change", this, a, true)
                        })
                    }
                    return false
                }
                Q.event.add(this, "beforeactivate._change", function(e) {
                    var a = e.target;
                    if (k1.test(a.nodeName) && !Q._data(a, "_change_attached")) {
                        Q.event.add(a, "change._change", function(b) {
                            if (this.parentNode && !b.isSimulated && !b.isTrigger) {
                                Q.event.simulate("change", this.parentNode, b, true)
                            }
                        });
                        Q._data(a, "_change_attached", true)
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
                return !k1.test(this.nodeName)
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
                        h.addEventListener(o, b, true)
                    }
                },
                teardown: function() {
                    if (--a === 0) {
                        h.removeEventListener(o, b, true)
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
                    a = a || s;
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
                f = r1
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
            return this.on(t, s, a, f, 1)
        },
        off: function(t, s, f) {
            var a, b;
            if (t && t.preventDefault && t.handleObj) {
                a = t.handleObj;
                Q(t.delegateTarget).off(a.namespace ? a.origType + "." + a.namespace : a.origType, a.selector, a.handler);
                return this
            }
            if (typeof t === "object") {
                for (b in t) {
                    this.off(b, s, t[b])
                }
                return this
            }
            if (s === false || typeof s === "function") {
                f = s;
                s = u
            }
            if (f === false) {
                f = r1
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
            return arguments.length == 1 ? this.off(s, "**") : this.off(t, s || "**", f)
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
        if (n1.test(n)) {
            Q.event.fixHooks[n] = Q.event.keyHooks
        }
        if (o1.test(n)) {
            Q.event.fixHooks[n] = Q.event.mouseHooks
        }
    });

    /*!
     * Sizzle CSS Selector Engine
     *  Copyright 2012 jQuery Foundation and other contributors
     *  Released under the MIT license
     *  http://sizzlejs.com/
     */

    (function(w, u) {
        var c, f, l, n, o, p, r, s, t, v, _ = true,
            r3 = "undefined",
            s3 = ("sizcache" + Math.random()).replace(".", ""),
            h = w.document,
            t3 = h.documentElement,
            u3 = 0,
            v3 = [].slice,
            w3 = [].push,
            x3 = function(a, b) {
                a[s3] = b || true;
                return a
            }, y3 = function() {
                var a = {}, b = [];
                return x3(function(i, j) {
                    if (b.push(i) > n.cacheLength) {
                        delete a[b.shift()]
                    }
                    return (a[i] = j)
                }, a)
            }, z3 = y3(),
            A3 = y3(),
            B3 = y3(),
            C3 = "[\\x20\\t\\r\\n\\f]",
            D3 = "(?:\\\\.|[-\\w]|[^\\x00-\\xa0])+",
            E3 = D3.replace("w", "w#"),
            F3 = "([*^$|!~]?=)",
            G3 = "\\[" + C3 + "*(" + D3 + ")" + C3 + "*(?:" + F3 + C3 + "*(?:(['\"])((?:\\\\.|[^\\\\])*?)\\3|(" + E3 + ")|)|)" + C3 + "*\\]",
            H3 = ":(" + D3 + ")(?:\\((?:(['\"])((?:\\\\.|[^\\\\])*?)\\2|([^()[\\]]*|(?:(?:" + G3 + ")|[^:]|\\\\.)*|.*))\\)|)",
            I3 = ":(nth|eq|gt|lt|first|last|even|odd)(?:\\(((?:-\\d)?\\d*)\\)|)(?=[^-]|$)",
            I = new RegExp("^" + C3 + "+|((?:^|[^\\\\])(?:\\\\.)*)" + C3 + "+$", "g"),
            J3 = new RegExp("^" + C3 + "*," + C3 + "*"),
            K3 = new RegExp("^" + C3 + "*([\\x20\\t\\r\\n\\f>+~])" + C3 + "*"),
            L3 = new RegExp(H3),
            J = /^(?:#([\w\-]+)|(\w+)|\.([\w\-]+))$/,
            M3 = /^:not/,
            N3 = /[\x20\t\r\n\f]*[+~]/,
            O3 = /:not\($/,
            P3 = /h\d/i,
            Q3 = /input|select|textarea|button/i,
            R3 = /\\(?!\\)/g,
            S3 = {
                "ID": new RegExp("^#(" + D3 + ")"),
                "CLASS": new RegExp("^\\.(" + D3 + ")"),
                "NAME": new RegExp("^\\[name=['\"]?(" + D3 + ")['\"]?\\]"),
                "TAG": new RegExp("^(" + D3.replace("w", "w*") + ")"),
                "ATTR": new RegExp("^" + G3),
                "PSEUDO": new RegExp("^" + H3),
                "CHILD": new RegExp("^:(only|nth|last|first)-child(?:\\(" + C3 + "*(even|odd|(([+-]|)(\\d*)n|)" + C3 + "*(?:([+-]|)" + C3 + "*(\\d+)|))" + C3 + "*\\)|)", "i"),
                "POS": new RegExp(I3, "ig"),
                "needsContext": new RegExp("^" + C3 + "*[>+~]|" + I3, "i")
            }, T3 = function(a) {
                var b = h.createElement("div");
                try {
                    return a(b)
                } catch (e) {
                    return false
                } finally {
                    b = null
                }
            }, U3 = T3(function(a) {
                a.appendChild(h.createComment(""));
                return !a.getElementsByTagName("*").length
            }),
            V3 = T3(function(a) {
                a.innerHTML = "<a href='#'></a>";
                return a.firstChild && typeof a.firstChild.getAttribute !== r3 && a.firstChild.getAttribute("href") === "#"
            }),
            W3 = T3(function(a) {
                a.innerHTML = "<select></select>";
                var b = typeof a.lastChild.getAttribute("multiple");
                return b !== "boolean" && b !== "string"
            }),
            X3 = T3(function(a) {
                a.innerHTML = "<div class='hidden e'></div><div class='hidden'></div>";
                if (!a.getElementsByClassName || !a.getElementsByClassName("e").length) {
                    return false
                }
                a.lastChild.className = "e";
                return a.getElementsByClassName("e").length === 2
            }),
            Y3 = T3(function(a) {
                a.id = s3 + 0;
                a.innerHTML = "<a name='" + s3 + "'></a><div name='" + s3 + "'></div>";
                t3.insertBefore(a, t3.firstChild);
                var b = h.getElementsByName && h.getElementsByName(s3).length === 2 + h.getElementsByName(s3 + 0).length;
                l = !h.getElementById(s3);
                t3.removeChild(a);
                return b
            });
        try {
            v3.call(t3.childNodes, 0)[0].nodeType
        } catch (e) {
            v3 = function(i) {
                var a, b = [];
                for (;
                (a = this[i]); i++) {
                    b.push(a)
                }
                return b
            }
        }
        function Z3(a, b, i, j) {
            i = i || [];
            b = b || h;
            var k4, l4, m4, m, n4 = b.nodeType;
            if (n4 !== 1 && n4 !== 9) {
                return []
            }
            if (!a || typeof a !== "string") {
                return i
            }
            m4 = p(b);
            if (!m4 && !j) {
                if ((k4 = J.exec(a))) {
                    if ((m = k4[1])) {
                        if (n4 === 9) {
                            l4 = b.getElementById(m);
                            if (l4 && l4.parentNode) {
                                if (l4.id === m) {
                                    i.push(l4);
                                    return i
                                }
                            } else {
                                return i
                            }
                        } else {
                            if (b.ownerDocument && (l4 = b.ownerDocument.getElementById(m)) && r(b, l4) && l4.id === m) {
                                i.push(l4);
                                return i
                            }
                        }
                    } else if (k4[2]) {
                        w3.apply(i, v3.call(b.getElementsByTagName(a), 0));
                        return i
                    } else if ((m = k4[3]) && X3 && b.getElementsByClassName) {
                        w3.apply(i, v3.call(b.getElementsByClassName(m), 0));
                        return i
                    }
                }
            }
            return j4(a, b, i, j, m4)
        }
        Z3.matches = function(a, b) {
            return Z3(a, null, null, b)
        };
        Z3.matchesSelector = function(a, b) {
            return Z3(b, null, null, [a]).length > 0
        };

        function $3(a) {
            return function(b) {
                var i = b.nodeName.toLowerCase();
                return i === "input" && b.type === a
            }
        }
        function _3(a) {
            return function(b) {
                var i = b.nodeName.toLowerCase();
                return (i === "input" || i === "button") && b.type === a
            }
        }
        o = Z3.getText = function(a) {
            var b, j = "",
                i = 0,
                m = a.nodeType;
            if (m) {
                if (m === 1 || m === 9 || m === 11) {
                    if (typeof a.textContent === "string") {
                        return a.textContent
                    } else {
                        for (a = a.firstChild; a; a = a.nextSibling) {
                            j += o(a)
                        }
                    }
                } else if (m === 3 || m === 4) {
                    return a.nodeValue
                }
            } else {
                for (;
                (b = a[i]); i++) {
                    j += o(b)
                }
            }
            return j
        };
        p = Z3.isXML = function p(a) {
            var b = a && (a.ownerDocument || a).documentElement;
            return b ? b.nodeName !== "HTML" : false
        };
        r = Z3.contains = t3.contains ? function(a, b) {
            var i = a.nodeType === 9 ? a.documentElement : a,
                j = b && b.parentNode;
            return a === j || !! (j && j.nodeType === 1 && i.contains && i.contains(j))
        } : t3.compareDocumentPosition ? function(a, b) {
            return b && !! (a.compareDocumentPosition(b) & 16)
        } : function(a, b) {
            while ((b = b.parentNode)) {
                if (b === a) {
                    return true
                }
            }
            return false
        };
        Z3.attr = function(a, b) {
            var i, j = p(a);
            if (!j) {
                b = b.toLowerCase()
            }
            if (n.attrHandle[b]) {
                return n.attrHandle[b](a)
            }
            if (W3 || j) {
                return a.getAttribute(b)
            }
            i = a.getAttributeNode(b);
            return i ? typeof a[b] === "boolean" ? a[b] ? b : null : i.specified ? i.value : null : null
        };
        n = Z3.selectors = {
            cacheLength: 50,
            createPseudo: x3,
            match: S3,
            order: new RegExp("ID|TAG" + (Y3 ? "|NAME" : "") + (X3 ? "|CLASS" : "")),
            attrHandle: V3 ? {} : {
                "href": function(a) {
                    return a.getAttribute("href", 2)
                },
                "type": function(a) {
                    return a.getAttribute("type")
                }
            },
            find: {
                "ID": l ? function(i, a, b) {
                    if (typeof a.getElementById !== r3 && !b) {
                        var m = a.getElementById(i);
                        return m && m.parentNode ? [m] : []
                    }
                } : function(i, a, b) {
                    if (typeof a.getElementById !== r3 && !b) {
                        var m = a.getElementById(i);
                        return m ? m.id === i || typeof m.getAttributeNode !== r3 && m.getAttributeNode("id").value === i ? [m] : u : []
                    }
                },
                "TAG": U3 ? function(a, b) {
                    if (typeof b.getElementsByTagName !== r3) {
                        return b.getElementsByTagName(a)
                    }
                } : function(a, b) {
                    var j = b.getElementsByTagName(a);
                    if (a === "*") {
                        var m, k4 = [],
                            i = 0;
                        for (;
                        (m = j[i]); i++) {
                            if (m.nodeType === 1) {
                                k4.push(m)
                            }
                        }
                        return k4
                    }
                    return j
                },
                "NAME": function(a, b) {
                    if (typeof b.getElementsByName !== r3) {
                        return b.getElementsByName(name)
                    }
                },
                "CLASS": function(a, b, i) {
                    if (typeof b.getElementsByClassName !== r3 && !i) {
                        return b.getElementsByClassName(a)
                    }
                }
            },
            relative: {
                ">": {
                    dir: "parentNode",
                    first: true
                },
                " ": {
                    dir: "parentNode"
                },
                "+": {
                    dir: "previousSibling",
                    first: true
                },
                "~": {
                    dir: "previousSibling"
                }
            },
            preFilter: {
                "ATTR": function(m) {
                    m[1] = m[1].replace(R3, "");
                    m[3] = (m[4] || m[5] || "").replace(R3, "");
                    if (m[2] === "~=") {
                        m[3] = " " + m[3] + " "
                    }
                    return m.slice(0, 4)
                },
                "CHILD": function(m) {
                    m[1] = m[1].toLowerCase();
                    if (m[1] === "nth") {
                        if (!m[2]) {
                            Z3.error(m[0])
                        }
                        m[3] = +(m[3] ? m[4] + (m[5] || 1) : 2 * (m[2] === "even" || m[2] === "odd"));
                        m[4] = +((m[6] + m[7]) || m[2] === "odd")
                    } else if (m[2]) {
                        Z3.error(m[0])
                    }
                    return m
                },
                "PSEUDO": function(m, a, b) {
                    var i, j;
                    if (S3["CHILD"].test(m[0])) {
                        return null
                    }
                    if (m[3]) {
                        m[2] = m[3]
                    } else if ((i = m[4])) {
                        if (L3.test(i) && (j = b4(i, a, b, true)) && (j = i.indexOf(")", i.length - j) - i.length)) {
                            i = i.slice(0, j);
                            m[0] = m[0].slice(0, j)
                        }
                        m[2] = i
                    }
                    return m.slice(0, 3)
                }
            },
            filter: {
                "ID": l ? function(i) {
                    i = i.replace(R3, "");
                    return function(a) {
                        return a.getAttribute("id") === i
                    }
                } : function(i) {
                    i = i.replace(R3, "");
                    return function(a) {
                        var b = typeof a.getAttributeNode !== r3 && a.getAttributeNode("id");
                        return b && b.value === i
                    }
                },
                "TAG": function(a) {
                    if (a === "*") {
                        return function() {
                            return true
                        }
                    }
                    a = a.replace(R3, "").toLowerCase();
                    return function(b) {
                        return b.nodeName && b.nodeName.toLowerCase() === a
                    }
                },
                "CLASS": function(a) {
                    var b = z3[s3][a];
                    if (!b) {
                        b = z3(a, new RegExp("(^|" + C3 + ")" + a + "(" + C3 + "|$)"))
                    }
                    return function(i) {
                        return b.test(i.className || (typeof i.getAttribute !== r3 && i.getAttribute("class")) || "")
                    }
                },
                "ATTR": function(a, b, i) {
                    if (!b) {
                        return function(j) {
                            return Z3.attr(j, a) != null
                        }
                    }
                    return function(j) {
                        var m = Z3.attr(j, a),
                            k4 = m + "";
                        if (m == null) {
                            return b === "!="
                        }
                        switch (b) {
                            case "=":
                                return k4 === i;
                            case "!=":
                                return k4 !== i;
                            case "^=":
                                return i && k4.indexOf(i) === 0;
                            case "*=":
                                return i && k4.indexOf(i) > -1;
                            case "$=":
                                return i && k4.substr(k4.length - i.length) === i;
                            case "~=":
                                return (" " + k4 + " ").indexOf(i) > -1;
                            case "|=":
                                return k4 === i || k4.substr(0, i.length + 1) === i + "-"
                        }
                    }
                },
                "CHILD": function(a, b, i, j) {
                    if (a === "nth") {
                        var m = u3++;
                        return function(k4) {
                            var l4, m4, n4 = 0,
                                o4 = k4;
                            if (i === 1 && j === 0) {
                                return true
                            }
                            l4 = k4.parentNode;
                            if (l4 && (l4[s3] !== m || !k4.sizset)) {
                                for (o4 = l4.firstChild; o4; o4 = o4.nextSibling) {
                                    if (o4.nodeType === 1) {
                                        o4.sizset = ++n4;
                                        if (o4 === k4) {
                                            break
                                        }
                                    }
                                }
                                l4[s3] = m
                            }
                            m4 = k4.sizset - j;
                            if (i === 0) {
                                return m4 === 0
                            } else {
                                return (m4 % i === 0 && m4 / i >= 0)
                            }
                        }
                    }
                    return function(k4) {
                        var l4 = k4;
                        switch (a) {
                            case "only":
                            case "first":
                                while ((l4 = l4.previousSibling)) {
                                    if (l4.nodeType === 1) {
                                        return false
                                    }
                                }
                                if (a === "first") {
                                    return true
                                }
                                l4 = k4;
                            case "last":
                                while ((l4 = l4.nextSibling)) {
                                    if (l4.nodeType === 1) {
                                        return false
                                    }
                                }
                                return true
                        }
                    }
                },
                "PSEUDO": function(a, b, i, j) {
                    var m, fn = n.pseudos[a] || n.pseudos[a.toLowerCase()];
                    if (!fn) {
                        Z3.error("unsupported pseudo: " + a)
                    }
                    if (!fn[s3]) {
                        if (fn.length > 1) {
                            m = [a, a, "", b];
                            return function(k4) {
                                return fn(k4, 0, m)
                            }
                        }
                        return fn
                    }
                    return fn(b, i, j)
                }
            },
            pseudos: {
                "not": x3(function(a, b, i) {
                    var m = s(a.replace(I, "$1"), b, i);
                    return function(j) {
                        return !m(j)
                    }
                }),
                "enabled": function(a) {
                    return a.disabled === false
                },
                "disabled": function(a) {
                    return a.disabled === true
                },
                "checked": function(a) {
                    var b = a.nodeName.toLowerCase();
                    return (b === "input" && !! a.checked) || (b === "option" && !! a.selected)
                },
                "selected": function(a) {
                    if (a.parentNode) {
                        a.parentNode.selectedIndex
                    }
                    return a.selected === true
                },
                "parent": function(a) {
                    return !n.pseudos["empty"](a)
                },
                "empty": function(a) {
                    var b;
                    a = a.firstChild;
                    while (a) {
                        if (a.nodeName > "@" || (b = a.nodeType) === 3 || b === 4) {
                            return false
                        }
                        a = a.nextSibling
                    }
                    return true
                },
                "contains": x3(function(a) {
                    return function(b) {
                        return (b.textContent || b.innerText || o(b)).indexOf(a) > -1
                    }
                }),
                "has": x3(function(a) {
                    return function(b) {
                        return Z3(a, b).length > 0
                    }
                }),
                "header": function(a) {
                    return P3.test(a.nodeName)
                },
                "text": function(a) {
                    var b, i;
                    return a.nodeName.toLowerCase() === "input" && (b = a.type) === "text" && ((i = a.getAttribute("type")) == null || i.toLowerCase() === b)
                },
                "radio": $3("radio"),
                "checkbox": $3("checkbox"),
                "file": $3("file"),
                "password": $3("password"),
                "image": $3("image"),
                "submit": _3("submit"),
                "reset": _3("reset"),
                "button": function(a) {
                    var b = a.nodeName.toLowerCase();
                    return b === "input" && a.type === "button" || b === "button"
                },
                "input": function(a) {
                    return Q3.test(a.nodeName)
                },
                "focus": function(a) {
                    var b = a.ownerDocument;
                    return a === b.activeElement && (!b.hasFocus || b.hasFocus()) && !! (a.type || a.href)
                },
                "active": function(a) {
                    return a === a.ownerDocument.activeElement
                }
            },
            setFilters: {
                "first": function(a, b, i) {
                    return i ? a.slice(1) : [a[0]]
                },
                "last": function(a, b, i) {
                    var j = a.pop();
                    return i ? a : [j]
                },
                "even": function(a, b, j) {
                    var m = [],
                        i = j ? 1 : 0,
                        k4 = a.length;
                    for (; i < k4; i = i + 2) {
                        m.push(a[i])
                    }
                    return m
                },
                "odd": function(a, b, j) {
                    var m = [],
                        i = j ? 0 : 1,
                        k4 = a.length;
                    for (; i < k4; i = i + 2) {
                        m.push(a[i])
                    }
                    return m
                },
                "lt": function(a, b, i) {
                    return i ? a.slice(+b) : a.slice(0, +b)
                },
                "gt": function(a, b, i) {
                    return i ? a.slice(0, +b + 1) : a.slice(+b + 1)
                },
                "eq": function(a, b, i) {
                    var j = a.splice(+b, 1);
                    return i ? a : j
                }
            }
        };

        function a4(a, b, i) {
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
        t = t3.compareDocumentPosition ? function(a, b) {
            if (a === b) {
                v = true;
                return 0
            }
            return (!a.compareDocumentPosition || !b.compareDocumentPosition ? a.compareDocumentPosition : a.compareDocumentPosition(b) & 4) ? -1 : 1
        } : function(a, b) {
            if (a === b) {
                v = true;
                return 0
            } else if (a.sourceIndex && b.sourceIndex) {
                return a.sourceIndex - b.sourceIndex
            }
            var j, m, ap = [],
                bp = [],
                k4 = a.parentNode,
                l4 = b.parentNode,
                m4 = k4;
            if (k4 === l4) {
                return a4(a, b)
            } else if (!k4) {
                return -1
            } else if (!l4) {
                return 1
            }
            while (m4) {
                ap.unshift(m4);
                m4 = m4.parentNode
            }
            m4 = l4;
            while (m4) {
                bp.unshift(m4);
                m4 = m4.parentNode
            }
            j = ap.length;
            m = bp.length;
            for (var i = 0; i < j && i < m; i++) {
                if (ap[i] !== bp[i]) {
                    return a4(ap[i], bp[i])
                }
            }
            return i === j ? a4(a, bp[i], -1) : a4(ap[i], b, 1)
        };
        [0, 0].sort(t);
        _ = !v;
        Z3.uniqueSort = function(a) {
            var b, i = 1;
            v = _;
            a.sort(t);
            if (v) {
                for (;
                (b = a[i]); i++) {
                    if (b === a[i - 1]) {
                        a.splice(i--, 1)
                    }
                }
            }
            return a
        };
        Z3.error = function(m) {
            throw new Error("Syntax error, unrecognized expression: " + m)
        };

        function b4(a, b, j, m) {
            var k4, l4, m4, n4, o4, p4, q4, i, r4, s4, t4 = !j && b !== h,
                u4 = (t4 ? "<s>" : "") + a.replace(I, "$1<s>"),
                v4 = A3[s3][u4];
            if (v4) {
                return m ? 0 : v3.call(v4, 0)
            }
            o4 = a;
            p4 = [];
            i = 0;
            r4 = n.preFilter;
            s4 = n.filter;
            while (o4) {
                if (!k4 || (l4 = J3.exec(o4))) {
                    if (l4) {
                        o4 = o4.slice(l4[0].length);
                        m4.selector = q4
                    }
                    p4.push(m4 = []);
                    q4 = "";
                    if (t4) {
                        o4 = " " + o4
                    }
                }
                k4 = false;
                if ((l4 = K3.exec(o4))) {
                    q4 += l4[0];
                    o4 = o4.slice(l4[0].length);
                    k4 = m4.push({
                        part: l4.pop().replace(I, " "),
                        string: l4[0],
                        captures: l4
                    })
                }
                for (n4 in s4) {
                    if ((l4 = S3[n4].exec(o4)) && (!r4[n4] || (l4 = r4[n4](l4, b, j)))) {
                        q4 += l4[0];
                        o4 = o4.slice(l4[0].length);
                        k4 = m4.push({
                            part: n4,
                            string: l4.shift(),
                            captures: l4
                        })
                    }
                }
                if (!k4) {
                    break
                }
            }
            if (q4) {
                m4.selector = q4
            }
            return m ? o4.length : o4 ? Z3.error(a) : v3.call(A3(u4, p4), 0)
        }
        function c4(m, a, b, i) {
            var j = a.dir,
                k4 = u3++;
            if (!m) {
                m = function(l4) {
                    return l4 === b
                }
            }
            return a.first ? function(l4) {
                while ((l4 = l4[j])) {
                    if (l4.nodeType === 1) {
                        return m(l4) && l4
                    }
                }
            } : i ? function(l4) {
                while ((l4 = l4[j])) {
                    if (l4.nodeType === 1) {
                        if (m(l4)) {
                            return l4
                        }
                    }
                }
            } : function(l4) {
                var m4, n4 = k4 + "." + c,
                    o4 = n4 + "." + f;
                while ((l4 = l4[j])) {
                    if (l4.nodeType === 1) {
                        if ((m4 = l4[s3]) === o4) {
                            return l4.sizset
                        } else if (typeof m4 === "string" && m4.indexOf(n4) === 0) {
                            if (l4.sizset) {
                                return l4
                            }
                        } else {
                            l4[s3] = o4;
                            if (m(l4)) {
                                l4.sizset = true;
                                return l4
                            }
                            l4.sizset = false
                        }
                    }
                }
            }
        }
        function d4(a, b) {
            return a ? function(i) {
                var j = b(i);
                return j && a(j === true ? i : j)
            } : b
        }
        function e4(a, b, j) {
            var m, k4, i = 0;
            for (;
            (m = a[i]); i++) {
                if (n.relative[m.part]) {
                    k4 = c4(k4, n.relative[m.part], b, j)
                } else {
                    k4 = d4(k4, n.filter[m.part].apply(null, m.captures.concat(b, j)))
                }
            }
            return k4
        }
        function f4(m) {
            return function(a) {
                var b, j = 0;
                for (;
                (b = m[j]); j++) {
                    if (b(a)) {
                        return true
                    }
                }
                return false
            }
        }
        s = Z3.compile = function(a, b, j) {
            var m, i, k4, l4 = B3[s3][a];
            if (l4 && l4.context === b) {
                return l4
            }
            m = b4(a, b, j);
            for (i = 0, k4 = m.length; i < k4; i++) {
                m[i] = e4(m[i], b, j)
            }
            l4 = B3(a, f4(m));
            l4.context = b;
            l4.runs = l4.dirruns = 0;
            return l4
        };

        function g4(a, b, j, m) {
            var i = 0,
                k4 = b.length;
            for (; i < k4; i++) {
                Z3(a, b[i], j, m)
            }
        }
        function h4(a, b, i, j, m, k4) {
            var l4, fn = n.setFilters[b.toLowerCase()];
            if (!fn) {
                Z3.error(b)
            }
            if (a || !(l4 = m)) {
                g4(a || "*", j, (l4 = []), m)
            }
            return l4.length > 0 ? fn(l4, i, k4) : []
        }
        function i4(a, b, m, k4) {
            var l4, m4, j, n4, o4, p4, q4, r4, s4, t4, u4, v4, w4, i = 0,
                x4 = a.length,
                y4 = S3["POS"],
                z4 = new RegExp("^" + y4.source + "(?!" + C3 + ")", "i"),
                A4 = function() {
                    var i = 1,
                        x4 = arguments.length - 2;
                    for (; i < x4; i++) {
                        if (arguments[i] === u) {
                            s4[i] = u
                        }
                    }
                };
            for (; i < x4; i++) {
                l4 = a[i];
                m4 = "";
                r4 = k4;
                for (j = 0, n4 = l4.length; j < n4; j++) {
                    o4 = l4[j];
                    p4 = o4.string;
                    if (o4.part === "PSEUDO") {
                        y4.exec("");
                        q4 = 0;
                        while ((s4 = y4.exec(p4))) {
                            t4 = true;
                            u4 = y4.lastIndex = s4.index + s4[0].length;
                            if (u4 > q4) {
                                m4 += p4.slice(q4, s4.index);
                                q4 = u4;
                                v4 = [b];
                                if (K3.test(m4)) {
                                    if (r4) {
                                        v4 = r4
                                    }
                                    r4 = k4
                                }
                                if ((w4 = O3.test(m4))) {
                                    m4 = m4.slice(0, -5).replace(K3, "$&*");
                                    q4++
                                }
                                if (s4.length > 1) {
                                    s4[0].replace(z4, A4)
                                }
                                r4 = h4(m4, s4[1], s4[2], v4, r4, w4)
                            }
                            m4 = ""
                        }
                    }
                    if (!t4) {
                        m4 += p4
                    }
                    t4 = false
                }
                if (m4) {
                    if (K3.test(m4)) {
                        g4(m4, r4 || [b], m, k4)
                    } else {
                        Z3(m4, b, m, k4 ? k4.concat(r4) : r4)
                    }
                } else {
                    w3.apply(m, r4)
                }
            }
            return x4 === 1 ? m : Z3.uniqueSort(m)
        }
        function j4(a, b, j, m, k4) {
            a = a.replace(I, "$1");
            var l4, m4, n4, o4, i, p4, q4, r4, s4, t4, u4 = b4(a, b, k4),
                v4 = b.nodeType;
            if (S3["POS"].test(a)) {
                return i4(u4, b, j, m)
            }
            if (m) {
                l4 = v3.call(m, 0)
            } else if (u4.length === 1) {
                if ((p4 = v3.call(u4[0], 0)).length > 2 && (q4 = p4[0]).part === "ID" && v4 === 9 && !k4 && n.relative[p4[1].part]) {
                    b = n.find["ID"](q4.captures[0].replace(R3, ""), b, k4)[0];
                    if (!b) {
                        return j
                    }
                    a = a.slice(p4.shift().string.length)
                }
                s4 = ((u4 = N3.exec(p4[0].string)) && !u4.index && b.parentNode) || b;
                r4 = "";
                for (i = p4.length - 1; i >= 0; i--) {
                    q4 = p4[i];
                    t4 = q4.part;
                    r4 = q4.string + r4;
                    if (n.relative[t4]) {
                        break
                    }
                    if (n.order.test(t4)) {
                        l4 = n.find[t4](q4.captures[0].replace(R3, ""), s4, k4);
                        if (l4 == null) {
                            continue
                        } else {
                            a = a.slice(0, a.length - r4.length) + r4.replace(S3[t4], "");
                            if (!a) {
                                w3.apply(j, v3.call(l4, 0))
                            }
                            break
                        }
                    }
                }
            }
            if (a) {
                m4 = s(a, b, k4);
                c = m4.dirruns++;
                if (l4 == null) {
                    l4 = n.find["TAG"]("*", (N3.test(a) && b.parentNode) || b)
                }
                for (i = 0;
                (o4 = l4[i]); i++) {
                    f = m4.runs++;
                    if (m4(o4)) {
                        j.push(o4)
                    }
                }
            }
            return j
        }
        if (h.querySelectorAll) {
            (function() {
                var a, b = j4,
                    j = /'|\\/g,
                    m = /\=[\x20\t\r\n\f]*([^'"\]]*)[\x20\t\r\n\f]*\]/g,
                    k4 = [],
                    l4 = [":active"],
                    m4 = t3.matchesSelector || t3.mozMatchesSelector || t3.webkitMatchesSelector || t3.oMatchesSelector || t3.msMatchesSelector;
                T3(function(i) {
                    i.innerHTML = "<select><option selected=''></option></select>";
                    if (!i.querySelectorAll("[selected]").length) {
                        k4.push("\\[" + C3 + "*(?:checked|disabled|ismap|multiple|readonly|selected|value)")
                    }
                    if (!i.querySelectorAll(":checked").length) {
                        k4.push(":checked")
                    }
                });
                T3(function(i) {
                    i.innerHTML = "<p test=''></p>";
                    if (i.querySelectorAll("[test^='']").length) {
                        k4.push("[*^$]=" + C3 + "*(?:\"\"|'')")
                    }
                    i.innerHTML = "<input type='hidden'/>";
                    if (!i.querySelectorAll(":enabled").length) {
                        k4.push(":enabled", ":disabled")
                    }
                });
                k4 = k4.length && new RegExp(k4.join("|"));
                j4 = function(n4, o4, p4, q4, r4) {
                    if (!q4 && !r4 && (!k4 || !k4.test(n4))) {
                        if (o4.nodeType === 9) {
                            try {
                                w3.apply(p4, v3.call(o4.querySelectorAll(n4), 0));
                                return p4
                            } catch (s4) {}
                        } else if (o4.nodeType === 1 && o4.nodeName.toLowerCase() !== "object") {
                            var t4, i, u4, v4 = o4.getAttribute("id"),
                                w4 = v4 || s3,
                                x4 = N3.test(n4) && o4.parentNode || o4;
                            if (v4) {
                                w4 = w4.replace(j, "\\$&")
                            } else {
                                o4.setAttribute("id", w4)
                            }
                            t4 = b4(n4, o4, r4);
                            w4 = "[id='" + w4 + "']";
                            for (i = 0, u4 = t4.length; i < u4; i++) {
                                t4[i] = w4 + t4[i].selector
                            }
                            try {
                                w3.apply(p4, v3.call(x4.querySelectorAll(t4.join(",")), 0));
                                return p4
                            } catch (s4) {} finally {
                                if (!v4) {
                                    o4.removeAttribute("id")
                                }
                            }
                        }
                    }
                    return b(n4, o4, p4, q4, r4)
                };
                if (m4) {
                    T3(function(i) {
                        a = m4.call(i, "div");
                        try {
                            m4.call(i, "[test!='']:sizzle");
                            l4.push(S3["PSEUDO"].source, S3["POS"].source, "!=")
                        } catch (e) {}
                    });
                    l4 = new RegExp(l4.join("|"));
                    Z3.matchesSelector = function(i, n4) {
                        n4 = n4.replace(m, "='$1']");
                        if (!p(i) && !l4.test(n4) && (!k4 || !k4.test(n4))) {
                            try {
                                var o4 = m4.call(i, n4);
                                if (o4 || a || i.document && i.document.nodeType !== 11) {
                                    return o4
                                }
                            } catch (e) {}
                        }
                        return Z3(n4, null, null, [i]).length > 0
                    }
                }
            })()
        }
        n.setFilters["nth"] = n.setFilters["eq"];
        n.filters = n.pseudos;
        Z3.attr = Q.attr;
        Q.find = Z3;
        Q.expr = Z3.selectors;
        Q.expr[":"] = Q.expr.pseudos;
        Q.unique = Z3.uniqueSort;
        Q.text = Z3.getText;
        Q.isXMLDoc = Z3.isXML;
        Q.contains = Z3.contains
    })(w);
    var t1 = /Until$/,
        u1 = /^(?:parents|prev(?:Until|All))/,
        v1 = /^.[^:#\[\.,]*$/,
        w1 = Q.expr.match.needsContext,
        x1 = {
            children: true,
            contents: true,
            next: true,
            prev: true
        };
    Q.fn.extend({
        find: function(s) {
            var i, l, a, n, r, b, c = this;
            if (typeof s !== "string") {
                return Q(s).filter(function() {
                    for (i = 0, l = c.length; i < l; i++) {
                        if (Q.contains(c[i], this)) {
                            return true
                        }
                    }
                })
            }
            b = this.pushStack("", "find", s);
            for (i = 0, l = this.length; i < l; i++) {
                a = b.length;
                Q.find(s, this[i], b);
                if (i > 0) {
                    for (n = a; n < b.length; n++) {
                        for (r = 0; r < a; r++) {
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
            var i, a = Q(t, this),
                l = a.length;
            return this.filter(function() {
                for (i = 0; i < l; i++) {
                    if (Q.contains(this, a[i])) {
                        return true
                    }
                }
            })
        },
        not: function(s) {
            return this.pushStack(A1(this, s, false), "not", s)
        },
        filter: function(s) {
            return this.pushStack(A1(this, s, true), "filter", s)
        },
        is: function(s) {
            return !!s && (typeof s === "string" ? w1.test(s) ? Q(s, this.context).index(this[0]) >= 0 : Q.filter(s, this).length > 0 : this.filter(s).length > 0)
        },
        closest: function(s, c) {
            var a, i = 0,
                l = this.length,
                r = [],
                p = w1.test(s) || typeof s !== "string" ? Q(s, c || this.context) : 0;
            for (; i < l; i++) {
                a = this[i];
                while (a && a.ownerDocument && a !== c && a.nodeType !== 11) {
                    if (p ? p.index(a) > -1 : Q.find.matchesSelector(a, s)) {
                        r.push(a);
                        break
                    }
                    a = a.parentNode
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
            return this.pushStack(y1(a[0]) || y1(b[0]) ? b : Q.unique(b))
        },
        addBack: function(s) {
            return this.add(s == null ? this.prevObject : this.prevObject.filter(s))
        }
    });
    Q.fn.andSelf = Q.fn.addBack;

    function y1(n) {
        return !n || !n.parentNode || n.parentNode.nodeType === 11
    }
    function z1(c, a) {
        do {
            c = c[a]
        } while (c && c.nodeType !== 1);
        return c
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
            return z1(a, "nextSibling")
        },
        prev: function(a) {
            return z1(a, "previousSibling")
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
            return Q.sibling((a.parentNode || {}).firstChild, a)
        },
        children: function(a) {
            return Q.sibling(a.firstChild)
        },
        contents: function(a) {
            return Q.nodeName(a, "iframe") ? a.contentDocument || a.contentWindow.document : Q.merge([], a.childNodes)
        }
    }, function(n, f) {
        Q.fn[n] = function(a, s) {
            var r = Q.map(this, f, a);
            if (!t1.test(n)) {
                s = a
            }
            if (s && typeof s === "string") {
                r = Q.filter(s, r)
            }
            r = this.length > 1 && !x1[n] ? Q.unique(r) : r;
            if (this.length > 1 && u1.test(n)) {
                r = r.reverse()
            }
            return this.pushStack(r, n, A.call(arguments).join(","))
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

    function A1(a, b, c) {
        b = b || 0;
        if (Q.isFunction(b)) {
            return Q.grep(a, function(j, i) {
                var r = !! b.call(j, i, j);
                return r === c
            })
        } else if (b.nodeType) {
            return Q.grep(a, function(j, i) {
                return (j === b) === c
            })
        } else if (typeof b === "string") {
            var f = Q.grep(a, function(i) {
                return i.nodeType === 1
            });
            if (v1.test(b)) {
                return Q.filter(b, f, !c)
            } else {
                b = Q.filter(b, f)
            }
        }
        return Q.grep(a, function(j, i) {
            return (Q.inArray(j, b) >= 0) === c
        })
    }
    function B1(h) {
        var l = C1.split("|"),
            s = h.createDocumentFragment();
        if (s.createElement) {
            while (l.length) {
                s.createElement(l.pop())
            }
        }
        return s
    }
    var C1 = "abbr|article|aside|audio|bdi|canvas|data|datalist|details|figcaption|figure|footer|" + "header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",
        D1 = / jQuery\d+="(?:null|\d+)"/g,
        E1 = /^\s+/,
        F1 = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,
        G1 = /<([\w:]+)/,
        H1 = /<tbody/i,
        I1 = /<|&#?\w+;/,
        J1 = /<(?:script|style|link)/i,
        K1 = /<(?:script|object|embed|option|style)/i,
        L1 = new RegExp("<(?:" + C1 + ")[\\s/>]", "i"),
        M1 = /^(?:checkbox|radio)$/,
        N1 = /checked\s*(?:[^=]|=\s*.checked.)/i,
        O1 = /\/(java|ecma)script/i,
        P1 = /^\s*<!(?:\[CDATA\[|\-\-)|[\]\-]{2}>\s*$/g,
        Q1 = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            legend: [1, "<fieldset>", "</fieldset>"],
            thead: [1, "<table>", "</table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
            area: [1, "<map>", "</map>"],
            _default: [0, "", ""]
        }, R1 = B1(h),
        S1 = R1.appendChild(h.createElement("div"));
    Q1.optgroup = Q1.option;
    Q1.tbody = Q1.tfoot = Q1.colgroup = Q1.caption = Q1.thead;
    Q1.th = Q1.td;
    if (!Q.support.htmlSerialize) {
        Q1._default = [1, "X<div>", "</div>"]
    }
    Q.fn.extend({
        text: function(v) {
            return Q.access(this, function(v) {
                return v === u ? Q.text(this) : this.empty().append((this[0] && this[0].ownerDocument || h).createTextNode(v))
            }, null, v, arguments.length)
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
                if (this.nodeType === 1 || this.nodeType === 11) {
                    this.appendChild(a)
                }
            })
        },
        prepend: function() {
            return this.domManip(arguments, true, function(a) {
                if (this.nodeType === 1 || this.nodeType === 11) {
                    this.insertBefore(a, this.firstChild)
                }
            })
        },
        before: function() {
            if (!y1(this[0])) {
                return this.domManip(arguments, false, function(a) {
                    this.parentNode.insertBefore(a, this)
                })
            }
            if (arguments.length) {
                var s = Q.clean(arguments);
                return this.pushStack(Q.merge(s, this), "before", this.selector)
            }
        },
        after: function() {
            if (!y1(this[0])) {
                return this.domManip(arguments, false, function(a) {
                    this.parentNode.insertBefore(a, this.nextSibling)
                })
            }
            if (arguments.length) {
                var s = Q.clean(arguments);
                return this.pushStack(Q.merge(this, s), "after", this.selector)
            }
        },
        remove: function(s, a) {
            var b, i = 0;
            for (;
            (b = this[i]) != null; i++) {
                if (!s || Q.filter(s, [b]).length) {
                    if (!a && b.nodeType === 1) {
                        Q.cleanData(b.getElementsByTagName("*"));
                        Q.cleanData([b])
                    }
                    if (b.parentNode) {
                        b.parentNode.removeChild(b)
                    }
                }
            }
            return this
        },
        empty: function() {
            var a, i = 0;
            for (;
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
            return Q.access(this, function(v) {
                var a = this[0] || {}, i = 0,
                    l = this.length;
                if (v === u) {
                    return a.nodeType === 1 ? a.innerHTML.replace(D1, "") : u
                }
                if (typeof v === "string" && !J1.test(v) && (Q.support.htmlSerialize || !L1.test(v)) && (Q.support.leadingWhitespace || !E1.test(v)) && !Q1[(G1.exec(v) || ["", ""])[1].toLowerCase()]) {
                    v = v.replace(F1, "<$1></$2>");
                    try {
                        for (; i < l; i++) {
                            a = this[i] || {};
                            if (a.nodeType === 1) {
                                Q.cleanData(a.getElementsByTagName("*"));
                                a.innerHTML = v
                            }
                        }
                        a = 0
                    } catch (e) {}
                }
                if (a) {
                    this.empty().append(v)
                }
            }, null, v, arguments.length)
        },
        replaceWith: function(v) {
            if (!y1(this[0])) {
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
            }
            return this.length ? this.pushStack(Q(Q.isFunction(v) ? v() : v), "replaceWith", v) : this
        },
        detach: function(s) {
            return this.remove(s, true)
        },
        domManip: function(a, t, c) {
            a = [].concat.apply([], a);
            var r, f, b, n, i = 0,
                v = a[0],
                s = [],
                l = this.length;
            if (!Q.support.checkClone && l > 1 && typeof v === "string" && N1.test(v)) {
                return this.each(function() {
                    Q(this).domManip(a, t, c)
                })
            }
            if (Q.isFunction(v)) {
                return this.each(function(i) {
                    var j = Q(this);
                    a[0] = v.call(this, i, t ? j.html() : u);
                    j.domManip(a, t, c)
                })
            }
            if (this[0]) {
                r = Q.buildFragment(a, this, s);
                b = r.fragment;
                f = b.firstChild;
                if (b.childNodes.length === 1) {
                    b = f
                }
                if (f) {
                    t = t && Q.nodeName(f, "tr");
                    for (n = r.cacheable || l - 1; i < l; i++) {
                        c.call(t && Q.nodeName(this[i], "table") ? T1(this[i], "tbody") : this[i], i === n ? b : Q.clone(b, true, true))
                    }
                }
                b = f = null;
                if (s.length) {
                    Q.each(s, function(i, j) {
                        if (j.src) {
                            if (Q.ajax) {
                                Q.ajax({
                                    url: j.src,
                                    type: "GET",
                                    dataType: "script",
                                    async :false,
                                    global: false,
                                    "throws": true
                                })
                            } else {
                                Q.error("no ajax")
                            }
                        } else {
                            Q.globalEval((j.text || j.textContent || j.innerHTML || "").replace(P1, ""))
                        }
                        if (j.parentNode) {
                            j.parentNode.removeChild(j)
                        }
                    })
                }
            }
            return this
        }
    });

    function T1(a, t) {
        return a.getElementsByTagName(t)[0] || a.appendChild(a.ownerDocument.createElement(t))
    }
    function U1(s, a) {
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
                    Q.event.add(a, t, b[t][i])
                }
            }
        }
        if (c.data) {
            c.data = Q.extend({}, c.data)
        }
    }
    function V1(s, a) {
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
            if (a.parentNode) {
                a.outerHTML = s.outerHTML
            }
            if (Q.support.html5Clone && (s.innerHTML && !Q.trim(a.innerHTML))) {
                a.innerHTML = s.innerHTML
            }
        } else if (n === "input" && M1.test(s.type)) {
            a.defaultChecked = a.checked = s.checked;
            if (a.value !== s.value) {
                a.value = s.value
            }
        } else if (n === "option") {
            a.selected = s.defaultSelected
        } else if (n === "input" || n === "textarea") {
            a.defaultValue = s.defaultValue
        } else if (n === "script" && a.text !== s.text) {
            a.text = s.text
        }
        a.removeAttribute(Q.expando)
    }
    Q.buildFragment = function(a, c, s) {
        var f, b, i, j = a[0];
        c = c || h;
        c = !c.nodeType && c[0] || c;
        c = c.ownerDocument || c;
        if (a.length === 1 && typeof j === "string" && j.length < 512 && c === h && j.charAt(0) === "<" && !K1.test(j) && (Q.support.checkClone || !N1.test(j)) && (Q.support.html5Clone || !L1.test(j))) {
            b = true;
            f = Q.fragments[j];
            i = f !== u
        }
        if (!f) {
            f = c.createDocumentFragment();
            Q.clean(a, c, f, s);
            if (b) {
                Q.fragments[j] = i && f
            }
        }
        return {
            fragment: f,
            cacheable: b
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
            var a, i = 0,
                r = [],
                b = Q(s),
                l = b.length,
                p = this.length === 1 && this[0].parentNode;
            if ((p == null || p && p.nodeType === 11 && p.childNodes.length === 1) && l === 1) {
                b[o](this[0]);
                return this
            } else {
                for (; i < l; i++) {
                    a = (i > 0 ? this.clone(true) : this).get();
                    Q(b[i])[o](a);
                    r = r.concat(a)
                }
                return this.pushStack(r, n, b.selector)
            }
        }
    });

    function W1(a) {
        if (typeof a.getElementsByTagName !== "undefined") {
            return a.getElementsByTagName("*")
        } else if (typeof a.querySelectorAll !== "undefined") {
            return a.querySelectorAll("*")
        } else {
            return []
        }
    }
    function X1(a) {
        if (M1.test(a.type)) {
            a.defaultChecked = a.checked
        }
    }
    Q.extend({
        clone: function(a, b, c) {
            var s, f, i, j;
            if (Q.support.html5Clone || Q.isXMLDoc(a) || !L1.test("<" + a.nodeName + ">")) {
                j = a.cloneNode(true)
            } else {
                S1.innerHTML = a.outerHTML;
                S1.removeChild(j = S1.firstChild)
            }
            if ((!Q.support.noCloneEvent || !Q.support.noCloneChecked) && (a.nodeType === 1 || a.nodeType === 11) && !Q.isXMLDoc(a)) {
                V1(a, j);
                s = W1(a);
                f = W1(j);
                for (i = 0; s[i]; ++i) {
                    if (f[i]) {
                        V1(s[i], f[i])
                    }
                }
            }
            if (b) {
                U1(a, j);
                if (c) {
                    s = W1(a);
                    f = W1(j);
                    for (i = 0; s[i]; ++i) {
                        U1(s[i], f[i])
                    }
                }
            }
            s = f = null;
            return j
        },
        clean: function(a, c, f, s) {
            var i, j, b, t, l, m, n, o, p, r, v, _, r3 = c === h && R1,
                s3 = [];
            if (!c || typeof c.createDocumentFragment === "undefined") {
                c = h
            }
            for (i = 0;
            (b = a[i]) != null; i++) {
                if (typeof b === "number") {
                    b += ""
                }
                if (!b) {
                    continue
                }
                if (typeof b === "string") {
                    if (!I1.test(b)) {
                        b = c.createTextNode(b)
                    } else {
                        r3 = r3 || B1(c);
                        n = c.createElement("div");
                        r3.appendChild(n);
                        b = b.replace(F1, "<$1></$2>");
                        t = (G1.exec(b) || ["", ""])[1].toLowerCase();
                        l = Q1[t] || Q1._default;
                        m = l[0];
                        n.innerHTML = l[1] + b + l[2];
                        while (m--) {
                            n = n.lastChild
                        }
                        if (!Q.support.tbody) {
                            o = H1.test(b);
                            p = t === "table" && !o ? n.firstChild && n.firstChild.childNodes : l[1] === "<table>" && !o ? n.childNodes : [];
                            for (j = p.length - 1; j >= 0; --j) {
                                if (Q.nodeName(p[j], "tbody") && !p[j].childNodes.length) {
                                    p[j].parentNode.removeChild(p[j])
                                }
                            }
                        }
                        if (!Q.support.leadingWhitespace && E1.test(b)) {
                            n.insertBefore(c.createTextNode(E1.exec(b)[0]), n.firstChild)
                        }
                        b = n.childNodes;
                        n.parentNode.removeChild(n)
                    }
                }
                if (b.nodeType) {
                    s3.push(b)
                } else {
                    Q.merge(s3, b)
                }
            }
            if (n) {
                b = n = r3 = null
            }
            if (!Q.support.appendChecked) {
                for (i = 0;
                (b = s3[i]) != null; i++) {
                    if (Q.nodeName(b, "input")) {
                        X1(b)
                    } else if (typeof b.getElementsByTagName !== "undefined") {
                        Q.grep(b.getElementsByTagName("input"), X1)
                    }
                }
            }
            if (f) {
                v = function(b) {
                    if (!b.type || O1.test(b.type)) {
                        return s ? s.push(b.parentNode ? b.parentNode.removeChild(b) : b) : f.appendChild(b)
                    }
                };
                for (i = 0;
                (b = s3[i]) != null; i++) {
                    if (!(Q.nodeName(b, "script") && v(b))) {
                        f.appendChild(b);
                        if (typeof b.getElementsByTagName !== "undefined") {
                            _ = Q.grep(Q.merge([], b.getElementsByTagName("script")), v);
                            s3.splice.apply(s3, [i + 1, 0].concat(_));
                            i += _.length
                        }
                    }
                }
            }
            return s3
        },
        cleanData: function(a, b) {
            var c, f, j, t, i = 0,
                l = Q.expando,
                m = Q.cache,
                n = Q.support.deleteExpando,
                s = Q.event.special;
            for (;
            (j = a[i]) != null; i++) {
                if (b || Q.acceptData(j)) {
                    f = j[l];
                    c = f && m[f];
                    if (c) {
                        if (c.events) {
                            for (t in c.events) {
                                if (s[t]) {
                                    Q.event.remove(j, t)
                                } else {
                                    Q.removeEvent(j, t, c.handle)
                                }
                            }
                        }
                        if (m[f]) {
                            delete m[f];
                            if (n) {
                                delete j[l]
                            } else if (j.removeAttribute) {
                                j.removeAttribute(l)
                            } else {
                                j[l] = null
                            }
                            Q.deletedIds.push(f)
                        }
                    }
                }
            }
        }
    });
    (function() {
        var m, b;
        Q.uaMatch = function(a) {
            a = a.toLowerCase();
            var c = /(chrome)[ \/]([\w.]+)/.exec(a) || /(webkit)[ \/]([\w.]+)/.exec(a) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(a) || /(msie) ([\w.]+)/.exec(a) || a.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(a) || [];
            return {
                browser: c[1] || "",
                version: c[2] || "0"
            }
        };
        m = Q.uaMatch(q.userAgent);
        b = {};
        if (m.browser) {
            b[m.browser] = true;
            b.version = m.version
        }
        if (b.chrome) {
            b.webkit = true
        } else if (b.webkit) {
            b.safari = true
        }
        Q.browser = b;
        Q.sub = function() {
            function j(s, c) {
                return new j.fn.init(s, c)
            }
            Q.extend(true, j, this);
            j.superclass = this;
            j.fn = j.prototype = this();
            j.fn.constructor = j;
            j.sub = this.sub;
            j.fn.init = function init(s, c) {
                if (c && c instanceof Q && !(c instanceof j)) {
                    c = j(c)
                }
                return Q.fn.init.call(this, s, c, r)
            };
            j.fn.init.prototype = j.fn;
            var r = j(h);
            return j
        }
    })();
    var Y1, Z1, $1, _1 = /alpha\([^)]*\)/i,
        a2 = /opacity=([^)]*)/,
        b2 = /^(top|right|bottom|left)$/,
        c2 = /^(none|table(?!-c[ea]).+)/,
        d2 = /^margin/,
        e2 = new RegExp("^(" + F + ")(.*)$", "i"),
        f2 = new RegExp("^(" + F + ")(?!px)[a-z%]+$", "i"),
        g2 = new RegExp("^([-+])=(" + F + ")", "i"),
        h2 = {}, i2 = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        }, j2 = {
            letterSpacing: 0,
            fontWeight: 400
        }, k2 = ["Top", "Right", "Bottom", "Left"],
        l2 = ["Webkit", "O", "Moz", "ms"],
        m2 = Q.fn.toggle;

    function n2(s, n) {
        if (n in s) {
            return n
        }
        var c = n.charAt(0).toUpperCase() + n.slice(1),
            o = n,
            i = l2.length;
        while (i--) {
            n = l2[i] + c;
            if (n in s) {
                return n
            }
        }
        return o
    }
    function o2(a, b) {
        a = b || a;
        return Q.css(a, "display") === "none" || !Q.contains(a.ownerDocument, a)
    }
    function p2(a, s) {
        var b, c, v = [],
            i = 0,
            l = a.length;
        for (; i < l; i++) {
            b = a[i];
            if (!b.style) {
                continue
            }
            v[i] = Q._data(b, "olddisplay");
            if (s) {
                if (!v[i] && b.style.display === "none") {
                    b.style.display = ""
                }
                if (b.style.display === "" && o2(b)) {
                    v[i] = Q._data(b, "olddisplay", t2(b.nodeName))
                }
            } else {
                c = Y1(b, "display");
                if (!v[i] && c !== "none") {
                    Q._data(b, "olddisplay", c)
                }
            }
        }
        for (i = 0; i < l; i++) {
            b = a[i];
            if (!b.style) {
                continue
            }
            if (!s || b.style.display === "none" || b.style.display === "") {
                b.style.display = s ? v[i] || "" : "none"
            }
        }
        return a
    }
    Q.fn.extend({
        css: function(n, v) {
            return Q.access(this, function(a, n, v) {
                return v !== u ? Q.style(a, n, v) : Q.css(a, n)
            }, n, v, arguments.length > 1)
        },
        show: function() {
            return p2(this, true)
        },
        hide: function() {
            return p2(this)
        },
        toggle: function(s, f) {
            var b = typeof s === "boolean";
            if (Q.isFunction(s) && Q.isFunction(f)) {
                return m2.apply(this, arguments)
            }
            return this.each(function() {
                if (b ? s : o2(this)) {
                    Q(this).show()
                } else {
                    Q(this).hide()
                }
            })
        }
    });
    Q.extend({
        cssHooks: {
            opacity: {
                get: function(a, c) {
                    if (c) {
                        var r = Y1(a, "opacity");
                        return r === "" ? "1" : r
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
            var r, t, c, o = Q.camelCase(n),
                s = a.style;
            n = Q.cssProps[o] || (Q.cssProps[o] = n2(s, o));
            c = Q.cssHooks[n] || Q.cssHooks[o];
            if (v !== u) {
                t = typeof v;
                if (t === "string" && (r = g2.exec(v))) {
                    v = (r[1] + 1) * r[2] + parseFloat(Q.css(a, n));
                    t = "number"
                }
                if (v == null || t === "number" && isNaN(v)) {
                    return
                }
                if (t === "number" && !Q.cssNumber[o]) {
                    v += "px"
                }
                if (!c || !("set" in c) || (v = c.set(a, v, b)) !== u) {
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
        css: function(a, n, b, c) {
            var v, f, i, o = Q.camelCase(n);
            n = Q.cssProps[o] || (Q.cssProps[o] = n2(a.style, o));
            i = Q.cssHooks[n] || Q.cssHooks[o];
            if (i && "get" in i) {
                v = i.get(a, true, c)
            }
            if (v === u) {
                v = Y1(a, n)
            }
            if (v === "normal" && n in j2) {
                v = j2[n]
            }
            if (b || c !== u) {
                f = parseFloat(v);
                return b || Q.isNumeric(f) ? f || 0 : v
            }
            return v
        },
        swap: function(a, o, c) {
            var r, n, b = {};
            for (n in o) {
                b[n] = a.style[n];
                a.style[n] = o[n]
            }
            r = c.call(a);
            for (n in o) {
                a.style[n] = b[n]
            }
            return r
        }
    });
    if (w.getComputedStyle) {
        Y1 = function(a, n) {
            var r, b, m, c, f = w.getComputedStyle(a, null),
                s = a.style;
            if (f) {
                r = f[n];
                if (r === "" && !Q.contains(a.ownerDocument, a)) {
                    r = Q.style(a, n)
                }
                if (f2.test(r) && d2.test(n)) {
                    b = s.width;
                    m = s.minWidth;
                    c = s.maxWidth;
                    s.minWidth = s.maxWidth = s.width = r;
                    r = f.width;
                    s.width = b;
                    s.minWidth = m;
                    s.maxWidth = c
                }
            }
            return r
        }
    } else if (h.documentElement.currentStyle) {
        Y1 = function(a, n) {
            var l, r, b = a.currentStyle && a.currentStyle[n],
                s = a.style;
            if (b == null && s && s[n]) {
                b = s[n]
            }
            if (f2.test(b) && !b2.test(n)) {
                l = s.left;
                r = a.runtimeStyle && a.runtimeStyle.left;
                if (r) {
                    a.runtimeStyle.left = a.currentStyle.left
                }
                s.left = n === "fontSize" ? "1em" : b;
                b = s.pixelLeft + "px";
                s.left = l;
                if (r) {
                    a.runtimeStyle.left = r
                }
            }
            return b === "" ? "auto" : b
        }
    }
    function q2(a, v, s) {
        var m = e2.exec(v);
        return m ? Math.max(0, m[1] - (s || 0)) + (m[2] || "px") : v
    }
    function r2(a, n, b, c) {
        var i = b === (c ? "border" : "content") ? 4 : n === "width" ? 1 : 0,
            v = 0;
        for (; i < 4; i += 2) {
            if (b === "margin") {
                v += Q.css(a, b + k2[i], true)
            }
            if (c) {
                if (b === "content") {
                    v -= parseFloat(Y1(a, "padding" + k2[i])) || 0
                }
                if (b !== "margin") {
                    v -= parseFloat(Y1(a, "border" + k2[i] + "Width")) || 0
                }
            } else {
                v += parseFloat(Y1(a, "padding" + k2[i])) || 0;
                if (b !== "padding") {
                    v += parseFloat(Y1(a, "border" + k2[i] + "Width")) || 0
                }
            }
        }
        return v
    }
    function s2(a, n, b) {
        var v = n === "width" ? a.offsetWidth : a.offsetHeight,
            c = true,
            i = Q.support.boxSizing && Q.css(a, "boxSizing") === "border-box";
        if (v <= 0 || v == null) {
            v = Y1(a, n);
            if (v < 0 || v == null) {
                v = a.style[n]
            }
            if (f2.test(v)) {
                return v
            }
            c = i && (Q.support.boxSizingReliable || v === a.style[n]);
            v = parseFloat(v) || 0
        }
        return (v + r2(a, n, b || (i ? "border" : "content"), c)) + "px"
    }
    function t2(n) {
        if (h2[n]) {
            return h2[n]
        }
        var a = Q("<" + n + ">").appendTo(h.body),
            b = a.css("display");
        a.remove();
        if (b === "none" || b === "") {
            Z1 = h.body.appendChild(Z1 || Q.extend(h.createElement("iframe"), {
                frameBorder: 0,
                width: 0,
                height: 0
            }));
            if (!$1 || !Z1.createElement) {
                $1 = (Z1.contentWindow || Z1.contentDocument).document;
                $1.write("<!doctype html><html><body>");
                $1.close()
            }
            a = $1.body.appendChild($1.createElement(n));
            b = Y1(a, "display");
            h.body.removeChild(Z1)
        }
        h2[n] = b;
        return b
    }
    Q.each(["height", "width"], function(i, n) {
        Q.cssHooks[n] = {
            get: function(a, c, b) {
                if (c) {
                    if (a.offsetWidth === 0 && c2.test(Y1(a, "display"))) {
                        return Q.swap(a, i2, function() {
                            return s2(a, n, b)
                        })
                    } else {
                        return s2(a, n, b)
                    }
                }
            },
            set: function(a, v, b) {
                return q2(a, v, b ? r2(a, n, b, Q.support.boxSizing && Q.css(a, "boxSizing") === "border-box") : 0)
            }
        }
    });
    if (!Q.support.opacity) {
        Q.cssHooks.opacity = {
            get: function(a, c) {
                return a2.test((c && a.currentStyle ? a.currentStyle.filter : a.style.filter) || "") ? (0.01 * parseFloat(RegExp.$1)) + "" : c ? "1" : ""
            },
            set: function(a, v) {
                var s = a.style,
                    c = a.currentStyle,
                    o = Q.isNumeric(v) ? "alpha(opacity=" + v * 100 + ")" : "",
                    f = c && c.filter || s.filter || "";
                s.zoom = 1;
                if (v >= 1 && Q.trim(f.replace(_1, "")) === "" && s.removeAttribute) {
                    s.removeAttribute("filter");
                    if (c && !c.filter) {
                        return
                    }
                }
                s.filter = _1.test(f) ? f.replace(_1, o) : f + " " + o
            }
        }
    }
    Q(function() {
        if (!Q.support.reliableMarginRight) {
            Q.cssHooks.marginRight = {
                get: function(a, c) {
                    return Q.swap(a, {
                        "display": "inline-block"
                    }, function() {
                        if (c) {
                            return Y1(a, "marginRight")
                        }
                    })
                }
            }
        }
        if (!Q.support.pixelPosition && Q.fn.position) {
            Q.each(["top", "left"], function(i, p) {
                Q.cssHooks[p] = {
                    get: function(a, c) {
                        if (c) {
                            var r = Y1(a, p);
                            return f2.test(r) ? Q(a).position()[p] + "px" : r
                        }
                    }
                }
            })
        }
    });
    if (Q.expr && Q.expr.filters) {
        Q.expr.filters.hidden = function(a) {
            return (a.offsetWidth === 0 && a.offsetHeight === 0) || (!Q.support.reliableHiddenOffsets && ((a.style && a.style.display) || Y1(a, "display")) === "none")
        };
        Q.expr.filters.visible = function(a) {
            return !Q.expr.filters.hidden(a)
        }
    }
    Q.each({
        margin: "",
        padding: "",
        border: "Width"
    }, function(p, s) {
        Q.cssHooks[p + s] = {
            expand: function(v) {
                var i, a = typeof v === "string" ? v.split(" ") : [v],
                    b = {};
                for (i = 0; i < 4; i++) {
                    b[p + k2[i] + s] = a[i] || a[i - 2] || a[0]
                }
                return b
            }
        };
        if (!d2.test(p)) {
            Q.cssHooks[p + s].set = q2
        }
    });
    var u2 = /%20/g,
        v2 = /\[\]$/,
        w2 = /\r?\n/g,
        x2 = /^(?:color|date|datetime|datetime-local|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i,
        y2 = /^(?:select|textarea)/i;
    Q.fn.extend({
        serialize: function() {
            return Q.param(this.serializeArray())
        },
        serializeArray: function() {
            return this.map(function() {
                return this.elements ? Q.makeArray(this.elements) : this
            }).filter(function() {
                return this.name && !this.disabled && (this.checked || y2.test(this.nodeName) || x2.test(this.type))
            }).map(function(i, a) {
                var v = Q(this).val();
                return v == null ? null : Q.isArray(v) ? Q.map(v, function(v, i) {
                    return {
                        name: a.name,
                        value: v.replace(w2, "\r\n")
                    }
                }) : {
                    name: a.name,
                    value: v.replace(w2, "\r\n")
                }
            }).get()
        }
    });
    Q.param = function(a, t) {
        var p, s = [],
            b = function(c, v) {
                v = Q.isFunction(v) ? v() : (v == null ? "" : v);
                s[s.length] = encodeURIComponent(c) + "=" + encodeURIComponent(v)
            };
        if (t === u) {
            t = Q.ajaxSettings && Q.ajaxSettings.traditional
        }
        if (Q.isArray(a) || (a.jquery && !Q.isPlainObject(a))) {
            Q.each(a, function() {
                b(this.name, this.value)
            })
        } else {
            for (p in a) {
                z2(p, a[p], t, b)
            }
        }
        return s.join("&").replace(u2, "+")
    };

    function z2(p, o, t, a) {
        var n;
        if (Q.isArray(o)) {
            Q.each(o, function(i, v) {
                if (t || v2.test(p)) {
                    a(p, v)
                } else {
                    z2(p + "[" + (typeof v === "object" ? i : "") + "]", v, t, a)
                }
            })
        } else if (!t && Q.type(o) === "object") {
            for (n in o) {
                z2(p + "[" + n + "]", o[n], t, a)
            }
        } else {
            a(p, o)
        }
    }
    var A2, B2, C2 = /#.*$/,
        D2 = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg,
        E2 = /^(?:about|app|app\-storage|.+\-extension|file|res|widget):$/,
        F2 = /^(?:GET|HEAD)$/,
        G2 = /^\/\//,
        H2 = /\?/,
        I2 = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
        J2 = /([?&])_=[^&]*/,
        K2 = /^([\w\+\.\-]+:)(?:\/\/([^\/?#:]*)(?::(\d+)|)|)/,
        L2 = Q.fn.load,
        M2 = {}, N2 = {}, O2 = ["*/"] + ["*"];
    try {
        A2 = k.href
    } catch (e) {
        A2 = h.createElement("a");
        A2.href = "";
        A2 = A2.href
    }
    B2 = K2.exec(A2.toLowerCase()) || [];

    function P2(s) {
        return function(a, f) {
            if (typeof a !== "string") {
                f = a;
                a = "*"
            }
            var b, l, p, c = a.toLowerCase().split(H),
                i = 0,
                j = c.length;
            if (Q.isFunction(f)) {
                for (; i < j; i++) {
                    b = c[i];
                    p = /^\+/.test(b);
                    if (p) {
                        b = b.substr(1) || "*"
                    }
                    l = s[b] = s[b] || [];
                    l[p ? "unshift" : "push"](f)
                }
            }
        }
    }
    function Q2(s, o, a, j, b, c) {
        b = b || o.dataTypes[0];
        c = c || {};
        c[b] = true;
        var f, l = s[b],
            i = 0,
            m = l ? l.length : 0,
            n = (s === M2);
        for (; i < m && (n || !f); i++) {
            f = l[i](o, a, j);
            if (typeof f === "string") {
                if (!n || c[f]) {
                    f = u
                } else {
                    o.dataTypes.unshift(f);
                    f = Q2(s, o, a, j, f, c)
                }
            }
        }
        if ((n || !f) && !c["*"]) {
            f = Q2(s, o, a, j, "*", c)
        }
        return f
    }
    function R2(t, s) {
        var a, b, f = Q.ajaxSettings.flatOptions || {};
        for (a in s) {
            if (s[a] !== u) {
                (f[a] ? t : (b || (b = {})))[a] = s[a]
            }
        }
        if (b) {
            Q.extend(true, t, b)
        }
    }
    Q.fn.load = function(a, p, c) {
        if (typeof a !== "string" && L2) {
            return L2.apply(this, arguments)
        }
        if (!this.length) {
            return this
        }
        var s, t, r, b = this,
            o = a.indexOf(" ");
        if (o >= 0) {
            s = a.slice(o, a.length);
            a = a.slice(0, o)
        }
        if (Q.isFunction(p)) {
            c = p;
            p = u
        } else if (p && typeof p === "object") {
            t = "POST"
        }
        Q.ajax({
            url: a,
            type: t,
            dataType: "html",
            data: p,
            complete: function(j, f) {
                if (c) {
                    b.each(c, r || [j.responseText, f, j])
                }
            }
        }).done(function(f) {
            r = arguments;
            b.html(s ? Q("<div>").append(f.replace(I2, "")).find(s) : f)
        });
        return this
    };
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
                R2(t, Q.ajaxSettings)
            } else {
                s = t;
                t = Q.ajaxSettings
            }
            R2(t, s);
            return t
        },
        ajaxSettings: {
            url: A2,
            isLocal: E2.test(B2[1]),
            global: true,
            type: "GET",
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            processData: true,
            async :true,
            accepts: {
                xml: "application/xml, text/xml",
                html: "text/html",
                text: "text/plain",
                json: "application/json, text/javascript",
                "*": O2
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
        ajaxPrefilter: P2(M2),
        ajaxTransport: P2(N2),
        ajax: function(a, o) {
            if (typeof a === "object") {
                o = a;
                a = u
            }
            o = o || {};
            var b, r, c, t, f, p, j, i, s = Q.ajaxSetup({}, o),
                l = s.context || s,
                m = l !== s && (l.nodeType || l instanceof Q) ? Q(l) : Q.event,
                n = Q.Deferred(),
                v = Q.Callbacks("once memory"),
                _ = s.statusCode || {}, r3 = {}, s3 = {}, t3 = 0,
                u3 = "canceled",
                v3 = {
                    readyState: 0,
                    setRequestHeader: function(x3, z3) {
                        if (!t3) {
                            var A3 = x3.toLowerCase();
                            x3 = s3[A3] = s3[A3] || x3;
                            r3[x3] = z3
                        }
                        return this
                    },
                    getAllResponseHeaders: function() {
                        return t3 === 2 ? r : null
                    },
                    getResponseHeader: function(x3) {
                        var z3;
                        if (t3 === 2) {
                            if (!c) {
                                c = {};
                                while ((z3 = D2.exec(r))) {
                                    c[z3[1].toLowerCase()] = z3[2]
                                }
                            }
                            z3 = c[x3.toLowerCase()]
                        }
                        return z3 === u ? null : z3
                    },
                    overrideMimeType: function(x3) {
                        if (!t3) {
                            s.mimeType = x3
                        }
                        return this
                    },
                    abort: function(x3) {
                        x3 = x3 || u3;
                        if (t) {
                            t.abort(x3)
                        }
                        w3(0, x3);
                        return this
                    }
                };

            function w3(x3, z3, A3, B3) {
                var C3, D3, E3, F3, G3, H3 = z3;
                if (t3 === 2) {
                    return
                }
                t3 = 2;
                if (f) {
                    clearTimeout(f)
                }
                t = u;
                r = B3 || "";
                v3.readyState = x3 > 0 ? 4 : 0;
                if (A3) {
                    F3 = S2(s, v3, A3)
                }
                if (x3 >= 200 && x3 < 300 || x3 === 304) {
                    if (s.ifModified) {
                        G3 = v3.getResponseHeader("Last-Modified");
                        if (G3) {
                            Q.lastModified[b] = G3
                        }
                        G3 = v3.getResponseHeader("Etag");
                        if (G3) {
                            Q.etag[b] = G3
                        }
                    }
                    if (x3 === 304) {
                        H3 = "notmodified";
                        C3 = true
                    } else {
                        C3 = T2(s, F3);
                        H3 = C3.state;
                        D3 = C3.data;
                        E3 = C3.error;
                        C3 = !E3
                    }
                } else {
                    E3 = H3;
                    if (!H3 || x3) {
                        H3 = "error";
                        if (x3 < 0) {
                            x3 = 0
                        }
                    }
                }
                v3.status = x3;
                v3.statusText = "" + (z3 || H3);
                if (C3) {
                    n.resolveWith(l, [D3, H3, v3])
                } else {
                    n.rejectWith(l, [v3, H3, E3])
                }
                v3.statusCode(_);
                _ = u;
                if (j) {
                    m.trigger("ajax" + (C3 ? "Success" : "Error"), [v3, s, C3 ? D3 : E3])
                }
                v.fireWith(l, [v3, H3]);
                if (j) {
                    m.trigger("ajaxComplete", [v3, s]);
                    if (!(--Q.active)) {
                        Q.event.trigger("ajaxStop")
                    }
                }
            }
            n.promise(v3);
            v3.success = v3.done;
            v3.error = v3.fail;
            v3.complete = v.add;
            v3.statusCode = function(x3) {
                if (x3) {
                    var z3;
                    if (t3 < 2) {
                        for (z3 in x3) {
                            _[z3] = [_[z3], x3[z3]]
                        }
                    } else {
                        z3 = x3[v3.status];
                        v3.always(z3)
                    }
                }
                return this
            };
            s.url = ((a || s.url) + "").replace(C2, "").replace(G2, B2[1] + "//");
            s.dataTypes = Q.trim(s.dataType || "*").toLowerCase().split(H);
            if (s.crossDomain == null) {
                p = K2.exec(s.url.toLowerCase());
                s.crossDomain = !! (p && (p[1] != B2[1] || p[2] != B2[2] || (p[3] || (p[1] === "http:" ? 80 : 443)) != (B2[3] || (B2[1] === "http:" ? 80 : 443))))
            }
            if (s.data && s.processData && typeof s.data !== "string") {
                s.data = Q.param(s.data, s.traditional)
            }
            Q2(M2, s, o, v3);
            if (t3 === 2) {
                return v3
            }
            j = s.global;
            s.type = s.type.toUpperCase();
            s.hasContent = !F2.test(s.type);
            if (j && Q.active++ === 0) {
                Q.event.trigger("ajaxStart")
            }
            if (!s.hasContent) {
                if (s.data) {
                    s.url += (H2.test(s.url) ? "&" : "?") + s.data;
                    delete s.data
                }
                b = s.url;
                if (s.cache === false) {
                    var ts = Q.now(),
                        y3 = s.url.replace(J2, "$1_=" + ts);
                    s.url = y3 + ((y3 === s.url) ? (H2.test(s.url) ? "&" : "?") + "_=" + ts : "")
                }
            }
            if (s.data && s.hasContent && s.contentType !== false || o.contentType) {
                v3.setRequestHeader("Content-Type", s.contentType)
            }
            if (s.ifModified) {
                b = b || s.url;
                if (Q.lastModified[b]) {
                    v3.setRequestHeader("If-Modified-Since", Q.lastModified[b])
                }
                if (Q.etag[b]) {
                    v3.setRequestHeader("If-None-Match", Q.etag[b])
                }
            }
            v3.setRequestHeader("Accept", s.dataTypes[0] && s.accepts[s.dataTypes[0]] ? s.accepts[s.dataTypes[0]] + (s.dataTypes[0] !== "*" ? ", " + O2 + "; q=0.01" : "") : s.accepts["*"]);
            for (i in s.headers) {
                v3.setRequestHeader(i, s.headers[i])
            }
            if (s.beforeSend && (s.beforeSend.call(l, v3, s) === false || t3 === 2)) {
                return v3.abort()
            }
            u3 = "abort";
            for (i in {
                success: 1,
                error: 1,
                complete: 1
            }) {
                v3[i](s[i])
            }
            t = Q2(N2, s, o, v3);
            if (!t) {
                w3(-1, "No Transport")
            } else {
                v3.readyState = 1;
                if (j) {
                    m.trigger("ajaxSend", [v3, s])
                }
                if (s.async &&s.timeout > 0) {
                    f = setTimeout(function() {
                        v3.abort("timeout")
                    }, s.timeout)
                }
                try {
                    t3 = 1;
                    t.send(r3, w3)
                } catch (e) {
                    if (t3 < 2) {
                        w3(-1, e)
                    } else {
                        throw e
                    }
                }
            }
            return v3
        },
        active: 0,
        lastModified: {},
        etag: {}
    });

    function S2(s, j, r) {
        var c, t, f, a, b = s.contents,
            i = s.dataTypes,
            l = s.responseFields;
        for (t in l) {
            if (t in r) {
                j[l[t]] = r[t]
            }
        }
        while (i[0] === "*") {
            i.shift();
            if (c === u) {
                c = s.mimeType || j.getResponseHeader("content-type")
            }
        }
        if (c) {
            for (t in b) {
                if (b[t] && b[t].test(c)) {
                    i.unshift(t);
                    break
                }
            }
        }
        if (i[0] in r) {
            f = i[0]
        } else {
            for (t in r) {
                if (!i[0] || s.converters[t + " " + i[0]]) {
                    f = t;
                    break
                }
                if (!a) {
                    a = t
                }
            }
            f = f || a
        }
        if (f) {
            if (f !== i[0]) {
                i.unshift(f)
            }
            return r[f]
        }
    }
    function T2(s, r) {
        var c, a, b, t, f = s.dataTypes.slice(),
            p = f[0],
            j = {}, i = 0;
        if (s.dataFilter) {
            r = s.dataFilter(r, s.dataType)
        }
        if (f[1]) {
            for (c in s.converters) {
                j[c.toLowerCase()] = s.converters[c]
            }
        }
        for (;
        (b = f[++i]);) {
            if (b !== "*") {
                if (p !== "*" && p !== b) {
                    c = j[p + " " + b] || j["* " + b];
                    if (!c) {
                        for (a in j) {
                            t = a.split(" ");
                            if (t[1] === b) {
                                c = j[p + " " + t[0]] || j["* " + t[0]];
                                if (c) {
                                    if (c === true) {
                                        c = j[a]
                                    } else if (j[a] !== true) {
                                        b = t[0];
                                        f.splice(i--, 0, b)
                                    }
                                    break
                                }
                            }
                        }
                    }
                    if (c !== true) {
                        if (c && s["throws"]) {
                            r = c(r)
                        } else {
                            try {
                                r = c(r)
                            } catch (e) {
                                return {
                                    state: "parsererror",
                                    error: c ? e : "No conversion from " + p + " to " + b
                                }
                            }
                        }
                    }
                }
                p = b
            }
        }
        return {
            state: "success",
            data: r
        }
    }
    var U2 = [],
        V2 = /\?/,
        W2 = /(=)\?(?=&|$)|\?\?/,
        X2 = Q.now();
    Q.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function() {
            var c = U2.pop() || (Q.expando + "_" + (X2++));
            this[c] = true;
            return c
        }
    });
    Q.ajaxPrefilter("json jsonp", function(s, o, j) {
        var c, a, r, b = s.data,
            f = s.url,
            i = s.jsonp !== false,
            l = i && W2.test(f),
            m = i && !l && typeof b === "string" && !(s.contentType || "").indexOf("application/x-www-form-urlencoded") && W2.test(b);
        if (s.dataTypes[0] === "jsonp" || l || m) {
            c = s.jsonpCallback = Q.isFunction(s.jsonpCallback) ? s.jsonpCallback() : s.jsonpCallback;
            a = w[c];
            if (l) {
                s.url = f.replace(W2, "$1" + c)
            } else if (m) {
                s.data = b.replace(W2, "$1" + c)
            } else if (i) {
                s.url += (V2.test(f) ? "&" : "?") + s.jsonp + "=" + c
            }
            s.converters["script json"] = function() {
                if (!r) {
                    Q.error(c + " was not called")
                }
                return r[0]
            };
            s.dataTypes[0] = "json";
            w[c] = function() {
                r = arguments
            };
            j.always(function() {
                w[c] = a;
                if (s[c]) {
                    s.jsonpCallback = o.jsonpCallback;
                    U2.push(c)
                }
                if (r && Q.isFunction(a)) {
                    a(r[0])
                }
                r = a = u
            });
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
            var a, b = h.head || h.getElementsByTagName("head")[0] || h.documentElement;
            return {
                send: function(_, c) {
                    a = h.createElement("script");
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
    var Y2, Z2 = w.ActiveXObject ? function() {
            for (var a in Y2) {
                Y2[a](0, 1)
            }
        } : false,
        $2 = 0;

    function _2() {
        try {
            return new w.XMLHttpRequest()
        } catch (e) {}
    }
    function a3() {
        try {
            return new w.ActiveXObject("Microsoft.XMLHTTP")
        } catch (e) {}
    }
    Q.ajaxSettings.xhr = w.ActiveXObject ? function() {
        return !this.isLocal && _2() || a3()
    } : _2;
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
                        var f, i, j = s.xhr();
                        if (s.username) {
                            j.open(s.type, s.url, s.async, s.username, s.password)
                        } else {
                            j.open(s.type, s.url, s.async)
                        }
                        if (s.xhrFields) {
                            for (i in s.xhrFields) {
                                j[i] = s.xhrFields[i]
                            }
                        }
                        if (s.mimeType && j.overrideMimeType) {
                            j.overrideMimeType(s.mimeType)
                        }
                        if (!s.crossDomain && !a["X-Requested-With"]) {
                            a["X-Requested-With"] = "XMLHttpRequest"
                        }
                        try {
                            for (i in a) {
                                j.setRequestHeader(i, a[i])
                            }
                        } catch (_) {}
                        j.send((s.hasContent && s.data) || null);
                        c = function(_, l) {
                            var m, n, r, o, p;
                            try {
                                if (c && (l || j.readyState === 4)) {
                                    c = u;
                                    if (f) {
                                        j.onreadystatechange = Q.noop;
                                        if (Z2) {
                                            delete Y2[f]
                                        }
                                    }
                                    if (l) {
                                        if (j.readyState !== 4) {
                                            j.abort()
                                        }
                                    } else {
                                        m = j.status;
                                        r = j.getAllResponseHeaders();
                                        o = {};
                                        p = j.responseXML;
                                        if (p && p.documentElement) {
                                            o.xml = p
                                        }
                                        try {
                                            o.text = j.responseText
                                        } catch (_) {}
                                        try {
                                            n = j.statusText
                                        } catch (e) {
                                            n = ""
                                        }
                                        if (!m && s.isLocal && !s.crossDomain) {
                                            m = o.text ? 200 : 404
                                        } else if (m === 1223) {
                                            m = 204
                                        }
                                    }
                                }
                            } catch (t) {
                                if (!l) {
                                    b(-1, t)
                                }
                            }
                            if (o) {
                                b(m, n, o, r)
                            }
                        };
                        if (!s.async) {
                            c()
                        } else if (j.readyState === 4) {
                            setTimeout(c, 0)
                        } else {
                            f = ++$2;
                            if (Z2) {
                                if (!Y2) {
                                    Y2 = {};
                                    Q(w).unload(Z2)
                                }
                                Y2[f] = c
                            }
                            j.onreadystatechange = c
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
    var b3, c3, d3 = /^(?:toggle|show|hide)$/,
        e3 = new RegExp("^(?:([-+])=|)(" + F + ")([a-z%]*)$", "i"),
        f3 = /queueHooks$/,
        g3 = [m3],
        h3 = {
            "*": [function(p, v) {
                var a, b, c, t = this.createTween(p, v),
                    f = e3.exec(v),
                    i = t.cur(),
                    s = +i || 0,
                    j = 1;
                if (f) {
                    a = +f[2];
                    b = f[3] || (Q.cssNumber[p] ? "" : "px");
                    if (b !== "px" && s) {
                        s = Q.css(t.elem, p, true) || a || 1;
                        do {
                            c = j = j || ".5";
                            s = s / j;
                            Q.style(t.elem, p, s + b);
                            j = t.cur() / i
                        } while (j !== 1 && j !== c)
                    }
                    t.unit = b;
                    t.start = s;
                    t.end = f[1] ? s + (f[1] + 1) * a : a
                }
                return t
            }]
        };

    function i3() {
        setTimeout(function() {
            b3 = u
        }, 0);
        return (b3 = Q.now())
    }
    function j3(a, p) {
        Q.each(p, function(b, v) {
            var c = (h3[b] || []).concat(h3["*"]),
                i = 0,
                l = c.length;
            for (; i < l; i++) {
                if (c[i].call(a, b, v)) {
                    return
                }
            }
        })
    }
    function k3(a, p, o) {
        var r, i = 0,
            t = 0,
            l = g3.length,
            b = Q.Deferred().always(function() {
                delete c.elem
            }),
            c = function() {
                var m = b3 || i3(),
                    n = Math.max(0, f.startTime + f.duration - m),
                    s = 1 - (n / f.duration || 0),
                    i = 0,
                    l = f.tweens.length;
                for (; i < l; i++) {
                    f.tweens[i].run(s)
                }
                b.notifyWith(a, [f, s, n]);
                if (s < 1 && l) {
                    return n
                } else {
                    b.resolveWith(a, [f]);
                    return false
                }
            }, f = b.promise({
                elem: a,
                props: Q.extend({}, p),
                opts: Q.extend(true, {
                    specialEasing: {}
                }, o),
                originalProperties: p,
                originalOptions: o,
                startTime: b3 || i3(),
                duration: o.duration,
                tweens: [],
                createTween: function(m, n, s) {
                    var v = Q.Tween(a, f.opts, m, n, f.opts.specialEasing[m] || f.opts.easing);
                    f.tweens.push(v);
                    return v
                },
                stop: function(m) {
                    var i = 0,
                        l = m ? f.tweens.length : 0;
                    for (; i < l; i++) {
                        f.tweens[i].run(1)
                    }
                    if (m) {
                        b.resolveWith(a, [f, m])
                    } else {
                        b.rejectWith(a, [f, m])
                    }
                    return this
                }
            }),
            j = f.props;
        l3(j, f.opts.specialEasing);
        for (; i < l; i++) {
            r = g3[i].call(f, a, j, f.opts);
            if (r) {
                return r
            }
        }
        j3(f, j);
        if (Q.isFunction(f.opts.start)) {
            f.opts.start.call(a, f)
        }
        Q.fx.timer(Q.extend(c, {
            anim: f,
            queue: f.opts.queue,
            elem: a
        }));
        return f.progress(f.opts.progress).done(f.opts.done, f.opts.complete).fail(f.opts.fail).always(f.opts.always)
    }
    function l3(p, s) {
        var i, n, a, v, b;
        for (i in p) {
            n = Q.camelCase(i);
            a = s[n];
            v = p[i];
            if (Q.isArray(v)) {
                a = v[1];
                v = p[i] = v[0]
            }
            if (i !== n) {
                p[n] = v;
                delete p[i]
            }
            b = Q.cssHooks[n];
            if (b && "expand" in b) {
                v = b.expand(v);
                delete p[n];
                for (i in v) {
                    if (!(i in p)) {
                        p[i] = v[i];
                        s[i] = a
                    }
                }
            } else {
                s[n] = a
            }
        }
    }
    Q.Animation = Q.extend(k3, {
        tweener: function(p, c) {
            if (Q.isFunction(p)) {
                c = p;
                p = ["*"]
            } else {
                p = p.split(" ")
            }
            var a, i = 0,
                l = p.length;
            for (; i < l; i++) {
                a = p[i];
                h3[a] = h3[a] || [];
                h3[a].unshift(c)
            }
        },
        prefilter: function(c, p) {
            if (p) {
                g3.unshift(c)
            } else {
                g3.push(c)
            }
        }
    });

    function m3(a, p, o) {
        var i, b, v, l, c, t, f, j, m = this,
            s = a.style,
            n = {}, r = [],
            _ = a.nodeType && o2(a);
        if (!o.queue) {
            f = Q._queueHooks(a, "fx");
            if (f.unqueued == null) {
                f.unqueued = 0;
                j = f.empty.fire;
                f.empty.fire = function() {
                    if (!f.unqueued) {
                        j()
                    }
                }
            }
            f.unqueued++;
            m.always(function() {
                m.always(function() {
                    f.unqueued--;
                    if (!Q.queue(a, "fx").length) {
                        f.empty.fire()
                    }
                })
            })
        }
        if (a.nodeType === 1 && ("height" in p || "width" in p)) {
            o.overflow = [s.overflow, s.overflowX, s.overflowY];
            if (Q.css(a, "display") === "inline" && Q.css(a, "float") === "none") {
                if (!Q.support.inlineBlockNeedsLayout || t2(a.nodeName) === "inline") {
                    s.display = "inline-block"
                } else {
                    s.zoom = 1
                }
            }
        }
        if (o.overflow) {
            s.overflow = "hidden";
            if (!Q.support.shrinkWrapBlocks) {
                m.done(function() {
                    s.overflow = o.overflow[0];
                    s.overflowX = o.overflow[1];
                    s.overflowY = o.overflow[2]
                })
            }
        }
        for (i in p) {
            v = p[i];
            if (d3.exec(v)) {
                delete p[i];
                if (v === (_ ? "hide" : "show")) {
                    continue
                }
                r.push(i)
            }
        }
        l = r.length;
        if (l) {
            c = Q._data(a, "fxshow") || Q._data(a, "fxshow", {});
            if (_) {
                Q(a).show()
            } else {
                m.done(function() {
                    Q(a).hide()
                })
            }
            m.done(function() {
                var b;
                Q.removeData(a, "fxshow", true);
                for (b in n) {
                    Q.style(a, b, n[b])
                }
            });
            for (i = 0; i < l; i++) {
                b = r[i];
                t = m.createTween(b, _ ? c[b] : 0);
                n[b] = c[b] || Q.style(a, b);
                if (!(b in c)) {
                    c[b] = t.start;
                    if (_) {
                        t.end = t.start;
                        t.start = b === "width" || b === "height" ? 1 : 0
                    }
                }
            }
        }
    }
    function n3(a, o, p, b, c) {
        return new n3.prototype.init(a, o, p, b, c)
    }
    Q.Tween = n3;
    n3.prototype = {
        constructor: n3,
        init: function(a, o, p, b, c, f) {
            this.elem = a;
            this.prop = p;
            this.easing = c || "swing";
            this.options = o;
            this.start = this.now = this.cur();
            this.end = b;
            this.unit = f || (Q.cssNumber[p] ? "" : "px")
        },
        cur: function() {
            var a = n3.propHooks[this.prop];
            return a && a.get ? a.get(this) : n3.propHooks._default.get(this)
        },
        run: function(p) {
            var a, b = n3.propHooks[this.prop];
            if (this.options.duration) {
                this.pos = a = Q.easing[this.easing](p, this.options.duration * p, 0, 1, this.options.duration)
            } else {
                this.pos = a = p
            }
            this.now = (this.end - this.start) * a + this.start;
            if (this.options.step) {
                this.options.step.call(this.elem, this.now, this)
            }
            if (b && b.set) {
                b.set(this)
            } else {
                n3.propHooks._default.set(this)
            }
            return this
        }
    };
    n3.prototype.init.prototype = n3.prototype;
    n3.propHooks = {
        _default: {
            get: function(t) {
                var r;
                if (t.elem[t.prop] != null && (!t.elem.style || t.elem.style[t.prop] == null)) {
                    return t.elem[t.prop]
                }
                r = Q.css(t.elem, t.prop, false, "");
                return !r || r === "auto" ? 0 : r
            },
            set: function(t) {
                if (Q.fx.step[t.prop]) {
                    Q.fx.step[t.prop](t)
                } else if (t.elem.style && (t.elem.style[Q.cssProps[t.prop]] != null || Q.cssHooks[t.prop])) {
                    Q.style(t.elem, t.prop, t.now + t.unit)
                } else {
                    t.elem[t.prop] = t.now
                }
            }
        }
    };
    n3.propHooks.scrollTop = n3.propHooks.scrollLeft = {
        set: function(t) {
            if (t.elem.nodeType && t.elem.parentNode) {
                t.elem[t.prop] = t.now
            }
        }
    };
    Q.each(["toggle", "show", "hide"], function(i, n) {
        var c = Q.fn[n];
        Q.fn[n] = function(s, a, b) {
            return s == null || typeof s === "boolean" || (!i && Q.isFunction(s) && Q.isFunction(a)) ? c.apply(this, arguments) : this.animate(o3(n, true), s, a, b)
        }
    });
    Q.fn.extend({
        fadeTo: function(s, t, a, c) {
            return this.filter(o2).css("opacity", 0).show().end().animate({
                opacity: t
            }, s, a, c)
        },
        animate: function(p, s, a, c) {
            var b = Q.isEmptyObject(p),
                o = Q.speed(s, a, c),
                f = function() {
                    var i = k3(this, Q.extend({}, p), o);
                    if (b) {
                        i.stop(true)
                    }
                };
            return b || o.queue === false ? this.each(f) : this.queue(o.queue, f)
        },
        stop: function(t, c, a) {
            var s = function(b) {
                var f = b.stop;
                delete b.stop;
                f(a)
            };
            if (typeof t !== "string") {
                a = c;
                c = t;
                t = u
            }
            if (c && t !== false) {
                this.queue(t || "fx", [])
            }
            return this.each(function() {
                var b = true,
                    i = t != null && t + "queueHooks",
                    f = Q.timers,
                    j = Q._data(this);
                if (i) {
                    if (j[i] && j[i].stop) {
                        s(j[i])
                    }
                } else {
                    for (i in j) {
                        if (j[i] && j[i].stop && f3.test(i)) {
                            s(j[i])
                        }
                    }
                }
                for (i = f.length; i--;) {
                    if (f[i].elem === this && (t == null || f[i].queue === t)) {
                        f[i].anim.stop(a);
                        b = false;
                        f.splice(i, 1)
                    }
                }
                if (b || !a) {
                    Q.dequeue(this, t)
                }
            })
        }
    });

    function o3(t, a) {
        var b, c = {
            height: t
        }, i = 0;
        a = a ? 1 : 0;
        for (; i < 4; i += 2 - a) {
            b = k2[i];
            c["margin" + b] = c["padding" + b] = t
        }
        if (a) {
            c.opacity = c.width = t
        }
        return c
    }
    Q.each({
        slideDown: o3("show"),
        slideUp: o3("hide"),
        slideToggle: o3("toggle"),
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
    Q.speed = function(s, a, f) {
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
        o.complete = function() {
            if (Q.isFunction(o.old)) {
                o.old.call(this)
            }
            if (o.queue) {
                Q.dequeue(this, o.queue)
            }
        };
        return o
    };
    Q.easing = {
        linear: function(p) {
            return p
        },
        swing: function(p) {
            return 0.5 - Math.cos(p * Math.PI) / 2
        }
    };
    Q.timers = [];
    Q.fx = n3.prototype.init;
    Q.fx.tick = function() {
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
    };
    Q.fx.timer = function(t) {
        if (t() && Q.timers.push(t) && !c3) {
            c3 = setInterval(Q.fx.tick, Q.fx.interval)
        }
    };
    Q.fx.interval = 13;
    Q.fx.stop = function() {
        clearInterval(c3);
        c3 = null
    };
    Q.fx.speeds = {
        slow: 600,
        fast: 200,
        _default: 400
    };
    Q.fx.step = {};
    if (Q.expr && Q.expr.filters) {
        Q.expr.filters.animated = function(a) {
            return Q.grep(Q.timers, function(f) {
                return a === f.elem
            }).length
        }
    }
    var p3 = /^(?:body|html)$/i;
    Q.fn.offset = function(o) {
        if (arguments.length) {
            return o === u ? this : this.each(function(i) {
                Q.offset.setOffset(this, o, i)
            })
        }
        var b, a, c, f, j, l, s, m, t, n, p = this[0],
            r = p && p.ownerDocument;
        if (!r) {
            return
        }
        if ((c = r.body) === p) {
            return Q.offset.bodyOffset(p)
        }
        a = r.documentElement;
        if (!Q.contains(a, p)) {
            return {
                top: 0,
                left: 0
            }
        }
        b = p.getBoundingClientRect();
        f = q3(r);
        j = a.clientTop || c.clientTop || 0;
        l = a.clientLeft || c.clientLeft || 0;
        s = f.pageYOffset || a.scrollTop;
        m = f.pageXOffset || a.scrollLeft;
        t = b.top + s - j;
        n = b.left + m - l;
        return {
            top: t,
            left: n
        }
    };
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
                l = (p === "absolute" || p === "fixed") && Q.inArray("auto", [f, j]) > -1,
                m = {}, n = {}, r, s;
            if (l) {
                n = c.position();
                r = n.top;
                s = n.left
            } else {
                r = parseFloat(f) || 0;
                s = parseFloat(j) || 0
            }
            if (Q.isFunction(o)) {
                o = o.call(a, i, b)
            }
            if (o.top != null) {
                m.top = (o.top - b.top) + r
            }
            if (o.left != null) {
                m.left = (o.left - b.left) + s
            }
            if ("using" in o) {
                o.using.call(a, m)
            } else {
                c.css(m)
            }
        }
    };
    Q.fn.extend({
        position: function() {
            if (!this[0]) {
                return
            }
            var a = this[0],
                o = this.offsetParent(),
                b = this.offset(),
                p = p3.test(o[0].nodeName) ? {
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
                var o = this.offsetParent || h.body;
                while (o && (!p3.test(o.nodeName) && Q.css(o, "position") === "static")) {
                    o = o.offsetParent
                }
                return o || h.body
            })
        }
    });
    Q.each({
        scrollLeft: "pageXOffset",
        scrollTop: "pageYOffset"
    }, function(m, p) {
        var t = /Y/.test(p);
        Q.fn[m] = function(v) {
            return Q.access(this, function(a, m, v) {
                var b = q3(a);
                if (v === u) {
                    return b ? (p in b) ? b[p] : b.document.documentElement[m] : a[m]
                }
                if (b) {
                    b.scrollTo(!t ? v : Q(b).scrollLeft(), t ? v : Q(b).scrollTop())
                } else {
                    a[m] = v
                }
            }, m, v, arguments.length, null)
        }
    });

    function q3(a) {
        return Q.isWindow(a) ? a : a.nodeType === 9 ? a.defaultView || a.parentWindow : false
    }
    Q.each({
        Height: "height",
        Width: "width"
    }, function(n, t) {
        Q.each({
            padding: "inner" + n,
            content: t,
            "": "outer" + n
        }, function(a, f) {
            Q.fn[f] = function(m, v) {
                var c = arguments.length && (a || typeof m !== "boolean"),
                    b = a || (m === true || v === true ? "margin" : "border");
                return Q.access(this, function(i, t, v) {
                    var j;
                    if (Q.isWindow(i)) {
                        return i.document.documentElement["client" + n]
                    }
                    if (i.nodeType === 9) {
                        j = i.documentElement;
                        return Math.max(i.body["scroll" + n], j["scroll" + n], i.body["offset" + n], j["offset" + n], j["client" + n])
                    }
                    return v === u ? Q.css(i, t, v, b) : Q.style(i, t, v, b)
                }, t, c ? m : u, c, null)
            }
        })
    });
    w.jQuery = w.$ = Q;
    if (typeof define === "function" && define.amd && define.amd.jQuery) {
        define("jquery", [], function() {
            return Q
        })
    }
})(window);