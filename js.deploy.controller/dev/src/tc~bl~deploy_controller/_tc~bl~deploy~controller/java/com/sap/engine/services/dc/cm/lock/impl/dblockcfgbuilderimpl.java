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

import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;

/**
 * Prepares the configuration for the initial work of the Deploy Controller.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class DBLockCfgBuilderImpl implements CfgBuilder {

	private static DBLockCfgBuilderImpl INSTANCE;

	private DBLockCfgBuilderImpl() {
	}

	static synchronized CfgBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DBLockCfgBuilderImpl();
		}

		return INSTANCE;
	}

	/**
	 * Gets tree representation of the configuration structure needed for the
	 * repository.
	 * 
	 * @return <code>TreeNode<code>
	 */
	public TreeNode getTree() {
		final TreeNode lock = new TreeNode(LockLocationConstants.LOCK);

		final TreeNode deployController = new TreeNode(
				LocationConstants.DEPLOY_CONTROLLER);
		deployController.addLeaf(lock);

		return deployController;
	}

}
