﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.form.FormContainer");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.core.Element");
sap.ui.core.Element.extend("sap.ui.layout.form.FormContainer", {
    metadata: {
        library: "sap.ui.layout",
        properties: {
            "expanded": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            },
            "expandable": {
                type: "boolean",
                group: "Misc",
                defaultValue: false
            },
            "visible": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            }
        },
        defaultAggregation: "formElements",
        aggregations: {
            "formElements": {
                type: "sap.ui.layout.form.FormElement",
                multiple: true,
                singularName: "formElement"
            },
            "title": {
                type: "sap.ui.core.Title",
                altTypes: ["string"],
                multiple: false
            }
        }
    }
});
jQuery.sap.require("sap.ui.core.EnabledPropagator");
jQuery.sap.require("sap.ui.core.theming.Parameters");
(function() {
    sap.ui.layout.form.FormContainer.prototype.init = function() {
        this._rb = sap.ui.getCore().getLibraryResourceBundle("sap.ui.layout")
    };
    sap.ui.layout.form.FormContainer.prototype.exit = function() {
        if (this._oExpandButton) {
            this._oExpandButton.destroy();
            delete this._oExpandButton
        }
        this._rb = undefined
    };
    sap.ui.layout.form.FormContainer.prototype.setExpandable = function(e) {
        this.setProperty("expandable", e);
        if (e) {
            var t = this;
            if (!this._oExpandButton) {
                this._oExpandButton = sap.ui.layout.form.FormHelper.createButton(this.getId() + "--Exp", h, t);
                this._oExpandButton.setParent(this)
            }
            _(t)
        }
    };
    sap.ui.layout.form.FormContainer.prototype.setExpanded = function(e) {
        this.setProperty("expanded", e, true);
        var t = this;
        _(t);
        var f = this.getParent();
        if (f && f.toggleContainerExpanded) {
            f.toggleContainerExpanded(t)
        }
    };
    sap.ui.layout.form.FormContainer.prototype.contentOnAfterRendering = function(f, c) {
        var p = this.getParent();
        if (p && p.contentOnAfterRendering) {
            p.contentOnAfterRendering(f, c)
        }
    };
    sap.ui.layout.form.FormContainer.prototype.onLayoutDataChange = function(e) {
        var p = this.getParent();
        if (p && p.onLayoutDataChange) {
            p.onLayoutDataChange(e)
        }
    };

    function _(c) {
        if (!c._oExpandButton) {
            return
        }
        var i, I, t, T;
        if (c.getExpanded()) {
            i = sap.ui.core.theming.Parameters.get('sapUiFormContainerColImageURL');
            I = sap.ui.core.theming.Parameters.get('sapUiFormContainerColImageDownURL');
            t = "-";
            T = c._rb.getText("FORM_COLLAPSE")
        } else {
            i = sap.ui.core.theming.Parameters.get('sapUiFormContainerExpImageURL');
            I = sap.ui.core.theming.Parameters.get('sapUiFormContainerExpImageDownURL');
            t = "+";
            T = c._rb.getText("FORM_EXPAND")
        }
        if (i) {
            i = jQuery.sap.getModulePath("sap.ui.layout", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + i;
            t = ""
        }
        if (I) {
            I = jQuery.sap.getModulePath("sap.ui.layout", '/') + "themes/" + sap.ui.getCore().getConfiguration().getTheme() + I
        }
        sap.ui.layout.form.FormHelper.setButtonContent(c._oExpandButton, t, T, i, I)
    };

    function h(e) {
        this.setExpanded(!this.getExpanded())
    }
}());