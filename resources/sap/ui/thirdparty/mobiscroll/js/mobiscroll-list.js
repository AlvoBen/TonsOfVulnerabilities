﻿(function($) {
    var m = $.mobiscroll,
        d = {
            invalid: [],
            showInput: true,
            inputClass: ''
        }, p = function(b) {
            var e = $.extend({}, b.settings),
                s = $.extend(b.settings, d, e),
                f = $(this),
                g, h, k = this.id + '_dummy',
                q = 0,
                r = 0,
                u = {}, x = s.wheelArray || L(f),
                y = E(q),
                z = [],
                A = J(x),
                w = F(A, q);

            function B(a, n, c, j) {
                var i = 0;
                while (i < n) {
                    var l = $('.dwwl' + i, a),
                        o = C(j, i, c);
                    $.each(o, function(i, v) {
                        $('.dw-li[data-val="' + v + '"]', l).removeClass('dw-v')
                    });
                    i++
                }
            }
            function C(a, c, j) {
                var i = 0,
                    n, l = j,
                    o = [];
                while (i < c) {
                    var t = a[i];
                    for (n in l) {
                        if (l[n].key == t) {
                            l = l[n].children;
                            break
                        }
                    }
                    i++
                }
                i = 0;
                while (i < l.length) {
                    if (l[i].invalid) {
                        o.push(l[i].key)
                    }
                    i++
                }
                return o
            }
            function D(n, i) {
                var a = [];
                while (n) {
                    a[--n] = true
                }
                a[i] = false;
                return a
            }
            function E(l) {
                var a = [],
                    i;
                for (i = 0; i < l; i++) {
                    a[i] = s.labels && s.labels[i] ? s.labels[i] : i
                }
                return a
            }
            function F(a, l, c) {
                var i = 0,
                    j, o, n, w = [],
                    t = x;
                if (l) {
                    for (j = 0; j < l; j++) {
                        w[j] = [{}]
                    }
                }
                while (i < a.length) {
                    w[i] = [H(t, y[i])];
                    j = 0;
                    n = undefined;
                    while (j < t.length && n === undefined) {
                        if (t[j].key == a[i] && ((c !== undefined && i <= c) || c === undefined)) {
                            n = j
                        }
                        j++
                    }
                    if (n !== undefined && t[n].children) {
                        i++;
                        t = t[n].children
                    } else if ((o = G(t)) && o.children) {
                        i++;
                        t = o.children
                    } else {
                        return w
                    }
                }
                return w
            }
            function G(a, c) {
                if (!a) {
                    return false
                }
                var i = 0,
                    o;
                while (i < a.length) {
                    if (!(o = a[i++]).invalid) {
                        return c ? i - 1 : o
                    }
                }
                return false
            }
            function H(o, l) {
                var a = {
                    keys: [],
                    values: [],
                    label: l
                }, j = 0;
                while (j < o.length) {
                    a.values.push(o[j].value);
                    a.keys.push(o[j].key);
                    j++
                }
                return a
            }
            function I(a, i) {
                $('.dwc', a).css('display', '').slice(i).hide()
            }
            function J(x) {
                var t = [],
                    n = x,
                    o, a = true,
                    i = 0;
                while (a) {
                    o = G(n);
                    t[i++] = o.key;
                    a = o.children;
                    if (a) {
                        n = a
                    }
                }
                return t
            }
            function K(a, c) {
                var t = [],
                    n = x,
                    q = 0,
                    j = false,
                    i, l, o;
                if (a[q] !== undefined && q <= c) {
                    i = 0;
                    l = a[q];
                    o = undefined;
                    while (i < n.length && o === undefined) {
                        if (n[i].key == a[q] && !n[i].invalid) {
                            o = i
                        }
                        i++
                    }
                } else {
                    o = G(n, true);
                    l = n[o].key
                }
                j = o !== undefined ? n[o].children : false;
                t[q] = l;
                while (j) {
                    n = n[o].children;
                    q++;
                    j = false;
                    o = undefined;
                    if (a[q] !== undefined && q <= c) {
                        i = 0;
                        l = a[q];
                        o = undefined;
                        while (i < n.length && o === undefined) {
                            if (n[i].key == a[q] && !n[i].invalid) {
                                o = i
                            }
                            i++
                        }
                    } else {
                        o = G(n, true);
                        o = o === false ? undefined : o;
                        l = n[o].key
                    }
                    j = o !== undefined && G(n[o].children) ? n[o].children : false;
                    t[q] = l
                }
                return {
                    lvl: q + 1,
                    nVector: t
                }
            }
            function L(a) {
                var i = [];
                q = q > r++ ? q : r;
                a.children('li').each(function(j) {
                    var t = $(this),
                        c = t.clone();
                    c.children('ul,ol').remove();
                    var v = c.html().replace(/^\s\s*/, '').replace(/\s\s*$/, ''),
                        l = t.data('invalid') ? true : false,
                        n = {
                            key: t.data('val') || j,
                            value: v,
                            invalid: l,
                            children: null
                        }, o = t.children('ul,ol');
                    if (o.length) {
                        n.children = L(o)
                    }
                    i.push(n)
                });
                r--;
                return i
            }
            $('#' + k).remove();
            if (s.showInput) {
                g = $('<input type="text" id="' + k + '" value="" class="' + s.inputClass + '" readonly />').insertBefore(f);
                s.anchor = g;
                b.attachShow(g)
            }
            if (!s.wheelArray) {
                f.hide().closest('.ui-field-contain').trigger('create')
            }
            return {
                width: 50,
                wheels: w,
                headerText: false,
                parseValue: function(v, b) {
                    return s.defaultValue || A
                },
                onBeforeShow: function(a) {
                    var t = b.temp;
                    z = t.slice(0);
                    s.wheels = F(t, q, q);
                    h = true
                },
                onSelect: function(v, b) {
                    if (g) {
                        g.val(v)
                    }
                },
                onChange: function(v, b) {
                    if (g && b.live) {
                        g.val(v)
                    }
                },
                onShow: function(a) {
                    $('.dwwl', a).on('mousedown touchstart', function() {
                        clearTimeout(u[$('.dwwl', a).index(this)])
                    })
                },
                onDestroy: function() {
                    if (g) {
                        g.remove()
                    }
                    f.show()
                },
                validate: function(a, c, j) {
                    var l = [],
                        t = b.temp,
                        i = (c || 0) + 1,
                        o;
                    if ((c !== undefined && z[c] != t[c]) || (c === undefined && !h)) {
                        s.wheels = F(t, null, c);
                        o = K(t, c);
                        if (c !== undefined) {
                            b.temp = o.nVector.slice(0)
                        }
                        while (i < o.lvl) {
                            l.push(i++)
                        }
                        I(a, o.lvl);
                        z = b.temp.slice(0);
                        if (l.length) {
                            h = true;
                            s.readonly = D(q, c);
                            clearTimeout(u[c]);
                            u[c] = setTimeout(function() {
                                b.changeWheel(l, undefined, c !== undefined);
                                s.readonly = false
                            }, j * 1000);
                            return false
                        }
                        B(a, o.lvl, x, b.temp)
                    } else {
                        o = K(t, t.length);
                        B(a, o.lvl, x, t);
                        I(a, o.lvl)
                    }
                    h = false
                }
            }
        };
    $.each(['list', 'image', 'treelist'], function(i, v) {
        m.presets[v] = p;
        m.presetShort(v)
    })
})(jQuery);