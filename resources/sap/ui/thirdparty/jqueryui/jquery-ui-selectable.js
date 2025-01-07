﻿/*!
 * jQuery UI Selectable 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Selectables
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.mouse.js
 *	jquery.ui.widget.js
 */

(function($, u) {
    $.widget("ui.selectable", $.ui.mouse, {
        options: {
            appendTo: 'body',
            autoRefresh: true,
            distance: 0,
            filter: '*',
            tolerance: 'touch'
        },
        _create: function() {
            var s = this;
            this.element.addClass("ui-selectable");
            this.dragged = false;
            var a;
            this.refresh = function() {
                a = $(s.options.filter, s.element[0]);
                a.addClass("ui-selectee");
                a.each(function() {
                    var b = $(this);
                    var p = b.offset();
                    $.data(this, "selectable-item", {
                        element: this,
                        $element: b,
                        left: p.left,
                        top: p.top,
                        right: p.left + b.outerWidth(),
                        bottom: p.top + b.outerHeight(),
                        startselected: false,
                        selected: b.hasClass('ui-selected'),
                        selecting: b.hasClass('ui-selecting'),
                        unselecting: b.hasClass('ui-unselecting')
                    })
                })
            };
            this.refresh();
            this.selectees = a.addClass("ui-selectee");
            this._mouseInit();
            this.helper = $("<div class='ui-selectable-helper'></div>")
        },
        destroy: function() {
            this.selectees.removeClass("ui-selectee").removeData("selectable-item");
            this.element.removeClass("ui-selectable ui-selectable-disabled").removeData("selectable").unbind(".selectable");
            this._mouseDestroy();
            return this
        },
        _mouseStart: function(e) {
            var s = this;
            this.opos = [e.pageX, e.pageY];
            if (this.options.disabled) return;
            var o = this.options;
            this.selectees = $(o.filter, this.element[0]);
            this._trigger("start", e);
            $(o.appendTo).append(this.helper);
            this.helper.css({
                "left": e.clientX,
                "top": e.clientY,
                "width": 0,
                "height": 0
            });
            if (o.autoRefresh) {
                this.refresh()
            }
            this.selectees.filter('.ui-selected').each(function() {
                var a = $.data(this, "selectable-item");
                a.startselected = true;
                if (!e.metaKey && !e.ctrlKey) {
                    a.$element.removeClass('ui-selected');
                    a.selected = false;
                    a.$element.addClass('ui-unselecting');
                    a.unselecting = true;
                    s._trigger("unselecting", e, {
                        unselecting: a.element
                    })
                }
            });
            $(e.target).parents().andSelf().each(function() {
                var a = $.data(this, "selectable-item");
                if (a) {
                    var d = (!e.metaKey && !e.ctrlKey) || !a.$element.hasClass('ui-selected');
                    a.$element.removeClass(d ? "ui-unselecting" : "ui-selected").addClass(d ? "ui-selecting" : "ui-unselecting");
                    a.unselecting = !d;
                    a.selecting = d;
                    a.selected = d;
                    if (d) {
                        s._trigger("selecting", e, {
                            selecting: a.element
                        })
                    } else {
                        s._trigger("unselecting", e, {
                            unselecting: a.element
                        })
                    }
                    return false
                }
            })
        },
        _mouseDrag: function(e) {
            var s = this;
            this.dragged = true;
            if (this.options.disabled) return;
            var o = this.options;
            var x = this.opos[0],
                y = this.opos[1],
                a = e.pageX,
                b = e.pageY;
            if (x > a) {
                var t = a;
                a = x;
                x = t
            }
            if (y > b) {
                var t = b;
                b = y;
                y = t
            }
            this.helper.css({
                left: x,
                top: y,
                width: a - x,
                height: b - y
            });
            this.selectees.each(function() {
                var c = $.data(this, "selectable-item");
                if (!c || c.element == s.element[0]) return;
                var h = false;
                if (o.tolerance == 'touch') {
                    h = (!(c.left > a || c.right < x || c.top > b || c.bottom < y))
                } else if (o.tolerance == 'fit') {
                    h = (c.left > x && c.right < a && c.top > y && c.bottom < b)
                }
                if (h) {
                    if (c.selected) {
                        c.$element.removeClass('ui-selected');
                        c.selected = false
                    }
                    if (c.unselecting) {
                        c.$element.removeClass('ui-unselecting');
                        c.unselecting = false
                    }
                    if (!c.selecting) {
                        c.$element.addClass('ui-selecting');
                        c.selecting = true;
                        s._trigger("selecting", e, {
                            selecting: c.element
                        })
                    }
                } else {
                    if (c.selecting) {
                        if ((e.metaKey || e.ctrlKey) && c.startselected) {
                            c.$element.removeClass('ui-selecting');
                            c.selecting = false;
                            c.$element.addClass('ui-selected');
                            c.selected = true
                        } else {
                            c.$element.removeClass('ui-selecting');
                            c.selecting = false;
                            if (c.startselected) {
                                c.$element.addClass('ui-unselecting');
                                c.unselecting = true
                            }
                            s._trigger("unselecting", e, {
                                unselecting: c.element
                            })
                        }
                    }
                    if (c.selected) {
                        if (!e.metaKey && !e.ctrlKey && !c.startselected) {
                            c.$element.removeClass('ui-selected');
                            c.selected = false;
                            c.$element.addClass('ui-unselecting');
                            c.unselecting = true;
                            s._trigger("unselecting", e, {
                                unselecting: c.element
                            })
                        }
                    }
                }
            });
            return false
        },
        _mouseStop: function(e) {
            var s = this;
            this.dragged = false;
            var o = this.options;
            $('.ui-unselecting', this.element[0]).each(function() {
                var a = $.data(this, "selectable-item");
                a.$element.removeClass('ui-unselecting');
                a.unselecting = false;
                a.startselected = false;
                s._trigger("unselected", e, {
                    unselected: a.element
                })
            });
            $('.ui-selecting', this.element[0]).each(function() {
                var a = $.data(this, "selectable-item");
                a.$element.removeClass('ui-selecting').addClass('ui-selected');
                a.selecting = false;
                a.selected = true;
                a.startselected = true;
                s._trigger("selected", e, {
                    selected: a.element
                })
            });
            this._trigger("stop", e);
            this.helper.remove();
            return false
        }
    });
    $.extend($.ui.selectable, {
        version: "1.8.23"
    })
})(jQuery);