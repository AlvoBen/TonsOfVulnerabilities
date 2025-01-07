/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Sep 7, 2005
 */
package com.sap.engine.services.dc.api.cmd;

import java.io.IOException;
import java.util.Properties;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Sep 7, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class CommandFactory {
	private static CommandFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.api.cmd.impl.CommandFactoryImpl";

	public static synchronized CommandFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static CommandFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			CommandFactory commandFactory = (CommandFactory) classFactory
					.newInstance();
			commandFactory.init();
			return commandFactory;
		} catch (Exception e) {
			final String errMsg = "An error occurred while creating an instance of "
					+ "class CommandFactory! " + Command.EOL + e.getMessage();
			throw new RuntimeException(errMsg);
		}
	}

	protected abstract void init() throws IOException;

	public abstract Command createCommand(String cmdName);

	public abstract Properties getProperties();
}
