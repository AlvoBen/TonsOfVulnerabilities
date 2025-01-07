/*
 * $Id: PortletComponentTag.java,v 1.1.2.1 2005/04/15 01:04:19 jayashri Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;



/**
 * This class is the tag handler that evaluates the <code>portletPage</code>
 * custom tag. All JSF tags in a portlet should be embedded with this this tag
 * if multiple instances of the same portlet can exist within a portal page.
 * Otherwise it may result in potential "id" clashes especially if the portlet
 * uses JavaScript.
 */

public class PortletComponentTag extends UIComponentTag {
    
    
    private String portletId = null;
    
    
    public void setPortletId(String portletId) {
        this.portletId = portletId;
    }
    
    
    public String getComponentType() {
        return ("PortletComponent");
    }
    
    
    public String getRendererType() {
        return null;
    }
    
    
    protected void setProperties(UIComponent component) {
        
        super.setProperties(component);
        ValueBinding vb = null;
        if (portletId != null) {
            if (isValueReference(portletId)) {
                vb = getFacesContext().getApplication().createValueBinding(portletId);
                component.setValueBinding("portletId", vb);
            } else {
                throw new 
                    FacesException("portletId attribute must be an ELExpression");
            }
        }
    }
}