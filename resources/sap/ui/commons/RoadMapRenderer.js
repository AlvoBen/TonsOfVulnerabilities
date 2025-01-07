﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.RoadMapRenderer");
(function() {
    sap.ui.commons.RoadMapRenderer = {};
    sap.ui.commons.RoadMapRenderer.render = function(R, j) {
        var l = R;
        j.doBeforeRendering();
        if (!j.getVisible()) {
            return
        }
        l.write("<div");
        l.writeControlData(j);
        l.addClass("sapUiRoadMap");
        l.writeClasses();
        l.writeAttribute("tabIndex", "0");
        var t = j.getTooltip_AsString();
        if (t) {
            l.writeAttributeEscaped("title", t)
        }
        l.writeAttribute("style", "width:" + (j.getWidth() ? j.getWidth() : "100%") + ";");
        l.write(">");
        r(l, j, true);
        l.write("<ul");
        l.writeAttribute("id", j.getId() + "-steparea");
        l.addClass("sapUiRoadMapStepArea");
        l.writeClasses();
        if (sap.ui.getCore().getConfiguration().getAccessibility()) {
            l.writeAttribute("role", "group");
            l.writeAttributeEscaped("aria-label", k("RDMP_DEFAULT_TOOLTIP", []));
            if (t) {
                l.writeAttributeEscaped("title", t)
            }
        }
        l.write(">");
        var S = j.getSteps();
        for (var i = 0; i < S.length; i++) {
            var v = S[i];
            if (v.getSubSteps().length > 0) {
                f(l, j, v)
            } else {
                a(l, j, v)
            }
        }
        l.write("</ul>");
        r(l, j, false);
        l.write("</div>")
    };
    sap.ui.commons.RoadMapRenderer.selectStepWithId = function(R, i) {
        var C = R.getSelectedStep();
        if (C) {
            jQuery.sap.byId(C).removeClass("sapUiRoadMapSelected")
        }
        if (i) {
            jQuery.sap.byId(i).addClass("sapUiRoadMapSelected")
        }
        if (sap.ui.getCore().getConfiguration().getAccessibility()) {
            if (C) {
                jQuery.sap.byId(C + "-box").removeAttr("aria-checked")
            }
            if (i) {
                jQuery.sap.byId(i + "-box").attr("aria-checked", true)
            }
        }
    };
    sap.ui.commons.RoadMapRenderer.selectStep = function(R, S, I, j, E, l) {
        if (!l) {
            sap.ui.commons.RoadMapRenderer.selectStepWithId(R, S.getId())
        }
        if (!I && S.getSubSteps().length > 0) {
            var t = S.getSubSteps();
            var D = jQuery.sap.byId(S.getId());
            var v = D.hasClass("sapUiRoadMapExpanded");
            var C = 1;
            var x = function() {
                C--;
                if (C > 0) {
                    return
                }
                if (E) {
                    E(!v ? "expanded" : "collapsed")
                }
                sap.ui.commons.RoadMapRenderer.updateStepArea(R)
            };
            var A = function(z, O, B) {
                var F = jQuery.sap.byId(z);
                if (!jQuery.fx.off && !j) {
                    F.width(O ? "0px" : R.iStepWidth);
                    var L = jQuery.sap.byId(z + "-label");
                    L.addClass("sapUiRoadMapHidden");
                    if (O) {
                        F.toggleClass("sapUiRoadMapHidden")
                    }
                    F.animate({
                        width: O ? R.iStepWidth : "0px"
                    }, "fast", function() {
                        if (!O) {
                            F.toggleClass("sapUiRoadMapHidden")
                        }
                        F.width("");
                        L.removeClass("sapUiRoadMapHidden");
                        if (B) {
                            B()
                        }
                    })
                } else {
                    F.toggleClass("sapUiRoadMapHidden");
                    if (B) {
                        B()
                    }
                }
            };
            D.toggleClass("sapUiRoadMapExpanded");
            if (sap.ui.getCore().getConfiguration().getAccessibility()) {
                var y = D.hasClass("sapUiRoadMapExpanded");
                jQuery.sap.byId(S.getId() + "-box").attr("aria-expanded", y);
                jQuery.sap.byId(S.getId() + "-expandend-box").attr("aria-expanded", y)
            }
            for (var i = 0; i < t.length; i++) {
                if (t[i].getVisible()) {
                    C++;
                    A(t[i].getId(), !v, x)
                }
            }
            A(S.getId() + "-expandend", !v, x)
        } else {
            if (E) {
                E("selected")
            }
        }
    };
    sap.ui.commons.RoadMapRenderer.updateStepArea = function(R) {
        if (R.iStepWidth != -1) {
            var S = jQuery.sap.byId(R.getId() + "-steparea");
            var j = jQuery.sap.byId(R.getId() + "-Start");
            var E = jQuery.sap.byId(R.getId() + "-End");
            var i = jQuery.sap.byId(R.getId());
            var l = S.scrollLeft();
            var A = i.width() - j.outerWidth(true) - E.outerWidth(true);
            var M = R.getNumberOfVisibleSteps();
            var C = m(R);
            if (M < 1) {
                M = C
            } else {
                M = Math.min(M, C)
            }
            var P = Math.floor(A / R.iStepWidth);
            var N = Math.min(M, P);
            S.width(N * R.iStepWidth).scrollLeft(l);
            u(R)
        }
    };
    sap.ui.commons.RoadMapRenderer.updateScrollArea = function(R, S) {
        sap.ui.commons.RoadMapRenderer.updateStepArea(R);
        if (!S) {
            var j = jQuery.sap.byId(R.getId() + "-steparea");
            var P = q(R, false);
            if (R.getFirstVisibleStep()) {
                var i = jQuery.sap.byId(R.getFirstVisibleStep());
                P = n(j, i)
            }
            s(R, P + o() * j.scrollLeft(), true)
        }
    };
    sap.ui.commons.RoadMapRenderer.isVisibleRef = function(R, I) {
        var S = jQuery.sap.byId(R.getId() + "-steparea");
        var j = S.children(":visible");
        for (var i = 0; i < j.length; i++) {
            var C = jQuery(j.get(i));
            if (C.attr("id") == I) {
                var P = n(S, C);
                return P >= 0 && P < S.width()
            }
        }
        return false
    };
    sap.ui.commons.RoadMapRenderer.getFirstVisibleRef = function(R) {
        var S = jQuery.sap.byId(R.getId() + "-steparea");
        var j = S.children(":visible");
        for (var i = 0; i < j.length; i++) {
            var C = jQuery(j.get(i));
            if (n(S, C) == 0) {
                return C
            }
        }
        return null
    };
    sap.ui.commons.RoadMapRenderer.setStepLabel = function(S, L) {
        var l = L ? jQuery.sap.escapeHTML(L) : "";
        jQuery.sap.byId(S.getId() + "-label").html(l);
        jQuery.sap.byId(S.getId() + "-expandend-label").html(l);
        if (!sap.ui.getCore().getConfiguration().getAccessibility()) {
            return
        }
        jQuery.sap.byId(S.getId() + "-box").attr("aria-label", c(S, L));
        jQuery.sap.byId(S.getId() + "-expandend-box").attr("aria-label", c(S, L))
    };
    sap.ui.commons.RoadMapRenderer.setStepEnabled = function(R, S, E) {
        var j = jQuery.sap.byId(S.getId());
        var i = jQuery.sap.byId(S.getId() + "-expandend");
        if (E) {
            j.removeClass("sapUiRoadMapDisabled");
            i.removeClass("sapUiRoadMapDisabled");
            if (sap.ui.getCore().getConfiguration().getAccessibility()) {
                jQuery.sap.byId(S.getId() + "-box").removeAttr("aria-disabled");
                jQuery.sap.byId(S.getId() + "-expandend-box").removeAttr("aria-disabled")
            }
            return false
        } else {
            var l = R.getSelectedStep() == S.getId();
            if (l) {
                j.removeClass("sapUiRoadMapSelected")
            }
            j.addClass("sapUiRoadMapDisabled");
            i.addClass("sapUiRoadMapDisabled");
            if (sap.ui.getCore().getConfiguration().getAccessibility()) {
                var t = jQuery.sap.byId(S.getId() + "-box");
                t.attr("aria-disabled", true);
                if (l) {
                    t.removeAttr("aria-checked")
                }
                jQuery.sap.byId(S.getId() + "-expandend-box").attr("aria-disabled", true)
            }
            return l
        }
    };
    sap.ui.commons.RoadMapRenderer.setStepVisible = function(R, S, I, v) {
        var j = jQuery.sap.byId(S.getId());
        var l = jQuery.sap.byId(S.getId() + "-expandend");
        var t = R.getSelectedStep() == S.getId();
        var P = S.getParent();
        if (I) {
            if (P.getEnabled() && P.getVisible() && P.getExpanded()) {
                if (v) {
                    j.removeClass("sapUiRoadMapHidden")
                } else {
                    j.addClass("sapUiRoadMapHidden")
                }
            }
        } else {
            if (v) {
                j.removeClass("sapUiRoadMapHidden")
            } else {
                j.addClass("sapUiRoadMapHidden")
            }
            var x = S.getSubSteps();
            if (x.length > 0 && S.getExpanded()) {
                if (v) {
                    l.removeClass("sapUiRoadMapHidden")
                } else {
                    l.addClass("sapUiRoadMapHidden")
                }
                for (var i = 0; i < x.length; i++) {
                    if (x[i].getVisible()) {
                        var y = jQuery.sap.byId(x[i].getId());
                        if (R.getSelectedStep() == x[i].getId()) {
                            t = true;
                            y.removeClass("sapUiRoadMapSelected");
                            jQuery.sap.byId(x[i].getId() + "-box").removeAttr("aria-checked")
                        }
                        if (v) {
                            y.removeClass("sapUiRoadMapHidden")
                        } else {
                            y.addClass("sapUiRoadMapHidden")
                        }
                    }
                }
            }
        }
        return t
    };
    sap.ui.commons.RoadMapRenderer.setRoadMapWidth = function(R, W) {
        var j = jQuery.sap.byId(R.getId());
        j.attr("style", "width:" + (W ? W : "100%") + ";")
    };
    sap.ui.commons.RoadMapRenderer.scrollToNextStep = function(R, D, E) {
        var P = D;
        if (D == "first" || D == "last") {
            P = q(R, D == "last")
        }
        s(R, P, false, E)
    };
    sap.ui.commons.RoadMapRenderer.addEllipses = function(S) {
        if (!S) {
            return
        }
        var j = jQuery.sap.byId(S.getId() + "-label");
        var O = jQuery.sap.escapeHTML(S.getLabel());
        var t = O + "";
        var C = jQuery("<label class=\"sapUiRoadMapTitle\" style=\"display:none;position:absolute;overflow:visible;font-weight:bold;height:auto\">" + t + "</label>");
        C.width(j.width());
        jQuery(sap.ui.getCore().getStaticAreaRef()).append(C);
        var i = false;
        while (t.length > 0 && C.height() > j.height()) {
            t = t.substr(0, t.length - 1);
            C.html(t + "...");
            i = true
        }
        if (i) {
            var H = C.html();
            H = H.substr(0, H.length - 3);
            H = jQuery.sap.escapeHTML(H);
            H = "<span>" + H + "</span>";
            j.html(H);
            j.attr("title", S.getLabel())
        } else {
            j.attr("title", g(S))
        }
        C.remove()
    };
    sap.ui.commons.RoadMapRenderer.updateStepAria = function(S) {
        if (!sap.ui.getCore().getConfiguration().getAccessibility()) {
            return
        }
        var I = S.getParent() instanceof sap.ui.commons.RoadMap;
        var j = S.getParent()[I ? "getSteps" : "getSubSteps"]();
        for (var i = 0; i < j.length; i++) {
            var P = d(j[i]);
            var l = e(j[i]);
            var t = jQuery.sap.byId(j[i].getId() + "-box");
            t.attr("aria-posinset", P);
            t.attr("aria-setsize", l);
            if (I && j[i].getSubSteps().length > 0) {
                t = jQuery.sap.byId(j[i].getId() + "-expandend-box");
                t.attr("aria-posinset", P);
                t.attr("aria-setsize", l)
            }
        }
    };
    var r = function(i, R, S) {
        var t = S ? "Start" : "End";
        i.write("<div");
        i.writeAttribute("id", R.getId() + "-" + t);
        i.writeAttribute("tabindex", "-1");
        var j = true;
        i.addClass(j ? "sapUiRoadMap" + t + "Scroll" : "sapUiRoadMap" + t + "Fixed");
        i.addClass("sapUiRoadMapDelim");
        i.addClass("sapUiRoadMapContent");
        i.writeClasses();
        i.write("></div>")
    };
    var a = function(j, R, S, A, l, I) {
        j.write("<li");
        if (I) {
            j.writeAttribute("id", I)
        } else {
            j.writeElementData(S)
        }
        var t = h(R, S);
        S.__stepName = t;
        var T = g(S);
        j.addClass("sapUiRoadMapContent");
        j.addClass("sapUiRoadMapStep");
        if (!S.getVisible()) {
            j.addClass("sapUiRoadMapHidden")
        }
        if (S.getEnabled()) {
            if (R.getSelectedStep() == S.getId()) {
                j.addClass("sapUiRoadMapSelected")
            }
        } else {
            j.addClass("sapUiRoadMapDisabled")
        }
        if (A) {
            for (var i = 0; i < A.length; i++) {
                j.addClass(A[i])
            }
        }
        j.writeClasses();
        j.write(">");
        b(j, I ? I : S.getId(), 1);
        j.write("<div");
        j.writeAttribute("id", (I ? I : S.getId()) + "-box");
        j.writeAttribute("tabindex", "-1");
        j.addClass("sapUiRoadMapStepBox");
        j.writeClasses();
        j.writeAttributeEscaped("title", T);
        w(j, R, S, l ? true : false);
        j.write("><span>");
        j.write(t);
        j.write("</span>");
        if (l) {
            l(j, R, S)
        }
        j.write("</div>");
        j.write("<label");
        j.writeAttribute("id", (I ? I : S.getId()) + "-label");
        j.addClass("sapUiRoadMapTitle");
        j.writeAttributeEscaped("title", T);
        j.writeClasses();
        j.write(">");
        var L = S.getLabel();
        if (L) {
            j.writeEscaped(L)
        }
        j.write("</label>");
        b(j, I ? I : S.getId(), 2);
        j.write("</li>")
    };
    var g = function(S) {
        var t = S.getTooltip_AsString();
        if (!t) {
            if (sap.ui.getCore().getConfiguration().getAccessibility()) {
                t = k("RDMP_DEFAULT_STEP_TOOLTIP", [S.__stepName])
            } else {
                t = ""
            }
        }
        return t
    };
    var b = function(i, I, j) {
        i.write("<div");
        i.writeAttribute("id", I + "-add" + j);
        i.addClass("sapUiRoadMapStepAdd" + j);
        i.writeClasses();
        i.write("></div>")
    };
    var w = function(i, R, S, I) {
        if (!sap.ui.getCore().getConfiguration().getAccessibility()) {
            return
        }
        i.writeAttribute("role", "treeitem");
        if (S.getEnabled()) {
            i.writeAttribute("aria-checked", R.getSelectedStep() == S.getId())
        } else {
            i.writeAttribute("aria-disabled", true)
        }
        i.writeAttribute("aria-haspopup", I);
        i.writeAttribute("aria-level", S.getParent() instanceof sap.ui.commons.RoadMap ? 1 : 2);
        i.writeAttribute("aria-posinset", d(S));
        i.writeAttribute("aria-setsize", e(S));
        i.writeAttributeEscaped("aria-label", c(S, S.getLabel()));
        if (!I) {
            return
        }
        i.writeAttribute("aria-expanded", S.getExpanded())
    };
    var c = function(S, l) {
        var i = S.getParent() instanceof sap.ui.commons.RoadMap && S.getSubSteps().length > 0;
        var R = l || "";
        if (S.getEnabled()) {
            R = k(i ? "RDMP_ARIA_EXPANDABLE_STEP" : "RDMP_ARIA_STANDARD_STEP", [R])
        }
        return R
    };
    var d = function(S) {
        var I = S.getParent() instanceof sap.ui.commons.RoadMap;
        var j = S.getParent()[I ? "indexOfStep" : "indexOfSubStep"](S);
        var C = 0;
        var l = S.getParent()[I ? "getSteps" : "getSubSteps"]();
        for (var i = 0; i < j; i++) {
            if (!l[i].getVisible()) {
                C++
            }
        }
        return j + 1 - C
    };
    var e = function(S) {
        var I = S.getParent() instanceof sap.ui.commons.RoadMap;
        var j = S.getParent()[I ? "getSteps" : "getSubSteps"]();
        var C = j.length;
        for (var i = 0; i < j.length; i++) {
            if (!j[i].getVisible()) {
                C--
            }
        }
        return C
    };
    var f = function(j, R, S) {
        var C = function(j, R, v, x, A) {
            j.write("<div");
            j.writeAttribute("id", v + "-ico");
            j.addClass("sapUiRoadMapStepIco");
            if (A) {
                j.addClass(A)
            }
            j.writeClasses();
            j.write("></div>")
        };
        var I = S.getExpanded();
        a(j, R, S, I ? ["sapUiRoadMapExpanded"] : null, function(j, R, S) {
            C(j, R, S.getId(), I ? "roundtripstart.gif" : "roundtrip.gif")
        });
        var l = S.getSubSteps();
        for (var i = 0; i < l.length; i++) {
            var t = ["sapUiRoadMapSubStep"];
            if (!I && l[i].getVisible()) {
                t.push("sapUiRoadMapHidden")
            }
            a(j, R, l[i], t)
        }
        t = ["sapUiRoadMapExpanded", "sapUiRoadMapStepEnd"];
        if (!I) {
            t.push("sapUiRoadMapHidden")
        }
        a(j, R, S, t, function(j, R, S) {
            C(j, R, S.getId() + "-expandend", "roundtripend.gif")
        }, S.getId() + "-expandend")
    };
    var h = function(R, S) {
        var P = S.getParent();
        if (P === R) {
            return P.indexOfStep(S) + 1
        }
        var i = P.indexOfSubStep(S);
        if (i < 26) {
            return String.fromCharCode(97 + i)
        }
        var j = Math.floor(i / 26) - 1;
        var l = i % 26;
        return String.fromCharCode(97 + j, 97 + l)
    };
    var u = function(R) {
        var i = o();
        var S = jQuery.sap.byId(R.getId() + "-steparea");
        var j = p(S);
        var l = jQuery.sap.byId(R.getId() + "-Start");
        l.removeClass("sapUiRoadMapStartScroll").removeClass("sapUiRoadMapStartFixed");
        l.addClass(i * j >= R.iStepWidth ? "sapUiRoadMapStartScroll" : "sapUiRoadMapStartFixed");
        var E = jQuery.sap.byId(R.getId() + "-End");
        E.removeClass("sapUiRoadMapEndScroll").removeClass("sapUiRoadMapEndFixed");
        var t = S.get(0).scrollWidth - i * j - S.width() < R.iStepWidth;
        E.addClass(t ? "sapUiRoadMapEndFixed" : "sapUiRoadMapEndScroll")
    };
    var k = function(K, A) {
        var i = sap.ui.getCore().getLibraryResourceBundle("sap.ui.commons");
        if (i) {
            return i.getText(K, A)
        }
        return K
    };
    var m = function(R) {
        var l = 0;
        var S = R.getSteps();
        for (var i = 0; i < S.length; i++) {
            if (S[i].getVisible()) {
                l++;
                if (S[i].getExpanded()) {
                    l++;
                    var t = S[i].getSubSteps();
                    for (var j = 0; j < t.length; j++) {
                        if (t[j].getVisible()) {
                            l++
                        }
                    }
                }
            }
        }
        return l
    };
    var n = function(S, j) {
        var P = j.position().left;
        if (sap.ui.getCore().getConfiguration().getRTL()) {
            P = S.width() - P - j.outerWidth()
        }
        return P
    };
    var o = function() {
        return sap.ui.getCore().getConfiguration().getRTL() && !! !sap.ui.Device.browser.internet_explorer ? -1 : 1
    };
    var p = function(S) {
        if (sap.ui.getCore().getConfiguration().getRTL() && !! sap.ui.Device.browser.webkit) {
            return (-1) * (S.get(0).scrollWidth - S.scrollLeft() - S.width())
        }
        return S.scrollLeft()
    };
    var q = function(R, l) {
        var S = jQuery.sap.byId(R.getId() + "-steparea").get(0).scrollWidth;
        if (sap.ui.getCore().getConfiguration().getRTL() && !! sap.ui.Device.browser.webkit) {
            return l ? 0 : (-1) * S
        }
        return l ? S : 0
    };
    var s = function(R, N, S, E) {
        var j = jQuery.sap.byId(R.getId() + "-steparea");
        j.stop(false, true);
        if (N == "next") {
            N = j.scrollLeft() + R.iStepWidth * o()
        } else if (N == "prev") {
            N = j.scrollLeft() - R.iStepWidth * o()
        } else if (N == "keep") {
            N = j.scrollLeft()
        } else {
            N = N * o()
        }
        var D = function() {
            u(R);
            if (E) {
                var F = sap.ui.commons.RoadMapRenderer.getFirstVisibleRef(R);
                E(F.attr("id"))
            }
        };
        if (!jQuery.fx.off && !S) {
            j.animate({
                scrollLeft: N
            }, "fast", D)
        } else {
            j.scrollLeft(N);
            D()
        }
    }
}());