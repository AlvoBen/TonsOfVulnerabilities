﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.Carousel");
jQuery.sap.require("sap.m.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.m.Carousel", {
    metadata: {
        publicMethods: ["next", "previous"],
        library: "sap.m",
        properties: {
            "height": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '100%'
            },
            "loop": {
                type: "boolean",
                group: "Misc",
                defaultValue: false
            },
            "visible": {
                type: "boolean",
                group: "Appearance",
                defaultValue: true
            },
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '100%'
            },
            "showPageIndicator": {
                type: "boolean",
                group: "Appearance",
                defaultValue: true
            },
            "pageIndicatorPlacement": {
                type: "sap.m.PlacementType",
                group: "Appearance",
                defaultValue: sap.m.PlacementType.Bottom
            },
            "showBusyIndicator": {
                type: "boolean",
                group: "Appearance",
                defaultValue: true
            },
            "busyIndicatorSize": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: '6em'
            }
        },
        defaultAggregation: "pages",
        aggregations: {
            "pages": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "page"
            }
        },
        associations: {
            "activePage": {
                type: "sap.ui.core.Control",
                multiple: false
            }
        },
        events: {
            "loadPage": {},
            "unloadPage": {},
            "pageChanged": {}
        }
    }
});
sap.m.Carousel.M_EVENTS = {
    'loadPage': 'loadPage',
    'unloadPage': 'unloadPage',
    'pageChanged': 'pageChanged'
};
jQuery.sap.require("sap.ui.thirdparty.swipe-view");

sap.m.Carousel.prototype.init = function() {};

sap.m.Carousel.prototype.exit = function() {
    if ( !! this._oSwipeView) {
        this._oSwipeView.destroy()
    }
    if ( !! this._prevButton) {
        this._prevButton.destroy()
    }
    if ( !! this._nextButton) {
        this._nextButton.destroy()
    }
    if (this._aBusyIndicators) {
        var i;
        for (i = 0; i < 3; i++) {
            this._aBusyIndicators[i].destroy()
        }
    }
    this._cleanUpTapBindings();
    if (this._sResizeListenerId) {
        sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
        this._sResizeListenerId = null
    }
};

sap.m.Carousel.prototype._cleanUpTapBindings = function() {
    if ( !! this.previousProxy) {
        jQuery.sap.byId(this._getPrevBtnId()).unbind("tap", this.previousProxy);
        delete this.previousProxy
    }
    if ( !! this.nextProxy) {
        jQuery.sap.byId(this._getNextBtnId()).unbind("tap", this.nextProxy);
        delete this.nextProxy
    }
};

sap.m.Carousel.prototype._createTapBindings = function() {
    if (!this.previousProxy) {
        this.previousProxy = jQuery.proxy(this.previous, this);
        jQuery.sap.byId(this._getPrevBtnId()).bind("tap", this.previousProxy)
    }
    if (!this.nextProxy) {
        this.nextProxy = jQuery.proxy(this.next, this);
        jQuery.sap.byId(this._getNextBtnId()).bind("tap", this.nextProxy)
    }
};

sap.m.Carousel.prototype.onBeforeRendering = function() {
    this._cleanUpTapBindings();
    if (this._sResizeListenerId) {
        sap.ui.core.ResizeHandler.deregister(this._sResizeListenerId);
        this._sResizeListenerId = null
    }
};

sap.m.Carousel.prototype.onAfterRendering = function() {
    var s = jQuery.sap.byId(this.getId()).parent('.sapMPageScroll');
    if (s.length > 0) {
        s.css('height', '100%');
        jQuery.sap.log.warning(false, "sap.m.Carousel.onAfterRendering: carousel is contained in scroll container. Changed its height to 100%")
    }
    var p = this.getPages();
    var c = jQuery.sap.domById(this._getContentId());
    if (!this._oSwipeView) {
        if (!this.getActivePage() && p.length > 0) {
            this.setActivePage(p[0].getId())
        }
        this._oSwipeView = new window.SwipeView(c, {
            numberOfPages: p.length,
            loop: this.getLoop()
        });
        var t = ['touchstart', 'touchmove', 'touchend', 'mousedown', 'mousemove', 'mouseup'];
        var i;
        for (i = 0; i < t.length; i++) {
            c.removeEventListener(t[i], this._oSwipeView, false)
        }
        var C = "cubic-bezier(0.33, 0.66, 0.66, 1)";
        jQuery(this._oSwipeView.slider).css("-webkit-transition-timing-function", C).css("transition-timing-function", C);
        this._oSwipeView.fnLoadingCallback = jQuery.proxy(this._toggleBusyIcon, this);
        this._oSwipeView.onFlip(jQuery.proxy(this._doSwipeCompleted, this));
        this._oSwipeView.onMoveOut(jQuery.proxy(this._doSwipeStarted, this));
        this._oSwipeView.onMoveIn(jQuery.proxy(this._doMoveIn, this));
        this._oSwipeView.updatePageCount(this.getPages().length);
        this._aMasterPageDivs = [];
        this._aBusyIndicators = [];
        this._rerenderBusinessIndicators(true);
        if (!this._moveToActivePage()) {
            this._doSwipeCompleted(null, true)
        }
    } else {
        if (this.getPageIndicatorPlacement() == sap.m.PlacementType.Top) {
            jQuery.sap.byId(this.getId()).append(this._oSwipeView.wrapper)
        } else {
            jQuery(this._oSwipeView.wrapper).insertBefore(jQuery.sap.byId(this._getNavId()))
        }
        this._doSwipeCompleted(null, true)
    }
    this._createTapBindings();
    if ( !! c) {
        this._sResizeListenerId = sap.ui.core.ResizeHandler.register(c, jQuery.proxy(this._handleResize, this))
    }
};

sap.m.Carousel.prototype.ontouchstart = function(e) {
    if (this._oSwipeView) {
        var E = jQuery(e.target).control(0);
        if (!(E instanceof sap.m.Slider || E instanceof sap.m.Switch)) {
            e.originalEvent._sapui_handledByControl = false
        }
        this._oSwipeView.__start(e)
    }
    e.originalEvent._sapui_handledByControl = true
};

sap.m.Carousel.prototype.ontouchmove = function(e) {
    if (this._oSwipeView) {
        this._oSwipeView.__move(e)
    }
};

sap.m.Carousel.prototype.ontouchend = function(e) {
    if (this._oSwipeView) {
        this._oSwipeView.__end(e);
        this._sOldActivePageId = this.getActivePage();
        this._updateActivePage()
    }
};

sap.m.Carousel.prototype.setActivePage = function(p) {
    var P = undefined;
    if (typeof(p) == 'string') {
        P = p
    } else if (p instanceof sap.ui.core.Control) {
        P = p.getId()
    }
    if (P) {
        this.setAssociation("activePage", P, true);
        var i = this._getPageNumber(P);
        if (!isNaN(i)) {
            if ( !! this._oSwipeView) {
                this._oSwipeView.goToPage(i)
            }
        }
    } else {}
    return this
};

sap.m.Carousel.prototype._addPage = function(p, I) {
    if (p instanceof sap.ui.core.mvc.View) {
        p.addStyleClass("sapMCarView")
    }
    var d = this.getPages().length == 0;
    var b = typeof(I) == 'number';
    if (b) {
        this.insertAggregation("pages", p, I, !d)
    } else {
        this.addAggregation("pages", p, !d)
    }
    if ( !! this._oSwipeView) {
        this._oSwipeView.updatePageCount(this.getPages().length);
        if (!d) {
            if (b) {
                var a;
                var i;
                for (i = 0; i < 3; i++) {
                    a = this._getContentId() + "-MstPgCont-" + i;
                    jQuery.sap.byId(a).empty()
                }
            }
            this._rerenderPageIndicatorDots()
        }
        this._oSwipeView.goToPage(this._oSwipeView.page)
    }
    return this
};

sap.m.Carousel.prototype.addPage = function(p) {
    return this._addPage(p)
};

sap.m.Carousel.prototype.insertPage = function(p, i) {
    return this._addPage(p, i)
};

sap.m.Carousel.prototype._removePages = function(a, d, p) {
    var r = this;
    if (a) {
        if (d) {
            this.destroyAggregation("pages", true)
        } else {
            r = this.removeAllAggregation("pages", true)
        }
    } else {
        r = this.removeAggregation("pages", p, true)
    }
    if ( !! this._oSwipeView) {
        this._rerenderPageIndicatorDots();
        this._oSwipeView.updatePageCount(this.getPages().length);
        var b;
        var i;
        for (i = 0; i < 3; i++) {
            b = this._getContentId() + "-MstPgCont-" + i;
            jQuery.sap.byId(b).empty()
        }
        this._oSwipeView.goToPage(this._oSwipeView.page)
    }
    return r
};

sap.m.Carousel.prototype.removePage = function(p) {
    return this._removePages(false, false, p)
};

sap.m.Carousel.prototype.removeAllPages = function() {
    return this._removePages(true, false)
};

sap.m.Carousel.prototype.destroyPages = function() {
    return this._removePages(true, true)
};

sap.m.Carousel.prototype.removePage = function(p) {
    return this._removePages(false, false, p)
};

sap.m.Carousel.prototype.setHeight = function(h) {
    this.setProperty("height", h, true);
    jQuery.sap.byId(this.getId()).css("height", h);
    return this
};

sap.m.Carousel.prototype.setWidth = function(w) {
    this.setProperty("width", w, true);
    jQuery.sap.byId(this.getId()).css("width", w);
    return this
};

sap.m.Carousel.prototype.setPageIndicatorPlacement = function(p) {
    if (sap.m.PlacementType.Top != p && sap.m.PlacementType.Bottom != p) {
        p = sap.m.PlacementType.Bottom
    }
    this.setProperty("pageIndicatorPlacement", p);
    return this
};

sap.m.Carousel.prototype.setLoop = function(l) {
    if ( !! this._oSwipeView) {
        this._oSwipeView.options.loop = l
    }
    this.setProperty("loop", l, true);
    return this
};

sap.m.Carousel.prototype.setShowPageIndicator = function(s) {
    var p = jQuery.sap.byId(this._getNavId());
    if ( !! p) {
        if (s) {
            p.show()
        } else {
            p.hide()
        }
    }
    this.setProperty("showPageIndicator", s, true);
    return this
};

sap.m.Carousel.prototype.setShowBusyIndicator = function(s) {
    this.setProperty("showBusyIndicator", s, true);
    this._rerenderBusinessIndicators(false);
    return this
};

sap.m.Carousel.prototype.setBusyIndicatorSize = function(b) {
    this.setProperty("busyIndicatorSize", b, true);
    this._rerenderBusinessIndicators(false);
    return this
};

sap.m.Carousel.prototype.previous = function() {
    if ( !! this._oSwipeView) {
        this._oSwipeView.prev();
        this._updateVisualIndicator(this._oSwipeView.pageIndex)
    }
    return this
};

sap.m.Carousel.prototype.next = function() {
    if ( !! this._oSwipeView) {
        this._oSwipeView.next();
        this._updateVisualIndicator(this._oSwipeView.pageIndex)
    }
    return this
};

sap.m.Carousel.prototype._doSwipeStarted = function() {
    this._oSwipeView.initialSizeCheck();
    var p = this.getPages();
    var a = this._oSwipeView.pageIndex;
    var i;
    if (p.length > 0) {
        var b;
        for (i = 0; i < 3; i++) {
            b = parseInt(this._oSwipeView.masterPages[i].dataset.pageIndex, 10);
            if (i != this._oSwipeView.currentMasterPage) {
                if (!this.getLoop()) {
                    if (p.length > 2) {
                        if ((a == 0 && b == p.length - 1) || (a == p.length - 1 && b == 0)) {
                            this._oSwipeView.masterPages[i].style.visibility = 'hidden'
                        }
                    } else {
                        this._toggleBusyIcon(i, false)
                    }
                } else {
                    if (p.length < 3) {
                        this._toggleBusyIcon(i, false)
                    }
                }
            }
        }
    }
    this._updateVisualIndicator(this._oSwipeView.pageIndex)
};

sap.m.Carousel.prototype._doMoveIn = function() {
    setTimeout(jQuery.proxy(function() {
        if ( !! this._oSwipeView) {
            this._doSwipeCompleted()
        }
    }, this), 250)
};

sap.m.Carousel.prototype._doSwipeCompleted = function(e, I) {
    var u, i;
    var p = this.getPages();
    if (p.length == 0) {
        return
    }
    var a = p[this._oSwipeView.pageIndex - 1] ? p[this._oSwipeView.pageIndex - 1].getId() : null;
    var n = p[this._oSwipeView.pageIndex + 1] ? p[this._oSwipeView.pageIndex + 1].getId() : null;
    if (this.getLoop()) {
        if (!a) {
            a = p[p.length - 1].getId()
        }
        if (!n) {
            n = p[0].getId()
        }
    }
    var r = sap.ui.getCore().createRenderManager();
    var d = [];
    var f = p.length > 2 ? 0 : 1;
    var l = p.length == 1 ? 2 : 3;
    for (i = f; i < l; i++) {
        u = parseInt(this._oSwipeView.masterPages[i].dataset.upcomingPageIndex, 10);
        var c = parseInt(this._oSwipeView.masterPages[i].dataset.pageIndex, 10);
        if (u != c || !p[u].getDomRef()) {
            d[i] = c;
            if (!p[u]._bShownInMCarousel) {
                p[u]._bShownInMCarousel = true;
                var b = jQuery.Event("BeforeFirstShow");
                b.srcControl = this;
                p[u]._handleEvent(b)
            }
            var B = jQuery.Event("BeforeShow");
            B.srcControl = this;
            p[u]._handleEvent(B);
            if ( !! p[u].getDomRef()) {
                jQuery(p[u].getDomRef()).remove()
            }
            r.renderControl(p[u]);
            r.flush(jQuery.sap.domById(this._getContentId() + "-MstPgCont-" + i), false);
            this.fireLoadPage({
                pageId: p[u].getId()
            })
        }
        var $ = jQuery(this._oSwipeView.masterPages[i]);
        if ($.hasClass("swipeview-loading") || I) {
            $.removeClass("swipeview-loading");
            this._toggleBusyIcon(i, false)
        }
        if ($.hasClass("swipeview-active")) {
            this._oSwipeView.pageIndex = u;
            if (I) {
                this._oSwipeView.masterPages[i].style.visibility = ''
            }
        }
    }
    this._updatePageWidths();
    if (!I) {
        for (i = 0; i < 3; i++) {
            if (!isNaN(d[i]) && !! p[d[i]]) {
                this.fireUnloadPage({
                    pageId: p[d[i]].getId()
                });
                var e = jQuery.Event("AfterHide");
                e.srcControl = this;
                p[d[i]]._handleEvent(e)
            }
        }
    }
    this._updateActivePage();
    setTimeout(jQuery.proxy(function() {
        this._oSwipeView.__resize();
        if (!I) {
            this.firePageChanged({
                oldActivePageId: this._sOldActivePageId,
                newActivePageId: this.getActivePage()
            });
            this._sOldActivePageId = undefined
        }
    }, this), 50);
    r.destroy();
    this.bSuppressFireSwipeEvents = false
};

sap.m.Carousel.prototype._moveToActivePage = function() {
    var r = false;
    var p = this.getPages();
    if ( !! this._oSwipeView && p.length > 0) {
        this.bSuppressFireSwipeEvents = true;
        var l = this._oSwipeView.pageIndex;
        var n = this._getPageNumber(this.getActivePage());
        if (isNaN(n)) {
            jQuery.sap.log.warning(false, "sap.m.Carousel._moveToActivePage: Cannot navigate to page '" + this.getActivePage() + "' because it is not contained in the carousel's pages aggregation. Using 1. page instead");
            this.setActivePage(p[0].getId());
            n = 0
        }
        var m = l < n ? this.next : this.previous;
        while (this._oSwipeView.pageIndex != n) {
            m.apply(this);
            r = true
        }
        this.bSuppressFireSwipeEvents = false
    }
    return r
};

sap.m.Carousel.prototype._getPageNumber = function(p) {
    var i, r;
    for (i = 0; i < this.getPages().length; i++) {
        if (this.getPages()[i].getId() == p) {
            r = i;
            break
        }
    }
    return r
};

sap.m.Carousel.prototype._updateVisualIndicator = function(a) {
    if ( !! this._oSwipeView) {
        var s = document.querySelector('#' + this._getNavId() + ' .sapMCrslIndLstItSel');
        if (s) {
            jQuery.sap.byId(s.id).removeClass('sapMCrslIndLstItSel')
        }
        var d = document.querySelectorAll('#' + this._getNavId() + ' .sapMCrslIndLstIt');
        if (d[a]) {
            jQuery.sap.byId(d[a].id).addClass('sapMCrslIndLstItSel')
        }
    }
};

sap.m.Carousel.prototype._updateActivePage = function() {
    if ( !! this._oSwipeView) {
        var a = this._oSwipeView.pageIndex;
        var p = this.getPages()[a];
        this.setAssociation("activePage", p, true);
        this._updateVisualIndicator(a)
    }
};

sap.m.Carousel.prototype._getNavId = function() {
    return this.getId() + '-nav'
};

sap.m.Carousel.prototype._getContentId = function() {
    return this.getId() + '-content'
};

sap.m.Carousel.prototype._getPrevBtnId = function() {
    return this.getId() + '-prevBtn'
};

sap.m.Carousel.prototype._getNextBtnId = function() {
    return this.getId() + '-nextBtn'
};

sap.m.Carousel.prototype._handleResize = function() {
    if ( !! this._oSwipeView) {
        this._oSwipeView.__resize();
        this._updatePageWidths()
    }
};

sap.m.Carousel.prototype._toggleBusyIcon = function(m, s) {
    if (s) {
        this._aBusyIndicators[m].$().show();
        this._aMasterPageDivs[m].css("visibility", "hidden")
    } else {
        this._aBusyIndicators[m].$().hide();
        this._aMasterPageDivs[m].css("visibility", "")
    }
};

sap.m.Carousel.prototype._updatePageWidths = function() {
    if (jQuery.os.android) {
        var p = this.getPages();
        var d = jQuery.sap.domById(this._getContentId());
        var a = d.clientWidth + "px";
        for (var i = 0; i < p.length; i++) {
            if (p[i] instanceof sap.m.Image) {
                jQuery.sap.byId(p[i].getId()).css("max-width", a)
            }
        }
    }
};

sap.m.Carousel.prototype._rerenderPageIndicatorDots = function() {
    this._cleanUpTapBindings();
    var p = jQuery.sap.domById(this._getNavId());
    var r = sap.ui.getCore().createRenderManager();
    sap.m.CarouselRenderer.renderPageIndicatorDots(r, this);
    r.flush(p);
    r.destroy();
    this._createTapBindings()
};

sap.m.Carousel.prototype._rerenderBusinessIndicators = function(w) {
    if ( !! this._oSwipeView) {
        var i;
        for (i = 0; i < 3; i++) {
            var a = jQuery.sap.byId(this.getId() + "-indicator-" + i);
            if (a.length > 0) {
                a.remove()
            }
        }
        var r = sap.ui.getCore().createRenderManager();
        var b;
        for (i = 0; i < 3; i++) {
            b = this._getContentId() + "-MstPgCont-" + i;
            if (w) {
                r.write("<div id='" + b + "' class='sapMCrslContMstPag'></div>")
            }
            var $ = jQuery.sap.domById(this._oSwipeView.masterPages[i].id);
            if (!this._aBusyIndicators[i]) {
                this._aBusyIndicators[i] = new sap.m.BusyIndicator(this.getId() + "-indicator-" + i, {
                    size: this.getBusyIndicatorSize()
                })
            } else {
                this._aBusyIndicators[i].setSize(this.getBusyIndicatorSize())
            }
            if (this.getShowBusyIndicator()) {
                r.renderControl(this._aBusyIndicators[i])
            }
            r.flush($, false, true);
            this._aMasterPageDivs[i] = jQuery.sap.byId(b);
            if (this.getShowBusyIndicator()) {
                this._aBusyIndicators[i].$().addClass("sapMCrslContMstPag");
                this._aBusyIndicators[i].$().css("position", "absolute");
                this._aBusyIndicators[i].$().css("top", "0px")
            }
        }
        r.destroy();
        for (i = 0; i < 3; i++) {
            this._toggleBusyIcon(i, false)
        }
    }
    return this
};