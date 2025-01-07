﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.format.NumberFormat");
jQuery.sap.require("sap.ui.core.LocaleData");
sap.ui.base.Object.extend("sap.ui.core.format.NumberFormat", {
    constructor: function(f) {
        throw new Error()
    }
});
sap.ui.core.format.NumberFormat.oDefaultIntegerFormat = {
    minIntegerDigits: 1,
    maxIntegerDigits: 99,
    minFractionDigits: 0,
    maxFractionDigits: 0,
    groupingEnabled: false,
    groupingSeparator: ",",
    decimalSeparator: ".",
    plusSign: "+",
    minusSign: "-",
    isInteger: true
};
sap.ui.core.format.NumberFormat.oDefaultFloatFormat = {
    minIntegerDigits: 1,
    maxIntegerDigits: 99,
    minFractionDigits: 0,
    maxFractionDigits: 99,
    groupingEnabled: true,
    groupingSeparator: ",",
    decimalSeparator: ".",
    plusSign: "+",
    minusSign: "-",
    isInteger: false
};

sap.ui.core.format.NumberFormat.getInstance = function(f, l) {
    return this.getFloatInstance(f, l)
};

sap.ui.core.format.NumberFormat.getFloatInstance = function(f, l) {
    var F = this.createInstance(f, l);
    F.oFormatOptions = jQuery.extend(false, {}, this.oDefaultFloatFormat, this.getLocaleFormatOptions(F.oLocaleData), f);
    return F
};

sap.ui.core.format.NumberFormat.getIntegerInstance = function(f, l) {
    var F = this.createInstance(f, l);
    F.oFormatOptions = jQuery.extend(false, {}, this.oDefaultIntegerFormat, this.getLocaleFormatOptions(F.oLocaleData), f);
    return F
};

sap.ui.core.format.NumberFormat.createInstance = function(f, l) {
    var F = jQuery.sap.newObject(this.prototype);
    if (f instanceof sap.ui.core.Locale) {
        l = f;
        f = undefined
    }
    if (!l) {
        l = sap.ui.getCore().getConfiguration().getFormatSettings().getFormatLocale()
    }
    F.oLocale = l;
    F.oLocaleData = sap.ui.core.LocaleData.getInstance(l);
    return F
};

sap.ui.core.format.NumberFormat.getLocaleFormatOptions = function(l) {
    return {
        plusSign: l.getNumberSymbol("plusSign"),
        minusSign: l.getNumberSymbol("minusSign"),
        decimalSeparator: l.getNumberSymbol("decimal"),
        groupingSeparator: l.getNumberSymbol("group")
    }
};

sap.ui.core.format.NumberFormat.prototype.format = function(v) {
    var n = "" + v,
        i = "",
        f = "",
        g = "",
        r = "",
        p = 0,
        l = 0,
        N = v < 0,
        d = -1,
        o = this.oFormatOptions;
    if (N) {
        n = n.substr(1)
    }
    d = n.indexOf(".");
    if (d > -1) {
        i = n.substr(0, d);
        f = n.substr(d + 1)
    } else {
        i = n
    }
    if (i.length < o.minIntegerDigits) {
        i = jQuery.sap.padLeft(i, "0", o.minIntegerDigits)
    } else if (i.length > o.maxIntegerDigits) {
        i = jQuery.sap.padLeft("", "?", o.maxIntegerDigits)
    }
    if (f.length < o.minFractionDigits) {
        f = jQuery.sap.padRight(f, "0", o.minFractionDigits)
    } else if (f.length > o.maxFractionDigits) {
        f = f.substr(0, o.maxFractionDigits)
    }
    l = i.length;
    if (o.groupingEnabled && l > 3) {
        p = l % 3 || 3;
        g = i.substr(0, p);
        while (p < i.length) {
            g += o.groupingSeparator;
            g += i.substr(p, 3);
            p += 3
        }
        i = g
    }
    if (N) {
        r = o.minusSign
    }
    r += i;
    if (f) {
        r += o.decimalSeparator + f
    }
    if (sap.ui.getCore().getConfiguration().getOriginInfo()) {
        r = new String(r);
        r.originInfo = {
            source: "Common Locale Data Repository",
            locale: this.oLocale.toString()
        }
    }
    return r
};

sap.ui.core.format.NumberFormat.prototype.parse = function(v) {
    var o = this.oFormatOptions,
        r = "^\\s*([+-]?(?:[0-9\\" + o.groupingSeparator + "]+|[0-9\\" + o.groupingSeparator + "]*\\" + o.decimalSeparator + "[0-9]+)([eE][+-][0-9]+)?)\\s*$",
        R = "^\\s*([+-]?[0-9\\" + o.groupingSeparator + "]+)\\s*$",
        g = new RegExp("\\" + o.groupingSeparator, "g"),
        d = new RegExp("\\" + o.decimalSeparator, "g"),
        a, b = 0;
    if (o.isInteger) {
        a = new RegExp(R)
    } else {
        a = new RegExp(r)
    }
    if (!a.test(v)) {
        return NaN
    }
    v = v.replace(g, "");
    if (o.isInteger) {
        b = parseInt(v, 10)
    } else {
        v = v.replace(d, ".");
        b = parseFloat(v)
    }
    return b
};