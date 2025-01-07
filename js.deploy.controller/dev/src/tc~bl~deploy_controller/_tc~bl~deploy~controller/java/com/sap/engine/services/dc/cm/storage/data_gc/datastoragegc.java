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
package com.sap.engine.services.dc.cm.storage.data_gc;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;

/**
 * Garbage collects the out of date configurations, which are wrapped from
 * com.sap.engine.services.dc.util.ConfigurationWrapper
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class DataStorageGC {

	private static DataStorageGC INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.storage.data_gc.impl.DataStorageGCImpl";

	protected DataStorageGC() {
	}

	public static synchronized DataStorageGC getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createInstance();
		}
		return INSTANCE;
	}

	private static DataStorageGC createInstance() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (DataStorageGC) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003176 An error occurred while creating an instance of "
					+ "class DataStorageGC! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Garbage collects the out of date configurations, which are wrapped from
	 * com.sap.engine.services.dc.util.ConfigurationWrapper
	 * 
	 * @param rootTrees
	 *            <code>TreeNode</code>[]
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws DataStorageGCException
	 *             in case the garbage collection fails
	 */
	public abstract void garbageCollect(TreeNode rootTrees[],
			ConfigurationHandler cfgHandler) throws DataStorageGCException;

}
