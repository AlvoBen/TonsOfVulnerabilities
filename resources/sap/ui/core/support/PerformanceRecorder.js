﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.support.PerformanceRecorder");
sap.ui.core.support.PerformanceRecorder = {};

sap.ui.core.support.PerformanceRecorder.start = function(c, i) {
    sap.ui.core.support.PerformanceRecorder.config = c;
    sap.ui.core.support.PerformanceRecorder.interactionSteps = i;
    sap.ui.core.support.PerformanceRecorder.interactionPointer = 0;
    sap.ui.core.support.PerformanceRecorder.stepPointer = 0;
    jQuery.sap.measure.setActive(true);
    sap.ui.core.support.PerformanceRecorder.processStepStart()
};

sap.ui.core.support.PerformanceRecorder.processStepStart = function() {
    var c = sap.ui.core.support.PerformanceRecorder.interactionSteps[sap.ui.core.support.PerformanceRecorder.interactionPointer];
    var a = c.steps[sap.ui.core.support.PerformanceRecorder.stepPointer];
    if (a.startTriggerEvent == "immediate") {
        if (sap.ui.core.support.PerformanceRecorder.stepPointer == 0) {
            jQuery.sap.measure.start(c.id, c.description)
        }
        jQuery.sap.measure.start(a.id, c.id);
        sap.ui.core.support.PerformanceRecorder.processStepStop()
    } else if (a.startTriggerEvent == "UIUpdated") {
        sap.ui.getCore().attachEvent(sap.ui.core.Core.M_EVENTS.UIUpdated, function() {
            if (sap.ui.core.support.stepPointer == 0) {
                jQuery.sap.measure.start(c.id, c.description)
            }
            jQuery.sap.measure.start(a.id, c.id);
            sap.ui.core.support.PerformanceRecorder.processStepStop()
        })
    } else if (a.startTriggerId && a.startTriggerEvent) {
        var t = sap.ui.getCore().byId(a.startTriggerId);
        sap.ui.core.support.PerformanceRecorder.oTriggerEvent = {};
        sap.ui.core.support.PerformanceRecorder.oTriggerEvent[a.startTriggerEvent] = function() {
            if (sap.ui.core.support.PerformanceRecorder.stepPointer == 0) {
                jQuery.sap.measure.start(c.id, c.description)
            }
            jQuery.sap.measure.start(a.id, c.id);
            sap.ui.core.support.PerformanceRecorder.processStepStop()
        };
        t.addDelegate(sap.ui.core.support.PerformanceRecorder.oTriggerEvent, true)
    }
};

sap.ui.core.support.PerformanceRecorder.processStepStop = function() {
    var c = sap.ui.core.support.PerformanceRecorder.interactionSteps[sap.ui.core.support.PerformanceRecorder.interactionPointer];
    var a = c.steps[sap.ui.core.support.PerformanceRecorder.stepPointer];
    if (a.startTriggerEvent == "UIUpdated") {
        sap.ui.getCore().detachEvent(sap.ui.core.Core.M_EVENTS.UIUpdated, sap.ui.core.support.PerformanceRecorder.processStepStop)
    } else if (a.startTriggerId && a.startTriggerEvent) {
        var t = sap.ui.getCore().byId(a.startTriggerId);
        t.removeDelegate(sap.ui.core.support.PerformanceRecorder.oTriggerEvent)
    }
    if (a.stopTriggerEvent == "UIUpdated") {
        sap.ui.getCore().attachEvent(sap.ui.core.Core.M_EVENTS.UIUpdated, sap.ui.core.support.PerformanceRecorder.concludeStep)
    } else if (a.stopTriggerId && a.stopTriggerEvent) {
        var t = sap.ui.getCore().byId(a.stopTriggerId);
        sap.ui.core.support.PerformanceRecorder.oTriggerEvent = {};
        sap.ui.core.support.PerformanceRecorder.oTriggerEvent[a.stopTriggerEvent] = function() {
            sap.ui.core.support.PerformanceRecorder.concludeStep()
        };
        t.addDelegate(sap.ui.core.support.PerformanceRecorder.oTriggerEvent, true)
    }
};

sap.ui.core.support.PerformanceRecorder.concludeStep = function() {
    var c = sap.ui.core.support.PerformanceRecorder.interactionSteps[sap.ui.core.support.PerformanceRecorder.interactionPointer];
    var a = c.steps[sap.ui.core.support.PerformanceRecorder.stepPointer];
    var l = sap.ui.core.support.PerformanceRecorder.interactionSteps.length - 1;
    var b = c.steps.length - 1;
    jQuery.sap.measure.end(a.id);
    if (a.stopTriggerEvent == "UIUpdated") {
        sap.ui.getCore().detachEvent(sap.ui.core.Core.M_EVENTS.UIUpdated, sap.ui.core.support.PerformanceRecorder.concludeStep)
    }
    if (sap.ui.core.support.PerformanceRecorder.stepPointer == b) {
        jQuery.sap.measure.end(c.id)
    }
    if (sap.ui.core.support.PerformanceRecorder.interactionPointer < l) {
        if (sap.ui.core.support.PerformanceRecorder.stepPointer < b) {
            sap.ui.core.support.PerformanceRecorder.stepPointer++
        } else {
            sap.ui.core.support.PerformanceRecorder.interactionPointer++;
            sap.ui.core.support.PerformanceRecorder.stepPointer = 0
        }
        sap.ui.core.support.PerformanceRecorder.processStepStart()
    } else {
        sap.ui.core.support.PerformanceRecorder.endRecording()
    }
};

sap.ui.core.support.PerformanceRecorder.endRecording = function() {
    var m = sap.ui.core.support.PerformanceRecorder.getAllMeasurementsAsHAR();
    var d = {
        log: {
            version: "1.2",
            creator: {
                name: "SAPUI5 PerformanceRecorder",
                version: "1.1"
            },
            browser: {
                name: navigator.userAgent,
                version: sap.ui.Device.browser.version
            }
        }
    };
    var p = [];
    var e = [];
    for (var i in m) {
        if (m[i].id.substr(-5) === "_page") {
            var a = {
                startedDateTime: m[i].startedDateTime,
                id: m[i].id,
                title: m[i].pageref,
                pageTimings: {
                    onContentLoad: -1,
                    onLoad: m[i].time
                }
            };
            p.push(a)
        } else {
            e.push(m[i])
        }
    }
    d.log.pages = p;
    d.log.entries = e;
    jQuery.ajax({
        type: 'POST',
        url: sap.ui.core.support.PerformanceRecorder.config.beaconUrl,
        data: d,
        dataType: 'text'
    })
};

sap.ui.core.support.PerformanceRecorder.getAllMeasurementsAsHAR = function() {
    var o = jQuery.sap.measure.getAllMeasurements();
    var m = new Array();
    var f = sap.ui.core.format.DateFormat.getDateTimeInstance({
        pattern: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    });
    jQuery.each(o, function(i, M) {
        var a = f.format(new Date(M.start), true);
        m.push({
            id: M.id,
            pageref: M.info,
            startedDateTime: a,
            time: M.duration,
            request: {
                method: "GET",
                url: M.id,
                httpVersion: "HTTP/1.1",
                cookies: [{
                    dummy: ""
                }],
                headers: [{
                    name: "",
                    value: ""
                }],
                queryString: [{
                    name: "",
                    value: ""
                }],
                headersSize: 0,
                bodySize: 0
            },
            response: {
                status: 200,
                statusText: "OK",
                httpVersion: "HTTP/1.1",
                cookies: [{
                    dummy: ""
                }],
                headers: [{
                    name: "",
                    value: ""
                }],
                content: {
                    size: 0,
                    compression: 0,
                    mimeType: "text/html; charset=utf-8",
                    text: "\n"
                },
                redirectURL: "",
                headersSize: 0,
                bodySize: 0
            },
            cache: {
                beforeRequest: {
                    lastAccess: "",
                    eTag: "",
                    hitCount: ""
                },
                afterRequest: {
                    lastAccess: "",
                    eTag: "",
                    hitCount: ""
                }
            },
            timings: {
                blocked: -1,
                dns: -1,
                connect: -1,
                send: -1,
                wait: -1,
                receive: M.duration,
                ssl: -1
            }
        })
    });
    return m
};