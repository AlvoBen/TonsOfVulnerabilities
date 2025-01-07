/*
 * $Id: FacesPortlet.java,v 1.3.22.2 2005/06/09 16:58:09 rogerk Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 * <p><strong>FacesPortlet</strong> is a portlet that manages the request
 * processing lifecycle for web applications that are utilizing JavaServer
 * Faces to construct the user interface in a portlet-based environment.</p>
 */

public class FacesPortlet implements Portlet {

    // The Log instance for this class
    private static final Log log = LogFactory.getLog(FacesPortlet.class);

    // ------------------------------------------------------ Manifest Constants


    /**
     * <p>Context initialization parameter name for the lifecycle identifier
     * of the {@link Lifecycle} instance to be utilized.</p>
     */
    private static final String LIFECYCLE_ID_ATTR =
        FacesServlet.LIFECYCLE_ID_ATTR;


    // ------------------------------------------------------ Instance Variables


    /**
     * <p>The {@link Application} instance for this web application.</p>
     */
    private Application application = null;


    /**
     * <p>Factory for {@link FacesContext} instances.</p>
     */
    private FacesContextFactory facesContextFactory = null;


    /**
     * <p>The {@link Lifecycle} instance to use for request processing.</p>
     */
    private Lifecycle lifecycle = null;


    /**
     * <p>The <code>PortletConfig</code> instance for this portlet.</p>
     */
    private PortletConfig portletConfig = null;


    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Release all resources acquired at startup time.</p>
     */
    public void destroy() {
        if (log.isTraceEnabled()) {
	    log.trace("Begin FacesPortlet.destory() ");
        }
        application = null;
        facesContextFactory = null;
        lifecycle = null;
        portletConfig = null;
        if (log.isTraceEnabled()) {
	    log.trace("End FacesPortlet.destory() ");
        }

    }


    /**
     * <p>Acquire the factory instance we will require.</p>
     *
     * @exception PortletException if, for any reason, the startp of
     *  this Faces application failed.  This includes errors in the
     *  config file that is parsed before or during the processing of
     *  this <code>init()</code> method.
     */
    public void init(PortletConfig portletConfig) throws PortletException {
    
        if (log.isTraceEnabled()) {
	    log.trace("Begin FacesPortlet.init() ");
        }

        // Save our PortletConfig instance
        this.portletConfig = portletConfig;
        if (log.isTraceEnabled()) {
	    log.trace("End FacesPortlet.init() ");
        }
    }

    public FacesContextFactory getFacesContextFactory() throws PortletException{
        if (facesContextFactory != null) {
            return facesContextFactory;
        }
        // Acquire our FacesContextFactory instance
        try {
            facesContextFactory = (FacesContextFactory)
                FactoryFinder.getFactory
                (FactoryFinder.FACES_CONTEXT_FACTORY);
            if (log.isTraceEnabled()) {
	        log.trace("Retrieved facesContextFactory " + 
                    facesContextFactory);
            }
        } catch (FacesException e) {
            Throwable rootCause = e.getCause();
            if (rootCause == null) {
                throw e;
            } else {
                throw new PortletException(e.getMessage(), rootCause);
            }
        }
        return facesContextFactory;
    }
    
    public Lifecycle getLifecycle() throws PortletException {
        if ( lifecycle != null ) {
            return lifecycle;
        }
        try {
            LifecycleFactory lifecycleFactory = (LifecycleFactory)
                FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            if (log.isTraceEnabled()) {
	        log.trace("Retrieved lifecycleFactory " + 
                    lifecycleFactory);
            }
            String lifecycleId =
                portletConfig.getPortletContext().getInitParameter
                (LIFECYCLE_ID_ATTR);
            if (log.isDebugEnabled()) {
	        log.debug("lifecycleId " + lifecycleId);
            }
            if (lifecycleId == null) {
                lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
            lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
            if (log.isTraceEnabled()) {
	        log.trace("Retrieved lifecycle from lifecycleFactory " + 
                    lifecycle);
            }
        } catch (FacesException e) {
            Throwable rootCause = e.getCause();
            if (rootCause == null) {
                throw e;
            } else {
                throw new PortletException(e.getMessage(), rootCause);
            }
        } 
        return lifecycle;
    }

    /**
     * <p>Perform the request processing lifecycle for the specified request,
     * up to (but not including) the <em>Render Response</em> phase.</p>
     *
     * @param request The portlet request we are processing
     * @param response The portlet response we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception PortletException if a portlet processing error occurs
     */
    public void processAction(ActionRequest request, ActionResponse response)
        throws IOException, PortletException {
        if (log.isTraceEnabled()) {
	    log.trace("Begin FacesPortlet.processAction()");
        }
        // if an INIT_VIEW is specified, store it in the requestMap
        // so that it could be used during the RenderResponse Phase
        // to display the initial view.
        String viewId = (String) portletConfig.
            getInitParameter(LifecycleImpl.INIT_VIEW_PARAMETER);
        if (viewId != null) {
            request.setAttribute(LifecycleImpl.INIT_VIEW_PARAMETER, viewId);
        } else {
            throw new PortletException("INIT_VIEW_PARAMETER must be specified");
        }
            
        // Acquire the FacesContext instance for this request
        //Ok we should init lifecycle
        
        FacesContext context =
            getFacesContextFactory().getFacesContext
            (portletConfig.getPortletContext(),
             request, response, lifecycle);
        if (log.isTraceEnabled()) {
	    log.trace("Begin Executing phases");
        }
        // Execute the pre-render request processing lifecycle for this request
        try {
            getLifecycle().execute(context);
            if (log.isTraceEnabled()) {
	        log.trace("End Executing phases");
            }
        } catch (FacesException e) {
            Throwable t = ((FacesException) e).getCause();
            if (t == null) {
                throw new PortletException(e.getMessage(), e);
            } else {
                if (t instanceof PortletException) {
                    throw ((PortletException) t);
                } else if (t instanceof IOException) {
                    throw ((IOException) t);
                } else {
                    throw new PortletException(t.getMessage(), t);
                }
            }

        } finally {
            // Release the FacesContext instance for this request
            context.release();
        }
        if (log.isTraceEnabled()) {
	    log.trace("End FacesPortlet.processAction()");
        }

    }


    /**
     * <p>Perform the <em>Render Response</em> phase of the request processing
     * lifecycle for the specified request.</p>
     *
     * @param request The portlet request we are processing
     * @param response The portlet response we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception PortletException if a portlet processing error occurs
     */
    public void render(RenderRequest request, RenderResponse response)
        throws IOException, PortletException {
        if (log.isTraceEnabled()) {
	    log.trace("Begin FacesPortlet.render()");
        }
        // in a portlet environment, the context type of reponse must
        // be set explicitly.
        response.setContentType(request.getResponseContentType());
        // set portlet title if its set.
        java.util.ResourceBundle bundle = 
           portletConfig.getResourceBundle(request.getLocale());
        if (bundle != null) {
            String title = null;
            try {
                title = bundle.getString("javax.portlet.title");
                response.setTitle(title);
            } catch (Exception e) {
            	// $JL-EXC$
            	// Ignore MissingResourceException
            }
        }
        
        // if an INIT_VIEW is specified, store it in the requestMap
        // so that it could be used during the RenderResponse Phase
        // to display the initial view.
        String viewId = (String) portletConfig.
            getInitParameter(LifecycleImpl.INIT_VIEW_PARAMETER);
        if (viewId != null) {
            request.setAttribute(LifecycleImpl.INIT_VIEW_PARAMETER, viewId);
        }else {
            throw new PortletException("INIT_VIEW_PARAMETER must be specified");
        }
       
        // Acquire the FacesContext instance for this request
        FacesContext context =
            getFacesContextFactory().getFacesContext
            (portletConfig.getPortletContext(),
             request, response, getLifecycle());
        
        if (log.isTraceEnabled()) {
	    log.trace("Begin executing RenderResponse phase ");
        }
        // Execute the render response phase for this request
        try {
            getLifecycle().render(context);
            if (log.isTraceEnabled()) {
	        log.trace("End executing RenderResponse phase ");
            }
        } catch (FacesException e) {
            System.out.println("caught FacesException in FacesPortlet");
            e.printStackTrace();
            Throwable t = ((FacesException) e).getCause();
            if (t == null) {
                throw new PortletException(e.getMessage(), e);
            } else {
                if (t instanceof PortletException) {
                    throw ((PortletException) t);
                } else if (t instanceof IOException) {
                    throw ((IOException) t);
                } else {
                    throw new PortletException(t.getMessage(), t);
                }
            }

        } finally {

            // Release the FacesContext instance for this request
            context.release();

        }
        if (log.isTraceEnabled()) {
	    log.trace("End FacesPortlet.render()");
        }

    }


    // --------------------------------------------------------- Private Methods


}
