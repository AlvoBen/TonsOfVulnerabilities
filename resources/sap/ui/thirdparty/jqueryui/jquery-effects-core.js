﻿/*!
 * jQuery UI Effects 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Effects/
 */

;
jQuery.effects || (function($, u) {
    $.effects = {};
    $.each(['backgroundColor', 'borderBottomColor', 'borderLeftColor', 'borderRightColor', 'borderTopColor', 'borderColor', 'color', 'outlineColor'], function(i, e) {
        $.fx.step[e] = function(l) {
            if (!l.colorInit) {
                l.start = a(l.elem, e);
                l.end = g(l.end);
                l.colorInit = true
            }
            l.elem.style[e] = 'rgb(' + Math.max(Math.min(parseInt((l.pos * (l.end[0] - l.start[0])) + l.start[0], 10), 255), 0) + ',' + Math.max(Math.min(parseInt((l.pos * (l.end[1] - l.start[1])) + l.start[1], 10), 255), 0) + ',' + Math.max(Math.min(parseInt((l.pos * (l.end[2] - l.start[2])) + l.start[2], 10), 255), 0) + ')'
        }
    });

    function g(e) {
        var r;
        if (e && e.constructor == Array && e.length == 3) return e;
        if (r = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(e)) return [parseInt(r[1], 10), parseInt(r[2], 10), parseInt(r[3], 10)];
        if (r = /rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(e)) return [parseFloat(r[1]) * 2.55, parseFloat(r[2]) * 2.55, parseFloat(r[3]) * 2.55];
        if (r = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(e)) return [parseInt(r[1], 16), parseInt(r[2], 16), parseInt(r[3], 16)];
        if (r = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(e)) return [parseInt(r[1] + r[1], 16), parseInt(r[2] + r[2], 16), parseInt(r[3] + r[3], 16)];
        if (r = /rgba\(0, 0, 0, 0\)/.exec(e)) return c['transparent'];
        return c[$.trim(e).toLowerCase()]
    }
    function a(e, i) {
        var l;
        do {
            l = $.css(e, i);
            if (l != '' && l != 'transparent' || $.nodeName(e, "body")) break;
            i = "backgroundColor"
        } while (e = e.parentNode);
        return g(l)
    };
    var c = {
        aqua: [0, 255, 255],
        azure: [240, 255, 255],
        beige: [245, 245, 220],
        black: [0, 0, 0],
        blue: [0, 0, 255],
        brown: [165, 42, 42],
        cyan: [0, 255, 255],
        darkblue: [0, 0, 139],
        darkcyan: [0, 139, 139],
        darkgrey: [169, 169, 169],
        darkgreen: [0, 100, 0],
        darkkhaki: [189, 183, 107],
        darkmagenta: [139, 0, 139],
        darkolivegreen: [85, 107, 47],
        darkorange: [255, 140, 0],
        darkorchid: [153, 50, 204],
        darkred: [139, 0, 0],
        darksalmon: [233, 150, 122],
        darkviolet: [148, 0, 211],
        fuchsia: [255, 0, 255],
        gold: [255, 215, 0],
        green: [0, 128, 0],
        indigo: [75, 0, 130],
        khaki: [240, 230, 140],
        lightblue: [173, 216, 230],
        lightcyan: [224, 255, 255],
        lightgreen: [144, 238, 144],
        lightgrey: [211, 211, 211],
        lightpink: [255, 182, 193],
        lightyellow: [255, 255, 224],
        lime: [0, 255, 0],
        magenta: [255, 0, 255],
        maroon: [128, 0, 0],
        navy: [0, 0, 128],
        olive: [128, 128, 0],
        orange: [255, 165, 0],
        pink: [255, 192, 203],
        purple: [128, 0, 128],
        violet: [128, 0, 128],
        red: [255, 0, 0],
        silver: [192, 192, 192],
        white: [255, 255, 255],
        yellow: [255, 255, 0],
        transparent: [255, 255, 255]
    };
    var b = ['add', 'remove', 'toggle'],
        s = {
            border: 1,
            borderBottom: 1,
            borderColor: 1,
            borderLeft: 1,
            borderRight: 1,
            borderTop: 1,
            borderWidth: 1,
            margin: 1,
            padding: 1
        };

    function d() {
        var e = document.defaultView ? document.defaultView.getComputedStyle(this, null) : this.currentStyle,
            n = {}, i, l;
        if (e && e.length && e[0] && e[e[0]]) {
            var m = e.length;
            while (m--) {
                i = e[m];
                if (typeof e[i] == 'string') {
                    l = i.replace(/\-(\w)/g, function(o, p) {
                        return p.toUpperCase()
                    });
                    n[l] = e[i]
                }
            }
        } else {
            for (i in e) {
                if (typeof e[i] === 'string') {
                    n[i] = e[i]
                }
            }
        }
        return n
    }
    function f(e) {
        var n, v;
        for (n in e) {
            v = e[n];
            if (v == null || $.isFunction(v) || n in s || (/scrollbar/).test(n) || (!(/color/i).test(n) && isNaN(parseFloat(v)))) {
                delete e[n]
            }
        }
        return e
    }
    function h(o, n) {
        var e = {
            _: 0
        }, i;
        for (i in n) {
            if (o[i] != n[i]) {
                e[i] = n[i]
            }
        }
        return e
    }
    $.effects.animateClass = function(v, e, l, m) {
        if ($.isFunction(l)) {
            m = l;
            l = null
        }
        return this.queue(function() {
            var t = $(this),
                o = t.attr('style') || ' ',
                n = f(d.call(this)),
                p, q = t.attr('class') || "";
            $.each(b, function(i, r) {
                if (v[r]) {
                    t[r + 'Class'](v[r])
                }
            });
            p = f(d.call(this));
            t.attr('class', q);
            t.animate(h(n, p), {
                queue: false,
                duration: e,
                easing: l,
                complete: function() {
                    $.each(b, function(i, r) {
                        if (v[r]) {
                            t[r + 'Class'](v[r])
                        }
                    });
                    if (typeof t.attr('style') == 'object') {
                        t.attr('style').cssText = '';
                        t.attr('style').cssText = o
                    } else {
                        t.attr('style', o)
                    }
                    if (m) {
                        m.apply(this, arguments)
                    }
                    $.dequeue(this)
                }
            })
        })
    };
    $.fn.extend({
        _addClass: $.fn.addClass,
        addClass: function(e, i, l, m) {
            return i ? $.effects.animateClass.apply(this, [{
                add: e
            },
            i, l, m]) : this._addClass(e)
        },
        _removeClass: $.fn.removeClass,
        removeClass: function(e, i, l, m) {
            return i ? $.effects.animateClass.apply(this, [{
                remove: e
            },
            i, l, m]) : this._removeClass(e)
        },
        _toggleClass: $.fn.toggleClass,
        toggleClass: function(e, i, l, m, n) {
            if (typeof i == "boolean" || i === u) {
                if (!l) {
                    return this._toggleClass(e, i)
                } else {
                    return $.effects.animateClass.apply(this, [(i ? {
                        add: e
                    } : {
                        remove: e
                    }), l, m, n])
                }
            } else {
                return $.effects.animateClass.apply(this, [{
                    toggle: e
                },
                i, l, m])
            }
        },
        switchClass: function(r, e, i, l, m) {
            return $.effects.animateClass.apply(this, [{
                add: e,
                remove: r
            },
            i, l, m])
        }
    });
    $.extend($.effects, {
        version: "1.8.23",
        save: function(e, l) {
            for (var i = 0; i < l.length; i++) {
                if (l[i] !== null) e.data("ec.storage." + l[i], e[0].style[l[i]])
            }
        },
        restore: function(e, l) {
            for (var i = 0; i < l.length; i++) {
                if (l[i] !== null) e.css(l[i], e.data("ec.storage." + l[i]))
            }
        },
        setMode: function(e, m) {
            if (m == 'toggle') m = e.is(':hidden') ? 'show' : 'hide';
            return m
        },
        getBaseline: function(o, e) {
            var y, x;
            switch (o[0]) {
                case 'top':
                    y = 0;
                    break;
                case 'middle':
                    y = 0.5;
                    break;
                case 'bottom':
                    y = 1;
                    break;
                default:
                    y = o[0] / e.height
            };
            switch (o[1]) {
                case 'left':
                    x = 0;
                    break;
                case 'center':
                    x = 0.5;
                    break;
                case 'right':
                    x = 1;
                    break;
                default:
                    x = o[1] / e.width
            };
            return {
                x: x,
                y: y
            }
        },
        createWrapper: function(l) {
            if (l.parent().is('.ui-effects-wrapper')) {
                return l.parent()
            }
            var p = {
                width: l.outerWidth(true),
                height: l.outerHeight(true),
                'float': l.css('float')
            }, w = $('<div></div>').addClass('ui-effects-wrapper').css({
                fontSize: '100%',
                background: 'transparent',
                border: 'none',
                margin: 0,
                padding: 0
            }),
                m = document.activeElement;
            try {
                m.id
            } catch (e) {
                m = document.body
            }
            l.wrap(w);
            if (l[0] === m || $.contains(l[0], m)) {
                $(m).focus()
            }
            w = l.parent();
            if (l.css('position') == 'static') {
                w.css({
                    position: 'relative'
                });
                l.css({
                    position: 'relative'
                })
            } else {
                $.extend(p, {
                    position: l.css('position'),
                    zIndex: l.css('z-index')
                });
                $.each(['top', 'left', 'bottom', 'right'], function(i, n) {
                    p[n] = l.css(n);
                    if (isNaN(parseInt(p[n], 10))) {
                        p[n] = 'auto'
                    }
                });
                l.css({
                    position: 'relative',
                    top: 0,
                    left: 0,
                    right: 'auto',
                    bottom: 'auto'
                })
            }
            return w.css(p).show()
        },
        removeWrapper: function(e) {
            var p, i = document.activeElement;
            if (e.parent().is('.ui-effects-wrapper')) {
                p = e.parent().replaceWith(e);
                if (e[0] === i || $.contains(e[0], i)) {
                    $(i).focus()
                }
                return p
            }
            return e
        },
        setTransition: function(e, l, m, v) {
            v = v || {};
            $.each(l, function(i, x) {
                var n = e.cssUnit(x);
                if (n[0] > 0) v[x] = n[0] * m + n[1]
            });
            return v
        }
    });

    function _(e, o, i, l) {
        if (typeof e == 'object') {
            l = o;
            i = null;
            o = e;
            e = o.effect
        }
        if ($.isFunction(o)) {
            l = o;
            i = null;
            o = {}
        }
        if (typeof o == 'number' || $.fx.speeds[o]) {
            l = i;
            i = o;
            o = {}
        }
        if ($.isFunction(i)) {
            l = i;
            i = null
        }
        o = o || {};
        i = i || o.duration;
        i = $.fx.off ? 0 : typeof i == 'number' ? i : i in $.fx.speeds ? $.fx.speeds[i] : $.fx.speeds._default;
        l = l || o.complete;
        return [e, o, i, l]
    }
    function j(e) {
        if (!e || typeof e === "number" || $.fx.speeds[e]) {
            return true
        }
        if (typeof e === "string" && !$.effects[e]) {
            return true
        }
        return false
    }
    $.fn.extend({
        effect: function(e, o, i, l) {
            var m = _.apply(this, arguments),
                n = {
                    options: m[1],
                    duration: m[2],
                    callback: m[3]
                }, p = n.options.mode,
                q = $.effects[e];
            if ($.fx.off || !q) {
                if (p) {
                    return this[p](n.duration, n.callback)
                } else {
                    return this.each(function() {
                        if (n.callback) {
                            n.callback.call(this)
                        }
                    })
                }
            }
            return q.call(this, n)
        },
        _show: $.fn.show,
        show: function(e) {
            if (j(e)) {
                return this._show.apply(this, arguments)
            } else {
                var i = _.apply(this, arguments);
                i[1].mode = 'show';
                return this.effect.apply(this, i)
            }
        },
        _hide: $.fn.hide,
        hide: function(e) {
            if (j(e)) {
                return this._hide.apply(this, arguments)
            } else {
                var i = _.apply(this, arguments);
                i[1].mode = 'hide';
                return this.effect.apply(this, i)
            }
        },
        __toggle: $.fn.toggle,
        toggle: function(e) {
            if (j(e) || typeof e === "boolean" || $.isFunction(e)) {
                return this.__toggle.apply(this, arguments)
            } else {
                var i = _.apply(this, arguments);
                i[1].mode = 'toggle';
                return this.effect.apply(this, i)
            }
        },
        cssUnit: function(e) {
            var l = this.css(e),
                v = [];
            $.each(['em', 'px', '%', 'pt'], function(i, m) {
                if (l.indexOf(m) > 0) v = [parseFloat(l), m]
            });
            return v
        }
    });
    var k = {};
    $.each(["Quad", "Cubic", "Quart", "Quint", "Expo"], function(i, n) {
        k[n] = function(p) {
            return Math.pow(p, i + 2)
        }
    });
    $.extend(k, {
        Sine: function(p) {
            return 1 - Math.cos(p * Math.PI / 2)
        },
        Circ: function(p) {
            return 1 - Math.sqrt(1 - p * p)
        },
        Elastic: function(p) {
            return p === 0 || p === 1 ? p : -Math.pow(2, 8 * (p - 1)) * Math.sin(((p - 1) * 80 - 7.5) * Math.PI / 15)
        },
        Back: function(p) {
            return p * p * (3 * p - 2)
        },
        Bounce: function(p) {
            var e, i = 4;
            while (p < ((e = Math.pow(2, --i)) - 1) / 11) {}
            return 1 / Math.pow(4, 3 - i) - 7.5625 * Math.pow((e * 3 - 2) / 22 - p, 2)
        }
    });
    $.each(k, function(n, e) {
        $.easing["easeIn" + n] = e;
        $.easing["easeOut" + n] = function(p) {
            return 1 - e(1 - p)
        };
        $.easing["easeInOut" + n] = function(p) {
            return p < .5 ? e(p * 2) / 2 : e(p * -2 + 2) / -2 + 1
        }
    })
})(jQuery);