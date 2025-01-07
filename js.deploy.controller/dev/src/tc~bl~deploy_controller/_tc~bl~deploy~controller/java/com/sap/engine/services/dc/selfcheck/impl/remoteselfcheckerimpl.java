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
package com.sap.engine.services.dc.selfcheck.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.repo.selfchecker.RepoChecker;
import com.sap.engine.services.dc.selfcheck.RemoteSelfChecker;
import com.sap.engine.services.dc.selfcheck.SelfChecker;
import com.sap.engine.services.dc.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.selfcheck.SelfCheckerFactory;
import com.sap.engine.services.dc.selfcheck.SelfCheckerResult;
import com.sap.engine.services.dc.selfcheck.SelfCheckerStatus;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.exception.ExceptionUtils;

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
public class RemoteSelfCheckerImpl implements RemoteSelfChecker {
	private final static SelfChecker[] selfCheckers;
	static {
		selfCheckers = new SelfChecker[] { new RepoChecker() };
	}

	RemoteSelfCheckerImpl() throws RemoteException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.selfcheck.SelfChecker#doCheck()
	 */
	public SelfCheckerResult doCheck() throws SelfCheckerException {
		StringBuffer buffer = new StringBuffer();
		SelfCheckerResult distinctResult;
		SelfCheckerStatus status = SelfCheckerStatus.OK;
		String description;
		SelfCheckerStatus nextCheckStatus;
		for (int i = 0; i < RemoteSelfCheckerImpl.selfCheckers.length; i++) {
			try {
				distinctResult = RemoteSelfCheckerImpl.selfCheckers[i]
						.doCheck();
				if (distinctResult != null) {
					nextCheckStatus = distinctResult.getStatus();
					if (SelfCheckerStatus.ERROR.equals(distinctResult
							.getStatus())) {
						status = SelfCheckerStatus.ERROR;
					}
					buffer.append(Constants.TAB).append("Checker name '")
							.append(
									RemoteSelfCheckerImpl.selfCheckers[i]
											.getName()).append("'.Status :'")
							.append(nextCheckStatus).append("'");
					description = distinctResult.getDescription();
					if (description != null && description.length() > 0) {
						buffer.append(Constants.EOL).append(description);
					}
				} else {
					buffer.append("Check Result is null.");
				}
				buffer.append(Constants.EOL);
			} catch (SelfCheckerException e) {
				buffer.append(Constants.TAB).append("Exception:").append(
						ExceptionUtils.getStackTrace(e));
			}
		}
		return SelfCheckerFactory.getInstance().createSelfCheckerResult(status,
				buffer.toString());
	}
}
