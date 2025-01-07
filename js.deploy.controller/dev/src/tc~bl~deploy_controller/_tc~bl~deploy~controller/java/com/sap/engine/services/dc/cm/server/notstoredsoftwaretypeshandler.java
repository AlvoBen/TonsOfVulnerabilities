/*
 * Created on 2006-2-14 by radoslav-i
 */
package com.sap.engine.services.dc.cm.server;

import com.sap.engine.services.dc.util.Constants;

/**
 * @author radoslav-i
 */
public abstract class NotStoredSoftwareTypesHandler {

	private static NotStoredSoftwareTypesHandler INSTANCE;
	private static final String HANDLER_IMPL = "com.sap.engine.services.dc.cm.server.impl.NotStoredSoftwareTypesHandlerImpl";

	protected NotStoredSoftwareTypesHandler() {
	}

	public static synchronized NotStoredSoftwareTypesHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static NotStoredSoftwareTypesHandler createFactory() {

		try {
			final Class classFactory = Class.forName(HANDLER_IMPL);
			return (NotStoredSoftwareTypesHandler) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003165 An error occurred while creating an instance of "
					+ "class NotStoredSoftwareTypesHandlerImpl! "
					+ Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Sets software type which archives will not be stored in storage
	 * 
	 * @param notStoredSoftwareTypes
	 *            String of software types, separated by ','.
	 */
	public abstract void setNotStoredSoftwareTypes(String notStoredSoftwareTypes);
}
