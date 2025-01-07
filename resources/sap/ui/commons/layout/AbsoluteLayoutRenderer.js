﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.layout.AbsoluteLayoutRenderer");
sap.ui.commons.layout.AbsoluteLayoutRenderer = {};
(function() {
    sap.ui.commons.layout.AbsoluteLayoutRenderer.render = function(r, c) {
        var a = r;
        c.doBeforeRendering();
        if (!c.getVisible()) {
            return
        }
        a.write("<div");
        a.writeControlData(c);
        a.addClass("sapUiLayoutAbs");
        a.addClass("sapUiLayoutAbsOvrflwY" + c.getVerticalScrolling());
        a.addClass("sapUiLayoutAbsOvrflwX" + c.getHorizontalScrolling());
        a.writeClasses();
        var s = "width:" + c.getWidth() + ";height:" + c.getHeight() + ";";
        a.writeAttribute("style", s);
        var t = c.getTooltip_AsString();
        if (t) {
            a.writeAttributeEscaped("title", t)
        }
        a.write(">");
        var p = c.getPositions();
        if (p && p.length > 0) {
            for (var i = 0; i < p.length; i++) {
                var P = p[i];
                var C = P.getControl();
                if (C) {
                    a.write("<div");
                    a.writeElementData(P);
                    a.writeAttribute("class", "sapUiLayoutAbsPos");
                    a.writeAttribute("style", g(P));
                    t = P.getTooltip_AsString();
                    if (t) {
                        a.writeAttributeEscaped("title", t)
                    }
                    a.write(">");
                    a.renderControl(C);
                    a.write("</div>")
                }
            }
        }
        a.write("</div>")
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.updateLayoutSize = function(l) {
        jQuery(l.getDomRef()).css("width", l.getWidth()).css("height", l.getHeight())
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.updateLayoutScolling = function(l) {
        var L = jQuery(l.getDomRef());
        for (var s in sap.ui.core.Scrolling) {
            L.removeClass("sapUiLayoutAbsOvrflwY" + s).removeClass("sapUiLayoutAbsOvrflwX" + s)
        }
        L.addClass("sapUiLayoutAbsOvrflwY" + l.getVerticalScrolling()).addClass("sapUiLayoutAbsOvrflwX" + l.getHorizontalScrolling())
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionStyles = function(p) {
        jQuery(p.getDomRef()).attr("style", g(p))
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.removePosition = function(p) {
        jQuery(p.getDomRef()).remove()
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.removeAllPositions = function(l) {
        jQuery(l.getDomRef()).html("")
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionedControl = function(p) {
        sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionStyles(p);
        var r = sap.ui.getCore().createRenderManager();
        r.renderControl(p.getControl());
        r.flush(p.getDomRef());
        r.destroy()
    };
    sap.ui.commons.layout.AbsoluteLayoutRenderer.insertPosition = function(l, p) {
        var i = l.indexOfPosition(p);
        var P = l.getPositions();
        var o = null;
        while (i > 0) {
            i--;
            if (P[i].getDomRef()) {
                o = P[i];
                break
            }
        }
        var h = "<div id=\"" + p.getId() + "\" data-sap-ui=\"" + p.getId() + "\" class=\"sapUiLayoutAbsPos\"></div>";
        if (!o) {
            jQuery(l.getDomRef()).prepend(h)
        } else {
            jQuery(o.getDomRef()).after(h)
        }
        sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionedControl(p)
    };
    var g = function(p) {
        var P = p.getComputedPosition();
        var a = function(p, b, s, v) {
            if (v) {
                b.push(s + ":" + v + ";")
            }
        };
        var b = [];
        a(p, b, "top", P.top);
        a(p, b, "bottom", P.bottom);
        a(p, b, "left", P.left);
        a(p, b, "right", P.right);
        a(p, b, "width", P.width);
        a(p, b, "height", P.height);
        return b.join("")
    }
}());