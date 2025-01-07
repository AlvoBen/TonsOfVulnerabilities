/*
 * Copyright (c) 2000 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved. This software is the confidential and proprietary information of
 * SAP AG, Walldorf. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement you
 * entered into with SAP.
 */
package com.sap.engine.services.dc.util.prepare;

import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.ClusterUtils;
import com.sap.engine.services.dc.util.lock.DCEnqueueLock;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;

/**
 * Prepares the configuration for the initial work.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class CfgPreparator {

	private static CfgPreparator INSTANCE = new CfgPreparator();

	private CfgPreparator() {
	}

	public static CfgPreparator getInstance() {
		return INSTANCE;
	}

	/**
	 * Prepares the tree structures in the <code>Configuration</code>
	 * 
	 * @param waitArtument
	 *            <code>String</code>
	 * @param waitMs
	 *            <code>Set</code> ms
	 * @param cm
	 *            <code>ClusterMonitor</code>
	 * @throws LockException
	 * @throws TechnicalLockException
	 * @throws IllegalArgumentException
	 * @throws ConfigurationException
	 * @throws NullPointerException
	 */
	public void prepare(String waitArtument, int waitMs, ClusterMonitor cm,
			ConfigurationHandler cfgHandler) throws LockException,
			TechnicalLockException, IllegalArgumentException,
			ConfigurationException, NullPointerException {

		if (ClusterUtils.areThereOthers(cm)) {
			return;
		}

		DCEnqueueLock.getInstance().lockAndWaitExclusiveNoncomulative(
				waitArtument, waitMs);
		try {
			if (ClusterUtils.areThereOthers(cm)) {
				return;
			}

			CfgUtils.createCfgTree(cfgHandler);
		} finally {
			DCEnqueueLock.getInstance().unlockExclusiveNoncomulative(
					waitArtument);
		}
	}
}