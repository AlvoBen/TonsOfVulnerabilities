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
package com.sap.engine.services.dc.util.lock;

import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * Utils class for lock operations.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DCEnqueueLock {
	
	private Location location = DCLog.getLocation(this.getClass());

	private static DCEnqueueLock INSTANCE;

	private DCEnqueueLock() {
	}

	public synchronized static DCEnqueueLock getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DCEnqueueLock();
		}
		return INSTANCE;
	}

	/**
	 * Locks the <code>String</code> with exclusive noncomulative lock.
	 * 
	 * @param argument
	 * @throws LockException
	 * @throws TechnicalLockException
	 * @throws IllegalArgumentException
	 */
	public void lockExclusiveNoncomulative(String argument)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		lock(argument, LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE);
	}

	/**
	 * Locks the <code>String</code> with exclusive noncomulative lock if the
	 * <code>LockData</code> is null and with shared lock <code>LockData</code>
	 * is not null. The <code>LockData</code> will be locked according to its
	 * flags.
	 * 
	 * @param argument
	 * @param lockData
	 * @throws LockException
	 * @throws TechnicalLockException
	 * @throws IllegalArgumentException
	 */
	public void lock4Parallel(String argument, LockData lockData)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		if (lockData == null) {
			lockExclusiveNoncomulative(argument);
		} else {
			int i = lockData.getLockItems().size() + 1;
			String arguments[] = new String[i];
			char modes[] = new char[i];
			lockData.initLockParameters(arguments, modes, argument,
					LockingContext.MODE_SHARED);
			lock(arguments, modes);
		}
	}

	/**
	 * Tries to lock the <code>String</code> with exclusive noncomulative lock.
	 * If cannot will try several times during <code>int</code> ms.
	 * 
	 * @param argument
	 * @param wait
	 * @throws LockException
	 * @throws TechnicalLockException
	 * @throws IllegalArgumentException
	 */
	public void lockAndWaitExclusiveNoncomulative(String argument, int wait)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		lockAndWait(argument, LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE, wait);
	}

	/**
	 * Unlocks the <code>String</code> with exclusive noncomulative lock.
	 * 
	 * @param argument
	 * @throws TechnicalLockException
	 * @throws IllegalArgumentException
	 */
	public void unlockExclusiveNoncomulative(String argument)
			throws TechnicalLockException, IllegalArgumentException {
		unlock(argument, LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE);
	}

	/**
	 * Unlocks the <code>String</code> with exclusive noncomulative unlock if
	 * the <code>LockData</code> is null and with shared unlock
	 * <code>LockData</code> is not null. The <code>LockData</code> will be
	 * unlocked according to its flags.
	 * 
	 * @param argument
	 * @param lockData
	 * @throws LockException
	 * @throws TechnicalLockException
	 * @throws IllegalArgumentException
	 */
	public void unlock4Parallel(String argument, LockData lockData)
			throws TechnicalLockException, IllegalArgumentException {
		if (lockData == null) {
			unlockExclusiveNoncomulative(argument);
		} else {
			int i = lockData.getLockItems().size() + 1;
			String arguments[] = new String[i];
			char modes[] = new char[i];
			lockData.initLockParameters(arguments, modes, argument,
					LockingContext.MODE_SHARED);
			unlock(arguments, modes);
		}
	}

	private void lock(String arguments[], char modes[]) throws LockException,
			TechnicalLockException {

		final String names[] = getNames(arguments.length);
		final ServerInternalLocking locking = ServiceConfigurer.getInstance()
				.getServerInternalLocking();

		// a failover with automatic retry if the locking fails due to
		// concurrent operation.
		int attempts = ServiceConfigurer.getInstance().getLockingRetries();
		final int waitInterval = ServiceConfigurer.getInstance()
				.getLockingInterval();

		while (attempts > 0) {

			try {
				locking.lock(names, arguments, modes);
				return;

			} catch (LockException e) {

				attempts--;

				if (attempts <= 0) {

					// this was the last attempt. Throw the locking exception
					// with the info for the collisions if any
					String collisions = getCollisions(names, arguments, modes,
							locking);
					DCLog
							.logInfo(
									location,
									"ASJ.dpl_dc.005910",
									"The following collisions were detected during the last attempt to lock the enqueue: [{0}]",
									new Object[] { collisions });
					throw e;

				}

				// if this isn't the last attempt, wait for a while and retry
				try {
					Thread.sleep(waitInterval);
				} catch (InterruptedException interruptedException) {

					// if the thread is interrupted throw the original exception
					// to
					// release the thread as soon as possible.
					DCLog.logDebugThrowable(location, interruptedException);
					throw e;

				}

			}
		}

	}

	/**
	 * Check which of the locks cannot be established
	 * 
	 * @param names
	 * @param arguments
	 * @param modes
	 * @param locking
	 * @return
	 */
	private String getCollisions(String[] names, String[] arguments,
			char[] modes, ServerInternalLocking locking) {

		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < arguments.length; i++) {

			char calculatedMode;
			String modeString;

			if (modes[i] == LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE) {

				calculatedMode = LockingConstants.MODE_CHECK_EXCLUSIVE_NONCUMULATIVE;
				modeString = "EXCLUSIVE";

			} else {

				calculatedMode = LockingConstants.MODE_CHECK_SHARED;
				modeString = "SHARED";
			}

			try {
				locking.lock(names[i], arguments[i], calculatedMode);
			} catch (Exception e) {

				buf.append(arguments[i] + "/" + modeString + " ");
			}
		}

		return (buf.length() == 0) ? "collisions cannot be determined" : buf
				.toString();

	}

	private void lock(String arguments[], char modes[], int wait)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		final String names[] = getNames(arguments.length);
		final ServerInternalLocking locking = ServiceConfigurer.getInstance()
				.getServerInternalLocking();
		locking.lock(names, arguments, modes, wait);
	}

	public void lock(String argument, char mode) throws LockException,
			TechnicalLockException, IllegalArgumentException {
		lock(new String[] { argument }, new char[] { mode });
	}

	private void lockAndWait(String argument, char mode, int wait)
			throws LockException, TechnicalLockException,
			IllegalArgumentException {
		lock(new String[] { argument }, new char[] { mode }, wait);
	}

	private void unlock(String arguments[], char modes[])
			throws TechnicalLockException, IllegalArgumentException {
		final String names[] = getNames(arguments.length);
		final ServerInternalLocking locking = ServiceConfigurer.getInstance()
				.getServerInternalLocking();
		locking.unlock(names, arguments, modes);
	}

	public void unlock(String argument, char mode)
			throws TechnicalLockException, IllegalArgumentException {
		unlock(new String[] { argument }, new char[] { mode });
	}

	private String[] getNames(int count) {
		final String names[] = new String[count];
		for (int i = 0; i < names.length; i++) {
			names[i] = ServiceConfigurer.SERVER_INTERNAL_LOCKING_ID;
		}
		return names;
	}

}
