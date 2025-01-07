﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("jquery.sap.properties", false);
jQuery.sap.require("jquery.sap.sjax");
(function() {
    var P = function() {
        this.mProperties = {};
        this.aKeys = []
    };
    P.prototype.getProperty = function(k, d) {
        var v = this.mProperties[k];
        if (typeof(v) == "string") {
            return v
        } else if (d) {
            return d
        }
        return null
    };
    P.prototype.getKeys = function() {
        return this.aKeys
    };
    P.prototype.setProperty = function(k, v) {
        if (typeof(v) != "string") {
            return
        }
        if (typeof(this.mProperties[k]) != "string") {
            this.aKeys.push(k)
        }
        this.mProperties[k] = v
    };
    P.prototype.clone = function() {
        var c = new P();
        c.mProperties = jQuery.extend({}, this.mProperties);
        c.aKeys = jQuery.merge([], this.aKeys);
        return c
    };
    var r = /(?:^|\r\n|\r|\n)[ \t\f]*/;
    var a = /(\\u[0-9a-fA-F]{0,4})|(\\.)|(\\$)|([ \t\f]*[ \t\f:=][ \t\f]*)/g;
    var e = {
        '\\f': '\f',
        '\\n': '\n',
        '\\r': '\r',
        '\\t': '\t'
    };

    function p(t, o) {
        var l = t.split(r),
            L, k, v, K, i, m, b;
        o.mProperties = {};
        o.aKeys = [];
        for (i = 0; i < l.length; i++) {
            L = l[i];
            if (L === "" || L.charAt(0) === "#" || L.charAt(0) === "!") {
                continue
            }
            a.lastIndex = b = 0;
            v = "";
            K = true;
            while (m = a.exec(L)) {
                if (b < m.index) {
                    v += L.slice(b, m.index)
                }
                b = a.lastIndex;
                if (m[1]) {
                    if (m[1].length !== 6) {
                        throw new Error("Incomplete Unicode Escape '" + m[1] + "'")
                    }
                    v += String.fromCharCode(parseInt(m[1].slice(2), 16))
                } else if (m[2]) {
                    v += e[m[2]] || m[2].slice(1)
                } else if (m[3]) {
                    L = l[++i];
                    a.lastIndex = b = 0
                } else if (m[4]) {
                    if (K) {
                        K = false;
                        k = v;
                        v = ""
                    } else {
                        v += m[4]
                    }
                }
            }
            if (b < L.length) {
                v += L.slice(b)
            }
            if (K) {
                k = v;
                v = ""
            }
            o.aKeys.push(k);
            o.mProperties[k] = v
        }
        jQuery.sap.unique(o.aKeys)
    }
    jQuery.sap.properties = function properties(m) {
        var o = new P();
        m = jQuery.extend({
            url: undefined,
            headers: {}
        }, m);
        if (typeof(m.url) == "string") {
            var t = jQuery.sap.sjax({
                url: m.url,
                type: 'GET',
                dataType: 'text',
                complexResult: false,
                fallback: undefined,
                headers: m.headers
            });
            if (typeof(t) == "string") {
                p(t, o)
            }
        }
        return o
    }
}());