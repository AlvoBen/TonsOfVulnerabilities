﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.form.Form");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.layout.form.Form", {
    metadata: {
        library: "sap.ui.layout",
        properties: {
            "width": {
                type: "sap.ui.core.CSSSize",
                group: "Dimension",
                defaultValue: null
            },
            "visible": {
                type: "boolean",
                group: "Misc",
                defaultValue: true
            }
        },
        defaultAggregation: "formContainers",
        aggregations: {
            "formContainers": {
                type: "sap.ui.layout.form.FormContainer",
                multiple: true,
                singularName: "formContainer"
            },
            "title": {
                type: "sap.ui.core.Title",
                altTypes: ["string"],
                multiple: false
            },
            "layout": {
                type: "sap.ui.layout.form.FormLayout",
                multiple: false
            }
        }
    }
});
(function() {
    sap.ui.layout.form.Form.prototype.toggleContainerExpanded = function(c) {
        var l = this.getLayout();
        if (l) {
            l.toggleContainerExpanded(c)
        }
    };
    sap.ui.layout.form.Form.prototype.contentOnAfterRendering = function(f, c) {
        var l = this.getLayout();
        if (l && l.contentOnAfterRendering) {
            l.contentOnAfterRendering(f, c)
        }
    };
    sap.ui.layout.form.Form.prototype.onLayoutDataChange = function(e) {
        var l = this.getLayout();
        if (l && l.onLayoutDataChange) {
            l.onLayoutDataChange(e)
        }
    }
}());