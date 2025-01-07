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
 * Base class for all checked exceptions thrown by the NetWeaver Scheduler
 * for Java.
 * 
 * @author Dirk Marwinski
 */
public abstract class SchedulerException extends Exception {

    static final long serialVersionUID = 1722401653959364845L;
    
    /**
     * Constructs a new SchedulerException with no message.
     */
    public SchedulerException() {
        super();
    }

    /**
     * Constructs a new SchedulerException with a message string.
     * 
     * @param message message string.
     */
    public SchedulerException(String message) {
        super(message);
    }
    /**
     * Constructs a new SchedulerException with a message and a throwable.
     * 
     * @param message message string 
     * @param throwable a thowable which has caused this exception
     */
    public SchedulerException(String message, Throwable throwable) {
        super(message, throwable);
    }
    /**
     * Constructs a new SchedulerException with a throwable.
     * 
     * @param throwable a thowable which has caused this exception
     */
    public SchedulerException(Throwable throwable) {
        super(throwable);
    }
}
