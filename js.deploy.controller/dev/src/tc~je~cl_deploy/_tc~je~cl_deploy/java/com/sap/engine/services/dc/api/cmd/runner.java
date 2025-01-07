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
public class Runner {

	public int execute(String cmdName, String[] arguments) {
		// System.out.println("cmdName="+cmdName+", aruments="+Arrays.asList(
		// arguments));
		Command command = CommandFactory.getInstance().createCommand(cmdName);
		if (command == null) {
			usage(false);
			return Command.CODE_CRITICAL_ERROR;
		}
		command.init(cmdName, arguments);
		try {
			int res = command.execute();
			System.out.println(res);
			if (res != Command.CODE_SUCCESS) {
				System.out.println(command.getDescription());
			}
			return res;
		} finally {
			command.destroy();
		}
	}

	private static void usage(boolean exit) {
		if (exit) {
			System.exit(10);
		}
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			usage(true);
		}
		String[] destArgs = new String[args.length - 1];
		System.arraycopy(args, 1, destArgs, 0, destArgs.length);
		Runner runner = new Runner();
		System.exit(runner.execute(args[0], destArgs));
	}
}
