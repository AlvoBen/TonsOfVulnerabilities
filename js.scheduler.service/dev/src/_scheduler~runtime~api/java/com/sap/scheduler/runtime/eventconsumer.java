/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime;

/**
 * Event consumers can implement this interface in order to get callbacks.
 * 
 * @author Dirk Marwinski
 */
public interface EventConsumer {
    
    /**
     * Callback-method which will be called when a event occurs for which the user has 
     * registered for.
     * <p>
     * @param ev the Event with the details.
     * @see com.sap.scheduler.runtime.Event
     * @see com.sap.scheduler.api.Scheduler#addEventListener(String[], EventConsumer)
     * @see com.sap.scheduler.api.Scheduler#removeEventListener(EventConsumer)
     */
    public void handle(Event ev);
    
    /**
     * This method returns a name under which a EventConsumer is registered. This 
     * information is needed that one and the same EventConsumer-instance will be 
     * registered only once. If this method returns <code>null</code> what is allowed
     * the class name is used internally as name.
     * <p>
     * @return the String representation for the EventConsumer name or <code>null</code>
     */
    public String getName();
}
