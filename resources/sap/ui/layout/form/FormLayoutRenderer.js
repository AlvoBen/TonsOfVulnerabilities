﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.form.FormLayoutRenderer");
sap.ui.layout.form.FormLayoutRenderer = {};

sap.ui.layout.form.FormLayoutRenderer.render = function(r, l) {
    var a = r;
    var f = l.getParent();
    if (f && f instanceof sap.ui.layout.form.Form) {
        this.renderForm(a, l, f)
    }
};

sap.ui.layout.form.FormLayoutRenderer.renderForm = function(r, l, f) {
    r.write("<div");
    r.writeControlData(l);
    r.addClass(this.getMainClass());
    r.writeClasses();
    r.write(">");
    var s = sap.ui.core.theming.Parameters.get('sap.ui.layout.FormLayout:sapUiFormTitleSize');
    this.renderTitle(r, f.getTitle(), undefined, false, s, f.getId());
    this.renderContainers(r, l, f);
    r.write("</div>")
};

sap.ui.layout.form.FormLayoutRenderer.getMainClass = function() {
    return "sapUiFormLayout"
};

sap.ui.layout.form.FormLayoutRenderer.renderContainers = function(r, l, f) {
    var c = f.getFormContainers();
    for (var i = 0, a = c.length; i < a; i++) {
        var C = c[i];
        if (C.getVisible()) {
            this.renderContainer(r, l, C)
        }
    }
};

sap.ui.layout.form.FormLayoutRenderer.renderContainer = function(r, l, c) {
    var e = c.getExpandable();
    r.write("<section");
    r.writeElementData(c);
    r.addClass("sapUiFormContainer");
    if (c.getTooltip_AsString()) {
        r.writeAttributeEscaped('title', c.getTooltip_AsString())
    }
    r.writeClasses();
    r.write(">");
    this.renderTitle(r, c.getTitle(), c._oExpandButton, e, sap.ui.core.TitleLevel.H4, c.getId());
    if (e) {
        r.write("<div id='" + c.getId() + "-content'");
        if (!c.getExpanded()) {
            r.addStyle("display", "none");
            r.writeStyles()
        }
        r.write(">")
    }
    var E = c.getFormElements();
    for (var j = 0, a = E.length; j < a; j++) {
        var o = E[j];
        if (o.getVisible()) {
            this.renderElement(r, l, o)
        }
    }
    if (e) {
        r.write("</div>")
    }
    r.write("</section>")
};

sap.ui.layout.form.FormLayoutRenderer.renderElement = function(r, l, e) {
    r.write("<div");
    r.writeElementData(e);
    r.addClass("sapUiFormElement");
    r.writeClasses();
    r.write(">");
    var L = e.getLabelControl();
    if (L) {
        r.renderControl(L)
    }
    var f = e.getFields();
    if (f && f.length > 0) {
        for (var k = 0, a = f.length; k < a; k++) {
            var F = f[k];
            r.renderControl(F)
        }
    }
    r.write("</div>")
};

sap.ui.layout.form.FormLayoutRenderer.renderTitle = function(r, t, e, E, l, c) {
    if (t) {
        var L = sap.ui.core.theming.Parameters.get('sap.ui.layout.FormLayout:sapUiFormSubTitleSize');
        if (l) {
            L = l
        }
        if (typeof t !== "string" && t.getLevel() != sap.ui.core.TitleLevel.Auto) {
            L = t.getLevel()
        }
        r.write("<" + L + " ");
        r.addClass("sapUiFormTitle");
        r.addClass("sapUiFormTitle" + L);
        if (typeof t !== "string") {
            r.writeElementData(t);
            if (t.getTooltip_AsString()) {
                r.writeAttributeEscaped('title', t.getTooltip_AsString())
            }
            if (t.getEmphasized()) {
                r.addClass("sapUiFormTitleEmph")
            }
        } else {
            r.writeAttribute("id", c + "--title")
        }
        r.writeClasses();
        r.write(">");
        if (E && e) {
            r.renderControl(e)
        }
        if (typeof t === "string") {
            r.writeEscaped(t, true)
        } else {
            if (t.getIcon()) {
                r.write("<img id=\"" + t.getId() + "-ico\" src=\"");
                r.writeEscaped(t.getIcon());
                r.write("\" role=\"presentation\" alt=\"\"/>")
            }
            r.writeEscaped(t.getText(), true)
        }
        r.write("</" + L + ">")
    }
};