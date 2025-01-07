/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet.filter;

import javax.portlet.Event;
import javax.portlet.EventRequest;

/**
 * The <code>EventRequestWrapper</code> provides a convenient 
 * implementation of the <code>EventRequest</code> interface 
 * that can be subclassed by developers wishing to adapt the request.
 * This class implements the Wrapper or Decorator pattern. 
 * Methods default to calling through to the wrapped request object.
 *
 * @since 2.0
 * @see EventRequest
 */
public class EventRequestWrapper extends PortletRequestWrapper implements EventRequest {

    EventRequest request;
    
    
    /**
     * Creates an <code>EventRequest</code> adaptor 
     * wrapping the given request object.
     * 
     * @param request  the event request to wrap
     * @throws java.lang.IllegalArgumentException if the request is <code>null</code>
     */
    public EventRequestWrapper(EventRequest request) {
    	super(request);
    	this.request = request;
    }

    /**
     * Return the wrapped request object.
     * 
     * @return the wrapped request
     */
    public EventRequest getRequest() {
        return request;
    }

    /**
     * Sets the request object being wrapped.
     * 
     * @param request the request to set
     * @throws java.lang.IllegalArgumentException   if the request is null.
     */
    public void setRequest(EventRequest request) {
    	if ( request == null)
    		throw new java.lang.IllegalArgumentException("Request is null");

        this.request = request;
    }

    /**
     * The default behavior of this method is to call 
     * <code>getEvent()</code> on the wrapped request object.
     */
    public Event getEvent() {
        return request.getEvent();
    }

    /**
     *  The default behavior of this method is to call 
     * <code>getMethod()</code> on the wrapped request object.
     */
    public String getMethod() {
        return request.getMethod();
    }


}
