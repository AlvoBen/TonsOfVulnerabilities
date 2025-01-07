﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("jquery.sap.script", false);
(function() {
    var I = 0;
    jQuery.sap.uid = function uid() {
        return "id-" + new Date().valueOf() + "-" + I++
    };
    jQuery.sap.delayedCall = function delayedCall(d, o, m, p) {
        return setTimeout(function() {
            if (jQuery.type(m) == "string") {
                m = o[m]
            }
            m.apply(o, p || [])
        }, d)
    };
    jQuery.sap.clearDelayedCall = function clearDelayedCall(d) {
        clearTimeout(d);
        return this
    };
    jQuery.sap.intervalCall = function intervalCall(i, o, m, p) {
        return setInterval(function() {
            if (jQuery.type(m) == "string") {
                m = o[m]
            }
            m.apply(o, p || [])
        }, i)
    };
    jQuery.sap.clearIntervalCall = function clearIntervalCall(i) {
        clearInterval(i);
        return this
    };
    var U = function(u) {
        this.mParams = {};
        var q = u || window.location.href;
        if (q.indexOf('#') >= 0) {
            q = q.slice(0, q.indexOf('#'))
        }
        if (q.indexOf("?") >= 0) {
            q = q.slice(q.indexOf("?") + 1);
            var p = q.split("&"),
                P = {}, a, n, v;
            for (var i = 0; i < p.length; i++) {
                a = p[i].split("=");
                n = decodeURIComponent(a[0]);
                v = a.length > 1 ? decodeURIComponent(a[1].replace(/\+/g, ' ')) : "";
                if (n) {
                    if (!Object.prototype.hasOwnProperty.call(P, n)) {
                        P[n] = []
                    }
                    P[n].push(v)
                }
            }
            this.mParams = P
        }
    };
    U.prototype = {};
    U.prototype.get = function(n, a) {
        var v = Object.prototype.hasOwnProperty.call(this.mParams, n) ? this.mParams[n] : [];
        return a === true ? v : (v[0] || null)
    };
    jQuery.sap.getUriParameters = function getUriParameters(u) {
        return new U(u)
    };
    jQuery.sap.unique = function(a) {
        var l = a.length;
        if (l > 1) {
            a.sort();
            for (var i = 1, j = 0; i < l; i++) {
                if (a[i] !== a[j]) {
                    a[++j] = a[i]
                }
            }
            if (++j < l) {
                a.splice(j, l - j)
            }
        }
        return a
    };
    jQuery.sap.equal = function(a, b, m, d) {
        if (!d) d = 0;
        if (!m) m = 10;
        if (d > m) return false;
        if (a === b) return true;
        if (jQuery.isArray(a) && jQuery.isArray(b)) {
            if (a.length != b.length) {
                return false
            }
            for (var i = 0; i < a.length; i++) {
                if (!jQuery.sap.equal(a[i], b[i], m, d + 1)) {
                    return false
                }
            }
            return true
        }
        if (typeof a == "object" && typeof b == "object") {
            if (!a || !b) {
                return false
            }
            if (a.constructor != b.constructor) {
                return false
            }
            if (a.nodeName && b.nodeName && a.namespaceURI && b.namespaceURI) {
                return jQuery.sap.isEqualNode(a, b)
            }
            if (a instanceof Date) {
                return a.valueOf() == b.valueOf()
            }
            for (var i in a) {
                if (!jQuery.sap.equal(a[i], b[i], m, d + 1)) {
                    return false
                }
            }
            for (var i in b) {
                if (a[i] === undefined) {
                    return false
                }
            }
            return true
        }
        return false
    };
    jQuery.sap.each = function(o, c) {
        var a = jQuery.isArray(o),
            l, i;
        if (a) {
            for (i = 0, l = o.length; i < l; i++) {
                if (c.call(o[i], i, o[i]) === false) {
                    break
                }
            }
        } else {
            for (i in o) {
                if (c.call(o[i], i, o[i]) === false) {
                    break
                }
            }
        }
        return o
    };
    jQuery.sap.forIn = {
        toString: null
    }.propertyIsEnumerable("toString") ? function(o, c) {
        for (var n in o) {
            if (c(n, o[n]) === false) {
                return
            }
        }
    } : (function() {
        var D = ["toString", "valueOf", "toLocaleString", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "constructor"],
            a = D.length,
            o = Object.prototype,
            h = o.hasOwnProperty;
        return function(O, c) {
            var n, i;
            for (n in O) {
                if (c(n, O[n]) === false) {
                    return
                }
            }
            for (var i = 0; i < a; i++) {
                n = D[i];
                if (h.call(O, n) || O[n] !== o[n]) {
                    if (c(n, O[n]) === false) {
                        return
                    }
                }
            }
        }
    }());
    jQuery.sap.arrayDiff = function(o, n, c) {
        c = c || function(v, V) {
            return jQuery.sap.equal(v, V)
        };
        var O = [];
        var N = [];
        var m = [];
        for (var i = 0; i < n.length; i++) {
            var a = n[i];
            var f = 0;
            var t;
            for (var j = 0; j < o.length; j++) {
                if (c(o[j], a)) {
                    f++;
                    t = j;
                    if (f > 1) {
                        break
                    }
                }
            }
            if (f == 1) {
                var M = {
                    oldIndex: t,
                    newIndex: i
                };
                if (m[t]) {
                    delete O[t];
                    delete N[m[t].newIndex]
                } else {
                    N[i] = {
                        data: n[i],
                        row: t
                    };
                    O[t] = {
                        data: o[t],
                        row: i
                    };
                    m[t] = M
                }
            }
        }
        for (var i = 0; i < n.length - 1; i++) {
            if (N[i] && !N[i + 1] && N[i].row + 1 < o.length && !O[N[i].row + 1] && c(o[N[i].row + 1], n[i + 1])) {
                N[i + 1] = {
                    data: n[i + 1],
                    row: N[i].row + 1
                };
                O[N[i].row + 1] = {
                    data: O[N[i].row + 1],
                    row: i + 1
                }
            }
        }
        for (var i = n.length - 1; i > 0; i--) {
            if (N[i] && !N[i - 1] && N[i].row > 0 && !O[N[i].row - 1] && c(o[N[i].row - 1], n[i - 1])) {
                N[i - 1] = {
                    data: n[i - 1],
                    row: N[i].row - 1
                };
                O[N[i].row - 1] = {
                    data: O[N[i].row - 1],
                    row: i - 1
                }
            }
        }
        var d = [];
        if (n.length == 0) {
            for (var i = 0; i < o.length; i++) {
                d.push({
                    index: 0,
                    type: 'delete'
                })
            }
        } else {
            var b = 0;
            if (!O[0]) {
                for (var i = 0; i < o.length && !O[i]; i++) {
                    d.push({
                        index: 0,
                        type: 'delete'
                    });
                    b = i + 1
                }
            }
            for (var i = 0; i < n.length; i++) {
                if (!N[i] || N[i].row > b) {
                    d.push({
                        index: i,
                        type: 'insert'
                    })
                } else {
                    b = N[i].row + 1;
                    for (var j = N[i].row + 1; j < o.length && (!O[j] || O[j].row < i); j++) {
                        d.push({
                            index: i + 1,
                            type: 'delete'
                        });
                        b = j + 1
                    }
                }
            }
        }
        return d
    };
    jQuery.sap.parseJS = (function() {
        var a, b, e = {
            '"': '"',
            '\'': '\'',
            '\\': '\\',
            '/': '/',
            b: '\b',
            f: '\f',
            n: '\n',
            r: '\r',
            t: '\t'
        }, t, d = function(m) {
            throw {
                name: 'SyntaxError',
                message: m,
                at: a,
                text: t
            }
        }, n = function(c) {
            if (c && c !== b) {
                d("Expected '" + c + "' instead of '" + b + "'")
            }
            b = t.charAt(a);
            a += 1;
            return b
        }, f = function() {
            var f, s = '';
            if (b === '-') {
                s = '-';
                n('-')
            }
            while (b >= '0' && b <= '9') {
                s += b;
                n()
            }
            if (b === '.') {
                s += '.';
                while (n() && b >= '0' && b <= '9') {
                    s += b
                }
            }
            if (b === 'e' || b === 'E') {
                s += b;
                n();
                if (b === '-' || b === '+') {
                    s += b;
                    n()
                }
                while (b >= '0' && b <= '9') {
                    s += b;
                    n()
                }
            }
            f = +s;
            if (!isFinite(f)) {
                d("Bad number")
            } else {
                return f
            }
        }, s = function() {
            var c, i, s = '',
                q, u;
            if (b === '"' || b === '\'') {
                q = b;
                while (n()) {
                    if (b === q) {
                        n();
                        return s
                    }
                    if (b === '\\') {
                        n();
                        if (b === 'u') {
                            u = 0;
                            for (i = 0; i < 4; i += 1) {
                                c = parseInt(n(), 16);
                                if (!isFinite(c)) {
                                    break
                                }
                                u = u * 16 + c
                            }
                            s += String.fromCharCode(u)
                        } else if (typeof e[b] === 'string') {
                            s += e[b]
                        } else {
                            break
                        }
                    } else {
                        s += b
                    }
                }
            }
            d("Bad string")
        }, g = function() {
            var g = '',
                c, i = function(b) {
                    return b === "_" || (b >= "0" && b <= "9") || (b >= "a" && b <= "z") || (b >= "A" && b <= "Z")
                };
            if (i(b)) {
                g += b
            } else {
                d("Bad name")
            }
            while (n()) {
                if (b === ' ') {
                    n();
                    return g
                }
                if (b === ':') {
                    return g
                }
                if (i(b)) {
                    g += b
                } else {
                    d("Bad name")
                }
            }
            d("Bad name")
        }, w = function() {
            while (b && b <= ' ') {
                n()
            }
        }, h = function() {
            switch (b) {
                case 't':
                    n('t');
                    n('r');
                    n('u');
                    n('e');
                    return true;
                case 'f':
                    n('f');
                    n('a');
                    n('l');
                    n('s');
                    n('e');
                    return false;
                case 'n':
                    n('n');
                    n('u');
                    n('l');
                    n('l');
                    return null
            }
            d("Unexpected '" + b + "'")
        }, v, j = function() {
            var j = [];
            if (b === '[') {
                n('[');
                w();
                if (b === ']') {
                    n(']');
                    return j
                }
                while (b) {
                    j.push(v());
                    w();
                    if (b === ']') {
                        n(']');
                        return j
                    }
                    n(',');
                    w()
                }
            }
            d("Bad array")
        }, o = function() {
            var k, o = {};
            if (b === '{') {
                n('{');
                w();
                if (b === '}') {
                    n('}');
                    return o
                }
                while (b) {
                    if (b >= "0" && b <= "9") {
                        k = f()
                    } else if (b === '"' || b === '\'') {
                        k = s()
                    } else {
                        k = g()
                    }
                    w();
                    n(':');
                    if (Object.hasOwnProperty.call(o, k)) {
                        d('Duplicate key "' + k + '"')
                    }
                    o[k] = v();
                    w();
                    if (b === '}') {
                        n('}');
                        return o
                    }
                    n(',');
                    w()
                }
            }
            d("Bad object")
        };
        v = function() {
            w();
            switch (b) {
                case '{':
                    return o();
                case '[':
                    return j();
                case '"':
                case '\'':
                    return s();
                case '-':
                    return f();
                default:
                    return b >= '0' && b <= '9' ? f():
                        h()
            }
        };
        return function(c, i) {
            var r;
            t = c;
            a = i || 0;
            b = ' ';
            r = v();
            if (isNaN(i)) {
                w();
                if (b) {
                    d("Syntax error")
                }
                return r
            } else {
                return {
                    result: r,
                    at: a - 1
                }
            }
        }
    }())
}());