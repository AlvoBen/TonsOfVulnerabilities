﻿/*!
 * jQuery UI Sortable 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Sortables
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.mouse.js
 *	jquery.ui.widget.js
 */

(function($, u) {
    $.widget("ui.sortable", $.ui.mouse, {
        widgetEventPrefix: "sort",
        ready: false,
        options: {
            appendTo: "parent",
            axis: false,
            connectWith: false,
            containment: false,
            cursor: 'auto',
            cursorAt: false,
            dropOnEmpty: true,
            forcePlaceholderSize: false,
            forceHelperSize: false,
            grid: false,
            handle: false,
            helper: "original",
            items: '> *',
            opacity: false,
            placeholder: false,
            revert: false,
            scroll: true,
            scrollSensitivity: 20,
            scrollSpeed: 20,
            scope: "default",
            tolerance: "intersect",
            zIndex: 1000
        },
        _create: function() {
            var o = this.options;
            this.containerCache = {};
            this.element.addClass("ui-sortable");
            this.refresh();
            this.floating = this.items.length ? o.axis === 'x' || (/left|right/).test(this.items[0].item.css('float')) || (/inline|table-cell/).test(this.items[0].item.css('display')) : false;
            this.offset = this.element.offset();
            this._mouseInit();
            this.ready = true
        },
        destroy: function() {
            $.Widget.prototype.destroy.call(this);
            this.element.removeClass("ui-sortable ui-sortable-disabled");
            this._mouseDestroy();
            for (var i = this.items.length - 1; i >= 0; i--) this.items[i].item.removeData(this.widgetName + "-item");
            return this
        },
        _setOption: function(k, v) {
            if (k === "disabled") {
                this.options[k] = v;
                this.widget()[v ? "addClass" : "removeClass"]("ui-sortable-disabled")
            } else {
                $.Widget.prototype._setOption.apply(this, arguments)
            }
        },
        _mouseCapture: function(e, o) {
            var t = this;
            if (this.reverting) {
                return false
            }
            if (this.options.disabled || this.options.type == 'static') return false;
            this._refreshItems(e);
            var c = null,
                s = this,
                n = $(e.target).parents().each(function() {
                    if ($.data(this, t.widgetName + '-item') == s) {
                        c = $(this);
                        return false
                    }
                });
            if ($.data(e.target, t.widgetName + '-item') == s) c = $(e.target);
            if (!c) return false;
            if (this.options.handle && !o) {
                var v = false;
                $(this.options.handle, c).find("*").andSelf().each(function() {
                    if (this == e.target) v = true
                });
                if (!v) return false
            }
            this.currentItem = c;
            this._removeCurrentsFromItems();
            return true
        },
        _mouseStart: function(e, a, n) {
            var o = this.options,
                s = this;
            this.currentContainer = this;
            this.refreshPositions();
            this.helper = this._createHelper(e);
            this._cacheHelperProportions();
            this._cacheMargins();
            this.scrollParent = this.helper.scrollParent();
            this.offset = this.currentItem.offset();
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
            this.helper.css("position", "absolute");
            this.cssPosition = this.helper.css("position");
            this.originalPosition = this._generatePosition(e);
            this.originalPageX = e.pageX;
            this.originalPageY = e.pageY;
            (o.cursorAt && this._adjustOffsetFromHelper(o.cursorAt));
            this.domPosition = {
                prev: this.currentItem.prev()[0],
                parent: this.currentItem.parent()[0]
            };
            if (this.helper[0] != this.currentItem[0]) {
                this.currentItem.hide()
            }
            this._createPlaceholder();
            if (o.containment) this._setContainment();
            if (o.cursor) {
                if ($('body').css("cursor")) this._storedCursor = $('body').css("cursor");
                $('body').css("cursor", o.cursor)
            }
            if (o.opacity) {
                if (this.helper.css("opacity")) this._storedOpacity = this.helper.css("opacity");
                this.helper.css("opacity", o.opacity)
            }
            if (o.zIndex) {
                if (this.helper.css("zIndex")) this._storedZIndex = this.helper.css("zIndex");
                this.helper.css("zIndex", o.zIndex)
            }
            if (this.scrollParent[0] != document && this.scrollParent[0].tagName != 'HTML') this.overflowOffset = this.scrollParent.offset();
            this._trigger("start", e, this._uiHash());
            if (!this._preserveHelperProportions) this._cacheHelperProportions();
            if (!n) {
                for (var i = this.containers.length - 1; i >= 0; i--) {
                    this.containers[i]._trigger("activate", e, s._uiHash(this))
                }
            }
            if ($.ui.ddmanager) $.ui.ddmanager.current = this;
            if ($.ui.ddmanager && !o.dropBehaviour) $.ui.ddmanager.prepareOffsets(this, e);
            this.dragging = true;
            this.helper.addClass("ui-sortable-helper");
            this._mouseDrag(e);
            return true
        },
        _mouseDrag: function(e) {
            this.position = this._generatePosition(e);
            this.positionAbs = this._convertPositionTo("absolute");
            if (!this.lastPositionAbs) {
                this.lastPositionAbs = this.positionAbs
            }
            if (this.options.scroll) {
                var o = this.options,
                    s = false;
                if (this.scrollParent[0] != document && this.scrollParent[0].tagName != 'HTML') {
                    if ((this.overflowOffset.top + this.scrollParent[0].offsetHeight) - e.pageY < o.scrollSensitivity) this.scrollParent[0].scrollTop = s = this.scrollParent[0].scrollTop + o.scrollSpeed;
                    else if (e.pageY - this.overflowOffset.top < o.scrollSensitivity) this.scrollParent[0].scrollTop = s = this.scrollParent[0].scrollTop - o.scrollSpeed;
                    if ((this.overflowOffset.left + this.scrollParent[0].offsetWidth) - e.pageX < o.scrollSensitivity) this.scrollParent[0].scrollLeft = s = this.scrollParent[0].scrollLeft + o.scrollSpeed;
                    else if (e.pageX - this.overflowOffset.left < o.scrollSensitivity) this.scrollParent[0].scrollLeft = s = this.scrollParent[0].scrollLeft - o.scrollSpeed
                } else {
                    if (e.pageY - $(document).scrollTop() < o.scrollSensitivity) s = $(document).scrollTop($(document).scrollTop() - o.scrollSpeed);
                    else if ($(window).height() - (e.pageY - $(document).scrollTop()) < o.scrollSensitivity) s = $(document).scrollTop($(document).scrollTop() + o.scrollSpeed);
                    if (e.pageX - $(document).scrollLeft() < o.scrollSensitivity) s = $(document).scrollLeft($(document).scrollLeft() - o.scrollSpeed);
                    else if ($(window).width() - (e.pageX - $(document).scrollLeft()) < o.scrollSensitivity) s = $(document).scrollLeft($(document).scrollLeft() + o.scrollSpeed)
                }
                if (s !== false && $.ui.ddmanager && !o.dropBehaviour) $.ui.ddmanager.prepareOffsets(this, e)
            }
            this.positionAbs = this._convertPositionTo("absolute");
            if (!this.options.axis || this.options.axis != "y") this.helper[0].style.left = this.position.left + 'px';
            if (!this.options.axis || this.options.axis != "x") this.helper[0].style.top = this.position.top + 'px';
            for (var i = this.items.length - 1; i >= 0; i--) {
                var a = this.items[i],
                    b = a.item[0],
                    c = this._intersectsWithPointer(a);
                if (!c) continue;
                if (b != this.currentItem[0] && this.placeholder[c == 1 ? "next" : "prev"]()[0] != b && !$.ui.contains(this.placeholder[0], b) && (this.options.type == 'semi-dynamic' ? !$.ui.contains(this.element[0], b) : true)) {
                    this.direction = c == 1 ? "down" : "up";
                    if (this.options.tolerance == "pointer" || this._intersectsWithSides(a)) {
                        this._rearrange(e, a)
                    } else {
                        break
                    }
                    this._trigger("change", e, this._uiHash());
                    break
                }
            }
            this._contactContainers(e);
            if ($.ui.ddmanager) $.ui.ddmanager.drag(this, e);
            this._trigger('sort', e, this._uiHash());
            this.lastPositionAbs = this.positionAbs;
            return false
        },
        _mouseStop: function(e, n) {
            if (!e) return;
            if ($.ui.ddmanager && !this.options.dropBehaviour) $.ui.ddmanager.drop(this, e);
            if (this.options.revert) {
                var s = this;
                var c = s.placeholder.offset();
                s.reverting = true;
                $(this.helper).animate({
                    left: c.left - this.offset.parent.left - s.margins.left + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollLeft),
                    top: c.top - this.offset.parent.top - s.margins.top + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollTop)
                }, parseInt(this.options.revert, 10) || 500, function() {
                    s._clear(e)
                })
            } else {
                this._clear(e, n)
            }
            return false
        },
        cancel: function() {
            var s = this;
            if (this.dragging) {
                this._mouseUp({
                    target: null
                });
                if (this.options.helper == "original") this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper");
                else this.currentItem.show();
                for (var i = this.containers.length - 1; i >= 0; i--) {
                    this.containers[i]._trigger("deactivate", null, s._uiHash(this));
                    if (this.containers[i].containerCache.over) {
                        this.containers[i]._trigger("out", null, s._uiHash(this));
                        this.containers[i].containerCache.over = 0
                    }
                }
            }
            if (this.placeholder) {
                if (this.placeholder[0].parentNode) this.placeholder[0].parentNode.removeChild(this.placeholder[0]);
                if (this.options.helper != "original" && this.helper && this.helper[0].parentNode) this.helper.remove();
                $.extend(this, {
                    helper: null,
                    dragging: false,
                    reverting: false,
                    _noFinalSort: null
                });
                if (this.domPosition.prev) {
                    $(this.domPosition.prev).after(this.currentItem)
                } else {
                    $(this.domPosition.parent).prepend(this.currentItem)
                }
            }
            return this
        },
        serialize: function(o) {
            var i = this._getItemsAsjQuery(o && o.connected);
            var s = [];
            o = o || {};
            $(i).each(function() {
                var r = ($(o.item || this).attr(o.attribute || 'id') || '').match(o.expression || (/(.+)[-=_](.+)/));
                if (r) s.push((o.key || r[1] + '[]') + '=' + (o.key && o.expression ? r[1] : r[2]))
            });
            if (!s.length && o.key) {
                s.push(o.key + '=')
            }
            return s.join('&')
        },
        toArray: function(o) {
            var i = this._getItemsAsjQuery(o && o.connected);
            var r = [];
            o = o || {};
            i.each(function() {
                r.push($(o.item || this).attr(o.attribute || 'id') || '')
            });
            return r
        },
        _intersectsWith: function(i) {
            var x = this.positionAbs.left,
                a = x + this.helperProportions.width,
                y = this.positionAbs.top,
                c = y + this.helperProportions.height;
            var l = i.left,
                r = l + i.width,
                t = i.top,
                b = t + i.height;
            var d = this.offset.click.top,
                e = this.offset.click.left;
            var f = (y + d) > t && (y + d) < b && (x + e) > l && (x + e) < r;
            if (this.options.tolerance == "pointer" || this.options.forcePointerForContainers || (this.options.tolerance != "pointer" && this.helperProportions[this.floating ? 'width' : 'height'] > i[this.floating ? 'width' : 'height'])) {
                return f
            } else {
                return (l < x + (this.helperProportions.width / 2) && a - (this.helperProportions.width / 2) < r && t < y + (this.helperProportions.height / 2) && c - (this.helperProportions.height / 2) < b)
            }
        },
        _intersectsWithPointer: function(i) {
            var a = (this.options.axis === 'x') || $.ui.isOverAxis(this.positionAbs.top + this.offset.click.top, i.top, i.height),
                b = (this.options.axis === 'y') || $.ui.isOverAxis(this.positionAbs.left + this.offset.click.left, i.left, i.width),
                c = a && b,
                v = this._getDragVerticalDirection(),
                h = this._getDragHorizontalDirection();
            if (!c) return false;
            return this.floating ? (((h && h == "right") || v == "down") ? 2 : 1) : (v && (v == "down" ? 2 : 1))
        },
        _intersectsWithSides: function(i) {
            var a = $.ui.isOverAxis(this.positionAbs.top + this.offset.click.top, i.top + (i.height / 2), i.height),
                b = $.ui.isOverAxis(this.positionAbs.left + this.offset.click.left, i.left + (i.width / 2), i.width),
                v = this._getDragVerticalDirection(),
                h = this._getDragHorizontalDirection();
            if (this.floating && h) {
                return ((h == "right" && b) || (h == "left" && !b))
            } else {
                return v && ((v == "down" && a) || (v == "up" && !a))
            }
        },
        _getDragVerticalDirection: function() {
            var d = this.positionAbs.top - this.lastPositionAbs.top;
            return d != 0 && (d > 0 ? "down" : "up")
        },
        _getDragHorizontalDirection: function() {
            var d = this.positionAbs.left - this.lastPositionAbs.left;
            return d != 0 && (d > 0 ? "right" : "left")
        },
        refresh: function(e) {
            this._refreshItems(e);
            this.refreshPositions();
            return this
        },
        _connectWith: function() {
            var o = this.options;
            return o.connectWith.constructor == String ? [o.connectWith] : o.connectWith
        },
        _getItemsAsjQuery: function(c) {
            var s = this;
            var a = [];
            var q = [];
            var b = this._connectWith();
            if (b && c) {
                for (var i = b.length - 1; i >= 0; i--) {
                    var d = $(b[i]);
                    for (var j = d.length - 1; j >= 0; j--) {
                        var e = $.data(d[j], this.widgetName);
                        if (e && e != this && !e.options.disabled) {
                            q.push([$.isFunction(e.options.items) ? e.options.items.call(e.element) : $(e.options.items, e.element).not(".ui-sortable-helper").not('.ui-sortable-placeholder'), e])
                        }
                    }
                }
            }
            q.push([$.isFunction(this.options.items) ? this.options.items.call(this.element, null, {
                options: this.options,
                item: this.currentItem
            }) : $(this.options.items, this.element).not(".ui-sortable-helper").not('.ui-sortable-placeholder'), this]);
            for (var i = q.length - 1; i >= 0; i--) {
                q[i][0].each(function() {
                    a.push(this)
                })
            };
            return $(a)
        },
        _removeCurrentsFromItems: function() {
            var l = this.currentItem.find(":data(" + this.widgetName + "-item)");
            for (var i = 0; i < this.items.length; i++) {
                for (var j = 0; j < l.length; j++) {
                    if (l[j] == this.items[i].item[0]) this.items.splice(i, 1)
                }
            }
        },
        _refreshItems: function(e) {
            this.items = [];
            this.containers = [this];
            var a = this.items;
            var s = this;
            var q = [
                [$.isFunction(this.options.items) ? this.options.items.call(this.element[0], e, {
                    item: this.currentItem
                }) : $(this.options.items, this.element), this]
            ];
            var c = this._connectWith();
            if (c && this.ready) {
                for (var i = c.length - 1; i >= 0; i--) {
                    var b = $(c[i]);
                    for (var j = b.length - 1; j >= 0; j--) {
                        var d = $.data(b[j], this.widgetName);
                        if (d && d != this && !d.options.disabled) {
                            q.push([$.isFunction(d.options.items) ? d.options.items.call(d.element[0], e, {
                                item: this.currentItem
                            }) : $(d.options.items, d.element), d]);
                            this.containers.push(d)
                        }
                    }
                }
            }
            for (var i = q.length - 1; i >= 0; i--) {
                var t = q[i][1];
                var _ = q[i][0];
                for (var j = 0, f = _.length; j < f; j++) {
                    var g = $(_[j]);
                    g.data(this.widgetName + '-item', t);
                    a.push({
                        item: g,
                        instance: t,
                        width: 0,
                        height: 0,
                        left: 0,
                        top: 0
                    })
                }
            }
        },
        refreshPositions: function(f) {
            if (this.offsetParent && this.helper) {
                this.offset.parent = this._getParentOffset()
            }
            for (var i = this.items.length - 1; i >= 0; i--) {
                var a = this.items[i];
                if (a.instance != this.currentContainer && this.currentContainer && a.item[0] != this.currentItem[0]) continue;
                var t = this.options.toleranceElement ? $(this.options.toleranceElement, a.item) : a.item;
                if (!f) {
                    a.width = t.outerWidth();
                    a.height = t.outerHeight()
                }
                var p = t.offset();
                a.left = p.left;
                a.top = p.top
            };
            if (this.options.custom && this.options.custom.refreshContainers) {
                this.options.custom.refreshContainers.call(this)
            } else {
                for (var i = this.containers.length - 1; i >= 0; i--) {
                    var p = this.containers[i].element.offset();
                    this.containers[i].containerCache.left = p.left;
                    this.containers[i].containerCache.top = p.top;
                    this.containers[i].containerCache.width = this.containers[i].element.outerWidth();
                    this.containers[i].containerCache.height = this.containers[i].element.outerHeight()
                }
            }
            return this
        },
        _createPlaceholder: function(t) {
            var s = t || this,
                o = s.options;
            if (!o.placeholder || o.placeholder.constructor == String) {
                var c = o.placeholder;
                o.placeholder = {
                    element: function() {
                        var e = $(document.createElement(s.currentItem[0].nodeName)).addClass(c || s.currentItem[0].className + " ui-sortable-placeholder").removeClass("ui-sortable-helper")[0];
                        if (!c) e.style.visibility = "hidden";
                        return e
                    },
                    update: function(a, p) {
                        if (c && !o.forcePlaceholderSize) return;
                        if (!p.height()) {
                            p.height(s.currentItem.innerHeight() - parseInt(s.currentItem.css('paddingTop') || 0, 10) - parseInt(s.currentItem.css('paddingBottom') || 0, 10))
                        };
                        if (!p.width()) {
                            p.width(s.currentItem.innerWidth() - parseInt(s.currentItem.css('paddingLeft') || 0, 10) - parseInt(s.currentItem.css('paddingRight') || 0, 10))
                        }
                    }
                }
            }
            s.placeholder = $(o.placeholder.element.call(s.element, s.currentItem));
            s.currentItem.after(s.placeholder);
            o.placeholder.update(s, s.placeholder)
        },
        _contactContainers: function(e) {
            var a = null,
                b = null;
            for (var i = this.containers.length - 1; i >= 0; i--) {
                if ($.ui.contains(this.currentItem[0], this.containers[i].element[0])) continue;
                if (this._intersectsWith(this.containers[i].containerCache)) {
                    if (a && $.ui.contains(this.containers[i].element[0], a.element[0])) continue;
                    a = this.containers[i];
                    b = i
                } else {
                    if (this.containers[i].containerCache.over) {
                        this.containers[i]._trigger("out", e, this._uiHash(this));
                        this.containers[i].containerCache.over = 0
                    }
                }
            }
            if (!a) return;
            if (this.containers.length === 1) {
                this.containers[b]._trigger("over", e, this._uiHash(this));
                this.containers[b].containerCache.over = 1
            } else if (this.currentContainer != this.containers[b]) {
                var d = 10000;
                var c = null;
                var f = this.positionAbs[this.containers[b].floating ? 'left' : 'top'];
                for (var j = this.items.length - 1; j >= 0; j--) {
                    if (!$.ui.contains(this.containers[b].element[0], this.items[j].item[0])) continue;
                    var g = this.containers[b].floating ? this.items[j].item.offset().left : this.items[j].item.offset().top;
                    if (Math.abs(g - f) < d) {
                        d = Math.abs(g - f);
                        c = this.items[j];
                        this.direction = (g - f > 0) ? 'down' : 'up'
                    }
                }
                if (!c && !this.options.dropOnEmpty) return;
                this.currentContainer = this.containers[b];
                c ? this._rearrange(e, c, null, true) : this._rearrange(e, null, this.containers[b].element, true);
                this._trigger("change", e, this._uiHash());
                this.containers[b]._trigger("change", e, this._uiHash(this));
                this.options.placeholder.update(this.currentContainer, this.placeholder);
                this.containers[b]._trigger("over", e, this._uiHash(this));
                this.containers[b].containerCache.over = 1
            }
        },
        _createHelper: function(e) {
            var o = this.options;
            var h = $.isFunction(o.helper) ? $(o.helper.apply(this.element[0], [e, this.currentItem])) : (o.helper == 'clone' ? this.currentItem.clone() : this.currentItem);
            if (!h.parents('body').length) $(o.appendTo != 'parent' ? o.appendTo : this.currentItem[0].parentNode)[0].appendChild(h[0]);
            if (h[0] == this.currentItem[0]) this._storedCSS = {
                width: this.currentItem[0].style.width,
                height: this.currentItem[0].style.height,
                position: this.currentItem.css("position"),
                top: this.currentItem.css("top"),
                left: this.currentItem.css("left")
            };
            if (h[0].style.width == '' || o.forceHelperSize) h.width(this.currentItem.width());
            if (h[0].style.height == '' || o.forceHelperSize) h.height(this.currentItem.height());
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
                var p = this.currentItem.position();
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
                left: (parseInt(this.currentItem.css("marginLeft"), 10) || 0),
                top: (parseInt(this.currentItem.css("marginTop"), 10) || 0)
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
            if (o.containment == 'document' || o.containment == 'window') this.containment = [0 - this.offset.relative.left - this.offset.parent.left, 0 - this.offset.relative.top - this.offset.parent.top, $(o.containment == 'document' ? document : window).width() - this.helperProportions.width - this.margins.left, ($(o.containment == 'document' ? document : window).height() || document.body.parentNode.scrollHeight) - this.helperProportions.height - this.margins.top];
            if (!(/^(document|window|parent)$/).test(o.containment)) {
                var c = $(o.containment)[0];
                var a = $(o.containment).offset();
                var b = ($(c).css("overflow") != 'hidden');
                this.containment = [a.left + (parseInt($(c).css("borderLeftWidth"), 10) || 0) + (parseInt($(c).css("paddingLeft"), 10) || 0) - this.margins.left, a.top + (parseInt($(c).css("borderTopWidth"), 10) || 0) + (parseInt($(c).css("paddingTop"), 10) || 0) - this.margins.top, a.left + (b ? Math.max(c.scrollWidth, c.offsetWidth) : c.offsetWidth) - (parseInt($(c).css("borderLeftWidth"), 10) || 0) - (parseInt($(c).css("paddingRight"), 10) || 0) - this.helperProportions.width - this.margins.left, a.top + (b ? Math.max(c.scrollHeight, c.offsetHeight) : c.offsetHeight) - (parseInt($(c).css("borderTopWidth"), 10) || 0) - (parseInt($(c).css("paddingBottom"), 10) || 0) - this.helperProportions.height - this.margins.top]
            }
        },
        _convertPositionTo: function(d, p) {
            if (!p) p = this.position;
            var m = d == "absolute" ? 1 : -1;
            var o = this.options,
                s = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                a = (/(html|body)/i).test(s[0].tagName);
            return {
                top: (p.top + this.offset.relative.top * m + this.offset.parent.top * m - ($.browser.safari && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : (a ? 0 : s.scrollTop())) * m)),
                left: (p.left + this.offset.relative.left * m + this.offset.parent.left * m - ($.browser.safari && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : a ? 0 : s.scrollLeft()) * m))
            }
        },
        _generatePosition: function(e) {
            var o = this.options,
                s = this.cssPosition == 'absolute' && !(this.scrollParent[0] != document && $.ui.contains(this.scrollParent[0], this.offsetParent[0])) ? this.offsetParent : this.scrollParent,
                a = (/(html|body)/i).test(s[0].tagName);
            if (this.cssPosition == 'relative' && !(this.scrollParent[0] != document && this.scrollParent[0] != this.offsetParent[0])) {
                this.offset.relative = this._getRelativeOffset()
            }
            var p = e.pageX;
            var b = e.pageY;
            if (this.originalPosition) {
                if (this.containment) {
                    if (e.pageX - this.offset.click.left < this.containment[0]) p = this.containment[0] + this.offset.click.left;
                    if (e.pageY - this.offset.click.top < this.containment[1]) b = this.containment[1] + this.offset.click.top;
                    if (e.pageX - this.offset.click.left > this.containment[2]) p = this.containment[2] + this.offset.click.left;
                    if (e.pageY - this.offset.click.top > this.containment[3]) b = this.containment[3] + this.offset.click.top
                }
                if (o.grid) {
                    var t = this.originalPageY + Math.round((b - this.originalPageY) / o.grid[1]) * o.grid[1];
                    b = this.containment ? (!(t - this.offset.click.top < this.containment[1] || t - this.offset.click.top > this.containment[3]) ? t : (!(t - this.offset.click.top < this.containment[1]) ? t - o.grid[1] : t + o.grid[1])) : t;
                    var l = this.originalPageX + Math.round((p - this.originalPageX) / o.grid[0]) * o.grid[0];
                    p = this.containment ? (!(l - this.offset.click.left < this.containment[0] || l - this.offset.click.left > this.containment[2]) ? l : (!(l - this.offset.click.left < this.containment[0]) ? l - o.grid[0] : l + o.grid[0])) : l
                }
            }
            return {
                top: (b - this.offset.click.top - this.offset.relative.top - this.offset.parent.top + ($.browser.safari && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollTop() : (a ? 0 : s.scrollTop())))),
                left: (p - this.offset.click.left - this.offset.relative.left - this.offset.parent.left + ($.browser.safari && this.cssPosition == 'fixed' ? 0 : (this.cssPosition == 'fixed' ? -this.scrollParent.scrollLeft() : a ? 0 : s.scrollLeft())))
            }
        },
        _rearrange: function(e, i, a, h) {
            a ? a[0].appendChild(this.placeholder[0]) : i.item[0].parentNode.insertBefore(this.placeholder[0], (this.direction == 'down' ? i.item[0] : i.item[0].nextSibling));
            this.counter = this.counter ? ++this.counter : 1;
            var s = this,
                c = this.counter;
            window.setTimeout(function() {
                if (c == s.counter) s.refreshPositions(!h)
            }, 0)
        },
        _clear: function(e, n) {
            this.reverting = false;
            var d = [],
                s = this;
            if (!this._noFinalSort && this.currentItem.parent().length) this.placeholder.before(this.currentItem);
            this._noFinalSort = null;
            if (this.helper[0] == this.currentItem[0]) {
                for (var i in this._storedCSS) {
                    if (this._storedCSS[i] == 'auto' || this._storedCSS[i] == 'static') this._storedCSS[i] = ''
                }
                this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")
            } else {
                this.currentItem.show()
            }
            if (this.fromOutside && !n) d.push(function(e) {
                this._trigger("receive", e, this._uiHash(this.fromOutside))
            });
            if ((this.fromOutside || this.domPosition.prev != this.currentItem.prev().not(".ui-sortable-helper")[0] || this.domPosition.parent != this.currentItem.parent()[0]) && !n) d.push(function(e) {
                this._trigger("update", e, this._uiHash())
            });
            if (!$.ui.contains(this.element[0], this.currentItem[0])) {
                if (!n) d.push(function(e) {
                    this._trigger("remove", e, this._uiHash())
                });
                for (var i = this.containers.length - 1; i >= 0; i--) {
                    if ($.ui.contains(this.containers[i].element[0], this.currentItem[0]) && !n) {
                        d.push((function(c) {
                            return function(e) {
                                c._trigger("receive", e, this._uiHash(this))
                            }
                        }).call(this, this.containers[i]));
                        d.push((function(c) {
                            return function(e) {
                                c._trigger("update", e, this._uiHash(this))
                            }
                        }).call(this, this.containers[i]))
                    }
                }
            };
            for (var i = this.containers.length - 1; i >= 0; i--) {
                if (!n) d.push((function(c) {
                    return function(e) {
                        c._trigger("deactivate", e, this._uiHash(this))
                    }
                }).call(this, this.containers[i]));
                if (this.containers[i].containerCache.over) {
                    d.push((function(c) {
                        return function(e) {
                            c._trigger("out", e, this._uiHash(this))
                        }
                    }).call(this, this.containers[i]));
                    this.containers[i].containerCache.over = 0
                }
            }
            if (this._storedCursor) $('body').css("cursor", this._storedCursor);
            if (this._storedOpacity) this.helper.css("opacity", this._storedOpacity);
            if (this._storedZIndex) this.helper.css("zIndex", this._storedZIndex == 'auto' ? '' : this._storedZIndex);
            this.dragging = false;
            if (this.cancelHelperRemoval) {
                if (!n) {
                    this._trigger("beforeStop", e, this._uiHash());
                    for (var i = 0; i < d.length; i++) {
                        d[i].call(this, e)
                    };
                    this._trigger("stop", e, this._uiHash())
                }
                this.fromOutside = false;
                return false
            }
            if (!n) this._trigger("beforeStop", e, this._uiHash());
            this.placeholder[0].parentNode.removeChild(this.placeholder[0]);
            if (this.helper[0] != this.currentItem[0]) this.helper.remove();
            this.helper = null;
            if (!n) {
                for (var i = 0; i < d.length; i++) {
                    d[i].call(this, e)
                };
                this._trigger("stop", e, this._uiHash())
            }
            this.fromOutside = false;
            return true
        },
        _trigger: function() {
            if ($.Widget.prototype._trigger.apply(this, arguments) === false) {
                this.cancel()
            }
        },
        _uiHash: function(i) {
            var s = i || this;
            return {
                helper: s.helper,
                placeholder: s.placeholder || $([]),
                position: s.position,
                originalPosition: s.originalPosition,
                offset: s.positionAbs,
                item: s.currentItem,
                sender: i ? i.element : null
            }
        }
    });
    $.extend($.ui.sortable, {
        version: "1.8.23"
    })
})(jQuery);