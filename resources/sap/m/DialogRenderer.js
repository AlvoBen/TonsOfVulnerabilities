﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.m.DialogRenderer");
jQuery.sap.require("sap.m.BarRenderer");
sap.m.DialogRenderer = {};

sap.m.DialogRenderer.render = function(r, c) {
    var a = c.getId(),
        s = c.getShowHeader(),
        t = c.getType(),
        h = c._getAnyHeader(),
        S = c.getSubHeader(),
        m = (t === sap.m.DialogType.Message),
        l = c.getBeginButton(),
        R = c.getEndButton(),
        H = c.getHorizontalScrolling(),
        v = c.getVerticalScrolling();
    if (h) {
        h._context = "header"
    }
    if (S) {
        S._context = "header"
    }
    r.write("<div");
    r.writeControlData(c);
    r.addClass("sapMDialog");
    r.addClass("sapMDialog-CTX");
    r.addClass("sapMPopup-CTX");
    r.addClass(sap.m.Dialog._mStateClasses[c.getState()]);
    if (c._forceDisableScrolling) {
        r.addClass("sapMDialogWithScrollCont")
    }
    if (S) {
        r.addClass("sapMDialogWithSubHeader")
    }
    if (m) {
        r.addClass("sapMMessageDialog")
    }
    if (!v) {
        r.addClass("sapMDialogVerScrollDisabled")
    }
    if (!H) {
        r.addClass("sapMDialogHorScrollDisabled")
    }
    if (!sap.m.Dialog._bOneDesign) {
        if (jQuery.os.ios && !h) {
            r.addClass("sapMDialogNoHeader")
        }
        if (m) {
            r.addClass("sapMCommonDialog")
        } else {
            if (jQuery.device.is.iphone) {
                r.addClass("sapMDialogHidden sapMDialogIPhone")
            }
        }
    } else {
        if (jQuery.device.is.phone) {
            r.addClass("sapMDialogPhone")
        }
    }
    r.writeClasses();
    var T = c.getTooltip_AsString();
    if (T) {
        r.writeAttributeEscaped("title", T)
    }
    r.write(">");
    if (jQuery.device.is.desktop) {
        r.write("<span id='" + c.getId() + "-firstfe' tabIndex='0'/>")
    }
    if (!sap.m.Dialog._bOneDesign) {
        if (jQuery.os.ios) {
            if (m) {
                if (s && c.getTitle()) {
                    r.write("<header class=\"sapMDialogTitle\">");
                    r.writeEscaped(c.getTitle());
                    r.write("</header>")
                }
            } else {
                if (h) {
                    r.renderControl(h)
                }
            }
        } else {
            if (s && (c.getIcon() || c.getTitle())) {
                r.write("<header class=\"sapMDialogTitle\">");
                r.write("<h1>");
                if (c._iconImage) {
                    r.renderControl(c._iconImage)
                }
                r.write("<span>");
                r.renderControl(c._headerTitle);
                r.write("</span>");
                r.write("</h1>");
                r.write("</header>")
            }
        }
    } else {
        if (h) {
            r.renderControl(h)
        }
    }
    if (S) {
        r.renderControl(S.addStyleClass("sapMDialogSubHeader"))
    }
    r.write("<section id='" + a + "-cont' style='width:" + c.getContentWidth() + "'>");
    r.write("<div id='" + a + "-scroll' class='sapMDialogScroll'>");
    r.write("<div id='" + a + "-scrollCont' class='sapMDialogScrollCont'>");
    var C = c.getContent();
    for (var i = 0; i < C.length; i++) {
        r.renderControl(C[i])
    }
    r.write("</div>");
    r.write("</div>");
    r.write("</section>");
    if ((sap.m.Dialog._bOneDesign || !jQuery.os.ios || m) && (l || R)) {
        r.write("<footer class='sapMDialogActions sapMBar-CTX sapMFooter-CTX'>");
        if (l) {
            r.write("<div class='sapMDialogAction'>");
            r.renderControl(l.addStyleClass("sapMDialogBtn", true));
            r.write("</div>")
        }
        if (R) {
            r.write("<div class='sapMDialogAction'>");
            r.renderControl(R.addStyleClass("sapMDialogBtn", true));
            r.write("</div>")
        }
        r.write("</footer>")
    }
    if (jQuery.device.is.desktop) {
        r.write("<span id='" + c.getId() + "-lastfe' tabIndex='0'/>")
    }
    r.write("</div>")
};