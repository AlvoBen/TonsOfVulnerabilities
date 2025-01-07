﻿jQuery.sap.declare('sap.ui.unified.library-all');
if (!jQuery.sap.isDeclared('sap.ui.unified.ContentSwitcherRenderer')) {

    /*!
     * SAP UI development toolkit for HTML5 (SAPUI5)
     * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
     * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
     */

    jQuery.sap.declare("sap.ui.unified.ContentSwitcherRenderer");
    sap.ui.unified.ContentSwitcherRenderer = {};
    sap.ui.unified.ContentSwitcherRenderer.render = function(r, c) {
        var i = c.getId();
        var a = c.getAnimation();
        if (!sap.ui.getCore().getConfiguration().getAnimation()) {
            a = sap.ui.unified.ContentSwitcherAnimation.None
        }
        var A = c.getActiveContent();
        r.write("<div");
        r.writeControlData(c);
        r.addClass("sapUiUfdCSwitcher");
        r.addClass("sapUiUfdCSwitcherAnimation" + a);
        r.writeClasses();
        r.write(">");
        r.write("<section id=\"" + i + "-content1\" class=\"sapUiUfdCSwitcherContent sapUiUfdCSwitcherContent1" + (A == 1 ? " sapUiUfdCSwitcherVisible" : "") + "\">");
        this.renderContent(r, c.getContent1());
        r.write("</section>");
        r.write("<section id=\"" + i + "-content2\" class=\"sapUiUfdCSwitcherContent sapUiUfdCSwitcherContent2" + (A == 2 ? " sapUiUfdCSwitcherVisible" : "") + "\">");
        this.renderContent(r, c.getContent2());
        r.write("</section>");
        r.write("</div>")
    };
    sap.ui.unified.ContentSwitcherRenderer.renderContent = function(r, c) {
        for (var i = 0; i < c.length; ++i) {
            r.renderControl(c[i])
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.unified.ShellOverlayRenderer')) {
    jQuery.sap.declare("sap.ui.unified.ShellOverlayRenderer");
    sap.ui.unified.ShellOverlayRenderer = {};
    sap.ui.unified.ShellOverlayRenderer.render = function(r, c) {
        r.write("<div");
        r.writeControlData(c);
        r.addClass("sapUiUfdShellOvrly");
        if (c._opening) {
            r.addClass("sapUiUfdShellOvrlyCntntHidden");
            r.addClass("sapUiUfdShellOvrlyOpening")
        }
        if (c._getAnimActive()) {
            r.addClass("sapUiUfdShellOvrlyAnim")
        }
        r.writeClasses();
        r.write("><div>");
        r.write("<header class='sapUiUfdShellOvrlyHead'>");
        r.write("<hr class='sapUiUfdShellOvrlyBrand'/>");
        r.write("<div class='sapUiUfdShellOvrlyHeadCntnt'>");
        r.write("<div id='" + c.getId() + "-hdr-center' class='sapUiUfdShellOvrlyHeadCenter'>");
        sap.ui.unified.ShellOverlayRenderer.renderSearch(r, c);
        r.write("</div>");
        var a = sap.ui.getCore().getLibraryResourceBundle("sap.ui.unified"),
            C = a.getText("SHELL_OVERLAY_CLOSE");
        r.write("<a tabindex='0' href='javascript:void(0);' id='" + c.getId() + "-close' class='sapUiUfdShellOvrlyHeadClose'");
        r.writeAttributeEscaped("title", C);
        r.write(">");
        r.writeEscaped(C);
        r.write("</a></div></header>");
        r.write("<div id='" + c.getId() + "-cntnt' class='sapUiUfdShellOvrlyCntnt'>");
        sap.ui.unified.ShellOverlayRenderer.renderContent(r, c);
        r.write("</div>");
        r.write("</div></div>")
    };
    sap.ui.unified.ShellOverlayRenderer.renderSearch = function(r, c) {
        var w = c._getSearchWidth();
        var s = "";
        if (w > 0 && c._opening) {
            s = "style='width:" + w + "px'"
        }
        r.write("<div id='" + c.getId() + "-search' class='sapUiUfdShellOvrlySearch' " + s + ">");
        var S = c.getSearch();
        if (S) {
            r.renderControl(S)
        }
        r.write("</div>")
    };
    sap.ui.unified.ShellOverlayRenderer.renderContent = function(r, c) {
        r.write("<div>");
        var C = c.getContent();
        for (var i = 0; i < C.length; i++) {
            r.renderControl(C[i])
        }
        r.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.unified.ShellRenderer')) {
    jQuery.sap.declare("sap.ui.unified.ShellRenderer");
    sap.ui.unified.ShellRenderer = {};
    sap.ui.unified.ShellRenderer.render = function(r, s) {
        var i = s.getId();
        r.write("<div");
        r.writeControlData(s);
        r.addClass("sapUiUfdShell");
        if (s._animation) {
            r.addClass("sapUiUfdShellAnim")
        }
        r.addClass("sapUiUfdShellHead" + (s._showHeader ? "Visible" : "Hidden"));
        if (s.getShowCurtain()) {
            r.addClass("sapUiUfdShellCurtainVisible")
        } else {
            r.addClass("sapUiUfdShellCurtainHidden");
            r.addClass("sapUiUfdShellCurtainClosed")
        }
        r.writeClasses();
        r.write(">");
        r.write("<hr id='", i, "-brand' class='sapUiUfdShellBrand'/>");
        r.write("<header id='", i, "-hdr'  class='sapUiUfdShellHead'><div>");
        r.write("<div id='", i, "-hdrcntnt' class='sapUiUfdShellCntnt'>");
        sap.ui.unified.ShellRenderer._renderHeaderContent(r, s);
        r.write("</div>", "</div>", "</header>");
        r.write("<section id='", i, "-curt' class='sapUiUfdShellCntnt sapUiUfdShellCurtain'>");
        r.write("<div id='", i, "-curtcntnt' class='sapUiUfdShellCntnt'>");
        r.renderControl(s._curtCont);
        r.write("</div>");
        r.write("<span id='", i, "-curt-focusDummyOut' tabindex='0'></span>");
        r.write("</section>");
        r.write("<div id='", i, "-cntnt' class='sapUiUfdShellCntnt sapUiUfdShellCanvas'>");
        r.renderControl(s._cont);
        r.write("</div>");
        r.write("<span id='", i, "-main-focusDummyOut' tabindex='" + (s.getShowCurtain() ? 0 : -1) + "'></span>");
        r.write("</div>")
    };
    sap.ui.unified.ShellRenderer._renderHeaderContent = function(r, s) {
        var i = s.getId();
        r.write("<div id='", i, "-hdr-begin' class='sapUiUfdShellHeadBegin'>");
        sap.ui.unified.ShellRenderer.renderHeaderItems(r, s, true);
        r.write("</div>");
        r.write("<div id='", i, "-hdr-center' class='sapUiUfdShellHeadCenter'>");
        sap.ui.unified.ShellRenderer.renderSearch(r, s);
        r.write("</div>");
        r.write("<div id='", i, "-hdr-end' class='sapUiUfdShellHeadEnd'>");
        sap.ui.unified.ShellRenderer.renderHeaderItems(r, s, false);
        r.write("</div>")
    };
    sap.ui.unified.ShellRenderer.renderSearch = function(r, s) {
        r.write("<div class='sapUiUfdShellSearch'>");
        var S = s.getSearch();
        if (S) {
            r.renderControl(S)
        }
        r.write("</div>")
    };
    sap.ui.unified.ShellRenderer.renderHeaderItems = function(r, s, b) {
        var I;
        if (b) {
            I = s.getHeadItems()
        } else {
            r.write("<div class='sapUiUfdShellHeadEndContainer'>");
            I = s.getHeadEndItems()
        }
        for (var i = 0; i < I.length; i++) {
            r.write("<a tabindex='0' href='javascript:void(0);'");
            r.writeElementData(I[i]);
            r.addClass("sapUiUfdShellHeadItm");
            if (I[i].getStartsSection()) {
                r.addClass("sapUiUfdShellHeadItmDelim")
            }
            if (I[i].getSelected()) {
                r.addClass("sapUiUfdShellHeadItmSel")
            }
            if (I[i].getShowMarker()) {
                r.addClass("sapUiUfdShellHeadItmMark")
            }
            r.writeClasses();
            var t = I[i].getTooltip_AsString();
            if (t) {
                r.writeAttributeEscaped("title", t)
            }
            r.write("><span></span><div class='sapUiUfdShellHeadItmMarker'><div></div></div></a>")
        }
        if (!b) {
            r.write("</div>");
            sap.ui.unified.ShellRenderer._renderIcon(r, s)
        }
    };
    sap.ui.unified.ShellRenderer._renderIcon = function(r, s) {
        var a = sap.ui.getCore().getLibraryResourceBundle("sap.ui.unified"),
            l = a.getText("SHELL_LOGO_TOOLTIP");
        r.write("<div class='sapUiUfdShellIco'>");
        r.write("<img id='", s.getId(), "-icon'");
        r.writeAttributeEscaped("title", l);
        r.writeAttributeEscaped("alt", l);
        r.write("src='", s._getIcon(), "' style='", s._getIcon() ? "" : "display:none;", "'></img>");
        r.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.unified.SplitContainerRenderer')) {
    jQuery.sap.declare("sap.ui.unified.SplitContainerRenderer");
    sap.ui.unified.SplitContainerRenderer = {};
    sap.ui.unified.SplitContainerRenderer.render = function(r, c) {
        var i = c.getId();
        r.write("<div");
        r.writeControlData(c);
        r.addClass("sapUiUfdSpltCont");
        if (sap.ui.getCore().getConfiguration().getAnimation()) {
            r.addClass("sapUiUfdSpltContAnim")
        }
        if (!c.getShowSecondaryContent()) {
            r.addClass("sapUiUfdSpltContPaneHidden")
        }
        r.writeClasses();
        r.write(">");
        var C = i + "-canvas";
        r.write("<section id='", C, "' class='sapUiUfdSpltContCanvas'>");
        this.renderContent(r, C, c.getContent(), c._bRootContent);
        r.write("</section>");
        var s = i + "-pane";
        var w = c.getShowSecondaryContent() ? c.getSecondaryContentWidth() : "0";
        r.write("<aside id='", s, "' style='width:", w, "'");
        r.addClass("sapUiUfdSpltContPane");
        if (!c.getShowSecondaryContent()) {
            r.addClass("sapUiUfdSplitContSecondClosed")
        }
        r.writeClasses();
        r.write(">");
        this.renderContent(r, s, c.getSecondaryContent(), c._bRootContent);
        r.write("</aside>");
        r.write("</div>")
    };
    sap.ui.unified.SplitContainerRenderer.renderContent = function(r, I, c, R) {
        r.write("<div id='", I, "cntnt' class='sapUiUfdSpltContCntnt'");
        if (R) {
            r.writeAttribute("data-sap-ui-root-content", "true")
        }
        r.write(">");
        for (var i = 0; i < c.length; i++) {
            r.renderControl(c[i])
        }
        r.write("</div>")
    }
};
if (!jQuery.sap.isDeclared('sap.ui.unified.library')) {
    jQuery.sap.declare("sap.ui.unified.library");
    jQuery.sap.require('sap.ui.core.Core');
    jQuery.sap.require('sap.ui.core.library');
    sap.ui.getCore().initLibrary({
        name: "sap.ui.unified",
        dependencies: ["sap.ui.core"],
        types: ["sap.ui.unified.ContentSwitcherAnimation"],
        interfaces: [],
        controls: ["sap.ui.unified.ContentSwitcher", "sap.ui.unified.Shell", "sap.ui.unified.ShellOverlay", "sap.ui.unified.SplitContainer"],
        elements: ["sap.ui.unified.ShellHeadItem"],
        version: "1.16.8-SNAPSHOT"
    });
    jQuery.sap.declare("sap.ui.unified.ContentSwitcherAnimation");
    sap.ui.unified.ContentSwitcherAnimation = {
        None: "None",
        Fade: "Fade",
        ZoomIn: "ZoomIn",
        ZoomOut: "ZoomOut",
        Rotate: "Rotate",
        SlideRight: "SlideRight",
        SlideOver: "SlideOver"
    };
    sap.ui.base.Object.extend("sap.ui.unified._ContentRenderer", {
        constructor: function(c, C, o, a) {
            sap.ui.base.Object.apply(this);
            this._id = C;
            this._cntnt = o;
            this._ctrl = c;
            this._rm = sap.ui.getCore().createRenderManager();
            this._cb = a || function() {}
        },
        destroy: function() {
            this._rm.destroy();
            delete this._rm;
            delete this._id;
            delete this._cntnt;
            delete this._cb;
            delete this._ctrl;
            if (this._rerenderTimer) {
                jQuery.sap.clearDelayedCall(this._rerenderTimer);
                delete this._rerenderTimer
            }
            sap.ui.base.Object.prototype.destroy.apply(this, arguments)
        },
        render: function() {
            if (!this._rm) {
                return
            }
            if (this._rerenderTimer) {
                jQuery.sap.clearDelayedCall(this._rerenderTimer)
            }
            this._rerenderTimer = jQuery.sap.delayedCall(0, this, function() {
                var $ = jQuery.sap.byId(this._id);
                var d = $.length > 0;
                if (d) {
                    if (typeof(this._cntnt) === "string") {
                        var c = this._ctrl.getAggregation(this._cntnt, []);
                        for (var i = 0; i < c.length; i++) {
                            this._rm.renderControl(c[i])
                        }
                    } else {
                        this._cntnt(this._rm)
                    }
                    this._rm.flush($[0])
                }
                this._cb(d)
            })
        }
    });
    sap.ui.unified._iNumberOfOpenedShellOverlays = 0
};
if (!jQuery.sap.isDeclared('sap.ui.unified.ContentSwitcher')) {
    jQuery.sap.declare("sap.ui.unified.ContentSwitcher");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.unified.ContentSwitcher", {
        metadata: {
            deprecated: true,
            library: "sap.ui.unified",
            properties: {
                "animation": {
                    type: "string",
                    group: "Appearance",
                    defaultValue: 'None'
                },
                "activeContent": {
                    type: "int",
                    group: "Behavior",
                    defaultValue: 1
                }
            },
            aggregations: {
                "content1": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content1"
                },
                "content2": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content2"
                }
            }
        }
    });
    (function(w, u) {
        sap.ui.unified.ContentSwitcher.prototype.init = function() {};
        sap.ui.unified.ContentSwitcher.prototype.switchContent = function() {
            this.setActiveContent(this.getActiveContent() == 1 ? 2 : 1);
            return this
        };
        sap.ui.unified.ContentSwitcher.prototype.onAfterRendering = function() {
            var i = this.getId();
            this._$Contents = [jQuery.sap.byId(i + "-content1"), jQuery.sap.byId(i + "-content2")]
        };
        sap.ui.unified.ContentSwitcher.prototype._showActiveContent = function(n) {
            this._$Contents[0].toggleClass("sapUiUfdCSwitcherVisible", n === 1);
            this._$Contents[1].toggleClass("sapUiUfdCSwitcherVisible", n === 2)
        };
        sap.ui.unified.ContentSwitcher.prototype.setActiveContent = function(n) {
            n = parseInt(n);
            if (isNaN(n) || n < 1) {
                n = 1;
                jQuery.sap.log.warning("setActiveContent argument must be either 1 or 2. Active content set to 1.")
            } else if (n > 2) {
                n = 2;
                jQuery.sap.log.warning("setActiveContent argument must be either 1 or 2. Active content set to 2.")
            }
            this.setProperty("activeContent", n, true);
            this._showActiveContent(n);
            return this
        };
        sap.ui.unified.ContentSwitcher.prototype.setAnimation = function(a, s) {
            if (typeof(a) !== "string") {
                a = sap.ui.unified.ContentSwitcherAnimation.None;
                jQuery.sap.log.warning("setAnimation argument must be a string. Animation was set to \"" + sap.ui.unified.ContentSwitcherAnimation.None + "\".")
            }
            a = a.replace(/[^a-zA-Z0-9]/g, "");
            var c = this.getProperty("animation");
            if (a === c) {
                return
            }
            var d = this.$();
            if (d[0]) {
                d.toggleClass("sapUiUfdCSwitcherAnimation" + c, false);
                d.toggleClass("sapUiUfdCSwitcherAnimation" + a, true)
            }
            this.setProperty("animation", a, s);
            return this
        }
    })(window)
};
if (!jQuery.sap.isDeclared('sap.ui.unified.ShellHeadItem')) {
    jQuery.sap.declare("sap.ui.unified.ShellHeadItem");
    jQuery.sap.require('sap.ui.core.Element');
    sap.ui.core.Element.extend("sap.ui.unified.ShellHeadItem", {
        metadata: {
            library: "sap.ui.unified",
            properties: {
                "startsSection": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: false
                },
                "selected": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: false
                },
                "showMarker": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: false
                },
                "icon": {
                    type: "sap.ui.core.URI",
                    group: "Appearance",
                    defaultValue: null
                }
            },
            events: {
                "press": {}
            }
        }
    });
    sap.ui.unified.ShellHeadItem.M_EVENTS = {
        'press': 'press'
    };
    jQuery.sap.require('sap.ui.core.IconPool');
    sap.ui.unified.ShellHeadItem.prototype.onclick = function(e) {
        this.firePress()
    };
    sap.ui.unified.ShellHeadItem.prototype.setStartsSection = function(s) {
        s = !! s;
        this.setProperty("startsSection", s, true);
        this.$().toggleClass("sapUiUfdShellHeadItmDelim", s);
        return this
    };
    sap.ui.unified.ShellHeadItem.prototype.setSelected = function(s) {
        s = !! s;
        this.setProperty("selected", s, true);
        this.$().toggleClass("sapUiUfdShellHeadItmSel", s);
        return this
    };
    sap.ui.unified.ShellHeadItem.prototype.setShowMarker = function(m) {
        m = !! m;
        this.setProperty("showMarker", m, true);
        this.$().toggleClass("sapUiUfdShellHeadItmMark", m);
        return this
    };
    sap.ui.unified.ShellHeadItem.prototype.setIcon = function(i) {
        this.setProperty("icon", i, true);
        if (this.getDomRef()) {
            this._refreshIcon()
        }
        return this
    };
    sap.ui.unified.ShellHeadItem.prototype._refreshIcon = function() {
        var i = jQuery(this.$().children()[0]);
        var I = this.getIcon();
        i.html("").css("style", "");
        if (sap.ui.core.IconPool.isIconURI(I)) {
            var o = sap.ui.core.IconPool.getIconInfo(I);
            if (o) {
                i.text(o.content).css("font-family", "'" + o.fontFamily + "'")
            }
        } else {
            i.html("<img src='" + I + "'></img>")
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.unified.SplitContainer')) {
    jQuery.sap.declare("sap.ui.unified.SplitContainer");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.unified.SplitContainer", {
        metadata: {
            library: "sap.ui.unified",
            properties: {
                "showSecondaryContent": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: null
                },
                "secondaryContentWidth": {
                    type: "sap.ui.core.CSSSize",
                    group: "Appearance",
                    defaultValue: '250px'
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                },
                "secondaryContent": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "secondaryContent"
                }
            }
        }
    });
    (function(w, u) {
        jQuery.sap.require('sap.ui.core.theming.Parameters');
        sap.ui.unified.SplitContainer.prototype.init = function() {
            this.bRtl = sap.ui.getCore().getConfiguration().getRTL();
            this._paneRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-panecntnt", "secondaryContent");
            this._canvasRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-canvascntnt", "content");
            this._moveContent = true
        };
        sap.ui.unified.SplitContainer.prototype.exit = function() {
            this._paneRenderer.destroy();
            delete this._paneRenderer;
            this._canvasRenderer.destroy();
            delete this._canvasRenderer;
            delete this._contentContainer;
            delete this._secondaryContentContainer
        };
        sap.ui.unified.SplitContainer.prototype.onAfterRendering = function() {
            this._contentContainer = jQuery.sap.byId(this.getId() + "-canvas");
            this._secondaryContentContainer = jQuery.sap.byId(this.getId() + "-pane");
            this._applySecondaryContentSize()
        };
        sap.ui.unified.SplitContainer.prototype._applySecondaryContentSize = function() {
            if (this.getDomRef()) {
                var d = this.bRtl ? "right" : "left";
                var s = this.getSecondaryContentWidth();
                var S = this.getShowSecondaryContent();
                if (this._closeContentDelayId) {
                    jQuery.sap.clearDelayedCall(this._closeContentDelayId)
                }
                this._secondaryContentContainer.css("width", s);
                this._secondaryContentContainer.css(d, S ? "0" : "-" + s);
                if (this._moveContent) {
                    this._contentContainer.css(d, S ? s : "0")
                } else {
                    this._contentContainer.css(d, "0")
                }
                if (!S) {
                    var h = parseInt(sap.ui.core.theming.Parameters.get("sapUiUfdSplitContAnimationDuration"), 10);
                    this._closeContentDelayId = jQuery.sap.delayedCall(h, this, function() {
                        this._secondaryContentContainer.toggleClass("sapUiUfdSplitContSecondClosed", true)
                    })
                } else {
                    this._secondaryContentContainer.toggleClass("sapUiUfdSplitContSecondClosed", false)
                }
            }
        };
        sap.ui.unified.SplitContainer.prototype._mod = function(m, d) {
            var r = !! this.getDomRef();
            var a = m.apply(this, [r]);
            if (r && d) {
                d.render()
            }
            return a
        };
        sap.ui.unified.SplitContainer.prototype.setShowSecondaryContent = function(s) {
            var r = this.getDomRef();
            this.setProperty("showSecondaryContent", !! s, r);
            this._applySecondaryContentSize();
            return this
        };
        sap.ui.unified.SplitContainer.prototype.setSecondaryContentWidth = function(s) {
            this.setProperty("secondaryContentWidth", s, true);
            this._applySecondaryContentSize();
            return this
        };
        sap.ui.unified.SplitContainer.prototype.insertContent = function(c, i) {
            return this._mod(function(r) {
                return this.insertAggregation("content", c, i, r)
            }, this._canvasRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.addContent = function(c) {
            return this._mod(function(r) {
                return this.addAggregation("content", c, r)
            }, this._canvasRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.removeContent = function(i) {
            return this._mod(function(r) {
                return this.removeAggregation("content", i, r)
            }, this._canvasRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.removeAllContent = function() {
            return this._mod(function(r) {
                return this.removeAllAggregation("content", r)
            }, this._canvasRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.destroyContent = function() {
            return this._mod(function(r) {
                return this.destroyAggregation("content", r)
            }, this._canvasRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.insertSecondaryContent = function(c, i) {
            return this._mod(function(r) {
                return this.insertAggregation("secondaryContent", c, i, r)
            }, this._paneRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.addSecondaryContent = function(c) {
            return this._mod(function(r) {
                return this.addAggregation("secondaryContent", c, r)
            }, this._paneRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.removeSecondaryContent = function(i) {
            return this._mod(function(r) {
                return this.removeAggregation("secondaryContent", i, r)
            }, this._paneRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.removeAllSecondaryContent = function() {
            return this._mod(function(r) {
                return this.removeAllAggregation("secondaryContent", r)
            }, this._paneRenderer)
        };
        sap.ui.unified.SplitContainer.prototype.destroySecondaryContent = function() {
            return this._mod(function(r) {
                return this.destroyAggregation("secondaryContent", r)
            }, this._paneRenderer)
        }
    })(window)
};
if (!jQuery.sap.isDeclared('sap.ui.unified.Shell')) {
    jQuery.sap.declare("sap.ui.unified.Shell");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.unified.Shell", {
        metadata: {
            library: "sap.ui.unified",
            properties: {
                "icon": {
                    type: "sap.ui.core.URI",
                    group: "Appearance",
                    defaultValue: null
                },
                "showPane": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: null
                },
                "showCurtain": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: null,
                    deprecated: true
                },
                "showCurtainPane": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: null,
                    deprecated: true
                },
                "headerHiding": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: null
                }
            },
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                },
                "paneContent": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "paneContent"
                },
                "curtainContent": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "curtainContent"
                },
                "curtainPaneContent": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "curtainPaneContent"
                },
                "headItems": {
                    type: "sap.ui.unified.ShellHeadItem",
                    multiple: true,
                    singularName: "headItem"
                },
                "headEndItems": {
                    type: "sap.ui.unified.ShellHeadItem",
                    multiple: true,
                    singularName: "headEndItem"
                },
                "search": {
                    type: "sap.ui.core.Control",
                    multiple: false
                },
                "canvasSplitContainer": {
                    type: "sap.ui.unified.SplitContainer",
                    multiple: false,
                    visibility: "hidden"
                },
                "curtainSplitContainer": {
                    type: "sap.ui.unified.SplitContainer",
                    multiple: false,
                    visibility: "hidden"
                }
            }
        }
    });
    jQuery.sap.require('sap.ui.Device');
    jQuery.sap.require('jquery.sap.script');
    jQuery.sap.require('jquery.sap.dom');
    jQuery.sap.require('sap.ui.core.Popup');
    jQuery.sap.require('sap.ui.core.theming.Parameters');
    sap.ui.unified.Shell._SIDEPANE_WIDTH_PHONE = 208;
    sap.ui.unified.Shell._SIDEPANE_WIDTH_TABLET = 208;
    sap.ui.unified.Shell._SIDEPANE_WIDTH_DESKTOP = 240;
    sap.ui.unified.Shell._HEADER_ALWAYS_VISIBLE = true;
    sap.ui.unified.Shell._HEADER_AUTO_CLOSE = true;
    sap.ui.unified.Shell._HEADER_TOUCH_TRESHOLD = 30;
    sap.ui.unified.Shell.prototype.init = function() {
        var t = this;
        this._rtl = sap.ui.getCore().getConfiguration().getRTL();
        this._animation = sap.ui.getCore().getConfiguration().getAnimation();
        this._showHeader = true;
        this._iHeaderHidingDelay = 3000;
        this._cont = new sap.ui.unified.SplitContainer(this.getId() + "-container");
        this._cont._bRootContent = true;
        this.setAggregation("canvasSplitContainer", this._cont, true);
        this._curtCont = new sap.ui.unified.SplitContainer(this.getId() + "-curt-container");
        this._curtCont._bRootContent = true;
        this.setAggregation("curtainSplitContainer", this._curtCont, true);

        function _(r) {
            if (!r) {
                r = sap.ui.Device.media.getCurrentRange(sap.ui.Device.media.RANGESETS.SAP_STANDARD).name
            }
            var w = sap.ui.unified.Shell["_SIDEPANE_WIDTH_" + r.toUpperCase()] + "px";
            t._cont.setSecondaryContentWidth(w);
            t._curtCont.setSecondaryContentWidth(w)
        };
        _();
        this._handleMediaChange = function(p) {
            if (!t.getDomRef()) {
                return
            }
            _(p.name);
            t._refreshHeader()
        };
        sap.ui.Device.media.attachHandler(this._handleMediaChange, this, sap.ui.Device.media.RANGESETS.SAP_STANDARD);

        function a() {
            t._refreshHeader()
        };
        this._headCenterRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-hdr-center", function(r) {
            sap.ui.unified.ShellRenderer.renderSearch(r, t)
        }, a);
        this._headBeginRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-hdr-begin", function(r) {
            sap.ui.unified.ShellRenderer.renderHeaderItems(r, t, true)
        }, a);
        this._headEndRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-hdr-end", function(r) {
            sap.ui.unified.ShellRenderer.renderHeaderItems(r, t, false)
        }, a)
    };
    sap.ui.unified.Shell.prototype.exit = function() {
        sap.ui.Device.media.detachHandler(this._handleMediaChange, this, sap.ui.Device.media.RANGESETS.SAP_STANDARD);
        delete this._handleMediaChange;
        this._headCenterRenderer.destroy();
        delete this._headCenterRenderer;
        this._headBeginRenderer.destroy();
        delete this._headBeginRenderer;
        this._headEndRenderer.destroy();
        delete this._headEndRenderer;
        delete this._cont;
        delete this._curtCont
    };
    sap.ui.unified.Shell.prototype.onAfterRendering = function() {
        var t = this;
        jQuery.sap.byId(this.getId() + "-icon").bind("load", function() {
            t._refreshHeader()
        });
        if (window.addEventListener && !sap.ui.unified.Shell._HEADER_ALWAYS_VISIBLE) {
            function h(b) {
                var e = jQuery.event.fix(b);
                if (jQuery.sap.containsOrEquals(jQuery.sap.domById(t.getId() + "-hdr"), e.target)) {
                    t._timedHideHeader(e.type === "focus")
                }
            };
            var H = jQuery.sap.domById(this.getId() + "-hdr");
            H.addEventListener("focus", h, true);
            H.addEventListener("blur", h, true)
        }
        this.onThemeChanged();
        jQuery.sap.byId(this.getId() + "-hdr-center").toggleClass("sapUiUfdShellAnim", !this._noHeadCenterAnim)
    };
    sap.ui.unified.Shell.prototype.onThemeChanged = function() {
        var d = this.getDomRef();
        if (!d) {
            return
        }
        this._repaint(d);
        this._refreshHeader();
        this._timedHideHeader()
    };
    sap.ui.unified.Shell.prototype.onfocusin = function(e) {
        var i = this.getId();
        if (e.target.id === i + "-curt-focusDummyOut") {
            jQuery.sap.focus(jQuery.sap.byId(i + "-hdrcntnt").firstFocusableDomRef())
        } else if (e.target.id === i + "-main-focusDummyOut") {
            jQuery.sap.focus(jQuery.sap.byId(i + "-curtcntnt").firstFocusableDomRef())
        }
    };
    (function() {
        if (sap.ui.Device.support.touch) {
            sap.ui.unified.Shell._HEADER_ALWAYS_VISIBLE = false;

            function _(s) {
                if (s._startY === undefined || s._currY === undefined) {
                    return
                }
                var y = s._currY - s._startY;
                if (Math.abs(y) > sap.ui.unified.Shell._HEADER_TOUCH_TRESHOLD) {
                    s._doShowHeader(y > 0);
                    s._startY = s._currY
                }
            };
            sap.ui.unified.Shell.prototype.ontouchstart = function(e) {
                this._startY = e.touches[0].pageY;
                if (this._startY > 2 * 48) {
                    this._startY = undefined
                }
                this._currY = this._startY
            };
            sap.ui.unified.Shell.prototype.ontouchend = function(e) {
                _(this);
                this._startY = undefined;
                this._currY = undefined
            };
            sap.ui.unified.Shell.prototype.ontouchmove = function(e) {
                this._currY = e.touches[0].pageY;
                _(this)
            }
        }
    })();
    sap.ui.unified.Shell.prototype.setHeaderHiding = function(e) {
        e = !! e;
        return this._mod(function(r) {
            return this.setProperty("headerHiding", e, r)
        }, function() {
            this._doShowHeader(!e ? true : this._showHeader)
        })
    };
    sap.ui.unified.Shell.prototype.setHeaderHidingDelay = function(d) {
        this._iHeaderHidingDelay = d;
        return this
    };
    sap.ui.unified.Shell.prototype.getHeaderHidingDelay = function() {
        return this._iHeaderHidingDelay
    };
    sap.ui.unified.Shell.prototype.getShowPane = function() {
        return this._cont.getShowSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.setShowPane = function(s) {
        this._cont.setShowSecondaryContent(s);
        this.setProperty("showPane", !! s, true);
        return this
    };
    sap.ui.unified.Shell.prototype.getShowPane = function() {
        return this._cont.getShowSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.setShowCurtainPane = function(s) {
        this._curtCont.setShowSecondaryContent(s);
        this.setProperty("showCurtainPane", !! s, true);
        return this
    };
    sap.ui.unified.Shell.prototype.getShowCurtainPane = function() {
        return this._curtCont.getShowSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.setShowCurtain = function(s) {
        s = !! s;
        return this._mod(function(r) {
            return this.setProperty("showCurtain", s, r)
        }, function() {
            var i = this.getId();
            jQuery.sap.byId(i + "-main-focusDummyOut").attr("tabindex", s ? 0 : -1);
            this.$().toggleClass("sapUiUfdShellCurtainHidden", !s).toggleClass("sapUiUfdShellCurtainVisible", s);
            if (s) {
                var z = sap.ui.core.Popup.getNextZIndex();
                jQuery.sap.byId(i + "-curt").css("z-index", z + 1);
                jQuery.sap.byId(i + "-hdr").css("z-index", z + 3);
                jQuery.sap.byId(i + "-brand").css("z-index", z + 7);
                this.$().toggleClass("sapUiUfdShellCurtainClosed", false)
            }
            this._timedCurtainClosed(s);
            this._doShowHeader(true)
        })
    };
    sap.ui.unified.Shell.prototype.setIcon = function(i) {
        return this._mod(function(r) {
            return this.setProperty("icon", i, r)
        }, this._headEndRenderer)
    };
    sap.ui.unified.Shell.prototype.setSearch = function(s) {
        return this._mod(function(r) {
            return this.setAggregation("search", s, r)
        }, this._headCenterRenderer)
    };
    sap.ui.unified.Shell.prototype.getContent = function() {
        return this._cont.getContent()
    };
    sap.ui.unified.Shell.prototype.insertContent = function(c, i) {
        this._cont.insertContent(c, i);
        return this
    };
    sap.ui.unified.Shell.prototype.addContent = function(c) {
        this._cont.addContent(c);
        return this
    };
    sap.ui.unified.Shell.prototype.removeContent = function(i) {
        return this._cont.removeContent(i)
    };
    sap.ui.unified.Shell.prototype.removeAllContent = function() {
        return this._cont.removeAllContent()
    };
    sap.ui.unified.Shell.prototype.destroyContent = function() {
        this._cont.destroyContent();
        return this
    };
    sap.ui.unified.Shell.prototype.getPaneContent = function() {
        return this._cont.getSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.insertPaneContent = function(c, i) {
        this._cont.insertSecondaryContent(c, i);
        return this
    };
    sap.ui.unified.Shell.prototype.addPaneContent = function(c) {
        this._cont.addSecondaryContent(c);
        return this
    };
    sap.ui.unified.Shell.prototype.removePaneContent = function(i) {
        return this._cont.removeSecondaryContent(i)
    };
    sap.ui.unified.Shell.prototype.removeAllPaneContent = function() {
        return this._cont.removeAllSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.destroyPaneContent = function() {
        this._cont.destroySecondaryContent();
        return this
    };
    sap.ui.unified.Shell.prototype.getCurtainContent = function() {
        return this._curtCont.getContent()
    };
    sap.ui.unified.Shell.prototype.insertCurtainContent = function(c, i) {
        this._curtCont.insertContent(c, i);
        return this
    };
    sap.ui.unified.Shell.prototype.addCurtainContent = function(c) {
        this._curtCont.addContent(c);
        return this
    };
    sap.ui.unified.Shell.prototype.removeCurtainContent = function(i) {
        return this._curtCont.removeContent(i)
    };
    sap.ui.unified.Shell.prototype.removeAllCurtainContent = function() {
        return this._curtCont.removeAllContent()
    };
    sap.ui.unified.Shell.prototype.destroyCurtainContent = function() {
        this._curtCont.destroyContent();
        return this
    };
    sap.ui.unified.Shell.prototype.getCurtainPaneContent = function() {
        return this._curtCont.getSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.insertCurtainPaneContent = function(c, i) {
        this._curtCont.insertSecondaryContent(c, i);
        return this
    };
    sap.ui.unified.Shell.prototype.addCurtainPaneContent = function(c) {
        this._curtCont.addSecondaryContent(c);
        return this
    };
    sap.ui.unified.Shell.prototype.removeCurtainPaneContent = function(i) {
        return this._curtCont.removeSecondaryContent(i)
    };
    sap.ui.unified.Shell.prototype.removeAllCurtainPaneContent = function() {
        return this._curtCont.removeAllSecondaryContent()
    };
    sap.ui.unified.Shell.prototype.destroyCurtainPaneContent = function() {
        this._curtCont.destroySecondaryContent();
        return this
    };
    sap.ui.unified.Shell.prototype.insertHeadItem = function(h, i) {
        return this._mod(function(r) {
            return this.insertAggregation("headItems", h, i, r)
        }, this._headBeginRenderer)
    };
    sap.ui.unified.Shell.prototype.addHeadItem = function(h) {
        return this._mod(function(r) {
            return this.addAggregation("headItems", h, r)
        }, this._headBeginRenderer)
    };
    sap.ui.unified.Shell.prototype.removeHeadItem = function(i) {
        return this._mod(function(r) {
            return this.removeAggregation("headItems", i, r)
        }, this._headBeginRenderer)
    };
    sap.ui.unified.Shell.prototype.removeAllHeadItems = function() {
        return this._mod(function(r) {
            return this.removeAllAggregation("headItems", r)
        }, this._headBeginRenderer)
    };
    sap.ui.unified.Shell.prototype.destroyHeadItems = function() {
        return this._mod(function(r) {
            return this.destroyAggregation("headItems", r)
        }, this._headBeginRenderer)
    };
    sap.ui.unified.Shell.prototype.insertHeadEndItem = function(h, i) {
        return this._mod(function(r) {
            return this.insertAggregation("headEndItems", h, i, r)
        }, this._headEndRenderer)
    };
    sap.ui.unified.Shell.prototype.addHeadEndItem = function(h) {
        return this._mod(function(r) {
            return this.addAggregation("headEndItems", h, r)
        }, this._headEndRenderer)
    };
    sap.ui.unified.Shell.prototype.removeHeadEndItem = function(i) {
        return this._mod(function(r) {
            return this.removeAggregation("headEndItems", i, r)
        }, this._headEndRenderer)
    };
    sap.ui.unified.Shell.prototype.removeAllHeadEndItems = function() {
        return this._mod(function(r) {
            return this.removeAllAggregation("headEndItems", r)
        }, this._headEndRenderer)
    };
    sap.ui.unified.Shell.prototype.destroyHeadEndItems = function() {
        return this._mod(function(r) {
            return this.destroyAggregation("headEndItems", r)
        }, this._headEndRenderer)
    };
    sap.ui.unified.Shell.prototype._doShowHeader = function(s) {
        this._showHeader = this._isHeaderHidingActive() ? !! s : true;
        this.$().toggleClass("sapUiUfdShellHeadHidden", !this._showHeader).toggleClass("sapUiUfdShellHeadVisible", this._showHeader);
        if (this._showHeader) {
            this._timedHideHeader()
        }
    };
    sap.ui.unified.Shell.prototype._timedHideHeader = function(c) {
        if (this._headerHidingTimer) {
            jQuery.sap.clearDelayedCall(this._headerHidingTimer);
            this._headerHidingTimer = null
        }
        if (c || !sap.ui.unified.Shell._HEADER_AUTO_CLOSE || !this._isHeaderHidingActive() || this._iHeaderHidingDelay <= 0) {
            return
        }
        this._headerHidingTimer = jQuery.sap.delayedCall(this._iHeaderHidingDelay, this, function() {
            if (this._isHeaderHidingActive() && this._iHeaderHidingDelay > 0 && !jQuery.sap.containsOrEquals(jQuery.sap.domById(this.getId() + "-hdr"), document.activeElement)) {
                this._doShowHeader(false)
            }
        })
    };
    sap.ui.unified.Shell.prototype._timedCurtainClosed = function(c) {
        if (this._curtainClosedTimer) {
            jQuery.sap.clearDelayedCall(this._curtainClosedTimer);
            this._curtainClosedTimer = null
        }
        if (c) {
            return
        }
        var d = parseInt(sap.ui.core.theming.Parameters.get("sapUiUfdShellAnimDuration"), 10);
        if (!this._animation || (sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version < 10)) {
            d = 0
        }
        this._curtainClosedTimer = jQuery.sap.delayedCall(d, this, function() {
            this._curtainClosedTimer = null;
            jQuery.sap.byId(this.getId() + "-curt").css("z-index", "");
            jQuery.sap.byId(this.getId() + "-hdr").css("z-index", "");
            jQuery.sap.byId(this.getId() + "-brand").css("z-index", "");
            this.$().toggleClass("sapUiUfdShellCurtainClosed", true)
        })
    };
    sap.ui.unified.Shell.prototype._mod = function(m, d) {
        var r = !! this.getDomRef();
        var a = m.apply(this, [r]);
        if (r && d) {
            if (d instanceof sap.ui.unified._ContentRenderer) {
                d.render()
            } else {
                d.apply(this)
            }
        }
        return a
    };
    sap.ui.unified.Shell.prototype._refreshHeader = function() {
        function u(I) {
            for (var i = 0; i < I.length; i++) {
                I[i]._refreshIcon()
            }
        }
        u(this.getHeadItems());
        u(this.getHeadEndItems());
        var a = this.getId(),
            b = jQuery("html").hasClass("sapUiMedia-Std-Phone"),
            w = jQuery.sap.byId(this.getId() + "-hdr-end").outerWidth(),
            c = jQuery.sap.byId(a + "-hdr-begin").outerWidth(),
            d = Math.max(w, c),
            e = (b ? c : d) + "px",
            f = (b ? w : d) + "px";
        jQuery.sap.byId(a + "-hdr-center").css({
            "left": this._rtl ? f : e,
            "right": this._rtl ? e : f
        })
    };
    sap.ui.unified.Shell.prototype._getIcon = function() {
        var i = this.getIcon();
        if (!i) {
            jQuery.sap.require("sap.ui.core.theming.Parameters");
            i = sap.ui.core.theming.Parameters.get("sapUiGlobalLogo");
            if (i) {
                var m = /url[\s]*\('?"?([^\'")]*)'?"?\)/.exec(i);
                if (m) {
                    i = m[1]
                } else if (i === "''") {
                    i = null
                }
            }
        }
        return i || sap.ui.resource('sap.ui.core', 'themes/base/img/1x1.gif')
    };
    sap.ui.unified.Shell.prototype._repaint = function(d) {
        if (sap.ui.Device.browser.webkit) {
            var a = d.style.display;
            d.style.display = "none";
            d.offsetHeight;
            d.style.display = a
        }
    };
    sap.ui.unified.Shell.prototype._isHeaderHidingActive = function() {
        if (sap.ui.unified.Shell._HEADER_ALWAYS_VISIBLE || this.getShowCurtain() || !this.getHeaderHiding() || sap.ui.unified._iNumberOfOpenedShellOverlays > 0) {
            return false
        }
        return true
    };
    sap.ui.unified.Shell.prototype.invalidate = function(o) {
        if (o instanceof sap.ui.unified.ShellHeadItem && this._headBeginRenderer && this._headEndRenderer) {
            this._headBeginRenderer.render();
            this._headEndRenderer.render()
        } else {
            sap.ui.core.Control.prototype.invalidate.apply(this, arguments)
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.unified.ShellOverlay')) {
    jQuery.sap.declare("sap.ui.unified.ShellOverlay");
    jQuery.sap.require('sap.ui.core.Control');
    sap.ui.core.Control.extend("sap.ui.unified.ShellOverlay", {
        metadata: {
            library: "sap.ui.unified",
            defaultAggregation: "content",
            aggregations: {
                "content": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "content"
                },
                "search": {
                    type: "sap.ui.core.Control",
                    multiple: false
                }
            },
            associations: {
                "shell": {
                    type: "sap.ui.unified.Shell",
                    multiple: false
                }
            },
            events: {
                "closed": {}
            }
        }
    });
    sap.ui.unified.ShellOverlay.M_EVENTS = {
        'closed': 'closed'
    };
    jQuery.sap.require('sap.ui.core.Popup');
    jQuery.sap.require('sap.ui.Device');
    jQuery.sap.require('jquery.sap.script');
    sap.ui.unified.ShellOverlay.prototype.open = function() {
        if (this._getPopup().isOpen()) {
            return
        }
        this._opening = true;
        this._forceShellHeaderVisible();
        this._getPopup().setModal(true, sap.ui.core.Popup.blStack.length == 0 && this._getAnimActive() ? "sapUiUfdShellOvrlyBly sapUiUfdShellOvrlyBlyTp" : "");
        this._getPopup().open(0, sap.ui.core.Popup.Dock.BeginTop, sap.ui.core.Popup.Dock.BeginTop, window, "0 0", "none");
        var s = this.getSearch();
        if (s) {
            s.focus()
        }
        this._opening = false;
        if (this._getAnimActive()) {
            jQuery.sap.delayedCall(50, this, function() {
                jQuery.sap.byId("sap-ui-blocklayer-popup").toggleClass("sapUiUfdShellOvrlyBlyTp", false)
            })
        }
        jQuery.sap.delayedCall(this._getAnimDuration(true), this, function() {
            this.$().toggleClass("sapUiUfdShellOvrlyOpening", false)
        })
    };
    sap.ui.unified.ShellOverlay.prototype.close = function() {
        if (!this._getPopup().isOpen()) {
            return
        }
        this.$().toggleClass("sapUiUfdShellOvrlyCntntHidden", true).toggleClass("sapUiUfdShellOvrlyClosing", true);
        this._setSearchWidth();
        jQuery.sap.delayedCall(Math.max(this._getAnimDuration(false) - this._getBLAnimDuration(), 0), this, function() {
            var b = jQuery.sap.byId("sap-ui-blocklayer-popup");
            if (sap.ui.core.Popup.blStack.length == 1 && this._getAnimActive() && b.hasClass("sapUiUfdShellOvrlyBly")) {
                b.toggleClass("sapUiUfdShellOvrlyBlyTp", true)
            }
        });
        jQuery.sap.delayedCall(this._getAnimDuration(false), this, function() {
            this._getPopup().close(0);
            this.$().remove();
            this._forceShellHeaderVisible();
            this.fireClosed()
        })
    };
    sap.ui.unified.ShellOverlay.prototype.setShell = function(s) {
        return this.setAssociation("shell", s, true)
    };
    sap.ui.unified.ShellOverlay.prototype.setSearch = function(s) {
        this.setAggregation("search", s, true);
        if ( !! this.getDomRef()) {
            this._headRenderer.render()
        }
        return this
    };
    sap.ui.unified.ShellOverlay.prototype.insertContent = function(c, i) {
        var r = this.insertAggregation("content", c, i, true);
        if ( !! this.getDomRef()) {
            this._contentRenderer.render()
        }
        return r
    };
    sap.ui.unified.ShellOverlay.prototype.addContent = function(c) {
        var r = this.addAggregation("content", c, true);
        if ( !! this.getDomRef()) {
            this._contentRenderer.render()
        }
        return r
    };
    sap.ui.unified.ShellOverlay.prototype.removeContent = function(i) {
        var r = this.removeAggregation("content", i, true);
        if ( !! this.getDomRef()) {
            this._contentRenderer.render()
        }
        return r
    };
    sap.ui.unified.ShellOverlay.prototype.removeAllContent = function() {
        var r = this.removeAllAggregation("content", true);
        if ( !! this.getDomRef()) {
            this._contentRenderer.render()
        }
        return r
    };
    sap.ui.unified.ShellOverlay.prototype.destroyContent = function() {
        var r = this.destroyAggregation("content", true);
        if ( !! this.getDomRef()) {
            this._contentRenderer.render()
        }
        return r
    };
    sap.ui.unified.ShellOverlay.prototype.init = function() {
        this._animOpenDuration = -1;
        this._animCloseDuration = -1;
        this._animBlockLayerDuration = -1;
        this._animation = sap.ui.getCore().getConfiguration().getAnimation();
        this._opening = false;
        var t = this;
        this._headRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-hdr-center", function(r) {
            sap.ui.unified.ShellOverlayRenderer.renderSearch(r, t)
        });
        this._contentRenderer = new sap.ui.unified._ContentRenderer(this, this.getId() + "-cntnt", function(r) {
            sap.ui.unified.ShellOverlayRenderer.renderContent(r, t)
        })
    };
    sap.ui.unified.ShellOverlay.prototype.exit = function() {
        if (this._popup) {
            this._popup.close(0);
            this._popup.destroy();
            this._popup = null
        }
        this._getPopup = function() {
            return null
        };
        this._headRenderer.destroy();
        delete this._headRenderer;
        this._contentRenderer.destroy();
        delete this._contentRenderer
    };
    sap.ui.unified.ShellOverlay.prototype.onAfterRendering = function() {
        if (this._opening) {
            this._setSearchWidth()
        }
        jQuery.sap.delayedCall(10, this, function() {
            this.$().toggleClass("sapUiUfdShellOvrlyCntntHidden", false);
            jQuery.sap.byId(this.getId() + "-search").css("width", "")
        })
    };
    sap.ui.unified.ShellOverlay.prototype.onclick = function(e) {
        if (jQuery(e.target).attr("id") === this.getId() + "-close") {
            this.close()
        }
    };
    sap.ui.unified.ShellOverlay.prototype.onThemeChanged = function() {
        this._animOpenDuration = -1;
        this._animCloseDuration = -1;
        this._animBlockLayerDuration = -1
    };
    sap.ui.unified.ShellOverlay.prototype._getAnimDurationThemeParam = function(p, c) {
        var v = parseInt(sap.ui.core.theming.Parameters.get(p), 10);
        if (!this._getAnimActive() && c) {
            v = 0
        }
        return v
    };
    sap.ui.unified.ShellOverlay.prototype._getAnimDuration = function(o) {
        if ((o && this._animOpenDuration == -1) || (!o && this._animCloseDuration == -1)) {
            var t = o ? "Open" : "Close";
            this["_anim" + t + "Duration"] = this._getAnimDurationThemeParam("sapUiUfdShellOvrly" + t + "AnimOverAll", true)
        }
        return o ? this._animOpenDuration : this._animCloseDuration
    };
    sap.ui.unified.ShellOverlay.prototype._getBLAnimDuration = function() {
        if (this._animBlockLayerDuration == -1) {
            this._animBlockLayerDuration = this._getAnimDurationThemeParam("sapUiUfdShellOvrlyBlockLayerAnimDuration", true)
        }
        return this._animBlockLayerDuration
    };
    sap.ui.unified.ShellOverlay.prototype._getAnimActive = function() {
        if (!this._animation || (sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version < 10)) {
            return false
        }
        return true
    };
    sap.ui.unified.ShellOverlay.prototype._getPopup = function() {
        if (!this._popup) {
            this._popup = new sap.ui.core.Popup(this, true, false, false);
            this._popup._applyPosition = function(p) {
                this._$().css("left", "0").css("top", "0");
                this._oLastPosition = p;
                this._oLastOfRect = jQuery(window).rect()
            };
            this._popup.attachOpened(function() {
                sap.ui.unified._iNumberOfOpenedShellOverlays++
            });
            this._popup.attachClosed(function() {
                sap.ui.unified._iNumberOfOpenedShellOverlays--
            })
        }
        return this._popup
    };
    sap.ui.unified.ShellOverlay.prototype._getShell = function() {
        var i = this.getShell();
        if (!i) {
            return
        }
        var s = sap.ui.getCore().byId(i);
        if (!s || !(s instanceof sap.ui.unified.Shell)) {
            return
        }
        return s
    };
    sap.ui.unified.ShellOverlay.prototype._forceShellHeaderVisible = function() {
        var s = this._getShell();
        if (s) {
            s._doShowHeader(true)
        }
    };
    sap.ui.unified.ShellOverlay.prototype._getSearchWidth = function() {
        var s = this.getShell();
        if (!s) {
            return -1
        }
        var S = jQuery.sap.byId(s + "-hdr-center").children();
        if (S.length) {
            return S.width()
        }
        return -1
    };
    sap.ui.unified.ShellOverlay.prototype._setSearchWidth = function() {
        var w = this._getSearchWidth();
        if (w <= 0) {
            return
        }
        var W = w + "px";
        if (sap.ui.Device.browser.safari) {
            var t = jQuery.sap.byId(this.getId() + "-hdr-center").width();
            if (t > w) {
                W = Math.round((w * 100) / t) + "%"
            } else {
                W = "100%"
            }
        }
        jQuery.sap.byId(this.getId() + "-search").css("width", W)
    }
};