/*
 * $Id: ViewHandlerImpl.java,v 1.2 2004/01/30 22:18:42 craigmcc Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import java.io.IOException;
import java.util.Locale;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 * <p>Concrete implementation of <code>ViewHandler</code> for use in
 * a portlet environment.  This implementation delegates to the
 * standard <codeViewHandler</code> instance provided to our constructor,
 * and only implements portlet-specific behavior where necessary.</p>
 */

public final class ViewHandlerImpl extends ViewHandler {


    // ------------------------------------------------------------- Constructor


    /**
     * <p>Construct a new <code>ViewHandler</code> instance that delegates
     * all non-portlet-specific behavior to the specified implementation.
     *
     * @param handler The <code>ViewHandler</code> instance to whom
     *  we can delegate
     *
     * @exception NullPointerException if <code>handler</code>
     *  is <code>null</code>
     */
    public ViewHandlerImpl(ViewHandler handler) {

        if (handler == null) {
            throw new NullPointerException();
        }
        if (log.isInfoEnabled()) {
            log.info("Delegating to '" + handler + "'");
        }
        this.handler = handler;

    }


    // -------------------------------------------------------- Static Variables


    /**
     * <p>The URL parameter we use to pass the view identifier of the
     * requested view.</p>
     */
    public static final String VIEW_ID_PARAMETER =
        "com.sun.faces.portlet.VIEW_ID";


    // The Log instance for this class
    private static final Log log = LogFactory.getLog(ViewHandlerImpl.class);


    // ------------------------------------------------------ Instance Variables


    // The ViewHandler we delegate to
    private ViewHandler handler;


    // ----------------------------------------------------- ViewHandler Methods


    public Locale calculateLocale(FacesContext context) {
        return (handler.calculateLocale(context));
    }


    public String calculateRenderKitId(FacesContext context) {
        return (handler.calculateRenderKitId(context));
    }


    public UIViewRoot createView(FacesContext context, String viewId) {
        return (handler.createView(context, viewId));
    }


    public String getActionURL(FacesContext context, String viewId) {
        Object r = context.getExternalContext().getResponse();
        if (!(r instanceof RenderResponse)) {
            throw new IllegalStateException("Must be a RenderResponse");
        }
        RenderResponse response = (RenderResponse) r;
        PortletURL actionURL = response.createActionURL();
        actionURL.setParameter(VIEW_ID_PARAMETER, viewId);
        return (actionURL.toString());
    }


    public String getResourceURL(FacesContext context, String path) {
        return (handler.getResourceURL(context, path));
    }


    public void renderView(FacesContext context, UIViewRoot viewToRender)
        throws IOException {
        handler.renderView(context, viewToRender);
    }


    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return (handler.restoreView(context, viewId));
    }


    public void writeState(FacesContext context) throws IOException {
        handler.writeState(context);
    }


}
