﻿/*!
 * URI.js - Mutating URLs
 *
 * Version: 1.11.2
 *
 * Author: Rodney Rehm
 * Web: http://medialize.github.com/URI.js/
 *
 * Licensed under
 *   MIT License http://www.opensource.org/licenses/mit-license
 *   GPL v3 http://opensource.org/licenses/GPL-3.0
 *
 */

(function(r, f) {
    if (typeof exports === 'object') {
        module.exports = f(require('./punycode'), require('./IPv6'), require('./SecondLevelDomains'))
    } else if (typeof define === 'function' && define.amd) {
        r.URI = f(r.punycode, r.IPv6, r.SecondLevelDomains, r);
        define([], function() {
            return r.URI
        })
    } else {
        r.URI = f(r.punycode, r.IPv6, r.SecondLevelDomains, r)
    }
}(this, function(a, I, S, r) {
    "use strict";
    var _ = r && r.URI;

    function U(c, d) {
        if (!(this instanceof U)) {
            return new U(c, d)
        }
        if (c === undefined) {
            if (typeof location !== 'undefined') {
                c = location.href + ""
            } else {
                c = ""
            }
        }
        this.href(c);
        if (d !== undefined) {
            return this.absoluteTo(d)
        }
        return this
    };
    var p = U.prototype;
    var h = Object.prototype.hasOwnProperty;

    function b(s) {
        return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1')
    }
    function g(v) {
        if (v === undefined) {
            return 'Undefined'
        }
        return String(Object.prototype.toString.call(v)).slice(8, -1)
    }
    function f(c) {
        return g(c) === "Array"
    }
    function j(d, v) {
        var l = {};
        var i, c;
        if (f(v)) {
            for (i = 0, c = v.length; i < c; i++) {
                l[v[i]] = true
            }
        } else {
            l[v] = true
        }
        for (i = 0, c = d.length; i < c; i++) {
            if (l[d[i]] !== undefined) {
                d.splice(i, 1);
                c--;
                i--
            }
        }
        return d
    }
    function k(l, v) {
        var i, c;
        if (f(v)) {
            for (i = 0, c = v.length; i < c; i++) {
                if (!k(l, v[i])) {
                    return false
                }
            }
            return true
        }
        var d = g(v);
        for (i = 0, c = l.length; i < c; i++) {
            if (d === 'RegExp') {
                if (typeof l[i] === 'string' && l[i].match(v)) {
                    return true
                }
            } else if (l[i] === v) {
                return true
            }
        }
        return false
    }
    function m(c, t) {
        if (!f(c) || !f(t)) {
            return false
        }
        if (c.length !== t.length) {
            return false
        }
        c.sort();
        t.sort();
        for (var i = 0, l = c.length; i < l; i++) {
            if (c[i] !== t[i]) {
                return false
            }
        }
        return true
    }
    U._parts = function() {
        return {
            protocol: null,
            username: null,
            password: null,
            hostname: null,
            urn: null,
            port: null,
            path: null,
            query: null,
            fragment: null,
            duplicateQueryParameters: U.duplicateQueryParameters,
            escapeQuerySpace: U.escapeQuerySpace
        }
    };
    U.duplicateQueryParameters = false;
    U.escapeQuerySpace = true;
    U.protocol_expression = /^[a-z][a-z0-9-+-]*$/i;
    U.idn_expression = /[^a-z0-9\.-]/i;
    U.punycode_expression = /(xn--)/i;
    U.ip4_expression = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;
    U.ip6_expression = /^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/;
    U.find_uri_expression = /\b((?:[a-z][\w-]+:(?:\/{1,3}|[a-z0-9%])|www\d{0,3}[.]|[a-z0-9.\-]+[.][a-z]{2,4}\/)(?:[^\s()<>]+|\(([^\s()<>]+|(\([^\s()<>]+\)))*\))+(?:\(([^\s()<>]+|(\([^\s()<>]+\)))*\)|[^\s`!()\[\]{};:'".,<>?«»“”‘’]))/ig;
    U.defaultPorts = {
        http: "80",
        https: "443",
        ftp: "21",
        gopher: "70",
        ws: "80",
        wss: "443"
    };
    U.invalid_hostname_characters = /[^a-zA-Z0-9\.-]/;
    U.domAttributes = {
        'a': 'href',
        'blockquote': 'cite',
        'link': 'href',
        'base': 'href',
        'script': 'src',
        'form': 'action',
        'img': 'src',
        'area': 'href',
        'iframe': 'src',
        'embed': 'src',
        'source': 'src',
        'track': 'src',
        'input': 'src'
    };
    U.getDomAttribute = function(c) {
        if (!c || !c.nodeName) {
            return undefined
        }
        var d = c.nodeName.toLowerCase();
        if (d === 'input' && c.type !== 'image') {
            return undefined
        }
        return U.domAttributes[d]
    };

    function n(v) {
        return escape(v)
    }
    function o(s) {
        return encodeURIComponent(s).replace(/[!'()*]/g, n).replace(/\*/g, "%2A")
    }
    U.encode = o;
    U.decode = decodeURIComponent;
    U.iso8859 = function() {
        U.encode = escape;
        U.decode = unescape
    };
    U.unicode = function() {
        U.encode = o;
        U.decode = decodeURIComponent
    };
    U.characters = {
        pathname: {
            encode: {
                expression: /%(24|26|2B|2C|3B|3D|3A|40)/ig,
                map: {
                    "%24": "$",
                    "%26": "&",
                    "%2B": "+",
                    "%2C": ",",
                    "%3B": ";",
                    "%3D": "=",
                    "%3A": ":",
                    "%40": "@"
                }
            },
            decode: {
                expression: /[\/\?#]/g,
                map: {
                    "/": "%2F",
                    "?": "%3F",
                    "#": "%23"
                }
            }
        },
        reserved: {
            encode: {
                expression: /%(21|23|24|26|27|28|29|2A|2B|2C|2F|3A|3B|3D|3F|40|5B|5D)/ig,
                map: {
                    "%3A": ":",
                    "%2F": "/",
                    "%3F": "?",
                    "%23": "#",
                    "%5B": "[",
                    "%5D": "]",
                    "%40": "@",
                    "%21": "!",
                    "%24": "$",
                    "%26": "&",
                    "%27": "'",
                    "%28": "(",
                    "%29": ")",
                    "%2A": "*",
                    "%2B": "+",
                    "%2C": ",",
                    "%3B": ";",
                    "%3D": "="
                }
            }
        }
    };
    U.encodeQuery = function(s, e) {
        var c = U.encode(s + "");
        return e ? c.replace(/%20/g, '+') : c
    };
    U.decodeQuery = function(s, c) {
        s += "";
        try {
            return U.decode(c ? s.replace(/\+/g, '%20') : s)
        } catch (e) {
            return s
        }
    };
    U.recodePath = function(s) {
        var c = (s + "").split('/');
        for (var i = 0, l = c.length; i < l; i++) {
            c[i] = U.encodePathSegment(U.decode(c[i]))
        }
        return c.join('/')
    };
    U.decodePath = function(s) {
        var c = (s + "").split('/');
        for (var i = 0, l = c.length; i < l; i++) {
            c[i] = U.decodePathSegment(c[i])
        }
        return c.join('/')
    };
    var u = {
        'encode': 'encode',
        'decode': 'decode'
    };
    var w;
    var y = function(d, w) {
        return function(s) {
            return U[w](s + "").replace(U.characters[d][w].expression, function(c) {
                return U.characters[d][w].map[c]
            })
        }
    };
    for (w in u) {
        U[w + "PathSegment"] = y("pathname", u[w])
    }
    U.encodeReserved = y("reserved", "encode");
    U.parse = function(s, c) {
        var d;
        if (!c) {
            c = {}
        }
        d = s.indexOf('#');
        if (d > -1) {
            c.fragment = s.substring(d + 1) || null;
            s = s.substring(0, d)
        }
        d = s.indexOf('?');
        if (d > -1) {
            c.query = s.substring(d + 1) || null;
            s = s.substring(0, d)
        }
        if (s.substring(0, 2) === '//') {
            c.protocol = null;
            s = s.substring(2);
            s = U.parseAuthority(s, c)
        } else {
            d = s.indexOf(':');
            if (d > -1) {
                c.protocol = s.substring(0, d) || null;
                if (c.protocol && !c.protocol.match(U.protocol_expression)) {
                    c.protocol = undefined
                } else if (c.protocol === 'file') {
                    s = s.substring(d + 3)
                } else if (s.substring(d + 1, d + 3) === '//') {
                    s = s.substring(d + 3);
                    s = U.parseAuthority(s, c)
                } else {
                    s = s.substring(d + 1);
                    c.urn = true
                }
            }
        }
        c.path = s;
        return c
    };
    U.parseHost = function(s, c) {
        var d = s.indexOf('/');
        var e;
        var t;
        if (d === -1) {
            d = s.length
        }
        if (s.charAt(0) === "[") {
            e = s.indexOf(']');
            c.hostname = s.substring(1, e) || null;
            c.port = s.substring(e + 2, d) || null
        } else if (s.indexOf(':') !== s.lastIndexOf(':')) {
            c.hostname = s.substring(0, d) || null;
            c.port = null
        } else {
            t = s.substring(0, d).split(':');
            c.hostname = t[0] || null;
            c.port = t[1] || null
        }
        if (c.hostname && s.substring(d).charAt(0) !== '/') {
            d++;
            s = "/" + s
        }
        return s.substring(d) || '/'
    };
    U.parseAuthority = function(s, c) {
        s = U.parseUserinfo(s, c);
        return U.parseHost(s, c)
    };
    U.parseUserinfo = function(s, c) {
        var d = s.indexOf('/');
        var e = d > -1 ? s.lastIndexOf('@', d) : s.indexOf('@');
        var t;
        if (e > -1 && (d === -1 || e < d)) {
            t = s.substring(0, e).split(':');
            c.username = t[0] ? U.decode(t[0]) : null;
            t.shift();
            c.password = t[0] ? U.decode(t.join(':')) : null;
            s = s.substring(e + 1)
        } else {
            c.username = null;
            c.password = null
        }
        return s
    };
    U.parseQuery = function(s, e) {
        if (!s) {
            return {}
        }
        s = s.replace(/&+/g, '&').replace(/^\?*&*|&+$/g, '');
        if (!s) {
            return {}
        }
        var c = {};
        var d = s.split('&');
        var l = d.length;
        var v, t, x;
        for (var i = 0; i < l; i++) {
            v = d[i].split('=');
            t = U.decodeQuery(v.shift(), e);
            x = v.length ? U.decodeQuery(v.join('='), e) : null;
            if (c[t]) {
                if (typeof c[t] === "string") {
                    c[t] = [c[t]]
                }
                c[t].push(x)
            } else {
                c[t] = x
            }
        }
        return c
    };
    U.build = function(c) {
        var t = "";
        if (c.protocol) {
            t += c.protocol + ":"
        }
        if (!c.urn && (t || c.hostname)) {
            t += '//'
        }
        t += (U.buildAuthority(c) || '');
        if (typeof c.path === "string") {
            if (c.path.charAt(0) !== '/' && typeof c.hostname === "string") {
                t += '/'
            }
            t += c.path
        }
        if (typeof c.query === "string" && c.query) {
            t += '?' + c.query
        }
        if (typeof c.fragment === "string" && c.fragment) {
            t += '#' + c.fragment
        }
        return t
    };
    U.buildHost = function(c) {
        var t = "";
        if (!c.hostname) {
            return ""
        } else if (U.ip6_expression.test(c.hostname)) {
            if (c.port) {
                t += "[" + c.hostname + "]:" + c.port
            } else {
                t += c.hostname
            }
        } else {
            t += c.hostname;
            if (c.port) {
                t += ':' + c.port
            }
        }
        return t
    };
    U.buildAuthority = function(c) {
        return U.buildUserinfo(c) + U.buildHost(c)
    };
    U.buildUserinfo = function(c) {
        var t = "";
        if (c.username) {
            t += U.encode(c.username);
            if (c.password) {
                t += ':' + U.encode(c.password)
            }
            t += "@"
        }
        return t
    };
    U.buildQuery = function(d, c, e) {
        var t = "";
        var l, s, i, v;
        for (s in d) {
            if (h.call(d, s) && s) {
                if (f(d[s])) {
                    l = {};
                    for (i = 0, v = d[s].length; i < v; i++) {
                        if (d[s][i] !== undefined && l[d[s][i] + ""] === undefined) {
                            t += "&" + U.buildQueryParameter(s, d[s][i], e);
                            if (c !== true) {
                                l[d[s][i] + ""] = true
                            }
                        }
                    }
                } else if (d[s] !== undefined) {
                    t += '&' + U.buildQueryParameter(s, d[s], e)
                }
            }
        }
        return t.substring(1)
    };
    U.buildQueryParameter = function(c, v, e) {
        return U.encodeQuery(c, e) + (v !== null ? "=" + U.encodeQuery(v, e) : "")
    };
    U.addQuery = function(d, c, v) {
        if (typeof c === "object") {
            for (var e in c) {
                if (h.call(c, e)) {
                    U.addQuery(d, e, c[e])
                }
            }
        } else if (typeof c === "string") {
            if (d[c] === undefined) {
                d[c] = v;
                return
            } else if (typeof d[c] === "string") {
                d[c] = [d[c]]
            }
            if (!f(v)) {
                v = [v]
            }
            d[c] = d[c].concat(v)
        } else {
            throw new TypeError("URI.addQuery() accepts an object, string as the name parameter")
        }
    };
    U.removeQuery = function(d, c, v) {
        var i, l, e;
        if (f(c)) {
            for (i = 0, l = c.length; i < l; i++) {
                d[c[i]] = undefined
            }
        } else if (typeof c === "object") {
            for (e in c) {
                if (h.call(c, e)) {
                    U.removeQuery(d, e, c[e])
                }
            }
        } else if (typeof c === "string") {
            if (v !== undefined) {
                if (d[c] === v) {
                    d[c] = undefined
                } else if (f(d[c])) {
                    d[c] = j(d[c], v)
                }
            } else {
                d[c] = undefined
            }
        } else {
            throw new TypeError("URI.addQuery() accepts an object, string as the first parameter")
        }
    };
    U.hasQuery = function(d, c, v, e) {
        if (typeof c === "object") {
            for (var i in c) {
                if (h.call(c, i)) {
                    if (!U.hasQuery(d, i, c[i])) {
                        return false
                    }
                }
            }
            return true
        } else if (typeof c !== "string") {
            throw new TypeError("URI.hasQuery() accepts an object, string as the name parameter")
        }
        switch (g(v)) {
            case 'Undefined':
                return c in d;
            case 'Boolean':
                var l = Boolean(f(d[c]) ? d[c].length : d[c]);
                return v === l;
            case 'Function':
                return !!v(d[c], c, d);
            case 'Array':
                if (!f(d[c])) {
                    return false
                }
                var s = e ? k:
                    m;
                    return s(d[c], v);
                case 'RegExp':
                    if (!f(d[c])) {
                        return Boolean(d[c] && d[c].match(v))
                    }
                    if (!e) {
                        return false
                    }
                    return k(d[c], v);
                case 'Number':
                    v = String(v);
                case 'String':
                    if (!f(d[c])) {
                        return d[c] === v
                    }
                    if (!e) {
                        return false
                    }
                    return k(d[c], v);
                default:
                    throw new TypeError("URI.hasQuery() accepts undefined, boolean, string, number, RegExp, Function as the value parameter")
        }
    };
    U.commonPath = function(c, t) {
        var l = Math.min(c.length, t.length);
        var d;
        for (d = 0; d < l; d++) {
            if (c.charAt(d) !== t.charAt(d)) {
                d--;
                break
            }
        }
        if (d < 1) {
            return c.charAt(0) === t.charAt(0) && c.charAt(0) === '/' ? '/' : ''
        }
        if (c.charAt(d) !== '/' || t.charAt(d) !== '/') {
            d = c.substring(0, d).lastIndexOf('/')
        }
        return c.substring(0, d + 1)
    };
    U.withinString = function(s, c) {
        return s.replace(U.find_uri_expression, c)
    };
    U.ensureValidHostname = function(v) {
        if (v.match(U.invalid_hostname_characters)) {
            if (!a) {
                throw new TypeError("Hostname '" + v + "' contains characters other than [A-Z0-9.-] and Punycode.js is not available")
            }
            if (a.toASCII(v).match(U.invalid_hostname_characters)) {
                throw new TypeError("Hostname '" + v + "' contains characters other than [A-Z0-9.-]")
            }
        }
    };
    U.noConflict = function(c) {
        if (c) {
            var d = {
                URI: this.noConflict()
            };
            if (URITemplate && typeof URITemplate.noConflict == "function") {
                d.URITemplate = URITemplate.noConflict()
            }
            if (I && typeof I.noConflict == "function") {
                d.IPv6 = I.noConflict()
            }
            if (SecondLevelDomains && typeof SecondLevelDomains.noConflict == "function") {
                d.SecondLevelDomains = SecondLevelDomains.noConflict()
            }
            return d
        } else if (r.URI === this) {
            r.URI = _
        }
        return this
    };
    p.build = function(d) {
        if (d === true) {
            this._deferred_build = true
        } else if (d === undefined || this._deferred_build) {
            this._string = U.build(this._parts);
            this._deferred_build = false
        }
        return this
    };
    p.clone = function() {
        return new U(this)
    };
    p.valueOf = p.toString = function() {
        return this.build(false)._string
    };
    u = {
        protocol: 'protocol',
        username: 'username',
        password: 'password',
        hostname: 'hostname',
        port: 'port'
    };
    y = function(w) {
        return function(v, c) {
            if (v === undefined) {
                return this._parts[w] || ""
            } else {
                this._parts[w] = v || null;
                this.build(!c);
                return this
            }
        }
    };
    for (w in u) {
        p[w] = y(u[w])
    }
    u = {
        query: '?',
        fragment: '#'
    };
    y = function(w, c) {
        return function(v, d) {
            if (v === undefined) {
                return this._parts[w] || ""
            } else {
                if (v !== null) {
                    v = v + "";
                    if (v.charAt(0) === c) {
                        v = v.substring(1)
                    }
                }
                this._parts[w] = v;
                this.build(!d);
                return this
            }
        }
    };
    for (w in u) {
        p[w] = y(w, u[w])
    }
    u = {
        search: ['?', 'query'],
        hash: ['#', 'fragment']
    };
    y = function(w, c) {
        return function(v, d) {
            var t = this[w](v, d);
            return typeof t === "string" && t.length ? (c + t) : t
        }
    };
    for (w in u) {
        p[w] = y(u[w][1], u[w][0])
    }
    p.pathname = function(v, c) {
        if (v === undefined || v === true) {
            var d = this._parts.path || (this._parts.hostname ? '/' : '');
            return v ? U.decodePath(d) : d
        } else {
            this._parts.path = v ? U.recodePath(v) : "/";
            this.build(!c);
            return this
        }
    };
    p.path = p.pathname;
    p.href = function(c, d) {
        var e;
        if (c === undefined) {
            return this.toString()
        }
        this._string = "";
        this._parts = U._parts();
        var _ = c instanceof U;
        var i = typeof c === "object" && (c.hostname || c.path || c.pathname);
        if (c.nodeName) {
            var l = U.getDomAttribute(c);
            c = c[l] || "";
            i = false
        }
        if (!_ && i && c.pathname !== undefined) {
            c = c.toString()
        }
        if (typeof c === "string") {
            this._parts = U.parse(c, this._parts)
        } else if (_ || i) {
            var s = _ ? c._parts : c;
            for (e in s) {
                if (h.call(this._parts, e)) {
                    this._parts[e] = s[e]
                }
            }
        } else {
            throw new TypeError("invalid input")
        }
        this.build(!d);
        return this
    };
    p.is = function(c) {
        var i = false;
        var d = false;
        var e = false;
        var l = false;
        var s = false;
        var t = false;
        var a = false;
        var v = !this._parts.urn;
        if (this._parts.hostname) {
            v = false;
            d = U.ip4_expression.test(this._parts.hostname);
            e = U.ip6_expression.test(this._parts.hostname);
            i = d || e;
            l = !i;
            s = l && S && S.has(this._parts.hostname);
            t = l && U.idn_expression.test(this._parts.hostname);
            a = l && U.punycode_expression.test(this._parts.hostname)
        }
        switch (c.toLowerCase()) {
            case 'relative':
                return v;
            case 'absolute':
                return !v;
            case 'domain':
            case 'name':
                return l;
            case 'sld':
                return s;
            case 'ip':
                return i;
            case 'ip4':
            case 'ipv4':
            case 'inet4':
                return d;
            case 'ip6':
            case 'ipv6':
            case 'inet6':
                return e;
            case 'idn':
                return t;
            case 'url':
                return !this._parts.urn;
            case 'urn':
                return !!this._parts.urn;
            case 'punycode':
                return a
        }
        return null
    };
    var z = p.protocol;
    var A = p.port;
    var B = p.hostname;
    p.protocol = function(v, c) {
        if (v !== undefined) {
            if (v) {
                v = v.replace(/:(\/\/)?$/, '');
                if (v.match(/[^a-zA-z0-9\.+-]/)) {
                    throw new TypeError("Protocol '" + v + "' contains characters other than [A-Z0-9.+-]")
                }
            }
        }
        return z.call(this, v, c)
    };
    p.scheme = p.protocol;
    p.port = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v !== undefined) {
            if (v === 0) {
                v = null
            }
            if (v) {
                v += "";
                if (v.charAt(0) === ":") {
                    v = v.substring(1)
                }
                if (v.match(/[^0-9]/)) {
                    throw new TypeError("Port '" + v + "' contains characters other than [0-9]")
                }
            }
        }
        return A.call(this, v, c)
    };
    p.hostname = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v !== undefined) {
            var x = {};
            U.parseHost(v, x);
            v = x.hostname
        }
        return B.call(this, v, c)
    };
    p.host = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined) {
            return this._parts.hostname ? U.buildHost(this._parts) : ""
        } else {
            U.parseHost(v, this._parts);
            this.build(!c);
            return this
        }
    };
    p.authority = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined) {
            return this._parts.hostname ? U.buildAuthority(this._parts) : ""
        } else {
            U.parseAuthority(v, this._parts);
            this.build(!c);
            return this
        }
    };
    p.userinfo = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined) {
            if (!this._parts.username) {
                return ""
            }
            var t = U.buildUserinfo(this._parts);
            return t.substring(0, t.length - 1)
        } else {
            if (v[v.length - 1] !== '@') {
                v += '@'
            }
            U.parseUserinfo(v, this._parts);
            this.build(!c);
            return this
        }
    };
    p.resource = function(v, c) {
        var d;
        if (v === undefined) {
            return this.path() + this.search() + this.hash()
        }
        d = U.parse(v);
        this._parts.path = d.path;
        this._parts.query = d.query;
        this._parts.fragment = d.fragment;
        this.build(!c);
        return this
    };
    p.subdomain = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined) {
            if (!this._parts.hostname || this.is('IP')) {
                return ""
            }
            var d = this._parts.hostname.length - this.domain().length - 1;
            return this._parts.hostname.substring(0, d) || ""
        } else {
            var e = this._parts.hostname.length - this.domain().length;
            var s = this._parts.hostname.substring(0, e);
            var i = new RegExp('^' + b(s));
            if (v && v.charAt(v.length - 1) !== '.') {
                v += "."
            }
            if (v) {
                U.ensureValidHostname(v)
            }
            this._parts.hostname = this._parts.hostname.replace(i, v);
            this.build(!c);
            return this
        }
    };
    p.domain = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (typeof v === 'boolean') {
            c = v;
            v = undefined
        }
        if (v === undefined) {
            if (!this._parts.hostname || this.is('IP')) {
                return ""
            }
            var t = this._parts.hostname.match(/\./g);
            if (t && t.length < 2) {
                return this._parts.hostname
            }
            var e = this._parts.hostname.length - this.tld(c).length - 1;
            e = this._parts.hostname.lastIndexOf('.', e - 1) + 1;
            return this._parts.hostname.substring(e) || ""
        } else {
            if (!v) {
                throw new TypeError("cannot set domain empty")
            }
            U.ensureValidHostname(v);
            if (!this._parts.hostname || this.is('IP')) {
                this._parts.hostname = v
            } else {
                var d = new RegExp(b(this.domain()) + "$");
                this._parts.hostname = this._parts.hostname.replace(d, v)
            }
            this.build(!c);
            return this
        }
    };
    p.tld = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (typeof v === 'boolean') {
            c = v;
            v = undefined
        }
        if (v === undefined) {
            if (!this._parts.hostname || this.is('IP')) {
                return ""
            }
            var d = this._parts.hostname.lastIndexOf('.');
            var t = this._parts.hostname.substring(d + 1);
            if (c !== true && S && S.list[t.toLowerCase()]) {
                return S.get(this._parts.hostname) || t
            }
            return t
        } else {
            var e;
            if (!v) {
                throw new TypeError("cannot set TLD empty")
            } else if (v.match(/[^a-zA-Z0-9-]/)) {
                if (S && S.is(v)) {
                    e = new RegExp(b(this.tld()) + "$");
                    this._parts.hostname = this._parts.hostname.replace(e, v)
                } else {
                    throw new TypeError("TLD '" + v + "' contains characters other than [A-Z0-9]")
                }
            } else if (!this._parts.hostname || this.is('IP')) {
                throw new ReferenceError("cannot set TLD on non-domain host")
            } else {
                e = new RegExp(b(this.tld()) + "$");
                this._parts.hostname = this._parts.hostname.replace(e, v)
            }
            this.build(!c);
            return this
        }
    };
    p.directory = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined || v === true) {
            if (!this._parts.path && !this._parts.hostname) {
                return ''
            }
            if (this._parts.path === '/') {
                return '/'
            }
            var d = this._parts.path.length - this.filename().length - 1;
            var i = this._parts.path.substring(0, d) || (this._parts.hostname ? "/" : "");
            return v ? U.decodePath(i) : i
        } else {
            var e = this._parts.path.length - this.filename().length;
            var l = this._parts.path.substring(0, e);
            var s = new RegExp('^' + b(l));
            if (!this.is('relative')) {
                if (!v) {
                    v = '/'
                }
                if (v.charAt(0) !== '/') {
                    v = "/" + v
                }
            }
            if (v && v.charAt(v.length - 1) !== '/') {
                v += '/'
            }
            v = U.recodePath(v);
            this._parts.path = this._parts.path.replace(s, v);
            this.build(!c);
            return this
        }
    };
    p.filename = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined || v === true) {
            if (!this._parts.path || this._parts.path === '/') {
                return ""
            }
            var d = this._parts.path.lastIndexOf('/');
            var e = this._parts.path.substring(d + 1);
            return v ? U.decodePathSegment(e) : e
        } else {
            var i = false;
            if (v.charAt(0) === '/') {
                v = v.substring(1)
            }
            if (v.match(/\.?\//)) {
                i = true
            }
            var l = new RegExp(b(this.filename()) + "$");
            v = U.recodePath(v);
            this._parts.path = this._parts.path.replace(l, v);
            if (i) {
                this.normalizePath(c)
            } else {
                this.build(!c)
            }
            return this
        }
    };
    p.suffix = function(v, c) {
        if (this._parts.urn) {
            return v === undefined ? '' : this
        }
        if (v === undefined || v === true) {
            if (!this._parts.path || this._parts.path === '/') {
                return ""
            }
            var d = this.filename();
            var e = d.lastIndexOf('.');
            var s, i;
            if (e === -1) {
                return ""
            }
            s = d.substring(e + 1);
            i = (/^[a-z0-9%]+$/i).test(s) ? s : "";
            return v ? U.decodePathSegment(i) : i
        } else {
            if (v.charAt(0) === '.') {
                v = v.substring(1)
            }
            var l = this.suffix();
            var t;
            if (!l) {
                if (!v) {
                    return this
                }
                this._parts.path += '.' + U.recodePath(v)
            } else if (!v) {
                t = new RegExp(b("." + l) + "$")
            } else {
                t = new RegExp(b(l) + "$")
            }
            if (t) {
                v = U.recodePath(v);
                this._parts.path = this._parts.path.replace(t, v)
            }
            this.build(!c);
            return this
        }
    };
    p.segment = function(s, v, c) {
        var d = this._parts.urn ? ':' : '/';
        var e = this.path();
        var t = e.substring(0, 1) === '/';
        var x = e.split(d);
        if (s !== undefined && typeof s !== 'number') {
            c = v;
            v = s;
            s = undefined
        }
        if (s !== undefined && typeof s !== 'number') {
            throw new Error("Bad segment '" + s + "', must be 0-based integer")
        }
        if (t) {
            x.shift()
        }
        if (s < 0) {
            s = Math.max(x.length + s, 0)
        }
        if (v === undefined) {
            return s === undefined ? x : x[s]
        } else if (s === null || x[s] === undefined) {
            if (f(v)) {
                x = [];
                for (var i = 0, l = v.length; i < l; i++) {
                    if (!v[i].length && (!x.length || !x[x.length - 1].length)) {
                        continue
                    }
                    if (x.length && !x[x.length - 1].length) {
                        x.pop()
                    }
                    x.push(v[i])
                }
            } else if (v || (typeof v === "string")) {
                if (x[x.length - 1] === "") {
                    x[x.length - 1] = v
                } else {
                    x.push(v)
                }
            }
        } else {
            if (v || (typeof v === "string" && v.length)) {
                x[s] = v
            } else {
                x.splice(s, 1)
            }
        }
        if (t) {
            x.unshift("")
        }
        return this.path(x.join(d), c)
    };
    p.segmentCoded = function(s, v, c) {
        var d, i, l;
        if (typeof s !== 'number') {
            c = v;
            v = s;
            s = undefined
        }
        if (v === undefined) {
            d = this.segment(s, v, c);
            if (!f(d)) {
                d = d !== undefined ? U.decode(d) : undefined
            } else {
                for (i = 0, l = d.length; i < l; i++) {
                    d[i] = U.decode(d[i])
                }
            }
            return d
        }
        if (!f(v)) {
            v = typeof v === 'string' ? U.encode(v) : v
        } else {
            for (i = 0, l = v.length; i < l; i++) {
                v[i] = U.decode(v[i])
            }
        }
        return this.segment(s, v, c)
    };
    var q = p.query;
    p.query = function(v, c) {
        if (v === true) {
            return U.parseQuery(this._parts.query, this._parts.escapeQuerySpace)
        } else if (typeof v === "function") {
            var d = U.parseQuery(this._parts.query, this._parts.escapeQuerySpace);
            var e = v.call(this, d);
            this._parts.query = U.buildQuery(e || d, this._parts.duplicateQueryParameters, this._parts.escapeQuerySpace);
            this.build(!c);
            return this
        } else if (v !== undefined && typeof v !== "string") {
            this._parts.query = U.buildQuery(v, this._parts.duplicateQueryParameters, this._parts.escapeQuerySpace);
            this.build(!c);
            return this
        } else {
            return q.call(this, v, c)
        }
    };
    p.setQuery = function(c, v, d) {
        var e = U.parseQuery(this._parts.query, this._parts.escapeQuerySpace);
        if (typeof c === "object") {
            for (var i in c) {
                if (h.call(c, i)) {
                    e[i] = c[i]
                }
            }
        } else if (typeof c === "string") {
            e[c] = v !== undefined ? v : null
        } else {
            throw new TypeError("URI.addQuery() accepts an object, string as the name parameter")
        }
        this._parts.query = U.buildQuery(e, this._parts.duplicateQueryParameters, this._parts.escapeQuerySpace);
        if (typeof c !== "string") {
            d = v
        }
        this.build(!d);
        return this
    };
    p.addQuery = function(c, v, d) {
        var e = U.parseQuery(this._parts.query, this._parts.escapeQuerySpace);
        U.addQuery(e, c, v === undefined ? null : v);
        this._parts.query = U.buildQuery(e, this._parts.duplicateQueryParameters, this._parts.escapeQuerySpace);
        if (typeof c !== "string") {
            d = v
        }
        this.build(!d);
        return this
    };
    p.removeQuery = function(c, v, d) {
        var e = U.parseQuery(this._parts.query, this._parts.escapeQuerySpace);
        U.removeQuery(e, c, v);
        this._parts.query = U.buildQuery(e, this._parts.duplicateQueryParameters, this._parts.escapeQuerySpace);
        if (typeof c !== "string") {
            d = v
        }
        this.build(!d);
        return this
    };
    p.hasQuery = function(c, v, d) {
        var e = U.parseQuery(this._parts.query, this._parts.escapeQuerySpace);
        return U.hasQuery(e, c, v, d)
    };
    p.setSearch = p.setQuery;
    p.addSearch = p.addQuery;
    p.removeSearch = p.removeQuery;
    p.hasSearch = p.hasQuery;
    p.normalize = function() {
        if (this._parts.urn) {
            return this.normalizeProtocol(false).normalizeQuery(false).normalizeFragment(false).build()
        }
        return this.normalizeProtocol(false).normalizeHostname(false).normalizePort(false).normalizePath(false).normalizeQuery(false).normalizeFragment(false).build()
    };
    p.normalizeProtocol = function(c) {
        if (typeof this._parts.protocol === "string") {
            this._parts.protocol = this._parts.protocol.toLowerCase();
            this.build(!c)
        }
        return this
    };
    p.normalizeHostname = function(c) {
        if (this._parts.hostname) {
            if (this.is('IDN') && a) {
                this._parts.hostname = a.toASCII(this._parts.hostname)
            } else if (this.is('IPv6') && I) {
                this._parts.hostname = I.best(this._parts.hostname)
            }
            this._parts.hostname = this._parts.hostname.toLowerCase();
            this.build(!c)
        }
        return this
    };
    p.normalizePort = function(c) {
        if (typeof this._parts.protocol === "string" && this._parts.port === U.defaultPorts[this._parts.protocol]) {
            this._parts.port = null;
            this.build(!c)
        }
        return this
    };
    p.normalizePath = function(c) {
        if (this._parts.urn) {
            return this
        }
        if (!this._parts.path || this._parts.path === '/') {
            return this
        }
        var d;
        var e = this._parts.path;
        var i, l;
        if (e.charAt(0) !== '/') {
            d = true;
            e = '/' + e
        }
        e = e.replace(/(\/(\.\/)+)|(\/\.$)/g, '/').replace(/\/{2,}/g, '/');
        while (true) {
            i = e.indexOf('/../');
            if (i === -1) {
                break
            } else if (i === 0) {
                e = e.substring(3);
                break
            }
            l = e.substring(0, i).lastIndexOf('/');
            if (l === -1) {
                l = i
            }
            e = e.substring(0, l) + e.substring(i + 3)
        }
        if (d && this.is('relative')) {
            e = e.substring(1)
        }
        e = U.recodePath(e);
        this._parts.path = e;
        this.build(!c);
        return this
    };
    p.normalizePathname = p.normalizePath;
    p.normalizeQuery = function(c) {
        if (typeof this._parts.query === "string") {
            if (!this._parts.query.length) {
                this._parts.query = null
            } else {
                this.query(U.parseQuery(this._parts.query, this._parts.escapeQuerySpace))
            }
            this.build(!c)
        }
        return this
    };
    p.normalizeFragment = function(c) {
        if (!this._parts.fragment) {
            this._parts.fragment = null;
            this.build(!c)
        }
        return this
    };
    p.normalizeSearch = p.normalizeQuery;
    p.normalizeHash = p.normalizeFragment;
    p.iso8859 = function() {
        var e = U.encode;
        var d = U.decode;
        U.encode = escape;
        U.decode = decodeURIComponent;
        this.normalize();
        U.encode = e;
        U.decode = d;
        return this
    };
    p.unicode = function() {
        var e = U.encode;
        var d = U.decode;
        U.encode = o;
        U.decode = unescape;
        this.normalize();
        U.encode = e;
        U.decode = d;
        return this
    };
    p.readable = function() {
        var c = this.clone();
        c.username("").password("").normalize();
        var t = '';
        if (c._parts.protocol) {
            t += c._parts.protocol + '://'
        }
        if (c._parts.hostname) {
            if (c.is('punycode') && a) {
                t += a.toUnicode(c._parts.hostname);
                if (c._parts.port) {
                    t += ":" + c._parts.port
                }
            } else {
                t += c.host()
            }
        }
        if (c._parts.hostname && c._parts.path && c._parts.path.charAt(0) !== '/') {
            t += '/'
        }
        t += c.path(true);
        if (c._parts.query) {
            var q = '';
            for (var i = 0, d = c._parts.query.split('&'), l = d.length; i < l; i++) {
                var e = (d[i] || "").split('=');
                q += '&' + U.decodeQuery(e[0], this._parts.escapeQuerySpace).replace(/&/g, '%26');
                if (e[1] !== undefined) {
                    q += "=" + U.decodeQuery(e[1], this._parts.escapeQuerySpace).replace(/&/g, '%26')
                }
            }
            t += '?' + q.substring(1)
        }
        t += U.decodeQuery(c.hash(), true);
        return t
    };
    p.absoluteTo = function(c) {
        var d = this.clone();
        var e = ['protocol', 'username', 'password', 'hostname', 'port'];
        var l, i, p;
        if (this._parts.urn) {
            throw new Error('URNs do not have any generally defined hierarchical components')
        }
        if (!(c instanceof U)) {
            c = new U(c)
        }
        if (!d._parts.protocol) {
            d._parts.protocol = c._parts.protocol
        }
        if (this._parts.hostname) {
            return d
        }
        for (i = 0; p = e[i]; i++) {
            d._parts[p] = c._parts[p]
        }
        e = ['query', 'path'];
        for (i = 0; p = e[i]; i++) {
            if (!d._parts[p] && c._parts[p]) {
                d._parts[p] = c._parts[p]
            }
        }
        if (d.path().charAt(0) !== '/') {
            l = c.directory();
            d._parts.path = (l ? (l + '/') : '') + d._parts.path;
            d.normalizePath()
        }
        d.build();
        return d
    };
    p.relativeTo = function(c) {
        var d = this.clone().normalize();
        var e, i, l, s, t;
        if (d._parts.urn) {
            throw new Error('URNs do not have any generally defined hierarchical components')
        }
        c = new U(c).normalize();
        e = d._parts;
        i = c._parts;
        s = d.path();
        t = c.path();
        if (s.charAt(0) !== '/') {
            throw new Error('URI is already relative')
        }
        if (t.charAt(0) !== '/') {
            throw new Error('Cannot calculate a URI relative to another relative URI')
        }
        if (e.protocol === i.protocol) {
            e.protocol = null
        }
        if (e.username !== i.username || e.password !== i.password) {
            return d.build()
        }
        if (e.protocol !== null || e.username !== null || e.password !== null) {
            return d.build()
        }
        if (e.hostname === i.hostname && e.port === i.port) {
            e.hostname = null;
            e.port = null
        } else {
            return d.build()
        }
        if (s === t) {
            e.path = '';
            return d.build()
        }
        l = U.commonPath(d.path(), c.path());
        if (!l) {
            return d.build()
        }
        var v = i.path.substring(l.length).replace(/[^\/]*$/, '').replace(/.*?\//g, '../');
        e.path = v + e.path.substring(l.length);
        return d.build()
    };
    p.equals = function(c) {
        var d = this.clone();
        var t = new U(c);
        var e = {};
        var i = {};
        var l = {};
        var s, v, x;
        d.normalize();
        t.normalize();
        if (d.toString() === t.toString()) {
            return true
        }
        s = d.query();
        v = t.query();
        d.query("");
        t.query("");
        if (d.toString() !== t.toString()) {
            return false
        }
        if (s.length !== v.length) {
            return false
        }
        e = U.parseQuery(s, this._parts.escapeQuerySpace);
        i = U.parseQuery(v, this._parts.escapeQuerySpace);
        for (x in e) {
            if (h.call(e, x)) {
                if (!f(e[x])) {
                    if (e[x] !== i[x]) {
                        return false
                    }
                } else if (!m(e[x], i[x])) {
                    return false
                }
                l[x] = true
            }
        }
        for (x in i) {
            if (h.call(i, x)) {
                if (!l[x]) {
                    return false
                }
            }
        }
        return true
    };
    p.duplicateQueryParameters = function(v) {
        this._parts.duplicateQueryParameters = !! v;
        return this
    };
    p.escapeQuerySpace = function(v) {
        this._parts.escapeQuerySpace = !! v;
        return this
    };
    return U
}));