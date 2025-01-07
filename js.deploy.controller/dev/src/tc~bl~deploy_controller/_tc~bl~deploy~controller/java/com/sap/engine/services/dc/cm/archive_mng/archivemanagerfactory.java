package com.sap.engine.services.dc.cm.archive_mng;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class ArchiveManagerFactory {

	private static ArchiveManagerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.archive_mng.impl.ArchiveManagerFactoryImpl";
	private static final Location location = 
		DCLog.getLocation(ArchiveManagerFactory.class);
	
	protected ArchiveManagerFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized ArchiveManagerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ArchiveManagerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (ArchiveManagerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.006501",
							"[ERROR CODE DPL.DC.3019] An error occurred while creating an instance of class ArchiveManagerFactory. {0}{1}",
							new Object[] { Constants.EOL, e.getMessage() });

			DCLog.logErrorThrowable(location, null, errMsg, e);
			throw new RuntimeException(errMsg);
		}
	}

	public abstract ArchiveManager createArchiveManager()
			throws RemoteException;

}
