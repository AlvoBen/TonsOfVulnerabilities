/*
 * $Id: PortletComponent.java,v 1.1.2.1 2005/04/15 01:04:19 jayashri Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;


/**
 * <p><strong>PortletComponent</strong> is a {@link UIComponent} that 
 * acts as a container for all JSF components in a portlet page. This component
 * works around the problem of "id" clashes in a portal environment where same
 * portlet can be deployed multiple times.This component overrides the
 * <code>getClientId()</code> method to prepend a unique <code>id</code> 
 * everytime it is invoked, so that no two JSF components with in two
 * different portlets have the same <code>id</code>. If a <code>portletId</code>
 * is specified, it has to an EL expression and the application is responsible
 * for making sure that it is unique. If no <portletId> is specifed, 
 * <code>PortletComponent</code> guarantees <code>id</code> uniqueness. 
 */

public class PortletComponent extends UIComponentBase implements NamingContainer {


    // ------------------------------------------------------ Manifest Constants


    /**
     * <p>The standard component type for this component.</p>
     */
    public static final String COMPONENT_TYPE = "PortletComponent";


    /**
     * <p>The standard component family for this component.</p>
     */
    public static final String COMPONENT_FAMILY = "PortletComponent";

    private static final String PORTLET_ID_SERIAL = "PORTLET_ID_SERIAL";
    
     private static final String PORTLET_PAGE = "portletPage";
    
    // ------------------------------------------------------------ Constructors


    /**
     * <p>Create a new {@link PortletComponent} instance with default property
     * values.</p>
     */
    public PortletComponent() {

        super();

    }


    // ------------------------------------------------------ Instance Variables


    // -------------------------------------------------------------- Properties


    public String getFamily() {

        return (COMPONENT_FAMILY);

    }

    private String portletId = null;
    /**
     * <p>Returns the <code>value</code> property of the
     * <code>UICommand</code>. This is most often rendered as a label.</p>
     */
    public String getPortletId() {

	if (this.portletId != null) {
	    return (this.portletId);
	}
	ValueBinding vb = getValueBinding("portletId");
	if (vb != null) {
	    return ((String)vb.getValue(getFacesContext()));
	} else {
	    return (null);
	}

    }


    // ----------------------------------------------------- UIComponent Methods
     public String getClientId(FacesContext context) {
         if (portletId == null) {
             // generate a unique "id" and prepend it to "result"
             portletId = createUniquePortletId(context);
         }
         return portletId;
     }
     
    /**
     * Returns a unique PortletId for the portlet. Since the serial is
     * saved in Application scope, it is guaranteed to be unique for the
     * life of the application.
     */
    private String createUniquePortletId(FacesContext context) {
	int portletIdSerial = 1;
        Map applicationMap = context.getExternalContext().getApplicationMap();
        String porletIdStr = (String) applicationMap.get(PORTLET_ID_SERIAL);
        if (porletIdStr != null) {
            portletIdSerial = Integer.parseInt(porletIdStr);
            portletIdSerial++;
            if (portletIdSerial == Integer.MAX_VALUE) {
	        portletIdSerial = 1;
            }
	}
        applicationMap.put(PORTLET_ID_SERIAL, String.valueOf(portletIdSerial));
	return (PORTLET_PAGE + String.valueOf(portletIdSerial));
    }
}
    