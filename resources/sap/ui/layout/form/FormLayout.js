﻿/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company.
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */

jQuery.sap.declare("sap.ui.layout.form.FormLayout");
jQuery.sap.require("sap.ui.layout.library");
jQuery.sap.require("sap.ui.core.Control");
sap.ui.core.Control.extend("sap.ui.layout.form.FormLayout", {
    metadata: {
        library: "sap.ui.layout"
    }
});
jQuery.sap.require("sap.ui.layout.form.Form");
(function() {
    sap.ui.layout.form.FormLayout.prototype.contentOnAfterRendering = function(f, c) {
        jQuery(c.getFocusDomRef()).data("sap.InNavArea", true)
    };
    sap.ui.layout.form.FormLayout.prototype.toggleContainerExpanded = function(c) {
        var e = c.getExpanded();
        if (this.getDomRef()) {
            if (e) {
                jQuery.sap.byId(c.getId() + "-content").css("display", "")
            } else {
                jQuery.sap.byId(c.getId() + "-content").css("display", "none")
            }
        }
    };
    sap.ui.layout.form.FormLayout.prototype.getLayoutDataForElement = function(e, t) {
        var l = e.getLayoutData();
        var c = jQuery.sap.getObject(t);
        if (!l) {
            return undefined
        } else if (l instanceof c) {
            return l
        } else if (l.getMetadata().getName() == "sap.ui.core.VariantLayoutData") {
            var L = l.getMultipleLayoutData();
            for (var i = 0; i < L.length; i++) {
                var o = L[i];
                if (o instanceof c) {
                    return o
                }
            }
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapright = function(e) {
        if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
            var r = sap.ui.getCore().getConfiguration().getRTL();
            var t = this;
            if (!r) {
                this.navigateForward(e, t)
            } else {
                this.navigateBack(e, t)
            }
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapleft = function(e) {
        if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
            var r = sap.ui.getCore().getConfiguration().getRTL();
            var t = this;
            if (!r) {
                this.navigateBack(e, t)
            } else {
                this.navigateForward(e, t)
            }
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapdown = function(e) {
        if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                n = this.findFieldBelow(c, E)
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                n = this.findFirstFieldOfNextElement(E, 0)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapup = function(e) {
        if (sap.ui.layout.form.FormHelper.bArrowKeySupport) {
            var c = e.srcControl;
            var C = 0;
            var n;
            var r = this.findElement(c);
            var E = r.element;
            c = r.rootControl;
            if (E && E instanceof sap.ui.layout.form.FormElement) {
                n = this.findFieldAbove(c, E)
            } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
                var f = E.getParent();
                C = f.indexOfFormContainer(E);
                n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1)
            }
            if (n) {
                jQuery.sap.focus(n);
                e.preventDefault()
            }
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsaphome = function(e) {
        var c = e.srcControl;
        var C = 0;
        var n;
        var r = this.findElement(c);
        var E = r.element;
        var o = E.getParent();
        var f = o.getParent();
        C = f.indexOfFormContainer(o);
        n = this.findFirstFieldOfFirstElementInNextContainer(f, C);
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsaptop = function(e) {
        var c = e.srcControl;
        var r = this.findElement(c);
        var E = r.element;
        var n;
        var C;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            C = E.getParent()
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            C = E
        }
        var f = C.getParent();
        n = this.findFirstFieldOfForm(f);
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapend = function(e) {
        var c = e.srcControl;
        var C = 0;
        var n;
        var r = this.findElement(c);
        var E = r.element;
        var o = E.getParent();
        var f = o.getParent();
        C = f.indexOfFormContainer(o);
        n = this.findLastFieldOfLastElementInPrevContainer(f, C);
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapbottom = function(e) {
        var c = e.srcControl;
        var r = this.findElement(c);
        var E = r.element;
        var n;
        var C;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            C = E.getParent()
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            C = E
        }
        var f = C.getParent();
        var a = f.getFormContainers();
        var l = a.length;
        n = this.findLastFieldOfLastElementInPrevContainer(f, l - 1);
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapexpand = function(e) {
        var c = e.srcControl;
        var r = this.findElement(c);
        var E = r.element;
        var C = E.getParent();
        if (C.getExpandable()) {
            C.setExpanded(true)
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapcollapse = function(e) {
        var c = e.srcControl;
        var r = this.findElement(c);
        var E = r.element;
        var C = E.getParent();
        if (C.getExpandable()) {
            C.setExpanded(false)
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapskipforward = function(e) {
        var c = e.srcControl;
        var r = this.findElement(c);
        var E = r.element;
        c = r.rootControl;
        var n;
        var C;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            C = E.getParent()
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            C = E
        }
        var f = C.getParent();
        var i = f.indexOfFormContainer(C);
        n = this.findFirstFieldOfFirstElementInNextContainer(f, i + 1);
        if (!n) {
            n = this.findFirstFieldOfForm(f)
        }
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.onsapskipback = function(e) {
        var c = e.srcControl;
        var r = this.findElement(c);
        var E = r.element;
        c = r.rootControl;
        var n;
        var C;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            C = E.getParent()
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            C = E
        }
        var f = C.getParent();
        var a = f.getFormContainers();
        var i = f.indexOfFormContainer(C);
        while (!n && i >= 0) {
            var p = a[i - 1];
            if (!p.getExpandable() || p.getExpanded()) {
                n = this.findFirstFieldOfFirstElementInPrevContainer(f, i - 1)
            }
            i = i - 1
        }
        if (!n) {
            n = this.findLastFieldOfForm(f)
        }
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.findElement = function(c) {
        var e = c.getParent();
        var r = c;
        while (e && !(e instanceof sap.ui.layout.form.FormElement) && !(e && e instanceof sap.ui.layout.form.FormContainer) && !(e && e instanceof sap.ui.layout.form.Form)) {
            r = e;
            e = e.getParent()
        }
        return ({
            rootControl: r,
            element: e
        })
    };
    sap.ui.layout.form.FormLayout.prototype.navigateForward = function(e) {
        var c = e.srcControl;
        var C = 0;
        var n;
        var r = this.findElement(c);
        var E = r.element;
        c = r.rootControl;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            if (c == E.getLabelControl()) {
                C = -1
            } else {
                C = E.indexOfField(c)
            }
            n = this.findNextFieldOfElement(E, C + 1);
            if (!n) {
                var o = E.getParent();
                C = o.indexOfFormElement(E);
                n = this.findFirstFieldOfNextElement(o, C + 1);
                if (!n) {
                    var f = o.getParent();
                    C = f.indexOfFormContainer(o);
                    n = this.findFirstFieldOfFirstElementInNextContainer(f, C + 1)
                }
            }
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            n = this.findFirstFieldOfNextElement(E, 0)
        }
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.tabForward = function(e) {
        var c = e.srcControl;
        var C = 0;
        var n;
        var r = this.findElement(c);
        var E = r.element;
        c = r.rootControl;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            if (c == E.getLabelControl()) {
                C = -1
            } else {
                C = E.indexOfField(c)
            }
            n = this.findNextFieldOfElement(E, C + 1, true);
            if (!n) {
                var o = E.getParent();
                C = o.indexOfFormElement(E);
                n = this.findFirstFieldOfNextElement(o, C + 1, true);
                if (!n) {
                    var f = o.getParent();
                    C = f.indexOfFormContainer(o);
                    n = this.findFirstFieldOfFirstElementInNextContainer(f, C + 1, true)
                }
            }
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            n = this.findFirstFieldOfNextElement(E, 0, true);
            if (!n) {
                var f = E.getParent();
                C = f.indexOfFormContainer(E);
                n = this.findFirstFieldOfFirstElementInNextContainer(f, C + 1, true)
            }
        }
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.findNextFieldOfElement = function(e, s, t) {
        var f = e.getFields();
        var l = f.length;
        var n;
        for (var i = s; i < l; i++) {
            var F = f[i];
            var d = this._getDomRef(F);
            if (t == true) {
                if ((!F.getEditable || F.getEditable()) && (!F.getEnabled || F.getEnabled()) && d) {
                    n = d;
                    break
                }
            } else {
                if ((!F.getEnabled || F.getEnabled()) && d) {
                    n = d;
                    break
                }
            }
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfNextElement = function(c, s, t) {
        var e = c.getFormElements();
        var l = e.length;
        var n;
        var i = s;
        while (!n && i < l) {
            var E = e[i];
            if (t == true) {
                n = this.findNextFieldOfElement(E, 0, true)
            } else {
                n = this.findNextFieldOfElement(E, 0)
            }
            i++
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfForm = function(f) {
        var c = f.getFormContainers();
        var n;
        var C = c[0];
        if (!C.getExpandable() || C.getExpanded()) {
            n = this.findFirstFieldOfNextElement(C, 0)
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findLastFieldOfForm = function(f) {
        var c = f.getFormContainers();
        var l = c.length;
        var n;
        var C = c[l - 1];
        if (!C.getExpandable() || C.getExpanded()) {
            n = this.findFirstFieldOfNextElement(C, 0)
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfLastContainerOfForm = function(f) {
        var c = f.getFormContainers();
        var l = c.length;
        var n;
        var C = c[l - 1];
        if (!C.getExpandable() || C.getExpanded()) {
            n = this.findFirstFieldOfNextElement(C, 0)
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfFirstElementInNextContainer = function(f, s, t) {
        var c = f.getFormContainers();
        var l = c.length;
        var n;
        var i = s;
        while (!n && i < l) {
            var C = c[i];
            if (C.getExpandable() && t) {
                n = C._oExpandButton;
                return n;
                break
            }
            if (!C.getExpandable() || C.getExpanded()) {
                if (t == true) {
                    n = this.findFirstFieldOfNextElement(C, 0, true)
                } else {
                    n = this.findFirstFieldOfNextElement(C, 0)
                }
            }
            i++
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFirstFieldOfFirstElementInPrevContainer = function(f, s) {
        var c = f.getFormContainers();
        var l = c.length;
        var n;
        var i = s;
        while (!n && i < l && i >= 0) {
            var C = c[i];
            if (!C.getExpandable() || C.getExpanded()) {
                n = this.findFirstFieldOfNextElement(C, 0)
            }
            i++
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.navigateBack = function(e) {
        var c = e.srcControl;
        var C = 0;
        var n;
        var r = this.findElement(c);
        var E = r.element;
        c = r.rootControl;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            if (c == E.getLabelControl()) {
                C = 0
            } else {
                C = E.indexOfField(c)
            }
            n = this.findPrevFieldOfElement(E, C - 1);
            if (!n) {
                var o = E.getParent();
                C = o.indexOfFormElement(E);
                n = this.findLastFieldOfPrevElement(o, C - 1);
                if (!n) {
                    var f = o.getParent();
                    C = f.indexOfFormContainer(o);
                    n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1)
                }
            }
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            var f = E.getParent();
            C = f.indexOfFormContainer(E);
            n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1)
        }
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.tabBack = function(e) {
        var c = e.srcControl;
        var C = 0;
        var n;
        var r = this.findElement(c);
        var E = r.element;
        c = r.rootControl;
        if (E && E instanceof sap.ui.layout.form.FormElement) {
            if (c == E.getLabelControl()) {
                C = 0
            } else {
                C = E.indexOfField(c)
            }
            n = this.findPrevFieldOfElement(E, C - 1, true);
            if (!n) {
                var o = E.getParent();
                C = o.indexOfFormElement(E);
                n = this.findLastFieldOfPrevElement(o, C - 1, true);
                if (!n) {
                    var f = o.getParent();
                    C = f.indexOfFormContainer(o);
                    if (o.getExpandable()) {
                        n = o._oExpandButton
                    } else {
                        n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1, true)
                    }
                }
            }
        } else if (E && E instanceof sap.ui.layout.form.FormContainer) {
            var f = E.getParent();
            C = f.indexOfFormContainer(E);
            n = this.findLastFieldOfLastElementInPrevContainer(f, C - 1, true)
        }
        if (n) {
            jQuery.sap.focus(n);
            e.preventDefault()
        }
    };
    sap.ui.layout.form.FormLayout.prototype.findPrevFieldOfElement = function(e, s, t) {
        var f = e.getFields();
        var n;
        for (var i = s; i >= 0; i--) {
            var F = f[i];
            var d = this._getDomRef(F);
            if (t == true) {
                if ((!F.getEditable || F.getEditable()) && (!F.getEnabled || F.getEnabled()) && d) {
                    n = d;
                    break
                }
            } else {
                if ((!F.getEnabled || F.getEnabled()) && d) {
                    n = d;
                    break
                }
            }
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findLastFieldOfPrevElement = function(c, s, t) {
        var e = c.getFormElements();
        var n;
        var i = s;
        while (!n && i >= 0) {
            var E = e[i];
            var l = E.getFields().length;
            if (t == true) {
                n = this.findPrevFieldOfElement(E, l - 1, true)
            } else {
                n = this.findPrevFieldOfElement(E, l - 1)
            }
            i--
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findLastFieldOfLastElementInPrevContainer = function(f, s, t) {
        var c = f.getFormContainers();
        var n;
        var i = s;
        while (!n && i >= 0) {
            var C = c[i];
            if (C.getExpandable() && !C.getExpanded() && t) {
                n = C._oExpandButton;
                return n;
                break
            }
            if (!C.getExpandable() || C.getExpanded()) {
                var l = C.getFormElements().length;
                if (t == true) {
                    n = this.findLastFieldOfPrevElement(C, l - 1, true)
                } else {
                    n = this.findLastFieldOfPrevElement(C, l - 1, 0)
                }
            }
            i--
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFieldBelow = function(c, e) {
        var C = e.getParent();
        var i = C.indexOfFormElement(e);
        var n = this.findFirstFieldOfNextElement(C, i + 1);
        if (!n) {
            var f = C.getParent();
            i = f.indexOfFormContainer(C);
            n = this.findFirstFieldOfFirstElementInNextContainer(f, i + 1)
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype.findFieldAbove = function(c, e) {
        var C = e.getParent();
        var a = C.indexOfFormElement(e);
        var E = C.getFormElements();
        var n;
        var i = a - 1;
        while (!n && i >= 0) {
            var e = E[i];
            n = this.findPrevFieldOfElement(e, 0);
            i--
        }
        if (!n) {
            var f = C.getParent();
            a = f.indexOfFormContainer(C);
            n = this.findLastFieldOfLastElementInPrevContainer(f, a - 1)
        }
        return n
    };
    sap.ui.layout.form.FormLayout.prototype._getDomRef = function(c) {
        var d = c.getFocusDomRef();
        if (!jQuery(d).is(":sapFocusable")) {
            d = undefined
        }
        return d
    }
}());