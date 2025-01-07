﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.RoadMapStep");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Element");
sap.ui.core.Element.extend("sap.ui.commons.RoadMapStep", {
    metadata: {
        library: "sap.ui.commons",
        properties: {
            "label": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "enabled": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "expanded": {
                type: "boolean",
                group: "Misc",
                defaultValue: false,
                deprecated: true
            },
            "visible": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            }
        },
        defaultAggregation: "subSteps",
        aggregations: {
            "subSteps": {
                type: "sap.ui.commons.RoadMapStep",
                multiple: true,
                singularName: "subStep",
                deprecated: true
            }
        }
    }
});
(function() {
    sap.ui.commons.RoadMapStep.prototype.setLabel = function(l) {
        s(this, "label", l, function() {
            sap.ui.commons.RoadMapRenderer.setStepLabel(this, l);
            this.setProperty("label", l, true);
            sap.ui.commons.RoadMapRenderer.addEllipses(this);
            return true
        });
        return this
    };
    sap.ui.commons.RoadMapStep.prototype.setEnabled = function(e) {
        var o = this.getEnabled();
        if ((e && o) || (!e && !o)) {
            return this
        }
        s(this, "enabled", e, function() {
            var r = g(this);
            var w = sap.ui.commons.RoadMapRenderer.setStepEnabled(r, this, e);
            if (w) {
                r.setProperty("selectedStep", "", true)
            }
            if (!e) {
                this.setExpanded(false)
            }
            return false
        });
        return this
    };
    sap.ui.commons.RoadMapStep.prototype.setExpanded = function(e) {
        var o = this.getExpanded();
        if ((e && o) || (!e && !o)) {
            return this
        }
        s(this, "expanded", e, function() {
            if (i(this) || this.getSubSteps().length == 0 || !this.getEnabled() || !e) {
                this.setProperty("expanded", false, true);
                if (!i(this) && this.getSubSteps().length > 0 && this.getEnabled()) {
                    sap.ui.commons.RoadMapRenderer.selectStep(g(this), this, false, true, null, true)
                }
            } else {
                this.setProperty("expanded", true, true);
                sap.ui.commons.RoadMapRenderer.selectStep(g(this), this, false, true, null, true)
            }
            return true
        });
        return this
    };
    sap.ui.commons.RoadMapStep.prototype.setVisible = function(v) {
        var o = this.getVisible();
        if ((v && o) || (!v && !o)) {
            return this
        }
        s(this, "visible", v, function() {
            var r = g(this);
            var w = sap.ui.commons.RoadMapRenderer.setStepVisible(r, this, i(this), v);
            if (w) {
                r.setProperty("selectedStep", "", true)
            }
            this.setProperty("visible", v, true);
            sap.ui.commons.RoadMapRenderer.updateStepArea(r);
            sap.ui.commons.RoadMapRenderer.updateStepAria(this);
            return true
        });
        return this
    };
    sap.ui.commons.RoadMapStep.prototype.getFocusDomRef = function() {
        return jQuery.sap.byId(this.getFocusInfo().id).get(0) || null
    };
    sap.ui.commons.RoadMapStep.prototype.getFocusInfo = function() {
        return {
            id: this.getId() + "-box"
        }
    };
    sap.ui.commons.RoadMapStep.prototype.onclick = function(e) {
        this.handleSelect(e)
    };
    sap.ui.commons.RoadMapStep.prototype.onsapselect = function(e) {
        this.handleSelect(e)
    };
    sap.ui.commons.RoadMapStep.prototype.handleSelect = function(e, I) {
        e.stopPropagation();
        e.preventDefault();
        if (!I && !jQuery.sap.containsOrEquals(this.getDomRef(), e.target)) {
            return
        }
        if (this.getEnabled()) {
            var r = g(this);
            var S = this;
            sap.ui.commons.RoadMapRenderer.selectStep(r, this, i(this), false, function(t) {
                var w = r.getSelectedStep() == S.getId();
                r.setProperty("selectedStep", S.getId(), true);
                S.focus();
                if (t != "selected") {
                    S.setProperty("expanded", t == "expanded", true);
                    r.fireStepExpanded({
                        stepId: S.getId()
                    })
                }
                if (!w) {
                    r.fireStepSelected({
                        stepId: S.getId()
                    })
                }
            })
        } else {
            this.focus()
        }
    };
    var g = function(t) {
        var r = t.getParent();
        if (i(t)) {
            r = r.getParent()
        }
        return r
    };
    var i = function(t) {
        return !(t.getParent() instanceof sap.ui.commons.RoadMap)
    };
    var s = function(t, n, v, d) {
        if (!t.getDomRef()) {
            t.setProperty(n, v);
            return
        }
        var S = d.apply(t, []);
        if (!S) {
            t.setProperty(n, v, true)
        }
    }
}());