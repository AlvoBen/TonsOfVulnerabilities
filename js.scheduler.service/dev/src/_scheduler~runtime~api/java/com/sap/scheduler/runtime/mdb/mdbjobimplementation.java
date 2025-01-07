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
package com.sap.scheduler.runtime.mdb;

import javax.ejb.MessageDrivenContext;
import javax.jms.Message;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.scheduler.runtime.JobContext;
import com.sap.scheduler.runtime.mdb.MDBJobDelegate;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * Standard Message Driven Bean Job. All Message Driven Bean jobs must extend 
 * this class.
 * <p>
 * Jobs MUST NOT redefine any method defined in the MessageDrivenBean or 
 * MessageListener interfaces. Jobs MUST define an onJob method.
 */
public abstract class MDBJobImplementation implements MDBJob {
    
	private final static Location location = Location
                                    .getLocation(MDBJobImplementation.class);

	private final static Category category = LoggingHelper.SYS_SERVER;

	public static MDBJobDelegate getDelegate() {
        return MDBJobDelegateReferenceHolder.delegate;
	}

	// javax.ejb.EnterpriseBean is Serializable, MessageDrivenContext
	// is not
	transient private MessageDrivenContext mMessageDrivenContext;

	// MessageDrivenBean implementation
	public void ejbCreate() {
	}

	public void ejbRemove() {
	}

	/**
	 * Process an arbitary message. Delegate to onJob() for an appropriate 
     * message. Any exception from onJob causes a rollback.
	 * 
	 * @param message the message to process.
	 */
	public final void onMessage(Message message) {

		MDBJobDelegate delegate = getDelegate();
		delegate.onMessage(message, mMessageDrivenContext, this);
	}

	// MessageListener implementation
	public void setMessageDrivenContext(MessageDrivenContext mdc) {
		mMessageDrivenContext = mdc;
	}
    
    public MessageDrivenContext getMessageDrivenContext() {
        return mMessageDrivenContext;
    }

	/**
	 * Entry point for job code.
	 * 
	 * @param jobContext context of job.
	 */
	public abstract void onJob(JobContext jobContext) throws Exception;

}