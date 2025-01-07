﻿(function($) {
    var a = {
        inputClass: '',
        invalid: [],
        rtl: false,
        group: false,
        groupLabel: 'Groups'
    };
    $.mobiscroll.presetShort('select');
    $.mobiscroll.presets.select = function(b) {
        var o = $.extend({}, b.settings),
            s = $.extend(b.settings, a, o),
            c = $(this),
            m = c.prop('multiple'),
            f = this.id + '_dummy',
            g = m ? (c.val() ? c.val()[0] : $('option', c).attr('value')) : c.val(),
            h = c.find('option[value="' + g + '"]').parent(),
            p = h.index() + '',
            k = p,
            l, n = $('label[for="' + this.id + '"]').attr('for', f),
            q = $('label[for="' + f + '"]'),
            r = s.label !== undefined ? s.label : (q.length ? q.text() : c.attr('name')),
            u = [],
            x = [],
            y = {}, z, A, B, C, D = s.readonly,
            w;

        function E() {
            var d, e = 0,
                j = [],
                t = [],
                w = [
                    []
                ];
            if (s.group) {
                if (s.rtl) {
                    e = 1
                }
                $('optgroup', c).each(function(i) {
                    j.push($(this).attr('label'));
                    t.push(i)
                });
                w[e] = [{
                    values: j,
                    keys: t,
                    label: s.groupLabel
                }];
                d = h;
                e += (s.rtl ? -1 : 1)
            } else {
                d = c
            }
            j = [];
            t = [];
            $('option', d).each(function() {
                var v = $(this).attr('value');
                j.push($(this).text());
                t.push(v);
                if ($(this).prop('disabled')) {
                    u.push(v)
                }
            });
            w[e] = [{
                values: j,
                keys: t,
                label: r
            }];
            return w
        }
        function F(v, d) {
            var e = [];
            if (m) {
                var j = [],
                    i = 0;
                for (i in b._selectedValues) {
                    j.push(y[i]);
                    e.push(i)
                }
                C.val(j.join(', '))
            } else {
                C.val(v);
                e = d ? b.values[A] : null
            }
            if (d) {
                l = true;
                c.val(e).change()
            }
        }
        function G(d) {
            if (m && d.hasClass('dw-v') && d.closest('.dw').find('.dw-ul').index(d.closest('.dw-ul')) == A) {
                var e = d.attr('data-val'),
                    j = d.hasClass('dw-msel');
                if (j) {
                    d.removeClass('dw-msel').removeAttr('aria-selected');
                    delete b._selectedValues[e]
                } else {
                    d.addClass('dw-msel').attr('aria-selected', 'true');
                    b._selectedValues[e] = e
                }
                if (b.live) {
                    F(e, true)
                }
                return false
            }
        }
        if (s.group && !$('optgroup', c).length) {
            s.group = false
        }
        if (!s.invalid.length) {
            s.invalid = u
        }
        if (s.group) {
            if (s.rtl) {
                z = 1;
                A = 0
            } else {
                z = 0;
                A = 1
            }
        } else {
            z = -1;
            A = 0
        }
        $('#' + f).remove();
        C = $('<input type="text" id="' + f + '" class="' + s.inputClass + '" readonly />').insertBefore(c);
        $('option', c).each(function() {
            y[$(this).attr('value')] = $(this).text()
        });
        b.attachShow(C);
        var v = c.val() || [],
            i = 0;
        for (i; i < v.length; i++) {
            b._selectedValues[v[i]] = v[i]
        }
        F(y[g]);
        c.off('.dwsel').on('change.dwsel', function() {
            if (!l) {
                b.setValue(m ? c.val() || [] : [c.val()], true)
            }
            l = false
        }).hide().closest('.ui-field-contain').trigger('create');
        if (!b._setValue) {
            b._setValue = b.setValue
        }
        b.setValue = function(d, e, t, j, H, I) {
            var J, v = $.isArray(d) ? d[0] : d;
            g = v !== undefined ? v : $('option', c).attr('value');
            if (m) {
                b._selectedValues = {};
                var i = 0;
                for (i; i < d.length; i++) {
                    b._selectedValues[d[i]] = d[i]
                }
            }
            if (s.group) {
                h = c.find('option[value="' + g + '"]').parent();
                k = h.index();
                J = s.rtl ? [g, h.index()] : [h.index(), g];
                if (k !== p) {
                    s.wheels = E();
                    b.changeWheel([A], undefined, I);
                    p = k + ''
                }
            } else {
                J = [g]
            }
            b._setValue(J, e, t, j, H, I);
            if (e) {
                var K = m ? true : g !== c.val();
                F(y[g], K)
            }
        };
        b.getValue = function(t) {
            var d = t ? b.temp : b.values;
            return d[A]
        };
        return {
            width: 50,
            wheels: w,
            headerText: false,
            multiple: m,
            anchor: C,
            formatResult: function(d) {
                return y[d[A]]
            },
            parseValue: function() {
                var v = c.val() || [],
                    i = 0;
                if (m) {
                    b._selectedValues = {};
                    for (i; i < v.length; i++) {
                        b._selectedValues[v[i]] = v[i]
                    }
                }
                g = m ? (c.val() ? c.val()[0] : $('option', c).attr('value')) : c.val();
                h = c.find('option[value="' + g + '"]').parent();
                k = h.index();
                p = k + '';
                return s.group && s.rtl ? [g, k] : s.group ? [k, g] : [g]
            },
            validate: function(d, i, e) {
                if (i === undefined && m) {
                    var v = b._selectedValues,
                        j = 0;
                    $('.dwwl' + A + ' .dw-li', d).removeClass('dw-msel').removeAttr('aria-selected');
                    for (j in v) {
                        $('.dwwl' + A + ' .dw-li[data-val="' + v[j] + '"]', d).addClass('dw-msel').attr('aria-selected', 'true')
                    }
                }
                if (i === z) {
                    k = b.temp[z];
                    if (k !== p) {
                        h = c.find('optgroup').eq(k);
                        k = h.index();
                        g = h.find('option').eq(0).val();
                        g = g || c.val();
                        s.wheels = E();
                        if (s.group) {
                            b.temp = s.rtl ? [g, k] : [k, g];
                            s.readonly = [s.rtl, !s.rtl];
                            clearTimeout(B);
                            B = setTimeout(function() {
                                b.changeWheel([A], undefined, true);
                                s.readonly = D;
                                p = k + ''
                            }, e * 1000);
                            return false
                        }
                    } else {
                        s.readonly = D
                    }
                } else {
                    g = b.temp[A]
                }
                var t = $('.dw-ul', d).eq(A);
                $.each(s.invalid, function(i, v) {
                    $('.dw-li[data-val="' + v + '"]', t).removeClass('dw-v')
                })
            },
            onBeforeShow: function(d) {
                s.wheels = E();
                if (s.group) {
                    b.temp = s.rtl ? [g, h.index()] : [h.index(), g]
                }
            },
            onMarkupReady: function(d) {
                d.addClass('dw-select');
                $('.dwwl' + z, d).on('mousedown touchstart', function() {
                    clearTimeout(B)
                });
                if (m) {
                    d.addClass('dwms');
                    $('.dwwl', d).eq(A).addClass('dwwms').attr('aria-multiselectable', 'true');
                    $('.dwwl', d).on('keydown', function(e) {
                        if (e.keyCode == 32) {
                            e.preventDefault();
                            e.stopPropagation();
                            G($('.dw-sel', this))
                        }
                    });
                    x = $.extend({}, b._selectedValues)
                }
            },
            onValueTap: G,
            onSelect: function(v) {
                F(v, true);
                if (s.group) {
                    b.values = null
                }
            },
            onCancel: function() {
                if (s.group) {
                    b.values = null
                }
                if (!b.live && m) {
                    b._selectedValues = $.extend({}, x)
                }
            },
            onChange: function(v) {
                if (b.live && !m) {
                    C.val(v);
                    l = true;
                    c.val(b.temp[A]).change()
                }
            },
            onDestroy: function() {
                C.remove();
                c.show()
            }
        }
    }
})(jQuery);