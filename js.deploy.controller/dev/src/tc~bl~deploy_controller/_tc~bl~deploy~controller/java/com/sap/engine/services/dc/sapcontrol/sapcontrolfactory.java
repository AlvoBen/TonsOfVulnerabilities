package com.sap.engine.services.dc.sapcontrol;

import com.sap.engine.frame.core.thread.ThreadSystem;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public abstract class SapControlFactory {

	private static SapControlFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.sapcontrol.impl.SapControlFactoryImpl";

	protected SapControlFactory() {
	}

	public static synchronized SapControlFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static SapControlFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (SapControlFactory) classFactory.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"An error occurred while creating an instance of class SapControlFactory.",
					e);
		}
	}

	public abstract SapControl createSapControl(ThreadSystem threadSystem,
			String runDir, String host, String user, String pass, String instNum)
			throws SapControlException;

}
