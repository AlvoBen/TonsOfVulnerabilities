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
 * This exception is raised when the proided parameters for a job
 * do not match the parameters which are specified by the job definition.
 * 
 * @author Dirk Marwinski
 */
public class ParameterValidationException extends SchedulerException {

    static final long serialVersionUID = -5070437405019230577L;
    
    public ParameterValidationException() {
        super();
    }

    public ParameterValidationException(String arg0) {
        super(arg0);
    }
}
