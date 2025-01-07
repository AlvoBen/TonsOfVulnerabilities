﻿/*!
 * D3 v2.9.0 - http://d3js.org/
 *
 * Copyright (c) 2012, Michael Bostock
 * All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MICHAEL BOSTOCK BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

(function() {
    if (!Date.now) Date.now = function() {
        return +new Date
    };
    try {
        document.createElement("div").style.setProperty("opacity", 0, "")
    } catch (D) {
        var F = CSSStyleDeclaration.prototype,
            G = F.setProperty;
        F.setProperty = function(n, v, p) {
            G.call(this, n, v + "", p)
        }
    }
    d3 = {
        version: "2.9.6"
    };

    function I(c, p) {
        try {
            for (var k in p) {
                Object.defineProperty(c.prototype, k, {
                    value: p[k],
                    enumerable: false
                })
            }
        } catch (e) {
            c.prototype = p
        }
    }
    var J = M;

    function K(p) {
        var i = -1,
            n = p.length,
            a = [];
        while (++i < n) a.push(p[i]);
        return a
    }
    function M(p) {
        return Array.prototype.slice.call(p)
    }
    try {
        J(document.documentElement.childNodes)[0].nodeType
    } catch (e) {
        J = K
    }
    var N = [].__proto__ ? function(a, p) {
            a.__proto__ = p
        } : function(a, p) {
            for (var b in p) a[b] = p[b]
        };
    d3.map = function(o) {
        var m = new O;
        for (var k in o) m.set(k, o[k]);
        return m
    };

    function O() {}
    I(O, {
        has: function(k) {
            return P + k in this
        },
        get: function(k) {
            return this[P + k]
        },
        set: function(k, v) {
            return this[P + k] = v
        },
        remove: function(k) {
            k = P + k;
            return k in this && delete this[k]
        },
        keys: function() {
            var k = [];
            this.forEach(function(a) {
                k.push(a)
            });
            return k
        },
        values: function() {
            var v = [];
            this.forEach(function(k, a) {
                v.push(a)
            });
            return v
        },
        entries: function() {
            var a = [];
            this.forEach(function(k, v) {
                a.push({
                    key: k,
                    value: v
                })
            });
            return a
        },
        forEach: function(f) {
            for (var k in this) {
                if (k.charCodeAt(0) === Q) {
                    f.call(this, k.substring(1), this[k])
                }
            }
        }
    });
    var P = "\0",
        Q = P.charCodeAt(0);

    function R(d) {
        return d
    }
    function S() {
        return this
    }
    function T() {
        return true
    }
    function U(v) {
        return typeof v === "function" ? v : function() {
            return v
        }
    }
    d3.functor = U;
    d3.rebind = function(t, s) {
        var i = 1,
            n = arguments.length,
            m;
        while (++i < n) t[m = arguments[i]] = V(t, s, s[m]);
        return t
    };

    function V(t, s, m) {
        return function() {
            var v = m.apply(s, arguments);
            return arguments.length ? t : v
        }
    }
    d3.ascending = function(a, b) {
        return a < b ? -1 : a > b ? 1 : a >= b ? 0 : NaN
    };
    d3.descending = function(a, b) {
        return b < a ? -1 : b > a ? 1 : b >= a ? 0 : NaN
    };
    d3.mean = function(b, f) {
        var n = b.length,
            a, m = 0,
            i = -1,
            j = 0;
        if (arguments.length === 1) {
            while (++i < n) if (W(a = b[i])) m += (a - m) / ++j
        } else {
            while (++i < n) if (W(a = f.call(b, b[i], i))) m += (a - m) / ++j
        }
        return j ? m : undefined
    };
    d3.median = function(a, f) {
        if (arguments.length > 1) a = a.map(f);
        a = a.filter(W);
        return a.length ? d3.quantile(a.sort(d3.ascending), .5) : undefined
    };
    d3.min = function(c, f) {
        var i = -1,
            n = c.length,
            a, b;
        if (arguments.length === 1) {
            while (++i < n && ((a = c[i]) == null || a != a)) a = undefined;
            while (++i < n) if ((b = c[i]) != null && a > b) a = b
        } else {
            while (++i < n && ((a = f.call(c, c[i], i)) == null || a != a)) a = undefined;
            while (++i < n) if ((b = f.call(c, c[i], i)) != null && a > b) a = b
        }
        return a
    };
    d3.max = function(c, f) {
        var i = -1,
            n = c.length,
            a, b;
        if (arguments.length === 1) {
            while (++i < n && ((a = c[i]) == null || a != a)) a = undefined;
            while (++i < n) if ((b = c[i]) != null && b > a) a = b
        } else {
            while (++i < n && ((a = f.call(c, c[i], i)) == null || a != a)) a = undefined;
            while (++i < n) if ((b = f.call(c, c[i], i)) != null && b > a) a = b
        }
        return a
    };
    d3.extent = function(d, f) {
        var i = -1,
            n = d.length,
            a, b, c;
        if (arguments.length === 1) {
            while (++i < n && ((a = c = d[i]) == null || a != a)) a = c = undefined;
            while (++i < n) if ((b = d[i]) != null) {
                if (a > b) a = b;
                if (c < b) c = b
            }
        } else {
            while (++i < n && ((a = c = f.call(d, d[i], i)) == null || a != a)) a = undefined;
            while (++i < n) if ((b = f.call(d, d[i], i)) != null) {
                if (a > b) a = b;
                if (c < b) c = b
            }
        }
        return [a, c]
    };
    d3.random = {
        normal: function(m, d) {
            if (arguments.length < 2) d = 1;
            if (arguments.length < 1) m = 0;
            return function() {
                var x, y, r;
                do {
                    x = Math.random() * 2 - 1;
                    y = Math.random() * 2 - 1;
                    r = x * x + y * y
                } while (!r || r > 1);
                return m + d * x * Math.sqrt(-2 * Math.log(r) / r)
            }
        }
    };

    function W(x) {
        return x != null && !isNaN(x)
    }
    d3.sum = function(b, f) {
        var s = 0,
            n = b.length,
            a, i = -1;
        if (arguments.length === 1) {
            while (++i < n) if (!isNaN(a = +b[i])) s += a
        } else {
            while (++i < n) if (!isNaN(a = +f.call(b, b[i], i))) s += a
        }
        return s
    };
    d3.quantile = function(a, p) {
        var H = (a.length - 1) * p + 1,
            h = Math.floor(H),
            v = a[h - 1],
            e = H - h;
        return e ? v + e * (a[h] - v) : v
    };
    d3.transpose = function(m) {
        return d3.zip.apply(d3, m)
    };
    d3.zip = function() {
        if (!(n = arguments.length)) return [];
        for (var i = -1, m = d3.min(arguments, X), z = new Array(m); ++i < m;) {
            for (var j = -1, n, a = z[i] = new Array(n); ++j < n;) {
                a[j] = arguments[j][i]
            }
        }
        return z
    };

    function X(d) {
        return d.length
    }
    d3.bisector = function(f) {
        return {
            left: function(a, x, l, h) {
                if (arguments.length < 3) l = 0;
                if (arguments.length < 4) h = a.length;
                while (l < h) {
                    var m = l + h >> 1;
                    if (f.call(a, a[m], m) < x) l = m + 1;
                    else h = m
                }
                return l
            },
            right: function(a, x, l, h) {
                if (arguments.length < 3) l = 0;
                if (arguments.length < 4) h = a.length;
                while (l < h) {
                    var m = l + h >> 1;
                    if (x < f.call(a, a[m], m)) h = m;
                    else l = m + 1
                }
                return l
            }
        }
    };
    var Y = d3.bisector(function(d) {
        return d
    });
    d3.bisectLeft = Y.left;
    d3.bisect = d3.bisectRight = Y.right;
    d3.first = function(c, f) {
        var i = 0,
            n = c.length,
            a = c[0],
            b;
        if (arguments.length === 1) f = d3.ascending;
        while (++i < n) {
            if (f.call(c, a, b = c[i]) > 0) {
                a = b
            }
        }
        return a
    };
    d3.last = function(c, f) {
        var i = 0,
            n = c.length,
            a = c[0],
            b;
        if (arguments.length === 1) f = d3.ascending;
        while (++i < n) {
            if (f.call(c, a, b = c[i]) <= 0) {
                a = b
            }
        }
        return a
    };
    d3.nest = function() {
        var c = {}, k = [],
            s = [],
            g, r;

        function m(a, d) {
            if (d >= k.length) return r ? r.call(c, a) : (g ? a.sort(g) : a);
            var i = -1,
                n = a.length,
                b = k[d++],
                f, j, v = new O,
                l, o = {};
            while (++i < n) {
                if (l = v.get(f = b(j = a[i]))) {
                    l.push(j)
                } else {
                    v.set(f, [j])
                }
            }
            v.forEach(function(f) {
                o[f] = m(v.get(f), d)
            });
            return o
        }
        function h(m, d) {
            if (d >= k.length) return m;
            var a = [],
                f = s[d++],
                i;
            for (i in m) {
                a.push({
                    key: i,
                    values: h(m[i], d)
                })
            }
            if (f) a.sort(function(a, b) {
                return f(a.key, b.key)
            });
            return a
        }
        c.map = function(a) {
            return m(a, 0)
        };
        c.entries = function(a) {
            return h(m(a, 0), 0)
        };
        c.key = function(d) {
            k.push(d);
            return c
        };
        c.sortKeys = function(o) {
            s[k.length - 1] = o;
            return c
        };
        c.sortValues = function(o) {
            g = o;
            return c
        };
        c.rollup = function(f) {
            r = f;
            return c
        };
        return c
    };
    d3.keys = function(m) {
        var k = [];
        for (var a in m) k.push(a);
        return k
    };
    d3.values = function(m) {
        var v = [];
        for (var k in m) v.push(m[k]);
        return v
    };
    d3.entries = function(m) {
        var a = [];
        for (var k in m) a.push({
            key: k,
            value: m[k]
        });
        return a
    };
    d3.permute = function(a, b) {
        var p = [],
            i = -1,
            n = b.length;
        while (++i < n) p[i] = a[b[i]];
        return p
    };
    d3.merge = function(a) {
        return Array.prototype.concat.apply([], a)
    };
    d3.split = function(a, f) {
        var b = [],
            v = [],
            c, i = -1,
            n = a.length;
        if (arguments.length < 2) f = Z;
        while (++i < n) {
            if (f.call(v, c = a[i], i)) {
                v = []
            } else {
                if (!v.length) b.push(v);
                v.push(c)
            }
        }
        return b
    };

    function Z(d) {
        return d == null
    }
    function $(s) {
        return s.replace(/^\s+|\s+$/g, "").replace(/\s+/g, " ")
    }
    d3.range = function(s, a, b) {
        if (arguments.length < 3) {
            b = 1;
            if (arguments.length < 2) {
                a = s;
                s = 0
            }
        }
        if ((a - s) / b === Infinity) throw new Error("infinite range");
        var r = [],
            k = b1(Math.abs(b)),
            i = -1,
            j;
        s *= k, a *= k, b *= k;
        if (b < 0) while ((j = s + b * ++i) > a) r.push(j / k);
        else while ((j = s + b * ++i) < a) r.push(j / k);
        return r
    };

    function b1(x) {
        var k = 1;
        while (x * k % 1) k *= 10;
        return k
    }
    d3.requote = function(s) {
        return s.replace(f1, "\\$&")
    };
    var f1 = /[\\\^\$\*\+\?\|\[\]\(\)\.\{\}]/g;
    d3.round = function(x, n) {
        return n ? Math.round(x * (n = Math.pow(10, n))) / n : Math.round(x)
    };
    d3.xhr = function(u, m, c) {
        var r = new XMLHttpRequest;
        if (arguments.length < 3) c = m, m = null;
        else if (m && r.overrideMimeType) r.overrideMimeType(m);
        r.open("GET", u, true);
        if (m) r.setRequestHeader("Accept", m);
        r.onreadystatechange = function() {
            if (r.readyState === 4) {
                var s = r.status;
                c(!s && r.response || s >= 200 && s < 300 || s === 304 ? r : null)
            }
        };
        r.send(null)
    };
    d3.text = function(u, m, c) {
        function r(a) {
            c(a && a.responseText)
        }
        if (arguments.length < 3) {
            c = m;
            m = null
        }
        d3.xhr(u, m, r)
    };
    d3.json = function(u, c) {
        d3.text(u, "application/json", function(t) {
            c(t ? JSON.parse(t) : null)
        })
    };
    d3.html = function(u, c) {
        d3.text(u, "text/html", function(t) {
            if (t != null) {
                var r = document.createRange();
                r.selectNode(document.body);
                t = r.createContextualFragment(t)
            }
            c(t)
        })
    };
    d3.xml = function(u, m, c) {
        function r(a) {
            c(a && a.responseXML)
        }
        if (arguments.length < 3) {
            c = m;
            m = null
        }
        d3.xhr(u, m, r)
    };
    var g1 = {
        svg: "http://www.w3.org/2000/svg",
        xhtml: "http://www.w3.org/1999/xhtml",
        xlink: "http://www.w3.org/1999/xlink",
        xml: "http://www.w3.org/XML/1998/namespace",
        xmlns: "http://www.w3.org/2000/xmlns/"
    };
    d3.ns = {
        prefix: g1,
        qualify: function(n) {
            var i = n.indexOf(":"),
                p = n;
            if (i >= 0) {
                p = n.substring(0, i);
                n = n.substring(i + 1)
            }
            return g1.hasOwnProperty(p) ? {
                space: g1[p],
                local: n
            } : n
        }
    };
    d3.dispatch = function() {
        var d = new j1,
            i = -1,
            n = arguments.length;
        while (++i < n) d[arguments[i]] = k1(d);
        return d
    };

    function j1() {}
    j1.prototype.on = function(t, l) {
        var i = t.indexOf("."),
            n = "";
        if (i > 0) {
            n = t.substring(i + 1);
            t = t.substring(0, i)
        }
        return arguments.length < 2 ? this[t].on(n) : this[t].on(n, l)
    };

    function k1(d) {
        var a = [],
            b = new O;

        function c() {
            var z = a,
                i = -1,
                n = z.length,
                l;
            while (++i < n) if (l = z[i].on) l.apply(this, arguments);
            return d
        }
        c.on = function(n, f) {
            var l = b.get(n),
                i;
            if (arguments.length < 2) return l && l.on;
            if (l) {
                l.on = null;
                a = a.slice(0, i = a.indexOf(l)).concat(a.slice(i + 1));
                b.remove(n)
            }
            if (f) a.push(b.set(n, {
                on: f
            }));
            return d
        };
        return c
    }
    d3.format = function(s) {
        var m = o1.exec(s),
            f = m[1] || " ",
            a = m[3] || "",
            z = m[5],
            w = +m[6],
            c = m[7],
            p = m[8],
            t = m[9],
            b = 1,
            d = "",
            i = false;
        if (p) p = +p.substring(1);
        if (z) {
            f = "0";
            if (c) w -= Math.floor((w - 1) / 4)
        }
        switch (t) {
            case "n":
                c = true;
                t = "g";
                break;
            case "%":
                b = 100;
                d = "%";
                t = "f";
                break;
            case "p":
                b = 100;
                d = "%";
                t = "r";
                break;
            case "d":
                i = true;
                p = 0;
                break;
            case "s":
                b = -1;
                t = "r";
                break
        }
        if (t == "r" && !p) t = "g";
        t = q1.get(t) || w1;
        return function(v) {
            if (i && (v % 1)) return "";
            var n = (v < 0) && (v = -v) ? "\u2212" : a;
            if (b < 0) {
                var g = d3.formatPrefix(v, p);
                v = g.scale(v);
                d = g.symbol
            } else {
                v *= b
            }
            v = t(v, p);
            if (z) {
                var l = v.length + n.length;
                if (l < w) v = new Array(w - l + 1).join(f) + v;
                if (c) v = z1(v);
                v = n + v
            } else {
                if (c) v = z1(v);
                v = n + v;
                var l = v.length;
                if (l < w) v = new Array(w - l + 1).join(f) + v
            }
            return v + d
        }
    };
    var o1 = /(?:([^{])?([<>=^]))?([+\- ])?(#)?(0)?([0-9]+)?(,)?(\.[0-9]+)?([a-zA-Z%])?/;
    var q1 = d3.map({
        g: function(x, p) {
            return x.toPrecision(p)
        },
        e: function(x, p) {
            return x.toExponential(p)
        },
        f: function(x, p) {
            return x.toFixed(p)
        },
        r: function(x, p) {
            return d3.round(x, p = u1(x, p)).toFixed(Math.max(0, Math.min(20, p)))
        }
    });

    function u1(x, p) {
        return p - (x ? 1 + Math.floor(Math.log(x + Math.pow(10, 1 + Math.floor(Math.log(x) / Math.LN10) - p)) / Math.LN10) : 1)
    }
    function w1(x) {
        return x + ""
    }
    function z1(v) {
        var i = v.lastIndexOf("."),
            f = i >= 0 ? v.substring(i) : (i = v.length, ""),
            t = [];
        while (i > 0) t.push(v.substring(i -= 3, i + 3));
        return t.reverse().join(",") + f
    }
    var A1 = ["y", "z", "a", "f", "p", "n", "μ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y"].map(B1);
    d3.formatPrefix = function(v, p) {
        var i = 0;
        if (v) {
            if (v < 0) v *= -1;
            if (p) v = d3.round(v, u1(v, p));
            i = 1 + Math.floor(1e-12 + Math.log(v) / Math.LN10);
            i = Math.max(-24, Math.min(24, Math.floor((i <= 0 ? i + 1 : i - 1) / 3) * 3))
        }
        return A1[8 + i / 3]
    };

    function B1(d, i) {
        var k = Math.pow(10, Math.abs(8 - i) * 3);
        return {
            scale: i > 8 ? function(d) {
                return d / k
            } : function(d) {
                return d * k
            },
            symbol: d
        }
    }

    /*
     * TERMS OF USE - EASING EQUATIONS
     *
     * Open source under the BSD License.
     *
     * Copyright 2001 Robert Penner
     * All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     *
     * - Redistributions of source code must retain the above copyright notice, this
     *   list of conditions and the following disclaimer.
     *
     * - Redistributions in binary form must reproduce the above copyright notice,
     *   this list of conditions and the following disclaimer in the documentation
     *   and/or other materials provided with the distribution.
     *
     * - Neither the name of the author nor the names of contributors may be used to
     *   endorse or promote products derived from this software without specific
     *   prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
     * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
     * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
     * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
     * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
     * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
     * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
     * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
     * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
     * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
     * POSSIBILITY OF SUCH DAMAGE.
     */

    var C1 = L1(2),
        D1 = L1(3),
        E1 = function() {
            return K1
        };
    var F1 = d3.map({
        linear: E1,
        poly: L1,
        quad: function() {
            return C1
        },
        cubic: function() {
            return D1
        },
        sin: function() {
            return M1
        },
        exp: function() {
            return N1
        },
        circle: function() {
            return O1
        },
        elastic: P1,
        back: Q1,
        bounce: function() {
            return R1
        }
    });
    var G1 = d3.map({
        "in": K1,
        "out": I1,
        "in-out": J1,
        "out-in": function(f) {
            return J1(I1(f))
        }
    });
    d3.ease = function(n) {
        var i = n.indexOf("-"),
            t = i >= 0 ? n.substring(0, i) : n,
            m = i >= 0 ? n.substring(i + 1) : "in";
        t = F1.get(t) || E1;
        m = G1.get(m) || K1;
        return H1(m(t.apply(null, Array.prototype.slice.call(arguments, 1))))
    };

    function H1(f) {
        return function(t) {
            return t <= 0 ? 0 : t >= 1 ? 1 : f(t)
        }
    }
    function I1(f) {
        return function(t) {
            return 1 - f(1 - t)
        }
    }
    function J1(f) {
        return function(t) {
            return .5 * (t < .5 ? f(2 * t) : (2 - f(2 - 2 * t)))
        }
    }
    function K1(t) {
        return t
    }
    function L1(e) {
        return function(t) {
            return Math.pow(t, e)
        }
    }
    function M1(t) {
        return 1 - Math.cos(t * Math.PI / 2)
    }
    function N1(t) {
        return Math.pow(2, 10 * (t - 1))
    }
    function O1(t) {
        return 1 - Math.sqrt(1 - t * t)
    }
    function P1(a, p) {
        var s;
        if (arguments.length < 2) p = 0.45;
        if (arguments.length < 1) {
            a = 1;
            s = p / 4
        } else s = p / (2 * Math.PI) * Math.asin(1 / a);
        return function(t) {
            return 1 + a * Math.pow(2, 10 * -t) * Math.sin((t - s) * 2 * Math.PI / p)
        }
    }
    function Q1(s) {
        if (!s) s = 1.70158;
        return function(t) {
            return t * t * ((s + 1) * t - s)
        }
    }
    function R1(t) {
        return t < 1 / 2.75 ? 7.5625 * t * t : t < 2 / 2.75 ? 7.5625 * (t -= 1.5 / 2.75) * t + .75 : t < 2.5 / 2.75 ? 7.5625 * (t -= 2.25 / 2.75) * t + .9375 : 7.5625 * (t -= 2.625 / 2.75) * t + .984375
    }
    d3.event = null;

    function S1() {
        d3.event.stopPropagation();
        d3.event.preventDefault()
    }
    function T1() {
        var e = d3.event,
            s;
        while (s = e.sourceEvent) e = s;
        return e
    }
    function U1(t) {
        var d = new j1,
            i = 0,
            n = arguments.length;
        while (++i < n) d[arguments[i]] = k1(d);
        d.of = function(a, b) {
            return function(c) {
                try {
                    var f = c.sourceEvent = d3.event;
                    c.target = t;
                    d3.event = c;
                    d[c.type].apply(a, b)
                } finally {
                    d3.event = f
                }
            }
        };
        return d
    }
    d3.interpolate = function(a, b) {
        var i = d3.interpolators.length,
            f;
        while (--i >= 0 && !(f = d3.interpolators[i](a, b)));
        return f
    };
    d3.interpolateNumber = function(a, b) {
        b -= a;
        return function(t) {
            return a + b * t
        }
    };
    d3.interpolateRound = function(a, b) {
        b -= a;
        return function(t) {
            return Math.round(a + b * t)
        }
    };
    d3.interpolateString = function(a, b) {
        var m, i, j, c = 0,
            d = 0,
            s = [],
            q = [],
            n, o;
        V1.lastIndex = 0;
        for (i = 0; m = V1.exec(b); ++i) {
            if (m.index) s.push(b.substring(c, d = m.index));
            q.push({
                i: s.length,
                x: m[0]
            });
            s.push(null);
            c = V1.lastIndex
        }
        if (c < b.length) s.push(b.substring(c));
        for (i = 0, n = q.length;
        (m = V1.exec(a)) && i < n; ++i) {
            o = q[i];
            if (o.x == m[0]) {
                if (o.i) {
                    if (s[o.i + 1] == null) {
                        s[o.i - 1] += o.x;
                        s.splice(o.i, 1);
                        for (j = i + 1; j < n; ++j) q[j].i--
                    } else {
                        s[o.i - 1] += o.x + s[o.i + 1];
                        s.splice(o.i, 2);
                        for (j = i + 1; j < n; ++j) q[j].i -= 2
                    }
                } else {
                    if (s[o.i + 1] == null) {
                        s[o.i] = o.x
                    } else {
                        s[o.i] = o.x + s[o.i + 1];
                        s.splice(o.i + 1, 1);
                        for (j = i + 1; j < n; ++j) q[j].i--
                    }
                }
                q.splice(i, 1);
                n--;
                i--
            } else {
                o.x = d3.interpolateNumber(parseFloat(m[0]), parseFloat(o.x))
            }
        }
        while (i < n) {
            o = q.pop();
            if (s[o.i + 1] == null) {
                s[o.i] = o.x
            } else {
                s[o.i] = o.x + s[o.i + 1];
                s.splice(o.i + 1, 1)
            }
            n--
        }
        if (s.length === 1) {
            return s[0] == null ? q[0].x : function() {
                return b
            }
        }
        return function(t) {
            for (i = 0; i < n; ++i) s[(o = q[i]).i] = o.x(t);
            return s.join("")
        }
    };
    d3.interpolateTransform = function(a, b) {
        var s = [],
            q = [],
            n, A = d3.transform(a),
            B = d3.transform(b),
            c = A.translate,
            d = B.translate,
            r = A.rotate,
            f = B.rotate,
            w = A.skew,
            g = B.skew,
            k = A.scale,
            h = B.scale;
        if (c[0] != d[0] || c[1] != d[1]) {
            s.push("translate(", null, ",", null, ")");
            q.push({
                i: 1,
                x: d3.interpolateNumber(c[0], d[0])
            }, {
                i: 3,
                x: d3.interpolateNumber(c[1], d[1])
            })
        } else if (d[0] || d[1]) {
            s.push("translate(" + d + ")")
        } else {
            s.push("")
        }
        if (r != f) {
            if (r - f > 180) f += 360;
            else if (f - r > 180) r += 360;
            q.push({
                i: s.push(s.pop() + "rotate(", null, ")") - 2,
                x: d3.interpolateNumber(r, f)
            })
        } else if (f) {
            s.push(s.pop() + "rotate(" + f + ")")
        }
        if (w != g) {
            q.push({
                i: s.push(s.pop() + "skewX(", null, ")") - 2,
                x: d3.interpolateNumber(w, g)
            })
        } else if (g) {
            s.push(s.pop() + "skewX(" + g + ")")
        }
        if (k[0] != h[0] || k[1] != h[1]) {
            n = s.push(s.pop() + "scale(", null, ",", null, ")");
            q.push({
                i: n - 4,
                x: d3.interpolateNumber(k[0], h[0])
            }, {
                i: n - 2,
                x: d3.interpolateNumber(k[1], h[1])
            })
        } else if (h[0] != 1 || h[1] != 1) {
            s.push(s.pop() + "scale(" + h + ")")
        }
        n = q.length;
        return function(t) {
            var i = -1,
                o;
            while (++i < n) s[(o = q[i]).i] = o.x(t);
            return s.join("")
        }
    };
    d3.interpolateRgb = function(a, b) {
        a = d3.rgb(a);
        b = d3.rgb(b);
        var c = a.r,
            d = a.g,
            f = a.b,
            g = b.r - c,
            h = b.g - d,
            i = b.b - f;
        return function(t) {
            return "#" + _1(Math.round(c + g * t)) + _1(Math.round(d + h * t)) + _1(Math.round(f + i * t))
        }
    };
    d3.interpolateHsl = function(a, b) {
        a = d3.hsl(a);
        b = d3.hsl(b);
        var h = a.h,
            s = a.s,
            l = a.l,
            c = b.h - h,
            d = b.s - s,
            f = b.l - l;
        if (c > 180) c -= 360;
        else if (c < -180) c += 360;
        return function(t) {
            return h2(h + c * t, s + d * t, l + f * t).toString()
        }
    };
    d3.interpolateArray = function(a, b) {
        var x = [],
            c = [],
            n = a.length,
            d = b.length,
            f = Math.min(a.length, b.length),
            i;
        for (i = 0; i < f; ++i) x.push(d3.interpolate(a[i], b[i]));
        for (; i < n; ++i) c[i] = a[i];
        for (; i < d; ++i) c[i] = b[i];
        return function(t) {
            for (i = 0; i < f; ++i) c[i] = x[i](t);
            return c
        }
    };
    d3.interpolateObject = function(a, b) {
        var i = {}, c = {}, k;
        for (k in a) {
            if (k in b) {
                i[k] = W1(k)(a[k], b[k])
            } else {
                c[k] = a[k]
            }
        }
        for (k in b) {
            if (!(k in a)) {
                c[k] = b[k]
            }
        }
        return function(t) {
            for (k in i) c[k] = i[k](t);
            return c
        }
    };
    var V1 = /[-+]?(?:\d+\.?\d*|\.?\d+)(?:[eE][-+]?\d+)?/g;

    function W1(n) {
        return n == "transform" ? d3.interpolateTransform : d3.interpolate
    }
    d3.interpolators = [d3.interpolateObject, function(a, b) {
        return (b instanceof Array) && d3.interpolateArray(a, b)
    }, function(a, b) {
        return (typeof a === "string" || typeof b === "string") && d3.interpolateString(a + "", b + "")
    }, function(a, b) {
        return (typeof b === "string" ? d2.has(b) || /^(#|rgb\(|hsl\()/.test(b) : b instanceof $1 || b instanceof g2) && d3.interpolateRgb(a, b)
    }, function(a, b) {
        return !isNaN(a = +a) && !isNaN(b = +b) && d3.interpolateNumber(a, b)
    }];

    function X1(a, b) {
        b = b - (a = +a) ? 1 / (b - a) : 0;
        return function(x) {
            return (x - a) * b
        }
    }
    function Y1(a, b) {
        b = b - (a = +a) ? 1 / (b - a) : 0;
        return function(x) {
            return Math.max(0, Math.min(1, (x - a) * b))
        }
    }
    d3.rgb = function(r, g, b) {
        return arguments.length === 1 ? (r instanceof $1 ? Z1(r.r, r.g, r.b) : a2("" + r, Z1, h2)) : Z1(~~r, ~~g, ~~b)
    };

    function Z1(r, g, b) {
        return new $1(r, g, b)
    }
    function $1(r, g, b) {
        this.r = r;
        this.g = g;
        this.b = b
    }
    $1.prototype.brighter = function(k) {
        k = Math.pow(0.7, arguments.length ? k : 1);
        var r = this.r,
            g = this.g,
            b = this.b,
            i = 30;
        if (!r && !g && !b) return Z1(i, i, i);
        if (r && r < i) r = i;
        if (g && g < i) g = i;
        if (b && b < i) b = i;
        return Z1(Math.min(255, Math.floor(r / k)), Math.min(255, Math.floor(g / k)), Math.min(255, Math.floor(b / k)))
    };
    $1.prototype.darker = function(k) {
        k = Math.pow(0.7, arguments.length ? k : 1);
        return Z1(Math.floor(k * this.r), Math.floor(k * this.g), Math.floor(k * this.b))
    };
    $1.prototype.hsl = function() {
        return b2(this.r, this.g, this.b)
    };
    $1.prototype.toString = function() {
        return "#" + _1(this.r) + _1(this.g) + _1(this.b)
    };

    function _1(v) {
        return v < 0x10 ? "0" + Math.max(0, v).toString(16) : Math.min(255, v).toString(16)
    }
    function a2(f, a, h) {
        var r = 0,
            g = 0,
            b = 0,
            m, c, n;
        m = /([a-z]+)\((.*)\)/i.exec(f);
        if (m) {
            c = m[2].split(",");
            switch (m[1]) {
                case "hsl":
                    {
                        return h(parseFloat(c[0]), parseFloat(c[1]) / 100, parseFloat(c[2]) / 100)
                    }
                case "rgb":
                    {
                        return a(c2(c[0]), c2(c[1]), c2(c[2]))
                    }
            }
        }
        if (n = d2.get(f)) return a(n.r, n.g, n.b);
        if (f != null && f.charAt(0) === "#") {
            if (f.length === 4) {
                r = f.charAt(1);
                r += r;
                g = f.charAt(2);
                g += g;
                b = f.charAt(3);
                b += b
            } else if (f.length === 7) {
                r = f.substring(1, 3);
                g = f.substring(3, 5);
                b = f.substring(5, 7)
            }
            r = parseInt(r, 16);
            g = parseInt(g, 16);
            b = parseInt(b, 16)
        }
        return a(r, g, b)
    }
    function b2(r, g, b) {
        var m = Math.min(r /= 255, g /= 255, b /= 255),
            a = Math.max(r, g, b),
            d = a - m,
            h, s, l = (a + m) / 2;
        if (d) {
            s = l < .5 ? d / (a + m) : d / (2 - a - m);
            if (r == a) h = (g - b) / d + (g < b ? 6 : 0);
            else if (g == a) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;
            h *= 60
        } else {
            s = h = 0
        }
        return f2(h, s, l)
    }
    function c2(c) {
        var f = parseFloat(c);
        return c.charAt(c.length - 1) === "%" ? Math.round(f * 2.55) : f
    }
    var d2 = d3.map({
        aliceblue: "#f0f8ff",
        antiquewhite: "#faebd7",
        aqua: "#00ffff",
        aquamarine: "#7fffd4",
        azure: "#f0ffff",
        beige: "#f5f5dc",
        bisque: "#ffe4c4",
        black: "#000000",
        blanchedalmond: "#ffebcd",
        blue: "#0000ff",
        blueviolet: "#8a2be2",
        brown: "#a52a2a",
        burlywood: "#deb887",
        cadetblue: "#5f9ea0",
        chartreuse: "#7fff00",
        chocolate: "#d2691e",
        coral: "#ff7f50",
        cornflowerblue: "#6495ed",
        cornsilk: "#fff8dc",
        crimson: "#dc143c",
        cyan: "#00ffff",
        darkblue: "#00008b",
        darkcyan: "#008b8b",
        darkgoldenrod: "#b8860b",
        darkgray: "#a9a9a9",
        darkgreen: "#006400",
        darkgrey: "#a9a9a9",
        darkkhaki: "#bdb76b",
        darkmagenta: "#8b008b",
        darkolivegreen: "#556b2f",
        darkorange: "#ff8c00",
        darkorchid: "#9932cc",
        darkred: "#8b0000",
        darksalmon: "#e9967a",
        darkseagreen: "#8fbc8f",
        darkslateblue: "#483d8b",
        darkslategray: "#2f4f4f",
        darkslategrey: "#2f4f4f",
        darkturquoise: "#00ced1",
        darkviolet: "#9400d3",
        deeppink: "#ff1493",
        deepskyblue: "#00bfff",
        dimgray: "#696969",
        dimgrey: "#696969",
        dodgerblue: "#1e90ff",
        firebrick: "#b22222",
        floralwhite: "#fffaf0",
        forestgreen: "#228b22",
        fuchsia: "#ff00ff",
        gainsboro: "#dcdcdc",
        ghostwhite: "#f8f8ff",
        gold: "#ffd700",
        goldenrod: "#daa520",
        gray: "#808080",
        green: "#008000",
        greenyellow: "#adff2f",
        grey: "#808080",
        honeydew: "#f0fff0",
        hotpink: "#ff69b4",
        indianred: "#cd5c5c",
        indigo: "#4b0082",
        ivory: "#fffff0",
        khaki: "#f0e68c",
        lavender: "#e6e6fa",
        lavenderblush: "#fff0f5",
        lawngreen: "#7cfc00",
        lemonchiffon: "#fffacd",
        lightblue: "#add8e6",
        lightcoral: "#f08080",
        lightcyan: "#e0ffff",
        lightgoldenrodyellow: "#fafad2",
        lightgray: "#d3d3d3",
        lightgreen: "#90ee90",
        lightgrey: "#d3d3d3",
        lightpink: "#ffb6c1",
        lightsalmon: "#ffa07a",
        lightseagreen: "#20b2aa",
        lightskyblue: "#87cefa",
        lightslategray: "#778899",
        lightslategrey: "#778899",
        lightsteelblue: "#b0c4de",
        lightyellow: "#ffffe0",
        lime: "#00ff00",
        limegreen: "#32cd32",
        linen: "#faf0e6",
        magenta: "#ff00ff",
        maroon: "#800000",
        mediumaquamarine: "#66cdaa",
        mediumblue: "#0000cd",
        mediumorchid: "#ba55d3",
        mediumpurple: "#9370db",
        mediumseagreen: "#3cb371",
        mediumslateblue: "#7b68ee",
        mediumspringgreen: "#00fa9a",
        mediumturquoise: "#48d1cc",
        mediumvioletred: "#c71585",
        midnightblue: "#191970",
        mintcream: "#f5fffa",
        mistyrose: "#ffe4e1",
        moccasin: "#ffe4b5",
        navajowhite: "#ffdead",
        navy: "#000080",
        oldlace: "#fdf5e6",
        olive: "#808000",
        olivedrab: "#6b8e23",
        orange: "#ffa500",
        orangered: "#ff4500",
        orchid: "#da70d6",
        palegoldenrod: "#eee8aa",
        palegreen: "#98fb98",
        paleturquoise: "#afeeee",
        palevioletred: "#db7093",
        papayawhip: "#ffefd5",
        peachpuff: "#ffdab9",
        peru: "#cd853f",
        pink: "#ffc0cb",
        plum: "#dda0dd",
        powderblue: "#b0e0e6",
        purple: "#800080",
        red: "#ff0000",
        rosybrown: "#bc8f8f",
        royalblue: "#4169e1",
        saddlebrown: "#8b4513",
        salmon: "#fa8072",
        sandybrown: "#f4a460",
        seagreen: "#2e8b57",
        seashell: "#fff5ee",
        sienna: "#a0522d",
        silver: "#c0c0c0",
        skyblue: "#87ceeb",
        slateblue: "#6a5acd",
        slategray: "#708090",
        slategrey: "#708090",
        snow: "#fffafa",
        springgreen: "#00ff7f",
        steelblue: "#4682b4",
        tan: "#d2b48c",
        teal: "#008080",
        thistle: "#d8bfd8",
        tomato: "#ff6347",
        turquoise: "#40e0d0",
        violet: "#ee82ee",
        wheat: "#f5deb3",
        white: "#ffffff",
        whitesmoke: "#f5f5f5",
        yellow: "#ffff00",
        yellowgreen: "#9acd32"
    });
    d2.forEach(function(k, v) {
        d2.set(k, a2(v, Z1, h2))
    });
    d3.hsl = function(h, s, l) {
        return arguments.length === 1 ? (h instanceof g2 ? f2(h.h, h.s, h.l) : a2("" + h, b2, f2)) : f2(+h, +s, +l)
    };

    function f2(h, s, l) {
        return new g2(h, s, l)
    }
    function g2(h, s, l) {
        this.h = h;
        this.s = s;
        this.l = l
    }
    g2.prototype.brighter = function(k) {
        k = Math.pow(0.7, arguments.length ? k : 1);
        return f2(this.h, this.s, this.l / k)
    };
    g2.prototype.darker = function(k) {
        k = Math.pow(0.7, arguments.length ? k : 1);
        return f2(this.h, this.s, k * this.l)
    };
    g2.prototype.rgb = function() {
        return h2(this.h, this.s, this.l)
    };
    g2.prototype.toString = function() {
        return this.rgb().toString()
    };

    function h2(h, s, l) {
        var m, a;
        h = h % 360;
        if (h < 0) h += 360;
        s = s < 0 ? 0 : s > 1 ? 1 : s;
        l = l < 0 ? 0 : l > 1 ? 1 : l;
        a = l <= .5 ? l * (1 + s) : l + s - l * s;
        m = 2 * l - a;

        function v(h) {
            if (h > 360) h -= 360;
            else if (h < 0) h += 360;
            if (h < 60) return m + (a - m) * h / 60;
            if (h < 180) return a;
            if (h < 240) return m + (a - m) * (240 - h) / 60;
            return m
        }
        function b(h) {
            return Math.round(v(h) * 255)
        }
        return Z1(b(h + 120), b(h), b(h - 120))
    }
    function j2(g) {
        N(g, r2);
        return g
    }
    var k2 = function(s, n) {
        return n.querySelector(s)
    }, l2 = function(s, n) {
        return n.querySelectorAll(s)
    }, n2 = document.documentElement,
        o2 = n2.matchesSelector || n2.webkitMatchesSelector || n2.mozMatchesSelector || n2.msMatchesSelector || n2.oMatchesSelector,
        q2 = function(n, s) {
            return o2.call(n, s)
        };
    if (typeof Sizzle === "function") {
        k2 = function(s, n) {
            return Sizzle(s, n)[0] || null
        };
        l2 = function(s, n) {
            return Sizzle.uniqueSort(Sizzle(s, n))
        };
        q2 = Sizzle.matchesSelector
    }
    var r2 = [];
    d3.selection = function() {
        return E2
    };
    d3.selection.prototype = r2;
    r2.select = function(s) {
        var a = [],
            b, c, g, d;
        if (typeof s !== "function") s = u2(s);
        for (var j = -1, m = this.length; ++j < m;) {
            a.push(b = []);
            b.parentNode = (g = this[j]).parentNode;
            for (var i = -1, n = g.length; ++i < n;) {
                if (d = g[i]) {
                    b.push(c = s.call(d, d.__data__, i));
                    if (c && "__data__" in d) c.__data__ = d.__data__
                } else {
                    b.push(null)
                }
            }
        }
        return j2(a)
    };

    function u2(s) {
        return function() {
            return k2(s, this)
        }
    }
    r2.selectAll = function(s) {
        var a = [],
            b, c;
        if (typeof s !== "function") s = w2(s);
        for (var j = -1, m = this.length; ++j < m;) {
            for (var g = this[j], i = -1, n = g.length; ++i < n;) {
                if (c = g[i]) {
                    a.push(b = J(s.call(c, c.__data__, i)));
                    b.parentNode = c
                }
            }
        }
        return j2(a)
    };

    function w2(s) {
        return function() {
            return l2(s, this)
        }
    }
    r2.attr = function(n, v) {
        n = d3.ns.qualify(n);
        if (arguments.length < 2) {
            var a = this.node();
            return n.local ? a.getAttributeNS(n.space, n.local) : a.getAttribute(n)
        }
        function b() {
            this.removeAttribute(n)
        }
        function c() {
            this.removeAttributeNS(n.space, n.local)
        }
        function d() {
            this.setAttribute(n, v)
        }
        function f() {
            this.setAttributeNS(n.space, n.local, v)
        }
        function g() {
            var x = v.apply(this, arguments);
            if (x == null) this.removeAttribute(n);
            else this.setAttribute(n, x)
        }
        function h() {
            var x = v.apply(this, arguments);
            if (x == null) this.removeAttributeNS(n.space, n.local);
            else this.setAttributeNS(n.space, n.local, x)
        }
        return this.each(v == null ? (n.local ? c : b) : (typeof v === "function" ? (n.local ? h : g) : (n.local ? f : d)))
    };
    r2.classed = function(a, v) {
        var b = $(a).split(" "),
            n = b.length,
            i = -1;
        if (arguments.length > 1) {
            while (++i < n) z2.call(this, b[i], v);
            return this
        } else {
            while (++i < n) if (!z2.call(this, b[i])) return false;
            return true
        }
    };

    function z2(n, v) {
        var r = new RegExp("(^|\\s+)" + d3.requote(n) + "(\\s+|$)", "g");
        if (arguments.length < 2) {
            var a = this.node();
            if (c = a.classList) return c.contains(n);
            var c = a.className;
            r.lastIndex = 0;
            return r.test(c.baseVal != null ? c.baseVal : c)
        }
        function b() {
            if (c = this.classList) return c.add(n);
            var c = this.className,
                g = c.baseVal != null,
                h = g ? c.baseVal : c;
            r.lastIndex = 0;
            if (!r.test(h)) {
                h = $(h + " " + n);
                if (g) c.baseVal = h;
                else this.className = h
            }
        }
        function d() {
            if (c = this.classList) return c.remove(n);
            var c = this.className,
                g = c.baseVal != null,
                h = g ? c.baseVal : c;
            h = $(h.replace(r, " "));
            if (g) c.baseVal = h;
            else this.className = h
        }
        function f() {
            (v.apply(this, arguments) ? b : d).call(this)
        }
        return this.each(typeof v === "function" ? f : v ? b : d)
    }
    r2.style = function(n, v, p) {
        if (arguments.length < 3) p = "";
        if (arguments.length < 2) return window.getComputedStyle(this.node(), null).getPropertyValue(n);

        function s() {
            this.style.removeProperty(n)
        }
        function a() {
            this.style.setProperty(n, v, p)
        }
        function b() {
            var x = v.apply(this, arguments);
            if (x == null) this.style.removeProperty(n);
            else this.style.setProperty(n, x, p)
        }
        return this.each(v == null ? s : (typeof v === "function" ? b : a))
    };
    r2.property = function(n, v) {
        if (arguments.length < 2) return this.node()[n];

        function p() {
            delete this[n]
        }
        function a() {
            this[n] = v
        }
        function b() {
            var x = v.apply(this, arguments);
            if (x == null) delete this[n];
            else this[n] = x
        }
        return this.each(v == null ? p : (typeof v === "function" ? b : a))
    };
    r2.text = function(a) {
        return arguments.length < 1 ? this.node().textContent : this.each(typeof a === "function" ? function() {
            var v = a.apply(this, arguments);
            this.textContent = v == null ? "" : v
        } : a == null ? function() {
            this.textContent = ""
        } : function() {
            this.textContent = a
        })
    };
    r2.html = function(a) {
        return arguments.length < 1 ? this.node().innerHTML : this.each(typeof a === "function" ? function() {
            var v = a.apply(this, arguments);
            this.innerHTML = v == null ? "" : v
        } : a == null ? function() {
            this.innerHTML = ""
        } : function() {
            this.innerHTML = a
        })
    };
    r2.append = function(n) {
        n = d3.ns.qualify(n);

        function a() {
            return this.appendChild(document.createElementNS(this.namespaceURI, n))
        }
        function b() {
            return this.appendChild(document.createElementNS(n.space, n.local))
        }
        return this.select(n.local ? b : a)
    };
    r2.insert = function(n, b) {
        n = d3.ns.qualify(n);

        function i() {
            return this.insertBefore(document.createElementNS(this.namespaceURI, n), k2(b, this))
        }
        function a() {
            return this.insertBefore(document.createElementNS(n.space, n.local), k2(b, this))
        }
        return this.select(n.local ? a : i)
    };
    r2.remove = function() {
        return this.each(function() {
            var p = this.parentNode;
            if (p) p.removeChild(this)
        })
    };
    r2.data = function(v, k) {
        var i = -1,
            n = this.length,
            g, a;
        if (!arguments.length) {
            v = new Array(n = (g = this[0]).length);
            while (++i < n) {
                if (a = g[i]) {
                    v[i] = a.__data__
                }
            }
            return v
        }
        function b(g, f) {
            var i, n = g.length,
                m = f.length,
                h = Math.min(n, m),
                l = Math.max(n, m),
                o = [],
                p = [],
                q = [],
                a, r;
            if (k) {
                var s = new O,
                    t = [],
                    w, j = f.length;
                for (i = -1; ++i < n;) {
                    w = k.call(a = g[i], a.__data__, i);
                    if (s.has(w)) {
                        q[j++] = a
                    } else {
                        s.set(w, a)
                    }
                    t.push(w)
                }
                for (i = -1; ++i < m;) {
                    w = k.call(f, r = f[i], i);
                    if (s.has(w)) {
                        o[i] = a = s.get(w);
                        a.__data__ = r;
                        p[i] = q[i] = null
                    } else {
                        p[i] = A2(r);
                        o[i] = q[i] = null
                    }
                    s.remove(w)
                }
                for (i = -1; ++i < n;) {
                    if (s.has(t[i])) {
                        q[i] = g[i]
                    }
                }
            } else {
                for (i = -1; ++i < h;) {
                    a = g[i];
                    r = f[i];
                    if (a) {
                        a.__data__ = r;
                        o[i] = a;
                        p[i] = q[i] = null
                    } else {
                        p[i] = A2(r);
                        o[i] = q[i] = null
                    }
                }
                for (; i < m; ++i) {
                    p[i] = A2(f[i]);
                    o[i] = q[i] = null
                }
                for (; i < l; ++i) {
                    q[i] = g[i];
                    p[i] = o[i] = null
                }
            }
            p.update = o;
            p.parentNode = o.parentNode = q.parentNode = g.parentNode;
            c.push(p);
            u.push(o);
            d.push(q)
        }
        var c = F2([]),
            u = j2([]),
            d = j2([]);
        if (typeof v === "function") {
            while (++i < n) {
                b(g = this[i], v.call(g, g.parentNode.__data__, i))
            }
        } else {
            while (++i < n) {
                b(g = this[i], v)
            }
        }
        u.enter = function() {
            return c
        };
        u.exit = function() {
            return d
        };
        return u
    };

    function A2(d) {
        return {
            __data__: d
        }
    }
    r2.datum = r2.map = function(v) {
        return arguments.length < 1 ? this.property("__data__") : this.property("__data__", v)
    };
    r2.filter = function(f) {
        var s = [],
            a, g, b;
        if (typeof f !== "function") f = B2(f);
        for (var j = 0, m = this.length; j < m; j++) {
            s.push(a = []);
            a.parentNode = (g = this[j]).parentNode;
            for (var i = 0, n = g.length; i < n; i++) {
                if ((b = g[i]) && f.call(b, b.__data__, i)) {
                    a.push(b)
                }
            }
        }
        return j2(s)
    };

    function B2(s) {
        return function() {
            return q2(this, s)
        }
    }
    r2.order = function() {
        for (var j = -1, m = this.length; ++j < m;) {
            for (var g = this[j], i = g.length - 1, n = g[i], a; --i >= 0;) {
                if (a = g[i]) {
                    if (n && n !== a.nextSibling) n.parentNode.insertBefore(a, n);
                    n = a
                }
            }
        }
        return this
    };
    r2.sort = function(c) {
        c = C2.apply(this, arguments);
        for (var j = -1, m = this.length; ++j < m;) this[j].sort(c);
        return this.order()
    };

    function C2(c) {
        if (!arguments.length) c = d3.ascending;
        return function(a, b) {
            return c(a && a.__data__, b && b.__data__)
        }
    }
    r2.on = function(t, a, c) {
        if (arguments.length < 3) c = false;
        var n = "__on" + t,
            i = t.indexOf(".");
        if (i > 0) t = t.substring(0, i);
        if (arguments.length < 2) return (i = this.node()[n]) && i._;
        return this.each(function(d, i) {
            var b = this,
                o = b[n];
            if (o) {
                b.removeEventListener(t, o, o.$);
                delete b[n]
            }
            if (a) {
                b.addEventListener(t, b[n] = l, l.$ = c);
                l._ = a
            }
            function l(e) {
                var o = d3.event;
                d3.event = e;
                try {
                    a.call(b, b.__data__, i)
                } finally {
                    d3.event = o
                }
            }
        })
    };
    r2.each = function(c) {
        return D2(this, function(n, i, j) {
            c.call(n, n.__data__, i, j)
        })
    };

    function D2(g, c) {
        for (var j = 0, m = g.length; j < m; j++) {
            for (var a = g[j], i = 0, n = a.length, b; i < n; i++) {
                if (b = a[i]) c(b, i, j)
            }
        }
        return g
    }
    r2.call = function(c) {
        c.apply(this, (arguments[0] = this, arguments));
        return this
    };
    r2.empty = function() {
        return !this.node()
    };
    r2.node = function(c) {
        for (var j = 0, m = this.length; j < m; j++) {
            for (var g = this[j], i = 0, n = g.length; i < n; i++) {
                var a = g[i];
                if (a) return a
            }
        }
        return null
    };
    r2.transition = function() {
        var s = [],
            a, b;
        for (var j = -1, m = this.length; ++j < m;) {
            s.push(a = []);
            for (var g = this[j], i = -1, n = g.length; ++i < n;) {
                a.push((b = g[i]) ? {
                    node: b,
                    delay: R2,
                    duration: S2
                } : null)
            }
        }
        return H2(s, N2 || ++M2, Date.now())
    };
    var E2 = j2([
        [document]
    ]);
    E2[0].parentNode = n2;
    d3.select = function(s) {
        return typeof s === "string" ? E2.select(s) : j2([
            [s]
        ])
    };
    d3.selectAll = function(s) {
        return typeof s === "string" ? E2.selectAll(s) : j2([J(s)])
    };

    function F2(s) {
        N(s, G2);
        return s
    }
    var G2 = [];
    d3.selection.enter = F2;
    d3.selection.enter.prototype = G2;
    G2.append = r2.append;
    G2.insert = r2.insert;
    G2.empty = r2.empty;
    G2.node = r2.node;
    G2.select = function(s) {
        var a = [],
            b, c, u, g, d;
        for (var j = -1, m = this.length; ++j < m;) {
            u = (g = this[j]).update;
            a.push(b = []);
            b.parentNode = g.parentNode;
            for (var i = -1, n = g.length; ++i < n;) {
                if (d = g[i]) {
                    b.push(u[i] = c = s.call(g.parentNode, d.__data__, i));
                    c.__data__ = d.__data__
                } else {
                    b.push(null)
                }
            }
        }
        return j2(a)
    };

    function H2(g, a, b) {
        N(g, L2);
        var c = new O,
            f = d3.dispatch("start", "end"),
            h = T2;
        g.id = a;
        g.time = b;
        g.tween = function(n, t) {
            if (arguments.length < 2) return c.get(n);
            if (t == null) c.remove(n);
            else c.set(n, t);
            return g
        };
        g.ease = function(v) {
            if (!arguments.length) return h;
            h = typeof v === "function" ? v : d3.ease.apply(d3, arguments);
            return g
        };
        g.each = function(t, l) {
            if (arguments.length < 2) return U2.call(g, t);
            f.on(t, l);
            return g
        };
        d3.timer(function(k) {
            return D2(g, function(l, i, j) {
                var m = [],
                    o = l.delay,
                    p = l.duration,
                    q = (l = l.node).__transition__ || (l.__transition__ = {
                        active: 0,
                        count: 0
                    }),
                    d = l.__data__;
                ++q.count;
                o <= k ? s(k) : d3.timer(s, o, b);

                function s(k) {
                    if (q.active > a) return u();
                    q.active = a;
                    c.forEach(function(n, v) {
                        if (v = v.call(l, d, i)) {
                            m.push(v)
                        }
                    });
                    f.start.call(l, d, i);
                    if (!r(k)) d3.timer(r, 0, b);
                    return 1
                }
                function r(k) {
                    if (q.active !== a) return u();
                    var t = (k - o) / p,
                        e = h(t),
                        n = m.length;
                    while (n > 0) {
                        m[--n].call(l, e)
                    }
                    if (t >= 1) {
                        u();
                        N2 = a;
                        f.end.call(l, d, i);
                        N2 = 0;
                        return 1
                    }
                }
                function u() {
                    if (!--q.count) delete l.__transition__;
                    return 1
                }
            })
        }, 0, b);
        return g
    }
    var I2 = {};

    function J2(d, i, a) {
        return a != "" && I2
    }
    function K2(n, b) {
        var c = W1(n);

        function t(d, i, a) {
            var v = b.call(this, d, i);
            return v == null ? a != "" && I2 : a != v && c(a, v)
        }
        function f(d, i, a) {
            return a != b && c(a, b)
        }
        return typeof b === "function" ? t : b == null ? J2 : (b += "", f)
    }
    var L2 = [],
        M2 = 0,
        N2 = 0,
        O2 = 0,
        P2 = 250,
        Q2 = d3.ease("cubic-in-out"),
        R2 = O2,
        S2 = P2,
        T2 = Q2;
    L2.call = r2.call;
    d3.transition = function(s) {
        return arguments.length ? (N2 ? s.transition() : s) : E2.transition()
    };
    d3.transition.prototype = L2;
    L2.select = function(s) {
        var a = [],
            b, c, d;
        if (typeof s !== "function") s = u2(s);
        for (var j = -1, m = this.length; ++j < m;) {
            a.push(b = []);
            for (var g = this[j], i = -1, n = g.length; ++i < n;) {
                if ((d = g[i]) && (c = s.call(d.node, d.node.__data__, i))) {
                    if ("__data__" in d.node) c.__data__ = d.node.__data__;
                    b.push({
                        node: c,
                        delay: d.delay,
                        duration: d.duration
                    })
                } else {
                    b.push(null)
                }
            }
        }
        return H2(a, this.id, this.time).ease(this.ease())
    };
    L2.selectAll = function(s) {
        var a = [],
            b, c, d;
        if (typeof s !== "function") s = w2(s);
        for (var j = -1, m = this.length; ++j < m;) {
            for (var g = this[j], i = -1, n = g.length; ++i < n;) {
                if (d = g[i]) {
                    c = s.call(d.node, d.node.__data__, i);
                    a.push(b = []);
                    for (var k = -1, o = c.length; ++k < o;) {
                        b.push({
                            node: c[k],
                            delay: d.delay,
                            duration: d.duration
                        })
                    }
                }
            }
        }
        return H2(a, this.id, this.time).ease(this.ease())
    };
    L2.attr = function(n, v) {
        return this.attrTween(n, K2(n, v))
    };
    L2.attrTween = function(n, a) {
        var b = d3.ns.qualify(n);

        function c(d, i) {
            var f = a.call(this, d, i, this.getAttribute(b));
            return f === I2 ? (this.removeAttribute(b), null) : f && function(t) {
                this.setAttribute(b, f(t))
            }
        }
        function g(d, i) {
            var f = a.call(this, d, i, this.getAttributeNS(b.space, b.local));
            return f === I2 ? (this.removeAttributeNS(b.space, b.local), null) : f && function(t) {
                this.setAttributeNS(b.space, b.local, f(t))
            }
        }
        return this.tween("attr." + n, b.local ? g : c)
    };
    L2.style = function(n, v, p) {
        if (arguments.length < 3) p = "";
        return this.styleTween(n, K2(n, v), p)
    };
    L2.styleTween = function(n, a, p) {
        if (arguments.length < 3) p = "";
        return this.tween("style." + n, function(d, i) {
            var f = a.call(this, d, i, window.getComputedStyle(this, null).getPropertyValue(n));
            return f === I2 ? (this.style.removeProperty(n), null) : f && function(t) {
                this.style.setProperty(n, f(t), p)
            }
        })
    };
    L2.text = function(v) {
        return this.tween("text", function(d, i) {
            this.textContent = typeof v === "function" ? v.call(this, d, i) : v
        })
    };
    L2.remove = function() {
        return this.each("end.transition", function() {
            var p;
            if (!this.__transition__ && (p = this.parentNode)) p.removeChild(this)
        })
    };
    L2.delay = function(v) {
        return D2(this, typeof v === "function" ? function(n, i, j) {
            n.delay = v.call(n = n.node, n.__data__, i, j) | 0
        } : (v = v | 0, function(n) {
            n.delay = v
        }))
    };
    L2.duration = function(v) {
        return D2(this, typeof v === "function" ? function(n, i, j) {
            n.duration = Math.max(1, v.call(n = n.node, n.__data__, i, j) | 0)
        } : (v = Math.max(1, v | 0), function(n) {
            n.duration = v
        }))
    };

    function U2(c) {
        var a = N2,
            b = T2,
            d = R2,
            f = S2;
        N2 = this.id;
        T2 = this.ease();
        D2(this, function(n, i, j) {
            R2 = n.delay;
            S2 = n.duration;
            c.call(n = n.node, n.__data__, i, j)
        });
        N2 = a;
        T2 = b;
        R2 = d;
        S2 = f;
        return this
    }
    L2.transition = function() {
        return this.select(S)
    };
    var V2 = null,
        W2, X2;
    d3.timer = function(c, d, t) {
        var f = false,
            a, b = V2;
        if (arguments.length < 3) {
            if (arguments.length < 2) d = 0;
            else if (!isFinite(d)) return;
            t = Date.now()
        }
        while (b) {
            if (b.callback === c) {
                b.then = t;
                b.delay = d;
                f = true;
                break
            }
            a = b;
            b = b.next
        }
        if (!f) V2 = {
            callback: c,
            then: t,
            delay: d,
            next: V2
        };
        if (!W2) {
            X2 = clearTimeout(X2);
            W2 = 1;
            $2(Y2)
        }
    };

    function Y2() {
        var a, n = Date.now(),
            t = V2;
        while (t) {
            a = n - t.then;
            if (a >= t.delay) t.flush = t.callback(a);
            t = t.next
        }
        var d = Z2() - n;
        if (d > 24) {
            if (isFinite(d)) {
                clearTimeout(X2);
                X2 = setTimeout(Y2, d)
            }
            W2 = 0
        } else {
            W2 = 1;
            $2(Y2)
        }
    }
    d3.timer.flush = function() {
        var a, n = Date.now(),
            t = V2;
        while (t) {
            a = n - t.then;
            if (!t.delay) t.flush = t.callback(a);
            t = t.next
        }
        Z2()
    };

    function Z2() {
        var t = null,
            a = V2,
            b = Infinity;
        while (a) {
            if (a.flush) {
                a = t ? t.next = a.next : V2 = a.next
            } else {
                b = Math.min(b, a.then + a.delay);
                a = (t = a).next
            }
        }
        return b
    }
    var $2 = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function(c) {
            setTimeout(c, 17)
        };
    d3.transform = function(s) {
        var g = document.createElementNS(d3.ns.prefix.svg, "g"),
            i = {
                a: 1,
                b: 0,
                c: 0,
                d: 1,
                e: 0,
                f: 0
            };
        return (d3.transform = function(s) {
            g.setAttribute("transform", s);
            var t = g.transform.baseVal.consolidate();
            return new _2(t ? t.matrix : i)
        })(s)
    };

    function _2(m) {
        var r = [m.a, m.b],
            a = [m.c, m.d],
            k = b3(r),
            b = a3(r, a),
            c = b3(c3(a, r, -b)) || 0;
        if (r[0] * a[1] < a[0] * r[1]) {
            r[0] *= -1;
            r[1] *= -1;
            k *= -1;
            b *= -1
        }
        this.rotate = (k ? Math.atan2(r[1], r[0]) : Math.atan2(-a[0], a[1])) * e3;
        this.translate = [m.e, m.f];
        this.scale = [k, c];
        this.skew = c ? Math.atan2(b, c) * e3 : 0
    };
    _2.prototype.toString = function() {
        return "translate(" + this.translate + ")rotate(" + this.rotate + ")skewX(" + this.skew + ")scale(" + this.scale + ")"
    };

    function a3(a, b) {
        return a[0] * b[0] + a[1] * b[1]
    }
    function b3(a) {
        var k = Math.sqrt(a3(a, a));
        if (k) {
            a[0] /= k;
            a[1] /= k
        }
        return k
    }
    function c3(a, b, k) {
        a[0] += k * b[0];
        a[1] += k * b[1];
        return a
    }
    var e3 = 180 / Math.PI;
    d3.mouse = function(c) {
        return g3(c, T1())
    };
    var f3 = /WebKit/.test(navigator.userAgent) ? -1 : 0;

    function g3(c, e) {
        var s = c.ownerSVGElement || c;
        if (s.createSVGPoint) {
            var p = s.createSVGPoint();
            if ((f3 < 0) && (window.scrollX || window.scrollY)) {
                s = d3.select(document.body).append("svg").style("position", "absolute").style("top", 0).style("left", 0);
                var a = s[0][0].getScreenCTM();
                f3 = !(a.f || a.e);
                s.remove()
            }
            if (f3) {
                p.x = e.pageX;
                p.y = e.pageY
            } else {
                p.x = e.clientX;
                p.y = e.clientY
            }
            p = p.matrixTransform(c.getScreenCTM().inverse());
            return [p.x, p.y]
        }
        var r = c.getBoundingClientRect();
        return [e.clientX - r.left - c.clientLeft, e.clientY - r.top - c.clientTop]
    };
    d3.touches = function(c, t) {
        if (arguments.length < 2) t = T1().touches;
        return t ? J(t).map(function(a) {
            var p = g3(c, a);
            p.identifier = a.identifier;
            return p
        }) : []
    };

    function h3() {}
    d3.scale = {};

    function j3(d) {
        var s = d[0],
            a = d[d.length - 1];
        return s < a ? [s, a] : [a, s]
    }
    function k3(s) {
        return s.rangeExtent ? s.rangeExtent() : j3(s.range())
    }
    function l3(d, n) {
        var i = 0,
            a = d.length - 1,
            x = d[i],
            b = d[a],
            c;
        if (b < x) {
            c = i;
            i = a;
            a = c;
            c = x;
            x = b;
            b = c
        }
        if (c = b - x) {
            n = n(c);
            d[i] = n.floor(x);
            d[a] = n.ceil(b)
        }
        return d
    }
    function m3() {
        return Math
    }
    d3.scale.linear = function() {
        return n3([0, 1], [0, 1], d3.interpolate, false)
    };

    function n3(d, r, i, c) {
        var o, a;

        function b() {
            var l = Math.min(d.length, r.length) > 2 ? z3 : w3,
                u = c ? Y1 : X1;
            o = l(d, r, u, i);
            a = l(r, d, u, d3.interpolate);
            return s
        }
        function s(x) {
            return o(x)
        }
        s.invert = function(y) {
            return a(y)
        };
        s.domain = function(x) {
            if (!arguments.length) return d;
            d = x.map(Number);
            return b()
        };
        s.range = function(x) {
            if (!arguments.length) return r;
            r = x;
            return b()
        };
        s.rangeRound = function(x) {
            return s.range(x).interpolate(d3.interpolateRound)
        };
        s.clamp = function(x) {
            if (!arguments.length) return c;
            c = x;
            return b()
        };
        s.interpolate = function(x) {
            if (!arguments.length) return i;
            i = x;
            return b()
        };
        s.ticks = function(m) {
            return u3(d, m)
        };
        s.tickFormat = function(m) {
            return v3(d, m)
        };
        s.nice = function() {
            l3(d, q3);
            return b()
        };
        s.copy = function() {
            return n3(d, r, i, c)
        };
        return b()
    }
    function o3(s, l) {
        return d3.rebind(s, l, "range", "rangeRound", "interpolate", "clamp")
    }
    function q3(d) {
        d = Math.pow(10, Math.round(Math.log(d) / Math.LN10) - 1);
        return {
            floor: function(x) {
                return Math.floor(x / d) * d
            },
            ceil: function(x) {
                return Math.ceil(x / d) * d
            }
        }
    }
    function r3(d, m) {
        var a = j3(d),
            s = a[1] - a[0],
            b = Math.pow(10, Math.floor(Math.log(s / m) / Math.LN10)),
            c = m / s * b;
        if (c <= .15) b *= 10;
        else if (c <= .35) b *= 5;
        else if (c <= .75) b *= 2;
        a[0] = Math.ceil(a[0] / b) * b;
        a[1] = Math.floor(a[1] / b) * b + b * .5;
        a[2] = b;
        return a
    }
    function u3(d, m) {
        return d3.range.apply(d3, r3(d, m))
    }
    function v3(d, m) {
        return d3.format(",." + Math.max(0, -Math.floor(Math.log(r3(d, m)[2]) / Math.LN10 + .01)) + "f")
    }
    function w3(d, r, a, b) {
        var u = a(d[0], d[1]),
            i = b(r[0], r[1]);
        return function(x) {
            return i(u(x))
        }
    }
    function z3(d, r, a, b) {
        var u = [],
            i = [],
            j = 0,
            k = Math.min(d.length, r.length) - 1;
        if (d[k] < d[0]) {
            d = d.slice().reverse();
            r = r.slice().reverse()
        }
        while (++j <= k) {
            u.push(a(d[j - 1], d[j]));
            i.push(b(r[j - 1], r[j]))
        }
        return function(x) {
            var j = d3.bisect(d, x, 1, k) - 1;
            return i[j](u[j](x))
        }
    }
    d3.scale.log = function() {
        return A3(d3.scale.linear(), C3)
    };

    function A3(l, a) {
        var p = a.pow;

        function s(x) {
            return l(a(x))
        }
        s.invert = function(x) {
            return p(l.invert(x))
        };
        s.domain = function(x) {
            if (!arguments.length) return l.domain().map(p);
            a = x[0] < 0 ? D3 : C3;
            p = a.pow;
            l.domain(x.map(a));
            return s
        };
        s.nice = function() {
            l.domain(l3(l.domain(), m3));
            return s
        };
        s.ticks = function() {
            var b = j3(l.domain()),
                t = [];
            if (b.every(isFinite)) {
                var i = Math.floor(b[0]),
                    j = Math.ceil(b[1]),
                    u = p(b[0]),
                    v = p(b[1]);
                if (a === D3) {
                    t.push(p(i));
                    for (; i++ < j;) for (var k = 9; k > 0; k--) t.push(p(i) * k)
                } else {
                    for (; i < j; i++) for (var k = 1; k < 10; k++) t.push(p(i) * k);
                    t.push(p(i))
                }
                for (i = 0; t[i] < u; i++) {}
                for (j = t.length; t[j - 1] > v; j--) {}
                t = t.slice(i, j)
            }
            return t
        };
        s.tickFormat = function(n, b) {
            if (arguments.length < 2) b = B3;
            if (arguments.length < 1) return b;
            var k = Math.max(.1, n / s.ticks().length),
                f = a === D3 ? (e = -1e-12, Math.floor) : (e = 1e-12, Math.ceil),
                e;
            return function(d) {
                return d / p(f(a(d) + e)) <= k ? b(d) : ""
            }
        };
        s.copy = function() {
            return A3(l.copy(), a)
        };
        return o3(s, l)
    }
    var B3 = d3.format(".0e");

    function C3(x) {
        return Math.log(x < 0 ? 0 : x) / Math.LN10
    }
    function D3(x) {
        return -Math.log(x > 0 ? 0 : -x) / Math.LN10
    }
    C3.pow = function(x) {
        return Math.pow(10, x)
    };
    D3.pow = function(x) {
        return -Math.pow(10, -x)
    };
    d3.scale.pow = function() {
        return E3(d3.scale.linear(), 1)
    };

    function E3(l, a) {
        var p = F3(a),
            b = F3(1 / a);

        function s(x) {
            return l(p(x))
        }
        s.invert = function(x) {
            return b(l.invert(x))
        };
        s.domain = function(x) {
            if (!arguments.length) return l.domain().map(b);
            l.domain(x.map(p));
            return s
        };
        s.ticks = function(m) {
            return u3(s.domain(), m)
        };
        s.tickFormat = function(m) {
            return v3(s.domain(), m)
        };
        s.nice = function() {
            return s.domain(l3(s.domain(), q3))
        };
        s.exponent = function(x) {
            if (!arguments.length) return a;
            var d = s.domain();
            p = F3(a = x);
            b = F3(1 / a);
            return s.domain(d)
        };
        s.copy = function() {
            return E3(l.copy(), a)
        };
        return o3(s, l)
    }
    function F3(e) {
        return function(x) {
            return x < 0 ? -Math.pow(-x, e) : Math.pow(x, e)
        }
    }
    d3.scale.sqrt = function() {
        return d3.scale.pow().exponent(.5)
    };
    d3.scale.ordinal = function() {
        return G3([], {
            t: "range",
            x: []
        })
    };

    function G3(d, r) {
        var a, b, c;

        function s(x) {
            return b[((a.get(x) || a.set(x, d.push(x))) - 1) % b.length]
        }
        function f(g, h) {
            return d3.range(d.length).map(function(i) {
                return g + h * i
            })
        }
        s.domain = function(x) {
            if (!arguments.length) return d;
            d = [];
            a = new O;
            var i = -1,
                n = x.length,
                g;
            while (++i < n) if (!a.has(g = x[i])) a.set(g, d.push(g));
            return s[r.t](r.x, r.p)
        };
        s.range = function(x) {
            if (!arguments.length) return b;
            b = x;
            c = 0;
            r = {
                t: "range",
                x: x
            };
            return s
        };
        s.rangePoints = function(x, p) {
            if (arguments.length < 2) p = 0;
            var g = x[0],
                h = x[1],
                i = (h - g) / (d.length - 1 + p);
            b = f(d.length < 2 ? (g + h) / 2 : g + i * p / 2, i);
            c = 0;
            r = {
                t: "rangePoints",
                x: x,
                p: p
            };
            return s
        };
        s.rangeBands = function(x, p) {
            if (arguments.length < 2) p = 0;
            var g = x[1] < x[0],
                h = x[g - 0],
                i = x[1 - g],
                j = (i - h) / (d.length + p);
            b = f(h + j * p, j);
            if (g) b.reverse();
            c = j * (1 - p);
            r = {
                t: "rangeBands",
                x: x,
                p: p
            };
            return s
        };
        s.rangeRoundBands = function(x, p) {
            if (arguments.length < 2) p = 0;
            var g = x[1] < x[0],
                h = x[g - 0],
                i = x[1 - g],
                j = Math.floor((i - h) / (d.length + p)),
                D = i - h - (d.length - p) * j;
            b = f(h + Math.round(D / 2), j);
            if (g) b.reverse();
            c = Math.round(j * (1 - p));
            r = {
                t: "rangeRoundBands",
                x: x,
                p: p
            };
            return s
        };
        s.rangeBand = function() {
            return c
        };
        s.rangeExtent = function() {
            return j3(r.x)
        };
        s.copy = function() {
            return G3(d, r)
        };
        return s.domain(d)
    }
    d3.scale.category10 = function() {
        return d3.scale.ordinal().range(H3)
    };
    d3.scale.category20 = function() {
        return d3.scale.ordinal().range(I3)
    };
    d3.scale.category20b = function() {
        return d3.scale.ordinal().range(J3)
    };
    d3.scale.category20c = function() {
        return d3.scale.ordinal().range(K3)
    };
    var H3 = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"];
    var I3 = ["#1f77b4", "#aec7e8", "#ff7f0e", "#ffbb78", "#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd", "#c5b0d5", "#8c564b", "#c49c94", "#e377c2", "#f7b6d2", "#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf", "#9edae5"];
    var J3 = ["#393b79", "#5254a3", "#6b6ecf", "#9c9ede", "#637939", "#8ca252", "#b5cf6b", "#cedb9c", "#8c6d31", "#bd9e39", "#e7ba52", "#e7cb94", "#843c39", "#ad494a", "#d6616b", "#e7969c", "#7b4173", "#a55194", "#ce6dbd", "#de9ed6"];
    var K3 = ["#3182bd", "#6baed6", "#9ecae1", "#c6dbef", "#e6550d", "#fd8d3c", "#fdae6b", "#fdd0a2", "#31a354", "#74c476", "#a1d99b", "#c7e9c0", "#756bb1", "#9e9ac8", "#bcbddc", "#dadaeb", "#636363", "#969696", "#bdbdbd", "#d9d9d9"];
    d3.scale.quantile = function() {
        return L3([], [])
    };

    function L3(a, r) {
        var t;

        function b() {
            var k = 0,
                n = a.length,
                q = r.length;
            t = [];
            while (++k < q) t[k - 1] = d3.quantile(a, k / q);
            return s
        }
        function s(x) {
            if (isNaN(x = +x)) return NaN;
            return r[d3.bisect(t, x)]
        }
        s.domain = function(x) {
            if (!arguments.length) return a;
            a = x.filter(function(d) {
                return !isNaN(d)
            }).sort(d3.ascending);
            return b()
        };
        s.range = function(x) {
            if (!arguments.length) return r;
            r = x;
            return b()
        };
        s.quantiles = function() {
            return t
        };
        s.copy = function() {
            return L3(a, r)
        };
        return b()
    }
    d3.scale.quantize = function() {
        return M3(0, 1, [0, 1])
    };

    function M3(a, b, r) {
        var k, i;

        function s(x) {
            return r[Math.max(0, Math.min(i, Math.floor(k * (x - a))))]
        }
        function c() {
            k = r.length / (b - a);
            i = r.length - 1;
            return s
        }
        s.domain = function(x) {
            if (!arguments.length) return [a, b];
            a = +x[0];
            b = +x[x.length - 1];
            return c()
        };
        s.range = function(x) {
            if (!arguments.length) return r;
            r = x;
            return c()
        };
        s.copy = function() {
            return M3(a, b, r)
        };
        return c()
    }
    d3.scale.identity = function() {
        return N3([0, 1])
    };

    function N3(d) {
        function i(x) {
            return +x
        }
        i.invert = i;
        i.domain = i.range = function(x) {
            if (!arguments.length) return d;
            d = x.map(i);
            return i
        };
        i.ticks = function(m) {
            return u3(d, m)
        };
        i.tickFormat = function(m) {
            return v3(d, m)
        };
        i.copy = function() {
            return N3(d)
        };
        return i
    }
    d3.svg = {};
    d3.svg.arc = function() {
        var i = Q3,
            o = R3,
            s = S3,
            b = T3;

        function c() {
            var r = i.apply(this, arguments),
                a = o.apply(this, arguments),
                d = s.apply(this, arguments) + O3,
                f = b.apply(this, arguments) + O3,
                g = (f < d && (g = d, d = f, f = g), f - d),
                h = g < Math.PI ? "0" : "1",
                j = Math.cos(d),
                k = Math.sin(d),
                l = Math.cos(f),
                m = Math.sin(f);
            return g >= P3 ? (r ? "M0," + a + "A" + a + "," + a + " 0 1,1 0," + (-a) + "A" + a + "," + a + " 0 1,1 0," + a + "M0," + r + "A" + r + "," + r + " 0 1,0 0," + (-r) + "A" + r + "," + r + " 0 1,0 0," + r + "Z" : "M0," + a + "A" + a + "," + a + " 0 1,1 0," + (-a) + "A" + a + "," + a + " 0 1,1 0," + a + "Z") : (r ? "M" + a * j + "," + a * k + "A" + a + "," + a + " 0 " + h + ",1 " + a * l + "," + a * m + "L" + r * l + "," + r * m + "A" + r + "," + r + " 0 " + h + ",0 " + r * j + "," + r * k + "Z" : "M" + a * j + "," + a * k + "A" + a + "," + a + " 0 " + h + ",1 " + a * l + "," + a * m + "L0,0" + "Z")
        }
        c.innerRadius = function(v) {
            if (!arguments.length) return i;
            i = U(v);
            return c
        };
        c.outerRadius = function(v) {
            if (!arguments.length) return o;
            o = U(v);
            return c
        };
        c.startAngle = function(v) {
            if (!arguments.length) return s;
            s = U(v);
            return c
        };
        c.endAngle = function(v) {
            if (!arguments.length) return b;
            b = U(v);
            return c
        };
        c.centroid = function() {
            var r = (i.apply(this, arguments) + o.apply(this, arguments)) / 2,
                a = (s.apply(this, arguments) + b.apply(this, arguments)) / 2 + O3;
            return [Math.cos(a) * r, Math.sin(a) * r]
        };
        return c
    };
    var O3 = -Math.PI / 2,
        P3 = 2 * Math.PI - 1e-6;

    function Q3(d) {
        return d.innerRadius
    }
    function R3(d) {
        return d.outerRadius
    }
    function S3(d) {
        return d.startAngle
    }
    function T3(d) {
        return d.endAngle
    }
    function U3(p) {
        var x = V3,
            y = W3,
            a = T,
            b = X3,
            c = Z3,
            t = .7;

        function l(f) {
            var s = [],
                g = [],
                i = -1,
                n = f.length,
                d, h = U(x),
                j = U(y);

            function k() {
                s.push("M", c(p(g), t))
            }
            while (++i < n) {
                if (a.call(this, d = f[i], i)) {
                    g.push([+h.call(this, d, i), +j.call(this, d, i)])
                } else if (g.length) {
                    k();
                    g = []
                }
            }
            if (g.length) k();
            return s.length ? s.join("") : null
        }
        l.x = function(_) {
            if (!arguments.length) return x;
            x = _;
            return l
        };
        l.y = function(_) {
            if (!arguments.length) return y;
            y = _;
            return l
        };
        l.defined = function(_) {
            if (!arguments.length) return a;
            a = _;
            return l
        };
        l.interpolate = function(_) {
            if (!arguments.length) return b;
            if (!Y3.has(_ += "")) _ = X3;
            c = Y3.get(b = _);
            return l
        };
        l.tension = function(_) {
            if (!arguments.length) return t;
            t = _;
            return l
        };
        return l
    }
    d3.svg.line = function() {
        return U3(R)
    };

    function V3(d) {
        return d[0]
    }
    function W3(d) {
        return d[1]
    }
    var X3 = "linear";
    var Y3 = d3.map({
        "linear": Z3,
        "step-before": $3,
        "step-after": _3,
        "basis": f4,
        "basis-open": g4,
        "basis-closed": h4,
        "bundle": i4,
        "cardinal": c4,
        "cardinal-open": a4,
        "cardinal-closed": b4,
        "monotone": r4
    });

    function Z3(a) {
        var i = 0,
            n = a.length,
            p = a[0],
            b = [p[0], ",", p[1]];
        while (++i < n) b.push("L", (p = a[i])[0], ",", p[1]);
        return b.join("")
    }
    function $3(a) {
        var i = 0,
            n = a.length,
            p = a[0],
            b = [p[0], ",", p[1]];
        while (++i < n) b.push("V", (p = a[i])[1], "H", p[0]);
        return b.join("")
    }
    function _3(a) {
        var i = 0,
            n = a.length,
            p = a[0],
            b = [p[0], ",", p[1]];
        while (++i < n) b.push("H", (p = a[i])[0], "V", p[1]);
        return b.join("")
    }
    function a4(p, t) {
        return p.length < 4 ? Z3(p) : p[1] + d4(p.slice(1, p.length - 1), e4(p, t))
    }
    function b4(p, t) {
        return p.length < 3 ? Z3(p) : p[0] + d4((p.push(p[0]), p), e4([p[p.length - 2]].concat(p, [p[1]]), t))
    }
    function c4(p, t, c) {
        return p.length < 3 ? Z3(p) : p[0] + d4(p, e4(p, t))
    }
    function d4(a, b) {
        if (b.length < 1 || (a.length != b.length && a.length != b.length + 2)) {
            return Z3(a)
        }
        var q = a.length != b.length,
            c = "",
            d = a[0],
            p = a[1],
            f = b[0],
            t = f,
            g = 1;
        if (q) {
            c += "Q" + (p[0] - f[0] * 2 / 3) + "," + (p[1] - f[1] * 2 / 3) + "," + p[0] + "," + p[1];
            d = a[1];
            g = 2
        }
        if (b.length > 1) {
            t = b[1];
            p = a[g];
            g++;
            c += "C" + (d[0] + f[0]) + "," + (d[1] + f[1]) + "," + (p[0] - t[0]) + "," + (p[1] - t[1]) + "," + p[0] + "," + p[1];
            for (var i = 2; i < b.length; i++, g++) {
                p = a[g];
                t = b[i];
                c += "S" + (p[0] - t[0]) + "," + (p[1] - t[1]) + "," + p[0] + "," + p[1]
            }
        }
        if (q) {
            var l = a[g];
            c += "Q" + (p[0] + t[0] * 2 / 3) + "," + (p[1] + t[1] * 2 / 3) + "," + l[0] + "," + l[1]
        }
        return c
    }
    function e4(p, t) {
        var b = [],
            a = (1 - t) / 2,
            c, d = p[0],
            f = p[1],
            i = 1,
            n = p.length;
        while (++i < n) {
            c = d;
            d = f;
            f = p[i];
            b.push([a * (f[0] - c[0]), a * (f[1] - c[1])])
        }
        return b
    }
    function f4(p) {
        if (p.length < 3) return Z3(p);
        var i = 1,
            n = p.length,
            a = p[0],
            x = a[0],
            y = a[1],
            b = [x, x, x, (a = p[1])[0]],
            c = [y, y, y, a[1]],
            d = [x, ",", y];
        n4(d, b, c);
        while (++i < n) {
            a = p[i];
            b.shift();
            b.push(a[0]);
            c.shift();
            c.push(a[1]);
            n4(d, b, c)
        }
        i = -1;
        while (++i < 2) {
            b.shift();
            b.push(a[0]);
            c.shift();
            c.push(a[1]);
            n4(d, b, c)
        }
        return d.join("")
    }
    function g4(p) {
        if (p.length < 4) return Z3(p);
        var a = [],
            i = -1,
            n = p.length,
            b, c = [0],
            d = [0];
        while (++i < 3) {
            b = p[i];
            c.push(b[0]);
            d.push(b[1])
        }
        a.push(j4(m4, c) + "," + j4(m4, d));
        --i;
        while (++i < n) {
            b = p[i];
            c.shift();
            c.push(b[0]);
            d.shift();
            d.push(b[1]);
            n4(a, c, d)
        }
        return a.join("")
    }
    function h4(p) {
        var a, i = -1,
            n = p.length,
            m = n + 4,
            b, c = [],
            d = [];
        while (++i < 4) {
            b = p[i % n];
            c.push(b[0]);
            d.push(b[1])
        }
        a = [j4(m4, c), ",", j4(m4, d)];
        --i;
        while (++i < m) {
            b = p[i % n];
            c.shift();
            c.push(b[0]);
            d.shift();
            d.push(b[1]);
            n4(a, c, d)
        }
        return a.join("")
    }
    function i4(a, b) {
        var n = a.length - 1;
        if (n) {
            var x = a[0][0],
                y = a[0][1],
                d = a[n][0] - x,
                c = a[n][1] - y,
                i = -1,
                p, t;
            while (++i <= n) {
                p = a[i];
                t = i / n;
                p[0] = b * p[0] + (1 - b) * (x + t * d);
                p[1] = b * p[1] + (1 - b) * (y + t * c)
            }
        }
        return f4(a)
    }
    function j4(a, b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3]
    }
    var k4 = [0, 2 / 3, 1 / 3, 0],
        l4 = [0, 1 / 3, 2 / 3, 0],
        m4 = [0, 1 / 6, 2 / 3, 1 / 6];

    function n4(p, x, y) {
        p.push("C", j4(k4, x), ",", j4(k4, y), ",", j4(l4, x), ",", j4(l4, y), ",", j4(m4, x), ",", j4(m4, y))
    }
    function o4(p, a) {
        return (a[1] - p[1]) / (a[0] - p[0])
    }
    function p4(p) {
        var i = 0,
            j = p.length - 1,
            m = [],
            a = p[0],
            b = p[1],
            d = m[0] = o4(a, b);
        while (++i < j) {
            m[i] = d + (d = o4(a = b, b = p[i + 1]))
        }
        m[i] = d;
        return m
    }
    function q4(p) {
        var t = [],
            d, a, b, s, m = p4(p),
            i = -1,
            j = p.length - 1;
        while (++i < j) {
            d = o4(p[i], p[i + 1]);
            if (Math.abs(d) < 1e-6) {
                m[i] = m[i + 1] = 0
            } else {
                a = m[i] / d;
                b = m[i + 1] / d;
                s = a * a + b * b;
                if (s > 9) {
                    s = d * 3 / Math.sqrt(s);
                    m[i] = s * a;
                    m[i + 1] = s * b
                }
            }
        }
        i = -1;
        while (++i <= j) {
            s = (p[Math.min(j, i + 1)][0] - p[Math.max(0, i - 1)][0]) / (6 * (1 + m[i] * m[i]));
            t.push([s || 0, m[i] * s || 0])
        }
        return t
    }
    function r4(p) {
        return p.length < 3 ? Z3(p) : p[0] + d4(p, q4(p))
    }
    d3.svg.line.radial = function() {
        var l = U3(s4);
        l.radius = l.x, delete l.x;
        l.angle = l.y, delete l.y;
        return l
    };

    function s4(p) {
        var b, i = -1,
            n = p.length,
            r, a;
        while (++i < n) {
            b = p[i];
            r = b[0];
            a = b[1] + O3;
            b[0] = r * Math.cos(a);
            b[1] = r * Math.sin(a)
        }
        return p
    }
    function t4(p) {
        var a = V3,
            b = V3,
            c = 0,
            f = W3,
            g = T,
            h = X3,
            j = Z3,
            k = Z3,
            L = "L",
            t = .7;

        function l(m) {
            var s = [],
                o = [],
                q = [],
                i = -1,
                n = m.length,
                d, r = U(a),
                u = U(c),
                v = a === b ? function() {
                    return x
                } : U(b),
                w = c === f ? function() {
                    return y
                } : U(f),
                x, y;

            function z() {
                s.push("M", j(p(q), t), L, k(p(o.reverse()), t), "Z")
            }
            while (++i < n) {
                if (g.call(this, d = m[i], i)) {
                    o.push([x = +r.call(this, d, i), y = +u.call(this, d, i)]);
                    q.push([+v.call(this, d, i), +w.call(this, d, i)])
                } else if (o.length) {
                    z();
                    o = [];
                    q = []
                }
            }
            if (o.length) z();
            return s.length ? s.join("") : null
        }
        l.x = function(_) {
            if (!arguments.length) return b;
            a = b = _;
            return l
        };
        l.x0 = function(_) {
            if (!arguments.length) return a;
            a = _;
            return l
        };
        l.x1 = function(_) {
            if (!arguments.length) return b;
            b = _;
            return l
        };
        l.y = function(_) {
            if (!arguments.length) return f;
            c = f = _;
            return l
        };
        l.y0 = function(_) {
            if (!arguments.length) return c;
            c = _;
            return l
        };
        l.y1 = function(_) {
            if (!arguments.length) return f;
            f = _;
            return l
        };
        l.defined = function(_) {
            if (!arguments.length) return g;
            g = _;
            return l
        };
        l.interpolate = function(_) {
            if (!arguments.length) return h;
            if (!Y3.has(_ += "")) _ = X3;
            j = Y3.get(h = _);
            k = j.reverse || j;
            L = /-closed$/.test(_) ? "M" : "L";
            return l
        };
        l.tension = function(_) {
            if (!arguments.length) return t;
            t = _;
            return l
        };
        return l
    }
    $3.reverse = _3;
    _3.reverse = $3;
    d3.svg.area = function() {
        return t4(Object)
    };
    d3.svg.area.radial = function() {
        var a = t4(s4);
        a.radius = a.x, delete a.x;
        a.innerRadius = a.x0, delete a.x0;
        a.outerRadius = a.x1, delete a.x1;
        a.angle = a.y, delete a.y;
        a.startAngle = a.y0, delete a.y0;
        a.endAngle = a.y1, delete a.y1;
        return a
    };
    d3.svg.chord = function() {
        var c = u4,
            g = v4,
            h = w4,
            j = S3,
            k = T3;

        function l(d, i) {
            var s = m(this, c, d, i),
                t = m(this, g, d, i);
            return "M" + s.p0 + o(s.r, s.p1, s.a1 - s.a0) + (n(s, t) ? q(s.r, s.p1, s.r, s.p0) : q(s.r, s.p1, t.r, t.p0) + o(t.r, t.p1, t.a1 - t.a0) + q(t.r, t.p1, s.r, s.p0)) + "Z"
        }
        function m(s, f, d, i) {
            var m = f.call(s, d, i),
                r = h.call(s, m, i),
                a = j.call(s, m, i) + O3,
                b = k.call(s, m, i) + O3;
            return {
                r: r,
                a0: a,
                a1: b,
                p0: [r * Math.cos(a), r * Math.sin(a)],
                p1: [r * Math.cos(b), r * Math.sin(b)]
            }
        }
        function n(a, b) {
            return a.a0 == b.a0 && a.a1 == b.a1
        }
        function o(r, p, a) {
            return "A" + r + "," + r + " 0 " + +(a > Math.PI) + ",1 " + p
        }
        function q(r, p, a, b) {
            return "Q 0,0 " + b
        }
        l.radius = function(v) {
            if (!arguments.length) return h;
            h = U(v);
            return l
        };
        l.source = function(v) {
            if (!arguments.length) return c;
            c = U(v);
            return l
        };
        l.target = function(v) {
            if (!arguments.length) return g;
            g = U(v);
            return l
        };
        l.startAngle = function(v) {
            if (!arguments.length) return j;
            j = U(v);
            return l
        };
        l.endAngle = function(v) {
            if (!arguments.length) return k;
            k = U(v);
            return l
        };
        return l
    };

    function u4(d) {
        return d.source
    }
    function v4(d) {
        return d.target
    }
    function w4(d) {
        return d.radius
    }
    function z4(d) {
        return d.startAngle
    }
    function A4(d) {
        return d.endAngle
    }
    d3.svg.diagonal = function() {
        var s = u4,
            t = v4,
            a = B4;

        function b(d, i) {
            var c = s.call(this, d, i),
                f = t.call(this, d, i),
                m = (c.y + f.y) / 2,
                p = [c, {
                    x: c.x,
                    y: m
                }, {
                    x: f.x,
                    y: m
                },
                f];
            p = p.map(a);
            return "M" + p[0] + "C" + p[1] + " " + p[2] + " " + p[3]
        }
        b.source = function(x) {
            if (!arguments.length) return s;
            s = U(x);
            return b
        };
        b.target = function(x) {
            if (!arguments.length) return t;
            t = U(x);
            return b
        };
        b.projection = function(x) {
            if (!arguments.length) return a;
            a = x;
            return b
        };
        return b
    };

    function B4(d) {
        return [d.x, d.y]
    }
    d3.svg.diagonal.radial = function() {
        var d = d3.svg.diagonal(),
            p = B4,
            a = d.projection;
        d.projection = function(x) {
            return arguments.length ? a(C4(p = x)) : p
        };
        return d
    };

    function C4(p) {
        return function() {
            var d = p.apply(this, arguments),
                r = d[0],
                a = d[1] + O3;
            return [r * Math.cos(a), r * Math.sin(a)]
        }
    }
    d3.svg.mouse = d3.mouse;
    d3.svg.touches = d3.touches;
    d3.svg.symbol = function() {
        var t = E4,
            s = D4;

        function a(d, i) {
            return (G4.get(t.call(this, d, i)) || F4)(s.call(this, d, i))
        }
        a.type = function(x) {
            if (!arguments.length) return t;
            t = U(x);
            return a
        };
        a.size = function(x) {
            if (!arguments.length) return s;
            s = U(x);
            return a
        };
        return a
    };

    function D4() {
        return 64
    }
    function E4() {
        return "circle"
    }
    function F4(s) {
        var r = Math.sqrt(s / Math.PI);
        return "M0," + r + "A" + r + "," + r + " 0 1,1 0," + (-r) + "A" + r + "," + r + " 0 1,1 0," + r + "Z"
    }
    var G4 = d3.map({
        "circle": F4,
        "cross": function(s) {
            var r = Math.sqrt(s / 5) / 2;
            return "M" + -3 * r + "," + -r + "H" + -r + "V" + -3 * r + "H" + r + "V" + -r + "H" + 3 * r + "V" + r + "H" + r + "V" + 3 * r + "H" + -r + "V" + r + "H" + -3 * r + "Z"
        },
        "diamond": function(s) {
            var r = Math.sqrt(s / (2 * I4)),
                a = r * I4;
            return "M0," + -r + "L" + a + ",0" + " 0," + r + " " + -a + ",0" + "Z"
        },
        "square": function(s) {
            var r = Math.sqrt(s) / 2;
            return "M" + -r + "," + -r + "L" + r + "," + -r + " " + r + "," + r + " " + -r + "," + r + "Z"
        },
        "triangle-down": function(s) {
            var r = Math.sqrt(s / H4),
                a = r * H4 / 2;
            return "M0," + a + "L" + r + "," + -a + " " + -r + "," + -a + "Z"
        },
        "triangle-up": function(s) {
            var r = Math.sqrt(s / H4),
                a = r * H4 / 2;
            return "M0," + -a + "L" + r + "," + a + " " + -r + "," + a + "Z"
        }
    });
    d3.svg.symbolTypes = G4.keys();
    var H4 = Math.sqrt(3),
        I4 = Math.tan(30 * Math.PI / 180);
    d3.svg.axis = function() {
        var s = d3.scale.linear(),
            o = "bottom",
            t = 6,
            a = 6,
            b = 6,
            c = 3,
            f = [10],
            h = null,
            i, j = 0;

        function k(g) {
            g.each(function() {
                var g = d3.select(this);
                var l = h == null ? (s.ticks ? s.ticks.apply(s, f) : s.domain()) : h,
                    m = i == null ? (s.tickFormat ? s.tickFormat.apply(s, f) : String) : i;
                var n = L4(s, l, j),
                    p = g.selectAll(".minor").data(n, String),
                    q = p.enter().insert("line", "g").attr("class", "tick minor").style("opacity", 1e-6),
                    r = d3.transition(p.exit()).style("opacity", 1e-6).remove(),
                    u = d3.transition(p).style("opacity", 1);
                var v = g.selectAll("g").data(l, String),
                    w = v.enter().insert("g", "path").style("opacity", 1e-6),
                    y = d3.transition(v.exit()).style("opacity", 1e-6).remove(),
                    z = d3.transition(v).style("opacity", 1),
                    A;
                var B = k3(s),
                    C = g.selectAll(".domain").data([0]),
                    E = C.enter().append("path").attr("class", "domain"),
                    H = d3.transition(C);
                var L = s.copy(),
                    _ = this.__chart__ || L;
                this.__chart__ = L;
                w.append("line").attr("class", "tick");
                w.append("text");
                var a1 = w.select("line"),
                    c1 = z.select("line"),
                    d1 = v.select("text").text(m),
                    e1 = w.select("text"),
                    h1 = z.select("text");
                switch (o) {
                    case "bottom":
                        {
                            A = J4;
                            q.attr("y2", a);
                            u.attr("x2", 0).attr("y2", a);
                            a1.attr("y2", t);
                            e1.attr("y", Math.max(t, 0) + c);
                            c1.attr("x2", 0).attr("y2", t);
                            h1.attr("x", 0).attr("y", Math.max(t, 0) + c);
                            d1.attr("dy", ".71em").attr("text-anchor", "middle");
                            H.attr("d", "M" + B[0] + "," + b + "V0H" + B[1] + "V" + b);
                            break
                        }
                    case "top":
                        {
                            A = J4;
                            q.attr("y2", -a);
                            u.attr("x2", 0).attr("y2", -a);
                            a1.attr("y2", -t);
                            e1.attr("y", -(Math.max(t, 0) + c));
                            c1.attr("x2", 0).attr("y2", -t);
                            h1.attr("x", 0).attr("y", -(Math.max(t, 0) + c));
                            d1.attr("dy", "0em").attr("text-anchor", "middle");
                            H.attr("d", "M" + B[0] + "," + -b + "V0H" + B[1] + "V" + -b);
                            break
                        }
                    case "left":
                        {
                            A = K4;
                            q.attr("x2", -a);
                            u.attr("x2", -a).attr("y2", 0);
                            a1.attr("x2", -t);
                            e1.attr("x", -(Math.max(t, 0) + c));
                            c1.attr("x2", -t).attr("y2", 0);
                            h1.attr("x", -(Math.max(t, 0) + c)).attr("y", 0);
                            d1.attr("dy", ".32em").attr("text-anchor", "end");
                            H.attr("d", "M" + -b + "," + B[0] + "H0V" + B[1] + "H" + -b);
                            break
                        }
                    case "right":
                        {
                            A = K4;
                            q.attr("x2", a);
                            u.attr("x2", a).attr("y2", 0);
                            a1.attr("x2", t);
                            e1.attr("x", Math.max(t, 0) + c);
                            c1.attr("x2", t).attr("y2", 0);
                            h1.attr("x", Math.max(t, 0) + c).attr("y", 0);
                            d1.attr("dy", ".32em").attr("text-anchor", "start");
                            H.attr("d", "M" + b + "," + B[0] + "H0V" + B[1] + "H" + b);
                            break
                        }
                }
                if (s.ticks) {
                    w.call(A, _);
                    z.call(A, L);
                    y.call(A, L);
                    q.call(A, _);
                    u.call(A, L);
                    r.call(A, L)
                } else {
                    var dx = L.rangeBand() / 2,
                        x = function(d) {
                            return L(d) + dx
                        };
                    w.call(A, x);
                    z.call(A, x)
                }
            })
        }
        k.scale = function(x) {
            if (!arguments.length) return s;
            s = x;
            return k
        };
        k.orient = function(x) {
            if (!arguments.length) return o;
            o = x;
            return k
        };
        k.ticks = function() {
            if (!arguments.length) return f;
            f = arguments;
            return k
        };
        k.tickValues = function(x) {
            if (!arguments.length) return h;
            h = x;
            return k
        };
        k.tickFormat = function(x) {
            if (!arguments.length) return i;
            i = x;
            return k
        };
        k.tickSize = function(x, y, z) {
            if (!arguments.length) return t;
            var n = arguments.length - 1;
            t = +x;
            a = n > 1 ? +y : t;
            b = n > 0 ? +arguments[n] : t;
            return k
        };
        k.tickPadding = function(x) {
            if (!arguments.length) return c;
            c = +x;
            return k
        };
        k.tickSubdivide = function(x) {
            if (!arguments.length) return j;
            j = +x;
            return k
        };
        return k
    };

    function J4(s, x) {
        s.attr("transform", function(d) {
            return "translate(" + x(d) + ",0)"
        })
    }
    function K4(s, y) {
        s.attr("transform", function(d) {
            return "translate(0," + y(d) + ")"
        })
    }
    function L4(s, t, m) {
        b = [];
        if (m && t.length > 1) {
            var a = j3(s.domain()),
                b, i = -1,
                n = t.length,
                d = (t[1] - t[0]) / ++m,
                j, v;
            while (++i < n) {
                for (j = m; --j > 0;) {
                    if ((v = +t[i] - j * d) >= a[0]) {
                        b.push(v)
                    }
                }
            }
            for (--i, j = 0; ++j < m && (v = +t[i] + j * d) < a[1];) {
                b.push(v)
            }
        }
        return b
    }
    d3.svg.brush = function() {
        var a = U1(f, "brushstart", "brush", "brushend"),
            x = null,
            y = null,
            r = N4[0],
            b = [
                [0, 0],
                [0, 0]
            ],
            c;

        function f(g) {
            g.each(function() {
                var g = d3.select(this),
                    i = g.selectAll(".background").data([0]),
                    m = g.selectAll(".extent").data([0]),
                    t = g.selectAll(".resize").data(r, String),
                    e;
                g.style("pointer-events", "all").on("mousedown.brush", l).on("touchstart.brush", l);
                i.enter().append("rect").attr("class", "background").style("visibility", "hidden").style("cursor", "crosshair");
                m.enter().append("rect").attr("class", "extent").style("cursor", "move");
                t.enter().append("g").attr("class", function(d) {
                    return "resize " + d
                }).style("cursor", function(d) {
                    return M4[d]
                }).append("rect").attr("x", function(d) {
                    return /[ew]$/.test(d) ? -3 : null
                }).attr("y", function(d) {
                    return /^[ns]/.test(d) ? -3 : null
                }).attr("width", 6).attr("height", 6).style("visibility", "hidden");
                t.style("display", f.empty() ? "none" : null);
                t.exit().remove();
                if (x) {
                    e = k3(x);
                    i.attr("x", e[0]).attr("width", e[1] - e[0]);
                    j(g)
                }
                if (y) {
                    e = k3(y);
                    i.attr("y", e[0]).attr("height", e[1] - e[0]);
                    k(g)
                }
                h(g)
            })
        }
        function h(g) {
            g.selectAll(".resize").attr("transform", function(d) {
                return "translate(" + b[+/e$/.test(d)][0] + "," + b[+/^s/.test(d)][1] + ")"
            })
        }
        function j(g) {
            g.select(".extent").attr("x", b[0][0]);
            g.selectAll(".extent,.n>rect,.s>rect").attr("width", b[1][0] - b[0][0])
        }
        function k(g) {
            g.select(".extent").attr("y", b[0][1]);
            g.selectAll(".extent,.e>rect,.w>rect").attr("height", b[1][1] - b[0][1])
        }
        function l() {
            var t = this,
                d = d3.select(d3.event.target),
                m = a.of(t, arguments),
                g = d3.select(t),
                n = d.datum(),
                o = !/^(n|s)$/.test(n) && x,
                p = !/^(e|w)$/.test(n) && y,
                q = d.classed("extent"),
                s, u = B(),
                v;
            var w = d3.select(window).on("mousemove.brush", H).on("mouseup.brush", _).on("touchmove.brush", H).on("touchend.brush", _).on("keydown.brush", C).on("keyup.brush", E);
            if (q) {
                u[0] = b[0][0] - u[0];
                u[1] = b[0][1] - u[1]
            } else if (n) {
                var z = +/w$/.test(n),
                    A = +/^n/.test(n);
                v = [b[1 - z][0] - u[0], b[1 - A][1] - u[1]];
                u[0] = b[z][0];
                u[1] = b[A][1]
            } else if (d3.event.altKey) s = u.slice();
            g.style("pointer-events", "none").selectAll(".resize").style("display", null);
            d3.select("body").style("cursor", d.style("cursor"));
            m({
                type: "brushstart"
            });
            H();
            S1();

            function B() {
                var i = d3.event.changedTouches;
                return i ? d3.touches(t, i)[0] : d3.mouse(t)
            }
            function C() {
                if (d3.event.keyCode == 32) {
                    if (!q) {
                        s = null;
                        u[0] -= b[1][0];
                        u[1] -= b[1][1];
                        q = 2
                    }
                    S1()
                }
            }
            function E() {
                if (d3.event.keyCode == 32 && q == 2) {
                    u[0] += b[1][0];
                    u[1] += b[1][1];
                    q = 0;
                    S1()
                }
            }
            function H() {
                var i = B(),
                    a1 = false;
                if (v) {
                    i[0] += v[0];
                    i[1] += v[1]
                }
                if (!q) {
                    if (d3.event.altKey) {
                        if (!s) s = [(b[0][0] + b[1][0]) / 2, (b[0][1] + b[1][1]) / 2];
                        u[0] = b[+(i[0] < s[0])][0];
                        u[1] = b[+(i[1] < s[1])][1]
                    } else s = null
                }
                if (o && L(i, x, 0)) {
                    j(g);
                    a1 = true
                }
                if (p && L(i, y, 1)) {
                    k(g);
                    a1 = true
                }
                if (a1) {
                    h(g);
                    m({
                        type: "brush",
                        mode: q ? "move" : "resize"
                    })
                }
            }
            function L(a1, c1, i) {
                var d1 = k3(c1),
                    r0 = d1[0],
                    r1 = d1[1],
                    h1 = u[i],
                    i1 = b[1][i] - b[0][i],
                    l1, m1;
                if (q) {
                    r0 -= h1;
                    r1 -= i1 + h1
                }
                l1 = Math.max(r0, Math.min(r1, a1[i]));
                if (q) {
                    m1 = (l1 += h1) + i1
                } else {
                    if (s) h1 = Math.max(r0, Math.min(r1, 2 * s[i] - l1));
                    if (h1 < l1) {
                        m1 = l1;
                        l1 = h1
                    } else {
                        m1 = h1
                    }
                }
                if (b[0][i] !== l1 || b[1][i] !== m1) {
                    c = null;
                    b[0][i] = l1;
                    b[1][i] = m1;
                    return true
                }
            }
            function _() {
                H();
                g.style("pointer-events", "all").selectAll(".resize").style("display", f.empty() ? "none" : null);
                d3.select("body").style("cursor", null);
                w.on("mousemove.brush", null).on("mouseup.brush", null).on("touchmove.brush", null).on("touchend.brush", null).on("keydown.brush", null).on("keyup.brush", null);
                m({
                    type: "brushend"
                });
                S1()
            }
        }
        f.x = function(z) {
            if (!arguments.length) return x;
            x = z;
            r = N4[!x << 1 | !y];
            return f
        };
        f.y = function(z) {
            if (!arguments.length) return y;
            y = z;
            r = N4[!x << 1 | !y];
            return f
        };
        f.extent = function(z) {
            var d, g, i, m, t;
            if (!arguments.length) {
                z = c || b;
                if (x) {
                    d = z[0][0], g = z[1][0];
                    if (!c) {
                        d = b[0][0], g = b[1][0];
                        if (x.invert) d = x.invert(d), g = x.invert(g);
                        if (g < d) t = d, d = g, g = t
                    }
                }
                if (y) {
                    i = z[0][1], m = z[1][1];
                    if (!c) {
                        i = b[0][1], m = b[1][1];
                        if (y.invert) i = y.invert(i), m = y.invert(m);
                        if (m < i) t = i, i = m, m = t
                    }
                }
                return x && y ? [
                    [d, i],
                    [g, m]
                ] : x ? [d, g] : y && [i, m]
            }
            c = [
                [0, 0],
                [0, 0]
            ];
            if (x) {
                d = z[0], g = z[1];
                if (y) d = d[0], g = g[0];
                c[0][0] = d, c[1][0] = g;
                if (x.invert) d = x(d), g = x(g);
                if (g < d) t = d, d = g, g = t;
                b[0][0] = d | 0, b[1][0] = g | 0
            }
            if (y) {
                i = z[0], m = z[1];
                if (x) i = i[1], m = m[1];
                c[0][1] = i, c[1][1] = m;
                if (y.invert) i = y(i), m = y(m);
                if (m < i) t = i, i = m, m = t;
                b[0][1] = i | 0, b[1][1] = m | 0
            }
            return f
        };
        f.clear = function() {
            c = null;
            b[0][0] = b[0][1] = b[1][0] = b[1][1] = 0;
            return f
        };
        f.empty = function() {
            return (x && b[0][0] === b[1][0]) || (y && b[0][1] === b[1][1])
        };
        return d3.rebind(f, a, "on")
    };
    var M4 = {
        n: "ns-resize",
        e: "ew-resize",
        s: "ns-resize",
        w: "ew-resize",
        nw: "nwse-resize",
        ne: "nesw-resize",
        se: "nwse-resize",
        sw: "nesw-resize"
    };
    var N4 = [
        ["n", "e", "s", "w", "nw", "ne", "se", "sw"],
        ["e", "w"],
        ["n", "s"],
        []
    ];
    d3.behavior = {};
    d3.behavior.drag = function() {
        var a = U1(d, "drag", "dragstart", "dragend"),
            o = null;

        function d() {
            this.on("mousedown.drag", m).on("touchstart.drag", m)
        }
        function m() {
            var b = this,
                c = a.of(b, arguments),
                f = d3.event.target,
                g, h = j(),
                i = 0;
            var w = d3.select(window).on("mousemove.drag", k).on("touchmove.drag", k).on("mouseup.drag", l, true).on("touchend.drag", l, true);
            if (o) {
                g = o.apply(b, arguments);
                g = [g.x - h[0], g.y - h[1]]
            } else {
                g = [0, 0]
            }
            S1();
            c({
                type: "dragstart"
            });

            function j() {
                var p = b.parentNode,
                    t = d3.event.changedTouches;
                return t ? d3.touches(p, t)[0] : d3.mouse(p)
            }
            function k() {
                if (!b.parentNode) return l();
                var p = j(),
                    q = p[0] - h[0],
                    r = p[1] - h[1];
                i |= q | r;
                h = p;
                S1();
                c({
                    type: "drag",
                    x: p[0] + g[0],
                    y: p[1] + g[1],
                    dx: q,
                    dy: r
                })
            }
            function l() {
                c({
                    type: "dragend"
                });
                if (i) {
                    S1();
                    if (d3.event.target === f) w.on("click.drag", n, true)
                }
                w.on("mousemove.drag", null).on("touchmove.drag", null).on("mouseup.drag", null).on("touchend.drag", null)
            }
            function n() {
                S1();
                w.on("click.drag", null)
            }
        }
        d.origin = function(x) {
            if (!arguments.length) return o;
            o = x;
            return d
        };
        return d3.rebind(d, a, "on")
    };
    d3.behavior.zoom = function() {
        var a = [0, 0],
            b, c = 1,
            d, f = P4,
            g = U1(n, "zoom"),
            h, i, j, k, m;

        function n() {
            this.on("mousedown.zoom", A).on("mousewheel.zoom", B).on("mousemove.zoom", C).on("DOMMouseScroll.zoom", B).on("dblclick.zoom", E).on("touchstart.zoom", H).on("touchmove.zoom", L).on("touchend.zoom", H)
        }
        n.translate = function(x) {
            if (!arguments.length) return a;
            a = x.map(Number);
            return n
        };
        n.scale = function(x) {
            if (!arguments.length) return c;
            c = +x;
            return n
        };
        n.scaleExtent = function(x) {
            if (!arguments.length) return f;
            f = x == null ? P4 : x.map(Number);
            return n
        };
        n.x = function(z) {
            if (!arguments.length) return i;
            i = z;
            h = z.copy();
            return n
        };
        n.y = function(z) {
            if (!arguments.length) return k;
            k = z;
            j = z.copy();
            return n
        };

        function o(p) {
            return [(p[0] - a[0]) / c, (p[1] - a[1]) / c]
        }
        function q(l) {
            return [l[0] * c + a[0], l[1] * c + a[1]]
        }
        function r(s) {
            c = Math.max(f[0], Math.min(f[1], s))
        }
        function u(p, l) {
            l = q(l);
            a[0] += p[0] - l[0];
            a[1] += p[1] - l[1]
        }
        function v(g) {
            if (i) i.domain(h.range().map(function(x) {
                return (x - a[0]) / c
            }).map(h.invert));
            if (k) k.domain(j.range().map(function(y) {
                return (y - a[1]) / c
            }).map(j.invert));
            d3.event.preventDefault();
            g({
                type: "zoom",
                scale: c,
                translate: a
            })
        }
        function A() {
            var t = this,
                p = g.of(t, arguments),
                s = d3.event.target,
                x = 0,
                w = d3.select(window).on("mousemove.zoom", C).on("mouseup.zoom", y),
                l = o(d3.mouse(t));
            window.focus();
            S1();

            function C() {
                x = 1;
                u(d3.mouse(t), l);
                v(p)
            }
            function y() {
                if (x) S1();
                w.on("mousemove.zoom", null).on("mouseup.zoom", null);
                if (x && d3.event.target === s) w.on("click.zoom", z, true)
            }
            function z() {
                S1();
                w.on("click.zoom", null)
            }
        }
        function B() {
            if (!b) b = o(d3.mouse(this));
            r(Math.pow(2, Q4() * .002) * c);
            u(d3.mouse(this), b);
            v(g.of(this, arguments))
        }
        function C() {
            b = null
        }
        function E() {
            var p = d3.mouse(this),
                l = o(p);
            r(d3.event.shiftKey ? c / 2 : c * 2);
            u(p, l);
            v(g.of(this, arguments))
        }
        function H() {
            var s = d3.touches(this),
                w = Date.now();
            d = c;
            b = {};
            s.forEach(function(t) {
                b[t.identifier] = o(t)
            });
            S1();
            if ((s.length === 1) && (w - m < 500)) {
                var p = s[0],
                    l = o(s[0]);
                r(c * 2);
                u(p, l);
                v(g.of(this, arguments))
            }
            m = w
        }
        function L() {
            var t = d3.touches(this),
                p = t[0],
                l = b[p.identifier];
            if (s = t[1]) {
                var s, w = b[s.identifier];
                p = [(p[0] + s[0]) / 2, (p[1] + s[1]) / 2];
                l = [(l[0] + w[0]) / 2, (l[1] + w[1]) / 2];
                r(d3.event.scale * d)
            }
            u(p, l);
            v(g.of(this, arguments))
        }
        return d3.rebind(n, g, "on")
    };
    var O4, P4 = [0, Infinity];

    function Q4() {
        if (!O4) {
            O4 = d3.select("body").append("div").style("visibility", "hidden").style("top", 0).style("height", 0).style("width", 0).style("overflow-y", "scroll").append("div").style("height", "2000px").node().parentNode
        }
        var e = d3.event,
            d;
        try {
            O4.scrollTop = 1000;
            O4.dispatchEvent(e);
            d = 1000 - O4.scrollTop
        } catch (D) {
            d = e.wheelDelta || (-e.detail * 5)
        }
        return d
    }
    d3.layout = {};
    d3.layout.bundle = function() {
        return function(l) {
            var p = [],
                i = -1,
                n = l.length;
            while (++i < n) p.push(R4(l[i]));
            return p
        }
    };

    function R4(l) {
        var s = l.source,
            a = l.target,
            b = T4(s, a),
            p = [s];
        while (s !== b) {
            s = s.parent;
            p.push(s)
        }
        var k = p.length;
        while (a !== b) {
            p.splice(k, 0, a);
            a = a.parent
        }
        return p
    }
    function S4(n) {
        var a = [],
            p = n.parent;
        while (p != null) {
            a.push(n);
            n = p;
            p = p.parent
        }
        a.push(n);
        return a
    }
    function T4(a, b) {
        if (a === b) return a;
        var n = S4(a),
            c = S4(b),
            d = n.pop(),
            f = c.pop(),
            s = null;
        while (d === f) {
            s = d;
            d = n.pop();
            f = c.pop()
        }
        return s
    }
    d3.layout.chord = function() {
        var c = {}, f, g, m, n, p = 0,
            s, h, l;

        function r() {
            var q = {}, t = [],
                u = d3.range(n),
                w = [],
                k, x, y, i, j;
            f = [];
            g = [];
            k = 0, i = -1;
            while (++i < n) {
                x = 0, j = -1;
                while (++j < n) {
                    x += m[i][j]
                }
                t.push(x);
                w.push(d3.range(n));
                k += x
            }
            if (s) {
                u.sort(function(a, b) {
                    return s(t[a], t[b])
                })
            }
            if (h) {
                w.forEach(function(d, i) {
                    d.sort(function(a, b) {
                        return h(m[i][a], m[i][b])
                    })
                })
            }
            k = (2 * Math.PI - p * n) / k;
            x = 0, i = -1;
            while (++i < n) {
                y = x, j = -1;
                while (++j < n) {
                    var z = u[i],
                        A = w[z][j],
                        v = m[z][A],
                        B = x,
                        C = x += v * k;
                    q[z + "-" + A] = {
                        index: z,
                        subindex: A,
                        startAngle: B,
                        endAngle: C,
                        value: v
                    }
                }
                g[z] = {
                    index: z,
                    startAngle: y,
                    endAngle: x,
                    value: (x - y) / k
                };
                x += p
            }
            i = -1;
            while (++i < n) {
                j = i - 1;
                while (++j < n) {
                    var E = q[i + "-" + j],
                        H = q[j + "-" + i];
                    if (E.value || H.value) {
                        f.push(E.value < H.value ? {
                            source: H,
                            target: E
                        } : {
                            source: E,
                            target: H
                        })
                    }
                }
            }
            if (l) o()
        }
        function o() {
            f.sort(function(a, b) {
                return l((a.source.value + a.target.value) / 2, (b.source.value + b.target.value) / 2)
            })
        }
        c.matrix = function(x) {
            if (!arguments.length) return m;
            n = (m = x) && m.length;
            f = g = null;
            return c
        };
        c.padding = function(x) {
            if (!arguments.length) return p;
            p = x;
            f = g = null;
            return c
        };
        c.sortGroups = function(x) {
            if (!arguments.length) return s;
            s = x;
            f = g = null;
            return c
        };
        c.sortSubgroups = function(x) {
            if (!arguments.length) return h;
            h = x;
            f = null;
            return c
        };
        c.sortChords = function(x) {
            if (!arguments.length) return l;
            l = x;
            if (f) o();
            return c
        };
        c.chords = function() {
            if (!f) r();
            return f
        };
        c.groups = function() {
            if (!g) r();
            return g
        };
        return c
    };
    d3.layout.force = function() {
        var f = {}, a = d3.dispatch("start", "tick", "end"),
            b = [1, 1],
            c, g, p = .9,
            r = _4,
            u = a5,
            v = -30,
            z = .1,
            A = .8,
            B, C = [],
            E = [],
            H, L, _;

        function a1(n) {
            return function(q, x, y, d, h) {
                if (q.point !== n) {
                    var i = q.cx - n.x,
                        j = q.cy - n.y,
                        l = 1 / Math.sqrt(i * i + j * j);
                    if ((d - x) * l < A) {
                        var k = q.charge * l * l;
                        n.px -= i * k;
                        n.py -= j * k;
                        return true
                    }
                    if (q.point && isFinite(l)) {
                        var k = q.pointCharge * l * l;
                        n.px -= i * k;
                        n.py -= j * k
                    }
                }
                return !q.charge
            }
        }
        f.tick = function() {
            if ((g *= .99) < .005) {
                a.end({
                    type: "end",
                    alpha: g = 0
                });
                return true
            }
            var n = C.length,
                m = E.length,
                q, i, o, s, t, l, k, x, y;
            for (i = 0; i < m; ++i) {
                o = E[i];
                s = o.source;
                t = o.target;
                x = t.x - s.x;
                y = t.y - s.y;
                if (l = (x * x + y * y)) {
                    l = g * L[i] * ((l = Math.sqrt(l)) - H[i]) / l;
                    x *= l;
                    y *= l;
                    t.x -= x * (k = s.weight / (t.weight + s.weight));
                    t.y -= y * k;
                    s.x += x * (k = 1 - k);
                    s.y += y * k
                }
            }
            if (k = g * z) {
                x = b[0] / 2;
                y = b[1] / 2;
                i = -1;
                if (k) while (++i < n) {
                    o = C[i];
                    o.x += (x - o.x) * k;
                    o.y += (y - o.y) * k
                }
            }
            if (v) {
                $4(q = d3.geom.quadtree(C), g, _);
                i = -1;
                while (++i < n) {
                    if (!(o = C[i]).fixed) {
                        q.visit(a1(o))
                    }
                }
            }
            i = -1;
            while (++i < n) {
                o = C[i];
                if (o.fixed) {
                    o.x = o.px;
                    o.y = o.py
                } else {
                    o.x -= (o.px - (o.px = o.x)) * p;
                    o.y -= (o.py - (o.py = o.y)) * p
                }
            }
            a.tick({
                type: "tick",
                alpha: g
            })
        };
        f.nodes = function(x) {
            if (!arguments.length) return C;
            C = x;
            return f
        };
        f.links = function(x) {
            if (!arguments.length) return E;
            E = x;
            return f
        };
        f.size = function(x) {
            if (!arguments.length) return b;
            b = x;
            return f
        };
        f.linkDistance = function(x) {
            if (!arguments.length) return r;
            r = U(x);
            return f
        };
        f.distance = f.linkDistance;
        f.linkStrength = function(x) {
            if (!arguments.length) return u;
            u = U(x);
            return f
        };
        f.friction = function(x) {
            if (!arguments.length) return p;
            p = x;
            return f
        };
        f.charge = function(x) {
            if (!arguments.length) return v;
            v = typeof x === "function" ? x : +x;
            return f
        };
        f.gravity = function(x) {
            if (!arguments.length) return z;
            z = x;
            return f
        };
        f.theta = function(x) {
            if (!arguments.length) return A;
            A = x;
            return f
        };
        f.alpha = function(x) {
            if (!arguments.length) return g;
            if (g) {
                if (x > 0) g = x;
                else g = 0
            } else if (x > 0) {
                a.start({
                    type: "start",
                    alpha: g = x
                });
                d3.timer(f.tick)
            }
            return f
        };
        f.start = function() {
            var i, j, n = C.length,
                m = E.length,
                w = b[0],
                h = b[1],
                d, o;
            for (i = 0; i < n; ++i) {
                (o = C[i]).index = i;
                o.weight = 0
            }
            H = [];
            L = [];
            for (i = 0; i < m; ++i) {
                o = E[i];
                if (typeof o.source == "number") o.source = C[o.source];
                if (typeof o.target == "number") o.target = C[o.target];
                H[i] = r.call(this, o, i);
                L[i] = u.call(this, o, i);
                ++o.source.weight;
                ++o.target.weight
            }
            for (i = 0; i < n; ++i) {
                o = C[i];
                if (isNaN(o.x)) o.x = k("x", w);
                if (isNaN(o.y)) o.y = k("y", h);
                if (isNaN(o.px)) o.px = o.x;
                if (isNaN(o.py)) o.py = o.y
            }
            _ = [];
            if (typeof v === "function") {
                for (i = 0; i < n; ++i) {
                    _[i] = +v.call(this, C[i], i)
                }
            } else {
                for (i = 0; i < n; ++i) {
                    _[i] = v
                }
            }
            function k(q, b) {
                var d = l(i),
                    j = -1,
                    m = d.length,
                    x;
                while (++j < m) if (!isNaN(x = d[j][q])) return x;
                return Math.random() * b
            }
            function l() {
                if (!d) {
                    d = [];
                    for (j = 0; j < n; ++j) {
                        d[j] = []
                    }
                    for (j = 0; j < m; ++j) {
                        var o = E[j];
                        d[o.source.index].push(o.target);
                        d[o.target.index].push(o.source)
                    }
                }
                return d[i]
            }
            return f.resume()
        };
        f.resume = function() {
            return f.alpha(.1)
        };
        f.stop = function() {
            return f.alpha(0)
        };
        f.drag = function() {
            if (!c) c = d3.behavior.drag().origin(R).on("dragstart", c1).on("drag", Z4).on("dragend", Y4);
            this.on("mouseover.force", W4).on("mouseout.force", X4).call(c)
        };

        function c1(d) {
            W4(V4 = d);
            U4 = f
        }
        return d3.rebind(f, a, "on")
    };
    var U4, V4;

    function W4(d) {
        d.fixed |= 2
    }
    function X4(d) {
        if (d !== V4) d.fixed &= 1
    }
    function Y4() {
        V4.fixed &= 1;
        U4 = V4 = null
    }
    function Z4() {
        V4.px = d3.event.x;
        V4.py = d3.event.y;
        U4.resume()
    }
    function $4(q, a, b) {
        var d = 0,
            f = 0;
        q.charge = 0;
        if (!q.leaf) {
            var g = q.nodes,
                n = g.length,
                i = -1,
                c;
            while (++i < n) {
                c = g[i];
                if (c == null) continue;
                $4(c, a, b);
                q.charge += c.charge;
                d += c.charge * c.cx;
                f += c.charge * c.cy
            }
        }
        if (q.point) {
            if (!q.leaf) {
                q.point.x += Math.random() - .5;
                q.point.y += Math.random() - .5
            }
            var k = a * b[q.point.index];
            q.charge += q.pointCharge = k;
            d += k * q.point.x;
            f += k * q.point.y
        }
        q.cx = d / q.charge;
        q.cy = f / q.charge
    }
    function _4(l) {
        return 20
    }
    function a5(l) {
        return 1
    }
    d3.layout.partition = function() {
        var h = d3.layout.hierarchy(),
            s = [1, 1];

        function p(f, x, g, j) {
            var k = f.children;
            f.x = x;
            f.y = f.depth * j;
            f.dx = g;
            f.dy = j;
            if (k && (n = k.length)) {
                var i = -1,
                    n, c, d;
                g = f.value ? g / f.value : 0;
                while (++i < n) {
                    p(c = k[i], x, d = c.value * g, j);
                    x += d
                }
            }
        }
        function a(c) {
            var f = c.children,
                d = 0;
            if (f && (n = f.length)) {
                var i = -1,
                    n;
                while (++i < n) d = Math.max(d, a(f[i]))
            }
            return 1 + d
        }
        function b(d, i) {
            var n = h.call(this, d, i);
            p(n[0], 0, s[0], s[1] / a(n[0]));
            return n
        }
        b.size = function(x) {
            if (!arguments.length) return s;
            s = x;
            return b
        };
        return p5(b, h)
    };
    d3.layout.pie = function() {
        var v = Number,
            s = b5,
            b = 0,
            c = 2 * Math.PI;

        function p(f, i) {
            var g = f.map(function(d, i) {
                return +v.call(p, d, i)
            });
            var a = +(typeof b === "function" ? b.apply(this, arguments) : b);
            var k = ((typeof c === "function" ? c.apply(this, arguments) : c) - b) / d3.sum(g);
            var h = d3.range(f.length);
            if (s != null) h.sort(s === b5 ? function(i, j) {
                return g[j] - g[i]
            } : function(i, j) {
                return s(f[i], f[j])
            });
            var l = [];
            h.forEach(function(i) {
                var d;
                l[i] = {
                    data: f[i],
                    value: d = g[i],
                    startAngle: a,
                    endAngle: a += d * k
                }
            });
            return l
        }
        p.value = function(x) {
            if (!arguments.length) return v;
            v = x;
            return p
        };
        p.sort = function(x) {
            if (!arguments.length) return s;
            s = x;
            return p
        };
        p.startAngle = function(x) {
            if (!arguments.length) return b;
            b = x;
            return p
        };
        p.endAngle = function(x) {
            if (!arguments.length) return c;
            c = x;
            return p
        };
        return p
    };
    var b5 = {};
    d3.layout.stack = function() {
        var a = R,
            b = h5,
            c = i5,
            f = e5,
            x = c5,
            y = d5;

        function s(g, h) {
            var k = g.map(function(d, i) {
                return a.call(s, d, i)
            });
            var p = k.map(function(d, i) {
                return d.map(function(v, i) {
                    return [x.call(s, v, i), y.call(s, v, i)]
                })
            });
            var l = b.call(s, p, h);
            k = d3.permute(k, l);
            p = d3.permute(p, l);
            var q = c.call(s, p, h);
            var n = k.length,
                m = k[0].length,
                i, j, o;
            for (j = 0; j < m; ++j) {
                f.call(s, k[0][j], o = q[j], p[0][j][1]);
                for (i = 1; i < n; ++i) {
                    f.call(s, k[i][j], o += p[i - 1][j][1], p[i][j][1])
                }
            }
            return g
        }
        s.values = function(x) {
            if (!arguments.length) return a;
            a = x;
            return s
        };
        s.order = function(x) {
            if (!arguments.length) return b;
            b = typeof x === "function" ? x : f5.get(x) || h5;
            return s
        };
        s.offset = function(x) {
            if (!arguments.length) return c;
            c = typeof x === "function" ? x : g5.get(x) || i5;
            return s
        };
        s.x = function(z) {
            if (!arguments.length) return x;
            x = z;
            return s
        };
        s.y = function(z) {
            if (!arguments.length) return y;
            y = z;
            return s
        };
        s.out = function(z) {
            if (!arguments.length) return f;
            f = z;
            return s
        };
        return s
    };

    function c5(d) {
        return d.x
    }
    function d5(d) {
        return d.y
    }
    function e5(d, a, y) {
        d.y0 = a;
        d.y = y
    }
    var f5 = d3.map({
        "inside-out": function(d) {
            var n = d.length,
                i, j, m = d.map(j5),
                s = d.map(k5),
                c = d3.range(n).sort(function(a, b) {
                    return m[a] - m[b]
                }),
                t = 0,
                f = 0,
                g = [],
                h = [];
            for (i = 0; i < n; ++i) {
                j = c[i];
                if (t < f) {
                    t += s[j];
                    g.push(j)
                } else {
                    f += s[j];
                    h.push(j)
                }
            }
            return h.reverse().concat(g)
        },
        "reverse": function(d) {
            return d3.range(d.length).reverse()
        },
        "default": h5
    });
    var g5 = d3.map({
        "silhouette": function(d) {
            var n = d.length,
                m = d[0].length,
                s = [],
                a = 0,
                i, j, o, y = [];
            for (j = 0; j < m; ++j) {
                for (i = 0, o = 0; i < n; i++) o += d[i][j][1];
                if (o > a) a = o;
                s.push(o)
            }
            for (j = 0; j < m; ++j) {
                y[j] = (a - s[j]) / 2
            }
            return y
        },
        "wiggle": function(d) {
            var n = d.length,
                x = d[0],
                m = x.length,
                a = 0,
                i, j, k, s, b, c, f, o, g, y = [];
            y[0] = o = g = 0;
            for (j = 1; j < m; ++j) {
                for (i = 0, s = 0; i < n; ++i) s += d[i][j][1];
                for (i = 0, b = 0, f = x[j][0] - x[j - 1][0]; i < n; ++i) {
                    for (k = 0, c = (d[i][j][1] - d[i][j - 1][1]) / (2 * f); k < i; ++k) {
                        c += (d[k][j][1] - d[k][j - 1][1]) / f
                    }
                    b += c * d[i][j][1]
                }
                y[j] = o -= s ? b / s * f : 0;
                if (o < g) g = o
            }
            for (j = 0; j < m; ++j) y[j] -= g;
            return y
        },
        "expand": function(d) {
            var n = d.length,
                m = d[0].length,
                k = 1 / n,
                i, j, o, y = [];
            for (j = 0; j < m; ++j) {
                for (i = 0, o = 0; i < n; i++) o += d[i][j][1];
                if (o) for (i = 0; i < n; i++) d[i][j][1] /= o;
                else for (i = 0; i < n; i++) d[i][j][1] = k
            }
            for (j = 0; j < m; ++j) y[j] = 0;
            return y
        },
        "zero": i5
    });

    function h5(d) {
        return d3.range(d.length)
    }
    function i5(d) {
        var j = -1,
            m = d[0].length,
            y = [];
        while (++j < m) y[j] = 0;
        return y
    }
    function j5(a) {
        var i = 1,
            j = 0,
            v = a[0][1],
            k, n = a.length;
        for (; i < n; ++i) {
            if ((k = a[i][1]) > v) {
                j = i;
                v = k
            }
        }
        return j
    }
    function k5(d) {
        return d.reduce(l5, 0)
    }
    function l5(p, d) {
        return p + d[1]
    }
    d3.layout.histogram = function() {
        var f = true,
            v = Number,
            r = o5,
            b = m5;

        function h(d, i) {
            var a = [],
                c = d.map(v, this),
                g = r.call(this, c, i),
                t = b.call(this, g, c, i),
                j, i = -1,
                n = c.length,
                m = t.length - 1,
                k = f ? 1 : 1 / n,
                x;
            while (++i < m) {
                j = a[i] = [];
                j.dx = t[i + 1] - (j.x = t[i]);
                j.y = 0
            }
            if (m > 0) {
                i = -1;
                while (++i < n) {
                    x = c[i];
                    if ((x >= g[0]) && (x <= g[1])) {
                        j = a[d3.bisect(t, x, 1, m) - 1];
                        j.y += k;
                        j.push(d[i])
                    }
                }
            }
            return a
        }
        h.value = function(x) {
            if (!arguments.length) return v;
            v = x;
            return h
        };
        h.range = function(x) {
            if (!arguments.length) return r;
            r = U(x);
            return h
        };
        h.bins = function(x) {
            if (!arguments.length) return b;
            b = typeof x === "number" ? function(a) {
                return n5(a, x)
            } : U(x);
            return h
        };
        h.frequency = function(x) {
            if (!arguments.length) return f;
            f = !! x;
            return h
        };
        return h
    };

    function m5(r, v) {
        return n5(r, Math.ceil(Math.log(v.length) / Math.LN2 + 1))
    }
    function n5(r, n) {
        var x = -1,
            b = +r[0],
            m = (r[1] - b) / n,
            f = [];
        while (++x <= n) f[x] = m * x + b;
        return f
    }
    function o5(v) {
        return [d3.min(v), d3.max(v)]
    }
    d3.layout.hierarchy = function() {
        var s = s5,
            a = q5,
            b = r5;

        function r(g, k, l) {
            var m = a.call(h, g, k),
                o = u5 ? g : {
                    data: g
                };
            o.depth = k;
            l.push(o);
            if (m && (n = m.length)) {
                var i = -1,
                    n, c = o.children = [],
                    v = 0,
                    j = k + 1,
                    d;
                while (++i < n) {
                    d = r(m[i], j, l);
                    d.parent = o;
                    c.push(d);
                    v += d.value
                }
                if (s) c.sort(s);
                if (b) o.value = v
            } else if (b) {
                o.value = +b.call(h, g, k) || 0
            }
            return o
        }
        function f(c, d) {
            var a = c.children,
                v = 0;
            if (a && (n = a.length)) {
                var i = -1,
                    n, j = d + 1;
                while (++i < n) v += f(a[i], j)
            } else if (b) {
                v = +b.call(h, u5 ? c : c.data, d) || 0
            }
            if (b) c.value = v;
            return v
        }
        function h(d) {
            var n = [];
            r(d, 0, n);
            return n
        }
        h.sort = function(x) {
            if (!arguments.length) return s;
            s = x;
            return h
        };
        h.children = function(x) {
            if (!arguments.length) return a;
            a = x;
            return h
        };
        h.value = function(x) {
            if (!arguments.length) return b;
            b = x;
            return h
        };
        h.revalue = function(c) {
            f(c, 0);
            return c
        };
        return h
    };

    function p5(o, h) {
        d3.rebind(o, h, "sort", "children", "value");
        o.links = t5;
        o.nodes = function(d) {
            u5 = true;
            return (o.nodes = o)(d)
        };
        return o
    }
    function q5(d) {
        return d.children
    }
    function r5(d) {
        return d.value
    }
    function s5(a, b) {
        return b.value - a.value
    }
    function t5(n) {
        return d3.merge(n.map(function(p) {
            return (p.children || []).map(function(c) {
                return {
                    source: p,
                    target: c
                }
            })
        }))
    }
    var u5 = false;
    d3.layout.pack = function() {
        var a = d3.layout.hierarchy().sort(v5),
            s = [1, 1];

        function p(d, i) {
            var n = a.call(this, d, i),
                r = n[0];
            r.x = 0;
            r.y = 0;
            C5(r);
            var w = s[0],
                h = s[1],
                k = 1 / Math.max(2 * r.r / w, 2 * r.r / h);
            D5(r, w / 2, h / 2, k);
            return n
        }
        p.size = function(x) {
            if (!arguments.length) return s;
            s = x;
            return p
        };
        return p5(p, a)
    };

    function v5(a, b) {
        return a.value - b.value
    }
    function w5(a, b) {
        var c = a._pack_next;
        a._pack_next = b;
        b._pack_prev = a;
        b._pack_next = c;
        c._pack_prev = b
    }
    function x5(a, b) {
        a._pack_next = b;
        b._pack_prev = a
    }
    function y5(a, b) {
        var d = b.x - a.x,
            c = b.y - a.y,
            f = a.r + b.r;
        return f * f - d * d - c * c > .001
    }
    function z5(d) {
        var x = Infinity,
            f = -Infinity,
            y = Infinity,
            g = -Infinity,
            n = d.length,
            a, b, c, j, k;

        function h(r) {
            x = Math.min(r.x - r.r, x);
            f = Math.max(r.x + r.r, f);
            y = Math.min(r.y - r.r, y);
            g = Math.max(r.y + r.r, g)
        }
        d.forEach(A5);
        a = d[0];
        a.x = -a.r;
        a.y = 0;
        h(a);
        if (n > 1) {
            b = d[1];
            b.x = b.r;
            b.y = 0;
            h(b);
            if (n > 2) {
                c = d[2];
                E5(a, b, c);
                h(c);
                w5(a, c);
                a._pack_prev = c;
                w5(c, b);
                b = a._pack_next;
                for (var i = 3; i < n; i++) {
                    E5(a, b, c = d[i]);
                    var l = 0,
                        s = 1,
                        m = 1;
                    for (j = b._pack_next; j !== b; j = j._pack_next, s++) {
                        if (y5(j, c)) {
                            l = 1;
                            break
                        }
                    }
                    if (l == 1) {
                        for (k = a._pack_prev; k !== j._pack_prev; k = k._pack_prev, m++) {
                            if (y5(k, c)) {
                                break
                            }
                        }
                    }
                    if (l) {
                        if (s < m || (s == m && b.r < a.r)) x5(a, b = j);
                        else x5(a = k, b);
                        i--
                    } else {
                        w5(a, c);
                        b = c;
                        h(c)
                    }
                }
            }
        }
        var o = (x + f) / 2,
            p = (y + g) / 2,
            q = 0;
        for (var i = 0; i < n; i++) {
            var r = d[i];
            r.x -= o;
            r.y -= p;
            q = Math.max(q, r.r + Math.sqrt(r.x * r.x + r.y * r.y))
        }
        d.forEach(B5);
        return q
    }
    function A5(n) {
        n._pack_next = n._pack_prev = n
    }
    function B5(n) {
        delete n._pack_next;
        delete n._pack_prev
    }
    function C5(n) {
        var c = n.children;
        if (c && c.length) {
            c.forEach(C5);
            n.r = z5(c)
        } else {
            n.r = Math.sqrt(n.value)
        }
    }
    function D5(a, x, y, k) {
        var c = a.children;
        a.x = (x += k * a.x);
        a.y = (y += k * a.y);
        a.r *= k;
        if (c) {
            var i = -1,
                n = c.length;
            while (++i < n) D5(c[i], x, y, k)
        }
    }
    function E5(a, b, c) {
        var d = a.r + c.r,
            f = b.x - a.x,
            g = b.y - a.y;
        if (d && (f || g)) {
            var h = b.r + c.r,
                i = Math.sqrt(f * f + g * g),
                j = Math.max(-1, Math.min(1, (d * d + i * i - h * h) / (2 * d * i))),
                t = Math.acos(j),
                x = j * (d /= i),
                y = Math.sin(t) * d;
            c.x = a.x + x * f + y * g;
            c.y = a.y + x * g - y * f
        } else {
            c.x = a.x + d;
            c.y = a.y
        }
    }
    d3.layout.cluster = function() {
        var h = d3.layout.hierarchy().sort(null).value(null),
            s = J5,
            a = [1, 1];

        function c(d, i) {
            var n = h.call(this, d, i),
                r = n[0],
                p, x = 0,
                k, b;
            Q5(r, function(m) {
                var o = m.children;
                if (o && o.length) {
                    m.x = G5(o);
                    m.y = F5(o)
                } else {
                    m.x = p ? x += s(m, p) : 0;
                    m.y = 0;
                    p = m
                }
            });
            var l = H5(r),
                f = I5(r),
                g = l.x - s(l, f) / 2,
                j = f.x + s(f, l) / 2;
            Q5(r, function(m) {
                m.x = (m.x - g) / (j - g) * a[0];
                m.y = (1 - (r.y ? m.y / r.y : 1)) * a[1]
            });
            return n
        }
        c.separation = function(x) {
            if (!arguments.length) return s;
            s = x;
            return c
        };
        c.size = function(x) {
            if (!arguments.length) return a;
            a = x;
            return c
        };
        return p5(c, h)
    };

    function F5(c) {
        return 1 + d3.max(c, function(a) {
            return a.y
        })
    }
    function G5(c) {
        return c.reduce(function(x, a) {
            return x + a.x
        }, 0) / c.length
    }
    function H5(n) {
        var c = n.children;
        return c && c.length ? H5(c[0]) : n
    }
    function I5(a) {
        var c = a.children,
            n;
        return c && (n = c.length) ? I5(c[n - 1]) : a
    }
    d3.layout.tree = function() {
        var h = d3.layout.hierarchy().sort(null).value(null),
            s = J5,
            a = [1, 1];

        function t(d, i) {
            var b = h.call(this, d, i),
                r = b[0];

            function f(p, q) {
                var u = p.children,
                    v = p._tree;
                if (u && (n = u.length)) {
                    var n, w = u[0],
                        x, z = w,
                        A, i = -1;
                    while (++i < n) {
                        A = u[i];
                        f(A, x);
                        z = g(A, x, z);
                        x = A
                    }
                    R5(p);
                    var B = .5 * (w._tree.prelim + A._tree.prelim);
                    if (q) {
                        v.prelim = q._tree.prelim + s(p, q);
                        v.mod = v.prelim - B
                    } else {
                        v.prelim = B
                    }
                } else {
                    if (q) {
                        v.prelim = q._tree.prelim + s(p, q)
                    }
                }
            }
            function c(p, x) {
                p.x = p._tree.prelim + x;
                var q = p.children;
                if (q && (n = q.length)) {
                    var i = -1,
                        n;
                    x += p._tree.mod;
                    while (++i < n) {
                        c(q[i], x)
                    }
                }
            }
            function g(n, p, q) {
                if (p) {
                    var v = n,
                        u = n,
                        w = p,
                        x = n.parent.children[0],
                        z = v._tree.mod,
                        A = u._tree.mod,
                        B = w._tree.mod,
                        C = x._tree.mod,
                        E;
                    while (w = L5(w), v = K5(v), w && v) {
                        x = K5(x);
                        u = L5(u);
                        u._tree.ancestor = n;
                        E = w._tree.prelim + B - v._tree.prelim - z + s(w, v);
                        if (E > 0) {
                            S5(T5(w, n, q), n, E);
                            z += E;
                            A += E
                        }
                        B += w._tree.mod;
                        z += v._tree.mod;
                        C += x._tree.mod;
                        A += u._tree.mod
                    }
                    if (w && !L5(u)) {
                        u._tree.thread = w;
                        u._tree.mod += B - A
                    }
                    if (v && !K5(x)) {
                        x._tree.thread = v;
                        x._tree.mod += z - C;
                        q = n
                    }
                }
                return q
            }
            Q5(r, function(n, p) {
                n._tree = {
                    ancestor: n,
                    prelim: 0,
                    mod: 0,
                    change: 0,
                    shift: 0,
                    number: p ? p._tree.number + 1 : 0
                }
            });
            f(r);
            c(r, -r._tree.prelim);
            var l = M5(r, O5),
                j = M5(r, N5),
                k = M5(r, P5),
                m = l.x - s(l, j) / 2,
                o = j.x + s(j, l) / 2,
                y = k.depth || 1;
            Q5(r, function(n) {
                n.x = (n.x - m) / (o - m) * a[0];
                n.y = n.depth / y * a[1];
                delete n._tree
            });
            return b
        }
        t.separation = function(x) {
            if (!arguments.length) return s;
            s = x;
            return t
        };
        t.size = function(x) {
            if (!arguments.length) return a;
            a = x;
            return t
        };
        return p5(t, h)
    };

    function J5(a, b) {
        return a.parent == b.parent ? 1 : 2
    }
    function K5(n) {
        var c = n.children;
        return c && c.length ? c[0] : n._tree.thread
    }
    function L5(a) {
        var c = a.children,
            n;
        return c && (n = c.length) ? c[n - 1] : a._tree.thread
    }
    function M5(a, c) {
        var b = a.children;
        if (b && (n = b.length)) {
            var d, n, i = -1;
            while (++i < n) {
                if (c(d = M5(b[i], c), a) > 0) {
                    a = d
                }
            }
        }
        return a
    }
    function N5(a, b) {
        return a.x - b.x
    }
    function O5(a, b) {
        return b.x - a.x
    }
    function P5(a, b) {
        return a.depth - b.depth
    }
    function Q5(a, c) {
        function v(a, p) {
            var b = a.children;
            if (b && (n = b.length)) {
                var d, f = null,
                    i = -1,
                    n;
                while (++i < n) {
                    d = b[i];
                    v(d, f);
                    f = d
                }
            }
            c(a, p)
        }
        v(a, null)
    }
    function R5(n) {
        var s = 0,
            c = 0,
            a = n.children,
            i = a.length,
            b;
        while (--i >= 0) {
            b = a[i]._tree;
            b.prelim += s;
            b.mod += s;
            s += b.shift + (c += b.change)
        }
    }
    function S5(a, n, s) {
        a = a._tree;
        n = n._tree;
        var c = s / (n.number - a.number);
        a.change += c;
        n.change -= c;
        n.shift += s;
        n.prelim += s;
        n.mod += s
    }
    function T5(v, n, a) {
        return v._tree.ancestor.parent == n.parent ? v._tree.ancestor : a
    }
    d3.layout.treemap = function() {
        var h = d3.layout.hierarchy(),
            a = Math.round,
            b = [1, 1],
            c = null,
            f = U5,
            g = false,
            j, l = 0.5 * (1 + Math.sqrt(5));

        function m(d, k) {
            var i = -1,
                n = d.length,
                o, p;
            while (++i < n) {
                p = (o = d[i]).value * (k < 0 ? 0 : k);
                o.area = isNaN(p) || p <= 0 ? 0 : p
            }
        }
        function q(d) {
            var i = d.children;
            if (i && i.length) {
                var r = f(d),
                    k = [],
                    o = i.slice(),
                    p, s = Infinity,
                    v, u = Math.min(r.dx, r.dy),
                    n;
                m(o, r.dx * r.dy / d.value);
                k.area = 0;
                while ((n = o.length) > 0) {
                    k.push(p = o[n - 1]);
                    k.area += p.area;
                    if ((v = w(k, u)) <= s) {
                        o.pop();
                        s = v
                    } else {
                        k.area -= k.pop().area;
                        z(k, u, r, false);
                        u = Math.min(r.dx, r.dy);
                        k.length = k.area = 0;
                        s = Infinity
                    }
                }
                if (k.length) {
                    z(k, u, r, true);
                    k.length = k.area = 0
                }
                i.forEach(q)
            }
        }
        function t(n) {
            var d = n.children;
            if (d && d.length) {
                var r = f(n),
                    i = d.slice(),
                    k, o = [];
                m(i, r.dx * r.dy / n.value);
                o.area = 0;
                while (k = i.pop()) {
                    o.push(k);
                    o.area += k.area;
                    if (k.z != null) {
                        z(o, k.z ? r.dx : r.dy, r, !i.length);
                        o.length = o.area = 0
                    }
                }
                d.forEach(t)
            }
        }
        function w(d, u) {
            var s = d.area,
                r, k = 0,
                o = Infinity,
                i = -1,
                n = d.length;
            while (++i < n) {
                if (!(r = d[i].area)) continue;
                if (r < o) o = r;
                if (r > k) k = r
            }
            s *= s;
            u *= u;
            return s ? Math.max((u * k * l) / s, s / (u * o * l)) : Infinity
        }
        function z(r, u, d, k) {
            var i = -1,
                n = r.length,
                x = d.x,
                y = d.y,
                v = u ? a(r.area / u) : 0,
                o;
            if (u == d.dx) {
                if (k || v > d.dy) v = d.dy;
                while (++i < n) {
                    o = r[i];
                    o.x = x;
                    o.y = y;
                    o.dy = v;
                    x += o.dx = Math.min(d.x + d.dx - x, v ? a(o.area / v) : 0)
                }
                o.z = true;
                o.dx += d.x + d.dx - x;
                d.y += v;
                d.dy -= v
            } else {
                if (k || v > d.dx) v = d.dx;
                while (++i < n) {
                    o = r[i];
                    o.x = x;
                    o.y = y;
                    o.dx = v;
                    y += o.dy = Math.min(d.y + d.dy - y, v ? a(o.area / v) : 0)
                }
                o.z = false;
                o.dy += d.y + d.dy - y;
                d.x += v;
                d.dx -= v
            }
        }
        function A(d) {
            var n = j || h(d),
                r = n[0];
            r.x = 0;
            r.y = 0;
            r.dx = b[0];
            r.dy = b[1];
            if (j) h.revalue(r);
            m([r], r.dx * r.dy / r.value);
            (j ? t : q)(r);
            if (g) j = n;
            return n
        }
        A.size = function(x) {
            if (!arguments.length) return b;
            b = x;
            return A
        };
        A.padding = function(x) {
            if (!arguments.length) return c;

            function d(n) {
                var p = x.call(A, n, n.depth);
                return p == null ? U5(n) : V5(n, typeof p === "number" ? [p, p, p, p] : p)
            }
            function i(n) {
                return V5(n, x)
            }
            var k;
            f = (c = x) == null ? U5 : (k = typeof x) === "function" ? d : k === "number" ? (x = [x, x, x, x], i) : i;
            return A
        };
        A.round = function(x) {
            if (!arguments.length) return a != Number;
            a = x ? Math.round : Number;
            return A
        };
        A.sticky = function(x) {
            if (!arguments.length) return g;
            g = x;
            j = null;
            return A
        };
        A.ratio = function(x) {
            if (!arguments.length) return l;
            l = x;
            return A
        };
        return p5(A, h)
    };

    function U5(n) {
        return {
            x: n.x,
            y: n.y,
            dx: n.dx,
            dy: n.dy
        }
    }
    function V5(n, p) {
        var x = n.x + p[3],
            y = n.y + p[0],
            d = n.dx - p[1] - p[3],
            a = n.dy - p[0] - p[2];
        if (d < 0) {
            x += d / 2;
            d = 0
        }
        if (a < 0) {
            y += a / 2;
            a = 0
        }
        return {
            x: x,
            y: y,
            dx: d,
            dy: a
        }
    }
    d3.csv = function(u, c) {
        d3.text(u, "text/csv", function(t) {
            c(t && d3.csv.parse(t))
        })
    };
    d3.csv.parse = function(t) {
        var h;
        return d3.csv.parseRows(t, function(r, i) {
            if (i) {
                var o = {}, j = -1,
                    m = h.length;
                while (++j < m) o[h[j]] = r[j];
                return o
            } else {
                h = r;
                return null
            }
        })
    };
    d3.csv.parseRows = function(b, f) {
        var E = {}, d = {}, r = [],
            g = /\r\n|[,\r\n]/g,
            n = 0,
            t, h;
        g.lastIndex = 0;

        function k() {
            if (g.lastIndex >= b.length) return d;
            if (h) {
                h = false;
                return E
            }
            var j = g.lastIndex;
            if (b.charCodeAt(j) === 34) {
                var i = j;
                while (i++ < b.length) {
                    if (b.charCodeAt(i) === 34) {
                        if (b.charCodeAt(i + 1) !== 34) break;
                        i++
                    }
                }
                g.lastIndex = i + 2;
                var c = b.charCodeAt(i + 1);
                if (c === 13) {
                    h = true;
                    if (b.charCodeAt(i + 2) === 10) g.lastIndex++
                } else if (c === 10) {
                    h = true
                }
                return b.substring(j + 1, i).replace(/""/g, "\"")
            }
            var m = g.exec(b);
            if (m) {
                h = m[0].charCodeAt(0) !== 44;
                return b.substring(j, m.index)
            }
            g.lastIndex = b.length;
            return b.substring(j)
        }
        while ((t = k()) !== d) {
            var a = [];
            while ((t !== E) && (t !== d)) {
                a.push(t);
                t = k()
            }
            if (f && !(a = f(a, n++))) continue;
            r.push(a)
        }
        return r
    };
    d3.csv.format = function(r) {
        return r.map(W5).join("\n")
    };

    function W5(r) {
        return r.map(X5).join(",")
    }
    function X5(t) {
        return /[",\n]/.test(t) ? "\"" + t.replace(/\"/g, "\"\"") + "\"" : t
    }
    d3.geo = {};
    var Y5 = Math.PI / 180;
    d3.geo.azimuthal = function() {
        var m = "orthographic",
            o, s = 200,
            t = [480, 250],
            a, b, d, f;

        function g(h) {
            var i = h[0] * Y5 - a,
                j = h[1] * Y5,
                l = Math.cos(i),
                n = Math.sin(i),
                p = Math.cos(j),
                q = Math.sin(j),
                r = m !== "orthographic" ? f * q + d * p * l : null,
                c, k = m === "stereographic" ? 1 / (1 + r) : m === "gnomonic" ? 1 / r : m === "equidistant" ? (c = Math.acos(r), c ? c / Math.sin(c) : 0) : m === "equalarea" ? Math.sqrt(2 / (1 + r)) : 1,
                x = k * p * n,
                y = k * (f * p * l - d * q);
            return [s * x + t[0], s * y + t[1]]
        }
        g.invert = function(h) {
            var x = (h[0] - t[0]) / s,
                y = (h[1] - t[1]) / s,
                p = Math.sqrt(x * x + y * y),
                c = m === "stereographic" ? 2 * Math.atan(p) : m === "gnomonic" ? Math.atan(p) : m === "equidistant" ? p : m === "equalarea" ? 2 * Math.asin(.5 * p) : Math.asin(p),
                i = Math.sin(c),
                j = Math.cos(c);
            return [(a + Math.atan2(x * i, p * d * j + y * f * i)) / Y5, Math.asin(j * f - (p ? (y * i * d) / p : 0)) / Y5]
        };
        g.mode = function(x) {
            if (!arguments.length) return m;
            m = x + "";
            return g
        };
        g.origin = function(x) {
            if (!arguments.length) return o;
            o = x;
            a = o[0] * Y5;
            b = o[1] * Y5;
            d = Math.cos(b);
            f = Math.sin(b);
            return g
        };
        g.scale = function(x) {
            if (!arguments.length) return s;
            s = +x;
            return g
        };
        g.translate = function(x) {
            if (!arguments.length) return t;
            t = [+x[0], +x[1]];
            return g
        };
        return g.origin([0, 0])
    };
    d3.geo.albers = function() {
        var o = [-98, 38],
            a = [29.5, 45.5],
            b = 1000,
            d = [480, 250],
            l, n, C, f;

        function g(c) {
            var t = n * (Y5 * c[0] - l),
                p = Math.sqrt(C - 2 * n * Math.sin(Y5 * c[1])) / n;
            return [b * p * Math.sin(t) + d[0], b * (p * Math.cos(t) - f) + d[1]]
        }
        g.invert = function(c) {
            var x = (c[0] - d[0]) / b,
                y = (c[1] - d[1]) / b,
                h = f + y,
                t = Math.atan2(x, h),
                p = Math.sqrt(x * x + h * h);
            return [(l + t / n) / Y5, Math.asin((C - p * p * n * n) / (2 * n)) / Y5]
        };

        function r() {
            var p = Y5 * a[0],
                h = Y5 * a[1],
                i = Y5 * o[1],
                s = Math.sin(p),
                c = Math.cos(p);
            l = Y5 * o[0];
            n = .5 * (s + Math.sin(h));
            C = c * c + 2 * n * s;
            f = Math.sqrt(C - 2 * n * Math.sin(i)) / n;
            return g
        }
        g.origin = function(x) {
            if (!arguments.length) return o;
            o = [+x[0], +x[1]];
            return r()
        };
        g.parallels = function(x) {
            if (!arguments.length) return a;
            a = [+x[0], +x[1]];
            return r()
        };
        g.scale = function(x) {
            if (!arguments.length) return b;
            b = +x;
            return g
        };
        g.translate = function(x) {
            if (!arguments.length) return d;
            d = [+x[0], +x[1]];
            return g
        };
        return r()
    };
    d3.geo.albersUsa = function() {
        var l = d3.geo.albers();
        var a = d3.geo.albers().origin([-160, 60]).parallels([55, 65]);
        var h = d3.geo.albers().origin([-160, 20]).parallels([8, 18]);
        var p = d3.geo.albers().origin([-60, 10]).parallels([8, 18]);

        function b(c) {
            var d = c[0],
                f = c[1];
            return (f > 50 ? a : d < -140 ? h : f < 21 ? p : l)(c)
        }
        b.scale = function(x) {
            if (!arguments.length) return l.scale();
            l.scale(x);
            a.scale(x * .6);
            h.scale(x);
            p.scale(x * 1.5);
            return b.translate(l.translate())
        };
        b.translate = function(x) {
            if (!arguments.length) return l.translate();
            var d = l.scale() / 1000,
                c = x[0],
                f = x[1];
            l.translate(x);
            a.translate([c - 400 * d, f + 170 * d]);
            h.translate([c - 190 * d, f + 200 * d]);
            p.translate([c + 580 * d, f + 430 * d]);
            return b
        };
        return b.scale(l.scale())
    };
    d3.geo.bonne = function() {
        var s = 200,
            t = [480, 250],
            a, b, d, f;

        function g(c) {
            var x = c[0] * Y5 - a,
                y = c[1] * Y5 - b;
            if (d) {
                var p = f + d - y,
                    E = x * Math.cos(y) / p;
                x = p * Math.sin(E);
                y = p * Math.cos(E) - f
            } else {
                x *= Math.cos(y);
                y *= -1
            }
            return [s * x + t[0], s * y + t[1]]
        }
        g.invert = function(h) {
            var x = (h[0] - t[0]) / s,
                y = (h[1] - t[1]) / s;
            if (d) {
                var c = f + y,
                    p = Math.sqrt(x * x + c * c);
                y = f + d - p;
                x = a + p * Math.atan2(x, c) / Math.cos(y)
            } else {
                y *= -1;
                x /= Math.cos(y)
            }
            return [x / Y5, y / Y5]
        };
        g.parallel = function(x) {
            if (!arguments.length) return d / Y5;
            f = 1 / Math.tan(d = x * Y5);
            return g
        };
        g.origin = function(x) {
            if (!arguments.length) return [a / Y5, b / Y5];
            a = x[0] * Y5;
            b = x[1] * Y5;
            return g
        };
        g.scale = function(x) {
            if (!arguments.length) return s;
            s = +x;
            return g
        };
        g.translate = function(x) {
            if (!arguments.length) return t;
            t = [+x[0], +x[1]];
            return g
        };
        return g.origin([0, 0]).parallel(45)
    };
    d3.geo.equirectangular = function() {
        var s = 500,
            t = [480, 250];

        function a(c) {
            var x = c[0] / 360,
                y = -c[1] / 360;
            return [s * x + t[0], s * y + t[1]]
        }
        a.invert = function(c) {
            var x = (c[0] - t[0]) / s,
                y = (c[1] - t[1]) / s;
            return [360 * x, -360 * y]
        };
        a.scale = function(x) {
            if (!arguments.length) return s;
            s = +x;
            return a
        };
        a.translate = function(x) {
            if (!arguments.length) return t;
            t = [+x[0], +x[1]];
            return a
        };
        return a
    };
    d3.geo.mercator = function() {
        var s = 500,
            t = [480, 250];

        function m(c) {
            var x = c[0] / 360,
                y = -(Math.log(Math.tan(Math.PI / 4 + c[1] * Y5 / 2)) / Y5) / 360;
            return [s * x + t[0], s * Math.max(-.5, Math.min(.5, y)) + t[1]]
        }
        m.invert = function(c) {
            var x = (c[0] - t[0]) / s,
                y = (c[1] - t[1]) / s;
            return [360 * x, 2 * Math.atan(Math.exp(-360 * y * Y5)) / Y5 - 90]
        };
        m.scale = function(x) {
            if (!arguments.length) return s;
            s = +x;
            return m
        };
        m.translate = function(x) {
            if (!arguments.length) return t;
            t = [+x[0], +x[1]];
            return m
        };
        return m
    };

    function Z5(t, d) {
        return function(o) {
            return o && t.hasOwnProperty(o.type) ? t[o.type](o) : d
        }
    }
    d3.geo.path = function() {
        var a = 4.5,
            b = $5(a),
            c = d3.geo.albersUsa(),
            f = [];

        function g(d, i) {
            if (typeof a === "function") b = $5(a.apply(this, arguments));
            l(d);
            var j = f.length ? f.join("") : null;
            f = [];
            return j
        }
        function h(d) {
            return c(d).join(",")
        }
        var l = Z5({
            FeatureCollection: function(o) {
                var d = o.features,
                    i = -1,
                    n = d.length;
                while (++i < n) f.push(l(d[i].geometry))
            },
            Feature: function(o) {
                l(o.geometry)
            },
            Point: function(o) {
                f.push("M", h(o.coordinates), b)
            },
            MultiPoint: function(o) {
                var d = o.coordinates,
                    i = -1,
                    n = d.length;
                while (++i < n) f.push("M", h(d[i]), b)
            },
            LineString: function(o) {
                var d = o.coordinates,
                    i = -1,
                    n = d.length;
                f.push("M");
                while (++i < n) f.push(h(d[i]), "L");
                f.pop()
            },
            MultiLineString: function(o) {
                var d = o.coordinates,
                    i = -1,
                    n = d.length,
                    k, j, m;
                while (++i < n) {
                    k = d[i];
                    j = -1;
                    m = k.length;
                    f.push("M");
                    while (++j < m) f.push(h(k[j]), "L");
                    f.pop()
                }
            },
            Polygon: function(o) {
                var d = o.coordinates,
                    i = -1,
                    n = d.length,
                    k, j, m;
                while (++i < n) {
                    k = d[i];
                    j = -1;
                    if ((m = k.length - 1) > 0) {
                        f.push("M");
                        while (++j < m) f.push(h(k[j]), "L");
                        f[f.length - 1] = "Z"
                    }
                }
            },
            MultiPolygon: function(o) {
                var d = o.coordinates,
                    i = -1,
                    n = d.length,
                    v, j, m, w, k, p;
                while (++i < n) {
                    v = d[i];
                    j = -1;
                    m = v.length;
                    while (++j < m) {
                        w = v[j];
                        k = -1;
                        if ((p = w.length - 1) > 0) {
                            f.push("M");
                            while (++k < p) f.push(h(w[k]), "L");
                            f[f.length - 1] = "Z"
                        }
                    }
                }
            },
            GeometryCollection: function(o) {
                var d = o.geometries,
                    i = -1,
                    n = d.length;
                while (++i < n) f.push(l(d[i]))
            }
        });
        var q = g.area = Z5({
            FeatureCollection: function(o) {
                var u = 0,
                    d = o.features,
                    i = -1,
                    n = d.length;
                while (++i < n) u += q(d[i]);
                return u
            },
            Feature: function(o) {
                return q(o.geometry)
            },
            Polygon: function(o) {
                return r(o.coordinates)
            },
            MultiPolygon: function(o) {
                var d = 0,
                    j = o.coordinates,
                    i = -1,
                    n = j.length;
                while (++i < n) d += r(j[i]);
                return d
            },
            GeometryCollection: function(o) {
                var d = 0,
                    j = o.geometries,
                    i = -1,
                    n = j.length;
                while (++i < n) d += q(j[i]);
                return d
            }
        }, 0);

        function r(d) {
            var j = u(d[0]),
                i = 0,
                n = d.length;
            while (++i < n) j -= u(d[i]);
            return j
        }
        function s(d) {
            var p = d3.geom.polygon(d[0].map(c)),
                u = p.area(),
                j = p.centroid(u < 0 ? (u *= -1, 1) : -1),
                x = j[0],
                y = j[1],
                z = u,
                i = 0,
                n = d.length;
            while (++i < n) {
                p = d3.geom.polygon(d[i].map(c));
                u = p.area();
                j = p.centroid(u < 0 ? (u *= -1, 1) : -1);
                x -= j[0];
                y -= j[1];
                z -= u
            }
            return [x, y, 6 * z]
        }
        var t = g.centroid = Z5({
            Feature: function(o) {
                return t(o.geometry)
            },
            Polygon: function(o) {
                var d = s(o.coordinates);
                return [d[0] / d[2], d[1] / d[2]]
            },
            MultiPolygon: function(o) {
                var u = 0,
                    d = o.coordinates,
                    j, x = 0,
                    y = 0,
                    z = 0,
                    i = -1,
                    n = d.length;
                while (++i < n) {
                    j = s(d[i]);
                    x += j[0];
                    y += j[1];
                    z += j[2]
                }
                return [x / z, y / z]
            }
        });

        function u(d) {
            return Math.abs(d3.geom.polygon(d.map(c)).area())
        }
        g.projection = function(x) {
            c = x;
            return g
        };
        g.pointRadius = function(x) {
            if (typeof x === "function") a = x;
            else {
                a = +x;
                b = $5(a)
            }
            return g
        };
        return g
    };

    function $5(r) {
        return "m0," + r + "a" + r + "," + r + " 0 1,1 0," + (-2 * r) + "a" + r + "," + r + " 0 1,1 0," + (+2 * r) + "z"
    }
    d3.geo.bounds = function(f) {
        var l = Infinity,
            b = Infinity,
            r = -Infinity,
            t = -Infinity;
        _5(f, function(x, y) {
            if (x < l) l = x;
            if (x > r) r = x;
            if (y < b) b = y;
            if (y > t) t = y
        });
        return [[l, b], [r, t]]
    };

    function _5(o, f) {
        if (a6.hasOwnProperty(o.type)) a6[o.type](o, f)
    }
    var a6 = {
        Feature: b6,
        FeatureCollection: c6,
        GeometryCollection: d6,
        LineString: e6,
        MultiLineString: f6,
        MultiPoint: e6,
        MultiPolygon: g6,
        Point: h6,
        Polygon: i6
    };

    function b6(o, f) {
        _5(o.geometry, f)
    }
    function c6(o, f) {
        for (var a = o.features, i = 0, n = a.length; i < n; i++) {
            _5(a[i].geometry, f)
        }
    }
    function d6(o, f) {
        for (var a = o.geometries, i = 0, n = a.length; i < n; i++) {
            _5(a[i], f)
        }
    }
    function e6(o, f) {
        for (var a = o.coordinates, i = 0, n = a.length; i < n; i++) {
            f.apply(null, a[i])
        }
    }
    function f6(o, f) {
        for (var a = o.coordinates, i = 0, n = a.length; i < n; i++) {
            for (var b = a[i], j = 0, m = b.length; j < m; j++) {
                f.apply(null, b[j])
            }
        }
    }
    function g6(o, f) {
        for (var a = o.coordinates, i = 0, n = a.length; i < n; i++) {
            for (var b = a[i][0], j = 0, m = b.length; j < m; j++) {
                f.apply(null, b[j])
            }
        }
    }
    function h6(o, f) {
        f.apply(null, o.coordinates)
    }
    function i6(o, f) {
        for (var a = o.coordinates[0], i = 0, n = a.length; i < n; i++) {
            f.apply(null, a[i])
        }
    }
    d3.geo.circle = function() {
        var a = [0, 0],
            b = 90 - 1e-2,
            r = b * Y5,
            c = d3.geo.greatArc().source(a).target(R);

        function f() {}
        function v(p) {
            return c.distance(p) < r
        }
        f.clip = function(d) {
            if (typeof a === "function") c.source(a.apply(this, arguments));
            return g(d) || null
        };
        var g = Z5({
            FeatureCollection: function(o) {
                var d = o.features.map(g).filter(R);
                return d && (o = Object.create(o), o.features = d, o)
            },
            Feature: function(o) {
                var d = g(o.geometry);
                return d && (o = Object.create(o), o.geometry = d, o)
            },
            Point: function(o) {
                return v(o.coordinates) && o
            },
            MultiPoint: function(o) {
                var d = o.coordinates.filter(v);
                return d.length && {
                    type: o.type,
                    coordinates: d
                }
            },
            LineString: function(o) {
                var d = h(o.coordinates);
                return d.length && (o = Object.create(o), o.coordinates = d, o)
            },
            MultiLineString: function(o) {
                var i = o.coordinates.map(h).filter(function(d) {
                    return d.length
                });
                return i.length && (o = Object.create(o), o.coordinates = i, o)
            },
            Polygon: function(o) {
                var d = o.coordinates.map(h);
                return d[0].length && (o = Object.create(o), o.coordinates = d, o)
            },
            MultiPolygon: function(o) {
                var i = o.coordinates.map(function(d) {
                    return d.map(h)
                }).filter(function(d) {
                    return d[0].length
                });
                return i.length && (o = Object.create(o), o.coordinates = i, o)
            },
            GeometryCollection: function(o) {
                var d = o.geometries.map(g).filter(R);
                return d.length && (o = Object.create(o), o.geometries = d, o)
            }
        });

        function h(d) {
            var i = -1,
                n = d.length,
                j = [],
                p, l, m, o, q;
            while (++i < n) {
                q = c.distance(m = d[i]);
                if (q < r) {
                    if (l) j.push(m6(l, m)((o - r) / (o - q)));
                    j.push(m);
                    p = l = null
                } else {
                    l = m;
                    if (!p && j.length) {
                        j.push(m6(j[j.length - 1], l)((r - o) / (q - o)));
                        p = l
                    }
                }
                o = q
            }
            p = d[0];
            l = j[0];
            if (l && m[0] === p[0] && m[1] === p[1] && !(m[0] === l[0] && m[1] === l[1])) {
                j.push(l)
            }
            return k(j)
        }
        function k(d) {
            var i = 0,
                n = d.length,
                j, m, l = n ? [d[0]] : d,
                o, a = c.source();
            while (++i < n) {
                o = c.source(d[i - 1])(d[i]).coordinates;
                for (j = 0, m = o.length; ++j < m;) l.push(o[j])
            }
            c.source(a);
            return l
        }
        f.origin = function(x) {
            if (!arguments.length) return a;
            a = x;
            if (typeof a !== "function") c.source(a);
            return f
        };
        f.angle = function(x) {
            if (!arguments.length) return b;
            r = (b = +x) * Y5;
            return f
        };
        return d3.rebind(f, c, "precision")
    };
    d3.geo.greatArc = function() {
        var s = j6,
            p, a = k6,
            b, c = 6 * Y5,
            i = l6();

        function g() {
            var d = g.distance.apply(this, arguments),
                t = 0,
                f = c / d,
                h = [p];
            while ((t += f) < 1) h.push(i(t));
            h.push(b);
            return {
                type: "LineString",
                coordinates: h
            }
        }
        g.distance = function() {
            if (typeof s === "function") i.source(p = s.apply(this, arguments));
            if (typeof a === "function") i.target(b = a.apply(this, arguments));
            return i.distance()
        };
        g.source = function(_) {
            if (!arguments.length) return s;
            s = _;
            if (typeof s !== "function") i.source(p = s);
            return g
        };
        g.target = function(_) {
            if (!arguments.length) return a;
            a = _;
            if (typeof a !== "function") i.target(b = a);
            return g
        };
        g.precision = function(_) {
            if (!arguments.length) return c / Y5;
            c = _ * Y5;
            return g
        };
        return g
    };

    function j6(d) {
        return d.source
    }
    function k6(d) {
        return d.target
    }
    function l6() {
        var a, b, c, s, f, g, h, i, j, l, m, n, d, k;

        function o(t) {
            var B = Math.sin(t *= d) * k,
                A = Math.sin(d - t) * k,
                x = A * f + B * m,
                y = A * g + B * n,
                z = A * s + B * l;
            return [Math.atan2(y, x) / Y5, Math.atan2(z, Math.sqrt(x * x + y * y)) / Y5]
        }
        o.distance = function() {
            if (d == null) k = 1 / Math.sin(d = Math.acos(Math.max(-1, Math.min(1, s * l + c * j * Math.cos(h - a)))));
            return d
        };
        o.source = function(_) {
            var p = Math.cos(a = _[0] * Y5),
                q = Math.sin(a);
            c = Math.cos(b = _[1] * Y5);
            s = Math.sin(b);
            f = c * p;
            g = c * q;
            d = null;
            return o
        };
        o.target = function(_) {
            var p = Math.cos(h = _[0] * Y5),
                q = Math.sin(h);
            j = Math.cos(i = _[1] * Y5);
            l = Math.sin(i);
            m = j * p;
            n = j * q;
            d = null;
            return o
        };
        return o
    }
    function m6(a, b) {
        var i = l6().source(a).target(b);
        i.distance();
        return i
    }
    d3.geo.greatCircle = d3.geo.circle;
    d3.geom = {};
    d3.geom.contour = function(g, a) {
        var s = a || p6(g),
            c = [],
            x = s[0],
            y = s[1],
            d = 0,
            b = 0,
            p = NaN,
            f = NaN,
            i = 0;
        do {
            i = 0;
            if (g(x - 1, y - 1)) i += 1;
            if (g(x, y - 1)) i += 2;
            if (g(x - 1, y)) i += 4;
            if (g(x, y)) i += 8;
            if (i === 6) {
                d = f === -1 ? -1 : 1;
                b = 0
            } else if (i === 9) {
                d = 0;
                b = p === 1 ? -1 : 1
            } else {
                d = n6[i];
                b = o6[i]
            }
            if (d != p && b != f) {
                c.push([x, y]);
                p = d;
                f = b
            }
            x += d;
            y += b
        } while (s[0] != x || s[1] != y);
        return c
    };
    var n6 = [1, 0, 1, 1, -1, 0, -1, 1, 0, 0, 0, 0, -1, 0, -1, NaN],
        o6 = [0, -1, 0, 0, 0, -1, 0, 0, 1, -1, 1, 1, 0, -1, 0, NaN];

    function p6(g) {
        var x = 0,
            y = 0;
        while (true) {
            if (g(x, y)) {
                return [x, y]
            }
            if (x === 0) {
                x = y + 1;
                y = 0
            } else {
                x = x - 1;
                y = y + 1
            }
        }
    }
    d3.geom.hull = function(c) {
        if (c.length < 3) return [];
        var l = c.length,
            p = l - 1,
            d = [],
            s = [],
            i, j, h = 0,
            x, y, f, g, u, v, a, k;
        for (i = 1; i < l; ++i) {
            if (c[i][1] < c[h][1]) {
                h = i
            } else if (c[i][1] == c[h][1]) {
                h = (c[i][0] < c[h][0] ? i : h)
            }
        }
        for (i = 0; i < l; ++i) {
            if (i === h) continue;
            y = c[i][1] - c[h][1];
            x = c[i][0] - c[h][0];
            d.push({
                angle: Math.atan2(y, x),
                index: i
            })
        }
        d.sort(function(a, b) {
            return a.angle - b.angle
        });
        a = d[0].angle;
        v = d[0].index;
        u = 0;
        for (i = 1; i < p; ++i) {
            j = d[i].index;
            if (a == d[i].angle) {
                x = c[v][0] - c[h][0];
                y = c[v][1] - c[h][1];
                f = c[j][0] - c[h][0];
                g = c[j][1] - c[h][1];
                if ((x * x + y * y) >= (f * f + g * g)) {
                    d[i].index = -1
                } else {
                    d[u].index = -1;
                    a = d[i].angle;
                    u = i;
                    v = j
                }
            } else {
                a = d[i].angle;
                u = i;
                v = j
            }
        }
        s.push(h);
        for (i = 0, j = 0; i < 2; ++j) {
            if (d[j].index !== -1) {
                s.push(d[j].index);
                i++
            }
        }
        k = s.length;
        for (; j < p; ++j) {
            if (d[j].index === -1) continue;
            while (!q6(s[k - 2], s[k - 1], d[j].index, c)) {
                --k
            }
            s[k++] = d[j].index
        }
        var m = [];
        for (i = 0; i < k; ++i) {
            m.push(c[s[i]])
        }
        return m
    };

    function q6(i, g, h, v) {
        var t, a, b, c, d, e, f;
        t = v[i];
        a = t[0];
        b = t[1];
        t = v[g];
        c = t[0];
        d = t[1];
        t = v[h];
        e = t[0];
        f = t[1];
        return ((f - b) * (c - a) - (d - b) * (e - a)) > 0
    }
    d3.geom.polygon = function(f) {
        f.area = function() {
            var i = 0,
                n = f.length,
                a = f[n - 1][0] * f[0][1],
                b = f[n - 1][1] * f[0][0];
            while (++i < n) {
                a += f[i - 1][0] * f[i][1];
                b += f[i - 1][1] * f[i][0]
            }
            return (b - a) * .5
        };
        f.centroid = function(k) {
            var i = -1,
                n = f.length,
                x = 0,
                y = 0,
                a, b = f[n - 1],
                c;
            if (!arguments.length) k = -1 / (6 * f.area());
            while (++i < n) {
                a = b;
                b = f[i];
                c = a[0] * b[1] - b[0] * a[1];
                x += (a[0] + b[0]) * c;
                y += (a[1] + b[1]) * c
            }
            return [x * k, y * k]
        };
        f.clip = function(s) {
            var g, i = -1,
                n = f.length,
                j, m, a = f[n - 1],
                b, c, d;
            while (++i < n) {
                g = s.slice();
                s.length = 0;
                b = f[i];
                c = g[(m = g.length) - 1];
                j = -1;
                while (++j < m) {
                    d = g[j];
                    if (r6(d, a, b)) {
                        if (!r6(c, a, b)) {
                            s.push(s6(c, d, a, b))
                        }
                        s.push(d)
                    } else if (r6(c, a, b)) {
                        s.push(s6(c, d, a, b))
                    }
                    c = d
                }
                a = b
            }
            return s
        };
        return f
    };

    function r6(p, a, b) {
        return (b[0] - a[0]) * (p[1] - a[1]) < (b[1] - a[1]) * (p[0] - a[0])
    }
    function s6(c, d, a, b) {
        var x = c[0],
            f = d[0],
            g = a[0],
            h = b[0],
            y = c[1],
            i = d[1],
            j = a[1],
            k = b[1],
            l = x - g,
            m = f - x,
            n = h - g,
            o = y - j,
            p = i - y,
            q = k - j,
            u = (n * o - q * l) / (q * m - n * p);
        return [x + u * m, y + u * p]
    }

    // See lib/jit/LICENSE for details.

    d3.geom.voronoi = function(c) {
        var p = c.map(function() {
            return []
        });
        u6(c, function(e) {
            var s, a, x, b, y, d;
            if (e.a === 1 && e.b >= 0) {
                s = e.ep.r;
                a = e.ep.l
            } else {
                s = e.ep.l;
                a = e.ep.r
            }
            if (e.a === 1) {
                y = s ? s.y : -1e6;
                x = e.c - e.b * y;
                d = a ? a.y : 1e6;
                b = e.c - e.b * d
            } else {
                x = s ? s.x : -1e6;
                y = e.c - e.a * x;
                b = a ? a.x : 1e6;
                d = e.c - e.a * b
            }
            var v = [x, y],
                f = [b, d];
            p[e.region.l.index].push(v, f);
            p[e.region.r.index].push(v, f)
        });
        return p.map(function(f, i) {
            var g = c[i][0],
                h = c[i][1];
            f.forEach(function(v) {
                v.angle = Math.atan2(v[0] - g, v[1] - h)
            });
            return f.sort(function(a, b) {
                return a.angle - b.angle
            }).filter(function(d, i) {
                return !i || (d.angle - f[i - 1].angle > 1e-10)
            })
        })
    };
    var t6 = {
        "l": "r",
        "r": "l"
    };

    function u6(c, f) {
        var g = {
            list: c.map(function(v, i) {
                return {
                    index: i,
                    x: v[0],
                    y: v[1]
                }
            }).sort(function(a, b) {
                return a.y < b.y ? -1 : a.y > b.y ? 1 : a.x < b.x ? -1 : a.x > b.x ? 1 : 0
            }),
            bottomSite: null
        };
        var E = {
            list: [],
            leftEnd: null,
            rightEnd: null,
            init: function() {
                E.leftEnd = E.createHalfEdge(null, "l");
                E.rightEnd = E.createHalfEdge(null, "l");
                E.leftEnd.r = E.rightEnd;
                E.rightEnd.l = E.leftEnd;
                E.list.unshift(E.leftEnd, E.rightEnd)
            },
            createHalfEdge: function(a, s) {
                return {
                    edge: a,
                    side: s,
                    vertex: null,
                    "l": null,
                    "r": null
                }
            },
            insert: function(l, a) {
                a.l = l;
                a.r = l.r;
                l.r.l = a;
                l.r = a
            },
            leftBound: function(p) {
                var a = E.leftEnd;
                do {
                    a = a.r
                } while (a != E.rightEnd && h.rightOf(a, p));
                a = a.l;
                return a
            },
            del: function(a) {
                a.l.r = a.r;
                a.r.l = a.l;
                a.edge = null
            },
            right: function(a) {
                return a.r
            },
            left: function(a) {
                return a.l
            },
            leftRegion: function(a) {
                return a.edge == null ? g.bottomSite : a.edge.region[a.side]
            },
            rightRegion: function(a) {
                return a.edge == null ? g.bottomSite : a.edge.region[t6[a.side]]
            }
        };
        var h = {
            bisect: function(s, a) {
                var b = {
                    region: {
                        "l": s,
                        "r": a
                    },
                    ep: {
                        "l": null,
                        "r": null
                    }
                };
                var d = a.x - s.x,
                    i = a.y - s.y,
                    l = d > 0 ? d : -d,
                    t = i > 0 ? i : -i;
                b.c = s.x * d + s.y * i + (d * d + i * i) * .5;
                if (l > t) {
                    b.a = 1;
                    b.b = i / d;
                    b.c /= d
                } else {
                    b.b = 1;
                    b.a = d / i;
                    b.c /= i
                }
                return b
            },
            intersect: function(a, b) {
                var i = a.edge,
                    l = b.edge;
                if (!i || !l || (i.region.r == l.region.r)) {
                    return null
                }
                var d = (i.a * l.b) - (i.b * l.a);
                if (Math.abs(d) < 1e-10) {
                    return null
                }
                var s = (i.c * l.b - l.c * i.b) / d,
                    t = (l.c * i.a - i.c * l.a) / d,
                    A = i.region.r,
                    B = l.region.r,
                    C, e;
                if ((A.y < B.y) || (A.y == B.y && A.x < B.x)) {
                    C = a;
                    e = i
                } else {
                    C = b;
                    e = l
                }
                var H = (s >= e.region.r.x);
                if ((H && (C.side === "l")) || (!H && (C.side === "r"))) {
                    return null
                }
                return {
                    x: s,
                    y: t
                }
            },
            rightOf: function(a, p) {
                var e = a.edge,
                    t = e.region.r,
                    b = (p.x > t.x);
                if (b && (a.side === "l")) {
                    return 1
                }
                if (!b && (a.side === "r")) {
                    return 0
                }
                if (e.a === 1) {
                    var d = p.y - t.y,
                        i = p.x - t.x,
                        l = 0,
                        s = 0;
                    if ((!b && (e.b < 0)) || (b && (e.b >= 0))) {
                        s = l = (d >= e.b * i)
                    } else {
                        s = ((p.x + p.y * e.b) > e.c);
                        if (e.b < 0) {
                            s = !s
                        }
                        if (!s) {
                            l = 1
                        }
                    }
                    if (!l) {
                        var A = t.x - e.region.l.x;
                        s = (e.b * (i * i - d * d)) < (A * d * (1 + 2 * i / A + e.b * e.b));
                        if (e.b < 0) {
                            s = !s
                        }
                    }
                } else {
                    var B = e.c - e.a * p.x,
                        C = p.y - B,
                        H = p.x - t.x,
                        L = B - t.y;
                    s = (C * C) > (H * H + L * L)
                }
                return a.side === "l" ? s : !s
            },
            endPoint: function(a, s, b) {
                a.ep[s] = b;
                if (!a.ep[t6[s]]) return;
                f(a)
            },
            distance: function(s, t) {
                var d = s.x - t.x,
                    a = s.y - t.y;
                return Math.sqrt(d * d + a * a)
            }
        };
        var j = {
            list: [],
            insert: function(a, s, b) {
                a.vertex = s;
                a.ystar = s.y + b;
                for (var i = 0, d = j.list, l = d.length; i < l; i++) {
                    var t = d[i];
                    if (a.ystar > t.ystar || (a.ystar == t.ystar && s.x > t.vertex.x)) {
                        continue
                    } else {
                        break
                    }
                }
                d.splice(i, 0, a)
            },
            del: function(a) {
                for (var i = 0, b = j.list, l = b.length; i < l && (b[i] != a); ++i) {}
                b.splice(i, 1)
            },
            empty: function() {
                return j.list.length === 0
            },
            nextEvent: function(a) {
                for (var i = 0, b = j.list, l = b.length; i < l; ++i) {
                    if (b[i] == a) return b[i + 1]
                }
                return null
            },
            min: function() {
                var a = j.list[0];
                return {
                    x: a.vertex.x,
                    y: a.ystar
                }
            },
            extractMin: function() {
                return j.list.shift()
            }
        };
        E.init();
        g.bottomSite = g.list.shift();
        var n = g.list.shift(),
            k;
        var m, r, o, q, u;
        var w, x, y, p, v;
        var e, z;
        while (true) {
            if (!j.empty()) {
                k = j.min()
            }
            if (n && (j.empty() || n.y < k.y || (n.y == k.y && n.x < k.x))) {
                m = E.leftBound(n);
                r = E.right(m);
                w = E.rightRegion(m);
                e = h.bisect(w, n);
                u = E.createHalfEdge(e, "l");
                E.insert(m, u);
                p = h.intersect(m, u);
                if (p) {
                    j.del(m);
                    j.insert(m, p, h.distance(p, n))
                }
                m = u;
                u = E.createHalfEdge(e, "r");
                E.insert(m, u);
                p = h.intersect(u, r);
                if (p) {
                    j.insert(u, p, h.distance(p, n))
                }
                n = g.list.shift()
            } else if (!j.empty()) {
                m = j.extractMin();
                o = E.left(m);
                r = E.right(m);
                q = E.right(r);
                w = E.leftRegion(m);
                x = E.rightRegion(r);
                v = m.vertex;
                h.endPoint(m.edge, m.side, v);
                h.endPoint(r.edge, r.side, v);
                E.del(m);
                j.del(r);
                E.del(r);
                z = "l";
                if (w.y > x.y) {
                    y = w;
                    w = x;
                    x = y;
                    z = "r"
                }
                e = h.bisect(w, x);
                u = E.createHalfEdge(e, z);
                E.insert(o, u);
                h.endPoint(e, t6[z], v);
                p = h.intersect(o, u);
                if (p) {
                    j.del(o);
                    j.insert(o, p, h.distance(p, w))
                }
                p = h.intersect(u, q);
                if (p) {
                    j.insert(u, p, h.distance(p, w))
                }
            } else {
                break
            }
        }
        for (m = E.right(E.leftEnd); m != E.rightEnd; m = E.right(m)) {
            f(m.edge)
        }
    }
    d3.geom.delaunay = function(c) {
        var d = c.map(function() {
            return []
        }),
            t = [];
        u6(c, function(e) {
            d[e.region.l.index].push(c[e.region.r.index])
        });
        d.forEach(function(f, i) {
            var v = c[i],
                g = v[0],
                h = v[1];
            f.forEach(function(v) {
                v.angle = Math.atan2(v[0] - g, v[1] - h)
            });
            f.sort(function(a, b) {
                return a.angle - b.angle
            });
            for (var j = 0, m = f.length - 1; j < m; j++) {
                t.push([v, f[j], f[j + 1]])
            }
        });
        return t
    };
    d3.geom.quadtree = function(a, x, y, b, c) {
        var p, i = -1,
            n = a.length;
        if (n && isNaN(a[0].x)) a = a.map(x6);
        if (arguments.length < 5) {
            if (arguments.length === 3) {
                c = b = y;
                y = x
            } else {
                x = y = Infinity;
                b = c = -Infinity;
                while (++i < n) {
                    p = a[i];
                    if (p.x < x) x = p.x;
                    if (p.y < y) y = p.y;
                    if (p.x > b) b = p.x;
                    if (p.y > c) c = p.y
                }
                var d = b - x,
                    g = c - y;
                if (d > g) c = y + d;
                else b = x + g
            }
        }
        function h(n, p, x, y, b, c) {
            if (isNaN(p.x) || isNaN(p.y)) return;
            if (n.leaf) {
                var v = n.point;
                if (v) {
                    if ((Math.abs(v.x - p.x) + Math.abs(v.y - p.y)) < .01) {
                        j(n, p, x, y, b, c)
                    } else {
                        n.point = null;
                        j(n, v, x, y, b, c);
                        j(n, p, x, y, b, c)
                    }
                } else {
                    n.point = p
                }
            } else {
                j(n, p, x, y, b, c)
            }
        }
        function j(n, p, x, y, b, c) {
            var s = (x + b) * .5,
                f = (y + c) * .5,
                k = p.x >= s,
                l = p.y >= f,
                i = (l << 1) + k;
            n.leaf = false;
            n = n.nodes[i] || (n.nodes[i] = v6());
            if (k) x = s;
            else b = s;
            if (l) y = f;
            else c = f;
            h(n, p, x, y, b, c)
        }
        var r = v6();
        r.add = function(p) {
            h(r, p, x, y, b, c)
        };
        r.visit = function(f) {
            w6(f, r, x, y, b, c)
        };
        a.forEach(r.add);
        return r
    };

    function v6() {
        return {
            leaf: true,
            nodes: [],
            point: null
        }
    }
    function w6(f, n, x, y, a, b) {
        if (!f(n, x, y, a, b)) {
            var s = (x + a) * .5,
                c = (y + b) * .5,
                d = n.nodes;
            if (d[0]) w6(f, d[0], x, y, s, c);
            if (d[1]) w6(f, d[1], s, y, a, c);
            if (d[2]) w6(f, d[2], x, c, s, b);
            if (d[3]) w6(f, d[3], s, c, a, b)
        }
    }
    function x6(p) {
        return {
            x: p[0],
            y: p[1]
        }
    }
    d3.time = {};
    var y6 = Date;

    function z6() {
        this._ = new Date(arguments.length > 1 ? Date.UTC.apply(this, arguments) : arguments[0])
    }
    z6.prototype = {
        getDate: function() {
            return this._.getUTCDate()
        },
        getDay: function() {
            return this._.getUTCDay()
        },
        getFullYear: function() {
            return this._.getUTCFullYear()
        },
        getHours: function() {
            return this._.getUTCHours()
        },
        getMilliseconds: function() {
            return this._.getUTCMilliseconds()
        },
        getMinutes: function() {
            return this._.getUTCMinutes()
        },
        getMonth: function() {
            return this._.getUTCMonth()
        },
        getSeconds: function() {
            return this._.getUTCSeconds()
        },
        getTime: function() {
            return this._.getTime()
        },
        getTimezoneOffset: function() {
            return 0
        },
        valueOf: function() {
            return this._.valueOf()
        },
        setDate: function() {
            A6.setUTCDate.apply(this._, arguments)
        },
        setDay: function() {
            A6.setUTCDay.apply(this._, arguments)
        },
        setFullYear: function() {
            A6.setUTCFullYear.apply(this._, arguments)
        },
        setHours: function() {
            A6.setUTCHours.apply(this._, arguments)
        },
        setMilliseconds: function() {
            A6.setUTCMilliseconds.apply(this._, arguments)
        },
        setMinutes: function() {
            A6.setUTCMinutes.apply(this._, arguments)
        },
        setMonth: function() {
            A6.setUTCMonth.apply(this._, arguments)
        },
        setSeconds: function() {
            A6.setUTCSeconds.apply(this._, arguments)
        },
        setTime: function() {
            A6.setTime.apply(this._, arguments)
        }
    };
    var A6 = Date.prototype;
    d3.time.format = function(t) {
        var n = t.length;

        function a(d) {
            var s = [],
                i = -1,
                j = 0,
                c, f;
            while (++i < n) {
                if (t.charCodeAt(i) == 37) {
                    s.push(t.substring(j, i), (f = G6[c = t.charAt(++i)]) ? f(d) : c);
                    j = i + 1
                }
            }
            s.push(t.substring(j, i));
            return s.join("")
        }
        a.parse = function(s) {
            var d = {
                y: 1900,
                m: 0,
                d: 1,
                H: 0,
                M: 0,
                S: 0,
                L: 0
            }, i = B6(d, t, s, 0);
            if (i != s.length) return null;
            if ("p" in d) d.H = d.H % 12 + d.p * 12;
            var b = new y6();
            b.setFullYear(d.y, d.m, d.d);
            b.setHours(d.H, d.M, d.S, d.L);
            return b
        };
        a.toString = function() {
            return t
        };
        return a
    };

    function B6(d, t, s, j) {
        var c, p, i = 0,
            n = t.length,
            m = s.length;
        while (i < n) {
            if (j >= m) return -1;
            c = t.charCodeAt(i++);
            if (c == 37) {
                p = H6[t.charAt(i++)];
                if (!p || ((j = p(d, s, j)) < 0)) return -1
            } else if (c != s.charCodeAt(j++)) {
                return -1
            }
        }
        return j
    }
    var C6 = d3.format("02d"),
        D6 = d3.format("03d"),
        E6 = d3.format("04d"),
        F6 = d3.format("2d");
    var G6 = {
        a: function(d) {
            return M6[d.getDay()].substring(0, 3)
        },
        A: function(d) {
            return M6[d.getDay()]
        },
        b: function(d) {
            return S6[d.getMonth()].substring(0, 3)
        },
        B: function(d) {
            return S6[d.getMonth()]
        },
        c: d3.time.format("%a %b %e %H:%M:%S %Y"),
        d: function(d) {
            return C6(d.getDate())
        },
        e: function(d) {
            return F6(d.getDate())
        },
        H: function(d) {
            return C6(d.getHours())
        },
        I: function(d) {
            return C6(d.getHours() % 12 || 12)
        },
        j: function(d) {
            return D6(1 + d3.time.dayOfYear(d))
        },
        L: function(d) {
            return D6(d.getMilliseconds())
        },
        m: function(d) {
            return C6(d.getMonth() + 1)
        },
        M: function(d) {
            return C6(d.getMinutes())
        },
        p: function(d) {
            return d.getHours() >= 12 ? "PM" : "AM"
        },
        S: function(d) {
            return C6(d.getSeconds())
        },
        U: function(d) {
            return C6(d3.time.sundayOfYear(d))
        },
        w: function(d) {
            return d.getDay()
        },
        W: function(d) {
            return C6(d3.time.mondayOfYear(d))
        },
        x: d3.time.format("%m/%d/%y"),
        X: d3.time.format("%H:%M:%S"),
        y: function(d) {
            return C6(d.getFullYear() % 100)
        },
        Y: function(d) {
            return E6(d.getFullYear() % 10000)
        },
        Z: g7,
        "%": function(d) {
            return "%"
        }
    };
    var H6 = {
        a: I6,
        A: J6,
        b: N6,
        B: P6,
        c: T6,
        d: $6,
        e: $6,
        H: _6,
        I: _6,
        L: c7,
        m: Z6,
        M: a7,
        p: e7,
        S: b7,
        x: U6,
        X: V6,
        y: X6,
        Y: W6
    };

    function I6(d, s, i) {
        return K6.test(s.substring(i, i += 3)) ? i : -1
    }
    function J6(d, s, i) {
        L6.lastIndex = 0;
        var n = L6.exec(s.substring(i, i + 10));
        return n ? i += n[0].length : -1
    }
    var K6 = /^(?:sun|mon|tue|wed|thu|fri|sat)/i,
        L6 = /^(?:Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday)/i,
        M6 = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

    function N6(d, s, i) {
        var n = O6.get(s.substring(i, i += 3).toLowerCase());
        return n == null ? -1 : (d.m = n, i)
    }
    var O6 = d3.map({
        jan: 0,
        feb: 1,
        mar: 2,
        apr: 3,
        may: 4,
        jun: 5,
        jul: 6,
        aug: 7,
        sep: 8,
        oct: 9,
        nov: 10,
        dec: 11
    });

    function P6(d, s, i) {
        Q6.lastIndex = 0;
        var n = Q6.exec(s.substring(i, i + 12));
        return n ? (d.m = R6.get(n[0].toLowerCase()), i += n[0].length) : -1
    }
    var Q6 = /^(?:January|February|March|April|May|June|July|August|September|October|November|December)/ig;
    var R6 = d3.map({
        january: 0,
        february: 1,
        march: 2,
        april: 3,
        may: 4,
        june: 5,
        july: 6,
        august: 7,
        september: 8,
        october: 9,
        november: 10,
        december: 11
    });
    var S6 = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

    function T6(d, s, i) {
        return B6(d, G6.c.toString(), s, i)
    }
    function U6(d, s, i) {
        return B6(d, G6.x.toString(), s, i)
    }
    function V6(d, s, i) {
        return B6(d, G6.X.toString(), s, i)
    }
    function W6(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 4));
        return n ? (d.y = +n[0], i += n[0].length) : -1
    }
    function X6(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 2));
        return n ? (d.y = Y6() + +n[0], i += n[0].length) : -1
    }
    function Y6() {
        return ~~(new Date().getFullYear() / 1000) * 1000
    }
    function Z6(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 2));
        return n ? (d.m = n[0] - 1, i += n[0].length) : -1
    }
    function $6(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 2));
        return n ? (d.d = +n[0], i += n[0].length) : -1
    }
    function _6(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 2));
        return n ? (d.H = +n[0], i += n[0].length) : -1
    }
    function a7(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 2));
        return n ? (d.M = +n[0], i += n[0].length) : -1
    }
    function b7(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 2));
        return n ? (d.S = +n[0], i += n[0].length) : -1
    }
    function c7(d, s, i) {
        d7.lastIndex = 0;
        var n = d7.exec(s.substring(i, i + 3));
        return n ? (d.L = +n[0], i += n[0].length) : -1
    }
    var d7 = /\s*\d+/;

    function e7(d, s, i) {
        var n = f7.get(s.substring(i, i += 2).toLowerCase());
        return n == null ? -1 : (d.p = n, i)
    }
    var f7 = d3.map({
        am: 0,
        pm: 1
    });

    function g7(d) {
        var z = d.getTimezoneOffset(),
            a = z > 0 ? "-" : "+",
            b = ~~(Math.abs(z) / 60),
            c = Math.abs(z) % 60;
        return a + C6(b) + C6(c)
    }
    d3.time.format.utc = function(t) {
        var l = d3.time.format(t);

        function f(d) {
            try {
                y6 = z6;
                var u = new y6();
                u._ = d;
                return l(u)
            } finally {
                y6 = Date
            }
        }
        f.parse = function(s) {
            try {
                y6 = z6;
                var d = l.parse(s);
                return d && d._
            } finally {
                y6 = Date
            }
        };
        f.toString = l.toString;
        return f
    };
    var h7 = d3.time.format.utc("%Y-%m-%dT%H:%M:%S.%LZ");
    d3.time.format.iso = Date.prototype.toISOString ? i7 : h7;

    function i7(d) {
        return d.toISOString()
    }
    i7.parse = function(s) {
        var d = new Date(s);
        return isNaN(d) ? null : d
    };
    i7.toString = h7.toString;

    function j7(l, s, n) {
        function r(d) {
            var f = l(d),
                g = o(f, 1);
            return d - f < g - d ? f : g
        }
        function c(d) {
            s(d = l(new y6(d - 1)), 1);
            return d
        }
        function o(d, k) {
            s(d = new y6(+d), k);
            return d
        }
        function a(t, d, f) {
            var g = c(t),
                h = [];
            if (f > 1) {
                while (g < d) {
                    if (!(n(g) % f)) h.push(new Date(+g));
                    s(g, 1)
                }
            } else {
                while (g < d) h.push(new Date(+g)), s(g, 1)
            }
            return h
        }
        function b(t, d, f) {
            try {
                y6 = z6;
                var u = new z6();
                u._ = t;
                return a(u, d, f)
            } finally {
                y6 = Date
            }
        }
        l.floor = l;
        l.round = r;
        l.ceil = c;
        l.offset = o;
        l.range = a;
        var u = l.utc = k7(l);
        u.floor = u;
        u.round = k7(r);
        u.ceil = k7(c);
        u.offset = k7(o);
        u.range = b;
        return l
    }
    function k7(m) {
        return function(d, k) {
            try {
                y6 = z6;
                var u = new z6();
                u._ = d;
                return m(u, k)._
            } finally {
                y6 = Date
            }
        }
    }
    d3.time.second = j7(function(d) {
        return new y6(Math.floor(d / 1e3) * 1e3)
    }, function(d, o) {
        d.setTime(d.getTime() + Math.floor(o) * 1e3)
    }, function(d) {
        return d.getSeconds()
    });
    d3.time.seconds = d3.time.second.range;
    d3.time.seconds.utc = d3.time.second.utc.range;
    d3.time.minute = j7(function(d) {
        return new y6(Math.floor(d / 6e4) * 6e4)
    }, function(d, o) {
        d.setTime(d.getTime() + Math.floor(o) * 6e4)
    }, function(d) {
        return d.getMinutes()
    });
    d3.time.minutes = d3.time.minute.range;
    d3.time.minutes.utc = d3.time.minute.utc.range;
    d3.time.hour = j7(function(d) {
        var t = d.getTimezoneOffset() / 60;
        return new y6((Math.floor(d / 36e5 - t) + t) * 36e5)
    }, function(d, o) {
        d.setTime(d.getTime() + Math.floor(o) * 36e5)
    }, function(d) {
        return d.getHours()
    });
    d3.time.hours = d3.time.hour.range;
    d3.time.hours.utc = d3.time.hour.utc.range;
    d3.time.day = j7(function(d) {
        return new y6(d.getFullYear(), d.getMonth(), d.getDate())
    }, function(d, o) {
        d.setDate(d.getDate() + o)
    }, function(d) {
        return d.getDate() - 1
    });
    d3.time.days = d3.time.day.range;
    d3.time.days.utc = d3.time.day.utc.range;
    d3.time.dayOfYear = function(d) {
        var y = d3.time.year(d);
        return Math.floor((d - y) / 864e5 - (d.getTimezoneOffset() - y.getTimezoneOffset()) / 1440)
    };
    M6.forEach(function(d, i) {
        d = d.toLowerCase();
        i = 7 - i;
        var a = d3.time[d] = j7(function(b) {
            (b = d3.time.day(b)).setDate(b.getDate() - (b.getDay() + i) % 7);
            return b
        }, function(b, o) {
            b.setDate(b.getDate() + Math.floor(o) * 7)
        }, function(b) {
            var d = d3.time.year(b).getDay();
            return Math.floor((d3.time.dayOfYear(b) + (d + i) % 7) / 7) - (d !== i)
        });
        d3.time[d + "s"] = a.range;
        d3.time[d + "s"].utc = a.utc.range;
        d3.time[d + "OfYear"] = function(b) {
            var d = d3.time.year(b).getDay();
            return Math.floor((d3.time.dayOfYear(b) + (d + i) % 7) / 7)
        }
    });
    d3.time.week = d3.time.sunday;
    d3.time.weeks = d3.time.sunday.range;
    d3.time.weeks.utc = d3.time.sunday.utc.range;
    d3.time.weekOfYear = d3.time.sundayOfYear;
    d3.time.month = j7(function(d) {
        return new y6(d.getFullYear(), d.getMonth(), 1)
    }, function(d, o) {
        d.setMonth(d.getMonth() + o)
    }, function(d) {
        return d.getMonth()
    });
    d3.time.months = d3.time.month.range;
    d3.time.months.utc = d3.time.month.utc.range;
    d3.time.year = j7(function(d) {
        return new y6(d.getFullYear(), 0, 1)
    }, function(d, o) {
        d.setFullYear(d.getFullYear() + o)
    }, function(d) {
        return d.getFullYear()
    });
    d3.time.years = d3.time.year.range;
    d3.time.years.utc = d3.time.year.utc.range;

    function l7(l, a, f) {
        function s(x) {
            return l(x)
        }
        s.invert = function(x) {
            return n7(l.invert(x))
        };
        s.domain = function(x) {
            if (!arguments.length) return l.domain().map(n7);
            l.domain(x);
            return s
        };
        s.nice = function(m) {
            var b = m7(s.domain());
            return s.domain([m.floor(b[0]), m.ceil(b[1])])
        };
        s.ticks = function(m, k) {
            var b = m7(s.domain());
            if (typeof m !== "function") {
                var c = b[1] - b[0],
                    t = c / m,
                    i = d3.bisect(r7, t);
                if (i == r7.length) return a.year(b, m);
                if (!i) return l.ticks(m).map(n7);
                if (Math.log(t / r7[i - 1]) < Math.log(r7[i] / t))--i;
                m = a[i];
                k = m[1];
                m = m[0].range
            }
            return m(b[0], new Date(+b[1] + 1), k)
        };
        s.tickFormat = function() {
            return f
        };
        s.copy = function() {
            return l7(l.copy(), a, f)
        };
        return d3.rebind(s, l, "range", "rangeRound", "interpolate", "clamp")
    }
    function m7(d) {
        var s = d[0],
            a = d[d.length - 1];
        return s < a ? [s, a] : [a, s]
    }
    function n7(t) {
        return new Date(t)
    }
    function o7(a) {
        return function(d) {
            var i = a.length - 1,
                f = a[i];
            while (!f[1](d)) f = a[--i];
            return f[0](d)
        }
    }
    function p7(y) {
        var d = new Date(y, 0, 1);
        d.setFullYear(y);
        return d
    }
    function q7(d) {
        var y = d.getFullYear(),
            a = p7(y),
            b = p7(y + 1);
        return y + (d - a) / (b - a)
    }
    var r7 = [1e3, 5e3, 15e3, 3e4, 6e4, 3e5, 9e5, 18e5, 36e5, 108e5, 216e5, 432e5, 864e5, 1728e5, 6048e5, 2592e6, 7776e6, 31536e6];
    var s7 = [
        [d3.time.second, 1],
        [d3.time.second, 5],
        [d3.time.second, 15],
        [d3.time.second, 30],
        [d3.time.minute, 1],
        [d3.time.minute, 5],
        [d3.time.minute, 15],
        [d3.time.minute, 30],
        [d3.time.hour, 1],
        [d3.time.hour, 3],
        [d3.time.hour, 6],
        [d3.time.hour, 12],
        [d3.time.day, 1],
        [d3.time.day, 2],
        [d3.time.week, 1],
        [d3.time.month, 1],
        [d3.time.month, 3],
        [d3.time.year, 1]
    ];
    var t7 = [
        [d3.time.format("%Y"), function(d) {
            return true
        }],
        [d3.time.format("%B"), function(d) {
            return d.getMonth()
        }],
        [d3.time.format("%b %d"), function(d) {
            return d.getDate() != 1
        }],
        [d3.time.format("%a %d"), function(d) {
            return d.getDay() && d.getDate() != 1
        }],
        [d3.time.format("%I %p"), function(d) {
            return d.getHours()
        }],
        [d3.time.format("%I:%M"), function(d) {
            return d.getMinutes()
        }],
        [d3.time.format(":%S"), function(d) {
            return d.getSeconds()
        }],
        [d3.time.format(".%L"), function(d) {
            return d.getMilliseconds()
        }]
    ];
    var u7 = d3.scale.linear(),
        v7 = o7(t7);
    s7.year = function(a, m) {
        return u7.domain(a.map(q7)).ticks(m).map(p7)
    };
    d3.time.scale = function() {
        return l7(d3.scale.linear(), s7, v7)
    };
    var w7 = s7.map(function(m) {
        return [m[0].utc, m[1]]
    });
    var x7 = [
        [d3.time.format.utc("%Y"), function(d) {
            return true
        }],
        [d3.time.format.utc("%B"), function(d) {
            return d.getUTCMonth()
        }],
        [d3.time.format.utc("%b %d"), function(d) {
            return d.getUTCDate() != 1
        }],
        [d3.time.format.utc("%a %d"), function(d) {
            return d.getUTCDay() && d.getUTCDate() != 1
        }],
        [d3.time.format.utc("%I %p"), function(d) {
            return d.getUTCHours()
        }],
        [d3.time.format.utc("%I:%M"), function(d) {
            return d.getUTCMinutes()
        }],
        [d3.time.format.utc(":%S"), function(d) {
            return d.getUTCSeconds()
        }],
        [d3.time.format.utc(".%L"), function(d) {
            return d.getUTCMilliseconds()
        }]
    ];
    var y7 = o7(x7);

    function z7(y) {
        var d = new Date(Date.UTC(y, 0, 1));
        d.setUTCFullYear(y);
        return d
    }
    function A7(d) {
        var y = d.getUTCFullYear(),
            a = z7(y),
            b = z7(y + 1);
        return y + (d - a) / (b - a)
    }
    w7.year = function(a, m) {
        return u7.domain(a.map(A7)).ticks(m).map(z7)
    };
    d3.time.scale.utc = function() {
        return l7(d3.scale.linear(), w7, y7)
    }
})();