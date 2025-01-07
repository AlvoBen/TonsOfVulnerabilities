﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.ux3.ActionBar");
jQuery.sap.require("sap.ui.ux3.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.ux3.ActionBar", {
    metadata: {
        publicMethods: ["closePopups"],
        library: "sap.ui.ux3",
        properties: {
            "followState": {
                type: "sap.ui.ux3.FollowActionState",
                group: "Misc",
                defaultValue: sap.ui.ux3.FollowActionState.Default
            },
            "flagState": {
                type: "boolean",
                group: "Misc",
                defaultValue: null
            },
            "favoriteState": {
                type: "boolean",
                group: "Misc",
                defaultValue: null
            },
            "thingIconURI": {
                type: "sap.ui.core.URI",
                group: "Misc",
                defaultValue: null
            },
            "alwaysShowMoreMenu": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showUpdate": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showFollow": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showFlag": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showFavorite": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showOpen": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "dividerWidth": {
                type: "sap.ui.core.CSSSize",
                group: "Misc",
                defaultValue: null
            }
        },
        aggregations: {
            "businessActions": {
                type: "sap.ui.ux3.ThingAction",
                multiple: true,
                singularName: "businessAction"
            },
            "_businessActionButtons": {
                type: "sap.ui.commons.Button",
                multiple: true,
                singularName: "_businessActionButton",
                visibility: "hidden"
            },
            "_socialActions": {
                type: "sap.ui.ux3.ThingAction",
                multiple: true,
                singularName: "_socialAction",
                visibility: "hidden"
            }
        },
        events: {
            "actionSelected": {},
            "feedSubmit": {}
        }
    }
});
sap.ui.ux3.ActionBar.M_EVENTS = {
    'actionSelected': 'actionSelected',
    'feedSubmit': 'feedSubmit'
};
jQuery.sap.require("sap.ui.core.delegate.ItemNavigation");

sap.ui.ux3.ActionBar.prototype.init = function() {
    this.mActionMap = {};
    this.mActionKeys = sap.ui.ux3.ActionBarSocialActions;
    this.oRb = sap.ui.getCore().getLibraryResourceBundle("sap.ui.ux3");
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Update), true);
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Follow), true);
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Flag), true);
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Favorite), true);
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Open), true);
    if (!this._oItemNavigation) {
        this._oItemNavigation = new sap.ui.core.delegate.ItemNavigation();
        this.addDelegate(this._oItemNavigation)
    }
};

sap.ui.ux3.ActionBar.prototype.exit = function() {
    this.closePopups();
    if (this._oUpdatePopup) {
        this._oUpdatePopup.destroy();
        this._oUpdatePopup = null
    }
    if (this._oMoreMenuButton) {
        this._oMoreMenuButton.destroy();
        this._oMoreMenuButton = null
    }
    if (this._oMoreMenu) {
        this._oMoreMenu.destroy();
        this._oMoreMenu = null
    }
    if (this._oHoldItem) {
        this._oHoldItem.destroy()
    }
    if (this._oUnFollowItem) {
        this._oUnFollowItem.destroy()
    }
    if (this._oUnHoldItem) {
        this._oUnHoldItem.destroy()
    }
    if (this._sResizeListenerId) {
        sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
        this._sResizeListenerId = null
    }
    this.mActionKeys = null;
    this.mActionKeys = null;
    this.oRb = null;
    this.destroyAggregation("_socialActions");
    this.destroyAggregation("_businessActionButtons");
    if (this._oItemNavigation) {
        this.removeDelegate(this._oItemNavigation);
        this._oItemNavigation.destroy();
        delete this._oItemNavigation
    }
};

sap.ui.ux3.ActionBar.prototype.isActive = function() {
    var r = jQuery.sap.domById(this.getId()) != null;
    return r
};

sap.ui.ux3.ActionBar.prototype._getLocalizedText = function(k, a) {
    var t = undefined;
    if (this.oRb) {
        t = this.oRb.getText(k)
    }
    if (t && a) {
        for (var i = 0; i < a.length; i++) {
            t = t.replace("{" + i + "}", a[i])
        }
    }
    return t ? t : k
};

sap.ui.ux3.ActionBar.prototype._getSocialAction = function(a) {
    var r = this.mActionMap[a];
    if (!r) {
        r = new sap.ui.ux3.ThingAction({
            id: this.getId() + "-" + a
        });
        switch (a) {
            case this.mActionKeys.Update:
                r.name = this.mActionKeys.Update;
                r.tooltipKey = "ACTIONBAR_UPDATE_ACTION_TOOLTIP";
                r.cssClass = "sapUiUx3ActionBarUpdateAction";
                r.fnInit = function(A) {
                    A._oUpdatePopup = new sap.ui.ux3.ToolPopup({
                        id: A.getId() + "-UpdateActionPopup"
                    }).addStyleClass("sapUiUx3ActionBarUpdatePopup");
                    A._oUpdatePopup._ensurePopup().setAutoClose(true);
                    A._feeder = new sap.ui.ux3.Feeder({
                        id: A.getId() + "-Feeder",
                        type: sap.ui.ux3.FeederType.Comment,
                        thumbnailSrc: A.getThingIconURI(),
                        text: "",
                        submit: jQuery.proxy(function(e) {
                            var d = e.getParameter("text");
                            this.fireFeedSubmit({
                                text: d
                            });
                            this._oUpdatePopup.close()
                        }, A)
                    });
                    A._feeder.addStyleClass("sapUiUx3ActionBarFeeder");
                    A._oUpdatePopup.addContent(A._feeder)
                };
                r.fnActionSelected = function(e, A) {
                    if (A._oUpdatePopup.isOpen()) {
                        A._oUpdatePopup.close()
                    } else {
                        var d, t, C;
                        A._oUpdatePopup.setPosition(sap.ui.core.Popup.Dock.BeginBottom, sap.ui.core.Popup.Dock.BeginTop, e.getSource().getDomRef(), "-8 -13", "none");
                        A._oUpdatePopup.open();
                        d = jQuery(A._oUpdatePopup.getDomRef());
                        t = jQuery(window).height();
                        C = jQuery(A.getDomRef()).offset().top;
                        d.css("top", "auto").css("bottom", (t - C + 7) + "px");
                        jQuery.sap.delayedCall(1000, this, function() {
                            jQuery.sap.focus(A._feeder.getFocusDomRef())
                        })
                    }
                };
                r.fnExit = function(A) {
                    if (A._oUpdatePopup) {
                        A._oUpdatePopup.destroy();
                        A._oUpdatePopup = null
                    }
                };
                break;
            case this.mActionKeys.Follow:
                var f = r;
                r.name = this.mActionKeys.Follow;
                r.tooltipKey = "ACTIONBAR_FOLLOW_ACTION_TOOLTIP_FOLLOW";
                r.cssClass = "sapUiUx3ActionBarFollowAction";
                r.isMenu = function(A) {
                    return A.getFollowState() != sap.ui.ux3.FollowActionState.Default
                };
                r.fnActionSelected = function(e, A) {
                    if (A.getFollowState() == sap.ui.ux3.FollowActionState.Default) {
                        A._setFollowState(sap.ui.ux3.FollowActionState.Follow);
                        A.fireActionSelected({
                            id: f.name,
                            state: "followState",
                            action: f
                        });
                        this._fnPrepareFollowMenu(e, A)
                    } else {
                        var d = sap.ui.core.Popup.Dock;
                        A._oMenu.open(false, f.getFocusDomRef(), d.BeginBottom, d.BeginTop, f.getDomRef())
                    }
                };
                r.fnCalculateState = function(A) {
                    return A.getFollowState()
                };
                r._fnPrepareFollowMenu = function(e, A) {
                    var i = sap.ui.resource("sap.ui.ux3", "themes/" + sap.ui.getCore().getConfiguration().getTheme());
                    if (A.mActionMap[A.mActionKeys.Follow]) {
                        if (!A._oUnFollowItem) {
                            A._oUnFollowItem = new sap.ui.commons.MenuItem({
                                id: A.getId() + "-unfollowState",
                                text: A._getLocalizedText("TI_FOLLOW_ACTION_MENU_TXT_UNFOLLOW"),
                                icon: i + "/img/menu_unlisten.png"
                            })
                        }
                        if (!A._oHoldItem) {
                            A._oHoldItem = new sap.ui.commons.MenuItem({
                                id: A.getId() + "-holdState",
                                text: A._getLocalizedText("TI_FOLLOW_ACTION_MENU_TXT_HOLD"),
                                icon: i + "/img/menu_hold.png"
                            })
                        }
                        if (!A._oUnHoldItem) {
                            A._oUnHoldItem = new sap.ui.commons.MenuItem({
                                id: A.getId() + "-unholdState",
                                text: A._getLocalizedText("TI_FOLLOW_ACTION_MENU_TXT_UNHOLD"),
                                icon: i + "/img/menu_follow.png"
                            })
                        }
                        if (!A._oMenu) {
                            A._oMenu = new sap.ui.commons.Menu({
                                id: A.getId() + "-followActionMenu"
                            });
                            A._oMenu.attachItemSelect(jQuery.proxy(function(C) {
                                this._fnFollowMenuSelected(C, A)
                            }, this));
                            A._oMenu.addItem(A._oHoldItem);
                            A._oMenu.addItem(A._oUnHoldItem);
                            A._oMenu.addItem(A._oUnFollowItem)
                        }
                        if (A.getFollowState() == sap.ui.ux3.FollowActionState.Default) {
                            A.mActionMap[A.mActionKeys.Follow].setTooltip(A._getLocalizedText("TI_FOLLOW_ACTION_TOOLTIP_FOLLOW"));
                            A._oHoldItem.setVisible(false);
                            A._oUnFollowItem.setVisible(false);
                            A._oUnHoldItem.setVisible(false)
                        } else if (A.getFollowState() == sap.ui.ux3.FollowActionState.Follow) {
                            A.mActionMap[A.mActionKeys.Follow].setTooltip(A._getLocalizedText("TI_FOLLOW_ACTION_TOOLTIP_STOPPAUSE_FOLLOW"));
                            A._oHoldItem.setVisible(true);
                            A._oUnFollowItem.setVisible(true);
                            A._oUnHoldItem.setVisible(false)
                        } else if (A.getFollowState() == sap.ui.ux3.FollowActionState.Hold) {
                            A.mActionMap[A.mActionKeys.Follow].setTooltip(A._getLocalizedText("TI_FOLLOW_ACTION_TOOLTIP_STOPCONTINUE_FOLLOW"));
                            A._oHoldItem.setVisible(false);
                            A._oUnFollowItem.setVisible(true);
                            A._oUnHoldItem.setVisible(true)
                        }
                        A._updateSocialActionDomRef(r)
                    }
                };
                r._fnFollowMenuSelected = function(e, A) {
                    if (A.mActionMap[A.mActionKeys.Follow]) {
                        var i = e.getParameters().item.getId();
                        if (i == A.getId() + "-followState") {
                            A._setFollowState(sap.ui.ux3.FollowActionState.Follow)
                        } else if (i == A.getId() + "-unfollowState") {
                            A._setFollowState(sap.ui.ux3.FollowActionState.Default)
                        } else if (i == A.getId() + "-holdState") {
                            A._setFollowState(sap.ui.ux3.FollowActionState.Hold)
                        } else if (i + "-unholdState") {
                            A._setFollowState(sap.ui.ux3.FollowActionState.Follow)
                        }
                        A.fireActionSelected({
                            id: f.name,
                            state: i,
                            action: f
                        });
                        this._fnPrepareFollowMenu(e, A)
                    }
                };
                break;
            case this.mActionKeys.Favorite:
                var b = r;
                r.name = this.mActionKeys.Favorite;
                r.tooltipKey = "ACTIONBAR_FAVORITE_ACTION_TOOLTIP";
                r.cssClass = "sapUiUx3ActionBarFavoriteAction";
                r.fnActionSelected = function(e, A) {
                    if (A.getFavoriteState() == true) {
                        A._setFavoriteState(false)
                    } else {
                        A._setFavoriteState(true)
                    }
                    A.fireActionSelected({
                        id: b.name,
                        state: A.getFavoriteState(),
                        action: b
                    });
                    A._updateSocialActionDomRef(r)
                };
                r.fnCalculateState = function(A) {
                    var d = null;
                    if (A.getFavoriteState()) {
                        d = "Selected"
                    }
                    return d
                };
                break;
            case this.mActionKeys.Flag:
                var c = r;
                r.name = this.mActionKeys.Flag;
                r.tooltipKey = "ACTIONBAR_FLAG_ACTION_TOOLTIP";
                r.cssClass = "sapUiUx3ActionBarFlagAction";
                r.fnActionSelected = function(e, A) {
                    A._setFlagState(!A.getFlagState());
                    A.fireActionSelected({
                        id: c.name,
                        state: A.getFlagState(),
                        action: c
                    });
                    A._updateSocialActionDomRef(r)
                };
                r.fnCalculateState = function(A) {
                    var d = null;
                    if (A.getFlagState()) {
                        d = "Selected"
                    }
                    return d
                };
                break;
            case this.mActionKeys.Open:
                r.name = this.mActionKeys.Open;
                r.tooltipKey = "ACTIONBAR_OPEN_THING_ACTION_TOOLTIP";
                r.cssClass = "sapUiUx3ActionBarOpenThingAction";
                break;
            default:
                jQuery.sap.log.warning("Function \"sap.ui.ux3.ActionBar.prototype._getSocialAction\" was called with unknown action key \"" + a + "\".\n\tNo action will not be rendered.");
                return undefined
        }
    }
    return r
};

sap.ui.ux3.ActionBar.prototype._updateSocialActionDomRef = function(s) {
    var c = jQuery.sap.byId(s.getId());
    if (c) {
        c.attr("class", s.cssClass);
        if (s.fnCalculateState) {
            c.addClass("sapUiUx3ActionBarAction");
            c.addClass(s.fnCalculateState(this))
        }
        if (s.isMenu) {
            c.attr("aria-haspopup", s.isMenu(this) ? "true" : "false")
        }
    }
};

sap.ui.ux3.ActionBar.prototype._rerenderSocialActions = function() {
    var c = jQuery.sap.byId(this.getId() + "-socialActions");
    if (c.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        sap.ui.ux3.ActionBarRenderer.renderSocialActions(r, this);
        r.flush(c[0]);
        r.destroy()
    }
};

sap.ui.ux3.ActionBar.prototype._rerenderBusinessAction = function(b) {
    var c = jQuery.sap.byId(b.getId());
    if (c.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        r.renderControl(b);
        r.flush(c[0].parentNode);
        r.destroy()
    }
};

sap.ui.ux3.ActionBar.prototype._rerenderBusinessActions = function() {
    if (!this.getAlwaysShowMoreMenu()) {
        var c = jQuery.sap.byId(this.getId() + "-businessActions");
        if (c && c.length > 0) {
            var r = sap.ui.getCore().createRenderManager();
            sap.ui.ux3.ActionBarRenderer.renderBusinessActionButtons(r, this);
            r.flush(c[0]);
            r.destroy()
        }
    }
    this._onresize()
};

sap.ui.ux3.ActionBar.prototype.setFollowState = function(f) {
    this.setProperty("followState", f);
    if (!this._oMenu) {
        var F = this._getSocialAction(this.mActionKeys.Follow);
        F._fnPrepareFollowMenu(null, this)
    }
    return this
};

sap.ui.ux3.ActionBar.prototype.setShowUpdate = function(f) {
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Update), f);
    this.setProperty("showUpdate", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype.setShowFollow = function(f) {
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Follow), f);
    this.setProperty("showFollow", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype.setShowFlag = function(f) {
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Flag), f);
    this.setProperty("showFlag", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype.setShowFavorite = function(f) {
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Favorite), f);
    this.setProperty("showFavorite", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype.setShowOpen = function(f) {
    this._setShowSocialAction(this._getSocialAction(this.mActionKeys.Open), f);
    this.setProperty("showOpen", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype._setFollowState = function(f) {
    this.setProperty("followState", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype._setFlagState = function(f) {
    this.setProperty("flagState", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype._setFavoriteState = function(f) {
    this.setProperty("favoriteState", f, true);
    return this
};

sap.ui.ux3.ActionBar.prototype.setThingIconURI = function(i) {
    this.setProperty("thingIconURI", i, true);
    var u = this.mActionMap[this.mActionKeys.Update];
    if (u && this._feeder) {
        this._feeder.setThumbnailSrc(i)
    } else {
        jQuery.sap.log.warning("Function \"sap.ui.ux3.ActionBar.setThingIconURI\": failed to set new icon \"" + i + "\".\n\tReason: either updateAction " + u + " or feeder " + this._feeder + " is not defined.")
    }
    return this
};

sap.ui.ux3.ActionBar.prototype.setDividerWidth = function(w) {
    this._iSocActListWidth = null;
    this.setProperty("dividerWidth", w);
    return this
};

sap.ui.ux3.ActionBar.prototype.setAlwaysShowMoreMenu = function(f) {
    var o = this.getProperty("alwaysShowMoreMenu");
    var b = this.getAggregation("businessActions", []);
    this.setProperty("alwaysShowMoreMenu", f, true);
    if (o != f && b) {
        if (!f) {
            for (var i = 0; i < b.length; i++) {
                var B = b[i];
                this._createButtonForAction(B, this._oMoreMenu._getMenuItemForAction(B))
            }
        } else {
            var a = this._getBusinessActionButtons();
            for (var I = 0; I < a.length; I++) {
                if (a[I].oMenuItem) {
                    a[I].oMenuItem.setVisible(true);
                    a[I].oMenuItem = null
                }
            }
            this.destroyAggregation("_businessActionButtons")
        }
        this._styleMoreMenuButton()
    }
    this._bCallOnresize = true;
    this._rerenderBusinessActions();
    return this
};

sap.ui.ux3.ActionBar.prototype.closePopups = function() {
    if (this._oUpdatePopup) {
        this._oUpdatePopup.close()
    }
    if (this._oMoreMenu) {
        this._oMoreMenu.close()
    }
    if (this._oMenu) {
        this._oMenu.close()
    }
};

sap.ui.ux3.ActionBar.prototype._removeSocialAction = function(s) {
    var r = null;
    if (s.name && this.mActionMap[s.name]) {
        if (this.mActionMap[s.name].fnExit) {
            this.mActionMap[s.name].fnExit(this)
        }
        r = this.removeAggregation("_socialActions", this.mActionMap[s.name], true);
        this.mActionMap[s.name].destroy();
        delete this.mActionMap[s.name];
        this._rerenderSocialActions();
        this._iSocActListWidth = null
    }
    return r
};

sap.ui.ux3.ActionBar.prototype._removeAllSocialActions = function() {
    for (var k in this.mActionMap) {
        if (this.mActionMap[k] && this.mActionMap[k].fnExit) {
            this.mActionMap[k].fnExit(this)
        }
    }
    this.mActionMap = {};
    var r = this.removeAllAggregation("_socialActions", true);
    this._iSocActListWidth = null;
    this._rerenderSocialActions();
    return r
};

sap.ui.ux3.ActionBar.prototype._addSocialAction = function(s, i) {
    var r = null;
    if (!this.mActionMap[s.name]) {
        r = this._prepareSocialAction(s, i);
        if (s.fnInit) {
            s.fnInit(this)
        }
        this._iSocActListWidth = null
    }
    if (r) {
        this._rerenderSocialActions()
    }
    return r
};

sap.ui.ux3.ActionBar.prototype._prepareSocialAction = function(s, i) {
    s.attachSelect(jQuery.proxy(function(c) {
        if (s.fnActionSelected) {
            s.fnActionSelected(c, this)
        } else {
            this.fireActionSelected({
                id: s.name,
                action: s
            })
        }
    }, this));
    s.setTooltip(this._getLocalizedText(s.tooltipKey));
    this.mActionMap[s.name] = s;
    if (i) {
        this.insertAggregation("_socialActions", s, i, true)
    } else {
        this.addAggregation("_socialActions", s, true)
    }
    return s
};

sap.ui.ux3.ActionBar.prototype._setShowSocialAction = function(s, f) {
    return f ? this._addSocialAction(s) : this._removeSocialAction(s)
};

sap.ui.ux3.ActionBar.prototype.addBusinessAction = function(b) {
    return this._addBusinessAction(b)
};

sap.ui.ux3.ActionBar.prototype.insertBusinessAction = function(b, i) {
    return this._addBusinessAction(b, i)
};

sap.ui.ux3.ActionBar.prototype.removeBusinessAction = function(b) {
    return this._removeBusinessAction(b, true)
};

sap.ui.ux3.ActionBar.prototype._removeBusinessAction = function(b, r) {
    if (typeof b === "string") {
        var c = undefined;
        var a = b;
        for (var i = 0; i < this.getBusinessActions().length; i++) {
            var A = this.getBusinessActions()[i];
            if (A.getId() === a) {
                c = A;
                break
            }
        }
        b = c
    }
    if (this._oMoreMenu) {
        var m = this._oMoreMenu._getMenuItemForAction(b);
        if (m) {
            this._oMoreMenu.removeItem(m);
            m.destroy()
        }
        if (this._oMoreMenu.getItems().length == 0) {
            this._oMoreMenuButton.destroy();
            this._oMoreMenuButton = null;
            this._oMoreMenu.destroy();
            this._oMoreMenu = null
        }
    }
    if (!this.getAlwaysShowMoreMenu()) {
        var B = this._getButtonForAction(b);
        if (B) {
            this.removeAggregation("_businessActionButtons", B, true);
            B.destroy()
        }
    }
    var d = this.removeAggregation("businessActions", b, true);
    if (r) {
        this._rerenderBusinessActions()
    }
    return d
};

sap.ui.ux3.ActionBar.prototype.removeAllBusinessActions = function() {
    var b = this.getAggregation("businessActions", []);
    if (b) {
        for (var i = 0; i < b.length; i++) {
            this._removeBusinessAction(b[i], false)
        }
    }
    this._rerenderBusinessActions();
    var r = this.removeAllAggregation("businessActions", true);
    return r
};

sap.ui.ux3.ActionBar.prototype.destroyBusinessActions = function() {
    var b = this.getAggregation("businessActions", []);
    if (b) {
        for (var i = 0; i < b.length; i++) {
            var c = this._removeBusinessAction(b[i], false);
            if (c instanceof sap.ui.core.Element) {
                c.destroy(true)
            }
        }
    }
    this._rerenderBusinessActions();
    var r = this.destroyAggregation("businessActions", true);
    return r
};

sap.ui.ux3.ActionBar.prototype._getBusinessActionButtons = function() {
    return this.getAggregation("_businessActionButtons", [])
};

sap.ui.ux3.ActionBar.prototype._addBusinessAction = function(b, I) {
    var r, t = this;
    if (!I && I != 0) {
        r = this.addAggregation("businessActions", b, true)
    } else {
        r = this.insertAggregation("businessActions", b, I, true)
    }
    if (!this._oMoreMenuButton) {
        this._oMoreMenuButton = new sap.ui.commons.MenuButton(this.getId() + "-MoreMenuButton");
        this._oMoreMenuButton.setText(this._getLocalizedText("ACTIONBAR_BUTTON_MORE_TEXT"));
        this._oMoreMenuButton.setTooltip(this._getLocalizedText("ACTIONBAR_BUTTON_MORE_TOOLTIP"));
        var e = sap.ui.core.Popup.Dock;
        this._oMoreMenuButton.setDockButton(e.EndTop);
        this._oMoreMenuButton.setDockMenu(e.EndBottom);
        this._styleMoreMenuButton();
        this._oMoreMenu = new sap.ui.commons.Menu(this.getId() + "-MoreMenu", {
            ariaDescription: this._getLocalizedText("ACTIONBAR_BUTTON_MORE_TOOLTIP")
        });
        this._oMoreMenu._getMenuItemForAction = function(a) {
            for (var i = 0; i < this.getItems().length; i++) {
                var M = this.getItems()[i];
                if (M.action == a) {
                    return M
                }
            }
            return null
        };
        this._oMoreMenuButton.setMenu(this._oMoreMenu)
    }
    var m = this._oMoreMenu.getId() + "-MenuItem-" + b.getId();
    var M = new sap.ui.commons.MenuItem(m, {
        text: b.getText(),
        enabled: b.getEnabled()
    });
    M.action = b;
    M.attachSelect(jQuery.proxy(function(c) {
        this.fireActionSelected({
            id: b.getId(),
            action: b
        })
    }, this));
    if (I) {
        this._oMoreMenu.insertItem(M, I)
    } else {
        this._oMoreMenu.addItem(M)
    }
    this._createButtonForAction(b, M, I);
    this._rerenderBusinessActions();
    return r
};

sap.ui.ux3.ActionBar.prototype._getMoreMenuButton = function() {
    return this._oMoreMenuButton
};

sap.ui.ux3.ActionBar.prototype._onresize = function(e) {
    var a = jQuery.sap.byId(this.getId());
    if (a) {
        var A = this.getActionBarMinWidth() + "px";
        if (a.css('minWidth') != A) {
            a.css('minWidth', A)
        }
    }
    if (!this.getAlwaysShowMoreMenu() && this._oMoreMenuButton) {
        var s = false;
        if (this._getBusinessActionButtons().length > 1) {
            var m = jQuery.sap.byId(this._oMoreMenuButton.getId()).outerWidth();
            var M = a.outerWidth() - this._getSocialActionListMinWidth() - m;
            var b = this._getBusinessActionButtons();
            var B = 0;
            for (var i = 0; i < b.length; i++) {
                var I = jQuery.sap.byId(b[i].getId()).parent();
                B += I.outerWidth();
                if (i == b.length - 1) {
                    B -= m
                }
                if (B >= M) {
                    if (I.length > 0) {
                        I.css('display', 'none');
                        if (b[i].oMenuItem) {
                            b[i].oMenuItem.setVisible(true)
                        }
                        s = true
                    }
                } else {
                    if (I.length > 0) {
                        I.css('display', '');
                        if ( !! sap.ui.Device.browser.internet_explorer) {
                            this._rerenderBusinessAction(b[i])
                        }
                        if (b[i].oMenuItem) {
                            b[i].oMenuItem.setVisible(false)
                        }
                    }
                }
            }
            s |= this.getAggregation("businessActions").length > b.length
        }
        var o = jQuery.sap.byId(this._oMoreMenuButton.getId()).parent();
        if (o.length > 0) {
            s ? o.css('display', '') : o.css('display', 'none')
        }
        if (!s && this._oMoreMenu) {
            this._oMoreMenu.close()
        }
    }
    this._setItemNavigation()
};

sap.ui.ux3.ActionBar.prototype.onBeforeRendering = function() {
    sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
    this._sResizeListenerId = null
};

sap.ui.ux3.ActionBar.prototype.onAfterRendering = function() {
    this._sResizeListenerId = sap.ui.core.ResizeHandler.register(this.getDomRef(), jQuery.proxy(this._onresize, this));
    if (this._bCallOnresize) {
        this._onresize()
    }
    this._setItemNavigation()
};

sap.ui.ux3.ActionBar.prototype._getSocialActionListMinWidth = function() {
    if (!this._iSocActListWidth) {
        if (this.getDividerWidth()) {
            this._iSocActListWidth = parseInt(this.getDividerWidth(), 10)
        } else {
            var s = this.getAggregation("_socialActions", []);
            var a = s.length;
            this._iSocActListWidth = 24 * a + 12
        }
    }
    return this._iSocActListWidth
};

sap.ui.ux3.ActionBar.prototype.getActionBarMinWidth = function() {
    var r = this._getSocialActionListMinWidth();
    var R = this._oMoreMenuButton;
    if (!this.getAlwaysShowMoreMenu() && this._getBusinessActionButtons().length == 1) {
        R = this._getBusinessActionButtons()[0]
    }
    if (R) {
        var p = jQuery.sap.byId(R.getId()).parent();
        if (p) {
            r += p.outerWidth() - 3
        }
    }
    return r
};

sap.ui.ux3.ActionBar.prototype._getButtonForAction = function(a) {
    for (var i = 0; i < this._getBusinessActionButtons().length; i++) {
        var b = this._getBusinessActionButtons()[i];
        if (b.action == a) {
            return b
        }
    }
    return null
};

sap.ui.ux3.ActionBar.prototype._createButtonForAction = function(b, m, i) {
    if (!this.getAlwaysShowMoreMenu() && !b.showInMoreMenu) {
        var B = new sap.ui.commons.Button({
            id: this.getId() + "-" + b.getId() + "Button",
            text: b.getText(),
            tooltip: b.getTooltip(),
            enabled: b.getEnabled()
        });
        B.attachPress(jQuery.proxy(function(c) {
            this.fireActionSelected({
                id: b.getId(),
                action: b
            })
        }, this));
        B.oMenuItem = m;
        B.action = b;
        if (i) {
            this.insertAggregation("_businessActionButtons", B, i, true)
        } else {
            this.addAggregation("_businessActionButtons", B, true)
        }
        return B
    }
    return null
};

sap.ui.ux3.ActionBar.prototype._styleMoreMenuButton = function() {
    if (this._oMoreMenuButton) {
        if (this.getAlwaysShowMoreMenu()) {
            this._oMoreMenuButton.setLite(true);
            this._oMoreMenuButton.addStyleClass("sapUiUx3ActionBarLiteMoreButton")
        } else {
            this._oMoreMenuButton.setLite(false);
            this._oMoreMenuButton.removeStyleClass("sapUiUx3ActionBarLiteMoreButton")
        }
    }
};

sap.ui.ux3.ActionBar.prototype._setItemNavigation = function() {
    if (this.getDomRef()) {
        this._oItemNavigation.setRootDomRef(jQuery(this.getDomRef()).get(0));
        var I = [];
        var a = this.getAggregation("_socialActions", []);
        for (var i = 0; i < a.length; i++) {
            I.push(a[i].getDomRef())
        }
        a = this.getAggregation("_businessActionButtons", []);
        for (var i = 0; i < a.length; i++) {
            I.push(a[i].getDomRef())
        }
        if (this._oMoreMenuButton && this._oMoreMenuButton.getDomRef()) {
            I.push(this._oMoreMenuButton.getDomRef())
        }
        this._oItemNavigation.setItemDomRefs(I)
    }
};

sap.ui.ux3.ActionBar.prototype.invalidate = function(c) {
    if (c instanceof sap.ui.ux3.ThingAction) {
        var b = sap.ui.getCore().byId(this.getId() + "-" + c.getId() + "Button");
        var B = this._oMoreMenu && this._oMoreMenu._getMenuItemForAction(c);
        if (b) {
            b.setTooltip(c.getTooltip());
            b.setText(c.getText());
            b.setEnabled(c.getEnabled())
        }
        if (B) {
            B.setTooltip(c.getTooltip());
            B.setText(c.getText());
            B.setEnabled(c.getEnabled())
        }
        if (!b && !B) {
            sap.ui.core.Control.prototype.invalidate.apply(this, arguments)
        }
    } else {
        sap.ui.core.Control.prototype.invalidate.apply(this, arguments)
    }
};