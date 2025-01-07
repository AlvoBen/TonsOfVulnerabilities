﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("jquery.sap.storage", false);
(function() {
    var s = !! (window.JSON && JSON.parse && JSON.stringify);
    var S = "state.key_";
    var f = function(a, b) {
        var t = "unknown";
        var P = b || S;
        P += "-";
        var o;
        if (!o || typeof(a) === "string") {
            t = a || "session";
            try {
                o = window[t + "Storage"]
            } catch (e) {
                o = null
            }
        } else if (typeof(a) === Object) {
            t = a.getType ? a.getType() : "unknown";
            o = a
        }
        var c = !! o;
        this.put = function(i, d) {
            if (c && i) {
                try {
                    o.setItem(P + i, s ? JSON.stringify(d) : d);
                    return true
                } catch (e) {
                    return false
                }
            } else {
                return false
            }
        };
        this.get = function(i) {
            if (c && i) {
                try {
                    var I = o.getItem(P + i);
                    return s ? JSON.parse(I) : I
                } catch (e) {
                    return null
                }
            } else {
                return null
            }
        };
        this.remove = function(i) {
            if (c && i) {
                try {
                    o.removeItem(P + i);
                    return true
                } catch (e) {
                    return false
                }
            } else {
                return false
            }
        };
        this.removeAll = function(I) {
            if (c && o.length && typeof(o.key) === "function") {
                try {
                    var l = o.length;
                    var k = [];
                    var d, i;
                    var p = P + (I || "");
                    for (i = 0; i < l; i++) {
                        d = o.key(i);
                        if (d && d.indexOf(p) == 0) {
                            k.push(d)
                        }
                    }
                    for (i = 0; i < k.length; i++) {
                        o.removeItem(k[i])
                    }
                    return true
                } catch (e) {
                    return false
                }
            } else {
                return false
            }
        };
        this.clear = function() {
            if (c) {
                try {
                    o.clear();
                    return true
                } catch (e) {
                    return false
                }
            } else {
                return false
            }
        };
        this.getType = function() {
            return t
        }
    };
    var m = {};
    jQuery.sap.storage = function(o, i) {
        if (!o) {
            o = jQuery.sap.storage.Type.session
        }
        if (typeof(o) === "string" && jQuery.sap.storage.Type[o]) {
            var k = o;
            if (i && i != S) {
                k = o + "_" + i
            }
            return m[k] || (m[k] = new f(o, i))
        }
        return new f(o, i)
    };
    jQuery.sap.storage.Type = {
        local: "local",
        session: "session",
        global: "global"
    };
    f.apply(jQuery.sap.storage);
    m[jQuery.sap.storage.Type.session] = jQuery.sap.storage
}());