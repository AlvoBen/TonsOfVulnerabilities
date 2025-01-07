/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.selfcheck.impl;

import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;
import com.sap.engine.services.dc.api.selfcheck.SelfChecker;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerResult;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerStatus;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.DAUtils;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Apr 4, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class SelfCheckerImpl implements SelfChecker, IRemoteReferenceHandler {
	private final DALog daLog;
	private final com.sap.engine.services.dc.selfcheck.RemoteSelfChecker remote;

	// remote references to be handled within an instance of this class
	private Set remoteRefs = new HashSet();

	SelfCheckerImpl(Session session) throws SelfCheckerException,
			ConnectionException, AuthenticationException {
		this.daLog = session.getLog();
		// add the instance as a remote reference handler to the session
		session.addRemoteReferenceHandler(this);
		try {
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("Going to create remote Self Checker");
			}
			this.remote = session.createCM().getSelfChecker();
			// register the reference to the obtained remote object
			registerRemoteReference(remote);
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("Remote Self Checker created successfully");
			}
		} catch (com.sap.engine.services.dc.cm.security.authorize.AuthorizationException e) {
			throw new AuthenticationException(this.daLog.getLocation(),
					APIExceptionConstants.AUTH_EXCEPTION, new String[] { e
							.getLocalizedMessage() }, e);
		} catch (com.sap.engine.services.dc.selfcheck.SelfCheckerException e) {
			throw new SelfCheckerException(this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_SELFCHECKER_EXCEPTION,
					new String[] { DAUtils.getThrowableClassName(e),
							e.getMessage() }, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.selfcheck.SelfChecker#doCheck()
	 */
	public SelfCheckerResult doCheck() throws SelfCheckerException {
		try {
			long startTime = System.currentTimeMillis();
			this.daLog.logInfo("ASJ.dpl_api.001161", "Going to check.");
			com.sap.engine.services.dc.selfcheck.SelfCheckerResult remoteResult = this.remote
					.doCheck();
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("Check performed. Time: [{0}] ms.",
						new Object[] { (new Long(System.currentTimeMillis()
								- startTime)) });
			}
			if (remoteResult == null) {
				throw new SelfCheckerException(this.daLog.getLocation(),
						APIExceptionConstants.DC_SELFCHECKRESULT_NOTFOUND,
						new String[] { null });
			}
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("Prepare self check result.");
			}
			SelfCheckerStatus status = (com.sap.engine.services.dc.selfcheck.SelfCheckerStatus.ERROR
					.equals(remoteResult.getStatus())) ? SelfCheckerStatus.ERROR
					: SelfCheckerStatus.OK;
			SelfCheckerResult result = new SelfCheckerResultImpl(status,
					remoteResult.getDescription());
			this.daLog.logInfo("ASJ.dpl_api.001162", "Check result: [{0}]",
					new Object[] { result });
			return result;
		} catch (com.sap.engine.services.dc.selfcheck.SelfCheckerException e) {
			throw new SelfCheckerException(this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_SELFCHECKER_EXCEPTION,
					new String[] { DAUtils.getThrowableClassName(e),
							e.getMessage() }, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * registerRemoteReference(Remote)
	 */
	public void registerRemoteReference(Remote remote) {
		remoteRefs.add(remote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * releaseRemoteReferences()
	 */
	public void releaseRemoteReferences() {
		// try to release the remote references
		P4ObjectBroker broker = P4ObjectBroker.getBroker();
		if (broker == null) {
			this.daLog
					.logDebug(
							"ASJ.dpl_api.001163",
							"The P4ObjectBroker is null while trying to release remote references. The release operation is aborted!");
		} else {
			Iterator iter = this.remoteRefs.iterator();
			while (iter.hasNext()) {
				Remote remoteRef = (Remote) iter.next();
				// separate error handling for each resource
				// to release as much remote refs. as possible
				try {
					broker.release(remoteRef);
				} catch (Exception e) {
					this.daLog
							.logThrowable(
									"ASJ.dpl_api.001164",
									"An exception occured while trying to release remote reference for object [{0}]",
									e, new Object[] { remoteRef });
				}
			}
		}
		remoteRefs.clear();
	}
}
