﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.InstanceManager");
sap.m.InstanceManager = {};
(function() {
    var r = {}, e = [];
    var p = "_POPOVER_",
        d = "_DIALOG_";
    sap.m.InstanceManager.addInstance = function(c, i) {
        if (!r[c]) {
            r[c] = []
        }
        r[c].push(i);
        return this
    };
    sap.m.InstanceManager.removeInstance = function(c, I) {
        var C = r[c],
            i;
        if (!C) {
            jQuery.sap.log.warning("Can't remove control from a non-managed category id: " + c);
            return null
        }
        i = C.indexOf(I);
        return (i === -1) ? null : C.splice(i, 1)
    };
    sap.m.InstanceManager.getInstancesByCategoryId = function(c) {
        return r[c] || e
    };
    sap.m.InstanceManager.isInstanceManaged = function(c, i) {
        var C = r[c];
        if (!C || !i) {
            return false
        }
        return C.indexOf(i) !== -1
    };
    sap.m.InstanceManager.isCategoryEmpty = function(c) {
        var C = r[c];
        return !C || C.length === 0
    };
    sap.m.InstanceManager.addPopoverInstance = function(P) {
        if (typeof P.close === "function") {
            sap.m.InstanceManager.addInstance(p, P)
        } else {
            jQuery.sap.log.warning("In method addPopoverInstance: the parameter doesn't have a close method and can't be managed.")
        }
        return this
    };
    sap.m.InstanceManager.addDialogInstance = function(D) {
        if (typeof D.close === "function") {
            sap.m.InstanceManager.addInstance(d, D)
        } else {
            jQuery.sap.log.warning("In method addDialogInstance: the parameter doesn't have a close method and can't be managed.")
        }
        return this
    };
    sap.m.InstanceManager.removePopoverInstance = function(P) {
        return sap.m.InstanceManager.removeInstance(p, P)
    };
    sap.m.InstanceManager.removeDialogInstance = function(D) {
        return sap.m.InstanceManager.removeInstance(d, D)
    };
    sap.m.InstanceManager.hasOpenPopover = function() {
        return !sap.m.InstanceManager.isCategoryEmpty(p)
    };
    sap.m.InstanceManager.hasOpenDialog = function() {
        return !sap.m.InstanceManager.isCategoryEmpty(d)
    };
    sap.m.InstanceManager.isDialogOpen = function(D) {
        return sap.m.InstanceManager.isInstanceManaged(d, D)
    };
    sap.m.InstanceManager.isPopoverOpen = function(P) {
        return sap.m.InstanceManager.isInstanceManaged(p, P)
    };
    sap.m.InstanceManager.getOpenPopovers = function() {
        return sap.m.InstanceManager.getInstancesByCategoryId(p)
    };
    sap.m.InstanceManager.getOpenDialogs = function() {
        return sap.m.InstanceManager.getInstancesByCategoryId(d)
    };
    sap.m.InstanceManager.closeAllPopovers = function() {
        var I = sap.m.InstanceManager.getOpenPopovers(),
            i;
        for (i = 0; i < I.length; i++) {
            I[i].close()
        }
        return this
    };
    sap.m.InstanceManager.closeAllDialogs = function(c) {
        var D, a = [],
            I = sap.m.InstanceManager.getOpenDialogs(),
            b, i;
        for (i = 0; i < I.length; i++) {
            b = I[i];
            if (c) {
                D = new jQuery.Deferred().done();
                a.push(D);
                b.attachEvent("afterClose", function(f) {
                    return function() {
                        f.resolve()
                    }
                }(D))
            }
            b.close()
        }
        if (c) {
            jQuery.when.apply(this, a).then(c)
        }
        return this
    }
}());