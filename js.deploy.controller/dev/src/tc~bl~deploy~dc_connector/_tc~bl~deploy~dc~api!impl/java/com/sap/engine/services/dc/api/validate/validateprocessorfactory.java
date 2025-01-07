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
package com.sap.engine.services.dc.api.validate;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public abstract class ValidateProcessorFactory {
	private static ValidateProcessorFactory instance;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.validate.impl.ValidateProcessorFactoryImpl";

	public synchronized static final ValidateProcessorFactory getInstance() {
		if (instance == null) {
			try {
				Class classFactory = Class.forName(FACTORY_IMPL);
				instance = (ValidateProcessorFactory) classFactory
						.newInstance();
			} catch (Exception e) {
				final String errMsg = "[ERROR CODE DPL.DCAPI.1169] An error occurred while creating an "
						+ "instance of ValidateProcessorFactory! "
						+ DAConstants.EOL + e.getMessage();
				throw new RuntimeException(errMsg);
			}
		}
		return instance;
	}

	/**
	 * Creates new Validate processor implementation.
	 * 
	 * @param session
	 *            client session
	 * @return new Validate processor implementation.
	 */
	public abstract ValidateProcessor createValidateProcessor(Session session)
			throws ConnectionException, ValidateException;
}
