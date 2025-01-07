﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.form.ResponsiveGridLayout");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.layout.form.FormLayout");
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
jQuery.sap.require("sap.ui.layout.Grid");
jQuery.sap.require("sap.ui.layout.GridData");
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
}());