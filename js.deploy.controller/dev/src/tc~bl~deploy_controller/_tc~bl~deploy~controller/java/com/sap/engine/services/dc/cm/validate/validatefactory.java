/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.validate;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public abstract class ValidateFactory {
	
	private static Location location = DCLog.getLocation(ValidateFactory.class);

	private static ValidateFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.validate.impl.ValidateFactoryImpl";

	protected ValidateFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized ValidateFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ValidateFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (ValidateFactory) classFactory.newInstance();
		} catch (final Exception e) {
			String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.006403",
							"An error occurred while creating an instance of class ValidateFactory. {0}{1}",
							new Object[] { Constants.EOL, e.getMessage() });
			DCLog.logErrorThrowable(location, null, errMsg, e);

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Validator createValidator(final String performerUserUniqueId)
			throws RemoteException;
}
