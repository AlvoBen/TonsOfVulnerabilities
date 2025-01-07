﻿/*
File: flexie.js

About: Version
	1.0.3

Project: Flexie

Description:
	Legacy support for the CSS3 Flexible Box Model

License:
	The MIT License
	
	Copyright (c) 2010 Richard Herrera

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

var Flexie = (function(win, doc) {
    var FLX = {}, FLX_DOM_ID = 0,
        FLX_DOM_ATTR = "data-flexie-id",
        FLX_PARENT_ATTR = "data-flexie-parent",
        SUPPORT, ENGINE, ENGINES = {
            "NW": {
                s: "*.Dom.select"
            },
            "DOMAssistant": {
                s: "*.$",
                m: "*.DOMReady"
            },
            "Prototype": {
                s: "$$",
                m: "document.observe",
                p: "dom:loaded",
                c: "document"
            },
            "YAHOO": {
                s: "*.util.Selector.query",
                m: "*.util.Event.onDOMReady",
                c: "*.util.Event"
            },
            "MooTools": {
                s: "$$",
                m: "window.addEvent",
                p: "domready"
            },
            "Sizzle": {
                s: "*"
            },
            "jQuery": {
                s: "*",
                m: "*(document).ready"
            },
            "dojo": {
                s: "*.query",
                m: "*.addOnLoad"
            }
        }, LIBRARY, PIXEL = /^-?\d+(?:px)?$/i,
        NUMBER = /^-?\d/,
        SIZES = /width|height|margin|padding|border/,
        MSIE = /(msie) ([\w.]+)/,
        WHITESPACE_CHARACTERS = /\t|\n|\r/g,
        RESTRICTIVE_PROPERTIES = /^max\-([a-z]+)/,
        PROTOCOL = /^https?:\/\//i,
        LEADINGTRIM = /^\s\s*/,
        TRAILINGTRIM = /\s\s*$/,
        ONLY_WHITESPACE = /^\s*$/,
        CSS_SELECTOR = /\s?(\#|\.|\[|\:(\:)?[^first\-(line|letter)|before|after]+)/g,
        EMPTY_STRING = "",
        SPACE_STRING = " ",
        PLACEHOLDER_STRING = "$1",
        PADDING_RIGHT = "paddingRight",
        PADDING_BOTTOM = "paddingBottom",
        PADDING_LEFT = "paddingLeft",
        PADDING_TOP = "paddingTop",
        BORDER_RIGHT = "borderRightWidth",
        BORDER_BOTTOM = "borderBottomWidth",
        BORDER_LEFT = "borderLeftWidth",
        BORDER_TOP = "borderTopWidth",
        HORIZONTAL = "horizontal",
        VERTICAL = "vertical",
        INLINE_AXIS = "inline-axis",
        BLOCK_AXIS = "block-axis",
        INHERIT = "inherit",
        LEFT = "left",
        END_MUSTACHE = "}",
        PREFIXES = " -o- -moz- -ms- -webkit- -khtml- ".split(SPACE_STRING),
        DEFAULTS = {
            orient: HORIZONTAL,
            align: "stretch",
            direction: INHERIT,
            pack: "start"
        }, FLEX_BOXES = [],
        POSSIBLE_FLEX_CHILDREN = [],
        DOM_ORDERED, RESIZE_LISTENER, TRUE = true,
        FALSE = false,
        NULL = null,
        UNDEFINED, BROWSER = {
            IE: (function() {
                var i, u = win.navigator.userAgent,
                    m = (MSIE).exec(u.toLowerCase());
                if (m) {
                    i = parseInt(m[2], 10)
                }
                return i
            }())
        },

        /*
	    selectivizr v1.0.0 - (c) Keith Clark, freely distributable under the terms 
	    of the MIT license.
        
	    selectivizr.com
	    */

        selectivizrEngine;

    function trim(s) {
        if (s) {
            s = s.replace(LEADINGTRIM, EMPTY_STRING).replace(TRAILINGTRIM, EMPTY_STRING)
        }
        return s
    }
    function determineSelectorMethod() {
        var engines = ENGINES,
            method, engine, obj;
        for (engine in engines) {
            if (engines.hasOwnProperty(engine)) {
                obj = engines[engine];
                if (win[engine] && !method) {
                    method = eval(obj.s.replace("*", engine));
                    if (method) {
                        ENGINE = engine;
                        break
                    }
                }
            }
        }
        return method
    }
    function addEvent(t, f) {
        t = "on" + t;
        var o = win[t];
        if (typeof win[t] !== "function") {
            win[t] = f
        } else {
            win[t] = function() {
                if (o) {
                    o()
                }
                f()
            }
        }
    }
    function attachLoadMethod(handler) {
        if (!ENGINE) {
            LIBRARY = determineSelectorMethod()
        }
        var engines = ENGINES,
            method, caller, args, engine, obj;
        for (engine in engines) {
            if (engines.hasOwnProperty(engine)) {
                obj = engines[engine];
                if (win[engine] && !method && obj.m) {
                    method = eval(obj.m.replace("*", engine));
                    caller = obj.c ? eval(obj.c.replace("*", engine)) : win;
                    args = [];
                    if (method && caller) {
                        if (obj.p) {
                            args.push(obj.p)
                        }
                        args.push(handler);
                        method.apply(caller, args);
                        break
                    }
                }
            }
        }
        if (!method) {
            addEvent("load", handler)
        }
    }
    function buildSelector(n) {
        var s = n.nodeName.toLowerCase();
        if (n.id) {
            s += "#" + n.id
        } else if (n.FLX_DOM_ID) {
            s += "[" + FLX_DOM_ATTR + "='" + n.FLX_DOM_ID + "']"
        }
        return s
    }
    function setFlexieId(n) {
        if (!n.FLX_DOM_ID) {
            FLX_DOM_ID = (FLX_DOM_ID + 1);
            n.FLX_DOM_ID = FLX_DOM_ID;
            n.setAttribute(FLX_DOM_ATTR, n.FLX_DOM_ID)
        }
    }
    function buildSelectorTree(t) {
        var r = [],
            a, b, m, s, p, c, d, i, j, x;
        t = t.replace(WHITESPACE_CHARACTERS, EMPTY_STRING);
        t = t.replace(/\s?(\{|\:|\})\s?/g, PLACEHOLDER_STRING);
        a = t.split(END_MUSTACHE);
        for (i in a) {
            if (a.hasOwnProperty(i)) {
                t = a[i];
                if (t) {
                    b = [t, END_MUSTACHE].join(EMPTY_STRING);
                    m = (/(\@media[^\{]+\{)?(.*)\{(.*)\}/).exec(b);
                    if (m && m[3]) {
                        s = m[2];
                        p = m[3].split(";");
                        d = [];
                        for (j in p) {
                            if (p.hasOwnProperty(j)) {
                                x = p[j];
                                c = x.split(":");
                                if (c.length && c[1]) {
                                    d.push({
                                        property: c[0],
                                        value: c[1]
                                    })
                                }
                            }
                        }
                        if (s && d.length) {
                            r.push({
                                selector: s,
                                properties: d
                            })
                        }
                    }
                }
            }
        }
        return r
    }
    function findFlexboxElements(r) {
        var s, p, a, v, b, c = /\s?,\s?/,
            d, e, f, u = {}, g = {}, i, j, h, k, l, o, m, n, q;
        d = function(o, r, q, v) {
            var t, i, j, h;
            t = {
                selector: trim(o),
                properties: []
            };
            for (i = 0, j = r.properties.length; i < j; i++) {
                h = r.properties[i];
                t.properties.push({
                    property: trim(h.property),
                    value: trim(h.value)
                })
            }
            if (q && v) {
                t[q] = v
            }
            return t
        };
        e = function(o, r, q, v) {
            var t = (q && v) ? u[o] : g[o],
                w, x, i, j, h, k, l;
            if (t) {
                for (i = 0, j = r.properties.length; i < j; i++) {
                    h = r.properties[i];
                    for (k = 0, l = t.properties.length; k < l; k++) {
                        x = t.properties[k];
                        if (h.property === x.property) {
                            w = k;
                            return false
                        }
                    }
                    if (w) {
                        t.properties[w] = h
                    } else {
                        t.properties.push(h)
                    }
                }
                if (q && v) {
                    t[q] = v
                }
            } else {
                if (q && v) {
                    u[o] = d(o, r, q, v)
                } else {
                    g[o] = d(o, r, NULL, NULL)
                }
            }
        };
        for (i = 0, j = r.length; i < j; i++) {
            h = r[i];
            s = trim(h.selector).replace(c, ",").split(c);
            for (k = 0, l = s.length; k < l; k++) {
                o = trim(s[k]);
                p = h.properties;
                for (m = 0, n = p.length; m < n; m++) {
                    q = p[m];
                    a = trim(q.property);
                    v = trim(q.value);
                    if (a) {
                        b = a.replace("box-", EMPTY_STRING);
                        switch (b) {
                            case "display":
                                if (v === "box") {
                                    e(o, h, NULL, NULL)
                                }
                                break;
                            case "orient":
                            case "align":
                            case "direction":
                            case "pack":
                                e(o, h, NULL, NULL);
                                break;
                            case "flex":
                            case "flex-group":
                            case "ordinal-group":
                                e(o, h, b, v);
                                break
                        }
                    }
                }
            }
        }
        for (f in g) {
            if (g.hasOwnProperty(f)) {
                FLEX_BOXES.push(g[f])
            }
        }
        for (f in u) {
            if (u.hasOwnProperty(f)) {
                POSSIBLE_FLEX_CHILDREN.push(u[f])
            }
        }
        return {
            boxes: FLEX_BOXES,
            children: POSSIBLE_FLEX_CHILDREN
        }
    }
    function matchFlexChildren(p, a, b) {
        var c, u, m = [],
            i, j, d, k, l, n, e;
        for (i = 0, j = b.length; i < j; i++) {
            d = b[i];
            if (d.selector) {
                c = a(d.selector);
                c = c[0] ? c : [c];
                if (c[0]) {
                    for (k = 0, l = c.length; k < l; k++) {
                        n = c[k];
                        if (n.nodeName !== UNDEFINED) {
                            switch (n.nodeName.toLowerCase()) {
                                case "script":
                                case "style":
                                case "link":
                                    break;
                                default:
                                    if (n.parentNode === p) {
                                        setFlexieId(n);
                                        u = {};
                                        for (e in d) {
                                            if (d.hasOwnProperty(e)) {
                                                u[e] = d[e]
                                            }
                                        }
                                        u.match = n;
                                        m.push(u)
                                    }
                                    break
                            }
                        }
                    }
                }
            } else {
                setFlexieId(d);
                m.push({
                    match: d,
                    selector: buildSelector(d)
                })
            }
        }
        return m
    }
    function getParams(p) {
        var k;
        for (k in p) {
            if (p.hasOwnProperty(k)) {
                p[k] = p[k] || DEFAULTS[k]
            }
        }
        return p
    }
    function buildFlexieCall(f) {
        var s, a, b, v, c, d, e, g, h, q, r, t, u, w, x, y = {}, z, A, B, C = "[" + FLX_PARENT_ATTR + "]",
            i, j, D, k, l, E, F, G, m, n, H, o, p, I;
        if (!f) {
            return
        }
        for (i = 0, j = f.boxes.length; i < j; i++) {
            D = f.boxes[i];
            D.selector = trim(D.selector);
            s = D.selector;
            a = D.properties;
            d = e = g = h = q = NULL;
            for (k = 0, l = a.length; k < l; k++) {
                E = a[k];
                b = trim(E.property);
                v = trim(E.value);
                if (b) {
                    c = b.replace("box-", EMPTY_STRING);
                    switch (c) {
                        case "display":
                            if (v === "box") {
                                d = v
                            }
                            break;
                        case "orient":
                            e = v;
                            break;
                        case "align":
                            g = v;
                            break;
                        case "direction":
                            h = v;
                            break;
                        case "pack":
                            q = v;
                            break
                    }
                }
            }
            r = LIBRARY;
            t = r(D.selector);
            t = t[0] ? t : [t];
            for (k = 0, l = t.length; k < l; k++) {
                F = t[k];
                if (F.nodeType) {
                    setFlexieId(F);
                    u = matchFlexChildren(F, r, f.children);
                    B = s + " " + C;
                    x = {
                        target: F,
                        selector: s,
                        properties: a,
                        children: u,
                        display: d,
                        orient: e,
                        align: g,
                        direction: h,
                        pack: q,
                        nested: B
                    };
                    z = y[F.FLX_DOM_ID];
                    if (z) {
                        for (G in x) {
                            if (x.hasOwnProperty(G)) {
                                v = x[G];
                                switch (G) {
                                    case "selector":
                                        if (v && !(new RegExp(v).test(z[G]))) {
                                            z[G] += ", " + v
                                        }
                                        break;
                                    case "children":
                                        for (m = 0, n = x[G].length; m < n; m++) {
                                            H = x[G][m];
                                            A = FALSE;
                                            for (o = 0, p = z[G].length; o < p; o++) {
                                                I = z[G][o];
                                                if (H.match.FLX_DOM_ID === I.match.FLX_DOM_ID) {
                                                    A = TRUE
                                                }
                                            }
                                            if (!A) {
                                                z[G].push(H)
                                            }
                                        }
                                        break;
                                    default:
                                        if (v) {
                                            z[G] = v
                                        }
                                        break
                                }
                            }
                        }
                    } else {
                        y[F.FLX_DOM_ID] = getParams(x);
                        y[F.FLX_DOM_ID].target.setAttribute(FLX_PARENT_ATTR, TRUE)
                    }
                }
            }
        }
        DOM_ORDERED = LIBRARY(C);
        FLEX_BOXES = {};
        for (i = 0, j = DOM_ORDERED.length; i < j; i++) {
            F = DOM_ORDERED[i];
            FLEX_BOXES[F.FLX_DOM_ID] = y[F.FLX_DOM_ID]
        }
        for (G in FLEX_BOXES) {
            if (FLEX_BOXES.hasOwnProperty(G)) {
                D = FLEX_BOXES[G];
                if (D.display === "box") {
                    w = new FLX.box(D)
                }
            }
        }
    }
    function addFlexboxElement(p) {
        var t = p.target,
            s = "#" + t.id,
            f = "[" + FLX_PARENT_ATTR + "]";
        setFlexieId(t);
        children = matchFlexChildren(t, LIBRARY, t.childNodes);
        p.nestedFlexboxes = s + " " + f;
        FLEX_BOXES[t.FLX_DOM_ID] = getParams(p);
        FLEX_BOXES[t.FLX_DOM_ID].target.setAttribute(FLX_PARENT_ATTR, TRUE)
    }
    function calcPx(e, p, d) {
        var a = d.replace(d.charAt(0), d.charAt(0).toUpperCase()),
            v = e["offset" + a] || 0,
            i, j, b;
        if (v) {
            for (i = 0, j = p.length; i < j; i++) {
                b = parseFloat(e.currentStyle[p[i]]);
                if (!isNaN(b)) {
                    v -= b
                }
            }
        }
        return v
    }
    function getTrueValue(e, n) {
        var l, r, a = e.currentStyle && e.currentStyle[n],
            s = e.style;
        if (!PIXEL.test(a) && NUMBER.test(a)) {
            l = s.left;
            r = e.runtimeStyle.left;
            e.runtimeStyle.left = e.currentStyle.left;
            s.left = a || 0;
            a = s.pixelLeft + "px";
            s.left = l || 0;
            e.runtimeStyle.left = r
        }
        return a
    }
    function unAuto(e, p, n) {
        var a;
        switch (n) {
            case "width":
                a = [PADDING_LEFT, PADDING_RIGHT, BORDER_LEFT, BORDER_RIGHT];
                p = calcPx(e, a, n);
                break;
            case "height":
                a = [PADDING_TOP, PADDING_BOTTOM, BORDER_TOP, BORDER_BOTTOM];
                p = calcPx(e, a, n);
                break;
            default:
                p = getTrueValue(e, n);
                break
        }
        return p
    }
    function getPixelValue(e, p, n) {
        if (PIXEL.test(p)) {
            return p
        }
        if (p === "auto" || p === "medium") {
            p = unAuto(e, p, n)
        } else {
            p = getTrueValue(e, n)
        }
        return p
    }
    function getComputedStyle(e, p, r) {
        var v;
        if (e === UNDEFINED) {
            return
        }
        if (win.getComputedStyle) {
            v = win.getComputedStyle(e, NULL)[p]
        } else {
            if (SIZES.test(p)) {
                v = getPixelValue(e, (e && e.currentStyle) ? e.currentStyle[p] : 0, p)
            } else {
                v = e.currentStyle[p]
            }
        }
        if (r) {
            v = parseInt(v, 10);
            if (isNaN(v)) {
                v = 0
            }
        }
        return v
    }
    function clientWidth(e) {
        return e.innerWidth || e.clientWidth
    }
    function clientHeight(e) {
        return e.innerHeight || e.clientHeight
    }
    function appendProperty(t, p, v, a) {
        var c = [],
            i, j, b;
        for (i = 0, j = PREFIXES.length; i < j; i++) {
            b = PREFIXES[i];
            c.push((a ? b : EMPTY_STRING) + p + ":" + (!a ? b : EMPTY_STRING) + v)
        }
        t.style.cssText += c.join(";");
        return t
    }
    function appendPixelValue(t, p, v) {
        var a = t && t[0] ? t : [t],
            i, j;
        for (i = 0, j = a.length; i < j; i++) {
            t = a[i];
            if (t && t.style) {
                t.style[p] = (v ? (v + "px") : EMPTY_STRING)
            }
        }
    }
    function calculateSpecificity(s) {
        var a, m, t, i, j, c;
        a = s.replace(CSS_SELECTOR, function(e, f) {
            return "%" + f
        }).replace(/\s|\>|\+|\~/g, "%").split(/%/g);
        m = {
            _id: 100,
            _class: 10,
            _tag: 1
        };
        t = 0;
        for (i = 0, j = a.length; i < j; i++) {
            c = a[i];
            if ((/#/).test(c)) {
                t += m._id
            } else if ((/\.|\[|\:/).test(c)) {
                t += m._class
            } else if ((/[a-zA-Z]+/).test(c)) {
                t += m._tag
            }
        }
        return t
    }
    function filterDuplicates(m, c, t) {
        var a = [],
            e, s = (t ? "ordinal" : "flex") + "Specificity",
            i, j, x, k, l, f;
        for (i = 0, j = m.length; i < j; i++) {
            x = m[i];
            if ((!t && x.flex) || (t && x["ordinal-group"])) {
                x[s] = x[s] || calculateSpecificity(x.selector);
                e = FALSE;
                for (k = 0, l = a.length; k < l; k++) {
                    f = a[k];
                    if (f.match === x.match) {
                        if (f[s] < x[s]) {
                            a[j] = x
                        }
                        e = TRUE;
                        return FALSE
                    }
                }
                if (!e) {
                    a.push(x)
                }
            }
        }
        return a
    }
    function createMatchMatrix(m, c, t) {
        var g = {}, d = [],
            e = 0,
            f, o = "ordinal-group",
            B = "data-" + o,
            i, j, h, k, l, x, n;
        m = filterDuplicates(m, c, t);
        for (i = 0, j = c.length; i < j; i++) {
            h = c[i];
            for (k = 0, l = m.length; k < l; k++) {
                x = m[k];
                if (t) {
                    f = x[o] || "1";
                    if (x.match === h) {
                        x.match.setAttribute(B, f);
                        g[f] = g[f] || [];
                        g[f].push(x)
                    }
                } else {
                    f = x.flex || "0";
                    if (x.match === h && (!x[f] || (x[f] && parseInt(x[f], 10) <= 1))) {
                        e += parseInt(f, 10);
                        g[f] = g[f] || [];
                        g[f].push(x)
                    }
                }
            }
            if (t && !h.getAttribute(B)) {
                f = "1";
                h.setAttribute(B, f);
                g[f] = g[f] || [];
                g[f].push({
                    match: h
                })
            }
        }
        for (n in g) {
            if (g.hasOwnProperty(n)) {
                d.push(n)
            }
        }
        d.sort(function(a, b) {
            return b - a
        });
        return {
            keys: d,
            groups: g,
            total: e
        }
    }
    function attachResizeListener(c, p) {
        if (!RESIZE_LISTENER) {
            var s, a, b, d, e = doc.body,
                f = doc.documentElement,
                r, i = "innerWidth",
                g = "innerHeight",
                h = "clientWidth",
                j = "clientHeight";
            addEvent("resize", function() {
                if (r) {
                    window.clearTimeout(r)
                }
                r = window.setTimeout(function() {
                    b = win[i] || f[i] || f[h] || e[h];
                    d = win[g] || f[g] || f[j] || e[j];
                    if (s !== b || a !== d) {
                        FLX.updateInstance(NULL, NULL);
                        s = b;
                        a = d
                    }
                }, 250)
            });
            RESIZE_LISTENER = TRUE
        }
    }
    function cleanPositioningProperties(c) {
        var i, j, k, w, h;
        for (i = 0, j = c.length; i < j; i++) {
            k = c[i];
            w = k.style.width;
            h = k.style.height;
            k.style.cssText = EMPTY_STRING;
            k.style.width = w;
            k.style.height = h
        }
    }
    function sanitizeChildren(t, n) {
        var c = [],
            a, i, j;
        for (i = 0, j = n.length; i < j; i++) {
            a = n[i];
            if (a) {
                switch (a.nodeName.toLowerCase()) {
                    case "script":
                    case "style":
                    case "link":
                        break;
                    default:
                        if (a.nodeType === 1) {
                            c.push(a)
                        } else if ((a.nodeType === 3) && (a.isElementContentWhitespace || (ONLY_WHITESPACE).test(a.data))) {
                            t.removeChild(a);
                            i--
                        }
                        break
                }
            }
        }
        return c
    }
    function parentFlex(t) {
        var a = 0,
            p = t.parentNode,
            o, m, i;
        while (p.FLX_DOM_ID) {
            o = FLEX_BOXES[p.FLX_DOM_ID];
            m = createMatchMatrix(o.children, sanitizeChildren(p, p.childNodes), NULL);
            a += m.total;
            i = TRUE;
            p = p.parentNode
        }
        return {
            nested: i,
            flex: a
        }
    }
    function dimensionValues(t, p) {
        var a = t.parentNode,
            o, d, i, j, r;
        if (a.FLX_DOM_ID) {
            o = FLEX_BOXES[a.FLX_DOM_ID];
            for (i = 0, j = o.properties.length; i < j; i++) {
                r = o.properties[i];
                if ((new RegExp(p)).test(r.property)) {
                    d = TRUE;
                    return FALSE
                }
            }
        }
        return d
    }
    function updateChildValues(p) {
        var i, j, x;
        if (p.flexMatrix) {
            for (i = 0, j = p.children.length; i < j; i++) {
                x = p.children[i];
                x.flex = p.flexMatrix[i]
            }
        }
        if (p.ordinalMatrix) {
            for (i = 0, j = p.children.length; i < j; i++) {
                x = p.children[i];
                x["ordinal-group"] = p.ordinalMatrix[i]
            }
        }
        return p
    }
    function ensureStructuralIntegrity(p, i) {
        var t = p.target;
        if (!t.FLX_DOM_ID) {
            t.FLX_DOM_ID = t.FLX_DOM_ID || (++FLX_DOM_ID)
        }
        if (!p.nodes) {
            p.nodes = sanitizeChildren(t, t.childNodes)
        }
        if (!p.selector) {
            p.selector = buildSelector(t);
            t.setAttribute(FLX_PARENT_ATTR, TRUE)
        }
        if (!p.properties) {
            p.properties = []
        }
        if (!p.children) {
            p.children = matchFlexChildren(t, LIBRARY, sanitizeChildren(t, t.childNodes))
        }
        if (!p.nested) {
            p.nested = p.selector + " [" + FLX_PARENT_ATTR + "]"
        }
        p.target = t;
        p._instance = i;
        return p
    }
    selectivizrEngine = (function() {
        var R = /(\/\*[^*]*\*+([^\/][^*]*\*+)*\/)\s*?/g,
            a = /@import\s*(?:(?:(?:url\(\s*(['"]?)(.*)\1)\s*\))|(?:(['"])(.*)\3))\s*([^;]*);/g,
            b = /(behavior\s*?:\s*)?\burl\(\s*(["']?)(?!data:)([^"')]+)\2\s*\)/g,
            c = /((?:^|(?:\s*\})+)(?:\s*@media[^\{]+\{)?)\s*([^\{]*?[\[:][^{]+)/g,
            d = /([(\[+~])\s+/g,
            f = /\s+([)\]+~])/g,
            g = /\s+/g,
            h = /^\s*((?:[\S\s]*\S)?)\s*$/;

        function t(e) {
            return e.replace(h, PLACEHOLDER_STRING)
        }
        function n(e) {
            return t(e).replace(g, SPACE_STRING)
        }
        function k(e) {
            return n(e.replace(d, PLACEHOLDER_STRING).replace(f, PLACEHOLDER_STRING))
        }
        function p(e) {
            return e.replace(c, function(m, u, v) {
                var w, x, i, j, y;
                w = v.split(",");
                for (i = 0, j = w.length; i < j; i++) {
                    y = w[i];
                    x = k(y) + SPACE_STRING
                }
                return u + w.join(",")
            })
        }
        function l() {
            if (win.XMLHttpRequest) {
                return new win.XMLHttpRequest()
            }
            try {
                return new win.ActiveXObject("Microsoft.XMLHTTP")
            } catch (e) {
                return NULL
            }
        }
        function o(e) {
            var i = /<style[^<>]*>([^<>]*)<\/style[\s]?>/img,
                m = i.exec(e),
                j = [],
                u;
            while (m) {
                u = m[1];
                if (u) {
                    j.push(u)
                }
                m = i.exec(e)
            }
            return j.join("\n\n")
        }
        function q(u) {
            var x = l(),
                e;
            x.open("GET", u, FALSE);
            x.send();
            e = (x.status === 200) ? x.responseText : EMPTY_STRING;
            if (u === window.location.href) {
                e = o(e)
            }
            return e
        }
        function r(u, e) {
            if (!u) {
                return
            }
            function i(u) {
                return u.substring(0, u.indexOf("/", 8))
            }
            if (PROTOCOL.test(u)) {
                return i(e) === i(u) ? u : NULL
            }
            if (u.charAt(0) === "/") {
                return i(e) + u
            }
            var j = e.split("?")[0];
            if (u.charAt(0) !== "?" && j.charAt(j.length - 1) !== "/") {
                j = j.substring(0, j.lastIndexOf("/") + 1)
            }
            return j + u
        }
        function s(u) {
            if (u) {
                return q(u).replace(R, EMPTY_STRING).replace(a, function(m, e, i, j, v, w) {
                    var x = s(r(i || v, u));
                    return (w) ? "@media " + w + " {" + x + "}" : x
                }).replace(b, function(m, i, e, j) {
                    e = e || EMPTY_STRING;
                    return i ? m : " url(" + e + r(j, u, true) + e + ") "
                })
            }
            return EMPTY_STRING
        }
        return function() {
            var u, e = [],
                m, i, j, v = doc.getElementsByTagName("BASE"),
                w = (v.length > 0) ? v[0].href : doc.location.href,
                x = doc.styleSheets,
                y, z, A;
            for (i = 0, j = x.length; i < j; i++) {
                m = x[i];
                if (m != NULL) {
                    e.push(m)
                }
            }
            e.push(window.location);
            for (i = 0, j = e.length; i < j; i++) {
                m = e[i];
                if (m) {
                    u = r(m.href, w);
                    if (u) {
                        y = p(s(u))
                    }
                    if (y) {
                        z = buildSelectorTree(y);
                        A = findFlexboxElements(z)
                    }
                }
            }
            buildFlexieCall(A)
        }
    }());
    FLX.box = function(p) {
        return this.renderModel(p)
    };
    FLX.box.prototype = {
        properties: {
            boxModel: function(t, c, p) {
                var s, a, b, g, i, j, d;
                t.style.display = "block";
                if (BROWSER.IE === 8) {
                    t.style.overflow = "hidden"
                }
                if (!p.cleared) {
                    s = p.selector.split(/\s?,\s?/);
                    a = doc.styleSheets;
                    a = a[a.length - 1];
                    b = "padding-top:" + (getComputedStyle(t, PADDING_TOP, NULL) || "0.1px;");
                    g = ["content: '.'", "display: block", "height: 0", "overflow: hidden"].join(";");
                    for (i = 0, j = s.length; i < j; i++) {
                        d = s[i];
                        if (a.addRule) {
                            if (BROWSER.IE < 8) {
                                t.style.zoom = "1";
                                if (BROWSER.IE === 6) {
                                    a.addRule(d.replace(/\>|\+|\~/g, ""), b + "zoom:1;", 0)
                                } else if (BROWSER.IE === 7) {
                                    a.addRule(d, b + "display:inline-block;", 0)
                                }
                            } else {
                                a.addRule(d, b, 0);
                                a.addRule(d + ":before", g, 0);
                                a.addRule(d + ":after", g + ";clear:both;", 0)
                            }
                        } else if (a.insertRule) {
                            a.insertRule(d + "{" + b + "}", 0);
                            a.insertRule(d + ":after{" + g + ";clear:both;}", 0)
                        }
                    }
                    p.cleared = TRUE
                }
            },
            boxDirection: function(t, c, p) {
                var n, a, i, j, k, b;
                if ((p.direction === "reverse" && !p.reversed) || (p.direction === "normal" && p.reversed)) {
                    c = c.reverse();
                    for (i = 0, j = c.length; i < j; i++) {
                        k = c[i];
                        t.appendChild(k)
                    }
                    n = LIBRARY(p.nested);
                    for (i = 0, j = n.length; i < j; i++) {
                        b = n[i];
                        a = FLEX_BOXES[b.FLX_DOM_ID];
                        if (a && a.direction === INHERIT) {
                            a.direction = p.direction
                        }
                    }
                    p.reversed = !p.reversed
                }
            },
            boxOrient: function(t, c, p) {
                var s = this,
                    w, h, i, j, k;
                w = {
                    pos: "marginLeft",
                    opp: "marginRight",
                    dim: "width",
                    out: "offsetWidth",
                    main: "width",
                    func: clientWidth,
                    pad: [PADDING_LEFT, PADDING_RIGHT, BORDER_LEFT, BORDER_RIGHT]
                };
                h = {
                    pos: "marginTop",
                    opp: "marginBottom",
                    dim: "height",
                    out: "offsetHeight",
                    main: "height",
                    func: clientHeight,
                    pad: [PADDING_TOP, PADDING_BOTTOM, BORDER_TOP, BORDER_BOTTOM]
                };
                if (!SUPPORT) {
                    for (i = 0, j = c.length; i < j; i++) {
                        k = c[i];
                        k.style[(BROWSER.IE >= 9) ? "cssFloat" : "styleFloat"] = LEFT;
                        if (p.orient === VERTICAL || p.orient === BLOCK_AXIS) {
                            k.style.clear = LEFT
                        }
                        if (BROWSER.IE === 6) {
                            k.style.display = "inline"
                        }
                    }
                }
                switch (p.orient) {
                    case VERTICAL:
                    case BLOCK_AXIS:
                        s.props = h;
                        s.anti = w;
                        break;
                    default:
                        s.props = w;
                        s.anti = h;
                        break
                }
            },
            boxOrdinalGroup: function(t, c, p) {
                var o, m;
                if (!c.length) {
                    return
                }
                o = function(m) {
                    var a = m.keys,
                        b = p.reversed ? a : a.reverse(),
                        i, j, d, k, l, e;
                    for (i = 0, j = b.length; i < j; i++) {
                        d = b[i];
                        for (k = 0, l = c.length; k < l; k++) {
                            e = c[k];
                            if (d === e.getAttribute("data-ordinal-group")) {
                                t.appendChild(e)
                            }
                        }
                    }
                };
                m = createMatchMatrix(p.children, c, TRUE);
                if (m.keys.length > 1) {
                    o(m)
                }
            },
            boxFlex: function(t, c, p) {
                var s = this,
                    a, f, d, b, r, w, e;
                if (!c.length) {
                    return
                }
                a = function(b) {
                    var g = b.groups,
                        h = b.keys,
                        o, i, j, q, k, l, x, m, n, u;
                    for (i = 0, j = h.length; i < j; i++) {
                        q = h[i];
                        for (k = 0, l = g[q].length; k < l; k++) {
                            x = g[q][k];
                            o = NULL;
                            if (x.properties !== undefined && x.properties.length !== undefined) {
                                for (m = 0, n = x.properties.length; m < n; m++) {
                                    u = x.properties[m];
                                    if ((RESTRICTIVE_PROPERTIES).test(u.property)) {
                                        o = parseFloat(u.value)
                                    }
                                }
                            }
                            if (!o || x.match[s.props.out] > o) {
                                appendPixelValue(x.match, s.props.pos, NULL)
                            }
                        }
                    }
                };
                f = function(b) {
                    var g = 0,
                        w, h, i, j, m, k, l, n;
                    for (i = 0, j = c.length; i < j; i++) {
                        m = c[i];
                        g += getComputedStyle(m, s.props.dim, TRUE);
                        for (k = 0, l = s.props.pad.length; k < l; k++) {
                            n = s.props.pad[k];
                            g += getComputedStyle(m, n, TRUE)
                        }
                        g += getComputedStyle(m, s.props.pos, TRUE);
                        g += getComputedStyle(m, s.props.opp, TRUE)
                    }
                    w = Math.floor(parseFloat(window.getComputedStyle(t, null)[s.props.main])) - g;
                    h = (w / b.total);
                    return {
                        whitespace: w,
                        ration: h
                    }
                };
                d = function(b, w) {
                    var g = b.groups,
                        h = b.keys,
                        m, n, o = w.ration,
                        q, u, v, i, j, y, k, l, x;
                    for (i = 0, j = h.length; i < j; i++) {
                        y = h[i];
                        q = (o * y);
                        var z = 0;
                        for (k = 0, l = g[y].length; k < l; k++) {
                            x = g[y][k];
                            if (x.match) {
                                m = x.match.getAttribute("data-flex");
                                n = x.match.getAttribute("data-specificity");
                                if (!m || (n <= x.flexSpecificity)) {
                                    x.match.setAttribute("data-flex", y);
                                    x.match.setAttribute("data-specificity", x.flexSpecificity);
                                    u = getComputedStyle(x.match, s.props.dim, TRUE);
                                    v = Math.max(0, (u + q));
                                    z += q;
                                    if (k == l - 1) v--;
                                    appendPixelValue(x.match, s.props.dim, v)
                                }
                            }
                        }
                    }
                };
                b = createMatchMatrix(p.children, c, NULL);
                if (b.total) {
                    p.hasFlex = TRUE;
                    r = a(b);
                    w = f(b);
                    e = d(b, w)
                }
            },
            boxAlign: function(t, c, p) {
                var s = this,
                    a, b, f = parentFlex(t),
                    i, j, d, k, l, e;
                if (!SUPPORT && !f.flex && (p.orient === VERTICAL || p.orient === BLOCK_AXIS)) {
                    if (!dimensionValues(t, s.anti.dim)) {
                        appendPixelValue(t, s.anti.dim, NULL)
                    }
                    appendPixelValue(c, s.anti.dim, NULL)
                }
                a = t[s.anti.out];
                for (i = 0, j = s.anti.pad.length; i < j; i++) {
                    d = s.anti.pad[i];
                    a -= getComputedStyle(t, d, TRUE)
                }
                switch (p.align) {
                    case "start":
                        break;
                    case "end":
                        for (i = 0, j = c.length; i < j; i++) {
                            e = c[i];
                            b = a - e[s.anti.out];
                            b -= getComputedStyle(e, s.anti.opp, TRUE);
                            appendPixelValue(e, s.anti.pos, b)
                        }
                        break;
                    case "center":
                        for (i = 0, j = c.length; i < j; i++) {
                            e = c[i];
                            b = (a - e[s.anti.out]) / 2;
                            appendPixelValue(e, s.anti.pos, b)
                        }
                        break;
                    default:
                        for (i = 0, j = c.length; i < j; i++) {
                            e = c[i];
                            switch (e.nodeName.toLowerCase()) {
                                case "button":
                                case "input":
                                case "select":
                                    break;
                                default:
                                    var g = 0;
                                    for (k = 0, l = s.anti.pad.length; k < l; k++) {
                                        d = s.anti.pad[k];
                                        g += getComputedStyle(e, d, TRUE);
                                        g += getComputedStyle(t, d, TRUE)
                                    }
                                    e.style[s.anti.dim] = "100%";
                                    b = e[s.anti.out] - g;
                                    appendPixelValue(e, s.anti.dim, NULL);
                                    b = a;
                                    b -= getComputedStyle(e, s.anti.pos, TRUE);
                                    for (k = 0, l = s.anti.pad.length; k < l; k++) {
                                        d = s.anti.pad[k];
                                        b -= getComputedStyle(e, d, TRUE)
                                    }
                                    b -= getComputedStyle(e, s.anti.opp, TRUE);
                                    b = Math.max(0, b);
                                    appendPixelValue(e, s.anti.dim, b);
                                    break
                            }
                        }
                        break
                }
            },
            boxPack: function(t, c, p) {
                var s = this,
                    g = 0,
                    f = 0,
                    a = 0,
                    b, d, e, r, l = c.length - 1,
                    k, i, j, v, h;
                for (i = 0, j = c.length; i < j; i++) {
                    k = c[i];
                    g += k[s.props.out];
                    g += getComputedStyle(k, s.props.pos, TRUE);
                    g += getComputedStyle(k, s.props.opp, TRUE)
                }
                f = getComputedStyle(c[0], s.props.pos, TRUE);
                b = Math.floor(parseFloat(window.getComputedStyle(t, null)[s.props.main])) - g;
                if (b < 0) {
                    b = 0
                }
                switch (p.pack) {
                    case "end":
                        appendPixelValue(c[0], s.props.pos, a + f + b);
                        break;
                    case "center":
                        if (a) {
                            a /= 2
                        }
                        appendPixelValue(c[0], s.props.pos, a + f + (b / 2));
                        break;
                    case "justify":
                        d = Math.floor((a + b) / l);
                        r = (d * l) - b;
                        i = c.length - 1;
                        while (i) {
                            k = c[i];
                            e = d;
                            if (r) {
                                e++;
                                r++
                            }
                            v = getComputedStyle(k, s.props.pos, TRUE) + e - 1;
                            appendPixelValue(k, s.props.pos, v);
                            i--
                        }
                        break
                }
                t.style.overflow = ""
            }
        },
        setup: function(t, c, p) {
            var s = this,
                m, f, k, a;
            if (!t || !c || !p) {
                return
            }
            if (SUPPORT && SUPPORT.partialSupport) {
                m = createMatchMatrix(p.children, c, NULL);
                f = parentFlex(t);
                c = sanitizeChildren(t, t.childNodes);
                s.properties.boxOrient.call(s, t, c, p);
                if (!m.total || !LIBRARY(p.nested).length) {
                    if ((p.align === "stretch") && !SUPPORT.boxAlignStretch && (!f.nested || !f.flex)) {
                        s.properties.boxAlign.call(s, t, c, p)
                    }
                    if ((p.pack === "justify") && !SUPPORT.boxPackJustify && !m.total) {
                        s.properties.boxPack.call(s, t, c, p)
                    }
                }
            } else if (!SUPPORT) {
                for (k in s.properties) {
                    if (s.properties.hasOwnProperty(k)) {
                        a = s.properties[k];
                        a.call(s, t, sanitizeChildren(t, t.childNodes), p)
                    }
                }
            }
        },
        trackDOM: function(p) {
            attachResizeListener(this, p)
        },
        updateModel: function(p) {
            var s = this,
                t = p.target,
                c = p.nodes;
            cleanPositioningProperties(c);
            if (p.flexMatrix || p.ordinalMatrix) {
                p = updateChildValues(p)
            }
            s.setup(t, c, p);
            s.bubbleUp(t, p)
        },
        renderModel: function(p) {
            var s = this,
                t = p.target,
                n = t.childNodes;
            if (!t.length && !n) {
                return false
            }
            p = ensureStructuralIntegrity(p, this);
            s.updateModel(p);
            if (p.dynamic) {
                addFlexboxElement(p)
            }
            win.setTimeout(function() {
                s.trackDOM(p)
            }, 0);
            return s
        },
        bubbleUp: function(t, p) {
            var s = this,
                f, a = p.target.parentNode;
            while (a) {
                f = FLEX_BOXES[a.FLX_DOM_ID];
                if (f) {
                    cleanPositioningProperties(f.nodes);
                    s.setup(f.target, f.nodes, f)
                }
                a = a.parentNode
            }
        }
    };
    FLX.updateInstance = function(t, p) {
        var b, k;
        if (t) {
            b = FLEX_BOXES[t.FLX_DOM_ID];
            if (b && b._instance) {
                b._instance.updateModel(b)
            } else if (!b) {
                b = new FLX.box(p)
            }
        } else {
            for (k in FLEX_BOXES) {
                if (FLEX_BOXES.hasOwnProperty(k)) {
                    b = FLEX_BOXES[k];
                    if (b && b._instance) {
                        b._instance.updateModel(b)
                    }
                }
            }
        }
    };
    FLX.getInstance = function(t) {
        return FLEX_BOXES[t.FLX_DOM_ID]
    };
    FLX.destroyInstance = function(t) {
        var b, d, i, j, x, k;
        d = function(b) {
            b.target.FLX_DOM_ID = NULL;
            b.target.style.cssText = EMPTY_STRING;
            for (i = 0, j = b.children.length; i < j; i++) {
                x = b.children[i];
                x.match.style.cssText = EMPTY_STRING
            }
        };
        if (t) {
            b = FLEX_BOXES[t.FLX_DOM_ID];
            if (b) {
                d(b)
            }
        } else {
            for (k in FLEX_BOXES) {
                if (FLEX_BOXES.hasOwnProperty(k)) {
                    d(FLEX_BOXES[k])
                }
            }
            FLEX_BOXES = []
        }
    };
    FLX.flexboxSupport = function() {
        var p = {}, h = 100,
            c, d = doc.createElement("flxbox"),
            a = '<b style="margin: 0; padding: 0; display:block; width: 10px; height:' + (h / 2) + 'px"></b>',
            t, r, k, v;
        d.style.width = d.style.height = h + "px";
        d.innerHTML = (a + a + a);
        appendProperty(d, "display", "box", NULL);
        appendProperty(d, "box-align", "stretch", TRUE);
        appendProperty(d, "box-pack", "justify", TRUE);
        doc.body.appendChild(d);
        c = d.firstChild.offsetHeight;
        t = {
            boxAlignStretch: function() {
                return (c === 100)
            },
            boxPackJustify: function() {
                var b = 0,
                    i, j;
                for (i = 0, j = d.childNodes.length; i < j; i++) {
                    b += d.childNodes[i].offsetLeft
                }
                return (b === 135)
            }
        };
        for (k in t) {
            if (t.hasOwnProperty(k)) {
                v = t[k];
                r = v();
                if (!r) {
                    p.partialSupport = TRUE
                }
                p[k] = r
            }
        }
        doc.body.removeChild(d);
        return ~(d.style.display).indexOf("box") ? p : FALSE
    };
    FLX.init = function() {
        FLX.flexboxSupported = SUPPORT = FLX.flexboxSupport();
        if ((!SUPPORT || SUPPORT.partialSupport) && LIBRARY) {
            selectivizrEngine()
        }
    };
    FLX.version = "1.0.3";
    return FLX
}(this, document));