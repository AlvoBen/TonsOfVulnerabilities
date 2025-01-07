﻿/*
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.CustomSelect");
jQuery.sap.require("sap.m.Select");
(function(_) {
    var p = "sap.ui.thirdparty.mobiscroll.",
        c = p + "css",
        P = sap.ui.core.theming.Parameters.get("sapMPlatformDependent") == "true",
        a = P && sap.ui.Device.os.android && sap.ui.Device.os.version === 2.3,
        t = P ? "android-ics light" : "sapMCustomSlt";
    _.require("sap.m.InstanceManager");
    _.require(p + "js.mobiscroll-core");
    _.require(p + "js.mobiscroll-select");
    _.includeStyleSheet(_.getModulePath(c, "/") + "mobiscroll-core.css");
    if (P) {
        _.includeStyleSheet(_.getModulePath(c, "/") + "mobiscroll-android-ics.css")
    }
    var g = function() {
        return ((jQuery(document.defaultView).width() - 40) / parseFloat(sap.m.BaseFontSize)) + "rem"
    };
    sap.m.Select.prototype._onBeforeRenderingCustom = function() {
        if (this._$HtmlSelect) {
            this._$HtmlSelect.scroller("destroy")
        }
    };
    sap.m.Select.prototype._onAfterRenderingCustom = function() {
        var s = this,
            I = sap.m.InstanceManager,
            h = jQuery.proxy(this._handlePointerDown, this),
            H = jQuery.proxy(this._handleResizeEvent, this);
        if (this._$HtmlSelect.length) {
            this._$HtmlSelect.scroller({
                cancelText: this._oRb.getText("SELECT_CANCEL"),
                setText: this._oRb.getText("SELECT_ACCEPT"),
                lang: this._sLang,
                delay: 300,
                disabled: !this.getEnabled(),
                display: sap.ui.Device.system.phone ? "modal" : "bubble",
                mode: sap.ui.Device.system.phone || sap.ui.Device.system.tablet ? "scroller" : "mixed",
                preset: "select",
                showLabel: false,
                theme: t,
                width: 80,
                height: 40,
                inputClass: "sapMSltInput",
                rows: sap.ui.Device.system.phone ? 3 : 5,
                onMarkupReady: function($, S) {
                    $[0].querySelector(".dwwr").style.maxWidth = g()
                },
                onShow: function($, v, S) {
                    !a && document.addEventListener("touchstart", h, true);
                    jQuery(window).on("resize.sapMCustomSelect", {
                        $domRef: $
                    }, H);
                    I.addDialogInstance(s)
                },
                onClose: function() {
                    !a && document.removeEventListener("touchstart", h, true);
                    jQuery(window).off("resize.sapMCustomSelect", H);
                    I.removeDialogInstance(s)
                }
            });
            if (a) {
                setTimeout(function() {
                    if (s.getWidth() === "auto") {
                        s._$HtmlSelect[0].style.width = (s._$HtmlSelect.outerWidth() / parseFloat(sap.m.BaseFontSize)) + "rem"
                    }
                    s._$HtmlSelect.addClass("sapMSltNativeHidden")
                }, 0)
            }
        }
    };
    sap.m.Select.prototype._handlePointerDown = function(e) {
        if (!document.querySelector(".sapMCustomSlt .dwwr").contains(e.target)) {
            this.close()
        }
    };
    sap.m.Select.prototype.close = function() {
        this._$HtmlSelect.scroller("hide")
    };
    sap.m.Select.prototype._ontouchmoveCustom = function(e) {
        e.stopPropagation();
        e.preventDefault()
    };
    sap.m.Select.prototype._handleResizeEvent = function(e) {
        e.data.$domRef[0].querySelector(".dwwr").style.maxWidth = g()
    }
})(jQuery.sap);