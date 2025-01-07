﻿/*!
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
};