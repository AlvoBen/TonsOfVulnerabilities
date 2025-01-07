﻿jQuery.sap.declare('sap.ui.suite.library-all');
if (!jQuery.sap.isDeclared('sap.ui.suite.QuickViewUtils')) {
    jQuery.sap.declare("sap.ui.suite.QuickViewUtils");
    (function() {
        sap.ui.suite.QuickViewUtils = {
            createQuickView: function(s, c, t, f) {
                var m = new sap.ui.model.odata.ODataModel(s, false);
                var q = new sap.ui.ux3.QuickView({
                    firstTitle: "{title}",
                    firstTitleHref: "{titleLinkURL}",
                    type: "{Thing/text}",
                    icon: "{imageURL}"
                });
                q.setModel(m);
                q.bindElement("/QuickviewConfigs(name='" + c + "',thingKey='" + t + "')", {
                    expand: "Thing,QVAttributes/Attribute,QVActions/Action"
                });
                oMQVC = new sap.ui.suite.hcm.QvContent();
                oMQVC.bindAggregation("items", {
                    path: "QVAttributes",
                    factory: function(i, C) {
                        var Q = new sap.ui.suite.hcm.QvItem(i, {
                            label: "{Attribute/label}",
                            link: "{valueLinkURL}",
                            order: "{order}"
                        });
                        Q.bindProperty("value", "value", f && f[C.getProperty("Attribute/name")]);
                        return Q
                    }
                });
                q.addContent(oMQVC);
                return q
            },
            createQuickViewData: function(q, s, c, t, f) {
                var m = new sap.ui.model.odata.ODataModel(s, false);
                q.removeAllContent();
                q.setModel(m);
                q.bindProperty("firstTitle", "title");
                q.bindProperty("firstTitleHref", "titleLinkURL");
                q.bindProperty("type", "Thing/text");
                q.bindProperty("icon", "imageURL");
                q.bindElement("/QuickviewConfigs(name='" + c + "',thingKey='" + t + "')", {
                    expand: "Thing,QVAttributes/Attribute,QVActions/Action"
                });
                oMQVC = new sap.ui.suite.hcm.QvContent();
                oMQVC.bindAggregation("items", {
                    path: "QVAttributes",
                    factory: function(i, C) {
                        var Q = new sap.ui.suite.hcm.QvItem(i, {
                            label: "{Attribute/label}",
                            link: "{valueLinkURL}",
                            order: "{order}"
                        });
                        Q.bindProperty("value", "value", f && f[C.getProperty("Attribute/name")]);
                        return Q
                    }
                });
                q.addContent(oMQVC)
            },
            createDataSetQuickView: function(s, c, t, p, S) {
                var m = new sap.ui.model.odata.ODataModel(s, false);
                if (S) {
                    m.setSizeLimit(S)
                }
                var q = new sap.ui.ux3.QuickView({
                    type: t,
                    showActionBar: false
                });
                q.setModel(m);
                q.addContent(this._createDSContent(q, c, p));
                return q
            },
            createDataSetQuickViewData: function(q, s, c, t, p, S) {
                var m = new sap.ui.model.odata.ODataModel(s, false);
                if (S) {
                    m.setSizeLimit(S)
                }
                q.removeAllContent();
                q.setType(t);
                q.setShowActionBar(false);
                q.setModel(m);
                q.addContent(this._createDSContent(q, c, p))
            },
            _createDSContent: function(q, c, p) {
                var C = new sap.ui.commons.layout.MatrixLayout();
                var r = new sap.ui.commons.layout.MatrixLayoutRow();
                jQuery.each(p, function(i, P) {
                    var o;
                    if (P.href) {
                        o = new sap.ui.commons.Link({
                            text: P.value,
                            href: P.href
                        })
                    } else {
                        o = new sap.ui.commons.TextView({
                            text: P.value
                        })
                    }
                    var a = new sap.ui.commons.layout.MatrixLayoutCell({
                        content: [o]
                    });
                    a.addStyleClass("quickViewDS");
                    r.addCell(a)
                });
                C.bindAggregation("rows", c, r);
                return C
            }
        };
        sap.ui.core.Element.extend("sap.ui.suite.hcm.QvItem", {
            metadata: {
                properties: {
                    label: "string",
                    value: "string",
                    link: "string",
                    order: "string",
                    type: "string"
                }
            }
        });
        sap.ui.core.Control.extend("sap.ui.suite.hcm.QvContent", {
            metadata: {
                aggregations: {
                    "items": {
                        type: "sap.ui.suite.hcm.QvItem",
                        multiple: true
                    }
                }
            },
            init: function() {
                this._sorted = false
            },
            exit: function() {
                if (this._oML) {
                    this._oML.destroy()
                }
            },
            renderer: function(r, c) {
                r.write("<div");
                r.writeControlData(c);
                r.write(">");
                r.renderControl(c._createQVContent(c));
                r.write("</div>")
            },
            _createQVContent: function(c) {
                var m = new sap.ui.commons.layout.MatrixLayout({
                    widths: ["75px"]
                }),
                    I = c.getItems(),
                    M, o, l, t, L;
                if (this._oML) {
                    this._oML.destroy()
                };
                c._sortItems(c);
                for (var i = 0; i < I.length; i++) {
                    M = new sap.ui.commons.layout.MatrixLayoutRow();
                    o = new sap.ui.commons.layout.MatrixLayoutCell({
                        vAlign: 'Top'
                    });
                    l = new sap.ui.commons.Label({
                        text: I[i].getLabel() + ':'
                    });
                    o.addContent(l);
                    M.addCell(o);
                    o = new sap.ui.commons.layout.MatrixLayoutCell();
                    if (I[i].getLink()) {
                        L = new sap.ui.commons.Link({
                            text: I[i].getValue(),
                            href: I[i].getLink()
                        });
                        o.addContent(L)
                    } else {
                        t = new sap.ui.commons.TextView({
                            text: I[i].getValue()
                        });
                        o.addContent(t)
                    }
                    M.addCell(o);
                    m.addRow(M)
                }
                this._oML = m;
                return m
            },
            _sortItems: function(c) {
                if (!c._sorted) {
                    var I = c.removeAllAggregation("items", true);
                    I.sort(function(a, b) {
                        return (parseInt(a.getOrder(), 10) - parseInt(b.getOrder(), 10))
                    });
                    jQuery.each(I, function(i, o) {
                        c.addAggregation("items", o, false)
                    });
                    c._sorted = true
                }
            }
        })
    })()
};
if (!jQuery.sap.isDeclared('sap.ui.suite.TaskCircleRenderer')) {
    jQuery.sap.declare("sap.ui.suite.TaskCircleRenderer");
    sap.ui.suite.TaskCircleRenderer = function() {};
    sap.ui.suite.TaskCircleRenderer.render = function(r, c) {
        var a = r;
        var m = c.getMinValue();
        var b = c.getMaxValue();
        var v = c.getValue();
        if (m < 0 || m == Number.NaN) {
            m = 0
        }
        if (b < 0 || b == Number.NaN) {
            b = 1
        }
        if (v < 0 || v == Number.NaN) {
            v = 0
        }
        var d = v.toString();
        var e = c.getColor();
        var s = 'sapUiTaskCircleColorGray';
        switch (e) {
            case sap.ui.suite.TaskCircleColor.Red:
                s = 'sapUiTaskCircleColorRed';
                break;
            case sap.ui.suite.TaskCircleColor.Yellow:
                s = 'sapUiTaskCircleColorYellow';
                break;
            case sap.ui.suite.TaskCircleColor.Green:
                s = 'sapUiTaskCircleColorGreen';
                break;
            case sap.ui.suite.TaskCircleColor.Gray:
                s = 'sapUiTaskCircleColorGray';
                break
        }
        if (v < m) {
            m = v
        }
        if (v > b) {
            b = v
        }
        var p = 24;
        if (m > 10) {
            p = 32
        }
        if (m > 100) {
            p = 46
        }
        var f = 62;
        var g = parseInt(Math.sqrt((v - m) / (b - m) * (f * f - p * p) + p * p), 10);
        var h = (v + '').length;
        var i = g * 0.55;
        if (h > 1) {
            i = g / h
        }
        a.write("<div");
        a.writeControlData(c);
        a.writeAttribute('tabIndex', '0');
        if (c.getTooltip_AsString()) {
            a.writeAttributeEscaped("title", c.getTooltip_AsString())
        } else {
            a.writeAttributeEscaped("title", d)
        }
        if (sap.ui.getCore().getConfiguration().getAccessibility()) {
            a.writeAttribute('role', 'progressbar');
            a.writeAccessibilityState(c, {
                valuemin: m
            });
            a.writeAccessibilityState(c, {
                valuemax: b
            });
            a.writeAccessibilityState(c, {
                valuenow: v
            })
        }
        a.writeAttribute("class", "sapUiTaskCircle " + s);
        a.addStyle("width", g + "px");
        a.addStyle("height", g + "px");
        a.addStyle("line-height", g + "px");
        a.addStyle("font-size", parseInt(i, 10) + "px");
        a.addStyle("border-radius", g + "px");
        a.addStyle("-moz-border-radius", g + "px");
        a.writeClasses();
        a.writeStyles();
        a.write(">");
        a.write(v);
        a.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.suite.VerticalProgressIndicatorRenderer')) {
    jQuery.sap.declare("sap.ui.suite.VerticalProgressIndicatorRenderer");
    sap.ui.suite.VerticalProgressIndicatorRenderer = {};
    sap.ui.suite.VerticalProgressIndicatorRenderer.render = function(r, c) {
        var a = r;
        var V = c.getPercentage();
        if (V < 0 || V == Number.NaN) V = 0;
        if (V > 100) V = 100;
        var P = Math.round(V * 58 / 100);
        var b = 58 - P;
        var d = V.toString();
        a.write("<DIV");
        a.writeControlData(c);
        a.writeAttribute('tabIndex', '0');
        if (c.getTooltip_AsString()) {
            a.writeAttributeEscaped("title", c.getTooltip_AsString())
        } else {
            a.writeAttributeEscaped("title", d)
        }
        if (sap.ui.getCore().getConfiguration().getAccessibility()) {
            a.writeAttribute('role', 'progressbar');
            a.writeAccessibilityState(c, {
                valuemin: '0%'
            });
            a.writeAccessibilityState(c, {
                valuemax: '100%'
            });
            a.writeAccessibilityState(c, {
                valuenow: V + '%'
            })
        }
        a.writeAttribute("class", "sapUiVerticalProgressOuterContainer");
        a.write(">");
        a.write("<DIV");
        a.writeAttribute('id', c.getId() + '-bar');
        a.writeAttribute("class", "sapUiVerticalProgressInnerContainer");
        a.addStyle("top", b + "px");
        a.addStyle("height", P + "px");
        a.writeClasses();
        a.writeStyles();
        a.write(">");
        a.write("</DIV>");
        a.write("</DIV>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.suite.library')) {

    /*!
     * SAP UI development toolkit for HTML5 (SAPUI5)
     * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
     * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
     */

    jQuery.sap.declare("sap.ui.suite.library");
    jQuery.sap.require('sap.ui.core.Core');
    jQuery.sap.require('sap.ui.core.library');
    sap.ui.getCore().initLibrary({
        name: "sap.ui.suite",
        dependencies: ["sap.ui.core"],
        types: ["sap.ui.suite.TaskCircleColor"],
        interfaces: [],
        controls: ["sap.ui.suite.TaskCircle", "sap.ui.suite.VerticalProgressIndicator"],
        elements: [],
        version: "1.16.8-SNAPSHOT"
    });
    jQuery.sap.declare("sap.ui.suite.TaskCircleColor");
    sap.ui.suite.TaskCircleColor = {
        Red: "Red",
        Yellow: "Yellow",
        Green: "Green",
        Gray: "Gray"
    }
};
if (!jQuery.sap.isDeclared('sap.ui.suite.TaskCircle')) {
    jQuery.sap.declare("sap.ui.suite.TaskCircle");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.suite.TaskCircle", {
        metadata: {
            publicMethods: ["focus"],
            library: "sap.ui.suite",
            properties: {
                "value": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "maxValue": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 100
                },
                "minValue": {
                    type: "int",
                    group: "Misc",
                    defaultValue: 0
                },
                "color": {
                    type: "sap.ui.suite.TaskCircleColor",
                    group: "Misc",
                    defaultValue: sap.ui.suite.TaskCircleColor.Gray
                }
            },
            associations: {
                "ariaLabelledBy": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "ariaLabelledBy"
                },
                "ariaDescribedBy": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "ariaDescribedBy"
                }
            },
            events: {
                "press": {}
            }
        }
    });
    sap.ui.suite.TaskCircle.M_EVENTS = {
        'press': 'press'
    };
    jQuery.sap.require('sap.ui.core.EnabledPropagator');
    sap.ui.core.EnabledPropagator.call(sap.ui.suite.TaskCircle.prototype);
    sap.ui.suite.TaskCircle.prototype.init = function() {};
    sap.ui.suite.TaskCircle.prototype.onclick = function(e) {
        this.firePress({});
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.suite.TaskCircle.prototype.focus = function() {
        var d = this.getDomRef();
        if (d) {
            d.focus()
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.suite.VerticalProgressIndicator')) {
    jQuery.sap.declare("sap.ui.suite.VerticalProgressIndicator");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.suite.VerticalProgressIndicator", {
        metadata: {
            publicMethods: ["focus"],
            library: "sap.ui.suite",
            properties: {
                "percentage": {
                    type: "int",
                    group: "Misc",
                    defaultValue: null
                }
            },
            associations: {
                "ariaLabelledBy": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "ariaLabelledBy"
                },
                "ariaDescribedBy": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "ariaDescribedBy"
                }
            },
            events: {
                "press": {}
            }
        }
    });
    sap.ui.suite.VerticalProgressIndicator.M_EVENTS = {
        'press': 'press'
    };
    jQuery.sap.require('sap.ui.core.EnabledPropagator');
    sap.ui.core.EnabledPropagator.call(sap.ui.suite.VerticalProgressIndicator.prototype);
    sap.ui.suite.VerticalProgressIndicator.prototype.setPercentage = function(p) {
        var V = this.getPercentage();
        if (V == p) return this;
        this.oBar = jQuery.sap.domById(this.getId() + '-bar');
        V = p;
        if (V < 0 || V == Number.NaN) V = 0;
        if (V > 100) V = 100;
        var P = Math.round(V * 58 / 100);
        var a = 58 - P;
        this.setProperty('percentage', p, true);
        jQuery(this.oBar).css("top", a);
        jQuery(this.oBar).css("height", P);
        if (!this.oThis) {
            this.oThis = jQuery.sap.byId(this.getId())
        }
        this.oThis.attr('aria-valuenow', p + '%');
        return this
    };
    sap.ui.suite.VerticalProgressIndicator.prototype.onclick = function(e) {
        this.firePress({});
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.suite.VerticalProgressIndicator.prototype.focus = function() {
        var d = this.getDomRef();
        if (d) {
            d.focus()
        }
    }
};