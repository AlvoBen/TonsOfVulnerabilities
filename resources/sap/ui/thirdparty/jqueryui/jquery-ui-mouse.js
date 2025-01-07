﻿/*!
 * jQuery UI Mouse 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Mouse
 *
 * Depends:
 *	jquery.ui.widget.js
 */

(function($, u) {
    var m = false;
    $(document).mouseup(function(e) {
        m = false
    });
    $.widget("ui.mouse", {
        options: {
            cancel: ':input,option',
            distance: 1,
            delay: 0
        },
        _mouseInit: function() {
            var s = this;
            this.element.bind('mousedown.' + this.widgetName, function(e) {
                return s._mouseDown(e)
            }).bind('click.' + this.widgetName, function(e) {
                if (true === $.data(e.target, s.widgetName + '.preventClickEvent')) {
                    $.removeData(e.target, s.widgetName + '.preventClickEvent');
                    e.stopImmediatePropagation();
                    return false
                }
            });
            this.started = false
        },
        _mouseDestroy: function() {
            this.element.unbind('.' + this.widgetName);
            if (this._mouseMoveDelegate) {
                $(document).unbind('mousemove.' + this.widgetName, this._mouseMoveDelegate).unbind('mouseup.' + this.widgetName, this._mouseUpDelegate)
            }
        },
        _mouseDown: function(e) {
            if (m) {
                return
            };
            (this._mouseStarted && this._mouseUp(e));
            this._mouseDownEvent = e;
            var s = this,
                b = (e.which == 1),
                a = (typeof this.options.cancel == "string" && e.target.nodeName ? $(e.target).closest(this.options.cancel).length : false);
            if (!b || a || !this._mouseCapture(e)) {
                return true
            }
            this.mouseDelayMet = !this.options.delay;
            if (!this.mouseDelayMet) {
                this._mouseDelayTimer = setTimeout(function() {
                    s.mouseDelayMet = true
                }, this.options.delay)
            }
            if (this._mouseDistanceMet(e) && this._mouseDelayMet(e)) {
                this._mouseStarted = (this._mouseStart(e) !== false);
                if (!this._mouseStarted) {
                    e.preventDefault();
                    return true
                }
            }
            if (true === $.data(e.target, this.widgetName + '.preventClickEvent')) {
                $.removeData(e.target, this.widgetName + '.preventClickEvent')
            }
            this._mouseMoveDelegate = function(e) {
                return s._mouseMove(e)
            };
            this._mouseUpDelegate = function(e) {
                return s._mouseUp(e)
            };
            $(document).bind('mousemove.' + this.widgetName, this._mouseMoveDelegate).bind('mouseup.' + this.widgetName, this._mouseUpDelegate);
            e.preventDefault();
            m = true;
            return true
        },
        _mouseMove: function(e) {
            if ($.browser.msie && !(document.documentMode >= 9) && !e.button) {
                return this._mouseUp(e)
            }
            if (this._mouseStarted) {
                this._mouseDrag(e);
                return e.preventDefault()
            }
            if (this._mouseDistanceMet(e) && this._mouseDelayMet(e)) {
                this._mouseStarted = (this._mouseStart(this._mouseDownEvent, e) !== false);
                (this._mouseStarted ? this._mouseDrag(e) : this._mouseUp(e))
            }
            return !this._mouseStarted
        },
        _mouseUp: function(e) {
            $(document).unbind('mousemove.' + this.widgetName, this._mouseMoveDelegate).unbind('mouseup.' + this.widgetName, this._mouseUpDelegate);
            if (this._mouseStarted) {
                this._mouseStarted = false;
                if (e.target == this._mouseDownEvent.target) {
                    $.data(e.target, this.widgetName + '.preventClickEvent', true)
                }
                this._mouseStop(e)
            }
            return false
        },
        _mouseDistanceMet: function(e) {
            return (Math.max(Math.abs(this._mouseDownEvent.pageX - e.pageX), Math.abs(this._mouseDownEvent.pageY - e.pageY)) >= this.options.distance)
        },
        _mouseDelayMet: function(e) {
            return this.mouseDelayMet
        },
        _mouseStart: function(e) {},
        _mouseDrag: function(e) {},
        _mouseStop: function(e) {},
        _mouseCapture: function(e) {
            return true
        }
    })
})(jQuery);