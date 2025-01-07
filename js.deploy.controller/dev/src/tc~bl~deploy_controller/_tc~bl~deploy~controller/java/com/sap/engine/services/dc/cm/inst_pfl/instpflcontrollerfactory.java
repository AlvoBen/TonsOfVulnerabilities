package com.sap.engine.services.dc.cm.inst_pfl;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public abstract class InstPflControllerFactory {

	private static InstPflControllerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.inst_pfl.impl.InstPflControllerFactoryImpl";

	protected InstPflControllerFactory() {
	}

	public static synchronized InstPflControllerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static InstPflControllerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (InstPflControllerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "An error occurred while creating an instance of "
					+ "class InstPflControllerFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg, e);
		}
	}

	public abstract InstPflController createInstPflPersistantController(
			ThreadSystem threadSystem, String runDir, String fileName)
			throws InstPflException;

	public abstract InstPflController createInstPflRuntimeController(
			ThreadSystem threadSystem, String runDir, String host, String user,
			String pass, String instNum) throws InstPflException;

}
