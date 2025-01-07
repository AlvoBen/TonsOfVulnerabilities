﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.IntervalTrigger");
jQuery.sap.require("sap.ui.core.EventBus");
(function() {
    var _ = "sapUiIntervalTrigger-event";
    sap.ui.base.Object.extend("sap.ui.core.IntervalTrigger", {
        constructor: function(i) {
            sap.ui.base.Object.apply(this);
            this._oEventBus = new sap.ui.core.EventBus();
            this._delayedCallId = null;
            this._triggerProxy = jQuery.proxy(t, this);
            this._iInterval = 0;
            if (i) {
                this.setInterval(i)
            }
        }
    });
    var t = function() {
        jQuery.sap.clearDelayedCall(this._delayedCallId);
        var h = this._oEventBus._defaultChannel.hasListeners(_);
        if (this._iInterval > 0 && h) {
            this._oEventBus.publish(_);
            this._delayedCallId = jQuery.sap.delayedCall(this._iInterval, this, this._triggerProxy)
        }
    };
    sap.ui.core.IntervalTrigger.prototype.destroy = function() {
        sap.ui.base.Object.prototype.destroy.apply(this, arguments);
        delete this._triggerProxy;
        this._oEventBus.destroy();
        delete this._oEventBus
    };
    sap.ui.core.IntervalTrigger.prototype.setInterval = function(i) {
        if (this._iInterval !== i) {
            this._iInterval = i;
            this._triggerProxy()
        }
    };
    sap.ui.core.IntervalTrigger.prototype.addListener = function(f, l) {
        this._oEventBus.subscribe(_, f, l);
        this._triggerProxy()
    };
    sap.ui.core.IntervalTrigger.prototype.removeListener = function(f, l) {
        this._oEventBus.unsubscribe(_, f, l)
    };
    sap.ui.core.IntervalTrigger.prototype.getInterface = function() {
        return this
    }
}());