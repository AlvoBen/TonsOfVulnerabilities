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
package com.sap.engine.services.dc.selfcheck;

import com.sap.engine.services.dc.util.Constants;

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
public abstract class SelfCheckerFactory {
	private static SelfCheckerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.selfcheck.impl.SelfCheckerFactoryImpl";

	public synchronized static SelfCheckerFactory getInstance()
			throws SelfCheckerException {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static SelfCheckerFactory createFactory()
			throws SelfCheckerException {
		try {
			return (SelfCheckerFactory) Class.forName(FACTORY_IMPL)
					.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003370 An error occurred while creating an instance of "
					+ "class CMFactory! " + Constants.EOL + e.getMessage();
			throw new SelfCheckerException(errMsg);
		}
	}

	public abstract RemoteSelfChecker getSelfChecker()
			throws SelfCheckerException;

	public abstract SelfCheckerResult createSelfCheckerResult(
			SelfCheckerStatus status, String description);

}
