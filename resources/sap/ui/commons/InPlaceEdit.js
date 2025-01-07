﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.InPlaceEdit");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.commons.InPlaceEdit", {
    metadata: {
        publicMethods: ["clearOldText"],
        library: "sap.ui.commons",
        properties: {
            "valueState": {
                type: "sap.ui.core.ValueState",
                group: "Data",
                defaultValue: sap.ui.core.ValueState.None
            },
            "undoEnabled": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "design": {
                type: "sap.ui.commons.TextViewDesign",
                group: "Data",
                defaultValue: sap.ui.commons.TextViewDesign.Standard
            }
        },
        aggregations: {
            "content": {
                type: "sap.ui.core.Control",
                multiple: false
            }
        },
        events: {
            "change": {},
            "liveChange": {}
        }
    }
});
sap.ui.commons.InPlaceEdit.M_EVENTS = {
    'change': 'change',
    'liveChange': 'liveChange'
};
jQuery.sap.require("sap.ui.commons.TextView");
jQuery.sap.require("sap.ui.commons.TextField");
jQuery.sap.require("sap.ui.core.theming.Parameters");
jQuery.sap.require("sap.ui.core.ValueStateSupport");
(function() {
    sap.ui.commons.InPlaceEdit.prototype.init = function() {
        this._bEditMode = false
    };
    sap.ui.commons.InPlaceEdit.prototype.exit = function() {
        this._bEditMode = undefined;
        this._oDisplayControl = undefined;
        this._oEditControl = undefined;
        this._sOldText = undefined;
        this._sOldTextAvailable = undefined;
        this._bUseEditButton = undefined;
        this._iHeight = undefined;
        if (this._oTextView) {
            this._oTextView.destroy();
            delete this._oTextView
        }
        if (this._oTextField) {
            this._oTextField.destroy();
            delete this._oTextField
        }
        if (this._oUndoButton) {
            this._oUndoButton.destroy();
            delete this._oUndoButton
        }
        if (this._oEditButton) {
            this._oEditButton.destroy();
            delete this._oEditButton
        }
        var C = this.getContent();
        if (C) {
            C.detachEvent("_change", n, this);
            if (C instanceof sap.ui.commons.TextField) {
                C.detachEvent("change", l, this);
                C.detachEvent("liveChange", m, this)
            }
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onBeforeRendering = function() {
        var t = this;
        u(t);
        b(t);
        var T = this.getTooltip();
        if (T instanceof sap.ui.core.TooltipBase) {
            if (this._bEditMode) {
                T._currentControl = this._oEditControl
            } else {
                T._currentControl = this._oDisplayControl
            }
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onAfterRendering = function() {
        if (!this._bEditMode && this.getEditable() && this._oTextView && jQuery.sap.domById(this._oTextView.getId())) {
            jQuery.sap.byId(this._oTextView.getId()).attr("tabindex", "0")
        }
        var C = jQuery.sap.byId(this.getId());
        if (this._bEditMode) {
            jQuery.sap.byId(this._oEditControl.getId()).css("width", "100%");
            if (this._iHeight > 0) {
                var o = C.height();
                var D = this._iHeight - o;
                var M = C.outerHeight(true) - C.outerHeight(false);
                D = D + M;
                var p = Math.floor(D / 2);
                var q = D - p;
                C.css("margin-top", p + "px").css("margin-bottom", q + "px")
            }
        } else if (this._oDisplayControl.getMetadata().getName() == "sap.ui.commons.Link") {
            jQuery.sap.byId(this._oDisplayControl.getId()).css("width", "auto").css("max-width", "100%")
        } else {
            var $ = jQuery.sap.byId(this._oDisplayControl.getId());
            $.css("width", "100%");
            if (!this._iHeight && this._iHeight != 0 && this.getDesign() != sap.ui.commons.TextViewDesign.Standard) {
                var I = $.outerHeight(true);
                var o = C.innerHeight();
                if (o < I) {
                    var O = C.outerHeight() - C.innerHeight();
                    this._iHeight = I + O
                } else {
                    this._iHeight = 0
                }
            }
            if (this._iHeight > 0) {
                C.css("height", this._iHeight + "px")
            }
        }
        if (this._sOldTextAvailable && this._oUndoButton && jQuery.sap.domById(this._oUndoButton.getId())) {
            jQuery.sap.byId(this._oUndoButton.getId()).attr("tabindex", "-1")
        }
        if (this._oEditButton && jQuery.sap.domById(this._oEditButton.getId())) {
            jQuery.sap.byId(this._oEditButton.getId()).attr("tabindex", "-1")
        }
        if (this._delayedCallId) {
            jQuery.sap.clearDelayedCall(this._delayedCallId);
            this._delayedCallId = null
        }
        if (this.getValueState() == sap.ui.core.ValueState.Success) {
            this._delayedCallId = jQuery.sap.delayedCall(3000, this, "removeValidVisualization")
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.removeValidVisualization = function() {
        var D = jQuery.sap.byId(this.getId());
        if (D) {
            D.removeClass("sapUiIpeSucc")
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.clearOldText = function() {
        if (!this.getUndoEnabled()) {
            return
        }
        if (this._bEditMode) {
            this._sOldText = this._oEditControl.getValue();
            this._sOldTextAvailable = true
        } else {
            this._sOldText = undefined;
            this._sOldTextAvailable = false
        }
        this.rerender()
    };
    sap.ui.commons.InPlaceEdit.prototype.getRequired = function() {
        if (this.getContent() && this.getContent().getRequired) {
            return this.getContent().getRequired()
        } else {
            return false
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.getEditable = function() {
        var C = this.getContent();
        if ((C.getEditable && !C.getEditable()) || (C.getEnabled && !C.getEnabled())) {
            return false
        } else {
            return true
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onsapescape = function(E) {
        if (this.getUndoEnabled()) {
            if ( !! !sap.ui.Device.browser.firefox) {
                var t = this;
                d(t)
            } else {
                this._bEsc = true
            }
            if (jQuery.sap.byId(this.getId()).hasClass("sapUiIpeUndo")) {
                E.stopPropagation()
            }
            this._oEditControl._bEsc = undefined;
            this._oEditControl._sValue = undefined
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onkeypress = function(E) {
        if (this._bEsc) {
            var t = this;
            this._bEsc = undefined;
            d(t)
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onkeydown = function(E) {
        if (E.keyCode == jQuery.sap.KeyCodes.F2 && !this._bEditMode) {
            var t = this;
            s(t);
            jQuery.sap.byId(this.getId()).addClass("sapUiIpeFocus")
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onfocusin = function(E) {
        if (!this._bEditMode) {
            if (!this._bUseEditButton && E.target.id != this.getId() + "--X") {
                var t = this;
                s(t)
            }
            jQuery.sap.byId(this.getId()).addClass("sapUiIpeFocus")
        } else if (this._focusDelay) {
            jQuery.sap.clearDelayedCall(this._focusDelay);
            this._focusDelay = null
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.ontap = function(E) {
        if (sap.ui.Device.os.name == "iOS") {
            this.onfocusin(E)
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onfocusout = function(E) {
        if (this._focusDelay) {
            jQuery.sap.clearDelayedCall(this._focusDelay);
            this._focusDelay = null
        }
        this._focusDelay = jQuery.sap.delayedCall(200, this, "_handleFocusOut", arguments)
    };
    sap.ui.commons.InPlaceEdit.prototype._handleFocusOut = function(E) {
        var F = document.activeElement;
        if (!jQuery.sap.containsOrEquals(this.getDomRef(), F)) {
            if (!this._bEditMode) {
                jQuery.sap.byId(this.getId()).removeClass("sapUiIpeFocus")
            }
            var t = this;
            a(t)
        }
        this._focusDelay = undefined
    };
    sap.ui.commons.InPlaceEdit.prototype.setContent = function(C) {
        var o = this.getContent();
        if (o) {
            o.detachEvent("_change", n, this);
            if (o instanceof sap.ui.commons.TextField) {
                o.detachEvent("change", l, this);
                o.detachEvent("liveChange", m, this);
                o._propagateEsc = undefined
            }
        }
        this._sOldText = undefined;
        this._sOldTextAvailable = false;
        this._oDisplayControl = undefined;
        this._oEditControl = undefined;
        this.setAggregation("content", C);
        if (C) {
            C.attachEvent("_change", n, this);
            if (C instanceof sap.ui.commons.TextField) {
                C.attachEvent("change", l, this);
                C.attachEvent("liveChange", m, this);
                C._propagateEsc = true
            }
        }
        var t = this;
        u(t)
    };
    sap.ui.commons.InPlaceEdit.prototype.setValueState = function(v) {
        var C = this.getContent();
        if (C && C.setValueState) {
            C.setValueState(v)
        } else if (this._oEditControl && this._oEditControl.setValueState) {
            this._oEditControl.setValueState(v);
            n.call(this)
        } else {
            this.setProperty("valueState", v)
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.getValueState = function() {
        var C = this.getContent();
        if (C && C.getValueState) {
            return C.getValueState()
        } else if (this._oEditControl && this._oEditControl.getValueState) {
            return this._oEditControl.getValueState()
        } else {
            return this.getProperty("valueState")
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.setTooltip = function(t) {
        var C = this.getContent();
        if (C) {
            C.setTooltip(t)
        } else {
            this._refreshTooltipBaseDelegate(t);
            this.setAggregation("tooltip", t)
        }
        return this
    };
    sap.ui.commons.InPlaceEdit.prototype.getTooltip = function() {
        var C = this.getContent();
        if (C) {
            return C.getTooltip()
        } else {
            return this.getAggregation("tooltip")
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.setDesign = function(D) {
        this.setProperty("design", D);
        this._iHeight = undefined
    };
    sap.ui.commons.InPlaceEdit.prototype.clone = function() {
        var C = this.getContent();
        if (C) {
            C.detachEvent("_change", n, this);
            if (C instanceof sap.ui.commons.TextField) {
                C.detachEvent("change", l, this);
                C.detachEvent("liveChange", m, this)
            }
        }
        var o = sap.ui.core.Control.prototype.clone.apply(this, arguments);
        if (C) {
            C.attachEvent("_change", n, this);
            if (C instanceof sap.ui.commons.TextField) {
                C.attachEvent("change", l, this);
                C.attachEvent("liveChange", m, this)
            }
        }
        return o
    };
    sap.ui.commons.InPlaceEdit.prototype.getFocusDomRef = function() {
        if (!this.getDomRef()) {
            return
        }
        if (this._bEditMode) {
            return this._oEditControl.getFocusDomRef()
        } else {
            return this._oDisplayControl.getFocusDomRef()
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.getIdForLabel = function() {
        if (this._oDisplayControl && this._oDisplayControl.getMetadata().getName() == "sap.ui.commons.Link") {
            return this._oDisplayControl.getId()
        } else if (this._oEditControl) {
            return this._oEditControl.getId()
        } else {
            return this.getId()
        }
    };
    sap.ui.commons.InPlaceEdit.prototype.onThemeChanged = function(E) {
        var t = this;
        i(t);
        f(t);
        this._iHeight = undefined;
        if (this.getDomRef() && !this._bEditMode) {
            this.rerender()
        }
    };
    var c = {
        onAfterRendering: function() {
            this.onAfterRendering()
        }
    };

    function u(I) {
        var C = I.getContent();
        if (!C) {
            return
        }
        var t = C.getTooltip();
        switch (C.getMetadata().getName()) {
            case "sap.ui.commons.TextField":
            case "sap.ui.commons.ComboBox":
            case "sap.ui.commons.DropdownBox":
                if (!I._oTextView) {
                    I._oTextView = new sap.ui.commons.TextView(I.getId() + "--TV", {
                        wrapping: false
                    });
                    I._oTextView.setParent(I);
                    I._oTextView.removeDelegate(c);
                    I._oTextView.addDelegate(c, I);
                    I._oTextView.getTooltip = function() {
                        return this.getParent().getTooltip()
                    }
                }
                I._oTextView.setText(C.getValue());
                I._oTextView.setEnabled(C.getEnabled());
                I._oTextView.setTextDirection(C.getTextDirection());
                I._oTextView.setVisible(C.getVisible());
                I._oTextView.setWidth("100%");
                I._oTextView.setTextAlign(C.getTextAlign());
                I._oTextView.setDesign(I.getDesign());
                I._oTextView.setHelpId(C.getHelpId());
                I._oTextView.setAccessibleRole(C.getAccessibleRole());
                if (I._oTextView._oTooltip && I._oTextView._oTooltip != t) {
                    I._oTextView.removeDelegate(I._oTextView._oTooltip);
                    I._oTextView._oTooltip = undefined
                }
                if (t instanceof sap.ui.core.TooltipBase) {
                    if (!I._oTextView._oTooltip || I._oTextView._oTooltip != t) {
                        I._oTextView.addDelegate(t);
                        I._oTextView._oTooltip = t
                    }
                }
                I._oDisplayControl = I._oTextView;
                I._oEditControl = C;
                I._bUseEditButton = false;
                break;
            case "sap.ui.commons.Link":
                I._oDisplayControl = C;
                I._oDisplayControl.removeDelegate(c);
                I._oDisplayControl.addDelegate(c, I);
                if (I._oTextField) {
                    I._oTextField.setValue(C.getText());
                    I._oTextField.setWidth("100%");
                    I._oEditControl = I._oTextField;
                    if (I._oTextField._oTooltip && I._oTextField._oTooltip != t) {
                        I._oTextField.removeDelegate(I._oTextField._oTooltip);
                        I._oTextField._oTooltip = undefined
                    }
                    if (t instanceof sap.ui.core.TooltipBase) {
                        if (!I._oTextField._oTooltip || I._oTextField._oTooltip != t) {
                            I._oTextField.addDelegate(t);
                            I._oTextField._oTooltip = t
                        }
                    }
                }
                e(I);
                I._bUseEditButton = true;
                break;
            default:
                throw new Error("Control not supported for InPlaceEdit");
                break
        }
    };

    function s(I) {
        if (!I._bEditMode && I.getEditable()) {
            if (!I._oEditControl && I.getContent().getMetadata().getName() == "sap.ui.commons.Link") {
                var v = I.getValueState();
                I._oTextField = new sap.ui.commons.TextField(I.getId() + "--input", {
                    valueState: v
                });
                I._oTextField.setParent(I);
                I._oTextField.attachEvent('change', k, I);
                I._oTextField.attachEvent('liveChange', m, I);
                I._oTextField._propagateEsc = true;
                I._oTextField.getTooltip = function() {
                    return this.getParent().getTooltip()
                }
            }
            if (!I._sOldTextAvailable && I.getUndoEnabled()) {
                I._sOldText = g(I);
                I._sOldTextAvailable = true
            }
            I._bEditMode = true;
            I.rerender();
            I._oEditControl.focus()
        }
    };

    function a(I) {
        if (I._bEditMode && I.getEditable()) {
            I._bEditMode = false;
            if (I._sOldText == g(I)) {
                I._sOldText = undefined;
                I._sOldTextAvailable = false
            }
            I.rerender()
        }
    };

    function g(I) {
        var C = I.getContent();
        if (!C) {
            return
        }
        if (C.getValue) {
            return C.getValue()
        } else if (C.getText) {
            return C.getText()
        }
    };

    function b(I) {
        if (!I._oUndoButton && I.getUndoEnabled()) {
            I._oUndoButton = new sap.ui.commons.Button(I.getId() + "--X", {
                lite: true
            }).setParent(I);
            i(I);
            I._oUndoButton.attachEvent('press', h, I)
        }
        if (I._oUndoButton) {
            I._oUndoButton.setEnabled(I.getEditable())
        }
    };

    function i(I) {
        if (I._oUndoButton) {
            var o = sap.ui.core.theming.Parameters.get('sapUiIpeUndoImageURL');
            var p = sap.ui.core.theming.Parameters.get('sapUiIpeUndoImageDownURL');
            var t = "X";
            if (o) {
                o = jQuery.sap.getModulePath("sap.ui.commons", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + o;
                t = ""
            }
            if (p) {
                p = jQuery.sap.getModulePath("sap.ui.commons", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + p
            }
            I._oUndoButton.setIcon(o);
            I._oUndoButton.setIconHovered(p);
            I._oUndoButton.setText(t)
        }
    };

    function h(E) {
        var t = this;
        d(t);
        if (this._bEditMode) {
            this._oEditControl.focus();
            jQuery.sap.byId(this.getId()).removeClass("sapUiIpeUndo")
        }
    };

    function d(I) {
        if (I.getUndoEnabled() && I._sOldTextAvailable) {
            var C = I.getContent();
            if (!C) {
                return
            }
            if (C.setValue) {
                C.setValue(I._sOldText)
            } else if (C.setText) {
                C.setText(I._sOldText)
            }
            if (I._bEditMode) {
                I._oEditControl.setValue(I._sOldText)
            }
            if (C.fireChange) {
                C.fireChange({
                    newValue: I._sOldText
                })
            } else {
                I.fireChange({
                    newValue: I._sOldText
                })
            }
            if (!I._bEditMode) {
                I._sOldText = undefined;
                I._sOldTextAvailable = false
            }
        }
    };

    function e(I) {
        if (!I._oEditButton) {
            I._oEditButton = new sap.ui.commons.Button(I.getId() + "--Edit", {
                lite: true
            }).setParent(I);
            I._oEditButton.addStyleClass("sapUiIpeEBtn");
            f(I);
            I._oEditButton.attachEvent('press', j, I)
        }
    };

    function f(I) {
        if (I._oEditButton) {
            var o = sap.ui.core.theming.Parameters.get('sapUiIpeEditImageURL');
            var p = sap.ui.core.theming.Parameters.get('sapUiIpeEditImageDownURL');
            var t = "✎";
            if (o) {
                o = jQuery.sap.getModulePath("sap.ui.commons", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + o;
                t = ""
            }
            if (p) {
                p = jQuery.sap.getModulePath("sap.ui.commons", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + p
            }
            I._oEditButton.setIcon(o);
            I._oEditButton.setIconHovered(p);
            I._oEditButton.setText(t)
        }
    };

    function j(E) {
        var t = this;
        s(t);
        jQuery.sap.byId(this.getId()).addClass("sapUiIpeFocus")
    };

    function k(E) {
        var C = this.getContent();
        if (C.setText) {
            var N = E.getParameter("newValue");
            C.setText(N);
            this.fireChange({
                newValue: N
            })
        }
    };

    function l(E) {
        if (this._sOldText != E.getParameter("newValue") && this.getUndoEnabled()) {
            jQuery.sap.byId(this.getId()).addClass("sapUiIpeUndo")
        } else {
            jQuery.sap.byId(this.getId()).removeClass("sapUiIpeUndo")
        }
        this.fireChange(E.getParameters())
    };

    function m(E) {
        if (this._sOldText != E.getParameter("liveValue") && this.getUndoEnabled()) {
            jQuery.sap.byId(this.getId()).addClass("sapUiIpeUndo")
        } else {
            jQuery.sap.byId(this.getId()).removeClass("sapUiIpeUndo")
        }
        this.fireLiveChange({
            liveValue: E.getParameter("liveValue")
        })
    };

    function n() {
        if (!this._bEditMode) {
            this.invalidate()
        } else {
            switch (this.getValueState()) {
                case (sap.ui.core.ValueState.Error):
                    if (!jQuery.sap.byId(this.getId()).hasClass('sapUiIpeErr')) {
                        jQuery.sap.byId(this.getId()).addClass('sapUiIpeErr');
                        jQuery.sap.byId(this.getId()).removeClass('sapUiIpeWarn');
                        jQuery.sap.byId(this.getId()).removeClass('sapUiIpeSucc')
                    }
                    break;
                case (sap.ui.core.ValueState.Success):
                    if (!jQuery.sap.byId(this.getId()).hasClass('sapUiIpeSucc')) {
                        jQuery.sap.byId(this.getId()).addClass('sapUiIpeSucc');
                        jQuery.sap.byId(this.getId()).removeClass('sapUiIpeErr');
                        jQuery.sap.byId(this.getId()).removeClass('sapUiIpeWarn')
                    }
                    break;
                case (sap.ui.core.ValueState.Warning):
                    if (!jQuery.sap.byId(this.getId()).hasClass('sapUiIpeWarn')) {
                        jQuery.sap.byId(this.getId()).addClass('sapUiIpeWarn');
                        jQuery.sap.byId(this.getId()).removeClass('sapUiIpeErr');
                        jQuery.sap.byId(this.getId()).removeClass('sapUiIpeSucc')
                    }
                    break;
                default:
                    jQuery.sap.byId(this.getId()).removeClass('sapUiIpeWarn');
                    jQuery.sap.byId(this.getId()).removeClass('sapUiIpeErr');
                    jQuery.sap.byId(this.getId()).removeClass('sapUiIpeSucc');
                    break
            }
        }
    }
}());