package com.sap.engine.services.dc.cm.undeploy;

import java.rmi.RemoteException;

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
public abstract class AbstractUndeployFactory implements UndeployFactory {

	private static AbstractUndeployFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.undeploy.impl.UndeployFactoryImpl";

	protected AbstractUndeployFactory() throws RemoteException {
		super();
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized UndeployFactory getInstance()
			throws UndeploymentException {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}

		return INSTANCE;
	}

	private static AbstractUndeployFactory createFactory()
			throws UndeploymentException {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (AbstractUndeployFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003246 An error occurred while creating an instance of "
					+ "class UndeployFactory!";

			throw new UndeploymentException(errMsg, e);
		}
	}

}
