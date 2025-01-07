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
package com.sap.engine.services.dc.cmd.telnet.impl.version;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cmd.telnet.impl.DCCommand;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.explorer.RemoteRepositoryExplorerFactory;
import com.sap.engine.services.dc.repo.explorer.RepositoryExplorer;
import com.sap.engine.services.dc.repo.explorer.RepositoryExploringException;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.version.VersionProvider;
import com.sap.engine.services.dc.version.VersionProviderException;

/**
 * Explores the repository of deploy controller
 * <p>
 * NOTE: This read operation doesn't need to use <code>DCChangeLog</code>
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class VersionCommand extends DCCommand {

	private final static String CMD_NAME = "VERSION";
	private final static String helpMessage;
	static {
		helpMessage = Constants.EOL
				+ "The command will display information about all deployed software components on"
				+ Constants.EOL
				+ " this cluster."
				+ Constants.EOL
				+ "Usage: VERSION [-more]"
				+ Constants.EOL
				+ "Parameters:"
				+ Constants.EOL
				+ "  [-more] - It will display the component element on each software component."
				+ Constants.EOL;

	}

	public VersionCommand(CM cm) {
		this.cm = cm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.interfaces.shell.Command#exec(com.sap.engine.interfaces
	 * .shell.Environment, java.io.InputStream, java.io.OutputStream,
	 * java.lang.String[])
	 */
	public void exec(Environment environment, InputStream input,
			OutputStream output, String[] params) {
		init(output);

		try {
			logCommand(CMD_NAME, params);

			if (params != null) {
				if (params.length == 0) {
					println(VersionProvider.getInstance().getServerVersion(
							getAllSDUs()).getInfo());
				} else if (params.length == 1) {
					if ("-more".equalsIgnoreCase(params[0])) {
						println(VersionProvider.getInstance().getServerVersion(
								getAllSDUs()).getMoreInfo());
					} else if ("-?".equalsIgnoreCase(params[0])
							|| "-h".equalsIgnoreCase(params[0])
							|| "-help".equalsIgnoreCase(params[0])) {
					} else {
						println("The command " + getName()
								+ " has no parameter " + params[0] + ".");
					}
				} else {
					println(getHelpMessage());
				}
			} else {
				println(getHelpMessage());
			}
		} catch (VersionProviderException vpe) {
			println(vpe);
		} catch (RepositoryExploringException ree) {
			println(ree);
		} catch (CMException cme) {
			println(cme);
		}
	}

	private Set getAllSDUs() throws CMException, RepositoryExploringException {
		final RemoteRepositoryExplorerFactory repoExplorerFactory = this.cm
				.getRemoteRepositoryExplorerFactory();
		final RepositoryExplorer repoExplorer = repoExplorerFactory
				.createRepositoryExplorer();

		final Sdu sdus[] = repoExplorer.findAll();
		final Set sdusSet = new HashSet();
		if (sdus != null) {
			for (int i = 0; i < sdus.length; i++) {
				sdusSet.add(sdus[i]);
			}
		}

		return sdusSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.interfaces.shell.Command#getName()
	 */
	public String getName() {
		return CMD_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.interfaces.shell.Command#getHelpMessage()
	 */
	public String getHelpMessage() {
		return helpMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.interfaces.shell.Command#getGroup()
	 */
	public String getGroup() {
		return "ADMIN";
	}

}
