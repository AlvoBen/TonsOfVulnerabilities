package com.sap.engine.services.dc.cm.web_disp;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public abstract class WDControllerFactory {

	private static WDControllerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.web_disp.impl.WDControllerFactoryImpl";

	protected WDControllerFactory() {
	}

	public static synchronized WDControllerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static WDControllerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (WDControllerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "An error occurred while creating an instance of "
					+ "class WDControllerFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg, e);
		}
	}

	public abstract WDController createWDController(String wdServerInfo)
			throws WDException;

}
