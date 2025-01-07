﻿/*!
 * jQuery UI Datepicker 1.8.23
 *
 * Copyright 2012, AUTHORS.txt (http://jqueryui.com/about)
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 *
 * http://docs.jquery.com/UI/Datepicker
 *
 * Depends:
 *	jquery.ui.core.js
 */

(function($, undefined) {
    $.extend($.ui, {
        datepicker: {
            version: "1.8.23"
        }
    });
    var PROP_NAME = 'datepicker';
    var dpuuid = new Date().getTime();
    var instActive;

    function Datepicker() {
        this.debug = false;
        this._curInst = null;
        this._keyEvent = false;
        this._disabledInputs = [];
        this._datepickerShowing = false;
        this._inDialog = false;
        this._mainDivId = 'ui-datepicker-div';
        this._inlineClass = 'ui-datepicker-inline';
        this._appendClass = 'ui-datepicker-append';
        this._triggerClass = 'ui-datepicker-trigger';
        this._dialogClass = 'ui-datepicker-dialog';
        this._disableClass = 'ui-datepicker-disabled';
        this._unselectableClass = 'ui-datepicker-unselectable';
        this._currentClass = 'ui-datepicker-current-day';
        this._dayOverClass = 'ui-datepicker-days-cell-over';
        this.regional = [];
        this.regional[''] = {
            closeText: 'Done',
            prevText: 'Prev',
            nextText: 'Next',
            currentText: 'Today',
            monthNames: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
            monthNamesShort: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
            dayNames: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
            dayNamesShort: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            dayNamesMin: ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'],
            weekHeader: 'Wk',
            dateFormat: 'mm/dd/yy',
            firstDay: 0,
            isRTL: false,
            showMonthAfterYear: false,
            yearSuffix: ''
        };
        this._defaults = {
            showOn: 'focus',
            showAnim: 'fadeIn',
            showOptions: {},
            defaultDate: null,
            appendText: '',
            buttonText: '...',
            buttonImage: '',
            buttonImageOnly: false,
            hideIfNoPrevNext: false,
            navigationAsDateFormat: false,
            gotoCurrent: false,
            changeMonth: false,
            changeYear: false,
            yearRange: 'c-10:c+10',
            showOtherMonths: false,
            selectOtherMonths: false,
            showWeek: false,
            calculateWeek: this.iso8601Week,
            shortYearCutoff: '+10',
            minDate: null,
            maxDate: null,
            duration: 'fast',
            beforeShowDay: null,
            beforeShow: null,
            onSelect: null,
            onChangeMonthYear: null,
            onClose: null,
            numberOfMonths: 1,
            showCurrentAtPos: 0,
            stepMonths: 1,
            stepBigMonths: 12,
            altField: '',
            altFormat: '',
            constrainInput: true,
            showButtonPanel: false,
            autoSize: false,
            disabled: false
        };
        $.extend(this._defaults, this.regional['']);
        this.dpDiv = bindHover($('<div id="' + this._mainDivId + '" class="ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"></div>'))
    }
    $.extend(Datepicker.prototype, {
        markerClassName: 'hasDatepicker',
        maxRows: 4,
        log: function() {
            if (this.debug) console.log.apply('', arguments)
        },
        _widgetDatepicker: function() {
            return this.dpDiv
        },
        setDefaults: function(s) {
            extendRemove(this._defaults, s || {});
            return this
        },
        _attachDatepicker: function(target, settings) {
            var inlineSettings = null;
            for (var attrName in this._defaults) {
                var attrValue = target.getAttribute('date:' + attrName);
                if (attrValue) {
                    inlineSettings = inlineSettings || {};
                    try {
                        inlineSettings[attrName] = eval(attrValue)
                    } catch (err) {
                        inlineSettings[attrName] = attrValue
                    }
                }
            }
            var nodeName = target.nodeName.toLowerCase();
            var inline = (nodeName == 'div' || nodeName == 'span');
            if (!target.id) {
                this.uuid += 1;
                target.id = 'dp' + this.uuid
            }
            var inst = this._newInst($(target), inline);
            inst.settings = $.extend({}, settings || {}, inlineSettings || {});
            if (nodeName == 'input') {
                this._connectDatepicker(target, inst)
            } else if (inline) {
                this._inlineDatepicker(target, inst)
            }
        },
        _newInst: function(t, i) {
            var a = t[0].id.replace(/([^A-Za-z0-9_-])/g, '\\\\$1');
            return {
                id: a,
                input: t,
                selectedDay: 0,
                selectedMonth: 0,
                selectedYear: 0,
                drawMonth: 0,
                drawYear: 0,
                inline: i,
                dpDiv: (!i ? this.dpDiv : bindHover($('<div class="' + this._inlineClass + ' ui-datepicker ui-widget ui-widget-content ui-helper-clearfix ui-corner-all"></div>')))
            }
        },
        _connectDatepicker: function(t, i) {
            var a = $(t);
            i.append = $([]);
            i.trigger = $([]);
            if (a.hasClass(this.markerClassName)) return;
            this._attachments(a, i);
            a.addClass(this.markerClassName).keydown(this._doKeyDown).keypress(this._doKeyPress).keyup(this._doKeyUp).bind("setData.datepicker", function(e, k, v) {
                i.settings[k] = v
            }).bind("getData.datepicker", function(e, k) {
                return this._get(i, k)
            });
            this._autoSize(i);
            $.data(t, PROP_NAME, i);
            if (i.settings.disabled) {
                this._disableDatepicker(t)
            }
        },
        _attachments: function(i, a) {
            var b = this._get(a, 'appendText');
            var c = this._get(a, 'isRTL');
            if (a.append) a.append.remove();
            if (b) {
                a.append = $('<span class="' + this._appendClass + '">' + b + '</span>');
                i[c ? 'before' : 'after'](a.append)
            }
            i.unbind('focus', this._showDatepicker);
            if (a.trigger) a.trigger.remove();
            var s = this._get(a, 'showOn');
            if (s == 'focus' || s == 'both') i.focus(this._showDatepicker);
            if (s == 'button' || s == 'both') {
                var d = this._get(a, 'buttonText');
                var e = this._get(a, 'buttonImage');
                a.trigger = $(this._get(a, 'buttonImageOnly') ? $('<img/>').addClass(this._triggerClass).attr({
                    src: e,
                    alt: d,
                    title: d
                }) : $('<button type="button"></button>').addClass(this._triggerClass).html(e == '' ? d : $('<img/>').attr({
                    src: e,
                    alt: d,
                    title: d
                })));
                i[c ? 'before' : 'after'](a.trigger);
                a.trigger.click(function() {
                    if ($.datepicker._datepickerShowing && $.datepicker._lastInput == i[0]) $.datepicker._hideDatepicker();
                    else if ($.datepicker._datepickerShowing && $.datepicker._lastInput != i[0]) {
                        $.datepicker._hideDatepicker();
                        $.datepicker._showDatepicker(i[0])
                    } else $.datepicker._showDatepicker(i[0]);
                    return false
                })
            }
        },
        _autoSize: function(a) {
            if (this._get(a, 'autoSize') && !a.inline) {
                var d = new Date(2009, 12 - 1, 20);
                var b = this._get(a, 'dateFormat');
                if (b.match(/[DM]/)) {
                    var f = function(n) {
                        var m = 0;
                        var c = 0;
                        for (var i = 0; i < n.length; i++) {
                            if (n[i].length > m) {
                                m = n[i].length;
                                c = i
                            }
                        }
                        return c
                    };
                    d.setMonth(f(this._get(a, (b.match(/MM/) ? 'monthNames' : 'monthNamesShort'))));
                    d.setDate(f(this._get(a, (b.match(/DD/) ? 'dayNames' : 'dayNamesShort'))) + 20 - d.getDay())
                }
                a.input.attr('size', this._formatDate(a, d).length)
            }
        },
        _inlineDatepicker: function(t, i) {
            var d = $(t);
            if (d.hasClass(this.markerClassName)) return;
            d.addClass(this.markerClassName).append(i.dpDiv).bind("setData.datepicker", function(e, k, v) {
                i.settings[k] = v
            }).bind("getData.datepicker", function(e, k) {
                return this._get(i, k)
            });
            $.data(t, PROP_NAME, i);
            this._setDate(i, this._getDefaultDate(i), true);
            this._updateDatepicker(i);
            this._updateAlternate(i);
            if (i.settings.disabled) {
                this._disableDatepicker(t)
            }
            i.dpDiv.css("display", "block")
        },
        _dialogDatepicker: function(i, d, o, s, p) {
            var a = this._dialogInst;
            if (!a) {
                this.uuid += 1;
                var b = 'dp' + this.uuid;
                this._dialogInput = $('<input type="text" id="' + b + '" style="position: absolute; top: -100px; width: 0px;"/>');
                this._dialogInput.keydown(this._doKeyDown);
                $('body').append(this._dialogInput);
                a = this._dialogInst = this._newInst(this._dialogInput, false);
                a.settings = {};
                $.data(this._dialogInput[0], PROP_NAME, a)
            }
            extendRemove(a.settings, s || {});
            d = (d && d.constructor == Date ? this._formatDate(a, d) : d);
            this._dialogInput.val(d);
            this._pos = (p ? (p.length ? p : [p.pageX, p.pageY]) : null);
            if (!this._pos) {
                var c = document.documentElement.clientWidth;
                var e = document.documentElement.clientHeight;
                var f = document.documentElement.scrollLeft || document.body.scrollLeft;
                var g = document.documentElement.scrollTop || document.body.scrollTop;
                this._pos = [(c / 2) - 100 + f, (e / 2) - 150 + g]
            }
            this._dialogInput.css('left', (this._pos[0] + 20) + 'px').css('top', this._pos[1] + 'px');
            a.settings.onSelect = o;
            this._inDialog = true;
            this.dpDiv.addClass(this._dialogClass);
            this._showDatepicker(this._dialogInput[0]);
            if ($.blockUI) $.blockUI(this.dpDiv);
            $.data(this._dialogInput[0], PROP_NAME, a);
            return this
        },
        _destroyDatepicker: function(t) {
            var a = $(t);
            var i = $.data(t, PROP_NAME);
            if (!a.hasClass(this.markerClassName)) {
                return
            }
            var n = t.nodeName.toLowerCase();
            $.removeData(t, PROP_NAME);
            if (n == 'input') {
                i.append.remove();
                i.trigger.remove();
                a.removeClass(this.markerClassName).unbind('focus', this._showDatepicker).unbind('keydown', this._doKeyDown).unbind('keypress', this._doKeyPress).unbind('keyup', this._doKeyUp)
            } else if (n == 'div' || n == 'span') a.removeClass(this.markerClassName).empty()
        },
        _enableDatepicker: function(t) {
            var a = $(t);
            var i = $.data(t, PROP_NAME);
            if (!a.hasClass(this.markerClassName)) {
                return
            }
            var n = t.nodeName.toLowerCase();
            if (n == 'input') {
                t.disabled = false;
                i.trigger.filter('button').each(function() {
                    this.disabled = false
                }).end().filter('img').css({
                    opacity: '1.0',
                    cursor: ''
                })
            } else if (n == 'div' || n == 'span') {
                var b = a.children('.' + this._inlineClass);
                b.children().removeClass('ui-state-disabled');
                b.find("select.ui-datepicker-month, select.ui-datepicker-year").removeAttr("disabled")
            }
            this._disabledInputs = $.map(this._disabledInputs, function(v) {
                return (v == t ? null : v)
            })
        },
        _disableDatepicker: function(t) {
            var a = $(t);
            var i = $.data(t, PROP_NAME);
            if (!a.hasClass(this.markerClassName)) {
                return
            }
            var n = t.nodeName.toLowerCase();
            if (n == 'input') {
                t.disabled = true;
                i.trigger.filter('button').each(function() {
                    this.disabled = true
                }).end().filter('img').css({
                    opacity: '0.5',
                    cursor: 'default'
                })
            } else if (n == 'div' || n == 'span') {
                var b = a.children('.' + this._inlineClass);
                b.children().addClass('ui-state-disabled');
                b.find("select.ui-datepicker-month, select.ui-datepicker-year").attr("disabled", "disabled")
            }
            this._disabledInputs = $.map(this._disabledInputs, function(v) {
                return (v == t ? null : v)
            });
            this._disabledInputs[this._disabledInputs.length] = t
        },
        _isDisabledDatepicker: function(t) {
            if (!t) {
                return false
            }
            for (var i = 0; i < this._disabledInputs.length; i++) {
                if (this._disabledInputs[i] == t) return true
            }
            return false
        },
        _getInst: function(t) {
            try {
                return $.data(t, PROP_NAME)
            } catch (e) {
                throw 'Missing instance data for this datepicker'
            }
        },
        _optionDatepicker: function(t, n, v) {
            var i = this._getInst(t);
            if (arguments.length == 2 && typeof n == 'string') {
                return (n == 'defaults' ? $.extend({}, $.datepicker._defaults) : (i ? (n == 'all' ? $.extend({}, i.settings) : this._get(i, n)) : null))
            }
            var s = n || {};
            if (typeof n == 'string') {
                s = {};
                s[n] = v
            }
            if (i) {
                if (this._curInst == i) {
                    this._hideDatepicker()
                }
                var d = this._getDateDatepicker(t, true);
                var m = this._getMinMaxDate(i, 'min');
                var a = this._getMinMaxDate(i, 'max');
                extendRemove(i.settings, s);
                if (m !== null && s['dateFormat'] !== undefined && s['minDate'] === undefined) i.settings.minDate = this._formatDate(i, m);
                if (a !== null && s['dateFormat'] !== undefined && s['maxDate'] === undefined) i.settings.maxDate = this._formatDate(i, a);
                this._attachments($(t), i);
                this._autoSize(i);
                this._setDate(i, d);
                this._updateAlternate(i);
                this._updateDatepicker(i)
            }
        },
        _changeDatepicker: function(t, n, v) {
            this._optionDatepicker(t, n, v)
        },
        _refreshDatepicker: function(t) {
            var i = this._getInst(t);
            if (i) {
                this._updateDatepicker(i)
            }
        },
        _setDateDatepicker: function(t, d) {
            var i = this._getInst(t);
            if (i) {
                this._setDate(i, d);
                this._updateDatepicker(i);
                this._updateAlternate(i)
            }
        },
        _getDateDatepicker: function(t, n) {
            var i = this._getInst(t);
            if (i && !i.inline) this._setDateFromField(i, n);
            return (i ? this._getDate(i) : null)
        },
        _doKeyDown: function(e) {
            var i = $.datepicker._getInst(e.target);
            var h = true;
            var a = i.dpDiv.is('.ui-datepicker-rtl');
            i._keyEvent = true;
            if ($.datepicker._datepickerShowing) switch (e.keyCode) {
                case 9:
                    $.datepicker._hideDatepicker();
                    h = false;
                    break;
                case 13:
                    var s = $('td.' + $.datepicker._dayOverClass + ':not(.' + $.datepicker._currentClass + ')', i.dpDiv);
                    if (s[0]) $.datepicker._selectDay(e.target, i.selectedMonth, i.selectedYear, s[0]);
                    var o = $.datepicker._get(i, 'onSelect');
                    if (o) {
                        var d = $.datepicker._formatDate(i);
                        o.apply((i.input ? i.input[0] : null), [d, i])
                    } else $.datepicker._hideDatepicker();
                    return false;
                    break;
                case 27:
                    $.datepicker._hideDatepicker();
                    break;
                case 33:
                    $.datepicker._adjustDate(e.target, (e.ctrlKey ? -$.datepicker._get(i, 'stepBigMonths') : -$.datepicker._get(i, 'stepMonths')), 'M');
                    break;
                case 34:
                    $.datepicker._adjustDate(e.target, (e.ctrlKey ? +$.datepicker._get(i, 'stepBigMonths') : +$.datepicker._get(i, 'stepMonths')), 'M');
                    break;
                case 35:
                    if (e.ctrlKey || e.metaKey) $.datepicker._clearDate(e.target);
                    h = e.ctrlKey || e.metaKey;
                    break;
                case 36:
                    if (e.ctrlKey || e.metaKey) $.datepicker._gotoToday(e.target);
                    h = e.ctrlKey || e.metaKey;
                    break;
                case 37:
                    if (e.ctrlKey || e.metaKey) $.datepicker._adjustDate(e.target, (a ? +1 : -1), 'D');
                    h = e.ctrlKey || e.metaKey;
                    if (e.originalEvent.altKey) $.datepicker._adjustDate(e.target, (e.ctrlKey ? -$.datepicker._get(i, 'stepBigMonths') : -$.datepicker._get(i, 'stepMonths')), 'M');
                    break;
                case 38:
                    if (e.ctrlKey || e.metaKey) $.datepicker._adjustDate(e.target, -7, 'D');
                    h = e.ctrlKey || e.metaKey;
                    break;
                case 39:
                    if (e.ctrlKey || e.metaKey) $.datepicker._adjustDate(e.target, (a ? -1 : +1), 'D');
                    h = e.ctrlKey || e.metaKey;
                    if (e.originalEvent.altKey) $.datepicker._adjustDate(e.target, (e.ctrlKey ? +$.datepicker._get(i, 'stepBigMonths') : +$.datepicker._get(i, 'stepMonths')), 'M');
                    break;
                case 40:
                    if (e.ctrlKey || e.metaKey) $.datepicker._adjustDate(e.target, +7, 'D');
                    h = e.ctrlKey || e.metaKey;
                    break;
                default:
                    h = false
            } else if (e.keyCode == 36 && e.ctrlKey) $.datepicker._showDatepicker(this);
            else {
                h = false
            }
            if (h) {
                e.preventDefault();
                e.stopPropagation()
            }
        },
        _doKeyPress: function(e) {
            var i = $.datepicker._getInst(e.target);
            if ($.datepicker._get(i, 'constrainInput')) {
                var c = $.datepicker._possibleChars($.datepicker._get(i, 'dateFormat'));
                var a = String.fromCharCode(e.charCode == undefined ? e.keyCode : e.charCode);
                return e.ctrlKey || e.metaKey || (a < ' ' || !c || c.indexOf(a) > -1)
            }
        },
        _doKeyUp: function(e) {
            var i = $.datepicker._getInst(e.target);
            if (i.input.val() != i.lastVal) {
                try {
                    var d = $.datepicker.parseDate($.datepicker._get(i, 'dateFormat'), (i.input ? i.input.val() : null), $.datepicker._getFormatConfig(i));
                    if (d) {
                        $.datepicker._setDateFromField(i);
                        $.datepicker._updateAlternate(i);
                        $.datepicker._updateDatepicker(i)
                    }
                } catch (a) {
                    $.datepicker.log(a)
                }
            }
            return true
        },
        _showDatepicker: function(i) {
            i = i.target || i;
            if (i.nodeName.toLowerCase() != 'input') i = $('input', i.parentNode)[0];
            if ($.datepicker._isDisabledDatepicker(i) || $.datepicker._lastInput == i) return;
            var a = $.datepicker._getInst(i);
            if ($.datepicker._curInst && $.datepicker._curInst != a) {
                $.datepicker._curInst.dpDiv.stop(true, true);
                if (a && $.datepicker._datepickerShowing) {
                    $.datepicker._hideDatepicker($.datepicker._curInst.input[0])
                }
            }
            var b = $.datepicker._get(a, 'beforeShow');
            var c = b ? b.apply(i, [i, a]) : {};
            if (c === false) {
                return
            }
            extendRemove(a.settings, c);
            a.lastVal = null;
            $.datepicker._lastInput = i;
            $.datepicker._setDateFromField(a);
            if ($.datepicker._inDialog) i.value = '';
            if (!$.datepicker._pos) {
                $.datepicker._pos = $.datepicker._findPos(i);
                $.datepicker._pos[1] += i.offsetHeight
            }
            var d = false;
            $(i).parents().each(function() {
                d |= $(this).css('position') == 'fixed';
                return !d
            });
            if (d && $.browser.opera) {
                $.datepicker._pos[0] -= document.documentElement.scrollLeft;
                $.datepicker._pos[1] -= document.documentElement.scrollTop
            }
            var o = {
                left: $.datepicker._pos[0],
                top: $.datepicker._pos[1]
            };
            $.datepicker._pos = null;
            a.dpDiv.empty();
            a.dpDiv.css({
                position: 'absolute',
                display: 'block',
                top: '-1000px'
            });
            $.datepicker._updateDatepicker(a);
            o = $.datepicker._checkOffset(a, o, d);
            a.dpDiv.css({
                position: ($.datepicker._inDialog && $.blockUI ? 'static' : (d ? 'fixed' : 'absolute')),
                display: 'none',
                left: o.left + 'px',
                top: o.top + 'px'
            });
            if (!a.inline) {
                var s = $.datepicker._get(a, 'showAnim');
                var e = $.datepicker._get(a, 'duration');
                var p = function() {
                    var f = a.dpDiv.find('iframe.ui-datepicker-cover');
                    if ( !! f.length) {
                        var g = $.datepicker._getBorders(a.dpDiv);
                        f.css({
                            left: -g[0],
                            top: -g[1],
                            width: a.dpDiv.outerWidth(),
                            height: a.dpDiv.outerHeight()
                        })
                    }
                };
                a.dpDiv.zIndex($(i).zIndex() + 1);
                $.datepicker._datepickerShowing = true;
                if ($.effects && $.effects[s]) a.dpDiv.show(s, $.datepicker._get(a, 'showOptions'), e, p);
                else a.dpDiv[s || 'show']((s ? e : null), p);
                if (!s || !e) p();
                if (a.input.is(':visible') && !a.input.is(':disabled')) a.input.focus();
                $.datepicker._curInst = a
            }
        },
        _updateDatepicker: function(i) {
            var s = this;
            s.maxRows = 4;
            var b = $.datepicker._getBorders(i.dpDiv);
            instActive = i;
            i.dpDiv.empty().append(this._generateHTML(i));
            this._attachHandlers(i);
            var c = i.dpDiv.find('iframe.ui-datepicker-cover');
            if ( !! c.length) {
                c.css({
                    left: -b[0],
                    top: -b[1],
                    width: i.dpDiv.outerWidth(),
                    height: i.dpDiv.outerHeight()
                })
            }
            i.dpDiv.find('.' + this._dayOverClass + ' a').mouseover();
            var n = this._getNumberOfMonths(i);
            var a = n[1];
            var w = 17;
            i.dpDiv.removeClass('ui-datepicker-multi-2 ui-datepicker-multi-3 ui-datepicker-multi-4').width('');
            if (a > 1) i.dpDiv.addClass('ui-datepicker-multi-' + a).css('width', (w * a) + 'em');
            i.dpDiv[(n[0] != 1 || n[1] != 1 ? 'add' : 'remove') + 'Class']('ui-datepicker-multi');
            i.dpDiv[(this._get(i, 'isRTL') ? 'add' : 'remove') + 'Class']('ui-datepicker-rtl');
            if (i == $.datepicker._curInst && $.datepicker._datepickerShowing && i.input && i.input.is(':visible') && !i.input.is(':disabled') && i.input[0] != document.activeElement) i.input.focus();
            if (i.yearshtml) {
                var o = i.yearshtml;
                setTimeout(function() {
                    if (o === i.yearshtml && i.yearshtml) {
                        i.dpDiv.find('select.ui-datepicker-year:first').replaceWith(i.yearshtml)
                    }
                    o = i.yearshtml = null
                }, 0)
            }
        },
        _getBorders: function(e) {
            var c = function(v) {
                return {
                    thin: 1,
                    medium: 2,
                    thick: 3
                }[v] || v
            };
            return [parseFloat(c(e.css('border-left-width'))), parseFloat(c(e.css('border-top-width')))]
        },
        _checkOffset: function(i, o, a) {
            var d = i.dpDiv.outerWidth();
            var b = i.dpDiv.outerHeight();
            var c = i.input ? i.input.outerWidth() : 0;
            var e = i.input ? i.input.outerHeight() : 0;
            var v = document.documentElement.clientWidth + (a ? 0 : $(document).scrollLeft());
            var f = document.documentElement.clientHeight + (a ? 0 : $(document).scrollTop());
            o.left -= (this._get(i, 'isRTL') ? (d - c) : 0);
            o.left -= (a && o.left == i.input.offset().left) ? $(document).scrollLeft() : 0;
            o.top -= (a && o.top == (i.input.offset().top + e)) ? $(document).scrollTop() : 0;
            o.left -= Math.min(o.left, (o.left + d > v && v > d) ? Math.abs(o.left + d - v) : 0);
            o.top -= Math.min(o.top, (o.top + b > f && f > b) ? Math.abs(b + e) : 0);
            return o
        },
        _findPos: function(o) {
            var i = this._getInst(o);
            var a = this._get(i, 'isRTL');
            while (o && (o.type == 'hidden' || o.nodeType != 1 || $.expr.filters.hidden(o))) {
                o = o[a ? 'previousSibling' : 'nextSibling']
            }
            var p = $(o).offset();
            return [p.left, p.top]
        },
        _hideDatepicker: function(i) {
            var a = this._curInst;
            if (!a || (i && a != $.data(i, PROP_NAME))) return;
            if (this._datepickerShowing) {
                var s = this._get(a, 'showAnim');
                var d = this._get(a, 'duration');
                var p = function() {
                    $.datepicker._tidyDialog(a)
                };
                if ($.effects && $.effects[s]) a.dpDiv.hide(s, $.datepicker._get(a, 'showOptions'), d, p);
                else a.dpDiv[(s == 'slideDown' ? 'slideUp' : (s == 'fadeIn' ? 'fadeOut' : 'hide'))]((s ? d : null), p);
                if (!s) p();
                this._datepickerShowing = false;
                var o = this._get(a, 'onClose');
                if (o) o.apply((a.input ? a.input[0] : null), [(a.input ? a.input.val() : ''), a]);
                this._lastInput = null;
                if (this._inDialog) {
                    this._dialogInput.css({
                        position: 'absolute',
                        left: '0',
                        top: '-100px'
                    });
                    if ($.blockUI) {
                        $.unblockUI();
                        $('body').append(this.dpDiv)
                    }
                }
                this._inDialog = false
            }
        },
        _tidyDialog: function(i) {
            i.dpDiv.removeClass(this._dialogClass).unbind('.ui-datepicker-calendar')
        },
        _checkExternalClick: function(e) {
            if (!$.datepicker._curInst) return;
            var a = $(e.target),
                i = $.datepicker._getInst(a[0]);
            if (((a[0].id != $.datepicker._mainDivId && a.parents('#' + $.datepicker._mainDivId).length == 0 && !a.hasClass($.datepicker.markerClassName) && !a.closest("." + $.datepicker._triggerClass).length && $.datepicker._datepickerShowing && !($.datepicker._inDialog && $.blockUI))) || (a.hasClass($.datepicker.markerClassName) && $.datepicker._curInst != i)) $.datepicker._hideDatepicker()
        },
        _adjustDate: function(i, o, p) {
            var t = $(i);
            var a = this._getInst(t[0]);
            if (this._isDisabledDatepicker(t[0])) {
                return
            }
            this._adjustInstDate(a, o + (p == 'M' ? this._get(a, 'showCurrentAtPos') : 0), p);
            this._updateDatepicker(a)
        },
        _gotoToday: function(i) {
            var t = $(i);
            var a = this._getInst(t[0]);
            if (this._get(a, 'gotoCurrent') && a.currentDay) {
                a.selectedDay = a.currentDay;
                a.drawMonth = a.selectedMonth = a.currentMonth;
                a.drawYear = a.selectedYear = a.currentYear
            } else {
                var d = new Date();
                a.selectedDay = d.getDate();
                a.drawMonth = a.selectedMonth = d.getMonth();
                a.drawYear = a.selectedYear = d.getFullYear()
            }
            this._notifyChange(a);
            this._adjustDate(t)
        },
        _selectMonthYear: function(i, s, p) {
            var t = $(i);
            var a = this._getInst(t[0]);
            a['selected' + (p == 'M' ? 'Month' : 'Year')] = a['draw' + (p == 'M' ? 'Month' : 'Year')] = parseInt(s.options[s.selectedIndex].value, 10);
            this._notifyChange(a);
            this._adjustDate(t)
        },
        _selectDay: function(i, m, y, t) {
            var a = $(i);
            if ($(t).hasClass(this._unselectableClass) || this._isDisabledDatepicker(a[0])) {
                return
            }
            var b = this._getInst(a[0]);
            b.selectedDay = b.currentDay = $('a', t).html();
            b.selectedMonth = b.currentMonth = m;
            b.selectedYear = b.currentYear = y;
            this._selectDate(i, this._formatDate(b, b.currentDay, b.currentMonth, b.currentYear))
        },
        _clearDate: function(i) {
            var t = $(i);
            var a = this._getInst(t[0]);
            this._selectDate(t, '')
        },
        _selectDate: function(i, d) {
            var t = $(i);
            var a = this._getInst(t[0]);
            d = (d != null ? d : this._formatDate(a));
            if (a.input) a.input.val(d);
            this._updateAlternate(a);
            var o = this._get(a, 'onSelect');
            if (o) o.apply((a.input ? a.input[0] : null), [d, a]);
            else if (a.input) a.input.trigger('change');
            if (a.inline) this._updateDatepicker(a);
            else {
                this._hideDatepicker();
                this._lastInput = a.input[0];
                if (typeof(a.input[0]) != 'object') a.input.focus();
                this._lastInput = null
            }
        },
        _updateAlternate: function(i) {
            var a = this._get(i, 'altField');
            if (a) {
                var b = this._get(i, 'altFormat') || this._get(i, 'dateFormat');
                var d = this._getDate(i);
                var c = this.formatDate(b, d, this._getFormatConfig(i));
                $(a).each(function() {
                    $(this).val(c)
                })
            }
        },
        noWeekends: function(d) {
            var a = d.getDay();
            return [(a > 0 && a < 6), '']
        },
        iso8601Week: function(d) {
            var c = new Date(d.getTime());
            c.setDate(c.getDate() + 4 - (c.getDay() || 7));
            var t = c.getTime();
            c.setMonth(0);
            c.setDate(1);
            return Math.floor(Math.round((t - c) / 86400000) / 7) + 1
        },
        parseDate: function(f, c, s) {
            if (f == null || c == null) throw 'Invalid arguments';
            c = (typeof c == 'object' ? c.toString() : c + '');
            if (c == '') return null;
            var d = (s ? s.shortYearCutoff : null) || this._defaults.shortYearCutoff;
            d = (typeof d != 'string' ? d : new Date().getFullYear() % 100 + parseInt(d, 10));
            var e = (s ? s.dayNamesShort : null) || this._defaults.dayNamesShort;
            var g = (s ? s.dayNames : null) || this._defaults.dayNames;
            var m = (s ? s.monthNamesShort : null) || this._defaults.monthNamesShort;
            var h = (s ? s.monthNames : null) || this._defaults.monthNames;
            var y = -1;
            var j = -1;
            var l = -1;
            var n = -1;
            var o = false;
            var p = function(a) {
                var b = (F + 1 < f.length && f.charAt(F + 1) == a);
                if (b) F++;
                return b
            };
            var q = function(a) {
                var i = p(a);
                var b = (a == '@' ? 14 : (a == '!' ? 20 : (a == 'y' && i ? 4 : (a == 'o' ? 3 : 2))));
                var k = new RegExp('^\\d{1,' + b + '}');
                var v = c.substring(V).match(k);
                if (!v) throw 'Missing number at position ' + V;
                V += v[0].length;
                return parseInt(v[0], 10)
            };
            var r = function(x, z, A) {
                var B = $.map(p(x) ? A : z, function(v, k) {
                    return [[k, v]]
                }).sort(function(a, b) {
                    return -(a[1].length - b[1].length)
                });
                var C = -1;
                $.each(B, function(i, a) {
                    var b = a[1];
                    if (c.substr(V, b.length).toLowerCase() == b.toLowerCase()) {
                        C = a[0];
                        V += b.length;
                        return false
                    }
                });
                if (C != -1) return C + 1;
                else throw 'Unknown name at position ' + V
            };
            var t = function() {
                if (c.charAt(V) != f.charAt(F)) throw 'Unexpected literal at position ' + V;
                V++
            };
            var V = 0;
            for (var F = 0; F < f.length; F++) {
                if (o) if (f.charAt(F) == "'" && !p("'")) o = false;
                else t();
                else switch (f.charAt(F)) {
                    case 'd':
                        l = q('d');
                        break;
                    case 'D':
                        r('D', e, g);
                        break;
                    case 'o':
                        n = q('o');
                        break;
                    case 'm':
                        j = q('m');
                        break;
                    case 'M':
                        j = r('M', m, h);
                        break;
                    case 'y':
                        y = q('y');
                        break;
                    case '@':
                        var u = new Date(q('@'));
                        y = u.getFullYear();
                        j = u.getMonth() + 1;
                        l = u.getDate();
                        break;
                    case '!':
                        var u = new Date((q('!') - this._ticksTo1970) / 10000);
                        y = u.getFullYear();
                        j = u.getMonth() + 1;
                        l = u.getDate();
                        break;
                    case "'":
                        if (p("'")) t();
                        else o = true;
                        break;
                    default:
                        t()
                }
            }
            if (V < c.length) {
                throw "Extra/unparsed characters found in date: " + c.substring(V)
            }
            if (y == -1) y = new Date().getFullYear();
            else if (y < 100) y += new Date().getFullYear() - new Date().getFullYear() % 100 + (y <= d ? 0 : -100);
            if (n > -1) {
                j = 1;
                l = n;
                do {
                    var w = this._getDaysInMonth(y, j - 1);
                    if (l <= w) break;
                    j++;
                    l -= w
                } while (true)
            }
            var u = this._daylightSavingAdjust(new Date(y, j - 1, l));
            if (u.getFullYear() != y || u.getMonth() + 1 != j || u.getDate() != l) throw 'Invalid date';
            return u
        },
        ATOM: 'yy-mm-dd',
        COOKIE: 'D, dd M yy',
        ISO_8601: 'yy-mm-dd',
        RFC_822: 'D, d M y',
        RFC_850: 'DD, dd-M-y',
        RFC_1036: 'D, d M y',
        RFC_1123: 'D, d M yy',
        RFC_2822: 'D, d M yy',
        RSS: 'D, d M y',
        TICKS: '!',
        TIMESTAMP: '@',
        W3C: 'yy-mm-dd',
        _ticksTo1970: (((1970 - 1) * 365 + Math.floor(1970 / 4) - Math.floor(1970 / 100) + Math.floor(1970 / 400)) * 24 * 60 * 60 * 10000000),
        formatDate: function(f, d, s) {
            if (!d) return '';
            var a = (s ? s.dayNamesShort : null) || this._defaults.dayNamesShort;
            var b = (s ? s.dayNames : null) || this._defaults.dayNames;
            var m = (s ? s.monthNamesShort : null) || this._defaults.monthNamesShort;
            var c = (s ? s.monthNames : null) || this._defaults.monthNames;
            var l = function(i) {
                var j = (F + 1 < f.length && f.charAt(F + 1) == i);
                if (j) F++;
                return j
            };
            var e = function(i, v, j) {
                var n = '' + v;
                if (l(i)) while (n.length < j) n = '0' + n;
                return n
            };
            var g = function(i, v, j, k) {
                return (l(i) ? k[v] : j[v])
            };
            var o = '';
            var h = false;
            if (d) for (var F = 0; F < f.length; F++) {
                if (h) if (f.charAt(F) == "'" && !l("'")) h = false;
                else o += f.charAt(F);
                else switch (f.charAt(F)) {
                    case 'd':
                        o += e('d', d.getDate(), 2);
                        break;
                    case 'D':
                        o += g('D', d.getDay(), a, b);
                        break;
                    case 'o':
                        o += e('o', Math.round((new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime() - new Date(d.getFullYear(), 0, 0).getTime()) / 86400000), 3);
                        break;
                    case 'm':
                        o += e('m', d.getMonth() + 1, 2);
                        break;
                    case 'M':
                        o += g('M', d.getMonth(), m, c);
                        break;
                    case 'y':
                        o += (l('y') ? d.getFullYear() : (d.getYear() % 100 < 10 ? '0' : '') + d.getYear() % 100);
                        break;
                    case '@':
                        o += d.getTime();
                        break;
                    case '!':
                        o += d.getTime() * 10000 + this._ticksTo1970;
                        break;
                    case "'":
                        if (l("'")) o += "'";
                        else h = true;
                        break;
                    default:
                        o += f.charAt(F)
                }
            }
            return o
        },
        _possibleChars: function(f) {
            var c = '';
            var l = false;
            var a = function(m) {
                var b = (F + 1 < f.length && f.charAt(F + 1) == m);
                if (b) F++;
                return b
            };
            for (var F = 0; F < f.length; F++) if (l) if (f.charAt(F) == "'" && !a("'")) l = false;
            else c += f.charAt(F);
            else switch (f.charAt(F)) {
                case 'd':
                case 'm':
                case 'y':
                case '@':
                    c += '0123456789';
                    break;
                case 'D':
                case 'M':
                    return null;
                case "'":
                    if (a("'")) c += "'";
                    else l = true;
                    break;
                default:
                    c += f.charAt(F)
            }
            return c
        },
        _get: function(i, n) {
            return i.settings[n] !== undefined ? i.settings[n] : this._defaults[n]
        },
        _setDateFromField: function(i, n) {
            if (i.input.val() == i.lastVal) {
                return
            }
            var d = this._get(i, 'dateFormat');
            var a = i.lastVal = i.input ? i.input.val() : null;
            var b, c;
            b = c = this._getDefaultDate(i);
            var s = this._getFormatConfig(i);
            try {
                b = this.parseDate(d, a, s) || c
            } catch (e) {
                this.log(e);
                a = (n ? '' : a)
            }
            i.selectedDay = b.getDate();
            i.drawMonth = i.selectedMonth = b.getMonth();
            i.drawYear = i.selectedYear = b.getFullYear();
            i.currentDay = (a ? b.getDate() : 0);
            i.currentMonth = (a ? b.getMonth() : 0);
            i.currentYear = (a ? b.getFullYear() : 0);
            this._adjustInstDate(i)
        },
        _getDefaultDate: function(i) {
            return this._restrictMinMax(i, this._determineDate(i, this._get(i, 'defaultDate'), new Date()))
        },
        _determineDate: function(i, d, a) {
            var o = function(c) {
                var d = new Date();
                d.setDate(d.getDate() + c);
                return d
            };
            var b = function(c) {
                try {
                    return $.datepicker.parseDate($.datepicker._get(i, 'dateFormat'), c, $.datepicker._getFormatConfig(i))
                } catch (e) {}
                var d = (c.toLowerCase().match(/^c/) ? $.datepicker._getDate(i) : null) || new Date();
                var y = d.getFullYear();
                var m = d.getMonth();
                var f = d.getDate();
                var p = /([+-]?[0-9]+)\s*(d|D|w|W|m|M|y|Y)?/g;
                var g = p.exec(c);
                while (g) {
                    switch (g[2] || 'd') {
                        case 'd':
                        case 'D':
                            f += parseInt(g[1], 10);
                            break;
                        case 'w':
                        case 'W':
                            f += parseInt(g[1], 10) * 7;
                            break;
                        case 'm':
                        case 'M':
                            m += parseInt(g[1], 10);
                            f = Math.min(f, $.datepicker._getDaysInMonth(y, m));
                            break;
                        case 'y':
                        case 'Y':
                            y += parseInt(g[1], 10);
                            f = Math.min(f, $.datepicker._getDaysInMonth(y, m));
                            break
                    }
                    g = p.exec(c)
                }
                return new Date(y, m, f)
            };
            var n = (d == null || d === '' ? a : (typeof d == 'string' ? b(d) : (typeof d == 'number' ? (isNaN(d) ? a : o(d)) : new Date(d.getTime()))));
            n = (n && n.toString() == 'Invalid Date' ? a : n);
            if (n) {
                n.setHours(0);
                n.setMinutes(0);
                n.setSeconds(0);
                n.setMilliseconds(0)
            }
            return this._daylightSavingAdjust(n)
        },
        _daylightSavingAdjust: function(d) {
            if (!d) return null;
            d.setHours(d.getHours() > 12 ? d.getHours() + 2 : 0);
            return d
        },
        _setDate: function(i, d, n) {
            var c = !d;
            var o = i.selectedMonth;
            var a = i.selectedYear;
            var b = this._restrictMinMax(i, this._determineDate(i, d, new Date()));
            i.selectedDay = i.currentDay = b.getDate();
            i.drawMonth = i.selectedMonth = i.currentMonth = b.getMonth();
            i.drawYear = i.selectedYear = i.currentYear = b.getFullYear();
            if ((o != i.selectedMonth || a != i.selectedYear) && !n) this._notifyChange(i);
            this._adjustInstDate(i);
            if (i.input) {
                i.input.val(c ? '' : this._formatDate(i))
            }
        },
        _getDate: function(i) {
            var s = (!i.currentYear || (i.input && i.input.val() == '') ? null : this._daylightSavingAdjust(new Date(i.currentYear, i.currentMonth, i.currentDay)));
            return s
        },
        _attachHandlers: function(i) {
            var s = this._get(i, 'stepMonths');
            var a = '#' + i.id.replace(/\\\\/g, "\\");
            i.dpDiv.find('[data-handler]').map(function() {
                var h = {
                    prev: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._adjustDate(a, -s, 'M')
                    },
                    next: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._adjustDate(a, +s, 'M')
                    },
                    hide: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._hideDatepicker()
                    },
                    today: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._gotoToday(a)
                    },
                    selectDay: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._selectDay(a, +this.getAttribute('data-month'), +this.getAttribute('data-year'), this);
                        return false
                    },
                    selectMonth: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._selectMonthYear(a, this, 'M');
                        return false
                    },
                    selectYear: function() {
                        window['DP_jQuery_' + dpuuid].datepicker._selectMonthYear(a, this, 'Y');
                        return false
                    }
                };
                $(this).bind(this.getAttribute('data-event'), h[this.getAttribute('data-handler')])
            })
        },
        _generateHTML: function(i) {
            var t = new Date();
            t = this._daylightSavingAdjust(new Date(t.getFullYear(), t.getMonth(), t.getDate()));
            var a = this._get(i, 'isRTL');
            var s = this._get(i, 'showButtonPanel');
            var h = this._get(i, 'hideIfNoPrevNext');
            var n = this._get(i, 'navigationAsDateFormat');
            var b = this._getNumberOfMonths(i);
            var c = this._get(i, 'showCurrentAtPos');
            var d = this._get(i, 'stepMonths');
            var e = (b[0] != 1 || b[1] != 1);
            var f = this._daylightSavingAdjust((!i.currentDay ? new Date(9999, 9, 9) : new Date(i.currentYear, i.currentMonth, i.currentDay)));
            var m = this._getMinMaxDate(i, 'min');
            var g = this._getMinMaxDate(i, 'max');
            var j = i.drawMonth - c;
            var k = i.drawYear;
            if (j < 0) {
                j += 12;
                k--
            }
            if (g) {
                var l = this._daylightSavingAdjust(new Date(g.getFullYear(), g.getMonth() - (b[0] * b[1]) + 1, g.getDate()));
                l = (m && l < m ? m : l);
                while (this._daylightSavingAdjust(new Date(k, j, 1)) > l) {
                    j--;
                    if (j < 0) {
                        j = 11;
                        k--
                    }
                }
            }
            i.drawMonth = j;
            i.drawYear = k;
            var p = this._get(i, 'prevText');
            p = (!n ? p : this.formatDate(p, this._daylightSavingAdjust(new Date(k, j - d, 1)), this._getFormatConfig(i)));
            var o = (this._canAdjustMonth(i, -1, k, j) ? '<a class="ui-datepicker-prev ui-corner-all" data-handler="prev" data-event="click"' + ' title="' + p + '"><span class="ui-icon ui-icon-circle-triangle-' + (a ? 'e' : 'w') + '">' + p + '</span></a>' : (h ? '' : '<a class="ui-datepicker-prev ui-corner-all ui-state-disabled" title="' + p + '"><span class="ui-icon ui-icon-circle-triangle-' + (a ? 'e' : 'w') + '">' + p + '</span></a>'));
            var q = this._get(i, 'nextText');
            q = (!n ? q : this.formatDate(q, this._daylightSavingAdjust(new Date(k, j + d, 1)), this._getFormatConfig(i)));
            var r = (this._canAdjustMonth(i, +1, k, j) ? '<a class="ui-datepicker-next ui-corner-all" data-handler="next" data-event="click"' + ' title="' + q + '"><span class="ui-icon ui-icon-circle-triangle-' + (a ? 'w' : 'e') + '">' + q + '</span></a>' : (h ? '' : '<a class="ui-datepicker-next ui-corner-all ui-state-disabled" title="' + q + '"><span class="ui-icon ui-icon-circle-triangle-' + (a ? 'w' : 'e') + '">' + q + '</span></a>'));
            var u = this._get(i, 'currentText');
            var v = (this._get(i, 'gotoCurrent') && i.currentDay ? f : t);
            u = (!n ? u : this.formatDate(u, v, this._getFormatConfig(i)));
            var w = (!i.inline ? '<button type="button" class="ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all" data-handler="hide" data-event="click">' + this._get(i, 'closeText') + '</button>' : '');
            var x = (s) ? '<div class="ui-datepicker-buttonpane ui-widget-content">' + (a ? w : '') + (this._isInRange(i, v) ? '<button type="button" class="ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all" data-handler="today" data-event="click"' + '>' + u + '</button>' : '') + (a ? '' : w) + '</div>' : '';
            var y = parseInt(this._get(i, 'firstDay'), 10);
            y = (isNaN(y) ? 0 : y);
            var z = this._get(i, 'showWeek');
            var A = this._get(i, 'dayNames');
            var B = this._get(i, 'dayNamesShort');
            var C = this._get(i, 'dayNamesMin');
            var D = this._get(i, 'monthNames');
            var E = this._get(i, 'monthNamesShort');
            var F = this._get(i, 'beforeShowDay');
            var G = this._get(i, 'showOtherMonths');
            var H = this._get(i, 'selectOtherMonths');
            var I = this._get(i, 'calculateWeek') || this.iso8601Week;
            var J = this._getDefaultDate(i);
            var K = '';
            for (var L = 0; L < b[0]; L++) {
                var M = '';
                this.maxRows = 4;
                for (var N = 0; N < b[1]; N++) {
                    var O = this._daylightSavingAdjust(new Date(k, j, i.selectedDay));
                    var P = ' ui-corner-all';
                    var Q = '';
                    if (e) {
                        Q += '<div class="ui-datepicker-group';
                        if (b[1] > 1) switch (N) {
                            case 0:
                                Q += ' ui-datepicker-group-first';
                                P = ' ui-corner-' + (a ? 'right' : 'left');
                                break;
                            case b[1] - 1:
                                Q += ' ui-datepicker-group-last';
                                P = ' ui-corner-' + (a ? 'left' : 'right');
                                break;
                            default:
                                Q += ' ui-datepicker-group-middle';
                                P = '';
                                break
                        }
                        Q += '">'
                    }
                    Q += '<div class="ui-datepicker-header ui-widget-header ui-helper-clearfix' + P + '">' + (/all|left/.test(P) && L == 0 ? (a ? r : o) : '') + (/all|right/.test(P) && L == 0 ? (a ? o : r) : '') + this._generateMonthYearHeader(i, j, k, m, g, L > 0 || N > 0, D, E) + '</div><table class="ui-datepicker-calendar"><thead>' + '<tr>';
                    var R = (z ? '<th class="ui-datepicker-week-col">' + this._get(i, 'weekHeader') + '</th>' : '');
                    for (var S = 0; S < 7; S++) {
                        var T = (S + y) % 7;
                        R += '<th' + ((S + y + 6) % 7 >= 5 ? ' class="ui-datepicker-week-end"' : '') + '>' + '<span title="' + A[T] + '">' + C[T] + '</span></th>'
                    }
                    Q += R + '</tr></thead><tbody>';
                    var U = this._getDaysInMonth(k, j);
                    if (k == i.selectedYear && j == i.selectedMonth) i.selectedDay = Math.min(i.selectedDay, U);
                    var V = (this._getFirstDayOfMonth(k, j) - y + 7) % 7;
                    var W = Math.ceil((V + U) / 7);
                    var X = (e ? this.maxRows > W ? this.maxRows : W : W);
                    this.maxRows = X;
                    var Y = this._daylightSavingAdjust(new Date(k, j, 1 - V));
                    for (var Z = 0; Z < X; Z++) {
                        Q += '<tr>';
                        var _ = (!z ? '' : '<td class="ui-datepicker-week-col">' + this._get(i, 'calculateWeek')(Y) + '</td>');
                        for (var S = 0; S < 7; S++) {
                            var a1 = (F ? F.apply((i.input ? i.input[0] : null), [Y]) : [true, '']);
                            var b1 = (Y.getMonth() != j);
                            var c1 = (b1 && !H) || !a1[0] || (m && Y < m) || (g && Y > g);
                            _ += '<td class="' + ((S + y + 6) % 7 >= 5 ? ' ui-datepicker-week-end' : '') + (b1 ? ' ui-datepicker-other-month' : '') + ((Y.getTime() == O.getTime() && j == i.selectedMonth && i._keyEvent) || (J.getTime() == Y.getTime() && J.getTime() == O.getTime()) ? ' ' + this._dayOverClass : '') + (c1 ? ' ' + this._unselectableClass + ' ui-state-disabled' : '') + (b1 && !G ? '' : ' ' + a1[1] + (Y.getTime() == f.getTime() ? ' ' + this._currentClass : '') + (Y.getTime() == t.getTime() ? ' ui-datepicker-today' : '')) + '"' + ((!b1 || G) && a1[2] ? ' title="' + a1[2] + '"' : '') + (c1 ? '' : ' data-handler="selectDay" data-event="click" data-month="' + Y.getMonth() + '" data-year="' + Y.getFullYear() + '"') + '>' + (b1 && !G ? '&#xa0;' : (c1 ? '<span class="ui-state-default">' + Y.getDate() + '</span>' : '<a class="ui-state-default' + (Y.getTime() == t.getTime() ? ' ui-state-highlight' : '') + (Y.getTime() == f.getTime() ? ' ui-state-active' : '') + (b1 ? ' ui-priority-secondary' : '') + '" href="#">' + Y.getDate() + '</a>')) + '</td>';
                            Y.setDate(Y.getDate() + 1);
                            Y = this._daylightSavingAdjust(Y)
                        }
                        Q += _ + '</tr>'
                    }
                    j++;
                    if (j > 11) {
                        j = 0;
                        k++
                    }
                    Q += '</tbody></table>' + (e ? '</div>' + ((b[0] > 0 && N == b[1] - 1) ? '<div class="ui-datepicker-row-break"></div>' : '') : '');
                    M += Q
                }
                K += M
            }
            K += x + ($.browser.msie && parseInt($.browser.version, 10) < 7 && !i.inline ? '<iframe src="javascript:false;" class="ui-datepicker-cover" frameborder="0"></iframe>' : '');
            i._keyEvent = false;
            return K
        },
        _generateMonthYearHeader: function(i, d, a, m, b, s, c, e) {
            var f = this._get(i, 'changeMonth');
            var g = this._get(i, 'changeYear');
            var h = this._get(i, 'showMonthAfterYear');
            var j = '<div class="ui-datepicker-title">';
            var k = '';
            if (s || !f) k += '<span class="ui-datepicker-month">' + c[d] + '</span>';
            else {
                var l = (m && m.getFullYear() == a);
                var n = (b && b.getFullYear() == a);
                k += '<select class="ui-datepicker-month" data-handler="selectMonth" data-event="change">';
                for (var o = 0; o < 12; o++) {
                    if ((!l || o >= m.getMonth()) && (!n || o <= b.getMonth())) k += '<option value="' + o + '"' + (o == d ? ' selected="selected"' : '') + '>' + e[o] + '</option>'
                }
                k += '</select>'
            }
            if (!h) j += k + (s || !(f && g) ? '&#xa0;' : '');
            if (!i.yearshtml) {
                i.yearshtml = '';
                if (s || !g) j += '<span class="ui-datepicker-year">' + a + '</span>';
                else {
                    var y = this._get(i, 'yearRange').split(':');
                    var t = new Date().getFullYear();
                    var p = function(v) {
                        var q = (v.match(/c[+-].*/) ? a + parseInt(v.substring(1), 10) : (v.match(/[+-].*/) ? t + parseInt(v, 10) : parseInt(v, 10)));
                        return (isNaN(q) ? t : q)
                    };
                    var q = p(y[0]);
                    var r = Math.max(q, p(y[1] || ''));
                    q = (m ? Math.max(q, m.getFullYear()) : q);
                    r = (b ? Math.min(r, b.getFullYear()) : r);
                    i.yearshtml += '<select class="ui-datepicker-year" data-handler="selectYear" data-event="change">';
                    for (; q <= r; q++) {
                        i.yearshtml += '<option value="' + q + '"' + (q == a ? ' selected="selected"' : '') + '>' + q + '</option>'
                    }
                    i.yearshtml += '</select>';
                    j += i.yearshtml;
                    i.yearshtml = null
                }
            }
            j += this._get(i, 'yearSuffix');
            if (h) j += (s || !(f && g) ? '&#xa0;' : '') + k;
            j += '</div>';
            return j
        },
        _adjustInstDate: function(i, o, p) {
            var y = i.drawYear + (p == 'Y' ? o : 0);
            var m = i.drawMonth + (p == 'M' ? o : 0);
            var d = Math.min(i.selectedDay, this._getDaysInMonth(y, m)) + (p == 'D' ? o : 0);
            var a = this._restrictMinMax(i, this._daylightSavingAdjust(new Date(y, m, d)));
            i.selectedDay = a.getDate();
            i.drawMonth = i.selectedMonth = a.getMonth();
            i.drawYear = i.selectedYear = a.getFullYear();
            if (p == 'M' || p == 'Y') this._notifyChange(i)
        },
        _restrictMinMax: function(i, d) {
            var m = this._getMinMaxDate(i, 'min');
            var a = this._getMinMaxDate(i, 'max');
            var n = (m && d < m ? m : d);
            n = (a && n > a ? a : n);
            return n
        },
        _notifyChange: function(i) {
            var o = this._get(i, 'onChangeMonthYear');
            if (o) o.apply((i.input ? i.input[0] : null), [i.selectedYear, i.selectedMonth + 1, i])
        },
        _getNumberOfMonths: function(i) {
            var n = this._get(i, 'numberOfMonths');
            return (n == null ? [1, 1] : (typeof n == 'number' ? [1, n] : n))
        },
        _getMinMaxDate: function(i, m) {
            return this._determineDate(i, this._get(i, m + 'Date'), null)
        },
        _getDaysInMonth: function(y, m) {
            return 32 - this._daylightSavingAdjust(new Date(y, m, 32)).getDate()
        },
        _getFirstDayOfMonth: function(y, m) {
            return new Date(y, m, 1).getDay()
        },
        _canAdjustMonth: function(i, o, c, a) {
            var n = this._getNumberOfMonths(i);
            var d = this._daylightSavingAdjust(new Date(c, a + (o < 0 ? o : n[0] * n[1]), 1));
            if (o < 0) d.setDate(this._getDaysInMonth(d.getFullYear(), d.getMonth()));
            return this._isInRange(i, d)
        },
        _isInRange: function(i, d) {
            var m = this._getMinMaxDate(i, 'min');
            var a = this._getMinMaxDate(i, 'max');
            return ((!m || d.getTime() >= m.getTime()) && (!a || d.getTime() <= a.getTime()))
        },
        _getFormatConfig: function(i) {
            var s = this._get(i, 'shortYearCutoff');
            s = (typeof s != 'string' ? s : new Date().getFullYear() % 100 + parseInt(s, 10));
            return {
                shortYearCutoff: s,
                dayNamesShort: this._get(i, 'dayNamesShort'),
                dayNames: this._get(i, 'dayNames'),
                monthNamesShort: this._get(i, 'monthNamesShort'),
                monthNames: this._get(i, 'monthNames')
            }
        },
        _formatDate: function(i, d, m, y) {
            if (!d) {
                i.currentDay = i.selectedDay;
                i.currentMonth = i.selectedMonth;
                i.currentYear = i.selectedYear
            }
            var a = (d ? (typeof d == 'object' ? d : this._daylightSavingAdjust(new Date(y, m, d))) : this._daylightSavingAdjust(new Date(i.currentYear, i.currentMonth, i.currentDay)));
            return this.formatDate(this._get(i, 'dateFormat'), a, this._getFormatConfig(i))
        }
    });

    function bindHover(d) {
        var s = 'button, .ui-datepicker-prev, .ui-datepicker-next, .ui-datepicker-calendar td a';
        return d.bind('mouseout', function(e) {
            var a = $(e.target).closest(s);
            if (!a.length) {
                return
            }
            a.removeClass("ui-state-hover ui-datepicker-prev-hover ui-datepicker-next-hover")
        }).bind('mouseover', function(e) {
            var a = $(e.target).closest(s);
            if ($.datepicker._isDisabledDatepicker(instActive.inline ? d.parent()[0] : instActive.input[0]) || !a.length) {
                return
            }
            a.parents('.ui-datepicker-calendar').find('a').removeClass('ui-state-hover');
            a.addClass('ui-state-hover');
            if (a.hasClass('ui-datepicker-prev')) a.addClass('ui-datepicker-prev-hover');
            if (a.hasClass('ui-datepicker-next')) a.addClass('ui-datepicker-next-hover')
        })
    }
    function extendRemove(t, p) {
        $.extend(t, p);
        for (var n in p) if (p[n] == null || p[n] == undefined) t[n] = p[n];
        return t
    };

    function isArray(a) {
        return (a && (($.browser.safari && typeof a == 'object' && a.length) || (a.constructor && a.constructor.toString().match(/\Array\(\)/))))
    };
    $.fn.datepicker = function(o) {
        if (!this.length) {
            return this
        }
        if (!$.datepicker.initialized) {
            $(document).mousedown($.datepicker._checkExternalClick).find('body').append($.datepicker.dpDiv);
            $.datepicker.initialized = true
        }
        var a = Array.prototype.slice.call(arguments, 1);
        if (typeof o == 'string' && (o == 'isDisabled' || o == 'getDate' || o == 'widget')) return $.datepicker['_' + o + 'Datepicker'].apply($.datepicker, [this[0]].concat(a));
        if (o == 'option' && arguments.length == 2 && typeof arguments[1] == 'string') return $.datepicker['_' + o + 'Datepicker'].apply($.datepicker, [this[0]].concat(a));
        return this.each(function() {
            typeof o == 'string' ? $.datepicker['_' + o + 'Datepicker'].apply($.datepicker, [this].concat(a)) : $.datepicker._attachDatepicker(this, o)
        })
    };
    $.datepicker = new Datepicker();
    $.datepicker.initialized = false;
    $.datepicker.uuid = new Date().getTime();
    $.datepicker.version = "1.8.23";
    window['DP_jQuery_' + dpuuid] = $
})(jQuery);