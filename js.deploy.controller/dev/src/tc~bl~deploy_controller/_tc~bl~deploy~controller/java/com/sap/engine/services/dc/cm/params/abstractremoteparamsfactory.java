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
package com.sap.engine.services.dc.cm.params;

import java.rmi.RemoteException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class AbstractRemoteParamsFactory implements
		RemoteParamsFactory {

	private static RemoteParamsFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.params.impl.RemoteParamsFactoryImpl";

	protected AbstractRemoteParamsFactory() throws RemoteException {
		super();
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized RemoteParamsFactory getInstance()
			throws RemoteParamsFactoryException {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}

		return INSTANCE;
	}

	private static RemoteParamsFactory createFactory()
			throws RemoteParamsFactoryException {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (RemoteParamsFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "An error occurred while creating an instance of "
					+ "class RemoteParamsFactory!";

			RemoteParamsFactoryException rpfe = new RemoteParamsFactoryException(
					errMsg, e);
			rpfe.setMessageID("ASJ.dpl_dc.003149");
			throw rpfe;
		}
	}

}
