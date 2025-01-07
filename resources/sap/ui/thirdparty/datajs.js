﻿/*
 * Copyright (c) Microsoft.  All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal  in the Software without restriction, including without limitation the rights  to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * datajs.js
 */

(function(w, u) {
    var d = w.datajs || {};
    var o = w.OData || {};
    if (typeof define === 'function' && define.amd) {
        define('datajs', d);
        define('OData', o)
    } else {
        w.datajs = d;
        w.OData = o
    }
    var c = function(p) {
        if (w.ActiveXObject) {
            return new w.ActiveXObject(p)
        }
        return null
    };
    var f = function(a) {
        return a !== null && a !== u
    };
    var g = function(a, b) {
        var i, e;
        for (i = 0, e = a.length; i < e; i++) {
            if (a[i] === b) {
                return true
            }
        }
        return false
    };
    var h = function(a, b) {
        return (a !== u) ? a : b
    };
    var j = function(a) {
        if (arguments.length === 1) {
            w.setTimeout(a, 0);
            return
        }
        var b = Array.prototype.slice.call(arguments, 1);
        w.setTimeout(function() {
            a.apply(this, b)
        }, 0)
    };
    var k = function(a, b) {
        for (var e in b) {
            a[e] = b[e]
        }
        return a
    };
    var l = function(a, b) {
        if (a) {
            var i, e;
            for (i = 0, e = a.length; i < e; i++) {
                if (b(a[i])) {
                    return a[i]
                }
            }
        }
        return null
    };
    var m = function(a) {
        return Object.prototype.toString.call(a) === "[object Array]"
    };
    var n = function(a) {
        return Object.prototype.toString.call(a) === "[object Date]"
    };
    var q = function(a) {
        return typeof a === "object"
    };
    var r = function(a) {
        return parseInt(a, 10)
    };
    var s = function(a, b, e) {
        if (a.hasOwnProperty(b)) {
            a[e] = a[b];
            delete a[b]
        }
    };
    var t = function(e) {
        throw e
    };
    var v = function(a) {
        if (a.trim) {
            return a.trim()
        }
        return a.replace(/^\s+|\s+$/g, '')
    };
    var z = function(a, b) {
        return (a !== u) ? a : b
    };
    var A = /^([^:\/?#]+:)?(\/\/[^\/?#]*)?([^?#:]+)?(\?[^#]*)?(#.*)?/;
    var B = ["scheme", "authority", "path", "query", "fragment"];
    var C = function(a) {
        var b = {
            isAbsolute: false
        };
        if (a) {
            var e = A.exec(a);
            if (e) {
                var i, p;
                for (i = 0, p = B.length; i < p; i++) {
                    if (e[i + 1]) {
                        b[B[i]] = e[i + 1]
                    }
                }
            }
            if (b.scheme) {
                b.isAbsolute = true
            }
        }
        return b
    };
    var D = function(a) {
        return "".concat(a.scheme || "", a.authority || "", a.path || "", a.query || "", a.fragment || "")
    };
    var E = /^\/{0,2}(?:([^@]*)@)?([^:]+)(?::{1}(\d+))?/;
    var F = /%[0-9A-F]{2}/ig;
    var G = function(a) {
        var b = C(a);
        var e = b.scheme;
        var i = b.authority;
        if (e) {
            b.scheme = e.toLowerCase();
            if (i) {
                var p = E.exec(i);
                if (p) {
                    b.authority = "//" + (p[1] ? p[1] + "@" : "") + (p[2].toLowerCase()) + (p[3] ? ":" + p[3] : "")
                }
            }
        }
        a = D(b);
        return a.replace(F, function(x) {
            return x.toLowerCase()
        })
    };
    var H = function(a, b) {
        if (!b) {
            return a
        }
        var e = C(a);
        if (e.isAbsolute) {
            return a
        }
        var i = C(b);
        var p = {};
        var x;
        if (e.authority) {
            p.authority = e.authority;
            x = e.path;
            p.query = e.query
        } else {
            if (!e.path) {
                x = i.path;
                p.query = e.query || i.query
            } else {
                if (e.path.charAt(0) === '/') {
                    x = e.path
                } else {
                    x = I(e.path, i.path)
                }
                p.query = e.query
            }
            p.authority = i.authority
        }
        p.path = J(x);
        p.scheme = i.scheme;
        p.fragment = e.fragment;
        return D(p)
    };
    var I = function(a, b) {
        var p = "/";
        var e;
        if (b) {
            e = b.lastIndexOf("/");
            p = b.substring(0, e);
            if (p.charAt(p.length - 1) !== "/") {
                p = p + "/"
            }
        }
        return p + a
    };
    var J = function(p) {
        var a = "";
        var b = "";
        var e;
        while (p) {
            if (p.indexOf("..") === 0 || p.indexOf(".") === 0) {
                p = p.replace(/^\.\.?\/?/g, "")
            } else if (p.indexOf("/..") === 0) {
                p = p.replace(/^\/\..\/?/g, "/");
                e = a.lastIndexOf("/");
                if (e === -1) {
                    a = ""
                } else {
                    a = a.substring(0, e)
                }
            } else if (p.indexOf("/.") === 0) {
                p = p.replace(/^\/\.\/?/g, "/")
            } else {
                b = p;
                e = p.indexOf("/", 1);
                if (e !== -1) {
                    b = p.substring(0, e)
                }
                a = a + b;
                p = p.replace(b, "")
            }
        }
        return a
    };
    var K = "http://";
    var L = K + "www.w3.org/";
    var M = L + "1999/xhtml";
    var N = L + "2000/xmlns/";
    var O = L + "XML/1998/namespace";
    var P = K + "www.mozilla.org/newlayout/xml/parsererror.xml";
    var Q = function(a) {
        var b = /(^\s)|(\s$)/;
        return b.test(a)
    };
    var R = function(a) {
        var b = /^\s*$/;
        return a === null || b.test(a)
    };
    var S = function(a) {
        while (a !== null && a.nodeType === 1) {
            var b = c1(a, "space", O);
            if (b === "preserve") {
                return true
            } else if (b === "default") {
                break
            } else {
                a = a.parentNode
            }
        }
        return false
    };
    var T = function(a) {
        var b = a.nodeName;
        return b == "xmlns" || b.indexOf("xmlns:") == 0
    };
    var U = function(a, b, e) {
        try {
            a.setProperty(b, e)
        } catch (_) {}
    };
    var V = function() {
        var a = c("Msxml2.DOMDocument.3.0");
        if (a) {
            U(a, "ProhibitDTD", true);
            U(a, "MaxElementDepth", 256);
            U(a, "AllowDocumentFunction", false);
            U(a, "AllowXsltScript", false)
        }
        return a
    };
    var W = function() {
        try {
            var a = c("Msxml2.DOMDocument.6.0");
            if (a) {
                a.async = true
            }
            return a
        } catch (_) {
            return V()
        }
    };
    var X = function(a) {
        var b = W();
        if (!b) {
            return null
        }
        b.loadXML(a);
        var p = b.parseError;
        if (p.errorCode !== 0) {
            Y(p.reason, p.srcText, a)
        }
        return b
    };
    var Y = function(e, a, b) {
        if (typeof e === "string") {
            e = {
                message: e
            }
        };
        throw k(e, {
            srcText: a || "",
            errorXmlText: b || ""
        })
    };
    var Z = function(a) {
        var b = w.DOMParser && new w.DOMParser();
        var i;
        if (!b) {
            i = X(a);
            if (!i) {
                Y("XML DOM parser not supported")
            }
            return i
        }
        try {
            i = b.parseFromString(a, "text/xml")
        } catch (e) {
            Y(e, "", a)
        }
        var p = i.documentElement;
        var x = p.namespaceURI;
        var y = m1(p);
        if (y === "parsererror" && x === P) {
            var _ = i1(p, P, "sourcetext");
            var i9 = _ ? o1(_) : "";
            Y(l1(p) || "", i9, a)
        }
        if (y === "h3" && x === M || j1(p, M, "h3")) {
            var j9 = "";
            var k9 = [];
            var l9 = p.firstChild;
            while (l9) {
                if (l9.nodeType === 1) {
                    j9 += l1(l9) || ""
                }
                k9.push(l9.nextSibling);
                l9 = l9.firstChild || k9.shift()
            }
            j9 += l1(p) || "";
            Y(j9, "", a)
        }
        return i
    };
    var $ = function(p, a) {
        return p ? p + ":" + a : a
    };
    var a1 = function(a, b) {
        if (Q(b.data)) {
            var e = d1(a, O, "space");
            if (!e) {
                e = u1(a.ownerDocument, O, $("xml", "space"));
                t1(a, e)
            }
            e.value = "preserve"
        }
        a.appendChild(b);
        return a
    };
    var b1 = function(e, a) {
        var b = e.attributes;
        var i, p;
        for (i = 0, p = b.length; i < p; i++) {
            a(b.item(i))
        }
    };
    var c1 = function(a, b, e) {
        var i = d1(a, b, e);
        return i ? o1(i) : null
    };
    var d1 = function(a, b, e) {
        var i = a.attributes;
        if (i.getNamedItemNS) {
            return i.getNamedItemNS(e || null, b)
        }
        return i.getQualifiedItem(b, e) || null
    };
    var e1 = function(a, b) {
        var e = d1(a, "base", O);
        return (e ? H(e.value, b) : b) || null
    };
    var f1 = function(a, b) {
        p1(a, false, function(e) {
            if (e.nodeType === 1) {
                b(e)
            }
            return true
        })
    };
    var g1 = function(a, b, p) {
        var e = p.split("/");
        var i, x;
        for (i = 0, x = e.length; i < x; i++) {
            a = a && i1(a, b, e[i])
        }
        return a || null
    };
    var h1 = function(a, b, p) {
        var e = p.lastIndexOf("/");
        var i = p.substring(e + 1);
        var x = p.substring(0, e);
        var y = x ? g1(a, b, x) : a;
        if (y) {
            if (i.charAt(0) === "@") {
                return d1(y, i.substring(1), b)
            }
            return i1(y, b, i)
        }
        return null
    };
    var i1 = function(a, b, e) {
        return k1(a, b, e, false)
    };
    var j1 = function(a, b, e) {
        if (a.getElementsByTagNameNS) {
            var i = a.getElementsByTagNameNS(b, e);
            return i.length > 0 ? i[0] : null
        }
        return k1(a, b, e, true)
    };
    var k1 = function(a, b, e, i) {
        var p = null;
        p1(a, i, function(x) {
            if (x.nodeType === 1) {
                var y = !b || n1(x) === b;
                var _ = !e || m1(x) === e;
                if (y && _) {
                    p = x
                }
            }
            return p === null
        });
        return p
    };
    var l1 = function(x) {
        var a = null;
        var b = (x.nodeType === 9 && x.documentElement) ? x.documentElement : x;
        var e = b.ownerDocument.preserveWhiteSpace === false;
        var i;
        p1(b, false, function(p) {
            if (p.nodeType === 3 || p.nodeType === 4) {
                var y = o1(p);
                var _ = e || !R(y);
                if (!_) {
                    if (i === u) {
                        i = S(b)
                    }
                    _ = i
                }
                if (_) {
                    if (!a) {
                        a = y
                    } else {
                        a += y
                    }
                }
            }
            return true
        });
        return a
    };
    var m1 = function(a) {
        return a.localName || a.baseName
    };
    var n1 = function(a) {
        return a.namespaceURI || null
    };
    var o1 = function(a) {
        if (a.nodeType === 1) {
            return l1(a)
        }
        return a.nodeValue
    };
    var p1 = function(a, b, e) {
        var i = [];
        var p = a.firstChild;
        var x = true;
        while (p && x) {
            x = e(p);
            if (x) {
                if (b && p.firstChild) {
                    i.push(p.firstChild)
                }
                p = p.nextSibling || i.shift()
            }
        }
    };
    var q1 = function(a, b, e) {
        var i = a.nextSibling;
        while (i) {
            if (i.nodeType === 1) {
                var p = !b || n1(i) === b;
                var x = !e || m1(i) === e;
                if (p && x) {
                    return i
                }
            }
            i = i.nextSibling
        }
        return null
    };
    var r1 = function() {
        var i = w.document.implementation;
        return (i && i.createDocument) ? i.createDocument(null, null, null) : W()
    };
    var s1 = function(p, a) {
        if (!m(a)) {
            return t1(p, a)
        }
        var i, b;
        for (i = 0, b = a.length; i < b; i++) {
            a[i] && t1(p, a[i])
        }
        return p
    };
    var t1 = function(p, a) {
        if (a) {
            if (typeof a === "string") {
                return a1(p, y1(p.ownerDocument, a))
            }
            if (a.nodeType === 2) {
                p.setAttributeNodeNS ? p.setAttributeNodeNS(a) : p.setAttributeNode(a)
            } else {
                p.appendChild(a)
            }
        }
        return p
    };
    var u1 = function(a, b, e, i) {
        var p = a.createAttributeNS && a.createAttributeNS(b, e) || a.createNode(2, e, b || u);
        p.value = i || "";
        return p
    };
    var v1 = function(a, b, e, i) {
        var p = a.createElementNS && a.createElementNS(b, e) || a.createNode(1, e, b || u);
        return s1(p, i || [])
    };
    var w1 = function(a, b, p) {
        return u1(a, N, $("xmlns", p), b)
    };
    var x1 = function(a, b) {
        var e = "<c>" + b + "</c>";
        var i = Z(e);
        var p = i.documentElement;
        var x = ("importNode" in a) ? a.importNode(p, true) : p;
        var y = a.createDocumentFragment();
        var _ = x.firstChild;
        while (_) {
            y.appendChild(_);
            _ = _.nextSibling
        }
        return y
    };
    var y1 = function(a, b) {
        return a.createTextNode(b)
    };
    var z1 = function(a, b, e, p, x) {
        var y = "";
        var _ = x.split("/");
        var i9 = i1;
        var j9 = v1;
        var k9 = b;
        var i, l9;
        for (i = 0, l9 = _.length; i < l9; i++) {
            y = _[i];
            if (y.charAt(0) === "@") {
                y = y.substring(1);
                i9 = d1;
                j9 = u1
            }
            var m9 = i9(k9, e, y);
            if (!m9) {
                m9 = j9(a, e, $(p, y));
                t1(k9, m9)
            }
            k9 = m9
        }
        return k9
    };
    var A1 = function(a) {
        var v5 = w.XMLSerializer;
        if (v5) {
            var b = new v5();
            return b.serializeToString(a)
        }
        if (a.xml) {
            return a.xml
        }
        throw {
            message: "XML serialization unsupported"
        }
    };
    var B1 = function(a) {
        var b = a.childNodes;
        var i, e = b.length;
        if (e === 0) {
            return ""
        }
        var p = a.ownerDocument;
        var x = p.createDocumentFragment();
        var y = p.createElement("c");
        x.appendChild(y);
        for (i = 0; i < e; i++) {
            y.appendChild(b[i])
        }
        var _ = A1(x);
        _ = _.substr(3, _.length - 7);
        for (i = 0; i < e; i++) {
            a.appendChild(y.childNodes[i])
        }
        return _
    };
    var C1 = function(a) {
        var x = a.xml;
        if (x !== u) {
            return x
        }
        if (w.XMLSerializer) {
            var b = new w.XMLSerializer();
            return b.serializeToString(a)
        }
        throw {
            message: "XML serialization unsupported"
        }
    };
    var D1 = function(a, b, e) {
        return function() {
            a[b].apply(a, arguments);
            return e
        }
    };
    var E1 = function() {
        this._arguments = u;
        this._done = u;
        this._fail = u;
        this._resolved = false;
        this._rejected = false
    };
    E1.prototype = {
        then: function(a, e) {
            if (a) {
                if (!this._done) {
                    this._done = [a]
                } else {
                    this._done.push(a)
                }
            }
            if (e) {
                if (!this._fail) {
                    this._fail = [e]
                } else {
                    this._fail.push(e)
                }
            }
            if (this._resolved) {
                this.resolve.apply(this, this._arguments)
            } else if (this._rejected) {
                this.reject.apply(this, this._arguments)
            }
            return this
        },
        resolve: function() {
            if (this._done) {
                var i, a;
                for (i = 0, a = this._done.length; i < a; i++) {
                    this._done[i].apply(null, arguments)
                }
                this._done = u;
                this._resolved = false;
                this._arguments = u
            } else {
                this._resolved = true;
                this._arguments = arguments
            }
        },
        reject: function() {
            if (this._fail) {
                var i, a;
                for (i = 0, a = this._fail.length; i < a; i++) {
                    this._fail[i].apply(null, arguments)
                }
                this._fail = u;
                this._rejected = false;
                this._arguments = u
            } else {
                this._rejected = true;
                this._arguments = arguments
            }
        },
        promise: function() {
            var a = {};
            a.then = D1(this, "then", a);
            return a
        }
    };
    var F1 = function() {
        if (w.jQuery && w.jQuery.Deferred) {
            return new w.jQuery.Deferred()
        } else {
            return new E1()
        }
    };
    var G1 = function(a, b) {
        var e = ((a && a.__metadata) || {}).type;
        return e || (b ? b.type : null)
    };
    var H1 = "Edm.";
    var I1 = H1 + "Binary";
    var J1 = H1 + "Boolean";
    var K1 = H1 + "Byte";
    var L1 = H1 + "DateTime";
    var M1 = H1 + "DateTimeOffset";
    var N1 = H1 + "Decimal";
    var O1 = H1 + "Double";
    var P1 = H1 + "Guid";
    var Q1 = H1 + "Int16";
    var R1 = H1 + "Int32";
    var S1 = H1 + "Int64";
    var T1 = H1 + "SByte";
    var U1 = H1 + "Single";
    var V1 = H1 + "String";
    var W1 = H1 + "Time";
    var X1 = H1 + "Geography";
    var Y1 = X1 + "Point";
    var Z1 = X1 + "LineString";
    var $1 = X1 + "Polygon";
    var _1 = X1 + "Collection";
    var a2 = X1 + "MultiPolygon";
    var b2 = X1 + "MultiLineString";
    var c2 = X1 + "MultiPoint";
    var d2 = H1 + "Geometry";
    var e2 = d2 + "Point";
    var f2 = d2 + "LineString";
    var g2 = d2 + "Polygon";
    var h2 = d2 + "Collection";
    var i2 = d2 + "MultiPolygon";
    var j2 = d2 + "MultiLineString";
    var k2 = d2 + "MultiPoint";
    var l2 = "Point";
    var m2 = "LineString";
    var n2 = "Polygon";
    var o2 = "MultiPoint";
    var p2 = "MultiLineString";
    var q2 = "MultiPolygon";
    var r2 = "GeometryCollection";
    var s2 = [V1, R1, S1, J1, O1, U1, L1, M1, W1, N1, P1, K1, Q1, T1, I1];
    var t2 = [d2, e2, f2, g2, h2, i2, j2, k2];
    var u2 = [X1, Y1, Z1, $1, _1, a2, b2, c2];
    var v2 = function(a, b) {
        if (!a) {
            return null
        }
        if (m(a)) {
            var i, e, p;
            for (i = 0, e = a.length; i < e; i++) {
                p = v2(a[i], b);
                if (p) {
                    return p
                }
            }
            return null
        } else {
            if (a.dataServices) {
                return v2(a.dataServices.schema, b)
            }
            return b(a)
        }
    };
    var w2 = function(a, b) {
        if (a === 0) {
            a = ""
        } else {
            a = "." + A2(a.toString(), 3)
        }
        if (b > 0) {
            if (a === "") {
                a = ".000"
            }
            a += A2(b.toString(), 4)
        }
        return a
    };
    var x2 = function(a) {
        return "\/Date(" + a.getTime() + ")\/"
    };
    var y2 = function(a) {
        if (typeof a === "string") {
            return a
        }
        var b = J2(a);
        var e = B2(a.__offset);
        if (b && e !== "Z") {
            a = new Date(a.valueOf());
            var i = n3(e);
            var p = a.getUTCHours() + (i.d * i.h);
            var x = a.getMinutes() + (i.d * i.m);
            a.setUTCHours(p, x)
        } else if (!b) {
            e = ""
        }
        var y = a.getUTCFullYear();
        var _ = a.getUTCMonth() + 1;
        var i9 = "";
        if (y <= 0) {
            y = -(y - 1);
            i9 = "-"
        }
        var ms = w2(a.getUTCMilliseconds(), a.__ns);
        return i9 + A2(y, 4) + "-" + A2(_, 2) + "-" + A2(a.getUTCDate(), 2) + "T" + A2(a.getUTCHours(), 2) + ":" + A2(a.getUTCMinutes(), 2) + ":" + A2(a.getUTCSeconds(), 2) + ms + e
    };
    var z2 = function(a) {
        var b = a.ms;
        var e = "";
        if (b < 0) {
            e = "-";
            b = -b
        }
        var i = Math.floor(b / 86400000);
        b -= 86400000 * i;
        var p = Math.floor(b / 3600000);
        b -= 3600000 * p;
        var x = Math.floor(b / 60000);
        b -= 60000 * x;
        var y = Math.floor(b / 1000);
        b -= y * 1000;
        return e + "P" + A2(i, 2) + "DT" + A2(p, 2) + "H" + A2(x, 2) + "M" + A2(y, 2) + w2(b, a.ns) + "S"
    };
    var A2 = function(a, b, e) {
        var i = a.toString(10);
        while (i.length < b) {
            if (e) {
                i += "0"
            } else {
                i = "0" + i
            }
        }
        return i
    };
    var B2 = function(a) {
        return (!a || a === "Z" || a === "+00:00" || a === "-00:00") ? "Z" : a
    };
    var C2 = function(a) {
        if (typeof a === "string") {
            var e = a.indexOf(")", 10);
            if (a.indexOf("Collection(") === 0 && e > 0) {
                return a.substring(11, e)
            }
        }
        return null
    };
    var D2 = function(a, b, e, O3, i, p) {
        return i.request(a, function(x) {
            try {
                if (x.headers) {
                    e3(x.headers)
                }
                if (x.data === u && x.statusCode !== 204) {
                    O3.read(x, p)
                }
            } catch (y) {
                if (y.request === u) {
                    y.request = a
                }
                if (y.response === u) {
                    y.response = x
                }
                e(y);
                return
            }
            try {
                b(x.data, x)
            } catch (y) {
                y.bIsSuccessHandlerError = true;
                throw y
            }
        }, e)
    };
    var E2 = function(a) {
        return I2(a) && m(a.__batchRequests)
    };
    var F2 = /Collection\((.*)\)/;
    var G2 = function(a, b) {
        var e = a && a.results || a;
        return !!e && (H2(b)) || (!b && m(e) && !I2(e[0]))
    };
    var H2 = function(a) {
        return F2.test(a)
    };
    var I2 = function(a) {
        return !!a && q(a) && !m(a) && !n(a)
    };
    var J2 = function(a) {
        return (a.__edmType === "Edm.DateTimeOffset" || (!a.__edmType && a.__offset))
    };
    var K2 = function(a) {
        if (!a && !I2(a)) {
            return false
        }
        var b = a.__metadata || {};
        var e = a.__deferred || {};
        return !b.type && !! e.uri
    };
    var L2 = function(a) {
        return I2(a) && a.__metadata && "uri" in a.__metadata
    };
    var M2 = function(a, b) {
        var e = a && a.results || a;
        return m(e) && ((!H2(b)) && (I2(e[0])))
    };
    var N2 = function(a) {
        return g(u2, a)
    };
    var O2 = function(a) {
        return g(t2, a)
    };
    var P2 = function(a) {
        if (!a && !I2(a)) {
            return false
        }
        var b = a.__metadata;
        var e = a.__mediaresource;
        return !b && !! e && !! e.media_src
    };
    var Q2 = function(a) {
        return n(a) || typeof a === "string" || typeof a === "number" || typeof a === "boolean"
    };
    var R2 = function(a) {
        return g(s2, a)
    };
    var S2 = function(a, p) {
        if (K2(a)) {
            return "deferred"
        }
        if (L2(a)) {
            return "entry"
        }
        if (M2(a)) {
            return "feed"
        }
        if (p && p.relationship) {
            if (a === null || a === u || !M2(a)) {
                return "entry"
            }
            return "feed"
        }
        return null
    };
    var T2 = function(p, a) {
        return l(p, function(b) {
            return b.name === a
        })
    };
    var U2 = function(a, b, e) {
        return (a) ? v2(b, function(w6) {
            return b3(a, w6, e)
        }) : null
    };
    var V2 = function(e, a) {
        return l(e, function(b) {
            return b.name === a
        })
    };
    var W2 = function(a, b) {
        return U2(a, b, "complexType")
    };
    var X2 = function(a, b) {
        return U2(a, b, "entityType")
    };
    var Y2 = function(a) {
        return v2(a, function(w6) {
            return l(w6.entityContainer, function(b) {
                return f3(b.isDefaultEntityContainer)
            })
        })
    };
    var Z2 = function(a, b) {
        return U2(a, b, "entityContainer")
    };
    var $2 = function(a, b) {
        return l(a, function(e) {
            return e.name === b
        })
    };
    var _2 = function(a, b) {
        var e = null;
        if (a) {
            var p = a.relationship;
            var x = v2(b, function(w6) {
                var _ = a3(w6["namespace"], p);
                var i9 = w6.association;
                if (_ && i9) {
                    var i, j9;
                    for (i = 0, j9 = i9.length; i < j9; i++) {
                        if (i9[i].name === _) {
                            return i9[i]
                        }
                    }
                }
            });
            if (x) {
                var y = x.end[0];
                if (y.role !== a.toRole) {
                    y = x.end[1]
                }
                e = y.type
            }
        }
        return e
    };
    var a3 = function(a, b) {
        if (b.indexOf(a) === 0 && b.charAt(a.length) === ".") {
            return b.substr(a.length + 1)
        }
        return null
    };
    var b3 = function(a, w6, b) {
        if (a && w6) {
            var e = a3(w6["namespace"], a);
            if (e) {
                return l(w6[b], function(i) {
                    return i.name === e
                })
            }
        }
        return null
    };
    var c3 = function(a, b) {
        if (a === b) {
            return a
        }
        var e = a.split(".");
        var p = b.split(".");
        var x = (e.length >= p.length) ? e.length : p.length;
        for (var i = 0; i < x; i++) {
            var y = e[i] && r(e[i]);
            var _ = p[i] && r(p[i]);
            if (y > _) {
                return a
            }
            if (y < _) {
                return b
            }
        }
    };
    var d3 = {
        "accept": "Accept",
        "content-type": "Content-Type",
        "dataserviceversion": "DataServiceVersion",
        "maxdataserviceversion": "MaxDataServiceVersion"
    };
    var e3 = function(a) {
        for (var b in a) {
            var e = b.toLowerCase();
            var i = d3[e];
            if (i && b !== i) {
                var p = a[b];
                delete a[b];
                a[i] = p
            }
        }
    };
    var f3 = function(p) {
        if (typeof p === "boolean") {
            return p
        }
        return typeof p === "string" && p.toLowerCase() === "true"
    };
    var g3 = /^(-?\d{4,})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2}))?(?:\.(\d+))?(.*)$/;
    var h3 = function(a, b, e) {
        var p = g3.exec(a);
        var i = (p) ? B2(p[8]) : null;
        if (!p || (!b && i !== "Z")) {
            if (e) {
                return null
            }
            throw {
                message: "Invalid date/time value"
            }
        }
        var y = r(p[1]);
        if (y <= 0) {
            y++
        }
        var x = p[7];
        var _ = 0;
        if (!x) {
            x = 0
        } else {
            if (x.length > 7) {
                if (e) {
                    return null
                }
                throw {
                    message: "Cannot parse date/time value to given precision."
                }
            }
            _ = A2(x.substring(3), 4, true);
            x = A2(x.substring(0, 3), 3, true);
            x = r(x);
            _ = r(_)
        }
        var i9 = r(p[4]);
        var j9 = r(p[5]);
        var k9 = r(p[6]) || 0;
        if (i !== "Z") {
            var l9 = n3(i);
            var m9 = -(l9.d);
            i9 += l9.h * m9;
            j9 += l9.m * m9
        }
        var n9 = new Date();
        n9.setUTCFullYear(y, r(p[2]) - 1, r(p[3]));
        n9.setUTCHours(i9, j9, k9, x);
        if (isNaN(n9.valueOf())) {
            if (e) {
                return null
            }
            throw {
                message: "Invalid date/time value"
            }
        }
        if (b) {
            n9.__edmType = "Edm.DateTimeOffset";
            n9.__offset = i
        }
        if (_) {
            n9.__ns = _
        }
        return n9
    };
    var i3 = function(p, a) {
        return h3(p, false, a)
    };
    var j3 = function(p, a) {
        return h3(p, true, a)
    };
    var k3 = /^([+-])?P(?:(\d+)Y)?(?:(\d+)M)?(?:(\d+)D)?(?:T(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)(?:\.(\d+))?S)?)?/;
    var l3 = function(a) {
        k3.test(a)
    };
    var m3 = function(a) {
        var p = k3.exec(a);
        if (p === null) {
            throw {
                message: "Invalid duration value."
            }
        }
        var y = p[2] || "0";
        var b = p[3] || "0";
        var e = r(p[4] || 0);
        var i = r(p[5] || 0);
        var x = r(p[6] || 0);
        var _ = parseFloat(p[7] || 0);
        if (y !== "0" || b !== "0") {
            throw {
                message: "Unsupported duration value."
            }
        }
        var ms = p[8];
        var ns = 0;
        if (!ms) {
            ms = 0
        } else {
            if (ms.length > 7) {
                throw {
                    message: "Cannot parse duration value to given precision."
                }
            }
            ns = A2(ms.substring(3), 4, true);
            ms = A2(ms.substring(0, 3), 3, true);
            ms = r(ms);
            ns = r(ns)
        }
        ms += _ * 1000 + x * 60000 + i * 3600000 + e * 86400000;
        if (p[1] === "-") {
            ms = -ms
        }
        var i9 = {
            ms: ms,
            __edmType: "Edm.Time"
        };
        if (ns) {
            i9.ns = ns
        }
        return i9
    };
    var n3 = function(a) {
        var b = a.substring(0, 1);
        b = (b === "+") ? 1 : -1;
        var e = r(a.substring(1));
        var i = r(a.substring(a.indexOf(":") + 1));
        return {
            d: b,
            h: e,
            m: i
        }
    };
    var o3 = function(a, O3, b) {
        if (!a.method) {
            a.method = "GET"
        }
        if (!a.headers) {
            a.headers = {}
        } else {
            e3(a.headers)
        }
        if (a.headers.Accept === u) {
            a.headers.Accept = O3.accept
        }
        if (f(a.data) && a.body === u) {
            O3.write(a, b)
        }
        if (!f(a.headers.MaxDataServiceVersion)) {
            a.headers.MaxDataServiceVersion = O3.maxDataServiceVersion || "1.0"
        }
        if (a.async ===u) {
            a.async = true
        }
    };
    var p3 = function(i, a, b) {
        if (i && typeof i === "object") {
            for (var e in i) {
                var p = i[e];
                var x = p3(p, e, b);
                x = b(e, x, a);
                if (x !== p) {
                    if (p === u) {
                        delete i[e]
                    } else {
                        i[e] = x
                    }
                }
            }
        }
        return i
    };
    var q3 = function(i, a) {
        return a("", p3(i, "", a))
    };
    var r3 = 0;
    var s3 = function(a) {
        if (a.method && a.method !== "GET") {
            return false
        }
        return true
    };
    var t3 = function(a) {
        var i = w.document.createElement("IFRAME");
        i.style.display = "none";
        var b = a.replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/\</g, "&lt;");
        var e = "<html><head><script type=\"text/javascript\" src=\"" + b + "\"><\/script><\/head><body><\/body><\/html>";
        var p = w.document.getElementsByTagName("BODY")[0];
        p.appendChild(i);
        A3(i, e);
        return i
    };
    var u3 = function() {
        if (w.XMLHttpRequest) {
            return new w.XMLHttpRequest()
        }
        var a;
        if (w.ActiveXObject) {
            try {
                return new w.ActiveXObject("Msxml2.XMLHTTP.6.0")
            } catch (_) {
                try {
                    return new w.ActiveXObject("Msxml2.XMLHTTP.3.0")
                } catch (e) {
                    a = e
                }
            }
        } else {
            a = {
                message: "XMLHttpRequest not supported"
            }
        }
        throw a
    };
    var v3 = function(a) {
        return a.indexOf("http://") === 0 || a.indexOf("https://") === 0 || a.indexOf("file://") === 0
    };
    var w3 = function(a) {
        if (!v3(a)) {
            return true
        }
        var b = w.location;
        var e = b.protocol + "//" + b.host + "/";
        return (a.indexOf(e) === 0)
    };
    var x3 = function(a, b) {
        try {
            delete w[a]
        } catch (e) {
            w[a] = u;
            if (b === r3 - 1) {
                r3 -= 1
            }
        }
    };
    var y3 = function(i) {
        if (i) {
            A3(i, "");
            i.parentNode.removeChild(i)
        }
        return null
    };
    var z3 = function(x, a) {
        var b = x.getAllResponseHeaders();
        if (!b) {
            var C3 = x.getResponseHeader("Content-Type");
            var e = x.getResponseHeader("Content-Length");
            if (C3) a["Content-Type"] = C3;
            if (e) a["Content-Length"] = e
        } else {
            b = b.split(/\r?\n/);
            var i, p;
            for (i = 0, p = b.length; i < p; i++) {
                if (b[i]) {
                    var y = b[i].split(": ");
                    a[y[0]] = y[1]
                }
            }
        }
    };
    var A3 = function(i, a) {
        var b = (i.contentWindow) ? i.contentWindow.document : i.contentDocument.document;
        b.open();
        b.write(a);
        b.close()
    };
    o.defaultHttpClient = {
        callbackParameterName: "$callback",
        formatQueryString: "$format=json",
        enableJsonpCallback: false,
        request: function(a, b, e) {
            var i = {};
            var x = null;
            var p = false;
            var y;
            i.abort = function() {
                y = y3(y);
                if (p) {
                    return
                }
                p = true;
                if (x) {
                    x.abort();
                    x = null
                }
                e({
                    message: "Request aborted"
                })
            };
            var _ = function() {
                y = y3(y);
                if (!p) {
                    p = true;
                    x = null;
                    e({
                        message: "Request timed out"
                    })
                }
            };
            var i9;
            var j9 = a.requestUri;
            var k9 = h(a.enableJsonpCallback, this.enableJsonpCallback);
            var l9 = h(a.callbackParameterName, this.callbackParameterName);
            var m9 = h(a.formatQueryString, this.formatQueryString);
            if (!k9 || w3(j9)) {
                x = u3();
                var n9 = function() {
                    if (p || x === null || x.readyState !== 4) {
                        return
                    }
                    var v9 = x.statusText;
                    var w9 = x.status;
                    if (w9 === 1223) {
                        w9 = 204;
                        v9 = "No Content"
                    }
                    var x9 = [];
                    z3(x, x9);
                    var y9 = {
                        requestUri: j9,
                        statusCode: w9,
                        statusText: v9,
                        headers: x9,
                        body: x.responseText
                    };
                    p = true;
                    x = null;
                    if (w9 >= 200 && w9 <= 299) {
                        b(y9)
                    } else {
                        e({
                            message: "HTTP request failed",
                            request: a,
                            response: y9
                        })
                    }
                };
                if (a.withCredentials) {
                    x.withCredentials = true
                }
                if (a.user && a.password) {
                    x.open(a.method || "GET", j9, a.async, a.user, a.password)
                } else {
                    x.open(a.method || "GET", j9, a.async)
                }
                if (a.headers) {
                    for (i9 in a.headers) {
                        x.setRequestHeader(i9, a.headers[i9])
                    }
                }
                if (a.timeoutMS) {
                    x.timeout = a.timeoutMS;
                    x.ontimeout = _
                }
                if (a.async ||(!a.async &&a.async !==false)) {
                    x.onreadystatechange = n9
                }
                x.send(a.body);
                if (!a.async) {
                    n9()
                }
            } else {
                if (!s3(a)) {
                    throw {
                        message: "Request is not local and cannot be done through JSONP."
                    }
                }
                var o9 = r3;
                r3 += 1;
                var p9 = o9.toString();
                var q9 = false;
                var r9;
                i9 = "handleJSONP_" + p9;
                w[i9] = function(v9) {
                    y = y3(y);
                    if (!p) {
                        q9 = true;
                        w.clearTimeout(r9);
                        x3(i9, o9);
                        if (w.ActiveXObject && !w.DOMParser) {
                            v9 = w.JSON.parse(w.JSON.stringify(v9))
                        }
                        j(b, {
                            body: v9,
                            statusCode: 200,
                            headers: {
                                "Content-Type": "application/json"
                            }
                        })
                    }
                };
                var s9 = (a.timeoutMS) ? a.timeoutMS : 120000;
                r9 = w.setTimeout(_, s9);
                var t9 = l9 + "=parent." + i9;
                if (this.formatQueryString) {
                    t9 += "&" + m9
                }
                var u9 = j9.indexOf("?");
                if (u9 === -1) {
                    j9 = j9 + "?" + t9
                } else if (u9 === j9.length - 1) {
                    j9 = j9 + t9
                } else {
                    j9 = j9 + "&" + t9
                }
                y = t3(j9)
            }
            return i
        }
    };
    var B3 = "3.0";
    var C3 = function(a) {
        if (!a) {
            return null
        }
        var b = a.split(";");
        var p = {};
        var i, e;
        for (i = 1, e = b.length; i < e; i++) {
            var x = b[i].split("=");
            p[v(x[0])] = x[1]
        }
        return {
            mediaType: v(b[0]),
            properties: p
        }
    };
    var D3 = function(C3) {
        if (!C3) {
            return u
        }
        var a = C3.mediaType;
        var p;
        for (p in C3.properties) {
            a += ";" + p + "=" + C3.properties[p]
        }
        return a
    };
    var E3 = function(C3, a, b, O3) {
        var e = {};
        k(e, b);
        k(e, {
            contentType: C3,
            dataServiceVersion: a,
            handler: O3
        });
        return e
    };
    var F3 = function(a, b, e) {
        if (!a) {
            return
        }
        var i = a.headers;
        if (!i[b]) {
            i[b] = e
        }
    };
    var G3 = function(a, b) {
        if (a) {
            var e = a.headers;
            var i = e["DataServiceVersion"];
            e["DataServiceVersion"] = i ? c3(i, b) : b
        }
    };
    var H3 = function(a, b) {
        var e = a.headers;
        return (e && e[b]) || u
    };
    var I3 = function(a) {
        return C3(H3(a, "Content-Type"))
    };
    var J3 = /^\s?(\d+\.\d+);?.*$/;
    var K3 = function(a) {
        var b = H3(a, "DataServiceVersion");
        if (b) {
            var e = J3.exec(b);
            if (e && e.length) {
                return e[1]
            }
        }
    };
    var L3 = function(O3, a) {
        return O3.accept.indexOf(a.mediaType) >= 0
    };
    var M3 = function(O3, p, a, b) {
        if (!a || !a.headers) {
            return false
        }
        var e = I3(a);
        var i = K3(a) || "";
        var x = a.body;
        if (!f(x) || !x) {
            return false
        }
        if (L3(O3, e)) {
            var y = E3(e, i, b, O3);
            y.response = a;
            a.data = p(O3, x, y);
            return a.data !== u
        }
        return false
    };
    var N3 = function(O3, a, b, e) {
        if (!b || !b.headers) {
            return false
        }
        var i = I3(b);
        var p = K3(b);
        if (!i || L3(O3, i)) {
            var x = E3(i, p, e, O3);
            x.request = b;
            b.body = a(O3, b.data, x);
            if (b.body !== u) {
                G3(b, x.dataServiceVersion || "1.0");
                F3(b, "Content-Type", D3(x.contentType));
                F3(b, "MaxDataServiceVersion", O3.maxDataServiceVersion);
                return true
            }
        }
        return false
    };
    var O3 = function(p, a, b, e) {
        return {
            accept: b,
            maxDataServiceVersion: e,
            read: function(i, x) {
                return M3(this, p, i, x)
            },
            write: function(i, x) {
                return N3(this, a, i, x)
            }
        }
    };
    var P3 = function(O3, b) {
        return b
    };
    var Q3 = function(O3, a) {
        if (f(a)) {
            return a.toString()
        } else {
            return u
        }
    };
    o.textHandler = O3(P3, Q3, "text/plain", B3);
    var R3 = K + "www.opengis.net";
    var S3 = R3 + "/gml";
    var T3 = R3 + "/def/crs/EPSG/0/";
    var U3 = "gml";
    var V3 = function(a, b, e) {
        var i = {
            type: a
        };
        i[b] = e;
        return i
    };
    var W3 = function(a) {
        if (m(a) && a.length >= 2) {
            var b = a[0];
            a[0] = a[1];
            a[1] = b
        }
        return a
    };
    var X3 = function(a, b, e, i, p, x) {
        var y = Y3(a, e, i, p, x);
        return V3(b, "coordinates", y)
    };
    var Y3 = function(a, b, e, i, p) {
        var x = [];
        f1(a, function(y) {
            if (n1(y) !== S3) {
                return
            }
            var _ = m1(y);
            if (_ === b) {
                var i9 = i1(y, S3);
                if (i9) {
                    var j9 = i(i9, p);
                    if (j9) {
                        x.push(j9)
                    }
                }
                return
            }
            if (_ === e) {
                f1(y, function(i9) {
                    if (n1(i9) !== S3) {
                        return
                    }
                    var j9 = i(i9, p);
                    if (j9) {
                        x.push(j9)
                    }
                })
            }
        });
        return x
    };
    var Z3 = function(a, i) {
        var b = Y3(a, "geometryMember", "geometryMembers", l4, i);
        return V3(r2, "geometries", b)
    };
    var $3 = function(a, i) {
        return V3(m2, "coordinates", e4(a, i))
    };
    var _3 = function(a, i) {
        return X3(a, p2, "curveMember", "curveMembers", e4, i)
    };
    var a4 = function(a, i) {
        return X3(a, o2, "pointMember", "pointMembers", f4, i)
    };
    var b4 = function(a, i) {
        return X3(a, q2, "surfaceMember", "surfaceMembers", h4, i)
    };
    var c4 = function(a, i) {
        return V3(l2, "coordinates", f4(a, i))
    };
    var d4 = function(a, i) {
        return V3(n2, "coordinates", h4(a, i))
    };
    var e4 = function(a, i) {
        var b = [];
        f1(a, function(e) {
            var p = n1(e);
            if (p !== S3) {
                return
            }
            var x = m1(e);
            if (x === "posList") {
                b = j4(e, i);
                return
            }
            if (x === "pointProperty") {
                b.push(g4(e, i));
                return
            }
            if (x === "pos") {
                b.push(k4(e, i));
                return
            }
        });
        return b
    };
    var f4 = function(a, i) {
        var p = i1(a, S3, "pos");
        return p ? k4(p, i) : []
    };
    var g4 = function(a, i) {
        var p = i1(a, S3, "Point");
        return p ? f4(p, i) : []
    };
    var h4 = function(a, i) {
        var b = [];
        var e = false;
        f1(a, function(p) {
            if (n1(p) !== S3) {
                return
            }
            var x = m1(p);
            if (x === "exterior") {
                e = true;
                b.unshift(i4(p, i));
                return
            }
            if (x === "interior") {
                b.push(i4(p, i));
                return
            }
        });
        if (!e && b.length > 0) {
            b.unshift([
                []
            ])
        }
        return b
    };
    var i4 = function(a, i) {
        var b = [];
        f1(a, function(e) {
            if (n1(e) !== S3 || m1(e) !== "LinearRing") {
                return
            }
            b = e4(e, i)
        });
        return b
    };
    var j4 = function(a, b) {
        var e = k4(a, false);
        var p = e.length;
        if (p % 2 !== 0) {
            throw {
                message: "GML posList element has an uneven number of numeric values"
            }
        }
        var x = [];
        for (var i = 0; i < p; i += 2) {
            var y = e.slice(i, i + 2);
            x.push(b ? W3(y) : y)
        }
        return x
    };
    var k4 = function(a, i) {
        var b = [];
        var e = " \t\r\n";
        var p = l1(a);
        if (p) {
            var x = p.length;
            var y = 0;
            var _ = 0;
            while (_ <= x) {
                if (e.indexOf(p.charAt(_)) !== -1) {
                    var i9 = p.substring(y, _);
                    if (i9) {
                        b.push(parseFloat(i9))
                    }
                    y = _ + 1
                }
                _++
            }
        }
        return i ? W3(b) : b
    };
    var l4 = function(a, i) {
        var b = m1(a);
        var e;
        switch (b) {
            case "Point":
                e = c4;
                break;
            case "Polygon":
                e = d4;
                break;
            case "LineString":
                e = $3;
                break;
            case "MultiPoint":
                e = a4;
                break;
            case "MultiCurve":
                e = _3;
                break;
            case "MultiSurface":
                e = b4;
                break;
            case "MultiGeometry":
                e = Z3;
                break;
            default:
                throw {
                    message: "Unsupported element: " + b,
                    element: a
                }
        }
        var p = e(a, i);
        var x = c1(a, "srsName", S3) || c1(a, "srsName");
        if (x) {
            if (x.indexOf(T3) !== 0) {
                throw {
                    message: "Unsupported srs name: " + x,
                    element: a
                }
            }
            var y = x.substring(T3.length);
            if (y) {
                p.crs = {
                    type: "name",
                    properties: {
                        name: "EPSG:" + y
                    }
                }
            }
        }
        return p
    };
    var m4 = function(a, b, e, i) {
        var p;
        switch (e) {
            case l2:
                p = u4;
                break;
            case m2:
                p = v4;
                break;
            case n2:
                p = w4;
                break;
            case o2:
                p = y4;
                break;
            case p2:
                p = z4;
                break;
            case q2:
                p = A4;
                break;
            case r2:
                p = C4;
                break;
            default:
                return null
        }
        var x = p(a, b, i);
        var y = b.crs;
        if (y) {
            if (y.type === "name") {
                var _ = y.properties;
                var i9 = _ && _.name;
                if (i9 && i9.indexOf("ESPG:") === 0 && i9.length > 5) {
                    var j9 = i9.substring(5);
                    var k9 = u1(a, null, "srsName", U3 + j9);
                    t1(x, k9)
                }
            }
        }
        return x
    };
    var n4 = function(a, b, e) {
        return v1(a, S3, $(U3, b), e)
    };
    var o4 = function(a, b, i) {
        var p = m(b) ? b : [];
        p = i ? W3(p) : p;
        return n4(a, "pos", p.join(" "))
    };
    var p4 = function(a, b, e, p) {
        var x = n4(a, b);
        if (m(e)) {
            var i, y;
            for (i = 0, y = e.length; i < y; i++) {
                t1(x, o4(a, e[i], p))
            }
            if (y === 0) {
                t1(x, n4(a, "posList"))
            }
        }
        return x
    };
    var q4 = function(a, b, i) {
        return n4(a, "Point", o4(a, b, i))
    };
    var r4 = function(a, b, i) {
        return p4(a, "LineString", b, i)
    };
    var s4 = function(a, b, e, i) {
        var p = n4(a, b);
        if (m(e) && e.length > 0) {
            var x = p4(a, "LinearRing", e, i);
            t1(p, x)
        }
        return p
    };
    var t4 = function(a, b, e) {
        var p = b && b.length;
        var x = n4(a, "Polygon");
        if (m(b) && p > 0) {
            t1(x, s4(a, "exterior", b[0], e));
            var i;
            for (i = 1; i < p; i++) {
                t1(x, s4(a, "interior", b[i], e))
            }
        }
        return x
    };
    var u4 = function(a, b, i) {
        return q4(a, b.coordinates, i)
    };
    var v4 = function(a, b, i) {
        return r4(a, b.coordinates, i)
    };
    var w4 = function(a, b, i) {
        return t4(a, b.coordinates, i)
    };
    var x4 = function(a, b, e, p, x, y) {
        var _ = p && p.length;
        var i9 = n4(a, b);
        if (m(p) && _ > 0) {
            var j9 = n4(a, e);
            var i;
            for (i = 0; i < _; i++) {
                t1(j9, x(a, p[i], y))
            }
            t1(i9, j9)
        }
        return i9
    };
    var y4 = function(a, b, i) {
        return x4(a, "MultiPoint", "pointMembers", b.coordinates, q4, i)
    };
    var z4 = function(a, b, i) {
        return x4(a, "MultiCurve", "curveMembers", b.coordinates, r4, i)
    };
    var A4 = function(a, b, i) {
        return x4(a, "MultiSurface", "surfaceMembers", b.coordinates, t4, i)
    };
    var B4 = function(a, b, i) {
        return m4(a, b, b.type, i)
    };
    var C4 = function(a, b, i) {
        return x4(a, "MultiGeometry", "geometryMembers", b.geometries, B4, i)
    };
    var D4 = "application/xml";
    var E4 = K + "schemas.microsoft.com/ado/";
    var F4 = E4 + "2007/08/dataservices";
    var G4 = E4 + "2007/06/edmx";
    var H4 = E4 + "2006/04/edm";
    var I4 = E4 + "2007/05/edm";
    var J4 = E4 + "2008/01/edm";
    var K4 = E4 + "2008/09/edm";
    var L4 = E4 + "2009/08/edm";
    var M4 = E4 + "2009/11/edm";
    var N4 = F4;
    var O4 = F4 + "/metadata";
    var P4 = F4 + "/related/";
    var Q4 = F4 + "/scheme";
    var R4 = "d";
    var S4 = "m";
    var T4 = function(a, b) {
        var e = {
            name: m1(a),
            value: a.value
        };
        e[b ? "namespaceURI" : "namespace"] = n1(a);
        return e
    };
    var U4 = function(a, b) {
        var e = [];
        var p = [];
        var i, x;
        var y = a.attributes;
        for (i = 0, x = y.length; i < x; i++) {
            var _ = y[i];
            if (n1(_) !== N) {
                e.push(T4(_, b))
            }
        }
        var i9 = a.firstChild;
        while (i9 != null) {
            if (i9.nodeType === 1) {
                p.push(U4(i9, b))
            }
            i9 = i9.nextSibling
        };
        var j9 = {
            name: m1(a),
            value: l1(a),
            attributes: e,
            children: p
        };
        j9[b ? "namespaceURI" : "namespace"] = n1(a);
        return j9
    };
    var V4 = function(a) {
        return n1(a) === N4 && m1(a) === "element"
    };
    var W4 = function(a, e) {
        return {
            type: a,
            extensions: e
        }
    };
    var X4 = function(a) {
        if (i1(a, S3)) {
            return d2
        }
        var b = i1(a, N4);
        if (!b) {
            return V1
        }
        if (V4(b)) {
            var e = q1(b, N4);
            if (e && V4(e)) {
                return "Collection()"
            }
        }
        return null
    };
    var Y4 = function(a) {
        var b = null;
        var i = false;
        var e = [];
        b1(a, function(p) {
            var x = n1(p);
            var y = m1(p);
            var _ = o1(p);
            if (x === O4) {
                if (y === "null") {
                    i = (_.toLowerCase() === "true");
                    return
                }
                if (y === "type") {
                    b = _;
                    return
                }
            }
            if (x !== O && x !== N) {
                e.push(T4(p, true));
                return
            }
        });
        return {
            type: (!b && i ? V1 : b),
            isNull: i,
            extensions: e
        }
    };
    var Z4 = function(a) {
        if (n1(a) !== N4) {
            return null
        };
        var p = m1(a);
        var b = Y4(a);
        var e = b.isNull;
        var i = b.type;
        var x = W4(i, b.extensions);
        var y = e ? null : $4(a, i, x);
        return {
            name: p,
            value: y,
            metadata: x
        }
    };
    var $4 = function(a, p, b) {
        if (!p) {
            p = X4(a);
            b.type = p
        }
        var i = N2(p);
        if (i || O2(p)) {
            return _4(a, p, i)
        }
        if (R2(p)) {
            return a5(a, p)
        }
        if (H2(p)) {
            return c5(a, p, b)
        }
        return b5(a, p, b)
    };
    var _4 = function(a, p, i) {
        var b = i1(a, S3);
        var e = l4(b, i);
        e.__metadata = {
            type: p
        };
        return e
    };
    var a5 = function(a, p) {
        var b = o1(a) || "";
        switch (p) {
            case J1:
                return f3(b);
            case I1:
            case N1:
            case P1:
            case S1:
            case V1:
                return b;
            case K1:
            case Q1:
            case R1:
            case T1:
                return r(b);
            case O1:
            case U1:
                return parseFloat(b);
            case W1:
                return m3(b);
            case L1:
                return i3(b);
            case M1:
                return j3(b)
        };
        return b
    };
    var b5 = function(a, p, b) {
        var e = {
            __metadata: {
                type: p
            }
        };
        f1(a, function(i) {
            var x = Z4(i);
            var y = x.name;
            b.properties = b.properties || {};
            b.properties[y] = x.metadata;
            e[y] = x.value
        });
        return e
    };
    var c5 = function(a, p, b) {
        var i = [];
        var e = b.elements = [];
        var x = C2(p);
        f1(a, function(y) {
            if (V4(y)) {
                var _ = Y4(y);
                var i9 = _.extensions;
                var j9 = _.type || x;
                var k9 = W4(j9, i9);
                var l9 = $4(y, j9, k9);
                i.push(l9);
                e.push(k9)
            }
        });
        return {
            __metadata: {
                type: p === "Collection()" ? null : p
            },
            results: i
        }
    };
    var d5 = function(x, b) {
        if (n1(x) === N4) {
            b = e1(x, b);
            var a = m1(x);
            if (a === "links") {
                return e5(x, b)
            }
            if (a === "uri") {
                return f5(x, b)
            }
        }
        return u
    };
    var e5 = function(a, b) {
        var e = [];
        f1(a, function(i) {
            if (m1(i) === "uri" && n1(i) === N4) {
                e.push(f5(i, b))
            }
        });
        return {
            results: e
        }
    };
    var f5 = function(a, b) {
        var e = l1(a) || "";
        return {
            uri: H(e, b)
        }
    };
    var g5 = function(a, e) {
        if (e === d2 || e === X1) {
            return a && a.type
        }
        if (e === e2 || e === Y1) {
            return l2
        }
        if (e === f2 || e === Z1) {
            return m2
        }
        if (e === g2 || e === $1) {
            return n2
        }
        if (e === h2 || e === _1) {
            return r2
        }
        if (e === i2 || e === a2) {
            return q2
        }
        if (e === j2 || e === b2) {
            return p2
        }
        if (e === k2 || e === c2) {
            return o2
        }
        return null
    };
    var h5 = function(a, b, e) {
        return v1(a, O4, $(S4, b), e)
    };
    var i5 = function(a, b, e) {
        return u1(a, O4, $(S4, b), e)
    };
    var j5 = function(a, b, e) {
        return v1(a, N4, $(R4, b), e)
    };
    var k5 = function(a, b) {
        if (b === L1 || b === M1 || n(a)) {
            return y2(a)
        }
        if (b === W1) {
            return z2(a)
        }
        return a.toString()
    };
    var l5 = function(a, b) {
        return {
            element: a,
            dsv: b
        }
    };
    var m5 = function(a, b, e, i) {
        var p = e ? i5(a, "type", e) : null;
        var x = j5(a, b, p);
        return s1(x, i)
    };
    var n5 = function(a, b, e, i) {
        var p = k5(e, i);
        var x = m5(a, b, i, p);
        return l5(x, "1.0")
    };
    var o5 = function(a, b, e, i) {
        var p = i5(a, "null", "true");
        var x = m5(a, b, e, p);
        var y = W2(e, i) ? "2.0" : "1.0";
        return l5(x, y)
    };
    var p5 = function(a, b, e, p, x, y, _) {
        var i9 = C2(p);
        var j9 = m(e) ? e : e.results;
        var k9 = p ? {
            type: i9
        } : {};
        k9.properties = x.properties;
        var l9 = m5(a, b, i9 ? p : null);
        var i, m9;
        for (i = 0, m9 = j9.length; i < m9; i++) {
            var n9 = j9[i];
            var o9 = s5(a, "element", n9, k9, y, _);
            t1(l9, o9.element)
        }
        return l5(l9, "3.0")
    };
    var q5 = function(a, b, e, i, p, x, y) {
        var _ = m5(a, b, i);
        var i9 = p.properties || {};
        var j9 = W2(i, y) || {};
        var k9 = "1.0";
        for (var l9 in e) {
            if (l9 !== "__metadata") {
                var m9 = e[l9];
                var n9 = T2(j9.property, l9);
                var o9 = i9[l9] || {};
                var p9 = s5(a, l9, m9, o9, n9, y);
                k9 = c3(k9, p9.dsv);
                t1(_, p9.element)
            }
        };
        return l5(_, k9)
    };
    var r5 = function(a, b, e, i, p) {
        var x = g5(e, i);
        var y = m4(a, e, x, p);
        var _ = m5(a, b, i, y);
        return l5(_, "3.0")
    };
    var s5 = function(a, b, e, i, p, x) {
        var y = G1(e, i, p);
        if (!y) {
            y = G1(e, p)
        }
        if (Q2(e)) {
            return n5(a, b, e, y)
        }
        var _ = N2(y);
        if (_ || O2(y)) {
            return r5(a, b, e, y, _)
        }
        if (G2(e, y)) {
            return p5(a, b, e, y, i, p, x)
        }
        if (P2(e)) {
            return null
        }
        var i9 = S2(e, p);
        if (i9 !== null) {
            return null
        }
        if (e === null) {
            return o5(a, b, y)
        }
        return q5(a, b, e, y, i, p, x)
    };
    var t5 = function(a) {
        if (a && q(a)) {
            var b = r1();
            return t1(b, j5(b, "uri", a.uri))
        }
    };
    var u5 = function(O3, a) {
        if (a) {
            var b = Z(a);
            var e = i1(b);
            if (e) {
                return d5(e)
            }
        }
    };
    var v5 = function(O3, a, b) {
        var e = b.contentType = b.contentType || C3(D4);
        if (e && e.mediaType === D4) {
            return A1(t5(a))
        }
        return u
    };
    o.xmlHandler = O3(u5, v5, D4, B3);
    var w5 = "a";
    var x5 = L + "2005/Atom";
    var y5 = L + "2007/app";
    var z5 = F4 + "/edit-media/";
    var A5 = F4 + "/mediaresource/";
    var B5 = F4 + "/relatedlinks/";
    var C5 = ["application/atom+xml", "application/atomsvc+xml", "application/xml"];
    var D5 = C5[0];
    var E5 = [x5, y5, O, N];
    var F5 = {
        SyndicationAuthorEmail: "author/email",
        SyndicationAuthorName: "author/name",
        SyndicationAuthorUri: "author/uri",
        SyndicationContributorEmail: "contributor/email",
        SyndicationContributorName: "contributor/name",
        SyndicationContributorUri: "contributor/uri",
        SyndicationPublished: "published",
        SyndicationRights: "rights",
        SyndicationSummary: "summary",
        SyndicationTitle: "title",
        SyndicationUpdated: "updated"
    };
    var G5 = function(p) {
        return F5[p] || p
    };
    var H5 = function(a) {
        return !(g(E5, a))
    };
    var I5 = function(a, e, b, p, i) {
        i = i || "";
        var x = a["FC_TargetPath" + i];
        if (!x) {
            return null
        }
        var y = a["FC_SourcePath" + i];
        var _ = G5(x);
        var i9 = p ? p + (y ? "/" + y : "") : y;
        var j9 = i9 && W5(b, e, i9);
        var k9 = a["FC_NsUri" + i] || null;
        var l9 = a["FC_NsPrefix" + i] || null;
        var m9 = a["FC_KeepInContent" + i] || "";
        if (x !== _) {
            k9 = x5;
            l9 = w5
        }
        return {
            contentKind: a["FC_ContentKind" + i],
            keepInContent: m9.toLowerCase() === "true",
            nsPrefix: l9,
            nsURI: k9,
            propertyPath: i9,
            propertyType: j9,
            entryPath: _
        }
    };
    var J5 = function(e, a, b) {
        var p = [];
        while (e) {
            var x = e.FC_SourcePath;
            var y = I5(e, e, a);
            if (y) {
                b(y)
            }
            var _ = e.property || [];
            var i, i9;
            for (i = 0, i9 = _.length; i < i9; i++) {
                var j9 = _[i];
                var k9 = 0;
                var l9 = "";
                while (y = I5(j9, e, a, j9.name, l9)) {
                    b(y);
                    k9++;
                    l9 = "_" + k9
                }
            }
            e = X2(e.baseType, a)
        }
        return p
    };
    var K5 = function(a) {
        var e = [];
        b1(a, function(b) {
            var i = n1(b);
            if (H5(i)) {
                e.push(T4(b, true))
            }
        });
        return e
    };
    var L5 = function(a) {
        return U4(a, true)
    };
    var M5 = function(a, b, e) {
        var i = n1(a);
        var p = m1(a);
        if (i === y5 && p === "service") {
            return h6(a, b)
        }
        if (i === x5) {
            if (p === "feed") {
                return Q5(a, b, e)
            }
            if (p === "entry") {
                return X5(a, b, e)
            }
        }
    };
    var N5 = function(a, b) {
        var e = [];
        var i = {
            extensions: e
        };
        b1(a, function(p) {
            var x = m1(p);
            var y = n1(p);
            var _ = o1(p);
            if (y === null) {
                if (x === "title" || x === "metadata") {
                    i[x] = _;
                    return
                }
                if (x === "target") {
                    i.target = H(_, e1(a, b));
                    return
                }
            }
            if (H5(y)) {
                e.push(T4(p, true))
            }
        });
        return i
    };
    var O5 = function(a, b, p) {
        var e = p.actions = p.actions || [];
        e.push(N5(a, b))
    };
    var P5 = function(a, b, p) {
        var e = p.functions = p.functions || [];
        e.push(N5(a, b))
    };
    var Q5 = function(a, b, e) {
        var i = K5(a);
        var p = {
            feed_extensions: i
        };
        var x = [];
        var y = {
            __metadata: p,
            results: x
        };
        b = e1(a, b);
        f1(a, function(_) {
            var i9 = n1(_);
            var j9 = m1(_);
            if (i9 === O4) {
                if (j9 === "count") {
                    y.__count = parseInt(l1(_));
                    return
                }
                if (j9 === "action") {
                    O5(_, b, p);
                    return
                }
                if (j9 === "function") {
                    P5(_, b, p);
                    return
                }
            }
            if (H5(i9)) {
                i.push(U4(_));
                return
            };
            if (j9 === "entry") {
                x.push(X5(_, b, e));
                return
            }
            if (j9 === "link") {
                R5(_, y, b);
                return
            }
            if (j9 === "id") {
                p.uri = H(l1(_), b);
                p.uri_extensions = K5(_);
                return
            }
            if (j9 === "title") {
                p.title = l1(_) || "";
                p.title_extensions = K5(_);
                return
            }
        });
        return y
    };
    var R5 = function(a, b, e) {
        var i = S5(a, e);
        var p = i.href;
        var x = i.rel;
        var y = i.extensions;
        var _ = b.__metadata;
        if (x === "next") {
            b.__next = p;
            _.next_extensions = y;
            return
        }
        if (x === "self") {
            _.self = p;
            _.self_extensions = y;
            return
        }
    };
    var S5 = function(a, b) {
        b = e1(a, b);
        var e = [];
        var i = {
            extensions: e,
            baseURI: b
        };
        b1(a, function(p) {
            var x = n1(p);
            var y = m1(p);
            var _ = p.value;
            if (y === "href") {
                i.href = H(_, b);
                return
            }
            if (y === "type" || y === "rel") {
                i[y] = _;
                return
            }
            if (H5(x)) {
                e.push(T4(p, true))
            }
        });
        if (!i.href) {
            throw {
                error: "href attribute missing on link element",
                element: a
            }
        }
        return i
    };
    var T5 = function(p, a) {
        if (p.indexOf('/') === -1) {
            return a[p]
        } else {
            var b = p.split('/');
            var i, e;
            for (i = 0, e = b.length; i < e; i++) {
                if (a === null) {
                    return u
                }
                a = a[b[i]];
                if (a === u) {
                    return a
                }
            }
            return a
        }
    };
    var U5 = function(p, a, b, e) {
        var x;
        if (p.indexOf('/') === -1) {
            a[p] = b;
            x = p
        } else {
            var y = p.split('/');
            var i, _;
            for (i = 0, _ = (y.length - 1); i < _; i++) {
                var i9 = a[y[i]];
                if (i9 === u) {
                    i9 = {};
                    a[y[i]] = i9
                } else if (i9 === null) {
                    return
                }
                a = i9
            }
            x = y[i];
            a[x] = b
        }
        if (e) {
            var j9 = a.__metadata = a.__metadata || {};
            var k9 = j9.properties = j9.properties || {};
            var l9 = k9[x] = k9[x] || {};
            l9.type = e
        }
    };
    var V5 = function(a, b, e) {
        var p = a.propertyPath;
        if (a.keepInContent || T5(p, e) === null) {
            return
        }
        var x = h1(b, a.nsURI, a.entryPath);
        if (!x) {
            return
        }
        var i = a.propertyType;
        var y;
        if (a.contentKind === "xhtml") {
            y = B1(x)
        } else {
            y = a5(x, i || "Edm.String")
        }
        U5(p, e, y, i)
    };
    var W5 = function(a, b, p) {
        var e = p.split("/");
        var i, x;
        while (b) {
            var y = b;
            for (i = 0, x = e.length; i < x; i++) {
                var _ = y.property;
                if (!_) {
                    break
                }
                var i9 = T2(_, e[i]);
                if (!i9) {
                    break
                }
                var j9 = i9.type;
                if (!j9 || R2(j9)) {
                    return j9 || null
                }
                y = W2(j9, a);
                if (!y) {
                    return null
                }
            }
            b = X2(b.baseType, a)
        }
        return null
    };
    var X5 = function(a, b, e) {
        var i = {};
        var p = {
            __metadata: i
        };
        var x = c1(a, "etag", O4);
        if (x) {
            i.etag = x
        }
        b = e1(a, b);
        f1(a, function(_) {
            var i9 = n1(_);
            var j9 = m1(_);
            if (i9 === x5) {
                if (j9 === "id") {
                    Y5(_, i, b);
                    return
                }
                if (j9 === "category") {
                    Z5(_, i);
                    return
                }
                if (j9 === "content") {
                    $5(_, p, i, b);
                    return
                }
                if (j9 === "link") {
                    _5(_, p, i, b, e);
                    return
                }
                return
            }
            if (i9 === O4) {
                if (j9 === "properties") {
                    g6(_, p, i);
                    return
                }
                if (j9 === "action") {
                    O5(_, b, i);
                    return
                }
                if (j9 === "function") {
                    P5(_, b, i);
                    return
                }
            }
        });
        var y = X2(i.type, e);
        J5(y, e, function(_) {
            V5(_, a, p)
        });
        return p
    };
    var Y5 = function(a, e, b) {
        e.uri = H(l1(a), e1(a, b));
        e.uri_extensions = K5(a)
    };
    var Z5 = function(a, e) {
        if (c1(a, "scheme") === Q4) {
            if (e.type) {
                throw {
                    message: "Invalid AtomPub document: multiple category elements defining the entry type were encounterd withing an entry",
                    element: a
                }
            }
            var b = [];
            b1(a, function(i) {
                var p = n1(i);
                var x = m1(i);
                if (!p) {
                    if (x !== "scheme" && x !== "term") {
                        b.push(T4(i, true))
                    }
                    return
                }
                if (H5(p)) {
                    b.push(T4(i, true))
                }
            });
            e.type = c1(a, "term");
            e.type_extensions = b
        }
    };
    var $5 = function(a, e, b, i) {
        var p = c1(a, "src");
        var x = c1(a, "type");
        if (p) {
            if (!x) {
                throw {
                    message: "Invalid AtomPub document: content element must specify the type attribute if the src attribute is also specified",
                    element: a
                }
            }
            b.media_src = H(p, e1(a, i));
            b.content_type = x
        }
        f1(a, function(y) {
            if (p) {
                throw {
                    message: "Invalid AtomPub document: content element must not have child elements if the src attribute is specified",
                    element: a
                }
            }
            if (n1(y) === O4 && m1(y) === "properties") {
                g6(y, e, b)
            }
        })
    };
    var _5 = function(a, e, b, i, p) {
        var x = S5(a, i);
        var y = x.rel;
        var _ = x.href;
        var i9 = x.extensions;
        if (y === "self") {
            b.self = _;
            b.self_link_extensions = i9;
            return
        }
        if (y === "edit") {
            b.edit = _;
            b.edit_link_extensions = i9;
            return
        }
        if (y === "edit-media") {
            b.edit_media = x.href;
            b.edit_media_extensions = i9;
            f6(x, b);
            return
        }
        if (y.indexOf(z5) === 0) {
            c6(x, e, b);
            return
        }
        if (y.indexOf(A5) === 0) {
            d6(x, e, b);
            return
        }
        if (y.indexOf(P4) === 0) {
            b6(a, x, e, b, p);
            return
        }
        if (y.indexOf(B5) === 0) {
            a6(x, b);
            return
        }
    };
    var a6 = function(a, e) {
        var p = a.rel.substring(B5.length);
        e.properties = e.properties || {};
        var b = e.properties[p] = e.properties[p] || {};
        b.associationuri = a.href;
        b.associationuri_extensions = a.extensions
    };
    var b6 = function(a, b, e, i, p) {
        var x;
        var y = i1(a, O4, "inline");
        if (y) {
            var _ = i1(y);
            var i9 = e1(y, b.baseURI);
            x = _ ? M5(_, i9, p) : null
        } else {
            x = {
                __deferred: {
                    uri: b.href
                }
            }
        }
        var j9 = b.rel.substring(P4.length);
        e[j9] = x;
        i.properties = i.properties || {};
        var k9 = i.properties[j9] = i.properties[j9] || {};
        k9.extensions = b.extensions
    };
    var c6 = function(a, e, b) {
        var p = a.rel.substring(z5.length);
        var i = e6(p, e, b);
        var x = i.value;
        var y = i.metadata;
        var _ = a.href;
        x.edit_media = _;
        x.content_type = a.type;
        y.edit_media_extensions = a.extensions;
        x.media_src = x.media_src || _;
        y.media_src_extensions = y.media_src_extensions || [];
        f6(a, x)
    };
    var d6 = function(a, e, b) {
        var p = a.rel.substring(A5.length);
        var i = e6(p, e, b);
        var x = i.value;
        var y = i.metadata;
        x.media_src = a.href;
        y.media_src_extensions = a.extensions;
        x.content_type = a.type
    };
    var e6 = function(a, e, b) {
        b.properties = b.properties || {};
        var i = b.properties[a];
        var p = e[a] && e[a].__mediaresource;
        if (!p) {
            p = {};
            e[a] = {
                __mediaresource: p
            };
            b.properties[a] = i = {}
        }
        return {
            value: p,
            metadata: i
        }
    };
    var f6 = function(a, b) {
        var e = a.extensions;
        var i, p;
        for (i = 0, p = e.length; i < p; i++) {
            if (e[i].namespaceURI === O4 && e[i].name === "etag") {
                b.media_etag = e[i].value;
                e.splice(i, 1);
                return
            }
        }
    };
    var g6 = function(a, p, b) {
        f1(a, function(e) {
            var i = Z4(e);
            if (i) {
                var x = i.name;
                var y = b.properties = b.properties || {};
                y[x] = i.metadata;
                p[x] = i.value
            }
        })
    };
    var h6 = function(a, b) {
        var e = [];
        var i = [];
        b = e1(a, b);
        f1(a, function(p) {
            if (n1(p) === y5 && m1(p) === "workspace") {
                e.push(i6(p, b));
                return
            }
            i.push(U4(p))
        });
        if (e.length === 0) {
            throw {
                message: "Invalid AtomPub service document: No workspace element found.",
                element: a
            }
        }
        return {
            workspaces: e,
            extensions: i
        }
    };
    var i6 = function(a, b) {
        var e = [];
        var i = [];
        var p = u;
        b = e1(a, b);
        f1(a, function(x) {
            var y = n1(x);
            var _ = m1(x);
            if (y === x5) {
                if (_ === "title") {
                    if (p !== u) {
                        throw {
                            message: "Invalid AtomPub service document: workspace has more than one child title element",
                            element: x
                        }
                    }
                    p = l1(x);
                    return
                }
            }
            if (y === y5) {
                if (_ === "collection") {
                    e.push(j6(x, b))
                }
                return
            }
            i.push(L5(x))
        });
        return {
            title: p || "",
            collections: e,
            extensions: i
        }
    };
    var j6 = function(a, b) {
        var e = c1(a, "href");
        if (!e) {
            throw {
                message: "Invalid AtomPub service document: collection has no href attribute",
                element: a
            }
        }
        b = e1(a, b);
        e = H(e, e1(a, b));
        var i = [];
        var p = u;
        f1(a, function(x) {
            var y = n1(x);
            var _ = m1(x);
            if (y === x5) {
                if (_ === "title") {
                    if (p !== u) {
                        throw {
                            message: "Invalid AtomPub service document: collection has more than one child title element",
                            element: x
                        }
                    }
                    p = l1(x)
                }
                return
            }
            if (y !== y5) {
                i.push(L5(a))
            }
        });
        if (!p) {
            throw {
                message: "Invalid AtomPub service document: collection has no title element",
                element: a
            }
        }
        return {
            title: p,
            href: e,
            extensions: i
        }
    };
    var k6 = function(a, b, e) {
        return v1(a, x5, $(w5, b), e)
    };
    var l6 = function(a, b, e) {
        return u1(a, null, b, e)
    };
    var m6 = function(p) {
        if (p.childNodes.length > 0) {
            return false
        }
        var a = true;
        var b = p.attributes;
        var i, e;
        for (i = 0, e = b.length; i < e && a; i++) {
            var x = b[i];
            a = a && T(x) || (n1(x) == O4 && m1(x) === "type")
        }
        return a
    };
    var n6 = function(a, b, e, i, p) {
        var x = null;
        var y = null;
        var _ = null;
        var i9 = "";
        if (e !== "deferred") {
            x = l6(a, "type", "application/atom+xml;type=" + e);
            y = h5(a, "inline");
            if (i) {
                i9 = i.__metadata && i.__metadata.uri || "";
                _ = r6(a, i, p) || q6(a, i, p);
                t1(y, _.element)
            }
        } else {
            i9 = i.__deferred.uri
        };
        var j9 = k6(a, "link", [l6(a, "href", i9), l6(a, "rel", H(b, P4)), x, y]);
        return l5(j9, _ ? _.dsv : "1.0")
    };
    var o6 = function(a, b, e, i, p, x) {
        if (P2(e)) {
            return null
        }
        var y = s5(a, b, e, i, p, x);
        if (!y) {
            var _ = S2(e, p);
            y = n6(a, b, _, e, x)
        }
        return y
    };
    var p6 = function(a, e, b, i) {
        var p = g1(b, N4, i.propertyPath);
        var x = p && d1(p, "null", O4);
        var y;
        var _ = "1.0";
        if (x && x.value === "true") {
            return _
        }
        if (p) {
            y = l1(p) || "";
            if (!i.keepInContent) {
                _ = "2.0";
                var i9 = p.parentNode;
                var j9 = i9;
                i9.removeChild(p);
                while (j9 !== b && m6(j9)) {
                    i9 = j9.parentNode;
                    i9.removeChild(j9);
                    j9 = i9
                }
            }
        }
        var k9 = z1(a, e, i.nsURI, i.nsPrefix, i.entryPath);
        if (k9.nodeType === 2) {
            k9.value = y;
            return _
        }
        var l9 = i.contentKind;
        s1(k9, [l9 && u1(a, null, "type", l9), l9 === "xhtml" ? x1(a, y) : y]);
        return _
    };
    var q6 = function(a, b, e) {
        var p = b.__metadata || {};
        var i = p.properties || {};
        var x = p.etag;
        var y = p.uri;
        var _ = p.type;
        var i9 = X2(_, e);
        var j9 = h5(a, "properties");
        var k9 = k6(a, "entry", [k6(a, "author", k6(a, "name")), x && i5(a, "etag", x), y && k6(a, "id", y), _ && k6(a, "category", [l6(a, "term", _), l6(a, "scheme", Q4)]), k6(a, "content", [l6(a, "type", "application/xml"), j9])]);
        var l9 = "1.0";
        for (var m9 in b) {
            if (m9 !== "__metadata") {
                var n9 = i[m9] || {};
                var o9 = i9 && (T2(i9.property, m9) || T2(i9.navigationProperty, m9));
                var p9 = o6(a, m9, b[m9], n9, o9, e);
                if (p9) {
                    var q9 = p9.element;
                    var r9 = (n1(q9) === x5) ? k9 : j9;
                    t1(r9, q9);
                    l9 = c3(l9, p9.dsv)
                }
            }
        };
        J5(i9, e, function(s9) {
            var t9 = p6(a, k9, j9, s9);
            l9 = c3(l9, t9)
        });
        return l5(k9, l9)
    };
    var r6 = function(a, b, e) {
        var p = m(b) ? b : b.results;
        if (!p) {
            return null
        }
        var x = "1.0";
        var y = k6(a, "feed");
        var i, _;
        for (i = 0, _ = p.length; i < _; i++) {
            var i9 = q6(a, p[i], e);
            t1(y, i9.element);
            x = c3(x, i9.dsv)
        }
        return l5(y, x)
    };
    var s6 = function(a, b) {
        if (a) {
            var e = M2(a) && r6 || q(a) && q6;
            if (e) {
                var i = r1();
                var p = e(i, a, b);
                if (p) {
                    var x = p.element;
                    s1(x, [w1(i, O4, S4), w1(i, N4, R4)]);
                    return l5(t1(i, x), p.dsv)
                }
            }
        }
        return null
    };
    var t6 = function(O3, a, b) {
        if (a) {
            var e = Z(a);
            var i = i1(e);
            if (i) {
                return M5(i, null, b.metadata)
            }
        }
    };
    var u6 = function(O3, a, b) {
        var e = b.contentType = b.contentType || C3(D5);
        if (e && e.mediaType === D5) {
            var i = s6(a, b.metadata);
            if (i) {
                b.dataServiceVersion = c3(b.dataServiceVersion || "1.0", i.dsv);
                return A1(i.element)
            }
        }
    };
    o.atomHandler = O3(t6, u6, C5.join(","), B3);
    var v6 = function(a, e, b, i) {
        return {
            attributes: a,
            elements: e,
            text: b || false,
            ns: i
        }
    };
    var w6 = {
        elements: {
            Annotations: v6(["Target", "Qualifier"], ["TypeAnnotation*", "ValueAnnotation*"]),
            Association: v6(["Name"], ["End*", "ReferentialConstraint", "TypeAnnotation*", "ValueAnnotation*"]),
            AssociationSet: v6(["Name", "Association"], ["End*", "TypeAnnotation*", "ValueAnnotation*"]),
            Binary: v6(null, null, true),
            Bool: v6(null, null, true),
            Collection: v6(null, ["String*", "Int*", "Float*", "Decimal*", "Bool*", "DateTime*", "DateTimeOffset*", "Guid*", "Binary*", "Time*", "Collection*", "Record*"]),
            CollectionType: v6(["ElementType", "Nullable", "DefaultValue", "MaxLength", "FixedLength", "Precision", "Scale", "Unicode", "Collation", "SRID"]["CollectionType", "ReferenceType", "RowType", "TypeRef"]),
            ComplexType: v6(["Name", "BaseType", "Abstract"], ["Property*", "TypeAnnotation*", "ValueAnnotation*"]),
            DateTime: v6(null, null, true),
            DateTimeOffset: v6(null, null, true),
            Decimal: v6(null, null, true),
            DefiningExpression: v6(null, null, true),
            Dependent: v6(["Role"], ["PropertyRef*"]),
            Documentation: v6(null, null, true),
            End: v6(["Type", "Role", "Multiplicity", "EntitySet"], ["OnDelete"]),
            EntityContainer: v6(["Name", "Extends"], ["EntitySet*", "AssociationSet*", "FunctionImport*", "TypeAnnotation*", "ValueAnnotation*"]),
            EntitySet: v6(["Name", "EntityType"], ["TypeAnnotation*", "ValueAnnotation*"]),
            EntityType: v6(["Name", "BaseType", "Abstract", "OpenType"], ["Key", "Property*", "NavigationProperty*", "TypeAnnotation*", "ValueAnnotation*"]),
            EnumType: v6(["Name", "UnderlyingType", "IsFlags"], ["Member*"]),
            Float: v6(null, null, true),
            Function: v6(["Name", "ReturnType"], ["Parameter*", "DefiningExpression", "ReturnType", "TypeAnnotation*", "ValueAnnotation*"]),
            FunctionImport: v6(["Name", "ReturnType", "EntitySet", "IsSideEffecting", "IsComposable", "IsBindable", "EntitySetPath"], ["Parameter*", "ReturnType", "TypeAnnotation*", "ValueAnnotation*"]),
            Guid: v6(null, null, true),
            Int: v6(null, null, true),
            Key: v6(null, ["PropertyRef*"]),
            LabeledElement: v6(["Name"], ["Path", "String", "Int", "Float", "Decimal", "Bool", "DateTime", "DateTimeOffset", "Guid", "Binary", "Time", "Collection", "Record", "LabeledElement", "Null"]),
            Member: v6(["Name", "Value"]),
            NavigationProperty: v6(["Name", "Relationship", "ToRole", "FromRole", "ContainsTarget"], ["TypeAnnotation*", "ValueAnnotation*"]),
            Null: v6(null, null),
            OnDelete: v6(["Action"]),
            Path: v6(null, null, true),
            Parameter: v6(["Name", "Type", "Mode", "Nullable", "DefaultValue", "MaxLength", "FixedLength", "Precision", "Scale", "Unicode", "Collation", "ConcurrencyMode", "SRID"], ["CollectionType", "ReferenceType", "RowType", "TypeRef", "TypeAnnotation*", "ValueAnnotation*"]),
            Principal: v6(["Role"], ["PropertyRef*"]),
            Property: v6(["Name", "Type", "Nullable", "DefaultValue", "MaxLength", "FixedLength", "Precision", "Scale", "Unicode", "Collation", "ConcurrencyMode", "CollectionKind", "SRID"], ["CollectionType", "ReferenceType", "RowType", "TypeAnnotation*", "ValueAnnotation*"]),
            PropertyRef: v6(["Name"]),
            PropertyValue: v6(["Property", "Path", "String", "Int", "Float", "Decimal", "Bool", "DateTime", "DateTimeOffset", "Guid", "Binary", "Time"], ["Path", "String", "Int", "Float", "Decimal", "Bool", "DateTime", "DateTimeOffset", "Guid", "Binary", "Time", "Collection", "Record", "LabeledElement", "Null"]),
            ReferenceType: v6(["Type"]),
            ReferentialConstraint: v6(null, ["Principal", "Dependent"]),
            ReturnType: v6(["ReturnType", "Type", "EntitySet"], ["CollectionType", "ReferenceType", "RowType"]),
            RowType: v6(["Property*"]),
            String: v6(null, null, true),
            Schema: v6(["Namespace", "Alias"], ["Using*", "EntityContainer*", "EntityType*", "Association*", "ComplexType*", "Function*", "ValueTerm*", "Annotations*"]),
            Time: v6(null, null, true),
            TypeAnnotation: v6(["Term", "Qualifier"], ["PropertyValue*"]),
            TypeRef: v6(["Type", "Nullable", "DefaultValue", "MaxLength", "FixedLength", "Precision", "Scale", "Unicode", "Collation", "SRID"]),
            Using: v6(["Namespace", "Alias"]),
            ValueAnnotation: v6(["Term", "Qualifier", "Path", "String", "Int", "Float", "Decimal", "Bool", "DateTime", "DateTimeOffset", "Guid", "Binary", "Time"], ["Path", "String", "Int", "Float", "Decimal", "Bool", "DateTime", "DateTimeOffset", "Guid", "Binary", "Time", "Collection", "Record", "LabeledElement", "Null"]),
            ValueTerm: v6(["Name", "Type"], ["TypeAnnotation*", "ValueAnnotation*"]),
            Edmx: v6(["Version"], ["DataServices", "Reference*", "AnnotationsReference*"], false, G4),
            DataServices: v6(null, ["Schema*"], false, G4)
        }
    };
    var x6 = ["m:FC_ContentKind", "m:FC_KeepInContent", "m:FC_NsPrefix", "m:FC_NsUri", "m:FC_SourcePath", "m:FC_TargetPath"];
    w6.elements.Property.attributes = w6.elements.Property.attributes.concat(x6);
    w6.elements.EntityType.attributes = w6.elements.EntityType.attributes.concat(x6);
    w6.elements.Edmx = {
        attributes: ["Version"],
        elements: ["DataServices"],
        ns: G4
    };
    w6.elements.DataServices = {
        elements: ["Schema*"],
        ns: G4
    };
    w6.elements.EntityContainer.attributes.push("m:IsDefaultEntityContainer");
    w6.elements.Property.attributes.push("m:MimeType");
    w6.elements.FunctionImport.attributes.push("m:HttpMethod");
    w6.elements.FunctionImport.attributes.push("m:IsAlwaysBindable");
    w6.elements.EntityType.attributes.push("m:HasStream");
    w6.elements.DataServices.attributes = ["m:DataServiceVersion", "m:MaxDataServiceVersion"];
    var y6 = function(a) {
        if (!a) {
            return a
        }
        if (a.length > 1) {
            var b = a.substr(0, 2);
            if (b === b.toUpperCase()) {
                return a
            }
            return a.charAt(0).toLowerCase() + a.substr(1)
        }
        return a.charAt(0).toLowerCase()
    };
    var z6 = function(p, a) {
        if (a === "Documentation") {
            return {
                isArray: true,
                propertyName: "documentation"
            }
        }
        var e = p.elements;
        if (!e) {
            return null
        }
        var i, b;
        for (i = 0, b = e.length; i < b; i++) {
            var x = e[i];
            var y = false;
            if (x.charAt(x.length - 1) === "*") {
                y = true;
                x = x.substr(0, x.length - 1)
            }
            if (a === x) {
                var _ = y6(x);
                return {
                    isArray: y,
                    propertyName: _
                }
            }
        }
        return null
    };
    var A6 = /^(m:FC_.*)_[0-9]+$/;
    var B6 = function(a) {
        return a === H4 || a === I4 || a === J4 || a === K4 || a === L4 || a === M4
    };
    var C6 = function(e) {
        var a = m1(e);
        var b = n1(e);
        var i = w6.elements[a];
        if (!i) {
            return null
        }
        if (i.ns) {
            if (b !== i.ns) {
                return null
            }
        } else if (!B6(b)) {
            return null
        }
        var p = {};
        var x = [];
        var y = i.attributes || [];
        b1(e, function(_) {
            var a = m1(_);
            var b = n1(_);
            var i9 = _.value;
            if (b === N) {
                return
            }
            var j9 = null;
            var k9 = false;
            if (B6(b) || b === null) {
                j9 = ""
            } else if (b === O4) {
                j9 = "m:"
            }
            if (j9 !== null) {
                j9 += a;
                var l9 = A6.exec(j9);
                if (l9) {
                    j9 = l9[1]
                }
                if (g(y, j9)) {
                    k9 = true;
                    p[y6(a)] = i9
                }
            }
            if (!k9) {
                x.push(T4(_))
            }
        });
        f1(e, function(_) {
            var a = m1(_);
            var i9 = z6(i, a);
            if (i9) {
                if (i9.isArray) {
                    var j9 = p[i9.propertyName];
                    if (!j9) {
                        j9 = [];
                        p[i9.propertyName] = j9
                    }
                    j9.push(C6(_))
                } else {
                    p[i9.propertyName] = C6(_)
                }
            } else {
                x.push(U4(_))
            }
        });
        if (i.text) {
            p.text = l1(e)
        }
        if (x.length) {
            p.extensions = x
        }
        return p
    };
    var D6 = function(O3, a) {
        var b = Z(a);
        var e = i1(b);
        return C6(e) || u
    };
    o.metadataHandler = O3(D6, null, D4, B3);
    var E6 = "o";
    var F6 = "f";
    var G6 = "p";
    var H6 = "c";
    var I6 = "s";
    var J6 = "l";
    var K6 = "odata";
    var L6 = K6 + ".";
    var M6 = "@" + L6 + "bind";
    var N6 = L6 + "metadata";
    var O6 = L6 + "navigationLinkUrl";
    var P6 = L6 + "type";
    var Q6 = {
        readLink: "self",
        editLink: "edit",
        nextLink: "__next",
        mediaReadLink: "media_src",
        mediaEditLink: "edit_media",
        mediaContentType: "content_type",
        mediaETag: "media_etag",
        count: "__count",
        media_src: "mediaReadLink",
        edit_media: "mediaEditLink",
        content_type: "mediaContentType",
        media_etag: "mediaETag",
        url: "uri"
    };
    var R6 = function(a) {
        if (a.indexOf(".") > 0) {
            var b = a.indexOf("@");
            var e = b > -1 ? a.substring(0, b) : null;
            var i = a.substring(b + 1);
            return {
                target: e,
                name: i,
                isOData: i.indexOf(L6) === 0
            }
        }
        return null
    };
    var S6 = function(a, b, e, i, p) {
        return (I2(b) && b[P6]) || (e && e[a + "@" + P6]) || (i && i.type) || (_2(i, p)) || null
    };
    var T6 = function(a, b) {
        if (b) {
            return T2(b.property, a) || T2(b.navigationProperty, a)
        }
        return null
    };
    var U6 = function(a) {
        return I2(a) && a.hasOwnProperty(L6 + "id")
    };
    var V6 = function(a, b, e) {
        if ( !! b[a + "@" + O6] || (e && e.relationship)) {
            return true
        }
        var i = m(b[a]) ? b[a][0] : b[a];
        return U6(i)
    };
    var W6 = function(a) {
        return R2(a) || N2(a) || O2(a)
    };
    var X6 = function(a, b, e, i, p) {
        for (var x in a) {
            if (x.indexOf(".") > 0 && x.charAt(0) !== "#") {
                var y = R6(x);
                if (y) {
                    var _ = y.name;
                    var i9 = y.target;
                    var j9 = null;
                    var k9 = null;
                    if (i9) {
                        j9 = T6(i9, i);
                        k9 = S6(i9, a[i9], a, j9, p)
                    }
                    if (y.isOData) {
                        Y6(_, i9, k9, a[x], a, b, e)
                    } else {
                        b[x] = a[x]
                    }
                }
            }
        }
        return b
    };
    5;
    var Y6 = function(a, b, e, i, p, x, y) {
        var _ = a.substring(L6.length);
        switch (_) {
            case "navigationLinkUrl":
                a7(_, b, e, i, p, x, y);
                return;
            case "nextLink":
            case "count":
                $6(_, b, i, x, y);
                return;
            case "mediaReadLink":
            case "mediaEditLink":
            case "mediaContentType":
            case "mediaETag":
                _6(_, b, e, i, x, y);
                return;
            default:
                Z6(_, b, i, x, y);
                return
        }
    };
    var Z6 = function(a, b, e, i, p) {
        var x = i.__metadata = i.__metadata || {};
        var y = Q6[a] || a;
        if (a === "editLink") {
            x.uri = H(e, p);
            x[y] = x.uri;
            return
        }
        if (a === "readLink" || a === "associationLinkUrl") {
            e = H(e, p)
        }
        if (b) {
            var _ = x.properties = x.properties || {};
            var i9 = _[b] = _[b] || {};
            if (a === "type") {
                i9[y] = i9[y] || e;
                return
            }
            i9[y] = e;
            return
        }
        x[y] = e
    };
    var $6 = function(a, b, e, i, p) {
        var x = Q6[a];
        var y = b ? i[b] : i;
        y[x] = (a === "nextLink") ? H(e, p) : e
    };
    var _6 = function(a, b, e, i, p, x) {
        var y = p.__metadata = p.__metadata || {};
        var _ = Q6[a];
        if (a === "mediaReadLink" || a === "mediaEditLink") {
            i = H(i, x)
        }
        if (b) {
            var i9 = y.properties = y.properties || {};
            var j9 = i9[b] = i9[b] || {};
            j9.type = j9.type || e;
            p.__metadata = y;
            p[b] = p[b] || {
                __mediaresource: {}
            };
            p[b].__mediaresource[_] = i;
            return
        }
        y[_] = i
    };
    var a7 = function(a, b, e, i, p, x, y) {
        var _ = x.__metadata = x.__metadata || {};
        var i9 = _.properties = _.properties || {};
        var j9 = i9[b] = i9[b] || {};
        var k9 = H(i, y);
        if (p.hasOwnProperty(b)) {
            j9.navigationLinkUrl = k9;
            return
        }
        x[b] = {
            __deferred: {
                uri: k9
            }
        };
        j9.type = j9.type || e
    };
    var b7 = function(a, b, e, i, p, x, y) {
        if (typeof a === "string") {
            return c7(a, b, y)
        }
        if (!W6(b)) {
            if (m(a)) {
                return d7(a, b, e, i, x, y)
            }
            if (I2(a)) {
                return e7(a, b, e, i, x, y)
            }
        }
        return a
    };
    var c7 = function(a, p, b) {
        switch (p) {
            case W1:
                return m3(a);
            case L1:
                return i3(a, false);
            case M1:
                return j3(a, false)
        }
        if (b) {
            return i3(a, true) || j3(a, true) || a
        }
        return a
    };
    var d7 = function(a, p, b, e, x, y) {
        var _ = C2(p);
        var i9 = [];
        var j9 = [];
        var i, k9;
        for (i = 0, k9 = a.length; i < k9; i++) {
            var l9 = S6(null, a[i]) || _;
            var m9 = {
                type: l9
            };
            var n9 = b7(a[i], l9, m9, e, null, x, y);
            if (!W6(l9) && !Q2(a[i])) {
                i9.push(m9)
            }
            j9.push(n9)
        }
        if (i9.length > 0) {
            b.elements = i9
        }
        return {
            __metadata: {
                type: p
            },
            results: j9
        }
    };
    var e7 = function(a, p, b, e, i, x) {
        var y = g7(a, p, e, i, x);
        var _ = y.__metadata;
        var i9 = _.properties;
        if (i9) {
            b.properties = i9;
            delete _.properties
        }
        return y
    };
    var f7 = function(a, p, b, e, i) {
        if (m(a)) {
            return i7(a, p, b, e, i)
        }
        if (I2(a)) {
            return g7(a, p, b, e, i)
        }
        return null
    };
    var g7 = function(a, b, e, i, p) {
        var x = a[P6] || b;
        var y = X2(x, i) || W2(x, i);
        var _ = {
            type: x
        };
        var i9 = {
            __metadata: _
        };
        var j9 = {};
        for (var k9 in a) {
            if (k9.indexOf("#") === 0) {
                h7(k9.substring(1), a[k9], i9, e, i)
            } else {
                if (k9.indexOf(".") === -1) {
                    if (!_.properties) {
                        _.properties = j9
                    }
                    var l9 = a[k9];
                    var m9 = T6(k9, y);
                    var n9 = V6(k9, a, m9);
                    var o9 = S6(k9, l9, a, m9, i);
                    var p9 = j9[k9] = j9[k9] || {
                        type: o9
                    };
                    i9[k9] = n9 ? f7(l9, o9, e, i, p) : b7(l9, o9, p9, e, m9, i, p)
                }
            }
        }
        return X6(a, i9, e, y, i)
    };
    var h7 = function(a, b, e, p, x) {
        if (!a || !m(b) && !I2(b)) {
            return
        }
        var y = false;
        var _ = a.lastIndexOf(".");
        var i9 = a.substring(_ + 1);
        var j9 = (_ > -1) ? a.substring(0, _) : "";
        var k9 = (i9 === a || j9.indexOf(".") === -1) ? Y2(x) : Z2(j9, x);
        if (k9) {
            var l9 = $2(k9.functionImport, i9);
            if (l9 && !! l9.isSideEffecting) {
                y = !f3(l9.isSideEffecting)
            }
        };
        var m9 = e.__metadata;
        var n9 = y ? "functions" : "actions";
        var o9 = H(a, p);
        var p9 = (m(b)) ? b : [b];
        var i, q9;
        for (i = 0, q9 = p9.length; i < q9; i++) {
            var r9 = p9[i];
            if (r9) {
                var s9 = m9[n9] = m9[n9] || [];
                var t9 = {
                    metadata: o9,
                    title: r9.title,
                    target: H(r9.target, p)
                };
                s9.push(t9)
            }
        }
    };
    var i7 = function(a, b, e, p, x) {
        var y = m(a) ? a : a.value;
        var _ = [];
        var i, i9;
        for (i = 0, i9 = y.length; i < i9; i++) {
            _.push(g7(y[i], b, e, p, x))
        }
        var j9 = {
            results: _
        };
        if (I2(a)) {
            for (var k9 in a) {
                if (k9.indexOf("#") === 0) {
                    j9.__metadata = j9.__metadata || {};
                    h7(k9.substring(1), a[k9], j9, e, p)
                }
            }
            j9 = X6(a, j9, e)
        }
        return j9
    };
    var j7 = function(a, b, e, i) {
        var p = {
            type: b
        };
        var x = b7(a.value, b, p, e, null, null, i);
        return X6(a, {
            __metadata: p,
            value: x
        }, e)
    };
    var k7 = function(a, b, e, i, p) {
        var x = {};
        var y = d7(a.value, b, x, e, i, p);
        k(y.__metadata, x);
        return X6(a, y, e)
    };
    var l7 = function(a, b) {
        var e = a.value;
        if (!m(e)) {
            return m7(a, b)
        }
        var p = [];
        var i, x;
        for (i = 0, x = e.length; i < x; i++) {
            p.push(m7(e[i], b))
        }
        var y = {
            results: p
        };
        return X6(a, y, b)
    };
    var m7 = function(a, b) {
        var e = {
            uri: H(a.url, b)
        };
        e = X6(a, e, b);
        var i = e.__metadata || {};
        var p = i.properties || {};
        n7(p.url);
        s(p, "url", "uri");
        return e
    };
    var n7 = function(p) {
        if (p) {
            delete p.type
        }
    };
    var o7 = function(a, b) {
        var e = a.value;
        var p = [];
        var x = X6(a, {
            collections: p
        }, b);
        var y = x.__metadata || {};
        var _ = y.properties || {};
        n7(_.value);
        s(_, "value", "collections");
        var i, i9;
        for (i = 0, i9 = e.length; i < i9; i++) {
            var j9 = e[i];
            var k9 = {
                title: j9.name,
                href: H(j9.url, b)
            };
            k9 = X6(j9, k9, b);
            y = k9.__metadata || {};
            _ = y.properties || {};
            n7(_.name);
            n7(_.url);
            s(_, "name", "title");
            s(_, "url", "href");
            p.push(k9)
        }
        return {
            workspaces: [x]
        }
    };
    var p7 = function(a, b) {
        return {
            kind: a,
            type: b || null
        }
    };
    var q7 = function(a, b, i) {
        var e = a[N6];
        if (!e || typeof e !== "string") {
            return null
        }
        var p = e.lastIndexOf("#");
        if (p === -1) {
            return p7(I6)
        }
        var x = e.indexOf("@Element", p);
        var y = x - 1;
        if (y < 0) {
            y = e.indexOf("?", p);
            if (y === -1) {
                y = e.length
            }
        }
        var _ = e.substring(p + 1, y);
        if (_.indexOf("/$links/") > 0) {
            return p7(J6)
        }
        var i9 = _.split("/");
        if (i9.length >= 0) {
            var j9 = i9[0];
            var k9 = i9[1];
            if (W6(j9)) {
                return p7(G6, j9)
            }
            if (H2(j9)) {
                return p7(H6, j9)
            }
            var l9 = k9;
            if (!k9) {
                var m9 = j9.lastIndexOf(".");
                var n9 = j9.substring(m9 + 1);
                var o9 = (n9 === j9) ? Y2(b) : Z2(j9.substring(0, m9), b);
                if (o9) {
                    var p9 = V2(o9.entitySet, n9);
                    l9 = !! p9 ? p9.entityType : null
                }
            }
            if (x > 0) {
                return p7(E6, l9)
            }
            if (l9) {
                return p7(F6, l9)
            }
            if (m(a.value) && !W2(j9, b)) {
                var q9 = a.value[0];
                if (!Q2(q9)) {
                    if (U6(q9) || !i) {
                        return p7(F6, null)
                    }
                }
            }
            return p7(E6, j9)
        }
        return null
    };
    var r7 = function(a, b, e, i) {
        if (!I2(a)) {
            return a
        }
        var p = a[N6];
        var x = q7(a, b, i);
        var y = null;
        if (x) {
            delete a[N6];
            y = x.type;
            switch (x.kind) {
                case F6:
                    return i7(a, y, p, b, e);
                case H6:
                    return k7(a, y, p, b, e);
                case G6:
                    return j7(a, y, p, e);
                case I6:
                    return o7(a, p);
                case J6:
                    return l7(a, p)
            }
        }
        return g7(a, y, p, b, e)
    };
    var s7 = ["type", "etag", "media_src", "edit_media", "content_type", "media_etag"];
    var t7 = function(a, b) {
        var e = /\/\$links\//;
        var i = {};
        var p = a.__metadata;
        var x = b && e.test(b.request.requestUri);
        v7(a, (p && p.properties), i, x);
        return i
    };
    var u7 = function(a, b) {
        if (a) {
            var i, e;
            for (i = 0, e = s7.length; i < e; i++) {
                var p = s7[i];
                var x = L6 + (Q6[p] || p);
                D7(x, null, a[p], b)
            }
        }
    };
    var v7 = function(a, p, b, i) {
        for (var e in a) {
            var x = a[e];
            if (e === "__metadata") {
                u7(x, b)
            } else if (e.indexOf(".") === -1) {
                if (i && e === "uri") {
                    x7(x, b)
                } else {
                    w7(e, x, p, b, i)
                }
            } else {
                b[e] = x
            }
        }
    };
    var w7 = function(a, b, p, e) {
        var i = p && p[a] || {
            properties: u,
            type: u
        };
        var x = G1(b, i);
        if (Q2(b) || !b) {
            D7(P6, a, x, e);
            e[a] = b;
            return
        }
        if (M2(b, x) || L2(b)) {
            A7(a, b, e);
            return
        }
        if (!x && K2(b)) {
            y7(a, b, e);
            return
        }
        if (G2(b, x)) {
            if (C2(x)) {
                D7(P6, a, x, e)
            }
            z7(a, b, e);
            return
        }
        e[a] = {};
        D7(P6, null, x, e[a]);
        v7(b, i.properties, e[a])
    };
    var x7 = function(a, b) {
        b.url = a
    };
    var y7 = function(a, b, e) {
        D7(O6, a, b.__deferred.uri, e)
    };
    var z7 = function(a, b, e) {
        e[a] = [];
        var i = m(b) ? b : b.results;
        v7(i, null, e[a])
    };
    var A7 = function(a, b, e) {
        if (M2(b)) {
            e[a] = [];
            var p = m(b) ? b : b.results;
            var i, x;
            for (i = 0, x = p.length; i < x; i++) {
                B7(a, p[i], true, e)
            }
            return
        }
        B7(a, b, false, e)
    };
    var B7 = function(a, b, i, e) {
        var p = b.__metadata && b.__metadata.uri;
        if (p) {
            C7(a, p, i, e);
            return
        }
        var x = t7(b);
        if (i) {
            e[a].push(x);
            return
        }
        e[a] = x
    };
    var C7 = function(a, b, i, e) {
        var p = a + M6;
        if (i) {
            e[p] = e[p] || [];
            e[p].push(b);
            return
        }
        e[p] = b
    };
    var D7 = function(a, b, e, i) {
        if (e !== u) {
            b ? i[b + "@" + a] = e : i[a] = e
        }
    };
    var E7 = "application/json";
    var F7 = C3(E7);
    var G7 = function(a) {
        var b = [];
        for (name in a) {
            var i, e;
            for (i = 0, e = a[name].length; i < e; i++) {
                b.push(k({
                    metadata: name
                }, a[name][i]))
            }
        }
        return b
    };
    var H7 = function(a, b, e, p) {
        if (a && typeof a === "object") {
            var x;
            var y = a.__metadata;
            if (y) {
                if (y.actions) {
                    y.actions = G7(y.actions)
                }
                if (y.functions) {
                    y.functions = G7(y.functions)
                }
                x = y && y.type
            }
            var _ = X2(x, b) || W2(x, b);
            if (_) {
                var i9 = _.property;
                if (i9) {
                    var i, j9;
                    for (i = 0, j9 = i9.length; i < j9; i++) {
                        var k9 = i9[i];
                        var l9 = k9.name;
                        var m9 = a[l9];
                        if (k9.type === "Edm.DateTime" || k9.type === "Edm.DateTimeOffset") {
                            if (m9) {
                                m9 = e(m9);
                                if (!m9) {
                                    throw {
                                        message: "Invalid date/time value"
                                    }
                                }
                                a[l9] = m9
                            }
                        } else if (k9.type === "Edm.Time") {
                            if (m9) {
                                a[l9] = m3(m9)
                            }
                        }
                    }
                }
            } else if (p) {
                for (var n9 in a) {
                    m9 = a[n9];
                    if (typeof m9 === "string") {
                        a[n9] = e(m9) || m9
                    }
                }
            }
        }
        return a
    };
    var I7 = function(C3) {
        if (C3) {
            var o = C3.properties.odata;
            return o === "nometadata" || o === "minimalmetadata" || o === "fullmetadata"
        }
        return false
    };
    var J7 = function(a, b) {
        var e = {
            collections: []
        };
        var i, p;
        for (i = 0, p = a.EntitySets.length; i < p; i++) {
            var x = a.EntitySets[i];
            var y = {
                title: x,
                href: H(x, b)
            };
            e.collections.push(y)
        }
        return {
            workspaces: [e]
        }
    };
    var K7 = /^\/Date\((-?\d+)(\+|-)?(\d+)?\)\/$/;
    var L7 = function(a) {
        var b;
        if (a < 0) {
            b = "-";
            a = -a
        } else {
            b = "+"
        }
        var e = Math.floor(a / 60);
        a = a - (60 * e);
        return b + A2(e, 2) + ":" + A2(a, 2)
    };
    var M7 = function(a) {
        var b = a && K7.exec(a);
        if (b) {
            var e = new Date(r(b[1]));
            if (b[2]) {
                var i = r(b[3]);
                if (b[2] === "-") {
                    i = -i
                }
                var p = e.getUTCMinutes();
                e.setUTCMinutes(p - i);
                e.__edmType = "Edm.DateTimeOffset";
                e.__offset = L7(i)
            }
            if (!isNaN(e.valueOf())) {
                return e
            }
        }
    };
    var N7 = function(O3, a, b) {
        var e = h(b.recognizeDates, O3.recognizeDates);
        var i = h(b.inferJsonLightFeedAsObject, O3.inferJsonLightFeedAsObject);
        var p = b.metadata;
        var x = b.dataServiceVersion;
        var y = M7;
        var _ = (typeof a === "string") ? w.JSON.parse(a) : a;
        if ((c3("3.0", x) === x)) {
            if (I7(b.contentType)) {
                return r7(_, p, e, i)
            }
            y = i3
        }
        _ = q3(_.d, function(i9, j9) {
            return H7(j9, p, y, e)
        });
        _ = S7(_, b.dataServiceVersion);
        return R7(_, b.response.requestUri)
    };
    var O7 = function(a) {
        var b = u;
        var e = Date.prototype.toJSON;
        try {
            Date.prototype.toJSON = function() {
                return x2(this)
            };
            b = w.JSON.stringify(a, Q7);
            b = b.replace(/\/Date\(([0-9.+-]+)\)\//g, "\\/Date($1)\\/")
        } finally {
            Date.prototype.toJSON = e
        }
        return b
    };
    var P7 = function(O3, a, b) {
        var e = b.dataServiceVersion || "1.0";
        var i = h(b.useJsonLight, O3.useJsonLight);
        var p = b.contentType = b.contentType || F7;
        if (p && p.mediaType === F7.mediaType) {
            var x = a;
            if (i || I7(p)) {
                b.dataServiceVersion = c3(e, "3.0");
                x = t7(a, b);
                return O7(x)
            }
            if (c3("3.0", e) === e) {
                p.properties.odata = "verbose";
                b.contentType = p
            }
            return O7(x)
        }
        return u
    };
    var Q7 = function(_, a) {
        if (a && a.__edmType === "Edm.Time") {
            return z2(a)
        } else {
            return a
        }
    };
    var R7 = function(a, b) {
        var i = I2(a) && !a.__metadata && m(a.EntitySets);
        return i ? J7(a, b) : a
    };
    var S7 = function(a, b) {
        if (b && b.lastIndexOf(";") === b.length - 1) {
            b = b.substr(0, b.length - 1)
        }
        if (!b || b === "1.0") {
            if (m(a)) {
                a = {
                    results: a
                }
            }
        }
        return a
    };
    var T7 = O3(N7, P7, E7, B3);
    T7.recognizeDates = false;
    T7.useJsonLight = false;
    T7.inferJsonLightFeedAsObject = false;
    o.jsonHandler = T7;
    var U7 = "multipart/mixed";
    var V7 = /^HTTP\/1\.\d (\d{3}) (.*)$/i;
    var W7 = /^([^()<>@,;:\\"\/[\]?={} \t]+)\s?:\s?(.*)/;
    var X7 = function() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substr(1)
    };
    var Y7 = function(p) {
        return p + X7() + "-" + X7() + "-" + X7()
    };
    var Z7 = function(a) {
        return a.handler.partHandler
    };
    var $7 = function(a) {
        var b = a.boundaries;
        return b[b.length - 1]
    };
    var _7 = function(O3, a, b) {
        var e = b.contentType.properties["boundary"];
        return {
            __batchResponses: b8(a, {
                boundaries: [e],
                handlerContext: b
            })
        }
    };
    var a8 = function(O3, a, b) {
        var e = b.contentType = b.contentType || C3(U7);
        if (e.mediaType === U7) {
            return g8(a, b)
        }
    };
    var b8 = function(a, b) {
        var i = "--" + $7(b);
        f8(a, b, i);
        e8(a, b);
        var p = [];
        var x;
        while (x !== "--" && b.position < a.length) {
            var y = c8(a, b);
            var _ = C3(y["Content-Type"]);
            if (_ && _.mediaType === U7) {
                b.boundaries.push(_.properties["boundary"]);
                try {
                    var i9 = b8(a, b)
                } catch (e) {
                    e.response = d8(a, b, i);
                    i9 = [e]
                }
                p.push({
                    __changeResponses: i9
                });
                b.boundaries.pop();
                f8(a, b, "--" + $7(b))
            } else {
                if (!_ || _.mediaType !== "application/http") {
                    throw {
                        message: "invalid MIME part type "
                    }
                }
                e8(a, b);
                var j9 = d8(a, b, i);
                try {
                    if (j9.statusCode >= 200 && j9.statusCode <= 299) {
                        Z7(b.handlerContext).read(j9, b.handlerContext)
                    } else {
                        j9 = {
                            message: "HTTP request failed",
                            response: j9
                        }
                    }
                } catch (e) {
                    j9 = e
                }
                p.push(j9)
            }
            x = a.substr(b.position, 2);
            e8(a, b)
        }
        return p
    };
    var c8 = function(a, b) {
        var e = {};
        var p;
        var i;
        var x;
        do {
            x = b.position;
            i = e8(a, b);
            p = W7.exec(i);
            if (p !== null) {
                e[p[1]] = p[2]
            } else {
                b.position = x
            }
        } while (i && p);
        e3(e);
        return e
    };
    var d8 = function(a, b, e) {
        var p = b.position;
        var i = V7.exec(e8(a, b));
        var x;
        var y;
        var _;
        if (i) {
            x = i[1];
            y = i[2];
            _ = c8(a, b);
            e8(a, b)
        } else {
            b.position = p
        }
        return {
            statusCode: x,
            statusText: y,
            headers: _,
            body: f8(a, b, "\r\n" + e)
        }
    };
    var e8 = function(a, b) {
        return f8(a, b, "\r\n")
    };
    var f8 = function(a, b, e) {
        var i = b.position || 0;
        var p = a.length;
        if (e) {
            p = a.indexOf(e, i);
            if (p === -1) {
                return null
            }
            b.position = p + e.length
        } else {
            b.position = p
        }
        return a.substring(i, p)
    };
    var g8 = function(a, b) {
        if (!E2(a)) {
            throw {
                message: "Data is not a batch object."
            }
        }
        var e = Y7("batch_");
        var p = a.__batchRequests;
        var x = "";
        var i, y;
        for (i = 0, y = p.length; i < y; i++) {
            x += h8(e, false) + i8(p[i], b)
        }
        x += h8(e, true);
        var _ = b.contentType.properties;
        _.boundary = e;
        return x
    };
    var h8 = function(b, a) {
        var e = "\r\n--" + b;
        if (a) {
            e += "--"
        }
        return e + "\r\n"
    };
    var i8 = function(p, a, b) {
        var e = p.__changeRequests;
        var x;
        if (m(e)) {
            if (b) {
                throw {
                    message: "Not Supported: change set nested in other change set"
                }
            }
            var y = Y7("changeset_");
            x = "Content-Type: " + U7 + "; boundary=" + y + "\r\n";
            var i, _;
            for (i = 0, _ = e.length; i < _; i++) {
                x += h8(y, false) + i8(e[i], a, true)
            }
            x += h8(y, true)
        } else {
            x = "Content-Type: application/http\r\nContent-Transfer-Encoding: binary\r\n\r\n";
            var i9 = k({}, a);
            i9.handler = O3;
            i9.request = p;
            i9.contentType = null;
            o3(p, Z7(a), i9);
            x += j8(p)
        }
        return x
    };
    var j8 = function(a) {
        var b = (a.method ? a.method : "GET") + " " + a.requestUri + " HTTP/1.1\r\n";
        for (var e in a.headers) {
            if (a.headers[e]) {
                b = b + e + ": " + a.headers[e] + "\r\n"
            }
        }
        if (a.body) {
            function p(i) {
                if (i <= 0x7F) return 1;
                if (i <= 0x7FF) return 2;
                if (i <= 0xFFFF) return 3;
                if (i <= 0x1FFFFF) return 4;
                if (i <= 0x3FFFFFF) return 5;
                if (i <= 0x7FFFFFFF) return 6;
                throw new Error("Illegal argument: " + i)
            };

            function x(y) {
                var _ = 0;
                for (var i = 0; i < y.length; i++) {
                    var ch = y.charCodeAt(i);
                    _ += p(ch)
                }
                return _
            };
            b += "Content-Length: " + x(a.body) + "\r\n"
        }
        b += "\r\n";
        if (a.body) {
            b += a.body
        }
        return b
    };
    o.batchHandler = O3(_7, a8, U7, B3);
    var k8 = [o.jsonHandler, o.atomHandler, o.xmlHandler, o.textHandler];
    var l8 = function(a, b, e) {
        var i, p;
        for (i = 0, p = k8.length; i < p && !k8[i][a](b, e); i++) {}
        if (i === p) {
            throw {
                message: "no handler for data"
            }
        }
    };
    o.defaultSuccess = function(a) {
        w.alert(w.JSON.stringify(a))
    };
    o.defaultError = t;
    o.defaultHandler = {
        read: function(a, b) {
            if (a && a.body && a.headers["Content-Type"]) {
                l8("read", a, b)
            }
        },
        write: function(a, b) {
            l8("write", a, b)
        },
        maxDataServiceVersion: B3,
        accept: "application/atomsvc+xml;q=0.8, application/json;odata=fullmetadata;q=0.7, application/json;q=0.5, */*;q=0.1"
    };
    o.defaultMetadata = [];
    o.read = function(a, b, e, O3, i, p) {
        var x;
        if (a instanceof String || typeof a === "string") {
            x = {
                requestUri: a
            }
        } else {
            x = a
        }
        return o.request(x, b, e, O3, i, p)
    };
    o.request = function(a, b, e, O3, i, p) {
        b = b || o.defaultSuccess;
        e = e || o.defaultError;
        O3 = O3 || o.defaultHandler;
        i = i || o.defaultHttpClient;
        p = p || o.defaultMetadata;
        a.recognizeDates = h(a.recognizeDates, o.jsonHandler.recognizeDates);
        a.callbackParameterName = h(a.callbackParameterName, o.defaultHttpClient.callbackParameterName);
        a.formatQueryString = h(a.formatQueryString, o.defaultHttpClient.formatQueryString);
        a.enableJsonpCallback = h(a.enableJsonpCallback, o.defaultHttpClient.enableJsonpCallback);
        a.useJsonLight = h(a.useJsonLight, o.jsonHandler.enableJsonpCallback);
        a.inferJsonLightFeedAsObject = h(a.inferJsonLightFeedAsObject, o.jsonHandler.inferJsonLightFeedAsObject);
        var x = {
            metadata: p,
            recognizeDates: a.recognizeDates,
            callbackParameterName: a.callbackParameterName,
            formatQueryString: a.formatQueryString,
            enableJsonpCallback: a.enableJsonpCallback,
            useJsonLight: a.useJsonLight,
            inferJsonLightFeedAsObject: a.inferJsonLightFeedAsObject
        };
        try {
            o3(a, O3, x);
            return D2(a, b, e, O3, i, x)
        } catch (y) {
            if (y.bIsSuccessHandlerError) {
                throw y
            } else {
                e(y)
            }
        }
    };
    o.batchHandler.partHandler = o.defaultHandler;
    var m8 = null;
    var n8 = function() {
        var a = {
            v: this.valueOf(),
            t: "[object Date]"
        };
        for (var b in this) {
            a[b] = this[b]
        }
        return a
    };
    var o8 = function(_, a) {
        if (a && a.t === "[object Date]") {
            var b = new Date(a.v);
            for (var e in a) {
                if (e !== "t" && e !== "v") {
                    b[e] = a[e]
                }
            }
            a = b
        }
        return a
    };
    var p8 = function(a, b) {
        return a.name + "#!#" + b
    };
    var q8 = function(a, b) {
        return b.replace(a.name + "#!#", "")
    };
    var r8 = function(a) {
        this.name = a
    };
    r8.create = function(a) {
        if (r8.isSupported()) {
            m8 = m8 || w.localStorage;
            return new r8(a)
        }
        throw {
            message: "Web Storage not supported by the browser"
        }
    };
    r8.isSupported = function() {
        return !!w.localStorage
    };
    r8.prototype.add = function(a, b, e, i) {
        i = i || this.defaultError;
        var p = this;
        this.contains(a, function(x) {
            if (!x) {
                p.addOrUpdate(a, b, e, i)
            } else {
                j(i, {
                    message: "key already exists",
                    key: a
                })
            }
        }, i)
    };
    r8.prototype.addOrUpdate = function(a, b, i, p) {
        p = p || this.defaultError;
        if (a instanceof Array) {
            p({
                message: "Array of keys not supported"
            })
        } else {
            var x = p8(this, a);
            var y = Date.prototype.toJSON;
            try {
                var _ = b;
                if (_ !== u) {
                    Date.prototype.toJSON = n8;
                    _ = w.JSON.stringify(b)
                }
                m8.setItem(x, _);
                j(i, a, b)
            } catch (e) {
                if (e.code === 22 || e.number === 0x8007000E) {
                    j(p, {
                        name: "QUOTA_EXCEEDED_ERR",
                        error: e
                    })
                } else {
                    j(p, e)
                }
            } finally {
                Date.prototype.toJSON = y
            }
        }
    };
    r8.prototype.clear = function(a, b) {
        b = b || this.defaultError;
        try {
            var i = 0,
                p = m8.length;
            while (p > 0 && i < p) {
                var x = m8.key(i);
                var y = q8(this, x);
                if (x !== y) {
                    m8.removeItem(x);
                    p = m8.length
                } else {
                    i++
                }
            };
            j(a)
        } catch (e) {
            j(b, e)
        }
    };
    r8.prototype.close = function() {};
    r8.prototype.contains = function(a, b, i) {
        i = i || this.defaultError;
        try {
            var p = p8(this, a);
            var x = m8.getItem(p);
            j(b, x !== null)
        } catch (e) {
            j(i, e)
        }
    };
    r8.prototype.defaultError = t;
    r8.prototype.getAllKeys = function(a, b) {
        b = b || this.defaultError;
        var p = [];
        var i, x;
        try {
            for (i = 0, x = m8.length; i < x; i++) {
                var y = m8.key(i);
                var _ = q8(this, y);
                if (y !== _) {
                    p.push(_)
                }
            }
            j(a, p)
        } catch (e) {
            j(b, e)
        }
    };
    r8.prototype.mechanism = "dom";
    r8.prototype.read = function(a, b, i) {
        i = i || this.defaultError;
        if (a instanceof Array) {
            i({
                message: "Array of keys not supported"
            })
        } else {
            try {
                var p = p8(this, a);
                var x = m8.getItem(p);
                if (x !== null && x !== "undefined") {
                    x = w.JSON.parse(x, o8)
                } else {
                    x = u
                }
                j(b, a, x)
            } catch (e) {
                j(i, e)
            }
        }
    };
    r8.prototype.remove = function(a, b, i) {
        i = i || this.defaultError;
        if (a instanceof Array) {
            i({
                message: "Batches not supported"
            })
        } else {
            try {
                var p = p8(this, a);
                m8.removeItem(p);
                j(b)
            } catch (e) {
                j(i, e)
            }
        }
    };
    r8.prototype.update = function(a, b, e, i) {
        i = i || this.defaultError;
        var p = this;
        this.contains(a, function(x) {
            if (x) {
                p.addOrUpdate(a, b, e, i)
            } else {
                j(i, {
                    message: "key not found",
                    key: a
                })
            }
        }, i)
    };
    var s8 = w.mozIndexedDB || w.webkitIndexedDB || w.msIndexedDB || w.indexedDB;
    var t8 = w.IDBKeyRange || w.webkitIDBKeyRange;
    var u8 = w.IDBTransaction || w.webkitIDBTransaction || {};
    var v8 = u8.READ_ONLY || "readonly";
    var w8 = u8.READ_WRITE || "readwrite";
    var x8 = function(a, b) {
        return function(e) {
            var i = a || b;
            if (!i) {
                return
            }
            if (Object.prototype.toString.call(e) === "[object IDBDatabaseException]") {
                if (e.code === 11) {
                    i({
                        name: "QuotaExceededError",
                        error: e
                    });
                    return
                }
                i(e);
                return
            }
            var p;
            try {
                var x = e.target.error || e;
                p = x.name
            } catch (y) {
                p = (e.type === "blocked") ? "IndexedDBBlocked" : "UnknownError"
            }
            i({
                name: p,
                error: e
            })
        }
    };
    var y8 = function(a, b, e) {
        var i = a.name;
        var p = "_datajs_" + i;
        var x = s8.open(p);
        x.onblocked = e;
        x.onerror = e;
        x.onupgradeneeded = function() {
            var y = x.result;
            if (!y.objectStoreNames.contains(i)) {
                y.createObjectStore(i)
            }
        };
        x.onsuccess = function(y) {
            var _ = x.result;
            if (!_.objectStoreNames.contains(i)) {
                if ("setVersion" in _) {
                    var i9 = _.setVersion("1.0");
                    i9.onsuccess = function() {
                        var j9 = i9.transaction;
                        j9.oncomplete = function() {
                            b(_)
                        };
                        _.createObjectStore(i, null, false)
                    };
                    i9.onerror = e;
                    i9.onblocked = e;
                    return
                }
                y.target.error = {
                    name: "DBSchemaMismatch"
                };
                e(y);
                return
            }
            _.onversionchange = function(y) {
                y.target.close()
            };
            b(_)
        }
    };
    var z8 = function(a, b, e, i) {
        var p = a.name;
        var x = a.db;
        var y = x8(i, a.defaultError);
        if (x) {
            e(x.transaction(p, b));
            return
        }
        y8(a, function(_) {
            a.db = _;
            e(_.transaction(p, b))
        }, y)
    };
    var A8 = function(a) {
        this.name = a
    };
    A8.create = function(a) {
        if (A8.isSupported()) {
            return new A8(a)
        }
        throw {
            message: "IndexedDB is not supported on this browser"
        }
    };
    A8.isSupported = function() {
        return !!s8
    };
    A8.prototype.add = function(a, b, e, p) {
        var x = this.name;
        var y = this.defaultError;
        var _ = [];
        var i9 = [];
        if (a instanceof Array) {
            _ = a;
            i9 = b
        } else {
            _ = [a];
            i9 = [b]
        }
        z8(this, w8, function(j9) {
            j9.onabort = x8(p, y, a, "add");
            j9.oncomplete = function() {
                if (a instanceof Array) {
                    e(_, i9)
                } else {
                    e(a, b)
                }
            };
            for (var i = 0; i < _.length && i < i9.length; i++) {
                j9.objectStore(x).add({
                    v: i9[i]
                }, _[i])
            }
        }, p)
    };
    A8.prototype.addOrUpdate = function(a, b, e, p) {
        var x = this.name;
        var y = this.defaultError;
        var _ = [];
        var i9 = [];
        if (a instanceof Array) {
            _ = a;
            i9 = b
        } else {
            _ = [a];
            i9 = [b]
        }
        z8(this, w8, function(j9) {
            j9.onabort = x8(p, y);
            j9.oncomplete = function() {
                if (a instanceof Array) {
                    e(_, i9)
                } else {
                    e(a, b)
                }
            };
            for (var i = 0; i < _.length && i < i9.length; i++) {
                var k9 = {
                    v: i9[i]
                };
                j9.objectStore(x).put(k9, _[i])
            }
        }, p)
    };
    A8.prototype.clear = function(a, e) {
        var b = this.name;
        var i = this.defaultError;
        z8(this, w8, function(p) {
            p.onerror = x8(e, i);
            p.oncomplete = function() {
                a()
            };
            p.objectStore(b).clear()
        }, e)
    };
    A8.prototype.close = function() {
        if (this.db) {
            this.db.close();
            this.db = null
        }
    };
    A8.prototype.contains = function(a, b, e) {
        var i = this.name;
        var p = this.defaultError;
        z8(this, v8, function(x) {
            var y = x.objectStore(i);
            var _ = y["get"](a);
            x.oncomplete = function() {
                b( !! _.result)
            };
            x.onerror = x8(e, p)
        }, e)
    };
    A8.prototype.defaultError = t;
    A8.prototype.getAllKeys = function(a, e) {
        var b = this.name;
        var i = this.defaultError;
        z8(this, w8, function(p) {
            var x = [];
            p.oncomplete = function() {
                a(x)
            };
            var y = p.objectStore(b).openCursor();
            y.onerror = x8(e, i);
            y.onsuccess = function(_) {
                var i9 = _.target.result;
                if (i9) {
                    x.push(i9.key);
                    i9["continue"].call(i9)
                }
            }
        }, e)
    };
    A8.prototype.mechanism = "indexeddb";
    A8.prototype.read = function(a, b, e) {
        var p = this.name;
        var x = this.defaultError;
        var y = (a instanceof Array) ? a : [a];
        z8(this, v8, function(_) {
            var i9 = [];
            _.onerror = x8(e, x, a, "read");
            _.oncomplete = function() {
                if (a instanceof Array) {
                    b(y, i9)
                } else {
                    b(y[0], i9[0])
                }
            };
            for (var i = 0; i < y.length; i++) {
                var j9 = _.objectStore(p);
                var k9 = j9["get"].call(j9, y[i]);
                k9.onsuccess = function(l9) {
                    var m9 = l9.target.result;
                    i9.push(m9 ? m9.v : u)
                }
            }
        }, e)
    };
    A8.prototype.remove = function(a, b, e) {
        var p = this.name;
        var x = this.defaultError;
        var y = (a instanceof Array) ? a : [a];
        z8(this, w8, function(_) {
            _.onerror = x8(e, x);
            _.oncomplete = function() {
                b()
            };
            for (var i = 0; i < y.length; i++) {
                var i9 = _.objectStore(p);
                i9["delete"].call(i9, y[i])
            }
        }, e)
    };
    A8.prototype.update = function(a, b, e, p) {
        var x = this.name;
        var y = this.defaultError;
        var _ = [];
        var i9 = [];
        if (a instanceof Array) {
            _ = a;
            i9 = b
        } else {
            _ = [a];
            i9 = [b]
        }
        z8(this, w8, function(j9) {
            j9.onabort = x8(p, y);
            j9.oncomplete = function() {
                if (a instanceof Array) {
                    e(_, i9)
                } else {
                    e(a, b)
                }
            };
            for (var i = 0; i < _.length && i < i9.length; i++) {
                var k9 = j9.objectStore(x).openCursor(t8.only(_[i]));
                var l9 = {
                    v: i9[i]
                };
                k9.pair = {
                    key: _[i],
                    value: l9
                };
                k9.onsuccess = function(m9) {
                    var n9 = m9.target.result;
                    if (n9) {
                        n9.update(m9.target.pair.value)
                    } else {
                        j9.abort()
                    }
                }
            }
        }, p)
    };
    var B8 = function(a) {
        var b = [];
        var i = [];
        var e = {};
        this.name = a;
        var p = function(y) {
            return y || this.defaultError
        };
        var x = function(y, _) {
            var i9;
            if (y instanceof Array) {
                i9 = "Array of keys not supported"
            }
            if (y === u || y === null) {
                i9 = "Invalid key"
            }
            if (i9) {
                j(_, {
                    message: i9
                });
                return false
            }
            return true
        };
        this.add = function(y, _, i9, j9) {
            j9 = p(j9);
            if (x(y, j9)) {
                if (!e.hasOwnProperty(y)) {
                    this.addOrUpdate(y, _, i9, j9)
                } else {
                    j9({
                        message: "key already exists",
                        key: y
                    })
                }
            }
        };
        this.addOrUpdate = function(y, _, i9, j9) {
            j9 = p(j9);
            if (x(y, j9)) {
                var k9 = e[y];
                if (k9 === u) {
                    if (b.length > 0) {
                        k9 = b.splice(0, 1)
                    } else {
                        k9 = i.length
                    }
                }
                i[k9] = _;
                e[y] = k9;
                j(i9, y, _)
            }
        };
        this.clear = function(y) {
            i = [];
            e = {};
            b = [];
            j(y)
        };
        this.contains = function(y, _) {
            var i9 = e.hasOwnProperty(y);
            j(_, i9)
        };
        this.getAllKeys = function(y) {
            var _ = [];
            for (var a in e) {
                _.push(a)
            }
            j(y, _)
        };
        this.read = function(y, _, i9) {
            i9 = p(i9);
            if (x(y, i9)) {
                var j9 = e[y];
                j(_, y, i[j9])
            }
        };
        this.remove = function(y, _, i9) {
            i9 = p(i9);
            if (x(y, i9)) {
                var j9 = e[y];
                if (j9 !== u) {
                    if (j9 === i.length - 1) {
                        i.pop()
                    } else {
                        i[j9] = u;
                        b.push(j9)
                    }
                    delete e[y];
                    if (i.length === 0) {
                        b = []
                    }
                }
                j(_)
            }
        };
        this.update = function(y, _, i9, j9) {
            j9 = p(j9);
            if (x(y, j9)) {
                if (e.hasOwnProperty(y)) {
                    this.addOrUpdate(y, _, i9, j9)
                } else {
                    j9({
                        message: "key not found",
                        key: y
                    })
                }
            }
        }
    };
    B8.create = function(a) {
        return new B8(a)
    };
    B8.isSupported = function() {
        return true
    };
    B8.prototype.close = function() {};
    B8.prototype.defaultError = t;
    B8.prototype.mechanism = "memory";
    var C8 = {
        indexeddb: A8,
        dom: r8,
        memory: B8
    };
    d.defaultStoreMechanism = "best";
    d.createStore = function(a, b) {
        if (!b) {
            b = d.defaultStoreMechanism
        }
        if (b === "best") {
            b = (r8.isSupported()) ? "dom" : "memory"
        }
        var e = C8[b];
        if (e) {
            return e.create(a)
        }
        throw {
            message: "Failed to create store",
            name: a,
            mechanism: b
        }
    };
    var D8 = function(a, b) {
        var e = (a.indexOf("?") >= 0) ? "&" : "?";
        return a + e + b
    };
    var E8 = function(a, b) {
        var i = a.indexOf("?");
        var e = "";
        if (i >= 0) {
            e = a.substr(i);
            a = a.substr(0, i)
        }
        if (a[a.length - 1] !== "/") {
            a += "/"
        }
        return a + b + e
    };
    var F8 = function(a, b) {
        return {
            method: "GET",
            requestUri: a,
            user: b.user,
            password: b.password,
            withCredentials: b.withCredentials,
            enableJsonpCallback: b.enableJsonpCallback,
            callbackParameterName: b.callbackParameterName,
            formatQueryString: b.formatQueryString
        }
    };
    var G8 = function(a, b) {
        var e = -1;
        var i = a.indexOf("?");
        if (i !== -1) {
            var p = a.indexOf("?" + b + "=", i);
            if (p === -1) {
                p = a.indexOf("&" + b + "=", i)
            }
            if (p !== -1) {
                e = p + b.length + 2
            }
        }
        return e
    };
    var H8 = function(a, b, e, i) {
        var p = I8(a, b, [], e, i);
        return p
    };
    var I8 = function(a, b, e, i, p) {
        var x = F8(a, b);
        var y = o.request(x, function(_) {
            var i9 = _.__next;
            var j9 = _.results;
            e = e.concat(j9);
            if (i9) {
                y = I8(i9, b, e, i, p)
            } else {
                i(e)
            }
        }, p, u, b.httpClient, b.metadata);
        return {
            abort: function() {
                y.abort()
            }
        }
    };
    var J8 = function(a) {
        var b = this;
        var e = a.source;
        b.identifier = G(encodeURI(decodeURI(e)));
        b.options = a;
        b.count = function(i, p) {
            var a = b.options;
            return o.request(F8(E8(e, "$count"), a), function(x) {
                var y = r(x.toString());
                if (isNaN(y)) {
                    p({
                        message: "Count is NaN",
                        count: y
                    })
                } else {
                    i(y)
                }
            }, p, u, a.httpClient, a.metadata)
        };
        b.read = function(i, p, x, y) {
            var _ = "$skip=" + i + "&$top=" + p;
            return H8(D8(e, _), b.options, x, y)
        };
        return b
    };
    var K8 = function(a, p) {

        /// <param name="operation" type="Object">Operation with (i)ndex, (c)ount and (d)ata.</param>

        /// <param name="page" type="Object">Page with (i)ndex, (c)ount and (d)ata.</param>

        var i = L8(a, p);
        if (i) {
            var b = i.i - p.i;
            var e = b + (a.c - a.d.length);
            a.d = a.d.concat(p.d.slice(b, e))
        }
    };
    var L8 = function(x, y) {

        /// <summary>Returns the {(i)ndex, (c)ount} range for the intersection of x and y.</summary>

        /// <param name="x" type="Object">Range with (i)ndex and (c)ount members.</param>

        /// <param name="y" type="Object">Range with (i)ndex and (c)ount members.</param>

        /// <returns type="Object">The intersection (i)ndex and (c)ount; undefined if there is no intersection.</returns>

        var a = x.i + x.c;
        var b = y.i + y.c;
        var e = (x.i > y.i) ? x.i : y.i;
        var i = (a < b) ? a : b;
        var p;
        if (i >= e) {
            p = {
                i: e,
                c: i - e
            }
        }
        return p
    };
    var M8 = function(a, b) {
        if (a === u || typeof a !== "number") {
            throw {
                message: "'" + b + "' must be a number."
            }
        }
        if (isNaN(a) || a < 0 || !isFinite(a)) {
            throw {
                message: "'" + b + "' must be greater than or equal to zero."
            }
        }
    };
    var N8 = function(a, b) {
        if (a !== u) {
            if (typeof a !== "number") {
                throw {
                    message: "'" + b + "' must be a number."
                }
            }
            if (isNaN(a) || a <= 0 || !isFinite(a)) {
                throw {
                    message: "'" + b + "' must be greater than zero."
                }
            }
        }
    };
    var O8 = function(a, b) {
        if (a !== u && (typeof a !== "number" || isNaN(a) || !isFinite(a))) {
            throw {
                message: "'" + b + "' must be a number."
            }
        }
    };
    var P8 = function(a, b) {
        var i, e;
        for (i = 0, e = a.length; i < e; i++) {
            if (a[i] === b) {
                a.splice(i, 1);
                return true
            }
        }
        return false
    };
    var Q8 = function(a) {
        var b = 0;
        var e = typeof a;
        if (e === "object" && a) {
            for (var i in a) {
                b += i.length * 2 + Q8(a[i])
            }
        } else if (e === "string") {
            b = a.length * 2
        } else {
            b = 8
        }
        return b
    };
    var R8 = function(a, b, p) {

        /// <returns type="Object">A range with (i)ndex and (c)ount of elements.</returns>

        a = Math.floor(a / p) * p;
        b = Math.ceil((b + 1) / p) * p;
        return {
            i: a,
            c: b - a
        }
    };
    var S8 = "destroy";
    var T8 = "idle";
    var U8 = "init";
    var V8 = "read";
    var W8 = "prefetch";
    var X8 = "write";
    var Y8 = "cancel";
    var Z8 = "end";
    var $8 = "error";
    var _8 = "start";
    var a9 = "wait";
    var b9 = "clear";
    var c9 = "done";
    var d9 = "local";
    var e9 = "save";
    var f9 = "source";
    var g9 = function(a, p, i, b, e, x, y) {
        var _;
        var i9;
        var j9 = this;
        j9.p = p;
        j9.i = b;
        j9.c = e;
        j9.d = x;
        j9.s = _8;
        j9.canceled = false;
        j9.pending = y;
        j9.oncomplete = null;
        j9.cancel = function() {
            if (!i) {
                return
            }
            var m9 = j9.s;
            if (m9 !== $8 && m9 !== Z8 && m9 !== Y8) {
                j9.canceled = true;
                l9(Y8, _)
            }
        };
        j9.complete = function() {
            l9(Z8, _)
        };
        j9.error = function(m9) {
            if (!j9.canceled) {
                l9($8, m9)
            }
        };
        j9.run = function(m9) {
            i9 = m9;
            j9.transition(j9.s, _)
        };
        j9.wait = function(x) {
            l9(a9, x)
        };
        var k9 = function(m9, i9, x) {
            switch (m9) {
                case _8:
                    if (i9 !== U8) {
                        a(j9, m9, i9, x)
                    }
                    break;
                case a9:
                    a(j9, m9, i9, x);
                    break;
                case Y8:
                    a(j9, m9, i9, x);
                    j9.fireCanceled();
                    l9(Z8);
                    break;
                case $8:
                    a(j9, m9, i9, x);
                    j9.canceled = true;
                    j9.fireRejected(x);
                    l9(Z8);
                    break;
                case Z8:
                    if (j9.oncomplete) {
                        j9.oncomplete(j9)
                    }
                    if (!j9.canceled) {
                        j9.fireResolved()
                    }
                    a(j9, m9, i9, x);
                    break;
                default:
                    a(j9, m9, i9, x);
                    break
            }
        };
        var l9 = function(m9, x) {
            j9.s = m9;
            _ = x;
            k9(m9, i9, x)
        };
        j9.transition = l9;
        return j9
    };
    g9.prototype.fireResolved = function() {
        var p = this.p;
        if (p) {
            this.p = null;
            p.resolve(this.d)
        }
    };
    g9.prototype.fireRejected = function(a) {
        var p = this.p;
        if (p) {
            this.p = null;
            p.reject(a)
        }
    };
    g9.prototype.fireCanceled = function() {
        this.fireRejected({
            canceled: true,
            message: "Operation canceled"
        })
    };
    var h9 = function(a) {
        var b = U8;
        var e = {
            counts: 0,
            netReads: 0,
            prefetches: 0,
            cacheReads: 0
        };
        var p = [];
        var x = [];
        var y = [];
        var i9 = 0;
        var j9 = false;
        var k9 = z(a.cacheSize, 1048576);
        var l9 = 0;
        var m9 = 0;
        var n9 = 0;
        var o9 = k9 === 0;
        var p9 = z(a.pageSize, 50);
        var q9 = z(a.prefetchSize, p9);
        var r9 = "1.0";
        var s9;
        var t9 = 0;
        var u9 = a.source;
        if (typeof u9 === "string") {
            u9 = new J8(a)
        }
        u9.options = a;
        var v9 = d.createStore(a.name, a.mechanism);
        var w9 = this;
        w9.onidle = a.idle;
        w9.stats = e;
        w9.count = function() {
            if (s9) {
                throw s9
            }
            var i = F1();
            var _ = false;
            if (j9) {
                j(function() {
                    i.resolve(l9)
                });
                return i.promise()
            }
            var Q9 = u9.count(function(R9) {
                Q9 = null;
                e.counts++;
                i.resolve(R9)
            }, function(R9) {
                Q9 = null;
                i.reject(k(R9, {
                    canceled: _
                }))
            });
            return k(i.promise(), {
                cancel: function() {
                    if (Q9) {
                        _ = true;
                        Q9.abort();
                        Q9 = null
                    }
                }
            })
        };
        w9.clear = function() {
            if (s9) {
                throw s9
            }
            if (p.length === 0) {
                var i = F1();
                var _ = new g9(M9, i, false);
                F9(_, p);
                return i.promise()
            }
            return p[0].p
        };
        w9.filterForward = function(i, _, Q9) {
            return C9(i, _, Q9, false)
        };
        w9.filterBack = function(i, _, Q9) {
            return C9(i, _, Q9, true)
        };
        w9.readRange = function(i, _) {
            M8(i, "index");
            M8(_, "count");
            if (s9) {
                throw s9
            }
            var Q9 = F1();
            var op = new g9(O9, Q9, true, i, _, [], 0);
            F9(op, x);
            return k(Q9.promise(), {
                cancel: function() {
                    op.cancel()
                }
            })
        };
        w9.ToObservable = w9.toObservable = function() {
            if (!w.Rx || !w.Rx.Observable) {
                throw {
                    message: "Rx library not available - include rx.js"
                }
            }
            if (s9) {
                throw s9
            }
            return w.Rx.Observable.CreateWithDisposable(function(_) {
                var Q9 = false;
                var R9 = 0;
                var S9 = function(i) {
                    if (!Q9) {
                        _.OnError(i)
                    }
                };
                var T9 = function(U9) {
                    if (!Q9) {
                        var i, V9;
                        for (i = 0, V9 = U9.length; i < V9; i++) {
                            _.OnNext(U9[i])
                        }
                        if (U9.length < p9) {
                            _.OnCompleted()
                        } else {
                            R9 += p9;
                            w9.readRange(R9, p9).then(T9, S9)
                        }
                    }
                };
                w9.readRange(R9, p9).then(T9, S9);
                return {
                    Dispose: function() {
                        Q9 = true
                    }
                }
            })
        };
        var x9 = function(_) {
            return function(Q9) {
                s9 = {
                    message: _,
                    error: Q9
                };
                var i, R9;
                for (i = 0, R9 = x.length; i < R9; i++) {
                    x[i].fireRejected(s9)
                }
                for (i = 0, R9 = p.length; i < R9; i++) {
                    p[i].fireRejected(s9)
                }
                x = p = null
            }
        };
        var y9 = function(_) {
            if (_ !== b) {
                b = _;
                var Q9 = p.concat(x, y);
                var i, R9;
                for (i = 0, R9 = Q9.length; i < R9; i++) {
                    Q9[i].run(b)
                }
            }
        };
        var z9 = function() {
            var i = new E1();
            v9.clear(function() {
                i9 = 0;
                j9 = false;
                l9 = 0;
                m9 = 0;
                n9 = 0;
                o9 = k9 === 0;
                e = {
                    counts: 0,
                    netReads: 0,
                    prefetches: 0,
                    cacheReads: 0
                };
                w9.stats = e;
                v9.close();
                i.resolve()
            }, function(_) {
                i.reject(_)
            });
            return i
        };
        var A9 = function(i) {
            var _ = P8(p, i);
            if (!_) {
                _ = P8(x, i);
                if (!_) {
                    P8(y, i)
                }
            }
            t9--;
            y9(T8)
        };
        var B9 = function(i) {

            /// <returns type="DjsDeferred">A promise for a page object with (i)ndex, (c)ount, (d)ata.</returns>

            var _ = new E1();
            var Q9 = false;
            var R9 = u9.read(i, p9, function(S9) {
                var T9 = {
                    i: i,
                    c: S9.length,
                    d: S9
                };
                _.resolve(T9)
            }, function(S9) {
                _.reject(S9)
            });
            return k(_, {
                cancel: function() {
                    if (R9) {
                        R9.abort();
                        Q9 = true;
                        R9 = null
                    }
                }
            })
        };
        var C9 = function(_, Q9, R9, S9) {
            _ = r(_);
            Q9 = r(Q9);
            if (isNaN(_)) {
                throw {
                    message: "'index' must be a valid number.",
                    index: _
                }
            }
            if (isNaN(Q9)) {
                throw {
                    message: "'count' must be a valid number.",
                    count: Q9
                }
            }
            if (s9) {
                throw s9
            }
            _ = Math.max(_, 0);
            var T9 = F1();
            var U9 = [];
            var V9 = false;
            var W9 = null;
            var X9 = function(_9, aa) {
                if (!V9) {
                    if (Q9 >= 0 && U9.length >= Q9) {
                        T9.resolve(U9)
                    } else {
                        W9 = w9.readRange(_9, aa).then(function(ba) {
                            for (var i = 0, ca = ba.length; i < ca && (Q9 < 0 || U9.length < Q9); i++) {
                                var da = S9 ? ca - i - 1 : i;
                                var ea = ba[da];
                                if (R9(ea)) {
                                    var fa = {
                                        index: _9 + da,
                                        item: ea
                                    };
                                    S9 ? U9.unshift(fa) : U9.push(fa)
                                }
                            }
                            if ((!S9 && ba.length < aa) || (S9 && _9 <= 0)) {
                                T9.resolve(U9)
                            } else {
                                var ga = S9 ? Math.max(_9 - p9, 0) : _9 + aa;
                                X9(ga, p9)
                            }
                        }, function(i) {
                            T9.reject(i)
                        })
                    }
                }
            };
            var Y9 = R8(_, _, p9);
            var Z9 = S9 ? Y9.i : _;
            var $9 = S9 ? _ - Y9.i + 1 : Y9.i + Y9.c - _;
            X9(Z9, $9);
            return k(T9.promise(), {
                cancel: function() {
                    if (W9) {
                        W9.cancel()
                    }
                    V9 = true
                }
            })
        };
        var D9 = function() {
            if (w9.onidle && t9 === 0) {
                w9.onidle()
            }
        };
        var E9 = function(i) {
            if (j9 || q9 === 0 || o9) {
                return
            }
            if (y.length === 0 || (y[0] && y[0].c !== -1)) {
                var _ = new g9(N9, null, true, i, q9, null, q9);
                F9(_, y)
            }
        };
        var F9 = function(i, _) {
            i.oncomplete = A9;
            _.push(i);
            t9++;
            i.run(b)
        };
        var G9 = function(i) {

            /// <returns type="DjsDeferred">A promise for a found flag and page object with (i)ndex, (c)ount, (d)ata, and (t)icks.</returns>

            var Q9 = false;
            var R9 = k(new E1(), {
                cancel: function() {
                    Q9 = true
                }
            });
            var S9 = J9(R9, "Read page from store failure");
            v9.contains(i, function(T9) {
                if (Q9) {
                    return
                }
                if (T9) {
                    v9.read(i, function(_, U9) {
                        if (!Q9) {
                            R9.resolve(U9 !== u, U9)
                        }
                    }, S9);
                    return
                }
                R9.resolve(false)
            }, S9);
            return R9
        };
        var H9 = function(i, _) {

            /// <param name="page" type="Object">Object with (i)ndex, (c)ount, (d)ata, and (t)icks.</param>

            var Q9 = false;
            var R9 = k(new E1(), {
                cancel: function() {
                    Q9 = true
                }
            });
            var S9 = J9(R9, "Save page to store failure");
            var T9 = function() {
                R9.resolve(true)
            };
            if (_.c > 0) {
                var U9 = Q8(_);
                o9 = k9 >= 0 && k9 < i9 + U9;
                if (!o9) {
                    v9.addOrUpdate(i, _, function() {
                        K9(_, U9);
                        I9(T9, S9)
                    }, S9)
                } else {
                    T9()
                }
            } else {
                K9(_, 0);
                I9(T9, S9)
            }
            return R9
        };
        var I9 = function(i, _) {
            var Q9 = {
                actualCacheSize: i9,
                allDataLocal: j9,
                cacheSize: k9,
                collectionCount: l9,
                highestSavedPage: m9,
                highestSavedPageSize: n9,
                pageSize: p9,
                sourceId: u9.identifier,
                version: r9
            };
            v9.addOrUpdate("__settings", Q9, i, _)
        };
        var J9 = function(i) {
            return function() {
                i.resolve(false)
            }
        };
        var K9 = function(i, _) {

            /// <param name="page" type="Object">Object with (i)ndex, (c)ount, (d)ata.</param>

            var Q9 = i.c;
            var R9 = i.i;
            if (Q9 === 0) {
                if (m9 === R9 - p9) {
                    l9 = m9 + n9
                }
            } else {
                m9 = Math.max(m9, R9);
                if (m9 === R9) {
                    n9 = Q9
                }
                i9 += _;
                if (Q9 < p9 && !l9) {
                    l9 = R9 + Q9
                }
            }
            if (!j9 && l9 === m9 + n9) {
                j9 = true
            }
        };
        var L9 = function(i, _, Q9, R9) {
            var S9 = i.canceled && _ !== Z8;
            if (S9) {
                if (_ === Y8) {
                    if (R9 && R9.cancel) {
                        R9.cancel()
                    }
                }
            }
            return S9
        };
        var M9 = function(i, _, Q9) {
            var R9 = i.transition;
            if (Q9 !== S8) {
                y9(S8);
                return true
            }
            switch (_) {
                case _8:
                    R9(b9);
                    break;
                case Z8:
                    D9();
                    break;
                case b9:
                    z9().then(function() {
                        i.complete()
                    });
                    i.wait();
                    break;
                default:
                    return false
            }
            return true
        };
        var N9 = function(i, _, Q9, R9) {
            if (!L9(i, _, Q9, R9)) {
                var S9 = i.transition;
                if (Q9 !== W8) {
                    if (Q9 === S8) {
                        if (_ !== Y8) {
                            i.cancel()
                        }
                    } else if (Q9 === T8) {
                        y9(W8)
                    }
                    return true
                }
                switch (_) {
                    case _8:
                        if (y[0] === i) {
                            S9(d9, i.i)
                        }
                        break;
                    case c9:
                        var T9 = i.pending;
                        if (T9 > 0) {
                            T9 -= Math.min(T9, R9.c)
                        }
                        if (j9 || T9 === 0 || R9.c < p9 || o9) {
                            i.complete()
                        } else {
                            i.pending = T9;
                            S9(d9, R9.i + p9)
                        }
                        break;
                    default:
                        return P9(i, _, Q9, R9, true)
                }
            }
            return true
        };
        var O9 = function(i, _, Q9, R9) {
            if (!L9(i, _, Q9, R9)) {
                var S9 = i.transition;
                if (Q9 !== V8 && _ !== _8) {
                    if (Q9 === S8) {
                        if (_ !== _8) {
                            i.cancel()
                        }
                    } else if (Q9 !== X8) {
                        y9(V8)
                    }
                    return true
                }
                switch (_) {
                    case _8:
                        if (Q9 === T8 || Q9 === W8) {
                            y9(V8);
                            if (i.c > 0) {
                                var T9 = R8(i.i, i.c, p9);
                                S9(d9, T9.i)
                            } else {
                                S9(c9, i)
                            }
                        }
                        break;
                    case c9:
                        K8(i, R9);
                        var U9 = i.d.length;
                        if (i.c === U9 || R9.c < p9) {
                            e.cacheReads++;
                            E9(R9.i + R9.c);
                            i.complete()
                        } else {
                            S9(d9, R9.i + p9)
                        }
                        break;
                    default:
                        return P9(i, _, Q9, R9, false)
                }
            }
            return true
        };
        var P9 = function(i, _, Q9, R9, S9) {
            var T9 = i.error;
            var U9 = i.transition;
            var V9 = i.wait;
            var W9;
            switch (_) {
                case Z8:
                    D9();
                    break;
                case d9:
                    W9 = G9(R9).then(function(X9, Y9) {
                        if (!i.canceled) {
                            if (X9) {
                                U9(c9, Y9)
                            } else {
                                U9(f9, R9)
                            }
                        }
                    });
                    break;
                case f9:
                    W9 = B9(R9).then(function(X9) {
                        if (!i.canceled) {
                            if (S9) {
                                e.prefetches++
                            } else {
                                e.netReads++
                            }
                            U9(e9, X9)
                        }
                    }, T9);
                    break;
                case e9:
                    if (Q9 !== X8) {
                        y9(X8);
                        W9 = H9(R9.i, R9).then(function(X9) {
                            if (!i.canceled) {
                                if (!X9 && S9) {
                                    i.pending = 0
                                }
                                U9(c9, R9)
                            }
                            y9(T8)
                        })
                    }
                    break;
                default:
                    return false
            }
            if (W9) {
                if (i.canceled) {
                    W9.cancel()
                } else if (i.s === _) {
                    V9(W9)
                }
            }
            return true
        };
        v9.read("__settings", function(_, i) {
            if (f(i)) {
                var Q9 = i.version;
                if (!Q9 || Q9.indexOf("1.") !== 0) {
                    x9("Unsupported cache store version " + Q9)();
                    return
                }
                if (p9 !== i.pageSize || u9.identifier !== i.sourceId) {
                    z9().then(function() {
                        y9(T8)
                    }, x9("Unable to clear store during initialization"))
                } else {
                    i9 = i.actualCacheSize;
                    j9 = i.allDataLocal;
                    k9 = i.cacheSize;
                    l9 = i.collectionCount;
                    m9 = i.highestSavedPage;
                    n9 = i.highestSavedPageSize;
                    r9 = Q9;
                    y9(T8)
                }
            } else {
                I9(function() {
                    y9(T8)
                }, x9("Unable to write settings during initialization."))
            }
        }, x9("Unable to read settings from store."));
        return w9
    };
    d.createDataCache = function(a) {
        N8(a.pageSize, "pageSize");
        O8(a.cacheSize, "cacheSize");
        O8(a.prefetchSize, "prefetchSize");
        if (!f(a.name)) {
            throw {
                message: "Undefined or null name",
                options: a
            }
        }
        if (!f(a.source)) {
            throw {
                message: "Undefined source",
                options: a
            }
        }
        return new h9(a)
    }
})(window);