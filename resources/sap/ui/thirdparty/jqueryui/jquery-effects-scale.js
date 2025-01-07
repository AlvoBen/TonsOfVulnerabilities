﻿/*!
 * jQuery UI Effects Scale 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Effects/Scale
 *
 * Depends:
 *	jquery.effects.core.js
 */

(function($, u) {
    $.effects.puff = function(o) {
        return this.queue(function() {
            var e = $(this),
                m = $.effects.setMode(e, o.options.mode || 'hide'),
                p = parseInt(o.options.percent, 10) || 150,
                f = p / 100,
                a = {
                    height: e.height(),
                    width: e.width()
                };
            $.extend(o.options, {
                fade: true,
                mode: m,
                percent: m == 'hide' ? p : 100,
                from: m == 'hide' ? a : {
                    height: a.height * f,
                    width: a.width * f
                }
            });
            e.effect('scale', o.options, o.duration, o.callback);
            e.dequeue()
        })
    };
    $.effects.scale = function(o) {
        return this.queue(function() {
            var e = $(this);
            var a = $.extend(true, {}, o.options);
            var m = $.effects.setMode(e, o.options.mode || 'effect');
            var p = parseInt(o.options.percent, 10) || (parseInt(o.options.percent, 10) == 0 ? 0 : (m == 'hide' ? 0 : 100));
            var d = o.options.direction || 'both';
            var b = o.options.origin;
            if (m != 'effect') {
                a.origin = b || ['middle', 'center'];
                a.restore = true
            }
            var c = {
                height: e.height(),
                width: e.width()
            };
            e.from = o.options.from || (m == 'show' ? {
                height: 0,
                width: 0
            } : c);
            var f = {
                y: d != 'horizontal' ? (p / 100) : 1,
                x: d != 'vertical' ? (p / 100) : 1
            };
            e.to = {
                height: c.height * f.y,
                width: c.width * f.x
            };
            if (o.options.fade) {
                if (m == 'show') {
                    e.from.opacity = 0;
                    e.to.opacity = 1
                };
                if (m == 'hide') {
                    e.from.opacity = 1;
                    e.to.opacity = 0
                }
            };
            a.from = e.from;
            a.to = e.to;
            a.mode = m;
            e.effect('size', a, o.duration, o.callback);
            e.dequeue()
        })
    };
    $.effects.size = function(o) {
        return this.queue(function() {
            var e = $(this),
                p = ['position', 'top', 'bottom', 'left', 'right', 'width', 'height', 'overflow', 'opacity'];
            var a = ['position', 'top', 'bottom', 'left', 'right', 'overflow', 'opacity'];
            var b = ['width', 'height', 'overflow'];
            var c = ['fontSize'];
            var P = ['borderTopWidth', 'borderBottomWidth', 'paddingTop', 'paddingBottom'];
            var h = ['borderLeftWidth', 'borderRightWidth', 'paddingLeft', 'paddingRight'];
            var m = $.effects.setMode(e, o.options.mode || 'effect');
            var r = o.options.restore || false;
            var s = o.options.scale || 'both';
            var d = o.options.origin;
            var f = {
                height: e.height(),
                width: e.width()
            };
            e.from = o.options.from || f;
            e.to = o.options.to || f;
            if (d) {
                var g = $.effects.getBaseline(d, f);
                e.from.top = (f.height - e.from.height) * g.y;
                e.from.left = (f.width - e.from.width) * g.x;
                e.to.top = (f.height - e.to.height) * g.y;
                e.to.left = (f.width - e.to.width) * g.x
            };
            var i = {
                from: {
                    y: e.from.height / f.height,
                    x: e.from.width / f.width
                },
                to: {
                    y: e.to.height / f.height,
                    x: e.to.width / f.width
                }
            };
            if (s == 'box' || s == 'both') {
                if (i.from.y != i.to.y) {
                    p = p.concat(P);
                    e.from = $.effects.setTransition(e, P, i.from.y, e.from);
                    e.to = $.effects.setTransition(e, P, i.to.y, e.to)
                };
                if (i.from.x != i.to.x) {
                    p = p.concat(h);
                    e.from = $.effects.setTransition(e, h, i.from.x, e.from);
                    e.to = $.effects.setTransition(e, h, i.to.x, e.to)
                }
            };
            if (s == 'content' || s == 'both') {
                if (i.from.y != i.to.y) {
                    p = p.concat(c);
                    e.from = $.effects.setTransition(e, c, i.from.y, e.from);
                    e.to = $.effects.setTransition(e, c, i.to.y, e.to)
                }
            };
            $.effects.save(e, r ? p : a);
            e.show();
            $.effects.createWrapper(e);
            e.css('overflow', 'hidden').css(e.from);
            if (s == 'content' || s == 'both') {
                P = P.concat(['marginTop', 'marginBottom']).concat(c);
                h = h.concat(['marginLeft', 'marginRight']);
                b = p.concat(P).concat(h);
                e.find("*[width]").each(function() {
                    var j = $(this);
                    if (r) $.effects.save(j, b);
                    var k = {
                        height: j.height(),
                        width: j.width()
                    };
                    j.from = {
                        height: k.height * i.from.y,
                        width: k.width * i.from.x
                    };
                    j.to = {
                        height: k.height * i.to.y,
                        width: k.width * i.to.x
                    };
                    if (i.from.y != i.to.y) {
                        j.from = $.effects.setTransition(j, P, i.from.y, j.from);
                        j.to = $.effects.setTransition(j, P, i.to.y, j.to)
                    };
                    if (i.from.x != i.to.x) {
                        j.from = $.effects.setTransition(j, h, i.from.x, j.from);
                        j.to = $.effects.setTransition(j, h, i.to.x, j.to)
                    };
                    j.css(j.from);
                    j.animate(j.to, o.duration, o.options.easing, function() {
                        if (r) $.effects.restore(j, b)
                    })
                })
            };
            e.animate(e.to, {
                queue: false,
                duration: o.duration,
                easing: o.options.easing,
                complete: function() {
                    if (e.to.opacity === 0) {
                        e.css('opacity', e.from.opacity)
                    }
                    if (m == 'hide') e.hide();
                    $.effects.restore(e, r ? p : a);
                    $.effects.removeWrapper(e);
                    if (o.callback) o.callback.apply(this, arguments);
                    e.dequeue()
                }
            })
        })
    }
})(jQuery);