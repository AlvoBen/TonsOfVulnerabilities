﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.TreeRenderer");
sap.ui.commons.TreeRenderer = {};

sap.ui.commons.TreeRenderer.render = function(r, t) {
    var a = r;
    sap.ui.commons.TreeRenderer.bFirstNodeRendered = false;
    a.write("<div");
    a.writeControlData(t);
    a.addClass("sapUiTree");
    if (t.getHeight() != "" && t.getHeight() != "auto") {
        a.addClass("sapUiTreeFixedHeight")
    }
    if (!t.getShowHeader()) {
        a.addClass("sapUiTreeTransparent")
    }
    a.writeClasses();
    a.addStyle("width", t.getWidth() || "auto");
    a.addStyle("height", t.getHeight());
    a.addStyle("min-width", t.getMinWidth());
    a.writeStyles();
    a.writeAttribute('role', 'tree');
    a.write(">");
    if (t.getShowHeader()) {
        a.write("<div id=\"" + t.getId() + "-Header\" class=\"sapUiTreeHeader\"");
        a.writeAttribute('role', 'heading');
        a.write(">");
        a.write("<div class='sapUiTreeTitle'");
        if (t.getTooltip_AsString()) {
            a.writeAttributeEscaped("title", t.getTooltip_AsString())
        }
        a.write(">");
        a.writeEscaped(t.getTitle());
        a.write("</div>");
        if (t.getShowHeaderIcons()) {
            a.write("<div id='" + t.getId() + "-TBCont' class='sapUiTreeTbCont'");
            a.writeAttribute('role', 'toolbar');
            a.write(">");
            a.renderControl(t.oCollapseAllButton);
            a.renderControl(t.oExpandAllButton);
            a.write("</div>")
        }
        a.write("</div>")
    }
    a.write("<div id=\"" + t.getId() + "-TreeCont\"");
    a.addClass("sapUiTreeCont");
    var s = t.getShowHorizontalScrollbar();
    if (s) {
        a.addClass("sapUiTreeContScroll")
    } else {
        a.addClass("sapUiTreeContNoScroll")
    }
    a.writeClasses();
    a.write(">");
    a.write("<ul class=\"sapUiTreeList\">");
    var n = t.getNodes();
    for (var i = 0; i < n.length; i++) {
        sap.ui.commons.TreeRenderer.renderNode(a, n[i], 1, n.length, i + 1)
    }
    a.write("</ul>");
    a.write("</div>");
    a.write("</div>")
};

sap.ui.commons.TreeRenderer.renderNode = function(r, n, l, s, p) {
    var a = r;
    var e;
    a.write("<li");
    a.writeElementData(n);
    a.addClass("sapUiTreeNode");
    if (n.getExpanded() && (n.getHasExpander() || n.hasChildren())) {
        a.addClass("sapUiTreeNodeExpanded");
        e = true
    } else if (!n.getExpanded() && (n.getHasExpander() || n.hasChildren())) {
        a.addClass("sapUiTreeNodeCollapsed");
        e = false
    }
    if (n.getSelectable() && n.getIsSelected()) {
        a.addClass("sapUiTreeNodeSelected");
        a.writeAttribute('aria-selected', 'true')
    }
    if (!e && n.hasSelectedHiddenChild()) {
        a.addClass("sapUiTreeNodeSelectedParent");
        a.writeAttribute('aria-selected', 'true')
    }
    a.writeClasses();
    a.writeAttribute('role', 'treeitem');
    a.writeAttribute('aria-level', l);
    a.writeAttribute('aria-setsize', s);
    a.writeAttribute('aria-posinset', p);
    if (e) {
        a.writeAttribute("aria-expanded", "true")
    } else {
        if (n.getHasExpander()) {
            a.writeAttribute("aria-expanded", "false")
        }
    }
    a.writeAttributeEscaped("title", n.getTooltip_AsString());
    if (!sap.ui.commons.TreeRenderer.bFirstNodeRendered) {
        a.write("tabindex='0'");
        sap.ui.commons.TreeRenderer.bFirstNodeRendered = true
    }
    a.write(">");
    a.write("<span");
    a.addClass("sapUiTreeNodeContent");
    if (!n.getSelectable()) {
        a.addClass("sapUiTreeNodeNotSelectable")
    }
    a.writeClasses();
    a.write(">");
    if (n.getIcon()) {
        a.write("<img class='sapUiTreeIcon'");
        a.writeAttributeEscaped("src", n.getIcon());
        a.write("/>")
    }
    a.writeEscaped(n.getText());
    a.write("</span>");
    a.write("</li>");
    if (n.getNodes()) {
        var S = n.getNodes();
        a.write("<ul");
        a.writeAttribute("id", n.getId() + "-children");
        a.addClass("sapUiTreeChildrenNodes");
        if (!e) {
            a.addClass("sapUiTreeHiddenChildrenNodes")
        } else {
            a.writeAttribute("style", "display: block;")
        }
        a.writeClasses();
        a.write(">");
        l++;
        for (var i = 0; i < S.length; i++) {
            sap.ui.commons.TreeRenderer.renderNode(a, S[i], l, S.length, i + 1)
        }
        a.write("</ul>")
    }
};