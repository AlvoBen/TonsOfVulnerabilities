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
package com.sap.engine.services.dc.cm.lock.impl;

import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.lock.LockActionBuilder;
import com.sap.engine.services.dc.cm.lock.LockActionLocation;
import com.sap.engine.services.dc.repo.LocationConstants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class LockActionBuilderImpl extends LockActionBuilder {

	private final static String lockLocationRoot;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(LocationConstants.DEPLOY_CONTROLLER);
		sb.append(LocationConstants.PATH_SEPARATOR);
		sb.append(LockLocationConstants.LOCK);
		lockLocationRoot = sb.toString();
	}
	private final static String singleThreadLocation;
	static {
		final StringBuffer sb = new StringBuffer();
		sb.append(lockLocationRoot);
		sb.append(LocationConstants.PATH_SEPARATOR);
		sb.append(LockLocationConstants.SINGLE_THREAD);
		singleThreadLocation = sb.toString();
	}

	public LockActionBuilderImpl() {
	}

	public LockActionLocation buildSingleThread() {
		return new LockActionLocationImpl(singleThreadLocation);
	}

	public LockActionLocation build(LockAction lockAction) {
		final LockActionLocation location = buildLockActionLocation(lockAction);
		location.setLockAction(lockAction);
		return location;
	}

	public LockActionLocation build(String location) {
		return new LockActionLocationImpl(location);
	}

	private LockActionLocation buildLockActionLocation(LockAction lockAction) {
		if (LockAction.DEPLOY.equals(lockAction)
				|| LockAction.UNDEPLOY.equals(lockAction)
				|| LockAction.POST_PROCESS.equals(lockAction)) {
			return buildSingleThread();
		} else {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003107 The specified lock action '"
							+ lockAction + "' is not supported.");
		}
	}

	public LockActionLocation buildForInstance(LockAction lockAction,
			int instanceId) {
		if (LockAction.SYNC_PROCESS.equals(lockAction)) {
			final StringBuffer location = new StringBuffer();
			location.append(singleThreadLocation);
			location.append(LocationConstants.PATH_SEPARATOR);
			location.append(instanceId);
			LockActionLocation lockActionLocation = new LockActionLocationImpl(
					location.toString());
			lockActionLocation.setLockAction(lockAction);
			return lockActionLocation;
		} else {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003447 The specified lock action '"
							+ lockAction
							+ "' is not supported for lock on instance level.");
		}
	}

}
