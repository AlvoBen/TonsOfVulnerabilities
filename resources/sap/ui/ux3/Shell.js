﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.ux3.Shell");
jQuery.sap.require("sap.ui.ux3.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.ux3.Shell", {
    metadata: {
        publicMethods: ["setContent", "setPaneContent", "openPersonalizationDialog", "initializePersonalization", "getSearchField", "openPane", "closePane", "isPaneOpen"],
        library: "sap.ui.ux3",
        properties: {
            "appTitle": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "appIcon": {
                type: "sap.ui.core.URI",
                group: "Misc",
                defaultValue: null
            },
            "appIconTooltip": {
                type: "string",
                group: "Misc",
                defaultValue: null
            },
            "showLogoutButton": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "logoutButtonTooltip": {
                type: "string",
                group: "Accessibility",
                defaultValue: null
            },
            "showSearchTool": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showInspectorTool": {
                type: "boolean",
                group: "Misc",
                defaultValue: false,
                deprecated: true
            },
            "showFeederTool": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showTools": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showPane": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "headerType": {
                type: "sap.ui.ux3.ShellHeaderType",
                group: "Misc",
                defaultValue: sap.ui.ux3.ShellHeaderType.Standard
            },
            "designType": {
                type: "sap.ui.ux3.ShellDesignType",
                group: "Misc",
                defaultValue: sap.ui.ux3.ShellDesignType.Standard
            },
            "paneWidth": {
                type: "int",
                group: "Misc",
                defaultValue: 250
            },
            "applyContentPadding": {
                type: "boolean",
                group: "Appearance",
                defaultValue: true
            },
            "fullHeightContent": {
                type: "boolean",
                group: "Appearance",
                defaultValue: false
            },
            "allowOverlayHeaderAccess": {
                type: "boolean",
                group: "Appearance",
                defaultValue: false
            }
        },
        defaultAggregation: "content",
        aggregations: {
            "worksetItems": {
                type: "sap.ui.ux3.NavigationItem",
                multiple: true,
                singularName: "worksetItem"
            },
            "paneBarItems": {
                type: "sap.ui.core.Item",
                multiple: true,
                singularName: "paneBarItem"
            },
            "paneContent": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "paneContent"
            },
            "content": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "content"
            },
            "toolPopups": {
                type: "sap.ui.ux3.ToolPopup",
                multiple: true,
                singularName: "toolPopup"
            },
            "headerItems": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "headerItem"
            },
            "notificationBar": {
                type: "sap.ui.ux3.NotificationBar",
                multiple: false
            },
            "_paneOverflowMenu": {
                type: "sap.ui.commons.Menu",
                multiple: false,
                visibility: "hidden"
            }
        },
        associations: {
            "selectedWorksetItem": {
                type: "sap.ui.ux3.NavigationItem",
                multiple: false
            }
        },
        events: {
            "worksetItemSelected": {
                allowPreventDefault: true
            },
            "paneBarItemSelected": {},
            "logout": {},
            "search": {},
            "feedSubmit": {},
            "paneClosed": {}
        }
    }
});
sap.ui.ux3.Shell.M_EVENTS = {
    'worksetItemSelected': 'worksetItemSelected',
    'paneBarItemSelected': 'paneBarItemSelected',
    'logout': 'logout',
    'search': 'search',
    'feedSubmit': 'feedSubmit',
    'paneClosed': 'paneClosed'
};
jQuery.sap.require("sap.ui.core.theming.Parameters");
jQuery.sap.require("sap.ui.commons.Menu");
sap.ui.ux3.Shell.WSI_MENU_DELAY = 200;
sap.ui.ux3.Shell.WSI_OVERFLOW_SCROLL_STEP = 250;
sap.ui.ux3.Shell.TOOL_PREFIX = "-tool-";
sap.ui.ux3.Shell.FIRST_RENDERING = true;
sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH = null;
sap.ui.ux3.Shell.PANE_OVERFLOW_BUTTON_HEIGHT = null;

sap.ui.ux3.Shell.prototype.init = function() {
    this._iOpenPaneIndex = -1;
    this._sOpenWsiId = null;
    this._bPreviousScrollRight = false;
    this._bPreviousScrollLeft = false;
    this._sSelectedWorksetId = null;
    this._sSelectedFacetId = null;
    this._aSyncRefStack = [];
    this._mSyncRefs = {};
    this._oWorksetBar = new sap.ui.ux3.NavigationBar(this.getId() + "-wsBar", {
        toplevelVariant: true,
        select: [this._handleWorksetBarSelect, this]
    }).setParent(this);
    this._oFacetBar = new sap.ui.ux3.NavigationBar(this.getId() + "-facetBar", {
        select: [this._handleFacetBarSelect, this]
    }).setParent(this);
    this.setAggregation("_paneOverflowMenu", new sap.ui.commons.Menu());
    var t = this;
    this._checkResizeClosure = function() {
        t._checkResize()
    };
    this.currentToolPaletteWidth = 0;
    this._updateThemeVariables()
};

sap.ui.ux3.Shell.prototype.exit = function() {
    if (this._oWorksetBar) {
        this._oWorksetBar.destroy();
        delete this._oWorksetBar
    }
    if (this._oFacetBar) {
        this._oFacetBar.destroy();
        delete this._oFacetBar
    }
    this._oSearchField = null;
    jQuery(window).unbind("resize", this._checkResizeClosure)
};

sap.ui.ux3.Shell.prototype.onBeforeRendering = function() {
    var i = this.getId();
    this._beforeRenderingToolPalette();
    jQuery.sap.byId(this.getId() + "-focusDummyPane").unbind("focusin");
    if (window.FileReader) {
        var $ = jQuery.sap.byId(i + "-hdr");
        $.unbind('dragover', this._handleDragover).unbind('dragend', this._handleDragend).unbind('drop', this._handleDrop);
        var a = jQuery.sap.byId(i + "-bgImg");
        a.unbind('dragover', jQuery.proxy(this._handleDragover)).unbind('dragend', this._handleDragend).unbind('drop', this._handleDrop)
    }
};

sap.ui.ux3.Shell.prototype.onAfterRendering = function() {
    var i = this.getId();
    this._afterRenderingToolPalette();
    if (this._topSyncRefId && this._mSyncRefs[this._topSyncRefId].focusLast) {
        jQuery.sap.byId(this.getId() + "-focusDummyPane").focusin(this._mSyncRefs[this._topSyncRefId].focusLast)
    }
    if (window.FileReader) {
        var $ = jQuery.sap.byId(i + "-hdr");
        $.bind('dragover', jQuery.proxy(this._handleDragover, this)).bind('dragend', jQuery.proxy(this._handleDragend, this)).bind('drop', jQuery.proxy(this._handleDrop, this));
        var a = jQuery.sap.byId(i + "-bgImg");
        a.bind('dragover', jQuery.proxy(this._handleDragover, this)).bind('dragend', jQuery.proxy(this._handleDragend, this)).bind('drop', jQuery.proxy(this._handleDrop, this))
    }
    var s = (this._oFacetBar.getAssociatedItems().length > 0);
    var f = jQuery.sap.byId(this.getId() + "-facetBar");
    f.css("display", (s ? "block" : "none"));
    this._adaptContentHeight();
    this._bRtl = sap.ui.getCore().getConfiguration().getRTL();
    if (this._getPersonalization().hasChanges()) {
        this._getPersonalization().applySettings(this._getPersonalization().oSettings)
    }
    if (!this._oPaneItemNavigation) {
        this._oPaneItemNavigation = new sap.ui.core.delegate.ItemNavigation().setCycling(false);
        this.addDelegate(this._oPaneItemNavigation)
    }
    var p = jQuery.sap.byId(this.getId() + "-paneBarEntries");
    this._updatePaneBarItemNavigation(p);
    this._setNotifyVisibility();
    if (sap.ui.ux3.Shell.FIRST_RENDERING) {
        jQuery(window).bind("resize", this._checkResizeClosure)
    }
    this._checkResize();
    sap.ui.ux3.Shell.FIRST_RENDERING = false;
    if ( !! sap.ui.Device.browser.firefox && sap.ui.Device.browser.version == 17) {
        jQuery.sap.delayedCall(500, this, this._checkResize)
    }
};

sap.ui.ux3.Shell.prototype._updateThemeVariables = function() {
    sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH = parseInt(sap.ui.core.theming.Parameters.get("sapUiUx3ShellSideBarBaseWidth"), 10);
    sap.ui.ux3.Shell.PANE_OVERFLOW_BUTTON_HEIGHT = parseInt(sap.ui.core.theming.Parameters.get("sapUiUx3ShellPaneOverflowButtonHeight"), 10)
};

sap.ui.ux3.Shell.prototype._checkResize = function() {
    if (!this.getDomRef()) {
        return
    }
    jQuery.sap.clearDelayedCall(this._checkResizeDelayId);
    this._checkResizeDelayId = jQuery.sap.delayedCall(100, this, function() {
        this._rerenderPaneBarItems();
        this._checkToolPaletteSize()
    })
};

sap.ui.ux3.Shell.prototype._checkToolPaletteSize = function(h) {
    if (!this.getDomRef()) {
        return
    }
    if (h === undefined) {
        h = !this.getShowTools()
    }
    var i = this.getId();
    var t = jQuery.sap.byId(i + "-tp");
    var c = 1;
    var l = 9999999;
    var C = 0;
    var k = true;
    if (h) {
        c = 0;
        k = false
    }
    do {
        var b = c * sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH;
        var s = Math.floor(b / 2);
        var S = Math.floor(b / 4);
        var a = this._bRtl ? "right" : "left";
        jQuery.sap.byId(i + "-canvas").css(a, b + "px");
        jQuery.sap.byId(i + "-canvasBackground").css(a, b + "px");
        jQuery.sap.byId(i + "-wBar").css("margin-" + a, b + "px");
        var p = {};
        p[a] = b + "px";
        jQuery.sap.byId(i + "-notify").css(p);
        t.css("width", b + "px");
        var p = {};
        p["width"] = s + "px";
        p["margin-" + a] = S + "px";
        jQuery.sap.byId(i + "-tp-separator").css(p);
        if (!k) {
            break
        }
        sap.ui.core.RenderManager.forceRepaint(t[0]);
        C = t.children().last()[0].offsetTop;
        if (C >= l) {
            c--;
            k = false;
            continue
        }
        l = C;
        c++;
        if (c > 10) {
            jQuery.sap.log.error("The ToolPalette is growing too much, this must be a bug.");
            break
        }
    } while (C > t.height());
    this.currentToolPaletteWidth = this.getShowTools() ? b : 0;
    this._updateOverlaysOnToolPaletteChange()
};

sap.ui.ux3.Shell.prototype._updateOverlaysOnToolPaletteChange = function() {
    var o = this._getSyncRefs();
    if (this._bRtl) {
        o.css("right", this.currentToolPaletteWidth + "px")
    } else {
        o.css("left", this.currentToolPaletteWidth + "px")
    }
};

sap.ui.ux3.Shell.prototype._updatePaneBarItemNavigation = function(p) {
    this._oPaneItemNavigation.setRootDomRef(p[0]);
    var P = p.children().toArray();
    this._oPaneItemNavigation.setItemDomRefs(P);
    if (this._oPaneItemNavigation.getFocusedIndex() > P.length) {
        this._oPaneItemNavigation.setFocusedIndex(-1)
    }
    this._checkPaneBarOverflow(p)
};

sap.ui.ux3.Shell.prototype._checkPaneBarOverflow = function(p) {
    jQuery.sap.clearDelayedCall(this._checkPaneBarOverflowDelayId);
    this._checkPaneBarOverflowDelayId = jQuery.sap.delayedCall(200, this, function() {
        this._delayedCheckPaneBarOverflow(p)
    })
};

sap.ui.ux3.Shell.prototype._delayedCheckPaneBarOverflow = function(p) {
    var I = this.getId();
    if (!p) {
        p = jQuery.sap.byId(I + "-paneBarEntries")
    }
    if (p.length == 0) {
        this._checkPaneBarOverflow();
        return
    }
    var l = p.parent().height() - (this._bRtl ? p.innerHeight() : 0);
    var a = 0;
    var e = p.children();
    var b = false;
    var c = e.length;
    for (var i = c - 1; i >= 0; --i) {
        var d = e[i];
        if (this._bRtl) {
            a = (0 - d.offsetLeft)
        } else {
            a = d.offsetLeft + d.offsetWidth
        }
        var f = a < l;
        jQuery(d).css("display", f ? "inline-block" : "none");
        sap.ui.getCore().byId(d.id + "-overflow").setVisible(!f);
        if (f) {
            continue
        } else {
            b = true
        }
    }
    var P = jQuery.sap.byId(I + "-paneBarRight");
    var o = jQuery.sap.byId(I + "-paneBarOverflowButton");
    P.css("padding-bottom", sap.ui.ux3.Shell.PANE_OVERFLOW_BUTTON_HEIGHT + "px");
    if (b) {
        o.css("display", "block")
    } else {
        o.css("display", "none");
        if ( !! sap.ui.Device.browser.firefox && sap.ui.Device.browser.version == 17) {
            sap.ui.core.RenderManager.forceRepaint(document.getElementsByTagName("body")[0])
        }
    }
};

sap.ui.ux3.Shell.prototype._getPaneOverflowMenu = function() {
    return this.getAggregation("_paneOverflowMenu")
};

sap.ui.ux3.Shell.prototype.getFocusInfo = function() {
    try {
        var e = document.activeElement;
        return e ? {
            'sFocusId': e.id,
            'oFocusedElement': e
        } : {}
    } catch (a) {
        return {}
    }
};

sap.ui.ux3.Shell.prototype.applyFocusInfo = function(f) {
    var e = jQuery.sap.domById(f.sFocusId) || f.oFocusedElement;
    jQuery.sap.focus(e)
};

sap.ui.ux3.Shell._updateToolIcon = function(d) {
    if (d && d.firstChild) {
        var $ = jQuery(d);
        var t = d.id.substr(d.id.indexOf(sap.ui.ux3.Shell.TOOL_PREFIX) + 6);
        var a = sap.ui.getCore().byId(t);
        var i = $.is(".sapUiUx3ShellToolSelected") ? a.getIconSelected() : ($.is(".sapUiUx3ShellToolHover") ? a.getIconHover() : a.getIcon());
        d.firstChild.src = i
    }
};

sap.ui.ux3.Shell.prototype.onclick = function(e) {
    this.onUserActivation(e)
};

sap.ui.ux3.Shell.prototype.onsapspace = function(e) {
    this.onUserActivation(e)
};

sap.ui.ux3.Shell.prototype.onsapenter = function(e) {
    this.onUserActivation(e)
};

sap.ui.ux3.Shell.prototype.onUserActivation = function(e) {
    var t = e.target.id;
    var p = e.target.parentNode;
    var i = this.getId();
    if (e.target.className && e.target.className.indexOf && e.target.className.indexOf("sapUiUx3ShellHeader-logout") > -1) {
        this.fireLogout()
    } else if (p && p.parentNode && p.parentNode.className && p.parentNode.className.indexOf && p.parentNode.className.indexOf("sapUiUx3ShellToolPaletteArea") > -1) {
        this._handleToolItemClick(t)
    } else if (p && p.parentNode && p.parentNode.parentNode && p.parentNode.parentNode.className && p.parentNode.parentNode.className.indexOf && p.parentNode.parentNode.className.indexOf("sapUiUx3ShellToolPaletteArea") > -1) {
        this._handleToolItemClick(p.id)
    } else if (t === i + "-paneBarOverflowButtonIcon" || t === i + "-paneBarOverflowButton") {
        var T = jQuery.sap.byId(i + "-paneBarOverflowButton")[0];
        this._getPaneOverflowMenu().open(true, T, (this._bRtl ? "left" : "right") + " bottom", "center center", T)
    } else if (t) {
        if (p && p.className && p.className.indexOf && p.className.indexOf("sapUiUx3ShellPaneEntries") > -1) {
            this._handlePaneBarItemClick(t)
        }
    }
};

sap.ui.ux3.Shell.prototype._handleWorksetBarSelect = function(e) {
    var p = this._handleWorksetItemClick(e.getParameter("item"));
    if (!p) {
        e.preventDefault()
    }
};

sap.ui.ux3.Shell.prototype._handleFacetBarSelect = function(e) {
    var p = this._handleWorksetItemClick(e.getParameter("item"));
    if (!p) {
        e.preventDefault()
    }
};

sap.ui.ux3.Shell.prototype._handleWorksetItemClick = function(e) {
    var p = e.getParent(),
        i = e.getId(),
        f = i,
        P = true;
    if (p instanceof sap.ui.ux3.Shell) {
        if (i != this._sSelectedWorksetId) {
            var s = e.getSubItems();
            if (s.length > 0) {
                f = s[0].getId()
            }
            P = this._fireWorksetItemSelected(f);
            if (P) {
                this._sSelectedWorksetId = i;
                if (s.length > 0) {
                    this._sSelectedFacetId = s[0].getId();
                    i = this._sSelectedFacetId
                } else {
                    this._sSelectedFacetId = null
                }
                var s = e.getSubItems();
                this._oFacetBar.setAssociatedItems(s, true);
                var F = jQuery.sap.byId(this.getId() + "-facetBar");
                var a = this._calcFacetBarHeight(F);
                if (s.length > 0) {
                    if (!this._oFacetBar.isSelectedItemValid()) {
                        this._oFacetBar.setSelectedItem(s[0]);
                        this._sSelectedFacetId = s[0].getId()
                    }
                    F.slideDown();
                    this._adaptContentHeight(null, true, a)
                } else {
                    this._oFacetBar.setSelectedItem(null);
                    F.slideUp();
                    this._adaptContentHeight(null, true, a)
                }
            }
        }
    } else {
        if (i != this._sSelectedFacetId) {
            P = this._fireWorksetItemSelected(f);
            if (P) {
                this._sSelectedFacetId = i
            }
        }
    }
    return P
};

sap.ui.ux3.Shell.prototype._fireWorksetItemSelected = function(i) {
    var a = sap.ui.getCore().byId(i);
    var k = (a ? a.getKey() : null);
    var p = this.fireWorksetItemSelected({
        id: i,
        item: a,
        key: k
    });
    if (p) {
        this.setAssociation("selectedWorksetItem", i, true)
    }
    return p
};

sap.ui.ux3.Shell.prototype._closeCurrentToolPopup = function() {
    var o = undefined;
    if (this._oOpenToolPopup) {
        o = this._oOpenToolPopup.getId();
        jQuery.sap.byId(this.getId() + sap.ui.ux3.Shell.TOOL_PREFIX + this._oOpenToolPopup.getId()).removeClass("sapUiUx3ShellToolSelected").attr("aria-pressed", "false");
        if (o === this.getId() + "-feederTool") {
            var f = sap.ui.getCore().byId(this.getId() + "-feeder");
            f.setText("");
            f.rerender()
        }
        this._oOpenToolPopup.close();
        this._oOpenToolPopup = null
    }
    return o
};

sap.ui.ux3.Shell.prototype._handleToolItemClick = function(i) {
    var a = this._closeCurrentToolPopup();
    var t;
    var b = i.substr(i.indexOf(sap.ui.ux3.Shell.TOOL_PREFIX) + 6);
    if (b == (this.getId() + "-searchTool")) {
        t = this._getSearchTool()
    } else if (b == (this.getId() + "-feederTool")) {
        t = this._getFeederTool()
    } else {
        t = sap.ui.getCore().byId(b)
    }
    if (t && (t.getId() != a)) {
        this._oOpenToolPopup = t;
        t.setPosition(sap.ui.core.Popup.Dock.BeginTop, sap.ui.core.Popup.Dock.EndTop, jQuery.sap.domById(i), "13 -6", "fit");
        t.open();
        jQuery.sap.byId(i).addClass("sapUiUx3ShellToolSelected").attr("aria-pressed", "true");
        sap.ui.ux3.Shell._updateToolIcon(jQuery.sap.domById(i));
        var c = this;
        var o = function(e) {
            t.detachClosed(o);
            if (c._oOpenToolPopup && c._oOpenToolPopup.getId() === e.getParameter("id")) {
                c._closeCurrentToolPopup()
            }
            jQuery.sap.byId(i).removeClass("sapUiUx3ShellToolSelected");
            sap.ui.ux3.Shell._updateToolIcon(jQuery.sap.domById(i))
        };
        t.attachClosed(o)
    } else if (t.getId() == a) {
        sap.ui.ux3.Shell._updateToolIcon(jQuery.sap.domById(i))
    }
};

sap.ui.ux3.Shell.prototype._hasDarkDesign = function() {
    return (this.getDesignType() !== sap.ui.ux3.ShellDesignType.Light && this.getDesignType() !== sap.ui.ux3.ShellDesignType.Crystal)
};

sap.ui.ux3.Shell.prototype._getSearchTool = function() {
    if (!this._oSearchPopup) {
        var r = sap.ui.getCore().getLibraryResourceBundle("sap.ui.ux3");
        this._oSearchPopup = new sap.ui.ux3.ToolPopup(this.getId() + "-searchTool", {
            tooltip: r.getText("SHELL_SEARCH")
        }).addStyleClass("sapUiUx3TP-search");
        var i = this._hasDarkDesign();
        this._oSearchPopup.setInverted(i);
        var t = this;
        var s = new sap.ui.commons.SearchField(this.getId() + "-searchField", {
            enableListSuggest: false,
            search: function(e) {
                t.fireSearch({
                    text: e.getParameter("query")
                })
            }
        });
        var l = new sap.ui.commons.Label({
            text: r.getText("SHELL_SEARCH_LABEL") + ":"
        }).setLabelFor(s);
        this._oSearchPopup.addContent(l).addContent(s);
        this._oSearchPopup.attachOpen(function() {
            window.setTimeout(function() {
                s.focus()
            }, 100)
        });
        this._oSearchField = s
    }
    return this._oSearchPopup
};

sap.ui.ux3.Shell.prototype.getSearchField = function() {
    if (!this._oSearchField) {
        this._getSearchTool()
    }
    return this._oSearchField
};

sap.ui.ux3.Shell.prototype._getFeederTool = function() {
    if (!this._oFeederPopup) {
        var t = this;
        var r = sap.ui.getCore().getLibraryResourceBundle("sap.ui.ux3");
        this._oFeederPopup = new sap.ui.ux3.ToolPopup(this.getId() + "-feederTool", {
            tooltip: r.getText("SHELL_FEEDER")
        }).addStyleClass("sapUiUx3TP-feeder");
        var i = this._hasDarkDesign();
        this._oFeederPopup.setInverted(i);
        var f = new sap.ui.ux3.Feeder(this.getId() + "-feeder", {
            type: sap.ui.ux3.FeederType.Medium,
            submit: function(e) {
                t.fireFeedSubmit({
                    text: e.getParameter("text")
                });
                f.setText("");
                f.rerender();
                t._oFeederPopup.close()
            }
        });
        this._oFeederPopup.setInitialFocus(this.getId() + "-feeder");
        this._oFeederPopup.attachClose(function() {
            f.setText("")
        });
        this._oFeederPopup.addContent(f)
    }
    return this._oFeederPopup
};

sap.ui.ux3.Shell.prototype.openPane = function(p) {
    var i = sap.ui.getCore().byId(p);
    if (i && (p != this._sOpenPaneId) && this.getShowPane()) {
        var k = (i ? (i.getKey() == "" ? null : i.getKey()) : null);
        this.firePaneBarItemSelected({
            "id": p,
            "item": i,
            "key": k
        });
        var P = jQuery.sap.byId(p);
        P.siblings().removeClass("sapUiUx3ShellPaneEntrySelected");
        P.addClass("sapUiUx3ShellPaneEntrySelected");
        if (!this._sOpenPaneId) {
            this._openPane()
        } else {
            jQuery.sap.byId(this.getId() + "-pb_" + this._sOpenPaneId).removeClass("sapUiUx3ShellPaneEntrySelected")
        }
        this._sOpenPaneId = p
    }
    return this
};

sap.ui.ux3.Shell.prototype.closePane = function() {
    this._closePane();
    return this
};

sap.ui.ux3.Shell.prototype.isPaneOpen = function() {
    return (this._sOpenPaneId != null)
};

sap.ui.ux3.Shell.prototype._handlePaneBarItemClick = function(p) {
    if (p === this._sOpenPaneId) {
        this.closePane()
    } else {
        this.openPane(p)
    }
};

(function() {
    sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT = 0;
    sap.ui.ux3.Shell.prototype._closePane = function(c) {
        if (!this._sOpenPaneId) {
            if (c) {
                c()
            }
            return
        }
        var o = this._sOpenPaneId;
        var t = this;
        var i = this.getId();
        var d = this.getShowPane() ? (jQuery.sap.byId(i + "-paneBarRight").outerWidth() + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) : sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT;
        var p = {};
        p[this._bRtl ? "left" : "right"] = d + "px";
        var a = {};
        a[this._bRtl ? "marginLeft" : "marginRight"] = (d - sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px";
        jQuery.sap.byId(i + "-content").css("overflow-x", "hidden");
        jQuery.sap.byId(i + "-canvas").stop().animate(p);
        jQuery.sap.byId(i + "-notify").stop().animate(p);
        this._getSyncRefs().stop(false, true).animate(p);
        jQuery.sap.byId(i + "-wBar").stop().animate(a);
        jQuery.sap.byId(i + "-paneBar").removeClass("sapUiUx3ShellPaneBarOpened").addClass("sapUiUx3ShellPaneBarClose");
        jQuery.sap.byId(i + "-canvasBackground").removeClass("sapUiUx3ShellCanvasBackgroundOpen").addClass("sapUiUx3ShellCanvasBackgroundClosed").stop().animate(p, function() {
            jQuery.sap.byId(i + "-paneBar").removeClass("sapUiUx3ShellPaneBarOpen");
            jQuery.sap.byId(i + "-content").css("overflow-x", "");
            if (c) {
                c()
            }
            t.firePaneClosed({
                "id": o
            })
        });
        jQuery.sap.byId(this._sOpenPaneId).removeClass("sapUiUx3ShellPaneEntrySelected");
        this._sOpenPaneId = null
    };
    sap.ui.ux3.Shell.prototype._openPane = function() {
        var i = this.getId();
        var P = this.getPaneWidth();
        var p = jQuery.sap.byId(i + "-paneBarRight").outerWidth();
        jQuery.sap.byId(i + "-content").css("overflow-x", "hidden");
        if (this._bRtl) {
            jQuery.sap.byId(i + "-paneBar").removeClass("sapUiUx3ShellPaneBarClose").addClass("sapUiUx3ShellPaneBarOpen");
            jQuery.sap.byId(i + "-wBar").stop().animate({
                marginLeft: (P + p) + "px"
            });
            jQuery.sap.byId(i + "-canvas").stop().animate({
                left: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            });
            jQuery.sap.byId(i + "-notify").stop().animate({
                left: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            });
            this._getSyncRefs().stop(false, true).animate({
                left: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            });
            jQuery.sap.byId(i + "-canvasBackground").stop().animate({
                left: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            }, function() {
                jQuery.sap.byId(i + "-content").css("overflow-x", "");
                jQuery.sap.byId(i + "-canvasBackground").removeClass("sapUiUx3ShellCanvasBackgroundClosed").addClass("sapUiUx3ShellCanvasBackgroundOpen");
                jQuery.sap.byId(i + "-paneBar").addClass("sapUiUx3ShellPaneBarOpened")
            })
        } else {
            jQuery.sap.byId(i + "-paneBar").removeClass("sapUiUx3ShellPaneBarClose").addClass("sapUiUx3ShellPaneBarOpen");
            jQuery.sap.byId(i + "-wBar").stop().animate({
                marginRight: (P + p) + "px"
            });
            jQuery.sap.byId(i + "-canvas").stop().animate({
                right: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            });
            jQuery.sap.byId(i + "-notify").stop().animate({
                right: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            });
            this._getSyncRefs().stop(false, true).animate({
                right: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            });
            jQuery.sap.byId(i + "-canvasBackground").stop().animate({
                right: (P + p + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px"
            }, function() {
                jQuery.sap.byId(i + "-content").css("overflow-x", "");
                jQuery.sap.byId(i + "-canvasBackground").removeClass("sapUiUx3ShellCanvasBackgroundClosed").addClass("sapUiUx3ShellCanvasBackgroundOpen");
                jQuery.sap.byId(i + "-paneBar").addClass("sapUiUx3ShellPaneBarOpened")
            })
        }
    };
    sap.ui.ux3.Shell.prototype.setPaneWidth = function(w) {
        if (typeof w == "number" && (w > 0)) {
            w = Math.max(w, 50);
            if (this.getDomRef()) {
                var i = this.getId();
                jQuery.sap.byId(i + "-paneContent").css("width", w + "px");
                jQuery.sap.byId(i + "-paneBar").css("width", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH) + "px");
                if ( !! this._sOpenPaneId) {
                    if (this._bRtl) {
                        jQuery.sap.byId(i + "-wBar").css("marginLeft", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH) + "px");
                        jQuery.sap.byId(i + "-canvas").css("left", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px");
                        jQuery.sap.byId(i + "-notify").css("left", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px");
                        this._getSyncRefs().css("left", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px");
                        jQuery.sap.byId(i + "-canvasBackground").css("left", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px")
                    } else {
                        jQuery.sap.byId(i + "-wBar").css("marginRight", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH) + "px");
                        jQuery.sap.byId(i + "-canvas").css("right", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px");
                        jQuery.sap.byId(i + "-notify").css("right", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px");
                        this._getSyncRefs().css("right", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px");
                        jQuery.sap.byId(i + "-canvasBackground").css("right", (w + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH + sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT) + "px")
                    }
                }
            }
            this.setProperty("paneWidth", w, true)
        }
        return this
    };
    sap.ui.ux3.Shell.prototype.setOffsetRight = function(p, c, o) {
        if (p < 0) {
            p = 0
        }
        if (p > 600) {
            p = 600
        }
        sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT = p;
        var a = p + "px";
        var i = this.getId();
        var b = this._bRtl ? {
            "left": a
        } : {
            "right": a
        };
        jQuery.sap.byId(i + "-hdr").stop().animate(b);
        jQuery.sap.byId(i + "-hdrImg").stop().animate(b);
        jQuery.sap.byId(i + "-bg").stop().animate(b, function() {
            if (c) {
                c()
            }
        });
        jQuery.sap.byId(i + "-bgImg").stop().animate(b);
        jQuery.sap.byId(i + "-wBar").stop().animate(b);
        jQuery.sap.byId(i + "-paneBar").stop().animate(b);
        jQuery.sap.byId(o).stop().animate({
            "width": a
        });
        if (!this.$().hasClass("sapUiUx3ShellNoPane")) {
            a = (p + (this._sOpenPaneId ? this.getPaneWidth() : 0) + sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH) + "px"
        }
        b = this._bRtl ? {
            "left": a
        } : {
            "right": a
        };
        jQuery.sap.byId(i + "-notify").stop().animate(b);
        this._getSyncRefs().stop(false, true).animate(b);
        jQuery.sap.byId(i + "-canvas").stop().animate(b);
        jQuery.sap.byId(i + "-canvasBackground").stop().animate(b)
    };
    sap.ui.ux3.Shell.prototype._refreshCanvasOffsetRight = function(p) {
        var i = this.getId();
        var r = this._bRtl ? "left" : "right";
        var a = sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT;
        if (p) {
            var P = jQuery.sap.byId(i + "-paneBarRight").outerWidth();
            a = a + (this._sOpenPaneId ? this.getPaneWidth() : 0) + P
        }
        a = a + "px";
        jQuery.sap.byId(i + "-notify").css(r, a);
        jQuery.sap.byId(i + "-canvas").css(r, a);
        jQuery.sap.byId(i + "-canvasBackground").css(r, a);
        this._getSyncRefs().css(r, a)
    };
    sap.ui.ux3.Shell.prototype._setNotifyVisibility = function(v) {
        var n = this.getNotificationBar();
        if (!n) {
            return
        }
        if (!v) {
            v = n.getVisibleStatus()
        }
        var $ = jQuery.sap.byId(this.getId() + "-notify");
        var a = this._topSyncRefId ? jQuery.sap.byId(this._topSyncRefId) : jQuery(null);
        var i = a.hasClass("sapUiUx3TI");
        var h = 0;
        var b = 0;
        var z = 1;
        if (v === sap.ui.ux3.NotificationBarStatus.Min) {
            h = 10
        } else if (v === sap.ui.ux3.NotificationBarStatus.Max || v === sap.ui.ux3.NotificationBarStatus.Default) {
            if (this.getHeaderType() === sap.ui.ux3.ShellHeaderType.BrandOnly && i) {
                h = parseInt(n.getHeightOfStatus(sap.ui.ux3.NotificationBarStatus.Default))
            } else {
                h = 10
            }
            b = h
        }
        $.removeClass("sapUiUx3ShellNotifyTI").removeClass("sapUiUx3ShellNotifyOverlay");
        if (this._topSyncRefId) {
            this._getSyncRefs().animate({
                "bottom": b + "px"
            }, "fast");
            z = parseInt(a.css("z-index"));
            if (b > 0) {
                $.addClass(i ? "sapUiUx3ShellNotifyTI" : "sapUiUx3ShellNotifyOverlay")
            }
        }
        $.css("height", h + "px").css("z-index", z + 1);
        if (h > 0 && !this.$().hasClass("sapUiUx3ShellNotifyVisible")) {
            this.$().addClass("sapUiUx3ShellNotifyVisible")
        } else if (h == 0 && this.$().hasClass("sapUiUx3ShellNotifyVisible")) {
            this.$().removeClass("sapUiUx3ShellNotifyVisible")
        }
        this._adaptContentHeight(v)
    };
    sap.ui.ux3.Shell.prototype.syncWithCanvasSize = function(i, I, f, F, a) {
        var b = jQuery.inArray(i, this._aSyncRefStack);
        if (I) {
            var r = {
                id: i,
                focusFirst: f,
                focusLast: F,
                applyChanges: a
            };
            if (b < 0) {
                this._aSyncRefStack.push(i)
            }
            this._mSyncRefs[i] = r;
            r.applyChanges({
                showOverlay: this.getHeaderType() !== sap.ui.ux3.ShellHeaderType.BrandOnly
            })
        } else {
            if (b >= 0) {
                delete this._mSyncRefs[i];
                this._aSyncRefStack.splice(b, 1)
            }
        }
        jQuery.sap.byId(this.getId() + "-canvas").removeAttr("aria-hidden");
        jQuery.sap.byId(this.getId() + "-focusDummyTPStart").removeAttr("tabindex").unbind("focusin");
        jQuery.sap.byId(this.getId() + "-focusDummyTPEnd").removeAttr("tabindex").unbind("focusin");
        jQuery.sap.byId(this.getId() + "-focusDummyHdrStart").removeAttr("tabindex").unbind("focusin");
        jQuery.sap.byId(this.getId() + "-focusDummyHdrEnd").removeAttr("tabindex").unbind("focusin");
        jQuery.sap.byId(this.getId() + "-focusDummyPane").removeAttr("tabindex").unbind("focusin");
        this.$().toggleClass("sapUiUx3ShellBlockHeaderAccess", false);
        delete this._topSyncRefId;
        if (this._aSyncRefStack.length > 0) {
            var r = this._mSyncRefs[this._aSyncRefStack[this._aSyncRefStack.length - 1]];
            var d = this._getSyncRefs();
            var c = jQuery.sap.domById(this.getId() + "-canvas");
            d.css(this._bRtl ? "right" : "left", (this.getShowTools() ? this.currentToolPaletteWidth : 0) + "px");
            var R = sap.ui.ux3.Shell._SHELL_OFFSET_RIGHT;
            if (this.getShowPane()) {
                R += sap.ui.ux3.Shell.SIDE_BAR_BASE_WIDTH;
                if (this._sOpenPaneId) {
                    R += this.getPaneWidth()
                }
            }
            d.css(this._bRtl ? "left" : "right", R + "px");
            d.css("top", jQuery.sap.domById(this.getId() + (this.getAllowOverlayHeaderAccess() ? "-hdr" : "-hdrLine")).offsetHeight + "px");
            d.css("bottom", "0");
            jQuery(c).attr("aria-hidden", "true");
            jQuery.sap.byId(this.getId() + "-focusDummyTPEnd").attr("tabindex", "0").focusin(r.focusFirst);
            jQuery.sap.byId(this.getId() + "-focusDummyHdrStart").attr("tabindex", "0").focusin(jQuery.proxy(this.focusPaneEnd, this));
            jQuery.sap.byId(this.getId() + "-focusDummyHdrEnd").attr("tabindex", "0").focusin(jQuery.proxy(this.focusFirstTool, this));
            jQuery.sap.byId(this.getId() + "-focusDummyTPStart").attr("tabindex", "0").focusin(jQuery.proxy(this.focusLastHdr, this));
            jQuery.sap.byId(this.getId() + "-focusDummyPane").attr("tabindex", "0").focusin(r.focusLast);
            this.$().toggleClass("sapUiUx3ShellBlockHeaderAccess", !this.getAllowOverlayHeaderAccess());
            this._topSyncRefId = r.id
        }
        jQuery.sap.require("jquery.sap.script");
        if (this._sUpdateNotificationZIndex) {
            jQuery.sap.clearDelayedCall(this._sUpdateNotificationZIndex);
            delete this._sUpdateNotificationZIndex
        }
        this._sUpdateNotificationZIndex = jQuery.sap.delayedCall(0, this, function() {
            delete this._sUpdateNotificationZIndex;
            this._setNotifyVisibility();
            if (this._oOpenToolPopup && this._topSyncRefId) {
                var $ = jQuery.sap.byId(this._topSyncRefId);
                this._oOpenToolPopup.$().css("z-index", parseInt($.css("z-index"), 10) + 1)
            }
        })
    }
}());

sap.ui.ux3.Shell.prototype._getSyncRefs = function() {
    var r = [];
    var R;
    for (var i = 0; i < this._aSyncRefStack.length; i++) {
        R = jQuery.sap.domById(this._aSyncRefStack[i]);
        if (R) {
            r.push(R)
        }
    }
    return jQuery(r)
};

sap.ui.ux3.Shell.prototype.focusFirstHdr = function() {
    var t = jQuery.sap.byId(this.getId() + "-hdr-items").firstFocusableDomRef();
    if (t && this.getAllowOverlayHeaderAccess() && this.getHeaderType() != sap.ui.ux3.ShellHeaderType.BrandOnly) {
        jQuery.sap.focus(t)
    } else {
        this.focusFirstTool()
    }
};

sap.ui.ux3.Shell.prototype.focusLastHdr = function() {
    var t = jQuery.sap.byId(this.getId() + "-hdr-items").lastFocusableDomRef();
    if (t && this.getAllowOverlayHeaderAccess() && this.getHeaderType() != sap.ui.ux3.ShellHeaderType.BrandOnly) {
        jQuery.sap.focus(t)
    } else {
        this.focusPaneEnd()
    }
};

sap.ui.ux3.Shell.prototype.focusFirstTool = function() {
    var $ = jQuery.sap.byId(this.getId() + "-tp").find(".sapUiUx3ShellTool").first();
    if ($.length && this.getShowTools()) {
        jQuery.sap.focus($[0])
    } else {
        this._mSyncRefs[this._topSyncRefId].focusFirst()
    }
};

sap.ui.ux3.Shell.prototype.focusLastTool = function() {
    var $ = jQuery.sap.byId(this.getId() + "-tp").find(".sapUiUx3ShellTool").last();
    if ($.length && this.getShowTools()) {
        jQuery.sap.focus($[0])
    } else {
        this.focusPaneEnd()
    }
};

sap.ui.ux3.Shell.prototype.focusPaneStart = function() {
    var t = jQuery.sap.byId(this.getId() + "-paneBar").firstFocusableDomRef();
    if (t) {
        jQuery.sap.focus(t)
    } else {
        this.focusFirstTool()
    }
};

sap.ui.ux3.Shell.prototype.focusPaneEnd = function() {
    var t = jQuery.sap.byId(this.getId() + "-paneBar").lastFocusableDomRef();
    if (t) {
        jQuery.sap.focus(t)
    } else {
        this._mSyncRefs[this._topSyncRefId].focusLast()
    }
};

sap.ui.ux3.Shell.prototype.setAppTitle = function(a) {
    this.setProperty("appTitle", a, true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.setAppIcon = function(a) {
    this.setProperty("appIcon", a, true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.setShowLogoutButton = function(s) {
    this.setProperty("showLogoutButton", s, true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.setLogoutButtonTooltip = function(t) {
    this.setProperty("logoutButtonTooltip", t, true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.insertHeaderItem = function(h, i) {
    if (sap.ui.commons && sap.ui.commons.Button && (h instanceof sap.ui.commons.Button)) {
        h.setStyled(false)
    }
    this.insertAggregation("headerItems", h, i, true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.addHeaderItem = function(h) {
    if (sap.ui.commons && sap.ui.commons.Button && (h instanceof sap.ui.commons.Button)) {
        h.setStyled(false)
    }
    this.addAggregation("headerItems", h, true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.removeHeaderItem = function(i) {
    var r = this.removeAggregation("headerItems", i, true);
    this._rerenderHeader();
    return r
};

sap.ui.ux3.Shell.prototype.removeAllHeaderItems = function() {
    var r = this.removeAllAggregation("headerItems", true);
    this._rerenderHeader();
    return r
};

sap.ui.ux3.Shell.prototype.destroyHeaderItems = function() {
    this.destroyAggregation("headerItems", true);
    this._rerenderHeader();
    return this
};

sap.ui.ux3.Shell.prototype.setShowSearchTool = function(s) {
    this.setProperty("showSearchTool", s, true);
    this._rerenderToolPalette();
    return this
};

sap.ui.ux3.Shell.prototype.setShowFeederTool = function(s) {
    this.setProperty("showFeederTool", s, true);
    this._rerenderToolPalette();
    return this
};

sap.ui.ux3.Shell.prototype.setHeaderType = function(h) {
    var c = this.getHeaderType();
    this.setProperty("headerType", h, true);
    this.$().removeClass("sapUiUx3ShellHead" + c).addClass("sapUiUx3ShellHead" + this.getHeaderType());
    var s = this._getSyncRefs();
    if (s.length) {
        s.css("top", jQuery.sap.domById(this.getId() + (this.getAllowOverlayHeaderAccess() ? "-hdr" : "-hdrLine")).offsetHeight + "px")
    }
    var C = {
        showOverlay: h !== sap.ui.ux3.ShellHeaderType.BrandOnly
    };
    jQuery.each(this._mSyncRefs, function(i, r) {
        r.applyChanges(C)
    });
    return this
};

sap.ui.ux3.Shell.prototype.setAllowOverlayHeaderAccess = function(a) {
    this.setProperty("allowOverlayHeaderAccess", a, true);
    var s = this._getSyncRefs();
    if (s.length) {
        this.$().toggleClass("sapUiUx3ShellBlockHeaderAccess", !this.getAllowOverlayHeaderAccess());
        s.css("top", jQuery.sap.domById(this.getId() + (this.getAllowOverlayHeaderAccess() ? "-hdr" : "-hdrLine")).offsetHeight + "px")
    }
    return this
};

sap.ui.ux3.Shell.prototype.setDesignType = function(d) {
    var c = this.getDesignType();
    this.setProperty("designType", d, true);
    var I = this._hasDarkDesign();
    if (this._oSearchPopup) {
        this._oSearchPopup.setInverted(I)
    }
    if (this._oFeederPopup) {
        this._oFeederPopup.setInverted(I)
    }
    var t = this.getToolPopups();
    for (var i = 0; i < t.length; ++i) {
        t[i].setInverted(I)
    }
    this.$().removeClass("sapUiUx3ShellDesign" + c).addClass("sapUiUx3ShellDesign" + d).toggleClass("sapUiUx3ShellDesignLight", !I);
    return this
};

sap.ui.ux3.Shell.prototype.setShowTools = function(s) {
    var i = this.getId();
    this.setProperty("showTools", s, true);
    if (s) {
        this.$().removeClass("sapUiUx3ShellNoTools");
        this._checkResize()
    } else {
        this.$().addClass("sapUiUx3ShellNoTools");
        this._closeCurrentToolPopup();
        jQuery.sap.byId(i + "-tp").attr("style", "")
    }
    this._checkToolPaletteSize(true);
    return this
};

sap.ui.ux3.Shell.prototype.setShowPane = function(s) {
    this.setProperty("showPane", s, true);
    if (s) {
        this.$().removeClass("sapUiUx3ShellNoPane");
        this._refreshCanvasOffsetRight(true);
        this._checkPaneBarOverflow()
    } else {
        var t = this;
        this._closePane(function() {
            t._refreshCanvasOffsetRight(false);
            t.$().addClass("sapUiUx3ShellNoPane")
        })
    }
    return this
};

sap.ui.ux3.Shell.prototype.insertToolPopup = function(t, i) {
    this.insertAggregation("toolPopups", t, i, true);
    t.attachIconChanged(this._rerenderToolPalette, this);
    this._rerenderToolPalette();
    return this
};

sap.ui.ux3.Shell.prototype.addToolPopup = function(t) {
    var i = this._hasDarkDesign();
    t.setInverted(i);
    this.addAggregation("toolPopups", t, true);
    t.attachIconChanged(this._rerenderToolPalette, this);
    this._rerenderToolPalette();
    return this
};

sap.ui.ux3.Shell.prototype.removeToolPopup = function(i) {
    var r = this.removeAggregation("toolPopups", i, true);
    if (r === this._oOpenToolPopup) {
        this._closeCurrentToolPopup()
    }
    this._rerenderToolPalette();
    return r
};

sap.ui.ux3.Shell.prototype.removeAllToolPopups = function() {
    var r = this.removeAllAggregation("toolPopups", true);
    this._rerenderToolPalette();
    return r
};

sap.ui.ux3.Shell.prototype.destroyToolPopups = function() {
    this.destroyAggregation("toolPopups", true);
    this._rerenderToolPalette();
    return this
};

sap.ui.ux3.Shell.prototype.insertContent = function(c, i) {
    this.insertAggregation("content", c, i, true);
    this._rerenderContent();
    return this
};

sap.ui.ux3.Shell.prototype.addContent = function(c) {
    this.addAggregation("content", c, true);
    this._rerenderContent();
    return this
};

sap.ui.ux3.Shell.prototype.removeContent = function(i) {
    var r = this.removeAggregation("content", i, true);
    this._rerenderContent();
    return r
};

sap.ui.ux3.Shell.prototype.removeAllContent = function() {
    var r = this.removeAllAggregation("content", true);
    this._rerenderContent();
    return r
};

sap.ui.ux3.Shell.prototype.destroyContent = function() {
    this.destroyAggregation("content", true);
    this._rerenderContent();
    return this
};

sap.ui.ux3.Shell.prototype.addPaneBarItem = function(p) {
    this.addAggregation("paneBarItems", p, true);
    this._rerenderPaneBarItems();
    return this
};

sap.ui.ux3.Shell.prototype.insertPaneBarItem = function(p, i) {
    this.insertAggregation("paneBarItems", p, i, true);
    this._rerenderPaneBarItems();
    return this
};

sap.ui.ux3.Shell.prototype.removePaneBarItem = function(p) {
    var r = this.removeAggregation("paneBarItems", p, true);
    if (r) {
        var i = sap.ui.getCore().byId(r.getId() + "-overflow");
        if (i) {
            i.destroy()
        }
    }
    this._rerenderPaneBarItems();
    return r
};

sap.ui.ux3.Shell.prototype.removeAllPaneBarItems = function() {
    var r = this.removeAllAggregation("paneBarItems", true);
    this._getPaneOverflowMenu().destroyItems();
    this._rerenderPaneBarItems();
    return r
};

sap.ui.ux3.Shell.prototype.destroyPaneBarItems = function() {
    this.destroyAggregation("paneBarItems", true);
    this._getPaneOverflowMenu().destroyItems();
    this._rerenderPaneBarItems();
    return this
};

sap.ui.ux3.Shell.prototype.addWorksetItem = function(w) {
    this.addAggregation("worksetItems", w, true);
    this._rerenderWorksetItems();
    return this
};

sap.ui.ux3.Shell.prototype.insertWorksetItem = function(w, i) {
    this.insertAggregation("worksetItems", w, i, true);
    this._rerenderWorksetItems();
    return this
};

sap.ui.ux3.Shell.prototype.removeWorksetItem = function(w) {
    var r = this.removeAggregation("worksetItems", w, true);
    this._rerenderWorksetItems();
    return r
};

sap.ui.ux3.Shell.prototype.removeAllWorksetItems = function() {
    var r = this.removeAllAggregation("worksetItems", true);
    this._rerenderWorksetItems();
    return r
};

sap.ui.ux3.Shell.prototype.destroyWorksetItems = function() {
    this.destroyAggregation("worksetItems", true);
    this._rerenderWorksetItems();
    return this
};

(function() {
    function c(s) {
        var o = s.getNotificationBar();
        if (o) {
            s._setNotifyVisibility(sap.ui.ux3.NotificationBarStatus.None);
            o.setVisibleStatus = o.__orig_setVisibleStatus;
            o.detachDisplay(o.__fHandleNotifyDisplay);
            delete o.__orig_setVisibleStatus;
            delete o.__fHandleNotifyDisplay
        }
    };
    sap.ui.ux3.Shell.prototype.setNotificationBar = function(n) {
        c(this);
        if (n) {
            var t = this;
            n.__orig_setVisibleStatus = n.setVisibleStatus;
            n.setVisibleStatus = function(v) {
                this.__orig_setVisibleStatus.apply(this, arguments);
                t._setNotifyVisibility()
            }
        }
        this.setAggregation("notificationBar", n, true);
        this._rerenderNotificationArea();
        if (n) {
            n.__fHandleNotifyDisplay = function(e) {
                var s = e ? e.getParameter("show") : n.hasItems();
                n.setVisibleStatus(s ? sap.ui.ux3.NotificationBarStatus.Default : sap.ui.ux3.NotificationBarStatus.None)
            };
            n.attachDisplay(n.__fHandleNotifyDisplay);
            n.__fHandleNotifyDisplay()
        }
        return this
    };
    sap.ui.ux3.Shell.prototype.destroyNotificationBar = function() {
        c(this);
        this.destroyAggregation("notificationBar", true);
        this._rerenderNotificationArea();
        return this
    }
}());

sap.ui.ux3.Shell.prototype._rerenderNotificationArea = function() {
    var $ = jQuery.sap.byId(this.getId() + "-notify");
    if ($.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        sap.ui.ux3.ShellRenderer.renderNotificationArea(r, this);
        r.flush($[0], true);
        r.destroy()
    }
};

sap.ui.ux3.Shell.prototype._rerenderHeader = function() {
    var $ = jQuery.sap.byId(this.getId() + "-hdr");
    if ($.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        sap.ui.ux3.ShellRenderer.renderHeader(r, this);
        r.flush($[0], true);
        r.destroy()
    }
};

sap.ui.ux3.Shell.prototype._rerenderToolPalette = function() {
    var $ = jQuery.sap.byId(this.getId() + "-tp");
    if ($.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        this._beforeRenderingToolPalette();
        sap.ui.ux3.ShellRenderer.renderToolPalette(r, this);
        r.flush($[0], true);
        this._afterRenderingToolPalette();
        r.destroy();
        this._checkResize()
    }
};

sap.ui.ux3.Shell.prototype._beforeRenderingToolPalette = function() {
    jQuery.sap.byId(this.getId() + "-tp").find(".sapUiUx3ShellTool").unbind("mouseenter mouseleave");
    jQuery.sap.byId(this.getId() + "-focusDummyTPEnd").unbind("focusin");
    jQuery.sap.byId(this.getId() + "-focusDummyTPStart").unbind("focusin")
};

sap.ui.ux3.Shell.prototype._afterRenderingToolPalette = function() {
    var I = this.getId() + sap.ui.ux3.Shell.TOOL_PREFIX;
    var t = this.getToolPopups();
    var v = false;
    if (this._oOpenToolPopup && this._oOpenToolPopup.isOpen() && (this.indexOfToolPopup(this._oOpenToolPopup) >= 0 || this._oOpenToolPopup === this._oSearchPopup || this._oOpenToolPopup === this._oFeederPopup)) {
        this._oOpenToolPopup.setPosition(sap.ui.core.Popup.Dock.BeginTop, sap.ui.core.Popup.Dock.EndTop, jQuery.sap.domById(I + this._oOpenToolPopup.getId()), "13 -6", "fit");
        var p = jQuery.sap.domById(I + this._oOpenToolPopup.getId());
        jQuery(p).toggleClass("sapUiUx3ShellToolSelected", true);
        sap.ui.ux3.Shell._updateToolIcon(p);
        v = true
    }
    if (!v && this._oOpenToolPopup) {
        this._closeCurrentToolPopup()
    }
    for (var i = 0; i < t.length; i++) {
        var T = t[i];
        if (T instanceof sap.ui.ux3.ToolPopup) {
            jQuery.sap.byId(I + T.getId()).hover(function(e) {
                jQuery(this).toggleClass("sapUiUx3ShellToolHover", (e.type === "mouseenter"));
                sap.ui.ux3.Shell._updateToolIcon(this)
            })
        }
    }
    if (this._topSyncRefId && this._mSyncRefs[this._topSyncRefId].focusFirst) {
        jQuery.sap.byId(this.getId() + "-focusDummyTPEnd").attr("tabindex", "0").focusin(this._mSyncRefs[this._topSyncRefId].focusFirst)
    }
    if (this._aSyncRefStack.length > 0) {
        jQuery.sap.byId(this.getId() + "-focusDummyTPStart").attr("tabindex", "0").focusin(jQuery.proxy(this.focusLastHdr, this))
    }
};

sap.ui.ux3.Shell.prototype._rerenderContent = function(p) {
    var $ = jQuery.sap.byId(this.getId() + "-content");
    if ($.length > 0) {
        var c = this.getContent(),
            r = sap.ui.getCore().createRenderManager();
        for (var i = 0; i < c.length; i++) {
            r.renderControl(c[i])
        }
        r.flush($[0], p);
        r.destroy()
    }
};

sap.ui.ux3.Shell.prototype._rerenderPane = function(p) {
    var $ = jQuery.sap.byId(this.getId() + "-paneContent");
    if ($.length > 0) {
        var P = this.getPaneContent(),
            r = sap.ui.getCore().createRenderManager();
        for (var i = 0; i < P.length; i++) {
            r.renderControl(P[i])
        }
        r.flush($[0], p);
        r.destroy()
    }
};

sap.ui.ux3.Shell.prototype._rerenderPaneBarItems = function() {
    var p = jQuery.sap.byId(this.getId() + "-paneBar").find(".sapUiUx3ShellPaneEntries");
    if (p.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        sap.ui.ux3.ShellRenderer.renderPaneBarItems(r, this);
        r.flush(p[0]);
        r.destroy();
        this._updatePaneBarItemNavigation(p)
    }
    var t = this;
    var m = this._getPaneOverflowMenu();
    var P = this.getPaneBarItems();
    var o = function(e) {
        var b = {
            target: {
                id: e.getParameter("id").replace(/-overflow$/, ""),
                parentNode: {
                    className: "sapUiUx3ShellPaneEntries"
                }
            }
        };
        t.onUserActivation(b)
    };
    for (var i = 0; i < P.length; ++i) {
        var I = P[i].getId() + "-overflow";
        var a = sap.ui.getCore().byId(I);
        if (!a) {
            m.addItem(new sap.ui.commons.MenuItem(I, {
                text: P[i].getText(),
                visible: false,
                select: o
            }))
        }
    }
    this._checkPaneBarOverflow(p)
};

sap.ui.ux3.Shell.prototype._rerenderWorksetItems = function() {
    if (jQuery.sap.byId(this.getId() + "-wBar").length > 0) {
        sap.ui.ux3.ShellRenderer.renderWorksetItems(null, this);
        sap.ui.ux3.ShellRenderer.renderFacetBar(null, this);
        var i = this._oFacetBar.getAssociatedItems();
        this._oFacetBar.$().css("display", (i && i.length > 0 ? "block" : "none"));
        this._adaptContentHeight()
    }
};

sap.ui.ux3.Shell.prototype.forceInvalidation = sap.ui.core.Control.prototype.invalidate;

sap.ui.ux3.Shell.prototype.invalidate = function(s) {
    if (s == this) {} else if (!s) {
        this.forceInvalidation()
    } else if (s instanceof sap.ui.ux3.NavigationItem) {
        if (this._oWorksetBar && this._oFacetBar) {
            this._oWorksetBar.setAssociatedItems(this.getWorksetItems());
            var w = this.getSelectedWorksetItem();
            if (w) {
                var i;
                var a = sap.ui.getCore().byId(w);
                if (a && a.getParent() instanceof sap.ui.ux3.NavigationItem) {
                    this._oFacetBar.setAssociatedItems(a.getParent().getSubItems())
                } else if (a && (i = a.getSubItems())) {
                    if (i && (i.length > 0)) {
                        this._oFacetBar.setAssociatedItems(i)
                    }
                } else {}
                i = this._oFacetBar.getAssociatedItems();
                this._oFacetBar.$().css("display", (i && i.length > 0 ? "block" : "none"));
                this._adaptContentHeight()
            }
        }
    } else if ((s instanceof sap.ui.core.Item) && (s.getParent() == this)) {
        this.forceInvalidation()
    } else if (s instanceof sap.ui.ux3.ToolPopup) {
        this._rerenderToolPalette()
    } else if (this.indexOfHeaderItem(s) >= 0) {
        this._rerenderHeader()
    } else {}
};

sap.ui.ux3.Shell.prototype._setParent = sap.ui.core.Control.prototype.setParent;

sap.ui.ux3.Shell.prototype.setParent = function(p, a, s) {
    this._setParent(p, a, s);
    this.forceInvalidation()
};

sap.ui.ux3.Shell.prototype.setContent = function(c, d) {
    var o = [];
    var $ = jQuery.sap.byId(this.getId() + "-content");
    var p = false;
    if (!d) {
        o = this.removeAllAggregation("content", true);
        if ($.length > 0) {
            sap.ui.core.RenderManager.preserveContent($[0]);
            p = true;
            $.empty()
        }
    } else {
        this.destroyAggregation("content", true)
    }
    if (c instanceof sap.ui.core.Control) {
        this.addAggregation("content", c, true)
    } else if (c && typeof(c) == "object" && c.length) {
        for (var i = 0; i < c.length; i++) {
            this.addAggregation("content", c[i], true)
        }
    }
    this._rerenderContent(p);
    return o
};

sap.ui.ux3.Shell.prototype.setPaneContent = function(c, d) {
    var o = [];
    var $ = jQuery.sap.byId(this.getId() + "-paneContent");
    var p = false;
    if (!d) {
        o = this.removeAllAggregation("paneContent", true);
        if ($.length > 0) {
            sap.ui.core.RenderManager.preserveContent($[0]);
            p = true;
            $.empty()
        }
    } else {
        this.destroyAggregation("paneContent", true)
    }
    if (c instanceof sap.ui.core.Control) {
        this.addAggregation("paneContent", c, true)
    } else if (c && typeof(c) == "object" && c.length) {
        for (var i = 0; i < c.length; i++) {
            this.addAggregation("paneContent", c[i], true)
        }
    }
    this._rerenderPane(p);
    return o
};

sap.ui.ux3.Shell.prototype.getSelectedWorksetItem = function() {
    return this.getAssociation("selectedWorksetItem")
};

sap.ui.ux3.Shell.prototype.setSelectedWorksetItem = function(s) {
    var o = this.getSelectedWorksetItem();
    this.setAssociation("selectedWorksetItem", s, true);
    var n = this.getSelectedWorksetItem();
    if (o != n) {
        var a = sap.ui.getCore().byId(n);
        if (a) {
            this._sSelectedWorksetId = n;
            this._sSelectedFacetId = null;
            var b = a.getSubItems();
            if (b.length > 0) {
                a = b[0]
            }
            if (a && (a.getParent() instanceof sap.ui.ux3.NavigationItem)) {
                var c = a.getParent();
                this._sSelectedWorksetId = c.getId();
                this._sSelectedFacetId = a.getId();
                this._oWorksetBar.setSelectedItem(c);
                this._oFacetBar.setAssociatedItems(c.getSubItems());
                this._oFacetBar.setSelectedItem(a);
                if (this.getDomRef()) {
                    var f = jQuery.sap.byId(this.getId() + "-facetBar");
                    var F = this._calcFacetBarHeight(f);
                    f.slideDown();
                    this._adaptContentHeight(null, true, F)
                }
            } else if (a) {
                this._oWorksetBar.setSelectedItem(a);
                this._oFacetBar.setAssociatedItems([]);
                this._oFacetBar.setSelectedItem(null);
                if (this.getDomRef()) {
                    var f = jQuery.sap.byId(this.getId() + "-facetBar");
                    var F = this._calcFacetBarHeight(f);
                    f.slideUp();
                    this._adaptContentHeight(null, true, F)
                }
            } else {}
        } else {
            throw new Error("WorksetItem with ID " + n + " cannot be found.")
        }
    }
    return this
};

sap.ui.ux3.Shell.prototype.setApplyContentPadding = function(a) {
    this.setProperty("applyContentPadding", a, true);
    this.$().toggleClass("sapUiUx3ShellNoContentPadding", !a);
    this._adaptContentHeight()
};

sap.ui.ux3.Shell.prototype.setFullHeightContent = function(f) {
    this.setProperty("fullHeightContent", f, true);
    this.$().toggleClass("sapUiUx3ShellFullHeightContent", f);
    this._adaptContentHeight()
};

sap.ui.ux3.Shell.prototype._calcFacetBarHeight = function(f) {
    if (this._iFacetBarHeight === undefined) {
        this._iFacetBarHeight = 0
    }
    if (!f) {
        f = jQuery.sap.byId(this.getId() + "-facetBar")
    }
    if (f.length > 0) {
        var h = jQuery.sap.byId(this.getId() + "-facetBar").outerHeight(true);
        this._iFacetBarHeight = Math.max(this._iFacetBarHeight, h)
    }
    return this._iFacetBarHeight
};

sap.ui.ux3.Shell.prototype._adaptContentHeight = function(n, a, f) {
    if (!this.getDomRef()) {
        return
    }
    var $ = jQuery.sap.byId(this.getId() + "-content");
    var b = jQuery.sap.byId(this.getId() + "-canvas");
    if (this.getFullHeightContent()) {
        var p = this.getApplyContentPadding();
        var t = !p ? 0 : parseInt(b.css("paddingTop"), 10);
        var l = !p ? 0 : parseInt(b.css("paddingLeft"), 10);
        var r = !p ? 0 : parseInt(b.css("paddingRight"), 10);
        var _ = f ? f : this._calcFacetBarHeight();
        var T = (t + (this._oFacetBar.getAssociatedItems().length > 0 ? _ : 0)) + "px";
        if (a) {
            $.stop().animate({
                top: T
            })
        } else {
            $.stop().css("top", T)
        }
        var N = this.getNotificationBar();
        if (N && !n) {
            n = N.getVisibleStatus()
        }
        if (n === sap.ui.ux3.NotificationBarStatus.Default || n === sap.ui.ux3.NotificationBarStatus.Max) {
            $.css("bottom", N.getHeightOfStatus(sap.ui.ux3.NotificationBarStatus.Default))
        } else {
            var B = !p ? 0 : parseInt(b.css("paddingBottom"), 10);
            if (N && n === sap.ui.ux3.NotificationBarStatus.Min) {
                B += sap.ui.ux3.NotificationBar.HOVER_ITEM_HEIGHT
            }
            $.css("bottom", B + "px")
        }
        $.css(this._bRtl ? "right" : "left", l + "px");
        $.css(this._bRtl ? "left" : "right", r + "px")
    } else {
        $.removeAttr("style")
    }
    if ( !! sap.ui.Device.browser.webkit) {
        sap.ui.core.RenderManager.forceRepaint(this.getId() + "-canvas")
    }
};

sap.ui.ux3.Shell.prototype._handleDragover = function(e) {
    var i = e.target.id;
    if (!this._dragOverBlinking) {
        var $ = jQuery.sap.byId(i);
        $.css("opacity", "0.5");
        this._dragOverBlinking = true;
        var t = this;
        window.setTimeout(function() {
            $.css("opacity", "1");
            window.setTimeout(function() {
                t._dragOverBlinking = null
            }, 300)
        }, 300)
    }
    return false
};

sap.ui.ux3.Shell.prototype._handleDragend = function(e) {
    return false
};

sap.ui.ux3.Shell.prototype._handleDrop = function(a) {
    var i = a.target.id;
    a.preventDefault();
    var e = a.originalEvent;
    var f = e.dataTransfer.files[0];
    if (f) {
        var r = new window.FileReader();
        r.onload = jQuery.proxy(function(b) {
            var d = b.target.result;
            if (i == this.getId() + "-bgImg") {
                this._getPersonalization()._handleBackgroundImageChange(d, true)
            } else if (i == this.getId() + "-hdr") {
                this._getPersonalization()._handleHeaderImageChange(d, true)
            } else if (i == this.getId() + "-logoImg") {
                this._getPersonalization()._handleLogoImageChange(d, true)
            }
            r = null
        }, this);
        r.readAsDataURL(f)
    }
};

sap.ui.ux3.Shell.prototype._getPersonalization = function() {
    if (!this.oPersonalization) {
        jQuery.sap.require("sap.ui.ux3.ShellPersonalization");
        this.oPersonalization = new sap.ui.ux3.ShellPersonalization(this)
    }
    return this.oPersonalization
};

sap.ui.ux3.Shell.prototype.openPersonalizationDialog = function() {
    this._getPersonalization().openDialog()
};

sap.ui.ux3.Shell.prototype.initializePersonalization = function(s) {
    this._getPersonalization().initializeSettings(s)
};

sap.ui.ux3.Shell.prototype._convertImageParameter = function(p) {
    var r = new RegExp(/url[\s]*\('?"?([^\'")]*)'?"?\)/);
    return r.exec(p) ? r.exec(p)[1] : p
};

sap.ui.ux3.Shell.prototype.onThemeChanged = function(e) {
    this._iFacetBarHeight = undefined;
    if (!this.getDomRef()) {
        return
    }
    var i = "";
    if (!this.getAppIcon()) {
        i = sap.ui.core.theming.Parameters.get('sapUiUx3ShellApplicationImageURL');
        i = this._convertImageParameter(i);
        if (i) {
            jQuery.sap.byId(this.getId() + '-logoImg').attr('src', i)
        } else {
            jQuery.sap.byId(this.getId() + '-logoImg').attr('src', sap.ui.resource('sap.ui.core', 'themes/base/img/1x1.gif'))
        }
    }
    i = sap.ui.core.theming.Parameters.get('sapUiUx3ShellHeaderImageURL');
    i = this._convertImageParameter(i);
    if (i) {
        jQuery.sap.byId(this.getId() + '-hdrImg').attr('src', i)
    } else {
        jQuery.sap.byId(this.getId() + '-hdrImg').attr('src', sap.ui.resource('sap.ui.core', 'themes/base/img/1x1.gif'))
    }
    i = sap.ui.core.theming.Parameters.get('sapUiUx3ShellBackgroundImageURL');
    i = this._convertImageParameter(i);
    if (i) {
        jQuery.sap.byId(this.getId() + '-bgImg').attr('src', i)
    } else {
        jQuery.sap.byId(this.getId() + '-bgImg').attr('src', sap.ui.resource('sap.ui.core', 'themes/base/img/1x1.gif'))
    }
    jQuery.sap.byId(this.getId() + "-facetBar").stop(true, true);
    this._adaptContentHeight();
    this._updateThemeVariables();
    this._checkResize()
};