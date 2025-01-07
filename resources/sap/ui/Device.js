﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

if (window.jQuery && window.jQuery.sap && window.jQuery.sap.declare) {
    window.jQuery.sap.declare("sap.ui.Device", false)
}
if (typeof window.sap !== "object" && typeof window.sap !== "function") {
    window.sap = {}
}
if (typeof window.sap.ui !== "object") {
    window.sap.ui = {}
}(function() {
    if (typeof window.sap.ui.Device === "object" || typeof window.sap.ui.Device === "function") {
        return
    }
    var d = {};

    function p(i, w) {
        return ("000" + String(i)).slice(-w)
    }
    var F = 0,
        E = 1,
        W = 2,
        I = 3,
        D = 4,
        T = 5;
    var c = function() {
        this.defaultComponent = 'DEVICE';
        this.sWindowName = (window.top == window) ? "" : "[" + window.location.pathname.split('/').slice(-1)[0] + "] ";
        this.log = function(i, s, a) {
            a = a || this.defaultComponent || '';
            var b = new Date(),
                e = {
                    time: p(b.getHours(), 2) + ":" + p(b.getMinutes(), 2) + ":" + p(b.getSeconds(), 2),
                    date: p(b.getFullYear(), 4) + "-" + p(b.getMonth() + 1, 2) + "-" + p(b.getDate(), 2),
                    timestamp: b.getTime(),
                    level: i,
                    message: s || "",
                    component: a || ""
                };
            if (window.console) {
                var f = e.date + " " + e.time + " " + this.sWindowName + e.message + " - " + e.component;
                switch (i) {
                    case F:
                    case E:
                        console.error(f);
                        break;
                    case W:
                        console.warn(f);
                        break;
                    case I:
                        console.info ? console.info(f):
                            console.log(f);
                            break;
                        case D:
                            console.debug ? console.debug(f):
                                console.log(f);
                                break;
                            case T:
                                console.trace ? console.trace(f):
                                    console.log(f);
                                    break
                }
            }
            return e
        }
    };
    var l = new c();
    l.log(I, "Device API logging initialized");
    var m = {};

    function g(e, f, a) {
        if (!m[e]) {
            m[e] = []
        }
        m[e].push({
            oListener: a,
            fFunction: f
        })
    };

    function h(e, f, a) {
        var b = m[e];
        if (!b) {
            return this
        }
        for (var i = 0, q = b.length; i < q; i++) {
            if (b[i].fFunction === f && b[i].oListener === a) {
                b.splice(i, 1);
                break
            }
        }
        if (b.length == 0) {
            delete m[e]
        }
    };

    function j(e, a) {
        var b = m[e],
            f;
        if (b) {
            b = b.slice();
            for (var i = 0, q = b.length; i < q; i++) {
                f = b[i];
                f.fFunction.call(f.oListener || window, a)
            }
        }
    };
    var O = {
        "WINDOWS": "win",
        "MACINTOSH": "mac",
        "LINUX": "linux",
        "IOS": "iOS",
        "ANDROID": "Android",
        "BLACKBERRY": "bb",
        "WINDOWS_PHONE": "winphone"
    };

    function k(a) {
        function b() {
            var s = navigator.platform;
            if (s.indexOf("Win") != -1) {
                return {
                    "name": O.WINDOWS,
                    "versionStr": ""
                }
            } else if (s.indexOf("Mac") != -1) {
                return {
                    "name": O.MACINTOSH,
                    "versionStr": ""
                }
            } else if (s.indexOf("Linux") != -1) {
                return {
                    "name": O.LINUX,
                    "versionStr": ""
                }
            }
            l.log(I, "OS detection returned no result");
            return null
        }
        a = a || navigator.userAgent;
        var e = /\(([a-zA-Z ]+);\s(?:[U]?[;]?)([\D]+)((?:[\d._]*))(?:.*[\)][^\d]*)([\d.]*)\s/;
        var f = a.match(e);
        if (f) {
            var i = /iPhone|iPad|iPod/;
            var q = /PlayBook|BlackBerry/;
            if (f[0].match(i)) {
                f[3] = f[3].replace(/_/g, ".");
                return ({
                    "name": O.IOS,
                    "versionStr": f[3]
                })
            } else if (f[2].match(/Android/)) {
                f[2] = f[2].replace(/\s/g, "");
                return ({
                    "name": O.ANDROID,
                    "versionStr": f[3]
                })
            } else if (f[0].match(q)) {
                return ({
                    "name": O.BLACKBERRY,
                    "versionStr": f[4]
                })
            } else {
                return b()
            }
        } else if (a.indexOf("(BB10;") > 0) {
            e = /\sVersion\/([\d.]+)\s/;
            f = a.match(e);
            if (f) {
                return {
                    "name": O.BLACKBERRY,
                    "versionStr": f[1]
                }
            } else {
                return {
                    "name": O.BLACKBERRY,
                    "versionStr": '10'
                }
            }
        } else {
            e = /Windows Phone (?:OS )?([\d.]*)/;
            f = a.match(e);
            if (f) {
                return ({
                    "name": O.WINDOWS_PHONE,
                    "versionStr": f[1]
                })
            } else {
                return b()
            }
        }
    };

    function n() {
        d.os = k() || {};
        d.os.OS = O;
        d.os.version = d.os.versionStr ? parseFloat(d.os.versionStr) : -1;
        if (d.os.name) {
            for (var b in O) {
                if (O[b] === d.os.name) {
                    d.os[b.toLowerCase()] = true
                }
            }
        }
    }
    n();
    var B = {
        "INTERNET_EXPLORER": "ie",
        "FIREFOX": "ff",
        "CHROME": "cr",
        "SAFARI": "sf",
        "ANDROID": "an"
    };
    var u = navigator.userAgent;

    /*!
     * Taken from jQuery JavaScript Library v1.7.1
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

    function o() {
        var a = u.toLowerCase();
        var b = /(webkit)[ \/]([\w.]+)/;
        var e = /(opera)(?:.*version)?[ \/]([\w.]+)/;
        var f = /(msie) ([\w.]+)/;
        var i = /(trident)\/[\w.]+;.*rv:([\w.]+)/;
        var q = /(mozilla)(?:.*? rv:([\w.]+))?/;
        var Q = b.exec(a) || e.exec(a) || f.exec(a) || i.exec(a) || a.indexOf("compatible") < 0 && q.exec(a) || [];
        var s = {
            browser: Q[1] || "",
            version: Q[2] || "0"
        };
        s[s.browser] = true;
        return s
    };

    function r() {
        var b = o();
        if (b.mozilla) {
            if (u.match(/Firefox\/(\d+\.\d+)/)) {
                var a = parseFloat(RegExp.$1);
                return {
                    name: B.FIREFOX,
                    versionStr: "" + a,
                    version: a,
                    mobile: false
                }
            }
        } else if (b.webkit) {
            var e = u.toLowerCase().match(/webkit[\/]([\d.]+)/);
            if (e) {
                var w = e[1]
            }
            var f = /Mobile/;
            if (u.match(/Chrome\/(\d+\.\d+).\d+/)) {
                var a = parseFloat(RegExp.$1);
                return {
                    name: B.CHROME,
                    versionStr: "" + a,
                    version: a,
                    mobile: f.test(u),
                    webkit: true,
                    webkitVersion: w
                }
            } else if (u.match(/Android .+ Version\/(\d+\.\d+)/)) {
                var a = parseFloat(RegExp.$1);
                return {
                    name: B.ANDROID,
                    versionStr: "" + a,
                    version: a,
                    mobile: f.test(u),
                    webkit: true,
                    webkitVersion: w
                }
            } else {
                var i = /Version\/(\d+\.\d+).*Safari/;
                if (i.test(u)) {
                    var a = parseFloat(i.exec(u)[1]);
                    return {
                        name: B.SAFARI,
                        versionStr: "" + a,
                        version: a,
                        mobile: f.test(u),
                        webkit: true
                    }
                }
            }
        } else if (b.msie || b.trident) {
            var a;
            if (document.documentMode) {
                if (document.documentMode === 7) {
                    a = 8.0
                } else {
                    a = parseFloat(document.documentMode)
                }
            } else {
                a = parseFloat(b.version)
            }
            return {
                name: B.INTERNET_EXPLORER,
                versionStr: "" + a,
                version: a,
                mobile: false
            }
        }
        return {
            name: "",
            versionStr: "",
            version: -1,
            mobile: false
        }
    };

    function v() {
        d.browser = r();
        d.browser.BROWSER = B;
        if (d.browser.name) {
            for (var b in B) {
                if (B[b] === d.browser.name) {
                    d.browser[b.toLowerCase()] = true
                }
            }
        }
    }
    v();
    d.support = {};
    d.support.touch = !! (('ontouchstart' in window) || window.DocumentTouch && document instanceof window.DocumentTouch);
    d.support.matchmedia = !! window.matchMedia;
    d.support.matchmedialistener = !! (d.support.matchmedia && !! window.matchMedia("screen and (max-width:0px)").addListener);
    if (d.browser.safari && d.browser.version < 6) {
        d.support.matchmedialistener = false
    }
    d.support.orientation = !! ("orientation" in window && "onorientationchange" in window);
    d.support.retina = (window.retina || window.devicePixelRatio >= 2);
    d.support.websocket = ('WebSocket' in window);
    d.media = {};
    var R = {
        "SAP_3STEPS": "3Step",
        "SAP_4STEPS": "4Step",
        "SAP_6STEPS": "6Step",
        "SAP_STANDARD": "Std"
    };
    d.media.RANGESETS = R;
    d.media._predefinedRangeSets = {};
    d.media._predefinedRangeSets[R.SAP_3STEPS] = {
        points: [520, 960],
        unit: "px",
        name: R.SAP_3STEPS,
        names: ["S", "M", "L"]
    };
    d.media._predefinedRangeSets[R.SAP_4STEPS] = {
        points: [520, 760, 960],
        unit: "px",
        name: R.SAP_4STEPS,
        names: ["S", "M", "L", "XL"]
    };
    d.media._predefinedRangeSets[R.SAP_6STEPS] = {
        points: [241, 400, 541, 768, 960],
        unit: "px",
        name: R.SAP_6STEPS,
        names: ["XS", "S", "M", "L", "XL", "XXL"]
    };
    d.media._predefinedRangeSets[R.SAP_STANDARD] = {
        points: [600, 1024],
        unit: "px",
        name: R.SAP_STANDARD,
        names: ["Phone", "Tablet", "Desktop"]
    };
    var _ = R.SAP_STANDARD;
    var y = d.support.matchmedialistener ? 0 : 100;
    var z = {};
    var A = null;

    function C(f, t, a) {
        a = a || "px";
        var q = "screen";
        if (f > 0) {
            q = q + " and (min-width:" + f + a + ")"
        }
        if (t > 0) {
            q = q + " and (max-width:" + t + a + ")"
        }
        return q
    };

    function G(a) {
        if (!d.support.matchmedialistener && A == M()[0]) {
            return
        }
        if (z[a].timer) {
            clearTimeout(z[a].timer);
            z[a].timer = null
        }
        z[a].timer = setTimeout(function() {
            var b = J(a, false);
            if (b) {
                j("media_" + a, b)
            }
        }, y)
    };

    function H(s, i) {
        var q = z[s].queries[i];
        var a = {
            from: q.from,
            unit: z[s].unit
        };
        if (q.to >= 0) {
            a.to = q.to
        }
        if (z[s].names) {
            a.name = z[s].names[i]
        }
        return a
    };

    function J(a, b) {
        if (z[a]) {
            var e = z[a].queries;
            var f = null;
            for (var i = 0, s = e.length; i < s; i++) {
                var q = e[i];
                if ((q != z[a].currentquery || b) && window.sap.ui.Device.media.matches(q.from, q.to, z[a].unit)) {
                    if (!b) {
                        z[a].currentquery = q
                    }
                    if (!z[a].noClasses && z[a].names && !b) {
                        K(a, z[a].names[i])
                    }
                    f = H(a, i)
                }
            }
            return f
        }
        l.log(W, "No queryset with name " + a + " found", 'DEVICE.MEDIA');
        return null
    };

    function K(s, a, b) {
        var e = "sapUiMedia-" + s + "-";
        L(e + a, b, e)
    };

    function L(s, b, a) {
        var e = document.documentElement;
        if (e.className.length == 0) {
            if (!b) {
                e.className = s
            }
        } else {
            var f = e.className.split(" ");
            var q = "";
            for (var i = 0; i < f.length; i++) {
                if ((a && f[i].indexOf(a) != 0) || (!a && f[i] != s)) {
                    q = q + f[i] + " "
                }
            }
            if (!b) {
                q = q + s
            }
            e.className = q
        }
    };

    function M() {
        return [document.documentElement.clientWidth, document.documentElement.clientHeight]
    };

    function N(a, b) {
        if (b === "em" || b === "rem") {
            var s = window.getComputedStyle || function(e) {
                    return e.currentStyle
                };
            var x = s(document.documentElement).fontSize;
            var f = (x && x.indexOf("px") >= 0) ? parseFloat(x, 10) : 16;
            return a * f
        }
        return a
    };

    function P(f, t, e) {
        f = N(f, e);
        t = N(t, e);
        var w = M()[0];
        var a = f < 0 || f <= w;
        var b = t < 0 || w <= t;
        return a && b
    };

    function Q(f, t, a) {
        var q = C(f, t, a);
        return window.matchMedia(q).matches
    };
    d.media.matches = d.support.matchmedia ? Q : P;
    d.media.attachHandler = function(f, a, s) {
        var b = s || _;
        g("media_" + b, f, a)
    };
    d.media.detachHandler = function(f, a, s) {
        var b = s || _;
        h("media_" + b, f, a)
    };
    d.media.initRangeSet = function(s, a, b, e, f) {
        var t;
        if (!s) {
            t = d.media._predefinedRangeSets[_]
        } else if (s && d.media._predefinedRangeSets[s]) {
            t = d.media._predefinedRangeSets[s]
        } else {
            t = {
                name: s,
                unit: (b || "px").toLowerCase(),
                points: a || [],
                names: e,
                noClasses: !! f
            }
        }
        if (d.media.hasRangeSet(t.name)) {
            return;
            l.log(I, "Range set " + t.name + " hase already been initialized", 'DEVICE.MEDIA')
        }
        s = t.name;
        t.queries = [];
        t.timer = null;
        t.currentquery = null;
        t.listener = function() {
            return G(s)
        };
        var w, x, n1;
        var o1 = t.points;
        for (var i = 0, p1 = o1.length; i <= p1; i++) {
            w = (i == 0) ? 0 : o1[i - 1];
            x = (i == o1.length) ? -1 : o1[i];
            n1 = C(w, x, t.unit);
            t.queries.push({
                query: n1,
                from: w,
                to: x
            })
        };
        if (t.names && t.names.length != t.queries.length) {
            t.names = null
        }
        z[t.name] = t;
        if (d.support.matchmedialistener) {
            var q1 = t.queries;
            for (var i = 0; i < q1.length; i++) {
                var q = q1[i];
                q.media = window.matchMedia(q.query);
                q.media.addListener(t.listener)
            }
        } else {
            if (window.addEventListener) {
                window.addEventListener("resize", t.listener, false);
                window.addEventListener("orientationchange", t.listener, false)
            } else {
                window.attachEvent("onresize", t.listener)
            }
        }
        t.listener()
    };
    d.media.getCurrentRange = function(s) {
        if (!d.media.hasRangeSet(s)) {
            return null
        }
        return J(s, true)
    };
    d.media.hasRangeSet = function(s) {
        return s && !! z[s]
    };
    d.media.removeRangeSet = function(s) {
        if (!d.media.hasRangeSet(s)) {
            l.log(I, "RangeSet " + s + " not found, thus could not be removed.", 'DEVICE.MEDIA');
            return
        }
        for (var x in R) {
            if (s === R[x]) {
                l.log(W, "Cannot remove default rangeset - no action taken.", 'DEVICE.MEDIA');
                return
            }
        }
        var a = z[s];
        if (d.support.matchmedialistener) {
            var q = a.queries;
            for (var i = 0; i < q.length; i++) {
                q[i].media.removeListener(a.listener)
            }
        } else {
            if (window.removeEventListener) {
                window.removeEventListener("resize", a.listener, false);
                window.removeEventListener("orientationchange", a.listener, false)
            } else {
                window.detachEvent("onresize", a.listener)
            }
        }
        K(s, "", true);
        delete m["media_" + s];
        delete z[s]
    };
    d.orientation = {};
    d.resize = {};
    d.orientation.attachHandler = function(f, a) {
        g("orientation", f, a)
    };
    d.resize.attachHandler = function(f, a) {
        g("resize", f, a)
    };
    d.orientation.detachHandler = function(f, a) {
        h("orientation", f, a)
    };
    d.resize.detachHandler = function(f, a) {
        h("resize", f, a)
    };

    function S(i) {
        i.landscape = g1(true);
        i.portrait = !i.landscape
    };

    function U() {
        S(d.orientation);
        j("orientation", {
            landscape: d.orientation.landscape
        })
    };

    function V() {
        X(d.resize);
        j("resize", {
            height: d.resize.height,
            width: d.resize.width
        })
    };

    function X(i) {
        i.width = M()[0];
        i.height = M()[1]
    };

    function Y() {
        var w = window.sap.ui.Device.orientation.landscape;
        var i = g1();
        if (w != i) {
            U()
        }
        if (!c1) {
            c1 = window.setTimeout(Z, 150)
        }
    };

    function Z() {
        V();
        c1 = null
    };
    if (d.support.touch && d.support.orientation) {
        window.addEventListener("resize", h1, false);
        window.addEventListener("orientationchange", h1, false)
    } else {
        if (window.addEventListener) {
            window.addEventListener("resize", Y, false)
        } else {
            window.attachEvent("onresize", Y)
        }
    }
    var $ = false;
    var a1 = false;
    var b1;
    var c1;
    var d1 = M()[1];
    var e1 = M()[0];
    var f1 = false;

    function g1(f) {
        if (d.support.touch && d.support.orientation) {
            if (f1 && f) {
                return !d.orientation.landscape
            }
            if (f1) {
                return d.orientation.landscape
            }
        } else {
            if (d.support.matchmedia && d.support.orientation) {
                return !!window.matchMedia("(orientation: landscape)").matches
            }
        }
        var s = M();
        return s[0] > s[1]
    };

    function h1(e) {
        if (e.type == "resize") {
            var w = M()[1];
            var i = M()[0];
            if (w === d1 && i === e1) {
                return
            }
            a1 = true;
            if ((d1 != w) && (e1 == i)) {
                f1 = (w < d1);
                V()
            } else {
                e1 = i
            }
            d1 = w
        } else if (e.type == "orientationchange") {
            $ = true
        }
        if (b1) {
            clearTimeout(b1);
            b1 = null
        }
        b1 = window.setTimeout(i1, 50)
    };

    function i1() {
        if ($ && a1) {
            U();
            V();
            $ = false;
            a1 = false
        }
        b1 = null
    };
    var j1 = {
        "TABLET": "tablet",
        "PHONE": "phone",
        "DESKTOP": "desktop"
    };
    d.system = {};

    function k1(a) {
        var t = l1();
        var s = {};
        s.tablet = (d.support.touch || !! a) && t;
        s.phone = (d.support.touch || !! a) && !t;
        s.desktop = !s.tablet && !s.phone;
        s.SYSTEMTYPE = j1;
        for (var b in j1) {
            L("sap-" + j1[b], !s[j1[b]])
        }
        return s
    };

    function l1() {
        var a = (/(?=android)(?=.*mobile)/i.test(navigator.userAgent));
        if (d.os.name === d.os.OS.IOS) {
            return /ipad/i.test(navigator.userAgent)
        } else {
            if (d.support.touch) {
                var b = window.devicePixelRatio ? window.devicePixelRatio : 1;
                if ((d.os.name === d.os.OS.ANDROID) && d.browser.webkit && (d.browser.webkitVersion > 537.10)) {
                    b = 1
                }
                var t = (Math.min(window.screen.width / b, window.screen.height / b) >= 600);
                if (g1() && (window.screen.height === 552 || window.screen.height === 553) && (/Nexus 7/i.test(navigator.userAgent))) {
                    t = true
                }
                return t
            } else {
                var e = (d.os.name === d.os.OS.ANDROID) && !a;
                return e
            }
        }
    };

    function m1(a) {
        d.system = k1(a)
    }
    m1();
    d._update = function(a) {
        u = navigator.userAgent;
        l.log(W, "Device API values manipulated: NOT PRODUCTIVE FEATURE!!! This should be only used for test purposes. Only use if you know what you are doing.");
        v();
        n();
        m1(a)
    };
    X(d.resize);
    S(d.orientation);
    window.sap.ui.Device = d;
    d.media.initRangeSet();
    if (window.jQuery && jQuery.sap && jQuery.sap.define) {
        jQuery.sap.define("sap/ui/Device", [], function() {
            return d
        })
    }
}());