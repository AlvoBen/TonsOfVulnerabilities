﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.Grid");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.core.Control");
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
}());