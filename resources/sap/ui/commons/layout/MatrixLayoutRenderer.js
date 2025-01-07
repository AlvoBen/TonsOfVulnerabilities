﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.layout.MatrixLayoutRenderer");
sap.ui.commons.layout.MatrixLayoutRenderer = {};

sap.ui.commons.layout.MatrixLayoutRenderer.render = function(R, m) {
    var a = R;
    var r = sap.ui.commons.layout.MatrixLayoutRenderer;
    if (!m.getVisible()) {
        return
    }
    var b = sap.ui.getCore().getConfiguration().getRTL();
    a.write("<TABLE role=\"presentation\"");
    a.writeControlData(m);
    a.write(" cellpadding=\"0\" cellspacing=\"0\"");
    a.addStyle("border-collapse", "collapse");
    var M = m.getWidth();
    if (M) {
        a.addStyle("width", M)
    }
    var s = m.getHeight();
    if (s && s != 'auto') {
        a.addStyle("height", s);
        var o = r.getValueUnit(s)
    }
    if (m.getLayoutFixed()) {
        a.addStyle("table-layout", "fixed");
        if (!M) {
            a.addStyle("width", "100%")
        }
    }
    a.addClass("sapUiMlt");
    a.writeStyles();
    a.writeClasses();
    if (m.getTooltip_AsString()) {
        a.writeAttributeEscaped('title', m.getTooltip_AsString())
    }
    a.write('>');
    var c = m.getRows();
    var C = m.getColumns();
    if (C < 1) {
        for (var i = 0; i < c.length; i++) {
            var d = c[i];
            var e = d.getCells();
            if (C < e.length) {
                C = e.length
            }
        }
    }
    if (C > 0) {
        var w = m.getWidths();
        a.write("<colgroup>");
        for (var j = 0; j < C; j++) {
            a.write("<col");
            if (w && w[j] && w[j] != "auto") {
                a.addStyle('width', w[j]);
                a.writeStyles()
            }
            a.write("/>")
        }
        a.write("</colgroup>")
    }
    var D = true;
    var f = false;
    a.write('<TBODY style="width: 100%; height: 100%">');
    for (var i = 0; i < c.length; i++) {
        var d = c[i];
        var g = d.getHeight();
        if (g == "auto") {
            g = ""
        }
        if (g && o) {
            var h = r.getValueUnit(g);
            if (h.Unit == '%' && o.Unit != '%') {
                g = (o.Value * h.Value / 100) + o.Unit
            }
        }
        a.write("<tr");
        a.writeElementData(d);
        a.writeClasses(d);
        if (d.getTooltip_AsString()) {
            a.writeAttributeEscaped('title', d.getTooltip_AsString())
        }
        if (sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version >= 9 && g) {
            a.addStyle("height", g);
            a.writeStyles()
        }
        a.write(">");
        var e = d.getCells();
        var k = C;
        if (C < 1) {
            k = e.length
        }
        f = false;
        var l = 0;
        if (!d.RowSpanCells) {
            d.RowSpanCells = 0
        } else {
            f = true
        }
        for (var j = 0; j < k; j++) {
            if (j >= (k - l - d.RowSpanCells)) {
                break
            }
            var n = e[j];
            a.write("<td");
            if (g && (!n || n.getRowSpan() == 1)) {
                a.addStyle("height", g)
            }
            if (n) {
                a.writeElementData(n);
                if (n.getTooltip_AsString()) {
                    a.writeAttributeEscaped('title', n.getTooltip_AsString())
                }
                if (m.getLayoutFixed() && n.getContent().length > 0) {
                    a.addStyle("overflow", "hidden")
                }
                var H = r.getHAlign(n.getHAlign(), b);
                if (H) {
                    a.writeAttribute("align", H)
                }
                var v = r.getVAlign(n.getVAlign());
                if (v && v != "middle") {
                    a.writeAttribute("valign", v)
                }
                if (n.getColSpan() > 1) {
                    a.writeAttribute("colspan", n.getColSpan());
                    l = l + n.getColSpan() - 1;
                    f = true
                }
                if (n.getRowSpan() > 1) {
                    a.writeAttribute("rowspan", n.getRowSpan());
                    var V = 0;
                    var u = "";
                    for (var x = 0; x < n.getRowSpan(); x++) {
                        var p = c[i + x];
                        if (!p) {
                            u = false;
                            break
                        }
                        if (!p.RowSpanCells) {
                            p.RowSpanCells = 0
                        }
                        if (x > 0) {
                            p.RowSpanCells = p.RowSpanCells + n.getColSpan()
                        }
                        var q = p.getHeight();
                        if (!q || q == "auto") {
                            u = false
                        } else {
                            var t = r.getValueUnit(q);
                            if (t.Unit == '%' && o.Unit != '%') {
                                t.Value = (o.Value * h.Value / 100);
                                t.Unit = o.Unit
                            }
                            if (u == "") {
                                u = t.Unit
                            } else {
                                if (u != t.Unit) {
                                    u = false
                                }
                            }
                            V = V + t.Value
                        }
                    }
                    if (u != false) {
                        var S = V + u;
                        a.addStyle("height", S)
                    }
                }
                a.addClass(r.getBackgroundClass(n.getBackgroundDesign()));
                a.addClass(r.getSeparationClass(n.getSeparation()));
                if (!m.getLayoutFixed() || !g) {
                    a.addClass(r.getPaddingClass(n.getPadding()));
                    a.addClass("sapUiMltCell")
                } else {
                    a.addStyle("white-space", "nowrap")
                }
                a.writeClasses(n)
            }
            a.writeStyles();
            a.write(">");
            if (n) {
                if (m.getLayoutFixed() && g) {
                    a.write('<div');
                    if (n.getRowSpan() != 1 && S && S.search('%') == -1) {
                        a.addStyle("height", S)
                    } else if (g.search('%') != -1 || (n.getRowSpan() != 1 && !S)) {
                        a.addStyle("height", '100%')
                    } else {
                        a.addStyle("height", g)
                    }
                    a.addStyle("display", "inline-block");
                    if (v) {
                        a.addStyle("vertical-align", v)
                    }
                    a.writeStyles();
                    a.writeClasses(false);
                    a.write("></div>");
                    a.write('<div');
                    a.addStyle("display", "inline-block");
                    if (v) {
                        a.addStyle("vertical-align", v)
                    }
                    if (n.getRowSpan() != 1 && S && S.search('%') == -1) {
                        a.addStyle("max-height", S)
                    } else if (g.search('%') != -1 || (n.getRowSpan() != 1 && !S)) {
                        a.addStyle("max-height", '100%')
                    } else {
                        a.addStyle("max-height", g)
                    }
                    var y;
                    var z = "0";
                    var A = "";
                    var I = "0";
                    var B = n.getContent();
                    for (var E = 0, F = B.length; E < F; E++) {
                        if (B[E].getHeight && B[E].getHeight() != "") {
                            var G = r.getValueUnit(B[E].getHeight());
                            if (G) {
                                if (A == "") {
                                    A = G.Unit
                                }
                                if (A != G.Unit) {
                                    A = "%";
                                    z = "100";
                                    break
                                }
                                if (G.Unit == "%") {
                                    if (parseFloat(z) < parseFloat(G.Value)) {
                                        z = G.Value;
                                        if (z != "100") {
                                            I = 10000 / parseFloat(z)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (z != "0") {
                        a.addStyle("height", z + A)
                    }
                    a.addStyle("white-space", "normal");
                    a.addStyle("width", "100%");
                    a.writeStyles();
                    a.writeClasses(false);
                    a.write("><div");
                    a.addStyle("overflow", "hidden");
                    a.addStyle("text-overflow", "inherit");
                    if (z != "0") {
                        if (I != "0") {
                            a.addStyle("height", I + "%")
                        } else {
                            a.addStyle("height", "100%")
                        }
                    }
                    a.addClass("sapUiMltCell");
                    a.addClass(r.getPaddingClass(n.getPadding()));
                    a.writeStyles();
                    a.writeClasses(false);
                    a.write(">")
                }
                var B = n.getContent();
                for (var E = 0, F = B.length; E < F; E++) {
                    R.renderControl(B[E])
                }
                if (m.getLayoutFixed() && g) {
                    a.write("</div></div>")
                }
            }
            a.write("</td>")
        }
        a.write("</tr>");
        d.RowSpanCells = undefined;
        if (!f) {
            D = false
        }
    }
    if (D && sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version >= 9) {
        a.write("<tr style='height:0;'>");
        for (var i = 0; i < C; i++) {
            a.write("<td></td>")
        }
        a.write("</tr>")
    }
    a.write("</TBODY></TABLE>")
};

sap.ui.commons.layout.MatrixLayoutRenderer.getHAlign = function(h, r) {
    switch (h) {
        case sap.ui.commons.layout.HAlign.Begin:
            return null;
        case sap.ui.commons.layout.HAlign.Center:
            return "center";
        case sap.ui.commons.layout.HAlign.End:
            return r ? "left":
                "right";
            case sap.ui.commons.layout.HAlign.Left:
                return r ? "left":
                    null;
                case sap.ui.commons.layout.HAlign.Right:
                    return r ? null:
                        "right"
    }
    return null
};

sap.ui.commons.layout.MatrixLayoutRenderer.getVAlign = function(v) {
    switch (v) {
        case sap.ui.commons.layout.VAlign.Bottom:
            return "bottom";
        case sap.ui.commons.layout.VAlign.Middle:
            return "middle";
        case sap.ui.commons.layout.VAlign.Top:
            return "top"
    }
    return null
};

sap.ui.commons.layout.MatrixLayoutRenderer.getBackgroundClass = function(b) {
    switch (b) {
        case sap.ui.commons.layout.BackgroundDesign.Border:
            return "sapUiMltBgBorder";
        case sap.ui.commons.layout.BackgroundDesign.Fill1:
            return "sapUiMltBgFill1";
        case sap.ui.commons.layout.BackgroundDesign.Fill2:
            return "sapUiMltBgFill2";
        case sap.ui.commons.layout.BackgroundDesign.Fill3:
            return "sapUiMltBgFill3";
        case sap.ui.commons.layout.BackgroundDesign.Header:
            return "sapUiMltBgHeader";
        case sap.ui.commons.layout.BackgroundDesign.Plain:
            return "sapUiMltBgPlain";
        case sap.ui.commons.layout.BackgroundDesign.Transparent:
            return null
    }
    return null
};

sap.ui.commons.layout.MatrixLayoutRenderer.getPaddingClass = function(p) {
    switch (p) {
        case sap.ui.commons.layout.Padding.None:
            return "sapUiMltPadNone";
        case sap.ui.commons.layout.Padding.Begin:
            return "sapUiMltPadLeft";
        case sap.ui.commons.layout.Padding.End:
            return "sapUiMltPadRight";
        case sap.ui.commons.layout.Padding.Both:
            return "sapUiMltPadBoth";
        case sap.ui.commons.layout.Padding.Neither:
            return "sapUiMltPadNeither"
    }
    return null
};

sap.ui.commons.layout.MatrixLayoutRenderer.getSeparationClass = function(s) {
    switch (s) {
        case sap.ui.commons.layout.Separation.None:
            return null;
        case sap.ui.commons.layout.Separation.Small:
            return "sapUiMltSepS";
        case sap.ui.commons.layout.Separation.SmallWithLine:
            return "sapUiMltSepSWL";
        case sap.ui.commons.layout.Separation.Medium:
            return "sapUiMltSepM";
        case sap.ui.commons.layout.Separation.MediumWithLine:
            return "sapUiMltSepMWL";
        case sap.ui.commons.layout.Separation.Large:
            return "sapUiMltSepL";
        case sap.ui.commons.layout.Separation.LargeWithLine:
            return "sapUiMltSepLWL"
    }
    return null
};

sap.ui.commons.layout.MatrixLayoutRenderer.getValueUnit = function(s) {
    var v = 0;
    var u = "";
    var p = s.search('px');
    if (p > -1) {
        u = "px";
        v = parseInt(s.slice(0, p), 10);
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('pt');
    if (p > -1) {
        u = "pt";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('in');
    if (p > -1) {
        u = "in";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('mm');
    if (p > -1) {
        u = "mm";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('cm');
    if (p > -1) {
        u = "cm";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('em');
    if (p > -1) {
        u = "em";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('ex');
    if (p > -1) {
        u = "ex";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
    p = s.search('%');
    if (p > -1) {
        u = "%";
        v = parseFloat(s.slice(0, p));
        return ({
            Value: v,
            Unit: u
        })
    }
};