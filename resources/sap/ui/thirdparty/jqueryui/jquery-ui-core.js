﻿/*!
 * jQuery UI 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI
 */

(function($, u) {
    $.ui = $.ui || {};
    if ($.ui.version) {
        return
    }
    $.extend($.ui, {
        version: "1.8.23",
        keyCode: {
            ALT: 18,
            BACKSPACE: 8,
            CAPS_LOCK: 20,
            COMMA: 188,
            COMMAND: 91,
            COMMAND_LEFT: 91,
            COMMAND_RIGHT: 93,
            CONTROL: 17,
            DELETE: 46,
            DOWN: 40,
            END: 35,
            ENTER: 13,
            ESCAPE: 27,
            HOME: 36,
            INSERT: 45,
            LEFT: 37,
            MENU: 93,
            NUMPAD_ADD: 107,
            NUMPAD_DECIMAL: 110,
            NUMPAD_DIVIDE: 111,
            NUMPAD_ENTER: 108,
            NUMPAD_MULTIPLY: 106,
            NUMPAD_SUBTRACT: 109,
            PAGE_DOWN: 34,
            PAGE_UP: 33,
            PERIOD: 190,
            RIGHT: 39,
            SHIFT: 16,
            SPACE: 32,
            TAB: 9,
            UP: 38,
            WINDOWS: 91
        }
    });
    $.fn.extend({
        propAttr: $.fn.prop || $.fn.attr,
        _focus: $.fn.focus,
        focus: function(d, a) {
            return typeof d === "number" ? this.each(function() {
                var e = this;
                setTimeout(function() {
                    $(e).focus();
                    if (a) {
                        a.call(e)
                    }
                }, d)
            }) : this._focus.apply(this, arguments)
        },
        scrollParent: function() {
            var s;
            if (($.browser.msie && (/(static|relative)/).test(this.css('position'))) || (/absolute/).test(this.css('position'))) {
                s = this.parents().filter(function() {
                    return (/(relative|absolute|fixed)/).test($.curCSS(this, 'position', 1)) && (/(auto|scroll)/).test($.curCSS(this, 'overflow', 1) + $.curCSS(this, 'overflow-y', 1) + $.curCSS(this, 'overflow-x', 1))
                }).eq(0)
            } else {
                s = this.parents().filter(function() {
                    return (/(auto|scroll)/).test($.curCSS(this, 'overflow', 1) + $.curCSS(this, 'overflow-y', 1) + $.curCSS(this, 'overflow-x', 1))
                }).eq(0)
            }
            return (/fixed/).test(this.css('position')) || !s.length ? $(document) : s
        },
        zIndex: function(z) {
            if (z !== u) {
                return this.css("zIndex", z)
            }
            if (this.length) {
                var e = $(this[0]),
                    p, a;
                while (e.length && e[0] !== document) {
                    p = e.css("position");
                    if (p === "absolute" || p === "relative" || p === "fixed") {
                        a = parseInt(e.css("zIndex"), 10);
                        if (!isNaN(a) && a !== 0) {
                            return a
                        }
                    }
                    e = e.parent()
                }
            }
            return 0
        },
        disableSelection: function() {
            return this.bind(($.support.selectstart ? "selectstart" : "mousedown") + ".ui-disableSelection", function(e) {
                e.preventDefault()
            })
        },
        enableSelection: function() {
            return this.unbind(".ui-disableSelection")
        }
    });
    if (!$("<a>").outerWidth(1).jquery) {
        $.each(["Width", "Height"], function(i, n) {
            var s = n === "Width" ? ["Left", "Right"] : ["Top", "Bottom"],
                t = n.toLowerCase(),
                o = {
                    innerWidth: $.fn.innerWidth,
                    innerHeight: $.fn.innerHeight,
                    outerWidth: $.fn.outerWidth,
                    outerHeight: $.fn.outerHeight
                };

            function r(e, a, b, m) {
                $.each(s, function() {
                    a -= parseFloat($.curCSS(e, "padding" + this, true)) || 0;
                    if (b) {
                        a -= parseFloat($.curCSS(e, "border" + this + "Width", true)) || 0
                    }
                    if (m) {
                        a -= parseFloat($.curCSS(e, "margin" + this, true)) || 0
                    }
                });
                return a
            }
            $.fn["inner" + n] = function(a) {
                if (a === u) {
                    return o["inner" + n].call(this)
                }
                return this.each(function() {
                    $(this).css(t, r(this, a) + "px")
                })
            };
            $.fn["outer" + n] = function(a, m) {
                if (typeof a !== "number") {
                    return o["outer" + n].call(this, a)
                }
                return this.each(function() {
                    $(this).css(t, r(this, a, true, m) + "px")
                })
            }
        })
    }
    function f(e, i) {
        var n = e.nodeName.toLowerCase();
        if ("area" === n) {
            var m = e.parentNode,
                a = m.name,
                b;
            if (!e.href || !a || m.nodeName.toLowerCase() !== "map") {
                return false
            }
            b = $("img[usemap=#" + a + "]")[0];
            return !!b && v(b)
        }
        return (/input|select|textarea|button|object/.test(n) ? !e.disabled : "a" == n ? e.href || i : i) && v(e)
    }
    function v(e) {
        return !$(e).parents().andSelf().filter(function() {
            return $.curCSS(this, "visibility") === "hidden" || $.expr.filters.hidden(this)
        }).length
    }
    $.extend($.expr[":"], {
        data: $.expr.createPseudo ? $.expr.createPseudo(function(d) {
            return function(e) {
                return !!$.data(e, d)
            }
        }) : function(e, i, m) {
            return !!$.data(e, m[3])
        },
        focusable: function(e) {
            return f(e, !isNaN($.attr(e, "tabindex")))
        },
        tabbable: function(e) {
            var t = $.attr(e, "tabindex"),
                i = isNaN(t);
            return (i || t >= 0) && f(e, !i)
        }
    });
    $(function() {
        var b = document.body,
            d = b.appendChild(d = document.createElement("div"));
        d.offsetHeight;
        $.extend(d.style, {
            minHeight: "100px",
            height: "auto",
            padding: 0,
            borderWidth: 0
        });
        $.support.minHeight = d.offsetHeight === 100;
        $.support.selectstart = "onselectstart" in d;
        b.removeChild(d).style.display = "none"
    });
    if (!$.curCSS) {
        $.curCSS = $.css
    }
    $.extend($.ui, {
        plugin: {
            add: function(m, o, s) {
                var p = $.ui[m].prototype;
                for (var i in s) {
                    p.plugins[i] = p.plugins[i] || [];
                    p.plugins[i].push([o, s[i]])
                }
            },
            call: function(a, n, b) {
                var s = a.plugins[n];
                if (!s || !a.element[0].parentNode) {
                    return
                }
                for (var i = 0; i < s.length; i++) {
                    if (a.options[s[i][0]]) {
                        s[i][1].apply(a.element, b)
                    }
                }
            }
        },
        contains: function(a, b) {
            return document.compareDocumentPosition ? a.compareDocumentPosition(b) & 16 : a !== b && a.contains(b)
        },
        hasScroll: function(e, a) {
            if ($(e).css("overflow") === "hidden") {
                return false
            }
            var s = (a && a === "left") ? "scrollLeft" : "scrollTop",
                h = false;
            if (e[s] > 0) {
                return true
            }
            e[s] = 1;
            h = (e[s] > 0);
            e[s] = 0;
            return h
        },
        isOverAxis: function(x, r, s) {
            return (x > r) && (x < (r + s))
        },
        isOver: function(y, x, t, l, h, w) {
            return $.ui.isOverAxis(y, t, h) && $.ui.isOverAxis(x, l, w)
        }
    })
})(jQuery);