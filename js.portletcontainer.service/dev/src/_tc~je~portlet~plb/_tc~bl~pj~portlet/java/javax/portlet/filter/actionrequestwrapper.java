/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.portlet.ActionRequest;

/**
 * The <code>ActionRequestWrapper</code> provides a convenient 
 * implementation of the <code>ActionRequest</code> interface 
 * that can be subclassed by developers wishing to adapt the request.
 * This class implements the Wrapper or Decorator pattern. 
 * Methods default to calling through to the wrapped request object.
 *
 * @since 2.0
 * @see ActionRequest
 */
public class ActionRequestWrapper extends PortletRequestWrapper implements ActionRequest {

    ActionRequest request;
    
    /**
     * Creates an <code>ActionRequest</code> adaptor 
     * wrapping the given request object.
     * 
     * @param request  the action request to wrap
     * @throws java.lang.IllegalArgumentException if the request is <code>null</code>
     */
    public ActionRequestWrapper(ActionRequest request) {
        super(request);
        this.request = request;
    }

    /**
     * The default behavior of this method is to call 
     * <code>getCharacterEncoding()</code> on the wrapped request object.
     */
    public String getCharacterEncoding() {      
        return request.getCharacterEncoding();
    }

    /**
     * The default behavior of this method is to call 
     * <code>getContentLength()</code> on the wrapped request object.
     */
    public int getContentLength() {
        return request.getContentLength();
    }

    /**
     * The default behavior of this method is to call 
     * <code>getContentType()</code> on the wrapped request object.
     */
    public String getContentType() {
        return request.getContentType();
    }

    /**
     * The default behavior of this method is to call 
     * <code>getPortletInputStream()</code> on the wrapped request object.
     */
    public InputStream getPortletInputStream() throws IOException {
        return request.getPortletInputStream();
    }

    /**
     * The default behavior of this method is to call 
     * <code>getReader()</code> on the wrapped request object.
     */
    public BufferedReader getReader() throws UnsupportedEncodingException,
            IOException {
        return request.getReader();
    }

    /**
     * The default behavior of this method is to call 
     * <code>setCharacterEncoding(String enc)</code> 
     * on the wrapped request object.
     */
    public void setCharacterEncoding(String enc)
            throws UnsupportedEncodingException {
       request.setCharacterEncoding(enc);
    }


    /**
     * Return the wrapped request object.
     * 
     * @return the wrapped request
     */
    public ActionRequest getRequest() {
        return request;
    }

    /**
     * Sets the request object being wrapped.
     * 
     * @param request the request to set
     * @throws java.lang.IllegalArgumentException   if the request is null.
     */
    public void setRequest(ActionRequest request) {
    	if ( request == null)
    		throw new java.lang.IllegalArgumentException("Request is null");

        this.request = request;
    }

    /**
     *  The default behavior of this method is to call 
     * <code>getMethod()</code> on the wrapped request object.
     */
    public String getMethod() {
        return request.getMethod();
    }

}
