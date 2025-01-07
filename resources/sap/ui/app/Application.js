﻿/*
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.app.Application");
jQuery.sap.require("sap.ui.app.ApplicationMetadata");
jQuery.sap.require("sap.ui.base.ManagedObject");
(function(w, u) {
    sap.ui.base.ManagedObject.extend("sap.ui.app.Application", {
        metadata: {
            "abstract": true,
            properties: {
                root: "string",
                config: "any"
            },
            aggregations: {
                rootComponent: {
                    type: "sap.ui.core.UIComponent",
                    multiple: false
                }
            },
            publicMethods: ["getView"],
            deprecated: true
        },
        _fnErrorHandler: null,
        _fnBeforeExit: null,
        _fnExit: null,
        _mMockServers: null,
        constructor: function(i, s) {
            if (sap.ui.getApplication) {
                throw new Error("Only one instance of sap.ui.app.Application is allowed")
            }
            sap.ui.getApplication = jQuery.proxy(this._getInstance, this);
            this._mMockServers = {};
            if (this.onError) {
                this._fnErrorHandler = jQuery.proxy(function(e) {
                    var E = e.originalEvent;
                    this.onError(E.message, E.filename, E.lineno)
                }, this);
                jQuery(w).bind("error", this._fnErrorHandler)
            }
            sap.ui.base.ManagedObject.apply(this, arguments);
            this.register();
            sap.ui.getCore().attachInit(jQuery.proxy(function() {
                this._initApplicationModels();
                this._initRootComponent();
                this.main()
            }, this));
            this._fnBeforeExit = jQuery.proxy(this.onBeforeExit, this);
            jQuery(w).bind("beforeunload", this._fnBeforeExit);
            this._fnExit = jQuery.proxy(this.onExit, this);
            jQuery(w).bind("unload", this._fnExit)
        },
        _initRootComponent: function() {
            var r = this.createRootComponent();
            if (r) {
                this.setRootComponent(r);
                var c = new sap.ui.core.ComponentContainer({
                    component: r
                });
                c.placeAt(this.getRoot() || document.body)
            }
        },
        createRootComponent: function() {
            var r = this.getMetadata().getRootComponent();
            var R;
            if (r) {
                R = sap.ui.component({
                    name: r
                })
            }
            return R
        },
        getView: function() {
            return this.getRootComponent()
        },
        _getInstance: function() {
            return this
        },
        main: function() {},
        onBeforeExit: function() {},
        onExit: function() {},
        onError: null,
        setConfig: function(c) {
            if (typeof c === "string") {
                var U = c;
                var c = new sap.ui.model.json.JSONModel();
                var r = jQuery.sap.sjax({
                    url: U,
                    dataType: 'json'
                });
                if (r.success) {
                    c.setData(r.data)
                } else {
                    throw new Error("Could not load config file: " + U)
                }
            }
            if (typeof c === "object" && !c instanceof sap.ui.model.Model) {
                c = new sap.ui.model.JSONModel(c)
            }
            this.setProperty("config", c)
        },
        _initApplicationModels: function() {
            var m = this.getMetadata();
            var M = m.getModels(),
                s = m.getServices();
            if (M) {
                var c = function(n, U, a, b) {
                    if (this._mMockServers[n]) {
                        this._mMockServers[n].stop()
                    }
                    jQuery.sap.require("sap.ui.app.MockServer");
                    this._mMockServers[n] = new sap.ui.app.MockServer({
                        rootUri: U
                    });
                    this._mMockServers[n].simulate(a, b);
                    this._mMockServers[n].start()
                };
                var C = function(n, o) {
                    var U = o.uri,
                        T = o.type;
                    jQuery.sap.require(T);
                    var a = jQuery.sap.getObject(T);
                    var b;
                    if (T === "sap.ui.model.resource.ResourceModel") {
                        b = new a({
                            bundleUrl: U
                        })
                    } else if (T === "sap.ui.model.odata.ODataModel") {
                        if (o.mockserver) {
                            c.call(this, n, U, o.mockserver.model, o.mockserver.data)
                        }
                        b = new a(U, o.settings)
                    } else if (T === "sap.ui.model.json.JSONModel" || T === "sap.ui.model.xml.XMLModel") {
                        b = new a();
                        if (U) {
                            b.loadData(U)
                        }
                    }
                    return b
                };
                var t = this;
                jQuery.each(M, function(k, o) {
                    var S = o.service,
                        a;
                    if (S) {
                        var b = s[S];
                        a = C.call(t, k, b)
                    } else if (o.type) {
                        a = C.call(t, k, o)
                    }
                    if (a) {
                        t.setModel(a, k || u)
                    }
                })
            }
        },
        destroy: function(s) {
            if (this._mMockServers) {
                jQuery.each(this._mMockServers, function(n, m) {
                    m.stop()
                })
            }
            this._oMockServers = [];
            if (this._fnErrorHandler) {
                jQuery(w).unbind("error", this._fnErrorHandler);
                this._fnErrorHandler = null
            }
            if (this._fnBeforeExit) {
                jQuery(w).unbind("beforeunload", this._fnBeforeExit);
                this._fnBeforeExit = null
            }
            if (this._fnExit) {
                jQuery(w).unbind("unload", this._fnExit);
                this._fnExit = null
            }
            delete sap.ui.getApplication;
            sap.ui.base.ManagedObject.prototype.destroy.apply(this, arguments)
        }
    }, sap.ui.app.ApplicationMetadata)
})(window);