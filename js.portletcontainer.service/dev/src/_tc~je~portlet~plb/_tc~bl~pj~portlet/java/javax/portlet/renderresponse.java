/**
 * Copyright 2006 IBM Corporation.
 */

package javax.portlet;

/**
 * The <CODE>RenderResponse</CODE> defines an object to assist a portlet in
 * sending a response to the portal. It extends the <CODE>MimeResponse</CODE>
 * interface to provide specific render response functionality to portlets.<br>
 * The portlet container creates a <CODE>RenderResponse</CODE> object and
 * passes it as argument to the portlet's <CODE>render</CODE> method.
 * 
 * @see RenderRequest
 * @see PortletResponse
 * @see MimeResponse
 */
public interface RenderResponse extends MimeResponse {

    /**
     * This method sets the title of the portlet.
     * <p>
     * The value can be a text String
     * 
     * @param title
     *            portlet title as text String or resource URI
     */
    public void setTitle(String title);
   
    
	/**
     * This method allows the portlet to tell the portal the next possible
     * portlet modes that the make sense from the portlet point of view.
     * <p>
     * If set, the portal should honor these enumeration of portlet modes and
     * only provide the end user with choices to the provided portlet modes or a
     * subset of these modes based on access control considerations.
     * <p>
     * If the portlet does not set any next possible portlet modes the default
     * is that all portlet modes that the portlet has defined supporting in the
     * portlet deployment descriptor are meaningful new portlet modes.
     * 
     * @param portletModes
     *            <code>Enumeration</code> of <code>PortletMode</code> objects with the
     *            next possible portlet modes that the make sense from the
     *            portlet point of view, must not be <code>null</code> or an
     *            empty enumeration.
     * @since 2.0
     */
	public void setNextPossiblePortletModes(java.util.Collection<PortletMode> portletModes);

    /**
     * Sets the MIME type for the render response. The portlet should set the
     * content type before calling {@link #getWriter} or
     * {@link #getPortletOutputStream}.
     * <p>
     * Calling <code>setContentType</code> after <code>getWriter</code> or
     * <code>getOutputStream</code> does not change the content type.
     * <p>
     * The portlet container will ignore any character encoding
     * specified as part of the content type for <code>render</code>
     * calls.
     * 
     * @param type
     *            the content MIME type
     * 
     * @throws java.lang.IllegalArgumentException
     *             if the given type is not in the list 
     *             returned by <code>PortletRequest.getResponseContentTypes</code>
     * 
     * @see PortletRequest#getResponseContentTypes
     * @see #getContentType
     */
    public void setContentType(String type);

}
