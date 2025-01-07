﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.layout.BorderLayoutRenderer");
sap.ui.commons.layout.BorderLayoutRenderer = {};
(function() {
    sap.ui.commons.layout.BorderLayoutRenderer.render = function(R, c) {
        var a = {
            top: c.getTop(),
            begin: c.getBegin(),
            center: c.getCenter(),
            end: c.getEnd(),
            bottom: c.getBottom()
        };
        var A = {
            top: s(a.top),
            begin: s(a.begin),
            center: s(a.center),
            end: s(a.end),
            bottom: s(a.bottom)
        };
        var b = sap.ui.getCore().getConfiguration().getRTL();
        R.write("<div");
        R.writeControlData(c);
        R.addClass("sapUiBorderLayout");
        R.addStyle("width", c.getWidth());
        R.addStyle("height", c.getHeight());
        R.writeClasses();
        R.writeStyles();
        R.write(">");
        if (a.top) {
            r(R, "top", a.top, A, b)
        }
        if (a.begin) {
            r(R, "begin", a.begin, A, b)
        }
        if (a.center && a.center.getVisible()) {
            r(R, "center", a.center, A, b)
        }
        if (a.end) {
            r(R, "end", a.end, A, b)
        }
        if (a.bottom) {
            r(R, "bottom", a.bottom, A, b)
        }
        R.write("</div>")
    };
    sap.ui.commons.layout.BorderLayoutRenderer.animate = function(a, v) {
        var b = a.getParent();
        var R = sap.ui.getCore().getConfiguration().getRTL();
        var e = v ? a.getSize() : "0";
        switch (a.getAreaId()) {
            case "top":
                $(a, "top").animate({
                    height: e
                });
                $(a, "begin").animate({
                    top: e
                });
                $(a, "center").animate({
                    top: e
                });
                $(a, "end").animate({
                    top: e
                });
                break;
            case "begin":
                $(a, "begin").animate({
                    width: e
                });
                $(a, "center").animate(R ? {
                    right: e
                } : {
                    left: e
                });
                break;
            case "end":
                $(a, "center").animate(R ? {
                    left: e
                } : {
                    right: e
                });
                $(a, "end").animate({
                    width: e
                });
                break;
            case "bottom":
                $(a, "begin").animate({
                    bottom: e
                });
                $(a, "center").animate({
                    bottom: e
                });
                $(a, "end").animate({
                    bottom: e
                });
                $(a, "bottom").animate({
                    height: e
                });
                break;
            default:
                break
        }
    };

    function s(a) {
        var S = a && a.getVisible() && a.getSize();
        return S || "0"
    }
    function r(R, a, A, m, b) {
        var c = A.getContent();
        var l = c.length;
        R.write("<div");
        R.writeAttribute("id", A.getId());
        switch (a) {
            case "top":
                R.addClass("sapUiBorderLayoutTop");
                R.addStyle("height", m.top);
                break;
            case "begin":
                R.addClass("sapUiBorderLayoutBegin");
                R.addStyle("width", m.begin);
                R.addStyle("top", m.top);
                R.addStyle("bottom", m.bottom);
                break;
            case "center":
                R.addClass("sapUiBorderLayoutCenter");
                R.addStyle("top", m.top);
                R.addStyle("right", b ? m.begin : m.end);
                R.addStyle("bottom", m.bottom);
                R.addStyle("left", b ? m.end : m.begin);
                break;
            case "end":
                R.addClass("sapUiBorderLayoutEnd");
                R.addStyle("width", m.end);
                R.addStyle("top", m.top);
                R.addStyle("bottom", m.bottom);
                break;
            case "bottom":
                R.addClass("sapUiBorderLayoutBottom");
                R.addStyle("height", m.bottom);
                break;
            default:
                break
        }
        R.addStyle("overflow-x", A.getOverflowX());
        R.addStyle("overflow-y", A.getOverflowY());
        var d = A.getContentAlign();
        if (b) {
            if (d === "right") {
                d = "left"
            } else if (d === "left") {
                d = "right"
            }
        }
        R.addStyle("text-align", d);
        R.writeClasses(A);
        R.writeStyles();
        R.write(">");
        for (var i = 0; i < l; i++) {
            R.renderControl(c[i])
        }
        R.write("</div>")
    }
    function $(a, A) {
        var o = a.getParent().getArea(A);
        return o ? jQuery.sap.byId(o.getId()) : jQuery()
    }
}());