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
    var I = /MSIE 6/;
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