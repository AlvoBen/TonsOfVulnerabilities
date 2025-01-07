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
package com.sap.engine.services.dc.api.cmd.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.cmd.CommandFactory;

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
public class CommandFactoryImpl extends CommandFactory {
	private static final String CLTOOL_PROPS_LOCATION = "com/sap/engine/services/dc/api/cmd/util/cltool.properties";
	private Properties props;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.cmd.CommandFactory#createCommand(java.
	 * lang.String)
	 */
	public Command createCommand(String cmdName) {
		if (cmdName == null) {
			throw new NullPointerException(
					"Command name argument can not be null.");
		}
		String className = this.props.getProperty("cmd." + cmdName + ".impl");
		if (className == null) {
			String msg = "There is no available '" + cmdName + "' command.";
			System.err.println(msg);
			return null;
		}
		Class clazz;
		try {
			clazz = Class.forName(className);
			return (Command) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	protected void init() throws IOException {
		if (this.props != null) {
			return;
		}
		this.props = new Properties();
		InputStream is = null;
		try {
			final InputStream resInputStream = CommandFactoryImpl.class
					.getClassLoader()
					.getResourceAsStream(CLTOOL_PROPS_LOCATION);
			if (resInputStream == null) {
				new IOException(
						"The system could not get input stream from the "
								+ "specified resource '"
								+ CLTOOL_PROPS_LOCATION + "'.");
			}
			is = new BufferedInputStream(resInputStream);
			this.props.load(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					// $JL-EXC$
				}
			}
		}
	}

	public Properties getProperties() {
		return this.props;
	}

}
