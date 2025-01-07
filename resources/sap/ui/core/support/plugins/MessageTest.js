﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.support.plugins.MessageTest");
jQuery.sap.require("sap.ui.core.support.Plugin");
jQuery.sap.require("sap.ui.core.RenderManager");
(function() {
    sap.ui.core.support.Plugin.extend("sap.ui.core.support.plugins.MessageTest", {
        constructor: function(s) {
            sap.ui.core.support.Plugin.apply(this, ["sapUiSupportMessageTest", "Support Tool Communication Test", s]);
            this._aEventIds = [this.getId() + "Msg", sap.ui.core.support.Support.EventType.SETUP, sap.ui.core.support.Support.EventType.TEAR_DOWN];
            this._bFirstTime = true
        }
    });
    sap.ui.core.support.plugins.MessageTest.prototype.onsapUiSupportMessageTestMsg = function(e) {
        jQuery.sap.byId(this.getId() + "-Panel").removeClass("sapUiSupportHidden");
        r(this, this.getId() + "Msg", e.getParameter("message"), true)
    };
    sap.ui.core.support.plugins.MessageTest.prototype.init = function(s) {
        sap.ui.core.support.Plugin.prototype.init.apply(this, arguments);
        var t = this;
        if (this._bFirstTime) {
            this._bFirstTime = false;
            jQuery.sap.byId(this.getId() + "-Panel").addClass("sapUiSupportHidden")
        }
        var a = sap.ui.getCore().createRenderManager();
        a.write("<div class='sapUiSupportToolbar'>");
        a.write("<input type='text' id='" + this.getId() + "-input' class='sapUiSupportTxtFld'></input>");
        a.write("<button id='" + this.getId() + "-send' class='sapUiSupportBtn'>Send</button>");
        a.write("</div><div class='sapUiSupportMessageCntnt'></div>");
        a.flush(this.$().get(0));
        a.destroy();
        this._fSendHandler = function() {
            var v = jQuery.sap.byId(t.getId() + "-input").val();
            s.sendEvent(t.getId() + "Msg", {
                "message": v
            });
            r(t, t.getId() + "Msg", v, false)
        };
        jQuery.sap.byId(this.getId() + "-send").bind("click", this._fSendHandler);
        r(this, sap.ui.core.support.Support.EventType.SETUP, "", true)
    };
    sap.ui.core.support.plugins.MessageTest.prototype.exit = function(s) {
        r(this, sap.ui.core.support.Support.EventType.TEAR_DOWN, "", true);
        if (this._fSendHandler) {
            jQuery.sap.byId(this.getId() + "-send").unbind("click", this._fSendHandler);
            this._fSendHandler = null
        }
        sap.ui.core.support.Plugin.prototype.exit.apply(this, arguments)
    };

    function r(p, m, M, R) {
        jQuery(".sapUiSupportMessageCntnt", p.$()).append("<b style=\"color:" + (R ? "green" : "blue") + ";\">Message '" + m + "' " + (R ? "received" : "send") + (M ? ":</b> " + M : "</b>") + "<br>")
    }
}());