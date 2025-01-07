﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.table.Table");
jQuery.sap.require("sap.ui.table.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.table.Table", {
    metadata: {
        publicMethods: ["getSelectedIndices", "addSelectionInterval", "setSelectionInterval", "removeSelectionInterval", "isIndexSelected", "clearSelection", "selectAll", "getContextByIndex", "sort", "filter"],
        library: "sap.ui.table",
        properties: {
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: 'auto'
            },
            "rowHeight": {
                type: "int",
                group: "Appearance",
                defaultValue: null
            },
            "columnHeaderHeight": {
                type: "int",
                group: "Appearance",
                defaultValue: null
            },
            "columnHeaderVisible": {
                type: "boolean",
                group: "Appearance",
                defaultValue: true
            },
            "visibleRowCount": {
                type: "int",
                group: "Appearance",
                defaultValue: 10
            },
            "firstVisibleRow": {
                type: "int",
                group: "Appearance",
                defaultValue: 0
            },
            "selectionMode": {
                type: "sap.ui.table.SelectionMode",
                group: "Behavior",
                defaultValue: sap.ui.table.SelectionMode.Multi
            },
            "selectionBehavior": {
                type: "sap.ui.table.SelectionBehavior",
                group: "Behavior",
                defaultValue: sap.ui.table.SelectionBehavior.RowSelector
            },
            "selectedIndex": {
                type: "int",
                group: "Appearance",
                defaultValue: -1
            },
            "allowColumnReordering": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true,
                deprecated: true
            },
            "editable": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "visible": {
                type: "boolean",
                group: "Appearance",
                defaultValue: true
            },
            "navigationMode": {
                type: "sap.ui.table.NavigationMode",
                group: "Behavior",
                defaultValue: sap.ui.table.NavigationMode.Scrollbar
            },
            "threshold": {
                type: "int",
                group: "",
                defaultValue: 100
            },
            "enableColumnReordering": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "enableGrouping": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "showColumnVisibilityMenu": {
                type: "boolean",
                group: "Misc",
                defaultValue: false
            },
            "showNoData": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "visibleRowCountMode": {
                type: "sap.ui.table.VisibleRowCountMode",
                group: "Appearance",
                defaultValue: sap.ui.table.VisibleRowCountMode.Fixed
            },
            "fixedColumnCount": {
                type: "int",
                group: "Appearance",
                defaultValue: 0
            },
            "fixedRowCount": {
                type: "int",
                group: "Appearance",
                defaultValue: 0
            },
            "minAutoRowCount": {
                type: "int",
                group: "Appearance",
                defaultValue: 5
            }
        },
        defaultAggregation: "columns",
        aggregations: {
            "title": {
                type: "sap.ui.core.Control",
                altTypes: ["string"],
                multiple: false
            },
            "footer": {
                type: "sap.ui.core.Control",
                altTypes: ["string"],
                multiple: false
            },
            "toolbar": {
                type: "sap.ui.commons.Toolbar",
                multiple: false
            },
            "extension": {
                type: "sap.ui.core.Control",
                multiple: true,
                singularName: "extension"
            },
            "columns": {
                type: "sap.ui.table.Column",
                multiple: true,
                singularName: "column",
                bindable: "bindable"
            },
            "rows": {
                type: "sap.ui.table.Row",
                multiple: true,
                singularName: "row",
                bindable: "bindable"
            },
            "noData": {
                type: "sap.ui.core.Control",
                multiple: false
            }
        },
        associations: {
            "groupBy": {
                type: "sap.ui.table.Column",
                multiple: false
            }
        },
        events: {
            "rowSelectionChange": {},
            "columnSelect": {
                allowPreventDefault: true
            },
            "columnResize": {
                allowPreventDefault: true
            },
            "columnMove": {
                allowPreventDefault: true
            },
            "sort": {
                allowPreventDefault: true
            },
            "filter": {
                allowPreventDefault: true
            },
            "group": {
                allowPreventDefault: true
            },
            "columnVisibility": {
                allowPreventDefault: true
            }
        }
    }
});
sap.ui.table.Table.M_EVENTS = {
    'rowSelectionChange': 'rowSelectionChange',
    'columnSelect': 'columnSelect',
    'columnResize': 'columnResize',
    'columnMove': 'columnMove',
    'sort': 'sort',
    'filter': 'filter',
    'group': 'group',
    'columnVisibility': 'columnVisibility'
};
jQuery.sap.require("sap.ui.model.SelectionModel");
jQuery.sap.require("sap.ui.core.delegate.ItemNavigation");
jQuery.sap.require("sap.ui.core.theming.Parameters");
jQuery.sap.require("sap.ui.core.ScrollBar");
jQuery.sap.require("sap.ui.core.IntervalTrigger");
jQuery.sap.require("sap.ui.commons.TextView");
jQuery.sap.require("sap.ui.table.Row");
sap.ui.table.Table.ResizeTrigger = new sap.ui.core.IntervalTrigger(300);

sap.ui.table.Table.prototype.init = function() {
    this._oResBundle = sap.ui.getCore().getLibraryResourceBundle("sap.ui.table");
    this._bAccMode = sap.ui.getCore().getConfiguration().getAccessibility();
    this._bRtlMode = sap.ui.getCore().getConfiguration().getRTL();
    this._oSelection = new sap.ui.model.SelectionModel(sap.ui.model.SelectionModel.MULTI_SELECTION);
    this._oSelection.attachSelectionChanged(this._onSelectionChanged, this);
    this._iColMinWidth = 20;
    this._aIdxCols2Cells = [];
    this._aVisibleColumns = [];
    var f = {
        onAfterRendering: function(e) {
            jQuery.sap.byId(e.srcControl.getId() + "-sb").attr("tabindex", "-1").css("outline", "none")
        }
    };
    this._oVSb = new sap.ui.core.ScrollBar(this.getId() + "-vsb", {
        size: "100%"
    });
    this._oVSb.attachScroll(this.onvscroll, this);
    this._oVSb.addDelegate(f);
    this._oHSb = new sap.ui.core.ScrollBar(this.getId() + "-hsb", {
        size: "100%",
        contentSize: "0px",
        vertical: false
    });
    this._oHSb.attachScroll(this.onhscroll, this);
    this._oHSb.addDelegate(f);
    this._bActionMode = false;
    this._iLastFixedColIndex = -1;
    this._bInheritEditableToControls = false;
    this._bAllowColumnHeaderTextSelection = false;
    this._bCallUpdateTableCell = false;
    this._iTimerDelay = 250;
    this._bjQueryLess18 = jQuery.sap.Version(jQuery.fn.jquery).compareTo("1.8") < 0
};

sap.ui.table.Table.prototype.exit = function() {
    this._oVSb.destroy();
    this._oHSb.destroy();
    if (this._oPaginator) {
        this._oPaginator.destroy()
    }
    this._destroyItemNavigation();
    this._cleanUpTimers();
    this._detachEvents()
};

sap.ui.table.Table.prototype.onThemeChanged = function() {
    if (this.getDomRef()) {
        this.invalidate()
    }
};

sap.ui.table.Table.prototype.onBeforeRendering = function() {
    this._cleanUpTimers();
    this._detachEvents()
};

sap.ui.table.Table.prototype.onAfterRendering = function() {
    this._bOnAfterRendering = true;
    var $ = this.$();
    this._updateVSb();
    this._updateTableContent();
    this._handleResize();
    this._attachEvents();
    var c = this.getColumns();
    for (var i = 0, l = c.length; i < l; i++) {
        if (c[i].getVisible()) {
            c[i]._restoreIcons()
        }
    }
    if (!this._bAllowColumnHeaderTextSelection) {
        this._disableTextSelection($.find(".sapUiTableColHdrCnt"))
    }
    this._bOnAfterRendering = false;
    this._initItemNavigation()
};

sap.ui.table.Table.prototype._updateTableContent = function() {
    this._updateNoData();
    this._updateSelection();
    if (this._modifyRow) {
        var t = this;
        jQuery.each(this.getRows(), function(i, r) {
            t._modifyRow(i + t.getFirstVisibleRow(), r.$());
            t._modifyRow(i + t.getFirstVisibleRow(), jQuery.sap.byId(r.getId() + "-fixed"))
        })
    }
    this._updateRowHeader();
    if (this._bOnAfterRendering && this._bCallUpdateTableCell) {
        jQuery.each(this.getRows(), function(i, r) {
            jQuery.each(r.getCells(), function(i, c) {
                if (c._updateTableCell) {
                    c._updateTableCell(c, c.getBindingContext(), c.$().closest("td"))
                }
            })
        })
    }
};

sap.ui.table.Table.prototype._initItemNavigation = function() {
    var $ = this.$();
    var c = this._getVisibleColumnCount();
    var h = false;
    if (!this._oColHdrItemNav) {
        this._oColHdrItemNav = new sap.ui.core.delegate.ItemNavigation();
        this._oColHdrItemNav.setCycling(false);
        this.addDelegate(this._oColHdrItemNav)
    }
    var I = [];
    if (this.getFixedColumnCount() == 0) {
        I = $.find(".sapUiTableCtrl td[tabindex]").get()
    } else {
        var a = this.$().find('.sapUiTableCtrlFixed.sapUiTableCtrlRowFixed');
        var b = this.$().find('.sapUiTableCtrlScroll.sapUiTableCtrlRowFixed');
        var d = this.$().find('.sapUiTableCtrlFixed.sapUiTableCtrlRowScroll');
        var e = this.$().find('.sapUiTableCtrlScroll.sapUiTableCtrlRowScroll');
        for (var i = 0; i < this.getVisibleRowCount(); i++) {
            I = I.concat(a.find('tr[data-sap-ui-rowindex="' + i + '"]').find('td[tabindex]').get());
            I = I.concat(b.find('tr[data-sap-ui-rowindex="' + i + '"]').find('td[tabindex]').get());
            I = I.concat(d.find('tr[data-sap-ui-rowindex="' + i + '"]').find('td[tabindex]').get());
            I = I.concat(e.find('tr[data-sap-ui-rowindex="' + i + '"]').find('td[tabindex]').get())
        }
    }
    if (this.getSelectionMode() !== sap.ui.table.SelectionMode.None && this.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowOnly) {
        var r = $.find(".sapUiTableRowHdr").get();
        for (var i = r.length - 1; i >= 0; i--) {
            I.splice(i * c, 0, r[i])
        }
        c++;
        h = true
    }
    if (this.getColumnHeaderVisible()) {
        I = $.find(".sapUiTableCol").get().concat(I)
    }
    if (this.getSelectionMode() !== sap.ui.table.SelectionMode.None && this.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowOnly && this.getColumnHeaderVisible()) {
        I = $.find(".sapUiTableColRowHdr").get().concat(I)
    }
    if (!this._oItemNavigation) {
        this._oItemNavigation = new sap.ui.core.delegate.ItemNavigation();
        this._oItemNavigation.setTableMode(true);
        this._oItemNavigation.attachEvent(sap.ui.core.delegate.ItemNavigation.Events.BeforeFocus, function(E) {
            jQuery.sap.byId(this.getId() + "-ariadesc").text("")
        }, this);
        this.addDelegate(this._oItemNavigation)
    }
    this._oItemNavigation.setColumns(c);
    this._oItemNavigation.setRootDomRef($.find(".sapUiTableCnt").get(0));
    this._oItemNavigation.setItemDomRefs(I);
    this._oItemNavigation.setFocusedIndex((this.getColumnHeaderVisible() ? c : 0) + (h ? 1 : 0))
};

sap.ui.table.Table.prototype._destroyItemNavigation = function() {
    if (this._oItemNavigation) {
        this._oItemNavigation.destroy();
        this._oItemNavigation = undefined
    }
};

sap.ui.table.Table.prototype.getFocusInfo = function() {
    var i = this.$().find(":focus").attr("id");
    if (i) {
        return {
            customId: i
        }
    } else {
        return sap.ui.core.Element.prototype.getFocusInfo.apply(this, arguments)
    }
};

sap.ui.table.Table.prototype.applyFocusInfo = function(f) {
    if (f && f.customId) {
        this.$().find("#" + f.customId).focus()
    } else {
        sap.ui.core.Element.prototype.getFocusInfo.apply(this, arguments)
    }
    return this
};

sap.ui.table.Table.prototype.setTitle = function(t) {
    var T = t;
    if (typeof(t) === "string" || t instanceof String) {
        T = new sap.ui.commons.TextView({
            text: t,
            wrapping: false,
            width: "100%"
        }).addStyleClass("sapUiTableHdrTitle")
    }
    this.setAggregation("title", T);
    return this
};

sap.ui.table.Table.prototype.setFooter = function(f) {
    var F = f;
    if (typeof(f) === "string" || f instanceof String) {
        F = new sap.ui.commons.TextView({
            text: f,
            wrapping: false,
            width: "100%"
        })
    }
    this.setAggregation("footer", F);
    return this
};

sap.ui.table.Table.prototype.setSelectionMode = function(s) {
    this._oSelection.clearSelection();
    if (s === sap.ui.table.SelectionMode.Single) {
        this._oSelection.setSelectionMode(sap.ui.model.SelectionModel.SINGLE_SELECTION)
    } else {
        this._oSelection.setSelectionMode(sap.ui.model.SelectionModel.MULTI_SELECTION)
    }
    this.setProperty("selectionMode", s);
    return this
};

sap.ui.table.Table.prototype.setFirstVisibleRow = function(r, o) {
    this.setProperty("firstVisibleRow", r, true);
    if (this.getBinding("rows")) {
        this.updateRows()
    }
    return this
};

sap.ui.table.Table.prototype.getAllowColumnReordering = function() {
    jQuery.sap.log.warning("getAllowColumnReordering is deprecated - please use getEnableColumnReordering!");
    return sap.ui.table.Table.prototype.getEnableColumnReordering.apply(this, arguments)
};

sap.ui.table.Table.prototype.setAllowColumnReordering = function() {
    jQuery.sap.log.warning("setAllowColumnReordering is deprecated - please use setEnableColumnReordering!");
    return sap.ui.table.Table.prototype.setEnableColumnReordering.apply(this, arguments)
};

sap.ui.table.Table.getMetadata().getAllAggregations()["rows"]._doesNotRequireFactory = true;

sap.ui.table.Table.prototype.bindRows = function(b, t, s, f) {
    if (typeof b === "string" && (t instanceof sap.ui.model.Sorter || jQuery.isArray(s) && s[0] instanceof sap.ui.model.Filter)) {
        f = s;
        s = t;
        t = undefined
    }
    return this.bindAggregation("rows", b, t, s, f)
};

sap.ui.table.Table.prototype._bindAggregation = function(n, p, t, s, f) {
    sap.ui.core.Element.prototype._bindAggregation.apply(this, arguments);
    var b = this.getBinding("rows");
    if (n === "rows" && b) {
        b.attachChange(this._onBindingChange, this)
    }
    return this
};

sap.ui.table.Table.prototype.unbindAggregation = function(n, s) {
    var b = this.getBinding("rows");
    if (n === "rows" && b) {
        b.detachChange(this._onBindingChange);
        s = true
    }
    this.updateRows();
    return sap.ui.core.Element.prototype.unbindAggregation.apply(this, [n, s])
};

sap.ui.table.Table.prototype.setVisibleRowCountMode = function(v) {
    this.setProperty("visibleRowCountMode", v);
    this._handleRowCountMode();
    return this
};

sap.ui.table.Table.prototype.setVisibleRowCount = function(v) {
    v = this.validateProperty("visibleRowCount", v);
    if (this.getBinding("rows") && this.getBinding("rows").getLength() <= v) {
        this.setProperty("firstVisibleRow", 0)
    }
    this.setProperty("visibleRowCount", v);
    return this
};

sap.ui.table.Table.prototype.updateRows = function() {
    var s = this.getFirstVisibleRow();
    s = Math.max(s, 0);
    if (this.getNavigationMode() === sap.ui.table.NavigationMode.Scrollbar) {
        s = Math.min(s, Math.max(this._getRowCount() - this.getVisibleRowCount(), 0))
    }
    this.setProperty("firstVisibleRow", s, true);
    if (this._oVSb.getScrollPosition() !== s) {
        this._oVSb.setScrollPosition(s)
    }
    if (this._oPaginator && this.getNavigationMode() === sap.ui.table.NavigationMode.Paginator) {
        var n = 1;
        if (s < this.getBinding("rows").getLength()) {
            n = Math.ceil((s + 1) / this.getVisibleRowCount())
        }
        if (n !== this._oPaginator.getCurrentPage()) {
            this.setProperty("firstVisibleRow", (n - 1) * this.getVisibleRowCount(), true);
            this._oPaginator.setCurrentPage(n);
            if (this._oPaginator.getDomRef()) {
                this._oPaginator.rerender()
            }
        }
    }
    if (this.getDomRef()) {
        this._sBindingTimer = this._sBindingTimer || jQuery.sap.delayedCall(50, this, function() {
            if (!this.bIsDestroyed) {
                this._determineVisibleCols();
                this._updateVSb();
                this._updateBindingContexts();
                this._updateTableContent();
                this._sBindingTimer = undefined
            }
        })
    }
};

sap.ui.table.Table.prototype._onBindingChange = function(e) {
    var r = e.getParameter("reason");
    if (r === "sort" || r === "filter") {
        this.clearSelection();
        this.setFirstVisibleRow(0)
    }
};

sap.ui.table.Table.prototype.insertRow = function() {
    jQuery.sap.log.error("The control manages the rows aggregation. The method \"insertRow\" cannot be used programmatically!")
};

sap.ui.table.Table.prototype.addRow = function() {
    jQuery.sap.log.error("The control manages the rows aggregation. The method \"addRow\" cannot be used programmatically!")
};

sap.ui.table.Table.prototype.removeRow = function() {
    jQuery.sap.log.error("The control manages the rows aggregation. The method \"removeRow\" cannot be used programmatically!")
};

sap.ui.table.Table.prototype.removeAllRows = function() {
    jQuery.sap.log.error("The control manages the rows aggregation. The method \"removeAllRows\" cannot be used programmatically!")
};

sap.ui.table.Table.prototype.destroyRows = function() {
    jQuery.sap.log.error("The control manages the rows aggregation. The method \"destroyRows\" cannot be used programmatically!")
};

sap.ui.table.Table.prototype._attachEvents = function() {
    var $ = this.$();
    $.find(".sapUiTableColHdrScr").scroll(jQuery.proxy(this._oncolscroll, this));
    $.find(".sapUiTableCtrlScr").scroll(jQuery.proxy(this._oncntscroll, this));
    $.find(".sapUiTableCtrlScrFixed").scroll(jQuery.proxy(this._oncntscroll, this));
    $.find(".sapUiTableRowHdr").hover(function() {
        jQuery(this).addClass("sapUiTableRowHvr");
        var i = $.find(".sapUiTableRowHdr").index(this);
        $.find(".sapUiTableCtrlFixed > tbody > tr").filter(":eq(" + i + ")").addClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlScroll > tbody > tr").filter(":eq(" + i + ")").addClass("sapUiTableRowHvr")
    }, function() {
        jQuery(this).removeClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlFixed > tbody > tr").removeClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlScroll > tbody > tr").removeClass("sapUiTableRowHvr")
    });
    $.find(".sapUiTableCtrlFixed > tbody > tr").hover(function() {
        jQuery(this).addClass("sapUiTableRowHvr");
        var i = $.find(".sapUiTableCtrlFixed > tbody > tr").index(this);
        $.find(".sapUiTableRowHdr").filter(":eq(" + (i) + ")").addClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlScroll > tbody > tr").filter(":eq(" + i + ")").addClass("sapUiTableRowHvr")
    }, function() {
        jQuery(this).removeClass("sapUiTableRowHvr");
        $.find(".sapUiTableRowHdr").removeClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlScroll > tbody > tr").removeClass("sapUiTableRowHvr")
    });
    $.find(".sapUiTableCtrlScroll > tbody > tr").hover(function() {
        jQuery(this).addClass("sapUiTableRowHvr");
        var i = $.find(".sapUiTableCtrlScroll > tbody > tr").index(this);
        $.find(".sapUiTableRowHdr").filter(":eq(" + i + ")").addClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlFixed > tbody > tr").filter(":eq(" + i + ")").addClass("sapUiTableRowHvr")
    }, function() {
        jQuery(this).removeClass("sapUiTableRowHvr");
        $.find(".sapUiTableRowHdr").removeClass("sapUiTableRowHvr");
        $.find(".sapUiTableCtrlFixed > tbody > tr").removeClass("sapUiTableRowHvr")
    });
    $.find(".sapUiTableColRsz").mousedown(jQuery.proxy(this._onColumnResizeStart, this));
    sap.ui.table.Table.ResizeTrigger.addListener(this._checkTableSize, this);
    this._oHSb.bind($.find(".sapUiTableCtrlScr").get(0));
    this._oVSb.bind($.find(".sapUiTableCtrlScr").get(0));
    this._oHSb.bind($.find(".sapUiTableCtrlScrFixed").get(0));
    this._oVSb.bind($.find(".sapUiTableCtrlScrFixed").get(0))
};

sap.ui.table.Table.prototype._detachEvents = function() {
    var $ = this.$();
    $.find(".sapUiTableRowHdrScr").unbind();
    $.find(".sapUiTableColHdrScr").unbind();
    $.find(".sapUiTableCtrl > tbody > tr").unbind();
    $.find(".sapUiTableRowHdr").unbind();
    sap.ui.table.Table.ResizeTrigger.removeListener(this._checkTableSize, this);
    $.find(".sapUiTableColRsz").unbind();
    this._oHSb.unbind($.find(".sapUiTableCtrlScr").get(0));
    this._oVSb.unbind($.find(".sapUiTableCtrlScr").get(0));
    this._oHSb.unbind($.find(".sapUiTableCtrlScrFixed").get(0));
    this._oVSb.unbind($.find(".sapUiTableCtrlScrFixed").get(0))
};

sap.ui.table.Table.prototype._cleanUpTimers = function() {
    if (this._sBindingTimer) {
        jQuery.sap.clearDelayedCall(this._sBindingTimer);
        this._sBindingTimer = undefined
    }
    if (this._sDelayedMenuTimer) {
        jQuery.sap.clearDelayedCall(this._sDelayedMenuTimer);
        this._sDelayedMenuTimer = undefined
    }
    if (this._sDelayedActionTimer) {
        jQuery.sap.clearDelayedCall(this._sDelayedActionTimer);
        this._sDelayedActionTimer = undefined
    }
    if (this._sColHdrPosTimer) {
        jQuery.sap.clearDelayedCall(this._sColHdrPosTimer);
        this._sColHdrPosTimer = undefined
    }
    if (this._visibleRowCountTimer) {
        jQuery.sap.clearDelayedCall(this._visibleRowCountTimer);
        this._visibleRowCountTimer = undefined
    }
    sap.ui.table.Table.ResizeTrigger.removeListener(this._checkTableSize, this)
};

sap.ui.table.Table.prototype._createRows = function(s) {
    var f = this.getFirstVisibleRow();
    var v = this.getVisibleRowCount();
    s = s === undefined ? f : s;
    var t = new sap.ui.table.Row(this.getId() + "-rows");
    var c = this.getColumns();
    var C = 0;
    for (var i = 0, l = c.length; i < l; i++) {
        if (c[i].getVisible()) {
            var o = c[i].getTemplate();
            if (o) {
                var a = o.clone("col" + i);
                if (this._bInheritEditableToControls && !this.getEditable() && a.setEditable) {
                    a.setEditable(false)
                }
                a.data("sap-ui-colindex", i);
                t.addCell(a);
                this._aIdxCols2Cells[i] = C++
            }
        }
    }
    this.destroyAggregation("rows");
    var b = undefined;
    var B = this.getBinding("rows");
    var d = this.mBindingInfos["rows"];
    if (B) {
        var T = this.getThreshold() ? Math.max(this.getVisibleRowCount(), this.getThreshold()) : 0;
        b = B.getContexts(s, v, T)
    }
    for (var i = 0; i < v; i++) {
        var a = t.clone("row" + i);
        if (b && b[i]) {
            a.setBindingContext(b[i], d.model);
            a._bHidden = false
        } else {
            a._bHidden = true
        }
        this.addAggregation("rows", a, true)
    }
    t.destroy()
};

sap.ui.table.Table.prototype._updateHSb = function() {
    var $ = this.$();
    var c = $.find(".sapUiTableCtrlScroll").width();
    if ( !! sap.ui.Device.browser.safari) {
        c = Math.max(c, this._getColumnsWidth(this.getFixedColumnCount()))
    }
    if (c > $.find(".sapUiTableCtrlScr").width()) {
        if (!$.hasClass("sapUiTableHScr")) {
            $.addClass("sapUiTableHScr");
            if ( !! sap.ui.Device.browser.safari) {
                if (this._bjQueryLess18) {
                    $.find(".sapUiTableCtrlScroll, .sapUiTableColHdrScr > .sapUiTableColHdr").width(c)
                } else {
                    $.find(".sapUiTableCtrlScroll, .sapUiTableColHdrScr > .sapUiTableColHdr").outerWidth(c)
                }
            }
        }
        var s = $.find(".sapUiTableCtrlFixed").width();
        if ($.find(".sapUiTableRowHdrScr:visible").length > 0) {
            s += $.find(".sapUiTableRowHdrScr").width()
        }
        if (this._bRtlMode) {
            $.find(".sapUiTableHSb").css('padding-right', s + 'px')
        } else {
            $.find(".sapUiTableHSb").css('padding-left', s + 'px')
        }
        this._oHSb.setContentSize(c + "px");
        if (this._oHSb.getDomRef()) {
            this._oHSb.rerender()
        }
    } else {
        if ($.hasClass("sapUiTableHScr")) {
            $.removeClass("sapUiTableHScr");
            if ( !! sap.ui.Device.browser.safari) {
                $.find(".sapUiTableCtrlScroll, .sapUiTableColHdr").css("width", "")
            }
        }
    }
    this._syncHeaderAndContent()
};

sap.ui.table.Table.prototype._updateVSb = function() {
    var $ = this.$();
    var d = false;
    var b = this.getBinding("rows");
    if (b) {
        var s = Math.max(0, (b.getLength() || 0) - this.getVisibleRowCount());
        if (this._oPaginator && this.getNavigationMode() === sap.ui.table.NavigationMode.Paginator) {
            var n = Math.ceil((b.getLength() || 0) / this.getVisibleRowCount());
            this._oPaginator.setNumberOfPages(n);
            var p = Math.min(n, Math.ceil((this.getFirstVisibleRow() + 1) / this.getVisibleRowCount()));
            this.setProperty("firstVisibleRow", (Math.max(p, 1) - 1) * this.getVisibleRowCount(), true);
            this._oPaginator.setCurrentPage(p);
            if (this._oPaginator.getDomRef()) {
                this._oPaginator.rerender()
            }
            if ($.hasClass("sapUiTableVScr")) {
                $.removeClass("sapUiTableVScr")
            }
        } else {
            if (s > 0) {
                if (!$.hasClass("sapUiTableVScr")) {
                    $.addClass("sapUiTableVScr");
                    d = true
                }
            } else {
                if ($.hasClass("sapUiTableVScr")) {
                    $.removeClass("sapUiTableVScr");
                    d = true
                }
            }
        }
        if (s !== this._oVSb.getSteps() || this.getFirstVisibleRow() !== this._oVSb.getScrollPosition()) {
            this._oVSb.setSteps(s);
            if (this._oVSb.getDomRef()) {
                this._oVSb.rerender()
            }
            this._oVSb.setScrollPosition(this.getFirstVisibleRow())
        }
    } else {
        if (this._oPaginator && this.getNavigationMode() === sap.ui.table.NavigationMode.Paginator) {
            this._oPaginator.setNumberOfPages(0);
            this._oPaginator.setCurrentPage(0);
            if (this._oPaginator.getDomRef()) {
                this._oPaginator.rerender()
            }
        } else {
            if ($.hasClass("sapUiTableVScr")) {
                $.removeClass("sapUiTableVScr");
                d = true
            }
        }
    }
    if (d && !this._bOnAfterRendering) {
        this._handleResize()
    }
};

sap.ui.table.Table.prototype._updateBindingContexts = function() {
    var r = this.getRows(),
        b = this.getBinding("rows"),
        B = this.mBindingInfos["rows"],
        f = undefined,
        c = undefined,
        F = this.getFixedRowCount();
    if (b) {
        var t;
        if (F > 0) {
            t = this.getThreshold() ? Math.max((this.getVisibleRowCount() - F), this.getThreshold()) : 0;
            c = b.getContexts(this.getFirstVisibleRow() + F, r.length - F, t);
            f = b.getContexts(0, F);
            c = f.concat(c)
        } else {
            t = this.getThreshold() ? Math.max((this.getVisibleRowCount() - F), this.getThreshold()) : 0;
            c = b.getContexts(this.getFirstVisibleRow(), r.length, Math.max(this.getVisibleRowCount(), this.getThreshold()))
        }
    }
    for (var i = r.length - 1; i >= 0; i--) {
        var C = c ? c[i] : undefined;
        var R = r[i];
        if (R) {
            this._updateRowBindingContext(R, C, B && B.model)
        }
    }
};

sap.ui.table.Table.prototype._updateRowBindingContext = function(r, c, m) {
    var C = r.getCells();
    var $ = r.$();
    var a = jQuery.sap.byId(r.getId() + "-fixed");
    if (c && c instanceof sap.ui.model.Context) {
        for (var i = 0, l = this._aVisibleColumns.length; i < l; i++) {
            var b = this._aIdxCols2Cells[this._aVisibleColumns[i]];
            if (C[b]) {
                this._updateCellBindingContext(C[b], c, m)
            }
        }
        if ($.hasClass("sapUiTableRowHidden")) {
            $.removeClass("sapUiTableRowHidden");
            a.removeClass("sapUiTableRowHidden")
        }
        r._bHidden = false
    } else {
        if (!$.hasClass("sapUiTableRowHidden")) {
            $.addClass("sapUiTableRowHidden");
            a.addClass("sapUiTableRowHidden")
        }
        r._bHidden = true;
        for (var i = 0, l = this._aVisibleColumns.length; i < l; i++) {
            var b = this._aIdxCols2Cells[this._aVisibleColumns[i]];
            if (C[b]) {
                this._updateCellBindingContext(C[b], c, m)
            }
        }
    }
};

sap.ui.table.Table.prototype._updateCellBindingContext = function(c, C, m) {
    c.setBindingContext(C, m);
    if (this._bCallUpdateTableCell && c._updateTableCell) {
        c._updateTableCell(c, C, c.$().closest("td"))
    }
};

sap.ui.table.Table.prototype._updateNoData = function() {
    if (this.getShowNoData()) {
        var b = this.getBinding("rows");
        if (!b || (b.getLength() || 0) === 0) {
            if (!this.$().hasClass("sapUiTableEmpty")) {
                this.$().addClass("sapUiTableEmpty")
            }
            jQuery.sap.byId(this.getId() + "-ariacount").text(this._oResBundle.getText("TBL_DATA_ROWS", [0]))
        } else {
            if (this.$().hasClass("sapUiTableEmpty")) {
                this.$().removeClass("sapUiTableEmpty")
            }
            jQuery.sap.byId(this.getId() + "-ariacount").text(this._oResBundle.getText("TBL_DATA_ROWS", [(b.getLength() || 0)]))
        }
    }
};

sap.ui.table.Table.prototype._determineVisibleCols = function() {
    var $ = this.$(),
        t = this;
    if ($.hasClass("sapUiTableHScr")) {
        var s = this._oHSb.getNativeScrollPosition();
        var S = s + this._getScrollWidth();
        if (this._iOldScrollLeft !== s || this._iOldScrollRight !== S) {
            var r = this._bRtlMode;
            var L = r ? $.find(".sapUiTableCtrlScroll").width() : 0;
            this._aVisibleColumns = [];
            for (var i = 0, l = this.getFixedColumnCount(); i < l; i++) {
                this._aVisibleColumns.push(i)
            };
            var a = $.find(".sapUiTableCtrl.sapUiTableCtrlScroll .sapUiTableCtrlFirstCol > th[data-sap-ui-headcolindex]");
            a.each(function(I, e) {
                var w = jQuery(e).width();
                if (r) {
                    L -= w
                }
                if (L + w >= s && L <= S) {
                    t._aVisibleColumns.push(parseInt(jQuery(e).data('sap-ui-headcolindex'), 10))
                }
                if (!r) {
                    L += w
                }
            });
            this._iOldScrollLeft = s;
            this._iOldScrollRight = S
        }
    } else {
        this._aVisibleColumns = [];
        var c = this.getColumns();
        for (var i = 0, l = c.length; i < l; i++) {
            if (c[i].getVisible() && !c[i].getGrouped()) {
                this._aVisibleColumns.push(i)
            }
        }
    }
};

sap.ui.table.Table.prototype._getRowCount = function() {
    var b = this.getBinding("rows");
    return b ? (b.getLength() || 0) : 0
};

sap.ui.table.Table.prototype._getScrollTop = function() {
    if (this.$().hasClass("sapUiTableVScr")) {
        return this._oVSb.getScrollPosition() || 0
    } else {
        if (this.getNavigationMode() === sap.ui.table.NavigationMode.Paginator) {
            return (((this._oPaginator.getCurrentPage() || 1) - 1) * this.getVisibleRowCount())
        } else {
            return 0
        }
    }
};

sap.ui.table.Table.prototype._getScrollWidth = function() {
    return this.$().find(".sapUiTableCtrlScr").width()
};

sap.ui.table.Table.prototype._getScrollHeight = function() {
    return this.$().find(".sapUiTableCtrlScr").height()
};

sap.ui.table.Table.prototype._getVisibleColumns = function() {
    var c = [];
    var C = this.getColumns();
    for (var i = 0, l = C.length; i < l; i++) {
        if (C[i].getVisible() && !C[i].getGrouped()) {
            c.push(C[i])
        }
    }
    return c
};

sap.ui.table.Table.prototype._getVisibleColumnCount = function() {
    return this._getVisibleColumns().length
};

sap.ui.table.Table.prototype._getHeaderRowCount = function() {
    if (!this._useMultiHeader()) {
        return 1
    }
    var h = 0;
    jQuery.each(this._getVisibleColumns(), function(i, c) {
        h = Math.max(h, c.getMultiLabels().length)
    });
    return h
};

sap.ui.table.Table.prototype._useMultiHeader = function() {
    var u = false;
    jQuery.each(this._getVisibleColumns(), function(i, c) {
        if (c.getMultiLabels().length > 0) {
            u = true;
            return false
        }
    });
    return u
};

sap.ui.table.Table.prototype._getColumnsWidth = function(s, e) {
    var c = this.getColumns();
    var C = 0;
    if (s !== 0 && !s) {
        s = 0
    }
    if (e !== 0 && !e) {
        e = c.length
    }
    for (var i = s, l = e; i < l; i++) {
        if (c[i] && c[i].getVisible()) {
            var w = c[i].getWidth();
            var W = parseInt(w, 10);
            if (jQuery.sap.endsWith(w, "px")) {
                C += W
            } else {
                C += this._iColMinWidth
            }
        }
    }
    return C
};

sap.ui.table.Table.prototype._handleResize = function() {
    if (!this.getDomRef()) {
        return
    }
    this._updateHSb();
    this._updateColumnHeader();
    this._updateRowHeader();
    this._handleRowCountMode()
};

sap.ui.table.Table.prototype._checkTableSize = function() {
    if (!this.getDomRef()) return;
    var p = this.getDomRef().parentNode,
        h = p.offsetHeight,
        w = p.offsetWidth;
    if (p != this._lastParent || h != this._lastParentHeight || w != this._lastParentWidth) {
        this._handleResize();
        this._lastParent = p;
        this._lastParentHeight = h;
        this._lastParentWidth = w;
        if (this.getBinding("rows")) {
            this.updateRows()
        }
    }
};

sap.ui.table.Table.prototype._handleRowCountMode = function() {
    if (this.getVisibleRowCountMode() == sap.ui.table.VisibleRowCountMode.Auto) {
        var c = this.$().parent().height();
        var r = this._calculateRowsToDisplay(c);
        if (isNaN(r)) {
            return
        }
        var t = this;
        this._visibleRowCountTimer = setTimeout(function() {
            t.setVisibleRowCount(r)
        }, 0)
    }
};

sap.ui.table.Table.prototype._updateRowHeader = function() {
    var $ = this.$();
    var a = $.find(".sapUiTableCtrlFixed > tbody > tr");
    var b = $.find(".sapUiTableCtrlScroll > tbody > tr");
    var c = $.find(".sapUiTableRowHdr");
    if (this.getFixedColumnCount() > 0 && !this.getRowHeight()) {
        a.css('height', '');
        b.css('height', '')
    }
    for (var i = 0, l = b.length; i < l; i++) {
        var h = Math.max(jQuery(a[i]).height(), jQuery(b[i]).height());
        if (this._bjQueryLess18) {
            jQuery(c[i]).height(h);
            if (this.getFixedColumnCount() > 0 && !this.getRowHeight()) {
                jQuery(a[i]).height(h);
                jQuery(b[i]).height(h)
            }
        } else {
            jQuery(c[i]).outerHeight(h);
            if (this.getFixedColumnCount() > 0 && !this.getRowHeight()) {
                jQuery(a[i]).outerHeight(h);
                jQuery(b[i]).outerHeight(h)
            }
        }
    }
    var f = this.getFixedRowCount();
    if (f > 0) {
        var o = $.find('.sapUiTableCtrl.sapUiTableCtrlRowScroll.sapUiTableCtrlScroll')[0].offsetTop;
        this.$().find('.sapUiTableVSb').css('top', (o - 1) + 'px')
    }
};

sap.ui.table.Table.prototype._updateColumnHeader = function(u) {
    if (this._sColHdrPosTimer) {
        jQuery.sap.clearDelayedCall(this._sColHdrPosTimer)
    }
    var r = this._bRtlMode;
    var l = this._bRtlMode ? "99000px" : "-99000px";
    var s = function() {
        this._resizeDependentColumns();
        var t = this,
            $ = this.$();
        var a = $.find(".sapUiTableColHdr .sapUiTableCol");
        var c = this._getVisibleColumns();
        if (c.length == 0) return;
        var T = $.width();
        var b = $.find(".sapUiTableCtrlFirstCol > th");
        if (this.getSelectionMode() !== sap.ui.table.SelectionMode.None && this.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowOnly) {
            b = b.not(":nth-child(1)")
        }
        b.each(function(i, E) {
            jQuery(E).css('width', c[i].getWidth())
        });
        b.each(function(i, E) {
            var w = E.offsetWidth;
            var j = a.filter('[data-sap-ui-colindex=' + jQuery(E).data('sap-ui-headcolindex') + ']');
            if (j.length > 0) {
                if (!u) {
                    var C = sap.ui.getCore().byId(j.first().attr("id"));
                    if (t._bjQueryLess18) {
                        j.width(w)
                    } else {
                        j.outerWidth(w)
                    }
                    C._iRealWidth = w
                }
                var L = j.first().position().left + (r ? -2 : w - 3);
                var k = jQuery.sap.byId(j.first().attr("id") + "-rsz");
                if (L >= 0 && L <= T) {
                    k.css({
                        "left": L
                    })
                } else if (k.css("left") !== l) {
                    k.css({
                        "left": l
                    })
                }
            }
        });
        var h = this.getColumnHeaderHeight() > 0;
        if (!h && !u) {
            var d = $.find(".sapUiTableColHdr");
            var e = $.find(".sapUiTableColHdrCnt");
            var f = $.find(".sapUiTableColRowHdr");
            var g = f.add(e);
            g.height("auto");
            var H = Math.max(d.height(), e.height());
            var R = H / this._getHeaderRowCount();
            if (this._bjQueryLess18) {
                a.height(R);
                g.height(H)
            } else {
                a.outerHeight(R);
                g.outerHeight(H)
            }
        }
    };
    if (this._bOnAfterRendering) {
        s.apply(this, arguments)
    } else {
        this._sColHdrPosTimer = jQuery.sap.delayedCall(150, this, s)
    }
};

sap.ui.table.Table.prototype._disableTextSelection = function(e) {
    jQuery(e || document.body).attr("unselectable", "on").css({
        "-moz-user-select": "none",
        "-webkit-user-select": "none",
        "user-select": "none"
    }).bind("selectstart", function(E) {
        E.preventDefault();
        return false
    })
};

sap.ui.table.Table.prototype._enableTextSelection = function(e) {
    jQuery(e || document.body).attr("unselectable", "off").css({
        "-moz-user-select": "",
        "-webkit-user-select": "",
        "user-select": ""
    }).unbind("selectstart")
};

sap.ui.table.Table.prototype._clearTextSelection = function() {
    if (window.getSelection) {
        if (window.getSelection().empty) {
            window.getSelection().empty()
        } else if (window.getSelection().removeAllRanges) {
            window.getSelection().removeAllRanges()
        }
    } else if (document.selection && document.selection.empty) {
        try {
            document.selection.empty()
        } catch (e) {}
    }
};

sap.ui.table.Table.prototype.onvscroll = function(e) {
    this._leaveActionMode();
    this.setFirstVisibleRow(this._getScrollTop(), true)
};

sap.ui.table.Table.prototype._syncHeaderAndContent = function() {
    if (!this._bSyncScrollLeft) {
        this._bSyncScrollLeft = true;
        var $ = this.$();
        var s = this._oHSb.getNativeScrollPosition();
        $.find(".sapUiTableCtrlScr").scrollLeft(s);
        if ( !! sap.ui.Device.browser.webkit && this._bRtlMode) {
            var S = $.find(".sapUiTableColHdrScr").get(0);
            s = S.scrollWidth - S.clientWidth - this._oHSb.getScrollPosition()
        }
        $.find(".sapUiTableColHdrScr").scrollLeft(s);
        this._bSyncScrollLeft = false
    }
};

sap.ui.table.Table.prototype.onhscroll = function(e) {
    if (!this._bOnAfterRendering) {
        this._syncHeaderAndContent();
        this._updateColumnHeader(true);
        if (this.getBinding("rows")) {
            this.updateRows()
        }
    }
};

sap.ui.table.Table.prototype._oncolscroll = function(e) {
    if (!this._bSyncScrollLeft) {
        var $ = this.$().find(".sapUiTableColHdrScr");
        if ( !! sap.ui.Device.browser.webkit && this._bRtlMode) {
            var s = this.$().find(".sapUiTableColHdrScr").get(0);
            this._oHSb.setScrollPosition(s.scrollWidth - s.clientWidth - $.scrollLeft())
        } else {
            this._oHSb.setNativeScrollPosition($.scrollLeft())
        }
    }
};

sap.ui.table.Table.prototype._oncntscroll = function(e) {
    if (!this._bSyncScrollLeft) {
        var $ = this.$().find(".sapUiTableCtrlScr");
        this._oHSb.setNativeScrollPosition($.scrollLeft())
    }
};

sap.ui.table.Table.prototype.onmousedown = function(e) {
    var $ = jQuery(e.target);
    var a = jQuery.sap.byId(this.getId() + "-sb");
    if (e.target == a[0]) {
        jQuery(document.body).bind("selectstart", jQuery.proxy(this._splitterSelectStart, this));
        var o = a.offset();
        var h = a.height();
        var w = a.width();
        jQuery(document.body).append("<div id=\"" + this.getId() + "-ghost\" class=\"sapUiHSBGhost\" style =\" height:" + h + "px; width:" + w + "px; left:" + o.left + "px; top:" + o.top + "px\" ></div>");
        a.append("<div id=\"" + this.getId() + "-overlay\" style =\"left: 0px;" + " right: 0px; bottom: 0px; top: 0px; position:absolute\" ></div>");
        jQuery(document).bind("mouseup", jQuery.proxy(this._onGhostMouseRelease, this));
        jQuery(document).bind("mousemove", jQuery.proxy(this._onGhostMouseMove, this));
        e.preventDefault();
        e.stopPropagation();
        return
    }
    var b = $.closest(".sapUiTableCol");
    if (b.length === 1) {
        this._bShowMenu = true;
        this._sDelayedMenuTimer = jQuery.sap.delayedCall(200, this, function() {
            this._bShowMenu = false
        });
        if (this.getEnableColumnReordering()) {
            var i = parseInt(b.attr("data-sap-ui-colindex"), 10);
            if (i > this._iLastFixedColIndex) {
                var c = this.getColumns()[i];
                this._sDelayedActionTimer = jQuery.sap.delayedCall(200, this, function() {
                    this._onColumnMoveStart(c)
                })
            }
        }
    }
    var C = !! (e.metaKey || e.ctrlKey);
    if ( !! sap.ui.Device.browser.firefox && C) {
        e.preventDefault()
    }
};

sap.ui.table.Table.prototype.onmouseup = function(e) {
    if (this.$().find(".sapUiTableCtrl td :focus").length > 0) {
        this._enterActionMode(this.$().find(".sapUiTableCtrl td :focus").get(0))
    } else {
        this._leaveActionMode(e)
    }
};

sap.ui.table.Table.prototype.onclick = function(e) {
    jQuery.sap.clearDelayedCall(this._sDelayedActionTimer);
    this._onSelect(e)
};

sap.ui.table.Table.prototype.onfocusin = function(e) {
    var $ = jQuery(e.target);
    if (!this._bIgnoreFocusIn && ($.hasClass("sapUiTableCtrlBefore") || $.hasClass("sapUiTableCtrlAfter"))) {
        jQuery.sap.byId(this.getId() + "-ariadesc").text(this._oResBundle.getText("TBL_TABLE"));
        this._leaveActionMode();
        jQuery(this._oItemNavigation.getFocusedDomRef() || this._oItemNavigation.getRootDomRef()).focus()
    } else if (jQuery.sap.endsWith(e.target.id, "-rsz")) {
        e.preventDefault();
        e.stopPropagation()
    }
};

sap.ui.table.Table.prototype._onSelect = function(e) {
    var $ = jQuery(e.target);
    var s = e.shiftKey;
    var c = !! (e.metaKey || e.ctrlKey);
    var a = $.closest(".sapUiTableCol");
    if (this._bShowMenu && a.length === 1) {
        var i = parseInt(a.attr("data-sap-ui-colindex"), 10);
        var C = this.getColumns()[i];
        this._onColumnSelect(C);
        return
    }
    var b = $.closest(".sapUiTableRowHdr");
    if (b.length === 1) {
        var i = parseInt(b.attr("data-sap-ui-rowindex"), 10);
        this._onRowSelect(this.getFirstVisibleRow() + i, s, c);
        return
    }
    if ((this.getSelectionBehavior() === sap.ui.table.SelectionBehavior.Row || this.getSelectionBehavior() === sap.ui.table.SelectionBehavior.RowOnly)) {
        var b = $.closest(".sapUiTableCtrl > tbody > tr");
        if (b.length === 1) {
            var i = parseInt(b.attr("data-sap-ui-rowindex"), 10);
            this._onRowSelect(this.getFirstVisibleRow() + i, s, c);
            return
        }
    }
    if (jQuery.sap.containsOrEquals(jQuery.sap.domById(this.getId() + "-selall"), e.target)) {
        if (this._getRowCount() === this.getSelectedIndices().length) {
            this.clearSelection()
        } else {
            this.selectAll()
        }
        if ( !! sap.ui.Device.browser.internet_explorer) {
            jQuery.sap.byId(this.getId() + "-selall").focus()
        }
        return
    }
};

sap.ui.table.Table.prototype._onRowSelect = function(r, s, c) {
    if ( !! sap.ui.Device.browser.internet_explorer && s) {
        this._clearTextSelection()
    }
    var b = this.getBinding("rows");
    if (!b) {
        return
    }
    if (r < 0 || r >= (b.getLength() || 0)) {
        return
    }
    this._iSourceRowIndex = r;
    var S = this.getSelectionMode();
    if (S !== sap.ui.table.SelectionMode.None) {
        if (S === sap.ui.table.SelectionMode.Single) {
            if (!this.isIndexSelected(r)) {
                this.setSelectedIndex(r)
            } else {
                this.clearSelection()
            }
        } else {
            if (s) {
                var i = this.getSelectedIndex();
                if (i >= 0) {
                    this.addSelectionInterval(i, r)
                } else {
                    this.setSelectedIndex(r)
                }
            } else {
                if (!this.isIndexSelected(r)) {
                    if (c) {
                        this.addSelectionInterval(r, r)
                    } else {
                        this.setSelectedIndex(r)
                    }
                } else {
                    if (c) {
                        this.removeSelectionInterval(r, r)
                    } else {
                        if (this.getSelectedIndices().length === 1) {
                            this.clearSelection()
                        } else {
                            this.setSelectedIndex(r)
                        }
                    }
                }
            }
        }
    }
    this._iSourceRowIndex = undefined
};

sap.ui.table.Table.prototype._onColumnSelect = function(c) {
    var e = this.fireColumnSelect({
        column: c
    });
    if (e) {
        c._openMenu()
    }
};

sap.ui.table.Table.prototype._onColumnMoveStart = function(c) {
    this._disableTextSelection();
    var $ = c.$();
    var C = parseInt($.attr("data-sap-ui-colindex"), 10);
    if (C < this.getFixedColumnCount()) {
        return
    }
    this._$colGhost = $.clone().removeAttr("id");
    $.css({
        "opacity": ".25"
    });
    this._$colGhost.addClass("sapUiTableColGhost").css({
        "left": -10000,
        "top": -10000,
        "position": "absolute",
        "z-index": this.$().zIndex() + 10
    });
    this.$().find(".sapUiTableCol").each(function(i, e) {
        var $ = jQuery(this);
        $.css({
            position: "relative"
        });
        $.data("pos", {
            left: $.position().left,
            center: $.position().left + $.outerWidth() / 2,
            right: $.position().left + $.outerWidth()
        })
    });
    this._$colGhost.appendTo(document.body);
    jQuery(document.body).mousemove(jQuery.proxy(this._onColumnMove, this)).mouseup(jQuery.proxy(this._onColumnMoved, this))
};

sap.ui.table.Table.prototype._onColumnMove = function(e) {
    var $ = this.$();
    var r = this._bRtlMode;
    var R = e.pageX - $.offset().left;
    var d = parseInt(this._$colGhost.attr("data-sap-ui-colindex"), 10);
    var D = this.getColumns()[d].$();
    var o = this._iNewColPos;
    this._iNewColPos = d;
    var t = this;
    $.find(".sapUiTableCol").each(function(i, c) {
        var a = jQuery(c);
        var C = parseInt(a.attr("data-sap-ui-colindex"), 10);
        if (a.get(0) !== D.get(0)) {
            var p = a.data("pos");
            var b = R >= p.left && R <= p.center;
            var A = R >= p.center && R <= p.right;
            if (!r) {
                t._iNewColPos = b ? C : A ? C + 1 : t._iNewColPos
            } else {
                t._iNewColPos = A ? C : b ? C + 1 : t._iNewColPos
            }
            if ((b || A) && C > d) {
                t._iNewColPos--
            }
        }
    });
    if (this._iNewColPos <= this._iLastFixedColIndex) {
        this._iNewColPos = o
    }
    if (this._iNewColPos < this.getFixedColumnCount()) {
        this._iNewColPos = o
    }
    this._animateColumnMove(d, o, this._iNewColPos);
    this._$colGhost.css({
        "left": e.pageX + 5,
        "top": e.pageY + 5
    })
};

sap.ui.table.Table.prototype._animateColumnMove = function(c, o, n) {
    var r = this._bRtlMode;
    var d = this.getColumns()[c].$();
    if (o !== n) {
        for (var i = Math.min(o, n), l = Math.max(o, n); i <= l; i++) {
            var C = this.getColumns()[i];
            if (i !== c && C.getVisible()) {
                C.$().stop(true, true).animate({
                    left: "0px"
                })
            }
        }
        var O = 0;
        if (n < c) {
            for (var i = n; i < c; i++) {
                var C = this.getColumns()[i];
                if (C.getVisible()) {
                    var $ = C.$();
                    O -= $.outerWidth();
                    $.stop(true, true).animate({
                        left: d.outerWidth() * (r ? -1 : 1) + "px"
                    })
                }
            }
        } else {
            for (var i = c + 1, l = n + 1; i < l; i++) {
                var C = this.getColumns()[i];
                if (C.getVisible()) {
                    var $ = C.$();
                    O += $.outerWidth();
                    $.stop(true, true).animate({
                        left: d.outerWidth() * (r ? 1 : -1) + "px"
                    })
                }
            }
        }
        d.stop(true, true).animate({
            left: O * (r ? -1 : 1) + "px"
        })
    }
};

sap.ui.table.Table.prototype._onColumnMoved = function(e) {
    var d = parseInt(this._$colGhost.attr("data-sap-ui-colindex"), 10);
    var D = this.getColumns()[d];
    jQuery(document.body).unbind("mousemove", this._onColumnMove).unbind("mouseup", this._onColumnMoved);
    this._$colGhost.remove();
    this._$colGhost = undefined;
    this._enableTextSelection();
    var E = this.fireColumnMove({
        column: D,
        newPos: this._iNewColPos
    });
    if (E && this._iNewColPos !== undefined && this._iNewColPos !== d) {
        this.removeColumn(D);
        this.insertColumn(D, this._iNewColPos);
        this._oColHdrItemNav.setFocusedIndex(this._iNewColPos)
    } else {
        this._animateColumnMove(d, this._iNewColPos, d);
        D.$().css({
            "backgroundColor": "",
            "backgroundImage": "",
            "opacity": ""
        })
    }
    delete this._iNewColPos
};

sap.ui.table.Table.prototype._onColumnResizeStart = function(e) {
    this._iColumnResizeStart = e.pageX;
    this._disableTextSelection();
    this._$colResize = jQuery(e.target);
    jQuery(document.body).mousemove(jQuery.proxy(this._onColumnResize, this)).mouseup(jQuery.proxy(this._onColumnResized, this))
};

sap.ui.table.Table.prototype._onColumnResize = function(e) {
    if (this._iColumnResizeStart && this._iColumnResizeStart + 3 < e.pageX && this._iColumnResizeStart - 3 > e.pageX) {
        return
    }
    this._$colResize.addClass("sapUiTableColRszActive");
    this._iColumnResizeStart = null;
    var $ = this.$();
    var r = this._bRtlMode;
    var c = parseInt(this._$colResize.attr("data-sap-ui-colindex"), 10);
    var C = this.getColumns()[c];
    var a = C.$();
    var o = $.find(".sapUiTableCnt").offset().left;
    var R = e.pageX - o;
    var i = a.position().left;
    var p = R - i;
    var w = r ? a.outerWidth() - p : p;
    w = Math.max(w, this._iColMinWidth);
    var b = i + (r ? a.outerWidth() - w : w);
    b -= (this._$colResize.width() - 2) / 2;
    this._$colResize.css("left", b);
    C._iNewWidth = w
};

sap.ui.table.Table.prototype._onColumnResized = function(e) {
    if (!this._$colResize) {
        return
    }
    var c = parseInt(this._$colResize.attr("data-sap-ui-colindex"), 10);
    var C = this.getColumns()[c];
    if (!this._iColumnResizeStart) {
        if (!C._iNewWidth) {
            return
        }
        var w;
        var a = this.$().find(".sapUiTableCtrl").width();
        if (!this._checkPercentageColumnWidth()) {
            w = C._iNewWidth + "px"
        } else {
            var i = Math.round(100 / a * C._iNewWidth);
            w = i + "%"
        }
        this._updateColumnWidth(C, w);
        this._resizeDependentColumns(C, w);
        delete C._iNewWidth
    }
    jQuery(document.body).unbind("mousemove", this._onColumnResize).unbind("mouseup", this._onColumnResized);
    C.focus();
    this._$colResize.removeClass("sapUiTableColRszActive");
    this._$colResize = undefined;
    this._enableTextSelection();
    this.invalidate()
};

sap.ui.table.Table.prototype._resizeDependentColumns = function(c, w) {
    if (this._checkPercentageColumnWidth()) {
        var v = this._getVisibleColumns();
        var C = undefined;
        jQuery.each(v, function(I, e) {
            if (c === e) {
                C = I
            }
        });
        var o = 0;
        var l = v.length - 1;
        var t;
        if (C === undefined) {
            t = 0
        } else {
            t = parseInt(w, 10)
        }
        var p = 0;
        var O = [];
        var a = this;
        jQuery.each(v, function(I, e) {
            var f = a._getColumnPercentageWidth(e);
            if ((((C === l && I < C) || ((C !== l) && I > C)) && e.getFlexible()) || C === undefined) {
                o += e.$().outerWidth();
                p += f;
                O.push(e)
            } else if (I !== C) {
                t += f
            }
        });
        var b = t;
        jQuery.each(O, function(I, e) {
            var f = a._getColumnPercentageWidth(e);
            var N = Math.round((100 - b) / p * f);
            if (I === O.length - 1) {
                N = 100 - t
            } else {
                t += N
            }
            a._updateColumnWidth(e, N + "%")
        })
    } else {
        var v = this._getVisibleColumns(),
            A = this.$().find(".sapUiTableCtrl").width(),
            C, r = 0,
            L = 0,
            R = 0,
            n = 0;
        jQuery.each(v, function(I, e) {
            if (!jQuery.sap.endsWith(e.getWidth(), "px")) {
                n++;
                return false
            }
            if (C != undefined) {
                R += parseInt(e.getWidth(), 10);
                r++
            } else if (c !== e) {
                L += parseInt(e.getWidth(), 10)
            }
            if (c === e) {
                C = I;
                L += parseInt(w, 10)
            }
        });
        if (n > 0 || (L + R > A)) {
            return
        }
        A -= L;
        for (var i = C + 1; i < v.length; i++) {
            var c = v[i],
                d = parseInt(c.getWidth(), 10),
                P = d / R * 100,
                N = A / 100 * P;
            this._updateColumnWidth(c, Math.round(N) + 'px')
        }
    }
};

sap.ui.table.Table.prototype._getColumnPercentageWidth = function(c) {
    var C = c.getWidth();
    var i = parseInt(c.getWidth(), 10);
    var t = this.$().find(".sapUiTableCtrl").width();
    if (jQuery.sap.endsWith(C, "px")) {
        i = Math.round(100 / t * i)
    } else if (!jQuery.sap.endsWith(C, "%")) {
        i = Math.round(100 / t * c.$().width())
    }
    return i
};

sap.ui.table.Table.prototype._updateColumnWidth = function(c, w) {
    var e = this.fireColumnResize({
        column: c,
        width: w
    });
    if (e) {
        c.setProperty("width", w, true)
    }
};

sap.ui.table.Table.prototype._checkPercentageColumnWidth = function() {
    var c = this.getColumns();
    var h = false;
    jQuery.each(c, function(i, C) {
        if (jQuery.sap.endsWith(C.getWidth(), "%")) {
            h = true;
            return false
        }
    });
    return h
};

sap.ui.table.Table.prototype.sort = function(c, s, a) {
    if (jQuery.inArray(c, this.getColumns()) >= 0) {
        c.sort(s === sap.ui.table.SortOrder.Descending, a)
    }
};

sap.ui.table.Table.prototype.filter = function(c, v) {
    if (jQuery.inArray(c, this.getColumns()) >= 0) {
        c.filter(v)
    }
};

sap.ui.table.Table.prototype._updateSelection = function() {
    if (this.getSelectionMode() === sap.ui.table.SelectionMode.None) {
        return
    }
    var $ = this.$();
    var f = this.getFirstVisibleRow();
    var t = this;
    var r = this._oResBundle;
    var m = this._oSelection.getSelectedIndices().length > 1;
    $.find(".sapUiTableRowHdr").each(function(i, e) {
        var a = jQuery($.find(".sapUiTableCtrlFixed > tbody > tr").get(i));
        var b = jQuery($.find(".sapUiTableCtrlScroll > tbody > tr").get(i));
        var c = a.add(b);
        var d = jQuery(this);
        var g = d.add(c);
        if (t.isIndexSelected(f + i)) {
            if (!jQuery(this).hasClass("sapUiTableRowSel")) {
                jQuery(this).addClass("sapUiTableRowSel");
                c.addClass("sapUiTableRowSel");
                g.attr("aria-selected", "true");
                c.children("td").attr("aria-selected", "true")
            }
            if (m) {
                g.attr("title", r.getText("TBL_ROW_DESELECT_MULTI")).attr("aria-label", r.getText("TBL_ROW_DESELECT_MULTI_KEY"));
                c.find(".sapUiTableAriaRowSel").text(r.getText("TBL_ROW_DESELECT_MULTI_KEY"));
                if (t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowSelector) {
                    c.children("td").attr('aria-describedby', t.getId() + "-toggleedit " + t.getId() + "-deselectrowmulti")
                }
            } else {
                g.attr("title", r.getText("TBL_ROW_DESELECT")).attr("aria-label", r.getText("TBL_ROW_DESELECT_KEY"));
                c.find(".sapUiTableAriaRowSel").text(r.getText("TBL_ROW_DESELECT_KEY"));
                if (t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowSelector) {
                    c.children("td").attr('aria-describedby', t.getId() + "-toggleedit " + t.getId() + "-deselectrow")
                }
            }
        } else {
            if (jQuery(this).hasClass("sapUiTableRowSel")) {
                jQuery(this).removeClass("sapUiTableRowSel");
                c.removeClass("sapUiTableRowSel");
                if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                    g.attr("aria-selected", "false");
                    c.children("td").attr("aria-selected", "false")
                } else {
                    g.removeAttr("aria-selected");
                    c.children("td").removeAttr("aria-selected")
                }
            }
            if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi && t._oSelection.getSelectedIndices().length > 0) {
                g.attr("title", r.getText("TBL_ROW_SELECT")).attr("aria-label", r.getText("TBL_ROW_SELECT_MULTI_KEY"));
                c.find(".sapUiTableAriaRowSel").text(r.getText("TBL_ROW_SELECT_MULTI_KEY"));
                if (t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowSelector) {
                    c.children("td").attr('aria-describedby', t.getId() + "-toggleedit " + t.getId() + "-selectrowmulti")
                }
            } else {
                g.attr("title", r.getText("TBL_ROW_SELECT")).attr("aria-label", r.getText("TBL_ROW_SELECT_KEY"));
                c.find(".sapUiTableAriaRowSel").text(r.getText("TBL_ROW_SELECT_KEY"));
                if (t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowSelector) {
                    c.children("td").attr('aria-describedby', t.getId() + "-toggleedit " + t.getId() + "-selectrow")
                }
            }
        }
    });
    this.setProperty("selectedIndex", this.getSelectedIndex(), true)
};

sap.ui.table.Table.prototype._onSelectionChanged = function(e) {
    var r = e.getParameter("rowIndices");
    var R = this._iSourceRowIndex !== undefined ? this._iSourceRowIndex : this.getSelectedIndex();
    this._updateSelection();
    if (this.getSelectionMode() == "Multi") {
        jQuery.sap.byId(this.getId() + "-selall").attr('title', this._oResBundle.getText("TBL_SELECT_ALL"))
    }
    this.fireRowSelectionChange({
        rowIndex: R,
        rowContext: this.getContextByIndex(R),
        rowIndices: r
    })
};

sap.ui.table.Table.prototype.getContextByIndex = function(i) {
    var b = this.getBinding("rows");
    return i >= 0 && b ? b.getContexts(i, 1)[0] : null
};

sap.ui.table.Table.prototype.getSelectedIndex = function() {
    return this._oSelection.getLeadSelectedIndex()
};

sap.ui.table.Table.prototype.setSelectedIndex = function(i) {
    if (i === -1) {
        this._oSelection.clearSelection()
    } else {
        this._oSelection.setSelectionInterval(i, i)
    }
    return this
};

sap.ui.table.Table.prototype.clearSelection = function() {
    this._oSelection.clearSelection();
    jQuery.sap.byId(this.getId() + "-selall").attr('title', this._oResBundle.getText("TBL_SELECT_ALL"));
    return this
};

sap.ui.table.Table.prototype.selectAll = function() {
    if (this.getSelectionMode() != "Multi") {
        return this
    }
    var b = this.getBinding("rows");
    if (b) {
        this._oSelection.setSelectionInterval(0, (b.getLength() || 0) - 1);
        jQuery.sap.byId(this.getId() + "-selall").attr('title', this._oResBundle.getText("TBL_DESELECT_ALL"))
    }
    return this
};

sap.ui.table.Table.prototype.getSelectedIndices = function() {
    return this._oSelection.getSelectedIndices()
};

sap.ui.table.Table.prototype.addSelectionInterval = function(i, I) {
    this._oSelection.addSelectionInterval(i, I);
    return this
};

sap.ui.table.Table.prototype.setSelectionInterval = function(i, I) {
    this._oSelection.setSelectionInterval(i, I);
    return this
};

sap.ui.table.Table.prototype.removeSelectionInterval = function(i, I) {
    this._oSelection.removeSelectionInterval(i, I);
    return this
};

sap.ui.table.Table.prototype.isIndexSelected = function(i) {
    return this._oSelection.isSelectedIndex(i)
};

sap.ui.table.Table.prototype._scrollNext = function() {
    if (this.getFirstVisibleRow() < this._getRowCount() - this.getVisibleRowCount()) {
        this.setFirstVisibleRow(Math.min(this.getFirstVisibleRow() + 1, this._getRowCount() - this.getVisibleRowCount()))
    }
};

sap.ui.table.Table.prototype._scrollPrevious = function() {
    if (this.getFirstVisibleRow() > 0) {
        this.setFirstVisibleRow(Math.max(this.getFirstVisibleRow() - 1, 0))
    }
};

sap.ui.table.Table.prototype._scrollPageUp = function() {
    this.setFirstVisibleRow(Math.max(this.getFirstVisibleRow() - this.getVisibleRowCount(), 0))
};

sap.ui.table.Table.prototype._scrollPageDown = function() {
    this.setFirstVisibleRow(Math.min(this.getFirstVisibleRow() + this.getVisibleRowCount(), this._getRowCount() - this.getVisibleRowCount()))
};

sap.ui.table.Table.prototype._isTopRow = function(e) {
    var $ = jQuery(e.target);
    var r = parseInt($.add($.parent()).filter("[data-sap-ui-rowindex]").attr("data-sap-ui-rowindex"), 10);
    var f = this.getFixedRowCount();
    if (f > 0 && r >= f) {
        return r === f
    }
    return r === 0
};

sap.ui.table.Table.prototype._isBottomRow = function(e) {
    var $ = jQuery(e.target);
    var r = parseInt($.add($.parent()).filter("[data-sap-ui-rowindex]").attr("data-sap-ui-rowindex"), 10);
    return r === this.getVisibleRowCount() - 1
};

sap.ui.table.Table.prototype._enterActionMode = function(d) {
    if (d && !this._bActionMode) {
        if (jQuery(d).filter(":tabbable").length == 0) {
            return
        }
        this._bActionMode = true;
        this.removeDelegate(this._oItemNavigation);
        jQuery(this._oItemNavigation.getFocusedDomRef()).attr("tabindex", "-1");
        jQuery(d).focus()
    }
};

sap.ui.table.Table.prototype._leaveActionMode = function(e) {
    if (this._bActionMode) {
        this._bActionMode = false;
        this.addDelegate(this._oItemNavigation);
        jQuery(this._oItemNavigation.getFocusedDomRef()).attr("tabindex", "0");
        if (e) {
            if (jQuery(e.target).closest("td[tabindex=-1]").length > 0) {
                var i = jQuery(this._oItemNavigation.aItemDomRefs).index(jQuery(e.target).closest("td[tabindex=-1]").get(0));
                this._oItemNavigation.focusItem(i, null)
            } else {
                if (jQuery.sap.containsOrEquals(this.$().find(".sapUiTableCCnt").get(0), e.target)) {
                    this._oItemNavigation.focusItem(this._oItemNavigation.getFocusedIndex(), null)
                }
            }
        } else {
            this._oItemNavigation.focusItem(this._oItemNavigation.getFocusedIndex(), null)
        }
    }
};

sap.ui.table.Table.prototype.onsapselectmodifiers = sap.ui.table.Table.prototype.onsapselect = function(e) {
    if (e.srcControl !== this && jQuery.inArray(e.srcControl, this.getRows()) === -1 && jQuery.inArray(e.srcControl, this.getColumns()) === -1) {
        return
    }
    this._bShowMenu = true;
    this._onSelect(e);
    this._bShowMenu = false;
    e.preventDefault()
};

sap.ui.table.Table.prototype.onkeydown = function(e) {
    var $ = this.$();
    if (!this._bActionMode && e.keyCode == jQuery.sap.KeyCodes.F2 || e.keyCode == jQuery.sap.KeyCodes.ENTER) {
        if ($.find(".sapUiTableCtrl td:focus").length > 0) {
            this._enterActionMode($.find(".sapUiTableCtrl td:focus :sapFocusable").get(0));
            e.preventDefault();
            e.stopPropagation()
        }
    } else if (this._bActionMode && e.keyCode == jQuery.sap.KeyCodes.F2) {
        this._leaveActionMode(e)
    } else if (e.keyCode == jQuery.sap.KeyCodes.TAB && this._bActionMode) {
        if (this.getFixedColumnCount() > 0) {
            var a = jQuery(e.target);
            if (a.is("td[role=gridcell]") == false) {
                a = a.parents("td[role=gridcell]")
            }
            var b = a.parent("tr[data-sap-ui-rowindex]");
            var c = b.closest(".sapUiTableCtrl");
            var r = parseInt(b.attr("data-sap-ui-rowindex"), 10);
            var d = b.find("td[role=gridcell]");
            var C = d.index(a);
            var t = d.length;
            if (C === (t - 1)) {
                var f;
                if (c.hasClass("sapUiTableCtrlFixed")) {
                    f = $.find(".sapUiTableCtrl.sapUiTableCtrlScroll")
                } else {
                    f = $.find(".sapUiTableCtrl.sapUiTableCtrlFixed");
                    r++;
                    if (r == this.getVisibleRowCount()) {
                        r = 0
                    }
                }
                var g = f.find("tr[data-sap-ui-rowindex=" + r + "]");
                var h = g.find("td :sapFocusable[tabindex=0]").first();
                if (h.length > 0) {
                    h.focus();
                    e.preventDefault()
                }
            }
        }
    }
};

sap.ui.table.Table.prototype.onsapescape = function(e) {
    this._leaveActionMode(e)
};

sap.ui.table.Table.prototype.onsaptabprevious = function(e) {
    var $ = this.$();
    if (this._bActionMode) {
        if ($.find(".sapUiTableCtrlFixed").firstFocusableDomRef() === e.target) {
            $.find(".sapUiTableCtrlScroll").lastFocusableDomRef().focus();
            e.preventDefault();
            e.stopPropagation()
        }
    } else {
        if (this._oItemNavigation.getFocusedDomRef() === e.target && jQuery.sap.containsOrEquals($.find(".sapUiTableCCnt").get(0), e.target)) {
            this._bIgnoreFocusIn = true;
            $.find(".sapUiTableCtrlBefore").focus();
            this._bIgnoreFocusIn = false
        }
    }
};

sap.ui.table.Table.prototype.onsaptabnext = function(e) {
    var $ = this.$();
    if (this._bActionMode) {
        if ($.find(".sapUiTableCCnt").lastFocusableDomRef() === e.target) {
            $.find(".sapUiTableCCnt").firstFocusableDomRef().focus();
            e.preventDefault();
            e.stopPropagation()
        }
    } else {
        if (this._oItemNavigation.getFocusedDomRef() === e.target) {
            this._bIgnoreFocusIn = true;
            $.find(".sapUiTableCtrlAfter").focus();
            this._bIgnoreFocusIn = false
        }
    }
};

sap.ui.table.Table.prototype.onsapdown = function(e) {
    if (!this._bActionMode && this._isBottomRow(e)) {
        if (this.getNavigationMode() === sap.ui.table.NavigationMode.Scrollbar) {
            this._scrollNext()
        } else {
            this._scrollPageDown()
        }
    }
    e.preventDefault()
};

sap.ui.table.Table.prototype.onsapup = function(e) {
    if (!this._bActionMode && this._isTopRow(e)) {
        if (this.getFirstVisibleRow() != 0) {
            e.stopImmediatePropagation(true)
        }
        if (this.getNavigationMode() === sap.ui.table.NavigationMode.Scrollbar) {
            this._scrollPrevious()
        } else {
            this._scrollPageUp()
        }
    }
    e.preventDefault()
};

sap.ui.table.Table.prototype.onsappagedown = function(e) {
    if (!this._bActionMode && this._isBottomRow(e)) {
        this._scrollPageDown()
    }
    e.preventDefault()
};

sap.ui.table.Table.prototype.onsappageup = function(e) {
    if (!this._bActionMode) {
        var i = this._oItemNavigation;
        var I = this.getColumnHeaderVisible() ? i.iColumns : 0;
        if (i.iFocusedIndex >= I && this.getFirstVisibleRow() != 0) {
            var c = i.iFocusedIndex % i.iColumns;
            i.focusItem(I + c, e);
            e.stopImmediatePropagation(true)
        }
        if (this._isTopRow(e)) {
            this._scrollPageUp()
        }
    }
    e.preventDefault()
};

sap.ui.table.Table.prototype.onsaphomemodifiers = function(e) {
    if (e.metaKey || e.ctrlKey) {
        this.setFirstVisibleRow(0)
    }
};

sap.ui.table.Table.prototype.onsapendmodifiers = function(e) {
    if (e.metaKey || e.ctrlKey) {
        this.setFirstVisibleRow(this._getRowCount() - this.getVisibleRowCount())
    }
};

sap.ui.table.Table.prototype.setGroupBy = function(v) {
    var g = v;
    if (typeof g === "string") {
        g = sap.ui.getCore().byId(g)
    }
    var r = false;
    if (g && g instanceof sap.ui.table.Column) {
        if (jQuery.inArray(g, this.getColumns()) === -1) {
            throw new Error("Column has to be part of the columns aggregation!")
        }
        var e = this.fireGroup({
            column: g
        });
        var o = sap.ui.getCore().byId(this.getGroupBy());
        if (o) {
            o.setGrouped(false);
            r = true
        }
        if (e && g instanceof sap.ui.table.Column) {
            g.setGrouped(true)
        }
    }
    if (!g || r) {
        var b = this.getBindingInfo("rows");
        delete b.binding;
        this._bindAggregation("rows", b)
    }
    return this.setAssociation("groupBy", g)
};

sap.ui.table.Table.prototype.getBinding = function(n) {
    n = n || "rows";
    var b = sap.ui.core.Element.prototype.getBinding.call(this, n);
    if (this.getEnableGrouping()) {
        jQuery.sap.require("sap.ui.model.json.JSONListBinding");
        jQuery.sap.require("sap.ui.model.xml.XMLListBinding");
        var g = sap.ui.getCore().byId(this.getGroupBy());
        var I = g && g.getGrouped() && n === "rows" && b && (b instanceof sap.ui.model.json.JSONListBinding || b instanceof sap.ui.model.xml.XMLListBinding);
        if (I && !b._modified) {
            b._modified = true;
            this._modifyRow = function(r, $) {
                this.$().find(".sapUiTableRowHdrScr").css("display", "block");
                var a = this.$().find("div[data-sap-ui-rowindex=" + $.attr("data-sap-ui-rowindex") + "]");
                if (b.isGroupHeader(r)) {
                    $.addClass("sapUiTableGroupHeader sapUiTableRowHidden");
                    var s = b.isExpanded(r) ? "sapUiTableGroupIconOpen" : "sapUiTableGroupIconClosed";
                    a.html("<div class=\"sapUiTableGroupIcon " + s + "\" tabindex=\"-1\">" + b.getTitle(r) + "</div>");
                    a.addClass("sapUiTableGroupHeader").removeAttr("title")
                } else {
                    $.removeClass("sapUiTableGroupHeader");
                    a.html("");
                    a.removeClass("sapUiTableGroupHeader")
                }
            };
            this.onclick = function(e) {
                if (jQuery(e.target).hasClass("sapUiTableGroupIcon")) {
                    var $ = jQuery(e.target).parents("[data-sap-ui-rowindex]");
                    if ($.length > 0) {
                        var r = this.getFirstVisibleRow() + parseInt($.attr("data-sap-ui-rowindex"), 10);
                        var b = this.getBinding("rows");
                        if (b.isExpanded(r)) {
                            b.collapse(r);
                            jQuery(e.target).removeClass("sapUiTableGroupIconOpen").addClass("sapUiTableGroupIconClosed")
                        } else {
                            b.expand(r);
                            jQuery(e.target).removeClass("sapUiTableGroupIconClosed").addClass("sapUiTableGroupIconOpen")
                        }
                    }
                } else {
                    if (sap.ui.table.Table.prototype.onclick) {
                        sap.ui.table.Table.prototype.onclick.apply(this, arguments)
                    }
                }
            };
            var p = g.getSortProperty();
            b.sort(new sap.ui.model.Sorter(p));
            var l, c;
            if (b instanceof sap.ui.model.json.JSONListBinding) {
                l = sap.ui.model.json.JSONListBinding.prototype.getLength.apply(b, []);
                c = sap.ui.model.json.JSONListBinding.prototype.getContexts.apply(b, [0, l])
            } else {
                l = sap.ui.model.xml.XMLListBinding.prototype.getLength.apply(b, []);
                c = sap.ui.model.xml.XMLListBinding.prototype.getContexts.apply(b, [0, l])
            }
            var k = undefined;
            var C = 0;
            for (var i = l - 1; i >= 0; i--) {
                var N = c[i].getProperty(p);
                if (!k) {
                    k = N
                }
                if (k !== N) {
                    c.splice(i + 1, 0, {
                        oContext: c[i + 1],
                        name: k,
                        count: C,
                        groupHeader: true,
                        expanded: true
                    });
                    k = N;
                    C = 0
                }
                C++
            }
            c.splice(0, 0, {
                oContext: c[0],
                name: k,
                count: C,
                groupHeader: true,
                expanded: true
            });
            jQuery.extend(b, {
                getLength: function() {
                    return c.length
                },
                getContexts: function(s, l) {
                    return c.slice(s, s + l)
                },
                isGroupHeader: function(a) {
                    var o = c[a];
                    return o && !(o instanceof sap.ui.model.Context)
                },
                getTitle: function(a) {
                    var o = c[a];
                    return o && !(o instanceof sap.ui.model.Context) && (o["name"] + " - " + o["count"])
                },
                isExpanded: function(a) {
                    return this.isGroupHeader(a) && c[a].expanded
                },
                expand: function(a) {
                    if (this.isGroupHeader(a) && !c[a].expanded) {
                        for (var i = 0; i < c[a].childs.length; i++) {
                            c.splice(a + 1 + i, 0, c[a].childs[i])
                        }
                        delete c[a].childs;
                        c[a].expanded = true;
                        this._fireChange()
                    }
                },
                collapse: function(a) {
                    if (this.isGroupHeader(a) && c[a].expanded) {
                        c[a].childs = c.splice(a + 1, c[a].count);
                        c[a].expanded = false;
                        this._fireChange()
                    }
                }
            })
        }
    }
    return b
};

sap.ui.table.Table.prototype.resetGrouping = function() {
    var b = this.getBinding("rows");
    if (b && b._modified) {
        this.$().find(".sapUiTableRowHdrScr").css("display", "");
        this.onclick = sap.ui.table.Table.prototype.onclick;
        this._modifyRow = undefined;
        var B = this.getBindingInfo("rows");
        this.unbindRows();
        this.bindRows(B)
    }
};

sap.ui.table.Table.prototype.setEnableGrouping = function(e) {
    this.setProperty("enableGrouping", e);
    if (!e) {
        this.resetGrouping()
    }
    var c = this.getColumns();
    for (var i = 0, l = c.length; i < l; i++) {
        if (c[i].getMenu()) {
            c[i].getMenu()._bInvalidated = true
        }
    }
    return this
};

sap.ui.table.Table.prototype.setShowColumnVisibilityMenu = function(s) {
    this.setProperty("showColumnVisibilityMenu", s);
    var c = this.getColumns();
    for (var i = 0, l = c.length; i < l; i++) {
        if (c[i].getMenu()) {
            c[i].getMenu()._bInvalidated = true
        }
    }
    return this
};

sap.ui.table.Table.prototype._splitterSelectStart = function(e) {
    e.preventDefault();
    e.stopPropagation();
    return false
};

sap.ui.table.Table.prototype._onGhostMouseRelease = function(e) {
    var s = jQuery.sap.domById(this.getId() + "-ghost");
    var n = e.pageY - this.$().offset().top;
    this.setVisibleRowCount(this._calculateRowsToDisplay(n));
    jQuery(s).remove();
    jQuery.sap.byId(this.getId() + "-overlay").remove();
    jQuery(document.body).unbind("selectstart", this._splitterSelectStart);
    jQuery(document).unbind("mouseup", this._onGhostMouseRelease);
    jQuery(document).unbind("mousemove", this._onGhostMouseMove)
};

sap.ui.table.Table.prototype._onGhostMouseMove = function(e) {
    var s = jQuery.sap.domById(this.getId() + "-ghost");
    var m = this.$().offset().top;
    if (e.pageY > m) {
        jQuery(s).css("top", e.pageY + "px")
    }
};

sap.ui.table.Table.prototype._calculateRowsToDisplay = function(h) {
    var $ = this.$();
    var c = jQuery.sap.byId(this.getId()).outerHeight();
    var H = $.find('.sapUiTableColHdrCnt').outerHeight();
    var C = $.find('.sapUiTableCCnt').outerHeight();
    var m = this.getMinAutoRowCount() || 5;
    var r = $.find(".sapUiTableCtrl tr[data-sap-ui-rowindex=0]").outerHeight();
    if (r == null) {
        var R = "sap.ui.table.Table:sapUiTableRowHeight";
        r = parseInt(sap.ui.core.theming.Parameters.get(R), 10)
    }
    var a = h - (c - H - C) - H;
    return Math.max(m, Math.floor(a / r))
};