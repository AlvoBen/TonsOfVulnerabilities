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
 * job does not exist.
 * 
 * @author Dirk Marwinski
 */
public class NoSuchJobException extends SchedulerException {

    static final long serialVersionUID = -4474914035605199812L;
    
    public NoSuchJobException() {
        super();
    }

    public NoSuchJobException(String arg0) {
        super(arg0);
    }
}
