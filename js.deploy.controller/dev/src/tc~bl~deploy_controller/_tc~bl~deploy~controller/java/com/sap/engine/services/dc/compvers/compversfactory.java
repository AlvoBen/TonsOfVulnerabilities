package com.sap.engine.services.dc.compvers;

import javax.sql.DataSource;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class CompVersFactory {

	private static CompVersFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.compvers.impl.CompVersFactoryImpl";

	protected CompVersFactory() {
	}

	public static synchronized CompVersFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static CompVersFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (CompVersFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003274 An error occurred while creating an instance of "
					+ "class CompVersManagerFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract CompVersManager createCompVersManager()
			throws CompVersException;

	public abstract CompVersManager createCompVersManager(DataSource ds)
			throws CompVersException;

	public abstract CompVersSyncher createCompVersSyncher()
			throws CompVersException;

}
