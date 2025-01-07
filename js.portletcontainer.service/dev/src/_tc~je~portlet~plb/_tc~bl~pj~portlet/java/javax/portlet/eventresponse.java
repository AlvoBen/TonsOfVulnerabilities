/**
  * Copyright 2006 IBM Corporation.
  */
package javax.portlet;


/**
 * The <CODE>EventResponse</CODE> interface represents the portlet
 * response to an event request.
 * It extends the <CODE>StateAwareResponse</CODE> interface.<br>
 * The portlet container creates an <CODE>EventResponse</CODE> object and 
 * passes it as argument to the portlet's <CODE>processEvent</CODE> method.
 * 
 * @see StateAwareResponse 
 * @see PortletResponse
 * @since 2.0
 */
public interface EventResponse extends StateAwareResponse {

    /**
     * Maintain the current render parameters of the request for
     * the response.
     * <p>
     * All previously set render parameters are cleared.
     * <p>
     * These parameters will be accessible in all
     * subsequent render calls via the
     * <code>PortletRequest.getParameter</code> call until
     * a new request is targeted to the portlet.
     * <p>
     * The given parameters do not need to be encoded
     * prior to calling this method.
     *
     * @param  request   The request the portlet has been provided
     *                   with by the portlet container for the current
     *                   <code>processEvent</code> call, must not be 
     *                   <code>null</code>.
     */
    public void setRenderParameters(EventRequest request);

}
