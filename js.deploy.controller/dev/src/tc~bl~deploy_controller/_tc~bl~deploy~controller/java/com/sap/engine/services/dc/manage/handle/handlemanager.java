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
package com.sap.engine.services.dc.manage.handle;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class HandleManager {

	private static HandleManager INSTANCE;
	private static final String IMPL = "com.sap.engine.services.dc.manage.handle.impl.HandleManagerImpl";

	protected HandleManager() {
	}

	/**
	 * @return the object reference. The class is implemented as a Singleton.
	 */
	public static synchronized HandleManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static HandleManager createFactory() {

		try {
			final Class classFactory = Class.forName(IMPL);
			return (HandleManager) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003329 An error occurred while creating an instance of "
					+ "class HandleManager! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract void registerHandlers(
			ApplicationServiceContext appServiceCtx)
			throws HandleManagerException;

	public abstract void unregisterHandlers();

	public class HandleManagerException extends DCBaseException {
		private static final long serialVersionUID = 1L;

		public HandleManagerException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

	}

}
