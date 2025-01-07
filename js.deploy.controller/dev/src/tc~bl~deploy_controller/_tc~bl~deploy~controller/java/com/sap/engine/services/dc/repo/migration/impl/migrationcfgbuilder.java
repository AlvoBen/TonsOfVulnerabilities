package com.sap.engine.services.dc.repo.migration.impl;

import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-2
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class MigrationCfgBuilder implements CfgBuilder {

	private static MigrationCfgBuilder INSTANCE;

	private MigrationCfgBuilder() {
	}

	static synchronized CfgBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MigrationCfgBuilder();
		}

		return INSTANCE;
	}

	/**
	 * Gets tree representation of the configuration structure needed for the
	 * migration.
	 * 
	 * @return <code>TreeNode<code>
	 */
	public TreeNode getTree() {
		final TreeNode migration = new TreeNode(LocationConstants.MIGRATION);

		final TreeNode deployController = new TreeNode(
				LocationConstants.DEPLOY_CONTROLLER);
		deployController.addLeaf(migration);

		return deployController;
	}

}
