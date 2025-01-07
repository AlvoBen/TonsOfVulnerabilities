/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet.filter;

import javax.portlet.RenderRequest;

/**
 * The <code>RenderRequestWrapper</code> provides a convenient 
 * implementation of the <code>RenderRequest</code> interface 
 * that can be subclassed by developers wishing to adapt the request.
 * This class implements the Wrapper or Decorator pattern. 
 * Methods default to calling through to the wrapped request object.
 *
 * @since 2.0
 * @see RenderRequest
 */
public class RenderRequestWrapper extends PortletRequestWrapper implements RenderRequest {

    RenderRequest request;
    
    /**
     * Creates an <code>RenderRequest</code> adaptor 
     * wrapping the given request object.
     * 
     * @param request  the render request to wrap
     * @throws java.lang.IllegalArgumentException if the request is <code>null</code>
     */
    public RenderRequestWrapper(RenderRequest request) {
    	super(request);
    	this.request = request;
    }

    

    /**
     * Return the wrapped request object.
     * 
     * @return the wrapped request
     */
    public RenderRequest getRequest() {
        return request;
    }

    /**
     * Sets the request object being wrapped.
     * 
     * @param request the request to set
     * @throws java.lang.IllegalArgumentException   if the request is null.
     */
    public void setRequest(RenderRequest request) {
    	if ( request == null)
    		throw new java.lang.IllegalArgumentException("Request is null");

    	this.request = request;
    }

    /**
     * The default behavior of this method is to call 
     * <code>getETag()</code> on the wrapped request object.
     */
    public String getETag() {
        return request.getETag();
    }


}
