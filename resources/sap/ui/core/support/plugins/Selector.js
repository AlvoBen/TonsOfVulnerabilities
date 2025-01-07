﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.support.plugins.Selector");
jQuery.sap.require("sap.ui.core.support.Plugin");
jQuery.sap.require("sap.ui.core.Popup");
(function() {
    sap.ui.core.support.Plugin.extend("sap.ui.core.support.plugins.Selector", {
        constructor: function(s) {
            sap.ui.core.support.Plugin.apply(this, ["sapUiSupportSelector", "", s]);
            if (this.isToolPlugin()) {
                throw Error()
            }
            this._aEventIds = [this.getId() + "Highlight"];
            this._oPopup = new sap.ui.core.Popup()
        }
    });
    sap.ui.core.support.plugins.Selector.prototype.onsapUiSupportSelectorHighlight = function(e) {
        h(e.getParameter("id"), this, e.getParameter("sendInfo"))
    };
    sap.ui.core.support.plugins.Selector.prototype.init = function(s) {
        sap.ui.core.support.Plugin.prototype.init.apply(this, arguments);
        var p;
        if (!this._sPopupId) {
            this._sPopupId = this.getId() + "-" + jQuery.sap.uid();
            var r = sap.ui.getCore().createRenderManager();
            r.write("<div id='" + this._sPopupId + "' style='border: 2px solid rgb(0, 128, 0); background-color: rgba(0, 128, 0, .55);'></div>");
            r.flush(sap.ui.getCore().getStaticAreaRef(), false, true);
            r.destroy();
            p = jQuery.sap.byId(this._sPopupId);
            this._oPopup.setContent(p[0])
        } else {
            p = jQuery.sap.byId(this._sPopupId)
        }
        var t = this;
        this._fSelectHandler = function(e) {
            if (!e.shiftKey || !e.altKey || !e.ctrlKey) {
                return
            }
            var i = jQuery(e.target).closest("[data-sap-ui]").attr("id");
            if (h(i, t, true)) {
                e.stopPropagation();
                e.preventDefault()
            }
        };
        this._fCloseHandler = function(e) {
            t._oPopup.close(0)
        };
        p.bind("click", this._fCloseHandler);
        jQuery(document).bind("mousedown", this._fSelectHandler)
    };
    sap.ui.core.support.plugins.Selector.prototype.exit = function(s) {
        this._oPopup.close(0);
        if (this._fCloseHandler) {
            jQuery.sap.byId(this._sPopupId).unbind("click", this._fCloseHandler);
            this._fCloseHandler = null
        }
        if (this._fSelectHandler) {
            jQuery(document).unbind("mousedown", this._fSelectHandler);
            this._fSelectHandler = null
        }
        sap.ui.core.support.Plugin.prototype.exit.apply(this, arguments)
    };

    function h(i, p, s) {
        if (i) {
            var e = sap.ui.getCore().byId(i);
            if (e) {
                var P = jQuery.sap.byId(p._sPopupId);
                var r = e.$();
                if (r.is(":visible")) {
                    P.width(r.outerWidth());
                    P.height(r.outerHeight());
                    p._oPopup.open(0, "BeginTop", "BeginTop", r[0], "0 0", "none");
                    if (s) {
                        sap.ui.core.support.Support.getStub().sendEvent(p.getId() + "Select", g(e, p))
                    }
                    setTimeout(function() {
                        p._oPopup.close(0)
                    }, 1000);
                    return true
                }
            }
        }
        return false
    };

    function g(e, p) {
        return {
            "id": e.getId()
        }
    }
}());