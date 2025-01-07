/**
  * Copyright 2006 IBM Corporation.
  */
package javax.portlet;

/**
 * The <CODE>EventRequest</CODE> represents the request sent to the portlet
 * to handle an event.
 * It extends the PortletRequest interface to provide event request
 * information to portlets.<br>
 * The portlet container creates an <CODE>EventRequest</CODE> object and
 * passes it as argument to the portlet's <CODE>processEvent</CODE> method.
 * 
 * @see ActionRequest
 * @see PortletRequest
 * @since 2.0
 */
public interface EventRequest extends PortletRequest {

    /**
     * Returns the event that triggered the call to the processEvent method.
     * 
     * @return      the event that triggered the current processEvent call. 
     */
    
    public Event getEvent();
    
    /**
     * Returns the name of the HTTP method with which the original action request was made, 
     * for example, POST, or PUT.
     * 
     * @since 2.0
     * @return  a String specifying the name of the HTTP method with which 
     *          this request was made
     */
    public String getMethod();

}
