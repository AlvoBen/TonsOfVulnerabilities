﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.GrowingEnablement");
jQuery.sap.require("sap.ui.base.Object");
sap.ui.base.Object.extend("sap.m.GrowingEnablement", {
    constructor: function(c) {
        sap.ui.base.Object.apply(this);
        this._oControl = c;
        this._oControl.bUseExtendedChangeDetection = true;
        this._oControl.addDelegate(this);
        var r = this._oControl.getItems().length;
        this._iRenderedDataItems = r;
        this._iItemCount = r;
        this._bLastAsyncCheck = false;
        this._bRebuilding = false;
        this._fnRebuildQ = null;
        this._bLoading = false
    },
    destroy: function() {
        if (this._oBusyIndicator) {
            this._oBusyIndicator.destroy();
            delete this._oBusyIndicator
        }
        if (this._oTrigger) {
            this._oTrigger.destroy();
            delete this._oTrigger
        }
        if (this._oLoading) {
            this._oLoading.destroy();
            delete this._oLoading
        }
        if (this._oScrollDelegate) {
            this._oScrollDelegate.setGrowingList(null);
            this._oScrollDelegate = null
        }
        jQuery(this._oControl.getId() + "-triggerList").remove();
        this._oControl.bUseExtendedChangeDetection = false;
        this._oControl.removeDelegate(this);
        this._bLoading = false;
        this._oControl = null
    },
    render: function(r) {
        r.write("<ul id='" + this._oControl.getId() + "-triggerList'");
        if (this._oControl.getGrowingScrollToLoad()) {
            r.addStyle("display", "none");
            r.writeStyles()
        }
        r.addClass("sapMListUl");
        r.addClass("sapMGrowingList");
        if (this._oControl.setBackgroundDesign) {
            r.addClass("sapMListBG" + this._oControl.getBackgroundDesign())
        }
        if (this._oControl.getInset()) {
            r.addClass("sapMListInset")
        }
        r.writeClasses();
        r.write(">");
        var a;
        if (this._oControl.getGrowingScrollToLoad()) {
            a = this._getLoading(this._oControl.getId() + "-loading")
        } else {
            a = this._getTrigger(this._oControl.getId() + "-trigger")
        }
        a._renderInList = true;
        r.renderControl(a);
        r.write("</ul>")
    },
    onAfterRendering: function() {
        if (this._oControl.getGrowingScrollToLoad()) {
            var s = sap.m.getScrollDelegate(this._oControl);
            if (s) {
                this._oScrollDelegate = s;
                s.setGrowingList(this._oControl, jQuery.proxy(this._triggerLoadingByScroll, this))
            }
        }
        this._updateTrigger()
    },
    setTriggerText: function(t) {
        if (this._oTrigger) {
            this._oTrigger.$().find(".sapMSLITitle").text(t)
        }
    },
    reset: function() {
        this._iItemCount = 0
    },
    getInfo: function() {
        return {
            total: this._oControl.getMaxItemsCount(),
            actual: this._iRenderedDataItems
        }
    },
    requestNewPage: function() {
        if (this._oControl && !this._bLoading && this._iItemCount < this._oControl.getMaxItemsCount()) {
            this._showIndicator();
            jQuery.sap.delayedCall(0, this, function() {
                if (this._oControl) {
                    this._iItemCount += this._oControl.getGrowingThreshold();
                    this.updateItems("Growing")
                }
            })
        }
    },
    _onBeforePageLoaded: function(c) {
        this._bLoading = true;
        this._oControl.onBeforePageLoaded(this.getInfo(), c)
    },
    _onAfterPageLoaded: function(c) {
        this._hideIndicator();
        this._updateTrigger();
        this._bLoading = false;
        this._oControl.onAfterPageLoaded(this.getInfo(), c)
    },
    _renderItemIntoContainer: function(i, d, I) {
        var D = this._oControl.getItemsContainerDomRef();
        if (D) {
            var r = sap.ui.getCore().createRenderManager();
            r.renderControl(i);
            r.flush(D, d, I);
            r.destroy()
        }
    },
    _getBusyIndicator: function() {
        return this._oBusyIndicator || (this._oBusyIndicator = new sap.m.BusyIndicator({
            size: "2.0em"
        }))
    },
    _getLoading: function(i) {
        var t = this;
        return this._oLoading || (this._oLoading = new sap.m.CustomListItem({
            id: i,
            content: new sap.ui.core.HTML({
                content: "<div class='sapMSLIDiv sapMGrowingListLoading'>" + "<div class='sapMGrowingListBusyIndicator' id='" + i + "-busyIndicator'></div>" + "</div>",
                afterRendering: function(e) {
                    var b = t._getBusyIndicator();
                    var r = sap.ui.getCore().createRenderManager();
                    r.render(b, this.getDomRef().firstChild);
                    r.destroy()
                }
            })
        }).setParent(this._oControl, null, true))
    },
    _getTrigger: function(i) {
        var t = this;
        var T = sap.ui.getCore().getLibraryResourceBundle("sap.m").getText("LOAD_MORE_DATA");
        if (this._oControl.getGrowingTriggerText()) {
            T = this._oControl.getGrowingTriggerText()
        }
        this._oControl.addNavSection(i);
        return this._oTrigger || (this._oTrigger = new sap.m.CustomListItem({
            id: i,
            content: new sap.ui.core.HTML({
                content: "<div class='sapMGrowingListTrigger'>" + "<div class='sapMGrowingListBusyIndicator' id='" + i + "-busyIndicator'></div>" + "<div class='sapMSLITitleDiv sapMGrowingListTitel'>" + "<h1 class='sapMSLITitle'>" + jQuery.sap.encodeHTML(T) + "</h1>" + "</div>" + "<div class='sapMGrowingListDescription'>" + "<div class='sapMSLIDescription' id='" + i + "-itemInfo'>" + t._getListItemInfo() + "</div>" + "</div>" + "</div>",
                afterRendering: function(e) {
                    var b = t._getBusyIndicator();
                    var r = sap.ui.getCore().createRenderManager();
                    r.render(b, this.getDomRef().firstChild);
                    r.destroy()
                }
            }),
            type: sap.m.ListType.Active
        }).setParent(this._oControl, null, true).attachPress(this.requestNewPage, this).addEventDelegate({
            onsapspace: this.requestNewPage
        }, this))
    },
    _getListItemInfo: function() {
        return ("[ " + this._iRenderedDataItems + " / " + this._oControl.getMaxItemsCount() + " ]")
    },
    _getGroupForContext: function(c) {
        var n = this._oControl.getBinding("items").aSorters[0].fnGroup(c);
        if (typeof n == "string") {
            n = {
                key: n
            }
        }
        return n
    },
    _getDomIndex: function(i) {
        if (this._oControl.hasPopin && this._oControl.hasPopin()) {
            var I = this._oControl.getItems();
            I.slice(0, i).forEach(function(o) {
                if (o.hasPopin()) {
                    i++
                }
            })
        }
        return i
    },
    destroyListItems: function() {
        this._oControl.destroyAggregation("items");
        this._iRenderedDataItems = 0
    },
    addListItem: function(i, s) {
        this._iRenderedDataItems++;
        var b = this._oControl.getBinding("items"),
            B = this._oControl.getBindingInfo("items");
        if (b.isGrouped() && B) {
            var n = false,
                I = this._oControl.getItems(),
                m = B.model || undefined,
                N = this._getGroupForContext(i.getBindingContext(m));
            if (I.length == 0) {
                n = true
            } else if (N.key !== this._getGroupForContext(I[I.length - 1].getBindingContext(m)).key) {
                n = true
            }
            if (n) {
                var g = null;
                if (B.groupHeaderFactory) {
                    g = B.groupHeaderFactory(N)
                }
                this.addItemGroup(N, g)
            }
        }
        this._oControl.addAggregation("items", i, s);
        if (s) {
            this._renderItemIntoContainer(i, false, true)
        }
        return this
    },
    addListItems: function(c, b, s) {
        if (b && c) {
            for (var i = 0, l = c.length; i < l; i++) {
                var C = b.factory("", c[i]);
                C.setBindingContext(c[i], b.model);
                this.addListItem(C, s)
            }
        }
    },
    rebuildListItems: function(c, b, s) {
        if (this._bRebuilding) {
            this._fnRebuildQ = jQuery.proxy(this, "rebuildListItems", c, b, s);
            return
        }
        this._bRebuilding = true;
        this.destroyListItems();
        this.addListItems(c, b, s);
        this._bRebuilding = false;
        if (this._fnRebuildQ) {
            var r = this._fnRebuildQ;
            this._fnRebuildQ = null;
            r()
        }
    },
    addItemGroup: function(g, h) {
        h = this._oControl.addItemGroup(g, h, true);
        this._renderItemIntoContainer(h, false, true);
        return this
    },
    insertListItem: function(i, I) {
        this._oControl.insertAggregation("items", i, I, true);
        this._iRenderedDataItems++;
        this._renderItemIntoContainer(i, false, this._getDomIndex(I));
        return this
    },
    deleteListItem: function(i) {
        this._iRenderedDataItems--;
        this._oControl.removeAggregation("items", i, true);
        i.destroy();
        return this
    },
    updateItems: function(c) {
        var u = sap.ui.model,
            b = this._oControl.getBindingInfo("items"),
            B = b.binding,
            f = b.factory,
            o = u && u.odata && u.odata.ODataListBinding,
            O = o && B instanceof o;
        if (!this._iItemCount || c == sap.ui.model.ChangeReason.Filter) {
            this._iItemCount = this._oControl.getGrowingThreshold()
        }
        if (!O) {
            this._onBeforePageLoaded(c)
        } else if (!this._bLastAsyncCheck) {
            this._onBeforePageLoaded(c)
        }
        var L = this._iItemCount - this._oControl.getGrowingThreshold();
        var C = B ? B.getContexts(0, this._iItemCount) || [] : [];
        if (B.isGrouped()) {
            if (c && (c == sap.ui.model.ChangeReason.Sort || c == sap.ui.model.ChangeReason.Filter)) {
                this.destroyListItems();
                delete C.diff
            }
            var F = true;
            if (C.length > 0) {
                if (this._oControl.getDomRef()) {
                    if (C.diff) {
                        F = false;
                        var a = false;
                        for (var i = 0, l = C.diff.length; i < l; i++) {
                            if (C.diff[i].type === "delete") {
                                F = true;
                                break
                            } else if (C.diff[i].type === "insert") {
                                if (!a && C.diff[i].index !== this._iRenderedDataItems) {
                                    F = true;
                                    break
                                }
                                a = true;
                                var d = f("", C[C.diff[i].index]);
                                d.setBindingContext(C[C.diff[i].index], b.model);
                                this.addListItem(d, true)
                            }
                        }
                    }
                    if (F) {
                        this.rebuildListItems(C, b, false)
                    }
                } else {
                    this.rebuildListItems(C, b, true)
                }
            } else {
                this.destroyListItems()
            }
        } else {
            if (C.length > 0) {
                if (this._oControl.getDomRef()) {
                    if (C.diff) {
                        var I, d;
                        for (var i = 0, l = C.diff.length; i < l; i++) {
                            I = this._oControl.getItems();
                            if (C.diff[i].type === "delete") {
                                this.deleteListItem(I[C.diff[i].index])
                            } else if (C.diff[i].type === "insert") {
                                d = f("", C[C.diff[i].index]);
                                d.setBindingContext(C[C.diff[i].index], b.model);
                                this.insertListItem(d, C.diff[i].index)
                            }
                        }
                        I = this._oControl.getItems();
                        for (var i = 0, l = C.length; i < l; i++) {
                            I[i].setBindingContext(C[i], b.model)
                        }
                    } else {
                        this.rebuildListItems(C, b, false)
                    }
                } else {
                    this.rebuildListItems(C, b, true)
                }
            } else {
                this.destroyListItems()
            }
        }
        if (!O) {
            this._onAfterPageLoaded(c)
        } else if (this._bLastAsyncCheck) {
            this._onAfterPageLoaded(c);
            this._bLastAsyncCheck = false
        } else if (C.length > L) {
            this._onAfterPageLoaded(c)
        } else {
            this._bLastAsyncCheck = true
        }
    },
    _updateTrigger: function() {
        if (this._oControl.getGrowingScrollToLoad()) {
            return
        }
        var t = document.getElementById(this._oControl.getId() + "-triggerList");
        if (!t) {
            return
        }
        var m = this._oControl.getMaxItemsCount();
        var i = this._oControl.getItems().length;
        var d = (!i || !this._iItemCount || this._iItemCount >= m) ? "none" : "block";
        sap.ui.Device.system.desktop && d == "none" && t.contains(document.activeElement) && this._oControl.$().focus();
        t.style.display = d;
        jQuery.sap.byId(this._oControl.getId() + "-trigger-itemInfo").text(this._getListItemInfo())
    },
    _showIndicator: function() {
        if (!this._oControl.getGrowingScrollToLoad()) {
            jQuery.sap.byId(this._oControl.getId() + "-trigger-busyIndicator").addClass("sapMGrowingListBusyIndicatorVisible")
        } else {
            var $ = jQuery.sap.byId(this._oControl.getId() + "-triggerList").css("display", "block");
            if (jQuery.support.touch && this._oScrollDelegate) {
                if (this._oScrollDelegate.getMaxScrollTop() - this._oScrollDelegate.getScrollTop() < $.height()) {
                    this._oScrollDelegate.refresh();
                    this._oScrollDelegate.scrollTo(this._oScrollDelegate.getScrollLeft(), this._oScrollDelegate.getMaxScrollTop())
                }
            }
        }
        this._getBusyIndicator().setVisible(true)
    },
    _hideIndicator: function() {
        jQuery.sap.delayedCall(0, this, function() {
            if (this._oControl) {
                var i = this._oControl.getId();
                this._getBusyIndicator().setVisible(false);
                if (this._oControl.getGrowingScrollToLoad()) {
                    jQuery.sap.byId(i + "-triggerList").css("display", "none")
                } else {
                    jQuery.sap.byId(i + "-trigger-itemInfo").html(this._getListItemInfo());
                    jQuery.sap.byId(i + "-trigger-busyIndicator").removeClass("sapMGrowingListBusyIndicatorVisible")
                }
            }
        })
    },
    _triggerLoadingByScroll: function() {
        this.requestNewPage()
    }
});