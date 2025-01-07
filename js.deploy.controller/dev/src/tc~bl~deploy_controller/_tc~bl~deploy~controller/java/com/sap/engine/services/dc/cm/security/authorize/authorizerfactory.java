package com.sap.engine.services.dc.cm.security.authorize;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class AuthorizerFactory {

	private static AuthorizerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.security.authorize.impl.AuthorizerFactoryImpl";

	protected AuthorizerFactory() {
	}

	public static synchronized AuthorizerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static AuthorizerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (AuthorizerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003153 An error occurred while creating an instance of "
					+ "class AuthorizerFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Authorizer createAuthorizer();

}
