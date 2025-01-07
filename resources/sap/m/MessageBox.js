﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.MessageBox");
jQuery.sap.require("sap.m.Button");
jQuery.sap.require("sap.m.Dialog");
jQuery.sap.require("sap.m.Text");
jQuery.sap.require("sap.ui.core.IconPool");
jQuery.sap.require("sap.ui.core.theming.Parameters");
sap.m.MessageBox = {};
sap.m.MessageBox._bOneDesign = (sap.ui.core.theming.Parameters.get("sapMPlatformDependent") !== "true");
sap.m.MessageBox._rb = sap.ui.getCore().getLibraryResourceBundle("sap.m");
sap.m.MessageBox.Action = {
    OK: "OK",
    CANCEL: "CANCEL",
    YES: "YES",
    NO: "NO",
    ABORT: "ABORT",
    RETRY: "RETRY",
    IGNORE: "IGNORE",
    CLOSE: "CLOSE",
    DELETE: "DELETE"
};
sap.m.MessageBox.Icon = {
    NONE: undefined,
    INFORMATION: "INFORMATION",
    WARNING: "WARNING",
    ERROR: "ERROR",
    SUCCESS: "SUCCESS",
    QUESTION: "QUESTION"
};
(function() {
    var A = sap.m.MessageBox.Action,
        I = sap.m.MessageBox.Icon,
        c = {
            "INFORMATION": "sapMMessageBoxInfo",
            "WARNING": "sapMMessageBoxWarning",
            "ERROR": "sapMMessageBoxError",
            "SUCCESS": "sapMMessageBoxSuccess",
            "QUESTION": "sapMMessageBoxQuestion"
        }, u, i;
    if (sap.m.MessageBox._bOneDesign) {
        i = {
            "INFORMATION": sap.ui.core.IconPool.getIconURI("hint"),
            "WARNING": sap.ui.core.IconPool.getIconURI("warning2"),
            "ERROR": sap.ui.core.IconPool.getIconURI("alert"),
            "SUCCESS": sap.ui.core.IconPool.getIconURI("accept"),
            "QUESTION": sap.ui.core.IconPool.getIconURI("incident")
        }
    } else {
        u = jQuery.sap.getModulePath("sap.m", "/") + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + "/img/messagebox/";
        i = {
            "INFORMATION": u + "info.png",
            "WARNING": u + "warning.png",
            "ERROR": u + "error.png",
            "SUCCESS": u + "success.png",
            "QUESTION": u + "question.png"
        }
    }
    sap.m.MessageBox.show = function(m, o, t, a, C, d) {
        var D, r = null,
            b = this;
        if (typeof a !== "undefined" && !jQuery.isArray(a)) {
            a = [a]
        }
        if (!a || a.length === 0) {
            a = [A.OK]
        }
        if (a.length > 2) {
            a = a.slice(0, 2)
        }
        d = d || sap.ui.core.ElementMetadata.uid("mbox");

        function e(s) {
            var k = "MSGBOX_" + s,
                T = b._rb.getText(k);
            if (k === T) {
                T = s
            }
            var B = new sap.m.Button({
                id: sap.ui.core.ElementMetadata.uid("mbox-btn-"),
                text: T || s,
                press: function() {
                    r = s;
                    D.close()
                }
            });
            return B
        }
        function f() {
            if (typeof C === "function") {
                C(r)
            }
            D.detachAfterClose(f);
            D.destroy()
        }
        D = new sap.m.Dialog({
            id: d,
            type: sap.m.DialogType.Message,
            title: t,
            icon: i[o],
            leftButton: e(a[0]),
            content: new sap.m.Text({
                text: m
            }).addStyleClass("sapMMsgBoxText"),
            afterClose: f
        });
        if (c[o]) {
            D.addStyleClass(c[o])
        }
        if (a[1]) {
            D.setRightButton(e(a[1]))
        }
        D.open()
    };
    sap.m.MessageBox.alert = function(m, C, t, d) {
        return sap.m.MessageBox.show(m, I.NONE, t ? t : this._rb.getText("MSGBOX_TITLE_ALERT"), A.OK, function(a) {
            if (typeof C === "function") {
                C(a)
            }
        }, d || sap.ui.core.ElementMetadata.uid("alert"))
    };
    sap.m.MessageBox.confirm = function(m, C, t, d) {
        return sap.m.MessageBox.show(m, I.QUESTION, t ? t : this._rb.getText("MSGBOX_TITLE_CONFIRM"), [A.OK, A.CANCEL], function(a) {
            if (typeof C === "function") {
                C(a)
            }
        }, d || sap.ui.core.ElementMetadata.uid("confirm"))
    }
}());