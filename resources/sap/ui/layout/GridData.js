﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.GridData");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.core.LayoutData");
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
}());