﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.Input");
jQuery.sap.require("sap.m.library");
jQuery.sap.require("sap.m.InputBase");
sap.m.InputBase.extend("sap.m.Input", {
    metadata: {
        publicMethods: ["setFilterFunction"],
        library: "sap.m",
        properties: {
            "type": {
                type: "sap.m.InputType",
                group: "Data",
                defaultValue: sap.m.InputType.Text
            },
            "maxLength": {
                type: "int",
                group: "Behavior",
                defaultValue: 0
            },
            "valueStateText": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "showValueStateMessage": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "dateFormat": {
                type: "string",
                group: "Misc",
                defaultValue: 'YYYY-MM-dd',
                deprecated: true
            },
            "showValueHelp": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "showSuggestion": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            }
        },
        aggregations: {
            "suggestionItems": {
                type: "sap.ui.core.Item",
                multiple: true,
                singularName: "suggestionItem"
            }
        },
        events: {
            "liveChange": {},
            "valueHelpRequest": {},
            "suggest": {},
            "suggestionItemSelected": {}
        }
    }
});
sap.m.Input.M_EVENTS = {
    'liveChange': 'liveChange',
    'valueHelpRequest': 'valueHelpRequest',
    'suggest': 'suggest',
    'suggestionItemSelected': 'suggestionItemSelected'
};
jQuery.sap.require("jquery.sap.strings");
jQuery.sap.require("sap.m.Dialog");
jQuery.sap.require("sap.m.Popover");
jQuery.sap.require("sap.m.List");
jQuery.sap.require("sap.m.StandardListItem");
jQuery.sap.require("sap.m.Bar");
jQuery.sap.require("sap.ui.core.IconPool");
sap.ui.core.IconPool.insertFontFaceStyle();

sap.m.Input._DEFAULTFILTER = function(v, i) {
    return jQuery.sap.startsWithIgnoreCase(i.getText(), v)
};

sap.m.Input.prototype.init = function() {
    sap.m.InputBase.prototype.init.call(this);
    this._inputProxy = jQuery.proxy(this._onInput, this);
    this._fnFilter = sap.m.Input._DEFAULTFILTER
};

sap.m.Input.prototype.exit = function() {
    this._deregisterEvents();
    if (this._oSuggestionPopup) {
        this._oSuggestionPopup.destroy();
        this._oSuggestionPopup = null
    }
    if (this._oValueHelpIcon) {
        this._oValueHelpIcon.destroy();
        this._oValueHelpIcon = null
    }
};

sap.m.Input.prototype.onBeforeRendering = function() {
    sap.m.InputBase.prototype.onBeforeRendering.call(this);
    this._deregisterEvents()
};

sap.m.Input.prototype.onAfterRendering = function() {
    var t = this;
    sap.m.InputBase.prototype.onAfterRendering.call(this);
    this._bindToInputEvent(this._inputProxy);
    if (this._oList && !sap.ui.Device.system.phone) {
        this._oList.setWidth(this.$().outerWidth() + "px");
        this._sPopupResizeHandler = sap.ui.core.ResizeHandler.register(jQuery.sap.domById(this.getId()), function() {
            t._oList.setWidth(t.$().outerWidth() + "px")
        })
    }
    if (sap.ui.Device.system.phone && this._oSuggestionPopup) {
        this.$().on("click", jQuery.proxy(function() {
            if (this.getShowSuggestion() && this._oSuggestionPopup) {
                this._oSuggestionPopup.open();
                this._oPopupInput._$input.focus()
            }
        }, this))
    }
};

sap.m.Input.prototype._getValueHelpIcon = function() {
    var t = this;
    if (!this._oValueHelpIcon) {
        var u = sap.ui.core.IconPool.getIconURI("value-help");
        this._oValueHelpIcon = sap.ui.core.IconPool.createControlByURI({
            id: this.getId() + "__vhi",
            src: u
        });
        this._oValueHelpIcon.addStyleClass("sapMInputValHelpInner");
        this._oValueHelpIcon.attachPress(function(e) {
            t.fireValueHelpRequest()
        })
    }
    return this._oValueHelpIcon
};

sap.m.Input.prototype.setWidth = function(w) {
    return sap.m.InputBase.prototype.setWidth.call(this, w || "100%")
};

sap.m.Input.prototype.setFilterFunction = function(f) {
    this._fnFilter = f
};

sap.m.Input.prototype._doSelect = function(s, e) {
    if (sap.ui.Device.support.touch) {
        return
    }
    var d = this._$input[0];
    if (d) {
        var r = this._$input;
        d.focus();
        r.selectText(s ? s : 0, e ? e : r.val().length)
    }
    return this
};

sap.m.Input.prototype._scrollToItem = function(i, d) {
    var p = this._oSuggestionPopup,
        l = this._oList;
    if (!(p instanceof sap.m.Popover) || !l) {
        return
    }
    var L = l.getItems()[i],
        o = L && L.$()[0];
    if (o) {
        o.scrollIntoView(d === "up")
    }
};

sap.m.Input.prototype._onsaparrowkey = function(e, d) {
    if (d !== "up" && d !== "down") {
        return
    }
    if (!this.getEnabled() || !this.getEditable()) {
        return
    }
    if (!this._oSuggestionPopup || !this._oSuggestionPopup.isOpen()) {
        return
    }
    var l = this._oList,
        L = l.getItems(),
        v = this._$input.val(),
        i, I = -1,
        s;
    for (i = 0; i < L.length; i++) {
        if (L[i].getTitle() === v) {
            I = i;
            break
        }
    }
    if (I === -1) {
        s = 0
    } else {
        s = I;
        if (d === "down") {
            if (I < L.length - 1) {
                s = I + 1;
                L[I].$().removeClass("sapMLIBSelected")
            }
        } else {
            if (I > 0) {
                s = I - 1;
                L[I].$().removeClass("sapMLIBSelected")
            }
        }
    }
    L[s].$().addClass("sapMLIBSelected");
    if (sap.ui.Device.system.desktop) {
        this._scrollToItem(s, d)
    }
    this._$input.val(L[s].getTitle());
    this._doSelect();
    this._iPopupListSelectedIndex = s;
    e.preventDefault();
    e.stopPropagation()
};

sap.m.Input.prototype.onsapup = function(e) {
    this._onsaparrowkey(e, "up")
};

sap.m.Input.prototype.onsapdown = function(e) {
    this._onsaparrowkey(e, "down")
};

sap.m.Input.prototype.onsapescape = function(e) {
    if (this._oSuggestionPopup && this._oSuggestionPopup.isOpen()) {
        this._oSuggestionPopup.close()
    }
    if (sap.m.InputBase.prototype.onsapescape) {
        sap.m.InputBase.prototype.onsapescape.apply(this, arguments)
    }
};

sap.m.Input.prototype.onsapenter = function(e) {
    if (sap.m.InputBase.prototype.onsapenter) {
        sap.m.InputBase.prototype.onsapenter.apply(this, arguments)
    }
    if (this._oSuggestionPopup && this._oSuggestionPopup.isOpen() && this._iPopupListSelectedIndex >= 0) {
        var s = this._oList.getItems()[this._iPopupListSelectedIndex];
        this._changeProxy(e);
        this._oSuggestionPopup.close();
        this._doSelect();
        if (s) {
            this.fireSuggestionItemSelected({
                selectedItem: s._oItem
            })
        }
        this._iPopupListSelectedIndex = -1
    }
};

sap.m.Input.prototype.onsapfocusleave = function(e) {
    var p = this._oSuggestionPopup;
    if (!(p instanceof sap.m.Popover)) {
        return
    }
    if (e.relatedControlId && jQuery.sap.containsOrEquals(p.getFocusDomRef(), sap.ui.getCore().byId(e.relatedControlId).getFocusDomRef())) {
        this.focus()
    }
};

sap.m.Input.prototype.onmousedown = function(e) {
    var p = this._oSuggestionPopup;
    if ((p instanceof sap.m.Popover) && p.isOpen()) {
        e.stopPropagation()
    }
};

sap.m.Input.prototype._deregisterEvents = function() {
    if (this._sPopupResizeHandler) {
        sap.ui.core.ResizeHandler.deregister(this._sPopupResizeHandler);
        this._sPopupResizeHandler = null
    }
    if (sap.ui.Device.system.phone && this._oSuggestionPopup) {
        this.$().off("click")
    }
};

(function() {
    sap.m.Input.prototype.setShowSuggestion = function(v) {
        var t = this;
        this.setProperty("showSuggestion", v);
        if (v) {
            if (this._oSuggestionPopup) {
                return this
            }
            if (sap.ui.Device.system.phone) {
                this._oPopupInput = new sap.m.Input(this.getId() + "-popup-input", {
                    width: "100%",
                    suggest: function(e) {
                        t.fireSuggest({
                            suggestValue: e.getParameter("suggestValue")
                        })
                    },
                    liveChange: function(e) {
                        var V = e.getParameter("newValue");
                        t.setValue(V);
                        if (V) {
                            r(t)
                        }
                    }
                }).addStyleClass("sapMInputSuggInDialog")
            }
            this._oSuggestionPopup = !sap.ui.Device.system.phone ? (new sap.m.Popover(this.getId() + "-popup", {
                showHeader: false,
                placement: sap.m.PlacementType.Vertical,
                initialFocus: this
            })) : (new sap.m.Dialog(this.getId() + "-popup", {
                beginButton: new sap.m.Button(this.getId() + "-popup-closeButton", {
                    text: "Close",
                    press: function() {
                        t._oSuggestionPopup.close()
                    }
                }),
                stretch: true,
                customHeader: new sap.m.Bar(this.getId() + "-popup-header", {
                    contentMiddle: this._oPopupInput
                }),
                horizontalScrolling: false,
                initialFocus: this._oPopupInput
            }).attachAfterClose(function() {
                t._$input.val(t._oPopupInput._$input.val());
                t._changeProxy();
                t._oList.destroyItems()
            })).attachAfterOpen(function() {
                t._oPopupInput._$input.val(t._$input.val());
                if (t._$input.val()) {
                    t.fireSuggest({
                        suggestValue: t._$input.val()
                    })
                }
            });
            this._oSuggestionPopup.addStyleClass("sapMInputSuggestionPopup");
            if (!sap.ui.Device.system.phone) {
                o(this._oSuggestionPopup, this)
            }
            this._oList = new sap.m.List(this.getId() + "-popup-list", {
                width: "100%"
            });
            this._oSuggestionPopup.addContent(this._oList)
        } else {
            if (this._oSuggestionPopup) {
                this._oSuggestionPopup.destory();
                this._oSuggestionPopup = null
            }
        }
    };
    sap.m.Input.prototype._onInput = function(e) {
        var v = this._$input.val();
        if (this.getMaxLength() > 0 && v.length > this.getMaxLength()) {
            v = v.substring(0, this.getMaxLength());
            this._$input.val(v)
        }
        if (v != this.getProperty("value")) {
            this.setProperty("value", v, true);
            this._curpos = this._$input.cursorPos();
            this._setLabelVisibility();
            this.fireLiveChange({
                newValue: v
            });
            if (v) {
                r(this);
                this.fireSuggest({
                    suggestValue: v
                })
            } else {
                if (this._oSuggestionPopup && this._oSuggestionPopup.isOpen()) {
                    this._oSuggestionPopup.close()
                }
            }
        }
    };
    sap.m.Input.prototype.addSuggestionItem = function(i) {
        this.addAggregation("suggestionItems", i, true);
        r(this);
        return this
    };
    sap.m.Input.prototype.insertSuggestionItem = function(i, I) {
        this.insertAggregation("suggestionItems", I, i, true);
        r(this);
        return this
    };
    sap.m.Input.prototype.removeSuggestionItem = function(i) {
        var a = this.removeAggregation("suggestionItems", i, true);
        r(this);
        return a
    };
    sap.m.Input.prototype.removeAllSuggestionItems = function() {
        var a = this.removeAllAggregation("suggestionItems", true);
        r(this);
        return a
    };
    sap.m.Input.prototype.destroySuggestionItems = function() {
        this.destroyAggregation("suggestionItems", true);
        r(this);
        return this
    };

    function o(p, i) {
        p._marginTop = 0;
        p._marginLeft = 0;
        p._marginRight = 0;
        p._marginBottom = 0;
        p._arrowOffset = 0;
        p._offsets = ["0 0", "0 0", "0 0", "0 0"];
        p._myPositions = ["begin bottom", "begin center", "begin top", "end center"];
        p._atPositions = ["begin top", "end center", "begin bottom", "begin center"];
        p.open = function() {
            this.openBy(i, false, true)
        };
        p.oPopup.setAnimations(function(R, a, O) {
            O()
        }, function(R, a, c) {
            c()
        })
    }
    function r(I) {
        var s = I.getShowSuggestion();
        this._iPopupListSelectedIndex = -1;
        if (!(s && I.getDomRef() && (sap.ui.Device.system.phone || I.$().hasClass("sapMInputFocused")))) {
            return false
        }
        var a, b = I.getSuggestionItems(),
            t = I._$input.val(),
            l = I._oList,
            f = t && t.length > 0,
            h = [],
            p = I._oSuggestionPopup,
            L = {
                ontouchstart: function(e) {
                    (e.originalEvent || e)._sapui_cancelAutoClose = true
                }
            }, S;
        if (!f) {
            p.close();
            return false
        }
        l.destroyItems();
        for (var i = 0; i < b.length; i++) {
            a = b[i];
            if (!f || I._fnFilter(t, a)) {
                S = new sap.m.StandardListItem(a.getId() + "-sli", {
                    title: a.getText(),
                    type: a.getEnabled() ? sap.m.ListType.Active : sap.m.ListType.Inactive,
                    press: function() {
                        if (sap.ui.Device.system.phone) {
                            I._oPopupInput._$input.val(this.getTitle());
                            I._oPopupInput._doSelect()
                        } else {
                            I._$input.val(this.getTitle());
                            I._changeProxy()
                        }
                        p.close();
                        if (!sap.ui.Device.support.touch) {
                            I._doSelect()
                        }
                        I.fireSuggestionItemSelected({
                            selectedItem: this._oItem
                        })
                    }
                });
                S._oItem = a;
                S.addEventDelegate(L);
                h.push(S)
            }
        }
        var c = h.length;
        if (c > 0) {
            for (var i = 0; i < c; i++) {
                l.addItem(h[i])
            }
            if (!sap.ui.Device.system.phone) {
                if (I._sCloseTimer) {
                    clearTimeout(I._sCloseTimer);
                    I._sCloseTimer = null
                }
                if (!p.isOpen() && !I._sOpenTimer) {
                    I._sOpenTimer = setTimeout(function() {
                        p.open();
                        I._sOpenTimer = null
                    }, 0)
                }
            }
        } else {
            if (!sap.ui.Device.system.phone && p.isOpen()) {
                I._sCloseTimer = setTimeout(function() {
                    p.close()
                }, 0)
            }
        }
    }
})();
(function() {
    function c(i) {
        if (i._popup) {
            i._popup.close()
        }
    };

    function o(i) {
        var s = i.getValueState();
        if (i.getShowValueStateMessage() && s && ((s === sap.ui.core.ValueState.Warning) || (s === sap.ui.core.ValueState.Error)) && i.getEnabled() && i.getEditable()) {
            var t = i.getValueStateText();
            if (!t) {
                t = sap.ui.core.ValueStateSupport.getAdditionalText(i)
            }
            if (!t) {
                return
            }
            var m = i.getId() + "-message";
            if (!i._popup) {
                jQuery.sap.require("sap.ui.core.Popup");
                jQuery.sap.require("jquery.sap.encoder");
                i._popup = new sap.ui.core.Popup(jQuery("<span></span>")[0], false, false, false);
                i._popup.attachClosed(function() {
                    jQuery.sap.byId(m).remove()
                })
            }
            var I = jQuery(i.getFocusDomRef());
            var d = sap.ui.core.Popup.Dock;
            var b = I.css("text-align") === "right";
            var C = "sapMInputMessage " + ((s === sap.ui.core.ValueState.Warning) ? "sapMInputMessageWarning" : "sapMInputMessageError");
            i._popup.setContent(jQuery("<div style=\"max-width:" + I.outerWidth() + "px;\" class=\"" + C + "\" id=\"" + m + "\"><span id=\"" + m + "-text\">" + jQuery.sap.encodeHTML(t) + "</span></div>"));
            i._popup.close(0);
            i._popup.open(200, b ? d.EndTop : d.BeginTop, b ? d.EndBottom : d.BeginBottom, i.getFocusDomRef(), null, null, function() {
                i._popup.close()
            })
        }
    };
    sap.m.Input.prototype.setValueState = function(v) {
        var O = this.getValueState();
        sap.m.InputBase.prototype.setValueState.apply(this, arguments);
        var n = this.getValueState();
        if (this.getDomRef() && n != O && this.getFocusDomRef() === document.activeElement) {
            switch (n) {
                case sap.ui.core.ValueState.Error:
                case sap.ui.core.ValueState.Warning:
                    o(this);
                    break;
                default:
                    c(this)
            }
        }
        return this
    };
    sap.m.Input.prototype.setValueStateText = function(t) {
        jQuery.sap.byId(this.getId() + "-message-text").text(t);
        return this.setProperty("valueStateText", t, true)
    };
    sap.m.Input.prototype.onfocusin = function(e) {
        this.$().addClass("sapMInputFocused");
        o(this)
    };
    sap.m.Input.prototype.onsapshow = function(e) {
        if (!this.getEnabled() || !this.getShowValueHelp()) {
            return
        }
        this.fireValueHelpRequest();
        e.preventDefault();
        e.stopPropagation()
    };
    sap.m.Input.prototype.onkeydown = function(e) {
        c(this)
    };
    sap.m.Input.prototype.onfocusout = function(e) {
        this.$().removeClass("sapMInputFocused");
        c(this);
        sap.m.InputBase.prototype.onfocusout.apply(this)
    }
})();