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

import javax.ejb.MessageDrivenContext;
import javax.jms.Message;

/**
 * Delegate interface to be implemented by vendors providing support for MDB jobs. 
 * The class will be constructed using reflection. There is one class of this type
 * for each provider which is used by all jobs. 
 */
public interface MDBJobDelegate
{
  /**
   * Process the JMS message for the given MDB
   * 
   * @param msg JMS Message
   * @param mdc JMS Message Driven Bean Context
   * @param job MDB job that implements this job
   */
  public void onMessage(Message msg, MessageDrivenContext mdc, MDBJob job);
	
}
