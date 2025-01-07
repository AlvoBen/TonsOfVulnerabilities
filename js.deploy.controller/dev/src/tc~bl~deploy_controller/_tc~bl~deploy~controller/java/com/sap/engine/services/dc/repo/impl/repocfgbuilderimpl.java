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
package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;

/**
 * Prepares the configuration for the initial work of the Deploy Controller.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class RepoCfgBuilderImpl implements CfgBuilder {

	private static RepoCfgBuilderImpl INSTANCE;

	private RepoCfgBuilderImpl() {
	}

	static synchronized CfgBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RepoCfgBuilderImpl();
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
		final TreeNode repo = new TreeNode(LocationConstants.REPO);
		repo.addLeaf(new TreeNode(LocationConstants.DC));
		repo.addLeaf(new TreeNode(LocationConstants.SC));

		final TreeNode storage = new TreeNode(LocationConstants.STORAGE);
		storage.addLeaf(new TreeNode(LocationConstants.DC));
		storage.addLeaf(new TreeNode(LocationConstants.SC));

		/*
		 * final TreeNode history = new TreeNode(LocationConstants.HISTORY);
		 * history.addLeaf(new TreeNode(LocationConstants.DC));
		 * history.addLeaf(new TreeNode(LocationConstants.SC));
		 */
		final TreeNode deployController = new TreeNode(
				LocationConstants.DEPLOY_CONTROLLER);
		deployController.addLeaf(repo);
		deployController.addLeaf(storage);
		// deployController.addLeaf(history);

		return deployController;
	}

}
