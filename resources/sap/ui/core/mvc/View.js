﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.mvc.View");
jQuery.sap.require("sap.ui.core.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.core.mvc.View", {
    metadata: {
        publicMethods: ["getController"],
        library: "sap.ui.core",
        properties: {
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '100%'
            },
            "height": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: null
            },
            "viewName": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "displayBlock": {
                type: "boolean",
                group: "Appearance",
                defaultValue: false
            }
        },
        aggregations: {
            "content": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "content"
            }
        },
        events: {
            "afterInit": {},
            "beforeExit": {},
            "afterRendering": {},
            "beforeRendering": {}
        }
    }
});
sap.ui.core.mvc.View.M_EVENTS = {
    'afterInit': 'afterInit',
    'beforeExit': 'beforeExit',
    'afterRendering': 'afterRendering',
    'beforeRendering': 'beforeRendering'
};
(function() {
    sap.ui.core.mvc.View.prototype._initCompositeSupport = function(s) {
        this.oViewData = s.viewData;
        this.sViewName = s.viewName;
        if (this.initViewSettings) {
            this.initViewSettings(s)
        }
        c(this, s);
        if (this.onControllerConnected) {
            this.onControllerConnected(this.oController)
        }
        this.fireAfterInit()
    };
    sap.ui.core.mvc.View.prototype.getController = function() {
        return this.oController
    };
    sap.ui.core.mvc.View.prototype.byId = function(i) {
        return sap.ui.getCore().byId(this.createId(i))
    };
    sap.ui.core.mvc.View.prototype.createId = function(i) {
        if (!this.isPrefixedId(i)) {
            i = this.getId() + "--" + i
        }
        return i
    };
    sap.ui.core.mvc.View.prototype.isPrefixedId = function(i) {
        return (i && i.indexOf(this.getId() + "--") === 0)
    };
    var c = function(t, s) {
        var C = s.controller;
        if (!C && t.getControllerName) {
            var d = t.getControllerName();
            if (d) {
                C = sap.ui.controller(d)
            }
        }
        if (sap.ui.getCore().getConfiguration().getDesignMode() && !sap.ui.getCore().getConfiguration().getSuppressDeactivationOfControllerCode()) {
            for (var m in C) {
                if (typeof C[m] === "function" && !sap.ui.core.mvc.Controller.prototype[m]) {
                    C[m] = function() {}
                }
            }
        }
        if (C) {
            t.oController = C;
            C.connectToView(t)
        }
    };
    sap.ui.core.mvc.View.prototype.getViewData = function() {
        return this.oViewData
    };
    sap.ui.core.mvc.View.prototype.exit = function() {
        this.fireBeforeExit();
        this.oController = null
    };
    sap.ui.core.mvc.View.prototype.onAfterRendering = function() {
        this.fireAfterRendering()
    };
    sap.ui.core.mvc.View.prototype.onBeforeRendering = function() {
        this.fireBeforeRendering()
    };
    sap.ui.core.mvc.View.prototype.clone = function(i, l) {
        var s = {}, k, C;
        for (k in this.mProperties && !(this.isBound && this.isBound(k))) {
            if (this.mProperties.hasOwnProperty(k)) {
                s[k] = this.mProperties[k]
            }
        }
        C = sap.ui.core.Control.prototype.clone.call(this, i, l, {
            cloneChildren: false,
            cloneBindings: true
        });
        C.applySettings(s);
        return C
    };
    sap.ui.view = function(i, v, t) {
        var a = null,
            V = {};
        if (typeof i === "object" || typeof i === "string" && v === undefined) {
            v = i;
            i = undefined
        }
        if (v) {
            if (typeof v === "string") {
                V.viewName = v
            } else {
                V = v
            }
        }
        if (i) {
            V.id = i
        }
        if (t) {
            V.type = t
        }
        if (sap.ui.core.CustomizingConfiguration) {
            var b = sap.ui.core.CustomizingConfiguration.getViewReplacement(V.viewName);
            if (b) {
                jQuery.sap.log.info("Customizing: View replacement for view '" + V.viewName + "' found and applied: " + b.viewName + " (type: " + b.type + ")");
                jQuery.extend(V, b)
            }
        }
        if (!V.type) {
            throw new Error("No view type specified.")
        } else if (V.type === sap.ui.core.mvc.ViewType.JS) {
            a = new sap.ui.core.mvc.JSView(V)
        } else if (V.type === sap.ui.core.mvc.ViewType.JSON) {
            a = new sap.ui.core.mvc.JSONView(V)
        } else if (V.type === sap.ui.core.mvc.ViewType.XML) {
            a = new sap.ui.core.mvc.XMLView(V)
        } else if (V.type === sap.ui.core.mvc.ViewType.HTML) {
            a = new sap.ui.core.mvc.HTMLView(V)
        } else if (V.type === sap.ui.core.mvc.ViewType.Template) {
            a = new sap.ui.core.mvc.TemplateView(V)
        } else {
            throw new Error("Unknown view type " + V.type + " specified.")
        }
        return a
    }
}());