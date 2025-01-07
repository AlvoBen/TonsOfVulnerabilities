/**
 * Copyright 2006 IBM Corporation.
 */

package javax.portlet;

/**
 * The <code>PortletRequestDispatcher</code> interface defines an object that
 * receives requests from the client and sends them to the specified resources
 * (such as a servlet, HTML file, or JSP file) on the server. The portlet
 * container creates the <code>PortletRequestDispatcher</code> object, which
 * is used as a wrapper around a server resource located at a particular path or
 * given by a particular name.
 * 
 */

public interface PortletRequestDispatcher {

    /**
     * 
     * Includes the content of a resource (servlet, JSP page, HTML file) in the
     * response. In essence, this method enables programmatic server-side
     * includes.
     * <p>
     * The included servlet cannot set or change the response status code or set
     * headers; any attempt to make a change is ignored.
     * <p>
     * This method is kept in order to provide backward compatibility with
     * version 1.0. Please use {@link #include(PortletRequest, PortletResponse)} instead
     * of this method.
     * 
     * @param request
     *            a {@link RenderRequest} object that contains the client
     *            request. Must be either the render request passed to the
     *            portlet in <code>render</code> or a wrapped version of this
     *            render request.
     * 
     * @param response
     *            a {@link RenderResponse} object that contains the render
     *            response. Must be either the render response passed to the
     *            portlet in <code>render</code> or a wrapped version of this
     *            render response.
     * 
     * @exception PortletException
     *                if the included resource throws a ServletException, or
     *                other exceptions that are not Runtime- or IOExceptions.
     * 
     * @exception java.io.IOException
     *                if the included resource throws this exception
     * 
     *  
     */

    public void include(RenderRequest request, RenderResponse response)
            throws PortletException, java.io.IOException;

    /**
     * 
     * Includes the content of a resource (servlet, JSP page, HTML file) in the
     * response. In essence, this method enables programmatic server-side
     * includes.
     * <p>
     * The included servlet cannot set or change the response status code or set
     * headers; any attempt to make a change is ignored.
     * 
     * 
     * @param request
     *            a {@link PortletRequest} object that contains the portlet
     *            request. Must be either the original request passed to the
     *            portlet or a wrapped version of this request.
     * 
     * @param response
     *            a {@link PortletResponse} object that contains the portlet
     *            response. Must be either the portlet response passed to the
     *            portlet or a wrapped version of this response.
     * 
     * @exception PortletException
     *                if the included resource throws a ServletException, or
     *                other exceptions that are not Runtime- or IOExceptions.
     * 
     * @exception java.io.IOException
     *                if the included resource throws this exception
     *                
     * @since 2.0
     */

    public void include(PortletRequest request, PortletResponse response)
            throws PortletException, java.io.IOException;

    /**
     * Forwards a portlet request from a portlet to another resource (servlet, JSP file, or HTML file) 
     * on the server. This method allows the portlet to do preliminary processing of a 
     * request and another resource to generate the response.
     * <p>
     * The <code>forward</code> method should be called before the response has been 
     * committed to the portlet container (before response body output has been flushed). 
     * If the response already has been committed, this method throws an 
     * <code>IllegalStateException</code>. Uncommitted output in the response buffer 
     * is automatically cleared before the forward.
     * <p>
     * The request and response parameters must be either the same objects as were passed to 
     * the calling portlet or be wrapped versions of these.
     * 
     * @param request  a request object that represents the request to the 
     *                 portlet
     * @param response a reponse object that contains the portlet response
     *  
     * @exception PortletException
     *                if the included resource throws a ServletException, or
     *                other exceptions that are not Runtime- or IOExceptions.
     * @exception java.io.IOException
     *                if the included resource throws this exception
     * @exception java.lang.IllegalStateException
     *                if the response was already committed
     * @since 2.0
     */
    public void forward(PortletRequest request, PortletResponse response)
            throws PortletException, java.io.IOException;

    

}
