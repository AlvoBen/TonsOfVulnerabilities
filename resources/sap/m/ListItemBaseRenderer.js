﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.ListItemBaseRenderer");
jQuery.sap.require("sap.ui.core.theming.Parameters");
jQuery.sap.require("sap.ui.core.IconPool");
sap.m.ListItemBaseRenderer = {};

sap.m.ListItemBaseRenderer.render = function(r, l) {
    if (!l.getVisible()) {
        return
    }
    var _ = (sap.ui.core.theming.Parameters.get("sapUiLIUnreadAsBubble") == "true");
    var i = l.getId(),
        p = l.getParent(),
        a = !l.hasOwnProperty("_renderInList") && p && p.getColumns && p.getColumns().length,
        n = l._bNoFlex,
        b, c, o, d, h = false,
        t = l.getTooltip_AsString();
    if (!a) {
        b = "</li>";
        c = "<li tabindex='-1'";
        o = function() {
            r.write("<div")
        };
        d = function() {
            r.write("</div>")
        }
    } else {
        b = "</tr>";
        c = "<tr tabindex='-1'";
        h = p.hasPopin() && sap.m.ColumnListItem && l instanceof sap.m.ColumnListItem;
        o = function(k, D) {
            r.write("<td");
            if (k) {
                r.addClass(k);
                r.writeClasses()
            }
            if (!D) {
                r.write("><div")
            }
        };
        d = function(D) {
            if (!D) {
                r.write("</div>")
            }
            r.write("</td>")
        }
    }
    r.write(c);
    t && r.writeAttributeEscaped("title", t);
    r.writeControlData(l);
    r.addClass("sapMLIB");
    r.addClass("sapMLIB-CTX");
    if (!a && n) {
        r.addClass("sapMLIBNoFlex")
    }
    r.addClass("sapMLIBShowSeparator");
    if (l._includeItemInSelection || l._mode == "SingleSelectMaster" || (l.getType() != "Inactive" && l.getType() != "Detail")) {
        r.addClass("sapMLIBCursor")
    }
    if (l._showUnread && l.getUnread()) {
        r.addClass("sapMLIBUnread");
        if (!_) {
            r.addClass("sapMLIBUnreadBold")
        }
    }
    if (h) {
        r.addClass("sapMListTblSupRow")
    }
    if (l._showUnread && l.getUnread()) {
        r.addClass("sapMLIBUnreadRow")
    }
    if (this.renderLIAttributes) {
        this.renderLIAttributes(r, l)
    }
    r.addClass("sapMLIBType" + l.getType());
    if (this.renderLIContent) {
        var s = null;
        switch (l._mode) {
            case "SingleSelectLeft":
                s = l._getRadioButton((i + "-selectSingle"), l._listId + "_selectGroup");
                if (l.getSelected()) {
                    r.addClass("sapMLIBSelected")
                }
                r.writeClasses();
                r.write(">");
                o("sapMListTblSelCol");
                r.addClass("sapMLIBSelectSL");
                if (l._oldMode === "None" && l._modeAnimationOn) {
                    r.addClass("sapMLIBSelectAnimation")
                }
                r.writeAttribute("id", i + "-mode");
                r.writeClasses();
                r.write(">");
                r.renderControl(s);
                d();
                l._oldMode = l._mode;
                break;
            case "SingleSelect":
                s = l._getRadioButton((i + "-selectSingle"), l._listId + "_selectGroup");
                if (l.getSelected()) {
                    r.addClass("sapMLIBSelected")
                }
                r.writeClasses();
                r.write(">");
                break;
            case "SingleSelectMaster":
                s = l._getRadioButton((i + "-selectSingleMaster"), l._listId + "_selectMasterGroup");
                if (l.getSelected()) {
                    r.addClass("sapMLIBSelected")
                }
                r.writeClasses();
                r.write(">");
                o("sapMListTblNone");
                r.addClass("sapMLIBSelectSM");
                r.writeAttribute("id", i + "-mode");
                r.writeClasses();
                r.write(">");
                r.renderControl(s);
                d();
                l._oldMode = l._mode;
                break;
            case "MultiSelect":
                s = l._getCheckBox((i + "-selectMulti"));
                if (l.getSelected()) {
                    r.addClass("sapMLIBSelected")
                }
                r.writeClasses();
                r.write(">");
                o("sapMListTblSelCol");
                r.addClass("sapMLIBSelectM");
                if (l._oldMode === "None" && l._modeAnimationOn) {
                    r.addClass("sapMLIBSelectAnimation")
                }
                r.writeAttribute("id", i + "-mode");
                r.writeClasses();
                r.write(">");
                r.renderControl(s);
                d();
                l._oldMode = l._mode;
                break;
            case "Delete":
                r.writeClasses();
                r.write(">");
                if (sap.ui.core.theming.Parameters.get("sapMPlatformDependent") === "true") {
                    o("sapMListTblSelCol");
                    r.addClass("sapMLIBSelectD");
                    if (l._oldMode === "None" && l._modeAnimationOn) {
                        r.addClass("sapMLIBSelectAnimation")
                    }
                    r.writeAttribute("id", i + "-mode");
                    r.writeClasses();
                    r.write(">");
                    var e = null;
                    if (sap.ui.core.theming.Parameters.get("sapUiLIDelIcon") == "false") {
                        e = l._getDelImage((i + "-imgDel"), "sapMLIBImgDel", "delete_icon.png")
                    } else {
                        var u = sap.ui.core.IconPool.getIconURI("sys-cancel");
                        e = l._delIcon || new sap.ui.core.Icon(i + "-imgDel", {
                            src: u
                        }).setParent(l, null, true).addStyleClass("sapMLIBIconDel").attachPress(l._delete);
                    }
                    if (e) {
                        l._delIcon = e;
                        r.renderControl(e)
                    }
                    d();
                    l._oldMode = l._mode
                }
                break;
            case "None":
                r.writeClasses();
                r.write(">");
                if (!a && !n && l._oldMode && l._oldMode !== "None" && l._oldMode !== "SingleSelect" && l._oldMode !== "SingleSelectMaster" && !(l._oldMode == "Delete" && sap.ui.core.theming.Parameters.get("sapMPlatformDependent") !== "true") && l._modeAnimationOn) {
                    o();
                    r.addClass("sapMLIBUnselectAnimation");
                    r.writeAttribute("id", i + "-mode");
                    r.writeClasses();
                    r.write(">");
                    d()
                }
                break
        }
        if (l._showUnread && _) {
            o("sapMListTblUnreadCol");
            r.writeAttribute("id", i + "-unread");
            r.addClass("sapMLIBUnreadBase");
            if (l.getUnread()) {
                r.addClass("sapMLIBUnreadBubble")
            }
            r.writeClasses();
            r.write(">");
            d()
        }
        var f = l.getType(),
            g = "";
        switch (f) {
            case "Navigation":
                g = "NAV";
                break;
            case "Detail":
            case "DetailAndActive":
                g = "DET";
                break
        }
        if (a) {
            this.renderLIContent(r, l, p)
        } else {
            o();
            r.addClass("sapMLIBContent");
            if ((f == "Active" || f == "Inactive") && !l.getCounter()) {
                r.addClass("sapMLIBContentMargin")
            }
            r.writeClasses();
            r.write(">");
            if (n) {
                r.write('<div class="sapMLIBContentNF">')
            }
            this.renderLIContent(r, l);
            if (n) {
                r.write("</div>")
            }
            d()
        }
        if (!a && l.getCounter()) {
            r.write("<div");
            r.writeAttribute("id", i + "-counter");
            r.addClass("sapMLIBCounter");
            if (!g) {
                r.addClass("sapMLIBContentMargin")
            }
            r.writeClasses();
            r.write(">");
            r.write(l.getCounter());
            r.write("</div>")
        }
        if (g == "NAV" && (jQuery.os.ios || sap.ui.core.theming.Parameters.get("sapMPlatformDependent") !== "true") && l.getType() == "Navigation") {
            a && o("sapMListTblNavCol", true);
            if (jQuery.os.ios && sap.ui.core.theming.Parameters.get("sapMPlatformDependent") == "true") {
                !a && r.write("<div");
                r.addClass("sapMLIBImgNav");
                r.writeAttribute("id", i + "-imgNav");
                r.writeClasses();
                r.write(">");
                !a && r.write("</div>")
            } else {
                a && r.write(">");
                var u = sap.ui.core.IconPool.getIconURI("slim-arrow-right");
                var N = l._navIcon || new sap.ui.core.Icon(i + "-imgNav", {
                    src: u
                }).setParent(l, null, true).addStyleClass("sapMLIBImgNav");
                if (N) {
                    l._navIcon = N;
                    r.renderControl(N)
                }
            }
            a && d(true);
            p._navRenderedBy = i + "-imgNav"
        } else if (g == "DET") {
            o("sapMListTblNavCol");
            r.addClass("sapMLIBCursor");
            r.writeClasses();
            r.write(">");
            var j = null;
            if (sap.ui.core.theming.Parameters.get("sapUiLIDetailIcon") == "false") {
                j = l._getNavImage((i + "-imgDet"), "sapMLIBImgDet", "detail_disclosure.png", "detail_disclosure_pressed.png")
            } else {
                var u = sap.ui.core.IconPool.getIconURI("edit");
                j = l._detailIcon || new sap.ui.core.Icon(i + "-imgDet", {
                    src: u
                }).setParent(l, null, true).addStyleClass("sapMLIBIconDet")
            }
            if (j) {
                l._detailIcon = j;
                r.renderControl(j)
            }
            d();
            p._navRenderedBy = i + "-imgDet"
        } else if (a) {
            r.write("<td></td>")
        }
        switch (l._mode) {
            case "SingleSelect":
                o("sapMListTblSelCol");
                r.addClass("sapMLIBSelectS");
                if (l._oldMode === "None" && l._modeAnimationOn) {
                    r.addClass("sapMLIBSelectAnimation")
                }
                r.writeAttribute("id", i + "-mode");
                r.writeClasses();
                r.write(">");
                r.renderControl(s);
                d();
                l._oldMode = l._mode;
                break;
            case "Delete":
                if (sap.ui.core.theming.Parameters.get("sapMPlatformDependent") !== "true") {
                    o("sapMListTblSelCol");
                    r.addClass("sapMLIBSelectD");
                    if (l._oldMode === "None" && l._modeAnimationOn) {
                        r.addClass("sapMLIBSelectAnimation")
                    }
                    r.writeAttribute("id", i + "-mode");
                    r.writeClasses();
                    r.write(">");
                    var e = null;
                    if (sap.ui.core.theming.Parameters.get("sapUiLIDelIcon") == "false") {
                        e = l._getDelImage((i + "-imgDel"), "sapMLIBImgDel", "delete_icon.png")
                    } else {
                        var u = sap.ui.core.IconPool.getIconURI("sys-cancel");
                        e = l._delIcon || new sap.ui.core.Icon(i + "-imgDel", {
                            src: u
                        }).setParent(l, null, true).addStyleClass("sapMLIBIconDel").attachPress(l._delete);
                    }
                    if (e) {
                        l._delIcon = e;
                        r.renderControl(e)
                    }
                    d();
                    l._oldMode = l._mode
                }
                break;
            case "None":
                if (!a && !n && l._oldMode && l._oldMode !== "None" && (l._oldMode === "SingleSelect" || (l._oldMode === "Delete" && sap.ui.core.theming.Parameters.get("sapMPlatformDependent") !== "true")) && l._modeAnimationOn) {
                    o();
                    r.addClass("sapMLIBUnselectAnimation");
                    r.writeAttribute("id", i + "-mode");
                    r.writeClasses();
                    r.write(">");
                    d()
                }
                l._oldMode = l._mode;
                break
        }
    } else {
        r.writeClasses();
        r.write(">")
    }
    r.write(b);
    if (h) {
        this.renderPopin(r, l, p)
    }
};