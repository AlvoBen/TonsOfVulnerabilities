﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.core.support.trace.E2eTraceLib");
jQuery.sap.require("sap.ui.core.support.trace.EppLib");
sap.ui.core.support.trace.E2eTraceLib = (function() {
    var E = sap.ui.core.support.trace.EppLib;
    var t = /sap-ui-xx-e2e-trace-level=(low|medium|high)/.exec(location.search);
    var d;
    if (t && t.length >= 2) {
        d = t[1]
    } else {
        d = "medium"
    }
    var b;
    var a = false;
    var M = function(x) {
        this.idx = x.xidx;
        this.dsrGuid = x.xDsrGuid;
        this.method = x.xmethod;
        this.url = x.xurl;
        this.reqHeader = x.xRequestHeaders;
        this.respHeader = x.getAllResponseHeaders();
        this.statusCode = x.status;
        this.status = x.statusText;
        this.startTimestamp = x.xstartTimestamp;
        this.firstByteSent = x.xfirstByteSent ? x.xfirstByteSent : x.xstartTimestamp;
        this.lastByteSent = this.firstByteSent;
        this.firstByteReceived = x.xfirstByteReceived ? x.xfirstByteReceived : x.xlastByteReceived;
        this.lastByteReceived = x.xlastByteReceived;
        this.sentBytes = 0;
        this.receivedBytes = x.responseText.length;
        this.getDuration = function() {
            return this.lastByteReceived - this.startTimestamp
        };
        this.getRequestLine = function() {
            return this.method + " " + this.url + " HTTP/?.?"
        };
        this.getRequestHeader = function() {
            var r = this.getRequestLine() + "&#13;\n";
            for (var i = 0, l = this.reqHeader.length; i < l; i += 1) {
                r += this.reqHeader[i][0] + ": " + this.reqHeader[i][1] + "&#13;\n"
            }
            r += "&#13;\n";
            return r
        };
        this.getResponseHeader = function() {
            var r = "HTTP?/? " + this.statusCode + " " + this.status + "&#13;\n";
            r += this.respHeader;
            r += "&#13;\n";
            return r
        }
    };
    var T = function(b, f, g, h) {
        this.busTrx = b;
        this.trxStepIdx = f;
        this.name = "Step-" + (f + 1);
        this.date = g;
        this.trcLvl = h;
        this.messages = [];
        this.msgIdx = -1;
        this.pendingMessages = 0;
        this.transactionStepTimeoutId = null;
        this.messageStarted = function() {
            this.msgIdx += 1;
            this.pendingMessages += 1;
            return this.msgIdx
        };
        this.onMessageFinished = function(x, i) {
            x.xlastByteReceived = i;
            this.messages.push(new M(x));
            this.pendingMessages -= 1;
            if (this.pendingMessages === 0) {
                if (this.transactionStepTimeoutId) {
                    clearTimeout(this.transactionStepTimeoutId)
                }
                this.transactionStepTimeoutId = setTimeout(o, 3000)
            }
        };
        this.getId = function() {
            return this.busTrx.id + "-" + this.trxStepIdx
        };
        this.getTraceFlagsAsString = function() {
            return this.trcLvl[1].toString(16) + this.trcLvl[0].toString(16)
        }
    };
    var B = function(i, f, g, C) {
        this.id = i;
        this.date = f;
        this.trcLvl = g;
        this.trxSteps = [];
        this.fnCallback = C;
        this.createTransactionStep = function() {
            var h = new T(this, this.trxSteps.length, new Date(), this.trcLvl);
            this.trxSteps.push(h)
        };
        this.getCurrentTransactionStep = function() {
            return this.trxSteps[this.trxSteps.length - 1]
        };
        this.getBusinessTransactionXml = function() {
            var x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><BusinessTransaction id=\"" + this.id + "\" time=\"" + c(this.date) + "\" name=\"SAPUI5 Business Transaction\">";
            for (var h = 0, n = this.trxSteps.length; h < n; h += 1) {
                var j = this.trxSteps[h];
                x += "<TransactionStep id=\"" + j.getId() + "\" time=\"" + c(j.date) + "\" name=\"" + j.name + "\" traceflags=\"" + j.getTraceFlagsAsString() + "\">";
                var m = j.messages;
                for (var k = 0, l = m.length; k < l; k += 1) {
                    var p = m[k];
                    x += "<Message id=\"" + p.idx + "\" dsrGuid=\"" + p.dsrGuid + "\">";
                    x += "<x-timestamp>" + c(new Date(p.startTimestamp)) + "</x-timestamp>";
                    x += "<duration>" + p.getDuration() + "</duration>";
                    x += "<returnCode>" + p.statusCode + "</returnCode>";
                    x += "<sent>" + p.sentBytes + "</sent>";
                    x += "<rcvd>" + p.receivedBytes + "</rcvd>";
                    if (p.firstByteSent && p.lastByteReceived) {
                        x += "<firstByteSent>" + c(new Date(p.firstByteSent)) + "</firstByteSent>";
                        x += "<lastByteSent>" + c(new Date(p.lastByteSent)) + "</lastByteSent>";
                        x += "<firstByteReceived>" + c(new Date(p.firstByteReceived)) + "</firstByteReceived>";
                        x += "<lastByteReceived>" + c(new Date(p.lastByteReceived)) + "</lastByteReceived>"
                    }
                    x += "<requestLine><![CDATA[" + p.getRequestLine() + "]]></requestLine>";
                    x += "<requestHeader><![CDATA[" + p.getRequestHeader() + "]]></requestHeader>";
                    x += "<responseHeader><![CDATA[" + p.getResponseHeader() + "]]></responseHeader>";
                    x += "</Message>"
                }
                x += "</TransactionStep>"
            }
            x += "</BusinessTransaction>";
            return x
        }
    };
    var o = function() {
        if (b.getCurrentTransactionStep().pendingMessages === 0 && b.getCurrentTransactionStep().messages.length > 0) {
            var r = confirm("End of transaction step detected.\nNumber of new message(s): " + b.getCurrentTransactionStep().messages.length + "\n\nDo you like to record another transaction step?");
            if (r) {
                b.createTransactionStep()
            } else {
                (function() {
                    var f = b.getBusinessTransactionXml();
                    if (b.fnCallback && typeof(b.fnCallback) === 'function') {
                        b.fnCallback(f)
                    }
                    var x = window.open('', '', 'width=800,height=150');
                    var g = "----------ieoau._._+2_8_GoodLuck8.3-ds0d0J0S0Kl234324jfLdsjfdAuaoei-----";
                    var p = g + "\r\nContent-Disposition: form-data\r\nContent-Type: application/xml\r\n" + f + "\r\n" + g;
                    x.document.write("<script>");
                    x.document.write("function postXml() {");
                    x.document.write("var smdUrl = document.getElementsByName('smdUrl')[0].value;");
                    x.document.write("var postbody = document.getElementById('myPostDataTextarea').value;");
                    x.document.write("var xmlHttpPost = new XMLHttpRequest();");
                    x.document.write("xmlHttpPost.open('POST', smdUrl+'/E2EClientTraceUploadW/UploadForm.jsp', false);");
                    x.document.write("xmlHttpPost.setRequestHeader('Content-type', 'multipart/form-data; boundary=" + g + "');");
                    x.document.write("xmlHttpPost.send(postbody);");
                    x.document.write("document.getElementById('myUploadResult').innerHTML=xmlHttpPost.responseText;");
                    x.document.write("}</script>");
                    x.document.write("<div id='myUploadResult'></div>");
                    x.document.write("<div>");
                    x.document.write("SMD url: <input name='smdUrl' ltype='text' value='http://<host>:<port>' />");
                    x.document.write("<button onclick='postXml()'>Submit</button>");
                    x.document.write("<textarea id='myPostDataTextarea' style='width:100%;height:100px;'>" + p + "</textarea>");
                    x.document.write("</div>")
                })();
                b = null;
                a = false
            }
        }
    };
    var c = function(f) {
        var u = "";
        u += f.getUTCDate() < 10 ? "0" + f.getUTCDate() : f.getUTCDate();
        u += "." + (f.getUTCMonth() < 9 ? "0" + (f.getUTCMonth() + 1) : f.getUTCMonth() + 1);
        u += "." + f.getUTCFullYear();
        u += " " + (f.getUTCHours() < 10 ? "0" + f.getUTCHours() : f.getUTCHours());
        u += ":" + (f.getUTCMinutes() < 10 ? "0" + f.getUTCMinutes() : f.getUTCMinutes());
        u += ":" + (f.getUTCSeconds() < 10 ? "0" + f.getUTCSeconds() : f.getUTCSeconds());
        u += "." + (f.getUTCMilliseconds() < 100 ? f.getUTCMilliseconds() < 10 ? "00" + f.getUTCMilliseconds() : "0" + f.getUTCMilliseconds() : f.getUTCMilliseconds());
        u += " UTC";
        return u
    };
    (function() {
        var f, g;
        f = window.XMLHttpRequest.prototype.open;
        g = window.XMLHttpRequest.prototype.setRequestHeader;

        function h(m) {
            this.xfirstByteSent = m.timeStamp
        }
        function i(m) {
            if (m.loaded > 0) {
                if (!this.xfirstByteReceived) {
                    this.xfirstByteReceived = m.timeStamp
                }
                this.xlastByteReceived = m.timeStamp
            }
        }
        function j(m) {
            b.getCurrentTransactionStep().onMessageFinished(this, m.timeStamp)
        }
        function k(m) {
            b.getCurrentTransactionStep().onMessageFinished(this, m.timeStamp)
        }
        function l(m) {
            b.getCurrentTransactionStep().onMessageFinished(this, m.timeStamp)
        }
        window.XMLHttpRequest.prototype.setRequestHeader = function() {
            g.apply(this, arguments);
            if (a) {
                this.xRequestHeaders.push(arguments)
            }
        };
        window.XMLHttpRequest.prototype.open = function() {
            f.apply(this, arguments);
            if (a) {
                var m = b.getCurrentTransactionStep().messageStarted();
                this.xidx = m;
                this.xstartTimestamp = Date.now();
                this.xmethod = arguments[0];
                this.xurl = arguments[1];
                this.xRequestHeaders = [];
                this.xDsrGuid = E.createGUID();
                this.setRequestHeader("SAP-PASSPORT", E.passportHeader(b.getCurrentTransactionStep().trcLvl, b.id, this.xDsrGuid));
                this.setRequestHeader("X-CorrelationID", b.getCurrentTransactionStep().getId() + "-" + m);
                this.addEventListener("loadstart", h, false);
                this.addEventListener("progress", i, false);
                this.addEventListener("error", j, false);
                this.addEventListener("abort", k, false);
                this.addEventListener("load", l, false);
                m += 1
            }
        }
    })();
    var e = {
        start: function(s, C) {
            if (!a) {
                if (!s) {
                    s = d
                }
                b = new B(E.createGUID(), new Date(), E.traceFlags(s), C);
                b.createTransactionStep();
                a = true
            }
        },
        isStarted: function() {
            return a
        }
    };
    if (/sap-ui-xx-e2e-trace=(true|x|X)/.test(location.search)) {
        e.start()
    }
    return e
}());