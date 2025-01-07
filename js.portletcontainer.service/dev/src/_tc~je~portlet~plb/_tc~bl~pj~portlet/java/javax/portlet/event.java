/**
  * Copyright 2006 IBM Corporation.
  */
package javax.portlet;

/**
 * The <code>Event</code> interface represents an event that the portlet has received
 * in the event processing phase.
 * <p>
 * The <code>Event</code> interface encapsulates the event name and event payload,
 * it does not represent the event object itself.
 * <p>
 * The portlet must define the events it is able to receive in the portlet deployment
 * descriptor with the <code>supported-processing-event</code>.
 * 
 * @since 2.0
 * @see EventPortlet
 */
public interface Event {
    
    /**
     * Get the event QName.
     * 
     * @return  the QName of the event, never <code>null</code>.
     */
    public javax.xml.namespace.QName getQName();

    /**
     * Get the local part of the event name.
     * 
     * @return  the local part of the event, never <code>null</code>.
     */
    public java.lang.String getName();

    /**
     * Get the event payload.
     * 
     * @return  event payload, must be serializable.
     *          May return <code>null</code> if this event has no payload.
     */
    public java.io.Serializable getValue();
   
}
