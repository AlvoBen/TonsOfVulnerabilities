﻿jQuery.sap.declare('sap.ui.layout.library-all');
if (!jQuery.sap.isDeclared('sap.ui.layout.GridRenderer')) {

    /*!
     * SAP UI development toolkit for HTML5 (SAPUI5)
     * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
     * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
     */

    jQuery.sap.declare("sap.ui.layout.GridRenderer");
    sap.ui.layout.GridRenderer = {};
    sap.ui.layout.GridRenderer.render = function(r, c) {
        var I = /^([L](?:[0-9]|1[0-1]))? ?([M](?:[0-9]|1[0-1]))? ?([S](?:[0-9]|1[0-1]))?$/i;
        var S = /^([L](?:[1-9]|1[0-2]))? ?([M](?:[1-9]|1[0-2]))? ?([S](?:[1-9]|1[0-2]))?$/i;
        r.write("<div");
        r.writeControlData(c);
        r.addClass("sapUiRespGrid");
        var M = sap.ui.Device.media.getCurrentRange(sap.ui.Device.media.RANGESETS.SAP_STANDARD).name;
        r.addClass("sapUiRespGridMedia-Std-" + M);
        var h = c.getHSpacing();
        if (!((h >= 0) && (h <= 2))) {
            h = 1
        }
        r.addClass("sapUiRespGridHSpace" + h);
        var v = c.getVSpacing();
        if (!((v >= 0) && (v <= 2))) {
            v = 1
        }
        r.addClass("sapUiRespGridVSpace" + v);
        var p = c.getPosition();
        if (p) {
            p = p.toUpperCase();
            if (p === sap.ui.layout.GridPosition.Center.toUpperCase()) {
                r.addClass("sapUiRespGridPosCenter")
            } else if (p === sap.ui.layout.GridPosition.Right.toUpperCase()) {
                r.addClass("sapUiRespGridPosRight")
            }
        }
        r.writeClasses();
        var w = c.getWidth();
        if (w !== "100%" && w !== "auto" && w !== "inherit") {
            if (h == 0) {
                w = "width: " + w
            } else {
                w = "width: -webkit-calc(" + w + " - " + h + "rem); width: calc(" + w + " - " + h + "rem); "
            }
            r.writeAttribute("style", w)
        }
        r.write(">");
        var a = c.getContent();
        var d = c.getDefaultSpan();
        var b = c.getDefaultIndent();
        var D = I.exec(b);
        var e = ["", "L3", "M6", "S12"];
        var f = S.exec(d);
        for (var i = 0; i < a.length; i++) {
            r.write("<div");
            var L = c._getLayoutDataForControl(a[i]);
            if (L) {
                if (L.getLinebreak() === true) {
                    r.addClass("sapUiRespGridBreak")
                } else {
                    if (L.getLinebreakL() === true) {
                        r.addClass("sapUiRespGridBreakL")
                    }
                    if (L.getLinebreakM() === true) {
                        r.addClass("sapUiRespGridBreakM")
                    }
                    if (L.getLinebreakS() === true) {
                        r.addClass("sapUiRespGridBreakS")
                    }
                }
                var g;
                var k = L.getSpan();
                if (!k || !k.lenght == 0) {
                    g = f
                } else {
                    g = S.exec(k)
                }
                if (g) {
                    for (var j = 1; j < g.length; j++) {
                        var n = g[j];
                        if (!n) {
                            n = f[j];
                            if (!n) {
                                n = e[j]
                            }
                        }
                        var o = L.getSpanLarge();
                        var q = L.getSpanMedium();
                        var t = L.getSpanSmall();
                        n = n.toUpperCase();
                        if ((n.substr(0, 1) === "L") && (o > 0) && (o < 13)) {
                            r.addClass("sapUiRespGridSpanL" + o)
                        } else if ((n.substr(0, 1) === "M") && (q > 0) && (q < 13)) {
                            r.addClass("sapUiRespGridSpanM" + q)
                        } else if ((n.substr(0, 1) === "S") && (t > 0) && (t < 13)) {
                            r.addClass("sapUiRespGridSpanS" + t)
                        } else {
                            r.addClass("sapUiRespGridSpan" + n)
                        }
                    }
                }
                var u;
                var x = L.getIndent();
                if (!x || x.length == 0) {
                    u = D
                } else {
                    u = I.exec(x)
                }
                if (!u) {
                    u = D;
                    if (!u) {
                        u = undefined
                    }
                }
                if (u) {
                    for (var j = 1; j < u.length; j++) {
                        var y = u[j];
                        if (!y) {
                            if (D && D[j]) {
                                y = D[j]
                            }
                        }
                        if (y) {
                            y = y.toUpperCase();
                            if (!(/^(L0)? ?(M0)? ?(S0)?$/.exec(y))) {
                                var z = L.getIndentLarge();
                                var A = L.getIndentMedium();
                                var B = L.getIndentSmall();
                                if ((y.substr(0, 1) === "L") && (z > 0) && (z < 12)) {
                                    r.addClass("sapUiRespGridIndentL" + z)
                                } else if ((y.substr(0, 1) === "M") && (A > 0) && (A < 12)) {
                                    r.addClass("sapUiRespGridIndentM" + A)
                                } else if ((y.substr(0, 1) === "S") && (B > 0) && (B < 12)) {
                                    r.addClass("sapUiRespGridIndentS" + B)
                                } else {
                                    r.addClass("sapUiRespGridIndent" + y)
                                }
                            }
                        }
                    }
                }
                var l = L.getVisibleOnLarge(),
                    m = L.getVisibleOnMedium(),
                    s = L.getVisibleOnSmall();
                if (!l && m && s) {
                    r.addClass("sapUiRespGridHiddenL")
                } else if (!l && !m && s) {
                    r.addClass("sapUiRespGridVisibleS")
                } else if (l && !m && !s) {
                    r.addClass("sapUiRespGridVisibleL")
                } else if (!l && m && !s) {
                    r.addClass("sapUiRespGridVisibleM")
                } else if (l && !m && s) {
                    r.addClass("sapUiRespGridHiddenM")
                } else if (l && m && !s) {
                    r.addClass("sapUiRespGridHiddenS")
                }
                var C = L.getMoveBackwards();
                if (C && C.length > 0) {
                    var E = I.exec(C);
                    if (E) {
                        for (var j = 1; j < E.length; j++) {
                            var F = E[j];
                            if (F) {
                                r.addClass("sapUiRespGridBwd" + F.toUpperCase())
                            }
                        }
                    }
                }
                var G = L.getMoveForward();
                if (G && G.length > 0) {
                    var H = I.exec(G);
                    if (H) {
                        for (var j = 1; j < H.length; j++) {
                            var J = H[j];
                            if (J) {
                                r.addClass("sapUiRespGridFwd" + J.toUpperCase())
                            }
                        }
                    }
                }
                if (L._sStylesInternal) {
                    r.addClass(L._sStylesInternal)
                }
            }
            if (!L) {
                var n = "";
                if (f) {
                    for (var j = 1; j < f.length; j++) {
                        n = f[j];
                        if (!n) {
                            n = e[j]
                        }
                        r.addClass("sapUiRespGridSpan" + n.toUpperCase())
                    }
                } else {
                    for (var j = 1; j < e.length; j++) {
                        n = e[j];
                        r.addClass("sapUiRespGridSpan" + n.toUpperCase())
                    }
                }
                var y = "";
                if (D) {
                    for (var j = 1; j < D.length; j++) {
                        y = D[j];
                        if (y && (y.substr(1, 1) !== "0")) {
                            r.addClass("sapUiRespGridIndent" + y.toUpperCase())
                        }
                    }
                }
            }
            r.writeClasses();
            r.write(">");
            r.renderControl(a[i]);
            r.write("</div>")
        }
        r.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.HorizontalLayoutRenderer')) {
    jQuery.sap.declare("sap.ui.layout.HorizontalLayoutRenderer");
    sap.ui.layout.HorizontalLayoutRenderer = {};
    sap.ui.layout.HorizontalLayoutRenderer.render = function(r, c) {
        if (!c.getVisible()) {
            return
        }
        var a = r;
        var n = !c.getAllowWrapping();
        a.write("<div");
        a.writeControlData(c);
        a.addClass("sapUiHLayout");
        if (n) {
            a.addClass("sapUiHLayoutNoWrap")
        }
        a.writeClasses();
        a.write(">");
        var C = c.getContent();
        for (var i = 0; i < C.length; i++) {
            if (n) {
                a.write("<div class='sapUiHLayoutChildWrapper'>")
            }
            a.renderControl(C[i]);
            if (n) {
                a.write("</div>")
            }
        }
        a.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.ResponsiveFlowLayoutRenderer')) {
    jQuery.sap.declare("sap.ui.layout.ResponsiveFlowLayoutRenderer");
    sap.ui.layout.ResponsiveFlowLayoutRenderer = {};
    (function() {
        sap.ui.layout.ResponsiveFlowLayoutRenderer.render = function(r, c) {
            r.write("<div");
            r.writeControlData(c);
            r.addClass("sapUiRFL");
            r.writeClasses();
            r.write(">");
            r.write("</div>")
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.VerticalLayoutRenderer')) {
    jQuery.sap.declare("sap.ui.layout.VerticalLayoutRenderer");
    sap.ui.layout.VerticalLayoutRenderer = {};
    sap.ui.layout.VerticalLayoutRenderer.render = function(r, v) {
        var a = r;
        if (!v.getVisible()) {
            return
        }
        a.write("<DIV");
        a.writeControlData(v);
        a.addClass("sapUiVlt");
        a.addClass("sapuiVlt");
        if (v.getWidth() && v.getWidth() != '') {
            a.addStyle("width", v.getWidth())
        }
        a.writeStyles();
        a.writeClasses();
        a.write(">");
        var c = v.getContent();
        for (var i = 0; i < c.length; i++) {
            a.write("<DIV class=\"sapUiVltCell sapuiVltCell\">");
            a.renderControl(c[i]);
            a.write("</DIV>")
        }
        a.write("</DIV>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.FormLayoutRenderer')) {
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
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.FormRenderer')) {
    jQuery.sap.declare("sap.ui.layout.form.FormRenderer");
    sap.ui.layout.form.FormRenderer = {};
    sap.ui.layout.form.FormRenderer.render = function(r, f) {
        if (!f.getVisible()) {
            return
        }
        var a = r;
        var l = f.getLayout();
        a.write("<div");
        a.writeControlData(f);
        a.addClass("sapUiForm");
        var c = sap.ui.layout.form.FormHelper.addFormClass();
        if (c) {
            a.addClass(c)
        }
        if (f.getWidth()) {
            a.addStyle("width", f.getWidth())
        }
        if (f.getTooltip_AsString()) {
            a.writeAttributeEscaped('title', f.getTooltip_AsString())
        }
        a.writeClasses();
        a.writeStyles();
        var A = {
            role: "form"
        };
        var t = f.getTitle();
        if (t) {
            var i = "";
            if (typeof t == "string") {
                i = f.getId() + "--title"
            } else {
                i = t.getId()
            }
            A["describedby"] = i
        }
        a.writeAccessibilityState(f, A);
        a.write(">");
        if (l) {
            a.renderControl(l)
        } else {
            jQuery.sap.log.warning("Form \"" + f.getId() + "\" - Layout missing!", "Renderer", "Form")
        }
        a.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.GridLayoutRenderer')) {
    jQuery.sap.require('sap.ui.core.Renderer');
    jQuery.sap.declare("sap.ui.layout.form.GridLayoutRenderer");
    sap.ui.layout.form.GridLayoutRenderer = sap.ui.core.Renderer.extend(sap.ui.layout.form.FormLayoutRenderer);
    sap.ui.layout.form.GridLayoutRenderer.renderForm = function(r, l, f) {
        var s = l.getSingleColumn();
        var c = 16;
        var S = false;
        var C = 0;
        var a = f.getFormContainers();
        var b = a.length;
        if (s) {
            c = c / 2;
            C = c
        } else {
            C = c / 2;
            for (var i = 0; i < b; i++) {
                var o = this.getContainerData(l, a[i]);
                if (o && o.getHalfGrid()) {
                    S = true;
                    break
                }
            }
        }
        r.write("<table role=\"presentation\"");
        r.writeControlData(l);
        r.write(" cellpadding=\"0\" cellspacing=\"0\"");
        r.addStyle("border-collapse", "collapse");
        r.addStyle("table-layout", "fixed");
        r.addStyle("width", "100%");
        r.addClass("sapUiGrid");
        r.writeStyles();
        r.writeClasses();
        r.write(">");
        r.write("<colgroup>");
        r.write("<col span=" + C + ">");
        if (S) {
            r.write("<col class = \"sapUiGridSpace\"span=1>")
        }
        if (!s) {
            r.write("<col span=" + C + ">")
        }
        r.write("</colgroup><tbody>");
        if (f.getTitle()) {
            var t = c;
            if (S) {
                t++
            }
            r.write("<tr><th colspan=" + t + ">");
            var d = sap.ui.core.theming.Parameters.get('sap.ui.layout.FormLayout:sapUiFormTitleSize');
            this.renderTitle(r, f.getTitle(), undefined, false, d, f.getId());
            r.write("</th></tr>")
        }
        var i = 0;
        while (i < b) {
            var e = a[i];
            if (e.getVisible()) {
                var o = this.getContainerData(l, e);
                if (o && o.getHalfGrid() && !s) {
                    var g = a[i + 1];
                    var h = undefined;
                    if (g && g.getVisible()) {
                        h = this.getContainerData(l, g)
                    }
                    if (h && h.getHalfGrid()) {
                        this.renderContainerHalfSize(r, l, e, g, c);
                        i++
                    } else {
                        this.renderContainerHalfSize(r, l, e, undefined, c)
                    }
                } else {
                    this.renderContainerFullSize(r, l, e, c, S)
                }
            }
            i++
        }
        if ( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version >= 9) {
            r.write("<tr style=\"visibility:hidden;\">");
            for (var i = 0; i < c; i++) {
                r.write("<td style=\"visibility:hidden; padding:0; height: 0;\"></td>")
            }
            if (S) {
                r.write("<td style=\"visibility:hidden; padding:0; height: 0;\"></td>")
            }
            r.write("</tr>")
        }
        r.write("</tbody></table>")
    };
    sap.ui.layout.form.GridLayoutRenderer.renderContainerFullSize = function(r, l, c, C, s) {
        var e = c.getExpandable();
        var t = c.getTooltip_AsString();
        if (c.getTitle()) {
            var T = C;
            if (s) {
                T++
            }
            r.write("<tr><td colspan=" + T + " class=\"sapUiGridHeader\"");
            if (t) {
                r.writeAttributeEscaped('title', t)
            }
            r.write(">");
            this.renderTitle(r, c.getTitle(), c._oExpandButton, e, false, c.getId());
            r.write("</td></tr>")
        }
        if (!e || c.getExpanded()) {
            var E = c.getFormElements();
            var R = [];
            for (var j = 0, a = E.length; j < a; j++) {
                var o = E[j];
                if (o.getVisible()) {
                    var b = R[0] && (R[0][0] == C);
                    r.write("<tr");
                    if (R[0] != "full" && !b) {
                        r.writeElementData(o);
                        r.addClass("sapUiFormElement")
                    }
                    r.writeClasses();
                    r.write(">");
                    if (!b) {
                        R = this.renderElement(r, l, o, false, C, s, R)
                    } else {
                        R.splice(0, 1)
                    }
                    r.write("</tr>");
                    if (R[0] == "full" || b) {
                        j = j - 1
                    }
                }
            }
            if (R.length > 0) {
                for (var i = 0; i < R.length; i++) {
                    r.write("<tr></tr>")
                }
            }
        }
    };
    sap.ui.layout.form.GridLayoutRenderer.renderContainerHalfSize = function(r, l, c, C, a) {
        var b = a / 2;
        var e = c.getExpandable();
        var t = c.getTooltip_AsString();
        var T;
        var o = c.getTitle();
        var d;
        var E = [];
        if (!e || c.getExpanded()) {
            E = c.getFormElements()
        }
        var L = E.length;
        var f = [];
        var g = 0;
        var h = false;
        if (C) {
            h = C.getExpandable();
            T = C.getTooltip_AsString();
            d = C.getTitle();
            if (!h || C.getExpanded()) {
                f = C.getFormElements()
            }
            g = f.length
        }
        if (o || d) {
            r.write("<tr><td colspan=" + b + " class=\"sapUiGridHeader\"");
            if (t) {
                r.writeAttributeEscaped('title', t)
            }
            r.write(">");
            if (o) {
                this.renderTitle(r, o, c._oExpandButton, e, false, c.getId())
            }
            r.write("</td><td></td><td colspan=" + b + " class=\"sapUiGridHeader\"");
            if (T) {
                r.writeAttributeEscaped('title', T)
            }
            r.write(">");
            if (d) {
                this.renderTitle(r, d, C._oExpandButton, h, false, C.getId())
            }
            r.write("</td></tr>")
        }
        if ((!e || c.getExpanded()) || (!h || C.getExpanded())) {
            var R = [],
                j = [];
            var k = 0,
                m = 0;
            while (k < L || m < g) {
                var n = E[k];
                var p = f[m];
                var q = R[0] && (R[0][0] == b);
                var s = j[0] && (j[0][0] == b);
                if ((n && n.getVisible()) || (p && p.getVisible()) || q || s) {
                    r.write("<tr>");
                    if (!q) {
                        if (n && n.getVisible() && (!e || c.getExpanded())) {
                            R = this.renderElement(r, l, n, true, b, false, R)
                        } else {
                            r.write("<td colspan=" + b + "></td>")
                        }
                        if (R[0] != "full") {
                            k++
                        }
                    } else {
                        if (R[0][2] > 0) {
                            r.write("<td colspan=" + R[0][2] + "></td>")
                        }
                        R.splice(0, 1)
                    }
                    r.write("<td></td>");
                    if (!s) {
                        if (p && p.getVisible() && (!h || C.getExpanded())) {
                            j = this.renderElement(r, l, p, true, b, false, j)
                        } else {
                            r.write("<td colspan=" + b + "></td>")
                        }
                        if (j[0] != "full") {
                            m++
                        }
                    } else {
                        if (j[0][2] > 0) {
                            r.write("<td colspan=" + j[0][2] + "></td>")
                        }
                        j.splice(0, 1)
                    }
                    r.write("</tr>")
                } else {
                    k++;
                    m++
                }
            }
            if (R.length > 0 || j.length > 0) {
                for (var i = 0; i < R.length || i < j.length; i++) {
                    r.write("<tr></tr>")
                }
            }
        }
    };
    sap.ui.layout.form.GridLayoutRenderer.renderElement = function(r, l, e, h, c, s, R) {
        var L = e.getLabelControl();
        var a = 0;
        var f = e.getFields();
        var C = 0;
        var A = 0;
        var m = false;
        if (f.length == 1 && this.getElementData(l, f[0]) && this.getElementData(l, f[0]).getHCells() == "full") {
            if (R.length > 0 && R[0] != "full") {
                jQuery.sap.log.error("Element \"" + e.getId() + "\" - Too much fields for one row!", "Renderer", "GridLayout");
                return R
            }
            if (s) {
                c = c + 1
            }
            if (L && R[0] != "full") {
                r.write("<td colspan=" + c + " class=\"sapUiGridLabelFull\">");
                r.renderControl(L);
                r.write("</td>");
                return ["full"]
            } else {
                R.splice(0, 1);
                var b = this.getElementData(l, f[0]).getVCells();
                r.write("<td colspan=" + c);
                if (b > 1 && h) {
                    r.write(" rowspan=" + b);
                    for (var x = 0; x < b - 1; x++) {
                        R.push([c, undefined, false])
                    }
                }
                r.write(" >");
                r.renderControl(f[0]);
                r.write("</td>");
                return R
            }
        }
        if (R.length > 0 && R[0][0] > 0) {
            c = c - R[0][0] + R[0][2];
            m = R[0][1];
            a = R[0][2];
            R.splice(0, 1)
        }
        var d = a;
        if (L || a > 0) {
            d = 3;
            if (L && a == 0) {
                var E = this.getElementData(l, L);
                if (E) {
                    var g = E.getHCells();
                    if (g != "auto" && g != "full") {
                        d = parseInt(g)
                    }
                }
            }
            r.write("<td colspan=" + d + " class=\"sapUiGridLabel\">");
            if (L) {
                r.renderControl(L)
            }
            c = c - d;
            r.write("</td>")
        }
        if (f && f.length > 0) {
            var j = c;
            var k = f.length;
            for (var i = 0, n = f.length; i < n; i++) {
                var F = f[i];
                var E = this.getElementData(l, F);
                if (E && E.getHCells() != "auto") {
                    j = j - parseInt(E.getHCells());
                    k = k - 1
                }
            }
            for (var i = 0, o = 0, n = f.length; i < n; i++) {
                var F = f[i];
                var E = this.getElementData(l, F);
                var g = "auto";
                var p = 1;
                var b = 1;
                if (E) {
                    g = E.getHCells();
                    b = E.getVCells()
                }
                if (g == "auto") {
                    if (j > 0) {
                        p = Math.floor(j / k);
                        if (p < 1) {
                            p = 1
                        }
                        o++;
                        A = A + p;
                        if ((o == k) && (j > A)) {
                            p = p + (j - A)
                        }
                    } else {
                        p = 1
                    }
                } else {
                    p = parseInt(g)
                }
                C = C + p;
                if (C > c) {
                    jQuery.sap.log.error("Element \"" + e.getId() + "\" - Too much fields for one row!", "Renderer", "GridLayout");
                    C = C - p;
                    break
                }
                if (b > 1) {
                    for (var x = 0; x < b - 1; x++) {
                        if (L) {
                            a = d
                        }
                        if (R.length > x) {
                            R[x][0] = R[x][0] + p;
                            R[x][2] = a
                        } else {
                            R.push([d + p, undefined, a])
                        }
                    }
                }
                if (s && C >= Math.floor(c / 2) && !m) {
                    p = p + 1;
                    m = true;
                    if (b > 1) {
                        for (var x = 0; x < b - 1; x++) {
                            R[x][1] = true
                        }
                    }
                }
                r.write("<td");
                if (p > 1) {
                    r.write(" colspan=" + p)
                }
                if (b > 1) {
                    r.write(" rowspan=" + b)
                }
                r.write(" >");
                r.renderControl(F);
                r.write("</td>")
            }
        }
        if (C < c) {
            var q = c - C;
            if (!h && s && !m) {
                q++
            }
            r.write("<td colspan=" + q + " ></td>")
        }
        return R
    };
    sap.ui.layout.form.GridLayoutRenderer.getContainerData = function(l, c) {
        return l.getLayoutDataForElement(c, "sap.ui.layout.form.GridContainerData")
    };
    sap.ui.layout.form.GridLayoutRenderer.getElementData = function(l, c) {
        return l.getLayoutDataForElement(c, "sap.ui.layout.form.GridElementData")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.ResponsiveGridLayoutRenderer')) {
    jQuery.sap.require('sap.ui.core.Renderer');
    jQuery.sap.declare("sap.ui.layout.form.ResponsiveGridLayoutRenderer");
    sap.ui.layout.form.ResponsiveGridLayoutRenderer = sap.ui.core.Renderer.extend(sap.ui.layout.form.FormLayoutRenderer);
    sap.ui.layout.form.ResponsiveGridLayoutRenderer.getMainClass = function() {
        return "sapUiFormResGrid"
    };
    sap.ui.layout.form.ResponsiveGridLayoutRenderer.renderContainers = function(r, l, f) {
        var c = f.getFormContainers();
        var L = 0;
        for (var i = 0; i < c.length; i++) {
            var C = c[i];
            if (C.getVisible()) {
                L++
            }
        }
        if (L > 0) {
            if (L > 1) {
                r.renderControl(l._mainGrid)
            } else if (l.mContainers[c[0].getId()][0]) {
                r.renderControl(l.mContainers[c[0].getId()][0])
            } else {
                r.renderControl(l.mContainers[c[0].getId()][1])
            }
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.ResponsiveLayoutRenderer')) {
    jQuery.sap.require('sap.ui.core.Renderer');
    jQuery.sap.declare("sap.ui.layout.form.ResponsiveLayoutRenderer");
    sap.ui.layout.form.ResponsiveLayoutRenderer = sap.ui.core.Renderer.extend(sap.ui.layout.form.FormLayoutRenderer);
    sap.ui.layout.form.ResponsiveLayoutRenderer.getMainClass = function() {
        return "sapUiFormResLayout"
    };
    sap.ui.layout.form.ResponsiveLayoutRenderer.renderContainers = function(r, l, f) {
        var c = f.getFormContainers();
        var L = 0;
        for (var i = 0; i < c.length; i++) {
            var C = c[i];
            if (C.getVisible()) {
                L++
            }
        }
        if (L > 0) {
            if (L > 1) {
                r.renderControl(l._mainRFLayout)
            } else if (l.mContainers[c[0].getId()][0]) {
                r.renderControl(l.mContainers[c[0].getId()][0])
            } else {
                r.renderControl(l.mContainers[c[0].getId()][1])
            }
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.SimpleFormRenderer')) {
    jQuery.sap.declare("sap.ui.layout.form.SimpleFormRenderer");
    sap.ui.layout.form.SimpleFormRenderer = {};
    sap.ui.layout.form.SimpleFormRenderer.render = function(r, c) {
        r.write("<div");
        r.writeControlData(c);
        r.addClass("sapUiSimpleForm");
        r.writeClasses();
        r.write(">");
        var f = c.getAggregation("form");
        r.renderControl(f);
        r.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.library')) {
    jQuery.sap.declare("sap.ui.layout.library");
    jQuery.sap.require('sap.ui.core.Core');
    jQuery.sap.require('sap.ui.core.library');
    sap.ui.getCore().initLibrary({
        name: "sap.ui.layout",
        dependencies: ["sap.ui.core"],
        types: ["sap.ui.layout.GridIndent", "sap.ui.layout.GridPosition", "sap.ui.layout.GridSpan", "sap.ui.layout.form.GridElementCells", "sap.ui.layout.form.SimpleFormLayout"],
        interfaces: [],
        controls: ["sap.ui.layout.Grid", "sap.ui.layout.HorizontalLayout", "sap.ui.layout.ResponsiveFlowLayout", "sap.ui.layout.VerticalLayout", "sap.ui.layout.form.Form", "sap.ui.layout.form.FormLayout", "sap.ui.layout.form.GridLayout", "sap.ui.layout.form.ResponsiveGridLayout", "sap.ui.layout.form.ResponsiveLayout", "sap.ui.layout.form.SimpleForm"],
        elements: ["sap.ui.layout.GridData", "sap.ui.layout.ResponsiveFlowLayoutData", "sap.ui.layout.form.FormContainer", "sap.ui.layout.form.FormElement", "sap.ui.layout.form.GridContainerData", "sap.ui.layout.form.GridElementData"],
        version: "1.16.8-SNAPSHOT"
    });
    jQuery.sap.declare('sap.ui.layout.GridIndent');
    jQuery.sap.require('sap.ui.base.DataType');
    sap.ui.layout.GridIndent = sap.ui.base.DataType.createType('sap.ui.layout.GridIndent', {
        isValid: function(v) {
            return /^(([Ll](?:[0-9]|1[0-1]))? ?([Mm](?:[0-9]|1[0-1]))? ?([Ss](?:[0-9]|1[0-1]))?)$/.test(v)
        }
    }, sap.ui.base.DataType.getType('string'));
    jQuery.sap.declare("sap.ui.layout.GridPosition");
    sap.ui.layout.GridPosition = {
        Left: "Left",
        Right: "Right",
        Center: "Center"
    };
    jQuery.sap.declare('sap.ui.layout.GridSpan');
    jQuery.sap.require('sap.ui.base.DataType');
    sap.ui.layout.GridSpan = sap.ui.base.DataType.createType('sap.ui.layout.GridSpan', {
        isValid: function(v) {
            return /^(([Ll](?:[1-9]|1[0-2]))? ?([Mm](?:[1-9]|1[0-2]))? ?([Ss](?:[1-9]|1[0-2]))?)$/.test(v)
        }
    }, sap.ui.base.DataType.getType('string'));
    jQuery.sap.declare('sap.ui.layout.form.GridElementCells');
    jQuery.sap.require('sap.ui.base.DataType');
    sap.ui.layout.form.GridElementCells = sap.ui.base.DataType.createType('sap.ui.layout.form.GridElementCells', {
        isValid: function(v) {
            return /^(auto|full|([1-9]|1[0-6]))$/.test(v)
        }
    }, sap.ui.base.DataType.getType('string'));
    jQuery.sap.declare("sap.ui.layout.form.SimpleFormLayout");
    sap.ui.layout.form.SimpleFormLayout = {
        ResponsiveLayout: "ResponsiveLayout",
        GridLayout: "GridLayout",
        ResponsiveGridLayout: "ResponsiveGridLayout"
    };
    if (!sap.ui.layout.form.FormHelper) {
        sap.ui.layout.form.FormHelper = {
            createLabel: function(t) {
                throw new Error("no Label control available!")
            },
            createButton: function(i, p, t) {
                throw new Error("no Button control available!")
            },
            setButtonContent: function(b, t, T, i, I) {
                throw new Error("no Button control available!")
            },
            addFormClass: function() {
                return null
            },
            bArrowKeySupport: true,
            bFinal: false
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.Grid')) {
    jQuery.sap.declare("sap.ui.layout.Grid");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.Grid", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "width": {
                    type: "sap.ui.core.CSSSize",
                    group: "Dimension",
                    defaultValue: '100%'
                },
                "vSpacing": {
                    type: "int",
                    group: "Dimension",
                    defaultValue: 1
                },
                "hSpacing": {
                    type: "int",
                    group: "Dimension",
                    defaultValue: 1
                },
                "position": {
                    type: "sap.ui.layout.GridPosition",
                    group: "Dimension",
                    defaultValue: "Left"
                },
                "defaultSpan": {
                    type: "sap.ui.layout.GridSpan",
                    group: "Behavior",
                    defaultValue: "L3 M6 S12"
                },
                "defaultIndent": {
                    type: "sap.ui.layout.GridIndent",
                    group: "Behavior",
                    defaultValue: "L0 M0 S0"
                },
                "containerQuery": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: false
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                }
            }
        }
    });
    (function() {
        sap.ui.layout.Grid.prototype.init = function() {
            this._iBreakPointTablet = sap.ui.Device.media._predefinedRangeSets[sap.ui.Device.media.RANGESETS.SAP_STANDARD].points[0];
            this._iBreakPointDesktop = sap.ui.Device.media._predefinedRangeSets[sap.ui.Device.media.RANGESETS.SAP_STANDARD].points[1]
        };
        sap.ui.layout.Grid.prototype.onAfterRendering = function() {
            if (this.getContainerQuery()) {
                this._sContainerResizeListener = sap.ui.core.ResizeHandler.register(this, jQuery.proxy(this._onParentResize, this));
                this._onParentResize()
            } else {
                sap.ui.Device.media.attachHandler(this._handleMediaChange, this, sap.ui.Device.media.RANGESETS.SAP_STANDARD)
            }
        };
        sap.ui.layout.Grid.prototype.onBeforeRendering = function() {
            this._cleanup()
        };
        sap.ui.layout.Grid.prototype.exit = function() {
            this._cleanup()
        };
        sap.ui.layout.Grid.prototype._cleanup = function() {
            if (this._sContainerResizeListener) {
                sap.ui.core.ResizeHandler.deregister(this._sContainerResizeListener);
                this._sContainerResizeListener = null
            }
            sap.ui.Device.media.detachHandler(this._handleMediaChange, this, sap.ui.Device.media.RANGESETS.SAP_STANDARD)
        };
        sap.ui.layout.Grid.prototype._handleMediaChange = function(p) {
            this._toggleClass(p.name)
        };
        sap.ui.layout.Grid.prototype._setBreakPointTablet = function(b) {
            this._iBreakPointTablet = b
        };
        sap.ui.layout.Grid.prototype._setBreakPointDesktop = function(b) {
            this._iBreakPointDesktop = b
        };
        sap.ui.layout.Grid.prototype._onParentResize = function() {
            var d = this.getDomRef();
            if (!d) {
                this._cleanup();
                return
            }
            if (!jQuery(d).is(":visible")) {
                return
            }
            var c = d.clientWidth;
            if (c <= this._iBreakPointTablet) {
                this._toggleClass("Phone")
            } else if ((c > this._iBreakPointTablet) && (c <= this._iBreakPointDesktop)) {
                this._toggleClass("Tablet")
            } else {
                this._toggleClass("Desktop")
            }
        };
        sap.ui.layout.Grid.prototype._toggleClass = function(m) {
            var d = this.$();
            if (!d) return;
            if (d.hasClass("sapUiRespGridMedia-Std-" + m)) {
                return
            }
            d.toggleClass("sapUiRespGridMedia-Std-" + m, true);
            if (m === "Phone") {
                d.toggleClass("sapUiRespGridMedia-Std-Desktop", false).toggleClass("sapUiRespGridMedia-Std-Tablet", false)
            } else if (m === "Tablet") {
                d.toggleClass("sapUiRespGridMedia-Std-Desktop", false).toggleClass("sapUiRespGridMedia-Std-Phone", false)
            } else {
                d.toggleClass("sapUiRespGridMedia-Std-Phone", false).toggleClass("sapUiRespGridMedia-Std-Tablet", false)
            }
            this.fireEvent("mediaChanged", {
                media: m
            })
        };
        sap.ui.layout.Grid.prototype._getLayoutDataForControl = function(c) {
            var l = c.getLayoutData();
            if (!l) {
                return undefined
            } else if (l instanceof sap.ui.layout.GridData) {
                return l
            } else if (l.getMetadata().getName() == "sap.ui.core.VariantLayoutData") {
                var L = l.getMultipleLayoutData();
                for (var i = 0; i < L.length; i++) {
                    var o = L[i];
                    if (o instanceof sap.ui.layout.GridData) {
                        return o
                    }
                }
            }
        };
        sap.ui.layout.Grid.prototype.onLayoutDataChange = function(e) {
            if (this.getDomRef()) {
                this.invalidate()
            }
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.GridData')) {
    jQuery.sap.declare("sap.ui.layout.GridData");
    jQuery.sap.require('sap.ui.core.LayoutData');
    sap.ui.core.LayoutData.extend("sap.ui.layout.GridData", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "span": {
                    type: "sap.ui.layout.GridSpan",
                    group: "Behavior",
                    defaultValue: null
                },
                "indent": {
                    type: "sap.ui.layout.GridIndent",
                    group: "Behavior",
                    defaultValue: null
                },
                "visibleOnLarge": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: true
                },
                "visibleOnMedium": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: true
                },
                "visibleOnSmall": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: true
                },
                "moveBackwards": {
                    type: "sap.ui.layout.GridIndent",
                    group: "Misc",
                    defaultValue: null
                },
                "moveForward": {
                    type: "sap.ui.layout.GridIndent",
                    group: "Misc",
                    defaultValue: null
                },
                "linebreak": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "linebreakL": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "linebreakM": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "linebreakS": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "spanLarge": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: null
                },
                "spanMedium": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: null
                },
                "spanSmall": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: null
                },
                "indentLarge": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: null
                },
                "indentMedium": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: null
                },
                "indentSmall": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: null
                }
            }
        }
    });
    (function() {
        sap.ui.layout.GridData.prototype._setStylesInternal = function(s) {
            if (s && s.length > 0) {
                this._sStylesInternal = s
            } else {
                this._sStylesInternal = undefined
            }
        };
        sap.ui.layout.GridData.prototype._getEffectiveSpanLarge = function() {
            var s = this.getSpanLarge();
            if (s && (s > 0) && (s < 13)) {
                return s
            }
            var S = /L([1-9]|1[0-2])(?:\s|$)/i;
            var a = S.exec(this.getSpan());
            if (a) {
                var b = a[0];
                if (b) {
                    b = b.toUpperCase();
                    if (b.substr(0, 1) === "L") {
                        return parseInt(b.substr(1))
                    }
                }
            }
            return undefined
        };
        sap.ui.layout.GridData.prototype._getEffectiveSpanMedium = function() {
            var s = this.getSpanMedium();
            if (s && (s > 0) && (s < 13)) {
                return s
            }
            var S = /M([1-9]|1[0-2])(?:\s|$)/i;
            var a = S.exec(this.getSpan());
            if (a) {
                var b = a[0];
                if (b) {
                    b = b.toUpperCase();
                    if (b.substr(0, 1) === "M") {
                        return parseInt(b.substr(1))
                    }
                }
            }
            return undefined
        };
        sap.ui.layout.GridData.prototype._getEffectiveSpanSmall = function() {
            var s = this.getSpanSmall();
            if (s && (s > 0) && (s < 13)) {
                return s
            }
            var S = /S([1-9]|1[0-2])(?:\s|$)/i;
            var a = S.exec(this.getSpan());
            if (a) {
                var b = a[0];
                if (b) {
                    b = b.toUpperCase();
                    if (b.substr(0, 1) === "S") {
                        return parseInt(b.substr(1))
                    }
                }
            }
            return undefined
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.HorizontalLayout')) {
    jQuery.sap.declare("sap.ui.layout.HorizontalLayout");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.HorizontalLayout", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "allowWrapping": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "visible": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: true
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                }
            }
        }
    })
};
if (!jQuery.sap.isDeclared('sap.ui.layout.ResponsiveFlowLayoutData')) {
    jQuery.sap.declare("sap.ui.layout.ResponsiveFlowLayoutData");
    jQuery.sap.require('sap.ui.core.LayoutData');
    sap.ui.core.LayoutData.extend("sap.ui.layout.ResponsiveFlowLayoutData", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "minWidth": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 100
                },
                "weight": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 1
                },
                "linebreak": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "margin": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                },
                "linebreakable": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                }
            }
        }
    });

    /*!
     * @copyright@
     */

    sap.ui.layout.ResponsiveFlowLayoutData.MIN_WIDTH = 100;
    sap.ui.layout.ResponsiveFlowLayoutData.WEIGHT = 1;
    sap.ui.layout.ResponsiveFlowLayoutData.LINEBREAK = false;
    sap.ui.layout.ResponsiveFlowLayoutData.MARGIN = true;
    sap.ui.layout.ResponsiveFlowLayoutData.LINEBREAKABLE = true;
    sap.ui.layout.ResponsiveFlowLayoutData.prototype.setWeight = function(w) {
        if (w >= 1) {
            this.setProperty("weight", w)
        } else {
            jQuery.sap.log.warning("Values smaller than 1 are not valid. Default value '1' is used instead", this);
            this.setProperty("weight", sap.ui.layout.ResponsiveFlowLayoutData.WEIGHT)
        }
        return this
    };
    sap.ui.layout.ResponsiveFlowLayoutData.prototype.setLinebreak = function(l) {
        if (this.getLinebreakable() == false && l) {
            jQuery.sap.log.warning("Setting 'linebreak' AND 'linebreakable' doesn't make any sense! Please set either 'linebreak' or 'linebreakable'", this)
        } else {
            this.setProperty("linebreak", l)
        }
    };
    sap.ui.layout.ResponsiveFlowLayoutData.prototype.setLinebreakable = function(l) {
        if (this.getLinebreak() === true && l === false) {
            jQuery.sap.log.warning("Setting 'linebreak' AND 'linebreakable' doesn't make any sense! Please set either 'linebreak' or 'linebreakable'", this)
        } else {
            this.setProperty("linebreakable", l)
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.layout.VerticalLayout')) {

    /*!
     * SAP UI development toolkit for HTML5 (SAPUI5)
     * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
     * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
     */

    jQuery.sap.declare("sap.ui.layout.VerticalLayout");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.VerticalLayout", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "width": {
                    type: "sap.ui.core.CSSSize",
                    group: "Dimension",
                    defaultValue: null
                },
                "enabled": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: true
                },
                "visible": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: true
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                }
            }
        }
    });
    jQuery.sap.require('sap.ui.core.EnabledPropagator');
    sap.ui.core.EnabledPropagator.call(sap.ui.layout.VerticalLayout.prototype)
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.Form')) {
    jQuery.sap.declare("sap.ui.layout.form.Form");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.form.Form", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "width": {
                    type: "sap.ui.core.CSSSize",
                    group: "Dimension",
                    defaultValue: null
                },
                "visible": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                }
            },
            defaultAggregation: "formContainers",
            aggregations: {
                "formContainers": {
                    type: "sap.ui.layout.form.FormContainer",
                    multiple: true,
                    singularName: "formContainer"
                },
                "title": {
                    type: "sap.ui.core.Title",
                    altTypes: ["string"],
                    multiple: false
                },
                "layout": {
                    type: "sap.ui.layout.form.FormLayout",
                    multiple: false
                }
            }
        }
    });
    (function() {
        sap.ui.layout.form.Form.prototype.toggleContainerExpanded = function(c) {
            var l = this.getLayout();
            if (l) {
                l.toggleContainerExpanded(c)
            }
        };
        sap.ui.layout.form.Form.prototype.contentOnAfterRendering = function(f, c) {
            var l = this.getLayout();
            if (l && l.contentOnAfterRendering) {
                l.contentOnAfterRendering(f, c)
            }
        };
        sap.ui.layout.form.Form.prototype.onLayoutDataChange = function(e) {
            var l = this.getLayout();
            if (l && l.onLayoutDataChange) {
                l.onLayoutDataChange(e)
            }
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.FormContainer')) {
    jQuery.sap.declare("sap.ui.layout.form.FormContainer");
    jQuery.sap.require('sap.ui.core.Element');
    sap.ui.core.Element.extend("sap.ui.layout.form.FormContainer", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "expanded": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                },
                "expandable": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                },
                "visible": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                }
            },
            defaultAggregation: "formElements",
            aggregations: {
                "formElements": {
                    type: "sap.ui.layout.form.FormElement",
                    multiple: true,
                    singularName: "formElement"
                },
                "title": {
                    type: "sap.ui.core.Title",
                    altTypes: ["string"],
                    multiple: false
                }
            }
        }
    });
    jQuery.sap.require('sap.ui.core.EnabledPropagator');
    jQuery.sap.require('sap.ui.core.theming.Parameters');
    (function() {
        sap.ui.layout.form.FormContainer.prototype.init = function() {
            this._rb = sap.ui.getCore().getLibraryResourceBundle("sap.ui.layout")
        };
        sap.ui.layout.form.FormContainer.prototype.exit = function() {
            if (this._oExpandButton) {
                this._oExpandButton.destroy();
                delete this._oExpandButton
            }
            this._rb = undefined
        };
        sap.ui.layout.form.FormContainer.prototype.setExpandable = function(e) {
            this.setProperty("expandable", e);
            if (e) {
                var t = this;
                if (!this._oExpandButton) {
                    this._oExpandButton = sap.ui.layout.form.FormHelper.createButton(this.getId() + "--Exp", h, t);
                    this._oExpandButton.setParent(this)
                }
                _(t)
            }
        };
        sap.ui.layout.form.FormContainer.prototype.setExpanded = function(e) {
            this.setProperty("expanded", e, true);
            var t = this;
            _(t);
            var f = this.getParent();
            if (f && f.toggleContainerExpanded) {
                f.toggleContainerExpanded(t)
            }
        };
        sap.ui.layout.form.FormContainer.prototype.contentOnAfterRendering = function(f, c) {
            var p = this.getParent();
            if (p && p.contentOnAfterRendering) {
                p.contentOnAfterRendering(f, c)
            }
        };
        sap.ui.layout.form.FormContainer.prototype.onLayoutDataChange = function(e) {
            var p = this.getParent();
            if (p && p.onLayoutDataChange) {
                p.onLayoutDataChange(e)
            }
        };

        function _(c) {
            if (!c._oExpandButton) {
                return
            }
            var i, I, t, T;
            if (c.getExpanded()) {
                i = sap.ui.core.theming.Parameters.get('sapUiFormContainerColImageURL');
                I = sap.ui.core.theming.Parameters.get('sapUiFormContainerColImageDownURL');
                t = "-";
                T = c._rb.getText("FORM_COLLAPSE")
            } else {
                i = sap.ui.core.theming.Parameters.get('sapUiFormContainerExpImageURL');
                I = sap.ui.core.theming.Parameters.get('sapUiFormContainerExpImageDownURL');
                t = "+";
                T = c._rb.getText("FORM_EXPAND")
            }
            if (i) {
                i = jQuery.sap.getModulePath("sap.ui.layout", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + i;
                t = ""
            }
            if (I) {
                I = jQuery.sap.getModulePath("sap.ui.layout", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + I
            }
            sap.ui.layout.form.FormHelper.setButtonContent(c._oExpandButton, t, T, i, I)
        };

        function h(e) {
            this.setExpanded(!this.getExpanded())
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.FormElement')) {
    jQuery.sap.declare("sap.ui.layout.form.FormElement");
    jQuery.sap.require('sap.ui.core.Element');
    sap.ui.core.Element.extend("sap.ui.layout.form.FormElement", {
        metadata: {
            publicMethods: ["getLabelControl"],
            library: "sap.ui.layout",
            properties: {
                "visible": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                }
            },
            defaultAggregation: "fields",
            aggregations: {
                "label": {
                    type: "sap.ui.core.Label",
                    altTypes: ["string"],
                    multiple: false
                },
                "fields": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "field"
                }
            }
        }
    });
    jQuery.sap.require('sap.ui.core.EnabledPropagator');
    (function() {
        sap.ui.layout.form.FormElement.prototype.init = function() {
            this._oFieldDelegate = {
                oElement: this,
                onAfterRendering: b
            }
        };
        sap.ui.layout.form.FormElement.prototype.exit = function() {
            if (this._oLabel) {
                this._oLabel.destroy();
                delete this._oLabel
            }
            this._oFieldDelegate = undefined
        };
        sap.ui.layout.form.FormElement.prototype.setLabel = function(A) {
            if (!this._oLabel) {
                var o = this.getLabel();
                if (o) {
                    if (o.isRequired) {
                        o.isRequired = o._sapui_isRequired;
                        o._sapui_isRequired = undefined
                    }
                    if (o.getLabelForRendering) {
                        o.getLabelForRendering = o._sapui_getLabelForRendering;
                        o._sapui_getLabelForRendering = undefined
                    }
                }
            }
            this.setAggregation("label", A);
            var l = A;
            if (typeof l === "string") {
                if (!this._oLabel) {
                    this._oLabel = sap.ui.layout.form.FormHelper.createLabel(l);
                    this._oLabel.setParent(this);
                    if (l.isRequired) {
                        this._oLabel.isRequired = _
                    }
                    this._oLabel.getLabelForRendering = a
                } else {
                    this._oLabel.setText(l)
                }
            } else {
                if (this._oLabel) {
                    this._oLabel.destroy();
                    delete this._oLabel
                }
                if (!l) return this;
                if (l.isRequired) {
                    l._sapui_isRequired = l.isRequired;
                    l.isRequired = _
                }
                if (l.getLabelForRendering) {
                    l._sapui_getLabelForRendering = l.getLabelForRendering;
                    l.getLabelForRendering = a
                }
            }
            return this
        };
        sap.ui.layout.form.FormElement.prototype.getLabelControl = function() {
            if (this._oLabel) {
                return this._oLabel
            } else {
                return this.getLabel()
            }
        };
        sap.ui.layout.form.FormElement.prototype.addField = function(f) {
            this.addAggregation("fields", f);
            f.addDelegate(this._oFieldDelegate);
            return this
        };
        sap.ui.layout.form.FormElement.prototype.insertField = function(f, i) {
            this.insertAggregation("fields", f, i);
            f.addDelegate(this._oFieldDelegate);
            return this
        };
        sap.ui.layout.form.FormElement.prototype.removeField = function(f) {
            var r = this.removeAggregation("fields", f);
            r.removeDelegate(this._oFieldDelegate);
            return r
        };
        sap.ui.layout.form.FormElement.prototype.removeAllFields = function() {
            var r = this.removeAllAggregation("fields");
            for (var i = 0; i < r.length; i++) {
                var R = r[i];
                R.removeDelegate(this._oFieldDelegate)
            }
            return r
        };
        sap.ui.layout.form.FormElement.prototype.destroyFields = function() {
            var f = this.getFields();
            for (var i = 0; i < f.length; i++) {
                var F = f[i];
                F.removeDelegate(this._oFieldDelegate)
            }
            this.destroyAggregation("fields");
            return this
        };
        sap.ui.layout.form.FormElement.prototype.updateFields = function() {
            var f = this.getFields();
            for (var i = 0; i < f.length; i++) {
                var F = f[i];
                F.removeDelegate(this._oFieldDelegate)
            }
            this.updateAggregation("fields");
            f = this.getFields();
            for (var i = 0; i < f.length; i++) {
                var F = f[i];
                F.addDelegate(this._oFieldDelegate)
            }
            return this
        };
        sap.ui.layout.form.FormElement.prototype.enhanceAccessibilityState = function(e, A) {
            var l = this.getLabelControl();
            if (l && l != e) {
                if (!A["labelledby"]) {
                    A["labelledby"] = l.getId()
                }
                var c = this.getParent();
                var E = c.getFormElements();
                if (this == E[0]) {
                    var C = this.getFields();
                    if (e == C[0]) {
                        var t = c.getTitle();
                        if (t) {
                            var i = "";
                            if (typeof t == "string") {
                                i = c.getId() + "--title"
                            } else {
                                i = t.getId()
                            }
                            var d = A["describedby"];
                            if (d) {
                                d = d + " " + i
                            } else {
                                d = i
                            }
                            A["describedby"] = d
                        }
                    }
                }
            }
            return A
        };
        sap.ui.layout.form.FormElement.prototype.onLayoutDataChange = function(e) {
            var p = this.getParent();
            if (p && p.onLayoutDataChange) {
                p.onLayoutDataChange(e)
            }
        };
        var _ = function() {
            var f = this.getParent();
            var F = f.getFields();
            for (var i = 0; i < F.length; i++) {
                var o = F[i];
                if (o.getRequired && o.getRequired() === true) {
                    return true
                }
            }
            return false
        };
        var a = function() {
            if (this.getLabelFor()) {
                return this.getLabelFor()
            } else {
                var f = this.getParent();
                var F = f.getFields();
                if (F[0]) {
                    return F[0].getId()
                }
            }
        };
        var b = function(e) {
            var p = this.oElement.getParent();
            if (p && p.contentOnAfterRendering) {
                p.contentOnAfterRendering(this.oElement, e.srcControl)
            }
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.FormLayout')) {
    jQuery.sap.declare("sap.ui.layout.form.FormLayout");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.form.FormLayout", {
        metadata: {
            library: "sap.ui.layout"
        }
    });
    (function() {
        sap.ui.layout.form.FormLayout.prototype.contentOnAfterRendering = function(f, c) {
            jQuery(c.getFocusDomRef()).data("sap.InNavArea", true)
        };
        sap.ui.layout.form.FormLayout.prototype.toggleContainerExpanded = function(c) {
            var e = c.getExpanded();
            if (this.getDomRef()) {
                if (e) {
                    jQuery.sap.byId(c.getId() + "-content").css("display", "")
                } else {
                    jQuery.sap.byId(c.getId() + "-content").css("display", "none")
                }
            }
        };
        sap.ui.layout.form.FormLayout.prototype.getLayoutDataForElement = function(e, t) {
            var l = e.getLayoutData();
            var c = jQuery.sap.getObject(t);
            if (!l) {
                return undefined
            } else if (l instanceof c) {
                return l
            } else if (l.getMetadata().getName() == "sap.ui.core.VariantLayoutData") {
                var L = l.getMultipleLayoutData();
                for (var i = 0; i < L.length; i++) {
                    var o = L[i];
                    if (o instanceof c) {
                        return o
                    }
                }
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapright = function(e) {
            if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
                var r = sap.ui.getCore().getConfiguration().getRTL();
                var t = this;
                if (!r) {
                    this.navigateForward(e, t)
                } else {
                    this.navigateBack(e, t)
                }
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapleft = function(e) {
            if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
                var r = sap.ui.getCore().getConfiguration().getRTL();
                var t = this;
                if (!r) {
                    this.navigateBack(e, t)
                } else {
                    this.navigateForward(e, t)
                }
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapdown = function(e) {
            if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
                var c = e.srcControl;
                var C = 0;
                var n;
                var r = this.findElement(c);
                var E = r.element;
                c = r.rootControl;
                if (E && E instanceof sap.ui.layout.form.FormElement) {
                    n = this.findFieldBelow(c, E)
                } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                    n = this.findFirstFieldOfNextElement(E, 0)
                }
                if (n) {
                    jQuery.sap.focus(n);
                    e.preventDefault()
                }
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapup = function(e) {
            if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
                var c = e.srcControl;
                var C = 0;
                var n;
                var r = this.findElement(c);
                var E = r.element;
                c = r.rootControl;
                if (E && E instanceof sap.ui.layout.form.FormElement) {
                    n = this.findFieldAbove(c, E)
                } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                    var f = E.getParent();
                    C = f.indexOfFormContainer(E);
                    n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1)
                }
                if (n) {
                    jQuery.sap.focus(n);
                    e.preventDefault()
                }
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsaphome = function(e) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            var o = E.getParent();
            var f = o.getParent();
            C = f.indexOfFormContainer(o);
            n = this.findFirstFieldOfFirstElementInNextContainer(f, C);
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsaptop = function(e) {
            var c = e.srcControl;
            var r = this.findElement(c);
            var E = r.element;
            var n;
            var C;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                C = E.getParent()
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                C = E
            }
            var f = C.getParent();
            n = this.findFirstFieldOfForm(f);
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapend = function(e) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            var o = E.getParent();
            var f = o.getParent();
            C = f.indexOfFormContainer(o);
            n = this.findLastFieldOfLastElementInPrevContainer(f, C);
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapbottom = function(e) {
            var c = e.srcControl;
            var r = this.findElement(c);
            var E = r.element;
            var n;
            var C;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                C = E.getParent()
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                C = E
            }
            var f = C.getParent();
            var a = f.getFormContainers();
            var l = a.length;
            n = this.findLastFieldOfLastElementInPrevContainer(f, l - 1);
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapexpand = function(e) {
            var c = e.srcControl;
            var r = this.findElement(c);
            var E = r.element;
            var C = E.getParent();
            if (C.getExpandable()) {
                C.setExpanded(true)
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapcollapse = function(e) {
            var c = e.srcControl;
            var r = this.findElement(c);
            var E = r.element;
            var C = E.getParent();
            if (C.getExpandable()) {
                C.setExpanded(false)
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapskipforward = function(e) {
            var c = e.srcControl;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            var n;
            var C;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                C = E.getParent()
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                C = E
            }
            var f = C.getParent();
            var i = f.indexOfFormContainer(C);
            n = this.findFirstFieldOfFirstElementInNextContainer(f, i + 1);
            if (!n) {
                n = this.findFirstFieldOfForm(f)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.onsapskipback = function(e) {
            var c = e.srcControl;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            var n;
            var C;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                C = E.getParent()
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                C = E
            }
            var f = C.getParent();
            var a = f.getFormContainers();
            var i = f.indexOfFormContainer(C);
            while (!n && i >= 0) {
                var p = a[i - 1];
                if (!p.getExpandable() || p.getExpanded()) {
                    n = this.findFirstFieldOfFirstElementInPrevContainer(f, i - 1)
                }
                i = i - 1
            }
            if (!n) {
                n = this.findLastFieldOfForm(f)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.findElement = function(c) {
            var e = c.getParent();
            var r = c;
            while (e && !(e instanceof sap.ui.layout.form.FormElement) && !(e && e instanceof sap.ui.layout.form.FormContainer) && !(e && e instanceof sap.ui.layout.form.Form)) {
                r = e;
                e = e.getParent()
            }
            return ({
                rootControl: r,
                element: e
            })
        };
        sap.ui.layout.form.FormLayout.prototype.navigateForward = function(e) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                if (c == E.getLabelControl()) {
                    C = -1
                } else {
                    C = E.indexOfField(c)
                }
                n = this.findNextFieldOfElement(E, C + 1);
                if (!n) {
                    var o = E.getParent();
                    C = o.indexOfFormElement(E);
                    n = this.findFirstFieldOfNextElement(o, C + 1);
                    if (!n) {
                        var f = o.getParent();
                        C = f.indexOfFormContainer(o);
                        n = this.findFirstFieldOfFirstElementInNextContainer(f, C + 1)
                    }
                }
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                n = this.findFirstFieldOfNextElement(E, 0)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.tabForward = function(e) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                if (c == E.getLabelControl()) {
                    C = -1
                } else {
                    C = E.indexOfField(c)
                }
                n = this.findNextFieldOfElement(E, C + 1, true);
                if (!n) {
                    var o = E.getParent();
                    C = o.indexOfFormElement(E);
                    n = this.findFirstFieldOfNextElement(o, C + 1, true);
                    if (!n) {
                        var f = o.getParent();
                        C = f.indexOfFormContainer(o);
                        n = this.findFirstFieldOfFirstElementInNextContainer(f, C + 1, true)
                    }
                }
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                n = this.findFirstFieldOfNextElement(E, 0, true);
                if (!n) {
                    var f = E.getParent();
                    C = f.indexOfFormContainer(E);
                    n = this.findFirstFieldOfFirstElementInNextContainer(f, C + 1, true)
                }
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.findNextFieldOfElement = function(e, s, t) {
            var f = e.getFields();
            var l = f.length;
            var n;
            for (var i = s; i < l; i++) {
                var F = f[i];
                var d = this._getDomRef(F);
                if (t == true) {
                    if ((!F.getEditable || F.getEditable()) && (!F.getEnabled || F.getEnabled()) && d) {
                        n = d;
                        break
                    }
                } else {
                    if ((!F.getEnabled || F.getEnabled()) && d) {
                        n = d;
                        break
                    }
                }
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfNextElement = function(c, s, t) {
            var e = c.getFormElements();
            var l = e.length;
            var n;
            var i = s;
            while (!n && i < l) {
                var E = e[i];
                if (t == true) {
                    n = this.findNextFieldOfElement(E, 0, true)
                } else {
                    n = this.findNextFieldOfElement(E, 0)
                }
                i++
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfForm = function(f) {
            var c = f.getFormContainers();
            var n;
            var C = c[0];
            if (!C.getExpandable() || C.getExpanded()) {
                n = this.findFirstFieldOfNextElement(C, 0)
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findLastFieldOfForm = function(f) {
            var c = f.getFormContainers();
            var l = c.length;
            var n;
            var C = c[l - 1];
            if (!C.getExpandable() || C.getExpanded()) {
                n = this.findFirstFieldOfNextElement(C, 0)
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfLastContainerOfForm = function(f) {
            var c = f.getFormContainers();
            var l = c.length;
            var n;
            var C = c[l - 1];
            if (!C.getExpandable() || C.getExpanded()) {
                n = this.findFirstFieldOfNextElement(C, 0)
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfFirstElementInNextContainer = function(f, s, t) {
            var c = f.getFormContainers();
            var l = c.length;
            var n;
            var i = s;
            while (!n && i < l) {
                var C = c[i];
                if (C.getExpandable() && t) {
                    n = C._oExpandButton;
                    return n;
                    break
                }
                if (!C.getExpandable() || C.getExpanded()) {
                    if (t == true) {
                        n = this.findFirstFieldOfNextElement(C, 0, true)
                    } else {
                        n = this.findFirstFieldOfNextElement(C, 0)
                    }
                }
                i++
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfFirstElementInPrevContainer = function(f, s) {
            var c = f.getFormContainers();
            var l = c.length;
            var n;
            var i = s;
            while (!n && i < l && i >= 0) {
                var C = c[i];
                if (!C.getExpandable() || C.getExpanded()) {
                    n = this.findFirstFieldOfNextElement(C, 0)
                }
                i++
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.navigateBack = function(e) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                if (c == E.getLabelControl()) {
                    C = 0
                } else {
                    C = E.indexOfField(c)
                }
                n = this.findPrevFieldOfElement(E, C - 1);
                if (!n) {
                    var o = E.getParent();
                    C = o.indexOfFormElement(E);
                    n = this.findLastFieldOfPrevElement(o, C - 1);
                    if (!n) {
                        var f = o.getParent();
                        C = f.indexOfFormContainer(o);
                        n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1)
                    }
                }
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                var f = E.getParent();
                C = f.indexOfFormContainer(E);
                n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.tabBack = function(e) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                if (c == E.getLabelControl()) {
                    C = 0
                } else {
                    C = E.indexOfField(c)
                }
                n = this.findPrevFieldOfElement(E, C - 1, true);
                if (!n) {
                    var o = E.getParent();
                    C = o.indexOfFormElement(E);
                    n = this.findLastFieldOfPrevElement(o, C - 1, true);
                    if (!n) {
                        var f = o.getParent();
                        C = f.indexOfFormContainer(o);
                        if (o.getExpandable()) {
                            n = o._oExpandButton
                        } else {
                            n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1, true)
                        }
                    }
                }
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                var f = E.getParent();
                C = f.indexOfFormContainer(E);
                n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1, true)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        };
        sap.ui.layout.form.FormLayout.prototype.findPrevFieldOfElement = function(e, s, t) {
            var f = e.getFields();
            var n;
            for (var i = s; i >= 0; i--) {
                var F = f[i];
                var d = this._getDomRef(F);
                if (t == true) {
                    if ((!F.getEditable || F.getEditable()) && (!F.getEnabled || F.getEnabled()) && d) {
                        n = d;
                        break
                    }
                } else {
                    if ((!F.getEnabled || F.getEnabled()) && d) {
                        n = d;
                        break
                    }
                }
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findLastFieldOfPrevElement = function(c, s, t) {
            var e = c.getFormElements();
            var n;
            var i = s;
            while (!n && i >= 0) {
                var E = e[i];
                var l = E.getFields().length;
                if (t == true) {
                    n = this.findPrevFieldOfElement(E, l - 1, true)
                } else {
                    n = this.findPrevFieldOfElement(E, l - 1)
                }
                i--
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findLastFieldOfLastElementInPrevContainer = function(f, s, t) {
            var c = f.getFormContainers();
            var n;
            var i = s;
            while (!n && i >= 0) {
                var C = c[i];
                if (C.getExpandable() && !C.getExpanded() && t) {
                    n = C._oExpandButton;
                    return n;
                    break
                }
                if (!C.getExpandable() || C.getExpanded()) {
                    var l = C.getFormElements().length;
                    if (t == true) {
                        n = this.findLastFieldOfPrevElement(C, l - 1, true)
                    } else {
                        n = this.findLastFieldOfPrevElement(C, l - 1, 0)
                    }
                }
                i--
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFieldBelow = function(c, e) {
            var C = e.getParent();
            var i = C.indexOfFormElement(e);
            var n = this.findFirstFieldOfNextElement(C, i + 1);
            if (!n) {
                var f = C.getParent();
                i = f.indexOfFormContainer(C);
                n = this.findFirstFieldOfFirstElementInNextContainer(f, i + 1)
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype.findFieldAbove = function(c, e) {
            var C = e.getParent();
            var a = C.indexOfFormElement(e);
            var E = C.getFormElements();
            var n;
            var i = a - 1;
            while (!n && i >= 0) {
                var e = E[i];
                n = this.findPrevFieldOfElement(e, 0);
                i--
            }
            if (!n) {
                var f = C.getParent();
                a = f.indexOfFormContainer(C);
                n = this.findLastFieldOfLastElementInPrevContainer(f, a - 1)
            }
            return n
        };
        sap.ui.layout.form.FormLayout.prototype._getDomRef = function(c) {
            var d = c.getFocusDomRef();
            if (!jQuery(d).is(":sapFocusable")) {
                d = undefined
            }
            return d
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.GridContainerData')) {
    jQuery.sap.declare("sap.ui.layout.form.GridContainerData");
    jQuery.sap.require('sap.ui.core.LayoutData');
    sap.ui.core.LayoutData.extend("sap.ui.layout.form.GridContainerData", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "halfGrid": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                }
            }
        }
    })
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.GridElementData')) {
    jQuery.sap.declare("sap.ui.layout.form.GridElementData");
    jQuery.sap.require('sap.ui.core.LayoutData');
    sap.ui.core.LayoutData.extend("sap.ui.layout.form.GridElementData", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "hCells": {
                    type: "sap.ui.layout.form.GridElementCells",
                    group: "Appearance",
                    defaultValue: 'auto'
                },
                "vCells": {
                    type: "int",
                    group: "Appearance",
                    defaultValue: 1
                }
            }
        }
    })
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.GridLayout')) {
    jQuery.sap.declare("sap.ui.layout.form.GridLayout");
    sap.ui.layout.form.FormLayout.extend("sap.ui.layout.form.GridLayout", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "singleColumn": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: false
                }
            }
        }
    });
    (function() {
        sap.ui.layout.form.GridLayout.prototype.toggleContainerExpanded = function(c) {
            this.rerender()
        };
        sap.ui.layout.form.GridLayout.prototype.contentOnAfterRendering = function(f, c) {
            sap.ui.layout.form.FormLayout.prototype.contentOnAfterRendering.apply(this, arguments);
            if (c.getMetadata().getName() != "sap.ui.commons.Image") {
                c.$().css("width", "100%")
            }
        };
        sap.ui.layout.form.GridLayout.prototype.onLayoutDataChange = function(e) {
            if (this.getDomRef()) {
                this.rerender()
            }
        };
        sap.ui.layout.form.GridLayout.prototype.onsaptabnext = function(e) {
            var r = sap.ui.getCore().getConfiguration().getRTL();
            if (!r) {
                this.tabForward(e)
            } else {
                this.tabBack(e)
            }
        };
        sap.ui.layout.form.GridLayout.prototype.onsaptabprevious = function(e) {
            var r = sap.ui.getCore().getConfiguration().getRTL();
            if (!r) {
                this.tabBack(e)
            } else {
                this.tabForward(e)
            }
        };
        sap.ui.layout.form.GridLayout.prototype.findFieldOfElement = function(e, s, l) {
            if (!l) {
                return sap.ui.layout.form.FormLayout.prototype.findPrevFieldOfElement.apply(this, arguments)
            }
            if (!e.getVisible()) {
                return
            }
            var f = e.getFields();
            var n;
            var I = f.length;
            s = I - 1;
            for (var i = s; i >= 0; i--) {
                var F = f[i];
                var L = jQuery.sap.byId(F.getId()).offset().left;
                if (l < L && i != 0) {
                    continue
                }
                var d = this._getDomRef(F);
                if ((!F.getEnabled || F.getEnabled()) && d) {
                    n = d;
                    break
                }
            }
            return n
        };
        sap.ui.layout.form.GridLayout.prototype.findFieldBelow = function(c, e) {
            var C = e.getParent();
            var a = C.indexOfFormElement(e);
            var n;
            if (C.getVisible()) {
                var E = C.getFormElements();
                var m = E.length;
                var i = a + 1;
                var l = jQuery.sap.byId(c.getId()).offset().left;
                while (!n && i < m) {
                    var e = E[i];
                    n = this.findFieldOfElement(e, 0, l);
                    i++
                }
            }
            if (!n) {
                var f = C.getParent();
                a = f.indexOfFormContainer(C);
                n = this.findFirstFieldOfFirstElementInNextContainer(f, a + 1)
            }
            return n
        };
        sap.ui.layout.form.GridLayout.prototype.findFieldAbove = function(c, e) {
            var C = e.getParent();
            var a = C.indexOfFormElement(e);
            var n;
            if (C.getVisible()) {
                var E = C.getFormElements();
                var i = a - 1;
                var l = jQuery.sap.byId(c.getId()).offset().left;
                while (!n && i >= 0) {
                    var e = E[i];
                    n = this.findFieldOfElement(e, 0, l);
                    i--
                }
            }
            if (!n) {
                var f = C.getParent();
                a = f.indexOfFormContainer(C);
                n = this.findLastFieldOfLastElementInPrevContainer(f, a - 1)
            }
            return n
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.ResponsiveGridLayout')) {
    jQuery.sap.declare("sap.ui.layout.form.ResponsiveGridLayout");
    sap.ui.layout.form.FormLayout.extend("sap.ui.layout.form.ResponsiveGridLayout", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "labelSpanL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 4
                },
                "labelSpanM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 2
                },
                "labelSpanS": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 12
                },
                "emptySpanL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "emptySpanM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "emptySpanS": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "columnsL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 2
                },
                "columnsM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 1
                },
                "breakpointL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 1024
                },
                "breakpointM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 600
                }
            }
        }
    });
    sap.ui.core.Control.extend("sap.ui.layout.form.ResponsiveGridLayoutPanel", {
        metadata: {
            aggregations: {
                "content": {
                    type: "sap.ui.layout.Grid",
                    multiple: false
                }
            },
            associations: {
                "container": {
                    type: "sap.ui.layout.form.FormContainer",
                    multiple: false
                },
                "layout": {
                    type: "sap.ui.layout.form.ResponsiveLayout",
                    multiple: false
                },
            }
        },
        getLayoutData: function() {
            var c = sap.ui.getCore().byId(this.getContainer());
            var l = sap.ui.getCore().byId(this.getLayout());
            var L;
            if (l && c) {
                L = l.getLayoutDataForElement(c, "sap.ui.layout.GridData")
            }
            if (L) {
                return L
            } else {
                return this.getAggregation("layoutData")
            }
        },
        getCustomData: function() {
            var c = sap.ui.getCore().byId(this.getContainer());
            if (c) {
                return c.getCustomData()
            }
        },
        refreshExpanded: function() {
            var c = sap.ui.getCore().byId(this.getContainer());
            if (c) {
                if (c.getExpanded()) {
                    jQuery.sap.byId(this.getId()).removeClass("sapUiRGLContainerColl")
                } else {
                    jQuery.sap.byId(this.getId()).addClass("sapUiRGLContainerColl")
                }
            }
        },
        renderer: function(r, p) {
            var c = sap.ui.getCore().byId(p.getContainer());
            var l = sap.ui.getCore().byId(p.getLayout());
            var C = p.getContent();
            var e = c.getExpandable();
            var t = c.getTooltip_AsString();
            r.write("<div");
            r.writeControlData(p);
            r.addClass("sapUiRGLContainer");
            if (e && !c.getExpanded()) {
                r.addClass("sapUiRGLContainerColl")
            }
            if (t) {
                r.writeAttributeEscaped('title', t)
            }
            r.writeClasses();
            r.write(">");
            if (c.getTitle()) {
                l.getRenderer().renderTitle(r, c.getTitle(), c._oExpandButton, e, false, c.getId())
            }
            if (C) {
                r.write("<div");
                r.addClass("sapUiRGLContainerCont");
                r.writeClasses();
                r.write(">");
                r.renderControl(C);
                r.write("</div>")
            }
            r.write("</div>")
        }
    });
    (function() {
        sap.ui.layout.form.ResponsiveGridLayout.prototype.init = function() {
            this.mContainers = {};
            this.oDummyLayoutData = new sap.ui.layout.GridData(this.getId() + "--Dummy");
            this.SPANPATTERN = /^([L](?:[1-9]|1[0-2]))? ?([M](?:[1-9]|1[0-2]))? ?([S](?:[1-9]|1[0-2]))?$/i
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.exit = function() {
            var t = this;
            for (var C in this.mContainers) {
                g(t, C)
            }
            if (this._mainGrid) {
                this._mainGrid.destroy();
                delete this._mainGrid
            }
            this.oDummyLayoutData.destroy();
            this.oDummyLayoutData = undefined
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.onBeforeRendering = function(E) {
            var F = this.getParent();
            if (!F || !(F instanceof sap.ui.layout.form.Form)) {
                return
            }
            var t = this;
            _(t, F);
            h(t, F)
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.onAfterRendering = function(E) {
            if (this._mainGrid) {
                var m;
                if (this._mainGrid.$().hasClass("sapUiRespGridMedia-Std-Desktop")) {
                    m = "Desktop"
                } else if (this._mainGrid.$().hasClass("sapUiRespGridMedia-Std-Tablet")) {
                    m = "Tablet"
                } else if (this._mainGrid.$().hasClass("sapUiRespGridMedia-Std-Phone")) {
                    m = "Phone"
                }
            }
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.contentOnAfterRendering = function(F, C) {
            sap.ui.layout.form.FormLayout.prototype.contentOnAfterRendering.apply(this, arguments);
            if (C.getWidth && (!C.getWidth() || C.getWidth() == "auto") && C.getMetadata().getName() != "sap.ui.commons.Image") {
                C.$().css("width", "100%")
            }
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.toggleContainerExpanded = function(C) {
            var E = C.getExpanded();
            var s = C.getId();
            if (this.mContainers[s] && this.mContainers[s][0]) {
                var p = this.mContainers[s][0];
                p.refreshExpanded()
            }
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.onLayoutDataChange = function(E) {
            var s = E.srcControl;
            if (s instanceof sap.ui.layout.form.FormContainer) {
                if (this._mainGrid) {
                    this._mainRFGrid.onLayoutDataChange(E)
                }
            } else if (s instanceof sap.ui.layout.form.FormElement) {} else {
                var p = s.getParent();
                if (p instanceof sap.ui.layout.form.FormElement) {
                    var C = p.getParent();
                    var i = C.getId();
                    var j = p.getId();
                    if (this.mContainers[i] && this.mContainers[i][1]) {
                        this.mContainers[i][1].onLayoutDataChange(E)
                    }
                }
            }
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.onsapup = function(E) {
            this.onsapleft(E)
        };
        sap.ui.layout.form.ResponsiveGridLayout.prototype.onsapdown = function(E) {
            this.onsapright(E)
        };
        var _ = function(l, F) {
            var C = F.getFormContainers();
            var L = C.length;
            var v = 0;
            for (var i = 0; i < L; i++) {
                var o = C[i];
                if (o.getVisible()) {
                    v++;
                    var s = o.getId();
                    var p = undefined;
                    var G = undefined;
                    var j = C[i + 1];
                    if (l.mContainers[s] && l.mContainers[s][1]) {
                        G = l.mContainers[s][1]
                    } else {
                        G = c(l, o)
                    }
                    var t = o.getTitle();
                    if (t || o.getExpandable()) {
                        if (l.mContainers[s] && l.mContainers[s][0]) {
                            p = l.mContainers[s][0]
                        } else {
                            p = a(l, o, G);
                            e(G, true)
                        }
                        f(p, o, v, j)
                    } else {
                        if (l.mContainers[s] && l.mContainers[s][0]) {
                            b(l.mContainers[s][0]);
                            e(G, false)
                        }
                        f(G, o, v, j)
                    }
                    l.mContainers[s] = [p, G]
                }
            }
            var O = k(l.mContainers);
            if (v < O) {
                for (var s in l.mContainers) {
                    var m = false;
                    for (var i = 0; i < L; i++) {
                        var o = C[i];
                        if (s == o.getId() && o.getVisible()) {
                            m = true;
                            break
                        }
                    }
                    if (!m) {
                        g(l, s)
                    }
                }
            }
        };
        var a = function(l, C, G) {
            var s = C.getId();
            var p = new sap.ui.layout.form.ResponsiveGridLayoutPanel(s + "---Panel", {
                container: C,
                layout: l,
                content: G
            });
            return p
        };
        var b = function(p) {
            p.setContent("");
            p.setLayout("");
            p.setContainer("");
            p.destroy();
            delete p
        };
        var c = function(l, C) {
            var I = C.getId() + "--Grid";
            var G = new sap.ui.layout.Grid(I, {
                vSpacing: 0,
                hSpacing: 0,
                containerQuery: true
            });
            G.__myParentLayout = l;
            G.__myParentContainerId = C.getId();
            G.addStyleClass("sapUiFormResGridCont");
            G.getContent = function() {
                var C = sap.ui.getCore().byId(this.__myParentContainerId);
                if (C) {
                    var m = new Array;
                    var E = C.getFormElements();
                    var F;
                    var L;
                    for (var i = 0; i < E.length; i++) {
                        var o = E[i];
                        if (o.getVisible()) {
                            L = o.getLabelControl();
                            if (L) {
                                m.push(L)
                            }
                            F = o.getFields();
                            for (var j = 0; j < F.length; j++) {
                                m.push(F[j])
                            }
                        }
                    }
                    return m
                } else {
                    return false
                }
            };
            G._getLayoutDataForControl = function(o) {
                var l = this.__myParentLayout;
                var L = l.getLayoutDataForElement(o, "sap.ui.layout.GridData");
                var E = o.getParent();
                var j = E.getLabelControl();
                if (L) {
                    if (j == o) {
                        L._setStylesInternal("sapUiFormResGridLbl")
                    }
                    return L
                } else {
                    var C = sap.ui.getCore().byId(this.__myParentContainerId);
                    var m = l.getLayoutDataForElement(C, "sap.ui.layout.GridData");
                    var F = C.getParent();
                    var n = l.getLabelSpanL();
                    var p = l.getLabelSpanM();
                    var q = l.getLabelSpanS();
                    if (F.getFormContainers().length >= 1 && l.getColumnsM() > 1) {
                        p = l.getLabelSpanL()
                    }
                    if (m) {
                        if (m._getEffectiveSpanLarge() == 12) {
                            n = l.getLabelSpanM();
                            p = l.getLabelSpanM()
                        }
                    }
                    if (F.getFormContainers().length == 1 || l.getColumnsL() == 1) {
                        n = l.getLabelSpanM();
                        p = l.getLabelSpanM()
                    }
                    if (j == o) {
                        l.oDummyLayoutData.setSpan("L" + n + " M" + p + " S" + q);
                        l.oDummyLayoutData.setLinebreak(true);
                        l.oDummyLayoutData._setStylesInternal("sapUiFormResGridLbl");
                        return l.oDummyLayoutData
                    } else {
                        var r = 12 - l.getEmptySpanL();
                        var M = 12 - l.getEmptySpanM();
                        var s = 12 - l.getEmptySpanS();
                        var t;
                        if (j) {
                            var u = l.getLayoutDataForElement(j, "sap.ui.layout.GridData");
                            if (u) {
                                t = u._getEffectiveSpanLarge();
                                if (t) {
                                    n = t
                                }
                                t = u._getEffectiveSpanMedium();
                                if (t) {
                                    p = t
                                }
                                t = u._getEffectiveSpanSmall();
                                if (t) {
                                    q = t
                                }
                            }
                            if (n < 12) {
                                r = r - n
                            }
                            if (p < 12) {
                                M = M - p
                            }
                            if (q < 12) {
                                s = s - q
                            }
                        }
                        var v = E.getFields();
                        var w = v.length;
                        var D = 1;
                        var x = false;
                        for (var i = 0; i < w; i++) {
                            var y = v[i];
                            if (y != o) {
                                var z = l.getLayoutDataForElement(y, "sap.ui.layout.GridData");
                                if (z) {
                                    t = z._getEffectiveSpanLarge();
                                    if (t) {
                                        r = r - t
                                    }
                                    t = z._getEffectiveSpanMedium();
                                    if (t) {
                                        M = M - t
                                    }
                                    t = z._getEffectiveSpanSmall();
                                    if (t) {
                                        s = s - t
                                    }
                                } else {
                                    D++
                                }
                            } else {
                                if (D == 1) {
                                    x = true
                                }
                            }
                        }
                        var A, B, H = 12;
                        if (x) {
                            var R = r - Math.floor(r / D) * D;
                            A = Math.floor(r / D) + R;
                            R = M - Math.floor(M / D) * D;
                            B = Math.floor(M / D) + R;
                            if (q < 12) {
                                R = s - Math.floor(s / D) * D;
                                H = Math.floor(s / D) + R
                            }
                        } else {
                            A = Math.floor(r / D);
                            B = Math.floor(M / D);
                            if (q < 12) {
                                H = Math.floor(s / D)
                            }
                        }
                        l.oDummyLayoutData.setSpan("L" + A + " M" + B + " S" + H);
                        l.oDummyLayoutData.setLinebreak(x && !j);
                        l.oDummyLayoutData._setStylesInternal(undefined);
                        return l.oDummyLayoutData
                    }
                    return L
                }
            };
            G._onParentResizeOrg = G._onParentResize;
            G._onParentResize = function() {
                if (!this.getDomRef()) {
                    this._cleanup();
                    return
                }
                if (!jQuery(this.getDomRef()).is(":visible")) {
                    return
                }
                var l = this.__myParentLayout;
                if (!l._mainGrid || !l._mainGrid.__bIsUsed) {
                    var i = l.getParent().getFormContainers();
                    if (l.mContainers[i[0].getId()][0]) {
                        var D = l.mContainers[i[0].getId()][0].getDomRef();
                        var j = D.clientWidth;
                        if (j <= l.getBreakpointM()) {
                            this._toggleClass("Phone")
                        } else if ((j > l.getBreakpointM()) && (j <= l.getBreakpointL())) {
                            this._toggleClass("Tablet")
                        } else {
                            this._toggleClass("Desktop")
                        }
                    } else {
                        this._setBreakPointTablet(l.getBreakpointM());
                        this._setBreakPointDesktop(l.getBreakpointL());
                        this._onParentResizeOrg()
                    }
                } else {
                    var $ = l._mainGrid.$();
                    if ($.hasClass("sapUiRespGridMedia-Std-Phone")) {
                        this._toggleClass("Phone")
                    } else if ($.hasClass("sapUiRespGridMedia-Std-Tablet")) {
                        this._toggleClass("Tablet")
                    } else {
                        this._toggleClass("Desktop")
                    }
                }
            };
            return G
        };
        var d = function(G) {
            if (G.__myParentContainerId) {
                G.__myParentContainerId = undefined
            }
            G.__myParentLayout = undefined;
            G.destroy();
            delete G
        };
        var e = function(G, o) {
            if (o) {
                if (G.__originalGetLayoutData) {
                    G.getLayoutData = G.__originalGetLayoutData;
                    delete G.__originalGetLayoutData
                }
            } else if (!G.__originalGetLayoutData) {
                G.__originalGetLayoutData = G.getLayoutData;
                G.getLayoutData = function() {
                    var l = this.__myParentLayout;
                    var C = sap.ui.getCore().byId(this.__myParentContainerId);
                    var L;
                    if (C) {
                        L = l.getLayoutDataForElement(C, "sap.ui.layout.GridData")
                    }
                    if (L) {
                        return L
                    } else {
                        return this.getAggregation("layoutData")
                    }
                }
            }
        };
        var f = function(C, o, v, i) {
            var l;
            if (C instanceof sap.ui.layout.form.ResponsiveGridLayoutPanel) {
                l = sap.ui.getCore().byId(C.getLayout())
            } else {
                l = C.__myParentLayout
            }
            var j = l.getColumnsL();
            var m = l.getColumnsM();
            var L = l.getLayoutDataForElement(o, "sap.ui.layout.GridData");
            if (!L) {
                var n = (v % j) == 1;
                var p = (v % j) == 0;
                var q = (v % m) == 1;
                var r = (v % m) == 0;
                if (i) {
                    var s = l.getLayoutDataForElement(i, "sap.ui.layout.GridData");
                    if (s && (s.getLinebreak() || s.getLinebreakL())) {
                        p = true
                    }
                    if (s && (s.getLinebreak() || s.getLinebreakM())) {
                        r = true
                    }
                }
                var S = "";
                if (p) {
                    S = "sapUiFormResGridLastContL"
                }
                if (r) {
                    if (S) {
                        S = S + " "
                    }
                    S = S + "sapUiFormResGridLastContM"
                }
                L = C.getLayoutData();
                if (!L) {
                    L = new sap.ui.layout.GridData(C.getId() + "--LD", {
                        linebreakL: n,
                        linebreakM: q
                    });
                    C.setLayoutData(L)
                } else {
                    L.setLinebreakL(n);
                    L.setLinebreakM(q)
                }
                L._setStylesInternal(S)
            }
        };
        var g = function(l, C) {
            var i = l.mContainers[C];
            var G = i[1];
            if (G) {
                d(G)
            }
            var p = i[0];
            if (p) {
                b(p)
            }
            delete l.mContainers[C]
        };
        var h = function(l, F) {
            var C = F.getFormContainers();
            var L = 0;
            var j = 0;
            for (var i = 0; i < C.length; i++) {
                var o = C[i];
                if (o.getVisible()) {
                    L++
                }
            }
            if (L > 1) {
                var s = Math.floor(12 / l.getColumnsL());
                var S = Math.floor(12 / l.getColumnsM());
                if (!l._mainGrid) {
                    l._mainGrid = new sap.ui.layout.Grid(F.getId() + "--Grid", {
                        defaultSpan: "L" + s + " M" + S + " S12",
                        hSpacing: 0,
                        vSpacing: 0,
                        containerQuery: true
                    }).setParent(l);
                    l._mainGrid.addStyleClass("sapUiFormResGridMain")
                } else {
                    l._mainGrid.setDefaultSpan("L" + s + " M" + S + " S12");
                    var m = l._mainGrid.getContent();
                    j = m.length;
                    var E = false;
                    for (var i = 0; i < j; i++) {
                        var n = m[i];
                        var o = undefined;
                        if (n.getContainer) {
                            o = sap.ui.getCore().byId(n.getContainer())
                        } else {
                            o = sap.ui.getCore().byId(n.__myParentContainerId)
                        }
                        if (o && o.getVisible()) {
                            var p = l.mContainers[o.getId()];
                            if (p[0] && p[0] != n) {
                                E = true;
                                break
                            }
                            if (!p[0] && p[1] && p[1] != n) {
                                E = true;
                                break
                            }
                        } else {
                            l._mainGrid.removeContent(n)
                        }
                    }
                    if (E) {
                        l._mainGrid.removeAllContent();
                        j = 0
                    }
                }
                l._mainGrid._setBreakPointTablet(l.getBreakpointM());
                l._mainGrid._setBreakPointDesktop(l.getBreakpointL());
                l._mainGrid.__bIsUsed = true;
                if (j < L) {
                    for (var i = 0; i < C.length; i++) {
                        var o = C[i];
                        if (o.getVisible()) {
                            var q = o.getId();
                            if (l.mContainers[q]) {
                                if (l.mContainers[q][0]) {
                                    l._mainGrid.addContent(l.mContainers[q][0])
                                } else if (l.mContainers[q][1]) {
                                    l._mainGrid.addContent(l.mContainers[q][1])
                                }
                            }
                        }
                    }
                }
            } else if (l._mainGrid) {
                l._mainGrid.__bIsUsed = false
            }
        };
        var k = function(o) {
            var l = 0;
            if (!Object.keys) {
                jQuery.each(o, function() {
                    l++
                })
            } else {
                l = Object.keys(o).length
            }
            return l
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.SimpleForm')) {
    jQuery.sap.declare("sap.ui.layout.form.SimpleForm");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.form.SimpleForm", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "maxContainerCols": {
                    type: "int",
                    group: "Appearance",
                    defaultValue: 2
                },
                "minWidth": {
                    type: "int",
                    group: "Appearance",
                    defaultValue: -1
                },
                "editable": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: null
                },
                "labelMinWidth": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 192
                },
                "layout": {
                    type: "sap.ui.layout.form.SimpleFormLayout",
                    group: "Misc",
                    defaultValue: sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout
                },
                "labelSpanL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 4
                },
                "labelSpanM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 2
                },
                "labelSpanS": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 12
                },
                "emptySpanL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "emptySpanM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "emptySpanS": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "columnsL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 2
                },
                "columnsM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 1
                },
                "breakpointL": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 1024
                },
                "breakpointM": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 600
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Element",
                    multiple: true,
                    singularName: "content"
                },
                "form": {
                    type: "sap.ui.layout.form.Form",
                    multiple: false,
                    visibility: "hidden"
                },
                "title": {
                    type: "sap.ui.core.Title",
                    altTypes: ["string"],
                    multiple: false
                }
            }
        }
    });
    (function() {
        sap.ui.layout.form.SimpleForm.prototype.init = function() {
            this._iMaxWeight = 8;
            this._iLabelWeight = 3;
            this._iCurrentWidth = 0;
            var F = new sap.ui.layout.form.Form(this.getId() + "--Form");
            F.getTitle = function() {
                return this.getParent().getTitle()
            };
            this.setAggregation("form", F);
            this._aElements = null;
            this._aLayouts = [];
            var i = this;
            this._changedFormContainers = [];
            this._changedFormElements = []
        };
        sap.ui.layout.form.SimpleForm.prototype.exit = function() {
            if (this._sResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
                this._sResizeListenerId = null
            }
            for (var i = 0; i < this._aLayouts.length; i++) {
                var L = sap.ui.getCore().byId(this._aLayouts[i]);
                if (L && L.destroy) {
                    L.destroy()
                }
            }
            this._aLayouts = [];
            this._aElements = null;
            this._changedFormContainers = [];
            this._changedFormElements = []
        };
        sap.ui.layout.form.SimpleForm.prototype.onBeforeRendering = function() {
            if (this._sResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
                this._sResizeListenerId = null
            }
            var i = this;
            var F = this.getAggregation("form");
            if (!F.getLayout()) {
                _(i)
            }
            a(i)
        };
        sap.ui.layout.form.SimpleForm.prototype.onAfterRendering = function() {
            if (this.getLayout() == sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout) {
                this.$().css("visibility", "hidden");
                this._applyLinebreaks();
                this._sResizeListenerId = sap.ui.core.ResizeHandler.register(this.getDomRef(), jQuery.proxy(this._resize, this))
            }
        };
        sap.ui.layout.form.SimpleForm.prototype.setEditable = function(E) {
            var O = this.getEditable();
            this.setProperty("editable", E, true);
            if (E != O) {
                var F = this.getAggregation("form");
                if (this.getEditable()) {
                    F.addStyleClass("sapUiFormEdit sapUiFormEdit-CTX")
                } else {
                    F.addStyleClass("")
                }
            }
        };
        sap.ui.layout.form.SimpleForm.prototype.indexOfContent = function(O) {
            var C = this._aElements;
            if (C) {
                for (var i = 0; i < C.length; i++) {
                    if (C[i] == O) {
                        return i
                    }
                }
            }
            return -1
        };
        sap.ui.layout.form.SimpleForm.prototype.addContent = function(E) {
            E = this.validateAggregation("content", E, true);
            if (!this._aElements) {
                this._aElements = []
            }
            var L = this._aElements.length;
            var i;
            var F = this.getAggregation("form");
            var j;
            var k;
            var P;
            var u;
            if (E instanceof sap.ui.core.Title) {
                j = o(this, E);
                F.addFormContainer(j);
                this._changedFormContainers.push(j)
            } else if (E.getMetadata().isInstanceOf("sap.ui.core.Label")) {
                if (L > 0) {
                    i = this._aElements[L - 1];
                    P = i.getParent();
                    if (P instanceof sap.ui.layout.form.FormElement) {
                        j = P.getParent()
                    } else if (P instanceof sap.ui.layout.form.FormContainer) {
                        j = P
                    }
                }
                if (!j) {
                    j = o(this);
                    F.addFormContainer(j);
                    this._changedFormContainers.push(j)
                }
                k = l(this, j, E)
            } else {
                if (L > 0) {
                    i = this._aElements[L - 1];
                    P = i.getParent();
                    if (P instanceof sap.ui.layout.form.FormElement) {
                        j = P.getParent();
                        k = P;
                        u = d(this, E);
                        if (u instanceof sap.ui.layout.ResponsiveFlowLayoutData && !b(this, u)) {
                            if (u.getLinebreak()) {
                                k = l(this, j)
                            }
                        }
                    } else if (P instanceof sap.ui.layout.form.FormContainer) {
                        j = P;
                        k = l(this, j)
                    }
                } else {
                    j = o(this);
                    F.addFormContainer(j);
                    this._changedFormContainers.push(j);
                    k = l(this, j)
                }
                e(this, E, 5, false, true);
                k.addField(E);
                s(this._changedFormElements, k)
            }
            this._aElements.push(E);
            E.attachEvent("_change", t, this);
            this.invalidate();
            return this
        };
        sap.ui.layout.form.SimpleForm.prototype.insertContent = function(E, I) {
            E = this.validateAggregation("content", E, true);
            if (!this._aElements) {
                this._aElements = []
            }
            var L = this._aElements.length;
            var N = I < 0 ? 0 : (I > L ? L : I);
            if (N !== I) {
                jQuery.sap.log.warning("SimpleForm.insertContent: index '" + I + "' out of range [0," + L + "], forced to " + N)
            }
            if (N == L) {
                this.addContent(E);
                return this
            }
            var O = this._aElements[N];
            var F = this.getAggregation("form");
            var j;
            var k;
            var u;
            var v;
            var C;
            var w = 0;
            var x;
            var y;
            var z;
            var A;
            if (E instanceof sap.ui.core.Title) {
                if (I == 0 && !(O instanceof sap.ui.core.Title)) {
                    j = O.getParent().getParent();
                    j.setTitle(E)
                } else {
                    j = o(this, E);
                    if (O instanceof sap.ui.core.Title) {
                        u = O.getParent();
                        C = F.indexOfFormContainer(u)
                    } else {
                        v = O.getParent();
                        u = v.getParent();
                        C = F.indexOfFormContainer(u) + 1;
                        w = u.indexOfFormElement(v);
                        if (!O.getMetadata().isInstanceOf("sap.ui.core.Label")) {
                            x = v.indexOfField(O);
                            if (x > 0 || v.getLabel()) {
                                k = l(this, j);
                                this._changedFormElements.push(k);
                                s(this._changedFormElements, v);
                                y = v.getFields();
                                for (var i = x; i < y.length; i++) {
                                    var B = y[i];
                                    k.addField(B)
                                }
                                w++
                            }
                        }
                        z = u.getFormElements();
                        for (var i = w; i < z.length; i++) {
                            j.addFormElement(z[i])
                        }
                    }
                    F.insertFormContainer(j, C)
                }
                this._changedFormContainers.push(j)
            } else if (E.getMetadata().isInstanceOf("sap.ui.core.Label")) {
                if (O instanceof sap.ui.core.Title) {
                    u = O.getParent();
                    C = F.indexOfFormContainer(u);
                    A = F.getFormContainers();
                    j = A[C - 1];
                    k = l(this, j, E)
                } else if (O.getMetadata().isInstanceOf("sap.ui.core.Label")) {
                    u = O.getParent().getParent();
                    w = u.indexOfFormElement(O.getParent());
                    k = m(this, u, E, w)
                } else {
                    v = O.getParent();
                    u = v.getParent();
                    w = u.indexOfFormElement(v) + 1;
                    x = v.indexOfField(O);
                    if (x == 0 && !v.getLabel()) {
                        k = v;
                        k.setLabel(E);
                        e(this, E, this._iLabelWeight, false, true, this.getLabelMinWidth())
                    } else {
                        k = m(this, u, E, w);
                        s(this._changedFormElements, v);
                        y = v.getFields();
                        for (var i = x; i < y.length; i++) {
                            var B = y[i];
                            k.addField(B)
                        }
                    }
                }
                this._changedFormElements.push(k)
            } else {
                if (O instanceof sap.ui.core.Title) {
                    u = O.getParent();
                    C = F.indexOfFormContainer(u);
                    if (C == 0) {
                        j = o(this);
                        F.insertFormContainer(j, C);
                        this._changedFormContainers.push(j)
                    } else {
                        A = F.getFormContainers();
                        j = A[C - 1]
                    }
                    z = j.getFormElements();
                    if (z.length == 0) {
                        k = l(this, j)
                    } else {
                        k = z[z.length - 1]
                    }
                    k.addField(E)
                } else if (O.getMetadata().isInstanceOf("sap.ui.core.Label")) {
                    v = O.getParent();
                    j = v.getParent();
                    w = j.indexOfFormElement(v);
                    if (w == 0) {
                        k = m(this, j, null, 0)
                    } else {
                        z = j.getFormElements();
                        k = z[w - 1]
                    }
                    k.addField(E)
                } else {
                    k = O.getParent();
                    x = k.indexOfField(O);
                    k.insertField(E, x)
                }
                s(this._changedFormElements, k);
                e(this, E, 5, false, true)
            }
            this._aElements.splice(N, 0, E);
            E.attachEvent("_change", t, this);
            this.invalidate();
            return this
        };
        sap.ui.layout.form.SimpleForm.prototype.removeContent = function(E) {
            var j = null;
            var I = -1;
            if (this._aElements) {
                if (typeof(E) == "string") {
                    E = sap.ui.getCore().byId(E)
                }
                if (typeof(E) == "object") {
                    for (var i = 0; i < this._aElements.length; i++) {
                        if (this._aElements[i] == E) {
                            E = i;
                            break
                        }
                    }
                }
                if (typeof(E) == "number") {
                    if (E < 0 || E >= this._aElements.length) {
                        jQuery.sap.log.warning("Element.removeAggregation called with invalid index: Items, " + E)
                    } else {
                        I = E;
                        j = this._aElements[I]
                    }
                }
            }
            if (j) {
                var F = this.getAggregation("form");
                var k;
                var u;
                var v;
                var w;
                if (j instanceof sap.ui.core.Title) {
                    k = j.getParent();
                    k.setTitle(null);
                    if (I > 0) {
                        v = k.getFormElements();
                        var C = F.indexOfFormContainer(k);
                        var P = F.getFormContainers()[C - 1];
                        if (v && !v[0].getLabel()) {
                            var x = P.getFormElements();
                            var L = x[x.length - 1];
                            w = v[0].getFields();
                            for (var i = 0; i < w.length; i++) {
                                L.addField(w[i])
                            }
                            s(this._changedFormElements, L);
                            k.removeFormElement(v[0]);
                            v[0].destroy();
                            v.splice(0, 1)
                        }
                        for (var i = 0; i < v.length; i++) {
                            P.addFormElement(v[i])
                        }
                        s(this._changedFormContainers, P);
                        F.removeFormContainer(k);
                        k.destroy()
                    }
                } else if (j.getMetadata().isInstanceOf("sap.ui.core.Label")) {
                    u = j.getParent();
                    k = u.getParent();
                    u.setLabel(null);
                    var y = k.indexOfFormElement(u);
                    if (y == 0) {
                        if (!u.getFields()) {
                            k.removeFormElement(u);
                            u.destroy()
                        } else {
                            s(this._changedFormElements, u)
                        }
                    } else {
                        v = k.getFormElements();
                        var z = v[y - 1];
                        w = u.getFields();
                        for (var i = 0; i < w.length; i++) {
                            z.addField(w[i])
                        }
                        s(this._changedFormElements, z);
                        k.removeFormElement(u);
                        u.destroy()
                    }
                } else {
                    u = j.getParent();
                    u.removeField(j);
                    if (!u.getFields() && !u.getLabel()) {
                        k = u.getParent();
                        k.removeFormElement(u);
                        u.destroy()
                    } else {
                        s(this._changedFormElements, u)
                    }
                }
                this._aElements.splice(I, 1);
                j.setParent(null);
                j.detachEvent("_change", t, this);
                h(this, j);
                this.invalidate();
                return j
            }
            return null
        };
        sap.ui.layout.form.SimpleForm.prototype.removeAllContent = function() {
            if (this._aElements) {
                var F = this.getAggregation("form");
                var k = F.getFormContainers();
                for (var i = 0; i < k.length; i++) {
                    var u = k[i];
                    u.setTitle(null);
                    var v = u.getFormElements();
                    for (var j = 0; j < v.length; j++) {
                        var w = v[j];
                        w.setLabel(null);
                        w.removeAllFields()
                    }
                    u.destroyFormElements()
                }
                F.destroyFormContainers();
                for (var i = 0; i < this._aElements.length; i++) {
                    var E = this._aElements[i];
                    h(this, E);
                    E.detachEvent("_change", t, this)
                }
                var x = this._aElements;
                this._aElements = null;
                this.invalidate();
                return x
            } else {
                return []
            }
        };
        sap.ui.layout.form.SimpleForm.prototype.destroyContent = function() {
            var E = this.removeAllContent();
            if (E) {
                for (var i = 0; i < E.length; i++) {
                    E[i].destroy()
                }
                this.invalidate()
            }
            return this
        };
        sap.ui.layout.form.SimpleForm.prototype.getContent = function() {
            if (!this._aElements) {
                this._aElements = this.getAggregation("content")
            }
            return this._aElements
        };
        sap.ui.layout.form.SimpleForm.prototype.setLayout = function(L) {
            var O = this.getLayout();
            this.setProperty("layout", L);
            if (L != O) {
                var u = this;
                _(u);
                var F = this.getAggregation("form");
                var C = F.getFormContainers();
                var E;
                var v;
                var w;
                for (var i = 0; i < C.length; i++) {
                    var x = C[i];
                    this._changedFormContainers.push(x);
                    w = x.getLayoutData();
                    if (w) {
                        w.destroy()
                    }
                    g(this, x);
                    E = x.getFormElements();
                    for (var j = 0; j < E.length; j++) {
                        var y = E[j];
                        s(this._changedFormElements, y);
                        w = y.getLayoutData();
                        if (w) {
                            w.destroy()
                        }
                        f(this, y);
                        var z = y.getLabel();
                        if (z) {
                            h(this, z);
                            e(this, z, this._iLabelWeight, false, true, this.getLabelMinWidth())
                        }
                        v = y.getFields();
                        for (var k = 0; k < v.length; k++) {
                            var A = v[k];
                            h(this, A);
                            e(this, A, 5, false, true)
                        }
                    }
                }
            }
        };
        sap.ui.layout.form.SimpleForm.prototype.clone = function(I) {
            var C = sap.ui.core.Control.prototype.clone.apply(this, arguments);
            var k = this.getContent();
            for (var i = 0; i < k.length; i++) {
                var E = k[i];
                var L = E.getLayoutData();
                var u = E.clone(I);
                if (L) {
                    if (L.getMetadata().getName() == "sap.ui.core.VariantLayoutData") {
                        var v = L.getMultipleLayoutData();
                        for (var j = 0; j < v.length; j++) {
                            if (b(this, v[j])) {
                                C._aLayouts.push(u.getLayoutData().getMultipleLayoutData()[j].getId())
                            }
                        }
                    } else if (b(this, L)) {
                        C._aLayouts.push(u.getLayoutData().getId())
                    }
                }
                C.addContent(u)
            }
            return C
        };
        var _ = function(T) {
            var F = T.getAggregation("form");
            var L = F.getLayout();
            if (L) {
                L.destroy()
            }
            switch (T.getLayout()) {
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                    jQuery.sap.require("sap.ui.layout.form.ResponsiveLayout");
                    F.setLayout(new sap.ui.layout.form.ResponsiveLayout(T.getId() + "--Layout"));
                    break;
                case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                    jQuery.sap.require("sap.ui.layout.form.GridLayout");
                    jQuery.sap.require("sap.ui.layout.form.GridContainerData");
                    jQuery.sap.require("sap.ui.layout.form.GridElementData");
                    F.setLayout(new sap.ui.layout.form.GridLayout(T.getId() + "--Layout"));
                    break;
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveGridLayout:
                    jQuery.sap.require("sap.ui.layout.form.ResponsiveGridLayout");
                    jQuery.sap.require("sap.ui.layout.GridData");
                    F.setLayout(new sap.ui.layout.form.ResponsiveGridLayout(T.getId() + "--Layout"));
                    break;
                default:
                    break
            }
        };
        var a = function(T) {
            T._changedFormContainers = [];
            var L = T.getLayout();
            switch (L) {
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                    T._applyLinebreaks();
                    break;
                case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                    r(T);
                    break;
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveGridLayout:
                    var j = T.getAggregation("form").getLayout();
                    j.setLabelSpanL(T.getLabelSpanL());
                    j.setLabelSpanM(T.getLabelSpanM());
                    j.setLabelSpanS(T.getLabelSpanS());
                    j.setEmptySpanL(T.getEmptySpanL());
                    j.setEmptySpanM(T.getEmptySpanM());
                    j.setEmptySpanS(T.getEmptySpanS());
                    j.setColumnsL(T.getColumnsL());
                    j.setColumnsM(T.getColumnsM());
                    j.setBreakpointL(T.getBreakpointL());
                    j.setBreakpointM(T.getBreakpointM());
                    break;
                default:
                    break
            }
            for (var i = 0; i < T._changedFormElements.length; i++) {
                var F = T._changedFormElements[i];
                switch (L) {
                    case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                        p(T, F);
                        break;
                    case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                        break;
                    default:
                        break
                }
                q(T, F)
            }
            T._changedFormElements = []
        };
        var b = function(T, L) {
            var i = L.getId(),
                j = " " + T._aLayouts.join(" ") + " ";
            return j.indexOf(" " + i + " ") > -1
        };
        var c = function(T, w, L, i, M) {
            var j = new sap.ui.layout.ResponsiveFlowLayoutData({
                weight: w,
                linebreak: L === true,
                linebreakable: i === true
            });
            if (M) {
                j.setMinWidth(M)
            }
            T._aLayouts.push(j.getId());
            return j
        };
        var d = function(T, F) {
            var L;
            switch (T.getLayout()) {
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                    L = sap.ui.layout.form.FormLayout.prototype.getLayoutDataForElement(F, "sap.ui.layout.ResponsiveFlowLayoutData");
                    break;
                case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                    L = sap.ui.layout.form.FormLayout.prototype.getLayoutDataForElement(F, "sap.ui.layout.form.GridElementData");
                    break;
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveGridLayout:
                    L = sap.ui.layout.form.FormLayout.prototype.getLayoutDataForElement(F, "sap.ui.layout.GridData");
                    break;
                default:
                    break
            }
            return L
        };
        var e = function(T, F, w, L, i, M) {
            var j;
            switch (T.getLayout()) {
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                    j = d(T, F);
                    if (!j || !b(T, j)) {
                        j = F.getLayoutData();
                        if (j && j.getMetadata().getName() == "sap.ui.core.VariantLayoutData") {
                            j.addMultipleLayoutData(c(T, w, L, i, M))
                        } else if (!j) {
                            F.setLayoutData(c(T, w, L, i, M))
                        } else {
                            jQuery.sap.log.warning("ResponsiveFlowLayoutData can not be set on Field " + F.getId(), "_createFieldLayoutData", "SimpleForm")
                        }
                    }
                    break;
                case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                    break;
                default:
                    break
            }
        };
        var f = function(T, E) {
            switch (T.getLayout()) {
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                    E.setLayoutData(new sap.ui.layout.ResponsiveFlowLayoutData({
                        linebreak: true,
                        margin: false
                    }));
                    break;
                case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                    break;
                default:
                    break
            }
        };
        var g = function(T, C) {
            switch (T.getLayout()) {
                case sap.ui.layout.form.SimpleFormLayout.ResponsiveLayout:
                    C.setLayoutData(new sap.ui.layout.ResponsiveFlowLayoutData({
                        minWidth: 280
                    }));
                    break;
                case sap.ui.layout.form.SimpleFormLayout.GridLayout:
                    if (T.getMaxContainerCols() > 1) {
                        C.setLayoutData(new sap.ui.layout.form.GridContainerData({
                            halfGrid: true
                        }))
                    } else {
                        C.setLayoutData(new sap.ui.layout.form.GridContainerData({
                            halfGrid: false
                        }))
                    }
                    break;
                default:
                    break
            }
        };
        var h = function(T, E) {
            var L = d(T, E);
            if (L) {
                var j = L.getId();
                for (var i = 0; i < T._aLayouts.length; i++) {
                    var I = T._aLayouts[i];
                    if (j == I) {
                        L.destroy();
                        T._aLayouts.splice(i, 1);
                        break
                    }
                }
            }
        };
        var l = function(T, F, L) {
            var E = n(T, L);
            F.addFormElement(E);
            return E
        };
        var m = function(T, F, L, i) {
            var E = n(T, L);
            F.insertFormElement(E, i);
            return E
        };
        var n = function(T, L) {
            var E = new sap.ui.layout.form.FormElement();
            f(T, E);
            if (L) {
                L.addStyleClass("sapUiFormLabel-CTX");
                E.setLabel(L);
                if (!d(T, L)) {
                    e(T, L, T._iLabelWeight, false, true, T.getLabelMinWidth())
                }
            }
            E.setVisible(false);
            return E
        };
        var o = function(T, i) {
            var C = new sap.ui.layout.form.FormContainer();
            g(T, C);
            if (i) {
                C.setTitle(i)
            }
            return C
        };
        var p = function(T, E) {
            var M = T._iMaxWeight;
            var F = E.getFields();
            var j;
            var L = F.length;
            var k = E.getLabel();
            var u;
            if (k && d(T, k)) {
                M = M - d(T, k).getWeight()
            }
            for (var i = 0; i < F.length; i++) {
                j = F[i];
                u = d(T, j);
                if (u instanceof sap.ui.layout.ResponsiveFlowLayoutData && !b(T, u)) {
                    M = M - u.getWeight();
                    L--
                }
            }
            var w = Math.floor(M / L);
            var R = M % L;
            for (var i = 0; i < F.length; i++) {
                j = F[i];
                u = d(T, j);
                var C = w;
                if (!u) {
                    e(T, j, C, false, i == 0)
                } else if (b(T, u) && u instanceof sap.ui.layout.ResponsiveFlowLayoutData) {
                    if (R > 0) {
                        C++;
                        R--
                    }
                    u.setWeight(C)
                }
            }
        };
        var q = function(T, E) {
            var F = E.getFields();
            var L = F.length;
            var v = false;
            for (var i = 0; i < F.length; i++) {
                var j = F[i];
                if (!j.getVisible || j.getVisible()) {
                    v = true;
                    break
                }
            }
            if (E.getVisible() != v) {
                E.setVisible(v)
            }
        };
        sap.ui.layout.form.SimpleForm.prototype._applyLinebreaks = function() {
            var F = this.getAggregation("form"),
                C = F.getFormContainers();
            var D = this.getDomRef();
            var j = this.$();
            for (var i = 1; i < C.length; i++) {
                var k = C[i],
                    L = k.getLayoutData();
                if (!D || j.outerWidth(true) > this.getMinWidth()) {
                    if (i % this.getMaxContainerCols() == 0) {
                        L.setLinebreak(true)
                    } else {
                        L.setLinebreak(false)
                    }
                } else {
                    L.setLinebreak(true)
                }
            }
            if (D && j.css("visibility") == "hidden") {
                var u = this;
                setTimeout(function() {
                    if (u.getDomRef()) {
                        u.$().css("visibility", "inherit")
                    }
                }, 10)
            }
        };
        var r = function(T) {
            var F = T.getAggregation("form");
            var C = F.getFormContainers();
            var L = C.length;
            if (L % 2 > 0) {
                C[L - 1].getLayoutData().setHalfGrid(false)
            }
        };
        sap.ui.layout.form.SimpleForm.prototype._resize = function() {
            if (this._iCurrentWidth == this.$().outerWidth()) return;
            this._iCurrentWidth = this.$().outerWidth();
            this._applyLinebreaks()
        };
        var s = function(F, j) {
            var k = false;
            for (var i = 0; i < F.length; i++) {
                var C = F[i];
                if (C == j) {
                    k = true;
                    break
                }
            }
            if (!k) {
                F.push(j)
            }
        };
        var t = function(E) {
            if (E.getParameter("name") == "visible") {
                var F = E.oSource.getParent();
                q(this, F)
            }
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.ResponsiveFlowLayout')) {
    jQuery.sap.declare("sap.ui.layout.ResponsiveFlowLayout");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.layout.ResponsiveFlowLayout", {
        metadata: {
            library: "sap.ui.layout",
            properties: {
                "responsive": {
                    type: "boolean",
                    group: "Misc",
                    defaultValue: true
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                }
            }
        }
    });
    jQuery.sap.require('sap.ui.core.IntervalTrigger');
    jQuery.sap.require('sap.ui.core.theming.Parameters');
    (function() {
        sap.ui.layout.ResponsiveFlowLayout.prototype.init = function() {
            this._rows = [];
            this._bIsRegistered = false;
            this._proxyComputeWidths = jQuery.proxy(b, this);
            this.oRm = new sap.ui.core.RenderManager();
            this.oRm.writeStylesAndClasses = function() {
                this.writeStyles();
                this.writeClasses()
            };
            this.oRm.writeHeader = function(I, s, C) {
                this.write('<div id="' + I + '"');
                if (s) {
                    for (var k in s) {
                        if (k === "width" && s[k] === "100%") {
                            this.addClass("sapUiRFLFullLength")
                        }
                        this.addStyle(k, s[k])
                    }
                }
                for (var i = 0; i < C.length; i++) {
                    this.addClass(C[i])
                }
                this.writeStylesAndClasses();
                this.write(">")
            };
            this._iRowCounter = 0
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.exit = function() {
            delete this._rows;
            if (this._IntervalCall) {
                jQuery.sap.clearDelayedCall(this._IntervalCall);
                this._IntervalCall = undefined
            }
            if (this._resizeHandlerComputeWidthsID) {
                sap.ui.core.ResizeHandler.deregister(this._resizeHandlerComputeWidthsID)
            }
            delete this._resizeHandlerComputeWidthsID;
            delete this._proxyComputeWidths;
            this.oRm.destroy();
            delete this.oRm;
            delete this._$DomRef;
            delete this._oDomRef;
            delete this._iRowCounter
        };
        var u = function(t) {
            var C = t.getContent();
            var r = [];
            var R = -1;
            var I = {}, l = {};
            var s = "";
            var L;
            var m = 0,
                w = 0,
                d = 0;
            var B = false,
                M = false,
                e = false;
            for (var i = 0; i < C.length; i++) {
                m = sap.ui.layout.ResponsiveFlowLayoutData.MIN_WIDTH;
                w = sap.ui.layout.ResponsiveFlowLayoutData.WEIGHT;
                B = sap.ui.layout.ResponsiveFlowLayoutData.LINEBREAK;
                M = sap.ui.layout.ResponsiveFlowLayoutData.MARGIN;
                e = sap.ui.layout.ResponsiveFlowLayoutData.LINEBREAKABLE;
                L = _(C[i]);
                if (L instanceof sap.ui.layout.ResponsiveFlowLayoutData) {
                    B = L.getLinebreak();
                    m = L.getMinWidth();
                    w = L.getWeight();
                    M = L.getMargin();
                    e = L.getLinebreakable()
                }
                if (R < 0 || B) {
                    R++;
                    r.push({
                        height: -1,
                        cont: []
                    })
                }
                d = r[R].cont.length;
                s = C[i].getId() + "-cont" + R + "_" + d;
                I = {
                    minWidth: m,
                    weight: w,
                    linebreakable: e,
                    padding: M,
                    control: C[i],
                    id: s,
                    breakWith: []
                };
                var p = false;
                if ( !! !e) {
                    for (var f = d; f > 0; f--) {
                        l = r[R].cont[f - 1];
                        if (l.linebreakable) {
                            l.breakWith.push(I);
                            p = true;
                            break
                        }
                    }
                }
                if (!p) {
                    r[R].cont.push(I)
                }
            }
            t._rows = r
        };
        var g = function(R, $, t) {
            var r = [];
            var l = 10000000;
            var d = -1;
            for (var j = 0; j < R.cont.length; j++) {
                var e = jQuery.sap.byId(R.cont[j].id);
                if (e.length > 0) {
                    var o = e[0].offsetLeft;
                    if (l >= o) {
                        r.push({
                            cont: []
                        });
                        d++
                    }
                    l = o;
                    r[d].cont.push(R.cont[j])
                }
            }
            return r
        };
        var a = function(R, w) {
            var r = [];
            var d = -1;
            var e = 0;
            var t = 0;
            var i = 0;
            var f = 0,
                h = 0;
            var j = 0,
                k = 0;
            for (j = 0; j < R.cont.length; j++) {
                e = 0;
                t = 0;
                for (k = i; k <= j; k++) {
                    t = t + R.cont[k].weight
                }
                for (k = i; k <= j; k++) {
                    f = w / t * R.cont[k].weight;
                    f = Math.floor(f);
                    h = R.cont[k].minWidth;
                    e += Math.max(f, h)
                }
                if (d == -1 || e > w) {
                    r.push({
                        cont: []
                    });
                    if (d !== -1) {
                        i = j
                    }
                    d++
                }
                r[d].cont.push(R.cont[j])
            }
            return r
        };
        var c = function(w, d) {
            if (w.length != d.length) {
                return true
            }
            for (var i = 0; i < w.length; i++) {
                if (w[i].cont.length != d[i].cont.length) {
                    return true
                }
            }
            return false
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.renderContent = function(t, w) {
            var r = t;
            var R = 0;
            var W = [];
            var i = 0,
                d = 0,
                j = 0,
                e = 0;
            var f = 0;
            var p = 0;
            var C;
            var h = 0,
                k = 0;
            var B = [];
            var l = [];
            var I = this.getId();
            var H = "";
            for (i = 0; i < r.length; i++) {
                p = 0;
                W.length = 0;
                R = 100;
                l.length = 0;
                l.push("sapUiRFLRow");
                if (r[i].cont.length <= 1) {
                    l.push("sapUiRFLCompleteRow")
                }
                var s = I + "-row" + this._iRowCounter;
                var S = {};
                this.oRm.writeHeader(s, S, l);
                f = 0;
                for (d = 0; d < r[i].cont.length; d++) {
                    f += r[i].cont[d].weight
                }
                for (j = 0; j < r[i].cont.length; j++) {
                    C = r[i].cont[j];
                    h = 0;
                    k = 0;
                    if (C.breakWith.length > 0) {
                        h = C.weight;
                        k = C.minWidth;
                        for (var m = 0; m < C.breakWith.length; m++) {
                            h += C.breakWith[m].weight;
                            k += C.breakWith[m].minWidth
                        }
                    }
                    H = r[i].cont[j].id;
                    l.length = 0;
                    S = {
                        "min-width": C.breakWith.length > 0 ? k : C.minWidth
                    };
                    p = 100 / f * C.weight;
                    var P = S["min-width"] / w * 100;
                    var n = Math.ceil(P);
                    var o = Math.floor(p);
                    if (o !== 100 && n > o) {
                        p = n
                    } else {
                        p = o
                    }
                    p = R < p ? R : p;
                    R -= p;
                    W.push(p);
                    if (R > 0 && j === (r[i].cont.length - 1)) {
                        p += R
                    }
                    l.push("sapUiRFLContainer");
                    S["width"] = p + "%";
                    S["min-width"] = S["min-width"] + "px";
                    this.oRm.writeHeader(H, S, l);
                    l.length = 0;
                    l.push("sapUiRFLContainerContent");
                    if (C.breakWith.length > 0) {
                        l.push("sapUiRFLMultiContainerContent")
                    }
                    if (C.padding) {
                        l.push("sapUiRFLPaddingClass")
                    }
                    S = {};
                    this.oRm.writeHeader("", S, l);
                    if (C.breakWith.length > 0) {
                        H = r[i].cont[j].id + "-multi0";
                        l.length = 0;
                        S = {
                            "min-width": k + "px"
                        };
                        var q = 100 / h * C.weight;
                        q = Math.floor(q);
                        B.push(q);
                        l.push("sapUiRFLMultiContent");
                        S["width"] = q + "%";
                        if (r[i].cont[j].padding) {
                            l.push("sapUiRFLPaddingClass")
                        }
                        this.oRm.writeHeader(H, S, l);
                        var v = q;
                        this.oRm.renderControl(C.control);
                        this.oRm.write("</div>");
                        for (e = 0; e < C.breakWith.length; e++) {
                            H = C.breakWith[e].id + '-multi' + (e + 1);
                            l.length = 0;
                            S = {
                                "min-width": C.breakWith[e].minWidth + "px"
                            };
                            q = 100 / h * C.breakWith[e].weight;
                            q = Math.floor(q);
                            B.push(q);
                            v += q;
                            if (v < 100 && e === (C.breakWith.length - 1)) {
                                q += 100 - v
                            }
                            l.push("sapUiRFLMultiContent");
                            S["width"] = q + "%";
                            if (C.breakWith[e].padding) {
                                l.push("sapUiRFLPaddingClass")
                            }
                            this.oRm.writeHeader(H, S, l);
                            this.oRm.renderControl(C.breakWith[e].control);
                            this.oRm.write("</div>")
                        }
                    } else {
                        this.oRm.renderControl(C.control)
                    }
                    this.oRm.write("</div>");
                    this.oRm.write("</div>")
                }
                this.oRm.write("</div>");
                this._iRowCounter++
            }
        };
        var b = function(I) {
            this._iRowCounter = 0;
            this._oDomRef = this.getDomRef();
            if (this._oDomRef) {
                var s = this.getId();
                var d = this._oDomRef.offsetWidth;
                var r = false;
                if (this._rows) {
                    for (var i = 0; i < this._rows.length; i++) {
                        var R = this._$DomRef.find("#" + s + "-row" + i);
                        var t = a(this._rows[i], d);
                        var C = g(this._rows[i], R, this);
                        r = c(C, t);
                        var o = R.rect();
                        var p = this._rows[i].oRect;
                        if (o && p) {
                            r = r || (o.width !== p.width) && (o.height !== p.height)
                        }
                        r = r || I;
                        if (this._bLayoutDataChanged || r) {
                            this._oDomRef.innerHTML = "";
                            this._bLayoutDataChanged = false;
                            this.renderContent(t, d)
                        }
                    }
                    if (this._oDomRef.innerHTML === "") {
                        this.oRm.flush(this._oDomRef);
                        for (var i = 0; i < this._rows.length; i++) {
                            var T = jQuery.sap.byId(s + "-row" + i).rect();
                            this._rows[i].oRect = T
                        }
                    }
                    if (this._rows.length === 0) {
                        if (this._resizeHandlerComputeWidthsID) {
                            sap.ui.core.ResizeHandler.deregister(this._resizeHandlerComputeWidthsID)
                        }
                    }
                }
            }
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.onBeforeRendering = function() {
            u(this);
            if (this._resizeHandlerFullLengthID) {
                sap.ui.core.ResizeHandler.deregister(this._resizeHandlerFullLengthID)
            }
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.onAfterRendering = function(e) {
            this._oDomRef = this.getDomRef();
            this._$DomRef = jQuery(this._oDomRef);
            if (this.getResponsive()) {
                this._proxyComputeWidths(true);
                if (!this._resizeHandlerComputeWidthsID) {
                    this._resizeHandlerComputeWidthsID = sap.ui.core.ResizeHandler.register(this, this._proxyComputeWidths)
                }
            }
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.onThemeChanged = function(e) {
            if (e.type === "LayoutDataChange") {
                this._bLayoutDataChanged = true
            }
            if (!this._resizeHandlerComputeWidthsID) {
                this._resizeHandlerComputeWidthsID = sap.ui.core.ResizeHandler.register(this, this._proxyComputeWidths)
            }
            u(this);
            this._proxyComputeWidths()
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.onLayoutDataChange = sap.ui.layout.ResponsiveFlowLayout.prototype.onThemeChanged;
        var _ = function(C) {
            var l = C.getLayoutData();
            if (!l) {
                return undefined
            } else if (l instanceof sap.ui.layout.ResponsiveFlowLayoutData) {
                return l
            } else if (l.getMetadata().getName() == "sap.ui.core.VariantLayoutData") {
                var L = l.getMultipleLayoutData();
                for (var i = 0; i < L.length; i++) {
                    var o = L[i];
                    if (o instanceof sap.ui.layout.ResponsiveFlowLayoutData) {
                        return o
                    }
                }
            }
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.addContent = function(C) {
            if (C && this._IntervalCall) {
                jQuery.sap.clearDelayedCall(this._IntervalCall);
                this._IntervalCall = undefined
            }
            this.addAggregation("content", C)
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.insertContent = function(C, i) {
            if (C && this._IntervalCall) {
                jQuery.sap.clearDelayedCall(this._IntervalCall);
                this._IntervalCall = undefined
            }
            this.insertAggregation("content", C, i)
        };
        sap.ui.layout.ResponsiveFlowLayout.prototype.removeContent = function(C) {
            if (C && this._IntervalCall) {
                jQuery.sap.clearDelayedCall(this._IntervalCall);
                this._IntervalCall = undefined
            }
            this.removeAggregation("content", C)
        }
    }())
};
if (!jQuery.sap.isDeclared('sap.ui.layout.form.ResponsiveLayout')) {
    jQuery.sap.declare("sap.ui.layout.form.ResponsiveLayout");
    sap.ui.layout.form.FormLayout.extend("sap.ui.layout.form.ResponsiveLayout", {
        metadata: {
            library: "sap.ui.layout"
        }
    });
    sap.ui.core.Control.extend("sap.ui.layout.form.ResponsiveLayoutPanel", {
        metadata: {
            aggregations: {
                "content": {
                    type: "sap.ui.layout.ResponsiveFlowLayout",
                    multiple: false
                }
            },
            associations: {
                "container": {
                    type: "sap.ui.layout.form.FormContainer",
                    multiple: false
                },
                "layout": {
                    type: "sap.ui.layout.form.ResponsiveLayout",
                    multiple: false
                },
            }
        },
        getLayoutData: function() {
            var c = sap.ui.getCore().byId(this.getContainer());
            var l = sap.ui.getCore().byId(this.getLayout());
            var L;
            if (l && c) {
                L = l.getLayoutDataForElement(c, "sap.ui.layout.ResponsiveFlowLayoutData")
            }
            return L
        },
        getCustomData: function() {
            var c = sap.ui.getCore().byId(this.getContainer());
            if (c) {
                return c.getCustomData()
            }
        },
        refreshExpanded: function() {
            var c = sap.ui.getCore().byId(this.getContainer());
            if (c) {
                if (c.getExpanded()) {
                    jQuery.sap.byId(this.getId()).removeClass("sapUiRLContainerColl")
                } else {
                    jQuery.sap.byId(this.getId()).addClass("sapUiRLContainerColl")
                }
            }
        },
        renderer: function(r, p) {
            var c = sap.ui.getCore().byId(p.getContainer());
            var l = sap.ui.getCore().byId(p.getLayout());
            var C = p.getContent();
            var e = c.getExpandable();
            var t = c.getTooltip_AsString();
            r.write("<div");
            r.writeControlData(p);
            r.addClass("sapUiRLContainer");
            if (e && !c.getExpanded()) {
                r.addClass("sapUiRLContainerColl")
            }
            if (t) {
                r.writeAttributeEscaped('title', t)
            }
            r.writeClasses();
            r.write(">");
            if (c.getTitle()) {
                l.getRenderer().renderTitle(r, c.getTitle(), c._oExpandButton, e, false, c.getId())
            }
            if (C) {
                r.write("<div");
                r.addClass("sapUiRLContainerCont");
                r.writeClasses();
                r.write(">");
                r.renderControl(C);
                r.write("</div>")
            }
            r.write("</div>")
        }
    });
    (function() {
        sap.ui.layout.form.ResponsiveLayout.prototype.init = function() {
            this.mContainers = {};
            this._defaultLayoutData = new sap.ui.layout.ResponsiveFlowLayoutData({
                margin: false
            })
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.exit = function() {
            var t = this;
            for (var C in this.mContainers) {
                h(t, C)
            }
            if (this._mainRFLayout) {
                this._mainRFLayout.destroy();
                delete this._mainRFLayout
            }
            this._defaultLayoutData.destroy();
            delete this._defaultLayoutData
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.onBeforeRendering = function(E) {
            var F = this.getParent();
            if (!F || !(F instanceof sap.ui.layout.form.Form)) {
                return
            }
            var t = this;
            _(t, F);
            k(t, F)
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.contentOnAfterRendering = function(F, C) {
            sap.ui.layout.form.FormLayout.prototype.contentOnAfterRendering.apply(this, arguments);
            if (C.getWidth && !C.getWidth()) {
                C.$().css("width", "100%")
            }
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.toggleContainerExpanded = function(C) {
            var E = C.getExpanded();
            var s = C.getId();
            if (this.mContainers[s] && this.mContainers[s][0]) {
                var p = this.mContainers[s][0];
                p.refreshExpanded()
            }
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.onLayoutDataChange = function(E) {
            var s = E.srcControl;
            if (s instanceof sap.ui.layout.form.FormContainer) {
                if (this._mainRFLayout) {
                    this._mainRFLayout.onLayoutDataChange(E)
                }
            } else if (s instanceof sap.ui.layout.form.FormElement) {
                var C = s.getParent().getId();
                if (this.mContainers[C] && this.mContainers[C][1]) {
                    this.mContainers[C][1].onLayoutDataChange(E)
                }
            } else {
                var p = s.getParent();
                if (p instanceof sap.ui.layout.form.FormElement) {
                    var o = p.getParent();
                    var C = o.getId();
                    var i = p.getId();
                    if (this.mContainers[C] && this.mContainers[C][2] && this.mContainers[C][2][i]) {
                        this.mContainers[C][2][i][0].onLayoutDataChange(E)
                    }
                }
            }
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.onsapup = function(E) {
            this.onsapleft(E)
        };
        sap.ui.layout.form.ResponsiveLayout.prototype.onsapdown = function(E) {
            this.onsapright(E)
        };
        var _ = function(L, F) {
            var C = F.getFormContainers();
            var m = C.length;
            var v = 0;
            for (var i = 0; i < m; i++) {
                var o = C[i];
                if (o.getVisible()) {
                    v++;
                    var s = o.getId();
                    var p = undefined;
                    var r = undefined;
                    if (L.mContainers[s] && L.mContainers[s][1]) {
                        r = L.mContainers[s][1]
                    } else {
                        r = d(L, o, undefined)
                    }
                    var t = o.getTitle();
                    if (t || o.getExpandable()) {
                        if (L.mContainers[s] && L.mContainers[s][0]) {
                            p = L.mContainers[s][0]
                        } else {
                            p = a(L, o, r);
                            e(r, true)
                        }
                    } else {
                        if (L.mContainers[s] && L.mContainers[s][0]) {
                            b(L.mContainers[s][0]);
                            e(r, false)
                        }
                    }
                    var n = c(L, o, r);
                    L.mContainers[s] = [p, r, n]
                }
            }
            var O = l(L.mContainers);
            if (v < O) {
                for (var s in L.mContainers) {
                    var q = false;
                    for (var i = 0; i < m; i++) {
                        var o = C[i];
                        if (s == o.getId() && o.getVisible()) {
                            q = true;
                            break
                        }
                    }
                    if (!q) {
                        h(L, s)
                    }
                }
            }
        };
        var a = function(L, C, r) {
            var s = C.getId();
            var p = new sap.ui.layout.form.ResponsiveLayoutPanel(s + "--Panel", {
                container: C,
                layout: L,
                content: r
            });
            return p
        };
        var b = function(p) {
            p.setContent("");
            p.setLayout("");
            p.setContainer("");
            p.destroy();
            delete p
        };
        var c = function(L, C, o) {
            var s = C.getId();
            var E = C.getFormElements();
            var m = E.length;
            var v = 0;
            var r = {};
            if (L.mContainers[s] && L.mContainers[s][2]) {
                r = L.mContainers[s][2]
            }
            var R;
            var F;
            var n = -1;
            for (var i = 0; i < m; i++) {
                var p = E[i];
                if (p.getVisible()) {
                    var q = p.getId();
                    j(L, C, p, r, o, i);
                    if (r[q]) {
                        R = r[q][0];
                        n = o.indexOfContent(R)
                    } else {
                        R = d(L, C, p);
                        R.addStyleClass("sapUiRLElement");
                        if (p.getLabel()) {
                            R.addStyleClass("sapUiRLElementWithLabel")
                        }
                        r[q] = [R, undefined];
                        n++;
                        o.insertContent(R, n)
                    }
                    var t = p.getFields();
                    if (p.getLabel() && t.length > 1) {
                        if (r[q][1]) {
                            F = r[q][1]
                        } else {
                            F = d(L, C, p, true);
                            F.addStyleClass("sapUiRLElementFields");
                            r[q][1] = F
                        }
                        f(L, F, t)
                    } else {
                        if (r[q][1]) {
                            F = r[q][1];
                            g(F);
                            r[q][1] = undefined
                        }
                    }
                    v++
                }
            }
            var O = l(r);
            if (v < O) {
                for (var q in r) {
                    var u = false;
                    for (var i = 0; i < m; i++) {
                        var p = E[i];
                        if (q == p.getId() && p.getVisible()) {
                            u = true;
                            break
                        }
                    }
                    if (!u) {
                        if (r[q][1]) {
                            F = r[q][1];
                            g(F)
                        }
                        R = r[q][0];
                        o.removeContent(R);
                        g(R);
                        delete r[q]
                    }
                }
            }
            return r
        };
        var d = function(L, C, E, i) {
            var I;
            if (E && !i) {
                I = E.getId() + "--RFLayout"
            } else if (E && i) {
                I = E.getId() + "--content--RFLayout"
            } else if (C) {
                I = C.getId() + "--RFLayout"
            } else {
                return
            }
            var r = new sap.ui.layout.ResponsiveFlowLayout(I);
            r.__myParentLayout = L;
            r.__myParentContainerId = C.getId();
            if (E) {
                r.__myParentElementId = E.getId();
                if (!i) {
                    r.getContent = function() {
                        var E = sap.ui.getCore().byId(this.__myParentElementId);
                        if (E) {
                            var m = new Array();
                            var o = E.getLabelControl();
                            var F = E.getFields();
                            if (!o || F.length <= 1) {
                                var m = F;
                                if (o) {
                                    m.unshift(o)
                                }
                            } else {
                                var L = this.__myParentLayout;
                                var s = this.__myParentContainerId;
                                var n = E.getId();
                                if (o) {
                                    m.push(o)
                                }
                                if (L.mContainers[s] && L.mContainers[s][2] && L.mContainers[s][2][n]) {
                                    m.push(L.mContainers[s][2][n][1])
                                }
                            }
                            return m
                        } else {
                            return false
                        }
                    }
                } else {
                    r.getContent = function() {
                        var E = sap.ui.getCore().byId(this.__myParentElementId);
                        if (E) {
                            return E.getFields()
                        } else {
                            return false
                        }
                    }
                }
            }
            if ((E && !i) || (!E && !C.getTitle() && !C.getExpandable())) {
                e(r, false)
            } else {
                r.setLayoutData(new sap.ui.layout.ResponsiveFlowLayoutData({
                    margin: false
                }))
            }
            return r
        };
        var e = function(r, o) {
            if (o) {
                if (r.__originalGetLayoutData) {
                    r.getLayoutData = r.__originalGetLayoutData;
                    delete r.__originalGetLayoutData
                }
            } else if (!r.__originalGetLayoutData) {
                r.__originalGetLayoutData = r.getLayoutData;
                r.getLayoutData = function() {
                    var L = this.__myParentLayout;
                    var C = sap.ui.getCore().byId(this.__myParentContainerId);
                    var E = sap.ui.getCore().byId(this.__myParentElementId);
                    var i;
                    if (E) {
                        i = L.getLayoutDataForElement(E, "sap.ui.layout.ResponsiveFlowLayoutData")
                    } else if (C) {
                        i = L.getLayoutDataForElement(C, "sap.ui.layout.ResponsiveFlowLayoutData")
                    }
                    if (i) {
                        return i
                    } else {
                        return L._defaultLayoutData
                    }
                }
            }
        };
        var f = function(L, r, F) {
            var o;
            var w = 0;
            for (var i = 0; i < F.length; i++) {
                var m = F[i];
                o = L.getLayoutDataForElement(m, "sap.ui.layout.ResponsiveFlowLayoutData");
                if (o) {
                    w = w + o.getWeight()
                } else {
                    w++
                }
            }
            o = r.getLayoutData();
            if (o) {
                o.setWeight(w)
            } else {
                r.setLayoutData(new sap.ui.layout.ResponsiveFlowLayoutData({
                    weight: w
                }))
            }
        };
        var g = function(r) {
            if (r.__myParentContainerId) {
                r.__myParentContainerId = undefined
            }
            if (r.__myParentElementId) {
                r.__myParentElementId = undefined
            }
            r.__myParentLayout = undefined;
            r.destroy();
            delete r
        };
        var h = function(L, C) {
            var i = L.mContainers[C];
            var r;
            var E = i[2];
            if (E) {
                for (var s in E) {
                    if (E[s][1]) {
                        g(E[s][1])
                    }
                    r = E[s][0];
                    g(r);
                    delete E[s]
                }
            }
            r = i[1];
            if (r) {
                r.removeAllContent();
                g(r)
            }
            var p = i[0];
            if (p) {
                b(p)
            }
            delete L.mContainers[C]
        };
        var j = function(L, C, E, r, o, i) {
            var s = E.getId();
            var I = s + "--RFLayout";
            var R = sap.ui.getCore().byId(I);
            if (!r[s] && R) {
                var O = R.__myParentContainerId;
                r[s] = L.mContainers[O][2][s];
                o.insertContent(R, i);
                R.__myParentContainerId = C.getId();
                if (r[s][1]) {
                    r[s][1].__myParentContainerId = C.getId()
                }
                delete L.mContainers[O][2][s]
            }
        };
        var k = function(L, F) {
            var C = F.getFormContainers();
            var m = 0;
            var n = 0;
            for (var i = 0; i < C.length; i++) {
                var o = C[i];
                if (o.getVisible()) {
                    m++
                }
            }
            if (m > 1) {
                if (!L._mainRFLayout) {
                    L._mainRFLayout = new sap.ui.layout.ResponsiveFlowLayout(F.getId() + "--RFLayout").setParent(L)
                } else {
                    var p = L._mainRFLayout.getContent();
                    n = p.length;
                    var E = false;
                    for (var i = 0; i < n; i++) {
                        var q = p[i];
                        var o = undefined;
                        if (q.getContainer) {
                            o = sap.ui.getCore().byId(q.getContainer())
                        } else {
                            o = sap.ui.getCore().byId(q.__myParentContainerId)
                        }
                        if (o && o.getVisible()) {
                            var r = L.mContainers[o.getId()];
                            if (r[0] && r[0] != q) {
                                E = true;
                                break
                            }
                            if (!r[0] && r[1] && r[1] != q) {
                                E = true;
                                break
                            }
                        } else {
                            L._mainRFLayout.removeContent(q)
                        }
                    }
                    if (E) {
                        L._mainRFLayout.removeAllContent();
                        n = 0
                    }
                }
                if (n < m) {
                    for (var i = 0; i < C.length; i++) {
                        var o = C[i];
                        if (o.getVisible()) {
                            var s = o.getId();
                            if (L.mContainers[s]) {
                                if (L.mContainers[s][0]) {
                                    L._mainRFLayout.addContent(L.mContainers[s][0])
                                } else if (L.mContainers[s][1]) {
                                    L._mainRFLayout.addContent(L.mContainers[s][1])
                                }
                            }
                        }
                    }
                }
            }
        };
        var l = function(o) {
            var L = 0;
            if (!Object.keys) {
                jQuery.each(o, function() {
                    L++
                })
            } else {
                L = Object.keys(o).length
            }
            return L
        }
    }())
};