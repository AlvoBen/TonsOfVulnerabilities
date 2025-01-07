﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.SearchField");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.commons.SearchField", {
    metadata: {
        interfaces: ["sap.ui.commons.ToolbarItem"],
        publicMethods: ["clearHistory", "suggest"],
        library: "sap.ui.commons",
        properties: {
            "enableListSuggest": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "showListExpander": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "enableClear": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "showExternalButton": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "enableCache": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "enableFilterMode": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "value": {
                type: "string",
                group: "Data",
                defaultValue: ''
            },
            "enabled": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "editable": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "visible": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: null
            },
            "maxLength": {
                type: "int",
                group: "Behavior",
                defaultValue: 0
            },
            "textAlign": {
                type: "sap.ui.core.TextAlign",
                group: "Appearance",
                defaultValue: sap.ui.core.TextAlign.Begin
            },
            "visibleItemCount": {
                type: "int",
                group: "Behavior",
                defaultValue: 20
            },
            "startSuggestion": {
                type: "int",
                group: "Behavior",
                defaultValue: 3
            },
            "maxSuggestionItems": {
                type: "int",
                group: "Behavior",
                defaultValue: 10
            },
            "maxHistoryItems": {
                type: "int",
                group: "Behavior",
                defaultValue: 0
            }
        },
        aggregations: {
            "searchProvider": {
                type: "sap.ui.core.search.SearchProvider",
                multiple: false
            }
        },
        associations: {
            "ariaDescribedBy": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "ariaDescribedBy"
            },
            "ariaLabelledBy": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "ariaLabelledBy"
            }
        },
        events: {
            "search": {},
            "suggest": {}
        }
    }
});
sap.ui.commons.SearchField.M_EVENTS = {
    'search': 'search',
    'suggest': 'suggest'
};
jQuery.sap.require("sap.ui.commons.ComboBox");
jQuery.sap.require("sap.ui.commons.ComboBoxRenderer");
jQuery.sap.require("sap.ui.commons.TextField");
jQuery.sap.require("sap.ui.commons.TextFieldRenderer");
jQuery.sap.require("sap.ui.commons.ListBox");
jQuery.sap.require("sap.ui.core.Renderer");
jQuery.sap.require("sap.ui.core.History");
(function() {
    var _ = 20;
    sap.ui.commons.SearchField.prototype.init = function() {
        c(this, this.getEnableListSuggest());
        this._oHistory = new sap.ui.core.History(this.getId())
    };
    sap.ui.commons.SearchField.prototype.exit = function() {
        if (this._ctrl) {
            this._ctrl.destroy()
        }
        if (this._lb) {
            this._lb.destroy()
        }
        if (this._btn) {
            this._btn.destroy()
        }
        this._ctrl = null;
        this._lb = null;
        this._btn = null;
        this._oHistory = null
    };
    sap.ui.commons.SearchField.prototype.onThemeChanged = function(E) {
        if (this.getDomRef()) {
            this.invalidate()
        }
    };
    sap.ui.commons.SearchField.prototype.onAfterRendering = function() {
        if (this.getShowExternalButton()) {
            var B = jQuery.sap.byId(this._btn.getId()).outerWidth(true);
            jQuery.sap.byId(this._ctrl.getId()).css(sap.ui.getCore().getConfiguration().getRTL() ? "left" : "right", B + "px")
        }
    };
    sap.ui.commons.SearchField.prototype.getFocusDomRef = function() {
        return this._ctrl.getFocusDomRef()
    };
    sap.ui.commons.SearchField.prototype.getIdForLabel = function() {
        return this._ctrl.getId() + '-input'
    };
    sap.ui.commons.SearchField.prototype.onpaste = function(E) {
        var t = this;
        setTimeout(function() {
            t._ctrl._triggerValueHelp = true;
            t._ctrl.onkeyup()
        }, 0)
    };
    sap.ui.commons.SearchField.prototype.oncut = sap.ui.commons.SearchField.prototype.onpaste;
    sap.ui.commons.SearchField.prototype.fireSearch = function(A) {
        var v = jQuery(this._ctrl.getInputDomRef()).val();
        if ((!v && !this.getEnableFilterMode()) || !this.getEditable() || !this.getEnabled()) {
            return this
        }
        if (!A) {
            A = {}
        }
        this.setValue(v);
        if (!A.noFocus) {
            v = this.getValue();
            this.focus();
            if (v && (this.getMaxHistoryItems() > 0)) {
                this._oHistory.add(v)
            }
            this.fireEvent("search", {
                query: v
            })
        }
        return this
    };
    sap.ui.commons.SearchField.prototype.hasListExpander = function() {
        return d() ? false : this.getShowListExpander()
    };
    sap.ui.commons.SearchField.prototype.clearHistory = function() {
        this._oHistory.clear()
    };
    sap.ui.commons.SearchField.prototype.suggest = function(s, S) {
        if (!this.getEnableListSuggest() || !s || !S) {
            return
        }
        this._ctrl.updateSuggestions(s, S)
    };
    sap.ui.commons.SearchField.prototype.setEnableListSuggest = function(E) {
        if ((this.getEnableListSuggest() && E) || (!this.getEnableListSuggest() && !E)) {
            return
        }
        c(this, E);
        this.setProperty("enableListSuggest", E);
        return this
    };
    sap.ui.commons.SearchField.prototype.getValue = function() {
        return b(this, "Value")
    };
    sap.ui.commons.SearchField.prototype.setValue = function(v) {
        return a(this, "Value", v, !! this.getDomRef(), true)
    };
    sap.ui.commons.SearchField.prototype.setEnableCache = function(E) {
        return this.setProperty("enableCache", E, true)
    };
    sap.ui.commons.SearchField.prototype.getEnabled = function() {
        return b(this, "Enabled")
    };
    sap.ui.commons.SearchField.prototype.setEnabled = function(E) {
        if (this._btn) {
            this._btn.setEnabled(E && this.getEditable())
        }
        return a(this, "Enabled", E, false, true)
    };
    sap.ui.commons.SearchField.prototype.getEditable = function() {
        return b(this, "Editable")
    };
    sap.ui.commons.SearchField.prototype.setEditable = function(E) {
        if (this._btn) {
            this._btn.setEnabled(E && this.getEnabled())
        }
        return a(this, "Editable", E, false, true)
    };
    sap.ui.commons.SearchField.prototype.getMaxLength = function() {
        return b(this, "MaxLength")
    };
    sap.ui.commons.SearchField.prototype.setMaxLength = function(m) {
        return a(this, "MaxLength", m, false, true)
    };
    sap.ui.commons.SearchField.prototype.getTextAlign = function() {
        return b(this, "TextAlign")
    };
    sap.ui.commons.SearchField.prototype.setTextAlign = function(t) {
        return a(this, "TextAlign", t, false, true)
    };
    sap.ui.commons.SearchField.prototype.getTooltip = function() {
        return b(this, "Tooltip")
    };
    sap.ui.commons.SearchField.prototype.setTooltip = function(t) {
        return a(this, "Tooltip", t, true, false)
    };
    sap.ui.commons.SearchField.prototype.getVisibleItemCount = function() {
        return b(this, "MaxPopupItems")
    };
    sap.ui.commons.SearchField.prototype.setVisibleItemCount = function(v) {
        return a(this, "MaxPopupItems", v, false, true)
    };
    sap.ui.commons.SearchField.prototype.setShowExternalButton = function(s) {
        if (!this._btn) {
            jQuery.sap.require("sap.ui.commons.Button");
            var t = this;
            this._btn = new sap.ui.commons.Button(this.getId() + "-btn", {
                text: g("SEARCHFIELD_BUTTONTEXT"),
                enabled: this.getEditable() && this.getEnabled(),
                press: function() {
                    t.fireSearch()
                }
            });
            this._btn.setParent(this)
        }
        this.setProperty("showExternalButton", s);
        return this
    };
    sap.ui.commons.SearchField.prototype.getAriaDescribedBy = function() {
        return this._ctrl.getAriaDescribedBy()
    };
    sap.ui.commons.SearchField.prototype.getAriaLabelledBy = function() {
        return this._ctrl.getAriaLabelledBy()
    };
    sap.ui.commons.SearchField.prototype.removeAllAriaDescribedBy = function() {
        return this._ctrl.removeAllAriaDescribedBy()
    };
    sap.ui.commons.SearchField.prototype.removeAllAriaLabelledBy = function() {
        return this._ctrl.removeAllAriaLabelledBy()
    };
    sap.ui.commons.SearchField.prototype.removeAriaDescribedBy = function(v) {
        return this._ctrl.removeAriaDescribedBy(v)
    };
    sap.ui.commons.SearchField.prototype.removeAriaLabelledBy = function(v) {
        return this._ctrl.removeAriaLabelledBy(v)
    };
    sap.ui.commons.SearchField.prototype.addAriaDescribedBy = function(v) {
        this._ctrl.addAriaDescribedBy(v);
        return this
    };
    sap.ui.commons.SearchField.prototype.addAriaLabelledBy = function(v) {
        this._ctrl.addAriaLabelledBy(v);
        return this
    };
    var a = function(t, m, v, s, u) {
        var o = b(t, m);
        t._ctrl["set" + m](v);
        if (!s) {
            t.invalidate()
        }
        if (u) {
            t.updateModelProperty(m.toLowerCase(), v, o)
        }
        return t
    };
    var b = function(t, G) {
        return t._ctrl["get" + G]()
    };
    var c = function(t, E) {
        if (!t._lb) {
            t._lb = new sap.ui.commons.ListBox(t.getId() + "-lb")
        }
        var o = t._ctrl;
        var n = null;
        if (E) {
            n = new sap.ui.commons.SearchField.CB(t.getId() + "-cb", {
                listBox: t._lb,
                maxPopupItems: _
            })
        } else {
            n = new sap.ui.commons.SearchField.TF(t.getId() + "-tf")
        }
        n.setParent(t);
        if (o) {
            n.setValue(o.getValue());
            n.setEnabled(o.getEnabled());
            n.setEditable(o.getEditable());
            n.setMaxLength(o.getMaxLength());
            n.setTextAlign(o.getTextAlign());
            n.setTooltip(o.getTooltip());
            n.setMaxPopupItems(o.getMaxPopupItems());
            var A = o.getAriaDescribedBy();
            for (var i = 0; i < A.length; i++) {
                n.addAriaDescribedBy(A[i])
            }
            o.removeAllAriaDescribedBy();
            A = o.getAriaLabelledBy();
            for (var i = 0; i < A.length; i++) {
                n.addAriaLabelledBy(A[i])
            }
            o.removeAllAriaLabelledBy();
            o.destroy()
        }
        t._ctrl = n
    };
    var g = function(k, A) {
        var r = sap.ui.getCore().getLibraryResourceBundle("sap.ui.commons");
        if (r) {
            return r.getText(k, A)
        }
        return k
    };
    var d = function() {
        return sap.ui.Device.browser.mobile
    };
    var e = function(r, C) {
        r.write("<div");
        r.writeAttributeEscaped('id', C.getId() + '-searchico');
        r.writeAttribute('unselectable', 'on');
        if (sap.ui.getCore().getConfiguration().getAccessibility()) {
            r.writeAttribute("role", "presentation")
        }
        r.addClass("sapUiSearchFieldIco");
        r.writeClasses();
        r.write("></div>")
    };
    sap.ui.commons.TextField.extend("sap.ui.commons.SearchField.TF", {
        metadata: {
            visibility: "hidden"
        },
        constructor: function(i, s) {
            sap.ui.commons.TextField.apply(this, arguments)
        },
        getInputDomRef: function() {
            return jQuery.sap.domById(this.getId() + "-input")
        },
        onkeyup: function(E) {
            sap.ui.commons.SearchField.CB.prototype.onkeyup.apply(this, arguments)
        },
        _triggerSuggest: function(C) {
            this._sSuggest = null;
            if ((C && C.length >= this.getParent().getStartSuggestion()) || (!C && this.getParent().getStartSuggestion() == 0)) {
                this.getParent().fireSuggest({
                    value: C
                })
            }
        },
        _checkChange: function(E, D) {
            this.getParent().fireSearch({
                noFocus: D
            })
        },
        onfocusout: function(E) {
            if (this.getEditable() && this.getEnabled() && this.getRenderer().onblur) {
                this.getRenderer().onblur(this)
            }
            this._checkChange(E, true)
        },
        onclick: function(E) {
            if (E.target === jQuery.sap.domById(this.getId() + "-searchico")) {
                if (this.getEditable() && this.getEnabled()) {
                    this.focus()
                }
                if (!this.getParent().getEnableClear()) {
                    this._checkChange(E)
                } else {
                    if (!jQuery(this.getInputDomRef()).val() || !this.getEditable() || !this.getEnabled()) {
                        return
                    }
                    this.setValue("");
                    this._triggerValueHelp = true;
                    this.onkeyup();
                    if (this.getParent().getEnableFilterMode()) {
                        jQuery(this.getInputDomRef()).val("");
                        this.getParent().fireSearch()
                    }
                }
            }
        },
        getMaxPopupItems: function() {
            return this._iVisibleItemCount ? this._iVisibleItemCount : _
        },
        setMaxPopupItems: function(m) {
            this._iVisibleItemCount = m
        },
        renderer: {
            renderOuterContentBefore: e,
            renderOuterAttributes: function(r, C) {
                r.addClass("sapUiSearchFieldTf")
            },
            renderInnerAttributes: function(r, C) {
                r.writeAttribute("type", "search");
                if (d()) {
                    r.writeAttribute('autocapitalize', 'off');
                    r.writeAttribute('autocorrect', 'off')
                }
            }
        }
    });
    sap.ui.commons.SearchField.TF.prototype.getFocusDomRef = sap.ui.commons.SearchField.TF.prototype.getInputDomRef;
    sap.ui.commons.ComboBox.extend("sap.ui.commons.SearchField.CB", {
        metadata: {
            visibility: "hidden"
        },
        constructor: function(i, s) {
            sap.ui.commons.ComboBox.apply(this, arguments);
            this._mSuggestions = {};
            this._aSuggestValues = [];
            this.mobile = false
        },
        updateSuggestions: function(s, S) {
            this._mSuggestions[s] = S;
            if (this.getInputDomRef() && jQuery(this.getInputDomRef()).val() === s && this._hasSuggestValue(s)) {
                this._doUpdateList(s);
                this._aSuggestValues = [s]
            }
        },
        applyFocusInfo: function(f) {
            jQuery(this.getInputDomRef()).val(f.sTypedChars);
            return this
        },
        _getListBox: function() {
            return this.getParent()._lb
        },
        _hasSuggestValue: function(s) {
            return this._aSuggestValues.length > 0 && s == this._aSuggestValues[this._aSuggestValues.length - 1]
        },
        _doUpdateList: function(s, S) {
            if ((!this.oPopup || !this.oPopup.isOpen()) && !S) {
                this._open()
            } else {
                this._updateList(s)
            }
            if (!this._lastKeyIsDel && s === jQuery(this.getInputDomRef()).val()) {
                this._doTypeAhead()
            }
        },
        onclick: function(E) {
            sap.ui.commons.ComboBox.prototype.onclick.apply(this, arguments);
            if (E.target === jQuery.sap.domById(this.getId() + "-searchico")) {
                if (!this.getParent().getEnableClear()) {
                    this.getParent().fireSearch()
                } else if (jQuery(this.getInputDomRef()).val() && this.getEditable() && this.getEnabled()) {
                    this.setValue("");
                    this._triggerValueHelp = true;
                    this.onkeyup(null, true);
                    this._aSuggestValues = [];
                    if (this.getParent().getEnableFilterMode()) {
                        jQuery(this.getInputDomRef()).val("");
                        this.getParent().fireSearch()
                    }
                }
                if (this.getEditable() && this.getEnabled()) {
                    this.focus()
                }
            } else if (jQuery.sap.containsOrEquals(jQuery.sap.domById(this.getId() + "-providerico"), E.target)) {
                if (this.getEditable() && this.getEnabled()) {
                    this.focus()
                }
            }
        },
        onkeypress: sap.ui.commons.SearchField.TF.prototype.onkeypress,
        onkeyup: function(E, s) {
            this.getParent().$().toggleClass("sapUiSearchFieldVal", !! jQuery(this.getInputDomRef()).val());
            if (E) {
                var k = jQuery.sap.KeyCodes;
                if (sap.ui.commons.ComboBox._isHotKey(E) || E.keyCode === k.F4 && E.which === 0) {
                    return
                }
                var K = E.which || E.keyCode;
                if (K !== k.ESCAPE || this instanceof sap.ui.commons.SearchField.TF) {
                    this._triggerValueHelp = true;
                    this._lastKeyIsDel = K == k.DELETE || K == k.BACKSPACE
                }
            }
            if (this._triggerValueHelp) {
                this._triggerValueHelp = false;
                if (this._sSuggest) {
                    jQuery.sap.clearDelayedCall(this._sSuggest);
                    this._sSuggest = null
                }
                var C = jQuery(this.getInputDomRef()).val();
                if ((C && C.length >= this.getParent().getStartSuggestion()) || (!C && this.getParent().getStartSuggestion() == 0)) {
                    this._sSuggest = jQuery.sap.delayedCall(200, this, "_triggerSuggest", [C])
                } else if (this._doUpdateList) {
                    this._doUpdateList(C, s)
                }
            }
        },
        _triggerSuggest: function(s) {
            this._sSuggest = null;
            if (!this._mSuggestions[s] || !this.getParent().getEnableCache()) {
                this._aSuggestValues.push(s);
                var S = this.getParent().getSearchProvider();
                if (S) {
                    var o = this.getParent();
                    S.suggest(s, function(v, f) {
                        if (o) {
                            o.suggest(v, f)
                        }
                    })
                } else {
                    this.getParent().fireSuggest({
                        value: s
                    })
                }
            } else {
                this._doUpdateList(s)
            }
        },
        _updateList: function(s) {
            var E = false;
            var l = this._getListBox();
            l.destroyAggregation("items", true);
            var f = function(l, v, M, j) {
                v = v ? v : [];
                var C = Math.min(v.length, M);
                if (j && C > 0) {
                    l.addItem(new sap.ui.core.SeparatorItem())
                }
                for (var i = 0; i < C; i++) {
                    l.addItem(new sap.ui.core.ListItem({
                        text: v[i]
                    }))
                }
                return C
            };
            var h = f(l, this.getParent()._oHistory.get(s), this.getParent().getMaxHistoryItems(), false);
            var S = f(l, s && s.length >= this.getParent().getStartSuggestion() ? this._mSuggestions[s] : [], this.getParent().getMaxSuggestionItems(), h > 0);
            if (h <= 0 && S == 0) {
                l.addItem(new sap.ui.core.ListItem({
                    text: g("SEARCHFIELD_NO_ITEMS"),
                    enabled: false
                }));
                E = true
            }
            var I = l.getItems().length;
            var m = this.getMaxPopupItems();
            l.setVisibleItems(m < I ? m : I);
            l.setSelectedIndex(-1);
            l.setMinWidth(jQuery(this.getDomRef()).rect().width + "px");
            l.rerender();
            return E
        },
        _prepareOpen: function() {},
        _open: function() {
            sap.ui.commons.ComboBox.prototype._open.apply(this, [0])
        },
        _rerenderListBox: function() {
            return this._updateList(this._aSuggestValues.length > 0 ? this._aSuggestValues[this._aSuggestValues.length - 1] : null)
        },
        _checkChange: function(E, i, D) {
            this.getParent().fireSearch({
                noFocus: D
            })
        },
        onsapfocusleave: function(E) {
            if (E.relatedControlId === this._getListBox().getId()) {
                this.focus();
                return
            }
            this._checkChange(E, true, true)
        },
        onfocusout: function(E) {
            if (this.getEditable() && this.getEnabled() && this.getRenderer().onblur) {
                this.getRenderer().onblur(this)
            }
            this._checkChange(E, true, true)
        },
        onsapshow: function(E) {
            if (this.getParent().hasListExpander()) {
                sap.ui.commons.ComboBox.prototype.onsapshow.apply(this, arguments)
            } else {
                E.preventDefault();
                E.stopImmediatePropagation()
            }
        },
        _handleSelect: function(C) {
            var i = sap.ui.commons.ComboBox.prototype._handleSelect.apply(this, arguments);
            if (i && i.getEnabled()) {
                this.getParent().fireSearch()
            }
        },
        renderer: {
            renderOuterContentBefore: function(r, C) {
                if (C.getParent().hasListExpander()) {
                    sap.ui.commons.ComboBoxRenderer.renderOuterContentBefore.apply(this, arguments)
                }
                e.apply(this, arguments);
                if (C.getParent().getSearchProvider() && C.getParent().getSearchProvider().getIcon()) {
                    r.write("<div");
                    r.writeAttributeEscaped('id', C.getId() + '-providerico');
                    r.writeAttribute('unselectable', 'on');
                    if (sap.ui.getCore().getConfiguration().getAccessibility()) {
                        r.writeAttribute("role", "presentation")
                    }
                    r.addClass("sapUiSearchFieldProvIco");
                    r.writeClasses();
                    r.write("><img src=\"" + C.getParent().getSearchProvider().getIcon() + "\"/></div>")
                }
            },
            renderOuterAttributes: function(r, C) {
                sap.ui.commons.ComboBoxRenderer.renderOuterAttributes.apply(this, arguments);
                r.addClass("sapUiSearchFieldCb");
                if (C.getParent().getSearchProvider() && C.getParent().getSearchProvider().getIcon()) {
                    r.addClass("sapUiSearchFieldCbProv")
                }
            },
            renderInnerAttributes: function(r, C) {
                r.writeAttribute("type", "search");
                if (d()) {
                    r.writeAttribute('autocapitalize', 'off');
                    r.writeAttribute('autocorrect', 'off')
                }
            }
        }
    })
}());