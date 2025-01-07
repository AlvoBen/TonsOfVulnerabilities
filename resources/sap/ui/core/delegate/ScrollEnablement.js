﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.delegate.ScrollEnablement");
jQuery.sap.require("sap.ui.base.Object");
(function($) {
    sap.ui.base.Object.extend("sap.ui.core.delegate.ScrollEnablement", {
        constructor: function(c, s, C) {
            sap.ui.base.Object.apply(this);
            this._oControl = c;
            this._oControl.addDelegate(this);
            this._sContentId = s;
            this._bHorizontal = !! C.horizontal;
            this._bVertical = !! C.vertical;
            this._scrollX = 0;
            this._scrollY = 0;
            this._scroller = null;
            this._scrollbarClass = C.scrollbarClass || false;
            this._bounce = C.bounce;
            a(this, C);
            if (this._init) {
                this._init.apply(this, arguments)
            }
        },
        setHorizontal: function(h) {
            this._bHorizontal = !! h;
            if (this._scroller) {
                if (this._zynga) {
                    this._scroller.options.scrollingX = this._bHorizontal
                } else {
                    this._scroller.hScroll = this._scroller.hScrollbar = this._bHorizontal;
                    this._scroller._scrollbar('h')
                }
            } else {
                var c = $.sap.byId(this._sContentId).parent();
                c.css("overflow-x", this._bHorizontal ? "auto" : "hidden")
            }
        },
        setVertical: function(v) {
            this._bVertical = !! v;
            if (this._scroller) {
                if (this._zynga) {
                    this._scroller.options.scrollingY = this._bVertical
                } else {
                    this._scroller.vScroll = this._scroller.vScrollbar = this._bVertical;
                    this._scroller._scrollbar('v')
                }
            } else {
                var c = $.sap.byId(this._sContentId).parent();
                c.css("overflow-y", this._bVertical ? "auto" : "hidden")
            }
        },
        getHorizontal: function() {
            return this._bHorizontal
        },
        getVertical: function() {
            return this._bVertical
        },
        setBounce: function(b) {
            this._bounce = !! b
        },
        setPullDown: function(c) {
            this._oPullDown = c;
            return this
        },
        setGrowingList: function(g, s) {
            this._oGrowingList = g;
            this._fnScrollLoadCallback = jQuery.proxy(s, g);
            return this
        },
        setIconTabBar: function(I, s, S) {
            this._oIconTabBar = I;
            this._fnScrollEndCallback = jQuery.proxy(s, I);
            this._fnScrollStartCallback = jQuery.proxy(S, I);
            return this
        },
        scrollTo: function(x, y, t) {
            this._scrollX = x;
            this._scrollY = y;
            if (this._scroller) {
                if (this._zynga) {
                    if (!isNaN(t)) {
                        this._scroller.options.animationDuration = t
                    }
                    this._scroller.scrollTo(x, y, !! t)
                } else {
                    this._scrollTo(x, y, t)
                }
            } else {
                this._scrollTo(x, y, t)
            }
            return this
        },
        destroy: function() {
            if (this._exit) {
                this._exit()
            }
            if (this._oControl) {
                this._oControl.removeDelegate(this);
                this._oControl = undefined
            }
        },
        refresh: function() {
            if (this._refresh) {
                this._refresh()
            }
        }
    });
    var i = {
        getScrollTop: function() {
            return this._scrollY
        },
        getScrollLeft: function() {
            return this._scrollX
        },
        getMaxScrollTop: function() {
            return -this._scroller.maxScrollY
        },
        _scrollTo: function(x, y, t) {
            this._scroller.scrollTo(-x, -y, t, false)
        },
        _refresh: function() {
            if (this._scroller && this._sScrollerId) {
                var s = $.sap.domById(this._sScrollerId);
                if (s && (s.offsetHeight > 0)) {
                    this._bIgnoreScrollEnd = true;
                    this._scroller.refresh();
                    this._bIgnoreScrollEnd = false;
                    if (-this._scrollX != this._scroller.x || -this._scrollY != this._scroller.y) {
                        this._scroller.scrollTo(-this._scrollX, -this._scrollY, 0)
                    }
                    if (this._scroller.wrapper && this._scroller.wrapper.scrollTop) {
                        this._scroller.wrapper.scrollTop = 0
                    }
                }
            }
        },
        _cleanup: function() {
            this._toggleResizeListeners(false);
            if (this._scroller) {
                this._scroller.stop();
                this._scrollX = -this._scroller.x;
                var s = $.sap.domById(this._sScrollerId);
                if (s && (s.offsetHeight > 0)) {
                    this._scrollY = -this._scroller.y
                }
                this._scroller.destroy();
                this._scroller = null
            }
        },
        _toggleResizeListeners: function(t) {
            if (this._sScrollerResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sScrollerResizeListenerId);
                this._sScrollerResizeListenerId = null
            }
            if (this._sContentResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sContentResizeListenerId);
                this._sContentResizeListenerId = null
            }
            if (t && this._sContentId && $.sap.domById(this._sContentId)) {
                var b = $.proxy(this._refresh, this);
                this._sScrollerResizeListenerId = sap.ui.core.ResizeHandler.register($.sap.domById(this._sScrollerId), b);
                this._sContentResizeListenerId = sap.ui.core.ResizeHandler.register($.sap.domById(this._sContentId), b)
            }
        },
        onBeforeRendering: function() {
            this._cleanup()
        },
        onfocusin: function(e) {
            if (sap.ui.core.delegate.ScrollEnablement._bScrollToInput && sap.ui.Device.os.android) {
                var b = e.srcElement;
                this._sTimerId && jQuery.sap.clearDelayedCall(this._sTimerId);
                if (b && b.nodeName && (b.nodeName.toUpperCase() === "INPUT" || b.nodeName.toUpperCase() === "TEXTAREA")) {
                    this._sTimerId = jQuery.sap.delayedCall(400, this, function() {
                        var o = this._scroller._offset(b);
                        o.top += 48;
                        this._scroller.scrollTo(o.left, o.top)
                    })
                }
            }
        },
        onAfterRendering: function() {
            var t = this,
                b = (this._bounce !== undefined) ? this._bounce : sap.ui.Device.os.ios;
            var c = $.sap.byId(this._sContentId);
            this._sScrollerId = c.parent().attr("id");
            var d = ( !! sap.ui.Device.os.android && !sap.ui.Device.browser.chrome && (sap.ui.Device.os.version == 4 || !sap.ui.Device.os.versionStr.indexOf("2.3.4")) && c.find("input,textarea").length);
            this._iTopOffset = this._oPullDown && this._oPullDown.getDomRef && this._oPullDown.getDomRef().offsetHeight || 0;
            var x = this._scrollX || 0,
                y = this._scrollY || 0;
            this._scroller = new window.iScroll(this._sScrollerId, {
                useTransition: true,
                useTransform: !d,
                hideScrollbar: true,
                fadeScrollbar: true,
                bounce: !! b,
                momentum: true,
                handleClick: false,
                hScroll: this._bHorizontal,
                vScroll: this._bVertical,
                x: -x,
                y: -y,
                topOffset: this._iTopOffset,
                scrollbarClass: this._scrollbarClass,
                onBeforeScrollStart: function(e) {
                    if (t._isScrolling) {
                        e.stopPropagation();
                        e.preventDefault()
                    }
                },
                onScrollEnd: function() {
                    if (!t._bIgnoreScrollEnd && t._scroller) {
                        t._scrollX = -t._scroller.x;
                        t._scrollY = -t._scroller.y
                    }
                    if (t._oPullDown) {
                        t._oPullDown.doScrollEnd()
                    }
                    if (t._oGrowingList && t._fnScrollLoadCallback) {
                        var e = Math.floor(this.wrapperH / 4);
                        var I = -this.maxScrollY + this.y < e;
                        if (this.dirY > 0 && I) {
                            t._fnScrollLoadCallback()
                        }
                    }
                    if (t._oIconTabBar && t._fnScrollEndCallback) {
                        t._fnScrollEndCallback()
                    }
                    t._isScrolling = false
                },
                onRefresh: function() {
                    if (t._oPullDown) {
                        t._oPullDown.doRefresh()
                    }
                    t._toggleResizeListeners(true)
                },
                onScrollMove: function(e) {
                    if (!t._isScrolling) {
                        var r = /(INPUT|TEXTAREA)/i,
                            A = document.activeElement;
                        if (r.test(A.tagName) && e.target !== A) {
                            A.blur()
                        }
                    }
                    t._isScrolling = true;
                    if (t._oPullDown) {
                        t._oPullDown.doScrollMove()
                    }
                    if (t._oIconTabBar && t._fnScrollStartCallback) {
                        t._fnScrollStartCallback()
                    }
                }
            });
            for (var p = this._oControl; p = p.oParent;) {
                var s = p.getScrollDelegate ? p.getScrollDelegate() : null;
                if (s && (s.getVertical() && this.getVertical() || s.getHorizontal() && this.getHorizontal())) {
                    this._scroller._sapui_isNested = true;
                    break
                }
            }
            this._scroller._move = function(e) {
                if (e._sapui_handledByControl) {
                    return
                }
                if (this._sapui_isNested) {
                    e._sapui_handledByControl = !(this.dirY < 0 && this.y >= 0) && !(this.dirY > 0 && this.y <= this.maxScrollY) && !(this.dirX < 0 && this.x >= 0) && !(this.dirX > 0 && this.x <= this.maxScrollX)
                }
                window.iScroll.prototype._move.call(this, e)
            };
            var S = c.parent()[0];
            if (S && (S.offsetHeight > 0)) {
                if (this._scrollX != -this._scroller.x || this._scrollY != -this._scroller.y) {
                    this._scroller.scrollTo(-this._scrollX, -this._scrollY, 0)
                }
            }
            this._toggleResizeListeners(true)
        },
        ontouchmove: function(e) {
            if (this._preventTouchMoveDefault) {
                e.preventDefault()
            }
        }
    };
    var z = {
        _refresh: function() {
            if (this._scroller && this._sContentId && $.sap.domById(this._sContentId)) {
                var c = $.sap.byId(this._sContentId);
                var C = c.parent();
                this._scroller.setDimensions(C.width(), C.height(), c.width(), c.height())
            }
        },
        _cleanup: function() {
            if (this._sScrollerResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sScrollerResizeListenerId);
                this._sScrollerResizeListenerId = null
            }
            if (this._sContentResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sContentResizeListenerId);
                this._sContentResizeListenerId = null
            }
            if (this._scroller) {
                var v = this._scroller.getValues();
                this._scrollX = v.left;
                this._scrollY = v.top
            }
        },
        onBeforeRendering: function() {
            this._cleanup()
        },
        onAfterRendering: function() {
            this._refresh();
            this._scroller.scrollTo(this._scrollX, this._scrollY, false);
            this._sContentResizeListenerId = sap.ui.core.ResizeHandler.register($.sap.domById(this._sContentId), $.proxy(function() {
                if ((!this._sContentId || !$.sap.domById(this._sContentId)) && this._sContentResizeListenerId) {
                    sap.ui.core.ResizeHandler.deregister(this._sContentResizeListenerId);
                    this._sContentResizeListenerId = null
                } else {
                    this._refresh()
                }
            }, this))
        },
        ontouchstart: function(e) {
            if (e.target.tagName.match(/input|textarea|select/i)) {
                return
            }
            this._scroller.doTouchStart(e.touches, e.timeStamp)
        },
        ontouchend: function(e) {
            this._scroller.doTouchEnd(e.timeStamp)
        },
        ontouchmove: function(e) {
            this._scroller.doTouchMove(e.touches, e.timeStamp);
            if (this._preventTouchMoveDefault) {
                e.preventDefault()
            } else {
                e.stopPropagation()
            }
        }
    };
    var n = {
        getScrollTop: function() {
            return this._scrollY || 0
        },
        getScrollLeft: function() {
            return this._scrollX || 0
        },
        getMaxScrollTop: function() {
            var c = $.sap.byId(this._sContentId).parent();
            if (!c.length) {
                return -1
            }
            return c[0].scrollHeight - c.height()
        },
        onBeforeRendering: function() {
            if (this._sResizeListenerId) {
                sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
                this._sResizeListenerId = null
            }
            var c = $.sap.byId(this._sContentId).parent();
            if (c.length > 0) {
                if (c.height() > 0) {
                    this._scrollX = c.scrollLeft();
                    this._scrollY = c.scrollTop()
                }
                c.unbind("scroll", this._onScroll)
            }
        },
        _onScroll: function(e) {
            var b = $(this);
            var c = b.height();
            var t = e.data.that;
            if (t._fnScrollLoadCallback && this.scrollHeight - b.scrollTop() - c < 100) {
                t._fnScrollLoadCallback()
            }
            e.data.that._scrollX = b.scrollLeft();
            e.data.that._scrollY = b.scrollTop()
        },
        onAfterRendering: function() {
            var c = $.sap.byId(this._sContentId).parent();
            c.css("overflow-x", this._bHorizontal ? "auto" : "hidden").css("overflow-y", this._bVertical ? "auto" : "hidden");
            c.scrollLeft(this._scrollX);
            var h = (c.height() > 0);
            if (h) {
                c.scrollTop(this._scrollY)
            }
            if (!h || !! sap.ui.Device.browser.internet_explorer) {
                var t = this;
                this._sResizeListenerId = sap.ui.core.ResizeHandler.register(c[0], function() {
                    var c = $.sap.byId(t._sContentId).parent();
                    if (c.height() > 0) {
                        c.scrollTop(t._scrollY);
                        t._readActualScrollPosition.apply(t);
                        if ( !! !sap.ui.Device.browser.internet_explorer) {
                            sap.ui.core.ResizeHandler.deregister(t._sResizeListenerId);
                            this._sResizeListenerId = null
                        }
                    }
                })
            }
            c.scroll({
                that: this
            }, this._onScroll)
        },
        _readActualScrollPosition: function() {
            var c = $.sap.byId(this._sContentId).parent();
            if (c.width() > 0) {
                this._scrollX = c.scrollLeft()
            }
            if (c.height() > 0) {
                this._scrollY = c.scrollTop()
            }
        },
        _scrollTo: function(x, y, t) {
            var c = $.sap.byId(this._sContentId).parent();
            if (c.length > 0) {
                if (t > 0) {
                    c.animate({
                        scrollTop: y,
                        scrollLeft: x
                    }, t, jQuery.proxy(this._readActualScrollPosition, this))
                } else {
                    c.scrollTop(y);
                    c.scrollLeft(x);
                    this._readActualScrollPosition()
                }
            }
        }
    };

    function a(s, c) {
        var d;
        if (!$.support.touch && !$.sap.simulateMobileOnDesktop && !c.nonTouchScrolling) {
            d = {}
        } else if (!$.support.touch && !$.sap.simulateMobileOnDesktop && c.nonTouchScrolling === "scrollbar") {
            d = {
                _init: function(C, S, c) {
                    $.extend(this, n)
                }
            }
        } else {
            $.sap.require("jquery.sap.mobile");
            d = {
                _init: function(C, S, c) {
                    function b(e, h, v) {
                        var o = new window.Scroller(function(f, t, g) {
                            var j = $.sap.byId(e).parent();
                            j.scrollLeft(f);
                            j.scrollTop(t)
                        }, {
                            scrollingX: h,
                            scrollingY: v,
                            bouncing: false
                        });
                        return o
                    }
                    var l = c.zynga ? "z" : "i";
                    this._preventTouchMoveDefault = !! c.preventDefault;
                    this._scroller = null;
                    switch (l) {
                        case "z":
                            $.sap.require("sap.ui.thirdparty.zyngascroll");
                            $.extend(this, z);
                            this._zynga = true;
                            this._scroller = b(this._sContentId, this._bHorizontal, this._bVertical);
                            break;
                        default:
                            $.sap.require("sap.ui.thirdparty.iscroll");
                            $.extend(this, i);
                            break
                    }
                },
                _exit: function() {
                    if (this._cleanup) {
                        this._cleanup()
                    }
                    this._scroller = null
                }
            }
        }
        $.extend(s, d)
    }
}(jQuery));