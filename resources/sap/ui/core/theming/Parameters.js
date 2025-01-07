﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.theming.Parameters");
jQuery.sap.require("sap.ui.core.Core");
(function() {
    sap.ui.core.theming.Parameters = {};
    var p = null;
    var t = null;

    function r() {
        p = null
    }
    function l(u) {
        var R, o;
        u = u.replace(/\/library([^\/.]*)\.(?:css|less)($|[?#])/, function($, a, b) {
            return "/library-parameters" + a + ".json" + (b ? b : "")
        });
        R = jQuery.sap.sjax({
            url: u,
            dataType: 'json'
        });
        if (R.success) {
            o = (typeof R.data == "string") ? jQuery.parseJSON(R.data) : R.data;
            if (jQuery.isArray(o)) {
                for (var j = 0; j < o.length; j++) {
                    p = jQuery.extend(p, o[j])
                }
            } else {
                p = jQuery.extend(p, o)
            }
        } else {
            jQuery.sap.log.warning("Could not load theme parameters from: " + u)
        }
    }
    function g() {
        if (!p) {
            p = {};
            t = sap.ui.getCore().getConfiguration().getTheme();
            jQuery("link[id^=sap-ui-theme-]").each(function() {
                l(this.href)
            })
        }
        return p
    }
    sap.ui.core.theming.Parameters._addLibraryTheme = function(u) {
        if (p) {
            l(u)
        }
    };
    sap.ui.core.theming.Parameters.get = function(n) {
        if (arguments.length == 1) {
            return g()[n]
        } else if (arguments.length == 0) {
            var c = {};
            return jQuery.extend(c, g())
        } else {
            return undefined
        }
    };
    sap.ui.core.theming.Parameters.reset = function() {
        var o = arguments[0] === true;
        if (!o || sap.ui.getCore().getConfiguration().getTheme() !== t) {
            r()
        }
    }
}());