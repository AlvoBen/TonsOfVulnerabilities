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
package com.sap.engine.services.deploy.server.editor;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.utils.DSConstants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class EditorFactory {

	private static EditorFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.deploy.server.editor.impl.EditorFactoryImpl";

	protected EditorFactory() {
	}

	public static synchronized EditorFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static EditorFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (EditorFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_ds.006054 An error occurred while creating an instance of "
					+ "class EditorFactory! "
					+ DSConstants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract DIWriter getDIWriter(Configuration cfg)
			throws ServerDeploymentException;

	public abstract DIWriter getDIWriter(Version version)
			throws ServerDeploymentException;

	public abstract DIReader getDIReader(Configuration cfg)
			throws ServerDeploymentException;

	public abstract DIReader getDIReader(Version version)
			throws ServerDeploymentException;

	public abstract DIGC getDIGC(Configuration cfg)
			throws ServerDeploymentException;

	public abstract DIGC getDIGC(Version version)
			throws ServerDeploymentException;
}
