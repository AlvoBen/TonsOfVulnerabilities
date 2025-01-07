﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.Menu");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.commons.Menu", {
    metadata: {
        publicMethods: ["open", "close"],
        library: "sap.ui.commons",
        properties: {
            "enabled": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "ariaDescription": {
                type: "string",
                group: "Accessibility",
                defaultValue: null
            }
        },
        defaultAggregation: "items",
        aggregations: {
            "items": {
                type: "sap.ui.commons.MenuItemBase",
                multiple: true,
                singularName: "item"
            }
        },
        events: {
            "itemSelect": {}
        }
    }
});
sap.ui.commons.Menu.M_EVENTS = {
    'itemSelect': 'itemSelect'
};
(function(w, u) {
    jQuery.sap.require("sap.ui.commons.MenuItemBase");
    jQuery.sap.require("sap.ui.core.Popup");
    sap.ui.commons.Menu.prototype.init = function() {
        var t = this;
        this.bOpen = false;
        this.oOpenedSubMenu = null;
        this.oHoveredItem = null;
        this.oPopup = null;
        this.fAnyEventHandlerProxy = jQuery.proxy(this.onAnyEvent, this);
        this.fOrientationChangeHandler = function() {
            t.close()
        };
        this.bUseTopStyle = false
    };
    sap.ui.commons.Menu.prototype.exit = function() {
        if (this.oPopup) {
            this.oPopup.detachOpened(this._menuOpened, this);
            this.oPopup.detachClosed(this._menuClosed, this);
            this.oPopup.destroy();
            delete this.oPopup
        }
        jQuery.sap.unbindAnyEvent(this.fAnyEventHandlerProxy);
        if (this._bOrientationChangeBound) {
            jQuery(w).unbind("orientationchange", this.fOrientationChangeHandler);
            this._bOrientationChangeBound = false
        }
        if (this.sResizeListenerId) {
            sap.ui.core.ResizeHandler.deregister(this.sResizeListenerId);
            this.sResizeListenerId = null
        }
        if (this._sParentPopupId) {
            delete this._sParentPopupId;
            delete this._bBubbleAutoClose
        }
    };
    sap.ui.commons.Menu.prototype.onBeforeRendering = function() {
        if (this.sResizeListenerId) {
            sap.ui.core.ResizeHandler.deregister(this.sResizeListenerId);
            this.sResizeListenerId = null
        }
    };
    sap.ui.commons.Menu.prototype.onAfterRendering = function() {
        if (this.oHoveredItem) {
            this.oHoveredItem.hover(true, this)
        }
    };
    sap.ui.commons.Menu.prototype.open = function(W, o, m, b, c, d, e) {
        if (this.bOpen) {
            return
        }
        this.bOpen = true;
        this.oOpenerRef = o;
        this.getPopup().open(0, m, b, c, d || "0 0", e || "_sapUiCommonsMenuFlip _sapUiCommonsMenuFlip", true);
        var D = this.getDomRef();
        jQuery(D).attr("tabIndex", 0).focus();
        if (W) {
            this.setHoveredItem(this.getNextVisibleItem(-1))
        }
        jQuery.sap.bindAnyEvent(this.fAnyEventHandlerProxy);
        if (sap.ui.Device.support.orientation && this.getRootMenu() === this) {
            jQuery(w).bind("orientationchange", this.fOrientationChangeHandler);
            this._bOrientationChangeBound = true
        }
    };
    sap.ui.commons.Menu.prototype._menuOpened = function() {
        if (this.oOpenerRef) {
            var o = this.oOpenerRef instanceof sap.ui.core.Control ? this.oOpenerRef.$() : jQuery(this.oOpenerRef);
            var p = o.closest("[data-sap-ui-popup]");
            var P = p.attr("data-sap-ui-popup");
            if (P) {
                this._sParentPopupId = P;
                var O = {
                    id: this.getId()
                };
                var e = "sap.ui.core.Popup.addFocusableContent-" + this._sParentPopupId;
                sap.ui.getCore().getEventBus().publish("sap.ui", e, O)
            }
        }
        I(this)
    };
    sap.ui.commons.Menu.prototype.close = function() {
        if (!this.bOpen || sap.ui.commons.Menu._dbg) {
            return
        }
        jQuery.sap.unbindAnyEvent(this.fAnyEventHandlerProxy);
        if (this._bOrientationChangeBound) {
            jQuery(w).unbind("orientationchange", this.fOrientationChangeHandler);
            this._bOrientationChangeBound = false
        }
        this.bOpen = false;
        if (this.oOpenedSubMenu) {
            this.oOpenedSubMenu.close()
        }
        this.setHoveredItem();
        jQuery(this.getDomRef()).attr("tabIndex", -1);
        if (this.oOpenerRef && !this.ignoreOpenerDOMRef) {
            this.oOpenerRef.focus()
        }
        this.oOpenerRef = u;
        this.getPopup().close(0);
        this.onBeforeRendering();
        this.$().remove();
        this.bOutput = false;
        if (this.isSubMenu()) {
            this.getParent().getParent().oOpenedSubMenu = null
        }
    };
    sap.ui.commons.Menu.prototype._menuClosed = function() {
        if (this._sParentPopupId) {
            var o = {
                id: this.getId(),
                bAutoClose: this._bBubbleAutoClose
            };
            var e = "sap.ui.core.Popup.removeFocusableContent-" + this._sParentPopupId;
            sap.ui.getCore().getEventBus().publish("sap.ui", e, o)
        }
        delete this._sParentPopupId;
        delete this._bBubbleAutoClose
    };
    sap.ui.commons.Menu.prototype.onclick = function(e) {
        this.selectItem(this.getItemByDomRef(e.target), false, !! (e.metaKey || e.ctrlKey));
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsapnext = function(e) {
        if (e.keyCode != jQuery.sap.KeyCodes.ARROW_DOWN) {
            if (this.oHoveredItem && this.oHoveredItem.getSubmenu() && this.checkEnabled(this.oHoveredItem)) {
                this.openSubmenu(this.oHoveredItem, true);
                return
            }
        }
        var i = this.oHoveredItem ? this.indexOfAggregation("items", this.oHoveredItem) : -1;
        this.setHoveredItem(this.getNextVisibleItem(i));
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsapprevious = function(e) {
        if (e.keyCode != jQuery.sap.KeyCodes.ARROW_UP) {
            if (this.isSubMenu()) {
                this.close();
                e.preventDefault();
                e.stopPropagation();
                return
            }
        }
        var i = this.oHoveredItem ? this.indexOfAggregation("items", this.oHoveredItem) : -1;
        this.setHoveredItem(this.getPreviousVisibleItem(i));
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsaphome = function(e) {
        var b = this.getItems();
        var o = null;
        for (var i = 0; i < b.length; i++) {
            if (b[i].getVisible()) {
                o = b[i];
                break
            }
        }
        this.setHoveredItem(o);
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsapend = function(e) {
        var b = this.getItems();
        var o = null;
        for (var i = b.length - 1; i >= 0; i--) {
            if (b[i].getVisible()) {
                o = b[i];
                break
            }
        }
        this.setHoveredItem(o);
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsapselect = function(e) {
        this._sapSelectOnKeyDown = true;
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onkeyup = function(e) {
        if (!this._sapSelectOnKeyDown) {
            return
        } else {
            this._sapSelectOnKeyDown = false
        }
        if (!jQuery.sap.PseudoEvents.sapselect.fnCheck(e)) {
            return
        }
        this.selectItem(this.oHoveredItem, true, false);
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsapbackspace = function(e) {
        if (jQuery(e.target).prop("tagName") != "INPUT") {
            e.preventDefault()
        }
    };
    sap.ui.commons.Menu.prototype.onsapbackspacemodifiers = sap.ui.commons.Menu.prototype.onsapbackspace;
    sap.ui.commons.Menu.prototype.onsapescape = function(e) {
        this.close();
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.commons.Menu.prototype.onsaptabnext = sap.ui.commons.Menu.prototype.onsapescape;
    sap.ui.commons.Menu.prototype.onsaptabprevious = sap.ui.commons.Menu.prototype.onsapescape;
    sap.ui.commons.Menu.prototype.onmouseover = function(e) {
        var i = this.getItemByDomRef(e.target);
        if (!this.bOpen || !i || i == this.oHoveredItem) {
            return
        }
        if (this.oOpenedSubMenu && jQuery.sap.containsOrEquals(this.oOpenedSubMenu.getDomRef(), e.target)) {
            return
        }
        this.setHoveredItem(i);
        if (this.oOpenedSubMenu) {
            this.oOpenedSubMenu.close();
            this.oOpenedSubMenu = null
        }
        if (jQuery.sap.checkMouseEnterOrLeave(e, this.getDomRef())) {
            this.getDomRef().focus()
        }
        if (this.checkEnabled(i)) {
            this.openSubmenu(i, false)
        }
    };
    sap.ui.commons.Menu.prototype.onmouseout = function(e) {
        I(this);
        if (jQuery.sap.checkMouseEnterOrLeave(e, this.getDomRef())) {
            if (!this.oOpenedSubMenu || !this.oOpenedSubMenu.getParent() === this.oHoveredItem) {
                this.setHoveredItem(null)
            }
        }
    };
    sap.ui.commons.Menu.prototype.onAnyEvent = function(e) {
        if (!this.bOpen || e.type != "mousedown") {
            return
        }
        var s = e.target,
            d = this.getDomRef();
        if (!jQuery.sap.containsOrEquals(d, s) || s.tagName == "BODY") {
            this.getRootMenu().handleOuterEvent(this.getId(), e)
        }
    };
    sap.ui.commons.Menu.prototype.onsapfocusleave = function(e) {
        if (this.oOpenedSubMenu || !this.bOpen) {
            return
        }
        this.getRootMenu().handleOuterEvent(this.getId(), e)
    };
    sap.ui.commons.Menu.prototype.handleOuterEvent = function(m, e) {
        var i = false;
        if (e.type == "mousedown") {
            var c = this;
            while (c) {
                if (jQuery.sap.containsOrEquals(c.getDomRef(), e.target)) {
                    i = true
                }
                c = c.oOpenedSubMenu
            }
        } else if (e.type == "sapfocusleave") {
            if (e.relatedControlId) {
                var c = this;
                while (c) {
                    if ((c.oOpenedSubMenu && c.oOpenedSubMenu.getId() == e.relatedControlId) || jQuery.sap.containsOrEquals(c.getDomRef(), jQuery.sap.byId(e.relatedControlId).get(0))) {
                        i = true
                    }
                    c = c.oOpenedSubMenu
                }
            }
        }
        if (!i) {
            this.ignoreOpenerDOMRef = true;
            this._bBubbleAutoClose = !! this._sParentPopupId;
            this.close();
            this.ignoreOpenerDOMRef = false
        }
    };
    sap.ui.commons.Menu.prototype.getItemByDomRef = function(d) {
        var o = this.getItems(),
            l = o.length;
        for (var i = 0; i < l; i++) {
            var b = o[i],
                c = b.getDomRef();
            if (jQuery.sap.containsOrEquals(c, d)) {
                return b
            }
        }
        return null
    };
    sap.ui.commons.Menu.prototype.selectItem = function(i, W, c) {
        if (!i || !(i instanceof sap.ui.commons.MenuItemBase && this.checkEnabled(i))) {
            return
        }
        var s = i.getSubmenu();
        if (!s) {
            this.getRootMenu().close()
        } else {
            this.openSubmenu(i, W)
        }
        i.fireSelect({
            item: i,
            ctrlKey: c
        });
        this.getRootMenu().fireItemSelect({
            item: i
        })
    };
    sap.ui.commons.Menu.prototype.isSubMenu = function() {
        return this.getParent() && this.getParent().getParent && this.getParent().getParent() instanceof sap.ui.commons.Menu
    };
    sap.ui.commons.Menu.prototype.getRootMenu = function() {
        var m = this;
        while (m.isSubMenu()) {
            m = m.getParent().getParent()
        }
        return m
    };
    sap.ui.commons.Menu.prototype.getMenuLevel = function() {
        var l = 1;
        var m = this;
        while (m.isSubMenu()) {
            m = m.getParent().getParent();
            l++
        }
        return l
    };
    sap.ui.commons.Menu.prototype.getPopup = function() {
        if (!this.oPopup) {
            this.oPopup = new sap.ui.core.Popup(this, false, true);
            this.oPopup.attachOpened(this._menuOpened, this);
            this.oPopup.attachClosed(this._menuClosed, this)
        }
        return this.oPopup
    };
    sap.ui.commons.Menu.prototype.setHoveredItem = function(i) {
        if (this.oHoveredItem) {
            this.oHoveredItem.hover(false, this)
        }
        if (!i) {
            this.oHoveredItem = null;
            jQuery(this.getDomRef()).removeAttr("aria-activedescendant");
            return
        }
        this.oHoveredItem = i;
        i.hover(true, this);
        if (sap.ui.getCore().getConfiguration().getAccessibility()) {
            jQuery(this.getDomRef()).attr("aria-activedescendant", i.getId())
        }
    };
    sap.ui.commons.Menu.prototype.openSubmenu = function(i, W) {
        var s = i.getSubmenu();
        if (!s) {
            return
        }
        if (this.oOpenedSubMenu === s) {
            this.oOpenedSubMenu = null;
            s.close()
        } else {
            if (this.oOpenedSubMenu) {
                this.oOpenedSubMenu.close();
                this.oOpenedSubMenu = null
            }
            this.oOpenedSubMenu = s;
            var e = sap.ui.core.Popup.Dock;
            s.open(W, this, e.BeginTop, e.EndTop, i, "0 0")
        }
    };
    sap.ui.commons.Menu.prototype.checkEnabled = function(i) {
        I(this);
        return i && i.getEnabled() && this.getEnabled()
    };
    sap.ui.commons.Menu.prototype.getNextVisibleItem = function(b) {
        var o = null;
        var c = this.getItems();
        for (var i = b + 1; i < c.length; i++) {
            if (c[i].getVisible()) {
                o = c[i];
                break
            }
        }
        if (!o) {
            for (var i = 0; i <= b; i++) {
                if (c[i].getVisible()) {
                    o = c[i];
                    break
                }
            }
        }
        return o
    };
    sap.ui.commons.Menu.prototype.getPreviousVisibleItem = function(b) {
        var o = null;
        var c = this.getItems();
        for (var i = b - 1; i >= 0; i--) {
            if (c[i].getVisible()) {
                o = c[i];
                break
            }
        }
        if (!o) {
            for (var i = c.length - 1; i >= b; i--) {
                if (c[i].getVisible()) {
                    o = c[i];
                    break
                }
            }
        }
        return o
    };
    sap.ui.commons.Menu.prototype.setRootMenuTopStyle = function(U) {
        this.getRootMenu().bUseTopStyle = U;
        sap.ui.commons.Menu.rerenderMenu(this.getRootMenu())
    };
    sap.ui.commons.Menu.rerenderMenu = function(m) {
        var b = m.getItems();
        for (var i = 0; i < b.length; i++) {
            var s = b[i].getSubmenu();
            if (s) {
                sap.ui.commons.Menu.rerenderMenu(s)
            }
        }
        m.invalidate();
        m.rerender()
    };
    var I = function() {};
    if (sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version < 9) {
        I = function(m, d) {
            if (d === u) {
                d = 50
            }
            jQuery.sap.delayedCall(d, m, function() {
                var e = this.$();
                if (e.length > 0) {
                    var D = e[0].firstChild;
                    sap.ui.core.RenderManager.forceRepaint(D)
                }
            })
        }
    }

    /*!
     * The following code is taken from
     * jQuery UI 1.10.3 - 2013-11-18
     * jquery.ui.position.js
     *
     * http://jqueryui.com
     * Copyright 2013 jQuery Foundation and other contributors; Licensed MIT
     */

    function _(d) {
        var b = jQuery(w);
        d.within = {
            element: b,
            isWindow: true,
            offset: b.offset() || {
                left: 0,
                top: 0
            },
            scrollLeft: b.scrollLeft(),
            scrollTop: b.scrollTop(),
            width: b.width(),
            height: b.height()
        };
        d.collisionPosition = {
            marginLeft: 0,
            marginTop: 0
        };
        return d
    };
    var a = {
        fit: {
            left: function(p, d) {
                var b = d.within,
                    c = b.isWindow ? b.scrollLeft : b.offset.left,
                    o = b.width,
                    e = p.left - d.collisionPosition.marginLeft,
                    f = c - e,
                    g = e + d.collisionWidth - o - c,
                    n;
                if (d.collisionWidth > o) {
                    if (f > 0 && g <= 0) {
                        n = p.left + f + d.collisionWidth - o - c;
                        p.left += f - n
                    } else if (g > 0 && f <= 0) {
                        p.left = c
                    } else {
                        if (f > g) {
                            p.left = c + o - d.collisionWidth
                        } else {
                            p.left = c
                        }
                    }
                } else if (f > 0) {
                    p.left += f
                } else if (g > 0) {
                    p.left -= g
                } else {
                    p.left = Math.max(p.left - e, p.left)
                }
            },
            top: function(p, d) {
                var b = d.within,
                    c = b.isWindow ? b.scrollTop : b.offset.top,
                    o = d.within.height,
                    e = p.top - d.collisionPosition.marginTop,
                    f = c - e,
                    g = e + d.collisionHeight - o - c,
                    n;
                if (d.collisionHeight > o) {
                    if (f > 0 && g <= 0) {
                        n = p.top + f + d.collisionHeight - o - c;
                        p.top += f - n
                    } else if (g > 0 && f <= 0) {
                        p.top = c
                    } else {
                        if (f > g) {
                            p.top = c + o - d.collisionHeight
                        } else {
                            p.top = c
                        }
                    }
                } else if (f > 0) {
                    p.top += f
                } else if (g > 0) {
                    p.top -= g
                } else {
                    p.top = Math.max(p.top - e, p.top)
                }
            }
        },
        flip: {
            left: function(p, d) {
                var b = d.within,
                    c = b.offset.left + b.scrollLeft,
                    o = b.width,
                    e = b.isWindow ? b.scrollLeft : b.offset.left,
                    f = p.left - d.collisionPosition.marginLeft,
                    g = f - e,
                    h = f + d.collisionWidth - o - e,
                    m = d.my[0] === "left" ? -d.elemWidth : d.my[0] === "right" ? d.elemWidth : 0,
                    i = d.at[0] === "left" ? d.targetWidth : d.at[0] === "right" ? -d.targetWidth : 0,
                    j = -2 * d.offset[0],
                    n, k;
                if (g < 0) {
                    n = p.left + m + i + j + d.collisionWidth - o - c;
                    if (n < 0 || n < Math.abs(g)) {
                        p.left += m + i + j
                    }
                } else if (h > 0) {
                    k = p.left - d.collisionPosition.marginLeft + m + i + j - e;
                    if (k > 0 || Math.abs(k) < h) {
                        p.left += m + i + j
                    }
                }
            },
            top: function(p, d) {
                var b = d.within,
                    c = b.offset.top + b.scrollTop,
                    o = b.height,
                    e = b.isWindow ? b.scrollTop : b.offset.top,
                    f = p.top - d.collisionPosition.marginTop,
                    g = f - e,
                    h = f + d.collisionHeight - o - e,
                    t = d.my[1] === "top",
                    m = t ? -d.elemHeight : d.my[1] === "bottom" ? d.elemHeight : 0,
                    i = d.at[1] === "top" ? d.targetHeight : d.at[1] === "bottom" ? -d.targetHeight : 0,
                    j = -2 * d.offset[1],
                    n, k;
                if (g < 0) {
                    k = p.top + m + i + j + d.collisionHeight - o - c;
                    if ((p.top + m + i + j) > g && (k < 0 || k < Math.abs(g))) {
                        p.top += m + i + j
                    }
                } else if (h > 0) {
                    n = p.top - d.collisionPosition.marginTop + m + i + j - e;
                    if ((p.top + m + i + j) > h && (n > 0 || Math.abs(n) < h)) {
                        p.top += m + i + j
                    }
                }
            }
        },
        flipfit: {
            left: function() {
                a.flip.left.apply(this, arguments);
                a.fit.left.apply(this, arguments)
            },
            top: function() {
                a.flip.top.apply(this, arguments);
                a.fit.top.apply(this, arguments)
            }
        }
    };
    jQuery.ui.position._sapUiCommonsMenuFlip = {
        left: function(p, d) {
            if (jQuery.ui.position.flipfit) {
                jQuery.ui.position.flipfit.left.apply(this, arguments);
                return
            }
            d = _(d);
            a.flipfit.left.apply(this, arguments)
        },
        top: function(p, d) {
            if (jQuery.ui.position.flipfit) {
                jQuery.ui.position.flipfit.top.apply(this, arguments);
                return
            }
            d = _(d);
            a.flipfit.top.apply(this, arguments)
        }
    }
})(window);