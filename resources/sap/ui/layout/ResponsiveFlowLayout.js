﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.ResponsiveFlowLayout");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.core.Control");
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
jQuery.sap.require("sap.ui.layout.ResponsiveFlowLayoutData");
jQuery.sap.require("sap.ui.core.IntervalTrigger");
jQuery.sap.require("sap.ui.core.theming.Parameters");
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
}());