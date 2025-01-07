﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.routing.Route");
jQuery.sap.require("sap.ui.base.EventProvider");
jQuery.sap.require("sap.ui.thirdparty.signals");
jQuery.sap.require("sap.ui.thirdparty.crossroads");
(function() {
    sap.ui.base.EventProvider.extend("sap.ui.core.routing.Route", {
        constructor: function(r, c, p) {
            if (!c.name) {
                jQuery.sap.log.error("A name has to be specified for every route")
            }
            var t = this,
                R = c.pattern;
            if (!jQuery.isArray(R)) {
                R = [R]
            }
            if (jQuery.isArray(c.subroutes)) {
                var s = c.subroutes;
                c.subroutes = {};
                jQuery.each(s, function(S, o) {
                    c.subroutes[o.name] = o
                })
            }
            this._aPattern = [];
            this._aRoutes = [];
            this._oParent = p;
            this._oConfig = c;
            if (c.subroutes) {
                jQuery.each(c.subroutes, function(a, S) {
                    if (S.name == undefined) {
                        S.name = a
                    }
                    r.addRoute(S, t)
                })
            }
            if (c.pattern === undefined) {
                return
            }
            jQuery.each(R, function(i, a) {
                t._aPattern[i] = a;
                t._aRoutes[i] = r._oRouter.addRoute(a);
                t._aRoutes[i].matched.add(function() {
                    var A = {};
                    jQuery.each(arguments, function(b, d) {
                        A[t._aRoutes[i]._paramsIds[b]] = d
                    });
                    t._routeMatched(r, A, true)
                })
            })
        },
        metadata: {
            publicMethods: ["getURL", "getPattern"]
        }
    });
    sap.ui.core.routing.Route.prototype.getURL = function(p) {
        return this._aRoutes[0].interpolate(p)
    };
    sap.ui.core.routing.Route.prototype.getPattern = function() {
        return this._aPattern[0]
    };
    sap.ui.core.routing.Route.prototype._routeMatched = function(r, a, i) {
        var v, p, t, T;
        if (this._oParent) {
            p = this._oParent._routeMatched(r, a);
            t = p.oTargetParent;
            T = p.oTargetControl
        }
        var c = jQuery.extend({}, r._oConfig, this._oConfig);
        if ((T || c.targetControl) && c.targetAggregation) {
            if (!t) {
                if (c.targetParent) {
                    T = sap.ui.getCore().byId(c.targetParent).byId(c.targetControl)
                }
            } else {
                if (c.targetControl) {
                    T = t.byId(c.targetControl)
                }
            }
            if (!T) {
                T = sap.ui.getCore().byId(c.targetControl)
            }
            if (T) {
                var A = T.getMetadata().getJSONKeys()[c.targetAggregation];
                if (A) {
                    var V = c.view;
                    if (c.viewPath) {
                        V = c.viewPath + "." + V
                    }
                    v = r.getView(V, c.viewType);
                    if (c.clearTarget === true) {
                        T[A._sRemoveAllMutator]()
                    }
                    T[A._sMutator](v)
                } else {
                    jQuery.sap.log.error("Control " + c.targetControl + " does not has an aggregation called " + c.targetAggregation)
                }
            } else {
                jQuery.sap.log.error("Control with ID " + c.targetControl + " could not be found")
            }
        }
        if (c.callback) {
            c.callback(this, a, c, T, v)
        }
        r.fireRouteMatched({
            name: c.name,
            arguments: a,
            targetControl: T,
            view: v,
            config: c
        });
        if (i) {
            r.fireRoutePatternMatched({
                name: c.name,
                arguments: a,
                targetControl: T,
                view: v,
                config: c
            })
        }
        return {
            oTargetParent: v,
            oTargetControl: T
        }
    }
}());