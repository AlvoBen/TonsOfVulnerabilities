/*
 * $Id: FacesContextFactoryImpl.java,v 1.2 2004/04/19 23:50:08 jvisvanathan Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 * <p>Custom implementation of <code>FacesContextFactory</code> that
 * provides the portlet-specific <code>FacesContext</code> by default.</p>
 */

public final class FacesContextFactoryImpl extends FacesContextFactory {


    // -------------------------------------------------------- Static Variables


    // The Log instance for this class
    private static final Log log =
        LogFactory.getLog(FacesContextFactoryImpl.class);


    // --------------------------------------------- FacesContextFactory Methods


    public FacesContext getFacesContext(Object context,
                                        Object request,
                                        Object response,
                                        Lifecycle lifecycle)
        throws FacesException {
        if ((context == null) || (request == null) ||
            (response == null) || (lifecycle == null)) {
            throw new NullPointerException();
        }

        ExternalContext econtext =
            new ExternalContextImpl((PortletContext) context,
                                    (PortletRequest) request,
                                    (PortletResponse) response);
       
        return (new FacesContextImpl(econtext, lifecycle));

    }


}
