﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("jquery.sap.ui", false);
jQuery.sap.require("sap.ui.Global");
(function() {
    if (!jQuery.sap) {
        throw "Initialisation of jQuery.sap.ui failed. jQuery.sap plugin required!"
    }
    if (!window.sap || !window.sap.ui) {
        jQuery.sap.fatal("Initialisation of jQuery.sap.ui failed. Global SAP UI namespace required!")
    }
    if (jQuery.sap.ui) {
        return
    }
    function u(i) {
        return sap.ui.getCore().getUIArea(this.id) != null
    }
    function f(i, o) {
        return sap.ui.getCore().getUIArea(this.id)
    }
    function a(c, i) {
        return c.getUIArea().getInterface()
    }
    jQuery.fn.root = function(r) {
        var t;
        if (r) {
            sap.ui.getCore().setRoot(this.get(0), r);
            return this
        }
        var c = this.control();
        if (c.length > 0) {
            return jQuery.map(c, a)
        }
        var U = this.uiarea();
        if (U.length > 0) {
            return U
        }
        this.each(function(i) {
            sap.ui.getCore().createUIArea(this)
        });
        return this
    };
    jQuery.fn.uiarea = function(i) {
        var U = this.slice("[id]").filter(u).map(f).get();
        return typeof(i) === "number" ? U[i] : U
    };

    function b() {
        if (!this || !this.nodeType || this.nodeType === 9) {
            return null
        }
        try {
            var i = jQuery(this).closest("[data-sap-ui]").attr("id");
            return i ? sap.ui.getCore().byId(i) : null
        } catch (e) {
            return null
        }
    }
    jQuery.fn.control = function(i) {
        var c = this.map(b);
        if (i === undefined || isNaN(i)) {
            return c.get()
        } else {
            return c.get(i)
        }
    };
    jQuery.fn.sapui = function(c, i, C) {
        return this.each(function() {
            var o = null;
            if (this) {
                if (c.indexOf(".") == -1) {
                    c = "sap.ui.commons." + c
                }
                var d = jQuery.sap.getObject(c);
                if (d) {
                    if (typeof C == 'object' && typeof C.press == 'function') {
                        C.press = jQuery.proxy(C.press, this)
                    }
                    o = new(d)(i, C);
                    o.placeAt(this)
                }
            }
        })
    }
}());