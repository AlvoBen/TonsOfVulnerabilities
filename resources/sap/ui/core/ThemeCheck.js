﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.ThemeCheck");
jQuery.sap.require("sap.ui.base.Object");
jQuery.sap.require("jquery.sap.script");
(function() {
    sap.ui._maxThemeCheckCycles = 100;
    sap.ui.base.Object.extend("sap.ui.core.ThemeCheck", {
        constructor: function(C) {
            this._oCore = C;
            this._iCount = 0;
            this._CUSTOMCSSCHECK = ".sapUiThemeDesignerCustomCss";
            this._CUSTOMID = "sap-ui-core-customcss";
            this._customCSSAdded = false;
            this._themeCheckedForCustom = null
        },
        getInterface: function() {
            return this
        },
        fireThemeChangedEvent: function(o, f) {
            c(this);
            var u = sap.ui._maxThemeCheckCycles > 0;
            if (u || f) {
                d.apply(this, [true])
            } else {
                sap.ui.core.ThemeCheck.themeLoaded = true
            }
            if (!o && !this._sThemeCheckId) {
                this._oCore.fireThemeChanged({
                    theme: this._oCore.getConfiguration().getTheme()
                })
            }
        }
    });
    sap.ui.core.ThemeCheck.themeLoaded = false;
    sap.ui.core.ThemeCheck.checkStyle = function(s, l) {
        if (typeof(s) === "string") {
            s = jQuery.sap.domById(s)
        }
        var S = jQuery(s);
        try {
            var r = !s || !! ((s.sheet && s.sheet.cssRules.length > 0) || !! (s.styleSheet && s.styleSheet.cssText.length > 0) || !! (s.innerHTML && s.innerHTML.length > 0));
            var f = S.attr("sap-ui-ready");
            f = !! (f === "true" || f === "false");
            if (l) {
                jQuery.sap.log.debug("ThemeCheck: Check styles '" + S.attr("id") + "': " + r + "/" + f + "/" + !! s)
            }
            return r || f
        } catch (e) {}
        if (l) {
            jQuery.sap.log.debug("ThemeCheck: Error during check styles '" + S.attr("id") + "': false/false/" + !! s)
        }
        return false
    };

    function c(t) {
        sap.ui.core.ThemeCheck.themeLoaded = false;
        if (t._sThemeCheckId) {
            jQuery.sap.clearDelayedCall(t._sThemeCheckId);
            t._sThemeCheckId = null;
            t._iCount = 0
        }
    }
    function a(t) {
        var l = t._oCore.getLoadedLibraries();
        var T = t._oCore.getConfiguration().getTheme();
        var p = t._oCore._getThemePath("sap.ui.core", T) + "custom.css";
        var r = true;
        if ( !! t._customCSSAdded && t._themeCheckedForCustom === T) {
            l["sap-ui-theme-" + t._CUSTOMID] = {}
        }
        jQuery.each(l, function(e) {
            r = r && sap.ui.core.ThemeCheck.checkStyle("sap-ui-theme-" + e, true);
            if (e === "sap.ui.core" && r) {
                if (t._themeCheckedForCustom != T) {
                    t._themeCheckedForCustom = T;
                    if (b(t)) {
                        jQuery.sap.includeStyleSheet(p, t._CUSTOMID);
                        t._customCSSAdded = true;
                        jQuery.sap.log.warning("ThemeCheck delivered custom CSS needs to be loaded, Theme not yet applied");
                        r = false;
                        return false
                    } else {
                        var f = jQuery("LINK[id='" + t._CUSTOMID + "']");
                        if (f.length > 0) {
                            f.remove();
                            jQuery.sap.log.debug("Custom CSS removed")
                        }
                        t._customCSSAdded = false
                    }
                }
            }
        });
        if (!r) {
            jQuery.sap.log.warning("ThemeCheck: Theme not yet applied.")
        }
        return r
    }
    function b(t) {
        var r = null;
        jQuery.each(document.styleSheets, function(i, s) {
            if ( !! s.ownerNode && /sap.ui.core/.test(s.ownerNode.id) && s.cssRules && s.cssRules.length > 0) {
                r = s.cssRules[0].selectorText
            } else if ( !! s.owningElement && /sap.ui.core/.test(s.owningElement.id) && s.rules && s.rules.length > 0) {
                r = s.rules[0].selectorText
            }
        });
        if (r === t._CUSTOMCSSCHECK) {
            return true
        }
        return false
    }
    function d(f) {
        this._iCount++;
        var e = this._iCount > sap.ui._maxThemeCheckCycles;
        if (!a(this) && !e) {
            this._sThemeCheckId = jQuery.sap.delayedCall(2, this, d)
        } else if (!f) {
            c(this);
            sap.ui.core.ThemeCheck.themeLoaded = true;
            this._oCore.fireThemeChanged({
                theme: this._oCore.getConfiguration().getTheme()
            });
            if (e) {
                jQuery.sap.log.warning("ThemeCheck: max. check cycles reached.")
            }
        } else {
            sap.ui.core.ThemeCheck.themeLoaded = true
        }
    }
})();