﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.commons.FileUploader");
jQuery.sap.require("sap.ui.commons.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.commons.FileUploader", {
    metadata: {
        publicMethods: ["upload"],
        library: "sap.ui.commons",
        properties: {
            "value": {
                type: "string",
                group: "Data",
                defaultValue: ''
            },
            "enabled": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "visible": {
                type: "boolean",
                group: "Behavior",
                defaultValue: true
            },
            "uploadUrl": {
                type: "sap.ui.core.URI",
                group: "Data",
                defaultValue: ''
            },
            "name": {
                type: "string",
                group: "Data",
                defaultValue: null
            },
            "width": {
                type: "string",
                group: "Misc",
                defaultValue: ''
            },
            "uploadOnChange": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "additionalData": {
                type: "string",
                group: "Data",
                defaultValue: null
            },
            "sameFilenameAllowed": {
                type: "boolean",
                group: "Behavior",
                defaultValue: false
            },
            "buttonText": {
                type: "string",
                group: "Misc",
                defaultValue: null
            }
        },
        aggregations: {
            "parameters": {
                type: "sap.ui.commons.FileUploaderParameter",
                multiple: true,
                singularName: "parameter"
            }
        },
        events: {
            "change": {},
            "uploadComplete": {}
        }
    }
});
sap.ui.commons.FileUploader.M_EVENTS = {
    'change': 'change',
    'uploadComplete': 'uploadComplete'
};

sap.ui.commons.FileUploader.prototype.init = function() {
    if ( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 8) {
        this.oFilePath = new sap.ui.commons.TextField(this.getId() + "-fu_input", {
            width: "225px"
        })
    } else {
        this.oFilePath = new sap.ui.commons.TextField(this.getId() + "-fu_input", {
            width: this.getWidth()
        })
    }
    this.oFilePath.setParent(this);
    if (this.getButtonText()) {
        var b = this.getButtonText()
    } else {
        var b = this.getBrowseText()
    }
    if ( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 8) {
        this.oBrowse = new sap.ui.commons.Button({
            enabled: this.getEnabled(),
            text: b,
            width: "0px",
            height: "0px"
        })
    } else {
        this.oBrowse = new sap.ui.commons.Button({
            enabled: this.getEnabled(),
            text: b
        })
    }
    this.oBrowse.setParent(this);
    this.oFileUpload = null
};

sap.ui.commons.FileUploader.prototype.getIdForLabel = function() {
    return this.oBrowse.getId()
};

sap.ui.commons.FileUploader.prototype.exit = function() {
    this.oFilePath.destroy();
    this.oBrowse.destroy();
    if (this.oIFrameRef) {
        jQuery(this.oIFrameRef).unbind();
        sap.ui.getCore().getStaticAreaRef().removeChild(this.oIFrameRef);
        this.oIFrameRef = null
    }
};

sap.ui.commons.FileUploader.prototype.onBeforeRendering = function() {
    this._runOnce = false;
    if (this.getButtonText()) {
        this.oBrowse.setText(this.getButtonText())
    } else {
        this.oBrowse.setText(this.getBrowseText())
    }
    var s = sap.ui.getCore().getStaticAreaRef();
    jQuery(this.oFileUpload).appendTo(s);
    jQuery(this.oFileUpload).unbind()
};

sap.ui.commons.FileUploader.prototype.onAfterRendering = function() {
    this.prepareFileUploadAndIFrame();
    jQuery(this.oFileUpload).change(jQuery.proxy(this.handlechange, this));
    if (( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version <= 8)) {
        this.oBrowse.getDomRef().style.padding = "0px";
        this.oBrowse.getDomRef().style.visibility = "hidden";
        this.oFilePath.getDomRef().style.height = "20px";
        this.oFilePath.getDomRef().style.visibility = "hidden";
        jQuery(this.oFilePath.getDomRef()).removeClass('sapUiTfBrd')
    } else {
        this.oFilePath.$().attr("tabindex", "-1");
        if (( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 9)) {
            this.oBrowse.$().attr("tabindex", "-1")
        }
        if (this.getWidth()) {
            if (!this._runOnce) {
                this._runOnce = true;
                jQuery.sap.delayedCall(50, this, function() {
                    this.onAfterRendering()
                })
            } else {
                this._resizeDomElements()
            }
        }
    }
};

sap.ui.commons.FileUploader.prototype.getFocusDomRef = function() {
    return jQuery.sap.byId(this.getId() + "-fu").get(0)
};

sap.ui.commons.FileUploader.prototype._resizeDomElements = function() {
    var i = this.getId();
    this._oBrowseDomRef = this.oBrowse.getDomRef();
    var $ = jQuery(this._oBrowseDomRef);
    var _ = $.parent().outerWidth(true);
    this._oFilePathDomRef = this.oFilePath.getDomRef();
    var d = this._oFilePathDomRef;
    var w = this.getWidth();
    if (w.substr(-1) == "%") {
        while (d.id != i) {
            d.style.width = "100%";
            d = d.parentNode
        }
        d.style.width = w
    } else {
        d.style.width = w;
        var a = jQuery(this._oFilePathDomRef);
        var b = a.outerWidth() - _;
        if (b < 0) {
            this.oFilePath.getDomRef().style.width = "0px";
            if ( !! !sap.ui.Device.browser.internet_explorer) {
                this.oFileUpload.style.width = $.outerWidth(true)
            }
        } else {
            this.oFilePath.getDomRef().style.width = b + "px"
        }
    }
};

sap.ui.commons.FileUploader.prototype.onresize = function() {
    this.onAfterRendering()
};

sap.ui.commons.FileUploader.prototype.onThemeChanged = function() {
    this.onAfterRendering()
};

sap.ui.commons.FileUploader.prototype.setEnabled = function(e) {
    this.setProperty("enabled", e, true);
    this.oFilePath.setEnabled(e);
    this.oBrowse.setEnabled(e);
    if (e) {
        jQuery.sap.byId(this.getId() + "-fu").removeAttr('disabled')
    } else {
        jQuery.sap.byId(this.getId() + "-fu").attr('disabled', 'disabled')
    }
    return this
};

sap.ui.commons.FileUploader.prototype.setUploadUrl = function(v, f) {
    this.setProperty("uploadUrl", v, true);
    var $ = jQuery.sap.byId(this.getId() + "-fu_form");
    $.attr("action", this.getUploadUrl());
    return this
};

sap.ui.commons.FileUploader.prototype.setValue = function(v, f) {
    var o = this.getValue();
    if ((o != v) || this.getSameFilenameAllowed()) {
        var u = this.getUploadOnChange() && v;
        this.setProperty("value", v, u);
        if (this.oFilePath) {
            this.oFilePath.setValue(v);
            if (!( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 8) && this.oFilePath.getFocusDomRef()) {
                this.oFilePath.getFocusDomRef().focus()
            }
        }
        if (this.oFileUpload && !v) {
            jQuery.sap.domById(this.getId() + "-fu_form").reset();
            jQuery.sap.byId(this.getId() + "-fu_data").val(this.getAdditionalData())
        }
        if (f) {
            this.fireChange({
                id: this.getId(),
                newValue: v
            })
        }
        if (u) {
            this.upload()
        }
    }
    return this
};

sap.ui.commons.FileUploader.prototype.onmouseover = function() {
    jQuery(this.oBrowse.getDomRef()).addClass('sapUiBtnStdHover')
};

sap.ui.commons.FileUploader.prototype.onmouseout = function() {
    jQuery(this.oBrowse.getDomRef()).removeClass('sapUiBtnStdHover')
};

sap.ui.commons.FileUploader.prototype.onfocusin = function() {
    jQuery(this.oBrowse.getDomRef()).addClass('sapUiBtnStdHover')
};

sap.ui.commons.FileUploader.prototype.onfocusout = function() {
    jQuery(this.oBrowse.getDomRef()).removeClass('sapUiBtnStdHover')
};

sap.ui.commons.FileUploader.prototype.setAdditionalData = function(a) {
    this.setProperty("additionalData", a, true);
    var A = jQuery.sap.domById(this.getId() + "-fu_data");
    if (A) {
        var a = this.getAdditionalData() || "";
        A.value = a
    }
    return this
};

sap.ui.commons.FileUploader.prototype.upload = function() {
    var u = jQuery.sap.domById(this.getId() + "-fu_form");
    try {
        if (u) {
            this._bUploading = true;
            u.submit();
            jQuery.sap.log.info("File uploading to " + this.getUploadUrl())
        }
    } catch (e) {
        jQuery.sap.log.error("File upload failed:\n" + e.message)
    }
};

sap.ui.commons.FileUploader.prototype.onkeypress = function(e) {
    this.onkeydown(e)
};

sap.ui.commons.FileUploader.prototype.onkeydown = function(e) {
    if (!this.getEnabled()) {
        return
    }
    var k = e.keyCode,
        a = jQuery.sap.KeyCodes;
    if (k == a.DELETE || k == a.BACKSPACE) {
        if (this.oFileUpload) {
            this.setValue("", true)
        }
    } else if (k == a.SPACE || k == a.ENTER) {
        if (!( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version <= 9) && this.oFileUpload) {
            this.oFileUpload.click();
            e.preventDefault();
            e.stopPropagation()
        }
    } else if (k != a.TAB && k != a.SHIFT && k != a.F6) {
        e.preventDefault();
        e.stopPropagation()
    }
};

sap.ui.commons.FileUploader.prototype.handlechange = function(e) {
    if (this.oFileUpload && this.getEnabled()) {
        var v = this.oFileUpload.value || "";
        var i = v.lastIndexOf("\\");
        if (i >= 0) {
            v = v.substring(i + 1)
        }
        if (v || sap.ui.Device.browser.chrome) {
            this.setValue(v, true)
        }
    }
};

sap.ui.commons.FileUploader.prototype.getBrowseText = function() {
    var r = sap.ui.getCore().getLibraryResourceBundle("sap.ui.commons");
    var t = undefined;
    if (r) {
        t = r.getText("FILEUPLOAD_BROWSE")
    }
    return t ? t : "Browse..."
};

sap.ui.commons.FileUploader.prototype.getShortenValue = function() {
    return this.getValue()
};

sap.ui.commons.FileUploader.prototype.prepareFileUploadAndIFrame = function() {
    if (!this.oFileUpload) {
        var f = [];
        f.push('<input ');
        f.push('type="file" ');
        if (this.getName()) {
            f.push('name="' + this.getName() + '" ')
        } else {
            f.push('name="' + this.getId() + '" ')
        }
        f.push('id="' + this.getId() + '-fu" ');
        if (!( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 8)) {
            if (!( !! sap.ui.Device.browser.internet_explorer && sap.ui.Device.browser.version == 9)) {
                f.push('tabindex="-1" ')
            }
            f.push('size="1" ')
        }
        if (this.getTooltip_AsString()) {
            f.push('title="' + jQuery.sap.escapeHTML(this.getTooltip_AsString()) + '" ')
        } else if (this.getTooltip()) {} else if (this.getValue() != "") {
            f.push('title="' + jQuery.sap.escapeHTML(this.getValue()) + '" ')
        }
        if (!this.getEnabled()) {
            f.push('disabled="disabled" ')
        }
        f.push('>');
        this.oFileUpload = jQuery(f.join("")).prependTo(this.$().find(".sapUiFupInputMask")).get(0)
    } else {
        jQuery(this.oFileUpload).prependTo(this.$().find(".sapUiFupInputMask"))
    }
    if (!this.oIFrameRef) {
        var u = jQuery.sap.domById(this.getId() + "-fu_form");
        var i = document.createElement("iframe");
        i.style.display = "none";
        i.src = "javascript:''";
        i.id = this.sId + "-frame";
        sap.ui.getCore().getStaticAreaRef().appendChild(i);
        i.contentWindow.name = this.sId + "-frame";
        var t = this;
        this._bUploading = false;
        jQuery(i).load(function(e) {
            if (t._bUploading) {
                jQuery.sap.log.info("File uploaded to " + t.getUploadUrl());
                var r;
                try {
                    r = t.oIFrameRef.contentDocument.body.innerHTML
                } catch (a) {}
                t.fireUploadComplete({
                    "response": r
                });
                t._bUploading = false
            }
        });
        this.oIFrameRef = i
    }
};