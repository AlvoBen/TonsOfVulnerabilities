﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.layout.AbsoluteLayout");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.commons.layout.AbsoluteLayout", {
    metadata: {
        publicMethods: ["setPositionOfChild", "destroyContent", "indexOfContent", "removeAllContent", "removeContent", "insertContent", "addContent", "getContent"],
        library: "sap.ui.commons",
        properties: {
            "visible": {
                type: "boolean",
                group: "",
                defaultValue: true
            },
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '100%'
            },
            "height": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '100%'
            },
            "verticalScrolling": {
                type: "sap.ui.core.Scrolling",
                group: "Behavior",
                defaultValue: sap.ui.core.Scrolling.Hidden
            },
            "horizontalScrolling": {
                type: "sap.ui.core.Scrolling",
                group: "Behavior",
                defaultValue: sap.ui.core.Scrolling.Hidden
            }
        },
        defaultAggregation: "positions",
        aggregations: {
            "positions": {
                type: "sap.ui.commons.layout.PositionContainer",
                multiple: true,
                singularName: "position"
            }
        }
    }
});
jQuery.sap.require("sap.ui.commons.layout.PositionContainer");
(function() {
    sap.ui.commons.layout.AbsoluteLayout.prototype.setWidth = function(w) {
        return s(this, "width", w, "LYT_SIZE")
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.setHeight = function(h) {
        return s(this, "height", h, "LYT_SIZE")
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.setVerticalScrolling = function(v) {
        return s(this, "verticalScrolling", v, "LYT_SCROLL")
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.setHorizontalScrolling = function(h) {
        return s(this, "horizontalScrolling", h, "LYT_SCROLL")
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.insertPosition = function(p, i) {
        var h = !! this.getDomRef();
        this.insertAggregation("positions", p, i, h);
        if (h && p && p.getControl()) {
            this.contentChanged(p, "CTRL_ADD")
        }
        return this
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.addPosition = function(p) {
        var h = !! this.getDomRef();
        this.addAggregation("positions", p, h);
        if (h && p && p.getControl()) {
            this.contentChanged(p, "CTRL_ADD")
        }
        return this
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.removePosition = function(p) {
        var h = !! this.getDomRef();
        var r = this.removeAggregation("positions", p, h);
        if (r) {
            c([r]);
            this.contentChanged(r, "CTRL_REMOVE")
        }
        return r
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.removeAllPositions = function() {
        c(this.getPositions());
        var h = !! this.getDomRef();
        var r = this.removeAllAggregation("positions", h);
        if (h) {
            this.contentChanged(r, "CTRL_REMOVE_ALL")
        }
        return r
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.destroyPositions = function() {
        c(this.getPositions());
        var h = !! this.getDomRef();
        this.destroyAggregation("positions", h);
        if (h) {
            this.contentChanged(null, "CTRL_REMOVE_ALL")
        }
        return this
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.getContent = function() {
        var C = [];
        var p = this.getPositions();
        for (var i = 0; i < p.length; i++) {
            C.push(p[i].getControl())
        }
        return C
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.addContent = function(C, p) {
        var P = sap.ui.commons.layout.PositionContainer.createPosition(C, p);
        this.addPosition(P);
        return this
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.insertContent = function(C, i, p) {
        var P = sap.ui.commons.layout.PositionContainer.createPosition(C, p);
        this.insertPosition(P, i);
        return this
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.removeContent = function(C) {
        var i = C;
        if (typeof(C) == "string") {
            C = sap.ui.getCore().byId(C)
        }
        if (typeof(C) == "object") {
            i = this.indexOfContent(C)
        }
        if (i >= 0 && i < this.getContent().length) {
            this.removePosition(i);
            return C
        }
        return null
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.removeAllContent = function() {
        var C = this.getContent();
        this.removeAllPositions();
        return C
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.indexOfContent = function(C) {
        var d = this.getContent();
        for (var i = 0; i < d.length; i++) {
            if (C === d[i]) {
                return i
            }
        }
        return -1
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.destroyContent = function() {
        this.destroyPositions();
        return this
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.setPositionOfChild = function(C, p) {
        var i = this.indexOfContent(C);
        if (i >= 0) {
            var P = this.getPositions()[i];
            P.updatePosition(p);
            return true
        }
        return false
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.getPositionOfChild = function(C) {
        var i = this.indexOfContent(C);
        if (i >= 0) {
            var p = this.getPositions()[i];
            return p.getComputedPosition()
        }
        return {}
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.exit = function() {
        c(this.getPositions())
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.doBeforeRendering = function() {
        var p = this.getPositions();
        if (!p || p.length == 0) {
            return
        }
        for (var i = 0; i < p.length; i++) {
            var P = p[i];
            P.reinitializeEventHandlers(true);
            a(P, true)
        }
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.onAfterRendering = function() {
        var p = this.getPositions();
        if (!p || p.length == 0) {
            return
        }
        for (var i = 0; i < p.length; i++) {
            p[i].reinitializeEventHandlers()
        }
    };
    sap.ui.commons.layout.AbsoluteLayout.cleanUpControl = function(C) {
        if (C && C[S]) {
            C.removeDelegate(C[S]);
            C[S] = undefined
        }
    };
    sap.ui.commons.layout.AbsoluteLayout.prototype.contentChanged = function(p, C) {
        switch (C) {
            case "CTRL_POS":
                sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionStyles(p);
                a(p);
                p.reinitializeEventHandlers();
                break;
            case "CTRL_CHANGE":
                a(p, true);
                sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionedControl(p);
                p.reinitializeEventHandlers();
                break;
            case "CTRL_REMOVE":
                sap.ui.commons.layout.AbsoluteLayoutRenderer.removePosition(p);
                p.reinitializeEventHandlers(true);
                break;
            case "CTRL_REMOVE_ALL":
                sap.ui.commons.layout.AbsoluteLayoutRenderer.removeAllPositions(this);
                var P = p;
                if (P) {
                    for (var i = 0; i < P.length; i++) {
                        P[i].reinitializeEventHandlers(true)
                    }
                }
                break;
            case "CTRL_ADD":
                a(p, true);
                sap.ui.commons.layout.AbsoluteLayoutRenderer.insertPosition(this, p);
                p.reinitializeEventHandlers();
                break;
            case "LYT_SCROLL":
                sap.ui.commons.layout.AbsoluteLayoutRenderer.updateLayoutScolling(this);
                break;
            case "LYT_SIZE":
                sap.ui.commons.layout.AbsoluteLayoutRenderer.updateLayoutSize(this);
                break
        }
    };
    var S = "__absolutelayout__delegator";
    var c = function(p) {
        for (var i = 0; i < p.length; i++) {
            var P = p[i];
            var C = P.getControl();
            if (C) {
                sap.ui.commons.layout.AbsoluteLayout.cleanUpControl(C)
            }
        }
    };
    var a = function(p, r) {
        var C = p.getControl();
        if (C) {
            sap.ui.commons.layout.AbsoluteLayout.cleanUpControl(C);
            if (!r) {
                b(C)
            }
            var d = (function(o) {
                return {
                    onAfterRendering: function() {
                        b(o)
                    }
                }
            }(C));
            C[S] = d;
            C.addDelegate(d, true)
        }
    };
    var b = function(C) {
        var A = false;
        if (C.getParent() && C.getParent().getComputedPosition) {
            var p = C.getParent().getComputedPosition();
            if (p.top && p.bottom || p.height) {
                jQuery(C.getDomRef()).height("100%");
                A = true
            }
            if (p.left && p.right || p.width) {
                jQuery(C.getDomRef()).width("100%");
                A = true
            }
            if (A) {
                sap.ui.commons.layout.AbsoluteLayoutRenderer.updatePositionStyles(C.getParent())
            }
        }
        return A
    };
    var s = function(t, p, v, C) {
        var h = !! t.getDomRef();
        t.setProperty(p, v, h);
        if (h) {
            t.contentChanged(null, C)
        }
        return t
    }
}());