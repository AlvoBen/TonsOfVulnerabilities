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
package com.sap.engine.services.dc.cmd.telnet;

import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.dc.cmd.CMDRegisterException;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class TelnetCommandsManager {

	private static TelnetCommandsManager INSTANCE;
	private static final String IMPL = "com.sap.engine.services.dc.cmd.telnet.impl.TelnetCommandsManagerImpl";

	protected TelnetCommandsManager() {
	}

	/**
	 * @return the object reference. The class is implemented as a Singleton.
	 */
	public static synchronized TelnetCommandsManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static TelnetCommandsManager createFactory() {

		try {
			final Class classFactory = Class.forName(IMPL);
			return (TelnetCommandsManager) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003269 An error occurred while creating an instance of "
					+ "class TelnetCommandsManager! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract void registerTelnetCommands(ShellInterface shell)
			throws CMDRegisterException;

}
