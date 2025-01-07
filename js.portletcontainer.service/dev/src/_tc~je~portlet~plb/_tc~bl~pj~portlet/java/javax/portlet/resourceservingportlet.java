/**
  * Copyright 2006 IBM Corporation.
  */
package javax.portlet;

/**
 * The <code>ResourceServingPortlet</code> interface allows
 * serving resources through the portlet.
 * <p>
 * The portlet container must call this method for links created by
 * the <code>RenderResponse.createResourceURL()</code> call.
 * If the portlet creates resource URLs with <code>RenderResponse.createResourceURL()</code> 
 * it must implement this lifecycle method.
 * 
 * @since 2.0
 */
public interface ResourceServingPortlet {

    /**
     * Called by the portlet container to allow the portlet to generate
     * the resource content based on its current state.
     * The portal / portlet container must not render any output in addition 
     * to the content returned by the portlet. The portal / portlet container 
     * should expect that the portlet may return binary content for a 
     * <code>renderResource</code> call.
     *
     * @param   request
     *          the resource request
     * @param   response
     *          the resource response
     *
     * @exception   PortletException
     *              if the portlet has problems fulfilling the
     *              rendering request
     * @exception  UnavailableException     
     *                   if the portlet is unavailable to perform render at this time
     * @exception  PortletSecurityException  
     *                   if the portlet cannot fullfill this request because of security reasons
     * @exception  java.io.IOException
     *              if the streaming causes an I/O problem
     */


    public void serveResource(ResourceRequest request, ResourceResponse response)
        throws PortletException, java.io.IOException;
}
