/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime.mdb;

import javax.ejb.MessageDrivenBean;
import javax.jms.MessageListener;

import com.sap.scheduler.runtime.JobContext;

/**
 * All jobs implement this interface by inheriting from the 
 * MDBJobImplementation class
 * 
 * @author Dirk Marwinski, Andrew Evers
 */
public interface MDBJob extends MessageDrivenBean, MessageListener { 

    public abstract void onJob(JobContext jobContext)
                                          throws Exception;

}
