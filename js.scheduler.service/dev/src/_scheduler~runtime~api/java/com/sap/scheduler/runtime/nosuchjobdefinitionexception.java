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
 * This exception is thrown by various methods in case the specified 
 * job definition does not exist.
 * 
 * @author Dirk Marwinski
 */
public class NoSuchJobDefinitionException extends SchedulerException {

    static final long serialVersionUID = -2199749340702831141L;
    
    /**
     * Constructs a new NoSuchJobDefinitionException object with no arguments.
     */
    public NoSuchJobDefinitionException() {
        super();
    }

    /**
     * Constructs a new NoSuchJobDefinitionException object with a message
     * string.
     * 
     * @param msg message string
     */
    public NoSuchJobDefinitionException(String msg) {
        super(msg);
    }
}
