﻿/*!
 * jQuery UI Droppable 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Droppables
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	jquery.ui.mouse.js
 *	jquery.ui.draggable.js
 */

(function($, u) {
    $.widget("ui.droppable", {
        widgetEventPrefix: "drop",
        options: {
            accept: '*',
            activeClass: false,
            addClasses: true,
            greedy: false,
            hoverClass: false,
            scope: 'default',
            tolerance: 'intersect'
        },
        _create: function() {
            var o = this.options,
                a = o.accept;
            this.isover = 0;
            this.isout = 1;
            this.accept = $.isFunction(a) ? a : function(d) {
                return d.is(a)
            };
            this.proportions = {
                width: this.element[0].offsetWidth,
                height: this.element[0].offsetHeight
            };
            $.ui.ddmanager.droppables[o.scope] = $.ui.ddmanager.droppables[o.scope] || [];
            $.ui.ddmanager.droppables[o.scope].push(this);
            (o.addClasses && this.element.addClass("ui-droppable"))
        },
        destroy: function() {
            var d = $.ui.ddmanager.droppables[this.options.scope];
            for (var i = 0; i < d.length; i++) if (d[i] == this) d.splice(i, 1);
            this.element.removeClass("ui-droppable ui-droppable-disabled").removeData("droppable").unbind(".droppable");
            return this
        },
        _setOption: function(k, v) {
            if (k == 'accept') {
                this.accept = $.isFunction(v) ? v : function(d) {
                    return d.is(v)
                }
            }
            $.Widget.prototype._setOption.apply(this, arguments)
        },
        _activate: function(e) {
            var d = $.ui.ddmanager.current;
            if (this.options.activeClass) this.element.addClass(this.options.activeClass);
            (d && this._trigger('activate', e, this.ui(d)))
        },
        _deactivate: function(e) {
            var d = $.ui.ddmanager.current;
            if (this.options.activeClass) this.element.removeClass(this.options.activeClass);
            (d && this._trigger('deactivate', e, this.ui(d)))
        },
        _over: function(e) {
            var d = $.ui.ddmanager.current;
            if (!d || (d.currentItem || d.element)[0] == this.element[0]) return;
            if (this.accept.call(this.element[0], (d.currentItem || d.element))) {
                if (this.options.hoverClass) this.element.addClass(this.options.hoverClass);
                this._trigger('over', e, this.ui(d))
            }
        },
        _out: function(e) {
            var d = $.ui.ddmanager.current;
            if (!d || (d.currentItem || d.element)[0] == this.element[0]) return;
            if (this.accept.call(this.element[0], (d.currentItem || d.element))) {
                if (this.options.hoverClass) this.element.removeClass(this.options.hoverClass);
                this._trigger('out', e, this.ui(d))
            }
        },
        _drop: function(e, c) {
            var d = c || $.ui.ddmanager.current;
            if (!d || (d.currentItem || d.element)[0] == this.element[0]) return false;
            var a = false;
            this.element.find(":data(droppable)").not(".ui-draggable-dragging").each(function() {
                var i = $.data(this, 'droppable');
                if (i.options.greedy && !i.options.disabled && i.options.scope == d.options.scope && i.accept.call(i.element[0], (d.currentItem || d.element)) && $.ui.intersect(d, $.extend(i, {
                    offset: i.element.offset()
                }), i.options.tolerance)) {
                    a = true;
                    return false
                }
            });
            if (a) return false;
            if (this.accept.call(this.element[0], (d.currentItem || d.element))) {
                if (this.options.activeClass) this.element.removeClass(this.options.activeClass);
                if (this.options.hoverClass) this.element.removeClass(this.options.hoverClass);
                this._trigger('drop', e, this.ui(d));
                return this.element
            }
            return false
        },
        ui: function(c) {
            return {
                draggable: (c.currentItem || c.element),
                helper: c.helper,
                position: c.position,
                offset: c.positionAbs
            }
        }
    });
    $.extend($.ui.droppable, {
        version: "1.8.23"
    });
    $.ui.intersect = function(d, a, c) {
        if (!a.offset) return false;
        var x = (d.positionAbs || d.position.absolute).left,
            e = x + d.helperProportions.width,
            y = (d.positionAbs || d.position.absolute).top,
            f = y + d.helperProportions.height;
        var l = a.offset.left,
            r = l + a.proportions.width,
            t = a.offset.top,
            b = t + a.proportions.height;
        switch (c) {
            case 'fit':
                return (l <= x && e <= r && t <= y && f <= b);
                break;
            case 'intersect':
                return (l < x + (d.helperProportions.width / 2) && e - (d.helperProportions.width / 2) < r && t < y + (d.helperProportions.height / 2) && f - (d.helperProportions.height / 2) < b);
                break;
            case 'pointer':
                var g = ((d.positionAbs || d.position.absolute).left + (d.clickOffset || d.offset.click).left),
                    h = ((d.positionAbs || d.position.absolute).top + (d.clickOffset || d.offset.click).top),
                    i = $.ui.isOver(h, g, t, l, a.proportions.height, a.proportions.width);
                return i;
                break;
            case 'touch':
                return ((y >= t && y <= b) || (f >= t && f <= b) || (y < t && f > b)) && ((x >= l && x <= r) || (e >= l && e <= r) || (x < l && e > r));
                break;
            default:
                return false;
                break
        }
    };
    $.ui.ddmanager = {
        current: null,
        droppables: {
            'default': []
        },
        prepareOffsets: function(t, e) {
            var m = $.ui.ddmanager.droppables[t.options.scope] || [];
            var a = e ? e.type : null;
            var l = (t.currentItem || t.element).find(":data(droppable)").andSelf();
            droppablesLoop: for (var i = 0; i < m.length; i++) {
                if (m[i].options.disabled || (t && !m[i].accept.call(m[i].element[0], (t.currentItem || t.element)))) continue;
                for (var j = 0; j < l.length; j++) {
                    if (l[j] == m[i].element[0]) {
                        m[i].proportions.height = 0;
                        continue droppablesLoop
                    }
                };
                m[i].visible = m[i].element.css("display") != "none";
                if (!m[i].visible) continue;
                if (a == "mousedown") m[i]._activate.call(m[i], e);
                m[i].offset = m[i].element.offset();
                m[i].proportions = {
                    width: m[i].element[0].offsetWidth,
                    height: m[i].element[0].offsetHeight
                }
            }
        },
        drop: function(d, e) {
            var a = false;
            $.each($.ui.ddmanager.droppables[d.options.scope] || [], function() {
                if (!this.options) return;
                if (!this.options.disabled && this.visible && $.ui.intersect(d, this, this.options.tolerance)) a = this._drop.call(this, e) || a;
                if (!this.options.disabled && this.visible && this.accept.call(this.element[0], (d.currentItem || d.element))) {
                    this.isout = 1;
                    this.isover = 0;
                    this._deactivate.call(this, e)
                }
            });
            return a
        },
        dragStart: function(d, e) {
            d.element.parents(":not(body,html)").bind("scroll.droppable", function() {
                if (!d.options.refreshPositions) $.ui.ddmanager.prepareOffsets(d, e)
            })
        },
        drag: function(d, e) {
            if (d.options.refreshPositions) $.ui.ddmanager.prepareOffsets(d, e);
            $.each($.ui.ddmanager.droppables[d.options.scope] || [], function() {
                if (this.options.disabled || this.greedyChild || !this.visible) return;
                var i = $.ui.intersect(d, this, this.options.tolerance);
                var c = !i && this.isover == 1 ? 'isout' : (i && this.isover == 0 ? 'isover' : null);
                if (!c) return;
                var p;
                if (this.options.greedy) {
                    var a = this.element.parents(':data(droppable):eq(0)');
                    if (a.length) {
                        p = $.data(a[0], 'droppable');
                        p.greedyChild = (c == 'isover' ? 1 : 0)
                    }
                }
                if (p && c == 'isover') {
                    p['isover'] = 0;
                    p['isout'] = 1;
                    p._out.call(p, e)
                }
                this[c] = 1;
                this[c == 'isout' ? 'isover' : 'isout'] = 0;
                this[c == "isover" ? "_over" : "_out"].call(this, e);
                if (p && c == 'isout') {
                    p['isout'] = 0;
                    p['isover'] = 1;
                    p._over.call(p, e)
                }
            })
        },
        dragStop: function(d, e) {
            d.element.parents(":not(body,html)").unbind("scroll.droppable");
            if (!d.options.refreshPositions) $.ui.ddmanager.prepareOffsets(d, e)
        }
    }
})(jQuery);