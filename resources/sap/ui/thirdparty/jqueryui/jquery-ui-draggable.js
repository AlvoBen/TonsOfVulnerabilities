﻿/*!
 * jQuery UI Draggable 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Draggables
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.mouse.js
 *	jquery.ui.widget.js
 */

(function($, u) {
    $.widget("ui.draggable", $.ui.mouse, {
        widgetEventPrefix: "drag",
        options: {
            addClasses: true,
            appendTo: "parent",
            axis: false,
            connectToSortable: false,
            containment: false,
            cursor: "auto",
            cursorAt: false,
            grid: false,
            handle: false,
            helper: "original",
            iframeFix: false,
            opacity: false,
            refreshPositions: false,
            revert: false,
            revertDuration: 500,
            scope: "default",
            scroll: true,
            scrollSensitivity: 20,
            scrollSpeed: 20,
            snap: false,
            snapMode: "both",
            snapTolerance: 20,
            stack: false,
            zIndex: false
        },
        _create: function() {
            if (this.options.helper == 'original' && !(/^(?:r|a|f)/).test(this.element.css("position"))) this.element[0].style.position = 'relative';
            (this.options.addClasses && this.element.addClass("ui-draggable"));
            (this.options.disabled && this.element.addClass("ui-draggable-disabled"));
            this._mouseInit()
        },
        destroy: function() {
            if (!this.element.data('draggable')) return;
            this.element.removeData("draggable").unbind(".draggable").removeClass("ui-draggable" + " ui-draggable-dragging" + " ui-draggable-disabled");
            this._mouseDestroy();
            return this
        },
        _mouseCapture: function(e) {
            var o = this.options;
            if (this.helper || o.disabled || $(e.target).is('.ui-resizable-handle')) return false;
            this.handle = this._getHandle(e);
            if (!this.handle) return false;
            if (o.iframeFix) {
                $(o.iframeFix === true ? "iframe" : o.iframeFix).each(function() {
                    $('<div class="ui-draggable-iframeFix" style="background: #fff;"></div>').css({
                        width: this.offsetWidth + "px",
                        height: this.offsetHeight + "px",
                        position: "absolute",
                        opacity: "0.001",
                        zIndex: 1000
                    }).css($(this).offset()).appendTo("body")
                })
            }
            return true
        },
        _mouseStart: function(e) {
            var o = this.options;
            this.helper = this._createHelper(e);
            this.helper.addClass("ui-draggable-dragging");
            this._cacheHelperProportions();
            if ($.ui.ddmanager) $.ui.ddmanager.current = this;
            this._cacheMargins();
            this.cssPosition = this.helper.css("position");
            this.scrollParent = this.helper.scrollParent();
            this.offset = this.positionAbs = this.element.offset();
            this.offset = {
                top: this.offset.top - this.margins.top,
                left: this.offset.left - this.margins.left
            };
            $.extend(this.offset, {
                click: {
                    left: e.pageX - this.offset.left,
                    top: e.pageY - this.offset.top
                },
                parent: this._getParentOffset(),
                relative: this._getRelativeOffset()
            });
            this.originalPosition = this.position = this._generatePosition(e);
            this.originalPageX = e.pageX;
            this.originalPageY = e.pageY;
            (o.cursorAt && this._adjustOffsetFromHelper(o.cursorAt));
            if (o.containment) this._setContainment();
            if (this._trigger("start", e) === false) {
                this._clear();
                return false
            }
            this._cacheHelperProportions();
            if ($.ui.ddmanager && !o.dropBehaviour) $.ui.ddmanager.prepareOffsets(this, e);
            this._mouseDrag(e, true);
            if ($.ui.ddmanager) $.ui.ddmanager.dragStart(this, e);
            return true
        },
        _mouseDrag: function(e, n) {
            this.position = this._generatePosition(e);
            this.positionAbs = this._convertPositionTo("absolute");
            if (!n) {
                var a = this._uiHash();
                if (this._trigger('drag', e, a) === false) {
                    this._mouseUp({});
                    return false
                }
                this.position = a.position
            }
            if (!this.options.axis || this.options.axis != "y") this.helper[0].style.left = this.position.left + 'px';
            if (!this.options.axis || this.options.axis != "x") this.helper[0].style.top = this.position.top + 'px';
            if ($.ui.ddmanager) $.ui.ddmanager.drag(this, e);
            return false
        },
        _mouseStop: function(e) {
            var d = false;
            if ($.ui.ddmanager && !this.options.dropBehaviour) d = $.ui.ddmanager.drop(this, e);
            if (this.dropped) {
                d = this.dropped;
                this.dropped = false
            }
            var a = this.element[0],
                b = false;
            while (a && (a = a.parentNode)) {
                if (a == document) {
                    b = true
                }
            }
            if (!b && this.options.helper === "original") return false;
            if ((this.options.revert == "invalid" && !d) || (this.options.revert == "valid" && d) || this.options.revert === true || ($.isFunction(this.options.revert) && this.options.revert.call(this.element, d))) {
                var s = this;
                $(this.helper).animate(this.originalPosition, parseInt(this.options.revertDuration, 10), function() {
                    if (s._trigger("stop", e) !== false) {
                        s._clear()
                    }
                })
            } else {
                if (this._trigger("stop", e) !== false) {
                    this._clear()
                }
            }
            return false
        },
        _mouseUp: function(e) {
            if (this.options.iframeFix === true) {
                $("div.ui-draggable-iframeFix").each(function() {
                    this.parentNode.removeChild(this)
                })
            }
            if ($.ui.ddmanager) $.ui.ddmanager.dragStop(this, e);
            return $.ui.mouse.prototype._mouseUp.call(this, e)
        },
        cancel: function() {
            if (this.helper.is(".ui-draggable-dragging")) {
                this._mouseUp({})
            } else {
                this._clear()
            }
            return this
        },
        _getHandle: function(e) {
            var h = !this.options.handle || !$(this.options.handle, this.element).length ? true : false;
            $(this.options.handle, this.element).find("*").andSelf().each(function() {
                if (this == e.target) h = true
            });
            return h
        },
        _createHelper: function(e) {
            var o = this.options;
            var h = $.isFunction(o.helper) ? $(o.helper.apply(this.element[0], [e])) : (o.helper == 'clone' ? this.element.clone().removeAttr('id') : this.element);
            if (!h.parents('body').length) h.appendTo((o.appendTo == 'parent' ? this.element[0].parentNode : o.appendTo));
            if (h[0] != this.element[0] && !(/(fixed|absolute)/).test(h.css("position"))) h.css("position", "absolute");
            return h
        },
        _adjustOffsetFromHelper: function(o) {
            if (typeof o == 'string') {
                o = o.split(' ')
            }
            if ($.isArray(o)) {
                o = {
                    left: +o[0],
                    top: +o[1] || 0
                }
            }
            if ('left' in o) {
                this.offset.click.left = o.left + this.margins.left
            }
            if ('right' in o) {
                this.offset.click.left = this.helperProportions.width - o.right + this.margins.left
            }
            if ('top' in o) {
                this.offset.click.top = o.top + this.margins.top
            }
            if ('bottom' in o) {
                this.offset.click.top = this.helperProportions.height - o.bottom + this.margins.top
            }
        },
        _getParentOffset: function() {
            this.offsetParent = this.helper.offsetParent();
            var p = this.offsetParent.offset();
            if (this.cssPosition == 'absolute' && this.scrollParent[0] != document && $.ui.contains(this.scrollParent[0], this.offsetParent[0])) {
                p.left += this.scrollParent.scrollLeft();
                p.top += this.scrollParent.scrollTop()
            }
            if ((this.offsetParent[0] == document.body) || (this.offsetParent[0].tagName && this.offsetParent[0].tagName.toLowerCase() == 'html' && $.browser.msie)) p = {
                top: 0,
                left: 0
            };
            return {
                top: p.top + (parseInt(this.offsetParent.css("borderTopWidth"), 10) || 0),
                left: p.left + (parseInt(this.offsetParent.css("borderLeftWidth"), 10) || 0)
            }
        },
        _getRelativeOffset: function() {
            if (this.cssPosition == "relative") {
                var p = this.element.position();
                return {
                    top: p.top - (parseInt(this.helper.css("top"), 10) || 0) + this.scrollParent.scrollTop(),
                    left: p.left - (parseInt(this.helper.css("left"), 10) || 0) + this.scrollParent.scrollLeft()
                }
            } else {
                return {
                    top: 0,
                    left: 0
                }
            }
        },
        _cacheMargins: function() {
            this.margins = {
                left: (parseInt(this.element.css("marginLeft"), 10) || 0),
                top: (parseInt(this.element.css("marginTop"), 10) || 0),
                right: (parseInt(this.element.css("marginRight"), 10) || 0),
                bottom: (parseInt(this.element.css("marginBottom"), 10) || 0)
            }
        },
        _cacheHelperProportions: function() {
            this.helperProportions = {
                width: this.helper.outerWidth(),
                height: this.helper.outerHeight()
            }
        },
        _setContainment: function() {
            var o = this.options;
            if (o.containment == 'parent') o.containment = this.helper[0].parentNode;
            if (o.containment == 'document' || o.containment == 'window') this.containment = [o.containment == 'document' ? 0 : $(window).scrollLeft() - this.offset.relative.left - this.offset.parent.left, o.containment == 'document' ? 0 : $(window).scrollTop() - this.offset.relative.top - this.offset.parent.top, (o.containment == 'document' ? 0 : $(window).scrollLeft()) + $(o.containment == 'document' ? document : window).width() - this.helperProportions.width - this.margins.left, (o.containment == 'document' ? 0 : $(window).scrollTop()) + ($(o.containment == 'document' ? document : window).height() || document.body.parentNode.scrollHeight) - this.helperProportions.height - this.margins.top];
            if (!(/^(document|window|parent)$/).test(o.containment) && o.containment.constructor != Array) {
                var c = $(o.containment);
                var a = c[0];
                if (!a) return;
                var b = c.offset();
                var d = ($(a).css("overflow") != 'hidden');
                this.containment = [(parseInt($(a).css("borderLeftWidth"), 10) || 0) + (parseInt($(a).css("paddingLeft"), 10) || 0), (parseInt($(a).css("borderTopWidth"), 10) || 0) + (parseInt($(a).css("paddingTop"), 10) || 0), (d ? Math.max(a.scrollWidth, a.offsetWidth) : a.offsetWidth) - (parseInt($(a).css("borderLeftWidth"), 10) || 0) - (parseInt($(a).css("paddingRight"), 10) || 0) - this.helperProportions.width - this.margins.left - this.margins.right, (d ? Math.max(a.scrollHeight, a.offsetHeight) : a.offsetHeight) - (parseInt($(a).css("borderTopWidth"), 10) || 0) - (parseInt($(a).css("paddingBottom"), 10) || 0) - this.helperProportions.height - this.margins.top - this.margins.bottom];
                this.relative_container = c
            } else if (o.containment.constructor == Array) {
                this.containment = o.containment
            }
        },
        _convertPositionTo: function(d, p) {
            if (!p) p = this.position;
            var m = d == "absolute" ? 1 : -1;
            var o = this.options,
                s = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                a = (/(html|body)/i).test(s[0].tagName);
            return {
                top: (p.top + this.offset.relative.top * m + this.offset.parent.top * m - ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : (a ? 0 : s.scrollTop())) * m)),
                left: (p.left + this.offset.relative.left * m + this.offset.parent.left * m - ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : a ? 0 : s.scrollLeft()) * m))
            }
        },
        _generatePosition: function(e) {
            var o = this.options,
                s = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                a = (/(html|body)/i).test(s[0].tagName);
            var p = e.pageX;
            var b = e.pageY;
            if (this.originalPosition) {
                var c;
                if (this.containment) {
                    if (this.relative_container) {
                        var d = this.relative_container.offset();
                        c = [this.containment[0] + d.left, this.containment[1] + d.top, this.containment[2] + d.left, this.containment[3] + d.top]
                    } else {
                        c = this.containment
                    }
                    if (e.pageX - this.offset.click.left < c[0]) p = c[0] + this.offset.click.left;
                    if (e.pageY - this.offset.click.top < c[1]) b = c[1] + this.offset.click.top;
                    if (e.pageX - this.offset.click.left > c[2]) p = c[2] + this.offset.click.left;
                    if (e.pageY - this.offset.click.top > c[3]) b = c[3] + this.offset.click.top
                }
                if (o.grid) {
                    var t = o.grid[1] ? this.originalPageY + Math.round((b - this.originalPageY) / o.grid[1]) * o.grid[1] : this.originalPageY;
                    b = c ? (!(t - this.offset.click.top < c[1] || t - this.offset.click.top > c[3]) ? t : (!(t - this.offset.click.top < c[1]) ? t - o.grid[1] : t + o.grid[1])) : t;
                    var l = o.grid[0] ? this.originalPageX + Math.round((p - this.originalPageX) / o.grid[0]) * o.grid[0] : this.originalPageX;
                    p = c ? (!(l - this.offset.click.left < c[0] || l - this.offset.click.left > c[2]) ? l : (!(l - this.offset.click.left < c[0]) ? l - o.grid[0] : l + o.grid[0])) : l
                }
            }
            return {
                top: (b - this.offset.click.top - this.offset.relative.top - this.offset.parent.top + ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : (a ? 0 : s.scrollTop())))),
                left: (p - this.offset.click.left - this.offset.relative.left - this.offset.parent.left + ($.browser.safari && $.browser.version < 526 && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : a ? 0 : s.scrollLeft())))
            }
        },
        _clear: function() {
            this.helper.removeClass("ui-draggable-dragging");
            if (this.helper[0] != this.element[0] && !this.cancelHelperRemoval) this.helper.remove();
            this.helper = null;
            this.cancelHelperRemoval = false
        },
        _trigger: function(t, e, a) {
            a = a || this._uiHash();
            $.ui.plugin.call(this, t, [e, a]);
            if (t == "drag") this.positionAbs = this._convertPositionTo("absolute");
            return $.Widget.prototype._trigger.call(this, t, e, a)
        },
        plugins: {},
        _uiHash: function(e) {
            return {
                helper: this.helper,
                position: this.position,
                originalPosition: this.originalPosition,
                offset: this.positionAbs
            }
        }
    });
    $.extend($.ui.draggable, {
        version: "1.8.23"
    });
    $.ui.plugin.add("draggable", "connectToSortable", {
        start: function(e, a) {
            var i = $(this).data("draggable"),
                o = i.options,
                b = $.extend({}, a, {
                    item: i.element
                });
            i.sortables = [];
            $(o.connectToSortable).each(function() {
                var s = $.data(this, 'sortable');
                if (s && !s.options.disabled) {
                    i.sortables.push({
                        instance: s,
                        shouldRevert: s.options.revert
                    });
                    s.refreshPositions();
                    s._trigger("activate", e, b)
                }
            })
        },
        stop: function(e, a) {
            var i = $(this).data("draggable"),
                b = $.extend({}, a, {
                    item: i.element
                });
            $.each(i.sortables, function() {
                if (this.instance.isOver) {
                    this.instance.isOver = 0;
                    i.cancelHelperRemoval = true;
                    this.instance.cancelHelperRemoval = false;
                    if (this.shouldRevert) this.instance.options.revert = true;
                    this.instance._mouseStop(e);
                    this.instance.options.helper = this.instance.options._helper;
                    if (i.options.helper == 'original') this.instance.currentItem.css({
                        top: 'auto',
                        left: 'auto'
                    })
                } else {
                    this.instance.cancelHelperRemoval = false;
                    this.instance._trigger("deactivate", e, b)
                }
            })
        },
        drag: function(e, a) {
            var b = $(this).data("draggable"),
                s = this;
            var c = function(o) {
                var d = this.offset.click.top,
                    f = this.offset.click.left;
                var h = this.positionAbs.top,
                    g = this.positionAbs.left;
                var i = o.height,
                    j = o.width;
                var k = o.top,
                    l = o.left;
                return $.ui.isOver(h + d, g + f, k, l, i, j)
            };
            $.each(b.sortables, function(i) {
                this.instance.positionAbs = b.positionAbs;
                this.instance.helperProportions = b.helperProportions;
                this.instance.offset.click = b.offset.click;
                if (this.instance._intersectsWith(this.instance.containerCache)) {
                    if (!this.instance.isOver) {
                        this.instance.isOver = 1;
                        this.instance.currentItem = $(s).clone().removeAttr('id').appendTo(this.instance.element).data("sortable-item", true);
                        this.instance.options._helper = this.instance.options.helper;
                        this.instance.options.helper = function() {
                            return a.helper[0]
                        };
                        e.target = this.instance.currentItem[0];
                        this.instance._mouseCapture(e, true);
                        this.instance._mouseStart(e, true, true);
                        this.instance.offset.click.top = b.offset.click.top;
                        this.instance.offset.click.left = b.offset.click.left;
                        this.instance.offset.parent.left -= b.offset.parent.left - this.instance.offset.parent.left;
                        this.instance.offset.parent.top -= b.offset.parent.top - this.instance.offset.parent.top;
                        b._trigger("toSortable", e);
                        b.dropped = this.instance.element;
                        b.currentItem = b.element;
                        this.instance.fromOutside = b
                    }
                    if (this.instance.currentItem) this.instance._mouseDrag(e)
                } else {
                    if (this.instance.isOver) {
                        this.instance.isOver = 0;
                        this.instance.cancelHelperRemoval = true;
                        this.instance.options.revert = false;
                        this.instance._trigger('out', e, this.instance._uiHash(this.instance));
                        this.instance._mouseStop(e, true);
                        this.instance.options.helper = this.instance.options._helper;
                        this.instance.currentItem.remove();
                        if (this.instance.placeholder) this.instance.placeholder.remove();
                        b._trigger("fromSortable", e);
                        b.dropped = false
                    }
                }
            })
        }
    });
    $.ui.plugin.add("draggable", "cursor", {
        start: function(e, a) {
            var t = $('body'),
                o = $(this).data('draggable').options;
            if (t.css("cursor")) o._cursor = t.css("cursor");
            t.css("cursor", o.cursor)
        },
        stop: function(e, a) {
            var o = $(this).data('draggable').options;
            if (o._cursor) $('body').css("cursor", o._cursor)
        }
    });
    $.ui.plugin.add("draggable", "opacity", {
        start: function(e, a) {
            var t = $(a.helper),
                o = $(this).data('draggable').options;
            if (t.css("opacity")) o._opacity = t.css("opacity");
            t.css('opacity', o.opacity)
        },
        stop: function(e, a) {
            var o = $(this).data('draggable').options;
            if (o._opacity) $(a.helper).css('opacity', o._opacity)
        }
    });
    $.ui.plugin.add("draggable", "scroll", {
        start: function(e, a) {
            var i = $(this).data("draggable");
            if (i.scrollParent[0] != document && i.scrollParent[0].tagName != 'HTML') i.overflowOffset = i.scrollParent.offset()
        },
        drag: function(e, a) {
            var i = $(this).data("draggable"),
                o = i.options,
                s = false;
            if (i.scrollParent[0] != document && i.scrollParent[0].tagName != 'HTML') {
                if (!o.axis || o.axis != 'x') {
                    if ((i.overflowOffset.top + i.scrollParent[0].offsetHeight) - e.pageY < o.scrollSensitivity) i.scrollParent[0].scrollTop = s = i.scrollParent[0].scrollTop + o.scrollSpeed;
                    else if (e.pageY - i.overflowOffset.top < o.scrollSensitivity) i.scrollParent[0].scrollTop = s = i.scrollParent[0].scrollTop - o.scrollSpeed
                }
                if (!o.axis || o.axis != 'y') {
                    if ((i.overflowOffset.left + i.scrollParent[0].offsetWidth) - e.pageX < o.scrollSensitivity) i.scrollParent[0].scrollLeft = s = i.scrollParent[0].scrollLeft + o.scrollSpeed;
                    else if (e.pageX - i.overflowOffset.left < o.scrollSensitivity) i.scrollParent[0].scrollLeft = s = i.scrollParent[0].scrollLeft - o.scrollSpeed
                }
            } else {
                if (!o.axis || o.axis != 'x') {
                    if (e.pageY - $(document).scrollTop() < o.scrollSensitivity) s = $(document).scrollTop($(document).scrollTop() - o.scrollSpeed);
                    else if ($(window).height() - (e.pageY - $(document).scrollTop()) < o.scrollSensitivity) s = $(document).scrollTop($(document).scrollTop() + o.scrollSpeed)
                }
                if (!o.axis || o.axis != 'y') {
                    if (e.pageX - $(document).scrollLeft() < o.scrollSensitivity) s = $(document).scrollLeft($(document).scrollLeft() - o.scrollSpeed);
                    else if ($(window).width() - (e.pageX - $(document).scrollLeft()) < o.scrollSensitivity) s = $(document).scrollLeft($(document).scrollLeft() + o.scrollSpeed)
                }
            }
            if (s !== false && $.ui.ddmanager && !o.dropBehaviour) $.ui.ddmanager.prepareOffsets(i, e)
        }
    });
    $.ui.plugin.add("draggable", "snap", {
        start: function(e, a) {
            var i = $(this).data("draggable"),
                o = i.options;
            i.snapElements = [];
            $(o.snap.constructor != String ? (o.snap.items || ':data(draggable)') : o.snap).each(function() {
                var b = $(this);
                var c = b.offset();
                if (this != i.element[0]) i.snapElements.push({
                    item: this,
                    width: b.outerWidth(),
                    height: b.outerHeight(),
                    top: c.top,
                    left: c.left
                })
            })
        },
        drag: function(e, a) {
            var c = $(this).data("draggable"),
                o = c.options;
            var d = o.snapTolerance;
            var x = a.offset.left,
                f = x + c.helperProportions.width,
                y = a.offset.top,
                g = y + c.helperProportions.height;
            for (var i = c.snapElements.length - 1; i >= 0; i--) {
                var l = c.snapElements[i].left,
                    r = l + c.snapElements[i].width,
                    t = c.snapElements[i].top,
                    b = t + c.snapElements[i].height;
                if (!((l - d < x && x < r + d && t - d < y && y < b + d) || (l - d < x && x < r + d && t - d < g && g < b + d) || (l - d < f && f < r + d && t - d < y && y < b + d) || (l - d < f && f < r + d && t - d < g && g < b + d))) {
                    if (c.snapElements[i].snapping)(c.options.snap.release && c.options.snap.release.call(c.element, e, $.extend(c._uiHash(), {
                        snapItem: c.snapElements[i].item
                    })));
                    c.snapElements[i].snapping = false;
                    continue
                }
                if (o.snapMode != 'inner') {
                    var h = Math.abs(t - g) <= d;
                    var j = Math.abs(b - y) <= d;
                    var k = Math.abs(l - f) <= d;
                    var m = Math.abs(r - x) <= d;
                    if (h) a.position.top = c._convertPositionTo("relative", {
                        top: t - c.helperProportions.height,
                        left: 0
                    }).top - c.margins.top;
                    if (j) a.position.top = c._convertPositionTo("relative", {
                        top: b,
                        left: 0
                    }).top - c.margins.top;
                    if (k) a.position.left = c._convertPositionTo("relative", {
                        top: 0,
                        left: l - c.helperProportions.width
                    }).left - c.margins.left;
                    if (m) a.position.left = c._convertPositionTo("relative", {
                        top: 0,
                        left: r
                    }).left - c.margins.left
                }
                var n = (h || j || k || m);
                if (o.snapMode != 'outer') {
                    var h = Math.abs(t - y) <= d;
                    var j = Math.abs(b - g) <= d;
                    var k = Math.abs(l - x) <= d;
                    var m = Math.abs(r - f) <= d;
                    if (h) a.position.top = c._convertPositionTo("relative", {
                        top: t,
                        left: 0
                    }).top - c.margins.top;
                    if (j) a.position.top = c._convertPositionTo("relative", {
                        top: b - c.helperProportions.height,
                        left: 0
                    }).top - c.margins.top;
                    if (k) a.position.left = c._convertPositionTo("relative", {
                        top: 0,
                        left: l
                    }).left - c.margins.left;
                    if (m) a.position.left = c._convertPositionTo("relative", {
                        top: 0,
                        left: r - c.helperProportions.width
                    }).left - c.margins.left
                }
                if (!c.snapElements[i].snapping && (h || j || k || m || n))(c.options.snap.snap && c.options.snap.snap.call(c.element, e, $.extend(c._uiHash(), {
                    snapItem: c.snapElements[i].item
                })));
                c.snapElements[i].snapping = (h || j || k || m || n)
            }
        }
    });
    $.ui.plugin.add("draggable", "stack", {
        start: function(e, c) {
            var o = $(this).data("draggable").options;
            var g = $.makeArray($(o.stack)).sort(function(a, b) {
                return (parseInt($(a).css("zIndex"), 10) || 0) - (parseInt($(b).css("zIndex"), 10) || 0)
            });
            if (!g.length) {
                return
            }
            var m = parseInt(g[0].style.zIndex) || 0;
            $(g).each(function(i) {
                this.style.zIndex = m + i
            });
            this[0].style.zIndex = m + g.length
        }
    });
    $.ui.plugin.add("draggable", "zIndex", {
        start: function(e, a) {
            var t = $(a.helper),
                o = $(this).data("draggable").options;
            if (t.css("zIndex")) o._zIndex = t.css("zIndex");
            t.css('zIndex', o.zIndex)
        },
        stop: function(e, a) {
            var o = $(this).data("draggable").options;
            if (o._zIndex) $(a.helper).css('zIndex', o._zIndex)
        }
    })
})(jQuery);