﻿/*!
 * jQuery JavaScript Library v1.10.1
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2005, 2013 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2013-05-30T21:49Z
 */

(function(w, u) {
    var c, d, f = typeof u,
        g = w.location,
        h = w.document,
        k = h.documentElement,
        o = w.jQuery,
        q = w.$,
        x = {}, y = [],
        z = "1.10.1",
        A = y.concat,
        B = y.push,
        C = y.slice,
        D = y.indexOf,
        E = x.toString,
        F = x.hasOwnProperty,
        G = z.trim,
        Q = function(s, a) {
            return new Q.fn.init(s, a, d)
        }, H = /[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source,
        I = /\S+/g,
        J = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,
        K = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/,
        L = /^<(\w+)\s*\/?>(?:<\/\1>|)$/,
        M = /^[\],:{}\s]*$/,
        N = /(?:^|:|,)(?:\s*\[)+/g,
        O = /\\(?:["\\\/bfnrt]|u[\da-fA-F]{4})/g,
        P = /"[^"\\\r\n]*"|true|false|null|-?(?:\d+\.|)\d+(?:[eE][+-]?\d+|)/g,
        R = /^-ms-/,
        S = /-([\da-z])/gi,
        T = function(a, l) {
            return l.toUpperCase()
        }, U = function(a) {
            if (h.addEventListener || a.type === "load" || h.readyState === "complete") {
                V();
                Q.ready()
            }
        }, V = function() {
            if (h.addEventListener) {
                h.removeEventListener("DOMContentLoaded", U, false);
                w.removeEventListener("load", U, false)
            } else {
                h.detachEvent("onreadystatechange", U);
                w.detachEvent("onload", U)
            }
        };
    Q.fn = Q.prototype = {
        jquery: z,
        constructor: Q,
        init: function(s, a, d) {
            var m, b;
            if (!s) {
                return this
            }
            if (typeof s === "string") {
                if (s.charAt(0) === "<" && s.charAt(s.length - 1) === ">" && s.length >= 3) {
                    m = [null, s, null]
                } else {
                    m = K.exec(s)
                }
                if (m && (m[1] || !a)) {
                    if (m[1]) {
                        a = a instanceof Q ? a[0] : a;
                        Q.merge(this, Q.parseHTML(m[1], a && a.nodeType ? a.ownerDocument || a : h, true));
                        if (L.test(m[1]) && Q.isPlainObject(a)) {
                            for (m in a) {
                                if (Q.isFunction(this[m])) {
                                    this[m](a[m])
                                } else {
                                    this.attr(m, a[m])
                                }
                            }
                        }
                        return this
                    } else {
                        b = h.getElementById(m[2]);
                        if (b && b.parentNode) {
                            if (b.id !== m[2]) {
                                return d.find(s)
                            }
                            this.length = 1;
                            this[0] = b
                        }
                        this.context = h;
                        this.selector = s;
                        return this
                    }
                } else if (!a || a.jquery) {
                    return (a || d).find(s)
                } else {
                    return this.constructor(a).find(s)
                }
            } else if (s.nodeType) {
                this.context = this[0] = s;
                this.length = 1;
                return this
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
        length: 0,
        toArray: function() {
            return C.call(this)
        },
        get: function(n) {
            return n == null ? this.toArray() : (n < 0 ? this[this.length + n] : this[n])
        },
        pushStack: function(a) {
            var r = Q.merge(this.constructor(), a);
            r.prevObject = this;
            r.context = this.context;
            return r
        },
        each: function(a, b) {
            return Q.each(this, a, b)
        },
        ready: function(a) {
            Q.ready.promise().done(a);
            return this
        },
        slice: function() {
            return this.pushStack(C.apply(this, arguments))
        },
        first: function() {
            return this.eq(0)
        },
        last: function() {
            return this.eq(-1)
        },
        eq: function(i) {
            var l = this.length,
                j = +i + (i < 0 ? l : 0);
            return this.pushStack(j >= 0 && j < l ? [this[j]] : [])
        },
        map: function(a) {
            return this.pushStack(Q.map(this, function(b, i) {
                return a.call(b, i, b)
            }))
        },
        end: function() {
            return this.prevObject || this.constructor(null)
        },
        push: B,
        sort: [].sort,
        splice: [].splice
    };
    Q.fn.init.prototype = Q.fn;
    Q.extend = Q.fn.extend = function() {
        var s, a, b, n, j, l, t = arguments[0] || {}, i = 1,
            m = arguments.length,
            p = false;
        if (typeof t === "boolean") {
            p = t;
            t = arguments[1] || {};
            i = 2
        }
        if (typeof t !== "object" && !Q.isFunction(t)) {
            t = {}
        }
        if (m === i) {
            t = this;
            --i
        }
        for (; i < m; i++) {
            if ((j = arguments[i]) != null) {
                for (n in j) {
                    s = t[n];
                    b = j[n];
                    if (t === b) {
                        continue
                    }
                    if (p && b && (Q.isPlainObject(b) || (a = Q.isArray(b)))) {
                        if (a) {
                            a = false;
                            l = s && Q.isArray(s) ? s : []
                        } else {
                            l = s && Q.isPlainObject(s) ? s : {}
                        }
                        t[n] = Q.extend(p, l, b)
                    } else if (b !== u) {
                        t[n] = b
                    }
                }
            }
        }
        return t
    };
    Q.extend({
        expando: "jQuery" + (z + Math.random()).replace(/\D/g, ""),
        noConflict: function(a) {
            if (w.$ === Q) {
                w.$ = q
            }
            if (a && w.jQuery === Q) {
                w.jQuery = o
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
                return setTimeout(Q.ready)
            }
            Q.isReady = true;
            if (a !== true && --Q.readyWait > 0) {
                return
            }
            c.resolveWith(h, [Q]);
            if (Q.fn.trigger) {
                Q(h).trigger("ready").off("ready")
            }
        },
        isFunction: function(a) {
            return Q.type(a) === "function"
        },
        isArray: Array.isArray || function(a) {
            return Q.type(a) === "array"
        },
        isWindow: function(a) {
            return a != null && a == a.window
        },
        isNumeric: function(a) {
            return !isNaN(parseFloat(a)) && isFinite(a)
        },
        type: function(a) {
            if (a == null) {
                return String(a)
            }
            return typeof a === "object" || typeof a === "function" ? x[E.call(a)] || "object" : typeof a
        },
        isPlainObject: function(a) {
            var b;
            if (!a || Q.type(a) !== "object" || a.nodeType || Q.isWindow(a)) {
                return false
            }
            try {
                if (a.constructor && !F.call(a, "constructor") && !F.call(a.constructor.prototype, "isPrototypeOf")) {
                    return false
                }
            } catch (e) {
                return false
            }
            if (Q.support.ownLast) {
                for (b in a) {
                    return F.call(a, b)
                }
            }
            for (b in a) {}
            return b === u || F.call(a, b)
        },
        isEmptyObject: function(a) {
            var n;
            for (n in a) {
                return false
            }
            return true
        },
        error: function(m) {
            throw new Error(m)
        },
        parseHTML: function(a, b, i) {
            if (!a || typeof a !== "string") {
                return null
            }
            if (typeof b === "boolean") {
                i = b;
                b = false
            }
            b = b || h;
            var p = L.exec(a),
                s = !i && [];
            if (p) {
                return [b.createElement(p[1])]
            }
            p = Q.buildFragment([a], b, s);
            if (s) {
                Q(s).remove()
            }
            return Q.merge([], p.childNodes)
        },
        parseJSON: function(a) {
            if (w.JSON && w.JSON.parse) {
                return w.JSON.parse(a)
            }
            if (a === null) {
                return a
            }
            if (typeof a === "string") {
                a = Q.trim(a);
                if (a) {
                    if (M.test(a.replace(O, "@").replace(P, "]").replace(N, ""))) {
                        return (new Function("return " + a))()
                    }
                }
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
            if (a && Q.trim(a)) {
                (w.execScript || function(a) {
                    w["eval"].call(w, a)
                })(a)
            }
        },
        camelCase: function(s) {
            return s.replace(R, "ms-").replace(S, T)
        },
        nodeName: function(a, n) {
            return a.nodeName && a.nodeName.toLowerCase() === n.toLowerCase()
        },
        each: function(a, b, j) {
            var v, i = 0,
                l = a.length,
                m = W(a);
            if (j) {
                if (m) {
                    for (; i < l; i++) {
                        v = b.apply(a[i], j);
                        if (v === false) {
                            break
                        }
                    }
                } else {
                    for (i in a) {
                        v = b.apply(a[i], j);
                        if (v === false) {
                            break
                        }
                    }
                }
            } else {
                if (m) {
                    for (; i < l; i++) {
                        v = b.call(a[i], i, a[i]);
                        if (v === false) {
                            break
                        }
                    }
                } else {
                    for (i in a) {
                        v = b.call(a[i], i, a[i]);
                        if (v === false) {
                            break
                        }
                    }
                }
            }
            return a
        },
        trim: G && !G.call("\uFEFF\xA0") ? function(t) {
            return t == null ? "" : G.call(t)
        } : function(t) {
            return t == null ? "" : (t + "").replace(J, "")
        },
        makeArray: function(a, r) {
            var b = r || [];
            if (a != null) {
                if (W(Object(a))) {
                    Q.merge(b, typeof a === "string" ? [a] : a)
                } else {
                    B.call(b, a)
                }
            }
            return b
        },
        inArray: function(a, b, i) {
            var l;
            if (b) {
                if (D) {
                    return D.call(b, a, i)
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
        merge: function(a, s) {
            var l = s.length,
                i = a.length,
                j = 0;
            if (typeof l === "number") {
                for (; j < l; j++) {
                    a[i++] = s[j]
                }
            } else {
                while (s[j] !== u) {
                    a[i++] = s[j++]
                }
            }
            a.length = i;
            return a
        },
        grep: function(a, b, j) {
            var r, l = [],
                i = 0,
                m = a.length;
            j = !! j;
            for (; i < m; i++) {
                r = !! b(a[i], i);
                if (j !== r) {
                    l.push(a[i])
                }
            }
            return l
        },
        map: function(a, b, j) {
            var v, i = 0,
                l = a.length,
                m = W(a),
                r = [];
            if (m) {
                for (; i < l; i++) {
                    v = b(a[i], i, j);
                    if (v != null) {
                        r[r.length] = v
                    }
                }
            } else {
                for (i in a) {
                    v = b(a[i], i, j);
                    if (v != null) {
                        r[r.length] = v
                    }
                }
            }
            return A.apply([], r)
        },
        guid: 1,
        proxy: function(a, b) {
            var i, p, t;
            if (typeof b === "string") {
                t = a[b];
                b = a;
                a = t
            }
            if (!Q.isFunction(a)) {
                return u
            }
            i = C.call(arguments, 2);
            p = function() {
                return a.apply(b || this, i.concat(C.call(arguments)))
            };
            p.guid = a.guid = a.guid || Q.guid++;
            return p
        },
        access: function(a, b, j, v, l, m, r) {
            var i = 0,
                n = a.length,
                p = j == null;
            if (Q.type(j) === "object") {
                l = true;
                for (i in j) {
                    Q.access(a, b, i, j[i], true, m, r)
                }
            } else if (v !== u) {
                l = true;
                if (!Q.isFunction(v)) {
                    r = true
                }
                if (p) {
                    if (r) {
                        b.call(a, v);
                        b = null
                    } else {
                        p = b;
                        b = function(s, j, v) {
                            return p.call(Q(s), v)
                        }
                    }
                }
                if (b) {
                    for (; i < n; i++) {
                        b(a[i], j, r ? v : v.call(a[i], i, b(a[i], j)))
                    }
                }
            }
            return l ? a : p ? b.call(a) : n ? b(a[0], j) : m
        },
        now: function() {
            return (new Date()).getTime()
        },
        swap: function(a, b, i, j) {
            var r, n, l = {};
            for (n in b) {
                l[n] = a.style[n];
                a.style[n] = b[n]
            }
            r = i.apply(a, j || []);
            for (n in b) {
                a.style[n] = l[n]
            }
            return r
        }
    });
    Q.ready.promise = function(a) {
        if (!c) {
            c = Q.Deferred();
            if (h.readyState === "complete") {
                setTimeout(Q.ready)
            } else if (h.addEventListener) {
                h.addEventListener("DOMContentLoaded", U, false);
                w.addEventListener("load", U, false)
            } else {
                h.attachEvent("onreadystatechange", U);
                w.attachEvent("onload", U);
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
                            V();
                            Q.ready()
                        }
                    })()
                }
            }
        }
        return c.promise(a)
    };
    Q.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function(i, n) {
        x["[object " + n + "]"] = n.toLowerCase()
    });

    function W(a) {
        var l = a.length,
            t = Q.type(a);
        if (Q.isWindow(a)) {
            return false
        }
        if (a.nodeType === 1 && l) {
            return true
        }
        return t === "array" || t !== "function" && (l === 0 || typeof l === "number" && l > 0 && (l - 1) in a)
    }
    d = Q(h);

    /*!
     * Sizzle CSS Selector Engine v1.9.4-pre
     * http://sizzlejs.com/
     *
     * Copyright 2013 jQuery Foundation, Inc. and other contributors
     * Released under the MIT license
     * http://jquery.org/license
     *
     * Date: 2013-05-27
     */

    (function(w, u) {
        var i, s, l, n, p, r, t, v, s3, t3, h, k, u3, v3, w3, x3, y3, z3 = "sizzle" + -(new Date()),
            A3 = w.document,
            B3 = 0,
            C3 = 0,
            D3 = k4(),
            E3 = k4(),
            F3 = k4(),
            G3 = false,
            H3 = function() {
                return 0
            }, I3 = typeof u,
            J3 = 1 << 31,
            K3 = ({}).hasOwnProperty,
            L3 = [],
            M3 = L3.pop,
            N3 = L3.push,
            O3 = L3.push,
            P3 = L3.slice,
            Q3 = L3.indexOf || function(a) {
                var i = 0,
                    b = this.length;
                for (; i < b; i++) {
                    if (this[i] === a) {
                        return i
                    }
                }
                return -1
            }, R3 = "checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",
            S3 = "[\\x20\\t\\r\\n\\f]",
            T3 = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+",
            U3 = T3.replace("w", "w#"),
            V3 = "\\[" + S3 + "*(" + T3 + ")" + S3 + "*(?:([*^$|!~]?=)" + S3 + "*(?:(['\"])((?:\\\\.|[^\\\\])*?)\\3|(" + U3 + ")|)|)" + S3 + "*\\]",
            W3 = ":(" + T3 + ")(?:\\(((['\"])((?:\\\\.|[^\\\\])*?)\\3|((?:\\\\.|[^\\\\()[\\]]|" + V3.replace(3, 8) + ")*)|.*)\\)|)",
            J = new RegExp("^" + S3 + "+|((?:^|[^\\\\])(?:\\\\.)*)" + S3 + "+$", "g"),
            X3 = new RegExp("^" + S3 + "*," + S3 + "*"),
            Y3 = new RegExp("^" + S3 + "*([>+~]|" + S3 + ")" + S3 + "*"),
            Z3 = new RegExp(S3 + "*[+~]"),
            $3 = new RegExp("=" + S3 + "*([^\\]'\"]*)" + S3 + "*\\]", "g"),
            _3 = new RegExp(W3),
            a4 = new RegExp("^" + U3 + "$"),
            b4 = {
                "ID": new RegExp("^#(" + T3 + ")"),
                "CLASS": new RegExp("^\\.(" + T3 + ")"),
                "TAG": new RegExp("^(" + T3.replace("w", "w*") + ")"),
                "ATTR": new RegExp("^" + V3),
                "PSEUDO": new RegExp("^" + W3),
                "CHILD": new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + S3 + "*(even|odd|(([+-]|)(\\d*)n|)" + S3 + "*(?:([+-]|)" + S3 + "*(\\d+)|))" + S3 + "*\\)|)", "i"),
                "bool": new RegExp("^(?:" + R3 + ")$", "i"),
                "needsContext": new RegExp("^" + S3 + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" + S3 + "*((?:-\\d)?\\d*)" + S3 + "*\\)|)(?=[^-]|$)", "i")
            }, c4 = /^[^{]+\{\s*\[native \w/,
            K = /^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,
            d4 = /^(?:input|select|textarea|button)$/i,
            e4 = /^h\d$/i,
            f4 = /'|\\/g,
            g4 = new RegExp("\\\\([\\da-f]{1,6}" + S3 + "?|(" + S3 + ")|.)", "ig"),
            h4 = function(_, a, b) {
                var j = "0x" + a - 0x10000;
                return j !== j || b ? a : j < 0 ? String.fromCharCode(j + 0x10000) : String.fromCharCode(j >> 10 | 0xD800, j & 0x3FF | 0xDC00)
            };
        try {
            O3.apply((L3 = P3.call(A3.childNodes)), A3.childNodes);
            L3[A3.childNodes.length].nodeType
        } catch (e) {
            O3 = {
                apply: L3.length ? function(a, b) {
                    N3.apply(a, P3.call(b))
                } : function(a, b) {
                    var j = a.length,
                        i = 0;
                    while ((a[j++] = b[i++])) {}
                    a.length = j - 1
                }
            }
        }
        function i4(a, b, j, _) {
            var G4, H4, m, I4, i, J4, K4, L4, M4, N4;
            if ((b ? b.ownerDocument || b : A3) !== h) {
                t3(b)
            }
            b = b || h;
            j = j || [];
            if (!a || typeof a !== "string") {
                return j
            }
            if ((I4 = b.nodeType) !== 1 && I4 !== 9) {
                return []
            }
            if (u3 && !_) {
                if ((G4 = K.exec(a))) {
                    if ((m = G4[1])) {
                        if (I4 === 9) {
                            H4 = b.getElementById(m);
                            if (H4 && H4.parentNode) {
                                if (H4.id === m) {
                                    j.push(H4);
                                    return j
                                }
                            } else {
                                return j
                            }
                        } else {
                            if (b.ownerDocument && (H4 = b.ownerDocument.getElementById(m)) && y3(b, H4) && H4.id === m) {
                                j.push(H4);
                                return j
                            }
                        }
                    } else if (G4[2]) {
                        O3.apply(j, b.getElementsByTagName(a));
                        return j
                    } else if ((m = G4[3]) && s.getElementsByClassName && b.getElementsByClassName) {
                        O3.apply(j, b.getElementsByClassName(m));
                        return j
                    }
                }
                if (s.qsa && (!v3 || !v3.test(a))) {
                    L4 = K4 = z3;
                    M4 = b;
                    N4 = I4 === 9 && a;
                    if (I4 === 1 && b.nodeName.toLowerCase() !== "object") {
                        J4 = v4(a);
                        if ((K4 = b.getAttribute("id"))) {
                            L4 = K4.replace(f4, "\\$&")
                        } else {
                            b.setAttribute("id", L4)
                        }
                        L4 = "[id='" + L4 + "'] ";
                        i = J4.length;
                        while (i--) {
                            J4[i] = L4 + w4(J4[i])
                        }
                        M4 = Z3.test(a) && b.parentNode || b;
                        N4 = J4.join(",")
                    }
                    if (N4) {
                        try {
                            O3.apply(j, M4.querySelectorAll(N4));
                            return j
                        } catch (O4) {} finally {
                            if (!K4) {
                                b.removeAttribute("id")
                            }
                        }
                    }
                }
            }
            return E4(a.replace(J, "$1"), b, j, _)
        }
        function j4(a) {
            return c4.test(a + "")
        }
        function k4() {
            var a = [];

            function b(j, m) {
                if (a.push(j += " ") > n.cacheLength) {
                    delete b[a.shift()]
                }
                return (b[j] = m)
            }
            return b
        }
        function l4(a) {
            a[z3] = true;
            return a
        }
        function m4(a) {
            var b = h.createElement("div");
            try {
                return !!a(b)
            } catch (e) {
                return false
            } finally {
                if (b.parentNode) {
                    b.parentNode.removeChild(b)
                }
                b = null
            }
        }
        function n4(a, b, j) {
            a = a.split("|");
            var m, i = a.length,
                _ = j ? null : b;
            while (i--) {
                if (!(m = n.attrHandle[a[i]]) || m === b) {
                    n.attrHandle[a[i]] = _
                }
            }
        }
        function o4(a, b) {
            var j = a.getAttributeNode(b);
            return j && j.specified ? j.value : a[b] === true ? b.toLowerCase() : null
        }
        function p4(a, b) {
            return a.getAttribute(b, b.toLowerCase() === "type" ? 1 : 2)
        }
        function q4(a) {
            if (a.nodeName.toLowerCase() === "input") {
                return a.defaultValue
            }
        }
        function r4(a, b) {
            var j = b && a,
                m = j && a.nodeType === 1 && b.nodeType === 1 && (~b.sourceIndex || J3) - (~a.sourceIndex || J3);
            if (m) {
                return m
            }
            if (j) {
                while ((j = j.nextSibling)) {
                    if (j === b) {
                        return -1
                    }
                }
            }
            return a ? 1 : -1
        }
        function s4(a) {
            return function(b) {
                var j = b.nodeName.toLowerCase();
                return j === "input" && b.type === a
            }
        }
        function t4(a) {
            return function(b) {
                var j = b.nodeName.toLowerCase();
                return (j === "input" || j === "button") && b.type === a
            }
        }
        function u4(a) {
            return l4(function(b) {
                b = +b;
                return l4(function(m, x3) {
                    var j, _ = a([], m.length, b),
                        i = _.length;
                    while (i--) {
                        if (m[(j = _[i])]) {
                            m[j] = !(x3[j] = m[j])
                        }
                    }
                })
            })
        }
        r = i4.isXML = function(a) {
            var b = a && (a.ownerDocument || a).documentElement;
            return b ? b.nodeName !== "HTML" : false
        };
        s = i4.support = {};
        t3 = i4.setDocument = function(j) {
            var _ = j ? j.ownerDocument || j : A3,
                G4 = _.parentWindow;
            if (_ === h || _.nodeType !== 9 || !_.documentElement) {
                return h
            }
            h = _;
            k = _.documentElement;
            u3 = !r(_);
            if (G4 && G4.frameElement) {
                G4.attachEvent("onbeforeunload", function() {
                    t3()
                })
            }
            s.attributes = m4(function(a) {
                a.innerHTML = "<a href='#'></a>";
                n4("type|href|height|width", p4, a.firstChild.getAttribute("href") === "#");
                n4(R3, o4, a.getAttribute("disabled") == null);
                a.className = "i";
                return !a.getAttribute("className")
            });
            s.input = m4(function(a) {
                a.innerHTML = "<input>";
                a.firstChild.setAttribute("value", "");
                return a.firstChild.getAttribute("value") === ""
            });
            n4("value", q4, s.attributes && s.input);
            s.getElementsByTagName = m4(function(a) {
                a.appendChild(_.createComment(""));
                return !a.getElementsByTagName("*").length
            });
            s.getElementsByClassName = m4(function(a) {
                a.innerHTML = "<div class='a'></div><div class='a i'></div>";
                a.firstChild.className = "i";
                return a.getElementsByClassName("i").length === 2
            });
            s.getById = m4(function(a) {
                k.appendChild(a).id = z3;
                return !_.getElementsByName || !_.getElementsByName(z3).length
            });
            if (s.getById) {
                n.find["ID"] = function(a, b) {
                    if (typeof b.getElementById !== I3 && u3) {
                        var m = b.getElementById(a);
                        return m && m.parentNode ? [m] : []
                    }
                };
                n.filter["ID"] = function(a) {
                    var b = a.replace(g4, h4);
                    return function(m) {
                        return m.getAttribute("id") === b
                    }
                }
            } else {
                delete n.find["ID"];
                n.filter["ID"] = function(a) {
                    var b = a.replace(g4, h4);
                    return function(m) {
                        var j = typeof m.getAttributeNode !== I3 && m.getAttributeNode("id");
                        return j && j.value === b
                    }
                }
            }
            n.find["TAG"] = s.getElementsByTagName ? function(a, b) {
                if (typeof b.getElementsByTagName !== I3) {
                    return b.getElementsByTagName(a)
                }
            } : function(a, b) {
                var m, H4 = [],
                    i = 0,
                    I4 = b.getElementsByTagName(a);
                if (a === "*") {
                    while ((m = I4[i++])) {
                        if (m.nodeType === 1) {
                            H4.push(m)
                        }
                    }
                    return H4
                }
                return I4
            };
            n.find["CLASS"] = s.getElementsByClassName && function(a, b) {
                if (typeof b.getElementsByClassName !== I3 && u3) {
                    return b.getElementsByClassName(a)
                }
            };
            w3 = [];
            v3 = [];
            if ((s.qsa = j4(_.querySelectorAll))) {
                m4(function(a) {
                    a.innerHTML = "<select><option selected=''></option></select>";
                    if (!a.querySelectorAll("[selected]").length) {
                        v3.push("\\[" + S3 + "*(?:value|" + R3 + ")")
                    }
                    if (!a.querySelectorAll(":checked").length) {
                        v3.push(":checked")
                    }
                });
                m4(function(a) {
                    var b = _.createElement("input");
                    b.setAttribute("type", "hidden");
                    a.appendChild(b).setAttribute("t", "");
                    if (a.querySelectorAll("[t^='']").length) {
                        v3.push("[*^$]=" + S3 + "*(?:''|\"\")")
                    }
                    if (!a.querySelectorAll(":enabled").length) {
                        v3.push(":enabled", ":disabled")
                    }
                    a.querySelectorAll("*,:x");
                    v3.push(",.*:")
                })
            }
            if ((s.matchesSelector = j4((x3 = k.webkitMatchesSelector || k.mozMatchesSelector || k.oMatchesSelector || k.msMatchesSelector)))) {
                m4(function(a) {
                    s.disconnectedMatch = x3.call(a, "div");
                    x3.call(a, "[s!='']:x");
                    w3.push("!=", W3)
                })
            }
            v3 = v3.length && new RegExp(v3.join("|"));
            w3 = w3.length && new RegExp(w3.join("|"));
            y3 = j4(k.contains) || k.compareDocumentPosition ? function(a, b) {
                var m = a.nodeType === 9 ? a.documentElement : a,
                    H4 = b && b.parentNode;
                return a === H4 || !! (H4 && H4.nodeType === 1 && (m.contains ? m.contains(H4) : a.compareDocumentPosition && a.compareDocumentPosition(H4) & 16))
            } : function(a, b) {
                if (b) {
                    while ((b = b.parentNode)) {
                        if (b === a) {
                            return true
                        }
                    }
                }
                return false
            };
            s.sortDetached = m4(function(a) {
                return a.compareDocumentPosition(_.createElement("div")) & 1
            });
            H3 = k.compareDocumentPosition ? function(a, b) {
                if (a === b) {
                    G3 = true;
                    return 0
                }
                var m = b.compareDocumentPosition && a.compareDocumentPosition && a.compareDocumentPosition(b);
                if (m) {
                    if (m & 1 || (!s.sortDetached && b.compareDocumentPosition(a) === m)) {
                        if (a === _ || y3(A3, a)) {
                            return -1
                        }
                        if (b === _ || y3(A3, b)) {
                            return 1
                        }
                        return s3 ? (Q3.call(s3, a) - Q3.call(s3, b)) : 0
                    }
                    return m & 4 ? -1 : 1
                }
                return a.compareDocumentPosition ? -1 : 1
            } : function(a, b) {
                var m, i = 0,
                    H4 = a.parentNode,
                    I4 = b.parentNode,
                    ap = [a],
                    bp = [b];
                if (a === b) {
                    G3 = true;
                    return 0
                } else if (!H4 || !I4) {
                    return a === _ ? -1 : b === _ ? 1 : H4 ? -1 : I4 ? 1 : s3 ? (Q3.call(s3, a) - Q3.call(s3, b)) : 0
                } else if (H4 === I4) {
                    return r4(a, b)
                }
                m = a;
                while ((m = m.parentNode)) {
                    ap.unshift(m)
                }
                m = b;
                while ((m = m.parentNode)) {
                    bp.unshift(m)
                }
                while (ap[i] === bp[i]) {
                    i++
                }
                return i ? r4(ap[i], bp[i]) : ap[i] === A3 ? -1 : bp[i] === A3 ? 1 : 0
            };
            return _
        };
        i4.matches = function(a, b) {
            return i4(a, null, null, b)
        };
        i4.matchesSelector = function(a, b) {
            if ((a.ownerDocument || a) !== h) {
                t3(a)
            }
            b = b.replace($3, "='$1']");
            if (s.matchesSelector && u3 && (!w3 || !w3.test(b)) && (!v3 || !v3.test(b))) {
                try {
                    var j = x3.call(a, b);
                    if (j || s.disconnectedMatch || a.document && a.document.nodeType !== 11) {
                        return j
                    }
                } catch (e) {}
            }
            return i4(b, h, null, [a]).length > 0
        };
        i4.contains = function(a, b) {
            if ((a.ownerDocument || a) !== h) {
                t3(a)
            }
            return y3(a, b)
        };
        i4.attr = function(a, b) {
            if ((a.ownerDocument || a) !== h) {
                t3(a)
            }
            var j = n.attrHandle[b.toLowerCase()],
                m = (j && K3.call(n.attrHandle, b.toLowerCase()) ? j(a, b, !u3) : u);
            return m === u ? s.attributes || !u3 ? a.getAttribute(b) : (m = a.getAttributeNode(b)) && m.specified ? m.value : null : m
        };
        i4.error = function(m) {
            throw new Error("Syntax error, unrecognized expression: " + m)
        };
        i4.uniqueSort = function(a) {
            var b, m = [],
                j = 0,
                i = 0;
            G3 = !s.detectDuplicates;
            s3 = !s.sortStable && a.slice(0);
            a.sort(H3);
            if (G3) {
                while ((b = a[i++])) {
                    if (b === a[i]) {
                        j = m.push(i)
                    }
                }
                while (j--) {
                    a.splice(m[j], 1)
                }
            }
            return a
        };
        p = i4.getText = function(a) {
            var b, j = "",
                i = 0,
                m = a.nodeType;
            if (!m) {
                for (;
                (b = a[i]); i++) {
                    j += p(b)
                }
            } else if (m === 1 || m === 9 || m === 11) {
                if (typeof a.textContent === "string") {
                    return a.textContent
                } else {
                    for (a = a.firstChild; a; a = a.nextSibling) {
                        j += p(a)
                    }
                }
            } else if (m === 3 || m === 4) {
                return a.nodeValue
            }
            return j
        };
        n = i4.selectors = {
            cacheLength: 50,
            createPseudo: l4,
            match: b4,
            attrHandle: {},
            find: {},
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
                    m[1] = m[1].replace(g4, h4);
                    m[3] = (m[4] || m[5] || "").replace(g4, h4);
                    if (m[2] === "~=") {
                        m[3] = " " + m[3] + " "
                    }
                    return m.slice(0, 4)
                },
                "CHILD": function(m) {
                    m[1] = m[1].toLowerCase();
                    if (m[1].slice(0, 3) === "nth") {
                        if (!m[3]) {
                            i4.error(m[0])
                        }
                        m[4] = +(m[4] ? m[5] + (m[6] || 1) : 2 * (m[3] === "even" || m[3] === "odd"));
                        m[5] = +((m[7] + m[8]) || m[3] === "odd")
                    } else if (m[3]) {
                        i4.error(m[0])
                    }
                    return m
                },
                "PSEUDO": function(m) {
                    var a, b = !m[5] && m[2];
                    if (b4["CHILD"].test(m[0])) {
                        return null
                    }
                    if (m[3] && m[4] !== u) {
                        m[2] = m[4]
                    } else if (b && _3.test(b) && (a = v4(b, true)) && (a = b.indexOf(")", b.length - a) - b.length)) {
                        m[0] = m[0].slice(0, a);
                        m[2] = b.slice(0, a)
                    }
                    return m.slice(0, 3)
                }
            },
            filter: {
                "TAG": function(a) {
                    var b = a.replace(g4, h4).toLowerCase();
                    return a === "*" ? function() {
                        return true
                    } : function(j) {
                        return j.nodeName && j.nodeName.toLowerCase() === b
                    }
                },
                "CLASS": function(a) {
                    var b = D3[a + " "];
                    return b || (b = new RegExp("(^|" + S3 + ")" + a + "(" + S3 + "|$)")) && D3(a, function(j) {
                        return b.test(typeof j.className === "string" && j.className || typeof j.getAttribute !== I3 && j.getAttribute("class") || "")
                    })
                },
                "ATTR": function(a, b, j) {
                    return function(m) {
                        var _ = i4.attr(m, a);
                        if (_ == null) {
                            return b === "!="
                        }
                        if (!b) {
                            return true
                        }
                        _ += "";
                        return b === "=" ? _ === j : b === "!=" ? _ !== j : b === "^=" ? j && _.indexOf(j) === 0 : b === "*=" ? j && _.indexOf(j) > -1 : b === "$=" ? j && _.slice(-j.length) === j : b === "~=" ? (" " + _ + " ").indexOf(j) > -1 : b === "|=" ? _ === j || _.slice(0, j.length + 1) === j + "-" : false
                    }
                },
                "CHILD": function(a, b, j, m, _) {
                    var G4 = a.slice(0, 3) !== "nth",
                        H4 = a.slice(-4) !== "last",
                        I4 = b === "of-type";
                    return m === 1 && _ === 0 ? function(J4) {
                        return !!J4.parentNode
                    } : function(J4, K4, L4) {
                        var M4, N4, O4, P4, Q4, R4, S4 = G4 !== H4 ? "nextSibling" : "previousSibling",
                            T4 = J4.parentNode,
                            U4 = I4 && J4.nodeName.toLowerCase(),
                            V4 = !L4 && !I4;
                        if (T4) {
                            if (G4) {
                                while (S4) {
                                    O4 = J4;
                                    while ((O4 = O4[S4])) {
                                        if (I4 ? O4.nodeName.toLowerCase() === U4 : O4.nodeType === 1) {
                                            return false
                                        }
                                    }
                                    R4 = S4 = a === "only" && !R4 && "nextSibling"
                                }
                                return true
                            }
                            R4 = [H4 ? T4.firstChild : T4.lastChild];
                            if (H4 && V4) {
                                N4 = T4[z3] || (T4[z3] = {});
                                M4 = N4[a] || [];
                                Q4 = M4[0] === B3 && M4[1];
                                P4 = M4[0] === B3 && M4[2];
                                O4 = Q4 && T4.childNodes[Q4];
                                while ((O4 = ++Q4 && O4 && O4[S4] || (P4 = Q4 = 0) || R4.pop())) {
                                    if (O4.nodeType === 1 && ++P4 && O4 === J4) {
                                        N4[a] = [B3, Q4, P4];
                                        break
                                    }
                                }
                            } else if (V4 && (M4 = (J4[z3] || (J4[z3] = {}))[a]) && M4[0] === B3) {
                                P4 = M4[1]
                            } else {
                                while ((O4 = ++Q4 && O4 && O4[S4] || (P4 = Q4 = 0) || R4.pop())) {
                                    if ((I4 ? O4.nodeName.toLowerCase() === U4 : O4.nodeType === 1) && ++P4) {
                                        if (V4) {
                                            (O4[z3] || (O4[z3] = {}))[a] = [B3, P4]
                                        }
                                        if (O4 === J4) {
                                            break
                                        }
                                    }
                                }
                            }
                            P4 -= _;
                            return P4 === m || (P4 % m === 0 && P4 / m >= 0)
                        }
                    }
                },
                "PSEUDO": function(a, b) {
                    var j, m = n.pseudos[a] || n.setFilters[a.toLowerCase()] || i4.error("unsupported pseudo: " + a);
                    if (m[z3]) {
                        return m(b)
                    }
                    if (m.length > 1) {
                        j = [a, a, "", b];
                        return n.setFilters.hasOwnProperty(a.toLowerCase()) ? l4(function(_, x3) {
                            var G4, H4 = m(_, b),
                                i = H4.length;
                            while (i--) {
                                G4 = Q3.call(_, H4[i]);
                                _[G4] = !(x3[G4] = H4[i])
                            }
                        }) : function(_) {
                            return m(_, 0, j)
                        }
                    }
                    return m
                }
            },
            pseudos: {
                "not": l4(function(a) {
                    var b = [],
                        j = [],
                        m = t(a.replace(J, "$1"));
                    return m[z3] ? l4(function(_, x3, G4, H4) {
                        var I4, J4 = m(_, null, H4, []),
                            i = _.length;
                        while (i--) {
                            if ((I4 = J4[i])) {
                                _[i] = !(x3[i] = I4)
                            }
                        }
                    }) : function(_, G4, H4) {
                        b[0] = _;
                        m(b, null, H4, j);
                        return !j.pop()
                    }
                }),
                "has": l4(function(a) {
                    return function(b) {
                        return i4(a, b).length > 0
                    }
                }),
                "contains": l4(function(a) {
                    return function(b) {
                        return (b.textContent || b.innerText || p(b)).indexOf(a) > -1
                    }
                }),
                "lang": l4(function(a) {
                    if (!a4.test(a || "")) {
                        i4.error("unsupported lang: " + a)
                    }
                    a = a.replace(g4, h4).toLowerCase();
                    return function(b) {
                        var j;
                        do {
                            if ((j = u3 ? b.lang : b.getAttribute("xml:lang") || b.getAttribute("lang"))) {
                                j = j.toLowerCase();
                                return j === a || j.indexOf(a + "-") === 0
                            }
                        } while ((b = b.parentNode) && b.nodeType === 1);
                        return false
                    }
                }),
                "target": function(a) {
                    var b = w.location && w.location.hash;
                    return b && b.slice(1) === a.id
                },
                "root": function(a) {
                    return a === k
                },
                "focus": function(a) {
                    return a === h.activeElement && (!h.hasFocus || h.hasFocus()) && !! (a.type || a.href || ~a.tabIndex)
                },
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
                "empty": function(a) {
                    for (a = a.firstChild; a; a = a.nextSibling) {
                        if (a.nodeName > "@" || a.nodeType === 3 || a.nodeType === 4) {
                            return false
                        }
                    }
                    return true
                },
                "parent": function(a) {
                    return !n.pseudos["empty"](a)
                },
                "header": function(a) {
                    return e4.test(a.nodeName)
                },
                "input": function(a) {
                    return d4.test(a.nodeName)
                },
                "button": function(a) {
                    var b = a.nodeName.toLowerCase();
                    return b === "input" && a.type === "button" || b === "button"
                },
                "text": function(a) {
                    var b;
                    return a.nodeName.toLowerCase() === "input" && a.type === "text" && ((b = a.getAttribute("type")) == null || b.toLowerCase() === a.type)
                },
                "first": u4(function() {
                    return [0]
                }),
                "last": u4(function(m, a) {
                    return [a - 1]
                }),
                "eq": u4(function(m, a, b) {
                    return [b < 0 ? b + a : b]
                }),
                "even": u4(function(m, a) {
                    var i = 0;
                    for (; i < a; i += 2) {
                        m.push(i)
                    }
                    return m
                }),
                "odd": u4(function(m, a) {
                    var i = 1;
                    for (; i < a; i += 2) {
                        m.push(i)
                    }
                    return m
                }),
                "lt": u4(function(m, a, b) {
                    var i = b < 0 ? b + a : b;
                    for (; --i >= 0;) {
                        m.push(i)
                    }
                    return m
                }),
                "gt": u4(function(m, a, b) {
                    var i = b < 0 ? b + a : b;
                    for (; ++i < a;) {
                        m.push(i)
                    }
                    return m
                })
            }
        };
        for (i in {
            radio: true,
            checkbox: true,
            file: true,
            password: true,
            image: true
        }) {
            n.pseudos[i] = s4(i)
        }
        for (i in {
            submit: true,
            reset: true
        }) {
            n.pseudos[i] = t4(i)
        }
        function v4(a, b) {
            var m, j, _, G4, H4, I4, J4, K4 = E3[a + " "];
            if (K4) {
                return b ? 0 : K4.slice(0)
            }
            H4 = a;
            I4 = [];
            J4 = n.preFilter;
            while (H4) {
                if (!m || (j = X3.exec(H4))) {
                    if (j) {
                        H4 = H4.slice(j[0].length) || H4
                    }
                    I4.push(_ = [])
                }
                m = false;
                if ((j = Y3.exec(H4))) {
                    m = j.shift();
                    _.push({
                        value: m,
                        type: j[0].replace(J, " ")
                    });
                    H4 = H4.slice(m.length)
                }
                for (G4 in n.filter) {
                    if ((j = b4[G4].exec(H4)) && (!J4[G4] || (j = J4[G4](j)))) {
                        m = j.shift();
                        _.push({
                            value: m,
                            type: G4,
                            matches: j
                        });
                        H4 = H4.slice(m.length)
                    }
                }
                if (!m) {
                    break
                }
            }
            return b ? H4.length : H4 ? i4.error(a) : E3(a, I4).slice(0)
        }
        function w4(a) {
            var i = 0,
                b = a.length,
                j = "";
            for (; i < b; i++) {
                j += a[i].value
            }
            return j
        }
        function x4(m, a, b) {
            var j = a.dir,
                _ = b && j === "parentNode",
                G4 = C3++;
            return a.first ? function(H4, I4, J4) {
                while ((H4 = H4[j])) {
                    if (H4.nodeType === 1 || _) {
                        return m(H4, I4, J4)
                    }
                }
            } : function(H4, I4, J4) {
                var K4, L4, M4, N4 = B3 + " " + G4;
                if (J4) {
                    while ((H4 = H4[j])) {
                        if (H4.nodeType === 1 || _) {
                            if (m(H4, I4, J4)) {
                                return true
                            }
                        }
                    }
                } else {
                    while ((H4 = H4[j])) {
                        if (H4.nodeType === 1 || _) {
                            M4 = H4[z3] || (H4[z3] = {});
                            if ((L4 = M4[j]) && L4[0] === N4) {
                                if ((K4 = L4[1]) === true || K4 === l) {
                                    return K4 === true
                                }
                            } else {
                                L4 = M4[j] = [N4];
                                L4[1] = m(H4, I4, J4) || l;
                                if (L4[1] === true) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        function y4(m) {
            return m.length > 1 ? function(a, b, j) {
                var i = m.length;
                while (i--) {
                    if (!m[i](a, b, j)) {
                        return false
                    }
                }
                return true
            } : m[0]
        }
        function z4(a, m, b, j, _) {
            var G4, H4 = [],
                i = 0,
                I4 = a.length,
                J4 = m != null;
            for (; i < I4; i++) {
                if ((G4 = a[i])) {
                    if (!b || b(G4, j, _)) {
                        H4.push(G4);
                        if (J4) {
                            m.push(i)
                        }
                    }
                }
            }
            return H4
        }
        function A4(a, b, m, j, _, G4) {
            if (j && !j[z3]) {
                j = A4(j)
            }
            if (_ && !_[z3]) {
                _ = A4(_, G4)
            }
            return l4(function(H4, I4, J4, K4) {
                var L4, i, M4, N4 = [],
                    O4 = [],
                    P4 = I4.length,
                    Q4 = H4 || D4(b || "*", J4.nodeType ? [J4] : J4, []),
                    R4 = a && (H4 || !b) ? z4(Q4, N4, a, J4, K4) : Q4,
                    S4 = m ? _ || (H4 ? a : P4 || j) ? [] : I4 : R4;
                if (m) {
                    m(R4, S4, J4, K4)
                }
                if (j) {
                    L4 = z4(S4, O4);
                    j(L4, [], J4, K4);
                    i = L4.length;
                    while (i--) {
                        if ((M4 = L4[i])) {
                            S4[O4[i]] = !(R4[O4[i]] = M4)
                        }
                    }
                }
                if (H4) {
                    if (_ || a) {
                        if (_) {
                            L4 = [];
                            i = S4.length;
                            while (i--) {
                                if ((M4 = S4[i])) {
                                    L4.push((R4[i] = M4))
                                }
                            }
                            _(null, (S4 = []), L4, K4)
                        }
                        i = S4.length;
                        while (i--) {
                            if ((M4 = S4[i]) && (L4 = _ ? Q3.call(H4, M4) : N4[i]) > -1) {
                                H4[L4] = !(I4[L4] = M4)
                            }
                        }
                    }
                } else {
                    S4 = z4(S4 === I4 ? S4.splice(P4, S4.length) : S4);
                    if (_) {
                        _(null, I4, S4, K4)
                    } else {
                        O3.apply(I4, S4)
                    }
                }
            })
        }
        function B4(a) {
            var b, m, j, _ = a.length,
                G4 = n.relative[a[0].type],
                H4 = G4 || n.relative[" "],
                i = G4 ? 1 : 0,
                I4 = x4(function(L4) {
                    return L4 === b
                }, H4, true),
                J4 = x4(function(L4) {
                    return Q3.call(b, L4) > -1
                }, H4, true),
                K4 = [function(L4, M4, N4) {
                    return (!G4 && (N4 || M4 !== v)) || ((b = M4).nodeType ? I4(L4, M4, N4) : J4(L4, M4, N4))
                }];
            for (; i < _; i++) {
                if ((m = n.relative[a[i].type])) {
                    K4 = [x4(y4(K4), m)]
                } else {
                    m = n.filter[a[i].type].apply(null, a[i].matches);
                    if (m[z3]) {
                        j = ++i;
                        for (; j < _; j++) {
                            if (n.relative[a[j].type]) {
                                break
                            }
                        }
                        return A4(i > 1 && y4(K4), i > 1 && w4(a.slice(0, i - 1).concat({
                            value: a[i - 2].type === " " ? "*" : ""
                        })).replace(J, "$1"), m, i < j && B4(a.slice(i, j)), j < _ && B4((a = a.slice(j))), j < _ && w4(a))
                    }
                    K4.push(m)
                }
            }
            return y4(K4)
        }
        function C4(a, b) {
            var m = 0,
                _ = b.length > 0,
                G4 = a.length > 0,
                H4 = function(I4, J4, K4, L4, M4) {
                    var N4, j, O4, P4 = [],
                        Q4 = 0,
                        i = "0",
                        R4 = I4 && [],
                        S4 = M4 != null,
                        T4 = v,
                        U4 = I4 || G4 && n.find["TAG"]("*", M4 && J4.parentNode || J4),
                        V4 = (B3 += T4 == null ? 1 : Math.random() || 0.1);
                    if (S4) {
                        v = J4 !== h && J4;
                        l = m
                    }
                    for (;
                    (N4 = U4[i]) != null; i++) {
                        if (G4 && N4) {
                            j = 0;
                            while ((O4 = a[j++])) {
                                if (O4(N4, J4, K4)) {
                                    L4.push(N4);
                                    break
                                }
                            }
                            if (S4) {
                                B3 = V4;
                                l = ++m
                            }
                        }
                        if (_) {
                            if ((N4 = !O4 && N4)) {
                                Q4--
                            }
                            if (I4) {
                                R4.push(N4)
                            }
                        }
                    }
                    Q4 += i;
                    if (_ && i !== Q4) {
                        j = 0;
                        while ((O4 = b[j++])) {
                            O4(R4, P4, J4, K4)
                        }
                        if (I4) {
                            if (Q4 > 0) {
                                while (i--) {
                                    if (!(R4[i] || P4[i])) {
                                        P4[i] = M3.call(L4)
                                    }
                                }
                            }
                            P4 = z4(P4)
                        }
                        O3.apply(L4, P4);
                        if (S4 && !I4 && P4.length > 0 && (Q4 + b.length) > 1) {
                            i4.uniqueSort(L4)
                        }
                    }
                    if (S4) {
                        B3 = V4;
                        v = T4
                    }
                    return R4
                };
            return _ ? l4(H4) : H4
        }
        t = i4.compile = function(a, b) {
            var i, j = [],
                m = [],
                _ = F3[a + " "];
            if (!_) {
                if (!b) {
                    b = v4(a)
                }
                i = b.length;
                while (i--) {
                    _ = B4(b[i]);
                    if (_[z3]) {
                        j.push(_)
                    } else {
                        m.push(_)
                    }
                }
                _ = F3(a, C4(m, j))
            }
            return _
        };

        function D4(a, b, j) {
            var i = 0,
                m = b.length;
            for (; i < m; i++) {
                i4(a, b[i], j)
            }
            return j
        }
        function E4(a, b, j, m) {
            var i, _, G4, H4, I4, J4 = v4(a);
            if (!m) {
                if (J4.length === 1) {
                    _ = J4[0] = J4[0].slice(0);
                    if (_.length > 2 && (G4 = _[0]).type === "ID" && s.getById && b.nodeType === 9 && u3 && n.relative[_[1].type]) {
                        b = (n.find["ID"](G4.matches[0].replace(g4, h4), b) || [])[0];
                        if (!b) {
                            return j
                        }
                        a = a.slice(_.shift().value.length)
                    }
                    i = b4["needsContext"].test(a) ? 0 : _.length;
                    while (i--) {
                        G4 = _[i];
                        if (n.relative[(H4 = G4.type)]) {
                            break
                        }
                        if ((I4 = n.find[H4])) {
                            if ((m = I4(G4.matches[0].replace(g4, h4), Z3.test(_[0].type) && b.parentNode || b))) {
                                _.splice(i, 1);
                                a = m.length && w4(_);
                                if (!a) {
                                    O3.apply(j, m);
                                    return j
                                }
                                break
                            }
                        }
                    }
                }
            }
            t(a, J4)(m, b, !u3, j, Z3.test(a));
            return j
        }
        n.pseudos["nth"] = n.pseudos["eq"];

        function F4() {}
        F4.prototype = n.filters = n.pseudos;
        n.setFilters = new F4();
        s.sortStable = z3.split("").sort(H3).join("") === z3;
        t3();
        [0, 0].sort(H3);
        s.detectDuplicates = G3;
        Q.find = i4;
        Q.expr = i4.selectors;
        Q.expr[":"] = Q.expr.pseudos;
        Q.unique = i4.uniqueSort;
        Q.text = i4.getText;
        Q.isXMLDoc = i4.isXML;
        Q.contains = i4.contains
    })(w);
    var X = {};

    function Y(a) {
        var b = X[a] = {};
        Q.each(a.match(I) || [], function(_, i) {
            b[i] = true
        });
        return b
    }
    Q.Callbacks = function(a) {
        a = typeof a === "string" ? (X[a] || Y(a)) : Q.extend({}, a);
        var b, m, i, j, l, n, p = [],
            s = !a.once && [],
            r = function(v) {
                m = a.memory && v;
                i = true;
                l = n || 0;
                n = 0;
                j = p.length;
                b = true;
                for (; p && l < j; l++) {
                    if (p[l].apply(v[0], v[1]) === false && a.stopOnFalse) {
                        m = false;
                        break
                    }
                }
                b = false;
                if (p) {
                    if (s) {
                        if (s.length) {
                            r(s.shift())
                        }
                    } else if (m) {
                        p = []
                    } else {
                        t.disable()
                    }
                }
            }, t = {
                add: function() {
                    if (p) {
                        var v = p.length;
                        (function add(s3) {
                            Q.each(s3, function(_, t3) {
                                var u3 = Q.type(t3);
                                if (u3 === "function") {
                                    if (!a.unique || !t.has(t3)) {
                                        p.push(t3)
                                    }
                                } else if (t3 && t3.length && u3 !== "string") {
                                    add(t3)
                                }
                            })
                        })(arguments);
                        if (b) {
                            j = p.length
                        } else if (m) {
                            n = v;
                            r(m)
                        }
                    }
                    return this
                },
                remove: function() {
                    if (p) {
                        Q.each(arguments, function(_, v) {
                            var s3;
                            while ((s3 = Q.inArray(v, p, s3)) > -1) {
                                p.splice(s3, 1);
                                if (b) {
                                    if (s3 <= j) {
                                        j--
                                    }
                                    if (s3 <= l) {
                                        l--
                                    }
                                }
                            }
                        })
                    }
                    return this
                },
                has: function(v) {
                    return v ? Q.inArray(v, p) > -1 : !! (p && p.length)
                },
                empty: function() {
                    p = [];
                    j = 0;
                    return this
                },
                disable: function() {
                    p = s = m = u;
                    return this
                },
                disabled: function() {
                    return !p
                },
                lock: function() {
                    s = u;
                    if (!m) {
                        t.disable()
                    }
                    return this
                },
                locked: function() {
                    return !s
                },
                fireWith: function(v, _) {
                    _ = _ || [];
                    _ = [v, _.slice ? _.slice() : _];
                    if (p && (!i || s)) {
                        if (b) {
                            s.push(_)
                        } else {
                            r(_)
                        }
                    }
                    return this
                },
                fire: function() {
                    t.fireWith(this, arguments);
                    return this
                },
                fired: function() {
                    return !!i
                }
            };
        return t
    };
    Q.extend({
        Deferred: function(a) {
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
                        b.done(arguments).fail(arguments);
                        return this
                    },
                    then: function() {
                        var j = arguments;
                        return Q.Deferred(function(n) {
                            Q.each(t, function(i, l) {
                                var m = l[0],
                                    r = Q.isFunction(j[i]) && j[i];
                                b[l[1]](function() {
                                    var v = r && r.apply(this, arguments);
                                    if (v && Q.isFunction(v.promise)) {
                                        v.promise().done(n.resolve).fail(n.reject).progress(n.notify)
                                    } else {
                                        n[m + "With"](this === p ? n.promise() : this, r ? [v] : arguments)
                                    }
                                })
                            });
                            j = null
                        }).promise()
                    },
                    promise: function(i) {
                        return i != null ? Q.extend(i, p) : p
                    }
                }, b = {};
            p.pipe = p.then;
            Q.each(t, function(i, j) {
                var l = j[2],
                    m = j[3];
                p[j[1]] = l.add;
                if (m) {
                    l.add(function() {
                        s = m
                    }, t[i ^ 1][2].disable, t[2][2].lock)
                }
                b[j[0]] = function() {
                    b[j[0] + "With"](this === b ? p : this, arguments);
                    return this
                };
                b[j[0] + "With"] = l.fireWith
            });
            p.promise(b);
            if (a) {
                a.call(b, b)
            }
            return b
        },
        when: function(s) {
            var i = 0,
                r = C.call(arguments),
                l = r.length,
                a = l !== 1 || (s && Q.isFunction(s.promise)) ? l : 0,
                b = a === 1 ? s : Q.Deferred(),
                j = function(i, t, v) {
                    return function(_) {
                        t[i] = this;
                        v[i] = arguments.length > 1 ? C.call(arguments) : _;
                        if (v === p) {
                            b.notifyWith(t, v)
                        } else if (!(--a)) {
                            b.resolveWith(t, v)
                        }
                    }
                }, p, m, n;
            if (l > 1) {
                p = new Array(l);
                m = new Array(l);
                n = new Array(l);
                for (; i < l; i++) {
                    if (r[i] && Q.isFunction(r[i].promise)) {
                        r[i].promise().done(j(i, n, r)).fail(b.reject).progress(j(i, m, p))
                    } else {
                        --a
                    }
                }
            }
            if (!a) {
                b.resolveWith(n, r)
            }
            return b.promise()
        }
    });
    Q.support = (function(s) {
        var b, a, j, l, m, n, p, r, i, t = h.createElement("div");
        t.setAttribute("className", "t");
        t.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";
        b = t.getElementsByTagName("*") || [];
        a = t.getElementsByTagName("a")[0];
        if (!a || !a.style || !b.length) {
            return s
        }
        l = h.createElement("select");
        n = l.appendChild(h.createElement("option"));
        j = t.getElementsByTagName("input")[0];
        a.style.cssText = "top:1px;float:left;opacity:.5";
        s.getSetAttribute = t.className !== "t";
        s.leadingWhitespace = t.firstChild.nodeType === 3;
        s.tbody = !t.getElementsByTagName("tbody").length;
        s.htmlSerialize = !! t.getElementsByTagName("link").length;
        s.style = /top/.test(a.getAttribute("style"));
        s.hrefNormalized = a.getAttribute("href") === "/a";
        s.opacity = /^0.5/.test(a.style.opacity);
        s.cssFloat = !! a.style.cssFloat;
        s.checkOn = !! j.value;
        s.optSelected = n.selected;
        s.enctype = !! h.createElement("form").enctype;
        s.html5Clone = h.createElement("nav").cloneNode(true).outerHTML !== "<:nav></:nav>";
        s.inlineBlockNeedsLayout = false;
        s.shrinkWrapBlocks = false;
        s.pixelPosition = false;
        s.deleteExpando = true;
        s.noCloneEvent = true;
        s.reliableMarginRight = true;
        s.boxSizingReliable = true;
        j.checked = true;
        s.noCloneChecked = j.cloneNode(true).checked;
        l.disabled = true;
        s.optDisabled = !n.disabled;
        try {
            delete t.test
        } catch (e) {
            s.deleteExpando = false
        }
        j = h.createElement("input");
        j.setAttribute("value", "");
        s.input = j.getAttribute("value") === "";
        j.value = "t";
        j.setAttribute("type", "radio");
        s.radioValue = j.value === "t";
        j.setAttribute("checked", "t");
        j.setAttribute("name", "t");
        m = h.createDocumentFragment();
        m.appendChild(j);
        s.appendChecked = j.checked;
        s.checkClone = m.cloneNode(true).cloneNode(true).lastChild.checked;
        if (t.attachEvent) {
            t.attachEvent("onclick", function() {
                s.noCloneEvent = false
            });
            t.cloneNode(true).click()
        }
        for (i in {
            submit: true,
            change: true,
            focusin: true
        }) {
            t.setAttribute(p = "on" + i, "t");
            s[i + "Bubbles"] = p in w || t.attributes[p].expando === false
        }
        t.style.backgroundClip = "content-box";
        t.cloneNode(true).style.backgroundClip = "";
        s.clearCloneStyle = t.style.backgroundClip === "content-box";
        for (i in Q(s)) {
            break
        }
        s.ownLast = i !== "0";
        Q(function() {
            var v, _, s3, t3 = "padding:0;margin:0;border:0;display:block;box-sizing:content-box;-moz-box-sizing:content-box;-webkit-box-sizing:content-box;",
                u3 = h.getElementsByTagName("body")[0];
            if (!u3) {
                return
            }
            v = h.createElement("div");
            v.style.cssText = "border:0;width:0;height:0;position:absolute;top:0;left:-9999px;margin-top:1px";
            u3.appendChild(v).appendChild(t);
            t.innerHTML = "<table><tr><td></td><td>t</td></tr></table>";
            s3 = t.getElementsByTagName("td");
            s3[0].style.cssText = "padding:0;margin:0;border:0;display:none";
            r = (s3[0].offsetHeight === 0);
            s3[0].style.display = "";
            s3[1].style.display = "none";
            s.reliableHiddenOffsets = r && (s3[0].offsetHeight === 0);
            t.innerHTML = "";
            t.style.cssText = "box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box;padding:1px;border:1px;display:block;width:4px;margin-top:1%;position:absolute;top:1%;";
            Q.swap(u3, u3.style.zoom != null ? {
                zoom: 1
            } : {}, function() {
                s.boxSizing = t.offsetWidth === 4
            });
            if (w.getComputedStyle) {
                s.pixelPosition = (w.getComputedStyle(t, null) || {}).top !== "1%";
                s.boxSizingReliable = (w.getComputedStyle(t, null) || {
                    width: "4px"
                }).width === "4px";
                _ = t.appendChild(h.createElement("div"));
                _.style.cssText = t.style.cssText = t3;
                _.style.marginRight = _.style.width = "0";
                t.style.width = "1px";
                s.reliableMarginRight = !parseFloat((w.getComputedStyle(_, null) || {}).marginRight)
            }
            if (typeof t.style.zoom !== f) {
                t.innerHTML = "";
                t.style.cssText = t3 + "width:1px;padding:1px;display:inline;zoom:1";
                s.inlineBlockNeedsLayout = (t.offsetWidth === 3);
                t.style.display = "block";
                t.innerHTML = "<div></div>";
                t.firstChild.style.width = "5px";
                s.shrinkWrapBlocks = (t.offsetWidth !== 3);
                if (s.inlineBlockNeedsLayout) {
                    u3.style.zoom = 1
                }
            }
            u3.removeChild(v);
            v = t = s3 = _ = null
        });
        b = l = m = n = a = j = null;
        return s
    })({});
    var Z = /(?:\{[\s\S]*\}|\[[\s\S]*\])$/,
        $ = /([A-Z])/g;

    function a1(a, n, b, p) {
        if (!Q.acceptData(a)) {
            return
        }
        var r, t, i = Q.expando,
            j = a.nodeType,
            l = j ? Q.cache : a,
            m = j ? a[i] : a[i] && i;
        if ((!m || !l[m] || (!p && !l[m].data)) && b === u && typeof n === "string") {
            return
        }
        if (!m) {
            if (j) {
                m = a[i] = y.pop() || Q.guid++
            } else {
                m = i
            }
        }
        if (!l[m]) {
            l[m] = j ? {} : {
                toJSON: Q.noop
            }
        }
        if (typeof n === "object" || typeof n === "function") {
            if (p) {
                l[m] = Q.extend(l[m], n)
            } else {
                l[m].data = Q.extend(l[m].data, n)
            }
        }
        t = l[m];
        if (!p) {
            if (!t.data) {
                t.data = {}
            }
            t = t.data
        }
        if (b !== u) {
            t[Q.camelCase(n)] = b
        }
        if (typeof n === "string") {
            r = t[n];
            if (r == null) {
                r = t[Q.camelCase(n)]
            }
        } else {
            r = t
        }
        return r
    }
    function b1(a, n, p) {
        if (!Q.acceptData(a)) {
            return
        }
        var t, i, b = a.nodeType,
            j = b ? Q.cache : a,
            l = b ? a[Q.expando] : Q.expando;
        if (!j[l]) {
            return
        }
        if (n) {
            t = p ? j[l] : j[l].data;
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
                } else {
                    n = n.concat(Q.map(n, Q.camelCase))
                }
                i = n.length;
                while (i--) {
                    delete t[n[i]]
                }
                if (p ? !d1(t) : !Q.isEmptyObject(t)) {
                    return
                }
            }
        }
        if (!p) {
            delete j[l].data;
            if (!d1(j[l])) {
                return
            }
        }
        if (b) {
            Q.cleanData([a], true)
        } else if (Q.support.deleteExpando || j != j.window) {
            delete j[l]
        } else {
            j[l] = null
        }
    }
    Q.extend({
        cache: {},
        noData: {
            "applet": true,
            "embed": true,
            "object": "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
        },
        hasData: function(a) {
            a = a.nodeType ? Q.cache[a[Q.expando]] : a[Q.expando];
            return !!a && !d1(a)
        },
        data: function(a, n, b) {
            return a1(a, n, b)
        },
        removeData: function(a, n) {
            return b1(a, n)
        },
        _data: function(a, n, b) {
            return a1(a, n, b, true)
        },
        _removeData: function(a, n) {
            return b1(a, n, true)
        },
        acceptData: function(a) {
            if (a.nodeType && a.nodeType !== 1 && a.nodeType !== 9) {
                return false
            }
            var n = a.nodeName && Q.noData[a.nodeName.toLowerCase()];
            return !n || n !== true && a.getAttribute("classid") === n
        }
    });
    Q.fn.extend({
        data: function(a, v) {
            var b, n, j = null,
                i = 0,
                l = this[0];
            if (a === u) {
                if (this.length) {
                    j = Q.data(l);
                    if (l.nodeType === 1 && !Q._data(l, "parsedAttrs")) {
                        b = l.attributes;
                        for (; i < b.length; i++) {
                            n = b[i].name;
                            if (n.indexOf("data-") === 0) {
                                n = Q.camelCase(n.slice(5));
                                c1(l, n, j[n])
                            }
                        }
                        Q._data(l, "parsedAttrs", true)
                    }
                }
                return j
            }
            if (typeof a === "object") {
                return this.each(function() {
                    Q.data(this, a)
                })
            }
            return arguments.length > 1 ? this.each(function() {
                Q.data(this, a, v)
            }) : l ? c1(l, a, Q.data(l, a)) : null
        },
        removeData: function(a) {
            return this.each(function() {
                Q.removeData(this, a)
            })
        }
    });

    function c1(a, b, i) {
        if (i === u && a.nodeType === 1) {
            var n = "data-" + b.replace($, "-$1").toLowerCase();
            i = a.getAttribute(n);
            if (typeof i === "string") {
                try {
                    i = i === "true" ? true : i === "false" ? false : i === "null" ? null : +i + "" === i ? +i : Z.test(i) ? Q.parseJSON(i) : i
                } catch (e) {}
                Q.data(a, b, i)
            } else {
                i = u
            }
        }
        return i
    }
    function d1(a) {
        var n;
        for (n in a) {
            if (n === "data" && Q.isEmptyObject(a[n])) {
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
            var i;
            if (a) {
                t = (t || "fx") + "queue";
                i = Q._data(a, t);
                if (b) {
                    if (!i || Q.isArray(b)) {
                        i = Q._data(a, t, Q.makeArray(b))
                    } else {
                        i.push(b)
                    }
                }
                return i || []
            }
        },
        dequeue: function(a, t) {
            t = t || "fx";
            var b = Q.queue(a, t),
                s = b.length,
                i = b.shift(),
                j = Q._queueHooks(a, t),
                n = function() {
                    Q.dequeue(a, t)
                };
            if (i === "inprogress") {
                i = b.shift();
                s--
            }
            if (i) {
                if (t === "fx") {
                    b.unshift("inprogress")
                }
                delete j.stop;
                i.call(a, n, j)
            }
            if (!s && j) {
                j.empty.fire()
            }
        },
        _queueHooks: function(a, t) {
            var b = t + "queueHooks";
            return Q._data(a, b) || Q._data(a, b, {
                empty: Q.Callbacks("once memory").add(function() {
                    Q._removeData(a, t + "queue");
                    Q._removeData(a, b)
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
                var i = setTimeout(n, t);
                b.stop = function() {
                    clearTimeout(i)
                }
            })
        },
        clearQueue: function(t) {
            return this.queue(t || "fx", [])
        },
        promise: function(t, a) {
            var b, j = 1,
                l = Q.Deferred(),
                m = this,
                i = this.length,
                r = function() {
                    if (!(--j)) {
                        l.resolveWith(m, [m])
                    }
                };
            if (typeof t !== "string") {
                a = t;
                t = u
            }
            t = t || "fx";
            while (i--) {
                b = Q._data(m[i], t + "queueHooks");
                if (b && b.empty) {
                    j++;
                    b.empty.add(r)
                }
            }
            r();
            return l.promise(a)
        }
    });
    var e1, f1, g1 = /[\t\r\n\f]/g,
        h1 = /\r/g,
        i1 = /^(?:input|select|textarea|button|object)$/i,
        j1 = /^(?:a|area)$/i,
        k1 = /^(?:checked|selected)$/i,
        l1 = Q.support.getSetAttribute,
        m1 = Q.support.input;
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
            var a, b, l, m, j, i = 0,
                n = this.length,
                p = typeof v === "string" && v;
            if (Q.isFunction(v)) {
                return this.each(function(j) {
                    Q(this).addClass(v.call(this, j, this.className))
                })
            }
            if (p) {
                a = (v || "").match(I) || [];
                for (; i < n; i++) {
                    b = this[i];
                    l = b.nodeType === 1 && (b.className ? (" " + b.className + " ").replace(g1, " ") : " ");
                    if (l) {
                        j = 0;
                        while ((m = a[j++])) {
                            if (l.indexOf(" " + m + " ") < 0) {
                                l += m + " "
                            }
                        }
                        b.className = Q.trim(l)
                    }
                }
            }
            return this
        },
        removeClass: function(v) {
            var a, b, l, m, j, i = 0,
                n = this.length,
                p = arguments.length === 0 || typeof v === "string" && v;
            if (Q.isFunction(v)) {
                return this.each(function(j) {
                    Q(this).removeClass(v.call(this, j, this.className))
                })
            }
            if (p) {
                a = (v || "").match(I) || [];
                for (; i < n; i++) {
                    b = this[i];
                    l = b.nodeType === 1 && (b.className ? (" " + b.className + " ").replace(g1, " ") : "");
                    if (l) {
                        j = 0;
                        while ((m = a[j++])) {
                            while (l.indexOf(" " + m + " ") >= 0) {
                                l = l.replace(" " + m + " ", " ")
                            }
                        }
                        b.className = v ? Q.trim(l) : ""
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
                    var b, i = 0,
                        j = Q(this),
                        l = s,
                        m = v.match(I) || [];
                    while ((b = m[i++])) {
                        l = a ? l : !j.hasClass(b);
                        j[l ? "addClass" : "removeClass"](b)
                    }
                } else if (t === f || t === "boolean") {
                    if (this.className) {
                        Q._data(this, "__className__", this.className)
                    }
                    this.className = this.className || v === false ? "" : Q._data(this, "__className__") || ""
                }
            })
        },
        hasClass: function(s) {
            var a = " " + s + " ",
                i = 0,
                l = this.length;
            for (; i < l; i++) {
                if (this[i].nodeType === 1 && (" " + this[i].className + " ").replace(g1, " ").indexOf(a) >= 0) {
                    return true
                }
            }
            return false
        },
        val: function(v) {
            var r, a, b, j = this[0];
            if (!arguments.length) {
                if (j) {
                    a = Q.valHooks[j.type] || Q.valHooks[j.nodeName.toLowerCase()];
                    if (a && "get" in a && (r = a.get(j, "value")) !== u) {
                        return r
                    }
                    r = j.value;
                    return typeof r === "string" ? r.replace(h1, "") : r == null ? "" : r
                }
                return
            }
            b = Q.isFunction(v);
            return this.each(function(i) {
                var l;
                if (this.nodeType !== 1) {
                    return
                }
                if (b) {
                    l = v.call(this, i, Q(this).val())
                } else {
                    l = v
                }
                if (l == null) {
                    l = ""
                } else if (typeof l === "number") {
                    l += ""
                } else if (Q.isArray(l)) {
                    l = Q.map(l, function(v) {
                        return v == null ? "" : v + ""
                    })
                }
                a = Q.valHooks[this.type] || Q.valHooks[this.nodeName.toLowerCase()];
                if (!a || !("set" in a) || a.set(this, l, "value") === u) {
                    this.value = l
                }
            })
        }
    });
    Q.extend({
        valHooks: {
            option: {
                get: function(a) {
                    var v = Q.find.attr(a, "value");
                    return v != null ? v : a.text
                }
            },
            select: {
                get: function(a) {
                    var v, b, j = a.options,
                        l = a.selectedIndex,
                        m = a.type === "select-one" || l < 0,
                        n = m ? null : [],
                        p = m ? l + 1 : j.length,
                        i = l < 0 ? p : m ? l : 0;
                    for (; i < p; i++) {
                        b = j[i];
                        if ((b.selected || i === l) && (Q.support.optDisabled ? !b.disabled : b.getAttribute("disabled") === null) && (!b.parentNode.disabled || !Q.nodeName(b.parentNode, "optgroup"))) {
                            v = Q(b).val();
                            if (m) {
                                return v
                            }
                            n.push(v)
                        }
                    }
                    return n
                },
                set: function(a, v) {
                    var b, j, l = a.options,
                        m = Q.makeArray(v),
                        i = l.length;
                    while (i--) {
                        j = l[i];
                        if ((j.selected = Q.inArray(Q(j).val(), m) >= 0)) {
                            b = true
                        }
                    }
                    if (!b) {
                        a.selectedIndex = -1
                    }
                    return m
                }
            }
        },
        attr: function(a, n, v) {
            var b, r, i = a.nodeType;
            if (!a || i === 3 || i === 8 || i === 2) {
                return
            }
            if (typeof a.getAttribute === f) {
                return Q.prop(a, n, v)
            }
            if (i !== 1 || !Q.isXMLDoc(a)) {
                n = n.toLowerCase();
                b = Q.attrHooks[n] || (Q.expr.match.bool.test(n) ? f1 : e1)
            }
            if (v !== u) {
                if (v === null) {
                    Q.removeAttr(a, n)
                } else if (b && "set" in b && (r = b.set(a, v, n)) !== u) {
                    return r
                } else {
                    a.setAttribute(n, v + "");
                    return v
                }
            } else if (b && "get" in b && (r = b.get(a, n)) !== null) {
                return r
            } else {
                r = Q.find.attr(a, n);
                return r == null ? u : r
            }
        },
        removeAttr: function(a, v) {
            var n, p, i = 0,
                b = v && v.match(I);
            if (b && a.nodeType === 1) {
                while ((n = b[i++])) {
                    p = Q.propFix[n] || n;
                    if (Q.expr.match.bool.test(n)) {
                        if (m1 && l1 || !k1.test(n)) {
                            a[p] = false
                        } else {
                            a[Q.camelCase("default-" + n)] = a[p] = false
                        }
                    } else {
                        Q.attr(a, n, "")
                    }
                    a.removeAttribute(l1 ? n : p)
                }
            }
        },
        attrHooks: {
            type: {
                set: function(a, v) {
                    if (!Q.support.radioValue && v === "radio" && Q.nodeName(a, "input")) {
                        var b = a.value;
                        a.setAttribute("type", v);
                        if (b) {
                            a.value = b
                        }
                        return v
                    }
                }
            }
        },
        propFix: {
            "for": "htmlFor",
            "class": "className"
        },
        prop: function(a, n, v) {
            var r, b, i, j = a.nodeType;
            if (!a || j === 3 || j === 8 || j === 2) {
                return
            }
            i = j !== 1 || !Q.isXMLDoc(a);
            if (i) {
                n = Q.propFix[n] || n;
                b = Q.propHooks[n]
            }
            if (v !== u) {
                return b && "set" in b && (r = b.set(a, v, n)) !== u ? r : (a[n] = v)
            } else {
                return b && "get" in b && (r = b.get(a, n)) !== null ? r : a[n]
            }
        },
        propHooks: {
            tabIndex: {
                get: function(a) {
                    var t = Q.find.attr(a, "tabindex");
                    return t ? parseInt(t, 10) : i1.test(a.nodeName) || j1.test(a.nodeName) && a.href ? 0 : -1
                }
            }
        }
    });
    f1 = {
        set: function(a, v, n) {
            if (v === false) {
                Q.removeAttr(a, n)
            } else if (m1 && l1 || !k1.test(n)) {
                a.setAttribute(!l1 && Q.propFix[n] || n, n)
            } else {
                a[Q.camelCase("default-" + n)] = a[n] = true
            }
            return n
        }
    };
    Q.each(Q.expr.match.bool.source.match(/\w+/g), function(i, n) {
        var a = Q.expr.attrHandle[n] || Q.find.attr;
        Q.expr.attrHandle[n] = m1 && l1 || !k1.test(n) ? function(b, n, j) {
            var l = Q.expr.attrHandle[n],
                r = j ? u : (Q.expr.attrHandle[n] = u) != a(b, n, j) ? n.toLowerCase() : null;
            Q.expr.attrHandle[n] = l;
            return r
        } : function(b, n, j) {
            return j ? u : b[Q.camelCase("default-" + n)] ? n.toLowerCase() : null
        }
    });
    if (!m1 || !l1) {
        Q.attrHooks.value = {
            set: function(a, v, n) {
                if (Q.nodeName(a, "input")) {
                    a.defaultValue = v
                } else {
                    return e1 && e1.set(a, v, n)
                }
            }
        }
    }
    if (!l1) {
        e1 = {
            set: function(a, v, n) {
                var r = a.getAttributeNode(n);
                if (!r) {
                    a.setAttributeNode((r = a.ownerDocument.createAttribute(n)))
                }
                r.value = v += "";
                return n === "value" || v === a.getAttribute(n) ? v : u
            }
        };
        Q.expr.attrHandle.id = Q.expr.attrHandle.name = Q.expr.attrHandle.coords = function(a, n, i) {
            var r;
            return i ? u : (r = a.getAttributeNode(n)) && r.value !== "" ? r.value : null
        };
        Q.valHooks.button = {
            get: function(a, n) {
                var r = a.getAttributeNode(n);
                return r && r.specified ? r.value : u
            },
            set: e1.set
        };
        Q.attrHooks.contenteditable = {
            set: function(a, v, n) {
                e1.set(a, v === "" ? false : v, n)
            }
        };
        Q.each(["width", "height"], function(i, n) {
            Q.attrHooks[n] = {
                set: function(a, v) {
                    if (v === "") {
                        a.setAttribute(n, "auto");
                        return v
                    }
                }
            }
        })
    }
    if (!Q.support.hrefNormalized) {
        Q.each(["href", "src"], function(i, n) {
            Q.propHooks[n] = {
                get: function(a) {
                    return a.getAttribute(n, 4)
                }
            }
        })
    }
    if (!Q.support.style) {
        Q.attrHooks.style = {
            get: function(a) {
                return a.style.cssText || u
            },
            set: function(a, v) {
                return (a.style.cssText = v + "")
            }
        }
    }
    if (!Q.support.optSelected) {
        Q.propHooks.selected = {
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
        }
    }
    Q.each(["tabIndex", "readOnly", "maxLength", "cellSpacing", "cellPadding", "rowSpan", "colSpan", "useMap", "frameBorder", "contentEditable"], function() {
        Q.propFix[this.toLowerCase()] = this
    });
    if (!Q.support.enctype) {
        Q.propFix.enctype = "encoding"
    }
    Q.each(["radio", "checkbox"], function() {
        Q.valHooks[this] = {
            set: function(a, v) {
                if (Q.isArray(v)) {
                    return (a.checked = Q.inArray(Q(a).val(), v) >= 0)
                }
            }
        };
        if (!Q.support.checkOn) {
            Q.valHooks[this].get = function(a) {
                return a.getAttribute("value") === null ? "on" : a.value
            }
        }
    });
    var n1 = /^(?:input|select|textarea)$/i,
        o1 = /^key/,
        p1 = /^(?:mouse|contextmenu)|click/,
        q1 = /^(?:focusinfocus|focusoutblur)$/,
        r1 = /^([^.]*)(?:\.(.+)|)$/;

    function s1() {
        return true
    }
    function t1() {
        return false
    }
    function u1() {
        try {
            return h.activeElement
        } catch (a) {}
    }
    Q.event = {
        global: {},
        add: function(a, b, i, j, s) {
            var l, m, t, n, p, r, v, _, s3, t3, u3, v3 = Q._data(a);
            if (!v3) {
                return
            }
            if (i.handler) {
                n = i;
                i = n.handler;
                s = n.selector
            }
            if (!i.guid) {
                i.guid = Q.guid++
            }
            if (!(m = v3.events)) {
                m = v3.events = {}
            }
            if (!(r = v3.handle)) {
                r = v3.handle = function(e) {
                    return typeof Q !== f && (!e || Q.event.triggered !== e.type) ? Q.event.dispatch.apply(r.elem, arguments) : u
                };
                r.elem = a
            }
            b = (b || "").match(I) || [""];
            t = b.length;
            while (t--) {
                l = r1.exec(b[t]) || [];
                s3 = u3 = l[1];
                t3 = (l[2] || "").split(".").sort();
                if (!s3) {
                    continue
                }
                p = Q.event.special[s3] || {};
                s3 = (s ? p.delegateType : p.bindType) || s3;
                p = Q.event.special[s3] || {};
                v = Q.extend({
                    type: s3,
                    origType: u3,
                    data: j,
                    handler: i,
                    guid: i.guid,
                    selector: s,
                    needsContext: s && Q.expr.match.needsContext.test(s),
                    namespace: t3.join(".")
                }, n);
                if (!(_ = m[s3])) {
                    _ = m[s3] = [];
                    _.delegateCount = 0;
                    if (!p.setup || p.setup.call(a, j, t3, r) === false) {
                        if (a.addEventListener) {
                            a.addEventListener(s3, r, false)
                        } else if (a.attachEvent) {
                            a.attachEvent("on" + s3, r)
                        }
                    }
                }
                if (p.add) {
                    p.add.call(a, v);
                    if (!v.handler.guid) {
                        v.handler.guid = i.guid
                    }
                }
                if (s) {
                    _.splice(_.delegateCount++, 0, v)
                } else {
                    _.push(v)
                }
                Q.event.global[s3] = true
            }
            a = null
        },
        remove: function(a, b, i, s, m) {
            var j, l, n, p, t, r, v, _, s3, t3, u3, v3 = Q.hasData(a) && Q._data(a);
            if (!v3 || !(r = v3.events)) {
                return
            }
            b = (b || "").match(I) || [""];
            t = b.length;
            while (t--) {
                n = r1.exec(b[t]) || [];
                s3 = u3 = n[1];
                t3 = (n[2] || "").split(".").sort();
                if (!s3) {
                    for (s3 in r) {
                        Q.event.remove(a, s3 + b[t], i, s, true)
                    }
                    continue
                }
                v = Q.event.special[s3] || {};
                s3 = (s ? v.delegateType : v.bindType) || s3;
                _ = r[s3] || [];
                n = n[2] && new RegExp("(^|\\.)" + t3.join("\\.(?:.*\\.|)") + "(\\.|$)");
                p = j = _.length;
                while (j--) {
                    l = _[j];
                    if ((m || u3 === l.origType) && (!i || i.guid === l.guid) && (!n || n.test(l.namespace)) && (!s || s === l.selector || s === "**" && l.selector)) {
                        _.splice(j, 1);
                        if (l.selector) {
                            _.delegateCount--
                        }
                        if (v.remove) {
                            v.remove.call(a, l)
                        }
                    }
                }
                if (p && !_.length) {
                    if (!v.teardown || v.teardown.call(a, t3, v3.handle) === false) {
                        Q.removeEvent(a, s3, v3.handle)
                    }
                    delete r[s3]
                }
            }
            if (Q.isEmptyObject(r)) {
                delete v3.handle;
                Q._removeData(a, "events")
            }
        },
        trigger: function(a, b, j, l) {
            var m, n, p, r, s, t, i, v = [j || h],
                _ = F.call(a, "type") ? a.type : a,
                s3 = F.call(a, "namespace") ? a.namespace.split(".") : [];
            p = t = j = j || h;
            if (j.nodeType === 3 || j.nodeType === 8) {
                return
            }
            if (q1.test(_ + Q.event.triggered)) {
                return
            }
            if (_.indexOf(".") >= 0) {
                s3 = _.split(".");
                _ = s3.shift();
                s3.sort()
            }
            n = _.indexOf(":") < 0 && "on" + _;
            a = a[Q.expando] ? a : new Q.Event(_, typeof a === "object" && a);
            a.isTrigger = l ? 2 : 3;
            a.namespace = s3.join(".");
            a.namespace_re = a.namespace ? new RegExp("(^|\\.)" + s3.join("\\.(?:.*\\.|)") + "(\\.|$)") : null;
            a.result = u;
            if (!a.target) {
                a.target = j
            }
            b = b == null ? [a] : Q.makeArray(b, [a]);
            s = Q.event.special[_] || {};
            if (!l && s.trigger && s.trigger.apply(j, b) === false) {
                return
            }
            if (!l && !s.noBubble && !Q.isWindow(j)) {
                r = s.delegateType || _;
                if (!q1.test(r + _)) {
                    p = p.parentNode
                }
                for (; p; p = p.parentNode) {
                    v.push(p);
                    t = p
                }
                if (t === (j.ownerDocument || h)) {
                    v.push(t.defaultView || t.parentWindow || w)
                }
            }
            i = 0;
            while ((p = v[i++]) && !a.isPropagationStopped()) {
                a.type = i > 1 ? r : s.bindType || _;
                m = (Q._data(p, "events") || {})[a.type] && Q._data(p, "handle");
                if (m) {
                    m.apply(p, b)
                }
                m = n && p[n];
                if (m && Q.acceptData(p) && m.apply && m.apply(p, b) === false) {
                    a.preventDefault()
                }
            }
            a.type = _;
            if (!l && !a.isDefaultPrevented()) {
                if ((!s._default || s._default.apply(v.pop(), b) === false) && Q.acceptData(j)) {
                    if (n && j[_] && !Q.isWindow(j)) {
                        t = j[n];
                        if (t) {
                            j[n] = null
                        }
                        Q.event.triggered = _;
                        try {
                            j[_]()
                        } catch (e) {}
                        Q.event.triggered = u;
                        if (t) {
                            j[n] = t
                        }
                    }
                }
            }
            return a.result
        },
        dispatch: function(a) {
            a = Q.event.fix(a);
            var i, r, b, m, j, l = [],
                n = C.call(arguments),
                p = (Q._data(this, "events") || {})[a.type] || [],
                s = Q.event.special[a.type] || {};
            n[0] = a;
            a.delegateTarget = this;
            if (s.preDispatch && s.preDispatch.call(this, a) === false) {
                return
            }
            l = Q.event.handlers.call(this, a, p);
            i = 0;
            while ((m = l[i++]) && !a.isPropagationStopped()) {
                a.currentTarget = m.elem;
                j = 0;
                while ((b = m.handlers[j++]) && !a.isImmediatePropagationStopped()) {
                    if (!a.namespace_re || a.namespace_re.test(b.namespace)) {
                        a.handleObj = b;
                        a.data = b.data;
                        r = ((Q.event.special[b.origType] || {}).handle || b.handler).apply(m.elem, n);
                        if (r !== u) {
                            if ((a.result = r) === false) {
                                a.preventDefault();
                                a.stopPropagation()
                            }
                        }
                    }
                }
            }
            if (s.postDispatch) {
                s.postDispatch.call(this, a)
            }
            return a.result
        },
        handlers: function(a, b) {
            var s, j, m, i, l = [],
                n = b.delegateCount,
                p = a.target;
            if (n && p.nodeType && (!a.button || a.type !== "click")) {
                for (; p != this; p = p.parentNode || this) {
                    if (p.nodeType === 1 && (p.disabled !== true || a.type !== "click")) {
                        m = [];
                        for (i = 0; i < n; i++) {
                            j = b[i];
                            s = j.selector + " ";
                            if (m[s] === u) {
                                m[s] = j.needsContext ? Q(s, this).index(p) >= 0 : Q.find(s, this, null, [p]).length
                            }
                            if (m[s]) {
                                m.push(j)
                            }
                        }
                        if (m.length) {
                            l.push({
                                elem: p,
                                handlers: m
                            })
                        }
                    }
                }
            }
            if (n < b.length) {
                l.push({
                    elem: this,
                    handlers: b.slice(n)
                })
            }
            return l
        },
        fix: function(a) {
            if (a[Q.expando]) {
                return a
            }
            var i, p, b, t = a.type,
                j = a,
                l = this.fixHooks[t];
            if (!l) {
                this.fixHooks[t] = l = p1.test(t) ? this.mouseHooks : o1.test(t) ? this.keyHooks : {}
            }
            b = l.props ? this.props.concat(l.props) : this.props;
            a = new Q.Event(j);
            i = b.length;
            while (i--) {
                p = b[i];
                a[p] = j[p]
            }
            if (!a.target) {
                a.target = j.srcElement || h
            }
            if (a.target.nodeType === 3) {
                a.target = a.target.parentNode
            }
            a.metaKey = !! a.metaKey;
            return l.filter ? l.filter(a, j) : a
        },
        props: "altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),
        fixHooks: {},
        keyHooks: {
            props: "char charCode key keyCode".split(" "),
            filter: function(a, b) {
                if (a.which == null) {
                    a.which = b.charCode != null ? b.charCode : b.keyCode
                }
                return a
            }
        },
        mouseHooks: {
            props: "button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
            filter: function(a, b) {
                var i, j, l, m = b.button,
                    n = b.fromElement;
                if (a.pageX == null && b.clientX != null) {
                    j = a.target.ownerDocument || h;
                    l = j.documentElement;
                    i = j.body;
                    a.pageX = b.clientX + (l && l.scrollLeft || i && i.scrollLeft || 0) - (l && l.clientLeft || i && i.clientLeft || 0);
                    a.pageY = b.clientY + (l && l.scrollTop || i && i.scrollTop || 0) - (l && l.clientTop || i && i.clientTop || 0)
                }
                if (!a.relatedTarget && n) {
                    a.relatedTarget = n === a.target ? b.toElement : n
                }
                if (!a.which && m !== u) {
                    a.which = (m & 1 ? 1 : (m & 2 ? 3 : (m & 4 ? 2 : 0)))
                }
                return a
            }
        },
        special: {
            load: {
                noBubble: true
            },
            focus: {
                trigger: function() {
                    if (this !== u1() && this.focus) {
                        try {
                            this.focus();
                            return false
                        } catch (e) {}
                    }
                },
                delegateType: "focusin"
            },
            blur: {
                trigger: function() {
                    if (this === u1() && this.blur) {
                        this.blur();
                        return false
                    }
                },
                delegateType: "focusout"
            },
            click: {
                trigger: function() {
                    if (Q.nodeName(this, "input") && this.type === "checkbox" && this.click) {
                        this.click();
                        return false
                    }
                },
                _default: function(a) {
                    return Q.nodeName(a.target, "a")
                }
            },
            beforeunload: {
                postDispatch: function(a) {
                    if (a.result !== u) {
                        a.originalEvent.returnValue = a.result
                    }
                }
            }
        },
        simulate: function(t, a, b, i) {
            var e = Q.extend(new Q.Event(), b, {
                type: t,
                isSimulated: true,
                originalEvent: {}
            });
            if (i) {
                Q.event.trigger(e, null, a)
            } else {
                Q.event.dispatch.call(a, e)
            }
            if (e.isDefaultPrevented()) {
                b.preventDefault()
            }
        }
    };
    Q.removeEvent = h.removeEventListener ? function(a, t, b) {
        if (a.removeEventListener) {
            a.removeEventListener(t, b, false)
        }
    } : function(a, t, b) {
        var n = "on" + t;
        if (a.detachEvent) {
            if (typeof a[n] === f) {
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
            this.isDefaultPrevented = (s.defaultPrevented || s.returnValue === false || s.getPreventDefault && s.getPreventDefault()) ? s1 : t1
        } else {
            this.type = s
        }
        if (p) {
            Q.extend(this, p)
        }
        this.timeStamp = s && s.timeStamp || Q.now();
        this[Q.expando] = true
    };
    Q.Event.prototype = {
        isDefaultPrevented: t1,
        isPropagationStopped: t1,
        isImmediatePropagationStopped: t1,
        preventDefault: function() {
            var e = this.originalEvent;
            this.isDefaultPrevented = s1;
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
            var e = this.originalEvent;
            this.isPropagationStopped = s1;
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
        }
    };
    Q.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function(a, b) {
        Q.event.special[a] = {
            delegateType: b,
            bindType: b,
            handle: function(i) {
                var r, t = this,
                    j = i.relatedTarget,
                    l = i.handleObj;
                if (!j || (j !== t && !Q.contains(t, j))) {
                    i.type = l.origType;
                    r = l.handler.apply(this, arguments);
                    i.type = b
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
                        b = Q.nodeName(a, "input") || Q.nodeName(a, "button") ? a.form : u;
                    if (b && !Q._data(b, "submitBubbles")) {
                        Q.event.add(b, "submit._submit", function(i) {
                            i._submit_bubble = true
                        });
                        Q._data(b, "submitBubbles", true)
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
                if (n1.test(this.nodeName)) {
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
                    if (n1.test(a.nodeName) && !Q._data(a, "changeBubbles")) {
                        Q.event.add(a, "change._change", function(b) {
                            if (this.parentNode && !b.isSimulated && !b.isTrigger) {
                                Q.event.simulate("change", this.parentNode, b, true)
                            }
                        });
                        Q._data(a, "changeBubbles", true)
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
                return !n1.test(this.nodeName)
            }
        }
    }
    if (!Q.support.focusinBubbles) {
        Q.each({
            focus: "focusin",
            blur: "focusout"
        }, function(a, b) {
            var i = 0,
                j = function(l) {
                    Q.event.simulate(b, l.target, Q.event.fix(l), true)
                };
            Q.event.special[b] = {
                setup: function() {
                    if (i++ === 0) {
                        h.addEventListener(a, j, true)
                    }
                },
                teardown: function() {
                    if (--i === 0) {
                        h.removeEventListener(a, j, true)
                    }
                }
            }
        })
    }
    Q.fn.extend({
        on: function(t, s, a, b, i) {
            var j, l;
            if (typeof t === "object") {
                if (typeof s !== "string") {
                    a = a || s;
                    s = u
                }
                for (j in t) {
                    this.on(j, s, a, t[j], i)
                }
                return this
            }
            if (a == null && b == null) {
                b = s;
                a = s = u
            } else if (b == null) {
                if (typeof s === "string") {
                    b = a;
                    a = u
                } else {
                    b = a;
                    a = s;
                    s = u
                }
            }
            if (b === false) {
                b = t1
            } else if (!b) {
                return this
            }
            if (i === 1) {
                l = b;
                b = function(m) {
                    Q().off(m);
                    return l.apply(this, arguments)
                };
                b.guid = l.guid || (l.guid = Q.guid++)
            }
            return this.each(function() {
                Q.event.add(this, t, b, a, s)
            })
        },
        one: function(t, s, a, b) {
            return this.on(t, s, a, b, 1)
        },
        off: function(t, s, a) {
            var b, i;
            if (t && t.preventDefault && t.handleObj) {
                b = t.handleObj;
                Q(t.delegateTarget).off(b.namespace ? b.origType + "." + b.namespace : b.origType, b.selector, b.handler);
                return this
            }
            if (typeof t === "object") {
                for (i in t) {
                    this.off(i, s, t[i])
                }
                return this
            }
            if (s === false || typeof s === "function") {
                a = s;
                s = u
            }
            if (a === false) {
                a = t1
            }
            return this.each(function() {
                Q.event.remove(this, t, a, s)
            })
        },
        trigger: function(t, a) {
            return this.each(function() {
                Q.event.trigger(t, a, this)
            })
        },
        triggerHandler: function(t, a) {
            var b = this[0];
            if (b) {
                return Q.event.trigger(t, a, b, true)
            }
        }
    });
    var v1 = /^.[^:#\[\.,]*$/,
        w1 = /^(?:parents|prev(?:Until|All))/,
        x1 = Q.expr.match.needsContext,
        y1 = {
            children: true,
            contents: true,
            next: true,
            prev: true
        };
    Q.fn.extend({
        find: function(s) {
            var i, r = [],
                a = this,
                l = a.length;
            if (typeof s !== "string") {
                return this.pushStack(Q(s).filter(function() {
                    for (i = 0; i < l; i++) {
                        if (Q.contains(a[i], this)) {
                            return true
                        }
                    }
                }))
            }
            for (i = 0; i < l; i++) {
                Q.find(s, a[i], r)
            }
            r = this.pushStack(l > 1 ? Q.unique(r) : r);
            r.selector = this.selector ? this.selector + " " + s : s;
            return r
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
            return this.pushStack(A1(this, s || [], true))
        },
        filter: function(s) {
            return this.pushStack(A1(this, s || [], false))
        },
        is: function(s) {
            return !!A1(this, typeof s === "string" && x1.test(s) ? Q(s) : s || [], false).length
        },
        closest: function(s, a) {
            var b, i = 0,
                l = this.length,
                r = [],
                p = x1.test(s) || typeof s !== "string" ? Q(s, a || this.context) : 0;
            for (; i < l; i++) {
                for (b = this[i]; b && b !== a; b = b.parentNode) {
                    if (b.nodeType < 11 && (p ? p.index(b) > -1 : b.nodeType === 1 && Q.find.matchesSelector(b, s))) {
                        b = r.push(b);
                        break
                    }
                }
            }
            return this.pushStack(r.length > 1 ? Q.unique(r) : r)
        },
        index: function(a) {
            if (!a) {
                return (this[0] && this[0].parentNode) ? this.first().prevAll().length : -1
            }
            if (typeof a === "string") {
                return Q.inArray(this[0], Q(a))
            }
            return Q.inArray(a.jquery ? a[0] : a, this)
        },
        add: function(s, a) {
            var b = typeof s === "string" ? Q(s, a) : Q.makeArray(s && s.nodeType ? [s] : s),
                i = Q.merge(this.get(), b);
            return this.pushStack(Q.unique(i))
        },
        addBack: function(s) {
            return this.add(s == null ? this.prevObject : this.prevObject.filter(s))
        }
    });

    function z1(a, b) {
        do {
            a = a[b]
        } while (a && a.nodeType !== 1);
        return a
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
    }, function(n, a) {
        Q.fn[n] = function(b, s) {
            var r = Q.map(this, a, b);
            if (n.slice(-5) !== "Until") {
                s = b
            }
            if (s && typeof s === "string") {
                r = Q.filter(s, r)
            }
            if (this.length > 1) {
                if (!y1[n]) {
                    r = Q.unique(r)
                }
                if (w1.test(n)) {
                    r = r.reverse()
                }
            }
            return this.pushStack(r)
        }
    });
    Q.extend({
        filter: function(a, b, n) {
            var i = b[0];
            if (n) {
                a = ":not(" + a + ")"
            }
            return b.length === 1 && i.nodeType === 1 ? Q.find.matchesSelector(i, a) ? [i] : [] : Q.find.matches(a, Q.grep(b, function(i) {
                return i.nodeType === 1
            }))
        },
        dir: function(a, b, i) {
            var m = [],
                j = a[b];
            while (j && j.nodeType !== 9 && (i === u || j.nodeType !== 1 || !Q(j).is(i))) {
                if (j.nodeType === 1) {
                    m.push(j)
                }
                j = j[b]
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

    function A1(a, b, n) {
        if (Q.isFunction(b)) {
            return Q.grep(a, function(j, i) {
                return !!b.call(j, i, j) !== n
            })
        }
        if (b.nodeType) {
            return Q.grep(a, function(i) {
                return (i === b) !== n
            })
        }
        if (typeof b === "string") {
            if (v1.test(b)) {
                return Q.filter(b, a, n)
            }
            b = Q.filter(b, a)
        }
        return Q.grep(a, function(i) {
            return (Q.inArray(i, b) >= 0) !== n
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
        E1 = new RegExp("<(?:" + C1 + ")[\\s/>]", "i"),
        F1 = /^\s+/,
        G1 = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,
        H1 = /<([\w:]+)/,
        I1 = /<tbody/i,
        J1 = /<|&#?\w+;/,
        K1 = /<(?:script|style|link)/i,
        L1 = /^(?:checkbox|radio)$/i,
        M1 = /checked\s*(?:[^=]|=\s*.checked.)/i,
        N1 = /^$|\/(?:java|ecma)script/i,
        O1 = /^true\/(.*)/,
        P1 = /^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g,
        Q1 = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            legend: [1, "<fieldset>", "</fieldset>"],
            area: [1, "<map>", "</map>"],
            param: [1, "<object>", "</object>"],
            thead: [1, "<table>", "</table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            _default: Q.support.htmlSerialize ? [0, "", ""] : [1, "X<div>", "</div>"]
        }, R1 = B1(h),
        S1 = R1.appendChild(h.createElement("div"));
    Q1.optgroup = Q1.option;
    Q1.tbody = Q1.tfoot = Q1.colgroup = Q1.caption = Q1.thead;
    Q1.th = Q1.td;
    Q.fn.extend({
        text: function(v) {
            return Q.access(this, function(v) {
                return v === u ? Q.text(this) : this.empty().append((this[0] && this[0].ownerDocument || h).createTextNode(v))
            }, null, v, arguments.length)
        },
        append: function() {
            return this.domManip(arguments, function(a) {
                if (this.nodeType === 1 || this.nodeType === 11 || this.nodeType === 9) {
                    var t = T1(this, a);
                    t.appendChild(a)
                }
            })
        },
        prepend: function() {
            return this.domManip(arguments, function(a) {
                if (this.nodeType === 1 || this.nodeType === 11 || this.nodeType === 9) {
                    var t = T1(this, a);
                    t.insertBefore(a, t.firstChild)
                }
            })
        },
        before: function() {
            return this.domManip(arguments, function(a) {
                if (this.parentNode) {
                    this.parentNode.insertBefore(a, this)
                }
            })
        },
        after: function() {
            return this.domManip(arguments, function(a) {
                if (this.parentNode) {
                    this.parentNode.insertBefore(a, this.nextSibling)
                }
            })
        },
        remove: function(s, a) {
            var b, j = s ? Q.filter(s, this) : this,
                i = 0;
            for (;
            (b = j[i]) != null; i++) {
                if (!a && b.nodeType === 1) {
                    Q.cleanData(Z1(b))
                }
                if (b.parentNode) {
                    if (a && Q.contains(b.ownerDocument, b)) {
                        W1(Z1(b, "script"))
                    }
                    b.parentNode.removeChild(b)
                }
            }
            return this
        },
        empty: function() {
            var a, i = 0;
            for (;
            (a = this[i]) != null; i++) {
                if (a.nodeType === 1) {
                    Q.cleanData(Z1(a, false))
                }
                while (a.firstChild) {
                    a.removeChild(a.firstChild)
                }
                if (a.options && Q.nodeName(a, "select")) {
                    a.options.length = 0
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
                if (typeof v === "string" && !K1.test(v) && (Q.support.htmlSerialize || !E1.test(v)) && (Q.support.leadingWhitespace || !F1.test(v)) && !Q1[(H1.exec(v) || ["", ""])[1].toLowerCase()]) {
                    v = v.replace(G1, "<$1></$2>");
                    try {
                        for (; i < l; i++) {
                            a = this[i] || {};
                            if (a.nodeType === 1) {
                                Q.cleanData(Z1(a, false));
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
        replaceWith: function() {
            var a = Q.map(this, function(b) {
                return [b.nextSibling, b.parentNode]
            }),
                i = 0;
            this.domManip(arguments, function(b) {
                var n = a[i++],
                    p = a[i++];
                if (p) {
                    if (n && n.parentNode !== p) {
                        n = this.nextSibling
                    }
                    Q(this).remove();
                    p.insertBefore(b, n)
                }
            }, true);
            return i ? this : this.remove()
        },
        detach: function(s) {
            return this.remove(s, true)
        },
        domManip: function(a, b, j) {
            a = A.apply([], a);
            var m, n, p, s, r, t, i = 0,
                l = this.length,
                v = this,
                _ = l - 1,
                s3 = a[0],
                t3 = Q.isFunction(s3);
            if (t3 || !(l <= 1 || typeof s3 !== "string" || Q.support.checkClone || !M1.test(s3))) {
                return this.each(function(u3) {
                    var v3 = v.eq(u3);
                    if (t3) {
                        a[0] = s3.call(this, u3, v3.html())
                    }
                    v3.domManip(a, b, j)
                })
            }
            if (l) {
                t = Q.buildFragment(a, this[0].ownerDocument, false, !j && this);
                m = t.firstChild;
                if (t.childNodes.length === 1) {
                    t = m
                }
                if (m) {
                    s = Q.map(Z1(t, "script"), U1);
                    p = s.length;
                    for (; i < l; i++) {
                        n = t;
                        if (i !== _) {
                            n = Q.clone(n, true, true);
                            if (p) {
                                Q.merge(s, Z1(n, "script"))
                            }
                        }
                        b.call(this[i], n, i)
                    }
                    if (p) {
                        r = s[s.length - 1].ownerDocument;
                        Q.map(s, V1);
                        for (i = 0; i < p; i++) {
                            n = s[i];
                            if (N1.test(n.type || "") && !Q._data(n, "globalEval") && Q.contains(r, n)) {
                                if (n.src) {
                                    Q._evalUrl(n.src)
                                } else {
                                    Q.globalEval((n.text || n.textContent || n.innerHTML || "").replace(P1, ""))
                                }
                            }
                        }
                    }
                    t = m = null
                }
            }
            return this
        }
    });

    function T1(a, b) {
        return Q.nodeName(a, "table") && Q.nodeName(b.nodeType === 1 ? b : b.firstChild, "tr") ? a.getElementsByTagName("tbody")[0] || a.appendChild(a.ownerDocument.createElement("tbody")) : a
    }
    function U1(a) {
        a.type = (Q.find.attr(a, "type") !== null) + "/" + a.type;
        return a
    }
    function V1(a) {
        var m = O1.exec(a.type);
        if (m) {
            a.type = m[1]
        } else {
            a.removeAttribute("type")
        }
        return a
    }
    function W1(a, r) {
        var b, i = 0;
        for (;
        (b = a[i]) != null; i++) {
            Q._data(b, "globalEval", !r || Q._data(r[i], "globalEval"))
        }
    }
    function X1(s, a) {
        if (a.nodeType !== 1 || !Q.hasData(s)) {
            return
        }
        var t, i, l, b = Q._data(s),
            j = Q._data(a, b),
            m = b.events;
        if (m) {
            delete j.handle;
            j.events = {};
            for (t in m) {
                for (i = 0, l = m[t].length; i < l; i++) {
                    Q.event.add(a, t, m[t][i])
                }
            }
        }
        if (j.data) {
            j.data = Q.extend({}, j.data)
        }
    }
    function Y1(s, a) {
        var n, e, b;
        if (a.nodeType !== 1) {
            return
        }
        n = a.nodeName.toLowerCase();
        if (!Q.support.noCloneEvent && a[Q.expando]) {
            b = Q._data(a);
            for (e in b.events) {
                Q.removeEvent(a, e, b.handle)
            }
            a.removeAttribute(Q.expando)
        }
        if (n === "script" && a.text !== s.text) {
            U1(a).text = s.text;
            V1(a)
        } else if (n === "object") {
            if (a.parentNode) {
                a.outerHTML = s.outerHTML
            }
            if (Q.support.html5Clone && (s.innerHTML && !Q.trim(a.innerHTML))) {
                a.innerHTML = s.innerHTML
            }
        } else if (n === "input" && L1.test(s.type)) {
            a.defaultChecked = a.checked = s.checked;
            if (a.value !== s.value) {
                a.value = s.value
            }
        } else if (n === "option") {
            a.defaultSelected = a.selected = s.defaultSelected
        } else if (n === "input" || n === "textarea") {
            a.defaultValue = s.defaultValue
        }
    }
    Q.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function(n, a) {
        Q.fn[n] = function(s) {
            var b, i = 0,
                r = [],
                j = Q(s),
                l = j.length - 1;
            for (; i <= l; i++) {
                b = i === l ? this : this.clone(true);
                Q(j[i])[a](b);
                B.apply(r, b.get())
            }
            return this.pushStack(r)
        }
    });

    function Z1(a, t) {
        var b, j, i = 0,
            l = typeof a.getElementsByTagName !== f ? a.getElementsByTagName(t || "*") : typeof a.querySelectorAll !== f ? a.querySelectorAll(t || "*") : u;
        if (!l) {
            for (l = [], b = a.childNodes || a;
            (j = b[i]) != null; i++) {
                if (!t || Q.nodeName(j, t)) {
                    l.push(j)
                } else {
                    Q.merge(l, Z1(j, t))
                }
            }
        }
        return t === u || t && Q.nodeName(a, t) ? Q.merge([a], l) : l
    }
    function $1(a) {
        if (L1.test(a.type)) {
            a.defaultChecked = a.checked
        }
    }
    Q.extend({
        clone: function(a, b, j) {
            var l, n, m, i, s, p = Q.contains(a.ownerDocument, a);
            if (Q.support.html5Clone || Q.isXMLDoc(a) || !E1.test("<" + a.nodeName + ">")) {
                m = a.cloneNode(true)
            } else {
                S1.innerHTML = a.outerHTML;
                S1.removeChild(m = S1.firstChild)
            }
            if ((!Q.support.noCloneEvent || !Q.support.noCloneChecked) && (a.nodeType === 1 || a.nodeType === 11) && !Q.isXMLDoc(a)) {
                l = Z1(m);
                s = Z1(a);
                for (i = 0;
                (n = s[i]) != null; ++i) {
                    if (l[i]) {
                        Y1(n, l[i])
                    }
                }
            }
            if (b) {
                if (j) {
                    s = s || Z1(a);
                    l = l || Z1(m);
                    for (i = 0;
                    (n = s[i]) != null; i++) {
                        X1(n, l[i])
                    }
                } else {
                    X1(a, m)
                }
            }
            l = Z1(m, "script");
            if (l.length > 0) {
                W1(l, !p && Z1(a, "script"))
            }
            l = s = n = null;
            return m
        },
        buildFragment: function(a, b, s, m) {
            var j, n, p, t, r, v, _, l = a.length,
                s3 = B1(b),
                t3 = [],
                i = 0;
            for (; i < l; i++) {
                n = a[i];
                if (n || n === 0) {
                    if (Q.type(n) === "object") {
                        Q.merge(t3, n.nodeType ? [n] : n)
                    } else if (!J1.test(n)) {
                        t3.push(b.createTextNode(n))
                    } else {
                        t = t || s3.appendChild(b.createElement("div"));
                        r = (H1.exec(n) || ["", ""])[1].toLowerCase();
                        _ = Q1[r] || Q1._default;
                        t.innerHTML = _[1] + n.replace(G1, "<$1></$2>") + _[2];
                        j = _[0];
                        while (j--) {
                            t = t.lastChild
                        }
                        if (!Q.support.leadingWhitespace && F1.test(n)) {
                            t3.push(b.createTextNode(F1.exec(n)[0]))
                        }
                        if (!Q.support.tbody) {
                            n = r === "table" && !I1.test(n) ? t.firstChild : _[1] === "<table>" && !I1.test(n) ? t : 0;
                            j = n && n.childNodes.length;
                            while (j--) {
                                if (Q.nodeName((v = n.childNodes[j]), "tbody") && !v.childNodes.length) {
                                    n.removeChild(v)
                                }
                            }
                        }
                        Q.merge(t3, t.childNodes);
                        t.textContent = "";
                        while (t.firstChild) {
                            t.removeChild(t.firstChild)
                        }
                        t = s3.lastChild
                    }
                }
            }
            if (t) {
                s3.removeChild(t)
            }
            if (!Q.support.appendChecked) {
                Q.grep(Z1(t3, "input"), $1)
            }
            i = 0;
            while ((n = t3[i++])) {
                if (m && Q.inArray(n, m) !== -1) {
                    continue
                }
                p = Q.contains(n.ownerDocument, n);
                t = Z1(s3.appendChild(n), "script");
                if (p) {
                    W1(t)
                }
                if (s) {
                    j = 0;
                    while ((n = t[j++])) {
                        if (N1.test(n.type || "")) {
                            s.push(n)
                        }
                    }
                }
            }
            t = null;
            return s3
        },
        cleanData: function(a, b) {
            var j, t, l, m, i = 0,
                n = Q.expando,
                p = Q.cache,
                r = Q.support.deleteExpando,
                s = Q.event.special;
            for (;
            (j = a[i]) != null; i++) {
                if (b || Q.acceptData(j)) {
                    l = j[n];
                    m = l && p[l];
                    if (m) {
                        if (m.events) {
                            for (t in m.events) {
                                if (s[t]) {
                                    Q.event.remove(j, t)
                                } else {
                                    Q.removeEvent(j, t, m.handle)
                                }
                            }
                        }
                        if (p[l]) {
                            delete p[l];
                            if (r) {
                                delete j[n]
                            } else if (typeof j.removeAttribute !== f) {
                                j.removeAttribute(n)
                            } else {
                                j[n] = null
                            }
                            y.push(l)
                        }
                    }
                }
            }
        },
        _evalUrl: function(a) {
            return Q.ajax({
                url: a,
                type: "GET",
                dataType: "script",
                async :false,
                global: false,
                "throws": true
            })
        }
    });
    Q.fn.extend({
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
                    var i = this;
                    while (i.firstChild && i.firstChild.nodeType === 1) {
                        i = i.firstChild
                    }
                    return i
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
                    b = s.contents();
                if (b.length) {
                    b.wrapAll(a)
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
        }
    });
    var _1, a2, b2, c2 = /alpha\([^)]*\)/i,
        d2 = /opacity\s*=\s*([^)]*)/,
        e2 = /^(top|right|bottom|left)$/,
        f2 = /^(none|table(?!-c[ea]).+)/,
        g2 = /^margin/,
        h2 = new RegExp("^(" + H + ")(.*)$", "i"),
        i2 = new RegExp("^(" + H + ")(?!px)[a-z%]+$", "i"),
        j2 = new RegExp("^([+-])=(" + H + ")", "i"),
        k2 = {
            BODY: "block"
        }, l2 = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        }, m2 = {
            letterSpacing: 0,
            fontWeight: 400
        }, n2 = ["Top", "Right", "Bottom", "Left"],
        o2 = ["Webkit", "O", "Moz", "ms"];

    function p2(s, n) {
        if (n in s) {
            return n
        }
        var a = n.charAt(0).toUpperCase() + n.slice(1),
            b = n,
            i = o2.length;
        while (i--) {
            n = o2[i] + a;
            if (n in s) {
                return n
            }
        }
        return b
    }
    function q2(a, b) {
        a = b || a;
        return Q.css(a, "display") === "none" || !Q.contains(a.ownerDocument, a)
    }
    function r2(a, s) {
        var b, i, j, v = [],
            l = 0,
            m = a.length;
        for (; l < m; l++) {
            i = a[l];
            if (!i.style) {
                continue
            }
            v[l] = Q._data(i, "olddisplay");
            b = i.style.display;
            if (s) {
                if (!v[l] && b === "none") {
                    i.style.display = ""
                }
                if (i.style.display === "" && q2(i)) {
                    v[l] = Q._data(i, "olddisplay", v2(i.nodeName))
                }
            } else {
                if (!v[l]) {
                    j = q2(i);
                    if (b && b !== "none" || !j) {
                        Q._data(i, "olddisplay", j ? b : Q.css(i, "display"))
                    }
                }
            }
        }
        for (l = 0; l < m; l++) {
            i = a[l];
            if (!i.style) {
                continue
            }
            if (!s || i.style.display === "none" || i.style.display === "") {
                i.style.display = s ? v[l] || "" : "none"
            }
        }
        return a
    }
    Q.fn.extend({
        css: function(n, v) {
            return Q.access(this, function(a, n, v) {
                var l, s, m = {}, i = 0;
                if (Q.isArray(n)) {
                    s = a2(a);
                    l = n.length;
                    for (; i < l; i++) {
                        m[n[i]] = Q.css(a, n[i], false, s)
                    }
                    return m
                }
                return v !== u ? Q.style(a, n, v) : Q.css(a, n)
            }, n, v, arguments.length > 1)
        },
        show: function() {
            return r2(this, true)
        },
        hide: function() {
            return r2(this)
        },
        toggle: function(s) {
            var b = typeof s === "boolean";
            return this.each(function() {
                if (b ? s : q2(this)) {
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
                get: function(a, b) {
                    if (b) {
                        var r = b2(a, "opacity");
                        return r === "" ? "1" : r
                    }
                }
            }
        },
        cssNumber: {
            "columnCount": true,
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
            var r, t, i, j = Q.camelCase(n),
                s = a.style;
            n = Q.cssProps[j] || (Q.cssProps[j] = p2(s, j));
            i = Q.cssHooks[n] || Q.cssHooks[j];
            if (v !== u) {
                t = typeof v;
                if (t === "string" && (r = j2.exec(v))) {
                    v = (r[1] + 1) * r[2] + parseFloat(Q.css(a, n));
                    t = "number"
                }
                if (v == null || t === "number" && isNaN(v)) {
                    return
                }
                if (t === "number" && !Q.cssNumber[j]) {
                    v += "px"
                }
                if (!Q.support.clearCloneStyle && v === "" && n.indexOf("background") === 0) {
                    s[n] = "inherit"
                }
                if (!i || !("set" in i) || (v = i.set(a, v, b)) !== u) {
                    try {
                        s[n] = v
                    } catch (e) {}
                }
            } else {
                if (i && "get" in i && (r = i.get(a, false, b)) !== u) {
                    return r
                }
                return s[n]
            }
        },
        css: function(a, n, b, s) {
            var i, v, j, l = Q.camelCase(n);
            n = Q.cssProps[l] || (Q.cssProps[l] = p2(a.style, l));
            j = Q.cssHooks[n] || Q.cssHooks[l];
            if (j && "get" in j) {
                v = j.get(a, true, b)
            }
            if (v === u) {
                v = b2(a, n, s)
            }
            if (v === "normal" && n in m2) {
                v = m2[n]
            }
            if (b === "" || b) {
                i = parseFloat(v);
                return b === true || Q.isNumeric(i) ? i || 0 : v
            }
            return v
        }
    });
    if (w.getComputedStyle) {
        a2 = function(a) {
            return w.getComputedStyle(a, null)
        };
        b2 = function(a, n, _) {
            var b, m, i, j = _ || a2(a),
                r = j ? j.getPropertyValue(n) || j[n] : u,
                s = a.style;
            if (j) {
                if (r === "" && !Q.contains(a.ownerDocument, a)) {
                    r = Q.style(a, n)
                }
                if (i2.test(r) && g2.test(n)) {
                    b = s.width;
                    m = s.minWidth;
                    i = s.maxWidth;
                    s.minWidth = s.maxWidth = s.width = r;
                    r = j.width;
                    s.width = b;
                    s.minWidth = m;
                    s.maxWidth = i
                }
            }
            return r
        }
    } else if (h.documentElement.currentStyle) {
        a2 = function(a) {
            return a.currentStyle
        };
        b2 = function(a, n, _) {
            var l, r, b, i = _ || a2(a),
                j = i ? i[n] : u,
                s = a.style;
            if (j == null && s && s[n]) {
                j = s[n]
            }
            if (i2.test(j) && !e2.test(n)) {
                l = s.left;
                r = a.runtimeStyle;
                b = r && r.left;
                if (b) {
                    r.left = a.currentStyle.left
                }
                s.left = n === "fontSize" ? "1em" : j;
                j = s.pixelLeft + "px";
                s.left = l;
                if (b) {
                    r.left = b
                }
            }
            return j === "" ? "auto" : j
        }
    }
    function s2(a, v, s) {
        var m = h2.exec(v);
        return m ? Math.max(0, m[1] - (s || 0)) + (m[2] || "px") : v
    }
    function t2(a, n, b, j, s) {
        var i = b === (j ? "border" : "content") ? 4 : n === "width" ? 1 : 0,
            v = 0;
        for (; i < 4; i += 2) {
            if (b === "margin") {
                v += Q.css(a, b + n2[i], true, s)
            }
            if (j) {
                if (b === "content") {
                    v -= Q.css(a, "padding" + n2[i], true, s)
                }
                if (b !== "margin") {
                    v -= Q.css(a, "border" + n2[i] + "Width", true, s)
                }
            } else {
                v += Q.css(a, "padding" + n2[i], true, s);
                if (b !== "padding") {
                    v += Q.css(a, "border" + n2[i] + "Width", true, s)
                }
            }
        }
        return v
    }
    function u2(a, n, b) {
        var v = true,
            i = n === "width" ? a.offsetWidth : a.offsetHeight,
            s = a2(a),
            j = Q.support.boxSizing && Q.css(a, "boxSizing", false, s) === "border-box";
        if (i <= 0 || i == null) {
            i = b2(a, n, s);
            if (i < 0 || i == null) {
                i = a.style[n]
            }
            if (i2.test(i)) {
                return i
            }
            v = j && (Q.support.boxSizingReliable || i === a.style[n]);
            i = parseFloat(i) || 0
        }
        return (i + t2(a, n, b || (j ? "border" : "content"), v, s)) + "px"
    }
    function v2(n) {
        var a = h,
            b = k2[n];
        if (!b) {
            b = w2(n, a);
            if (b === "none" || !b) {
                _1 = (_1 || Q("<iframe frameborder='0' width='0' height='0'/>").css("cssText", "display:block !important")).appendTo(a.documentElement);
                a = (_1[0].contentWindow || _1[0].contentDocument).document;
                a.write("<!doctype html><html><body>");
                a.close();
                b = w2(n, a);
                _1.detach()
            }
            k2[n] = b
        }
        return b
    }
    function w2(n, a) {
        var b = Q(a.createElement(n)).appendTo(a.body),
            i = Q.css(b[0], "display");
        b.remove();
        return i
    }
    Q.each(["height", "width"], function(i, n) {
        Q.cssHooks[n] = {
            get: function(a, b, j) {
                if (b) {
                    return a.offsetWidth === 0 && f2.test(Q.css(a, "display")) ? Q.swap(a, l2, function() {
                        return u2(a, n, j)
                    }) : u2(a, n, j)
                }
            },
            set: function(a, v, b) {
                var s = b && a2(a);
                return s2(a, v, b ? t2(a, n, b, Q.support.boxSizing && Q.css(a, "boxSizing", false, s) === "border-box", s) : 0)
            }
        }
    });
    if (!Q.support.opacity) {
        Q.cssHooks.opacity = {
            get: function(a, b) {
                return d2.test((b && a.currentStyle ? a.currentStyle.filter : a.style.filter) || "") ? (0.01 * parseFloat(RegExp.$1)) + "" : b ? "1" : ""
            },
            set: function(a, v) {
                var s = a.style,
                    b = a.currentStyle,
                    i = Q.isNumeric(v) ? "alpha(opacity=" + v * 100 + ")" : "",
                    j = b && b.filter || s.filter || "";
                s.zoom = 1;
                if ((v >= 1 || v === "") && Q.trim(j.replace(c2, "")) === "" && s.removeAttribute) {
                    s.removeAttribute("filter");
                    if (v === "" || b && !b.filter) {
                        return
                    }
                }
                s.filter = c2.test(j) ? j.replace(c2, i) : j + " " + i
            }
        }
    }
    Q(function() {
        if (!Q.support.reliableMarginRight) {
            Q.cssHooks.marginRight = {
                get: function(a, b) {
                    if (b) {
                        return Q.swap(a, {
                            "display": "inline-block"
                        }, b2, [a, "marginRight"])
                    }
                }
            }
        }
        if (!Q.support.pixelPosition && Q.fn.position) {
            Q.each(["top", "left"], function(i, p) {
                Q.cssHooks[p] = {
                    get: function(a, b) {
                        if (b) {
                            b = b2(a, p);
                            return i2.test(b) ? Q(a).position()[p] + "px" : b
                        }
                    }
                }
            })
        }
    });
    if (Q.expr && Q.expr.filters) {
        Q.expr.filters.hidden = function(a) {
            return a.offsetWidth <= 0 && a.offsetHeight <= 0 || (!Q.support.reliableHiddenOffsets && ((a.style && a.style.display) || Q.css(a, "display")) === "none")
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
                var i = 0,
                    a = {}, b = typeof v === "string" ? v.split(" ") : [v];
                for (; i < 4; i++) {
                    a[p + n2[i] + s] = b[i] || b[i - 2] || b[0]
                }
                return a
            }
        };
        if (!g2.test(p)) {
            Q.cssHooks[p + s].set = s2
        }
    });
    var x2 = /%20/g,
        y2 = /\[\]$/,
        z2 = /\r?\n/g,
        A2 = /^(?:submit|button|image|reset|file)$/i,
        B2 = /^(?:input|select|textarea|keygen)/i;
    Q.fn.extend({
        serialize: function() {
            return Q.param(this.serializeArray())
        },
        serializeArray: function() {
            return this.map(function() {
                var a = Q.prop(this, "elements");
                return a ? Q.makeArray(a) : this
            }).filter(function() {
                var t = this.type;
                return this.name && !Q(this).is(":disabled") && B2.test(this.nodeName) && !A2.test(t) && (this.checked || !L1.test(t))
            }).map(function(i, a) {
                var v = Q(this).val();
                return v == null ? null : Q.isArray(v) ? Q.map(v, function(v) {
                    return {
                        name: a.name,
                        value: v.replace(z2, "\r\n")
                    }
                }) : {
                    name: a.name,
                    value: v.replace(z2, "\r\n")
                }
            }).get()
        }
    });
    Q.param = function(a, t) {
        var p, s = [],
            b = function(i, v) {
                v = Q.isFunction(v) ? v() : (v == null ? "" : v);
                s[s.length] = encodeURIComponent(i) + "=" + encodeURIComponent(v)
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
                C2(p, a[p], t, b)
            }
        }
        return s.join("&").replace(x2, "+")
    };

    function C2(p, a, t, b) {
        var n;
        if (Q.isArray(a)) {
            Q.each(a, function(i, v) {
                if (t || y2.test(p)) {
                    b(p, v)
                } else {
                    C2(p + "[" + (typeof v === "object" ? i : "") + "]", v, t, b)
                }
            })
        } else if (!t && Q.type(a) === "object") {
            for (n in a) {
                C2(p + "[" + n + "]", a[n], t, b)
            }
        } else {
            b(p, a)
        }
    }
    Q.each(("blur focus focusin focusout load resize scroll unload click dblclick " + "mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave " + "change select submit keydown keypress keyup error contextmenu").split(" "), function(i, n) {
        Q.fn[n] = function(a, b) {
            return arguments.length > 0 ? this.on(n, null, a, b) : this.trigger(n)
        }
    });
    Q.fn.extend({
        hover: function(a, b) {
            return this.mouseenter(a).mouseleave(b || a)
        },
        bind: function(t, a, b) {
            return this.on(t, null, a, b)
        },
        unbind: function(t, a) {
            return this.off(t, null, a)
        },
        delegate: function(s, t, a, b) {
            return this.on(t, s, a, b)
        },
        undelegate: function(s, t, a) {
            return arguments.length === 1 ? this.off(s, "**") : this.off(t, s || "**", a)
        }
    });
    var D2, E2, F2 = Q.now(),
        G2 = /\?/,
        H2 = /#.*$/,
        I2 = /([?&])_=[^&]*/,
        J2 = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg,
        K2 = /^(?:about|app|app-storage|.+-extension|file|res|widget):$/,
        L2 = /^(?:GET|HEAD)$/,
        M2 = /^\/\//,
        N2 = /^([\w.+-]+:)(?:\/\/([^\/?#:]*)(?::(\d+)|)|)/,
        O2 = Q.fn.load,
        P2 = {}, Q2 = {}, R2 = "*/".concat("*");
    try {
        E2 = g.href
    } catch (e) {
        E2 = h.createElement("a");
        E2.href = "";
        E2 = E2.href
    }
    D2 = N2.exec(E2.toLowerCase()) || [];

    function S2(s) {
        return function(a, b) {
            if (typeof a !== "string") {
                b = a;
                a = "*"
            }
            var j, i = 0,
                l = a.toLowerCase().match(I) || [];
            if (Q.isFunction(b)) {
                while ((j = l[i++])) {
                    if (j[0] === "+") {
                        j = j.slice(1) || "*";
                        (s[j] = s[j] || []).unshift(b)
                    } else {
                        (s[j] = s[j] || []).push(b)
                    }
                }
            }
        }
    }
    function T2(s, a, b, j) {
        var i = {}, l = (s === Q2);

        function m(n) {
            var p;
            i[n] = true;
            Q.each(s[n] || [], function(_, r) {
                var t = r(a, b, j);
                if (typeof t === "string" && !l && !i[t]) {
                    a.dataTypes.unshift(t);
                    m(t);
                    return false
                } else if (l) {
                    return !(p = t)
                }
            });
            return p
        }
        return m(a.dataTypes[0]) || !i["*"] && m("*")
    }
    function U2(t, s) {
        var a, b, i = Q.ajaxSettings.flatOptions || {};
        for (b in s) {
            if (s[b] !== u) {
                (i[b] ? t : (a || (a = {})))[b] = s[b]
            }
        }
        if (a) {
            Q.extend(true, t, a)
        }
        return t
    }
    Q.fn.load = function(a, p, b) {
        if (typeof a !== "string" && O2) {
            return O2.apply(this, arguments)
        }
        var s, r, t, i = this,
            j = a.indexOf(" ");
        if (j >= 0) {
            s = a.slice(j, a.length);
            a = a.slice(0, j)
        }
        if (Q.isFunction(p)) {
            b = p;
            p = u
        } else if (p && typeof p === "object") {
            t = "POST"
        }
        if (i.length > 0) {
            Q.ajax({
                url: a,
                type: t,
                dataType: "html",
                data: p
            }).done(function(l) {
                r = arguments;
                i.html(s ? Q("<div>").append(Q.parseHTML(l)).find(s) : l)
            }).complete(b && function(l, m) {
                i.each(b, r || [l.responseText, m, l])
            })
        }
        return this
    };
    Q.each(["ajaxStart", "ajaxStop", "ajaxComplete", "ajaxError", "ajaxSuccess", "ajaxSend"], function(i, t) {
        Q.fn[t] = function(a) {
            return this.on(t, a)
        }
    });
    Q.extend({
        active: 0,
        lastModified: {},
        etag: {},
        ajaxSettings: {
            url: E2,
            type: "GET",
            isLocal: K2.test(D2[1]),
            global: true,
            processData: true,
            async :true,
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            accepts: {
                "*": R2,
                text: "text/plain",
                html: "text/html",
                xml: "application/xml, text/xml",
                json: "application/json, text/javascript"
            },
            contents: {
                xml: /xml/,
                html: /html/,
                json: /json/
            },
            responseFields: {
                xml: "responseXML",
                text: "responseText",
                json: "responseJSON"
            },
            converters: {
                "* text": String,
                "text html": true,
                "text json": Q.parseJSON,
                "text xml": Q.parseXML
            },
            flatOptions: {
                url: true,
                context: true
            }
        },
        ajaxSetup: function(t, s) {
            return s ? U2(U2(t, Q.ajaxSettings), s) : U2(Q.ajaxSettings, t)
        },
        ajaxPrefilter: S2(P2),
        ajaxTransport: S2(Q2),
        ajax: function(a, b) {
            if (typeof a === "object") {
                b = a;
                a = u
            }
            b = b || {};
            var p, i, j, r, t, l, m, n, s = Q.ajaxSetup({}, b),
                v = s.context || s,
                _ = s.context && (v.nodeType || v.jquery) ? Q(v) : Q.event,
                s3 = Q.Deferred(),
                t3 = Q.Callbacks("once memory"),
                u3 = s.statusCode || {}, v3 = {}, w3 = {}, x3 = 0,
                y3 = "canceled",
                z3 = {
                    readyState: 0,
                    getResponseHeader: function(B3) {
                        var C3;
                        if (x3 === 2) {
                            if (!n) {
                                n = {};
                                while ((C3 = J2.exec(r))) {
                                    n[C3[1].toLowerCase()] = C3[2]
                                }
                            }
                            C3 = n[B3.toLowerCase()]
                        }
                        return C3 == null ? null : C3
                    },
                    getAllResponseHeaders: function() {
                        return x3 === 2 ? r : null
                    },
                    setRequestHeader: function(B3, C3) {
                        var D3 = B3.toLowerCase();
                        if (!x3) {
                            B3 = w3[D3] = w3[D3] || B3;
                            v3[B3] = C3
                        }
                        return this
                    },
                    overrideMimeType: function(B3) {
                        if (!x3) {
                            s.mimeType = B3
                        }
                        return this
                    },
                    statusCode: function(B3) {
                        var C3;
                        if (B3) {
                            if (x3 < 2) {
                                for (C3 in B3) {
                                    u3[C3] = [u3[C3], B3[C3]]
                                }
                            } else {
                                z3.always(B3[z3.status])
                            }
                        }
                        return this
                    },
                    abort: function(B3) {
                        var C3 = B3 || y3;
                        if (m) {
                            m.abort(C3)
                        }
                        A3(0, C3);
                        return this
                    }
                };
            s3.promise(z3).complete = t3.add;
            z3.success = z3.done;
            z3.error = z3.fail;
            s.url = ((a || s.url || E2) + "").replace(H2, "").replace(M2, D2[1] + "//");
            s.type = b.method || b.type || s.method || s.type;
            s.dataTypes = Q.trim(s.dataType || "*").toLowerCase().match(I) || [""];
            if (s.crossDomain == null) {
                p = N2.exec(s.url.toLowerCase());
                s.crossDomain = !! (p && (p[1] !== D2[1] || p[2] !== D2[2] || (p[3] || (p[1] === "http:" ? "80" : "443")) !== (D2[3] || (D2[1] === "http:" ? "80" : "443"))))
            }
            if (s.data && s.processData && typeof s.data !== "string") {
                s.data = Q.param(s.data, s.traditional)
            }
            T2(P2, s, b, z3);
            if (x3 === 2) {
                return z3
            }
            l = s.global;
            if (l && Q.active++ === 0) {
                Q.event.trigger("ajaxStart")
            }
            s.type = s.type.toUpperCase();
            s.hasContent = !L2.test(s.type);
            j = s.url;
            if (!s.hasContent) {
                if (s.data) {
                    j = (s.url += (G2.test(j) ? "&" : "?") + s.data);
                    delete s.data
                }
                if (s.cache === false) {
                    s.url = I2.test(j) ? j.replace(I2, "$1_=" + F2++) : j + (G2.test(j) ? "&" : "?") + "_=" + F2++
                }
            }
            if (s.ifModified) {
                if (Q.lastModified[j]) {
                    z3.setRequestHeader("If-Modified-Since", Q.lastModified[j])
                }
                if (Q.etag[j]) {
                    z3.setRequestHeader("If-None-Match", Q.etag[j])
                }
            }
            if (s.data && s.hasContent && s.contentType !== false || b.contentType) {
                z3.setRequestHeader("Content-Type", s.contentType)
            }
            z3.setRequestHeader("Accept", s.dataTypes[0] && s.accepts[s.dataTypes[0]] ? s.accepts[s.dataTypes[0]] + (s.dataTypes[0] !== "*" ? ", " + R2 + "; q=0.01" : "") : s.accepts["*"]);
            for (i in s.headers) {
                z3.setRequestHeader(i, s.headers[i])
            }
            if (s.beforeSend && (s.beforeSend.call(v, z3, s) === false || x3 === 2)) {
                return z3.abort()
            }
            y3 = "abort";
            for (i in {
                success: 1,
                error: 1,
                complete: 1
            }) {
                z3[i](s[i])
            }
            m = T2(Q2, s, b, z3);
            if (!m) {
                A3(-1, "No Transport")
            } else {
                z3.readyState = 1;
                if (l) {
                    _.trigger("ajaxSend", [z3, s])
                }
                if (s.async &&s.timeout > 0) {
                    t = setTimeout(function() {
                        z3.abort("timeout")
                    }, s.timeout)
                }
                try {
                    x3 = 1;
                    m.send(v3, A3)
                } catch (e) {
                    if (x3 < 2) {
                        A3(-1, e)
                    } else {
                        throw e
                    }
                }
            }
            function A3(B3, C3, D3, E3) {
                var F3, G3, H3, I3, J3, K3 = C3;
                if (x3 === 2) {
                    return
                }
                x3 = 2;
                if (t) {
                    clearTimeout(t)
                }
                m = u;
                r = E3 || "";
                z3.readyState = B3 > 0 ? 4 : 0;
                F3 = B3 >= 200 && B3 < 300 || B3 === 304;
                if (D3) {
                    I3 = V2(s, z3, D3)
                }
                I3 = W2(s, I3, z3, F3);
                if (F3) {
                    if (s.ifModified) {
                        J3 = z3.getResponseHeader("Last-Modified");
                        if (J3) {
                            Q.lastModified[j] = J3
                        }
                        J3 = z3.getResponseHeader("etag");
                        if (J3) {
                            Q.etag[j] = J3
                        }
                    }
                    if (B3 === 204 || s.type === "HEAD") {
                        K3 = "nocontent"
                    } else if (B3 === 304) {
                        K3 = "notmodified"
                    } else {
                        K3 = I3.state;
                        G3 = I3.data;
                        H3 = I3.error;
                        F3 = !H3
                    }
                } else {
                    H3 = K3;
                    if (B3 || !K3) {
                        K3 = "error";
                        if (B3 < 0) {
                            B3 = 0
                        }
                    }
                }
                z3.status = B3;
                z3.statusText = (C3 || K3) + "";
                if (F3) {
                    s3.resolveWith(v, [G3, K3, z3])
                } else {
                    s3.rejectWith(v, [z3, K3, H3])
                }
                z3.statusCode(u3);
                u3 = u;
                if (l) {
                    _.trigger(F3 ? "ajaxSuccess" : "ajaxError", [z3, s, F3 ? G3 : H3])
                }
                t3.fireWith(v, [z3, K3]);
                if (l) {
                    _.trigger("ajaxComplete", [z3, s]);
                    if (!(--Q.active)) {
                        Q.event.trigger("ajaxStop")
                    }
                }
            }
            return z3
        },
        getJSON: function(a, b, i) {
            return Q.get(a, b, i, "json")
        },
        getScript: function(a, b) {
            return Q.get(a, u, b, "script")
        }
    });
    Q.each(["get", "post"], function(i, m) {
        Q[m] = function(a, b, j, t) {
            if (Q.isFunction(b)) {
                t = t || j;
                j = b;
                b = u
            }
            return Q.ajax({
                url: a,
                type: m,
                dataType: t,
                data: b,
                success: j
            })
        }
    });

    function V2(s, j, r) {
        var a, b, i, t, l = s.contents,
            m = s.dataTypes;
        while (m[0] === "*") {
            m.shift();
            if (b === u) {
                b = s.mimeType || j.getResponseHeader("Content-Type")
            }
        }
        if (b) {
            for (t in l) {
                if (l[t] && l[t].test(b)) {
                    m.unshift(t);
                    break
                }
            }
        }
        if (m[0] in r) {
            i = m[0]
        } else {
            for (t in r) {
                if (!m[0] || s.converters[t + " " + m[0]]) {
                    i = t;
                    break
                }
                if (!a) {
                    a = t
                }
            }
            i = i || a
        }
        if (i) {
            if (i !== m[0]) {
                m.unshift(i)
            }
            return r[i]
        }
    }
    function W2(s, r, j, i) {
        var a, b, l, t, p, m = {}, n = s.dataTypes.slice();
        if (n[1]) {
            for (l in s.converters) {
                m[l.toLowerCase()] = s.converters[l]
            }
        }
        b = n.shift();
        while (b) {
            if (s.responseFields[b]) {
                j[s.responseFields[b]] = r
            }
            if (!p && i && s.dataFilter) {
                r = s.dataFilter(r, s.dataType)
            }
            p = b;
            b = n.shift();
            if (b) {
                if (b === "*") {
                    b = p
                } else if (p !== "*" && p !== b) {
                    l = m[p + " " + b] || m["* " + b];
                    if (!l) {
                        for (a in m) {
                            t = a.split(" ");
                            if (t[1] === b) {
                                l = m[p + " " + t[0]] || m["* " + t[0]];
                                if (l) {
                                    if (l === true) {
                                        l = m[a]
                                    } else if (m[a] !== true) {
                                        b = t[0];
                                        n.unshift(t[1])
                                    }
                                    break
                                }
                            }
                        }
                    }
                    if (l !== true) {
                        if (l && s["throws"]) {
                            r = l(r)
                        } else {
                            try {
                                r = l(r)
                            } catch (e) {
                                return {
                                    state: "parsererror",
                                    error: l ? e : "No conversion from " + p + " to " + b
                                }
                            }
                        }
                    }
                }
            }
        }
        return {
            state: "success",
            data: r
        }
    }
    Q.ajaxSetup({
        accepts: {
            script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
        },
        contents: {
            script: /(?:java|ecma)script/
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
            var a, b = h.head || Q("head")[0] || h.documentElement;
            return {
                send: function(_, i) {
                    a = h.createElement("script");
                    a.async = true;
                    if (s.scriptCharset) {
                        a.charset = s.scriptCharset
                    }
                    a.src = s.url;
                    a.onload = a.onreadystatechange = function(_, j) {
                        if (j || !a.readyState || /loaded|complete/.test(a.readyState)) {
                            a.onload = a.onreadystatechange = null;
                            if (a.parentNode) {
                                a.parentNode.removeChild(a)
                            }
                            a = null;
                            if (!j) {
                                i(200, "success")
                            }
                        }
                    };
                    b.insertBefore(a, b.firstChild)
                },
                abort: function() {
                    if (a) {
                        a.onload(u, true)
                    }
                }
            }
        }
    });
    var X2 = [],
        Y2 = /(=)\?(?=&|$)|\?\?/;
    Q.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function() {
            var a = X2.pop() || (Q.expando + "_" + (F2++));
            this[a] = true;
            return a
        }
    });
    Q.ajaxPrefilter("json jsonp", function(s, a, j) {
        var b, i, r, l = s.jsonp !== false && (Y2.test(s.url) ? "url" : typeof s.data === "string" && !(s.contentType || "").indexOf("application/x-www-form-urlencoded") && Y2.test(s.data) && "data");
        if (l || s.dataTypes[0] === "jsonp") {
            b = s.jsonpCallback = Q.isFunction(s.jsonpCallback) ? s.jsonpCallback() : s.jsonpCallback;
            if (l) {
                s[l] = s[l].replace(Y2, "$1" + b)
            } else if (s.jsonp !== false) {
                s.url += (G2.test(s.url) ? "&" : "?") + s.jsonp + "=" + b
            }
            s.converters["script json"] = function() {
                if (!r) {
                    Q.error(b + " was not called")
                }
                return r[0]
            };
            s.dataTypes[0] = "json";
            i = w[b];
            w[b] = function() {
                r = arguments
            };
            j.always(function() {
                w[b] = i;
                if (s[b]) {
                    s.jsonpCallback = a.jsonpCallback;
                    X2.push(b)
                }
                if (r && Q.isFunction(i)) {
                    i(r[0])
                }
                r = i = u
            });
            return "script"
        }
    });
    var Z2, $2, _2 = 0,
        a3 = w.ActiveXObject && function() {
            var a;
            for (a in Z2) {
                Z2[a](u, true)
            }
        };

    function b3() {
        try {
            return new w.XMLHttpRequest()
        } catch (e) {}
    }
    function c3() {
        try {
            return new w.ActiveXObject("Microsoft.XMLHTTP")
        } catch (e) {}
    }
    Q.ajaxSettings.xhr = w.ActiveXObject ? function() {
        return !this.isLocal && b3() || c3()
    } : b3;
    $2 = Q.ajaxSettings.xhr();
    Q.support.cors = !! $2 && ("withCredentials" in $2);
    $2 = Q.support.ajax = !! $2;
    if ($2) {
        Q.ajaxTransport(function(s) {
            if (!s.crossDomain || Q.support.cors) {
                var a;
                return {
                    send: function(b, j) {
                        var l, i, m = s.xhr();
                        if (s.username) {
                            m.open(s.type, s.url, s.async, s.username, s.password)
                        } else {
                            m.open(s.type, s.url, s.async)
                        }
                        if (s.xhrFields) {
                            for (i in s.xhrFields) {
                                m[i] = s.xhrFields[i]
                            }
                        }
                        if (s.mimeType && m.overrideMimeType) {
                            m.overrideMimeType(s.mimeType)
                        }
                        if (!s.crossDomain && !b["X-Requested-With"]) {
                            b["X-Requested-With"] = "XMLHttpRequest"
                        }
                        try {
                            for (i in b) {
                                m.setRequestHeader(i, b[i])
                            }
                        } catch (n) {}
                        m.send((s.hasContent && s.data) || null);
                        a = function(_, p) {
                            var r, t, v, s3;
                            try {
                                if (a && (p || m.readyState === 4)) {
                                    a = u;
                                    if (l) {
                                        m.onreadystatechange = Q.noop;
                                        if (a3) {
                                            delete Z2[l]
                                        }
                                    }
                                    if (p) {
                                        if (m.readyState !== 4) {
                                            m.abort()
                                        }
                                    } else {
                                        s3 = {};
                                        r = m.status;
                                        t = m.getAllResponseHeaders();
                                        if (typeof m.responseText === "string") {
                                            s3.text = m.responseText
                                        }
                                        try {
                                            v = m.statusText
                                        } catch (e) {
                                            v = ""
                                        }
                                        if (!r && s.isLocal && !s.crossDomain) {
                                            r = s3.text ? 200 : 404
                                        } else if (r === 1223) {
                                            r = 204
                                        }
                                    }
                                }
                            } catch (t3) {
                                if (!p) {
                                    j(-1, t3)
                                }
                            }
                            if (s3) {
                                j(r, v, s3, t)
                            }
                        };
                        if (!s.async) {
                            a()
                        } else if (m.readyState === 4) {
                            setTimeout(a)
                        } else {
                            l = ++_2;
                            if (a3) {
                                if (!Z2) {
                                    Z2 = {};
                                    Q(w).unload(a3)
                                }
                                Z2[l] = a
                            }
                            m.onreadystatechange = a
                        }
                    },
                    abort: function() {
                        if (a) {
                            a(u, true)
                        }
                    }
                }
            }
        })
    }
    var d3, e3, f3 = /^(?:toggle|show|hide)$/,
        g3 = new RegExp("^(?:([+-])=|)(" + H + ")([a-z%]*)$", "i"),
        h3 = /queueHooks$/,
        i3 = [o3],
        j3 = {
            "*": [function(p, v) {
                var t = this.createTween(p, v),
                    a = t.cur(),
                    b = g3.exec(v),
                    i = b && b[3] || (Q.cssNumber[p] ? "" : "px"),
                    s = (Q.cssNumber[p] || i !== "px" && +a) && g3.exec(Q.css(t.elem, p)),
                    j = 1,
                    m = 20;
                if (s && s[3] !== i) {
                    i = i || s[3];
                    b = b || [];
                    s = +a || 1;
                    do {
                        j = j || ".5";
                        s = s / j;
                        Q.style(t.elem, p, s + i)
                    } while (j !== (j = t.cur() / a) && j !== 1 && --m)
                }
                if (b) {
                    s = t.start = +s || +a || 0;
                    t.unit = i;
                    t.end = b[1] ? s + (b[1] + 1) * b[2] : +b[2]
                }
                return t
            }]
        };

    function k3() {
        setTimeout(function() {
            d3 = u
        });
        return (d3 = Q.now())
    }
    function l3(v, p, a) {
        var t, b = (j3[p] || []).concat(j3["*"]),
            i = 0,
            l = b.length;
        for (; i < l; i++) {
            if ((t = b[i].call(a, p, v))) {
                return t
            }
        }
    }
    function m3(a, p, b) {
        var r, s, i = 0,
            l = i3.length,
            j = Q.Deferred().always(function() {
                delete t.elem
            }),
            t = function() {
                if (s) {
                    return false
                }
                var v = d3 || k3(),
                    _ = Math.max(0, m.startTime + m.duration - v),
                    s3 = _ / m.duration || 0,
                    t3 = 1 - s3,
                    i = 0,
                    l = m.tweens.length;
                for (; i < l; i++) {
                    m.tweens[i].run(t3)
                }
                j.notifyWith(a, [m, t3, _]);
                if (t3 < 1 && l) {
                    return _
                } else {
                    j.resolveWith(a, [m]);
                    return false
                }
            }, m = j.promise({
                elem: a,
                props: Q.extend({}, p),
                opts: Q.extend(true, {
                    specialEasing: {}
                }, b),
                originalProperties: p,
                originalOptions: b,
                startTime: d3 || k3(),
                duration: b.duration,
                tweens: [],
                createTween: function(v, _) {
                    var s3 = Q.Tween(a, m.opts, v, _, m.opts.specialEasing[v] || m.opts.easing);
                    m.tweens.push(s3);
                    return s3
                },
                stop: function(v) {
                    var i = 0,
                        l = v ? m.tweens.length : 0;
                    if (s) {
                        return this
                    }
                    s = true;
                    for (; i < l; i++) {
                        m.tweens[i].run(1)
                    }
                    if (v) {
                        j.resolveWith(a, [m, v])
                    } else {
                        j.rejectWith(a, [m, v])
                    }
                    return this
                }
            }),
            n = m.props;
        n3(n, m.opts.specialEasing);
        for (; i < l; i++) {
            r = i3[i].call(m, a, n, m.opts);
            if (r) {
                return r
            }
        }
        Q.map(n, l3, m);
        if (Q.isFunction(m.opts.start)) {
            m.opts.start.call(a, m)
        }
        Q.fx.timer(Q.extend(t, {
            elem: a,
            anim: m,
            queue: m.opts.queue
        }));
        return m.progress(m.opts.progress).done(m.opts.done, m.opts.complete).fail(m.opts.fail).always(m.opts.always)
    }
    function n3(p, s) {
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
    Q.Animation = Q.extend(m3, {
        tweener: function(p, a) {
            if (Q.isFunction(p)) {
                a = p;
                p = ["*"]
            } else {
                p = p.split(" ")
            }
            var b, i = 0,
                l = p.length;
            for (; i < l; i++) {
                b = p[i];
                j3[b] = j3[b] || [];
                j3[b].unshift(a)
            }
        },
        prefilter: function(a, p) {
            if (p) {
                i3.unshift(a)
            } else {
                i3.push(a)
            }
        }
    });

    function o3(a, p, b) {
        var i, v, t, j, l, m, n = this,
            r = {}, s = a.style,
            _ = a.nodeType && q2(a),
            s3 = Q._data(a, "fxshow");
        if (!b.queue) {
            l = Q._queueHooks(a, "fx");
            if (l.unqueued == null) {
                l.unqueued = 0;
                m = l.empty.fire;
                l.empty.fire = function() {
                    if (!l.unqueued) {
                        m()
                    }
                }
            }
            l.unqueued++;
            n.always(function() {
                n.always(function() {
                    l.unqueued--;
                    if (!Q.queue(a, "fx").length) {
                        l.empty.fire()
                    }
                })
            })
        }
        if (a.nodeType === 1 && ("height" in p || "width" in p)) {
            b.overflow = [s.overflow, s.overflowX, s.overflowY];
            if (Q.css(a, "display") === "inline" && Q.css(a, "float") === "none") {
                if (!Q.support.inlineBlockNeedsLayout || v2(a.nodeName) === "inline") {
                    s.display = "inline-block"
                } else {
                    s.zoom = 1
                }
            }
        }
        if (b.overflow) {
            s.overflow = "hidden";
            if (!Q.support.shrinkWrapBlocks) {
                n.always(function() {
                    s.overflow = b.overflow[0];
                    s.overflowX = b.overflow[1];
                    s.overflowY = b.overflow[2]
                })
            }
        }
        for (i in p) {
            v = p[i];
            if (f3.exec(v)) {
                delete p[i];
                t = t || v === "toggle";
                if (v === (_ ? "hide" : "show")) {
                    continue
                }
                r[i] = s3 && s3[i] || Q.style(a, i)
            }
        }
        if (!Q.isEmptyObject(r)) {
            if (s3) {
                if ("hidden" in s3) {
                    _ = s3.hidden
                }
            } else {
                s3 = Q._data(a, "fxshow", {})
            }
            if (t) {
                s3.hidden = !_
            }
            if (_) {
                Q(a).show()
            } else {
                n.done(function() {
                    Q(a).hide()
                })
            }
            n.done(function() {
                var i;
                Q._removeData(a, "fxshow");
                for (i in r) {
                    Q.style(a, i, r[i])
                }
            });
            for (i in r) {
                j = l3(_ ? s3[i] : 0, i, n);
                if (!(i in s3)) {
                    s3[i] = j.start;
                    if (_) {
                        j.end = j.start;
                        j.start = i === "width" || i === "height" ? 1 : 0
                    }
                }
            }
        }
    }
    function p3(a, b, p, i, j) {
        return new p3.prototype.init(a, b, p, i, j)
    }
    Q.Tween = p3;
    p3.prototype = {
        constructor: p3,
        init: function(a, b, p, i, j, l) {
            this.elem = a;
            this.prop = p;
            this.easing = j || "swing";
            this.options = b;
            this.start = this.now = this.cur();
            this.end = i;
            this.unit = l || (Q.cssNumber[p] ? "" : "px")
        },
        cur: function() {
            var a = p3.propHooks[this.prop];
            return a && a.get ? a.get(this) : p3.propHooks._default.get(this)
        },
        run: function(p) {
            var a, b = p3.propHooks[this.prop];
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
                p3.propHooks._default.set(this)
            }
            return this
        }
    };
    p3.prototype.init.prototype = p3.prototype;
    p3.propHooks = {
        _default: {
            get: function(t) {
                var r;
                if (t.elem[t.prop] != null && (!t.elem.style || t.elem.style[t.prop] == null)) {
                    return t.elem[t.prop]
                }
                r = Q.css(t.elem, t.prop, "");
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
    p3.propHooks.scrollTop = p3.propHooks.scrollLeft = {
        set: function(t) {
            if (t.elem.nodeType && t.elem.parentNode) {
                t.elem[t.prop] = t.now
            }
        }
    };
    Q.each(["toggle", "show", "hide"], function(i, n) {
        var a = Q.fn[n];
        Q.fn[n] = function(s, b, j) {
            return s == null || typeof s === "boolean" ? a.apply(this, arguments) : this.animate(q3(n, true), s, b, j)
        }
    });
    Q.fn.extend({
        fadeTo: function(s, t, a, b) {
            return this.filter(q2).css("opacity", 0).show().end().animate({
                opacity: t
            }, s, a, b)
        },
        animate: function(p, s, a, b) {
            var i = Q.isEmptyObject(p),
                j = Q.speed(s, a, b),
                l = function() {
                    var m = m3(this, Q.extend({}, p), j);
                    if (i || Q._data(this, "finish")) {
                        m.stop(true)
                    }
                };
            l.finish = l;
            return i || j.queue === false ? this.each(l) : this.queue(j.queue, l)
        },
        stop: function(t, a, b) {
            var s = function(i) {
                var j = i.stop;
                delete i.stop;
                j(b)
            };
            if (typeof t !== "string") {
                b = a;
                a = t;
                t = u
            }
            if (a && t !== false) {
                this.queue(t || "fx", [])
            }
            return this.each(function() {
                var i = true,
                    j = t != null && t + "queueHooks",
                    l = Q.timers,
                    m = Q._data(this);
                if (j) {
                    if (m[j] && m[j].stop) {
                        s(m[j])
                    }
                } else {
                    for (j in m) {
                        if (m[j] && m[j].stop && h3.test(j)) {
                            s(m[j])
                        }
                    }
                }
                for (j = l.length; j--;) {
                    if (l[j].elem === this && (t == null || l[j].queue === t)) {
                        l[j].anim.stop(b);
                        i = false;
                        l.splice(j, 1)
                    }
                }
                if (i || !b) {
                    Q.dequeue(this, t)
                }
            })
        },
        finish: function(t) {
            if (t !== false) {
                t = t || "fx"
            }
            return this.each(function() {
                var i, a = Q._data(this),
                    b = a[t + "queue"],
                    j = a[t + "queueHooks"],
                    l = Q.timers,
                    m = b ? b.length : 0;
                a.finish = true;
                Q.queue(this, t, []);
                if (j && j.stop) {
                    j.stop.call(this, true)
                }
                for (i = l.length; i--;) {
                    if (l[i].elem === this && l[i].queue === t) {
                        l[i].anim.stop(true);
                        l.splice(i, 1)
                    }
                }
                for (i = 0; i < m; i++) {
                    if (b[i] && b[i].finish) {
                        b[i].finish.call(this)
                    }
                }
                delete a.finish
            })
        }
    });

    function q3(t, a) {
        var b, j = {
            height: t
        }, i = 0;
        a = a ? 1 : 0;
        for (; i < 4; i += 2 - a) {
            b = n2[i];
            j["margin" + b] = j["padding" + b] = t
        }
        if (a) {
            j.opacity = j.width = t
        }
        return j
    }
    Q.each({
        slideDown: q3("show"),
        slideUp: q3("hide"),
        slideToggle: q3("toggle"),
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
        Q.fn[n] = function(s, a, b) {
            return this.animate(p, s, a, b)
        }
    });
    Q.speed = function(s, a, b) {
        var i = s && typeof s === "object" ? Q.extend({}, s) : {
            complete: b || !b && a || Q.isFunction(s) && s,
            duration: s,
            easing: b && a || a && !Q.isFunction(a) && a
        };
        i.duration = Q.fx.off ? 0 : typeof i.duration === "number" ? i.duration : i.duration in Q.fx.speeds ? Q.fx.speeds[i.duration] : Q.fx.speeds._default;
        if (i.queue == null || i.queue === true) {
            i.queue = "fx"
        }
        i.old = i.complete;
        i.complete = function() {
            if (Q.isFunction(i.old)) {
                i.old.call(this)
            }
            if (i.queue) {
                Q.dequeue(this, i.queue)
            }
        };
        return i
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
    Q.fx = p3.prototype.init;
    Q.fx.tick = function() {
        var t, a = Q.timers,
            i = 0;
        d3 = Q.now();
        for (; i < a.length; i++) {
            t = a[i];
            if (!t() && a[i] === t) {
                a.splice(i--, 1)
            }
        }
        if (!a.length) {
            Q.fx.stop()
        }
        d3 = u
    };
    Q.fx.timer = function(t) {
        if (t() && Q.timers.push(t)) {
            Q.fx.start()
        }
    };
    Q.fx.interval = 13;
    Q.fx.start = function() {
        if (!e3) {
            e3 = setInterval(Q.fx.tick, Q.fx.interval)
        }
    };
    Q.fx.stop = function() {
        clearInterval(e3);
        e3 = null
    };
    Q.fx.speeds = {
        slow: 600,
        fast: 200,
        _default: 400
    };
    Q.fx.step = {};
    if (Q.expr && Q.expr.filters) {
        Q.expr.filters.animated = function(a) {
            return Q.grep(Q.timers, function(b) {
                return a === b.elem
            }).length
        }
    }
    Q.fn.offset = function(a) {
        if (arguments.length) {
            return a === u ? this : this.each(function(i) {
                Q.offset.setOffset(this, a, i)
            })
        }
        var k, b, j = {
            top: 0,
            left: 0
        }, l = this[0],
            m = l && l.ownerDocument;
        if (!m) {
            return
        }
        k = m.documentElement;
        if (!Q.contains(k, l)) {
            return j
        }
        if (typeof l.getBoundingClientRect !== f) {
            j = l.getBoundingClientRect()
        }
        b = r3(m);
        return {
            top: j.top + (b.pageYOffset || k.scrollTop) - (k.clientTop || 0),
            left: j.left + (b.pageXOffset || k.scrollLeft) - (k.clientLeft || 0)
        }
    };
    Q.offset = {
        setOffset: function(a, b, i) {
            var p = Q.css(a, "position");
            if (p === "static") {
                a.style.position = "relative"
            }
            var j = Q(a),
                l = j.offset(),
                m = Q.css(a, "top"),
                n = Q.css(a, "left"),
                r = (p === "absolute" || p === "fixed") && Q.inArray("auto", [m, n]) > -1,
                s = {}, t = {}, v, _;
            if (r) {
                t = j.position();
                v = t.top;
                _ = t.left
            } else {
                v = parseFloat(m) || 0;
                _ = parseFloat(n) || 0
            }
            if (Q.isFunction(b)) {
                b = b.call(a, i, l)
            }
            if (b.top != null) {
                s.top = (b.top - l.top) + v
            }
            if (b.left != null) {
                s.left = (b.left - l.left) + _
            }
            if ("using" in b) {
                b.using.call(a, s)
            } else {
                j.css(s)
            }
        }
    };
    Q.fn.extend({
        position: function() {
            if (!this[0]) {
                return
            }
            var a, b, p = {
                top: 0,
                left: 0
            }, i = this[0];
            if (Q.css(i, "position") === "fixed") {
                b = i.getBoundingClientRect()
            } else {
                a = this.offsetParent();
                b = this.offset();
                if (!Q.nodeName(a[0], "html")) {
                    p = a.offset()
                }
                p.top += Q.css(a[0], "borderTopWidth", true);
                p.left += Q.css(a[0], "borderLeftWidth", true)
            }
            return {
                top: b.top - p.top - Q.css(i, "marginTop", true),
                left: b.left - p.left - Q.css(i, "marginLeft", true)
            }
        },
        offsetParent: function() {
            return this.map(function() {
                var a = this.offsetParent || k;
                while (a && (!Q.nodeName(a, "html") && Q.css(a, "position") === "static")) {
                    a = a.offsetParent
                }
                return a || k
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
                var b = r3(a);
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

    function r3(a) {
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
        }, function(a, b) {
            Q.fn[b] = function(m, v) {
                var i = arguments.length && (a || typeof m !== "boolean"),
                    j = a || (m === true || v === true ? "margin" : "border");
                return Q.access(this, function(l, t, v) {
                    var p;
                    if (Q.isWindow(l)) {
                        return l.document.documentElement["client" + n]
                    }
                    if (l.nodeType === 9) {
                        p = l.documentElement;
                        return Math.max(l.body["scroll" + n], p["scroll" + n], l.body["offset" + n], p["offset" + n], p["client" + n])
                    }
                    return v === u ? Q.css(l, t, j) : Q.style(l, t, v, j)
                }, t, i ? m : u, i, null)
            }
        })
    });
    Q.fn.size = function() {
        return this.length
    };
    Q.fn.andSelf = Q.fn.addBack;
    if (typeof module === "object" && module && typeof module.exports === "object") {
        module.exports = Q
    } else {
        w.jQuery = w.$ = Q;
        if (typeof define === "function" && define.amd) {
            define("jquery", [], function() {
                return Q
            })
        }
    }
})(window);