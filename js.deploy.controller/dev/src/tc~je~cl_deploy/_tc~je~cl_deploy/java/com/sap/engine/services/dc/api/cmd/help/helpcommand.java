/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 20, 2005
 */
package com.sap.engine.services.dc.api.cmd.help;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.cmd.CommandFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 20, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class HelpCommand implements Command {
	private String name;
	private String[] commandNames;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.Command#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.Command#init(java.lang.String,
	 * java.lang.String[])
	 */
	public int init(String aName, String[] argList) {
		this.name = aName;
		this.commandNames = argList;
		return Command.CODE_SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.Command#execute()
	 */
	public int execute() {
		usage();
		return Command.CODE_SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.Command#usage()
	 */
	public void usage() {
		String[] cmdList;
		if (this.commandNames == null || this.commandNames.length == 0) {
			Properties props = CommandFactory.getInstance().getProperties();
			String key;
			// cmd.undeploy.impl
			String cmd;
			ArrayList list = new ArrayList();
			for (Enumeration keys = props.keys(); keys.hasMoreElements();) {
				key = (String) keys.nextElement();
				if (key.matches("cmd\\..*\\.impl")) {
					cmd = key.substring(4, key.length() - 5);
					list.add(cmd);
				}
			}
			cmdList = new String[list.size()];
			cmdList = (String[]) list.toArray(cmdList);
		} else {
			cmdList = this.commandNames;
		}

		for (int i = 0; i < cmdList.length; i++) {
			System.out.println(buildHelp(cmdList[i]));
			System.out
					.println("-------------------------------------------------");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.Command#getDescription()
	 */
	public String getDescription() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.Command#destroy()
	 */
	public void destroy() {
	}

	private static StringBuffer buildHelp(String cmd) {
		Properties props = CommandFactory.getInstance().getProperties();
		StringBuffer buffer = new StringBuffer(cmd).append(" ").append(
				props.getProperty("cmd.usage." + cmd + ".line",
						"Not available\r\n")).append(
				props.getProperty("cmd.usage.common", "Not available")).append(
				Command.EOL).append(
				props
						.getProperty("cmd.usage." + cmd + ".desc",
								"Not available"));
		return buffer;
	}

}
