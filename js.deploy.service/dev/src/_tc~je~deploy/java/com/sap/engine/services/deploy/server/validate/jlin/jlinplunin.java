/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.validate.jlin;

import java.util.Set;

/**
 * 
 * 
 * @author anton-g
 * @version 7.1
 */
public abstract class JLinPlunin {

	private static JLinPlunin INSTANCE;
	private static final String JLIN_IMPL = "com.sap.engine.services.deploy.server.validate.jlin.impl.JLinPluninImpl";

	protected JLinPlunin() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized JLinPlunin getInstance()
			throws JLinExecException {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}

		return INSTANCE;
	}

	private static JLinPlunin createFactory() throws JLinExecException {
		try {
			final Class classFactory = Class.forName(JLIN_IMPL);
			return (JLinPlunin) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_ds.006111 An error occurred while creating an instance of "
					+ "class JLinPlunin!";

			throw new JLinExecException(errMsg, e);
		}
	}

	/**
	 * Executes the JLin checks.
	 * 
	 * @param appJlinInfo
	 * @return <code>Set<String></code> with warnings.
	 * @throws JLinExecException
	 * @throws JLinValidationException
	 */
	public abstract Set<String> exec(AppJLinInfo appJlinInfo)
			throws JLinExecException, JLinValidationException;

}
