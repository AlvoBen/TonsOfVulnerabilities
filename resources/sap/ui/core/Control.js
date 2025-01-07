﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.Control");
jQuery.sap.require("sap.ui.core.Element");
sap.ui.core.Element.extend("sap.ui.core.Control", {
    metadata: {
        stereotype: "control",
        "abstract": true,
        publicMethods: ["placeAt", "attachBrowserEvent", "detachBrowserEvent"],
        library: "sap.ui.core",
        properties: {
            "busy": {
                type: "boolean",
                defaultValue: false
            },
            "busyIndicatorDelay": {
                type: "int",
                defaultValue: 1000
            }
        },
        aggregations: {},
        associations: {},
        events: {}
    },
    constructor: function(i, s) {
        this.bAllowTextSelection = true;
        sap.ui.core.Element.apply(this, arguments);
        this.bOutput = this.getDomRef() != null;
        if (this._sapUiCoreLocalBusy_initBusyIndicator) {
            this._sapUiCoreLocalBusy_initBusyIndicator()
        }
    },
    renderer: null
});

sap.ui.core.Control.prototype.clone = function() {
    var c = sap.ui.core.Element.prototype.clone.apply(this, arguments);
    if (this.aBindParameters) {
        for (var i = 0, l = this.aBindParameters.length; i < l; i++) {
            var p = this.aBindParameters[i];
            c.attachBrowserEvent(p.sEventType, p.fnHandler, p.oListener !== this ? p.oListener : undefined)
        }
    }
    c.bAllowTextSelection = this.bAllowTextSelection;
    return c
};

jQuery.sap.require("sap.ui.core.CustomStyleClassSupport");
sap.ui.core.CustomStyleClassSupport.apply(sap.ui.core.Control.prototype);

sap.ui.core.Control.prototype.isActive = function() {
    return jQuery.sap.domById(this.sId) != null
};

sap.ui.core.Control.prototype.invalidate = function(o) {
    var u;
    if (this.bOutput && (u = this.getUIArea())) {
        u.addInvalidatedControl(this)
    } else {
        var p = this.getParent();
        if (p && (this.bOutput || !(this.getVisible && this.getVisible() === false))) {
            p.invalidate(this)
        }
    }
};

sap.ui.core.Control.prototype.rerender = function() {
    sap.ui.core.UIArea.rerenderControl(this)
};

sap.ui.core.Control.prototype.allowTextSelection = function(a) {
    this.bAllowTextSelection = a;
    return this
};

sap.ui.core.Control.prototype.attachBrowserEvent = function(e, h, l) {
    if (e && (typeof(e) === "string")) {
        if (h && typeof(h) === "function") {
            if (!this.aBindParameters) {
                this.aBindParameters = []
            }
            l = l || this;
            var p = function() {
                h.apply(l, arguments)
            };
            this.aBindParameters.push({
                sEventType: e,
                fnHandler: h,
                oListener: l,
                fnProxy: p
            });
            this.$().bind(e, p)
        }
    }
    return this
};

sap.ui.core.Control.prototype.detachBrowserEvent = function(e, h, l) {
    if (e && (typeof(e) === "string")) {
        if (h && typeof(h) === "function") {
            var $ = this.$(),
                i, p;
            l = l || this;
            if (this.aBindParameters) {
                for (i = this.aBindParameters.length - 1; i >= 0; i--) {
                    p = this.aBindParameters[i];
                    if (p.sEventType === e && p.fnHandler === h && p.oListener === l) {
                        this.aBindParameters.splice(i, 1);
                        $.unbind(e, p.fnProxy)
                    }
                }
            }
        }
    }
    return this
};

sap.ui.core.Control.prototype.getRenderer = function() {
    return sap.ui.core.RenderManager.getRenderer(this)
};

sap.ui.core.Control.prototype.placeAt = function(r, p) {
    var c = sap.ui.getCore();
    if (c.isInitialized()) {
        var C = r;
        if (typeof C === "string") {
            C = c.byId(r)
        }
        var i = false;
        if (!(C instanceof sap.ui.core.Element)) {
            C = c.createUIArea(r);
            i = true
        }
        if (!C) {
            return
        }
        if (!i) {
            var o = C.getMetadata().getAllAggregations()["content"];
            var b = true;
            if (o) {
                if (!o.multiple || o.type != "sap.ui.core.Control") {
                    b = false
                }
            } else {
                if (!C.addContent || !C.insertContent || !C.removeAllContent) {
                    b = false
                }
            }
            if (!b) {
                jQuery.sap.log.warning("placeAt cannot be processed because container " + C + " does not have an aggregation 'content'.");
                return
            }
        }
        if (typeof p === "number") {
            C.insertContent(this, p)
        } else {
            p = p || "last";
            switch (p) {
                case "last":
                    C.addContent(this);
                    break;
                case "first":
                    C.insertContent(this, 0);
                    break;
                case "only":
                    C.removeAllContent();
                    C.addContent(this);
                    break;
                default:
                    jQuery.sap.log.warning("Position " + p + " is not supported for function placeAt.")
            }
        }
    } else {
        var t = this;
        c.attachInitEvent(function() {
            t.placeAt(r, p)
        })
    }
    return this
};

sap.ui.core.Control.prototype.onselectstart = function(b) {
    if (!this.bAllowTextSelection) {
        b.preventDefault();
        b.stopPropagation()
    }
};

sap.ui.core.Control.prototype.getIdForLabel = function() {
    return this.getId()
};

sap.ui.core.Control.prototype.destroy = function(s) {
    this._cleanupBusyIndicator();
    sap.ui.core.ResizeHandler.deregisterAllForControl(this.getId());
    sap.ui.core.Element.prototype.destroy.call(this, s)
};

(function() {
    var p = "focusin focusout keydown keypress keyup",
        b = {
            onAfterRendering: function() {
                if (this.getProperty("busy") === true && this.$()) {
                    a.apply(this)
                }
            }
        }, a = function() {
            var $ = this.$(),
                f = ["area", "base", "br", "col", "embed", "hr", "img", "input", "keygen", "link", "menuitem", "meta", "param", "source", "track", "wbr"];
            if (this._busyIndicatorDelayedCallId) {
                jQuery.sap.clearDelayedCall(this._busyIndicatorDelayedCallId);
                delete this._busyIndicatorDelayedCallId
            }
            var t = $.get(0) && $.get(0).tagName;
            if (t && jQuery.inArray(t.toLowerCase(), f) >= 0) {
                jQuery.sap.log.warning("Busy Indicator cannot be placed in elements with tag " + t);
                return
            }
            if ($.css('position') == 'static') {
                this._busyStoredPosition = 'static';
                $.css('position', 'relative')
            }
            var B = jQuery('<div class="sapUiLocalBusyIndicator"><div class="sapUiLocalBusyIndicatorAnimation"><div class="sapUiLocalBusyIndicatorBox"></div><div class="sapUiLocalBusyIndicatorBox"></div><div class="sapUiLocalBusyIndicatorBox"></div></div></div>');
            B.attr("id", this.getId() + "-busyIndicator");
            $.append(B);
            this._busyDelayedCallId = jQuery.sap.delayedCall(1200, this, A);
            h.apply(this, [true])
        }, h = function(B) {
            if (B) {
                var $ = this.$(),
                    t = $.find('[tabindex]'),
                    c = this;
                this._busyTabIndices = [];
                t.each(function(i, o) {
                    var r = jQuery(o),
                        T = r.attr('tabindex');
                    if (T < 0) return true;
                    c._busyTabIndices.push({
                        ref: r,
                        tabindex: T
                    });
                    r.attr('tabindex', -1);
                    r.bind(p, P)
                })
            } else {
                if (this._busyTabIndices) {
                    jQuery.each(this._busyTabIndices, function(i, o) {
                        o.ref.attr('tabindex', o.tabindex);
                        o.ref.unbind(p, P)
                    })
                }
                this._busyTabIndices = []
            }
        }, P = function(e) {
            e.preventDefault();
            e.stopImmediatePropagation()
        }, A = function() {
            var $ = this.$().children('.sapUiLocalBusyIndicator').children('.sapUiLocalBusyIndicatorAnimation');
            var t = this;
            t._busyAnimationTimer1 = setTimeout(function() {
                $.children(":eq(0)").addClass('active');
                $.children(":not(:eq(0))").removeClass('active');
                t._busyAnimationTimer2 = setTimeout(function() {
                    $.children(":eq(1)").addClass('active');
                    $.children(":not(:eq(1))").removeClass('active');
                    t._busyAnimationTimer3 = setTimeout(function() {
                        $.children(":eq(2)").addClass('active');
                        $.children(":not(:eq(2))").removeClass('active');
                        t._busyAnimationTimer4 = setTimeout(function() {
                            $.children().removeClass('active')
                        }, 150)
                    }, 150)
                }, 150)
            }, 150);
            this._busyDelayedCallId = jQuery.sap.delayedCall(1200, this, A)
        };
    sap.ui.core.Control.prototype.setBusy = function(B) {
        var $ = this.$();
        if (B == this.getProperty("busy")) {
            return
        }
        this.setProperty("busy", B, true);
        if (B) {
            this.addDelegate(b, false, this)
        } else {
            this.removeDelegate(b)
        }
        if (!this.getDomRef()) {
            return
        }
        if (B) {
            if (this.getBusyIndicatorDelay() <= 0) {
                a.apply(this)
            } else {
                this._busyIndicatorDelayedCallId = jQuery.sap.delayedCall(this.getBusyIndicatorDelay(), this, a)
            }
        } else {
            if (this._busyIndicatorDelayedCallId) {
                jQuery.sap.clearDelayedCall(this._busyIndicatorDelayedCallId);
                delete this._busyIndicatorDelayedCallId
            }
            jQuery.sap.byId(this.getId() + "-busyIndicator").remove();
            if (this._busyStoredPosition) {
                $.css('position', this._busyStoredPosition);
                delete this._busyStoredPosition
            }
            h.apply(this, [false]);
            if (this._busyDelayedCallId) {
                jQuery.sap.clearDelayedCall(this._busyDelayedCallId);
                delete this._busyDelayedCallId
            }
        }
    };
    sap.ui.core.Control.prototype.isBusy = function() {
        return this.getProperty("busy")
    };
    sap.ui.core.Control.prototype.setBusyIndicatorDelay = function(d) {
        this.setProperty("busyIndicatorDelay", d, true);
        return this
    };
    sap.ui.core.Control.prototype._cleanupBusyIndicator = function() {
        if (this._busyIndicatorDelayedCallId) {
            jQuery.sap.clearDelayedCall(this._busyIndicatorDelayedCallId);
            delete this._busyIndicatorDelayedCallId
        }
        if (this._busyDelayedCallId) {
            jQuery.sap.clearDelayedCall(this._busyDelayedCallId);
            delete this._busyDelayedCallId
        }
        if (this._busyAnimationTimer1) {
            clearTimeout(this._busyAnimationTimer1);
            delete this._busyAnimationTimer1
        }
        if (this._busyAnimationTimer2) {
            clearTimeout(this._busyAnimationTimer2);
            delete this._busyAnimationTimer2
        }
        if (this._busyAnimationTimer3) {
            clearTimeout(this._busyAnimationTimer3);
            delete this._busyAnimationTimer3
        }
        if (this._busyAnimationTimer4) {
            clearTimeout(this._busyAnimationTimer4);
            delete this._busyAnimationTimer4
        }
    }
})();