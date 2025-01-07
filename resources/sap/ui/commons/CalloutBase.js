﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.CalloutBase");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.TooltipBase");
sap.ui.core.TooltipBase.extend("sap.ui.commons.CalloutBase", {
    metadata: {
        publicMethods: ["adjustPosition", "close", "setPosition"],
        library: "sap.ui.commons",
        events: {
            "open": {},
            "close": {},
            "beforeOpen": {
                allowPreventDefault: true
            },
            "opened": {},
            "closed": {}
        }
    }
});
sap.ui.commons.CalloutBase.M_EVENTS = {
    'open': 'open',
    'close': 'close',
    'beforeOpen': 'beforeOpen',
    'opened': 'opened',
    'closed': 'closed'
};

sap.ui.commons.CalloutBase.prototype.init = function() {
    this.oPopup = new sap.ui.core.Popup();
    this.oPopup.setShadow(true);
    this.oRb = sap.ui.getCore().getLibraryResourceBundle("sap.ui.commons");
    this.setPosition(sap.ui.core.Popup.Dock.BeginBottom, sap.ui.core.Popup.Dock.BeginTop);
    this.fAnyEventHandlerProxy = jQuery.proxy(this.onAnyEvent, this);
    var t = this;
    this.oPopup._applyPosition = function(p) {
        sap.ui.core.Popup.prototype._applyPosition.call(this, p);
        t.setTip()
    };
    this.oPopup.setFollowOf(true)
};

sap.ui.commons.CalloutBase.prototype.exit = function() {
    this.oPopup.close();
    this.oPopup.detachEvent("opened", this.handleOpened, this);
    this.oPopup.detachEvent("closed", this.handleClosed, this);
    this.oPopup.destroy();
    delete this.oPopup;
    delete this.oRb;
    jQuery.sap.unbindAnyEvent(this.fAnyEventHandlerProxy)
};

sap.ui.commons.CalloutBase.prototype._getPopup = function() {
    return this.oPopup
};

sap.ui.commons.CalloutBase.prototype.hasChild = function(d) {
    return d && !! (jQuery(d).closest(this.getDomRef()).length)
};

sap.ui.commons.CalloutBase.prototype.isPopupElement = function(d) {
    if (!d) {
        return false
    }
    if (this.hasChild(d)) {
        return true
    }
    var s = sap.ui.getCore().getStaticAreaRef();
    var t = parseInt(jQuery(d).closest(jQuery(s).children()).css("z-index"), 10);
    var a = parseInt(this.$().css("z-index"), 10);
    return t && a && t >= a
};

sap.ui.commons.CalloutBase.prototype.setTip = function() {
    if (!this.oPopup || !this.oPopup.isOpen()) {
        return
    }
    var $ = this._currentControl.$(),
        a = this.$(),
        b = jQuery.sap.byId(this.getId() + "-arrow"),
        c = a.offset(),
        d = $.offset(),
        s = true,
        e = {}, t = {
            l: c.left,
            r: c.left + a.outerWidth(),
            w: a.outerWidth(),
            t: c.top,
            b: c.top + a.outerHeight(),
            h: a.outerHeight()
        }, p = {
            l: d.left,
            r: d.left + $.outerWidth(),
            w: $.outerWidth(),
            t: d.top,
            b: d.top + $.outerHeight(),
            h: $.outerHeight()
        }, f = (a.outerWidth() - a.innerWidth()) / 2,
        g = b.outerWidth() * 1.4,
        h = b.outerWidth() / 5,
        i = h - f - 8,
        m = this.getMyPosition();
    if (t.r < p.l - i) {
        e.x = "right"
    } else if (t.l - i > p.r) {
        e.x = "left"
    }
    if (t.t > p.b - i) {
        e.y = "top"
    } else if (t.b < p.t + i) {
        e.y = "bottom"
    }
    if (e.x) {
        var P = 0;
        if (m.indexOf("top") > -1) {
            P = 20
        } else if (m.indexOf("bottom") > -1) {
            P = t.h - 20 - g
        } else {
            P = (t.h - g) / 2
        }
        var j = t.t + P + g / 2 + f;
        if ((j < p.t) || (j > p.b) || (p.t > t.t && p.b < t.b)) {
            P = (Math.max(t.t, p.t) + Math.min(t.b, p.b)) / 2 - t.t - g / 2
        }
        if ( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 8 && e.x == "left") {
            i = i - 8
        }
        b.css(e.x, i + "px");
        b.css("top", P);
        if (P < 0 || P > t.h - g) {
            s = false
        }
    }
    if (e.y) {
        var r = sap.ui.getCore().getConfiguration().getRTL();
        if (r) {
            m.replace("begin", "right").replace("end", "left")
        }
        var k = 0;
        if ((m.indexOf("begin") > -1) || (m.indexOf("left") > -1)) {
            k = 20
        } else if ((m.indexOf("right") > -1) || (m.indexOf("end") > -1)) {
            k = t.w - 20 - g
        } else {
            k = (t.w - g) / 2
        }
        var l = t.l + k + g / 2 + f;
        if ((l < p.l) || (l > p.r)) {
            k = (Math.max(t.l, p.l) + Math.min(t.r, p.r)) / 2 - t.l - g / 2
        }
        if ( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 8 && e.y == "top") {
            i = i - 8
        }
        b.css(e.y, i + "px");
        b.css("left", k + "px");
        if (k < 0 || k > t.w - g) {
            s = false
        }
    }
    if (e.x && e.y || !e.x && !e.y) {
        s = false
    }
    b.toggle(s)
};

sap.ui.commons.CalloutBase.prototype.adjustPosition = function() {
    function _() {
        if (this.oPopup) {
            var p = this._currentControl.getDomRef();
            this.oPopup.setPosition(this.getMyPosition(), this.getAtPosition(), p, this.getOffset(), this.getCollision())
        }
    }
    setTimeout(jQuery.proxy(_, this), 0)
};

sap.ui.commons.CalloutBase.prototype.focus = function() {
    if (this.oPopup.isOpen()) {
        var c = jQuery.sap.byId(this.getId() + "-cont");
        jQuery.sap.focus(c.firstFocusableDomRef() || c.get(0))
    }
};

sap.ui.commons.CalloutBase.prototype.openPopup = function(s) {
    if (this.oPopup.isOpen()) {
        return
    }
    if (sap.ui.core.TooltipBase.sOpenTimeout) {
        jQuery.sap.clearDelayedCall(sap.ui.core.TooltipBase.sOpenTimeout);
        sap.ui.core.TooltipBase.sOpenTimeout = undefined
    }
    if (!this.fireEvent("beforeOpen", {
        parent: this._currentControl
    }, true, false)) {
        if (!this.sCloseNowTimeout) {
            sap.ui.core.TooltipBase.sOpenTimeout = jQuery.sap.delayedCall(200, this, "openPopup", [this._currentControl])
        }
        return
    };
    this.oParentFocusInfo = s.getFocusInfo();
    this.oPopup.attachEvent("opened", this.handleOpened, this);
    sap.ui.core.TooltipBase.prototype.openPopup.call(this, s);
    this.adjustPosition();
    this.fireOpen({
        parent: this._currentControl
    })
};

sap.ui.commons.CalloutBase.prototype.close = function() {
    if (this.oPopup.isOpen() && !this.sCloseNowTimeout) {
        if (sap.ui.core.TooltipBase.sOpenTimeout) {
            jQuery.sap.clearDelayedCall(sap.ui.core.TooltipBase.sOpenTimeout);
            sap.ui.core.TooltipBase.sOpenTimeout = undefined
        }
        this.closePopup()
    }
};

sap.ui.commons.CalloutBase.prototype.closePopup = function() {
    var w = this._getPopup().isOpen();
    if (this.fAnyEventHandlerProxy) {
        jQuery.sap.unbindAnyEvent(this.onAnyEvent)
    }
    sap.ui.core.TooltipBase.prototype.closePopup.call(this);
    if (w && this._currentControl && this.bFocused) {
        this._currentControl.applyFocusInfo(this.oParentFocusInfo);
        this.bFocused = false
    }
    this.fireClose({})
};

sap.ui.commons.CalloutBase.prototype.handleClosed = function() {
    if (this.oPopup) {
        this.oPopup.detachEvent("closed", this.handleClosed, this);
        this.fireEvent(sap.ui.core.Popup.M_EVENTS.closed)
    }
};

sap.ui.commons.CalloutBase.prototype.onkeydown = function(e) {
    var c = e.ctrlKey && e.which == jQuery.sap.KeyCodes.I;
    var E = e.which == jQuery.sap.KeyCodes.ESCAPE;
    if (!c && !E) {
        if (jQuery(e.target).control(0) === this._currentControl) {
            this.close()
        }
        return
    }
    if (c) {
        if (this.oPopup.isOpen()) {
            return
        }
        this.bDoFocus = true
    }
    sap.ui.core.TooltipBase.prototype.onkeydown.call(this, e)
};

sap.ui.commons.CalloutBase.prototype.handleOpened = function() {
    this.oPopup.detachEvent("opened", this.handleOpened, this);
    if (this.bDoFocus) {
        this.focus();
        this.bDoFocus = false;
        this.bFocused = true
    }
    this.fireEvent(sap.ui.core.Popup.M_EVENTS.opened);
    jQuery.sap.bindAnyEvent(this.fAnyEventHandlerProxy)
};

sap.ui.commons.CalloutBase.prototype.onfocusin = function(e) {
    this.bFocused = true;
    var s = e.target;
    if (s.id === this.getId() + "-fhfe") {
        jQuery.sap.focus(jQuery.sap.byId(this.getId() + "-cont").lastFocusableDomRef())
    } else if (s.id === this.getId() + "-fhee") {
        jQuery.sap.focus(jQuery.sap.byId(this.getId() + "-cont").firstFocusableDomRef())
    }
};

sap.ui.commons.CalloutBase.prototype.onfocusout = function(e) {
    return
};

sap.ui.commons.CalloutBase.prototype.onmouseover = function(e) {
    if (this.oPopup.isOpen() && this.oPopup.getContent() == this) {
        if (this.sCloseNowTimeout) {
            jQuery.sap.clearDelayedCall(this.sCloseNowTimeout);
            this.sCloseNowTimeout = null
        }
        return
    } else {
        sap.ui.core.TooltipBase.prototype.onmouseover.call(this, e)
    }
};

sap.ui.commons.CalloutBase.prototype.onmouseout = function(e) {
    if (this.oPopup.isOpen() && this.isPopupElement(e.relatedTarget)) {
        return
    }
    sap.ui.core.TooltipBase.prototype.onmouseout.call(this, e)
};

sap.ui.commons.CalloutBase.prototype.onmousedown = function(e) {
    if (jQuery(e.target).control(0) === this._currentControl) {
        this.close()
    }
};

sap.ui.commons.CalloutBase.prototype.onAnyEvent = function(e) {
    if (!this.oPopup.isOpen() || e.type != "mouseover" || this.hasChild(e.target)) {
        return
    }
    var d = this.isPopupElement(e.target) || jQuery(e.target).control(0) === this._currentControl;
    if (!d && !this.sCloseNowTimeout && !sap.ui.core.TooltipBase.sOpenTimeout) {
        this.sCloseNowTimeout = jQuery.sap.delayedCall(400, this, "closePopup")
    }
    if (d && this.sCloseNowTimeout) {
        jQuery.sap.clearDelayedCall(this.sCloseNowTimeout);
        this.sCloseNowTimeout = null
    }
};

sap.ui.commons.CalloutBase.prototype.setPosition = function(m, a) {
    var b = m || sap.ui.core.Popup.Dock.BeginBottom;
    var c = a || sap.ui.core.Popup.Dock.BeginTop;
    var d = 0,
        e = 0,
        f = 0,
        g = 0,
        h = 5;
    if ((b.indexOf("begin") > -1) || (b.indexOf("left") > -1)) {
        d = -1
    } else if ((b.indexOf("right") > -1) || (b.indexOf("end") > -1)) {
        d = 1
    }
    if ((c.indexOf("begin") > -1) || (c.indexOf("left") > -1)) {
        f = -1
    } else if ((c.indexOf("right") > -1) || (c.indexOf("end") > -1)) {
        f = 1
    }
    if (b.indexOf("top") > -1) {
        e = -1
    } else if (b.indexOf("bottom") > -1) {
        e = 1
    }
    if (c.indexOf("top") > -1) {
        g = -1
    } else if (c.indexOf("bottom") > -1) {
        g = 1
    }
    var o = ((d - f) * d * f * h) + " " + ((e - g) * e * g * h);
    this.setMyPosition(b);
    this.setAtPosition(c);
    this.setOffset(o);
    return this
};