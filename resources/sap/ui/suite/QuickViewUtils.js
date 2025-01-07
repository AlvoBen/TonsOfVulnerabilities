﻿jQuery.sap.declare("sap.ui.suite.QuickViewUtils");
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
})();