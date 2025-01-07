﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.support.plugins.Performance");
jQuery.sap.require("sap.ui.core.support.Plugin");
jQuery.sap.require("sap.ui.core.RenderManager");
(function() {
    sap.ui.core.support.Plugin.extend("sap.ui.core.support.plugins.Performance", {
        constructor: function(s) {
            sap.ui.core.support.Plugin.apply(this, ["sapUiSupportPerf", "Performance", s]);
            this._oStub = s;
            if (this.isToolPlugin()) {
                this._aEventIds = [this.getId() + "SetMeasurements", this.getId() + "SetActive"];
                jQuery.sap.require("sap.ui.core.format.DateFormat");
                this._oDateFormat = sap.ui.core.format.DateFormat.getTimeInstance({
                    pattern: "HH:mm:ss '+' SSS"
                })
            } else {
                this._aEventIds = [this.getId() + "Refresh", this.getId() + "Clear", this.getId() + "Start", this.getId() + "Stop", this.getId() + "Activate"]
            }
        }
    });
    sap.ui.core.support.plugins.Performance.prototype.init = function(s) {
        sap.ui.core.support.Plugin.prototype.init.apply(this, arguments);
        if (this.isToolPlugin()) {
            a.call(this, s)
        } else {
            b.call(this, s)
        }
    };
    sap.ui.core.support.plugins.Performance.prototype.exit = function(s) {
        sap.ui.core.support.Plugin.prototype.exit.apply(this, arguments)
    };

    function a(s) {
        var r = sap.ui.getCore().createRenderManager();
        r.write("<div class=\"sapUiSupportToolbar\">");
        r.write("<button id=\"" + this.getId() + "-refresh\" class=\"sapUiSupportBtn\">Refresh</button>");
        r.write("<button id=\"" + this.getId() + "-clear\" class=\"sapUiSupportBtn\">Clear</button>");
        r.write("<input type=\"checkbox\" id=\"" + this.getId() + "-active\" class=\"sapUiSupportChB\">");
        r.write("<label for=\"" + this.getId() + "-active\" class=\"sapUiSupportLabel\">Active</label>");
        r.write("</div><div class=\"sapUiSupportPerfCntnt\">");
        r.write("<table id=\"" + this.getId() + "-tab\" width=\"100%\">");
        r.write("<colgroup><col><col><col><col><col><col></colgroup>");
        r.write("<thead style=\"text-align:left;\"><tr>");
        r.write("<th>ID</th>");
        r.write("<th>Info</th>");
        r.write("<th>Start</th>");
        r.write("<th>End</th>");
        r.write("<th>Time</th>");
        r.write("<th>Duration</th>");
        r.write("</tr></thead>");
        r.write("<tbody id=\"" + this.getId() + "-tabBody\"></tbody>");
        r.write("</table></div>");
        r.flush(this.$().get(0));
        r.destroy();
        jQuery.sap.byId(this.getId() + "-refresh").click(jQuery.proxy(function(e) {
            this._oStub.sendEvent(this.getId() + "Refresh")
        }, this));
        jQuery.sap.byId(this.getId() + "-clear").click(jQuery.proxy(function(e) {
            this._oStub.sendEvent(this.getId() + "Clear")
        }, this));
        jQuery.sap.byId(this.getId() + "-active").click(jQuery.proxy(function(e) {
            var A = false;
            if (jQuery.sap.byId(this.getId() + "-active").attr("checked")) {
                A = true
            }
            this._oStub.sendEvent(this.getId() + "Activate", {
                "active": A
            })
        }, this))
    };

    function b(s) {
        g.call(this)
    };

    function g(s) {
        var A = jQuery.sap.measure.getActive();
        var m = new Array();
        if (A) {
            m = jQuery.sap.measure.getAllMeasurements()
        }
        this._oStub.sendEvent(this.getId() + "SetMeasurements", {
            "measurements": m
        });
        this._oStub.sendEvent(this.getId() + "SetActive", {
            "active": A
        })
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfSetMeasurements = function(e) {
        var m = e.getParameter("measurements");
        var t = jQuery.sap.byId(this.getId() + "-tabBody");
        var r = sap.ui.getCore().createRenderManager();
        for (var i = 0; i < m.length; i++) {
            var M = m[i];
            r.write("<tr>");
            r.write("<td>" + M.id + "</td>");
            r.write("<td>" + M.info + "</td>");
            r.write("<td>" + this._oDateFormat.format(new Date(M.start)) + "</td>");
            r.write("<td>" + this._oDateFormat.format(new Date(M.end)) + "</td>");
            r.write("<td>" + M.time + "</td>");
            r.write("<td>" + M.duration + "</td>");
            r.write("</tr>")
        }
        r.flush(t[0]);
        r.destroy()
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfSetActive = function(e) {
        var A = e.getParameter("active");
        var c = jQuery.sap.byId(this.getId() + "-active");
        if (A) {
            c.attr("checked", "checked")
        } else {
            c.removeAttr("checked")
        }
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfRefresh = function(e) {
        g.call(this)
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfClear = function(e) {
        jQuery.sap.measure.clear();
        this._oStub.sendEvent(this.getId() + "SetMeasurements", {
            "measurements": []
        })
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfStart = function(e) {
        jQuery.sap.measure.start(this.getId() + "-perf", "Measurement by support tool")
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfEnd = function(e) {
        jQuery.sap.measure.end(this.getId() + "-perf")
    };
    sap.ui.core.support.plugins.Performance.prototype.onsapUiSupportPerfActivate = function(e) {
        var A = e.getParameter("active");
        if (jQuery.sap.measure.getActive() != A) {
            jQuery.sap.measure.setActive(A)
        }
    }
}());