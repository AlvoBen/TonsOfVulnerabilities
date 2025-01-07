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
package com.sap.engine.services.dc.cm.storage.file_gc;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.util.Constants;

/**
 * Garbage collects the out of date files.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public abstract class FileStorageGC {

	private static FileStorageGC INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.storage.file_gc.impl.FileStorageGCImpl";

	protected FileStorageGC() {
	}

	public static synchronized FileStorageGC getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createInstance();
		}
		return INSTANCE;
	}

	private static FileStorageGC createInstance() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (FileStorageGC) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003177 An error occurred while creating an instance of "
					+ "class FileStorageGC! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * Garbage collects the out of date files.
	 * 
	 * @param uploadDirNameParent
	 *            <code>String</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws FileStorageGCException
	 *             in case the garbage collection fails
	 */
	public abstract void garbageCollect(String uploadDirNameParent,
			ConfigurationHandler cfgHandler) throws FileStorageGCException;

}
