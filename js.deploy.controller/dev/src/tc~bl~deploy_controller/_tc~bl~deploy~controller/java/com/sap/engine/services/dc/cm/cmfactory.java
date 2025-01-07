package com.sap.engine.services.dc.cm;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-1
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class CMFactory {
	
	private static Location location = DCLog.getLocation(CMFactory.class);
	
	private static CMFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.impl.CMFactoryImpl";

	protected CMFactory() {
	}

	public static synchronized CMFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static CMFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (CMFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.004704",
							"An error occurred while creating an instance of class CMFactory. {0}{1}",
							new Object[] { Constants.EOL, e.getMessage() });

			DCLog.logErrorThrowable(location, null, errMsg, e);

			throw new RuntimeException(errMsg);
		}
	}

	public abstract CM createComponentManager() throws RemoteException;

}
