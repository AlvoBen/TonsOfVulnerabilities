﻿jQuery.sap.declare('sap.ui.table.library-all');
if (!jQuery.sap.isDeclared('sap.ui.table.ColumnMenuRenderer')) {

    /*!
     * SAP UI development toolkit for HTML5 (SAPUI5)
     * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
     * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
     */

    jQuery.sap.declare("sap.ui.table.ColumnMenuRenderer");
    jQuery.sap.require('sap.ui.core.Renderer');
    jQuery.sap.require('sap.ui.commons.MenuRenderer');
    sap.ui.table.ColumnMenuRenderer = sap.ui.core.Renderer.extend(sap.ui.commons.MenuRenderer)
};
if (!jQuery.sap.isDeclared('sap.ui.table.TableRenderer')) {
    jQuery.sap.declare("sap.ui.table.TableRenderer");
    sap.ui.table.TableRenderer = {};
    sap.ui.table.TableRenderer.render = function(r, t) {
        if (!t.getVisible()) {
            return
        }
        t._createRows();
        r.write("<div");
        if (t._bAccMode) {
            var a = [];
            if (t.getToolbar()) {
                a.push(t.getToolbar().getId())
            }
            a.push(t.getId() + "-table");
            r.writeAttribute("aria-owns", a.join(" "));
            r.writeAttribute("aria-readonly", "true");
            if (t.getTitle()) {
                r.writeAttribute("aria-labelledby", t.getTitle().getId())
            }
            if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                r.writeAttribute("aria-multiselectable", "true")
            }
        }
        r.writeControlData(t);
        r.addClass("sapUiTable");
        if (t.getColumnHeaderVisible()) {
            r.addClass("sapUiTableCHdr")
        }
        if (t.getSelectionMode() !== sap.ui.table.SelectionMode.None && t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowOnly) {
            r.addClass("sapUiTableRSel")
        }
        r.addClass("sapUiTableSelMode" + t.getSelectionMode());
        if (t.getNavigationMode() === sap.ui.table.NavigationMode.Scrollbar) {
            r.addClass("sapUiTableVScr")
        }
        if (t.getEditable()) {
            r.addClass("sapUiTableEdt")
        }
        r.addClass("sapUiTableShNoDa");
        if (t.getShowNoData() && t._getRowCount() === 0) {
            r.addClass("sapUiTableEmpty")
        }
        r.writeClasses();
        if (t.getWidth()) {
            r.addStyle("width", t.getWidth())
        }
        r.writeStyles();
        r.write(">");
        if (t.getTitle()) {
            this.renderHeader(r, t, t.getTitle())
        }
        if (t.getToolbar()) {
            this.renderToolbar(r, t, t.getToolbar())
        }
        if (t.getExtension() && t.getExtension().length > 0) {
            this.renderExtensions(r, t, t.getExtension())
        }
        r.write("<div");
        r.addClass("sapUiTableCnt");
        r.writeClasses();
        if (t._bAccMode) {
            r.writeAttribute("aria-describedby", t.getId() + "-ariacount")
        }
        r.write(">");
        this.renderColHdr(r, t);
        this.renderTable(r, t);
        if (t._bAccMode) {
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-ariadesc");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write(t._oResBundle.getText("TBL_TABLE"));
            r.write("</span>");
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-ariacount");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write("</span>");
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-toggleedit");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write(t._oResBundle.getText("TBL_TOGGLE_EDIT_KEY"));
            r.write("</span>");
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-selectrow");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write(t._oResBundle.getText("TBL_ROW_SELECT_KEY"));
            r.write("</span>");
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-selectrowmulti");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write(t._oResBundle.getText("TBL_ROW_SELECT_MULTI_KEY"));
            r.write("</span>");
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-deselectrow");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write(t._oResBundle.getText("TBL_ROW_DESELECT_KEY"));
            r.write("</span>");
            r.write("<span");
            r.writeAttribute("id", t.getId() + "-deselectrowmulti");
            r.addStyle("position", "absolute");
            r.addStyle("top", "-20000px");
            r.writeStyles();
            r.write(">");
            r.write(t._oResBundle.getText("TBL_ROW_DESELECT_MULTI_KEY"));
            r.write("</span>")
        }
        r.write("</div>");
        if (t.getNavigationMode() === sap.ui.table.NavigationMode.Paginator) {
            r.write("<div");
            r.addClass("sapUiTablePaginator");
            r.writeClasses();
            r.write(">");
            if (!t._oPaginator) {
                jQuery.sap.require("sap.ui.commons.Paginator");
                t._oPaginator = new sap.ui.commons.Paginator(t.getId() + "-paginator");
                t._oPaginator.attachPage(jQuery.proxy(t.onvscroll, t))
            }
            r.renderControl(t._oPaginator);
            r.write("</div>")
        }
        if (t.getFooter()) {
            this.renderFooter(r, t, t.getFooter())
        }
        if (t.getVisibleRowCountMode() == sap.ui.table.VisibleRowCountMode.Interactive) {
            this.renderVariableHeight(r, t)
        }
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderHeader = function(r, t, T) {
        r.write("<div");
        r.addClass("sapUiTableHdr");
        r.writeClasses();
        if (t._bAccMode) {
            r.writeAttribute("role", "heading")
        }
        r.write(">");
        r.renderControl(T);
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderToolbar = function(r, t, T) {
        r.write("<div");
        r.addClass("sapUiTableTbr");
        r.writeClasses();
        r.write(">");
        if (T.getStandalone()) {
            T.setStandalone(false)
        }
        r.renderControl(T);
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderExtensions = function(r, t, e) {
        for (var i = 0, l = e.length; i < l; i++) {
            this.renderExtension(r, t, e[i])
        }
    };
    sap.ui.table.TableRenderer.renderExtension = function(r, t, e) {
        r.write("<div");
        r.addClass("sapUiTableExt");
        r.writeClasses();
        r.write(">");
        r.renderControl(e);
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderTable = function(r, t) {
        r.write("<div");
        r.addClass("sapUiTableCCnt");
        r.writeClasses();
        r.write(">");
        this.renderRowHdr(r, t);
        this.renderTableCtrl(r, t);
        this.renderVSb(r, t);
        r.write("</div>");
        this.renderHSb(r, t)
    };
    sap.ui.table.TableRenderer.renderFooter = function(r, t, f) {
        r.write("<div");
        r.addClass("sapUiTableFtr");
        r.writeClasses();
        r.write(">");
        r.renderControl(f);
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderVariableHeight = function(r, t) {
        r.write('<div id="' + t.getId() + '-sb" tabIndex="-1"');
        r.addClass("sapUiTableSplitterBar");
        r.addStyle("height", "5px");
        r.writeClasses();
        r.writeStyles();
        r.write(">");
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderColHdr = function(r, t) {
        r.write("<div");
        r.addClass("sapUiTableColHdrCnt");
        r.writeClasses();
        if (t.getColumnHeaderHeight() > 0) {
            r.addStyle("height", (t.getColumnHeaderHeight() * t._getHeaderRowCount()) + "px")
        }
        if (t._bAccMode && (t.getSelectionMode() === sap.ui.table.SelectionMode.None || t.getSelectionBehavior() === sap.ui.table.SelectionBehavior.RowOnly)) {
            r.writeAttribute("role", "row")
        }
        r.writeStyles();
        r.write(">");
        this.renderColRowHdr(r, t);
        var c = t.getColumns();
        if (t.getFixedColumnCount() > 0) {
            r.write("<div");
            r.addClass("sapUiTableColHdrFixed");
            r.writeClasses();
            r.write(">");
            for (var h = 0; h < t._getHeaderRowCount(); h++) {
                r.write("<div");
                r.addClass("sapUiTableColHdr");
                r.writeClasses();
                r.addStyle("min-width", t._getColumnsWidth(0, t.getFixedColumnCount()) + "px");
                r.writeStyles();
                r.write(">");
                for (var i = 0, l = t.getFixedColumnCount(); i < l; i++) {
                    if (c[i] && c[i].getVisible() && !c[i].getGrouped()) {
                        this.renderCol(r, t, c[i], i, h);
                        if (h == 0) {
                            this.renderColRsz(r, t, c[i], i)
                        }
                    }
                }
                r.write("<p style=\"clear: both;\"></p>");
                r.write("</div>")
            }
            r.write("</div>")
        }
        r.write("<div");
        r.addClass("sapUiTableColHdrScr");
        r.writeClasses();
        if (t.getFixedColumnCount() > 0) {
            if (t._bRtlMode) {
                r.addStyle("margin-right", "0")
            } else {
                r.addStyle("margin-left", "0")
            }
            r.writeStyles()
        }
        r.write(">");
        for (var h = 0; h < t._getHeaderRowCount(); h++) {
            r.write("<div");
            r.addClass("sapUiTableColHdr");
            r.writeClasses();
            r.addStyle("min-width", t._getColumnsWidth(t.getFixedColumnCount(), c.length) + "px");
            r.writeStyles();
            r.write(">");
            for (var i = t.getFixedColumnCount(), l = c.length; i < l; i++) {
                if (c[i].getVisible() && !c[i].getGrouped()) {
                    this.renderCol(r, t, c[i], i, h);
                    if (h == 0) {
                        this.renderColRsz(r, t, c[i], i)
                    }
                }
            }
            r.write("<p style=\"clear: both;\"></p>");
            r.write("</div>")
        }
        r.write("</div>");
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderColRowHdr = function(r, t) {
        r.write("<div");
        r.writeAttribute("id", t.getId() + "-selall");
        if (t.getSelectionMode() == "Multi") {
            r.writeAttributeEscaped("title", t._oResBundle.getText("TBL_SELECT_ALL"))
        }
        r.addClass("sapUiTableColRowHdr");
        r.writeClasses();
        if (t._bAccMode) {
            r.writeAttribute("tabindex", "-1");
            r.writeAttributeEscaped("aria-label", t._oResBundle.getText("TBL_SELECT_ALL_KEY"))
        }
        r.write(">");
        if (t.getSelectionMode() !== sap.ui.table.SelectionMode.Single) {
            r.write("<div");
            r.addClass("sapUiTableColRowHdrIco");
            r.writeClasses();
            if (t.getColumnHeaderHeight() > 0) {
                r.addStyle("height", t.getColumnHeaderHeight() + "px")
            }
            r.write(">");
            r.write("</div>")
        }
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderCol = function(r, t, c, i, h) {
        var l;
        if (c.getMultiLabels().length > 0) {
            l = c.getMultiLabels()[h]
        } else if (h == 0) {
            l = c.getLabel()
        }
        r.write("<div");
        if (h === 0) {
            r.writeElementData(c)
        } else {
            r.writeAttribute('id', c.getId() + "_" + h)
        }
        r.writeAttribute("data-sap-ui-colindex", i);
        if (t._bAccMode) {
            if ( !! sap.ui.Device.browser.internet_explorer) {
                r.writeAttribute("role", "columnheader")
            }
            r.writeAttribute("aria-haspopup", "true");
            r.writeAttribute("tabindex", "-1")
        }
        r.addClass("sapUiTableCol");
        r.writeClasses();
        r.addStyle("width", c.getWidth());
        if (t.getColumnHeaderHeight() > 0) {
            r.addStyle("height", t.getColumnHeaderHeight() + "px")
        }
        r.writeStyles();
        var T = c.getTooltip_AsString();
        if (T) {
            r.writeAttributeEscaped("title", T)
        }
        r.write("><div");
        r.addClass("sapUiTableColCell");
        r.writeClasses();
        r.write(">");
        r.write("<div id=\"" + c.getId() + "-icons\" class=\"sapUiTableColIcons\"></div>");
        if (l) {
            r.renderControl(l)
        }
        r.write("</div></div>")
    };
    sap.ui.table.TableRenderer.renderColRsz = function(r, t, c, i) {
        if (c.getResizable()) {
            r.write("<div");
            r.writeAttribute("id", c.getId() + "-rsz");
            r.writeAttribute("data-sap-ui-colindex", i);
            r.writeAttribute("tabindex", "-1");
            r.addClass("sapUiTableColRsz");
            r.writeClasses();
            r.addStyle("left", t._bRtlMode ? "99000px" : "-99000px");
            r.writeStyles();
            r.write("></div>")
        }
    };
    sap.ui.table.TableRenderer.renderRowHdr = function(r, t) {
        r.write("<div");
        r.addClass("sapUiTableRowHdrScr");
        r.writeClasses();
        r.write(">");
        for (var a = 0, c = t.getRows().length; a < c; a++) {
            this.renderRowHdrRow(r, t, t.getRows()[a], a)
        }
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderRowHdrRow = function(r, t, R, i) {
        r.write("<div");
        r.writeAttribute("id", t.getId() + "-rowsel" + i);
        r.writeAttribute("data-sap-ui-rowindex", i);
        r.addClass("sapUiTableRowHdr");
        r.writeClasses();
        if (t.getRowHeight() > 0) {
            r.addStyle("height", t.getRowHeight() + "px")
        }
        if (t._bAccMode) {
            var c = [];
            jQuery.each(R.getCells(), function(I, C) {
                c.push(R.getId() + "-col" + I)
            });
            if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                r.writeAttribute("aria-selected", "false")
            }
            if (t.getSelectionMode() !== sap.ui.table.SelectionMode.None) {
                r.writeAttributeEscaped("title", t._oResBundle.getText("TBL_ROW_SELECT"));
                if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi && t._oSelection.getSelectedIndices().length > 1) {
                    r.writeAttributeEscaped("aria-label", t._oResBundle.getText("TBL_ROW_SELECT_MULTI_KEY"))
                } else {
                    r.writeAttributeEscaped("aria-label", t._oResBundle.getText("TBL_ROW_SELECT_KEY"))
                }
            }
            r.writeAttribute("tabindex", "-1")
        }
        r.writeStyles();
        r.write("></div>")
    };
    sap.ui.table.TableRenderer.renderTableCtrl = function(r, t) {
        r.write("<div");
        r.addClass("sapUiTableCtrlBefore");
        r.writeClasses();
        r.writeAttribute("tabindex", "0");
        r.write("></div>");
        if (t.getFixedColumnCount() > 0) {
            r.write("<div");
            r.addClass("sapUiTableCtrlScrFixed");
            r.writeClasses();
            r.write(">");
            this.renderTableControl(r, t, true);
            r.write("</div>")
        }
        r.write("<div");
        r.addClass("sapUiTableCtrlScr");
        r.writeClasses();
        if (t.getFixedColumnCount() > 0) {
            if (t._bRtlMode) {
                r.addStyle("margin-right", "0")
            } else {
                r.addStyle("margin-left", "0")
            }
            r.writeStyles()
        }
        r.write(">");
        r.write("<div");
        r.addClass("sapUiTableCtrlCnt");
        r.writeClasses();
        r.write(">");
        this.renderTableControl(r, t, false);
        r.write("</div>");
        r.write("<div");
        r.addClass("sapUiTableCtrlAfter");
        r.writeClasses();
        r.writeAttribute("tabindex", "0");
        r.write("></div>");
        r.write("</div>");
        r.write("<div");
        r.addClass("sapUiTableCtrlEmpty");
        r.writeClasses();
        r.writeAttribute("tabindex", "0");
        r.write(">");
        if (t.getNoData()) {
            r.renderControl(t.getNoData())
        } else {
            r.write("<span");
            r.addClass("sapUiTableCtrlEmptyMsg");
            r.writeClasses();
            r.write(">");
            r.writeEscaped(t._oResBundle.getText("TBL_NO_DATA"));
            r.write("</span>")
        }
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderTableControl = function(r, t, f) {
        var s, e;
        if (f) {
            s = 0;
            e = t.getFixedColumnCount()
        } else {
            s = t.getFixedColumnCount();
            e = t.getColumns().length
        }
        var F = t.getFixedRowCount();
        var R = t.getRows();
        if (F > 0) {
            this.renderTableControlCnt(r, t, f, s, e, true, 0, F)
        }
        this.renderTableControlCnt(r, t, f, s, e, false, F, R.length)
    };
    sap.ui.table.TableRenderer.renderTableControlCnt = function(r, t, f, s, e, F, S, E) {
        r.write("<table");
        var i = t.getId() + "-table";
        if (f) {
            i += "-fixed";
            r.addClass("sapUiTableCtrlFixed")
        } else {
            r.addClass("sapUiTableCtrlScroll")
        }
        if (F) {
            i += "-fixrow";
            r.addClass("sapUiTableCtrlRowFixed")
        } else {
            r.addClass("sapUiTableCtrlRowScroll")
        }
        r.writeAttribute("id", i);
        if (t._bAccMode) {
            r.writeAttribute("role", "grid")
        }
        r.addClass("sapUiTableCtrl");
        r.writeClasses();
        r.addStyle("min-width", t._getColumnsWidth(s, e) + "px");
        if (f && ( !! sap.ui.Device.browser.firefox || !! sap.ui.Device.browser.chrome)) {
            r.addStyle("width", t._getColumnsWidth(s, e) + "px")
        }
        r.writeStyles();
        r.write(">");
        r.write("<thead>");
        r.write("<tr");
        r.addClass("sapUiTableCtrlCol");
        if (S == 0) {
            r.addClass("sapUiTableCtrlFirstCol")
        }
        r.writeClasses();
        r.write(">");
        var c = t.getColumns();
        if (t.getSelectionMode() !== sap.ui.table.SelectionMode.None && t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowOnly) {
            r.write("<th");
            r.addStyle("width", "0px");
            r.writeStyles();
            if (t._bAccMode && S == 0) {
                r.writeAttribute("role", "columnheader");
                r.writeAttribute("scope", "col");
                r.writeAttribute("id", t.getId() + "_colsel")
            }
            r.write("></th>")
        } else {
            if (c.length === 0) {
                r.write("<th></th>")
            }
        }
        for (var a = s, b = e; a < b; a++) {
            var C = c[a];
            if (C && C.getVisible() && !C.getGrouped()) {
                r.write("<th");
                r.addStyle("width", C.getWidth());
                r.writeStyles();
                if (S == 0) {
                    if (t._bAccMode) {
                        r.writeAttribute("aria-owns", C.getId());
                        r.writeAttribute("aria-labelledby", C.getId());
                        r.writeAttribute("role", "columnheader");
                        r.writeAttribute("scope", "col");
                        r.writeAttribute("id", t.getId() + "_col" + a)
                    }
                }
                r.writeAttribute("data-sap-ui-headcolindex", a);
                r.write(">");
                if (S == 0) {
                    if (C.getMultiLabels().length > 0) {
                        r.renderControl(C.getMultiLabels()[0])
                    } else {
                        r.renderControl(C.getLabel())
                    }
                }
                r.write("</th>")
            }
        }
        r.write("</tr>");
        r.write("</thead>");
        r.write("<tbody>");
        var R = t.getRows();
        for (var d = S, b = E; d < b; d++) {
            this.renderTableRow(r, t, R[d], d, f, s, e, false)
        }
        r.write("</tbody>");
        r.write("</table>")
    };
    sap.ui.table.TableRenderer.renderTableRow = function(r, t, R, i, f, s, e, F) {
        r.write("<tr");
        r.addClass("sapUiTableTr");
        if (f) {
            r.writeAttribute("id", R.getId() + "-fixed")
        } else {
            r.writeElementData(R)
        }
        if (R._bHidden) {
            r.addClass("sapUiTableRowHidden")
        }
        if (i % 2 === 0) {
            r.addClass("sapUiTableRowEven")
        } else {
            r.addClass("sapUiTableRowOdd")
        }
        r.writeClasses();
        r.writeAttribute("data-sap-ui-rowindex", i);
        if (t.getRowHeight() > 0) {
            r.addStyle("height", t.getRowHeight() + "px")
        }
        r.writeStyles();
        if (t._bAccMode) {
            r.writeAttribute("role", "row");
            if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                r.writeAttribute("aria-selected", "false")
            }
        }
        r.write(">");
        var c = R.getCells();
        if (t.getSelectionMode() !== sap.ui.table.SelectionMode.None && t.getSelectionBehavior() !== sap.ui.table.SelectionBehavior.RowOnly) {
            r.write("<td");
            if (t._bAccMode) {
                r.writeAttribute("role", "gridcell");
                r.writeAttribute("headers", t.getId() + "_colsel");
                r.writeAttribute("aria-owns", t.getId() + "-rowsel" + i);
                if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                    r.writeAttribute("aria-selected", "false")
                }
            }
            r.write(">");
            if (t._bAccMode) {
                r.write("<div");
                r.addClass("sapUiTableAriaRowSel");
                r.writeClasses();
                r.write(">");
                r.write(t._oResBundle.getText("TBL_ROW_SELECT_KEY"));
                r.write("</div>")
            }
            r.write("</td>")
        } else {
            if (c.length === 0) {
                r.write("<td");
                if (t._bAccMode) {
                    r.writeAttribute("role", "gridcell");
                    r.writeAttribute("headers", t.getId() + "_colsel");
                    r.writeAttribute("aria-owns", t.getId() + "-rowsel" + i);
                    if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                        r.writeAttribute("aria-selected", "false")
                    }
                }
                r.write(">");
                if (t._bAccMode) {
                    r.write("<div");
                    r.addClass("sapUiTableAriaRowSel");
                    r.writeClasses();
                    r.write(">");
                    r.write(t._oResBundle.getText("TBL_ROW_SELECT_KEY"));
                    r.write("</div>")
                }
                r.write("</td>")
            }
        }
        for (var a = 0, b = c.length; a < b; a++) {
            this.renderTableCell(r, t, R, c[a], a, f, s, e)
        }
        r.write("</tr>")
    };
    sap.ui.table.TableRenderer.renderTableCell = function(r, t, R, c, C, f, s, e) {
        var i = c.data("sap-ui-colindex");
        var o = t.getColumns()[i];
        if (o.getVisible() && !o.getGrouped() && s <= i && e > i) {
            r.write("<td");
            var I = R.getId() + "-col" + C;
            r.writeAttribute("id", I);
            if (t._bAccMode) {
                r.writeAttribute("headers", t.getId() + "_col" + i);
                r.writeAttribute("role", "gridcell");
                r.writeAttribute("aria-labelledby", t.getId() + "-ariadesc " + o.getId() + " " + c.getId());
                r.writeAttribute("aria-describedby", t.getId() + "-toggleedit");
                r.writeAttribute("aria-activedescendant", c.getId());
                r.writeAttribute("tabindex", "-1");
                if (t.getSelectionMode() === sap.ui.table.SelectionMode.Multi) {
                    r.writeAttribute("aria-selected", "false")
                }
            }
            var h = this.getHAlign(o.getHAlign(), t._bRtlMode);
            if (h) {
                r.addStyle("text-align", h)
            }
            r.writeStyles();
            if (C === 0) {
                r.addClass("sapUiTableTdFirst")
            }
            r.writeClasses();
            r.write("><div");
            r.addClass("sapUiTableCell");
            r.writeClasses();
            r.write(">");
            this.renderTableCellControl(r, t, c, C);
            r.write("</div></td>")
        }
    };
    sap.ui.table.TableRenderer.renderTableCellControl = function(r, t, c, C) {
        r.renderControl(c)
    };
    sap.ui.table.TableRenderer.renderVSb = function(r, t) {
        r.write("<div");
        r.addClass("sapUiTableVSb");
        r.writeClasses();
        r.write(">");
        r.renderControl(t._oVSb);
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.renderHSb = function(r, t) {
        r.write("<div");
        r.addClass("sapUiTableHSb");
        r.writeClasses();
        r.write(">");
        r.renderControl(t._oHSb);
        r.write("</div>")
    };
    sap.ui.table.TableRenderer.getHAlign = function(h, r) {
        switch (h) {
            case sap.ui.commons.layout.HAlign.Center:
                return "center";
            case sap.ui.commons.layout.HAlign.End:
            case sap.ui.commons.layout.HAlign.Right:
                return r ? "left":
                    "right"
        }
        return r ? "right" : "left"
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.TreeTableRenderer')) {
    jQuery.sap.declare("sap.ui.table.TreeTableRenderer");
    jQuery.sap.require('sap.ui.core.Renderer');
    sap.ui.table.TreeTableRenderer = sap.ui.core.Renderer.extend(sap.ui.table.TableRenderer);
    sap.ui.table.TreeTableRenderer.renderTableCellControl = function(r, t, c, C) {
        if (t.isTreeBinding("rows") && C === 0) {
            r.write("<span class=\"sapUiTableTreeIcon sapUiTableTreeIconLeaf\" tabindex=\"-1\">&nbsp;</span>")
        }
        r.renderControl(c)
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.library')) {
    jQuery.sap.declare("sap.ui.table.library");
    jQuery.sap.require('sap.ui.core.Core');
    jQuery.sap.require('sap.ui.core.library');
    jQuery.sap.require('sap.ui.commons.library');
    sap.ui.getCore().initLibrary({
        name: "sap.ui.table",
        dependencies: ["sap.ui.core", "sap.ui.commons"],
        types: ["sap.ui.table.NavigationMode", "sap.ui.table.SelectionBehavior", "sap.ui.table.SelectionMode", "sap.ui.table.SortOrder", "sap.ui.table.VisibleRowCountMode"],
        interfaces: [],
        controls: ["sap.ui.table.ColumnMenu", "sap.ui.table.DataTable", "sap.ui.table.Table", "sap.ui.table.TreeTable"],
        elements: ["sap.ui.table.Column", "sap.ui.table.Row"],
        version: "1.16.8-SNAPSHOT"
    });
    jQuery.sap.declare("sap.ui.table.NavigationMode");
    sap.ui.table.NavigationMode = {
        Scrollbar: "Scrollbar",
        Paginator: "Paginator"
    };
    jQuery.sap.declare("sap.ui.table.SelectionBehavior");
    sap.ui.table.SelectionBehavior = {
        Row: "Row",
        RowSelector: "RowSelector",
        RowOnly: "RowOnly"
    };
    jQuery.sap.declare("sap.ui.table.SelectionMode");
    sap.ui.table.SelectionMode = {
        Multi: "Multi",
        Single: "Single",
        None: "None"
    };
    jQuery.sap.declare("sap.ui.table.SortOrder");
    sap.ui.table.SortOrder = {
        Ascending: "Ascending",
        Descending: "Descending"
    };
    jQuery.sap.declare("sap.ui.table.VisibleRowCountMode");
    sap.ui.table.VisibleRowCountMode = {
        Fixed: "Fixed",
        Interactive: "Interactive",
        Auto: "Auto"
    };
    sap.ui.table.ColumnHeader = sap.ui.table.Column;
    sap.ui.table.SelectionMode.All = sap.ui.table.SelectionMode.Multi;
    sap.ui.table.SelectionMode.MultiToggle = sap.ui.table.SelectionMode.Multi
};
if (!jQuery.sap.isDeclared('sap.ui.table.Column')) {
    jQuery.sap.declare("sap.ui.table.Column");
    jQuery.sap.require('sap.ui.core.Element');
    sap.ui.core.Element.extend("sap.ui.table.Column", {
        metadata: {
            publicMethods: ["sort", "toggleSort"],
            library: "sap.ui.table",
            properties: {
                "width": {
                    type: "sap.ui.core.CSSSize",
                    group: "Dimension",
                    defaultValue: null
                },
                "flexible": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: true
                },
                "resizable": {
                    type: "boolean",
                    group: "Behavior",
                    defaultValue: true
                },
                "hAlign": {
                    type: "sap.ui.commons.layout.HAlign",
                    group: "Appearance",
                    defaultValue: sap.ui.commons.layout.HAlign.Begin
                },
                "sorted": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: false
                },
                "sortOrder": {
                    type: "sap.ui.table.SortOrder",
                    group: "Appearance",
                    defaultValue: sap.ui.table.SortOrder.Ascending
                },
                "sortProperty": {
                    type: "string",
                    group: "Behavior",
                    defaultValue: null
                },
                "filtered": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: false
                },
                "filterProperty": {
                    type: "string",
                    group: "Behavior",
                    defaultValue: null
                },
                "filterValue": {
                    type: "string",
                    group: "Behavior",
                    defaultValue: null
                },
                "filterOperator": {
                    type: "string",
                    group: "Behavior",
                    defaultValue: null
                },
                "grouped": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: false
                },
                "visible": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: true
                },
                "filterType": {
                    type: "any",
                    group: "Misc",
                    defaultValue: null
                },
                "name": {
                    type: "string",
                    group: "Appearance",
                    defaultValue: null
                },
                "showFilterMenuEntry": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: true
                },
                "showSortMenuEntry": {
                    type: "boolean",
                    group: "Appearance",
                    defaultValue: true
                }
            },
            defaultAggregation: "label",
            aggregations: {
                "label": {
                    type: "sap.ui.core.Control",
                    multiple: false
                },
                "multiLabels": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "multiLabel"
                },
                "template": {
                    type: "sap.ui.core.Control",
                    multiple: false
                },
                "menu": {
                    type: "sap.ui.commons.Menu",
                    multiple: false
                }
            }
        }
    });
    jQuery.sap.require('sap.ui.core.RenderManager');
    jQuery.sap.require('sap.ui.model.Type');
    jQuery.sap.require('sap.ui.model.Filter');
    jQuery.sap.require('sap.ui.model.Sorter');
    jQuery.sap.require('sap.ui.commons.Image');
    jQuery.sap.require('sap.ui.commons.Label');
    jQuery.sap.require('sap.ui.commons.TextField');
    jQuery.sap.require('sap.ui.model.type.String');
    sap.ui.table.Column._DEFAULT_FILTER_TYPE = new sap.ui.model.type.String();
    sap.ui.table.Column.prototype.init = function() {
        this.oResBundle = sap.ui.getCore().getLibraryResourceBundle("sap.ui.table");
        this._oSorter = null
    };
    sap.ui.table.Column.prototype.exit = function() {
        var s = sap.ui.getCore().byId(this.getId() + "-sortIcon");
        if (s) {
            s.destroy()
        }
        var f = sap.ui.getCore().byId(this.getId() + "-filterIcon");
        if (f) {
            f.destroy()
        }
    };
    sap.ui.table.Column.prototype.invalidate = function(o) {
        if (o !== this.getTemplate() && !(o instanceof sap.ui.table.ColumnMenu)) {
            sap.ui.core.Element.prototype.invalidate.apply(this, arguments)
        }
    };
    sap.ui.table.Column.prototype.setVisible = function(v) {
        var t = this.getParent();
        var e = true;
        if (t) {
            e = t.fireColumnVisibility({
                column: this,
                newVisible: v
            })
        }
        if (e) {
            this.setProperty("visible", v)
        }
        return this
    };
    sap.ui.table.Column.prototype.setLabel = function(l) {
        var L = l;
        if (typeof(l) === "string") {
            L = new sap.ui.commons.Label({
                text: l
            })
        }
        this.setAggregation("label", L);
        return this
    };
    sap.ui.table.Column.prototype.setTemplate = function(t) {
        var T = t;
        if (typeof(t) === "string") {
            T = new sap.ui.commons.TextView().bindText(t)
        }
        this.setAggregation("template", T);
        this.invalidate();
        return this
    };
    sap.ui.table.Column.prototype.getMenu = function() {
        var m = this.getAggregation("menu");
        if (!m) {
            m = this._createMenu();
            this.setMenu(m)
        }
        return m
    };
    sap.ui.table.Column.prototype.setMenu = function(m) {
        this.setAggregation("menu", m, true);
        return this
    };
    sap.ui.table.Column.prototype._createMenu = function() {
        jQuery.sap.require("sap.ui.table.ColumnMenu");
        return new sap.ui.table.ColumnMenu(this.getId() + "-menu")
    };
    sap.ui.table.Column.prototype.setWidth = function(w) {
        this.setProperty("width", w);
        return this
    };
    sap.ui.table.Column.prototype.setSorted = function(f) {
        this.setProperty("sorted", f, true);
        this._renderSortIcon();
        return this
    };
    sap.ui.table.Column.prototype.setSortOrder = function(t) {
        this.setProperty("sortOrder", t, true);
        this._renderSortIcon();
        return this
    };
    sap.ui.table.Column.prototype.setFiltered = function(f) {
        this.setProperty("filtered", f, true);
        this._renderFilterIcon();
        return this
    };
    sap.ui.table.Column.prototype.setFilterValue = function(v) {
        this.setProperty("filterValue", v, true);
        if (this.getMenu()) {
            this.getMenu()._setFilterValue(v)
        }
        return this
    };
    sap.ui.table.Column.prototype.onmousedown = function(e) {
        var m = this.getAggregation("menu");
        this._bSkipOpen = m && m.bOpen
    };
    sap.ui.table.Column.prototype.onmouseout = function(e) {
        if (this._bSkipOpen && jQuery.sap.checkMouseEnterOrLeave(e, jQuery.sap.domById(this.getId()))) {
            this._bSkipOpen = false
        }
    };
    sap.ui.table.Column.prototype._openMenu = function() {
        if (this._bSkipOpen) {
            this._bSkipOpen = false;
            return
        }
        var m = this.getMenu();
        var e = sap.ui.core.Popup.Dock;
        m.open(false, this.getFocusDomRef(), e.BeginTop, e.BeginBottom, this.getDomRef(), "none none")
    };
    sap.ui.table.Column.prototype.toggleSort = function() {
        this.sort(this.getSorted() && this.getSortOrder() === sap.ui.table.SortOrder.Ascending)
    };
    sap.ui.table.Column.prototype.sort = function(d, a) {
        var t = this.getParent();
        if (t) {
            var n = d ? sap.ui.table.SortOrder.Descending : sap.ui.table.SortOrder.Ascending;
            var e = t.fireSort({
                column: this,
                sortOrder: n,
                columnAdded: a
            });
            if (e) {
                var s = [];
                var c = t.getColumns();
                if (a) {
                    for (var i = 0, l = c.length; i < l; i++) {
                        if (c[i] == this) {
                            this.setProperty("sorted", true, true);
                            this.setProperty("sortOrder", n, true);
                            this._oSorter = new sap.ui.model.Sorter(this.getSortProperty(), this.getSortOrder() === sap.ui.table.SortOrder.Descending);
                            s.push(this._oSorter)
                        } else {
                            var S = c[i]._oSorter;
                            if (S) {
                                s.push(S)
                            }
                        }
                    }
                } else {
                    for (var i = 0, l = c.length; i < l; i++) {
                        if (c[i] !== this) {
                            c[i].setProperty("sorted", false, true);
                            c[i].setProperty("sortOrder", sap.ui.table.SortOrder.Ascending, true);
                            c[i]._renderSortIcon()
                        }
                    }
                    this.setProperty("sorted", true, true);
                    this.setProperty("sortOrder", n, true);
                    this._oSorter = new sap.ui.model.Sorter(this.getSortProperty(), this.getSortOrder() === sap.ui.table.SortOrder.Descending);
                    s.push(this._oSorter)
                }
                if (t.isBound("rows")) {
                    t.getBinding("rows").sort(s)
                }
                this._renderSortIcon()
            }
        }
        return this
    };
    sap.ui.table.Column.prototype._renderSortIcon = function() {
        var t = this.getParent();
        if (t && t.getDomRef()) {
            if (this.getSorted()) {
                var c = sap.ui.getCore().getConfiguration().getTheme();
                var i = sap.ui.getCore().byId(this.getId() + "-sortIcon") || new sap.ui.commons.Image(this.getId() + "-sortIcon");
                i.addStyleClass("sapUiTableColIconsOrder");
                if (this.getSortOrder() === sap.ui.table.SortOrder.Ascending) {
                    i.setSrc(sap.ui.resource("sap.ui.table", "themes/" + c + "/img/ico12_sort_asc.gif"))
                } else {
                    i.setSrc(sap.ui.resource("sap.ui.table", "themes/" + c + "/img/ico12_sort_desc.gif"))
                }
                var r = new sap.ui.core.RenderManager();
                var h = r.getHTML(i);
                this.$().find(".sapUiTableColIconsOrder").remove();
                jQuery(h).prependTo(jQuery.sap.domById(this.getId() + "-icons"));
                this.$().attr("aria-sort", this.getSortOrder() === sap.ui.table.SortOrder.Ascending ? "ascending" : "descending");
                this.$().find(".sapUiTableColCell").addClass("sapUiTableColSorted")
            } else {
                this.$().find(".sapUiTableColIconsOrder").remove();
                this.$().removeAttr("aria-sort");
                this.$().find(".sapUiTableColCell").removeClass("sapUiTableColSorted")
            }
        }
    };
    sap.ui.table.Column.prototype._getFilter = function() {
        var f = undefined,
            p = this.getFilterProperty(),
            v = this.getFilterValue(),
            o = this.getFilterOperator(),
            P, s, t = this.getFilterType() || sap.ui.table.Column._DEFAULT_FILTER_TYPE,
            i = t instanceof sap.ui.model.type.String,
            b;
        if (v) {
            if (!o) {
                b = v.match(/(.*)\s*\.\.\s*(.*)/);
                if (v.indexOf("=") == 0) {
                    o = sap.ui.model.FilterOperator.EQ;
                    P = v.substr(1)
                } else if (v.indexOf("!=") == 0) {
                    o = sap.ui.model.FilterOperator.NE;
                    P = v.substr(2)
                } else if (v.indexOf("<=") == 0) {
                    o = sap.ui.model.FilterOperator.LE;
                    P = v.substr(2)
                } else if (v.indexOf("<") == 0) {
                    o = sap.ui.model.FilterOperator.LT;
                    P = v.substr(1)
                } else if (v.indexOf(">=") == 0) {
                    o = sap.ui.model.FilterOperator.GE;
                    P = v.substr(2)
                } else if (v.indexOf(">") == 0) {
                    o = sap.ui.model.FilterOperator.GT;
                    P = v.substr(1)
                } else if (b) {
                    if (b[1] && b[2]) {
                        o = sap.ui.model.FilterOperator.BT;
                        P = b[1];
                        s = b[2]
                    } else if (b[1] && !b[2]) {
                        o = sap.ui.model.FilterOperator.GE;
                        P = b[1]
                    } else {
                        o = sap.ui.model.FilterOperator.LE;
                        P = b[2]
                    }
                } else if (i && v.indexOf("*") == 0 && v.lastIndexOf("*") == v.length - 1) {
                    o = sap.ui.model.FilterOperator.Contains;
                    P = v.substr(1, v.length - 2)
                } else if (i && v.indexOf("*") == 0) {
                    o = sap.ui.model.FilterOperator.EndsWith;
                    P = v.substr(1)
                } else if (i && v.lastIndexOf("*") == v.length - 1) {
                    o = sap.ui.model.FilterOperator.StartsWith;
                    P = v.substr(0, v.length - 1)
                } else {
                    if (i) {
                        o = sap.ui.model.FilterOperator.Contains
                    } else {
                        o = sap.ui.model.FilterOperator.EQ
                    }
                    P = v.substr(0)
                }
                if (!s) {
                    f = new sap.ui.model.Filter(p, o, this._parseFilterValue(P))
                } else {
                    f = new sap.ui.model.Filter(p, o, this._parseFilterValue(P), this._parseFilterValue(s))
                }
            } else {
                f = new sap.ui.model.Filter(p, o, this._parseFilterValue(v))
            }
        }
        return f
    };
    sap.ui.table.Column.prototype.filter = function(v) {
        var t = this.getParent();
        if (t.isBound("rows")) {
            var e = t.fireFilter({
                column: this,
                value: v
            });
            if (e) {
                this.setProperty("filtered", !! v, true);
                this.setProperty("filterValue", v, true);
                var f = [];
                var c = t.getColumns();
                for (var i = 0, l = c.length; i < l; i++) {
                    var F = c[i]._getFilter();
                    if (F) {
                        f.push(F)
                    }
                }
                t.getBinding("rows").filter(f, sap.ui.model.FilterType.Control);
                this._renderFilterIcon()
            }
        }
        return this
    };
    sap.ui.table.Column.prototype._parseFilterValue = function(v) {
        var f = this.getFilterType();
        if (f) {
            if (jQuery.isFunction(f)) {
                v = f(v)
            } else {
                try {
                    v = f.parseValue(v, "string")
                } catch (e) {
                    jQuery.sap.log.error(e.message)
                }
            }
        }
        return v
    };
    sap.ui.table.Column.prototype._renderFilterIcon = function() {
        var t = this.getParent();
        if (t && t.getDomRef()) {
            var c = sap.ui.getCore().getConfiguration().getTheme();
            var i = sap.ui.getCore().byId(this.getId() + "-filterIcon") || new sap.ui.commons.Image(this.getId() + "-filterIcon");
            jQuery.sap.byId(i.getId()).remove();
            i.addStyleClass("sapUiTableColIconsFilter");
            if (this.getFiltered()) {
                i.setSrc(sap.ui.resource("sap.ui.table", "themes/" + c + "/img/ico12_filter.gif"));
                var r = new sap.ui.core.RenderManager();
                var h = r.getHTML(i);
                jQuery(h).prependTo(jQuery.sap.domById(this.getId() + "-icons"));
                this.$().find(".sapUiTableColCell").addClass("sapUiTableColFiltered")
            } else {
                this.$().find(".sapUiTableColCell").removeClass("sapUiTableColFiltered")
            }
        }
    };
    sap.ui.table.Column.prototype._restoreIcons = function() {
        if (this.getSorted()) {
            this._renderSortIcon()
        }
        if (this.getFiltered()) {
            this._renderFilterIcon()
        }
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.ColumnMenu')) {
    jQuery.sap.declare("sap.ui.table.ColumnMenu");
    jQuery.sap.require('sap.ui.commons.Menu');
    sap.ui.commons.Menu.extend("sap.ui.table.ColumnMenu", {
        metadata: {
            library: "sap.ui.table"
        }
    });
    jQuery.sap.require('sap.ui.core.RenderManager');
    jQuery.sap.require('sap.ui.commons.Menu');
    jQuery.sap.require('sap.ui.commons.MenuItem');
    jQuery.sap.require('sap.ui.commons.TextField');
    sap.ui.table.ColumnMenu.prototype.init = function() {
        this.addStyleClass("sapUiTableColumnMenu");
        this.oResBundle = sap.ui.getCore().getLibraryResourceBundle("sap.ui.table");
        this._bInvalidated = true;
        this._iPopupClosedTimeoutId = null;
        this._oColumn = null;
        this._oTable = null;
        this._attachPopupClosed()
    };
    sap.ui.table.ColumnMenu.prototype.exit = function() {
        window.clearTimeout(this._iPopupClosedTimeoutId);
        this._detachEvents();
        this._oColumn = this._oTable = null
    };
    sap.ui.table.ColumnMenu.prototype.onThemeChanged = function() {
        if (this.getDomRef()) {
            this._invalidate()
        }
    };
    sap.ui.table.ColumnMenu.prototype.setParent = function(p) {
        this._detachEvents();
        this._invalidate();
        this._oColumn = p;
        if (p) {
            this._oTable = this._oColumn.getParent();
            if (this._oTable) {}
        }
        this._attachEvents();
        return sap.ui.commons.Menu.prototype.setParent.apply(this, arguments)
    };
    sap.ui.table.ColumnMenu.prototype._attachEvents = function() {
        if (this._oTable) {
            this._oTable.attachColumnVisibility(this._invalidate, this);
            this._oTable.attachColumnMove(this._invalidate, this)
        }
    };
    sap.ui.table.ColumnMenu.prototype._detachEvents = function() {
        if (this._oTable) {
            this._oTable.detachColumnVisibility(this._invalidate, this);
            this._oTable.detachColumnMove(this._invalidate, this)
        }
    };
    sap.ui.table.ColumnMenu.prototype._invalidate = function() {
        this._bInvalidated = true
    };
    sap.ui.table.ColumnMenu.prototype._attachPopupClosed = function() {
        var t = this;
        if (!sap.ui.Device.support.touch) {
            this.getPopup().attachClosed(function(e) {
                t._iPopupClosedTimeoutId = window.setTimeout(function() {
                    if (t._oColumn) {
                        t._oColumn.focus()
                    }
                }, 0)
            })
        }
    };
    sap.ui.table.ColumnMenu.prototype.open = function() {
        if (this._bInvalidated) {
            this._bInvalidated = false;
            this.destroyItems();
            this._addMenuItems()
        }
        if (this.getItems().length > 0) {
            sap.ui.commons.Menu.prototype.open.apply(this, arguments)
        }
    };
    sap.ui.table.ColumnMenu.prototype._addMenuItems = function() {
        if (this._oColumn) {
            this._addSortMenuItem(false);
            this._addSortMenuItem(true);
            this._addFilterMenuItem();
            this._addGroupMenuItem();
            this._addColumnVisibilityMenuItem()
        }
    };
    sap.ui.table.ColumnMenu.prototype._addSortMenuItem = function(d) {
        var c = this._oColumn;
        var D = d ? "desc" : "asc";
        if (c.getSortProperty() && c.getShowSortMenuEntry()) {
            this.addItem(this._createMenuItem(D, "TBL_SORT_" + D.toUpperCase(), "ico12_sort_" + D + ".gif", function(e) {
                c.sort(d, e.getParameter("ctrlKey") === true)
            }))
        }
    };
    sap.ui.table.ColumnMenu.prototype._addFilterMenuItem = function() {
        var c = this._oColumn;
        if (c.getFilterProperty() && c.getShowFilterMenuEntry()) {
            this.addItem(this._createMenuTextFieldItem("filter", "TBL_FILTER", "ico12_filter.gif", c.getFilterValue(), function(e) {
                c.filter(this.getValue())
            }))
        }
    };
    sap.ui.table.ColumnMenu.prototype._addGroupMenuItem = function() {
        var c = this._oColumn;
        var t = this._oTable;
        if (t && t.getEnableGrouping() && c.getSortProperty()) {
            this.addItem(this._createMenuItem("group", "TBL_GROUP", null, jQuery.proxy(function(e) {
                t.setGroupBy(c)
            }, this)))
        }
    };
    sap.ui.table.ColumnMenu.prototype._addColumnVisibilityMenuItem = function() {
        var t = this._oTable;
        if (t && t.getShowColumnVisibilityMenu()) {
            var c = this._createMenuItem("column-visibilty", "TBL_COLUMNS");
            this.addItem(c);
            var C = new sap.ui.commons.Menu(c.getId() + "-menu");
            C.addStyleClass("sapUiTableColumnVisibilityMenu");
            c.setSubmenu(C);
            var a = t.getColumns();
            for (var i = 0, l = a.length; i < l; i++) {
                var m = this._createColumnVisibilityMenuItem(C.getId() + "-item-" + i, a[i]);
                C.addItem(m)
            }
        }
    };
    sap.ui.table.ColumnMenu.prototype._createColumnVisibilityMenuItem = function(i, c) {
        var t = c.getName() || (c.getLabel() && c.getLabel().getText ? c.getLabel().getText() : null);
        return new sap.ui.commons.MenuItem(i, {
            text: t,
            icon: c.getVisible() ? this._getThemedIcon("ico_tick.png") : null,
            select: jQuery.proxy(function(e) {
                var m = e.getSource();
                var v = !c.getVisible();
                if (v || this._oTable._getVisibleColumnCount() > 1) {
                    c.setVisible(v);
                    m.setIcon(v ? this._getThemedIcon("ico_tick.png") : null)
                }
            }, this)
        })
    };
    sap.ui.table.ColumnMenu.prototype._createMenuItem = function(i, t, I, h) {
        return new sap.ui.commons.MenuItem(this.getId() + "-" + i, {
            text: this.oResBundle.getText(t),
            icon: I ? this._getThemedIcon(I) : null,
            select: h || function() {}
        })
    };
    sap.ui.table.ColumnMenu.prototype._createMenuTextFieldItem = function(i, t, I, v, h) {
        jQuery.sap.require("sap.ui.commons.MenuTextFieldItem");
        h = h || function() {};
        return new sap.ui.commons.MenuTextFieldItem(this.getId() + "-" + i, {
            label: this.oResBundle.getText(t),
            icon: I ? this._getThemedIcon(I) : null,
            value: v,
            select: h || function() {}
        })
    };
    sap.ui.table.ColumnMenu.prototype._getThemedIcon = function(i) {
        var c = sap.ui.getCore().getConfiguration().getTheme();
        return sap.ui.resource("sap.ui.table", "themes/" + c + "/img/" + i)
    };
    sap.ui.table.ColumnMenu.prototype._setFilterValue = function(v) {
        var f = sap.ui.getCore().byId(this.getId() + "-filter");
        if (f) {
            f.setValue(v)
        }
        return this
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.DataTableRenderer')) {
    jQuery.sap.declare("sap.ui.table.DataTableRenderer");
    jQuery.sap.require('sap.ui.core.Renderer');
    sap.ui.table.DataTableRenderer = sap.ui.core.Renderer.extend(sap.ui.table.TreeTableRenderer)
};
if (!jQuery.sap.isDeclared('sap.ui.table.Row')) {
    jQuery.sap.declare("sap.ui.table.Row");
    jQuery.sap.require('sap.ui.core.Element');
    sap.ui.core.Element.extend("sap.ui.table.Row", {
        metadata: {
            publicMethods: ["getIndex"],
            library: "sap.ui.table",
            defaultAggregation: "cells",
            aggregations: {
                "cells": {
                    type: "sap.ui.core.Control",
                    multiple: true,
                    singularName: "cell"
                }
            }
        }
    });
    sap.ui.table.Row.prototype.getIndex = function() {
        var t = this.getParent();
        if (t) {
            var f = t.getFirstVisibleRow();
            var r = t.indexOfRow(this);
            return f + r
        }
        return -1
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.Table')) {
    jQuery.sap.declare("sap.ui.table.Table");
    jQuery.sap.require('sap.ui.core.Control');
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
    jQuery.sap.require('sap.ui.model.SelectionModel');
    jQuery.sap.require('sap.ui.core.delegate.ItemNavigation');
    jQuery.sap.require('sap.ui.core.theming.Parameters');
    jQuery.sap.require('sap.ui.core.ScrollBar');
    jQuery.sap.require('sap.ui.core.IntervalTrigger');
    jQuery.sap.require('sap.ui.commons.TextView');
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
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.TreeTable')) {
    jQuery.sap.declare("sap.ui.table.TreeTable");
    sap.ui.table.Table.extend("sap.ui.table.TreeTable", {
        metadata: {
            publicMethods: ["expand", "collapse", "isExpanded"],
            library: "sap.ui.table",
            properties: {
                "expandFirstLevel": {
                    type: "boolean",
                    group: "",
                    defaultValue: false
                }
            },
            events: {
                "toggleOpenState": {}
            }
        }
    });
    sap.ui.table.TreeTable.M_EVENTS = {
        'toggleOpenState': 'toggleOpenState'
    };
    sap.ui.table.TreeTable.prototype.init = function() {
        sap.ui.table.Table.prototype.init.apply(this, arguments);
        this._iLastFixedColIndex = 0
    };
    sap.ui.table.TreeTable.prototype.onAfterRendering = function() {
        sap.ui.table.Table.prototype.onAfterRendering.apply(this, arguments);
        this.$().find("[role=grid]").attr("role", "treegrid")
    };
    sap.ui.table.TreeTable.prototype.isTreeBinding = function(n) {
        n = n || "rows";
        if (n === "rows") {
            return true
        }
        return sap.ui.core.Element.prototype.isTreeBinding.apply(this, n)
    };
    sap.ui.table.TreeTable.prototype.getBinding = function(n) {
        n = n || "rows";
        var b = sap.ui.core.Element.prototype.getBinding.call(this, n);
        if (b && this.isTreeBinding(n) && n === "rows" && !b.getLength) {
            var t = this;
            jQuery.extend(b, {
                _init: function(e) {
                    this.mContextInfo = {};
                    this._initContexts();
                    if (e) {
                        var t = this;
                        if (this.aContexts) {
                            jQuery.each(this.aContexts.slice(), function(i, c) {
                                t._loadChildContexts(c);
                                t._getContextInfo(c).bExpanded = true
                            })
                        }
                    }
                },
                _initContexts: function() {
                    this.aContexts = this.getRootContexts();
                    for (var i = 0, l = this.aContexts.length; i < l; i++) {
                        var o = this._getContextInfo(this.aContexts[i]);
                        this._setContextInfo({
                            oContext: this.aContexts[i],
                            iLevel: 0,
                            bExpanded: o ? o.bExpanded : false,
                            bHasChildren: this._hasChildContexts(this.aContexts[i])
                        })
                    }
                },
                _fnFireFilter: b._fireFilter,
                _fireFilter: function() {
                    this._fnFireFilter.apply(this, arguments);
                    this._initContexts();
                    this._restoreContexts(this.aContexts)
                },
                _fnFireChange: b._fireChange,
                _fireChange: function() {
                    this._fnFireChange.apply(this, arguments);
                    this._initContexts();
                    this._restoreContexts(this.aContexts)
                },
                _restoreContexts: function(c) {
                    var t = this;
                    var N = [];
                    jQuery.each(c.slice(), function(i, C) {
                        var o = t._getContextInfo(C);
                        if (o && o.bExpanded) {
                            N.push.apply(N, t._loadChildContexts(C))
                        }
                    });
                    if (N.length > 0) {
                        this._restoreContexts(N)
                    }
                },
                _loadChildContexts: function(c) {
                    var C = this._getContextInfo(c);
                    var I = jQuery.inArray(c, this.aContexts);
                    var N = this.getNodeContexts(c);
                    for (var i = 0, l = N.length; i < l; i++) {
                        this.aContexts.splice(I + i + 1, 0, N[i]);
                        var o = this._getContextInfo(N[i]);
                        this._setContextInfo({
                            oParentContext: c,
                            oContext: N[i],
                            iLevel: C.iLevel + 1,
                            bExpanded: o ? o.bExpanded : false,
                            bHasChildren: this._hasChildContexts(N[i])
                        })
                    }
                    return N
                },
                _hasChildContexts: function(c) {
                    return this.getNodeContexts(c).length > 0
                },
                _getContextInfo: function(c) {
                    return c ? this.mContextInfo[c.getPath()] : undefined
                },
                _setContextInfo: function(d) {
                    if (d && d.oContext) {
                        this.mContextInfo[d.oContext.getPath()] = d
                    }
                },
                getLength: function() {
                    return this.aContexts ? this.aContexts.length : 0
                },
                getContexts: function(s, l) {
                    return this.aContexts.slice(s, s + l)
                },
                getLevel: function(c) {
                    var C = this._getContextInfo(c);
                    return C ? C.iLevel : -1
                },
                isExpanded: function(c) {
                    var C = this._getContextInfo(c);
                    return C ? C.bExpanded : false
                },
                hasChildren: function(c) {
                    var C = this._getContextInfo(c);
                    return C ? C.bHasChildren : false
                },
                expandContext: function(c) {
                    var C = this._getContextInfo(c);
                    if (C && !C.bExpanded) {
                        this.storeSelection();
                        this._loadChildContexts(c);
                        C.bExpanded = true;
                        this._fireChange();
                        this.restoreSelection()
                    }
                },
                collapseContext: function(c) {
                    var C = this._getContextInfo(c);
                    if (C && C.bExpanded) {
                        this.storeSelection();
                        for (var i = this.aContexts.length - 1; i > 0; i--) {
                            if (this._getContextInfo(this.aContexts[i]).oParentContext === c) {
                                this.aContexts.splice(i, 1)
                            }
                        }
                        C.bExpanded = false;
                        this._fireChange();
                        this.restoreSelection()
                    }
                },
                toggleContext: function(c) {
                    var C = this._getContextInfo(c);
                    if (C) {
                        if (C.bExpanded) {
                            this.collapseContext(c)
                        } else {
                            this.expandContext(c)
                        }
                    }
                },
                storeSelection: function() {
                    var s = t.getSelectedIndices();
                    var S = [];
                    jQuery.each(s, function(i, v) {
                        S.push(t.getContextByIndex(v))
                    });
                    this._aSelectedContexts = S
                },
                restoreSelection: function() {
                    t.clearSelection();
                    var _ = this._aSelectedContexts;
                    jQuery.each(this.aContexts, function(i, c) {
                        if (jQuery.inArray(c, _) >= 0) {
                            t.addSelectionInterval(i, i)
                        }
                    });
                    this._aSelectedContexts = undefined
                },
                attachSort: function() {},
                detachSort: function() {}
            });
            b._init(this.getExpandFirstLevel())
        }
        return b
    };
    sap.ui.table.TreeTable.prototype._updateTableContent = function() {
        sap.ui.table.Table.prototype._updateTableContent.apply(this, arguments);
        var b = this.getBinding("rows");
        if (b && this.isTreeBinding("rows")) {
            var $ = this.$();
            var f = this.getFirstVisibleRow();
            var c = this.getVisibleRowCount();
            for (var r = 0; r < c; r++) {
                var C = this.getContextByIndex(f + r);
                var l = b.getLevel ? b.getLevel(C) : 0;
                var a = $.find("[data-sap-ui-rowindex=" + r + "]");
                var t = a.find(".sapUiTableTreeIcon");
                t.css("marginLeft", l * 17);
                var T = "sapUiTableTreeIconLeaf";
                if (b.hasChildren && b.hasChildren(C)) {
                    T = b.isExpanded(C) ? "sapUiTableTreeIconNodeOpen" : "sapUiTableTreeIconNodeClosed";
                    a.attr('aria-expanded', b.isExpanded(C))
                } else {
                    a.attr('aria-expanded', false)
                }
                t.removeClass("sapUiTableTreeIconLeaf sapUiTableTreeIconNodeOpen sapUiTableTreeIconNodeClosed").addClass(T);
                a.attr("data-sap-ui-level", l);
                a.attr('aria-level', l + 1)
            }
        }
    };
    sap.ui.table.TreeTable.prototype.onclick = function(e) {
        if (jQuery(e.target).hasClass("sapUiTableTreeIcon")) {
            this._onNodeSelect(e)
        } else {
            if (sap.ui.table.Table.prototype.onclick) {
                sap.ui.table.Table.prototype.onclick.apply(this, arguments)
            }
        }
    };
    sap.ui.table.TreeTable.prototype.onsapselect = function(e) {
        if (jQuery(e.target).hasClass("sapUiTableTreeIcon")) {
            this._onNodeSelect(e)
        } else {
            if (sap.ui.table.Table.prototype.onsapselect) {
                sap.ui.table.Table.prototype.onsapselect.apply(this, arguments)
            }
        }
    };
    sap.ui.table.TreeTable.prototype._onNodeSelect = function(e) {
        var $ = jQuery(e.target).parents("tr");
        if ($.length > 0) {
            var r = this.getFirstVisibleRow() + parseInt($.attr("data-sap-ui-rowindex"), 10);
            var c = this.getContextByIndex(r);
            this.fireToggleOpenState({
                rowIndex: r,
                rowContext: c,
                expanded: !this.getBinding().isExpanded(c)
            });
            this.getBinding("rows").toggleContext(c)
        }
        e.preventDefault();
        e.stopPropagation()
    };
    sap.ui.table.TreeTable.prototype.expand = function(r) {
        var b = this.getBinding("rows");
        if (b) {
            var c = this.getContextByIndex(r);
            b.expandContext(c)
        }
    };
    sap.ui.table.TreeTable.prototype.collapse = function(r) {
        var b = this.getBinding("rows");
        if (b) {
            var c = this.getContextByIndex(r);
            b.collapseContext(c)
        }
    };
    sap.ui.table.TreeTable.prototype.isExpanded = function(r) {
        var b = this.getBinding("rows");
        if (b) {
            var c = this.getContextByIndex(r);
            return b.isExpanded(c)
        }
        return false
    };
    sap.ui.table.TreeTable.prototype._enterActionMode = function(d) {
        var $ = jQuery(d);
        sap.ui.table.Table.prototype._enterActionMode.apply(this, arguments);
        if (d && this._bActionMode && $.hasClass("sapUiTableTreeIcon")) {
            $.attr("tabindex", 0)
        }
    };
    sap.ui.table.TreeTable.prototype._leaveActionMode = function(e) {
        sap.ui.table.Table.prototype._leaveActionMode.apply(this, arguments);
        this.$().find(".sapUiTableTreeIcon").attr("tabindex", -1)
    }
};
if (!jQuery.sap.isDeclared('sap.ui.table.DataTable')) {
    jQuery.sap.declare("sap.ui.table.DataTable");
    sap.ui.table.TreeTable.extend("sap.ui.table.DataTable", {
        metadata: {
            deprecated: true,
            library: "sap.ui.table",
            properties: {
                "expandedVisibleRowCount": {
                    type: "int",
                    group: "",
                    defaultValue: null
                },
                "expanded": {
                    type: "boolean",
                    group: "",
                    defaultValue: false
                },
                "hierarchical": {
                    type: "boolean",
                    group: "",
                    defaultValue: false
                }
            },
            events: {
                "rowSelect": {}
            }
        }
    });
    sap.ui.table.DataTable.M_EVENTS = {
        'rowSelect': 'rowSelect'
    };
    sap.ui.table.DataTable.prototype.init = function() {
        sap.ui.table.TreeTable.prototype.init.apply(this, arguments);
        this._bInheritEditableToControls = true;
        this.setEditable(false);
        this.setSelectionBehavior(sap.ui.table.SelectionBehavior.Row);
        this.attachRowSelectionChange(function(e) {
            this.fireRowSelect(e.mParameters)
        });
        this._iLastFixedColIndex = -1
    };
    sap.ui.table.DataTable.prototype.isTreeBinding = function(n) {
        n = n || "rows";
        if (n === "rows") {
            return this.getHierarchical()
        }
        return sap.ui.core.Element.prototype.isTreeBinding.apply(this, arguments)
    };
    sap.ui.table.DataTable.prototype.setHierarchical = function(h) {
        this.setProperty("hierarchical", h);
        this._iLastFixedColIndex = h ? 0 : -1
    };
    sap.ui.table.DataTable.prototype.setVisibleRowCount = function(r) {
        this._iVisibleRowCount = r;
        if (!this.getExpanded()) {
            sap.ui.table.Table.prototype.setVisibleRowCount.apply(this, arguments)
        }
    };
    sap.ui.table.DataTable.prototype.setExpandedVisibleRowCount = function(r) {
        this.setProperty("expandedVisibleRowCount", r, true);
        if (this.getExpanded()) {
            sap.ui.table.Table.prototype.setVisibleRowCount.apply(this, arguments)
        }
    };
    sap.ui.table.DataTable.prototype.setExpanded = function(e) {
        this.setProperty("expanded", e, true);
        if (this.getExpandedVisibleRowCount() > 0) {
            var r = e ? this.getExpandedVisibleRowCount() : this._iVisibleRowCount;
            sap.ui.table.Table.prototype.setVisibleRowCount.call(this, r)
        }
    }
};