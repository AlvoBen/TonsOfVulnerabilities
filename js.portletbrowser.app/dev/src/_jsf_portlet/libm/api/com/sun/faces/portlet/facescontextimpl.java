/*
 * $Id: FacesContextImpl.java,v 1.3 2004/04/19 23:50:09 jvisvanathan Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import com.sun.faces.el.ELContextImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.el.ELContext;
     
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseStream;
import javax.faces.event.FacesEvent;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import com.sun.org.apache.commons.collections.CursorableLinkedList;
import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 * <p>Concrete implementation of <code>FacesContext</code> for use in
 * a portlet environment.</p>
 */

public final class FacesContextImpl extends FacesContext {


    // ------------------------------------------------------------ Constructors


    public FacesContextImpl(ExternalContext econtext, Lifecycle lifecycle) {
        if ((econtext == null) || (lifecycle == null)) {
            throw new NullPointerException();
        }
        this.econtext = econtext;
        this.lifecycle = lifecycle;
        setCurrentInstance(this);
        if (log.isTraceEnabled()) {
	    log.trace("Created FacesContext " + this);
	}
    }


    // -------------------------------------------------------- Static Variables


    // The Log instance for this class
    private static final Log log =
        LogFactory.getLog(FacesContextFactoryImpl.class);


    // ------------------------------------------------------ Instance Variables


    private Application application;
    private ExternalContext econtext;
    private Lifecycle lifecycle;
    private Map messages = new HashMap();
    private boolean released = false;
    private boolean renderResponse = false;
    private boolean responseComplete = false;
    private ResponseStream responseStream;
    private ResponseWriter responseWriter;
    private UIViewRoot viewRoot;
     private ELContext elContext = null;


    // ------------------------------------------------- FacesContext Properties


    public Application getApplication() {
        assertNotReleased();
        if (application == null) {
            ApplicationFactory af = (ApplicationFactory)
                FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
            application = af.getApplication();
        }
        return (application);
    }


    public Iterator getClientIdsWithMessages() {
        return (messages.keySet().iterator());
    }


    public ExternalContext getExternalContext() {
        assertNotReleased();
        return (econtext);
    }


    public Severity getMaximumSeverity() {
        assertNotReleased();
        Iterator messages = getMessages();
        Severity maximum = null;
        while (messages.hasNext()) {
            Severity severity = ((FacesMessage) messages.next()).getSeverity();
            if (maximum == null) {
                maximum = severity;
            } else if (maximum.getOrdinal() < severity.getOrdinal()) {
                maximum = severity;
            }
        }
        return (maximum);
    }


    public Iterator getMessages() {
        assertNotReleased();
        List results = new ArrayList();
        Iterator clientIds = messages.keySet().iterator();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            results.addAll((List) messages.get(clientId));
        }
        return (results.iterator());
    }


    public RenderKit getRenderKit() {
        assertNotReleased();
        UIViewRoot vr = getViewRoot();
        if (vr == null) {
            return (null);
        }
        String renderKitId = vr.getRenderKitId();
        if (renderKitId == null) {
            return (null);
        }
        RenderKitFactory rkFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        return (rkFactory.getRenderKit(this, renderKitId));
    }


    public boolean getRenderResponse() {
        assertNotReleased();
        return (renderResponse);
    }


    public boolean getResponseComplete() {
        assertNotReleased();
        return (responseComplete);
    }


    public ResponseStream getResponseStream() {
        assertNotReleased();
        return (responseStream);
    }


    public void setResponseStream(ResponseStream responseStream) {
        assertNotReleased();
        if (responseStream == null) {
            throw new NullPointerException();
        }
        this.responseStream = responseStream;
    }


    public ResponseWriter getResponseWriter() {
        assertNotReleased();
        return (responseWriter);
    }


    public void setResponseWriter(ResponseWriter responseWriter) {
        assertNotReleased();
        if (responseWriter == null) {
            throw new NullPointerException();
        }
        this.responseWriter = responseWriter;
    }


    public UIViewRoot getViewRoot() {
        assertNotReleased();
        return (viewRoot);
    }


    public void setViewRoot(UIViewRoot viewRoot) {
        assertNotReleased();
        if (viewRoot == null) {
            throw new NullPointerException();
        }
        this.viewRoot = viewRoot;
    }


    // ---------------------------------------------------- FacesContext Methods


    public void addMessage(String clientId, FacesMessage message) {
        assertNotReleased();
        if (message == null) {
            throw new NullPointerException();
        }
        List list = (List) messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
            messages.put(clientId, list);
        }
        list.add(message);
    }


    public Iterator getMessages(String clientId) {
        assertNotReleased();
        List list = (List) messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
        }
        return (list.iterator());
    }


    public void release() {
        assertNotReleased();
        released = true;
    }


    public void renderResponse() {
        assertNotReleased();
        renderResponse = true;
    }


    public void responseComplete() {
        assertNotReleased();
        responseComplete = true;
    }



    // --------------------------------------------------------- Private Methods


    /**
     * <p>Throw an exception if this instance has been released.</p>
     */
    private void assertNotReleased() { // FIXME - i18n
        if (released) {
            throw new IllegalStateException("This instance has been released");
        }
    }

    public ELContext getELContext() {
         assertNotReleased();
         if (elContext == null) {
             elContext = new ELContextImpl(getApplication().getELResolver());
             elContext.putContext(FacesContext.class, this);
             UIViewRoot root = this.getViewRoot();
             if (null != root) {
                 elContext.setLocale(root.getLocale());
             }
         }
         return elContext;
     }
}
