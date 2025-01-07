﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.mvc.Controller");
jQuery.sap.require("sap.ui.base.EventProvider");
(function() {
    var r = {};
    sap.ui.base.EventProvider.extend("sap.ui.core.mvc.Controller", {
        constructor: function(n) {
            var t = null;
            if (typeof(n) == "string") {
                if (!r[n]) {
                    jQuery.sap.require({
                        modName: n,
                        type: "controller"
                    });
                    if (!r[n]) {
                        throw new Error("Controller type " + n + " is undefined.")
                    }
                }
                t = r[n]
            }
            sap.ui.base.EventProvider.apply(this, arguments);
            if (t) {
                jQuery.extend(this, r[n])
            }
        }
    });
    var c = {
        "onInit": true,
        "onExit": false,
        "onBeforeRendering": false,
        "onAfterRendering": true
    };

    function e(C, n) {
        var o;
        if (sap.ui.core.CustomizingConfiguration) {
            var a = sap.ui.core.CustomizingConfiguration.getControllerExtension(n);
            if (a) {
                var s = a.controllerName;
                jQuery.sap.log.info("Customizing: Controller '" + n + "' is now extended by '" + s + "'");
                if (!r[s] && !jQuery.sap.getObject(s)) {
                    jQuery.sap.require({
                        modName: s,
                        type: "controller"
                    })
                }
                if (o = r[s]) {
                    for (var m in o) {
                        if (c[m] !== undefined) {
                            var O = C[m];
                            if (O && typeof O === "function") {
                                (function(m, O) {
                                    C[m] = function() {
                                        if (c[m]) {
                                            O.apply(C, arguments);
                                            o[m].apply(C, arguments)
                                        } else {
                                            o[m].apply(C, arguments);
                                            O.apply(C, arguments)
                                        }
                                    }
                                })(m, O)
                            } else {
                                C[m] = o[m]
                            }
                        } else {
                            C[m] = o[m]
                        }
                    }
                    return C
                } else {}
            }
        }
        return C
    }
    sap.ui.controller = function(n, C) {
        if (!n) {
            throw new Error("Controller name ('sName' parameter) is required")
        }
        if (!C) {
            if (!r[n] && !jQuery.sap.getObject(n)) {
                jQuery.sap.require({
                    modName: n,
                    type: "controller"
                })
            }
            if (r[n]) {
                var o = new sap.ui.core.mvc.Controller(n);
                o = e(o, n);
                return o
            } else {
                var a = jQuery.sap.getObject(n);
                if (typeof a === "function" && a.prototype instanceof sap.ui.core.mvc.Controller) {
                    var o = new a();
                    o = e(o, n);
                    return o
                }
            }
            throw new Error("Controller " + n + " couldn't be instantiated")
        } else {
            r[n] = C
        }
    };
    sap.ui.core.mvc.Controller.prototype.getView = function() {
        return this.oView
    };
    sap.ui.core.mvc.Controller.prototype.byId = function(i) {
        return this.oView ? this.oView.byId(i) : undefined
    };
    sap.ui.core.mvc.Controller.prototype.createId = function(i) {
        return this.oView ? this.oView.createId(i) : undefined
    };
    sap.ui.core.mvc.Controller.prototype.connectToView = function(v) {
        this.oView = v;
        if (this.onInit) {
            v.attachAfterInit(this.onInit, this)
        }
        if (this.onExit) {
            v.attachBeforeExit(this.onExit, this)
        }
        if (this.onAfterRendering) {
            v.attachAfterRendering(this.onAfterRendering, this)
        }
        if (this.onBeforeRendering) {
            v.attachBeforeRendering(this.onBeforeRendering, this)
        }
    }
}());