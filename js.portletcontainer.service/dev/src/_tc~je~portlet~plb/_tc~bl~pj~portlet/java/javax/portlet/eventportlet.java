/**
  * Copyright 2006 IBM Corporation.
  */
package javax.portlet;

import java.io.IOException;

/**
 * The <code>EventPortlet</code> interface allows
 * portlets receiving events.
 * <p>
 * Events are part of the action processing and must
 * be finished before the rendering phase can start.
 * Portlets can receive events issued by other portlets or
 * portlet container defined events.
 * <p>
 * Portlets should declare the events it would like to receive
 * via the <code>supported-processing-event</code> tag and events published
 * via the <code>supported-publishing-event</code> tag in the portlet 
 * deployment descriptor. 
 * <p>
 * The event model is a loosely coupled model where the wiring between
 * published and receiving events is done at the portal / portlet container
 * level.
 * 
 * @since 2.0
 */

public interface EventPortlet {

    /**
     * Called by the portlet container requesting the portlet
     * to process a specific event.
     * 
     * @param request  the event request
     * @param response  the event response
     * @exception  PortletException
     *                   if the portlet has problems fulfilling the
     *                   request
     * @exception  UnavailableException   
     *                   if the portlet is unavailable to process the event at this time
     * @exception  PortletSecurityException  
     *                   if the portlet cannot fullfill this request because of security reasons
     * @exception  IOException
     *                   if the streaming causes an I/O problem
     */
    public void processEvent (EventRequest request, EventResponse response) 
        throws PortletException, java.io.IOException;
}
