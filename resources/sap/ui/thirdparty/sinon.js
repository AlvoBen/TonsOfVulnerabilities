﻿/**
 * Sinon.JS 1.5.2, 2012/11/27
 *
 * @author Christian Johansen (christian@cjohansen.no)
 * @author Contributors: https://github.com/cjohansen/Sinon.JS/blob/master/AUTHORS
 *
 * (The BSD License)
 *
 * Copyright (c) 2010-2012, Christian Johansen, christian@cjohansen.no
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of Christian Johansen nor the names of his contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var sinon = (function() {
    "use strict";
    var buster = (function(s, B) {
        var a = typeof require == "function" && typeof module == "object";
        var d = typeof document != "undefined" && document.createElement("div");
        var F = function() {};
        var b = {
            bind: function bind(o, m) {
                var e = typeof m == "string" ? o[m] : m;
                var f = Array.prototype.slice.call(arguments, 2);
                return function() {
                    var g = f.concat(Array.prototype.slice.call(arguments));
                    return e.apply(o, g)
                }
            },
            partial: function partial(f) {
                var e = [].slice.call(arguments, 1);
                return function() {
                    return f.apply(this, e.concat([].slice.call(arguments)))
                }
            },
            create: function create(o) {
                F.prototype = o;
                return new F()
            },
            extend: function extend(t) {
                if (!t) {
                    return
                }
                for (var i = 1, l = arguments.length, e; i < l; ++i) {
                    for (e in arguments[i]) {
                        t[e] = arguments[i][e]
                    }
                }
                return t
            },
            nextTick: function nextTick(e) {
                if (typeof process != "undefined" && process.nextTick) {
                    return process.nextTick(e)
                }
                s(e, 0)
            },
            functionName: function functionName(f) {
                if (!f) return "";
                if (f.displayName) return f.displayName;
                if (f.name) return f.name;
                var m = f.toString().match(/function\s+([^\(]+)/m);
                return m && m[1] || ""
            },
            isNode: function a(o) {
                if (!d) return false;
                try {
                    o.appendChild(d);
                    o.removeChild(d)
                } catch (e) {
                    return false
                }
                return true
            },
            isElement: function isElement(o) {
                return o && o.nodeType === 1 && b.isNode(o)
            },
            isArray: function isArray(e) {
                return Object.prototype.toString.call(e) == "[object Array]"
            },
            flatten: function flatten(e) {
                var r = [],
                    e = e || [];
                for (var i = 0, l = e.length; i < l; ++i) {
                    r = r.concat(b.isArray(e[i]) ? flatten(e[i]) : e[i])
                }
                return r
            },
            each: function each(e, f) {
                for (var i = 0, l = e.length; i < l; ++i) {
                    f(e[i])
                }
            },
            map: function map(e, f) {
                var r = [];
                for (var i = 0, l = e.length; i < l; ++i) {
                    r.push(f(e[i]))
                }
                return r
            },
            parallel: function parallel(f, e) {
                function g(j, k) {
                    if (typeof e == "function") {
                        e(j, k);
                        e = null
                    }
                }
                if (f.length == 0) {
                    return g(null, [])
                }
                var r = f.length,
                    h = [];

                function m(n) {
                    return function done(j, k) {
                        if (j) {
                            return g(j)
                        }
                        h[n] = k;
                        if (--r == 0) {
                            g(null, h)
                        }
                    }
                }
                for (var i = 0, l = f.length; i < l; ++i) {
                    f[i](m(i))
                }
            },
            series: function series(f, e) {
                function g(j, k) {
                    if (typeof e == "function") {
                        e(j, k)
                    }
                }
                var r = f.slice();
                var h = [];

                function i() {
                    if (r.length == 0) return g(null, h);
                    var j = r.shift()(n);
                    if (j && typeof j.then == "function") {
                        j.then(b.partial(n, null), n)
                    }
                }
                function n(j, k) {
                    if (j) return g(j);
                    h.push(k);
                    i()
                }
                i()
            },
            countdown: function countdown(n, e) {
                return function() {
                    if (--n == 0) e()
                }
            }
        };
        if (typeof process === "object" && typeof require === "function" && typeof module === "object") {
            var c = require("crypto");
            var p = require("path");
            b.tmpFile = function(f) {
                var h = c.createHash("sha1");
                h.update(f);
                var t = h.digest("hex");
                if (process.platform == "win32") {
                    return p.join(process.env["TEMP"], t)
                } else {
                    return p.join("/tmp", t)
                }
            }
        }
        if (Array.prototype.some) {
            b.some = function(e, f, t) {
                return e.some(f, t)
            }
        } else {
            b.some = function(e, f, t) {
                if (e == null) {
                    throw new TypeError()
                }
                e = Object(e);
                var l = e.length >>> 0;
                if (typeof f !== "function") {
                    throw new TypeError()
                }
                for (var i = 0; i < l; i++) {
                    if (e.hasOwnProperty(i) && f.call(t, e[i], i, e)) {
                        return true
                    }
                }
                return false
            }
        }
        if (Array.prototype.filter) {
            b.filter = function(e, f, t) {
                return e.filter(f, t)
            }
        } else {
            b.filter = function(f, e) {
                if (this == null) {
                    throw new TypeError()
                }
                var t = Object(this);
                var l = t.length >>> 0;
                if (typeof f != "function") {
                    throw new TypeError()
                }
                var r = [];
                for (var i = 0; i < l; i++) {
                    if (i in t) {
                        var v = t[i];
                        if (f.call(e, v, i, t)) {
                            r.push(v)
                        }
                    }
                }
                return r
            }
        }
        if (a) {
            module.exports = b;
            b.eventEmitter = require("./buster-event-emitter");
            Object.defineProperty(b, "defineVersionGetter", {
                get: function() {
                    return require("./define-version-getter")
                }
            })
        }
        return b.extend(B || {}, b)
    }(setTimeout, buster));
    if (typeof buster === "undefined") {
        var buster = {}
    }
    if (typeof module === "object" && typeof require === "function") {
        buster = require("buster-core")
    }
    buster.format = buster.format || {};
    buster.format.excludeConstructors = ["Object", /^.$/];
    buster.format.quoteStrings = true;
    buster.format.ascii = (function() {
        var h = Object.prototype.hasOwnProperty;
        var s = [];
        if (typeof global != "undefined") {
            s.push({
                obj: global,
                value: "[object global]"
            })
        }
        if (typeof document != "undefined") {
            s.push({
                obj: document,
                value: "[object HTMLDocument]"
            })
        }
        if (typeof window != "undefined") {
            s.push({
                obj: window,
                value: "[object Window]"
            })
        }
        function a(o) {
            var k = Object.keys && Object.keys(o) || [];
            if (k.length == 0) {
                for (var p in o) {
                    if (h.call(o, p)) {
                        k.push(p)
                    }
                }
            }
            return k.sort()
        }
        function b(o, d) {
            if (typeof o != "object") {
                return false
            }
            for (var i = 0, l = d.length; i < l; ++i) {
                if (d[i] === o) {
                    return true
                }
            }
            return false
        }
        function c(o, p, d) {
            if (typeof o == "string") {
                var q = typeof this.quoteStrings != "boolean" || this.quoteStrings;
                return p || q ? '"' + o + '"' : o
            }
            if (typeof o == "function" && !(o instanceof RegExp)) {
                return c.func(o)
            }
            p = p || [];
            if (b(o, p)) {
                return "[Circular]"
            }
            if (Object.prototype.toString.call(o) == "[object Array]") {
                return c.array.call(this, o, p)
            }
            if (!o) {
                return "" + o
            }
            if (buster.isElement(o)) {
                return c.element(o)
            }
            if (typeof o.toString == "function" && o.toString !== Object.prototype.toString) {
                return o.toString()
            }
            for (var i = 0, l = s.length; i < l; i++) {
                if (o === s[i].obj) {
                    return s[i].value
                }
            }
            return c.object.call(this, o, p, d)
        }
        c.func = function(f) {
            return "function " + buster.functionName(f) + "() {}"
        };
        c.array = function(d, p) {
            p = p || [];
            p.push(d);
            var e = [];
            for (var i = 0, l = d.length; i < l; ++i) {
                e.push(c.call(this, d[i], p))
            }
            return "[" + e.join(", ") + "]"
        };
        c.object = function(o, p, d) {
            p = p || [];
            p.push(o);
            d = d || 0;
            var e = [],
                f = a(o),
                g, j, k;
            var m = "";
            var n = 3;
            for (var i = 0, l = d; i < l; ++i) {
                m += " "
            }
            for (i = 0, l = f.length; i < l; ++i) {
                g = f[i];
                k = o[g];
                if (b(k, p)) {
                    j = "[Circular]"
                } else {
                    j = c.call(this, k, p, d + 2)
                }
                j = (/\s/.test(g) ? '"' + g + '"' : g) + ": " + j;
                n += j.length;
                e.push(j)
            }
            var q = c.constructorName.call(this, o);
            var r = q ? "[" + q + "] " : "";
            return (n + d) > 80 ? r + "{\n  " + m + e.join(",\n  " + m) + "\n" + m + "}" : r + "{ " + e.join(", ") + " }"
        };
        c.element = function(e) {
            var t = e.tagName.toLowerCase();
            var d = e.attributes,
                f, p = [],
                g;
            for (var i = 0, l = d.length; i < l; ++i) {
                f = d.item(i);
                g = f.nodeName.toLowerCase().replace("html:", "");
                if (g == "contenteditable" && f.nodeValue == "inherit") {
                    continue
                }
                if ( !! f.nodeValue) {
                    p.push(g + "=\"" + f.nodeValue + "\"")
                }
            }
            var j = "<" + t + (p.length > 0 ? " " : "");
            var k = e.innerHTML;
            if (k.length > 20) {
                k = k.substr(0, 20) + "[...]"
            }
            var r = j + p.join(" ") + ">" + k + "</" + t + ">";
            return r.replace(/ contentEditable="inherit"/, "")
        };
        c.constructorName = function(o) {
            var n = buster.functionName(o && o.constructor);
            var e = this.excludeConstructors || buster.format.excludeConstructors || [];
            for (var i = 0, l = e.length; i < l; ++i) {
                if (typeof e[i] == "string" && e[i] == n) {
                    return ""
                } else if (e[i].test && e[i].test(n)) {
                    return ""
                }
            }
            return n
        };
        return c
    }());
    if (typeof module != "undefined") {
        module.exports = buster.format
    }

    /**
     * Sinon core utilities. For internal use only.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    var sinon = (function(c) {
        var d = typeof document != "undefined" && document.createElement("div");
        var h = Object.prototype.hasOwnProperty;

        function f(o) {
            var a = false;
            try {
                o.appendChild(d);
                a = d.parentNode == o
            } catch (e) {
                return false
            } finally {
                try {
                    o.removeChild(d)
                } catch (e) {}
            }
            return a
        }
        function g(o) {
            return d && o && o.nodeType === 1 && f(o)
        }
        function j(o) {
            return typeof o === "function" || !! (o && o.constructor && o.call && o.apply)
        }
        function m(t, a) {
            for (var p in a) {
                if (!h.call(t, p)) {
                    t[p] = a[p]
                }
            }
        }
        var s = {
            wrapMethod: function wrapMethod(o, p, a) {
                if (!o) {
                    throw new TypeError("Should wrap property of object")
                }
                if (typeof a != "function") {
                    throw new TypeError("Method wrapper should be function")
                }
                var w = o[p];
                if (!j(w)) {
                    throw new TypeError("Attempted to wrap " + (typeof w) + " property " + p + " as function")
                }
                if (w.restore && w.restore.sinon) {
                    throw new TypeError("Attempted to wrap " + p + " which is already wrapped")
                }
                if (w.calledBefore) {
                    var v = !! w.returns ? "stubbed" : "spied on";
                    throw new TypeError("Attempted to wrap " + p + " which is already " + v)
                }
                var b = h.call(o, p);
                o[p] = a;
                a.displayName = p;
                a.restore = function() {
                    if (!b) {
                        delete o[p]
                    }
                    if (o[p] === a) {
                        o[p] = w
                    }
                };
                a.restore.sinon = true;
                m(a, w);
                return a
            },
            extend: function extend(t) {
                for (var i = 1, l = arguments.length; i < l; i += 1) {
                    for (var p in arguments[i]) {
                        if (arguments[i].hasOwnProperty(p)) {
                            t[p] = arguments[i][p]
                        }
                        if (arguments[i].hasOwnProperty("toString") && arguments[i].toString != t.toString) {
                            t.toString = arguments[i].toString
                        }
                    }
                }
                return t
            },
            create: function create(p) {
                var F = function() {};
                F.prototype = p;
                return new F()
            },
            deepEqual: function deepEqual(a, b) {
                if (s.match && s.match.isMatcher(a)) {
                    return a.test(b)
                }
                if (typeof a != "object" || typeof b != "object") {
                    return a === b
                }
                if (g(a) || g(b)) {
                    return a === b
                }
                if (a === b) {
                    return true
                }
                if ((a === null && b !== null) || (a !== null && b === null)) {
                    return false
                }
                var S = Object.prototype.toString.call(a);
                if (S != Object.prototype.toString.call(b)) {
                    return false
                }
                if (S == "[object Array]") {
                    if (a.length !== b.length) {
                        return false
                    }
                    for (var i = 0, l = a.length; i < l; i += 1) {
                        if (!deepEqual(a[i], b[i])) {
                            return false
                        }
                    }
                    return true
                }
                var p, L = 0,
                    o = 0;
                for (p in a) {
                    L += 1;
                    if (!deepEqual(a[p], b[p])) {
                        return false
                    }
                }
                for (p in b) {
                    o += 1
                }
                if (L != o) {
                    return false
                }
                return true
            },
            functionName: function functionName(a) {
                var b = a.displayName || a.name;
                if (!b) {
                    var i = a.toString().match(/function ([^\s\(]+)/);
                    b = i && i[1]
                }
                return b
            },
            functionToString: function toString() {
                if (this.getCall && this.callCount) {
                    var t, p, i = this.callCount;
                    while (i--) {
                        t = this.getCall(i).thisValue;
                        for (p in t) {
                            if (t[p] === this) {
                                return p
                            }
                        }
                    }
                }
                return this.displayName || "sinon fake"
            },
            getConfig: function(a) {
                var b = {};
                a = a || {};
                var i = s.defaultConfig;
                for (var p in i) {
                    if (i.hasOwnProperty(p)) {
                        b[p] = a.hasOwnProperty(p) ? a[p] : i[p]
                    }
                }
                return b
            },
            format: function(v) {
                return "" + v
            },
            defaultConfig: {
                injectIntoThis: true,
                injectInto: null,
                properties: ["spy", "stub", "mock", "clock", "server", "requests"],
                useFakeTimers: true,
                useFakeServer: true
            },
            timesInWords: function timesInWords(a) {
                return a == 1 && "once" || a == 2 && "twice" || a == 3 && "thrice" || (a || 0) + " times"
            },
            calledInOrder: function(a) {
                for (var i = 1, l = a.length; i < l; i++) {
                    if (!a[i - 1].calledBefore(a[i])) {
                        return false
                    }
                }
                return true
            },
            orderByFirstCall: function(i) {
                return i.sort(function(a, b) {
                    var C = a.getCall(0);
                    var l = b.getCall(0);
                    var I = C && C.callId || -1;
                    var o = l && l.callId || -1;
                    return I < o ? -1 : 1
                })
            },
            log: function() {},
            logError: function(l, a) {
                var b = l + " threw exception: ";
                s.log(b + "[" + a.name + "] " + a.message);
                if (a.stack) {
                    s.log(a.stack)
                }
                setTimeout(function() {
                    a.message = b + a.message;
                    throw a
                }, 0)
            },
            typeOf: function(v) {
                if (v === null) {
                    return "null"
                } else if (v === undefined) {
                    return "undefined"
                }
                var a = Object.prototype.toString.call(v);
                return a.substring(8, a.length - 1).toLowerCase()
            }
        };
        var k = typeof module == "object" && typeof require == "function";
        if (k) {
            try {
                c = {
                    format: require("buster-format")
                }
            } catch (e) {}
            module.exports = s;
            module.exports.spy = require("./sinon/spy");
            module.exports.stub = require("./sinon/stub");
            module.exports.mock = require("./sinon/mock");
            module.exports.collection = require("./sinon/collection");
            module.exports.assert = require("./sinon/assert");
            module.exports.sandbox = require("./sinon/sandbox");
            module.exports.test = require("./sinon/test");
            module.exports.testCase = require("./sinon/test_case");
            module.exports.assert = require("./sinon/assert");
            module.exports.match = require("./sinon/match")
        }
        if (c) {
            var n = s.create(c.format);
            n.quoteStrings = false;
            s.format = function() {
                return n.ascii.apply(n, arguments)
            }
        } else if (k) {
            try {
                var u = require("util");
                s.format = function(v) {
                    return typeof v == "object" && v.toString === Object.prototype.toString ? u.inspect(v) : v
                }
            } catch (e) {}
        }
        return s
    }(typeof buster == "object" && buster));

    /**
     * Match functions
     *
     * @author Maximilian Antoni (mail@maxantoni.de)
     * @license BSD
     *
     * Copyright (c) 2012 Maximilian Antoni
     */

    (function(s) {
        var c = typeof module == "object" && typeof require == "function";
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s) {
            return
        }
        function a(v, t, n) {
            var g = s.typeOf(v);
            if (g !== t) {
                throw new TypeError("Expected type of " + n + " to be " + t + ", but was " + g)
            }
        }
        var b = {
            toString: function() {
                return this.message
            }
        };

        function i(o) {
            return b.isPrototypeOf(o)
        }
        function d(g, h) {
            if (h === null || h === undefined) {
                return false
            }
            for (var k in g) {
                if (g.hasOwnProperty(k)) {
                    var j = g[k];
                    var l = h[k];
                    if (e.isMatcher(j)) {
                        if (!j.test(l)) {
                            return false
                        }
                    } else if (s.typeOf(j) === "object") {
                        if (!d(j, l)) {
                            return false
                        }
                    } else if (!s.deepEqual(j, l)) {
                        return false
                    }
                }
            }
            return true
        }
        b.or = function(m) {
            if (!i(m)) {
                throw new TypeError("Matcher expected")
            }
            var g = this;
            var o = s.create(b);
            o.test = function(h) {
                return g.test(h) || m.test(h)
            };
            o.message = g.message + ".or(" + m.message + ")";
            return o
        };
        b.and = function(m) {
            if (!i(m)) {
                throw new TypeError("Matcher expected")
            }
            var g = this;
            var h = s.create(b);
            h.test = function(j) {
                return g.test(j) && m.test(j)
            };
            h.message = g.message + ".and(" + m.message + ")";
            return h
        };
        var e = function(g, h) {
            var m = s.create(b);
            var t = s.typeOf(g);
            switch (t) {
                case "object":
                    if (typeof g.test === "function") {
                        m.test = function(l) {
                            return g.test(l) === true
                        };
                        m.message = "match(" + s.functionName(g.test) + ")";
                        return m
                    }
                    var j = [];
                    for (var k in g) {
                        if (g.hasOwnProperty(k)) {
                            j.push(k + ": " + g[k])
                        }
                    }
                    m.test = function(l) {
                        return d(g, l)
                    };
                    m.message = "match(" + j.join(", ") + ")";
                    break;
                case "number":
                    m.test = function(l) {
                        return g == l
                    };
                    break;
                case "string":
                    m.test = function(l) {
                        if (typeof l !== "string") {
                            return false
                        }
                        return l.indexOf(g) !== -1
                    };
                    m.message = "match(\"" + g + "\")";
                    break;
                case "regexp":
                    m.test = function(l) {
                        if (typeof l !== "string") {
                            return false
                        }
                        return g.test(l)
                    };
                    break;
                case "function":
                    m.test = g;
                    if (h) {
                        m.message = h
                    } else {
                        m.message = "match(" + s.functionName(g) + ")"
                    }
                    break;
                default:
                    m.test = function(l) {
                        return s.deepEqual(g, l)
                    }
            }
            if (!m.message) {
                m.message = "match(" + g + ")"
            }
            return m
        };
        e.isMatcher = i;
        e.any = e(function() {
            return true
        }, "any");
        e.defined = e(function(g) {
            return g !== null && g !== undefined
        }, "defined");
        e.truthy = e(function(g) {
            return !!g
        }, "truthy");
        e.falsy = e(function(g) {
            return !g
        }, "falsy");
        e.same = function(g) {
            return e(function(h) {
                return g === h
            }, "same(" + g + ")")
        };
        e.typeOf = function(t) {
            a(t, "string", "type");
            return e(function(g) {
                return s.typeOf(g) === t
            }, "typeOf(\"" + t + "\")")
        };
        e.instanceOf = function(t) {
            a(t, "function", "type");
            return e(function(g) {
                return g instanceof t
            }, "instanceOf(" + s.functionName(t) + ")")
        };

        function f(p, m) {
            return function(g, v) {
                a(g, "string", "property");
                var o = arguments.length === 1;
                var h = m + "(\"" + g + "\"";
                if (!o) {
                    h += ", " + v
                }
                h += ")";
                return e(function(j) {
                    if (j === undefined || j === null || !p(j, g)) {
                        return false
                    }
                    return o || s.deepEqual(v, j[g])
                }, h)
            }
        }
        e.has = f(function(g, p) {
            if (typeof g === "object") {
                return p in g
            }
            return g[p] !== undefined
        }, "has");
        e.hasOwn = f(function(g, p) {
            return g.hasOwnProperty(p)
        }, "hasOwn");
        e.bool = e.typeOf("boolean");
        e.number = e.typeOf("number");
        e.string = e.typeOf("string");
        e.object = e.typeOf("object");
        e.func = e.typeOf("function");
        e.array = e.typeOf("array");
        e.regexp = e.typeOf("regexp");
        e.date = e.typeOf("date");
        if (c) {
            module.exports = e
        } else {
            s.match = e
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Spy functions
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(sinon) {
        var commonJSModule = typeof module == "object" && typeof require == "function";
        var spyCall;
        var callId = 0;
        var push = [].push;
        var slice = Array.prototype.slice;
        if (!sinon && commonJSModule) {
            sinon = require("../sinon")
        }
        if (!sinon) {
            return
        }
        function spy(o, p) {
            if (!p && typeof o == "function") {
                return spy.create(o)
            }
            if (!o && !p) {
                return spy.create(function() {})
            }
            var m = o[p];
            return sinon.wrapMethod(o, p, spy.create(m))
        }
        sinon.extend(spy, (function() {
            function delegateToCalls(a, m, b, c, n) {
                a[m] = function() {
                    if (!this.called) {
                        if (n) {
                            return n.apply(this, arguments)
                        }
                        return false
                    }
                    var d;
                    var e = 0;
                    for (var i = 0, l = this.callCount; i < l; i += 1) {
                        d = this.getCall(i);
                        if (d[c || m].apply(d, arguments)) {
                            e += 1;
                            if (b) {
                                return true
                            }
                        }
                    }
                    return e === this.callCount
                }
            }
            function matchingFake(f, a, s) {
                if (!f) {
                    return
                }
                var b = a.length;
                for (var i = 0, l = f.length; i < l; i++) {
                    if (f[i].matches(a, s)) {
                        return f[i]
                    }
                }
            }
            function incrementCallCount() {
                this.called = true;
                this.callCount += 1;
                this.notCalled = false;
                this.calledOnce = this.callCount == 1;
                this.calledTwice = this.callCount == 2;
                this.calledThrice = this.callCount == 3
            }
            function createCallProperties() {
                this.firstCall = this.getCall(0);
                this.secondCall = this.getCall(1);
                this.thirdCall = this.getCall(2);
                this.lastCall = this.getCall(this.callCount - 1)
            }
            var vars = "a,b,c,d,e,f,g,h,i,j,k,l";

            function createProxy(func) {
                var p;
                if (func.length) {
                    eval("p = (function proxy(" + vars.substring(0, func.length * 2 - 1) + ") { return p.invoke(func, this, slice.call(arguments)); });")
                } else {
                    p = function proxy() {
                        return p.invoke(func, this, slice.call(arguments))
                    }
                }
                return p
            }
            var uuid = 0;
            var spyApi = {
                reset: function() {
                    this.called = false;
                    this.notCalled = true;
                    this.calledOnce = false;
                    this.calledTwice = false;
                    this.calledThrice = false;
                    this.callCount = 0;
                    this.firstCall = null;
                    this.secondCall = null;
                    this.thirdCall = null;
                    this.lastCall = null;
                    this.args = [];
                    this.returnValues = [];
                    this.thisValues = [];
                    this.exceptions = [];
                    this.callIds = [];
                    if (this.fakes) {
                        for (var i = 0; i < this.fakes.length; i++) {
                            this.fakes[i].reset()
                        }
                    }
                },
                create: function create(f) {
                    var n;
                    if (typeof f != "function") {
                        f = function() {}
                    } else {
                        n = sinon.functionName(f)
                    }
                    var p = createProxy(f);
                    sinon.extend(p, spy);
                    delete p.create;
                    sinon.extend(p, f);
                    p.reset();
                    p.prototype = f.prototype;
                    p.displayName = n || "spy";
                    p.toString = sinon.functionToString;
                    p._create = sinon.spy.create;
                    p.id = "spy#" + uuid++;
                    return p
                },
                invoke: function invoke(f, t, a) {
                    var m = matchingFake(this.fakes, a);
                    var b, r;
                    incrementCallCount.call(this);
                    push.call(this.thisValues, t);
                    push.call(this.args, a);
                    push.call(this.callIds, callId++);
                    try {
                        if (m) {
                            r = m.invoke(f, t, a)
                        } else {
                            r = (this.func || f).apply(t, a)
                        }
                    } catch (e) {
                        push.call(this.returnValues, undefined);
                        b = e;
                        throw e
                    } finally {
                        push.call(this.exceptions, b)
                    }
                    push.call(this.returnValues, r);
                    createCallProperties.call(this);
                    return r
                },
                getCall: function getCall(i) {
                    if (i < 0 || i >= this.callCount) {
                        return null
                    }
                    return spyCall.create(this, this.thisValues[i], this.args[i], this.returnValues[i], this.exceptions[i], this.callIds[i])
                },
                calledBefore: function calledBefore(s) {
                    if (!this.called) {
                        return false
                    }
                    if (!s.called) {
                        return true
                    }
                    return this.callIds[0] < s.callIds[s.callIds.length - 1]
                },
                calledAfter: function calledAfter(s) {
                    if (!this.called || !s.called) {
                        return false
                    }
                    return this.callIds[this.callCount - 1] > s.callIds[s.callCount - 1]
                },
                withArgs: function() {
                    var a = slice.call(arguments);
                    if (this.fakes) {
                        var m = matchingFake(this.fakes, a, true);
                        if (m) {
                            return m
                        }
                    } else {
                        this.fakes = []
                    }
                    var o = this;
                    var f = this._create();
                    f.matchingAguments = a;
                    push.call(this.fakes, f);
                    f.withArgs = function() {
                        return o.withArgs.apply(o, arguments)
                    };
                    for (var i = 0; i < this.args.length; i++) {
                        if (f.matches(this.args[i])) {
                            incrementCallCount.call(f);
                            push.call(f.thisValues, this.thisValues[i]);
                            push.call(f.args, this.args[i]);
                            push.call(f.returnValues, this.returnValues[i]);
                            push.call(f.exceptions, this.exceptions[i]);
                            push.call(f.callIds, this.callIds[i])
                        }
                    }
                    createCallProperties.call(f);
                    return f
                },
                matches: function(a, s) {
                    var m = this.matchingAguments;
                    if (m.length <= a.length && sinon.deepEqual(m, a.slice(0, m.length))) {
                        return !s || m.length == a.length
                    }
                },
                printf: function(f) {
                    var s = this;
                    var a = slice.call(arguments, 1);
                    var b;
                    return (f || "").replace(/%(.)/g, function(m, c) {
                        b = spyApi.formatters[c];
                        if (typeof b == "function") {
                            return b.call(null, s, a)
                        } else if (!isNaN(parseInt(c), 10)) {
                            return sinon.format(a[c - 1])
                        }
                        return "%" + c
                    })
                }
            };
            delegateToCalls(spyApi, "calledOn", true);
            delegateToCalls(spyApi, "alwaysCalledOn", false, "calledOn");
            delegateToCalls(spyApi, "calledWith", true);
            delegateToCalls(spyApi, "calledWithMatch", true);
            delegateToCalls(spyApi, "alwaysCalledWith", false, "calledWith");
            delegateToCalls(spyApi, "alwaysCalledWithMatch", false, "calledWithMatch");
            delegateToCalls(spyApi, "calledWithExactly", true);
            delegateToCalls(spyApi, "alwaysCalledWithExactly", false, "calledWithExactly");
            delegateToCalls(spyApi, "neverCalledWith", false, "notCalledWith", function() {
                return true
            });
            delegateToCalls(spyApi, "neverCalledWithMatch", false, "notCalledWithMatch", function() {
                return true
            });
            delegateToCalls(spyApi, "threw", true);
            delegateToCalls(spyApi, "alwaysThrew", false, "threw");
            delegateToCalls(spyApi, "returned", true);
            delegateToCalls(spyApi, "alwaysReturned", false, "returned");
            delegateToCalls(spyApi, "calledWithNew", true);
            delegateToCalls(spyApi, "alwaysCalledWithNew", false, "calledWithNew");
            delegateToCalls(spyApi, "callArg", false, "callArgWith", function() {
                throw new Error(this.toString() + " cannot call arg since it was not yet invoked.")
            });
            spyApi.callArgWith = spyApi.callArg;
            delegateToCalls(spyApi, "yield", false, "yield", function() {
                throw new Error(this.toString() + " cannot yield since it was not yet invoked.")
            });
            spyApi.invokeCallback = spyApi.yield;
            delegateToCalls(spyApi, "yieldTo", false, "yieldTo", function(p) {
                throw new Error(this.toString() + " cannot yield to '" + p + "' since it was not yet invoked.")
            });
            spyApi.formatters = {
                "c": function(s) {
                    return sinon.timesInWords(s.callCount)
                },
                "n": function(s) {
                    return s.toString()
                },
                "C": function(s) {
                    var c = [];
                    for (var i = 0, l = s.callCount; i < l; ++i) {
                        push.call(c, "    " + s.getCall(i).toString())
                    }
                    return c.length > 0 ? "\n" + c.join("\n") : ""
                },
                "t": function(s) {
                    var o = [];
                    for (var i = 0, l = s.callCount; i < l; ++i) {
                        push.call(o, sinon.format(s.thisValues[i]))
                    }
                    return o.join(", ")
                },
                "*": function(s, a) {
                    var f = [];
                    for (var i = 0, l = a.length; i < l; ++i) {
                        push.call(f, sinon.format(a[i]))
                    }
                    return f.join(", ")
                }
            };
            return spyApi
        }()));
        spyCall = (function() {
            function t(p, a, b) {
                var m = sinon.functionName(p) + a;
                if (b.length) {
                    m += " Received [" + slice.call(b).join(", ") + "]"
                }
                throw new Error(m)
            }
            var c = {
                create: function create(s, a, b, r, e, i) {
                    var p = sinon.create(spyCall);
                    delete p.create;
                    p.proxy = s;
                    p.thisValue = a;
                    p.args = b;
                    p.returnValue = r;
                    p.exception = e;
                    p.callId = typeof i == "number" && i || callId++;
                    return p
                },
                calledOn: function calledOn(a) {
                    if (sinon.match && sinon.match.isMatcher(a)) {
                        return a.test(this.thisValue)
                    }
                    return this.thisValue === a
                },
                calledWith: function calledWith() {
                    for (var i = 0, l = arguments.length; i < l; i += 1) {
                        if (!sinon.deepEqual(arguments[i], this.args[i])) {
                            return false
                        }
                    }
                    return true
                },
                calledWithMatch: function calledWithMatch() {
                    for (var i = 0, l = arguments.length; i < l; i += 1) {
                        var a = this.args[i];
                        var e = arguments[i];
                        if (!sinon.match || !sinon.match(e).test(a)) {
                            return false
                        }
                    }
                    return true
                },
                calledWithExactly: function calledWithExactly() {
                    return arguments.length == this.args.length && this.calledWith.apply(this, arguments)
                },
                notCalledWith: function notCalledWith() {
                    return !this.calledWith.apply(this, arguments)
                },
                notCalledWithMatch: function notCalledWithMatch() {
                    return !this.calledWithMatch.apply(this, arguments)
                },
                returned: function returned(v) {
                    return sinon.deepEqual(v, this.returnValue)
                },
                threw: function threw(e) {
                    if (typeof e == "undefined" || !this.exception) {
                        return !!this.exception
                    }
                    if (typeof e == "string") {
                        return this.exception.name == e
                    }
                    return this.exception === e
                },
                calledWithNew: function calledWithNew(a) {
                    return this.thisValue instanceof this.proxy
                },
                calledBefore: function(o) {
                    return this.callId < o.callId
                },
                calledAfter: function(o) {
                    return this.callId > o.callId
                },
                callArg: function(p) {
                    this.args[p]()
                },
                callArgWith: function(p) {
                    var a = slice.call(arguments, 1);
                    this.args[p].apply(null, a)
                },
                "yield": function() {
                    var a = this.args;
                    for (var i = 0, l = a.length; i < l; ++i) {
                        if (typeof a[i] === "function") {
                            a[i].apply(null, slice.call(arguments));
                            return
                        }
                    }
                    t(this.proxy, " cannot yield since no callback was passed.", a)
                },
                yieldTo: function(p) {
                    var a = this.args;
                    for (var i = 0, l = a.length; i < l; ++i) {
                        if (a[i] && typeof a[i][p] === "function") {
                            a[i][p].apply(null, slice.call(arguments, 1));
                            return
                        }
                    }
                    t(this.proxy, " cannot yield to '" + p + "' since no callback was passed.", a)
                },
                toString: function() {
                    var a = this.proxy.toString() + "(";
                    var b = [];
                    for (var i = 0, l = this.args.length; i < l; ++i) {
                        push.call(b, sinon.format(this.args[i]))
                    }
                    a = a + b.join(", ") + ")";
                    if (typeof this.returnValue != "undefined") {
                        a += " => " + sinon.format(this.returnValue)
                    }
                    if (this.exception) {
                        a += " !" + this.exception.name;
                        if (this.exception.message) {
                            a += "(" + this.exception.message + ")"
                        }
                    }
                    return a
                }
            };
            c.invokeCallback = c.yield;
            return c
        }());
        spy.spyCall = spyCall;
        sinon.spyCall = spyCall;
        if (commonJSModule) {
            module.exports = spy
        } else {
            sinon.spy = spy
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Stub functions
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(s) {
        var c = typeof module == "object" && typeof require == "function";
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s) {
            return
        }
        function a(o, p, f) {
            if ( !! f && typeof f != "function") {
                throw new TypeError("Custom stub should be function")
            }
            var w;
            if (f) {
                w = s.spy && s.spy.create ? s.spy.create(f) : f
            } else {
                w = a.create()
            }
            if (!o && !p) {
                return s.stub.create()
            }
            if (!p && !! o && typeof o == "object") {
                for (var h in o) {
                    if (typeof o[h] === "function") {
                        a(o, h)
                    }
                }
                return o
            }
            return s.wrapMethod(o, p, w)
        }
        function g(a, p) {
            var i = a.callCount - 1;
            var f = i in a[p] ? a[p][i] : a[p + "Last"];
            a[p + "Last"] = f;
            return f
        }
        function b(a, f) {
            var h = g(a, "callArgAts");
            if (h < 0) {
                var k = g(a, "callArgProps");
                for (var i = 0, l = f.length; i < l; ++i) {
                    if (!k && typeof f[i] == "function") {
                        return f[i]
                    }
                    if (k && f[i] && typeof f[i][k] == "function") {
                        return f[i][k]
                    }
                }
                return null
            }
            return f[h]
        }
        var j = Array.prototype.join;

        function d(a, f, h) {
            if (a.callArgAtsLast < 0) {
                var m;
                if (a.callArgPropsLast) {
                    m = s.functionName(a) + " expected to yield to '" + a.callArgPropsLast + "', but no object with such a property was passed."
                } else {
                    m = s.functionName(a) + " expected to yield, but no callback was passed."
                }
                if (h.length > 0) {
                    m += " Received [" + j.call(h, ", ") + "]"
                }
                return m
            }
            return "argument at index " + a.callArgAtsLast + " is not a function: " + f
        }
        var n = (function() {
            if (typeof process === "object" && typeof process.nextTick === "function") {
                return process.nextTick
            } else if (typeof msSetImmediate === "function") {
                return msSetImmediate.bind(window)
            } else if (typeof setImmediate === "function") {
                return setImmediate
            } else {
                return function(f) {
                    setTimeout(f, 0)
                }
            }
        })();

        function e(a, f) {
            if (a.callArgAts.length > 0) {
                var h = b(a, f);
                if (typeof h != "function") {
                    throw new TypeError(d(a, h, f))
                }
                var i = a.callCount - 1;
                var k = g(a, "callbackArguments");
                var l = g(a, "callbackContexts");
                if (a.callbackAsync) {
                    n(function() {
                        h.apply(l, k)
                    })
                } else {
                    h.apply(l, k)
                }
            }
        }
        var u = 0;
        s.extend(a, (function() {
            var f = Array.prototype.slice,
                p;

            function t(h, i) {
                if (typeof h == "string") {
                    this.exception = new Error(i || "");
                    this.exception.name = h
                } else if (!h) {
                    this.exception = new Error("Error")
                } else {
                    this.exception = h
                }
                return this
            }
            p = {
                create: function create() {
                    var h = function() {
                        e(h, arguments);
                        if (h.exception) {
                            throw h.exception
                        } else if (typeof h.returnArgAt == 'number') {
                            return arguments[h.returnArgAt]
                        } else if (h.returnThis) {
                            return this
                        }
                        return h.returnValue
                    };
                    h.id = "stub#" + u++;
                    var o = h;
                    h = s.spy.create(h);
                    h.func = o;
                    h.callArgAts = [];
                    h.callbackArguments = [];
                    h.callbackContexts = [];
                    h.callArgProps = [];
                    s.extend(h, a);
                    h._create = s.stub.create;
                    h.displayName = "stub";
                    h.toString = s.functionToString;
                    return h
                },
                returns: function returns(v) {
                    this.returnValue = v;
                    return this
                },
                returnsArg: function returnsArg(h) {
                    if (typeof h != "number") {
                        throw new TypeError("argument index is not number")
                    }
                    this.returnArgAt = h;
                    return this
                },
                returnsThis: function returnsThis() {
                    this.returnThis = true;
                    return this
                },
                "throws": t,
                throwsException: t,
                callsArg: function callsArg(h) {
                    if (typeof h != "number") {
                        throw new TypeError("argument index is not number")
                    }
                    this.callArgAts.push(h);
                    this.callbackArguments.push([]);
                    this.callbackContexts.push(undefined);
                    this.callArgProps.push(undefined);
                    return this
                },
                callsArgOn: function callsArgOn(h, i) {
                    if (typeof h != "number") {
                        throw new TypeError("argument index is not number")
                    }
                    if (typeof i != "object") {
                        throw new TypeError("argument context is not an object")
                    }
                    this.callArgAts.push(h);
                    this.callbackArguments.push([]);
                    this.callbackContexts.push(i);
                    this.callArgProps.push(undefined);
                    return this
                },
                callsArgWith: function callsArgWith(h) {
                    if (typeof h != "number") {
                        throw new TypeError("argument index is not number")
                    }
                    this.callArgAts.push(h);
                    this.callbackArguments.push(f.call(arguments, 1));
                    this.callbackContexts.push(undefined);
                    this.callArgProps.push(undefined);
                    return this
                },
                callsArgOnWith: function callsArgWith(h, i) {
                    if (typeof h != "number") {
                        throw new TypeError("argument index is not number")
                    }
                    if (typeof i != "object") {
                        throw new TypeError("argument context is not an object")
                    }
                    this.callArgAts.push(h);
                    this.callbackArguments.push(f.call(arguments, 2));
                    this.callbackContexts.push(i);
                    this.callArgProps.push(undefined);
                    return this
                },
                yields: function() {
                    this.callArgAts.push(-1);
                    this.callbackArguments.push(f.call(arguments, 0));
                    this.callbackContexts.push(undefined);
                    this.callArgProps.push(undefined);
                    return this
                },
                yieldsOn: function(h) {
                    if (typeof h != "object") {
                        throw new TypeError("argument context is not an object")
                    }
                    this.callArgAts.push(-1);
                    this.callbackArguments.push(f.call(arguments, 1));
                    this.callbackContexts.push(h);
                    this.callArgProps.push(undefined);
                    return this
                },
                yieldsTo: function(h) {
                    this.callArgAts.push(-1);
                    this.callbackArguments.push(f.call(arguments, 1));
                    this.callbackContexts.push(undefined);
                    this.callArgProps.push(h);
                    return this
                },
                yieldsToOn: function(h, i) {
                    if (typeof i != "object") {
                        throw new TypeError("argument context is not an object")
                    }
                    this.callArgAts.push(-1);
                    this.callbackArguments.push(f.call(arguments, 2));
                    this.callbackContexts.push(i);
                    this.callArgProps.push(h);
                    return this
                }
            };
            for (var m in p) {
                if (p.hasOwnProperty(m) && m.match(/^(callsArg|yields|thenYields$)/) && !m.match(/Async/)) {
                    p[m + 'Async'] = (function(h) {
                        return function() {
                            this.callbackAsync = true;
                            return this[h].apply(this, arguments)
                        }
                    })(m)
                }
            }
            return p
        }()));
        if (c) {
            module.exports = a
        } else {
            s.stub = a
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Mock functions.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(s) {
        var c = typeof module == "object" && typeof require == "function";
        var p = [].push;
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s) {
            return
        }
        function m(o) {
            if (!o) {
                return s.expectation.create("Anonymous mock")
            }
            return m.create(o)
        }
        s.mock = m;
        s.extend(m, (function() {
            function e(a, b) {
                if (!a) {
                    return
                }
                for (var i = 0, l = a.length; i < l; i += 1) {
                    b(a[i])
                }
            }
            return {
                create: function create(o) {
                    if (!o) {
                        throw new TypeError("object is null")
                    }
                    var a = s.extend({}, m);
                    a.object = o;
                    delete a.create;
                    return a
                },
                expects: function expects(a) {
                    if (!a) {
                        throw new TypeError("method is falsy")
                    }
                    if (!this.expectations) {
                        this.expectations = {};
                        this.proxies = []
                    }
                    if (!this.expectations[a]) {
                        this.expectations[a] = [];
                        var b = this;
                        s.wrapMethod(this.object, a, function() {
                            return b.invokeMethod(a, this, arguments)
                        });
                        p.call(this.proxies, a)
                    }
                    var d = s.expectation.create(a);
                    p.call(this.expectations[a], d);
                    return d
                },
                restore: function restore() {
                    var o = this.object;
                    e(this.proxies, function(a) {
                        if (typeof o[a].restore == "function") {
                            o[a].restore()
                        }
                    })
                },
                verify: function verify() {
                    var a = this.expectations || {};
                    var b = [],
                        d = [];
                    e(this.proxies, function(f) {
                        e(a[f], function(g) {
                            if (!g.met()) {
                                p.call(b, g.toString())
                            } else {
                                p.call(d, g.toString())
                            }
                        })
                    });
                    this.restore();
                    if (b.length > 0) {
                        s.expectation.fail(b.concat(d).join("\n"))
                    } else {
                        s.expectation.pass(b.concat(d).join("\n"))
                    }
                    return true
                },
                invokeMethod: function invokeMethod(a, b, d) {
                    var f = this.expectations && this.expectations[a];
                    var l = f && f.length || 0,
                        i;
                    for (i = 0; i < l; i += 1) {
                        if (!f[i].met() && f[i].allowsCall(b, d)) {
                            return f[i].apply(b, d)
                        }
                    }
                    var g = [],
                        h, j = 0;
                    for (i = 0; i < l; i += 1) {
                        if (f[i].allowsCall(b, d)) {
                            h = h || f[i]
                        } else {
                            j += 1
                        }
                        p.call(g, "    " + f[i].toString())
                    }
                    if (j === 0) {
                        return h.apply(b, d)
                    }
                    g.unshift("Unexpected call: " + s.spyCall.toString.call({
                        proxy: a,
                        args: d
                    }));
                    s.expectation.fail(g.join("\n"))
                }
            }
        }()));
        var t = s.timesInWords;
        s.expectation = (function() {
            var a = Array.prototype.slice;
            var _ = s.spy.invoke;

            function b(f) {
                if (f == 0) {
                    return "never called"
                } else {
                    return "called " + t(f)
                }
            }
            function e(f) {
                var g = f.minCalls;
                var h = f.maxCalls;
                if (typeof g == "number" && typeof h == "number") {
                    var i = t(g);
                    if (g != h) {
                        i = "at least " + i + " and at most " + t(h)
                    }
                    return i
                }
                if (typeof g == "number") {
                    return "at least " + t(g)
                }
                return "at most " + t(h)
            }
            function r(f) {
                var h = typeof f.minCalls == "number";
                return !h || f.callCount >= f.minCalls
            }
            function d(f) {
                if (typeof f.maxCalls != "number") {
                    return false
                }
                return f.callCount == f.maxCalls
            }
            return {
                minCalls: 1,
                maxCalls: 1,
                create: function create(f) {
                    var g = s.extend(s.stub.create(), s.expectation);
                    delete g.create;
                    g.method = f;
                    return g
                },
                invoke: function invoke(f, g, h) {
                    this.verifyCallAllowed(g, h);
                    return _.apply(this, arguments)
                },
                atLeast: function atLeast(n) {
                    if (typeof n != "number") {
                        throw new TypeError("'" + n + "' is not number")
                    }
                    if (!this.limitsSet) {
                        this.maxCalls = null;
                        this.limitsSet = true
                    }
                    this.minCalls = n;
                    return this
                },
                atMost: function atMost(n) {
                    if (typeof n != "number") {
                        throw new TypeError("'" + n + "' is not number")
                    }
                    if (!this.limitsSet) {
                        this.minCalls = null;
                        this.limitsSet = true
                    }
                    this.maxCalls = n;
                    return this
                },
                never: function never() {
                    return this.exactly(0)
                },
                once: function once() {
                    return this.exactly(1)
                },
                twice: function twice() {
                    return this.exactly(2)
                },
                thrice: function thrice() {
                    return this.exactly(3)
                },
                exactly: function exactly(n) {
                    if (typeof n != "number") {
                        throw new TypeError("'" + n + "' is not a number")
                    }
                    this.atLeast(n);
                    return this.atMost(n)
                },
                met: function met() {
                    return !this.failed && r(this)
                },
                verifyCallAllowed: function verifyCallAllowed(f, g) {
                    if (d(this)) {
                        this.failed = true;
                        s.expectation.fail(this.method + " already called " + t(this.maxCalls))
                    }
                    if ("expectedThis" in this && this.expectedThis !== f) {
                        s.expectation.fail(this.method + " called with " + f + " as thisValue, expected " + this.expectedThis)
                    }
                    if (!("expectedArguments" in this)) {
                        return
                    }
                    if (!g) {
                        s.expectation.fail(this.method + " received no arguments, expected " + s.format(this.expectedArguments))
                    }
                    if (g.length < this.expectedArguments.length) {
                        s.expectation.fail(this.method + " received too few arguments (" + s.format(g) + "), expected " + s.format(this.expectedArguments))
                    }
                    if (this.expectsExactArgCount && g.length != this.expectedArguments.length) {
                        s.expectation.fail(this.method + " received too many arguments (" + s.format(g) + "), expected " + s.format(this.expectedArguments))
                    }
                    for (var i = 0, l = this.expectedArguments.length; i < l; i += 1) {
                        if (!s.deepEqual(this.expectedArguments[i], g[i])) {
                            s.expectation.fail(this.method + " received wrong arguments " + s.format(g) + ", expected " + s.format(this.expectedArguments))
                        }
                    }
                },
                allowsCall: function allowsCall(f, g) {
                    if (this.met() && d(this)) {
                        return false
                    }
                    if ("expectedThis" in this && this.expectedThis !== f) {
                        return false
                    }
                    if (!("expectedArguments" in this)) {
                        return true
                    }
                    g = g || [];
                    if (g.length < this.expectedArguments.length) {
                        return false
                    }
                    if (this.expectsExactArgCount && g.length != this.expectedArguments.length) {
                        return false
                    }
                    for (var i = 0, l = this.expectedArguments.length; i < l; i += 1) {
                        if (!s.deepEqual(this.expectedArguments[i], g[i])) {
                            return false
                        }
                    }
                    return true
                },
                withArgs: function withArgs() {
                    this.expectedArguments = a.call(arguments);
                    return this
                },
                withExactArgs: function withExactArgs() {
                    this.withArgs.apply(this, arguments);
                    this.expectsExactArgCount = true;
                    return this
                },
                on: function on(f) {
                    this.expectedThis = f;
                    return this
                },
                toString: function() {
                    var f = (this.expectedArguments || []).slice();
                    if (!this.expectsExactArgCount) {
                        p.call(f, "[...]")
                    }
                    var g = s.spyCall.toString.call({
                        proxy: this.method,
                        args: f
                    });
                    var h = g.replace(", [...", "[, ...") + " " + e(this);
                    if (this.met()) {
                        return "Expectation met: " + h
                    }
                    return "Expected " + h + " (" + b(this.callCount) + ")"
                },
                verify: function verify() {
                    if (!this.met()) {
                        s.expectation.fail(this.toString())
                    } else {
                        s.expectation.pass(this.toString())
                    }
                    return true
                },
                pass: function(f) {
                    s.assert.pass(f)
                },
                fail: function(f) {
                    var g = new Error(f);
                    g.name = "ExpectationError";
                    throw g
                }
            }
        }());
        if (c) {
            module.exports = m
        } else {
            s.mock = m
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Collections of stubs, spies and mocks.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(s) {
        var c = typeof module == "object" && typeof require == "function";
        var p = [].push;
        var h = Object.prototype.hasOwnProperty;
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s) {
            return
        }
        function g(f) {
            if (!f.fakes) {
                f.fakes = []
            }
            return f.fakes
        }
        function a(f, m) {
            var e = g(f);
            for (var i = 0, l = e.length; i < l; i += 1) {
                if (typeof e[i][m] == "function") {
                    e[i][m]()
                }
            }
        }
        function b(f) {
            var e = g(f);
            var i = 0;
            while (i < e.length) {
                e.splice(i, 1)
            }
        }
        var d = {
            verify: function resolve() {
                a(this, "verify")
            },
            restore: function restore() {
                a(this, "restore");
                b(this)
            },
            verifyAndRestore: function verifyAndRestore() {
                var f;
                try {
                    this.verify()
                } catch (e) {
                    f = e
                }
                this.restore();
                if (f) {
                    throw f
                }
            },
            add: function add(f) {
                p.call(g(this), f);
                return f
            },
            spy: function spy() {
                return this.add(s.spy.apply(s, arguments))
            },
            stub: function stub(o, e, v) {
                if (e) {
                    var f = o[e];
                    if (typeof f != "function") {
                        if (!h.call(o, e)) {
                            throw new TypeError("Cannot stub non-existent own property " + e)
                        }
                        o[e] = v;
                        return this.add({
                            restore: function() {
                                o[e] = f
                            }
                        })
                    }
                }
                if (!e && !! o && typeof o == "object") {
                    var i = s.stub.apply(s, arguments);
                    for (var j in i) {
                        if (typeof i[j] === "function") {
                            this.add(i[j])
                        }
                    }
                    return i
                }
                return this.add(s.stub.apply(s, arguments))
            },
            mock: function mock() {
                return this.add(s.mock.apply(s, arguments))
            },
            inject: function inject(o) {
                var e = this;
                o.spy = function() {
                    return e.spy.apply(e, arguments)
                };
                o.stub = function() {
                    return e.stub.apply(e, arguments)
                };
                o.mock = function() {
                    return e.mock.apply(e, arguments)
                };
                return o
            }
        };
        if (c) {
            module.exports = d
        } else {
            s.collection = d
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Fake timer API
     * setTimeout
     * setInterval
     * clearTimeout
     * clearInterval
     * tick
     * reset
     * Date
     *
     * Inspired by jsUnitMockTimeOut from JsUnit
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    if (typeof sinon == "undefined") {
        var sinon = {}
    }(function(global) {
        var id = 1;

        function addTimer(a, r) {
            if (a.length === 0) {
                throw new Error("Function requires at least 1 parameter")
            }
            var t = id++;
            var d = a[1] || 0;
            if (!this.timeouts) {
                this.timeouts = {}
            }
            this.timeouts[t] = {
                id: t,
                func: a[0],
                callAt: this.now + d,
                invokeArgs: Array.prototype.slice.call(a, 2)
            };
            if (r === true) {
                this.timeouts[t].interval = d
            }
            return t
        }
        function parseTime(s) {
            if (!s) {
                return 0
            }
            var a = s.split(":");
            var l = a.length,
                i = l;
            var m = 0,
                p;
            if (l > 3 || !/^(\d\d:){0,2}\d\d?$/.test(s)) {
                throw new Error("tick only understands numbers and 'h:m:s'")
            }
            while (i--) {
                p = parseInt(a[i], 10);
                if (p >= 60) {
                    throw new Error("Invalid time " + s)
                }
                m += p * Math.pow(60, (l - i - 1))
            }
            return m * 1000
        }
        function createObject(o) {
            var n;
            if (Object.create) {
                n = Object.create(o)
            } else {
                var F = function() {};
                F.prototype = o;
                n = new F()
            }
            n.Date.clock = n;
            return n
        }
        sinon.clock = {
            now: 0,
            create: function create(n) {
                var c = createObject(this);
                if (typeof n == "number") {
                    c.now = n
                }
                if ( !! n && typeof n == "object") {
                    throw new TypeError("now should be milliseconds since UNIX epoch")
                }
                return c
            },
            setTimeout: function setTimeout(c, t) {
                return addTimer.call(this, arguments, false)
            },
            clearTimeout: function clearTimeout(t) {
                if (!this.timeouts) {
                    this.timeouts = []
                }
                if (t in this.timeouts) {
                    delete this.timeouts[t]
                }
            },
            setInterval: function setInterval(c, t) {
                return addTimer.call(this, arguments, true)
            },
            clearInterval: function clearInterval(t) {
                this.clearTimeout(t)
            },
            tick: function tick(m) {
                m = typeof m == "number" ? m : parseTime(m);
                var t = this.now,
                    a = this.now + m,
                    p = this.now;
                var b = this.firstTimerInRange(t, a);
                var f;
                while (b && t <= a) {
                    if (this.timeouts[b.id]) {
                        t = this.now = b.callAt;
                        try {
                            this.callTimer(b)
                        } catch (e) {
                            f = f || e
                        }
                    }
                    b = this.firstTimerInRange(p, a);
                    p = t
                }
                this.now = a;
                if (f) {
                    throw f
                }
            },
            firstTimerInRange: function(f, t) {
                var a, s, o;
                for (var i in this.timeouts) {
                    if (this.timeouts.hasOwnProperty(i)) {
                        if (this.timeouts[i].callAt < f || this.timeouts[i].callAt > t) {
                            continue
                        }
                        if (!s || this.timeouts[i].callAt < s) {
                            o = this.timeouts[i];
                            s = this.timeouts[i].callAt;
                            a = {
                                func: this.timeouts[i].func,
                                callAt: this.timeouts[i].callAt,
                                interval: this.timeouts[i].interval,
                                id: this.timeouts[i].id,
                                invokeArgs: this.timeouts[i].invokeArgs
                            }
                        }
                    }
                }
                return a || null
            },
            callTimer: function(timer) {
                if (typeof timer.interval == "number") {
                    this.timeouts[timer.id].callAt += timer.interval
                } else {
                    delete this.timeouts[timer.id]
                }
                try {
                    if (typeof timer.func == "function") {
                        timer.func.apply(null, timer.invokeArgs)
                    } else {
                        eval(timer.func)
                    }
                } catch (e) {
                    var exception = e
                }
                if (!this.timeouts[timer.id]) {
                    if (exception) {
                        throw exception
                    }
                    return
                }
                if (exception) {
                    throw exception
                }
            },
            reset: function reset() {
                this.timeouts = {}
            },
            Date: (function() {
                var N = Date;

                function C(y, m, d, h, a, s, b) {
                    switch (arguments.length) {
                        case 0:
                            return new N(C.clock.now);
                        case 1:
                            return new N(y);
                        case 2:
                            return new N(y, m);
                        case 3:
                            return new N(y, m, d);
                        case 4:
                            return new N(y, m, d, h);
                        case 5:
                            return new N(y, m, d, h, a);
                        case 6:
                            return new N(y, m, d, h, a, s);
                        default:
                            return new N(y, m, d, h, a, s, b)
                    }
                }
                return mirrorDateProperties(C, N)
            }())
        };

        function mirrorDateProperties(t, s) {
            if (s.now) {
                t.now = function now() {
                    return t.clock.now
                }
            } else {
                delete t.now
            }
            if (s.toSource) {
                t.toSource = function toSource() {
                    return s.toSource()
                }
            } else {
                delete t.toSource
            }
            t.toString = function toString() {
                return s.toString()
            };
            t.prototype = s.prototype;
            t.parse = s.parse;
            t.UTC = s.UTC;
            t.prototype.toUTCString = s.prototype.toUTCString;
            return t
        }
        var methods = ["Date", "setTimeout", "setInterval", "clearTimeout", "clearInterval"];

        function restore() {
            var m;
            for (var i = 0, l = this.methods.length; i < l; i++) {
                m = this.methods[i];
                if (global[m].hadOwnProperty) {
                    global[m] = this["_" + m]
                } else {
                    delete global[m]
                }
            }
            this.methods = []
        }
        function stubGlobal(m, c) {
            c[m].hadOwnProperty = Object.prototype.hasOwnProperty.call(global, m);
            c["_" + m] = global[m];
            if (m == "Date") {
                var d = mirrorDateProperties(c[m], global[m]);
                global[m] = d
            } else {
                global[m] = function() {
                    return c[m].apply(c, arguments)
                };
                for (var p in c[m]) {
                    if (c[m].hasOwnProperty(p)) {
                        global[m][p] = c[m][p]
                    }
                }
            }
            global[m].clock = c
        }
        sinon.useFakeTimers = function useFakeTimers(n) {
            var c = sinon.clock.create(n);
            c.restore = restore;
            c.methods = Array.prototype.slice.call(arguments, typeof n == "number" ? 1 : 0);
            if (c.methods.length === 0) {
                c.methods = methods
            }
            for (var i = 0, l = c.methods.length; i < l; i++) {
                stubGlobal(c.methods[i], c)
            }
            return c
        }
    }(typeof global != "undefined" && typeof global !== "function" ? global : this));
    sinon.timers = {
        setTimeout: setTimeout,
        clearTimeout: clearTimeout,
        setInterval: setInterval,
        clearInterval: clearInterval,
        Date: Date
    };
    if (typeof module == "object" && typeof require == "function") {
        module.exports = sinon
    }

    /**
     * Minimal Event interface implementation
     *
     * Original implementation by Sven Fuchs: https://gist.github.com/995028
     * Modifications and tests by Christian Johansen.
     *
     * @author Sven Fuchs (svenfuchs@artweb-design.de)
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2011 Sven Fuchs, Christian Johansen
     */

    if (typeof sinon == "undefined") {
        this.sinon = {}
    }(function() {
        var p = [].push;
        sinon.Event = function Event(t, b, c) {
            this.initEvent(t, b, c)
        };
        sinon.Event.prototype = {
            initEvent: function(t, b, c) {
                this.type = t;
                this.bubbles = b;
                this.cancelable = c
            },
            stopPropagation: function() {},
            preventDefault: function() {
                this.defaultPrevented = true
            }
        };
        sinon.EventTarget = {
            addEventListener: function addEventListener(e, l, u) {
                this.eventListeners = this.eventListeners || {};
                this.eventListeners[e] = this.eventListeners[e] || [];
                p.call(this.eventListeners[e], l)
            },
            removeEventListener: function removeEventListener(e, a, u) {
                var b = this.eventListeners && this.eventListeners[e] || [];
                for (var i = 0, l = b.length; i < l; ++i) {
                    if (b[i] == a) {
                        return b.splice(i, 1)
                    }
                }
            },
            dispatchEvent: function dispatchEvent(e) {
                var t = e.type;
                var l = this.eventListeners && this.eventListeners[t] || [];
                for (var i = 0; i < l.length; i++) {
                    if (typeof l[i] == "function") {
                        l[i].call(this, e)
                    } else {
                        l[i].handleEvent(e)
                    }
                }
                return !!e.defaultPrevented
            }
        }
    }());

    /**
     * Fake XMLHttpRequest object
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    if (typeof sinon == "undefined") {
        this.sinon = {}
    }
    sinon.xhr = {
        XMLHttpRequest: this.XMLHttpRequest
    };
    (function(g) {
        var x = sinon.xhr;
        x.GlobalXMLHttpRequest = g.XMLHttpRequest;
        x.GlobalActiveXObject = g.ActiveXObject;
        x.supportsActiveX = typeof x.GlobalActiveXObject != "undefined";
        x.supportsXHR = typeof x.GlobalXMLHttpRequest != "undefined";
        x.workingXHR = x.supportsXHR ? x.GlobalXMLHttpRequest : x.supportsActiveX ? function() {
            return new x.GlobalActiveXObject("MSXML2.XMLHTTP.3.0")
        } : false;
        var u = {
            "Accept-Charset": true,
            "Accept-Encoding": true,
            "Connection": true,
            "Content-Length": true,
            "Cookie": true,
            "Cookie2": true,
            "Content-Transfer-Encoding": true,
            "Date": true,
            "Expect": true,
            "Host": true,
            "Keep-Alive": true,
            "Referer": true,
            "TE": true,
            "Trailer": true,
            "Transfer-Encoding": true,
            "Upgrade": true,
            "User-Agent": true,
            "Via": true
        };

        function F() {
            this.readyState = F.UNSENT;
            this.requestHeaders = {};
            this.requestBody = null;
            this.status = 0;
            this.statusText = "";
            if (typeof F.onCreate == "function") {
                F.onCreate(this)
            }
        }
        function v(x) {
            if (x.readyState !== F.OPENED) {
                throw new Error("INVALID_STATE_ERR")
            }
            if (x.sendFlag) {
                throw new Error("INVALID_STATE_ERR")
            }
        }
        function a(e, h) {
            if (!e) return;
            for (var i = 0, l = e.length; i < l; i += 1) {
                h(e[i])
            }
        }
        function s(e, h) {
            for (var i = 0; i < e.length; i++) {
                if (h(e[i]) === true) return true
            };
            return false
        }
        var b = function(o, m, e) {
            switch (e.length) {
                case 0:
                    return o[m]();
                case 1:
                    return o[m](e[0]);
                case 2:
                    return o[m](e[0], e[1]);
                case 3:
                    return o[m](e[0], e[1], e[2]);
                case 4:
                    return o[m](e[0], e[1], e[2], e[3]);
                case 5:
                    return o[m](e[0], e[1], e[2], e[3], e[4])
            }
        };
        F.filters = [];
        F.addFilter = function(e) {
            this.filters.push(e)
        };
        var I = /MSIE/;
        F.defake = function(h, i) {
            var x = new sinon.xhr.workingXHR();
            a(["open", "setRequestHeader", "send", "abort", "getResponseHeader", "getAllResponseHeaders", "addEventListener", "overrideMimeType", "removeEventListener"], function(m) {
                h[m] = function() {
                    return b(x, m, arguments)
                }
            });
            var j = function(m) {
                a(m, function(n) {
                    try {
                        h[n] = x[n]
                    } catch (e) {
                        if (!I.test(navigator.userAgent)) throw e
                    }
                })
            };
            var k = function() {
                h.readyState = x.readyState;
                if (x.readyState >= F.HEADERS_RECEIVED) {
                    j(["status", "statusText"])
                }
                if (x.readyState >= F.LOADING) {
                    j(["responseText"])
                }
                if (x.readyState === F.DONE) {
                    j(["responseXML"])
                }
                if (h.onreadystatechange) h.onreadystatechange.call(h)
            };
            if (x.addEventListener) {
                for (var l in h.eventListeners) {
                    if (h.eventListeners.hasOwnProperty(l)) {
                        a(h.eventListeners[l], function(e) {
                            x.addEventListener(l, e)
                        })
                    }
                }
                x.addEventListener("readystatechange", k)
            } else {
                x.onreadystatechange = k
            }
            b(x, "open", i)
        };
        F.useFilters = false;

        function c(x) {
            if (x.readyState == F.DONE) {
                throw new Error("Request done")
            }
        }
        function d(x) {
            if (x.async &&x.readyState != F.HEADERS_RECEIVED) {
                throw new Error("No headers received")
            }
        }
        function f(e) {
            if (typeof e != "string") {
                var h = new Error("Attempted to respond to fake XMLHttpRequest with " + e + ", which is not a string.");
                h.name = "InvalidBodyException";
                throw h
            }
        }
        sinon.extend(F.prototype, sinon.EventTarget, {
            async :true, open: function open(m, e, h, i, p) {
                this.method = m;
                this.url = e;
                this.async = typeof h == "boolean" ? h : true;
                this.username = i;
                this.password = p;
                this.responseText = null;
                this.responseXML = null;
                this.requestHeaders = {};
                this.sendFlag = false;
                if (sinon.FakeXMLHttpRequest.useFilters === true) {
                    var j = arguments;
                    var k = s(F.filters, function(l) {
                        return l.apply(this, j)
                    });
                    if (k) {
                        return sinon.FakeXMLHttpRequest.defake(this, arguments)
                    }
                }
                this.readyStateChange(F.OPENED)
            },
            readyStateChange: function readyStateChange(h) {
                this.readyState = h;
                if (typeof this.onreadystatechange == "function") {
                    try {
                        this.onreadystatechange()
                    } catch (e) {
                        sinon.logError("Fake XHR onreadystatechange handler", e)
                    }
                }
                this.dispatchEvent(new sinon.Event("readystatechange"))
            },
            setRequestHeader: function setRequestHeader(h, e) {
                v(this);
                if (u[h] || /^(Sec-|Proxy-)/.test(h)) {
                    throw new Error("Refused to set unsafe header \"" + h + "\"")
                }
                if (this.requestHeaders[h]) {
                    this.requestHeaders[h] += "," + e
                } else {
                    this.requestHeaders[h] = e
                }
            },
            setResponseHeaders: function setResponseHeaders(h) {
                this.responseHeaders = {};
                for (var e in h) {
                    if (h.hasOwnProperty(e)) {
                        this.responseHeaders[e] = h[e]
                    }
                }
                if (this.async) {
                    this.readyStateChange(F.HEADERS_RECEIVED)
                } else {
                    this.readyState = F.HEADERS_RECEIVED
                }
            },
            send: function send(e) {
                v(this);
                if (!/^(get|head)$/i.test(this.method)) {
                    if (this.requestHeaders["Content-Type"]) {
                        var h = this.requestHeaders["Content-Type"].split(";");
                        this.requestHeaders["Content-Type"] = h[0] + ";charset=utf-8"
                    } else {
                        this.requestHeaders["Content-Type"] = "text/plain;charset=utf-8"
                    }
                    this.requestBody = e
                }
                this.errorFlag = false;
                this.sendFlag = this.async;
                this.readyStateChange(F.OPENED);
                if (typeof this.onSend == "function") {
                    this.onSend(this)
                }
            },
            abort: function abort() {
                this.aborted = true;
                this.responseText = null;
                this.errorFlag = true;
                this.requestHeaders = {};
                if (this.readyState > sinon.FakeXMLHttpRequest.UNSENT && this.sendFlag) {
                    this.readyStateChange(sinon.FakeXMLHttpRequest.DONE);
                    this.sendFlag = false
                }
                this.readyState = sinon.FakeXMLHttpRequest.UNSENT
            },
            getResponseHeader: function getResponseHeader(e) {
                if (this.readyState < F.HEADERS_RECEIVED) {
                    return null
                }
                if (/^Set-Cookie2?$/i.test(e)) {
                    return null
                }
                e = e.toLowerCase();
                for (var h in this.responseHeaders) {
                    if (h.toLowerCase() == e) {
                        return this.responseHeaders[h]
                    }
                }
                return null
            },
            getAllResponseHeaders: function getAllResponseHeaders() {
                if (this.readyState < F.HEADERS_RECEIVED) {
                    return ""
                }
                var h = "";
                for (var e in this.responseHeaders) {
                    if (this.responseHeaders.hasOwnProperty(e) && !/^Set-Cookie2?$/i.test(e)) {
                        h += e + ": " + this.responseHeaders[e] + "\r\n"
                    }
                }
                return h
            },
            setResponseBody: function setResponseBody(h) {
                c(this);
                d(this);
                f(h);
                var i = this.chunkSize || 10;
                var j = 0;
                this.responseText = "";
                do {
                    if (this.async) {
                        this.readyStateChange(F.LOADING)
                    }
                    this.responseText += h.substring(j, j + i);
                    j += i
                } while (j < h.length);
                var t = this.getResponseHeader("Content-Type");
                if (this.responseText && (!t || /(text\/xml)|(application\/xml)|(\+xml)/.test(t))) {
                    try {
                        this.responseXML = F.parseXML(this.responseText)
                    } catch (e) {}
                }
                if (this.async) {
                    this.readyStateChange(F.DONE)
                } else {
                    this.readyState = F.DONE
                }
            },
            respond: function respond(e, h, i) {
                this.setResponseHeaders(h || {});
                this.status = typeof e == "number" ? e : 200;
                this.statusText = F.statusCodes[this.status];
                this.setResponseBody(i || "")
            }
        });
        sinon.extend(F, {
            UNSENT: 0,
            OPENED: 1,
            HEADERS_RECEIVED: 2,
            LOADING: 3,
            DONE: 4
        });
        F.parseXML = function parseXML(t) {
            var e;
            if (typeof DOMParser != "undefined") {
                var p = new DOMParser();
                e = p.parseFromString(t, "text/xml")
            } else {
                e = new ActiveXObject("Microsoft.XMLDOM");
                e.async = "false";
                e.loadXML(t)
            }
            return e
        };
        F.statusCodes = {
            100: "Continue",
            101: "Switching Protocols",
            200: "OK",
            201: "Created",
            202: "Accepted",
            203: "Non-Authoritative Information",
            204: "No Content",
            205: "Reset Content",
            206: "Partial Content",
            300: "Multiple Choice",
            301: "Moved Permanently",
            302: "Found",
            303: "See Other",
            304: "Not Modified",
            305: "Use Proxy",
            307: "Temporary Redirect",
            400: "Bad Request",
            401: "Unauthorized",
            402: "Payment Required",
            403: "Forbidden",
            404: "Not Found",
            405: "Method Not Allowed",
            406: "Not Acceptable",
            407: "Proxy Authentication Required",
            408: "Request Timeout",
            409: "Conflict",
            410: "Gone",
            411: "Length Required",
            412: "Precondition Failed",
            413: "Request Entity Too Large",
            414: "Request-URI Too Long",
            415: "Unsupported Media Type",
            416: "Requested Range Not Satisfiable",
            417: "Expectation Failed",
            422: "Unprocessable Entity",
            500: "Internal Server Error",
            501: "Not Implemented",
            502: "Bad Gateway",
            503: "Service Unavailable",
            504: "Gateway Timeout",
            505: "HTTP Version Not Supported"
        };
        sinon.useFakeXMLHttpRequest = function() {
            sinon.FakeXMLHttpRequest.restore = function restore(k) {
                if (x.supportsXHR) {
                    g.XMLHttpRequest = x.GlobalXMLHttpRequest
                }
                if (x.supportsActiveX) {
                    g.ActiveXObject = x.GlobalActiveXObject
                }
                delete sinon.FakeXMLHttpRequest.restore;
                if (k !== true) {
                    delete sinon.FakeXMLHttpRequest.onCreate
                }
            };
            if (x.supportsXHR) {
                g.XMLHttpRequest = sinon.FakeXMLHttpRequest
            }
            if (x.supportsActiveX) {
                g.ActiveXObject = function ActiveXObject(o) {
                    if (o == "Microsoft.XMLHTTP" || /^Msxml2\.XMLHTTP/i.test(o)) {
                        return new sinon.FakeXMLHttpRequest()
                    }
                    return new x.GlobalActiveXObject(o)
                }
            }
            return sinon.FakeXMLHttpRequest
        };
        sinon.FakeXMLHttpRequest = F
    })(this);
    if (typeof module == "object" && typeof require == "function") {
        module.exports = sinon
    }

    /**
     * The Sinon "server" mimics a web server that receives requests from
     * sinon.FakeXMLHttpRequest and provides an API to respond to those requests,
     * both synchronously and asynchronously. To respond synchronuously, canned
     * answers have to be provided upfront.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    if (typeof sinon == "undefined") {
        var sinon = {}
    }
    sinon.fakeServer = (function() {
        var p = [].push;

        function F() {}
        function c(d) {
            F.prototype = d;
            return new F()
        }
        function r(h) {
            var d = h;
            if (Object.prototype.toString.call(h) != "[object Array]") {
                d = [200, {},
                h]
            }
            if (typeof d[2] != "string") {
                throw new TypeError("Fake server response body should be string, but was " + typeof d[2])
            }
            return d
        }
        var w = typeof window !== "undefined" ? window.location : {};
        var a = new RegExp("^" + w.protocol + "//" + w.host);

        function m(d, e, f) {
            var g = d.method;
            var h = !g || g.toLowerCase() == e.toLowerCase();
            var u = d.url;
            var i = !u || u == f || (typeof u.test == "function" && u.test(f));
            return h && i
        }
        function b(d, e) {
            var f = this.getHTTPMethod(e);
            var g = e.url;
            if (!/^https?:\/\//.test(g) || a.test(g)) {
                g = g.replace(a, "")
            }
            if (m(d, this.getHTTPMethod(e), g)) {
                if (typeof d.response == "function") {
                    var h = d.url;
                    var i = [e].concat(!h ? [] : g.match(h).slice(1));
                    return d.response.apply(d, i)
                }
                return true
            }
            return false
        }
        return {
            create: function() {
                var s = c(this);
                this.xhr = sinon.useFakeXMLHttpRequest();
                s.requests = [];
                this.xhr.onCreate = function(x) {
                    s.addRequest(x)
                };
                return s
            },
            addRequest: function addRequest(x) {
                var s = this;
                p.call(this.requests, x);
                x.onSend = function() {
                    s.handleRequest(this)
                };
                if (this.autoRespond && !this.responding) {
                    setTimeout(function() {
                        s.responding = false;
                        s.respond()
                    }, this.autoRespondAfter || 10);
                    this.responding = true
                }
            },
            getHTTPMethod: function getHTTPMethod(d) {
                if (this.fakeHTTPMethods && /post/i.test(d.method)) {
                    var e = (d.requestBody || "").match(/_method=([^\b;]+)/);
                    return !!e ? e[1] : d.method
                }
                return d.method
            },
            handleRequest: function handleRequest(x) {
                if (x.async) {
                    if (!this.queue) {
                        this.queue = []
                    }
                    p.call(this.queue, x)
                } else {
                    this.processRequest(x)
                }
            },
            respondWith: function respondWith(d, u, e) {
                if (arguments.length == 1 && typeof d != "function") {
                    this.response = r(d);
                    return
                }
                if (!this.responses) {
                    this.responses = []
                }
                if (arguments.length == 1) {
                    e = d;
                    u = d = null
                }
                if (arguments.length == 2) {
                    e = u;
                    u = d;
                    d = null
                }
                p.call(this.responses, {
                    method: d,
                    url: u,
                    response: typeof e == "function" ? e : r(e)
                })
            },
            respond: function respond() {
                if (arguments.length > 0) this.respondWith.apply(this, arguments);
                var q = this.queue || [];
                var d;
                while (d = q.shift()) {
                    this.processRequest(d)
                }
            },
            processRequest: function processRequest(d) {
                try {
                    if (d.aborted) {
                        return
                    }
                    var f = this.response || [404, {}, ""];
                    if (this.responses) {
                        for (var i = 0, l = this.responses.length; i < l; i++) {
                            if (b.call(this, this.responses[i], d)) {
                                f = this.responses[i].response;
                                break
                            }
                        }
                    }
                    if (d.readyState != 4) {
                        d.respond(f[0], f[1], f[2])
                    }
                } catch (e) {
                    sinon.logError("Fake server request processing", e)
                }
            },
            restore: function restore() {
                return this.xhr.restore && this.xhr.restore.apply(this.xhr, arguments)
            }
        }
    }());
    if (typeof module == "object" && typeof require == "function") {
        module.exports = sinon
    }

    /**
     * Add-on for sinon.fakeServer that automatically handles a fake timer along with
     * the FakeXMLHttpRequest. The direct inspiration for this add-on is jQuery
     * 1.3.x, which does not use xhr object's onreadystatehandler at all - instead,
     * it polls the object for completion with setInterval. Dispite the direct
     * motivation, there is nothing jQuery-specific in this file, so it can be used
     * in any environment where the ajax implementation depends on setInterval or
     * setTimeout.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function() {
        function S() {}
        S.prototype = sinon.fakeServer;
        sinon.fakeServerWithClock = new S();
        sinon.fakeServerWithClock.addRequest = function addRequest(x) {
            if (x.async) {
                if (typeof setTimeout.clock == "object") {
                    this.clock = setTimeout.clock
                } else {
                    this.clock = sinon.useFakeTimers();
                    this.resetClock = true
                }
                if (!this.longestTimeout) {
                    var c = this.clock.setTimeout;
                    var a = this.clock.setInterval;
                    var s = this;
                    this.clock.setTimeout = function(f, t) {
                        s.longestTimeout = Math.max(t, s.longestTimeout || 0);
                        return c.apply(this, arguments)
                    };
                    this.clock.setInterval = function(f, t) {
                        s.longestTimeout = Math.max(t, s.longestTimeout || 0);
                        return a.apply(this, arguments)
                    }
                }
            }
            return sinon.fakeServer.addRequest.call(this, x)
        };
        sinon.fakeServerWithClock.respond = function respond() {
            var r = sinon.fakeServer.respond.apply(this, arguments);
            if (this.clock) {
                this.clock.tick(this.longestTimeout || 0);
                this.longestTimeout = 0;
                if (this.resetClock) {
                    this.clock.restore();
                    this.resetClock = false
                }
            }
            return r
        };
        sinon.fakeServerWithClock.restore = function restore() {
            if (this.clock) {
                this.clock.restore()
            }
            return sinon.fakeServer.restore.apply(this, arguments)
        }
    }());

    /**
     * Manages fake collections as well as fake utilities such as Sinon's
     * timers and fake XHR implementation in one convenient object.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    if (typeof module == "object" && typeof require == "function") {
        var sinon = require("../sinon");
        sinon.extend(sinon, require("./util/fake_timers"))
    }(function() {
        var p = [].push;

        function e(s, c, k, v) {
            if (!v) {
                return
            }
            if (c.injectInto) {
                c.injectInto[k] = v
            } else {
                p.call(s.args, v)
            }
        }
        function a(c) {
            var s = sinon.create(sinon.sandbox);
            if (c.useFakeServer) {
                if (typeof c.useFakeServer == "object") {
                    s.serverPrototype = c.useFakeServer
                }
                s.useFakeServer()
            }
            if (c.useFakeTimers) {
                if (typeof c.useFakeTimers == "object") {
                    s.useFakeTimers.apply(s, c.useFakeTimers)
                } else {
                    s.useFakeTimers()
                }
            }
            return s
        }
        sinon.sandbox = sinon.extend(sinon.create(sinon.collection), {
            useFakeTimers: function useFakeTimers() {
                this.clock = sinon.useFakeTimers.apply(sinon, arguments);
                return this.add(this.clock)
            },
            serverPrototype: sinon.fakeServer,
            useFakeServer: function useFakeServer() {
                var b = this.serverPrototype || sinon.fakeServer;
                if (!b || !b.create) {
                    return null
                }
                this.server = b.create();
                return this.add(this.server)
            },
            inject: function(o) {
                sinon.collection.inject.call(this, o);
                if (this.clock) {
                    o.clock = this.clock
                }
                if (this.server) {
                    o.server = this.server;
                    o.requests = this.server.requests
                }
                return o
            },
            create: function(c) {
                if (!c) {
                    return sinon.create(sinon.sandbox)
                }
                var s = a(c);
                s.args = s.args || [];
                var b, v, d = s.inject({});
                if (c.properties) {
                    for (var i = 0, l = c.properties.length; i < l; i++) {
                        b = c.properties[i];
                        v = d[b] || b == "sandbox" && s;
                        e(s, c, b, v)
                    }
                } else {
                    e(s, c, "sandbox", v)
                }
                return s
            }
        });
        sinon.sandbox.useFakeXMLHttpRequest = sinon.sandbox.useFakeServer;
        if (typeof module == "object" && typeof require == "function") {
            module.exports = sinon.sandbox
        }
    }());

    /**
     * Test function, sandboxes fakes
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(s) {
        var c = typeof module == "object" && typeof require == "function";
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s) {
            return
        }
        function t(a) {
            var b = typeof a;
            if (b != "function") {
                throw new TypeError("sinon.test needs to wrap a test function, got " + b)
            }
            return function() {
                var d = s.getConfig(s.config);
                d.injectInto = d.injectIntoThis && this || d.injectInto;
                var f = s.sandbox.create(d);
                var g, r;
                var h = Array.prototype.slice.call(arguments).concat(f.args);
                try {
                    r = a.apply(this, h)
                } catch (e) {
                    g = e
                }
                if (typeof g !== "undefined") {
                    f.restore();
                    throw g
                } else {
                    f.verifyAndRestore()
                }
                return r
            }
        }
        t.config = {
            injectIntoThis: true,
            injectInto: null,
            properties: ["spy", "stub", "mock", "clock", "server", "requests"],
            useFakeTimers: true,
            useFakeServer: true
        };
        if (c) {
            module.exports = t
        } else {
            s.test = t
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Test case, sandboxes all test functions
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(s) {
        var c = typeof module == "object" && typeof require == "function";
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s || !Object.prototype.hasOwnProperty) {
            return
        }
        function a(p, b, d) {
            return function() {
                if (b) {
                    b.apply(this, arguments)
                }
                var f, r;
                try {
                    r = p.apply(this, arguments)
                } catch (e) {
                    f = e
                }
                if (d) {
                    d.apply(this, arguments)
                }
                if (f) {
                    throw f
                }
                return r
            }
        }
        function t(b, p) {
            if (!b || typeof b != "object") {
                throw new TypeError("sinon.testCase needs an object with test functions")
            }
            p = p || "test";
            var r = new RegExp("^" + p);
            var m = {}, d, e, f;
            var g = b.setUp;
            var h = b.tearDown;
            for (d in b) {
                if (b.hasOwnProperty(d)) {
                    e = b[d];
                    if (/^(setUp|tearDown)$/.test(d)) {
                        continue
                    }
                    if (typeof e == "function" && r.test(d)) {
                        f = e;
                        if (g || h) {
                            f = a(e, g, h)
                        }
                        m[d] = s.test(f)
                    } else {
                        m[d] = b[d]
                    }
                }
            }
            return m
        }
        if (c) {
            module.exports = t
        } else {
            s.testCase = t
        }
    }(typeof sinon == "object" && sinon || null));

    /**
     * Assertions matching the test spy retrieval interface.
     *
     * @author Christian Johansen (christian@cjohansen.no)
     * @license BSD
     *
     * Copyright (c) 2010-2011 Christian Johansen
     */

    (function(s, g) {
        var c = typeof module == "object" && typeof require == "function";
        var a = Array.prototype.slice;
        var b;
        if (!s && c) {
            s = require("../sinon")
        }
        if (!s) {
            return
        }
        function v() {
            var e;
            for (var i = 0, l = arguments.length; i < l; ++i) {
                e = arguments[i];
                if (!e) {
                    b.fail("fake is not a spy")
                }
                if (typeof e != "function") {
                    b.fail(e + " is not a function")
                }
                if (typeof e.getCall != "function") {
                    b.fail(e + " is not stubbed")
                }
            }
        }
        function f(o, e) {
            o = o || g;
            var h = o.fail || b.fail;
            h.call(o, e)
        }
        function m(n, e, h) {
            if (arguments.length == 2) {
                h = e;
                e = n
            }
            b[n] = function(i) {
                v(i);
                var j = a.call(arguments, 1);
                var k = false;
                if (typeof e == "function") {
                    k = !e(i)
                } else {
                    k = typeof i[e] == "function" ? !i[e].apply(i, j) : !i[e]
                }
                if (k) {
                    f(this, i.printf.apply(i, [h].concat(j)))
                } else {
                    b.pass(n)
                }
            }
        }
        function d(p, e) {
            return !p || /^fail/.test(e) ? e : p + e.slice(0, 1).toUpperCase() + e.slice(1)
        };
        b = {
            failException: "AssertError",
            fail: function fail(e) {
                var h = new Error(e);
                h.name = this.failException || b.failException;
                throw h
            },
            pass: function pass(e) {},
            callOrder: function assertCallOrder() {
                v.apply(null, arguments);
                var h = "",
                    i = "";
                if (!s.calledInOrder(arguments)) {
                    try {
                        h = [].join.call(arguments, ", ");
                        i = s.orderByFirstCall(a.call(arguments)).join(", ")
                    } catch (e) {}
                    f(this, "expected " + h + " to be " + "called in order but were called as " + i)
                } else {
                    b.pass("callOrder")
                }
            },
            callCount: function assertCallCount(e, h) {
                v(e);
                if (e.callCount != h) {
                    var i = "expected %n to be called " + s.timesInWords(h) + " but was called %c%C";
                    f(this, e.printf(i))
                } else {
                    b.pass("callCount")
                }
            },
            expose: function expose(t, e) {
                if (!t) {
                    throw new TypeError("target is null or undefined")
                }
                var o = e || {};
                var p = typeof o.prefix == "undefined" && "assert" || o.prefix;
                var i = typeof o.includeFail == "undefined" || !! o.includeFail;
                for (var h in this) {
                    if (h != "export" && (i || !/^(fail)/.test(h))) {
                        t[d(p, h)] = this[h]
                    }
                }
                return t
            }
        };
        m("called", "expected %n to have been called at least once but was never called");
        m("notCalled", function(e) {
            return !e.called
        }, "expected %n to not have been called but was called %c%C");
        m("calledOnce", "expected %n to be called once but was called %c%C");
        m("calledTwice", "expected %n to be called twice but was called %c%C");
        m("calledThrice", "expected %n to be called thrice but was called %c%C");
        m("calledOn", "expected %n to be called with %1 as this but was called with %t");
        m("alwaysCalledOn", "expected %n to always be called with %1 as this but was called with %t");
        m("calledWithNew", "expected %n to be called with new");
        m("alwaysCalledWithNew", "expected %n to always be called with new");
        m("calledWith", "expected %n to be called with arguments %*%C");
        m("calledWithMatch", "expected %n to be called with match %*%C");
        m("alwaysCalledWith", "expected %n to always be called with arguments %*%C");
        m("alwaysCalledWithMatch", "expected %n to always be called with match %*%C");
        m("calledWithExactly", "expected %n to be called with exact arguments %*%C");
        m("alwaysCalledWithExactly", "expected %n to always be called with exact arguments %*%C");
        m("neverCalledWith", "expected %n to never be called with arguments %*%C");
        m("neverCalledWithMatch", "expected %n to never be called with match %*%C");
        m("threw", "%n did not throw exception%C");
        m("alwaysThrew", "%n did not always throw exception%C");
        if (c) {
            module.exports = b
        } else {
            s.assert = b
        }
    }(typeof sinon == "object" && sinon || null, typeof window != "undefined" ? window : global));
    return sinon
}.call(typeof window != 'undefined' && window || {}));