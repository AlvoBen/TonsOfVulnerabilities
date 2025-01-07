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

package com.sap.engine.services.deploy.server.utils;

import java.util.Arrays;

import com.sap.engine.frame.ProcessEnvironment;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.deploy.server.properties.PropManager;

/**
 * Utility class for lock operations.
 * 
 * @author Anton Georgiev
 * @version 7.00
 */
public class LockUtils {

	public static void lock(String argument, char mode) throws LockException,
			TechnicalLockException, IllegalArgumentException {
		lock(new String[] { argument }, new char[] { mode });
	}

	public static void lockAndWait(String argument, char mode, int wait)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		lock(new String[] { argument }, new char[] { mode }, wait);
	}

	public static void unlock(String argument, char mode)
			throws TechnicalLockException, IllegalArgumentException {
		unlock(new String[] { argument }, new char[] { mode });
	}

	private static void lock(String arguments[], char modes[])
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		//wait 1 second to avoid waiting forever with 0 
		lock(arguments, modes, 1);
	}

	private static void lock(String arguments[], char modes[], int wait)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		final String names[] = getNames(arguments.length);
		try {
			getServerInternalLocking().lock(names, arguments, modes, wait);
		} catch (LockException lEx) {			
			ProcessEnvironment.getThreadDump("Cannot lock names " + Arrays.toString(names)
					+ " with arguments " + Arrays.toString(arguments) + " and modes "
					+ Arrays.toString(modes) + " for time out [" + wait + "], because of ["
					+ lEx.getLocalizedMessage() + "].\r\n"
					+ "Hint: 1). Ask for help in BC-JAS-DPL.");
			throw lEx;
		}
	}

	private static void unlock(String arguments[], char modes[])
			throws TechnicalLockException, IllegalArgumentException {
		final String names[] = getNames(arguments.length);
		getServerInternalLocking().unlock(names, arguments, modes);
	}

	private static String[] getNames(int count) {
		final String names[] = new String[count];
		for (int i = 0; i < names.length; i++) {
			names[i] = PropManager.SERVER_INTERNAL_LOCKING_ID;
		}
		return names;
	}

	private static ServerInternalLocking getServerInternalLocking() {
		return PropManager.getInstance().getServerInternalLocking();
	}
}