﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.CustomizingConfiguration");
jQuery.sap.require("sap.ui.core.Core");
(function() {
    var C = "sap.ui.viewReplacements",
        a = "sap.ui.viewExtensions",
        b = "sap.ui.viewModifications",
        c = "sap.ui.controllerExtensions";
    var m = {};

    function f(t, d) {
        jQuery.each(m, function(n, o) {
            if (o && o[t]) {
                if (d(o[t])) {
                    return false
                }
            }
        })
    };
    sap.ui.core.CustomizingConfiguration = {
        log: function() {
            if (window.console) {
                window.console.log(m)
            }
        },
        activateForComponent: function(s) {
            jQuery.sap.log.info("CustomizingConfiguration: activateForComponent('" + s + "')");
            var F = s + ".Component";
            jQuery.sap.require(F);
            var o = jQuery.sap.getObject(F).getMetadata().getCustomizing();
            m[s] = o
        },
        deactivateForComponent: function(s) {
            jQuery.sap.log.info("CustomizingConfiguration: deactivateForComponent('" + s + "')");
            delete m[s]
        },
        getViewReplacement: function(v) {
            var r = undefined;
            f(C, function(o) {
                r = o[v];
                return !!r
            });
            return r
        },
        getViewExtension: function(v, e) {
            var r = undefined;
            f(a, function(o) {
                r = o[v] && o[v][e];
                return !!r
            });
            return r
        },
        getControllerExtension: function(s) {
            var r = undefined;
            f(c, function(o) {
                r = o[s];
                return !!r
            });
            return r
        },
        getCustomProperties: function(v, s) {
            var S = {};
            f(b, function(o) {
                var d = o[v] && o[v][s];
                var u = {};
                if (d) {
                    jQuery.each(d, function(n, V) {
                        if (n === "visible") {
                            u[n] = V;
                            jQuery.sap.log.info("Customizing: custom value for property '" + n + "' of control '" + s + "' in View '" + v + "' applied: " + V)
                        } else {
                            jQuery.sap.log.warning("Customizing: custom value for property '" + n + "' of control '" + s + "' in View '" + v + "' ignored: only the 'visible' property can be customized.")
                        }
                    });
                    jQuery.extend(S, u)
                }
            });
            return S
        }
    };
    if (sap.ui.getCore().getConfiguration().getDisableCustomizing()) {
        jQuery.sap.log.info("CustomizingConfiguration: disabling Customizing now");
        jQuery.each(sap.ui.core.CustomizingConfiguration, function(n, A) {
            if (typeof A === "function") {
                sap.ui.core.CustomizingConfiguration[n] = function() {}
            }
        })
    }
}());