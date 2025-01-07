﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.RoadMap");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.commons.RoadMap", {
    metadata: {
        library: "sap.ui.commons",
        properties: {
            "numberOfVisibleSteps": {
                type: "int",
                group: "Misc",
                defaultValue: null
            },
            "firstVisibleStep": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "selectedStep": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '100%'
            },
            "visible": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            }
        },
        defaultAggregation: "steps",
        aggregations: {
            "steps": {
                type: "sap.ui.commons.RoadMapStep",
                multiple: true,
                singularName: "step"
            }
        },
        events: {
            "stepSelected": {},
            "stepExpanded": {}
        }
    }
});
sap.ui.commons.RoadMap.M_EVENTS = {
    'stepSelected': 'stepSelected',
    'stepExpanded': 'stepExpanded'
};
(function() {
    sap.ui.commons.RoadMap.prototype.init = function() {
        this.iStepWidth = -1;
        this.sCurrentFocusedStepRefId = null
    };
    sap.ui.commons.RoadMap.prototype.exit = function() {
        if (this.sResizeListenerId) {
            sap.ui.core.ResizeHandler.deregister(this.sResizeListenerId);
            this.sResizeListenerId = null
        }
    };
    sap.ui.commons.RoadMap.prototype.setNumberOfVisibleSteps = function(n) {
        var i = this.getDomRef() ? true : false;
        this.setProperty("numberOfVisibleSteps", n, i);
        if (i) {
            sap.ui.commons.RoadMapRenderer.updateScrollArea(this, true)
        }
        return this
    };
    sap.ui.commons.RoadMap.prototype.setFirstVisibleStep = function(F) {
        var i = this.getDomRef() ? true : false;
        if (i) {
            if (F) {
                var S = sap.ui.getCore().byId(F);
                if (S && S.getParent() && (S.getParent() === this || S.getParent().getParent() === this) && S.getVisible()) {
                    this.setProperty("firstVisibleStep", F, true);
                    sap.ui.commons.RoadMapRenderer.updateScrollArea(this)
                }
            } else {
                this.setProperty("firstVisibleStep", "", true);
                sap.ui.commons.RoadMapRenderer.updateScrollArea(this)
            }
        } else {
            this.setProperty("firstVisibleStep", F)
        }
        return this
    };
    sap.ui.commons.RoadMap.prototype.setWidth = function(w) {
        var i = this.getDomRef() ? true : false;
        this.setProperty("width", w, i);
        if (i) {
            sap.ui.commons.RoadMapRenderer.setRoadMapWidth(this, w);
            sap.ui.commons.RoadMapRenderer.updateScrollArea(this, true)
        }
        return this
    };
    sap.ui.commons.RoadMap.prototype.setSelectedStep = function(S) {
        var i = this.getDomRef() ? true : false;
        if (i) {
            if (S) {
                var o = sap.ui.getCore().byId(S);
                if (o && o.getParent() && (o.getParent() === this || o.getParent().getParent() === this) && o.getEnabled() && o.getVisible()) {
                    sap.ui.commons.RoadMapRenderer.selectStepWithId(this, S);
                    this.setProperty("selectedStep", S, true)
                }
            } else {
                sap.ui.commons.RoadMapRenderer.selectStepWithId(this, "");
                this.setProperty("selectedStep", "", true)
            }
        } else {
            this.setProperty("selectedStep", S)
        }
        return this
    };
    sap.ui.commons.RoadMap.prototype.onThemeChanged = function(e) {
        this.iStepWidth = -1;
        if (this.getDomRef()) {
            this.invalidate()
        }
    };
    sap.ui.commons.RoadMap.prototype.doBeforeRendering = function() {
        var I = false;
        var b = false;
        var S = this.getSteps();
        for (var i = 0; i < S.length; i++) {
            var o = S[i];
            if (o.getSubSteps().length == 0 || !o.getEnabled()) {
                o.setProperty("expanded", false, true)
            }
            if (!o.getEnabled() && !o.getVisible() && this.getSelectedStep() == o.getId()) {
                this.setProperty("selectedStep", "", true)
            } else if (o.getEnabled() && o.getVisible() && this.getSelectedStep() == o.getId()) {
                I = true
            }
            if (o.getVisible() && this.getFirstVisibleStep() == o.getId()) {
                b = true
            }
            var a = o.getSubSteps();
            for (var j = 0; j < a.length; j++) {
                var c = a[j];
                c.setProperty("expanded", false, true);
                if (!c.getEnabled() && !c.getVisible() && this.getSelectedStep() == c.getId()) {
                    this.setProperty("selectedStep", "", true)
                } else if (c.getEnabled() && c.getVisible() && this.getSelectedStep() == c.getId()) {
                    I = true
                }
                if (c.getVisible() && this.getFirstVisibleStep() == c.getId()) {
                    b = true
                }
            }
        }
        if (!I) {
            this.setProperty("selectedStep", "", true)
        }
        if (!b) {
            this.setProperty("firstVisibleStep", "", true)
        }
        if (this.sResizeListenerId) {
            sap.ui.core.ResizeHandler.deregister(this.sResizeListenerId);
            this.sResizeListenerId = null
        }
    };
    sap.ui.commons.RoadMap.prototype.onAfterRendering = function() {
        var S = this.getSteps();
        if (this.iStepWidth == -1 && S.length > 0) {
            var R = jQuery.sap.byId(S[0].getId());
            this.iStepWidth = R.outerWidth()
        }
        for (var i = 0; i < S.length; i++) {
            var o = S[i];
            sap.ui.commons.RoadMapRenderer.addEllipses(o);
            var a = o.getSubSteps();
            for (var j = 0; j < a.length; j++) {
                sap.ui.commons.RoadMapRenderer.addEllipses(a[j])
            }
        }
        sap.ui.commons.RoadMapRenderer.updateScrollArea(this);
        this.sResizeListenerId = sap.ui.core.ResizeHandler.register(this.getDomRef(), jQuery.proxy(this.onresize, this))
    };
    sap.ui.commons.RoadMap.prototype.onresize = function(e) {
        var d = function() {
            if (this.getDomRef()) {
                sap.ui.commons.RoadMapRenderer.updateScrollArea(this, true);
                r(this, "prev");
                this.sResizeInProgress = null
            }
        };
        if ( !! sap.ui.Device.browser.firefox) {
            d.apply(this, [])
        } else {
            if (!this.sResizeInProgress) {
                this.sResizeInProgress = jQuery.sap.delayedCall(300, this, d)
            }
        }
    };
    sap.ui.commons.RoadMap.prototype.onclick = function(e) {
        h(this, e)
    };
    sap.ui.commons.RoadMap.prototype.onsapselect = function(e) {
        h(this, e)
    };
    sap.ui.commons.RoadMap.prototype.onfocusin = function(e) {
        var t = jQuery(e.target);
        var T = t.attr("id");
        if (T && jQuery.sap.endsWith(T, "-box")) {
            this.sCurrentFocusedStepRefId = T.substring(0, T.length - 4)
        } else if (T && (jQuery.sap.endsWith(T, "-Start") || jQuery.sap.endsWith(T, "-End"))) {} else {
            this.sCurrentFocusedStepRefId = sap.ui.commons.RoadMapRenderer.getFirstVisibleRef(this).attr("id");
            r(this)
        }
        jQuery.sap.byId(this.getId()).attr("tabindex", "-1")
    };
    sap.ui.commons.RoadMap.prototype.onfocusout = function(e) {
        jQuery.sap.byId(this.getId()).attr("tabindex", "0")
    };
    sap.ui.commons.RoadMap.prototype.onsapprevious = function(e) {
        f(e, this, "prev")
    };
    sap.ui.commons.RoadMap.prototype.onsapnext = function(e) {
        f(e, this, "next")
    };
    sap.ui.commons.RoadMap.prototype.onsaphome = function(e) {
        f(e, this, "first")
    };
    sap.ui.commons.RoadMap.prototype.onsapend = function(e) {
        f(e, this, "last")
    };
    var h = function(t, e) {
        e.stopPropagation();
        e.preventDefault();
        var T = jQuery(e.target);
        var a = T.attr("id");
        if (!a) {
            return
        }
        var i = a.lastIndexOf("-expandend");
        if (i != -1) {
            var S = sap.ui.getCore().byId(a.substring(0, i));
            if (S && t.indexOfStep(S) >= 0) {
                S.handleSelect(e, true);
                return
            }
        }
        if (a == t.getId() + "-Start") {
            if (T.hasClass("sapUiRoadMapStartScroll")) {
                s(t, "prev", true)
            } else {
                r(t)
            }
        } else if (a == t.getId() + "-End") {
            if (T.hasClass("sapUiRoadMapEndScroll")) {
                s(t, "next", true)
            } else {
                r(t)
            }
        }
    };
    var s = function(t, d, u) {
        sap.ui.commons.RoadMapRenderer.scrollToNextStep(t, d, function(F) {
            var i = F.lastIndexOf("-expandend");
            if (i != -1) {
                F = F.substring(0, i)
            }
            t.setProperty("firstVisibleStep", F, true);
            if (u) {
                r(t, d)
            }
        })
    };
    var f = function(e, t, d) {
        if (e) {
            e.stopPropagation();
            e.preventDefault()
        }
        if (!t.sCurrentFocusedStepRefId) {
            return
        }
        var F = d + "All";
        var i = false;
        if (d == "first") {
            F = "prevAll";
            i = true
        } else if (d == "last") {
            F = "nextAll";
            i = true
        }
        var c = jQuery.sap.byId(t.sCurrentFocusedStepRefId);
        var j = c[F](":visible");
        var a = jQuery(j.get(i ? j.length - 1 : 0)).attr("id");
        if (a) {
            if (!sap.ui.commons.RoadMapRenderer.isVisibleRef(t, a)) {
                s(t, d)
            }
            jQuery.sap.byId(a + "-box").get(0).focus()
        }
    };
    var r = function(t, d) {
        if (!t.sCurrentFocusedStepRefId) {
            return
        }
        if (d && !sap.ui.commons.RoadMapRenderer.isVisibleRef(t, t.sCurrentFocusedStepRefId)) {
            f(null, t, d)
        } else {
            jQuery.sap.byId(t.sCurrentFocusedStepRefId + "-box").get(0).focus()
        }
    }
}());