/**
  * Copyright 2006 IBM Corporation.
  */

package javax.portlet;


/**
 * The <CODE>RenderRequest</CODE> represents the request sent to the portlet
 * to handle a render.
 * It extends the PortletRequest interface to provide render request
 * information to portlets.<br>
 * The portlet container creates a <CODE>RenderRequest</CODE> object and
 * passes it as argument to the portlet's <CODE>render</CODE> method.
 * 
 * @see PortletRequest
 * @see ActionRequest
 */
public interface RenderRequest extends PortletRequest
{

    /**
     * This property is set by the container if the portlet container
     * has a cached response for the given validation tag. The property can be
     * retrieved using the <code>getProperty</code> method. 
     * <P>
     * The value is <code>"portlet.ETag "</code>.
     * 
     * @since 2.0
     */
    public static final String ETAG = "portlet.ETag";

    
    /**
     * Returns the validation tag if the portlet container
     * has a cached response for this validation tag, or
     * <code>null</code> if no cached response exists.
     * <p>
     * This call returns the same value as 
     * <code>RenderRequest.getProperty(RenderRequest.ETAG)</code>.
     * 
     * @return  the validation tag if the portlet container
     *          has a cached response for this validation tag, or
     *          <code>null</code> if no cached response exists.
     *          
     * @since 2.0
     */
    public String getETag();
    


}
