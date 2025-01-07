/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
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
 * This interface is called in order to determin if an event subscriber
 * will accept the event. If the event subscriber is not interested it is
 * not stored in the event queue (prevents queue overflow in most 
 * situations).
 * 
 * @author Dirk Marwinski
 *
 */
public interface EventAcceptor {

    public boolean acceptEvent(Event e);
}
