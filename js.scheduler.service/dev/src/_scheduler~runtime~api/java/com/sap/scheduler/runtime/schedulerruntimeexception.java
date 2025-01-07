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
 * A SchedulerRuntimeException can be thrown by the scheduler in case 
 * there is a problem to fulfill the request.
 * <p>
 * In most cases this error is thrown due to a database failure. In this
 * case the result of the job will most likely be void. Catch this 
 * exception in case you need any special handling in this kind of error 
 * situation, otherwise the scheduler runtime will report this exeption.
 * 
 */
public class SchedulerRuntimeException extends RuntimeException
{
    public SchedulerRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Create a SchedulerRuntimeException with a message, cause and code.
     *
     * @param message exception error message.
     * @param cause cause of the exception.
     */
    public SchedulerRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}