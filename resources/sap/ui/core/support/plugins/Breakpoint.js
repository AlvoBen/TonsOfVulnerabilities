﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.support.plugins.Breakpoint");
jQuery.sap.require("sap.ui.core.support.Plugin");
(function($) {
    sap.ui.core.support.Plugin.extend("sap.ui.core.support.plugins.Breakpoint", {
        constructor: function(s) {
            sap.ui.core.support.Plugin.apply(this, ["sapUiSupportBreakpoint", "", s]);
            if (this.isToolPlugin()) {
                throw Error()
            }
            this._oStub = s;
            this._methodType = {
                clazz: 1,
                proto: 2
            };
            this._mBreakpointData = {
                classes: {},
                instances: {}
            };
            this._idPrefix = "id_";
            this._methodPrefix = "method_";
            this._classPrefix = "class_";
            this._bAlertNoDebugger = false;
            this._aEventIds = [this.getId() + "RequestInstanceMethods", this.getId() + "ChangeInstanceBreakpoint", this.getId() + "RequestClasses", this.getId() + "RequestClassMethods", this.getId() + "ChangeClassBreakpoint", this.getId() + "RemoveAllClassBreakpoints"]
        }
    });
    sap.ui.core.support.plugins.Breakpoint.prototype.init = function(s) {
        sap.ui.core.support.Plugin.prototype.init.apply(this, arguments);
        window.bp = this;
        this.requestClasses("sapUiSupportDebuggingReceiveClasses")
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.onsapUiSupportBreakpointRequestInstanceMethods = function(e) {
        var c = e.getParameter("callback"),
            C = e.getParameter("controlId");
        var m = this.getInstanceMethods(C);
        this._oStub.sendEvent(c, {
            methods: JSON.stringify(m),
            breakpointCount: JSON.stringify({
                active: $.grep(m, function(M, i) {
                    return M.active
                }).length,
                all: m.length
            }),
            controlId: C
        })
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.onsapUiSupportBreakpointChangeInstanceBreakpoint = function(e) {
        var d = {
            controlId: e.getParameter("controlId"),
            methodName: e.getParameter("methodName"),
            active: e.getParameter("active")
        }, c = e.getParameter("callback");
        this.changeInstanceBreakpoint(d.controlId, d.methodName, d.active);
        var m = this.getInstanceMethods(d.controlId);
        d.breakpointCount = JSON.stringify({
            active: $.grep(m, function(M, i) {
                return M.active
            }).length,
            all: m.length
        });
        d.methods = JSON.stringify(m);
        this._oStub.sendEvent(c, d)
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.requestClasses = function(c) {
        this._oStub.sendEvent(c, {
            classes: JSON.stringify(this.getClasses())
        })
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.onsapUiSupportBreakpointRequestClasses = function(e) {
        this.requestClasses(e.getParameter("callback"))
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.onsapUiSupportBreakpointRequestClassMethods = function(e) {
        var c = e.getParameter("callback"),
            C = e.getParameter("className");
        var m = this.getClassMethods(C);
        this._oStub.sendEvent(c, {
            methods: JSON.stringify(m),
            breakpointCount: JSON.stringify({
                active: $.grep(m, function(M, i) {
                    return M.active
                }).length,
                all: m.length
            }),
            className: C
        })
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.onsapUiSupportBreakpointChangeClassBreakpoint = function(e) {
        var d = {
            className: e.getParameter("className"),
            methodName: e.getParameter("methodName"),
            active: e.getParameter("active"),
            type: e.getParameter("type")
        }, c = e.getParameter("callback");
        this.changeClassBreakpoint(d.className, d.methodName, d.active, d.type);
        var m = this.getClassMethods(d.className);
        d.breakpointCount = JSON.stringify({
            active: $.grep(m, function(M, i) {
                return M.active
            }).length,
            all: m.length
        });
        d.methods = JSON.stringify(m);
        this._oStub.sendEvent(c, d)
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.onsapUiSupportBreakpointRemoveAllClassBreakpoints = function(e) {
        var c = e.getParameter("className");
        var m = this._mBreakpointData.classes[this._classPrefix + c];
        for (var a in m) {
            this.changeClassBreakpoint(c, a.replace(this._methodPrefix, ""), false)
        }
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.getInstanceMethods = function(c) {
        var C = sap.ui.getCore().byId(c),
            m = [];
        if (!C) return m;
        for (var p in C) {
            if (!$.isFunction(C[p])) continue;
            m.push({
                name: p,
                active: this.isInstanceBreakpointActive(C, p)
            })
        }
        return m.sort(function(a, b) {
            return ((a.name < b.name) ? -1 : ((a.name > b.name) ? 1 : 0))
        })
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.getClassMethods = function(c) {
        var O = jQuery.sap.getObject(c);
        var m = [];
        if (!O) return m;
        for (var k in O) {
            if (!$.isFunction(O[k])) continue;
            m.push({
                name: k,
                type: this._methodType.clazz,
                active: this.isClassBreakpointActive(c, k)
            })
        }
        for (var k in O.prototype) {
            if (!$.isFunction(O.prototype[k])) continue;
            if ($.grep(m, function(o) {
                return (o.name === k)
            }).length === 1) continue;
            m.push({
                name: k,
                type: this._methodType.proto,
                active: this.isClassBreakpointActive(c, k)
            })
        }
        return m.sort(function(a, b) {
            return ((a.name < b.name) ? -1 : ((a.name > b.name) ? 1 : 0))
        })
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.getClasses = function() {
        function f() {
            var c = [];
            var m = jQuery.sap.getAllDeclaredModules();
            for (var i = 0; i < m.length; i++) {
                if (c.indexOf(m[i]) > -1) continue;
                var o = jQuery.sap.getObject(m[i]);
                if (typeof(o) === 'undefined' || o === null) continue;
                if (typeof(o.getMetadata) === 'function' && o.getMetadata() instanceof sap.ui.core.ElementMetadata) {
                    c.push(o.getMetadata().getName())
                }
            }
            return c
        }
        return f().sort()
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.changeInstanceBreakpoint = function(c, m, a) {
        var C = sap.ui.getCore().byId(c);
        if (!C || !m || !C[m]) return;
        if (this.isInstanceBreakpointActive(C, m) === a) return;
        if (a) {
            this.applyInstanceMethodHook(c, C, m)
        } else {
            this.removeInstanceMethodHook(c, C, m)
        }
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.changeClassBreakpoint = function(c, m, a, t) {
        var C = jQuery.sap.getObject(c);
        if (!C || !m) return;
        if (this.isClassBreakpointActive(c, m) === a) return;
        if (a) {
            this.applyClassMethodHook(c, C, m, t)
        } else {
            this.removeClassMethodHook(c, C, m)
        }
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.getInstanceBreakpointData = function(c, i) {
        if (typeof(i) === 'undefined') i = false;
        var C = this._mBreakpointData.instances[this._classPrefix + c.getMetadata().getName()];
        if (!C) {
            if (i) {
                this._mBreakpointData.instances[this._classPrefix + c.getMetadata().getName()] = C = {}
            } else {
                return null
            }
        }
        var I = C[this._idPrefix + c.getId()];
        if (!I) {
            if (i) {
                C[this._idPrefix + c.getId()] = I = {}
            } else {
                return null
            }
        }
        return I
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.getClassBreakpointData = function(c, i) {
        if (typeof(i) === 'undefined') i = false;
        var C = this._mBreakpointData.classes[this._classPrefix + c];
        if (!C) {
            if (i) {
                this._mBreakpointData.classes[this._classPrefix + c] = C = {}
            } else {
                return null
            }
        }
        return C
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.isInstanceBreakpointActive = function(c, m) {
        var C = this.getInstanceBreakpointData(c);
        if (!C) return false;
        var M = C[this._methodPrefix + m];
        if (!M) return false;
        return M.active
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.isClassBreakpointActive = function(c, m) {
        var C = this.getClassBreakpointData(c);
        if (!C) return false;
        var M = C[this._methodPrefix + m];
        if (!M) return false;
        return M.active
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.applyInstanceMethodHook = function(c, C, m) {
        var a = this.getInstanceBreakpointData(C, true),
            M = a[this._methodPrefix + m] || (a[this._methodPrefix + m] = {});
        M.originalMethod = C[m];
        M.active = true;
        C[m] = this.methodHook(M.originalMethod)
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.removeInstanceMethodHook = function(c, C, m) {
        var a = this.getInstanceBreakpointData(C, true),
            M = a[this._methodPrefix + m];
        C[m] = M.originalMethod;
        delete M.originalMethod;
        M.active = false
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.applyClassMethodHook = function(c, C, m, t) {
        var a = this.getClassBreakpointData(c, true),
            M = a[this._methodPrefix + m] || (a[this._methodPrefix + m] = {});
        if (t === this._methodType.clazz) {
            M.originalMethod = C[m];
            C[m] = this.methodHook(M.originalMethod)
        } else {
            M.originalMethod = C.prototype[m];
            C.prototype[m] = this.methodHook(M.originalMethod)
        }
        M.type = t;
        M.active = true
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.removeClassMethodHook = function(c, C, m) {
        var a = this.getClassBreakpointData(c, true),
            M = a[this._methodPrefix + m];
        if (M.type === this._methodType.clazz) {
            C[m] = M.originalMethod
        } else {
            C.prototype[m] = M.originalMethod
        }
        delete M.originalMethod;
        M.active = false
    };
    sap.ui.core.support.plugins.Breakpoint.prototype.methodHook = function(m) {
        var t = this;
        return function() {
            var a = (new Date()).getTime();
            debugger;
            if ((new Date().getTime()) - a < 50) {
                t._alertNoDebugger()
            };
            return m.apply(this, arguments)
        }
    };
    sap.ui.core.support.plugins.Breakpoint.prototype._alertNoDebugger = function() {
        if (this._bAlertNoDebugger) return;
        var t = null;
        if ( !! sap.ui.Device.browser.chrome) {
            t = "Please open your debugger by pressing CTRL + SHIFT + I."
        }
        if ( !! sap.ui.Device.browser.internet_explorer) {
            t = "Please open your debugger using F12, go to the 'Script' tab and attach it by pressing F5."
        }
        if (t == null) {
            t = "Please open your debugger."
        }
        this._bAlertNoDebugger = true;
        alert("There is no debugger attached.\n\n" + t)
    }
})(jQuery);