﻿(function($) {
    var a;
    $.mobiscroll.themes.wp = {
        defaults: {
            width: 70,
            height: 76,
            accent: 'none',
            dateOrder: 'mmMMddDDyy',
            showLabel: false,
            btnWidth: false,
            onAnimStart: function(d, i, t) {
                $('.dwwl' + i, d).addClass('wpam');
                clearTimeout(a[i]);
                a[i] = setTimeout(function() {
                    $('.dwwl' + i, d).removeClass('wpam')
                }, t * 1000 + 100)
            }
        },
        load: function(l, s) {
            if (l && l.dateOrder && !s.dateOrder) {
                var o = l.dateOrder;
                o = o.match(/mm/i) ? o.replace(/mmMM|mm|MM/, 'mmMM') : o.replace(/mM|m|M/, 'mM');
                o = o.match(/dd/i) ? o.replace(/ddDD|dd|DD/, 'ddDD') : o.replace(/dD|d|D/, 'dD');
                s.dateOrder = o
            }
        },
        init: function(e, i) {
            var c, b;
            a = {};
            $('.dw', e).addClass('wp-' + i.settings.accent);
            $('.dwwl', e).on('touchstart mousedown DOMMouseScroll mousewheel', '.dw-sel', function() {
                c = true;
                b = $(this).closest('.dwwl').hasClass('wpa');
                $('.dwwl', e).removeClass('wpa');
                $(this).closest('.dwwl').addClass('wpa')
            }).on('touchmove mousemove', function() {
                c = false
            }).on('touchend mouseup', function() {
                if (c && b) {
                    $(this).closest('.dwwl').removeClass('wpa')
                }
            })
        }
    };
    $.mobiscroll.themes['wp light'] = $.mobiscroll.themes.wp
})(jQuery);