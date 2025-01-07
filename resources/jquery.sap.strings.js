﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("jquery.sap.strings", false);
(function() {
    jQuery.sap.endsWith = function endsWith(s, e) {
        if (typeof(e) != "string" || e == "") {
            return false
        }
        var p = s.lastIndexOf(e);
        return p >= 0 && p == s.length - e.length
    };
    jQuery.sap.endsWithIgnoreCase = function endsWithIgnoreCase(s, e) {
        if (typeof(e) != "string" || e == "") {
            return false
        }
        s = s.toUpperCase();
        e = e.toUpperCase();
        return jQuery.sap.endsWith(s, e)
    };
    jQuery.sap.startsWith = function startsWith(s, S) {
        if (typeof(S) != "string" || S == "") {
            return false
        }
        if (s == S) {
            return true
        }
        return s.indexOf(S) == 0
    };
    jQuery.sap.startsWithIgnoreCase = function startsWithIgnoreCase(s, S) {
        if (typeof(S) != "string" || S == "") {
            return false
        }
        s = s.toUpperCase();
        S = S.toUpperCase();
        return jQuery.sap.startsWith(s, S)
    };
    jQuery.sap.charToUpperCase = function charToUpperCase(s, p) {
        if (!s) {
            return s
        }
        if (!p || isNaN(p) || p <= 0 || p >= s.length) {
            p = 0
        }
        var C = s.charAt(p).toUpperCase();
        if (p > 0) {
            return s.substring(0, p) + C + s.substring(p + 1)
        }
        return C + s.substring(p + 1)
    };
    jQuery.sap.padLeft = function padLeft(s, p, l) {
        if (!s) {
            s = ""
        }
        while (s.length < l) {
            s = p + s
        }
        return s
    };
    jQuery.sap.padRight = function padRight(s, p, l) {
        if (!s) {
            s = ""
        }
        while (s.length < l) {
            s = s + p
        }
        return s
    };
    var r = /-(.)/ig;
    jQuery.sap.camelCase = function camelCase(s) {
        return s.replace(r, function(m, C) {
            return C.toUpperCase()
        })
    };
    var a = /([A-Z])/g;
    jQuery.sap.hyphen = function hyphen(s) {
        return s.replace(a, function(m, C) {
            return "-" + C.toLowerCase()
        })
    };
    var b = /[-[\]{}()*+?.,\\^$|#\s]/g;
    jQuery.sap.escapeRegExp = function escapeRegExp(s) {
        return s.replace(b, "\\$&")
    };
    jQuery.sap.formatMessage = function formatMessage(p, v) {
        if (arguments.length > 2 || (v != null && !jQuery.isArray(v))) {
            v = Array.prototype.slice.call(arguments, 1)
        }
        v = v || [];
        return p.replace(c, function($, d, e, f, o) {
            if (d) {
                return "'"
            } else if (e) {
                return e.replace(/''/g, "'")
            } else if (f) {
                return String(v[parseInt(f, 10)])
            }
            throw new Error("formatMessage: pattern syntax error at pos. " + o)
        })
    };
    var c = /('')|'([^']+(?:''[^']*)*)(?:'|$)|\{([0-9]+(?:\s*,[^{}]*)?)\}|[{}]/g
}());