package com.sap.engine.services.dc.cm.undeploy;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class UndeploymentProcessorFactory {

	private static UndeploymentProcessorFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.undeploy.impl.UndeploymentProcessorFactoryImpl";

	protected UndeploymentProcessorFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized UndeploymentProcessorFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static UndeploymentProcessorFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (UndeploymentProcessorFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003248 An error occurred while creating an instance of "
					+ "class UndeploymentProcessorFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * @param undeploymentStrategy
	 * @param errorStrategy
	 * @param observers
	 * @param denericDelivery
	 * @return a new <code>UndeploymentProcessor</code>.
	 */
	public abstract UndeploymentProcessor createUndeploymentProcessor(
			UndeploymentData undeploymentData) throws UndeploymentException;

}
