/*
 * $Id: LifecycleImpl.java,v 1.4.22.2 2005/06/09 16:58:10 rogerk Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.portlet;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKitFactory;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.sun.org.apache.commons.logging.Log;
import com.sun.org.apache.commons.logging.LogFactory;


/**
 * <p>Custom implementation of <code>Lifecycle</code> that implements a
 * portlet-specific <code>Lifecycle</code>.</p>
 */

public final class LifecycleImpl extends Lifecycle {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a new <code>LifecycleImpl</code> instance.</p>
     */
    public LifecycleImpl() {
        super();
        if (log.isTraceEnabled()) {
	    log.trace("Created Lifecycle " + this);
	}
    }


    // -------------------------------------------------------- Static Variables


    /**
     * <p><code>Log</code> instance for this class.</p>
     */
    private static final Log log = LogFactory.getLog(LifecycleImpl.class);

    /**
     * <p>Context initialization parameter under which the application
     * may specify an initial view identifier to be displayed.</p>
     */
    public static final String INIT_VIEW_PARAMETER =
        "com.sun.faces.portlet.INIT_VIEW";
    
    /**
     * <p>Portlet session attribute (in portlet scope) under which we will
     * save state information for the current window.</p>
     */
    private static final String WINDOW_STATE_ATTR =
        "com.sun.faces.portlet.WINDOW_STATE";


    // ------------------------------------------------------ Instance Variables


    /**
     * <p>The set of <code>PhaseListener</code>s registered with this
     * <code>Lifecycle</code> instance, in order of registration.</p>
     */
    private List listeners = new ArrayList();


    /**
     * <p>The set of {@link Phase} instances that are to be
     * executed by the <code>execute()</code> method, in order by the
     * ordinal property of each phase.</p>
     */
    private Phase phases[] = {
        null, // ANY_PHASE holder
        new RestoreViewPhase(),
        new ApplyRequestValuesPhase(),
        new ProcessValidationsPhase(),
        new UpdateModelValuesPhase(),
        new InvokeApplicationPhase()
    };


    /**
     * <p>The {@link Phase} instance to process Render Response phase.</p>
     */
    private Phase response = new RenderResponsePhase();


    // ------------------------------------------------------- Lifecycle Methods


    // Add a new PhaseListener to the set of registered listeners
    public void addPhaseListener(PhaseListener listener) {

        if (listener == null) {
	    throw new NullPointerException();
        }
        if (log.isDebugEnabled()) {
            log.debug("addPhaseListener(" + listener.getPhaseId().toString()
                      + "," + listener);
        }
        synchronized (listeners) {
            listeners.add(listener);
        }

    }


    // Execute the phases up to but not including Render Response
    public void execute(FacesContext context) throws FacesException {

        if (context == null) {
	    throw new NullPointerException();
        }

        if (log.isDebugEnabled()) {
            log.debug("execute(" + context + ")");
        }

        restore(context, true);

        for (int i = 1; i < phases.length; i++) {
            if (context.getRenderResponse() ||
                context.getResponseComplete()) {
                break;
            }
            phase((PhaseId) PhaseId.VALUES.get(i), phases[i], context);
        }

        save(context, true);

    }


    // Return the set of PhaseListeners that have been registered
    public PhaseListener[] getPhaseListeners() {

        synchronized (listeners) {
            PhaseListener results[] = new PhaseListener[listeners.size()];
            return ((PhaseListener[]) listeners.toArray(results));
        }

    }


    // Execute the Render Response phase
    public void render(FacesContext context) throws FacesException {
        if (context == null) {
	    throw new NullPointerException();
        }

        if (log.isDebugEnabled()) {
            log.debug("render(" + context + ")");
        }

        restore(context, false);

        if (!context.getResponseComplete()) {
            phase(PhaseId.RENDER_RESPONSE, response, context);
        }
        
        save(context, false);
    }


    // Remove a registered PhaseListener from the set of registered listeners
    public void removePhaseListener(PhaseListener listener) {

        if (listener == null) {
	    throw new NullPointerException();
        }
        if (log.isDebugEnabled()) {
            log.debug("removePhaseListener(" + listener.getPhaseId().toString()
                      + "," + listener);
        }
        synchronized (listeners) {
            listeners.remove(listener);
        }

    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Execute the specified phase instance for the current request.</p>
     *
     * @param phaseId Phase identifier of the current phase
     * @param phase {@link Phase} instance to execute
     * @param context <code>FacesContext</code> for the current request
     *
     * @exception FacesException if thrown by the phase execution
     */
    private void phase(PhaseId phaseId, Phase phase, FacesContext context)
        throws FacesException {

        if (log.isTraceEnabled()) {
            log.trace("phase(" + phaseId.toString() + "," + context + ")");
        }

        // Notify the "beforePhase" method of interested listeners
        synchronized (listeners) {
            if (listeners.size() > 0) {
                PhaseEvent event = new PhaseEvent(context, phaseId, this);
                for (int i = 0; i < listeners.size(); i++) {
                    PhaseListener listener = (PhaseListener) listeners.get(i);
                    if (phaseId.equals(listener.getPhaseId()) ||
                        PhaseId.ANY_PHASE.equals(listener.getPhaseId())) {
                        listener.beforePhase(event);
                    }
                }
            }
        }

        // Execute this phase itself
        phase.execute(context);

        // Notify the "afterPhase" method of interested listeners
        synchronized (listeners) {
            if (listeners.size() > 0) {
                PhaseEvent event = new PhaseEvent(context, phaseId, this);
                for (int i = listeners.size() - 1; i >= 0; i--) {
                    PhaseListener listener = (PhaseListener) listeners.get(i);
                    if (phaseId.equals(listener.getPhaseId()) ||
                        PhaseId.ANY_PHASE.equals(listener.getPhaseId())) {
                        listener.afterPhase(event);
                    }
                }
            }
        }

    }


    /**
     * <p>Restore state information for the current window into the
     * specified <code>FacesContext</code> instance.</p>
     *
     * @param context <code>FacesContext</code> for this request
     * @param action Flag indicating this is action mode (true)
     *  or render mode (false)
     */
    private void restore(FacesContext context, boolean action) {
        if (log.isTraceEnabled()) {
            log.trace("Being restore()");
        }
        
        // Retrieve the cached state information (if any)
        PortletRequest request = (PortletRequest)
            context.getExternalContext().getRequest();
        PortletSession session = request.getPortletSession();
        FacesPortletState state = (FacesPortletState)
            session.getAttribute(WINDOW_STATE_ATTR,
                                 PortletSession.PORTLET_SCOPE);
        if (state == null) {
            setViewId(context);
            return;
        }

        // Restore the cached state information
        if (!action) {
            Iterator messages;
            Iterator clientIds = state.getClientIds();
            while (clientIds.hasNext()) {
                String clientId = (String) clientIds.next();
                messages = state.getMessages(clientId);
                while (messages.hasNext()) {
                    context.addMessage(clientId, (FacesMessage) messages.next());
                }
            }
        }
        context.setViewRoot(state.getViewRoot());

        // Remove the cached state information
        session.removeAttribute(WINDOW_STATE_ATTR,
                                PortletSession.PORTLET_SCOPE);
        if (log.isTraceEnabled()) {
            log.trace("End restore()");
        }

    }


    /**
     * <p>Save state information for the current window from the
     * specified <code>FacesContext</code> instance.</p>
     *
     * @param context <code>FacesContext</code> for this request
     * @param action Flag indicating this is action mode (true)
     *  or render mode (false)
     */
    private void save(FacesContext context, boolean action) {
        if (log.isTraceEnabled()) {
            log.trace("Being save()");
        }
        // Save state information from this FacesContext
        FacesPortletState state = new FacesPortletState();
        Iterator messages;
        Iterator clientIds = context.getClientIdsWithMessages();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            messages = context.getMessages(clientId);
            while (messages.hasNext()) {
                state.addMessage(clientId, (FacesMessage) messages.next());
            }
        }
        state.setViewRoot(context.getViewRoot());

        // Cache the state information in a session in portlet scope
        PortletRequest request = (PortletRequest)
            context.getExternalContext().getRequest();
        PortletSession session = request.getPortletSession();
        session.setAttribute(WINDOW_STATE_ATTR, state,
                             PortletSession.PORTLET_SCOPE);
       if (log.isTraceEnabled()) {
            log.trace("End save()");
       }

    }


    /**
     * <p>Set the view identifier to a default page specified in a
     * context init parameter.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     */
    private void setViewId(FacesContext context) {
        String viewId = (String) context.getExternalContext().
            getRequestMap().get(INIT_VIEW_PARAMETER);
        if (context.getViewRoot() == null) {
            context.setViewRoot(context.getApplication().getViewHandler().createView(context, viewId));
            if (log.isDebugEnabled()) {
                log.debug("Created new ViewRoot" + context.getViewRoot());
            }
        } else {
            context.getViewRoot().setViewId(viewId); 
            if (log.isDebugEnabled()) {
                log.debug("set viewId to " + viewId);
            }
        }
        
        context.getViewRoot().setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
    }

// ------------------------------------------------------- Phase Implementations


    interface Phase {

        public void execute(FacesContext context)
            throws FacesException;

    }


    final class RestoreViewPhase implements Phase {

        public void execute(FacesContext context) throws FacesException {
            if (log.isDebugEnabled()) {
                log.debug("Begin RestoreViewPhase");
            }
            // If an app had explicitly set the tree in the context, use that
            UIViewRoot viewRoot = context.getViewRoot();
            if (viewRoot != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Using precreated view " + viewRoot.getViewId());
                }
                doPerComponentActions(context, viewRoot);
                return;
            }

            // Identify the view identifier of the requested view
            Map requestMap = context.getExternalContext().getRequestMap();
            String viewId = (String)
                requestMap.get("javax.servlet.include.path_info");
            if (viewId == null) {
                viewId = context.getExternalContext().getRequestPathInfo();
            }
            if (viewId == null) {
                viewId = (String)
                    requestMap.get("javax.servlet.include.servlet_path");
            }
            if (viewId == null) {
                viewId = context.getExternalContext().getRequestServletPath();
            }
            if (viewId == null) {
                if (log.isTraceEnabled()) {
                    log.trace("No view identifier found");
                }
                throw new FacesException // PENDING - i18n
                    ("No view identifier in this request");
            }

            // Try to restore the view
            ViewHandler vh = context.getApplication().getViewHandler();
            viewRoot = vh.restoreView(context, viewId);
            if (viewRoot == null) {
                if (log.isTraceEnabled()) {
                    log.trace("Creating new view '" + viewId + "'");
                }
                viewRoot = vh.createView(context, viewId);
                context.renderResponse();
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Restoring old view '" + viewId + "'");
                }
            }
            context.setViewRoot(viewRoot);
            doPerComponentActions(context, viewRoot);
            if (log.isDebugEnabled()) {
                log.debug("End RestoreViewPhase");
            }

        }


        // Do the per-component actions needed during restore
        private void doPerComponentActions
            (FacesContext context, UIComponent component) {

            Iterator kids = component.getFacetsAndChildren();
            while (kids.hasNext()) {
                doPerComponentActions(context, (UIComponent) kids.next());
            }
            ValueBinding vb = component.getValueBinding("binding");
            if (vb != null) {
                vb.setValue(context, component);
            }
        }


    }


    final class ApplyRequestValuesPhase implements Phase {

        public void execute(FacesContext context) throws FacesException {
            if (log.isDebugEnabled()) {
                log.debug("Begin ApplyRequestValuesPhase");
            }
            UIViewRoot viewRoot = context.getViewRoot();
            viewRoot.processDecodes(context);
            if (log.isDebugEnabled()) {
                log.debug("End ApplyRequestValuesPhase");
            }
        }

    }


    final class ProcessValidationsPhase implements Phase {

        public void execute(FacesContext context) throws FacesException {
            if (log.isDebugEnabled()) {
                log.debug("Begin ProcessValidationsPhase");
            }
            UIViewRoot viewRoot = context.getViewRoot();
            viewRoot.processValidators(context);
            if (log.isDebugEnabled()) {
                log.debug("End ProcessValidationsPhase");
            }
        }

    }


    final class UpdateModelValuesPhase implements Phase {

        public void execute(FacesContext context) throws FacesException {
            if (log.isDebugEnabled()) {
                log.debug("Begin UpdateModelValuesPhase");
            }
            UIViewRoot viewRoot = context.getViewRoot();
            viewRoot.processUpdates(context);
            if (log.isDebugEnabled()) {
                log.debug("End UpdateModelValuesPhase");
            }
        }

    }


    final class InvokeApplicationPhase implements Phase {

        public void execute(FacesContext context) throws FacesException {
            if (log.isDebugEnabled()) {
                log.debug("Begin InvokeApplicationPhase");
            }
            UIViewRoot viewRoot = context.getViewRoot();
            viewRoot.processApplication(context);
            if (log.isDebugEnabled()) {
                log.debug("End InvokeApplicationPhase");
            }
        }

    }


    final class RenderResponsePhase implements Phase {

        public void execute(FacesContext context) throws FacesException {
            if (log.isDebugEnabled()) {
                log.debug("Begin RenderResponsePhase");
            }
            try {
                context.getApplication().getViewHandler().
                    renderView(context, context.getViewRoot());
            } catch (IOException e) {
                throw new FacesException(e);
            }
            if (log.isDebugEnabled()) {
                log.debug("End RenderResponsePhase");
            }
        }

    }


}


// ------------------------------------------------------------- Private Classes


/**
 * <p>Private class to represent the JavaServer Faces specific information
 * that is saved and restored for a particular window.
 */

final class FacesPortletState {


    // Methods Saving and Restoring Messages
    private Map messages = new HashMap(); // key=clientId, value=List of FacesMessage
    public void addMessage(String clientId, FacesMessage message) {
        List list = (List) messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
            messages.put(clientId, list);
        }
        list.add(message);
    }
    public Iterator getMessages(String clientId) {
        List list = (List) messages.get(clientId);
        if (list != null) {
            return (list.iterator());
        } else {
            return (Collections.EMPTY_LIST.iterator());
        }
    }
    public Iterator getClientIds() {
        return (messages.keySet().iterator());
    }

    // The UIViewRoot that is the root of our component tree
    private UIViewRoot viewRoot;
    public UIViewRoot getViewRoot() { return this.viewRoot; }
    public void setViewRoot(UIViewRoot viewRoot)
    { this.viewRoot = viewRoot; }


}
