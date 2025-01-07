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

package com.sap.engine.services.deploy.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.sap.engine.frame.core.locking.AdministrativeLocking;
import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.deploy.logging.DSChangeLog;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.engine.services.deploy.server.ExceptionConstants;


/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class UnlockApplicationCommand extends DSCommand {

	public static final String CMD_NAME = "UNLOCK_APP";

	/**
	 * 
	 * @param deploy
	 */
	public UnlockApplicationCommand(DSChangeLog deploy) {
		this.deploy = deploy;
	}

	/**
	 * This method executes the command when called from the shell.
	 * 
	 * @param env
	 *            - the environment of the corresponding process, which executes
	 *            the command.
	 * @param is
	 *            - an input stream for this command.
	 * @param os
	 *            - an output stream for the resusts of this command.
	 * @param params
	 *            - parameters of the command.
	 */
	public void exec(Environment env, InputStream is, OutputStream os,
			String[] params) {
		isRemote = env.isRemote();
		pw = new PrintWriter(os, true);

		if (params.length != 1 || params[0].equalsIgnoreCase("-h")
				|| params[0].equalsIgnoreCase("-help")
				|| params[0].equals("-?")) {
			pw.println(getHelpMessage());
			return;
		}

		println(OTHER);

		final ApplicationName appNameO = new ApplicationName(params[0]);
		println("The [{0}] command will be executed for [{1}] application.",
				getName(),
				appNameO.getApplicationName());
		if (unlockEnqueue(appNameO)) {
			println("\t - enqueue unlocked [{0}].", appNameO.getApplicationName());
			if (clearTransactionTable(appNameO)) {
				println("\t - transaction table unlocked [{0}].",
						appNameO
								.getApplicationName());
			} else {
				println("\t - transaction table didn't unlock [{0}].",
						appNameO
								.getApplicationName());
			}
		} else {
			println("\t - enqueue didn't unlock [{0}].", appNameO.getApplicationName());
		}
	}

	private boolean unlockEnqueue(final ApplicationName appNameO) {
		println("\n\t* Enqueue");
		boolean result = false;
		try {
			final AdministrativeLocking admLock = PropManager.getInstance()
					.getAppServiceCtx().getCoreContext().getLockingContext()
					.getAdministrativeLocking();

			final String name = PropManager.SERVER_INTERNAL_LOCKING_ID;
			final LockEntry[] lEntries = admLock.getLocks(name, appNameO
					.getApplicationName());

			if (lEntries == null || lEntries.length == 0) {
				println(
						"The [{0}] is not locked in the enqueue from the [{1}].",
						appNameO.getApplicationName(),
						name);
			} else {
				LockEntry entry = null;
				for (int i = 0; i < lEntries.length; i++) {
					entry = lEntries[i];
					if (entry == null) {
						println(
								"The lock entry with index [{0}] for [{1}] is null.",
								i,
								appNameO.getApplicationName());
						continue;
					}
					if (entry.getOwner().endsWith(
							PropManager.getInstance().getClElemID() + "")) {
						println(
								"Will unlock [{0}] in the enqueue, because its owner is [{1}]",
								appNameO.getApplicationName(),
								entry.getOwner());
						LockUtils.unlock(entry.getArgument(), entry.getMode());
						println(" - unlocked");
					} else {
						println(
								OTHER);
					}
				}
			}
			result = true;
		} catch (ThreadDeath td) {
			throw td;
		} catch (OutOfMemoryError oome) {
			throw oome;
		} catch (Throwable th) {
			handleProblem(
					ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() + " with "
							+ appNameO.getApplicationName() + " application." },
					th);
		}
		return result;
	}

	private boolean clearTransactionTable(final ApplicationName appNameO) {
		println("\n\t* Transaction Table");
		boolean result = false;
		try {
			println("Will unlock [{0}] from the transaction table.",
					appNameO
							.getApplicationName());
			deploy.forcedUnregisterTransactionWithoutLock(appNameO
					.getApplicationName(), VIA_TELNET);
			println(" - unlocked");
			result = true;
		} catch (ThreadDeath td) {
			throw td;
		} catch (OutOfMemoryError oome) {
			throw oome;
		} catch (Throwable th) {
			handleProblem(
					ExceptionConstants.UNEXPECTED_EXCEPTION_IN_OPERATION,
					new String[] { getName() + " with "
							+ appNameO.getApplicationName() + " application." },
					th);
		}
		return result;
	}

	/**
	 * Gets the command's help message.
	 * 
	 * @return the help message of the unlock application command.
	 */
	public String getHelpMessage() {
		return "Unlocks an the application on the current server node in the cluster. \n"
				+ "Usage: "
				+ getName()
				+ " <applicationName>\n"
				+ "Parameters:\n"
				+ "   <applicationName> - The name of the application to unlock.\n"
				+ noteMessage();
	}

	private String noteMessage() {
		return "\nThe command "
				+ getName()
				+ "\n"
				+ "1. Should be used only by experienced system administrators, because is not safe.\n"
				+ "2. Before starting it make sure that there are no running operations with the "
				+ "specified application in the cluster. Check the default traces, thread dumps, ...\n";
	}

	/**
	 * Gets the name of the command.
	 * 
	 * @return The name of the command.
	 */
	public String getName() {
		return CMD_NAME;
	}

}
