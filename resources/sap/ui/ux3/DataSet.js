﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.ux3.DataSet");
jQuery.sap.require("sap.ui.ux3.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.ux3.DataSet", {
    metadata: {
        publicMethods: ["setLeadSelection", "getLeadSelection", "addToolbarItem", "removeToolbarItem"],
        library: "sap.ui.ux3",
        properties: {
            "showToolbar": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showFilter": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "showSearchField": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "multiSelect": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            }
        },
        aggregations: {
            "items": {
                type: "sap.ui.ux3.DataSetItem",
                multiple: true,
                singularName: "item",
                bindable: "bindable"
            },
            "views": {
                type: "sap.ui.ux3.DataSetView",
                multiple: true,
                singularName: "view"
            },
            "filter": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "filter"
            },
            "_viewSwitches": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "_viewSwitch",
                visibility: "hidden"
            },
            "_toolbar": {
                type: "sap.ui.commons.Toolbar",
                multiple: false,
                visibility: "hidden"
            }
        },
        associations: {
            "selectedView": {
                type: "sap.ui.ux3.DataSetView",
                multiple: false
            }
        },
        events: {
            "selectionChanged": {},
            "search": {}
        }
    }
});
sap.ui.ux3.DataSet.M_EVENTS = {
    'selectionChanged': 'selectionChanged',
    'search': 'search'
};
jQuery.sap.require("sap.ui.core.ResizeHandler");

sap.ui.ux3.DataSet.prototype.init = function() {
    var t = this,
        T;
    jQuery.sap.require("sap.ui.model.SelectionModel");
    this.selectionModel = new sap.ui.model.SelectionModel(sap.ui.model.SelectionModel.SINGLE_SELECTION);
    this._oSegBut = new sap.ui.commons.SegmentedButton();
    this._oSegBut.attachSelect(function(e) {
        t.press(e)
    }, t);
    this._oSegBut.show = false;
    this._oSearchField = new sap.ui.commons.SearchField(this.getId() + "-searchValue");
    this._oSearchField.setShowListExpander(false);
    this._oSearchField.setEnableListSuggest(false);
    this._oSearchField.setEnableFilterMode(true);
    this._oSearchField.setEnableClear(true);
    this._oSearchField.show = false;
    t = this;
    this._oSearchField.attachSearch(function(e) {
        t.fireSearch(e.getParameters())
    });
    T = new sap.ui.commons.Toolbar();
    this._setToolbar(T);
    this._iShiftStart = null
};

sap.ui.ux3.DataSet.prototype.exit = function() {
    this._oSegBut.destroy();
    this._oSearchField.destroy();
    this.destroyAggregation("_toolbar")
};

sap.ui.ux3.DataSet.prototype._prepareToolbar = function() {
    var v = this.getViews().length,
        t = this._getToolbar();
    if (v > 1 && this._oSegBut.show == false) {
        t.insertItem(this._oSegBut, 0);
        this._oSegBut.show = true
    } else if (v <= 1 && this._oSegBut.show) {
        t.removeItem(this._oSegBut);
        this._oSegBut.show = false
    }
    if (this.getShowSearchField() && this._oSearchField.show == false) {
        t.insertRightItem(this._oSearchField, t.getRightItems().length);
        this._oSearchField.show = true
    } else if (!this.getShowSearchField() && this._oSearchField.show == true) {
        t.removeRightItem(this._oSearchField);
        this._oSearchField.show = false
    }
};

sap.ui.ux3.DataSet.prototype.press = function(e, s) {
    var b = e.getParameters().selectedButtonId,
        v = b.substring(b.lastIndexOf('-') + 1),
        o = sap.ui.getCore().byId(this.getSelectedView());
    o.exitView(this.getItems());
    this.setSelectedView(v)
};

sap.ui.ux3.DataSet.prototype.filter = function() {
    this.fireFilter({
        filterValue: this.getFilterValue()
    })
};

sap.ui.ux3.DataSet.prototype.sort = function() {
    this.fireSort()
};

sap.ui.ux3.DataSet.prototype.addSelectionInterval = function(i, I) {
    this.selectionModel.addSelectionInterval(i, I);
    return this
};

sap.ui.ux3.DataSet.prototype.removeSelectionInterval = function(i, I) {
    this.selectionModel.removeSelectionInterval(i, I);
    return this
};

sap.ui.ux3.DataSet.prototype.getSelectedIndex = function() {
    return this.selectionModel.getLeadSelectedIndex()
};

sap.ui.ux3.DataSet.prototype.getSelectedIndices = function() {
    return this.selectionModel.getSelectedIndices() || []
};

sap.ui.ux3.DataSet.prototype.clearSelection = function() {
    this.selectionModel.clearSelection();
    return this
};

sap.ui.ux3.DataSet.prototype.selectItem = function(e) {
    var p = e.getParameters(),
        i = e.getParameters().itemId,
        I = sap.ui.getCore().byId(i),
        a = this.getItems(),
        b = jQuery.inArray(I, a),
        o = this.getLeadSelection();
    if (!this.getMultiSelect()) {
        if (o == b && !p.shift) {
            this.setLeadSelection(-1)
        } else {
            this.setLeadSelection(b)
        }
        this._iShiftStart = null
    } else {
        if (p.ctrl) {
            if (!this.isSelectedIndex(b)) {
                this.addSelectionInterval(b, b)
            } else {
                this.removeSelectionInterval(b, b)
            }
            if (this._iShiftStart >= 0) {
                this._iShiftStart = b
            }
        }
        if (p.shift) {
            if (!this._iShiftStart && this._iShiftStart !== 0) {
                this._iShiftStart = o
            }
            if (!p.ctrl) {
                this.clearSelection()
            }
            if (this._iShiftStart >= 0) {
                this.addSelectionInterval(this._iShiftStart, b)
            } else {
                this.setLeadSelection(b);
                this._iShiftStart = b
            }
        }
        if (!p.shift && !p.ctrl) {
            if (o == b && b != this._iShiftStart) {
                this.setLeadSelection(-1)
            } else {
                this.setLeadSelection(b)
            }
            this._iShiftStart = null
        }
    }
    this.fireSelectionChanged({
        oldLeadSelectedIndex: o,
        newLeadSelectedIndex: b
    })
};

sap.ui.ux3.DataSet.prototype.prepareRendering = function() {
    var v, V = this.getViews().length;
    if (V == 0) {
        return
    }
    this._prepareToolbar();
    if (this._bDirty) {
        v = sap.ui.getCore().byId(this.getSelectedView());
        if (v.exitView) {
            v.exitView(this.getItems())
        }
        if (v.initView) {
            v.initView(this.getItems())
        }
        this._bDirty = false
    }
};

sap.ui.ux3.DataSet.prototype.getLeadSelection = function() {
    return this.selectionModel.getLeadSelectedIndex()
};

sap.ui.ux3.DataSet.prototype.setLeadSelection = function(i) {
    this.selectionModel.setLeadSelectedIndex(i)
};

sap.ui.ux3.DataSet.prototype.isSelectedIndex = function(i) {
    return (this.selectionModel.isSelectedIndex(i))
};

sap.ui.ux3.DataSet.prototype.getSelectedItemId = function(i) {
    return this.getItems()[i].getId()
};

sap.ui.ux3.DataSet.prototype.createViewSwitch = function(v, i) {
    var t = this,
        V;
    if (v.getIcon()) {
        V = new sap.ui.commons.Button({
            id: this.getId() + "-view-" + v.getId(),
            lite: true,
            icon: v.getIcon(),
            iconHovered: v.getIconHovered(),
            iconSelected: v.getIconSelected()
        })
    } else if (v.getName()) {
        V = new sap.ui.commons.Button({
            id: this.getId() + "-view-" + v.getId(),
            text: v.getName(),
            lite: true
        })
    } else {
        V = new sap.ui.commons.Button({
            id: this.getId() + "-view-" + v.getId(),
            text: v.getId(),
            lite: true
        })
    }
    V._viewIndex = i;
    return V
};

sap.ui.ux3.DataSet.prototype._rerenderToolbar = function() {
    var $ = jQuery.sap.byId(this.getId() + "-toolbar");
    this._prepareToolbar();
    if ($.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        sap.ui.ux3.DataSetRenderer.renderToolbar(r, this);
        r.flush($[0]);
        r.destroy()
    }
};

sap.ui.ux3.DataSet.prototype._rerenderFilter = function() {
    var $ = jQuery.sap.byId(this.getId() + "-filter");
    if ($.length > 0) {
        var r = sap.ui.getCore().createRenderManager();
        sap.ui.ux3.DataSetRenderer.renderFilterArea(r, this);
        r.flush($[0]);
        if (this.getShowFilter()) {
            $.removeClass("noPadding")
        } else {
            $.addClass("noPadding")
        }
        r.destroy()
    }
};

sap.ui.ux3.DataSet.prototype.setMultiSelect = function(m) {
    this.clearSelection();
    if (!m) {
        this.setProperty("multiSelect", false);
        if ( !! this.selectionModel) {
            this.selectionModel.setSelectionMode(sap.ui.model.SelectionModel.SINGLE_SELECTION)
        }
    } else {
        this.setProperty("multiSelect", true);
        if ( !! this.selectionModel) {
            this.selectionModel.setSelectionMode(sap.ui.model.SelectionModel.MULTI_SELECTION)
        }
    };
    return this
};

sap.ui.ux3.DataSet.prototype.removeItem = function(i) {
    var r = this.removeAggregation("items", i);
    if (r) {
        r.detachSelected(this.selectItem, this);
        r.destroyAggregation("_template", true);
        this._bDirty = true
    }
    return r
};

sap.ui.ux3.DataSet.prototype.removeAllItems = function() {
    var I = this.getItems(),
        r;
    jQuery.each(I, function(i, o) {
        o.destroyAggregation("_template", true);
        o.detachSelected(this.selectItem, this)
    });
    r = this.removeAllAggregation("items");
    this._bDirty = true;
    return r
};

sap.ui.ux3.DataSet.prototype.destroyItems = function() {
    var r = this.destroyAggregation("items");
    this._bDirty = true;
    return r
};

sap.ui.ux3.DataSet.prototype.addItem = function(i) {
    this.addAggregation("items", i);
    i.attachSelected(this.selectItem, this);
    this._bDirty = true;
    return this
};

sap.ui.ux3.DataSet.prototype.insertItem = function(i, I) {
    this.insertAggregation("items", i, I);
    i.attachSelected(this.selectItem, this);
    this._bDirty = true;
    return this
};

sap.ui.ux3.DataSet.prototype.setFilterValue = function(f) {
    this.setProperty("filterValue", f, true);
    return this
};

sap.ui.ux3.DataSet.prototype.getFilterValue = function() {
    return this.getProperty("filterValue")
};

sap.ui.ux3.DataSet.prototype.insertView = function(v, i) {
    var V = this.createViewSwitch(v, i, true);
    if (!this.getSelectedView()) {
        this.setSelectedView(v)
    }
    this.insertAggregation("views", v, i);
    this._oSegBut.insertButton(V, i);
    this._rerenderToolbar();
    return this
};

sap.ui.ux3.DataSet.prototype.addView = function(v) {
    var i = this.getViews().length,
        V = this.createViewSwitch(v, i);
    if (!this.getSelectedView()) {
        this.setSelectedView(v)
    }
    this.addAggregation("views", v, true);
    this._oSegBut.addButton(V);
    this._rerenderToolbar();
    return this
};

sap.ui.ux3.DataSet.prototype.removeView = function(v) {
    var r = this.removeAggregation("views", v, true);
    if (r) {
        if (this.getSelectedView() == r.getId()) {
            this.setSelectedView(this.getViews()[0]);
            this._bDirty = true;
            r.invalidate()
        } else {
            this._rerenderToolbar()
        }
        this._oSegBut.removeButton(this.getId() + "-view-" + r.getId()).destroy()
    }
    return r
};

sap.ui.ux3.DataSet.prototype.destroyViews = function() {
    this._oSegBut.destroyButtons();
    this.destroyAggregation("views");
    return this
};

sap.ui.ux3.DataSet.prototype.removeAllViews = function() {
    var r = this.removeAllAggregation("views");
    this._oSegBut.destroyButtons();
    return r
};

sap.ui.ux3.DataSet.prototype.setEnableSorting = function(e) {
    this.setProperty("enableSorting", e, true);
    this._rerenderToolbar();
    return this
};

sap.ui.ux3.DataSet.prototype.setEnableFiltering = function(e) {
    this.setProperty("enableFiltering", e, true);
    this._rerenderToolbar();
    return this
};

sap.ui.ux3.DataSet.prototype.setSelectedView = function(v) {
    var o = this.getSelectedView();
    this.setAssociation("selectedView", v);
    if (o != this.getSelectedView()) {
        this._bDirty = true
    }
    if (this.getId() + "-view-" + this.getSelectedView() !== this._oSegBut.getSelectedButton()) {
        this._oSegBut.setSelectedButton(this.getId() + "-view-" + this.getSelectedView())
    }
    return this
};

sap.ui.ux3.DataSet.prototype.addToolbarItem = function(t) {
    this._getToolbar().addItem(t);
    this._rerenderToolbar()
};

sap.ui.ux3.DataSet.prototype.removeToolbarItem = function(t) {
    this._getToolbar().removeItem(t);
    this._rerenderToolbar()
};

sap.ui.ux3.DataSet.prototype.setShowToolbar = function(s) {
    this.setProperty("showToolbar", s, true);
    this._rerenderToolbar()
};

sap.ui.ux3.DataSet.prototype.setShowFilter = function(s) {
    this.setProperty("showFilter", s, true);
    this._rerenderFilter()
};

sap.ui.ux3.DataSet.prototype.setShowSearchField = function(s) {
    this.setProperty("showSearchField", s, true);
    this._rerenderToolbar()
};

sap.ui.ux3.DataSet.prototype._setToolbar = function(t) {
    this.setAggregation("_toolbar", t, true);
    this._rerenderToolbar()
};

sap.ui.ux3.DataSet.prototype._getToolbar = function() {
    return this.getAggregation("_toolbar")
};

sap.ui.ux3.DataSet.prototype.updateItems = function(f) {
    var b = this.mBindingInfos["items"],
        a = this.getMetadata().getJSONKeys()["items"],
        s = sap.ui.getCore().byId(this.getSelectedView()),
        B = b.binding,
        F = b.factory,
        c, I, o, d, t = this,
        C = [];
    B.bUseExtendedChangeDetection = true;
    if (s && s.getItemCount && s.getItemCount()) {
        var e = Math.max(s.getItemCount(), this.getItems().length);
        if (e) {
            C = B.getContexts(0, e)
        } else {
            C = B.getContexts()
        }
    } else {
        C = B.getContexts()
    }
    if (C.diff && f !== true) {
        var D = C.diff;
        for (var i = 0; i < D.length; i++) {
            I = this.getItems();
            d = D[i].index;
            if (D[i].type === "delete") {
                o = I[d];
                D[i].item = o;
                this.removeItem(o)
            } else if (C.diff[i].type === "insert") {
                o = F("", C[d]);
                o.setBindingContext(C[d], b.model);
                D[i].item = o;
                this.insertItem(o, d)
            }
        }
        if (s && s.updateView) {
            s.updateView(D)
        }
    } else {
        this[a._sDestructor]();
        jQuery.each(C, function(d, g) {
            var h = t.getId() + "-" + d;
            c = F(h, g);
            c.setBindingContext(g, b.model);
            t[a._sMutator](c)
        })
    }
};