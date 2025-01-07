﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

(function($, D) {
    var o = "",
        a = {}, c = sap.ui.getCore(),
        l = sap.m.getLocale(),
        L = l.getLanguage(),
        b = sap.m.getLocaleData(),
        r = c.getLibraryResourceBundle("sap.m"),
        _ = function(T) {
            return r.getText("MOBISCROLL_" + T)
        }, f = "(?=([^']*'[^']*')*[^']*$)",
        C = $.sap.getModulePath("sap.ui.thirdparty.mobiscroll", "/css/"),
        s = {
            endYear: new Date().getFullYear() + 10,
            lang: L
        }, g = {
            setText: _("SET"),
            cancelText: _("CANCEL"),
            monthText: _("MONTH"),
            dayText: _("DAY"),
            yearText: _("YEAR"),
            hourText: _("HOURS"),
            minuteText: _("MINUTES"),
            secText: _("SECONDS"),
            nowText: _("NOW"),
            dayNames: b.getDaysStandAlone("wide"),
            dayNamesShort: b.getDaysStandAlone("abbreviated"),
            monthNames: b.getMonthsStandAlone("wide"),
            monthNamesShort: b.getMonthsStandAlone("abbreviated")
        }, t = sap.ui.core.theming.Parameters.get();
    $.sap.includeStyleSheet(C + "mobiscroll-core.css");
    $.sap.require("sap.ui.thirdparty.mobiscroll.js.mobiscroll-core");
    $.sap.require("sap.ui.thirdparty.mobiscroll.js.mobiscroll-datetime");
    a = $("<input>").scroller({}).scroller("getInst").settings;
    if (t["sapMPlatformDependent"] != "true") {
        var h = ["phone", "tablet", "desktop"].filter(function(d) {
            return D.system[d]
        })[0],
            u = function(d) {
                if (!d) {
                    return ""
                }
                return d.charAt(0).toUpperCase() + d.substr(1)
            }, i = function(k, d, p) {
                var v = t["sapUiDTICustom" + u(p) + u(k)];
                if (v) {
                    if (d == "bool") {
                        s[k] = (v.toLowerCase() == "true" ? true : false)
                    } else if (d == "int") {
                        v = parseInt(v, 10);
                        !isNaN(v) && (s[k] = v)
                    } else {
                        s[k] = v
                    }
                }
                if (!p && h) {
                    i(k, d, h)
                }
            };
        s.mode = "mixed";
        s.display = "modal";
        s.theme = "sapMDTICustom";
        i("mode");
        i("display");
        i("animate");
        i("rows", "int");
        i("width", "int");
        i("height", "int");
        i("showLabel", "bool");
        i("headerText", "bool");
        if (s.headerText) {
            s.headerText = "{value}"
        }
        $.sap.require("sap.ui.core.IconPool");
        sap.ui.core.IconPool.insertFontFaceStyle()
    } else if (D.os.ios) {
        o = "ios";
        s.display = "bubble"
    } else if (D.os.android) {
        o = "android-ics";
        if (D.os.version == 2.3) {
            s.mode = "clickpick";
            s.rows = 3
        } else if (D.os.version == 3.2) {
            s.mode = "scroller"
        }
    } else if (D.browser.internet_explorer) {
        o = "wp"
    }
    $.scroller.i18n[L] = $.extend(g);
    if (o) {
        s.theme = (D.os.android) ? o + " light" : o;
        $.sap.includeStyleSheet(C + "mobiscroll-" + o + ".css");
        $.sap.require("sap.ui.thirdparty.mobiscroll.js.mobiscroll-" + o);
        s = $.extend({}, $.mobiscroll.themes[s.theme].defaults, s)
    }
    $.sap.require("sap.m.InstanceManager");
    $.extend(sap.m.DateTimeInput.prototype, {
        close: function() {
            this._$input.scroller("hide")
        },
        _setScrollerHeader: function(v) {
            try {
                var d = this._$input.scroller("getInst").settings,
                    F = !this.getType().indexOf("Date") ? d.dateFormat : d.timeFormat,
                    j = $.mobiscroll.parseDate(F, v);
                return sap.ui.core.format.DateFormat.getDateInstance({
                    pattern: this.getDisplayFormat()
                }).format(j)
            } catch (e) {
                return v
            }
        },
        _autoClose: function(e) {
            var d = this.getDomRef();
            if (d && d.contains(e.target)) {
                e.stopPropagation();
                e.preventDefault();
                return
            }
            var j = document.querySelector(".sapMDTICustom .dwwr");
            if (j && !j.contains(e.target)) {
                this._$input.scroller("hide")
            }
        },
        _restrictMaxWidth: function(d) {
            d[0].querySelector(".dwwr").style.maxWidth = (document.documentElement.clientWidth - 22) + "px"
        },
        _handleResize: function(e) {
            this._restrictMaxWidth(e.data.$dialog)
        },
        _handleBtnKeyDown: function(e) {
            if (e.keyCode === jQuery.sap.KeyCodes.ENTER) {
                if (e.target && jQuery(e.target.parentElement).hasClass("dwb-c")) {
                    this._$input.scroller("cancel")
                } else {
                    this._$input.scroller("select")
                }
            } else if (e.keyCode === jQuery.sap.KeyCodes.ESCAPE) {
                this._$input.scroller("cancel")
            }
        },
        _getScrollerConfig: function() {
            var d = this,
                T = this.getType(),
                F = this.getDisplayFormat(),
                A = $.proxy(this._autoClose, this),
                H = $.proxy(this._handleResize, this),
                e = $.proxy(this._handleBtnKeyDown, this),
                j, k, m = $("<span class='sapMFirstFE' tabIndex='0'/>"),
                n = $("<span class='sapMLastFE' tabIndex='0'/>"),
                K, p, q = $.extend({}, s, {
                    preset: T.toLowerCase(),
                    disabled: !d.getEnabled() || !d.getEditable(),
                    onShow: function(v) {
                        if (D.browser.internet_explorer) {
                            if (d._popupIsShown) {
                                return
                            }
                            d._popupIsShown = true
                        }
                        sap.m.InstanceManager.addDialogInstance(d);
                        $(window).on("resize.sapMDTICustom", {
                            $dialog: v
                        }, H);
                        $(window).unbind('keydown.dw');
                        v.on('keydown.dw', e);
                        if (s.display == "bubble") {
                            document.addEventListener($.support.touch ? "touchstart" : "mousedown", A, true)
                        }
                        if (sap.ui.Device.system.desktop) {
                            var w = v.find('.dwcc'),
                                x = v.find('.dwbc'),
                                y = w.find(":focusable.dwww");
                            m.insertBefore(w);
                            k = $.proxy(d._getFocusInHandler(x, false), d);
                            m.focusin(k);
                            n.insertAfter(x);
                            j = $.proxy(d._getFocusInHandler(w, true), d);
                            n.focusin(j);
                            jQuery.sap.focus(w.firstFocusableDomRef());
                            p = v;
                            K = $.proxy(d._getKeyDownHandler(y), d);
                            v.keydown(K)
                        }
                    },
                    onClose: function() {
                        if (D.browser.internet_explorer) {
                            d._popupIsShown = false
                        }
                        sap.m.InstanceManager.removeDialogInstance(d);
                        $(window).off("resize.sapMDTICustom", H);
                        if (s.display == "bubble") {
                            document.removeEventListener($.support.touch ? "touchstart" : "mousedown", A, true)
                        }
                        m.unbind('focusin', k);
                        n.unbind('focusin', j);
                        if (p) {
                            p.unbind('keydown', K);
                            p.unbind('keydown.dw', e)
                        }
                    },
                    onMarkupReady: function(v, w) {
                        d._restrictMaxWidth(v);
                        if (s.theme != "sapMDTICustom") {
                            v.addClass("sapMDTICustom")
                        }
                        if (s.headerText !== false) {
                            v.addClass("sapMDTICustomHdr")
                        }
                        if (sap.ui.getCore().getConfiguration().getRTL()) {
                            var x = v.find(".dwbc");
                            var y = x.find(".dwb-c");
                            y.prependTo(x)
                        }
                    }
                });
            if (T == "Date") {
                F = this._convertDatePattern(F);
                $.extend(q, {
                    dateFormat: F,
                    dateOrder: this._getLongDatePattern(F.replace(/'.*?'/g, "")).replace(/[^ymd ]/ig, ""),
                })
            } else if (T == "Time") {
                F = this._convertTimePattern(F);
                $.extend(q, {
                    timeFormat: F,
                    timeWheels: F.replace(/'.*?'/g, "").replace(/[^hisa]/ig, "")
                })
            } else if (T == "DateTime") {
                F = this._convertDatePattern(this._convertTimePattern(F));
                $.extend(q, {
                    dateFormat: F,
                    dateOrder: this._getLongDatePattern(F.replace(/'.*?'/g, "")).replace(/[^ymd ]/ig, ""),
                    rows: this._getRowForDateTime(),
                    timeWheels: F,
                    timeFormat: "",
                    separator: ""
                })
            }
            if (/[^ymdhisa\W]/i.test(F)) {
                this._reformat = true;
                if (s.headerText !== false) {
                    q.headerText = $.proxy(this._setScrollerHeader, this)
                }
            } else {
                this._reformat = false
            }
            return q
        },
        _getRowForDateTime: function() {
            var d = s.rows || a.rows;
            if (!d || d <= 3) {
                return 3
            }
            return Math.min(window.innerWidth, window.innerHeight) < 360 ? 3 : d
        },
        _getFocusInHandler: function(d, e) {
            return function() {
                var E = e ? d.firstFocusableDomRef() : d.lastFocusableDomRef();
                jQuery.sap.focus(E)
            }
        },
        _getKeyDownHandler: function(F) {
            return function(e) {
                var k = e.which,
                    S = e.shiftKey,
                    A = e.altKey,
                    d = e.ctrlKey;
                if (!A && !S && !d) {
                    switch (k) {
                        case jQuery.sap.KeyCodes.ARROW_RIGHT:
                            var j = F.index(document.activeElement),
                                m = F.eq(j + 1).length ? F.eq(j + 1):
                                    F.eq(0);
                                m.focus();
                                break;
                            case jQuery.sap.KeyCodes.ARROW_LEFT:
                                var j = F.index(document.activeElement),
                                    n = F.eq(j - 1).length ? F.eq(j - 1):
                                        F.eq(F.length - 1);
                                    n.focus();
                                    break;
                                case jQuery.sap.KeyCodes.HOME:
                                    F[0].focus();
                                    break;
                                case jQuery.sap.KeyCodes.END:
                                    F[F.length - 1].focus();
                                    break;
                                default:
                                    break
                    }
                } else if (A && !S && !d) {
                    switch (k) {
                        case jQuery.sap.KeyCodes.ARROW_UP:
                            this._$input.scroller("select");
                            break;
                        default:
                            break
                    }
                }
            }
        },
        _rgxYear: new RegExp("y+" + f, "ig"),
        _rgxMonth: new RegExp("m+" + f, "ig"),
        _rgxDay: new RegExp("d+" + f, "g"),
        _rgxMinute: new RegExp("m" + f, "g"),
        _rgxAmPm: new RegExp("a" + f, "g"),
        _rgxDayOfWeekLong: new RegExp("EEEE" + f, "g"),
        _rgxDayOfWeekShort: new RegExp("E+" + f, "g"),
        _getLongDatePattern: function(p) {
            p = (p || this.getDisplayFormat()).replace(this._rgxYear, "YY");
            if (o == "wp") {
                return p.replace(this._rgxMonth, "mm MM").replace(this._rgxDay, "dd DD")
            }
            return p.replace(this._rgxMonth, "MM").replace(this._rgxDay, "dd")
        },
        _convertTimePattern: function(p) {
            p = p || this.getDisplayFormat();
            return p.replace(this._rgxMinute, "i").replace(this._rgxAmPm, "A")
        },
        _convertDatePattern: function(p) {
            p = p || this.getDisplayFormat();
            var I = p.indexOf("M"),
                d = p.lastIndexOf("M"),
                F = p,
                n;
            if (I == -1) {
                I = p.indexOf("L");
                d = p.lastIndexOf("L")
            }
            if (I > -1) {
                switch (d - I) {
                    case 0:
                        n = "m";
                        break;
                    case 1:
                        n = "mm";
                        break;
                    case 2:
                        n = "M";
                        break;
                    case 5:
                        n = "m";
                        break;
                    default:
                        n = "MM";
                        break
                }
                F = p.substring(0, I) + n + p.substring(d + 1)
            }
            var N;
            I = F.indexOf("y");
            if (I > -1) {
                d = F.lastIndexOf("y");
                if (d - I == 1) {
                    N = "y"
                } else {
                    N = "yy"
                }
                F = F.substring(0, I) + N + F.substring(d + 1)
            }
            var e;
            I = F.indexOf("D");
            if (I > -1) {
                d = F.lastIndexOf("D");
                if (d - I == 1) {
                    e = "o"
                } else {
                    e = "oo"
                }
                F = F.substring(0, I) + e + F.substring(d + 1)
            }
            F = F.replace(this._rgxDayOfWeekLong, "DD").replace(this._rgxDayOfWeekShort, "D");
            return F
        }
    })
})(jQuery, sap.ui.Device);