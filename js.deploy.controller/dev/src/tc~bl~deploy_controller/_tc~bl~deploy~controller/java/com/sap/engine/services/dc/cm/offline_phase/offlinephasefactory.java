package com.sap.engine.services.dc.cm.offline_phase;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class OfflinePhaseFactory {

	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.offline_phase.impl.OfflinePhaseFactoryImpl";
	private final static String EOL = System.getProperty("line.separator");

	private static OfflinePhaseFactory INSTANCE;

	protected OfflinePhaseFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized OfflinePhaseFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static OfflinePhaseFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (OfflinePhaseFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003118 An error occurred while creating an instance of "
					+ "class OfflinePhaseFactory! " + EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * @return a new <code>OfflinePhaseProcessor</code>.
	 */
	public abstract OfflinePhaseProcessor createOfflinePhaseProcessor(
			ConfigurationHandlerFactory cfgHandlerFactory)
			throws OfflinePhaseProcessException;

	/**
	 * @return a new <code>OfflinePhaseProcessor</code>.
	 */
	public abstract OfflinePhaseProcessor createOfflinePhaseProcessor(
			ConfigurationHandlerFactory cfgHandlerFactory, boolean loadLibs)
			throws OfflinePhaseProcessException;

}
