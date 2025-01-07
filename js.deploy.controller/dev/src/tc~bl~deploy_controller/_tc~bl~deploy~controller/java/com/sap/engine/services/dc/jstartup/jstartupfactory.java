package com.sap.engine.services.dc.jstartup;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class JStartupFactory {

	private static JStartupFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.jstartup.impl.JStartupFactoryImpl";

	protected JStartupFactory() {
	}

	public static synchronized JStartupFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static JStartupFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (JStartupFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003316 An error occurred while creating an instance of "
					+ "class JStartupFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract JStartupClusterManager createJStartupClusterManager(
			String msHost, int msPort, String osUserName, String osUserPass,
			ThreadSystem threadSystem);

}
