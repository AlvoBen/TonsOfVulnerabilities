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
package com.sap.engine.services.dc.util;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.dc.repo.SduFileStorageLocation;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;

/**
 * Provides object validation utils.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class ValidatorUtils {

	private ValidatorUtils() {
	}

	/**
	 * Throws <code>NullPointerException</code> if the given <code>Object</code>
	 * is null. The given <code>String</code> will be included into exception
	 * message.
	 * 
	 * @param obj
	 *            <code>Object</code>
	 * @param objType
	 *            <code>String</code>
	 * @throws NullPointerException
	 *             if the given <code>Object</code> is null.
	 */
	public static void validateNull(Object obj, String objType)
			throws NullPointerException {
		if (obj == null) {
			throw new NullPointerException("ASJ.dpl_dc.003401 The "
					+ objType + " is NULL.");
		}
	}

	// TEMPLATE
	// public static void validate()
	// throws NullPointerException {
	// validateNull();
	// }

	public static void validate(ShellInterface shell)
			throws NullPointerException {
		validateNull(shell, "com.sap.engine.interfaces.shell.ShellInterface");
	}

	public static void validate(Object obj) throws NullPointerException {
		validateNull(obj, "Object");
	}

	public static void validate(File file) throws NullPointerException {
		validateNull(file, "File");
	}

	public static void validate(File[] files) throws NullPointerException {
		validateNull(files, "File[]");
	}

	public static void validate(TreeNode tNode) throws NullPointerException {
		validateNull(tNode,
				"com.sap.engine.services.dc.util.structure.tree.TreeNode");
	}

	public static void validate(ApplicationServiceContext asc)
			throws NullPointerException {
		validateNull(asc, "com.sap.engine.frame.ApplicationServiceContext");
	}

	public static void validate(ConfigurationHandler cfgHandler)
			throws NullPointerException {
		validateNull(cfgHandler,
				"com.sap.engine.frame.core.configuration.ConfigurationHandler");
	}

	public static void validate(Configuration cfg) throws NullPointerException {
		validateNull(cfg,
				"com.sap.engine.frame.core.configuration.Configuration");
	}

	public static void validate(String string) throws NullPointerException {
		validateNull(string, "String");
	}

	public static void validate(String[] srtArr) throws NullPointerException {
		validateNull(srtArr, "String[]");
	}

	public static void validate(SduFileStorageLocation sduFileStorageLocation)
			throws NullPointerException {
		validateNull(sduFileStorageLocation,
				"com.sap.engine.services.dc.repo.SduFileStorageLocation");
	}

	public static void validate(SduRepoLocation sduRepoLocation)
			throws NullPointerException {
		validateNull(sduRepoLocation,
				"com.sap.engine.services.dc.repo.SduRepoLocation");
	}

	public static void validate(Properties props) throws NullPointerException {
		validateNull(props, "Properties");
	}

	public static void validate(PropertySheet pSheet)
			throws NullPointerException {
		validateNull(pSheet, "PropertySheet");
	}

	public static void validate(InputStream is) throws NullPointerException {
		validateNull(is, "InputStream");
	}

	public static void validate(Connection conn) throws NullPointerException {
		validateNull(conn, "java.sql.Connection");
	}
}
