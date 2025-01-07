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

import com.sap.engine.services.dc.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.selfcheck.SelfCheckerResult;
import com.sap.engine.services.dc.selfcheck.SelfCheckerStatus;
import com.sap.engine.services.dc.selfcheck.RemoteSelfChecker;
import com.sap.engine.services.dc.selfcheck.SelfCheckerFactory;

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
public class SelfCheckerFactoryImpl extends SelfCheckerFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.selfcheck.SelfCheckerFactory#getSelfChecker()
	 */
	public RemoteSelfChecker getSelfChecker() throws SelfCheckerException {
		try {
			return new RemoteSelfCheckerImpl();
		} catch (RemoteException e) {
			throw new SelfCheckerException("ASJ.dpl_dc.003369 "
					+ e.getLocalizedMessage(), e);
		}
	}

	public SelfCheckerResult createSelfCheckerResult(SelfCheckerStatus status,
			String description) {
		return new SelfCheckerResultImpl(status, description);
	}
}
