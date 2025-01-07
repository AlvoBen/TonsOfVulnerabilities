﻿// Copyright (c) 2009-2011, Alexis Sellier

// Licensed under the Apache 2.0 License.

(function(q, u) {
    function y(a) {
        return q.less[a.split('/')[1]]
    };

    // -- kriskowal Kris Kowal Copyright (C) 2009-2010 MIT License

    if (!Array.isArray) {
        Array.isArray = function(o) {
            return Object.prototype.toString.call(o) === "[object Array]" || (o instanceof Array)
        }
    }
    if (!Array.prototype.forEach) {
        Array.prototype.forEach = function(b, t) {
            var l = this.length >>> 0;
            for (var i = 0; i < l; i++) {
                if (i in this) {
                    b.call(t, this[i], i, this)
                }
            }
        }
    }
    if (!Array.prototype.map) {
        Array.prototype.map = function(f) {
            var l = this.length >>> 0;
            var r = new Array(l);
            var t = arguments[1];
            for (var i = 0; i < l; i++) {
                if (i in this) {
                    r[i] = f.call(t, this[i], i, this)
                }
            }
            return r
        }
    }
    if (!Array.prototype.filter) {
        Array.prototype.filter = function(b) {
            var v = [];
            var t = arguments[1];
            for (var i = 0; i < this.length; i++) {
                if (b.call(t, this[i])) {
                    v.push(this[i])
                }
            }
            return v
        }
    }
    if (!Array.prototype.reduce) {
        Array.prototype.reduce = function(f) {
            var l = this.length >>> 0;
            var i = 0;
            if (l === 0 && arguments.length === 1) throw new TypeError();
            if (arguments.length >= 2) {
                var r = arguments[1]
            } else {
                do {
                    if (i in this) {
                        r = this[i++];
                        break
                    }
                    if (++i >= l) throw new TypeError()
                } while (true)
            }
            for (; i < l; i++) {
                if (i in this) {
                    r = f.call(null, r, this[i], i, this)
                }
            }
            return r
        }
    }
    if (!Array.prototype.indexOf) {
        Array.prototype.indexOf = function(v) {
            var l = this.length;
            var i = arguments[1] || 0;
            if (!l) return -1;
            if (i >= l) return -1;
            if (i < 0) i += l;
            for (; i < l; i++) {
                if (!Object.prototype.hasOwnProperty.call(this, i)) {
                    continue
                }
                if (v === this[i]) return i
            }
            return -1
        }
    }
    if (!Object.keys) {
        Object.keys = function(o) {
            var k = [];
            for (var n in o) {
                if (Object.prototype.hasOwnProperty.call(o, n)) {
                    k.push(n)
                }
            }
            return k
        }
    }
    if (!String.prototype.trim) {
        String.prototype.trim = function() {
            return String(this).replace(/^\s\s*/, '').replace(/\s\s*$/, '')
        }
    }
    var z, A;
    if (typeof environment === "object" && ({}).toString.call(environment) === "[object Environment]") {
        if (typeof(q) === 'undefined') {
            z = {}
        } else {
            z = q.less = {}
        }
        A = z.tree = {};
        z.mode = 'rhino'
    } else if (typeof(q) === 'undefined') {
        z = exports, A = y('./tree');
        z.mode = 'node'
    } else {
        if (typeof(q.less) === 'undefined') {
            q.less = {}
        }
        z = q.less, A = q.less.tree = {};
        z.mode = 'browser'
    }
    z.Parser = function Parser(f) {
        var g, i, j, h, l, r, w, x, Q;
        var R = this;
        var f = f || {};
        if (!f.contents) {
            f.contents = {}
        }
        var S = function() {};
        var T = this.imports = {
            paths: f && f.paths || [],
            queue: [],
            files: {},
            contents: f.contents,
            mime: f && f.mime,
            error: null,
            push: function(p, c) {
                var R = this;
                this.queue.push(p);
                z.Parser.importer(p, this.paths, function(e, a) {
                    R.queue.splice(R.queue.indexOf(p), 1);
                    var b = p in R.files;
                    R.files[p] = a;
                    if (e && !R.error) {
                        R.error = e
                    }
                    c(e, a, b);
                    if (R.queue.length === 0) {
                        S(e)
                    }
                }, f)
            }
        };

        function U() {
            h = w[j], l = i, x = i
        }
        function V() {
            w[j] = h, i = l, x = i
        }
        function W() {
            if (i > x) {
                w[j] = w[j].slice(i - x);
                x = i
            }
        }
        function X(c) {
            var a = c.charCodeAt(0);
            return a === 32 || a === 10 || a === 9
        }
        function $(t) {
            var m, a, b, c, k;
            if (t instanceof Function) {
                return t.call(Q.parsers)
            } else if (typeof(t) === 'string') {
                m = g.charAt(i) === t ? t : null;
                b = 1;
                W()
            } else {
                W();
                if (m = t.exec(w[j])) {
                    b = m[0].length
                } else {
                    return null
                }
            }
            if (m) {
                Y(b);
                if (typeof(m) === 'string') {
                    return m
                } else {
                    return m.length === 1 ? m[0] : m
                }
            }
        }
        function Y(a) {
            var o = i,
                b = j,
                e = i + w[j].length,
                m = i += a;
            while (i < e) {
                if (!X(g.charAt(i))) {
                    break
                }
                i++
            }
            w[j] = w[j].slice(a + (i - m));
            x = i;
            if (w[j].length === 0 && j < w.length - 1) {
                j++
            }
            return o !== i || b !== j
        }
        function Z(a, m) {
            var b = $(a);
            if (!b) {
                P(m || (typeof(a) === 'string' ? "expected '" + a + "' got '" + g.charAt(i) + "'" : "unexpected token"))
            } else {
                return b
            }
        }
        function P(m, t) {
            throw {
                index: i,
                type: t || 'Syntax',
                message: m
            }
        }
        function a1(t) {
            if (typeof(t) === 'string') {
                return g.charAt(i) === t
            } else {
                if (t.test(w[j])) {
                    return true
                } else {
                    return false
                }
            }
        }
        function b1(e, f) {
            if (e.filename && f.filename && (e.filename !== f.filename)) {
                return Q.imports.contents[e.filename]
            } else {
                return g
            }
        }
        function c1(a, g) {
            for (var n = a, c = -1; n >= 0 && g.charAt(n) !== '\n'; n--) {
                c++
            }
            return {
                line: typeof(a) === 'number' ? (g.slice(0, a).match(/\n/g) || "").length : null,
                column: c
            }
        }
        function d1(e) {
            if (z.mode === 'browser' || z.mode === 'rhino') return e.filename;
            else return y('path').resolve(e.filename)
        }
        function e1(a, b, e) {
            return {
                lineNumber: c1(a, b).line + 1,
                fileName: d1(e)
            }
        }
        function f1(e, f) {
            var g = b1(e, f),
                a = c1(e.index, g),
                b = a.line,
                c = a.column,
                d = g.split('\n');
            this.type = e.type || 'Syntax';
            this.message = e.message;
            this.filename = e.filename || f.filename;
            this.index = e.index;
            this.line = typeof(b) === 'number' ? b + 1 : null;
            this.callLine = e.call && (c1(e.call, g).line + 1);
            this.callExtract = d[c1(e.call, g).line];
            this.stack = e.stack;
            this.column = c;
            this.extract = [d[b - 1], d[b], d[b + 1]]
        }
        this.env = f = f || {};
        this.optimization = ('optimization' in this.env) ? this.env.optimization : 1;
        this.env.filename = this.env.filename || null;
        return Q = {
            imports: T,
            parse: function(s, a) {
                var b, d, m, o, p, t, v = [],
                    c, P = null;
                i = j = x = r = 0;
                g = s.replace(/\r\n/g, '\n');
                g = g.replace(/^\uFEFF/, '');
                w = (function(w) {
                    var j = 0,
                        k = /(?:@\{[\w-]+\}|[^"'`\{\}\/\(\)\\])+/g,
                        h1 = /\/\*(?:[^*]|\*+[^\/*])*\*+\/|\/\/.*/g,
                        i1 = /"((?:[^"\\\r\n]|\\.)*)"|'((?:[^'\\\r\n]|\\.)*)'|`((?:[^`]|\\.)*)`/g,
                        j1 = 0,
                        k1, l1 = w[0],
                        m1;
                    for (var i = 0, c, cc; i < g.length; i++) {
                        k.lastIndex = i;
                        if (k1 = k.exec(g)) {
                            if (k1.index === i) {
                                i += k1[0].length;
                                l1.push(k1[0])
                            }
                        }
                        c = g.charAt(i);
                        h1.lastIndex = i1.lastIndex = i;
                        if (k1 = i1.exec(g)) {
                            if (k1.index === i) {
                                i += k1[0].length;
                                l1.push(k1[0]);
                                c = g.charAt(i)
                            }
                        }
                        if (!m1 && c === '/') {
                            cc = g.charAt(i + 1);
                            if (cc === '/' || cc === '*') {
                                if (k1 = h1.exec(g)) {
                                    if (k1.index === i) {
                                        i += k1[0].length;
                                        l1.push(k1[0]);
                                        c = g.charAt(i)
                                    }
                                }
                            }
                        }
                        switch (c) {
                            case '{':
                                if (!m1) {
                                    j1++;
                                    l1.push(c);
                                    break
                                }
                            case '}':
                                if (!m1) {
                                    j1--;
                                    l1.push(c);
                                    w[++j] = l1 = [];
                                    break
                                }
                            case '(':
                                if (!m1) {
                                    m1 = true;
                                    l1.push(c);
                                    break
                                }
                            case ')':
                                if (m1) {
                                    m1 = false;
                                    l1.push(c);
                                    break
                                }
                            default:
                                l1.push(c)
                        }
                    }
                    if (j1 > 0) {
                        P = new(f1)({
                            index: i,
                            type: 'Parse',
                            message: "missing closing `}`",
                            filename: f.filename
                        }, f)
                    }
                    return w.map(function(c) {
                        return c.join('')
                    });
                })([
                    []
                ]);
                if (P) {
                    return a(P)
                }
                try {
                    b = new(A.Ruleset)([], $(this.parsers.primary));
                    b.root = true
                } catch (e) {
                    return a(new(f1)(e, f))
                }
                b.toCSS = (function(h1) {
                    var p, t, g1;
                    return function(i1, j1) {
                        var k1 = [],
                            l1;
                        i1 = i1 || {};
                        if (typeof(j1) === 'object' && !Array.isArray(j1)) {
                            j1 = Object.keys(j1).map(function(k) {
                                var n1 = j1[k];
                                if (!(n1 instanceof A.Value)) {
                                    if (!(n1 instanceof A.Expression)) {
                                        n1 = new(A.Expression)([n1])
                                    }
                                    n1 = new(A.Value)([n1])
                                }
                                return new(A.Rule)('@' + k, n1, false, 0)
                            });
                            k1 = [new(A.Ruleset)(null, j1)]
                        }
                        try {
                            var m1 = h1.call(this, {
                                frames: k1
                            }).toCSS([], {
                                compress: i1.compress || false,
                                dumpLineNumbers: f.dumpLineNumbers
                            })
                        } catch (e) {
                            throw new(f1)(e, f)
                        }
                        if ((l1 = Q.imports.error)) {
                            if (l1 instanceof f1) throw l1;
                            else throw new(f1)(l1, f)
                        }
                        if (i1.yuicompress && z.mode === 'node') {
                            return y('./cssmin').compressor.cssmin(m1)
                        } else if (i1.compress) {
                            return m1.replace(/(\s)+/g, "$1")
                        } else {
                            return m1
                        }
                    }
                })(b.eval);
                if (i < g.length - 1) {
                    i = r;
                    t = g.split('\n');
                    p = (g.slice(0, i).match(/\n/g) || "").length + 1;
                    for (var n = i, g1 = -1; n >= 0 && g.charAt(n) !== '\n'; n--) {
                        g1++
                    }
                    P = {
                        type: "Parse",
                        message: "Syntax Error on line " + p,
                        index: i,
                        filename: f.filename,
                        line: p,
                        column: g1,
                        extract: [t[p - 2], t[p - 1], t[p]]
                    }
                }
                if (this.imports.queue.length > 0) {
                    S = function(e) {
                        if (e) a(e);
                        else a(null, b)
                    }
                } else {
                    a(P, b)
                }
            },
            parsers: {
                primary: function() {
                    var n, a = [];
                    while ((n = $(this.mixin.definition) || $(this.rule) || $(this.ruleset) || $(this.mixin.call) || $(this.comment) || $(this.directive)) || $(/^[\s\n]+/)) {
                        n && a.push(n)
                    }
                    return a
                },
                comment: function() {
                    var c;
                    if (g.charAt(i) !== '/') return;
                    if (g.charAt(i + 1) === '/') {
                        return new(A.Comment)($(/^\/\/.*/), true)
                    } else if (c = $(/^\/\*(?:[^*]|\*+[^\/*])*\*+\/\n?/)) {
                        return new(A.Comment)(c)
                    }
                },
                entities: {
                    quoted: function() {
                        var s, j = i,
                            e;
                        if (g.charAt(j) === '~') {
                            j++, e = true
                        }
                        if (g.charAt(j) !== '"' && g.charAt(j) !== "'") return;
                        e && $('~');
                        if (s = $(/^"((?:[^"\\\r\n]|\\.)*)"|'((?:[^'\\\r\n]|\\.)*)'/)) {
                            return new(A.Quoted)(s[0], s[1] || s[2], e)
                        }
                    },
                    keyword: function() {
                        var k;
                        if (k = $(/^[_A-Za-z-][_A-Za-z0-9-]*/)) {
                            if (A.colors.hasOwnProperty(k)) {
                                return new(A.Color)(A.colors[k].slice(1))
                            } else {
                                return new(A.Keyword)(k)
                            }
                        }
                    },
                    call: function() {
                        var n, a, b, c, d = i;
                        if (!(n = /^([\w-]+|%|progid:[\w\.]+)\(/.exec(w[j]))) return;
                        n = n[1];
                        a = n.toLowerCase();
                        if (a === 'url') {
                            return null
                        } else {
                            i += n.length
                        }
                        if (a === 'alpha') {
                            c = $(this.alpha);
                            if (typeof c !== 'undefined') {
                                return c
                            }
                        }
                        $('(');
                        b = $(this.entities.arguments);
                        if (!$(')')) return;
                        if (n) {
                            return new(A.Call)(n, b, d, f.filename)
                        }
                    },
                    arguments: function() {
                        var a = [],
                            b;
                        while (b = $(this.entities.assignment) || $(this.expression)) {
                            a.push(b);
                            if (!$(',')) {
                                break
                            }
                        }
                        return a
                    },
                    literal: function() {
                        return $(this.entities.ratio) || $(this.entities.dimension) || $(this.entities.color) || $(this.entities.quoted)
                    },
                    assignment: function() {
                        var k, v;
                        if ((k = $(/^\w+(?=\s?=)/i)) && $('=') && (v = $(this.entity))) {
                            return new(A.Assignment)(k, v)
                        }
                    },
                    url: function() {
                        var v;
                        if (g.charAt(i) !== 'u' || !$(/^url\(/)) return;
                        v = $(this.entities.quoted) || $(this.entities.variable) || $(/^(?:(?:\\[\(\)'"])|[^\(\)'"])+/) || "";
                        Z(')');
                        return new(A.URL)((v.value != null || v instanceof A.Variable) ? v : new(A.Anonymous)(v), T.paths)
                    },
                    variable: function() {
                        var n, a = i;
                        if (g.charAt(i) === '@' && (n = $(/^@@?[\w-]+/))) {
                            return new(A.Variable)(n, a, f.filename)
                        }
                    },
                    variableCurly: function() {
                        var n, c, a = i;
                        if (g.charAt(i) === '@' && (c = $(/^@\{([\w-]+)\}/))) {
                            return new(A.Variable)("@" + c[1], a, f.filename)
                        }
                    },
                    color: function() {
                        var a;
                        if (g.charAt(i) === '#' && (a = $(/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})/))) {
                            return new(A.Color)(a[1])
                        }
                    },
                    dimension: function() {
                        var v, c = g.charCodeAt(i);
                        if ((c > 57 || c < 45) || c === 47) return;
                        if (v = $(/^(-?\d*\.?\d+)(px|%|em|pc|ex|in|deg|s|ms|pt|cm|mm|rad|grad|turn|dpi|dpcm|dppx|rem|vw|vh|vmin|vm|ch)?/)) {
                            return new(A.Dimension)(v[1], v[2])
                        }
                    },
                    ratio: function() {
                        var v, c = g.charCodeAt(i);
                        if (c > 57 || c < 48) return;
                        if (v = $(/^(\d+\/\d+)/)) {
                            return new(A.Ratio)(v[1])
                        }
                    },
                    javascript: function() {
                        var s, j = i,
                            e;
                        if (g.charAt(j) === '~') {
                            j++, e = true
                        }
                        if (g.charAt(j) !== '`') {
                            return
                        }
                        e && $('~');
                        if (s = $(/^`([^`]*)`/)) {
                            return new(A.JavaScript)(s[1], i, e)
                        }
                    }
                },
                variable: function() {
                    var n;
                    if (g.charAt(i) === '@' && (n = $(/^(@[\w-]+)\s*:/))) {
                        return n[1]
                    }
                },
                shorthand: function() {
                    var a, b;
                    if (!a1(/^[@\w.%-]+\/[@\w.-]+/)) return;
                    U();
                    if ((a = $(this.entity)) && $('/') && (b = $(this.entity))) {
                        return new(A.Shorthand)(a, b)
                    }
                    V()
                },
                mixin: {
                    call: function() {
                        var a = [],
                            e, c, b = [],
                            d, k = i,
                            s = g.charAt(i),
                            n, v, m = false;
                        if (s !== '.' && s !== '#') {
                            return
                        }
                        U();
                        while (e = $(/^[#.](?:[\w-]|\\(?:[A-Fa-f0-9]{1,6} ?|[^A-Fa-f0-9]))+/)) {
                            a.push(new(A.Element)(c, e, i));
                            c = $('>')
                        }
                        if ($('(')) {
                            while (d = $(this.expression)) {
                                v = d;
                                n = null;
                                if (d.value.length == 1) {
                                    var o = d.value[0];
                                    if (o instanceof A.Variable) {
                                        if ($(':')) {
                                            if (v = $(this.expression)) {
                                                n = o.name
                                            } else {
                                                throw new(Error)("Expected value")
                                            }
                                        }
                                    }
                                }
                                b.push({
                                    name: n,
                                    value: v
                                });
                                if (!$(',')) {
                                    break
                                }
                            }
                            if (!$(')')) throw new(Error)("Expected )")
                        }
                        if ($(this.important)) {
                            m = true
                        }
                        if (a.length > 0 && ($(';') || a1('}'))) {
                            return new(A.mixin.Call)(a, b, k, f.filename, m)
                        }
                        V()
                    },
                    definition: function() {
                        var n, p = [],
                            m, a, b, v, c, d = false;
                        if ((g.charAt(i) !== '.' && g.charAt(i) !== '#') || a1(/^[^{]*(;|})/)) return;
                        U();
                        if (m = $(/^([#.](?:[\w-]|\\(?:[A-Fa-f0-9]{1,6} ?|[^A-Fa-f0-9]))+)\s*\(/)) {
                            n = m[1];
                            do {
                                if (g.charAt(i) === '.' && $(/^\.{3}/)) {
                                    d = true;
                                    break
                                } else if (b = $(this.entities.variable) || $(this.entities.literal) || $(this.entities.keyword)) {
                                    if (b instanceof A.Variable) {
                                        if ($(':')) {
                                            v = Z(this.expression, 'expected expression');
                                            p.push({
                                                name: b.name,
                                                value: v
                                            })
                                        } else if ($(/^\.{3}/)) {
                                            p.push({
                                                name: b.name,
                                                variadic: true
                                            });
                                            d = true;
                                            break
                                        } else {
                                            p.push({
                                                name: b.name
                                            })
                                        }
                                    } else {
                                        p.push({
                                            value: b
                                        })
                                    }
                                } else {
                                    break
                                }
                            } while ($(','));
                            if (!$(')')) {
                                r = i;
                                V()
                            }
                            if ($(/^when/)) {
                                c = Z(this.conditions, 'expected condition')
                            }
                            a = $(this.block);
                            if (a) {
                                return new(A.mixin.Definition)(n, p, a, c, d)
                            } else {
                                V()
                            }
                        }
                    }
                },
                entity: function() {
                    return $(this.entities.literal) || $(this.entities.variable) || $(this.entities.url) || $(this.entities.call) || $(this.entities.keyword) || $(this.entities.javascript) || $(this.comment)
                },
                end: function() {
                    return $(';') || a1('}')
                },
                alpha: function() {
                    var v;
                    if (!$(/^\(opacity=/i)) return;
                    if (v = $(/^\d+/) || $(this.entities.variable)) {
                        Z(')');
                        return new(A.Alpha)(v)
                    }
                },
                element: function() {
                    var e, t, c, v;
                    c = $(this.combinator);
                    e = $(/^(?:\d+\.\d+|\d+)%/) || $(/^(?:[.#]?|:*)(?:[\w-]|[^\x00-\x9f]|\\(?:[A-Fa-f0-9]{1,6} ?|[^A-Fa-f0-9]))+/) || $('*') || $('&') || $(this.attribute) || $(/^\([^)@]+\)/) || $(/^[\.#](?=@)/) || $(this.entities.variableCurly);
                    if (!e) {
                        if ($('(') && (v = ($(this.entities.variableCurly) || $(this.entities.variable))) && $(')')) {
                            e = new(A.Paren)(v)
                        }
                    }
                    if (e) {
                        return new(A.Element)(c, e, i)
                    }
                },
                combinator: function() {
                    var m, c = g.charAt(i);
                    if (c === '>' || c === '+' || c === '~') {
                        i++;
                        while (g.charAt(i).match(/\s/)) {
                            i++
                        }
                        return new(A.Combinator)(c)
                    } else if (g.charAt(i - 1).match(/\s/)) {
                        return new(A.Combinator)(" ")
                    } else {
                        return new(A.Combinator)(null)
                    }
                },
                selector: function() {
                    var s, e, a = [],
                        c, m;
                    if ($('(')) {
                        s = $(this.entity);
                        Z(')');
                        return new(A.Selector)([new(A.Element)('', s, i)])
                    }
                    while (e = $(this.element)) {
                        c = g.charAt(i);
                        a.push(e);
                        if (c === '{' || c === '}' || c === ';' || c === ',') {
                            break
                        }
                    }
                    if (a.length > 0) {
                        return new(A.Selector)(a)
                    }
                },
                tag: function() {
                    return $(/^[A-Za-z][A-Za-z-]*[0-9]?/) || $('*')
                },
                attribute: function() {
                    var a = '',
                        k, v, o;
                    if (!$('[')) return;
                    if (k = $(/^(?:[_A-Za-z0-9-]|\\.)+/) || $(this.entities.quoted)) {
                        if ((o = $(/^[|~*$^]?=/)) && (v = $(this.entities.quoted) || $(/^[\w-]+/))) {
                            a = [k, o, v.toCSS ? v.toCSS() : v].join('')
                        } else {
                            a = k
                        }
                    }
                    if (!$(']')) return;
                    if (a) {
                        return "[" + a + "]"
                    }
                },
                block: function() {
                    var c;
                    if ($('{') && (c = $(this.primary)) && $('}')) {
                        return c
                    }
                },
                ruleset: function() {
                    var a = [],
                        s, b, m, d;
                    U();
                    if (f.dumpLineNumbers) d = e1(i, g, f);
                    while (s = $(this.selector)) {
                        a.push(s);
                        $(this.comment);
                        if (!$(',')) {
                            break
                        }
                        $(this.comment)
                    }
                    if (a.length > 0 && (b = $(this.block))) {
                        var c = new(A.Ruleset)(a, b, f.strictImports);
                        if (f.dumpLineNumbers) c.debugInfo = d;
                        return c
                    } else {
                        r = i;
                        V()
                    }
                },
                rule: function() {
                    var n, v, c = g.charAt(i),
                        a, m;
                    U();
                    if (c === '.' || c === '#' || c === '&') {
                        return
                    }
                    if (n = $(this.variable) || $(this.property)) {
                        if ((n.charAt(0) != '@') && (m = /^([^@+\/'"*`(;{}-]*);/.exec(w[j]))) {
                            i += m[0].length - 1;
                            v = new(A.Anonymous)(m[1])
                        } else if (n === "font") {
                            v = $(this.font)
                        } else {
                            v = $(this.value)
                        }
                        a = $(this.important);
                        if (v && $(this.end)) {
                            return new(A.Rule)(n, v, a, l)
                        } else {
                            r = i;
                            V()
                        }
                    }
                },
                "import": function() {
                    var p, a, b = i;
                    U();
                    var d = $(/^@import(?:-(once))?\s+/);
                    if (d && (p = $(this.entities.quoted) || $(this.entities.url))) {
                        a = $(this.mediaFeatures);
                        if ($(';')) {
                            return new(A.Import)(p, T, a, (d[1] === 'once'), b)
                        }
                    }
                    V()
                },
                mediaFeature: function() {
                    var e, p, n = [];
                    do {
                        if (e = $(this.entities.keyword)) {
                            n.push(e)
                        } else if ($('(')) {
                            p = $(this.property);
                            e = $(this.entity);
                            if ($(')')) {
                                if (p && e) {
                                    n.push(new(A.Paren)(new(A.Rule)(p, e, null, i, true)))
                                } else if (e) {
                                    n.push(new(A.Paren)(e))
                                } else {
                                    return null
                                }
                            } else {
                                return null
                            }
                        }
                    } while (e);
                    if (n.length > 0) {
                        return new(A.Expression)(n)
                    }
                },
                mediaFeatures: function() {
                    var e, a = [];
                    do {
                        if (e = $(this.mediaFeature)) {
                            a.push(e);
                            if (!$(',')) {
                                break
                            }
                        } else if (e = $(this.entities.variable)) {
                            a.push(e);
                            if (!$(',')) {
                                break
                            }
                        }
                    } while (e);
                    return a.length > 0 ? a : null
                },
                media: function() {
                    var a, b, m, d;
                    if (f.dumpLineNumbers) d = e1(i, g, f);
                    if ($(/^@media/)) {
                        a = $(this.mediaFeatures);
                        if (b = $(this.block)) {
                            m = new(A.Media)(b, a);
                            if (f.dumpLineNumbers) m.debugInfo = d;
                            return m
                        }
                    }
                },
                directive: function() {
                    var n, v, a, b, e, c, d, k, m;
                    if (g.charAt(i) !== '@') return;
                    if (v = $(this['import']) || $(this.media)) {
                        return v
                    }
                    U();
                    n = $(/^@[a-z-]+/);
                    d = n;
                    if (n.charAt(1) == '-' && n.indexOf('-', 2) > 0) {
                        d = "@" + n.slice(n.indexOf('-', 2) + 1)
                    }
                    switch (d) {
                        case "@font-face":
                            k = true;
                            break;
                        case "@viewport":
                        case "@top-left":
                        case "@top-left-corner":
                        case "@top-center":
                        case "@top-right":
                        case "@top-right-corner":
                        case "@bottom-left":
                        case "@bottom-left-corner":
                        case "@bottom-center":
                        case "@bottom-right":
                        case "@bottom-right-corner":
                        case "@left-top":
                        case "@left-middle":
                        case "@left-bottom":
                        case "@right-top":
                        case "@right-middle":
                        case "@right-bottom":
                            k = true;
                            break;
                        case "@page":
                        case "@document":
                        case "@supports":
                        case "@keyframes":
                            k = true;
                            m = true;
                            break
                    }
                    if (m) {
                        n += " " + ($(/^[^{]+/) || '').trim()
                    }
                    if (k) {
                        if (a = $(this.block)) {
                            return new(A.Directive)(n, a)
                        }
                    } else {
                        if ((v = $(this.entity)) && $(';')) {
                            return new(A.Directive)(n, v)
                        }
                    }
                    V()
                },
                font: function() {
                    var v = [],
                        a = [],
                        b, s, c, e;
                    while (e = $(this.shorthand) || $(this.entity)) {
                        a.push(e)
                    }
                    v.push(new(A.Expression)(a));
                    if ($(',')) {
                        while (e = $(this.expression)) {
                            v.push(e);
                            if (!$(',')) {
                                break
                            }
                        }
                    }
                    return new(A.Value)(v)
                },
                value: function() {
                    var e, a = [],
                        b;
                    while (e = $(this.expression)) {
                        a.push(e);
                        if (!$(',')) {
                            break
                        }
                    }
                    if (a.length > 0) {
                        return new(A.Value)(a)
                    }
                },
                important: function() {
                    if (g.charAt(i) === '!') {
                        return $(/^! *important/)
                    }
                },
                sub: function() {
                    var e;
                    if ($('(') && (e = $(this.expression)) && $(')')) {
                        return e
                    }
                },
                multiplication: function() {
                    var m, a, o, b;
                    if (m = $(this.operand)) {
                        while (!a1(/^\/\*/) && (o = ($('/') || $('*'))) && (a = $(this.operand))) {
                            b = new(A.Operation)(o, [b || m, a])
                        }
                        return b || m
                    }
                },
                addition: function() {
                    var m, a, o, b;
                    if (m = $(this.multiplication)) {
                        while ((o = $(/^[-+]\s+/) || (!X(g.charAt(i - 1)) && ($('+') || $('-')))) && (a = $(this.multiplication))) {
                            b = new(A.Operation)(o, [b || m, a])
                        }
                        return b || m
                    }
                },
                conditions: function() {
                    var a, b, c = i,
                        d;
                    if (a = $(this.condition)) {
                        while ($(',') && (b = $(this.condition))) {
                            d = new(A.Condition)('or', d || a, b, c)
                        }
                        return d || a
                    }
                },
                condition: function() {
                    var a, b, c, o, d = i,
                        n = false;
                    if ($(/^not/)) {
                        n = true
                    }
                    Z('(');
                    if (a = $(this.addition) || $(this.entities.keyword) || $(this.entities.quoted)) {
                        if (o = $(/^(?:>=|=<|[<=>])/)) {
                            if (b = $(this.addition) || $(this.entities.keyword) || $(this.entities.quoted)) {
                                c = new(A.Condition)(o, a, b, d, n)
                            } else {
                                P('expected expression')
                            }
                        } else {
                            c = new(A.Condition)('=', a, new(A.Keyword)('true'), d, n)
                        }
                        Z(')');
                        return $(/^and/) ? new(A.Condition)('and', c, $(this.condition)) : c
                    }
                },
                operand: function() {
                    var n, p = g.charAt(i + 1);
                    if (g.charAt(i) === '-' && (p === '@' || p === '(')) {
                        n = $('-')
                    }
                    var o = $(this.sub) || $(this.entities.dimension) || $(this.entities.color) || $(this.entities.variable) || $(this.entities.call);
                    return n ? new(A.Operation)('*', [new(A.Dimension)(-1), o]) : o
                },
                expression: function() {
                    var e, a, b = [],
                        d;
                    while (e = $(this.addition) || $(this.entity)) {
                        b.push(e)
                    }
                    if (b.length > 0) {
                        return new(A.Expression)(b)
                    }
                },
                property: function() {
                    var n;
                    if (n = $(/^(\*?-?[_a-z0-9-]+)\s*:/)) {
                        return n[1]
                    }
                }
            }
        }
    };
    if (z.mode === 'browser' || z.mode === 'rhino') {
        z.Parser.importer = function(p, a, c, b) {
            if (!/^([a-z-]+:)?\//.test(p) && a.length > 0) {
                p = a[0] + p
            }
            I({
                href: p,
                title: p,
                type: b.mime,
                contents: b.contents
            }, function(e) {
                if (e && typeof(b.errback) === "function") {
                    b.errback.call(null, p, a, c, b)
                } else {
                    c.apply(null, arguments)
                }
            }, true)
        }
    }(function(A) {
        A.functions = {
            rgb: function(r, g, b) {
                return this.rgba(r, g, b, 1.0)
            },
            rgba: function(r, g, b, a) {
                var f = [r, g, b].map(function(c) {
                    return e(c)
                }),
                    a = e(a);
                return new(A.Color)(f, a)
            },
            hsl: function(h, s, l) {
                return this.hsla(h, s, l, 1.0)
            },
            hsla: function(h, s, l, a) {
                h = (e(h) % 360) / 360;
                s = e(s);
                l = e(l);
                a = e(a);
                var m = l <= 0.5 ? l * (s + 1) : l + s - l * s;
                var b = l * 2 - m;
                return this.rgba(c(h + 1 / 3) * 255, c(h) * 255, c(h - 1 / 3) * 255, a);

                function c(h) {
                    h = h < 0 ? h + 1 : (h > 1 ? h - 1 : h);
                    if (h * 6 < 1) return b + (m - b) * h * 6;
                    else if (h * 2 < 1) return m;
                    else if (h * 3 < 2) return b + (m - b) * (2 / 3 - h) * 6;
                    else return b
                }
            },
            hue: function(c) {
                return new(A.Dimension)(Math.round(c.toHSL().h))
            },
            saturation: function(c) {
                return new(A.Dimension)(Math.round(c.toHSL().s * 100), '%')
            },
            lightness: function(c) {
                return new(A.Dimension)(Math.round(c.toHSL().l * 100), '%')
            },
            red: function(c) {
                return new(A.Dimension)(c.rgb[0])
            },
            green: function(c) {
                return new(A.Dimension)(c.rgb[1])
            },
            blue: function(c) {
                return new(A.Dimension)(c.rgb[2])
            },
            alpha: function(c) {
                return new(A.Dimension)(c.toHSL().a)
            },
            luma: function(c) {
                return new(A.Dimension)(Math.round((0.2126 * (c.rgb[0] / 255) + 0.7152 * (c.rgb[1] / 255) + 0.0722 * (c.rgb[2] / 255)) * c.alpha * 100), '%')
            },
            saturate: function(c, a) {
                var h = c.toHSL();
                h.s += a.value / 100;
                h.s = j(h.s);
                return d(h)
            },
            desaturate: function(c, a) {
                var h = c.toHSL();
                h.s -= a.value / 100;
                h.s = j(h.s);
                return d(h)
            },
            lighten: function(c, a) {
                var h = c.toHSL();
                h.l += a.value / 100;
                h.l = j(h.l);
                return d(h)
            },
            darken: function(c, a) {
                var h = c.toHSL();
                h.l -= a.value / 100;
                h.l = j(h.l);
                return d(h)
            },
            fadein: function(c, a) {
                var h = c.toHSL();
                h.a += a.value / 100;
                h.a = j(h.a);
                return d(h)
            },
            fadeout: function(c, a) {
                var h = c.toHSL();
                h.a -= a.value / 100;
                h.a = j(h.a);
                return d(h)
            },
            fade: function(c, a) {
                var h = c.toHSL();
                h.a = a.value / 100;
                h.a = j(h.a);
                return d(h)
            },
            spin: function(c, a) {
                var h = c.toHSL();
                var b = (h.h + a.value) % 360;
                h.h = b < 0 ? 360 + b : b;
                return d(h)
            },

            // Copyright (c) 2006-2009 Hampton Catlin, Nathan Weizenbaum, and Chris Eppstein

            mix: function(c, b, f) {
                if (!f) {
                    f = new(A.Dimension)(50)
                }
                var p = f.value / 100.0;
                var w = p * 2 - 1;
                var a = c.toHSL().a - b.toHSL().a;
                var g = (((w * a == -1) ? w : (w + a) / (1 + w * a)) + 1) / 2.0;
                var h = 1 - g;
                var r = [c.rgb[0] * g + b.rgb[0] * h, c.rgb[1] * g + b.rgb[1] * h, c.rgb[2] * g + b.rgb[2] * h];
                var k = c.alpha * p + b.alpha * (1 - p);
                return new(A.Color)(r, k)
            },
            greyscale: function(c) {
                return this.desaturate(c, new(A.Dimension)(100))
            },
            contrast: function(c, a, l, t) {
                if (typeof l === 'undefined') {
                    l = this.rgba(255, 255, 255, 1.0)
                }
                if (typeof a === 'undefined') {
                    a = this.rgba(0, 0, 0, 1.0)
                }
                if (typeof t === 'undefined') {
                    t = 0.43
                } else {
                    t = t.value
                }
                if (((0.2126 * (c.rgb[0] / 255) + 0.7152 * (c.rgb[1] / 255) + 0.0722 * (c.rgb[2] / 255)) * c.alpha) < t) {
                    return l
                } else {
                    return a
                }
            },
            e: function(s) {
                return new(A.Anonymous)(s instanceof A.JavaScript ? s.evaluated : s)
            },
            escape: function(s) {
                return new(A.Anonymous)(encodeURI(s.value).replace(/=/g, "%3D").replace(/:/g, "%3A").replace(/#/g, "%23").replace(/;/g, "%3B").replace(/\(/g, "%28").replace(/\)/g, "%29"))
            },
            '%': function(a) {
                var b = Array.prototype.slice.call(arguments, 1),
                    s = a.value;
                for (var i = 0; i < b.length; i++) {
                    s = s.replace(/%[sda]/i, function(t) {
                        var v = t.match(/s/i) ? b[i].value : b[i].toCSS();
                        return t.match(/[A-Z]$/) ? encodeURIComponent(v) : v
                    })
                }
                s = s.replace(/%%/g, '%');
                return new(A.Quoted)('"' + s + '"', s)
            },
            round: function(n, f) {
                var a = typeof(f) === "undefined" ? 0 : f.value;
                if (n instanceof A.Dimension) {
                    return new(A.Dimension)(e(n).toFixed(a), n.unit)
                } else if (typeof(n) === 'number') {
                    return n.toFixed(a)
                } else {
                    throw {
                        type: "Argument",
                        message: "argument must be a number"
                    }
                }
            },
            ceil: function(n) {
                return this._math('ceil', n)
            },
            floor: function(n) {
                return this._math('floor', n)
            },
            _math: function(f, n) {
                if (n instanceof A.Dimension) {
                    return new(A.Dimension)(Math[f](e(n)), n.unit)
                } else if (typeof(n) === 'number') {
                    return Math[f](n)
                } else {
                    throw {
                        type: "Argument",
                        message: "argument must be a number"
                    }
                }
            },
            argb: function(c) {
                return new(A.Anonymous)(c.toARGB())
            },
            percentage: function(n) {
                return new(A.Dimension)(n.value * 100, '%')
            },
            color: function(n) {
                if (n instanceof A.Quoted) {
                    return new(A.Color)(n.value.slice(1))
                } else {
                    throw {
                        type: "Argument",
                        message: "argument must be a string"
                    }
                }
            },
            iscolor: function(n) {
                return this._isa(n, A.Color)
            },
            isnumber: function(n) {
                return this._isa(n, A.Dimension)
            },
            isstring: function(n) {
                return this._isa(n, A.Quoted)
            },
            iskeyword: function(n) {
                return this._isa(n, A.Keyword)
            },
            isurl: function(n) {
                return this._isa(n, A.URL)
            },
            ispixel: function(n) {
                return (n instanceof A.Dimension) && n.unit === 'px' ? A.True : A.False
            },
            ispercentage: function(n) {
                return (n instanceof A.Dimension) && n.unit === '%' ? A.True : A.False
            },
            isem: function(n) {
                return (n instanceof A.Dimension) && n.unit === 'em' ? A.True : A.False
            },
            _isa: function(n, T) {
                return (n instanceof T) ? A.True : A.False
            },
            multiply: function(c, a) {
                var r = c.rgb[0] * a.rgb[0] / 255;
                var g = c.rgb[1] * a.rgb[1] / 255;
                var b = c.rgb[2] * a.rgb[2] / 255;
                return this.rgb(r, g, b)
            },
            screen: function(c, a) {
                var r = 255 - (255 - c.rgb[0]) * (255 - a.rgb[0]) / 255;
                var g = 255 - (255 - c.rgb[1]) * (255 - a.rgb[1]) / 255;
                var b = 255 - (255 - c.rgb[2]) * (255 - a.rgb[2]) / 255;
                return this.rgb(r, g, b)
            },
            overlay: function(c, a) {
                var r = c.rgb[0] < 128 ? 2 * c.rgb[0] * a.rgb[0] / 255 : 255 - 2 * (255 - c.rgb[0]) * (255 - a.rgb[0]) / 255;
                var g = c.rgb[1] < 128 ? 2 * c.rgb[1] * a.rgb[1] / 255 : 255 - 2 * (255 - c.rgb[1]) * (255 - a.rgb[1]) / 255;
                var b = c.rgb[2] < 128 ? 2 * c.rgb[2] * a.rgb[2] / 255 : 255 - 2 * (255 - c.rgb[2]) * (255 - a.rgb[2]) / 255;
                return this.rgb(r, g, b)
            },
            softlight: function(c, a) {
                var t = a.rgb[0] * c.rgb[0] / 255;
                var r = t + c.rgb[0] * (255 - (255 - c.rgb[0]) * (255 - a.rgb[0]) / 255 - t) / 255;
                t = a.rgb[1] * c.rgb[1] / 255;
                var g = t + c.rgb[1] * (255 - (255 - c.rgb[1]) * (255 - a.rgb[1]) / 255 - t) / 255;
                t = a.rgb[2] * c.rgb[2] / 255;
                var b = t + c.rgb[2] * (255 - (255 - c.rgb[2]) * (255 - a.rgb[2]) / 255 - t) / 255;
                return this.rgb(r, g, b)
            },
            hardlight: function(c, a) {
                var r = a.rgb[0] < 128 ? 2 * a.rgb[0] * c.rgb[0] / 255 : 255 - 2 * (255 - a.rgb[0]) * (255 - c.rgb[0]) / 255;
                var g = a.rgb[1] < 128 ? 2 * a.rgb[1] * c.rgb[1] / 255 : 255 - 2 * (255 - a.rgb[1]) * (255 - c.rgb[1]) / 255;
                var b = a.rgb[2] < 128 ? 2 * a.rgb[2] * c.rgb[2] / 255 : 255 - 2 * (255 - a.rgb[2]) * (255 - c.rgb[2]) / 255;
                return this.rgb(r, g, b)
            },
            difference: function(c, a) {
                var r = Math.abs(c.rgb[0] - a.rgb[0]);
                var g = Math.abs(c.rgb[1] - a.rgb[1]);
                var b = Math.abs(c.rgb[2] - a.rgb[2]);
                return this.rgb(r, g, b)
            },
            exclusion: function(c, a) {
                var r = c.rgb[0] + a.rgb[0] * (255 - c.rgb[0] - c.rgb[0]) / 255;
                var g = c.rgb[1] + a.rgb[1] * (255 - c.rgb[1] - c.rgb[1]) / 255;
                var b = c.rgb[2] + a.rgb[2] * (255 - c.rgb[2] - c.rgb[2]) / 255;
                return this.rgb(r, g, b)
            },
            average: function(c, a) {
                var r = (c.rgb[0] + a.rgb[0]) / 2;
                var g = (c.rgb[1] + a.rgb[1]) / 2;
                var b = (c.rgb[2] + a.rgb[2]) / 2;
                return this.rgb(r, g, b)
            },
            negation: function(c, a) {
                var r = 255 - Math.abs(255 - a.rgb[0] - c.rgb[0]);
                var g = 255 - Math.abs(255 - a.rgb[1] - c.rgb[1]);
                var b = 255 - Math.abs(255 - a.rgb[2] - c.rgb[2]);
                return this.rgb(r, g, b)
            },
            tint: function(c, a) {
                return this.mix(this.rgb(255, 255, 255), c, a)
            },
            shade: function(c, a) {
                return this.mix(this.rgb(0, 0, 0), c, a)
            }
        };

        function d(d) {
            return A.functions.hsla(d.h, d.s, d.l, d.a)
        }
        function e(n) {
            if (n instanceof A.Dimension) {
                return parseFloat(n.unit == '%' ? n.value / 100 : n.value)
            } else if (typeof(n) === 'number') {
                return n
            } else {
                throw {
                    error: "RuntimeError",
                    message: "color functions take numbers as parameters"
                }
            }
        }
        function j(v) {
            return Math.min(1, Math.max(0, v))
        }
    })(y('./tree'));
    (function(A) {
        A.colors = {
            'aliceblue': '#f0f8ff',
            'antiquewhite': '#faebd7',
            'aqua': '#00ffff',
            'aquamarine': '#7fffd4',
            'azure': '#f0ffff',
            'beige': '#f5f5dc',
            'bisque': '#ffe4c4',
            'black': '#000000',
            'blanchedalmond': '#ffebcd',
            'blue': '#0000ff',
            'blueviolet': '#8a2be2',
            'brown': '#a52a2a',
            'burlywood': '#deb887',
            'cadetblue': '#5f9ea0',
            'chartreuse': '#7fff00',
            'chocolate': '#d2691e',
            'coral': '#ff7f50',
            'cornflowerblue': '#6495ed',
            'cornsilk': '#fff8dc',
            'crimson': '#dc143c',
            'cyan': '#00ffff',
            'darkblue': '#00008b',
            'darkcyan': '#008b8b',
            'darkgoldenrod': '#b8860b',
            'darkgray': '#a9a9a9',
            'darkgrey': '#a9a9a9',
            'darkgreen': '#006400',
            'darkkhaki': '#bdb76b',
            'darkmagenta': '#8b008b',
            'darkolivegreen': '#556b2f',
            'darkorange': '#ff8c00',
            'darkorchid': '#9932cc',
            'darkred': '#8b0000',
            'darksalmon': '#e9967a',
            'darkseagreen': '#8fbc8f',
            'darkslateblue': '#483d8b',
            'darkslategray': '#2f4f4f',
            'darkslategrey': '#2f4f4f',
            'darkturquoise': '#00ced1',
            'darkviolet': '#9400d3',
            'deeppink': '#ff1493',
            'deepskyblue': '#00bfff',
            'dimgray': '#696969',
            'dimgrey': '#696969',
            'dodgerblue': '#1e90ff',
            'firebrick': '#b22222',
            'floralwhite': '#fffaf0',
            'forestgreen': '#228b22',
            'fuchsia': '#ff00ff',
            'gainsboro': '#dcdcdc',
            'ghostwhite': '#f8f8ff',
            'gold': '#ffd700',
            'goldenrod': '#daa520',
            'gray': '#808080',
            'grey': '#808080',
            'green': '#008000',
            'greenyellow': '#adff2f',
            'honeydew': '#f0fff0',
            'hotpink': '#ff69b4',
            'indianred': '#cd5c5c',
            'indigo': '#4b0082',
            'ivory': '#fffff0',
            'khaki': '#f0e68c',
            'lavender': '#e6e6fa',
            'lavenderblush': '#fff0f5',
            'lawngreen': '#7cfc00',
            'lemonchiffon': '#fffacd',
            'lightblue': '#add8e6',
            'lightcoral': '#f08080',
            'lightcyan': '#e0ffff',
            'lightgoldenrodyellow': '#fafad2',
            'lightgray': '#d3d3d3',
            'lightgrey': '#d3d3d3',
            'lightgreen': '#90ee90',
            'lightpink': '#ffb6c1',
            'lightsalmon': '#ffa07a',
            'lightseagreen': '#20b2aa',
            'lightskyblue': '#87cefa',
            'lightslategray': '#778899',
            'lightslategrey': '#778899',
            'lightsteelblue': '#b0c4de',
            'lightyellow': '#ffffe0',
            'lime': '#00ff00',
            'limegreen': '#32cd32',
            'linen': '#faf0e6',
            'magenta': '#ff00ff',
            'maroon': '#800000',
            'mediumaquamarine': '#66cdaa',
            'mediumblue': '#0000cd',
            'mediumorchid': '#ba55d3',
            'mediumpurple': '#9370d8',
            'mediumseagreen': '#3cb371',
            'mediumslateblue': '#7b68ee',
            'mediumspringgreen': '#00fa9a',
            'mediumturquoise': '#48d1cc',
            'mediumvioletred': '#c71585',
            'midnightblue': '#191970',
            'mintcream': '#f5fffa',
            'mistyrose': '#ffe4e1',
            'moccasin': '#ffe4b5',
            'navajowhite': '#ffdead',
            'navy': '#000080',
            'oldlace': '#fdf5e6',
            'olive': '#808000',
            'olivedrab': '#6b8e23',
            'orange': '#ffa500',
            'orangered': '#ff4500',
            'orchid': '#da70d6',
            'palegoldenrod': '#eee8aa',
            'palegreen': '#98fb98',
            'paleturquoise': '#afeeee',
            'palevioletred': '#d87093',
            'papayawhip': '#ffefd5',
            'peachpuff': '#ffdab9',
            'peru': '#cd853f',
            'pink': '#ffc0cb',
            'plum': '#dda0dd',
            'powderblue': '#b0e0e6',
            'purple': '#800080',
            'red': '#ff0000',
            'rosybrown': '#bc8f8f',
            'royalblue': '#4169e1',
            'saddlebrown': '#8b4513',
            'salmon': '#fa8072',
            'sandybrown': '#f4a460',
            'seagreen': '#2e8b57',
            'seashell': '#fff5ee',
            'sienna': '#a0522d',
            'silver': '#c0c0c0',
            'skyblue': '#87ceeb',
            'slateblue': '#6a5acd',
            'slategray': '#708090',
            'slategrey': '#708090',
            'snow': '#fffafa',
            'springgreen': '#00ff7f',
            'steelblue': '#4682b4',
            'tan': '#d2b48c',
            'teal': '#008080',
            'thistle': '#d8bfd8',
            'tomato': '#ff6347',
            'turquoise': '#40e0d0',
            'violet': '#ee82ee',
            'wheat': '#f5deb3',
            'white': '#ffffff',
            'whitesmoke': '#f5f5f5',
            'yellow': '#ffff00',
            'yellowgreen': '#9acd32'
        }
    })(y('./tree'));
    (function(A) {
        A.Alpha = function(v) {
            this.value = v
        };
        A.Alpha.prototype = {
            toCSS: function() {
                return "alpha(opacity=" + (this.value.toCSS ? this.value.toCSS() : this.value) + ")"
            },
            eval: function(e) {
                if (this.value.eval) {
                    this.value = this.value.eval(e)
                }
                return this
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Anonymous = function(s) {
            this.value = s.value || s
        };
        A.Anonymous.prototype = {
            toCSS: function() {
                return this.value
            },
            eval: function() {
                return this
            },
            compare: function(x) {
                if (!x.toCSS) {
                    return -1
                }
                var l = this.toCSS(),
                    r = x.toCSS();
                if (l === r) {
                    return 0
                }
                return l < r ? -1 : 1
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Assignment = function(k, v) {
            this.key = k;
            this.value = v
        };
        A.Assignment.prototype = {
            toCSS: function() {
                return this.key + '=' + (this.value.toCSS ? this.value.toCSS() : this.value)
            },
            eval: function(e) {
                if (this.value.eval) {
                    return new(A.Assignment)(this.key, this.value.eval(e))
                }
                return this
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Call = function(n, a, b, f) {
            this.name = n;
            this.args = a;
            this.index = b;
            this.filename = f
        };
        A.Call.prototype = {
            eval: function(b) {
                var c = this.args.map(function(a) {
                    return a.eval(b)
                });
                if (this.name in A.functions) {
                    try {
                        return A.functions[this.name].apply(A.functions, c)
                    } catch (e) {
                        throw {
                            type: e.type || "Runtime",
                            message: "error evaluating function `" + this.name + "`" + (e.message ? ': ' + e.message : ''),
                            index: this.index,
                            filename: this.filename
                        }
                    }
                } else {
                    return new(A.Anonymous)(this.name + "(" + c.map(function(a) {
                        return a.toCSS(b)
                    }).join(', ') + ")")
                }
            },
            toCSS: function(e) {
                return this.eval(e).toCSS()
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Color = function(r, a) {
            if (Array.isArray(r)) {
                this.rgb = r
            } else if (r.length == 6) {
                this.rgb = r.match(/.{2}/g).map(function(c) {
                    return parseInt(c, 16)
                })
            } else {
                this.rgb = r.split('').map(function(c) {
                    return parseInt(c + c, 16)
                })
            }
            this.alpha = typeof(a) === 'number' ? a : 1
        };
        A.Color.prototype = {
            eval: function() {
                return this
            },
            toCSS: function() {
                if (this.alpha < 1.0) {
                    return "rgba(" + this.rgb.map(function(c) {
                        return Math.round(c)
                    }).concat(this.alpha).join(', ') + ")"
                } else {
                    return '#' + this.rgb.map(function(i) {
                        i = Math.round(i);
                        i = (i > 255 ? 255 : (i < 0 ? 0 : i)).toString(16);
                        return i.length === 1 ? '0' + i : i
                    }).join('')
                }
            },
            operate: function(o, a) {
                var r = [];
                if (!(a instanceof A.Color)) {
                    a = a.toColor()
                }
                for (var c = 0; c < 3; c++) {
                    r[c] = A.operate(o, this.rgb[c], a.rgb[c])
                }
                return new(A.Color)(r, this.alpha + a.alpha)
            },
            toHSL: function() {
                var r = this.rgb[0] / 255,
                    g = this.rgb[1] / 255,
                    b = this.rgb[2] / 255,
                    a = this.alpha;
                var m = Math.max(r, g, b),
                    c = Math.min(r, g, b);
                var h, s, l = (m + c) / 2,
                    d = m - c;
                if (m === c) {
                    h = s = 0
                } else {
                    s = l > 0.5 ? d / (2 - m - c) : d / (m + c);
                    switch (m) {
                        case r:
                            h = (g - b) / d + (g < b ? 6 : 0);
                            break;
                        case g:
                            h = (b - r) / d + 2;
                            break;
                        case b:
                            h = (r - g) / d + 4;
                            break
                    }
                    h /= 6
                }
                return {
                    h: h * 360,
                    s: s,
                    l: l,
                    a: a
                }
            },
            toARGB: function() {
                var a = [Math.round(this.alpha * 255)].concat(this.rgb);
                return '#' + a.map(function(i) {
                    i = Math.round(i);
                    i = (i > 255 ? 255 : (i < 0 ? 0 : i)).toString(16);
                    return i.length === 1 ? '0' + i : i
                }).join('')
            },
            compare: function(x) {
                if (!x.rgb) {
                    return -1
                }
                return (x.rgb[0] === this.rgb[0] && x.rgb[1] === this.rgb[1] && x.rgb[2] === this.rgb[2] && x.alpha === this.alpha) ? 0 : -1
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Comment = function(v, s) {
            this.value = v;
            this.silent = !! s
        };
        A.Comment.prototype = {
            toCSS: function(e) {
                return e.compress ? '' : this.value
            },
            eval: function() {
                return this
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Condition = function(o, l, r, i, n) {
            this.op = o.trim();
            this.lvalue = l;
            this.rvalue = r;
            this.index = i;
            this.negate = n
        };
        A.Condition.prototype.eval = function(e) {
            var a = this.lvalue.eval(e),
                b = this.rvalue.eval(e);
            var i = this.index,
                r;
            var r = (function(o) {
                switch (o) {
                    case 'and':
                        return a && b;
                    case 'or':
                        return a || b;
                    default:
                        if (a.compare) {
                            r = a.compare(b)
                        } else if (b.compare) {
                            r = b.compare(a)
                        } else {
                            throw {
                                type: "Type",
                                message: "Unable to perform comparison",
                                index: i
                            }
                        }
                        switch (r) {
                            case -1:
                                return o === '<' || o === '=<';
                            case 0:
                                return o === '=' || o === '>=' || o === '=<';
                            case 1:
                                return o === '>' || o === '>='
                        }
                }
            })(this.op);
            return this.negate ? !r : r
        }
    })(y('../tree'));
    (function(A) {
        A.Dimension = function(v, a) {
            this.value = parseFloat(v);
            this.unit = a || null
        };
        A.Dimension.prototype = {
            eval: function() {
                return this
            },
            toColor: function() {
                return new(A.Color)([this.value, this.value, this.value])
            },
            toCSS: function() {
                var c = this.value + this.unit;
                return c
            },
            operate: function(o, a) {
                return new(A.Dimension)(A.operate(o, this.value, a.value), this.unit || a.unit)
            },
            compare: function(o) {
                if (o instanceof A.Dimension) {
                    if (o.value > this.value) {
                        return -1
                    } else if (o.value < this.value) {
                        return 1
                    } else {
                        return 0
                    }
                } else {
                    return -1
                }
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Directive = function(n, v) {
            this.name = n;
            if (Array.isArray(v)) {
                this.ruleset = new(A.Ruleset)([], v);
                this.ruleset.allowImports = true
            } else {
                this.value = v
            }
        };
        A.Directive.prototype = {
            toCSS: function(c, e) {
                if (this.ruleset) {
                    this.ruleset.root = true;
                    return this.name + (e.compress ? '{' : ' {\n  ') + this.ruleset.toCSS(c, e).trim().replace(/\n/g, '\n  ') + (e.compress ? '}' : '\n}\n')
                } else {
                    return this.name + ' ' + this.value.toCSS() + ';\n'
                }
            },
            eval: function(e) {
                var a = this;
                if (this.ruleset) {
                    e.frames.unshift(this);
                    a = new(A.Directive)(this.name);
                    a.ruleset = this.ruleset.eval(e);
                    e.frames.shift()
                }
                return a
            },
            variable: function(n) {
                return A.Ruleset.prototype.variable.call(this.ruleset, n)
            },
            find: function() {
                return A.Ruleset.prototype.find.apply(this.ruleset, arguments)
            },
            rulesets: function() {
                return A.Ruleset.prototype.rulesets.apply(this.ruleset)
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Element = function(c, v, a) {
            this.combinator = c instanceof A.Combinator ? c : new(A.Combinator)(c);
            if (typeof(v) === 'string') {
                this.value = v.trim()
            } else if (v) {
                this.value = v
            } else {
                this.value = ""
            }
            this.index = a
        };
        A.Element.prototype.eval = function(e) {
            return new(A.Element)(this.combinator, this.value.eval ? this.value.eval(e) : this.value, this.index)
        };
        A.Element.prototype.toCSS = function(e) {
            var v = (this.value.toCSS ? this.value.toCSS(e) : this.value);
            if (v == '' && this.combinator.value.charAt(0) == '&') {
                return ''
            } else {
                return this.combinator.toCSS(e || {}) + v
            }
        };
        A.Combinator = function(v) {
            if (v === ' ') {
                this.value = ' '
            } else {
                this.value = v ? v.trim() : ""
            }
        };
        A.Combinator.prototype.toCSS = function(e) {
            return {
                '': '',
                ' ': ' ',
                ':': ' :',
                '+': e.compress ? '+' : ' + ',
                '~': e.compress ? '~' : ' ~ ',
                '>': e.compress ? '>' : ' > '
            }[this.value]
        }
    })(y('../tree'));
    (function(A) {
        A.Expression = function(v) {
            this.value = v
        };
        A.Expression.prototype = {
            eval: function(a) {
                if (this.value.length > 1) {
                    return new(A.Expression)(this.value.map(function(e) {
                        return e.eval(a)
                    }))
                } else if (this.value.length === 1) {
                    return this.value[0].eval(a)
                } else {
                    return this
                }
            },
            toCSS: function(a) {
                return this.value.map(function(e) {
                    return e.toCSS ? e.toCSS(a) : ''
                }).join(' ')
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Import = function(p, a, f, o, b) {
            var t = this;
            this.once = o;
            this.index = b;
            this._path = p;
            this.features = f && new(A.Value)(f);
            if (p instanceof A.Quoted) {
                this.path = /\.(le?|c)ss(\?.*)?$/.test(p.value) ? p.value : p.value + '.less'
            } else {
                this.path = p.value.value || p.value
            }
            this.css = /css(\?.*)?$/.test(this.path);
            if (!this.css) {
                a.push(this.path, function(e, r, c) {
                    if (e) {
                        e.index = b
                    }
                    if (c && t.once) t.skip = c;
                    t.root = r || new(A.Ruleset)([], [])
                })
            }
        };
        A.Import.prototype = {
            toCSS: function(e) {
                var f = this.features ? ' ' + this.features.toCSS(e) : '';
                if (this.css) {
                    return "@import " + this._path.toCSS() + f + ';\n'
                } else {
                    return ""
                }
            },
            eval: function(e) {
                var r, f = this.features && this.features.eval(e);
                if (this.skip) return [];
                if (this.css) {
                    return this
                } else {
                    r = new(A.Ruleset)([], this.root.rules.slice(0));
                    for (var i = 0; i < r.rules.length; i++) {
                        if (r.rules[i] instanceof A.Import) {
                            Array.prototype.splice.apply(r.rules, [i, 1].concat(r.rules[i].eval(e)))
                        }
                    }
                    return this.features ? new(A.Media)(r.rules, this.features.value) : r.rules
                }
            }
        }
    })(y('../tree'));
    (function(A) {
        A.JavaScript = function(s, a, e) {
            this.escaped = e;
            this.expression = s;
            this.index = a
        };
        A.JavaScript.prototype = {
            eval: function(a) {
                var r, t = this,
                    c = {};
                var b = this.expression.replace(/@\{([\w-]+)\}/g, function(_, n) {
                    return A.jsify(new(A.Variable)('@' + n, t.index).eval(a))
                });
                try {
                    b = new(Function)('return (' + b + ')')
                } catch (e) {
                    throw {
                        message: "JavaScript evaluation error: `" + b + "`",
                        index: this.index
                    }
                }
                for (var k in a.frames[0].variables()) {
                    c[k.slice(1)] = {
                        value: a.frames[0].variables()[k].value,
                        toJS: function() {
                            return this.value.eval(a).toCSS()
                        }
                    }
                }
                try {
                    r = b.call(c)
                } catch (e) {
                    throw {
                        message: "JavaScript evaluation error: '" + e.name + ': ' + e.message + "'",
                        index: this.index
                    }
                }
                if (typeof(r) === 'string') {
                    return new(A.Quoted)('"' + r + '"', r, this.escaped, this.index)
                } else if (Array.isArray(r)) {
                    return new(A.Anonymous)(r.join(', '))
                } else {
                    return new(A.Anonymous)(r)
                }
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Keyword = function(v) {
            this.value = v
        };
        A.Keyword.prototype = {
            eval: function() {
                return this
            },
            toCSS: function() {
                return this.value
            },
            compare: function(o) {
                if (o instanceof A.Keyword) {
                    return o.value === this.value ? 0 : 1
                } else {
                    return -1
                }
            }
        };
        A.True = new(A.Keyword)('true');
        A.False = new(A.Keyword)('false')
    })(y('../tree'));
    (function(A) {
        A.Media = function(v, f) {
            var s = this.emptySelectors();
            this.features = new(A.Value)(f);
            this.ruleset = new(A.Ruleset)(s, v);
            this.ruleset.allowImports = true
        };
        A.Media.prototype = {
            toCSS: function(c, e) {
                var f = this.features.toCSS(e);
                this.ruleset.root = (c.length === 0 || c[0].multiMedia);
                return '@media ' + f + (e.compress ? '{' : ' {\n  ') + this.ruleset.toCSS(c, e).trim().replace(/\n/g, '\n  ') + (e.compress ? '}' : '\n}\n')
            },
            eval: function(e) {
                if (!e.mediaBlocks) {
                    e.mediaBlocks = [];
                    e.mediaPath = []
                }
                var b = e.mediaBlocks.length;
                e.mediaPath.push(this);
                e.mediaBlocks.push(this);
                var m = new(A.Media)([], []);
                if (this.debugInfo) {
                    this.ruleset.debugInfo = this.debugInfo;
                    m.debugInfo = this.debugInfo
                }
                m.features = this.features.eval(e);
                e.frames.unshift(this.ruleset);
                m.ruleset = this.ruleset.eval(e);
                e.frames.shift();
                e.mediaBlocks[b] = m;
                e.mediaPath.pop();
                return e.mediaPath.length === 0 ? m.evalTop(e) : m.evalNested(e)
            },
            variable: function(n) {
                return A.Ruleset.prototype.variable.call(this.ruleset, n)
            },
            find: function() {
                return A.Ruleset.prototype.find.apply(this.ruleset, arguments)
            },
            rulesets: function() {
                return A.Ruleset.prototype.rulesets.apply(this.ruleset)
            },
            emptySelectors: function() {
                var e = new(A.Element)('', '&', 0);
                return [new(A.Selector)([e])]
            },
            evalTop: function(e) {
                var r = this;
                if (e.mediaBlocks.length > 1) {
                    var s = this.emptySelectors();
                    r = new(A.Ruleset)(s, e.mediaBlocks);
                    r.multiMedia = true
                }
                delete e.mediaBlocks;
                delete e.mediaPath;
                return r
            },
            evalNested: function(e) {
                var i, v, p = e.mediaPath.concat([this]);
                for (i = 0; i < p.length; i++) {
                    v = p[i].features instanceof A.Value ? p[i].features.value : p[i].features;
                    p[i] = Array.isArray(v) ? v : [v]
                }
                this.features = new(A.Value)(this.permute(p).map(function(p) {
                    p = p.map(function(f) {
                        return f.toCSS ? f : new(A.Anonymous)(f)
                    });
                    for (i = p.length - 1; i > 0; i--) {
                        p.splice(i, 0, new(A.Anonymous)("and"))
                    }
                    return new(A.Expression)(p)
                }));
                return new(A.Ruleset)([], [])
            },
            permute: function(a) {
                if (a.length === 0) {
                    return []
                } else if (a.length === 1) {
                    return a[0]
                } else {
                    var r = [];
                    var b = this.permute(a.slice(1));
                    for (var i = 0; i < b.length; i++) {
                        for (var j = 0; j < a[0].length; j++) {
                            r.push([a[0][j]].concat(b[i]))
                        }
                    }
                    return r
                }
            },
            bubbleSelectors: function(s) {
                this.ruleset = new(A.Ruleset)(s.slice(0), [this.ruleset])
            }
        }
    })(y('../tree'));
    (function(A) {
        A.mixin = {};
        A.mixin.Call = function(e, a, b, f, c) {
            this.selector = new(A.Selector)(e);
            this.arguments = a;
            this.index = b;
            this.filename = f;
            this.important = c
        };
        A.mixin.Call.prototype = {
            eval: function(b) {
                var c, d, r = [],
                    f = false;
                for (var i = 0; i < b.frames.length; i++) {
                    if ((c = b.frames[i].find(this.selector)).length > 0) {
                        d = this.arguments && this.arguments.map(function(a) {
                            return {
                                name: a.name,
                                value: a.value.eval(b)
                            }
                        });
                        for (var m = 0; m < c.length; m++) {
                            if (c[m].match(d, b)) {
                                try {
                                    Array.prototype.push.apply(r, c[m].eval(b, this.arguments, this.important).rules);
                                    f = true
                                } catch (e) {
                                    throw {
                                        message: e.message,
                                        index: this.index,
                                        filename: this.filename,
                                        stack: e.stack
                                    }
                                }
                            }
                        }
                        if (f) {
                            return r
                        } else {
                            throw {
                                type: 'Runtime',
                                message: 'No matching definition was found for `' + this.selector.toCSS().trim() + '(' + this.arguments.map(function(a) {
                                    return a.toCSS()
                                }).join(', ') + ")`",
                                index: this.index,
                                filename: this.filename
                            }
                        }
                    }
                }
                throw {
                    type: 'Name',
                    message: this.selector.toCSS().trim() + " is undefined",
                    index: this.index,
                    filename: this.filename
                }
            }
        };
        A.mixin.Definition = function(n, a, r, c, v) {
            this.name = n;
            this.selectors = [new(A.Selector)([new(A.Element)(null, n)])];
            this.params = a;
            this.condition = c;
            this.variadic = v;
            this.arity = a.length;
            this.rules = r;
            this._lookups = {};
            this.required = a.reduce(function(b, p) {
                if (!p.name || (p.name && !p.value)) {
                    return b + 1
                } else {
                    return b
                }
            }, 0);
            this.parent = A.Ruleset.prototype;
            this.frames = []
        };
        A.mixin.Definition.prototype = {
            toCSS: function() {
                return ""
            },
            variable: function(n) {
                return this.parent.variable.call(this, n)
            },
            variables: function() {
                return this.parent.variables.call(this)
            },
            find: function() {
                return this.parent.find.apply(this, arguments)
            },
            rulesets: function() {
                return this.parent.rulesets.apply(this)
            },
            evalParams: function(e, a) {
                var f = new(A.Ruleset)(null, []),
                    v, b;
                for (var i = 0, c, n; i < this.params.length; i++) {
                    b = a && a[i];
                    if (b && b.name) {
                        f.rules.unshift(new(A.Rule)(b.name, b.value.eval(e)));
                        a.splice(i, 1);
                        i--;
                        continue
                    }
                    if (n = this.params[i].name) {
                        if (this.params[i].variadic && a) {
                            v = [];
                            for (var j = i; j < a.length; j++) {
                                v.push(a[j].value.eval(e))
                            }
                            f.rules.unshift(new(A.Rule)(n, new(A.Expression)(v).eval(e)))
                        } else if (c = (b && b.value) || this.params[i].value) {
                            f.rules.unshift(new(A.Rule)(n, c.eval(e)))
                        } else {
                            throw {
                                type: 'Runtime',
                                message: "wrong number of arguments for " + this.name + ' (' + a.length + ' for ' + this.arity + ')'
                            }
                        }
                    }
                }
                return f
            },
            eval: function(e, a, b) {
                var f = this.evalParams(e, a),
                    c, d = [],
                    g, s;
                for (var i = 0; i < Math.max(this.params.length, a && a.length); i++) {
                    d.push((a[i] && a[i].value) || this.params[i].value)
                }
                f.rules.unshift(new(A.Rule)('@arguments', new(A.Expression)(d).eval(e)));
                g = b ? this.rules.map(function(r) {
                    return new(A.Rule)(r.name, r.value, '!important', r.index)
                }) : this.rules.slice(0);
                return new(A.Ruleset)(null, g).eval({
                    frames: [this, f].concat(this.frames, e.frames)
                })
            },
            match: function(a, e) {
                var b = (a && a.length) || 0,
                    l, f;
                if (!this.variadic) {
                    if (b < this.required) {
                        return false
                    }
                    if (b > this.params.length) {
                        return false
                    }
                    if ((this.required > 0) && (b > this.params.length)) {
                        return false
                    }
                }
                if (this.condition && !this.condition.eval({
                    frames: [this.evalParams(e, a)].concat(e.frames)
                })) {
                    return false
                }
                l = Math.min(b, this.arity);
                for (var i = 0; i < l; i++) {
                    if (!this.params[i].name) {
                        if (a[i].value.eval(e).toCSS() != this.params[i].value.eval(e).toCSS()) {
                            return false
                        }
                    }
                }
                return true
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Operation = function(o, a) {
            this.op = o.trim();
            this.operands = a
        };
        A.Operation.prototype.eval = function(e) {
            var a = this.operands[0].eval(e),
                b = this.operands[1].eval(e),
                t;
            if (a instanceof A.Dimension && b instanceof A.Color) {
                if (this.op === '*' || this.op === '+') {
                    t = b, b = a, a = t
                } else {
                    throw {
                        name: "OperationError",
                        message: "Can't substract or divide a color from a number"
                    }
                }
            }
            return a.operate(this.op, b)
        };
        A.operate = function(o, a, b) {
            switch (o) {
                case '+':
                    return a + b;
                case '-':
                    return a - b;
                case '*':
                    return a * b;
                case '/':
                    return a / b
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Paren = function(n) {
            this.value = n
        };
        A.Paren.prototype = {
            toCSS: function(e) {
                return '(' + this.value.toCSS(e) + ')'
            },
            eval: function(e) {
                return new(A.Paren)(this.value.eval(e))
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Quoted = function(s, c, e, i) {
            this.escaped = e;
            this.value = c || '';
            this.quote = s.charAt(0);
            this.index = i
        };
        A.Quoted.prototype = {
            toCSS: function() {
                if (this.escaped) {
                    return this.value
                } else {
                    return this.quote + this.value + this.quote
                }
            },
            eval: function(e) {
                var t = this;
                var a = this.value.replace(/`([^`]+)`/g, function(_, b) {
                    return new(A.JavaScript)(b, t.index, true).eval(e).value
                }).replace(/@\{([\w-]+)\}/g, function(_, n) {
                    var v = new(A.Variable)('@' + n, t.index).eval(e);
                    return ('value' in v) ? v.value : v.toCSS()
                });
                return new(A.Quoted)(this.quote + a + this.quote, a, this.escaped, this.index)
            },
            compare: function(x) {
                if (!x.toCSS) {
                    return -1
                }
                var l = this.toCSS(),
                    r = x.toCSS();
                if (l === r) {
                    return 0
                }
                return l < r ? -1 : 1
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Ratio = function(v) {
            this.value = v
        };
        A.Ratio.prototype = {
            toCSS: function(e) {
                return this.value
            },
            eval: function() {
                return this
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Rule = function(n, v, a, b, c) {
            this.name = n;
            this.value = (v instanceof A.Value) ? v : new(A.Value)([v]);
            this.important = a ? ' ' + a.trim() : '';
            this.index = b;
            this.inline = c || false;
            if (n.charAt(0) === '@') {
                this.variable = true
            } else {
                this.variable = false
            }
        };
        A.Rule.prototype.toCSS = function(e) {
            if (this.variable) {
                return ""
            } else {
                return this.name + (e.compress ? ':' : ': ') + this.value.toCSS(e) + this.important + (this.inline ? "" : ";")
            }
        };
        A.Rule.prototype.eval = function(c) {
            return new(A.Rule)(this.name, this.value.eval(c), this.important, this.index, this.inline)
        };
        A.Shorthand = function(a, b) {
            this.a = a;
            this.b = b
        };
        A.Shorthand.prototype = {
            toCSS: function(e) {
                return this.a.toCSS(e) + "/" + this.b.toCSS(e)
            },
            eval: function() {
                return this
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Ruleset = function(s, r, a) {
            this.selectors = s;
            this.rules = r;
            this._lookups = {};
            this.strictImports = a
        };
        A.Ruleset.prototype = {
            eval: function(e) {
                var a = this.selectors && this.selectors.map(function(s) {
                    return s.eval(e)
                });
                var r = new(A.Ruleset)(a, this.rules.slice(0), this.strictImports);
                var b = [];
                r.root = this.root;
                r.allowImports = this.allowImports;
                if (this.debugInfo) {
                    r.debugInfo = this.debugInfo
                }
                e.frames.unshift(r);
                if (r.root || r.allowImports || !r.strictImports) {
                    for (var i = 0; i < r.rules.length; i++) {
                        if (r.rules[i] instanceof A.Import) {
                            b = b.concat(r.rules[i].eval(e))
                        } else {
                            b.push(r.rules[i])
                        }
                    }
                    r.rules = b;
                    b = []
                }
                for (var i = 0; i < r.rules.length; i++) {
                    if (r.rules[i] instanceof A.mixin.Definition) {
                        r.rules[i].frames = e.frames.slice(0)
                    }
                }
                var m = (e.mediaBlocks && e.mediaBlocks.length) || 0;
                for (var i = 0; i < r.rules.length; i++) {
                    if (r.rules[i] instanceof A.mixin.Call) {
                        b = b.concat(r.rules[i].eval(e))
                    } else {
                        b.push(r.rules[i])
                    }
                }
                r.rules = b;
                for (var i = 0, c; i < r.rules.length; i++) {
                    c = r.rules[i];
                    if (!(c instanceof A.mixin.Definition)) {
                        r.rules[i] = c.eval ? c.eval(e) : c
                    }
                }
                e.frames.shift();
                if (e.mediaBlocks) {
                    for (var i = m; i < e.mediaBlocks.length; i++) {
                        e.mediaBlocks[i].bubbleSelectors(a)
                    }
                }
                return r
            },
            match: function(a) {
                return !a || a.length === 0
            },
            variables: function() {
                if (this._variables) {
                    return this._variables
                } else {
                    return this._variables = this.rules.reduce(function(h, r) {
                        if (r instanceof A.Rule && r.variable === true) {
                            h[r.name] = r
                        }
                        return h
                    }, {})
                }
            },
            variable: function(n) {
                return this.variables()[n]
            },
            rulesets: function() {
                if (this._rulesets) {
                    return this._rulesets
                } else {
                    return this._rulesets = this.rules.filter(function(r) {
                        return (r instanceof A.Ruleset) || (r instanceof A.mixin.Definition)
                    })
                }
            },
            find: function(s, a) {
                a = a || this;
                var r = [],
                    b, m, k = s.toCSS();
                if (k in this._lookups) {
                    return this._lookups[k]
                }
                this.rulesets().forEach(function(b) {
                    if (b !== a) {
                        for (var j = 0; j < b.selectors.length; j++) {
                            if (m = s.match(b.selectors[j])) {
                                if (s.elements.length > b.selectors[j].elements.length) {
                                    Array.prototype.push.apply(r, b.find(new(A.Selector)(s.elements.slice(1)), a))
                                } else {
                                    r.push(b)
                                }
                                break
                            }
                        }
                    }
                });
                return this._lookups[k] = r
            },
            toCSS: function(c, e) {
                var a = [],
                    r = [],
                    b = [],
                    d = [],
                    f = [],
                    g, h, j;
                if (!this.root) {
                    this.joinSelectors(f, c, this.selectors)
                }
                for (var i = 0; i < this.rules.length; i++) {
                    j = this.rules[i];
                    if (j.rules || (j instanceof A.Directive) || (j instanceof A.Media)) {
                        d.push(j.toCSS(f, e))
                    } else if (j instanceof A.Comment) {
                        if (!j.silent) {
                            if (this.root) {
                                d.push(j.toCSS(e))
                            } else {
                                r.push(j.toCSS(e))
                            }
                        }
                    } else {
                        if (j.toCSS && !j.variable) {
                            r.push(j.toCSS(e))
                        } else if (j.value && !j.variable) {
                            r.push(j.value.toString())
                        }
                    }
                }
                d = d.join('');
                if (this.root) {
                    a.push(r.join(e.compress ? '' : '\n'))
                } else {
                    if (r.length > 0) {
                        h = A.debugInfo(e, this);
                        g = f.map(function(p) {
                            return p.map(function(s) {
                                return s.toCSS(e)
                            }).join('').trim()
                        }).join(e.compress ? ',' : ',\n');
                        for (var i = r.length - 1; i >= 0; i--) {
                            if (b.indexOf(r[i]) === -1) {
                                b.unshift(r[i])
                            }
                        }
                        r = b;
                        a.push(h + g + (e.compress ? '{' : ' {\n  ') + r.join(e.compress ? '' : '\n  ') + (e.compress ? '}' : '\n}\n'))
                    }
                }
                a.push(d);
                return a.join('') + (e.compress ? '\n' : '')
            },
            joinSelectors: function(p, c, a) {
                for (var s = 0; s < a.length; s++) {
                    this.joinSelector(p, c, a[s])
                }
            },
            joinSelector: function(p, c, s) {
                var i, j, k, h, n, e, a, b, d, f, g, l, m, o, r;
                for (i = 0; i < s.elements.length; i++) {
                    e = s.elements[i];
                    if (e.value === '&') {
                        h = true
                    }
                }
                if (!h) {
                    if (c.length > 0) {
                        for (i = 0; i < c.length; i++) {
                            p.push(c[i].concat(s))
                        }
                    } else {
                        p.push([s])
                    }
                    return
                }
                o = [];
                n = [
                    []
                ];
                for (i = 0; i < s.elements.length; i++) {
                    e = s.elements[i];
                    if (e.value !== "&") {
                        o.push(e)
                    } else {
                        r = [];
                        if (o.length > 0) {
                            this.mergeElementsOnToSelectors(o, n)
                        }
                        for (j = 0; j < n.length; j++) {
                            a = n[j];
                            if (c.length == 0) {
                                if (a.length > 0) {
                                    a[0].elements = a[0].elements.slice(0);
                                    a[0].elements.push(new(A.Element)(e.combinator, '', 0))
                                }
                                r.push(a)
                            } else {
                                for (k = 0; k < c.length; k++) {
                                    b = c[k];
                                    d = [];
                                    f = [];
                                    l = true;
                                    if (a.length > 0) {
                                        d = a.slice(0);
                                        m = d.pop();
                                        g = new(A.Selector)(m.elements.slice(0));
                                        l = false
                                    } else {
                                        g = new(A.Selector)([])
                                    }
                                    if (b.length > 1) {
                                        f = f.concat(b.slice(1))
                                    }
                                    if (b.length > 0) {
                                        l = false;
                                        g.elements.push(new(A.Element)(e.combinator, b[0].elements[0].value, 0));
                                        g.elements = g.elements.concat(b[0].elements.slice(1))
                                    }
                                    if (!l) {
                                        d.push(g)
                                    }
                                    d = d.concat(f);
                                    r.push(d)
                                }
                            }
                        }
                        n = r;
                        o = []
                    }
                }
                if (o.length > 0) {
                    this.mergeElementsOnToSelectors(o, n)
                }
                for (i = 0; i < n.length; i++) {
                    p.push(n[i])
                }
            },
            mergeElementsOnToSelectors: function(e, s) {
                var i, a;
                if (s.length == 0) {
                    s.push([new(A.Selector)(e)]);
                    return
                }
                for (i = 0; i < s.length; i++) {
                    a = s[i];
                    if (a.length > 0) {
                        a[a.length - 1] = new(A.Selector)(a[a.length - 1].elements.concat(e))
                    } else {
                        a.push(new(A.Selector)(e))
                    }
                }
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Selector = function(e) {
            this.elements = e
        };
        A.Selector.prototype.match = function(o) {
            var l = this.elements.length,
                a = o.elements.length,
                m = Math.min(l, a);
            if (l < a) {
                return false
            } else {
                for (var i = 0; i < m; i++) {
                    if (this.elements[i].value !== o.elements[i].value) {
                        return false
                    }
                }
            }
            return true
        };
        A.Selector.prototype.eval = function(a) {
            return new(A.Selector)(this.elements.map(function(e) {
                return e.eval(a)
            }))
        };
        A.Selector.prototype.toCSS = function(a) {
            if (this._css) {
                return this._css
            }
            if (this.elements[0].combinator.value === "") {
                this._css = ' '
            } else {
                this._css = ''
            }
            this._css += this.elements.map(function(e) {
                if (typeof(e) === 'string') {
                    return ' ' + e.trim()
                } else {
                    return e.toCSS(a)
                }
            }).join('');
            return this._css
        }
    })(y('../tree'));
    (function(A) {
        A.URL = function(v, p) {
            this.value = v;
            this.paths = p
        };
        A.URL.prototype = {
            toCSS: function() {
                return "url(" + this.value.toCSS() + ")"
            },
            eval: function(c) {
                var v = this.value.eval(c);
                if (typeof q !== 'undefined' && typeof v.value === "string" && !/^(?:[a-z-]+:|\/)/.test(v.value) && this.paths.length > 0) {
                    v.value = this.paths[0] + (v.value.charAt(0) === '/' ? v.value.slice(1) : v.value)
                }
                return new(A.URL)(v, this.paths)
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Value = function(v) {
            this.value = v;
            this.is = 'value'
        };
        A.Value.prototype = {
            eval: function(e) {
                if (this.value.length === 1) {
                    return this.value[0].eval(e)
                } else {
                    return new(A.Value)(this.value.map(function(v) {
                        return v.eval(e)
                    }))
                }
            },
            toCSS: function(a) {
                return this.value.map(function(e) {
                    return e.toCSS(a)
                }).join(a.compress ? ',' : ', ')
            }
        }
    })(y('../tree'));
    (function(A) {
        A.Variable = function(n, a, f) {
            this.name = n, this.index = a, this.file = f
        };
        A.Variable.prototype = {
            eval: function(e) {
                var a, v, n = this.name;
                if (n.indexOf('@@') == 0) {
                    n = '@' + new(A.Variable)(n.slice(1)).eval(e).value
                }
                if (a = A.find(e.frames, function(f) {
                    if (v = f.variable(n)) {
                        return v.value.eval(e)
                    }
                })) {
                    return a
                } else {
                    throw {
                        type: 'Name',
                        message: "variable " + n + " is undefined",
                        filename: this.file,
                        index: this.index
                    }
                }
            }
        }
    })(y('../tree'));
    (function(A) {
        A.debugInfo = function(e, c) {
            var r = "";
            if (e.dumpLineNumbers && !e.compress) {
                switch (e.dumpLineNumbers) {
                    case 'comments':
                        r = A.debugInfo.asComment(c);
                        break;
                    case 'mediaquery':
                        r = A.debugInfo.asMediaQuery(c);
                        break;
                    case 'all':
                        r = A.debugInfo.asComment(c) + A.debugInfo.asMediaQuery(c);
                        break
                }
            }
            return r
        };
        A.debugInfo.asComment = function(c) {
            return '/* line ' + c.debugInfo.lineNumber + ', ' + c.debugInfo.fileName + ' */\n'
        };
        A.debugInfo.asMediaQuery = function(c) {
            return '@media -sass-debug-info{filename{font-family:"' + c.debugInfo.fileName + '";}line{font-family:"' + c.debugInfo.lineNumber + '";}}\n'
        };
        A.find = function(o, f) {
            for (var i = 0, r; i < o.length; i++) {
                if (r = f.call(o, o[i])) {
                    return r
                }
            }
            return null
        };
        A.jsify = function(o) {
            if (Array.isArray(o.value) && (o.value.length > 1)) {
                return '[' + o.value.map(function(v) {
                    return v.toCSS(false)
                }).join(', ') + ']'
            } else {
                return o.toCSS(false)
            }
        }
    })(y('./tree'));
    var B = /^(file|chrome(-extension)?|resource|qrc|app):/.test(location.protocol);
    z.env = z.env || (location.hostname == '127.0.0.1' || location.hostname == '0.0.0.0' || location.hostname == 'localhost' || location.port.length > 0 || B ? 'development' : 'production');
    z.async = z.async ||false;
    z.fileAsync = z.fileAsync || false;
    z.poll = z.poll || (B ? 1000 : 1500);
    z.watch = function() {
        return this.watchMode = true
    };
    z.unwatch = function() {
        return this.watchMode = false
    };
    if (z.env === 'development') {
        z.optimization = 0;
        if (/!watch/.test(location.hash)) {
            z.watch()
        }
        var C = /!dumpLineNumbers:(comments|mediaquery|all)/.exec(location.hash);
        if (C) {
            z.dumpLineNumbers = C[1]
        }
        z.watchTimer = setInterval(function() {
            if (z.watchMode) {
                H(function(e, r, _, s, a) {
                    if (r) {
                        K(r.toCSS(), s, a.lastModified)
                    }
                })
            }
        }, z.poll)
    } else {
        z.optimization = 3
    }
    var D;
    try {
        D = (typeof(q.localStorage) === 'undefined') ? null : q.localStorage
    } catch (_) {
        D = null
    }
    var E = document.getElementsByTagName('link');
    var F = /^text\/(x-)?less$/;
    z.sheets = [];
    for (var i = 0; i < E.length; i++) {
        if (E[i].rel === 'stylesheet/less' || (E[i].rel.match(/stylesheet/) && (E[i].type.match(F)))) {
            z.sheets.push(E[i])
        }
    }
    z.refresh = function(r) {
        var s, a;
        s = a = new(Date);
        H(function(e, b, _, c, d) {
            if (d.local) {
                O("loading " + c.href + " from cache.")
            } else {
                O("parsed " + c.href + " successfully.");
                K(b.toCSS(), c, d.lastModified)
            }
            O("css for " + c.href + " generated in " + (new(Date) - a) + 'ms');
            (d.remaining === 0) && O("css generated in " + (new(Date) - s) + 'ms');
            a = new(Date)
        }, r);
        G()
    };
    z.refreshStyles = G;
    z.refresh(z.env === 'development');

    function G() {
        var s = document.getElementsByTagName('style');
        for (var i = 0; i < s.length; i++) {
            if (s[i].type.match(F)) {
                new(z.Parser)({
                    filename: document.location.href.replace(/#.*$/, ''),
                    dumpLineNumbers: z.dumpLineNumbers
                }).parse(s[i].innerHTML || '', function(e, A) {
                    var c = A.toCSS();
                    var a = s[i];
                    a.type = 'text/css';
                    if (a.styleSheet) {
                        a.styleSheet.cssText = c
                    } else {
                        a.innerHTML = c
                    }
                })
            }
        }
    }
    function H(c, r) {
        for (var i = 0; i < z.sheets.length; i++) {
            I(z.sheets[i], c, r, z.sheets.length - (i + 1))
        }
    }
    function I(s, c, r, a) {
        var b = s.contents || {};
        var d = q.location.href.replace(/[#?].*$/, '');
        var h = s.href.replace(/\?.*$/, '');
        var f = D && D.getItem(h);
        var t = D && D.getItem(h + ':timestamp');
        var g = {
            css: f,
            timestamp: t
        };
        if (!/^[a-z-]+:/.test(h)) {
            if (h.charAt(0) == "/") {
                h = q.location.protocol + "//" + q.location.host + h
            } else {
                h = d.slice(0, d.lastIndexOf('/') + 1) + h
            }
        }
        L(s.href, s.type, function(j, l) {
            if (!r && g && l && (new(Date)(l).valueOf() === new(Date)(g.timestamp).valueOf())) {
                K(g.css, s);
                c(null, null, j, s, {
                    local: true,
                    remaining: a
                })
            } else {
                try {
                    b[h] = j;
                    new(z.Parser)({
                        optimization: z.optimization,
                        paths: [h.replace(/[\w\.-]+$/, '')],
                        mime: s.type,
                        filename: h,
                        'contents': b,
                        dumpLineNumbers: z.dumpLineNumbers
                    }).parse(j, function(e, k) {
                        if (e) {
                            return P(e, h)
                        }
                        try {
                            c(e, k, j, s, {
                                local: false,
                                lastModified: l,
                                remaining: a
                            });
                            N(document.getElementById('less-error-message:' + J(h)))
                        } catch (e) {
                            P(e, h)
                        }
                    })
                } catch (e) {
                    P(e, h)
                }
            }
        }, function(e, d) {
            throw new(Error)("Couldn't load " + d + " (" + e + ")")
        })
    }
    function J(h) {
        return h.replace(/^[a-z]+:\/\/?[^\/]+/, '').replace(/^\//, '').replace(/\?.*$/, '').replace(/\.[^\.\/]+$/, '').replace(/[^\.\w-]+/g, '-').replace(/\./g, ':')
    }
    function K(s, a, l) {
        var c;
        var h = a.href ? a.href.replace(/\?.*$/, '') : '';
        var b = 'less:' + (a.title || J(h));
        if ((c = document.getElementById(b)) === null) {
            c = document.createElement('style');
            c.type = 'text/css';
            if (a.media) {
                c.media = a.media
            }
            c.id = b;
            var n = a && a.nextSibling || null;
            document.getElementsByTagName('head')[0].insertBefore(c, n)
        }
        if (c.styleSheet) {
            try {
                c.styleSheet.cssText = s
            } catch (e) {
                throw new(Error)("Couldn't reassign styleSheet.cssText.")
            }
        } else {
            (function(d) {
                if (c.childNodes.length > 0) {
                    if (c.firstChild.nodeValue !== d.nodeValue) {
                        c.replaceChild(d, c.firstChild)
                    }
                } else {
                    c.appendChild(d)
                }
            })(document.createTextNode(s))
        }
        if (l && D) {
            O('saving ' + h + ' to cache.');
            try {
                D.setItem(h, s);
                D.setItem(h + ':timestamp', l)
            } catch (e) {
                O('failed to save')
            }
        }
    }
    function L(a, t, c, e) {
        var L = M();
        var b = B ? z.fileAsync : z.async;
        if (typeof(L.overrideMimeType) === 'function') {
            L.overrideMimeType('text/css')
        }
        L.open('GET', a, b);
        L.setRequestHeader('Accept', t || 'text/x-less, text/css; q=0.9, */*; q=0.5');
        L.send(null);
        if (B && !z.fileAsync) {
            if (L.status === 0 || (L.status >= 200 && L.status < 300)) {
                c(L.responseText)
            } else {
                e(L.status, a)
            }
        } else if (b) {
            L.onreadystatechange = function() {
                if (L.readyState == 4) {
                    h(L, c, e)
                }
            }
        } else {
            h(L, c, e)
        }
        function h(L, c, e) {
            if (L.status >= 200 && L.status < 300) {
                c(L.responseText, L.getResponseHeader("Last-Modified"))
            } else if (typeof(e) === 'function') {
                e(L.status, a)
            }
        }
    }
    function M() {
        if (q.XMLHttpRequest) {
            return new(XMLHttpRequest)
        } else {
            try {
                return new(ActiveXObject)("MSXML2.XMLHTTP.3.0")
            } catch (e) {
                O("browser doesn't support AJAX.");
                return null
            }
        }
    }
    function N(n) {
        return n && n.parentNode.removeChild(n)
    }
    function O(s) {
        if (z.env == 'development' && typeof(console) !== "undefined") {
            console.log('less: ' + s)
        }
    }
    function P(e, h) {
        var a = 'less-error-message:' + J(h);
        var t = '<li><label>{line}</label><pre class="{class}">{content}</pre></li>';
        var b = document.createElement('div'),
            c, d, P = [];
        var f = e.filename || h;
        var g = f.match(/([^\/]+)$/)[1];
        b.id = a;
        b.className = "less-error-message";
        d = '<h3>' + (e.message || 'There is an error in your .less file') + '</h3>' + '<p>in <a href="' + f + '">' + g + "</a> ";
        var j = function(e, i, k) {
            if (e.extract[i]) {
                P.push(t.replace(/\{line\}/, parseInt(e.line) + (i - 1)).replace(/\{class\}/, k).replace(/\{content\}/, e.extract[i]))
            }
        };
        if (e.stack) {
            d += '<br/>' + e.stack.split('\n').slice(1).join('<br/>')
        } else if (e.extract) {
            j(e, 0, '');
            j(e, 1, 'line');
            j(e, 2, '');
            d += 'on line ' + e.line + ', column ' + (e.column + 1) + ':</p>' + '<ul>' + P.join('') + '</ul>'
        }
        b.innerHTML = d;
        K(['.less-error-message ul, .less-error-message li {', 'list-style-type: none;', 'margin-right: 15px;', 'padding: 4px 0;', 'margin: 0;', '}', '.less-error-message label {', 'font-size: 12px;', 'margin-right: 15px;', 'padding: 4px 0;', 'color: #cc7777;', '}', '.less-error-message pre {', 'color: #dd6666;', 'padding: 4px 0;', 'margin: 0;', 'display: inline-block;', '}', '.less-error-message pre.line {', 'color: #ff0000;', '}', '.less-error-message h3 {', 'font-size: 20px;', 'font-weight: bold;', 'padding: 15px 0 5px 0;', 'margin: 0;', '}', '.less-error-message a {', 'color: #10a', '}', '.less-error-message .error {', 'color: red;', 'font-weight: bold;', 'padding-bottom: 2px;', 'border-bottom: 1px dashed red;', '}'].join('\n'), {
            title: 'error-message'
        });
        b.style.cssText = ["font-family: Arial, sans-serif", "border: 1px solid #e00", "background-color: #eee", "border-radius: 5px", "-webkit-border-radius: 5px", "-moz-border-radius: 5px", "color: #e00", "padding: 15px", "margin-bottom: 15px"].join(';');
        if (z.env == 'development') {
            c = setInterval(function() {
                if (document.body) {
                    if (document.getElementById(a)) {
                        document.body.replaceChild(b, document.getElementById(a))
                    } else {
                        document.body.insertBefore(b, document.body.firstChild)
                    }
                    clearInterval(c)
                }
            }, 10)
        }
    }
    if (typeof define === "function" && define.amd) {
        define("less", [], function() {
            return z
        })
    }
})(window);