﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.IconTabBarRenderer");
jQuery.sap.require("sap.ui.core.IconPool");
sap.m.IconTabBarRenderer = {};
sap.m.IconTabBarRenderer._aAllIconColors = ['sapMITBFilterCritical', 'sapMITBFilterPositive', 'sapMITBFilterNegative', 'sapMITBFilterDefault'];

sap.m.IconTabBarRenderer.render = function(r, c) {
    if (!c.getVisible()) {
        return
    }
    var I = c.getItems();
    var t = c._checkTextOnly(I);
    var n = c._checkNoText(I);
    r.write("<div ");
    r.addClass("sapMITB sapMITBNotScrollable");
    r.writeControlData(c);
    r.writeClasses();
    r.write(">");
    r.renderControl(c._getScrollingArrow("left"));
    if (sap.ui.Device.support.touch || jQuery.sap.simulateMobileOnDesktop) {
        r.write("<div id='" + c.getId() + "-scrollContainer' class='sapMITBScrollContainer'>")
    }
    r.write("<div id='" + c.getId() + "-head'");
    r.addClass("sapMITBHead");
    if (t) {
        r.addClass("sapMITBTextOnly")
    }
    if (n) {
        r.addClass("sapMITBNoText")
    }
    r.writeClasses();
    r.write(">");
    jQuery.each(I, function(a, b) {
        if (!(b instanceof sap.m.IconTabSeparator) && !b.getVisible()) {
            return
        }
        r.write("<div ");
        r.writeElementData(b);
        r.addClass("sapMITBItem");
        if (b instanceof sap.m.IconTabFilter) {
            if (b.getDesign() === sap.m.IconTabFilterDesign.Vertical) {
                r.addClass("sapMITBVertical")
            } else if (b.getDesign() === sap.m.IconTabFilterDesign.Horizontal) {
                r.addClass("sapMITBHorizontal")
            }
            if (b.getShowAll()) {
                r.addClass("sapMITBAll")
            } else {
                r.addClass("sapMITBFilter");
                r.addClass("sapMITBFilter" + b.getIconColor())
            }
            if (!b.getEnabled()) {
                r.addClass("sapMITBDisabled")
            }
            r.writeClasses();
            r.write(">");
            r.write("<div id='" + b.getId() + "-tab' class='sapMITBTab'>");
            if (!b.getShowAll() || !b.getIcon()) {
                r.renderControl(b._getImageControl(['sapMITBFilterIcon', 'sapMITBFilter' + b.getIconColor()], c, sap.m.IconTabBarRenderer._aAllIconColors))
            }
            if (!b.getShowAll() && !b.getIcon() && !t) {
                r.write("<span class='sapMITBFilterNoIcon'> </span>")
            }
            r.write("<span ");
            r.addClass("sapMITBCount");
            r.writeClasses();
            r.write(">");
            r.writeEscaped(b.getCount());
            r.write("</span>");
            r.write("</div>");
            if (b.getText().length) {
                r.write("<div id='" + b.getId() + "-text' class=\"sapMITBText\">");
                r.writeEscaped(b.getText());
                r.write("</div>")
            }
        } else {
            r.addClass("sapMITBSep");
            if (!b.getIcon()) {
                r.addClass("sapMITBSepLine")
            }
            r.writeClasses();
            r.write(">");
            if (b.getIcon()) {
                r.renderControl(b._getImageControl(['sapMITBSepIcon'], c))
            }
        }
        r.write("</div>")
    });
    r.write("</div>");
    if (sap.ui.Device.support.touch || jQuery.sap.simulateMobileOnDesktop) {
        r.write("</div>")
    }
    r.renderControl(c._getScrollingArrow("right"));
    var C = c.getContent();
    r.write("<div id=\"" + c.getId() + "-containerContent\" ");
    r.addClass("sapMITBContainerContent");
    if (!c.getExpanded()) {
        r.addClass("sapMITBContentClosed")
    }
    r.writeClasses();
    r.write(">");
    r.write("<div id=\"" + c.getId() + "-contentArrow\" ");
    r.addClass("sapMITBContentArrow");
    if (!c.getExpanded()) {
        r.addClass("sapMITBNoContentArrow")
    }
    r.writeClasses();
    r.write("></div>");
    r.write("<div id=\"" + c.getId() + "-content\" class=\"sapMITBContent\" ");
    if (!c.getExpanded()) {
        r.write("style=\"display: none\"")
    }
    r.write(">");
    if (c.getExpanded()) {
        if (c.oSelectedItem && c.oSelectedItem.getContent()) {
            var o = c.oSelectedItem.getContent();
            if (o.length > 0) {
                C = o
            }
        }
        if (C.length > 0) {
            for (var i = 0; i < C.length; i++) {
                r.renderControl(C[i])
            }
        }
    }
    r.write("</div>");
    r.write("</div>");
    r.write("</div>")
};