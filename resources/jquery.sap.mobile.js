﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("jquery.sap.mobile", false);
jQuery.sap.require("jquery.sap.dom");
jQuery.sap.require("jquery.sap.events");
jQuery.sap.require("sap.ui.Device");
(function($) {
    var F = /(?:\?|&)sap-ui-xx-fakeOS=([^&]+)/,
        m = undefined;
    $.sap.simulateMobileOnDesktop = false;
    if ((jQuery.browser.webkit || (jQuery.browser.msie && parseInt(jQuery.browser.version, 10) >= 10)) && !jQuery.support.touch) {
        var r = document.location.search.match(F);
        var a = r && r[1] || jQuery.sap.byId("sap-ui-bootstrap").attr("data-sap-ui-xx-fakeOS");
        if (a) {
            $.sap.simulateMobileOnDesktop = true;
            var u = {
                ios: "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0_1 like Mac OS X) AppleWebKit/534.48 (KHTML, like Gecko) Version/5.1 Mobile/9A406 Safari/7534.48.3",
                iphone: "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0_1 like Mac OS X) AppleWebKit/534.48 (KHTML, like Gecko) Version/5.1 Mobile/9A406 Safari/7534.48.3",
                ipad: "Mozilla/5.0 (iPad; CPU OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Mobile/9B206",
                android: "Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; GT-I9100 Build/IML74K) AppleWebKit/534.46 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.46",
                android_phone: "Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; GT-I9100 Build/IML74K) AppleWebKit/534.46 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.46",
                android_tablet: "Mozilla/5.0 (Linux; Android 4.1.2; Nexus 7 Build/JZ054K) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19",
                blackberry: "Mozilla/5.0 (BB10; Touch) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.0.9.2372 Mobile Safari/537.10+",
                winphone: "Mozilla/5.0 (compatible; MSIE 10.0; Windows Phone 8.0; Trident/6.0; IEMobile/10.0; ARM; Touch; NOKIA; Lumia 920)"
            }[a];
            if (u && (jQuery.browser.webkit && a !== "winphone" || jQuery.browser.msie && a === "winphone")) {
                m = {
                    ios: "'Helvetica Neue'",
                    android: "Roboto,'Droid Sans'",
                    blackberry: "'BBGlobal Sans','DejaVu Sans'",
                    winphone: "'Segoe WP', 'Segoe UI'"
                };
                if (jQuery.browser.safari) {
                    var _ = window.navigator;
                    window.navigator = new Object();
                    window.navigator.__proto__ = _;
                    window.navigator.__defineGetter__('userAgent', function() {
                        return u
                    })
                } else {
                    Object.defineProperty(navigator, "userAgent", {
                        get: function() {
                            return u
                        }
                    })
                }
                if (jQuery.browser.webkit) {
                    jQuery.browser.msie = jQuery.browser.opera = jQuery.browser.mozilla = false;
                    jQuery.browser.webkit = true;
                    jQuery.browser.version = "534.46"
                } else {}
                sap.ui.Device._update($.sap.simulateMobileOnDesktop)
            }
        }
    }
    function g(e) {
        e = e || navigator.userAgent;
        var p = /\(([a-zA-Z ]+);\s(?:[U]?[;]?)([\D]+)((?:[\d._]*))(?:.*[\)][^\d]*)([\d.]*)\s/;
        var r = e.match(p);
        if (r) {
            var j = /iPhone|iPad|iPod/;
            var k = /PlayBook|BlackBerry/;
            if (r[0].match(j)) {
                r[3] = r[3].replace(/_/g, ".");
                return ({
                    os: "ios",
                    version: r[3]
                })
            } else if (r[2].match(/Android/)) {
                r[2] = r[2].replace(/\s/g, "");
                return ({
                    os: "android",
                    version: r[3]
                })
            } else if (r[0].match(k)) {
                return ({
                    os: "blackberry",
                    version: r[4]
                })
            } else {
                return
            }
        } else if (e.indexOf("(BB10;") > 0) {
            p = /\sVersion\/([\d.]+)\s/;
            r = e.match(p);
            if (r) {
                return {
                    os: "blackberry",
                    version: r[1]
                }
            } else {
                return {
                    os: "blackberry",
                    version: 10
                }
            }
        } else {
            p = /Windows Phone (?:OS )?([\d.]*)/;
            r = e.match(p);
            if (r) {
                return {
                    os: "winphone",
                    version: r[1]
                }
            } else {
                return
            }
        }
    }
    var o = g() || {}, h = window.document.documentElement,
        d = 0,
        D = 0;

    function s() {
        d = h.clientWidth;
        D = h.clientHeight
    }
    if (o.os) {
        var f = parseFloat(o.version);
        $.os = $.extend({
            os: o.os,
            version: o.version,
            fVersion: f
        }, $.os);
        $.os[o.os] = true
    } else {
        if (!$.os) $.os = {}
    }
    if (m) {}
    $.extend($.support, {
        retina: window.devicePixelRatio >= 2
    });
    var A = null;

    function i() {
        if (jQuery.support.touch) {
            if (A) {
                return $.device.is.landscape
            }
            A = window.setTimeout(function() {
                A = null
            }, 50)
        }
        var w = h.clientWidth,
            H = h.clientHeight,
            k = false;
        if ($.support.touch) {
            if ((w === d) && (H !== D)) {
                k = true
            }
        }
        d = w;
        D = H;
        return k ? $.device.is.landscape : w > H
    }
    var l = h.clientWidth > h.clientHeight;
    var b = (/(?=android)(?=.*mobile)/i.test(navigator.userAgent));
    $.device = $.extend({}, $.device);
    $.device.is = $.extend({
        standalone: window.navigator.standalone,
        landscape: l,
        portrait: !l,
        iphone: /iphone/i.test(navigator.userAgent),
        ipad: /ipad/i.test(navigator.userAgent),
        android_phone: b,
        android_tablet: ( !! $.os.android && !b),
        tablet: sap.ui.Device.system.tablet,
        phone: sap.ui.Device.system.phone,
        desktop: sap.ui.Device.system.desktop
    }, $.device.is);
    $(window).bind("resize", function() {
        var l = i();
        $.device.is.landscape = l;
        $.device.is.portrait = !l
    });
    var c = false;
    $.sap.initMobile = function(e) {
        var j = $("head");
        if (!c) {
            c = true;
            e = $.extend({}, {
                viewport: true,
                statusBar: "default",
                hideBrowser: true,
                preventScroll: true,
                preventPhoneNumberDetection: true,
                useFullScreenHeight: true,
                homeIconPrecomposed: false
            }, e);
            if ($.os.ios && e.preventPhoneNumberDetection) {
                j.append($('<meta name="format-detection" content="telephone=no">'))
            } else if ($.browser.msie) {
                j.append($('<meta http-equiv="cleartype" content="on">'));
                j.append($('<meta name="msapplication-tap-highlight" content="no"/>'))
            }
            var I = sap.ui.Device.os.ios && sap.ui.Device.os.version >= 7 && sap.ui.Device.os.version < 8 && sap.ui.Device.browser.name === "sf";
            if (e.viewport) {
                var M;
                if (I && sap.ui.Device.system.phone) {
                    M = 'initial-scale=1.0,maximum-scale=1.0,user-scalable=0'
                } else if ($.device.is.iphone && (Math.max(window.screen.height, window.screen.width) === 568)) {
                    M = "user-scalable=0, initial-scale=1.0"
                } else if ($.os.android && $.os.fVersion < 3) {
                    M = "width=device-width, height=device-height, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"
                } else if ($.os.winphone) {
                    M = "width=320, user-scalable=no"
                } else {
                    M = "width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"
                }
                j.append($('<meta name="viewport" content="' + M + '">'))
            }
            if ($.os.ios) {
                j.append($('<meta name="apple-mobile-web-app-capable" content="yes">'));
                j.append($('<meta name="apple-mobile-web-app-status-bar-style" content="' + e.statusBar + '">'))
            }
            if (e.preventScroll) {
                $(window).bind("touchmove", function sapInitMobileTouchMoveHandle(E) {
                    if (!E.isDefaultPrevented()) {
                        E.preventDefault()
                    }
                })
            }
            if (e.useFullScreenHeight) {
                $(function() {
                    document.documentElement.style.height = "100%"
                })
            }
            s()
        }
        if (e.homeIcon) {
            var k;
            if (typeof e.homeIcon === "string") {
                k = {
                    phone: e.homeIcon
                }
            } else {
                k = $.extend({}, e.homeIcon)
            }
            k.precomposed = e.homeIconPrecomposed || k.precomposed;
            k.favicon = e.homeIcon.icon || k.favicon;
            k.icon = undefined;
            $.sap.setIcons(k)
        }
    };
    $.sap.setIcons = function(I) {
        if (!I || (typeof I !== "object")) {
            $.sap.log.warning("Call to jQuery.sap.setIcons() has been ignored because there were no icons given or the argument was not an object.");
            return
        }
        var e = $("head"),
            p = I.precomposed ? "-precomposed" : "",
            j = function(t) {
                return I[t] || I['tablet@2'] || I['phone@2'] || I['phone'] || I['tablet']
            }, S = {
                "phone": "",
                "tablet": "72x72",
                "phone@2": "114x114",
                "tablet@2": "144x144"
            };
        if (I["favicon"]) {
            var k = e.find("[rel^=shortcut]");
            k.each(function() {
                if (this.rel === "shortcut icon") {
                    $(this).remove()
                }
            });
            e.append($('<link rel="shortcut icon" href="' + I["favicon"] + '" />'))
        }
        if (j("phone")) {
            e.find("[rel=apple-touch-icon]").remove();
            e.find("[rel=apple-touch-icon-precomposed]").remove()
        }
        for (var n in S) {
            I[n] = I[n] || j(n);
            if (I[n]) {
                var q = S[n];
                e.append($('<link rel="apple-touch-icon' + p + '" ' + (q ? 'sizes="' + q + '"' : "") + ' href="' + I[n] + '" />'))
            }
        }
    }
})(jQuery);