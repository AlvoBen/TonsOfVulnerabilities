﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.mvc.XMLView");
jQuery.sap.require("sap.ui.core.library");
jQuery.sap.require("sap.ui.core.mvc.View");
sap.ui.core.mvc.View.extend("sap.ui.core.mvc.XMLView", {
    metadata: {
        library: "sap.ui.core"
    }
});
jQuery.sap.require("jquery.sap.xml");
jQuery.sap.require("sap.ui.base.DataType");
jQuery.sap.require("sap.ui.model.resource.ResourceModel");
jQuery.sap.require("sap.ui.core.XMLTemplateProcessor");
(function() {
    sap.ui.xmlview = function(i, v) {
        return sap.ui.view(i, v, sap.ui.core.mvc.ViewType.XML)
    };
    sap.ui.core.mvc.XMLView._sType = sap.ui.core.mvc.ViewType.XML;
    sap.ui.core.mvc.XMLView.prototype.initViewSettings = function(s) {
        if (!s) {
            throw new Error("mSettings must be given")
        }
        if (s.viewName && s.viewContent) {
            throw new Error("View name and view content are given. There is no point in doing this, so please decide.")
        } else if ((s.viewName || s.viewContent) && s.xmlNode) {
            throw new Error("View name/content AND an XML node are given. There is no point in doing this, so please decide.")
        } else if (!(s.viewName || s.viewContent) && !s.xmlNode) {
            throw new Error("Neither view name/content nor an XML node is given. One of them is required.")
        }
        if (s.viewName) {
            this._xContent = sap.ui.core.XMLTemplateProcessor.loadTemplate(s.viewName)
        } else if (s.viewContent) {
            this.mProperties["viewContent"] = s.viewContent;
            this._xContent = jQuery.sap.parseXML(s.viewContent);
            if (this._xContent.parseError.errorCode != 0) {
                var p = this._xContent.parseError;
                throw new Error("The following problem occurred: XML parse Error for " + p.url + " code: " + p.errorCode + " reason: " + p.reason + " src: " + p.srcText + " line: " + p.line + " linepos: " + p.linepos + " filepos: " + p.filepos)
            } else {
                this._xContent = this._xContent.documentElement
            }
        } else if (s.xmlNode) {
            this._xContent = s.xmlNode
        } else {}
        this._oContainingView = s.containingView || this;
        if (!this.isSubView()) {
            sap.ui.core.XMLTemplateProcessor.parseViewAttributes(this._xContent, this, s)
        } else {
            delete s.controller
        }
        if ((this._resourceBundleName || this._resourceBundleUrl) && (!s.models || !s.models[this._resourceBundleAlias])) {
            var m = new sap.ui.model.resource.ResourceModel({
                bundleName: this._resourceBundleName,
                bundleUrl: this._resourceBundleUrl,
                bundleLocale: this._resourceBundleLocale
            });
            this.setModel(m, this._resourceBundleAlias)
        }
        var t = this;
        this.oAfterRenderingNotifier = new sap.ui.core.mvc.XMLAfterRenderingNotifier();
        this.oAfterRenderingNotifier.addDelegate({
            onAfterRendering: function() {
                t.onAfterRenderingBeforeChildren()
            }
        })
    };
    sap.ui.core.mvc.XMLView.prototype.exit = function() {
        this.oAfterRenderingNotifier.destroy();
        sap.ui.core.mvc.View.prototype.exit.apply(this, arguments)
    };
    sap.ui.core.mvc.XMLView.prototype.onControllerConnected = function(c) {
        var t = this;
        sap.ui.base.ManagedObject.runWithPreprocessors(function() {
            t._aParsedContent = sap.ui.core.XMLTemplateProcessor.parseTemplate(t._xContent, t)
        })
    };
    sap.ui.core.mvc.XMLView.prototype.getControllerName = function() {
        return this._controllerName
    };
    sap.ui.core.mvc.XMLView.prototype.isSubView = function() {
        return this._oContainingView != this
    };
    sap.ui.core.mvc.XMLView.prototype.onAfterRenderingBeforeChildren = function() {
        if (this._$oldContent.length !== 0) {
            var c = this.getAggregation("content");
            if (c) {
                for (var i = 0; i < c.length; i++) {
                    var $ = c[i].$();
                    jQuery.sap.byId("sap-ui-dummy-" + c[i].getId(), this._$oldContent).replaceWith($)
                }
            }
            jQuery.sap.byId("sap-ui-dummy-" + this.getId()).replaceWith(this._$oldContent)
        }
        this._$oldContent = undefined
    };
    sap.ui.core.Control.extend("sap.ui.core.mvc.XMLAfterRenderingNotifier", {
        renderer: function(r, c) {
            r.write("")
        }
    })
}());