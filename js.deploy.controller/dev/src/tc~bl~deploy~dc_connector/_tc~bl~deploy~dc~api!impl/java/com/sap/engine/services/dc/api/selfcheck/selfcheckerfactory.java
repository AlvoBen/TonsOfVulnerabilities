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
package com.sap.engine.services.dc.api.selfcheck;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DAConstants;

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
	private static SelfCheckerFactory instance;

	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.selfcheck.impl.SelfCheckerFactoryImpl";

	public synchronized static final SelfCheckerFactory getInstance() {
		if (instance == null) {
			try {
				Class classFactory = Class.forName(FACTORY_IMPL);
				instance = (SelfCheckerFactory) classFactory.newInstance();
			} catch (Exception e) {
				final String errMsg = "[ERROR CODE DPL.DCAPI.1142] An error occurred while creating an "
						+ "instance of SelfCheckerFactory! "
						+ DAConstants.EOL
						+ e.getMessage();
				throw new RuntimeException(errMsg);
			}
		}
		return instance;
	}

	/**
	 * @return creates new SelfChecker
	 * @throws SelfCheckerException
	 * @throws ConnectionException
	 * @throws AuthenticationException
	 */
	public abstract SelfChecker createSelfChecker(Session session)
			throws SelfCheckerException, ConnectionException,
			AuthenticationException;

}
