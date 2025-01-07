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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.InvalidValueException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.NoWriteAccessException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.session_id.SessionIDFactory;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.utils.db.DBPoolSystemDataSourceBuilder;
import com.sap.engine.services.dc.cm.utils.db.SystemDataSourceBuildingException;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.migration.MigrationFactory;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;
import com.sap.engine.services.dc.util.structure.tree.TreeNode;
import com.sap.engine.services.dc.util.structure.tree.TreeNodeUtils;
import com.sap.tc.logging.Location;

/**
 * Extends the configuration functionality.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class CfgUtils {
	
	private static Location location = DCLog.getLocation(CfgUtils.class);

	public final static String _SER = ".ser";

	private CfgUtils() {
	}

	/**
	 * Creates the tree structures described <code>TreeNode</code>[] element, if
	 * they do not exist.
	 * 
	 * @param treeRoots
	 *            <code>TreeNode</code>[]
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if <code>Set</code> or <code>ConfigurationHandler</code> are
	 *             null.
	 */
	public static void createCfgTree(TreeNode treeRoots[],
			ConfigurationHandler cfgHandler) throws ConfigurationException,
			NullPointerException {
		final Set joinedTreeRoots = TreeNodeUtils.joinTreeRoots(treeRoots);
		final Iterator treeRootsIter = joinedTreeRoots.iterator();
		TreeNode root;
		while (treeRootsIter.hasNext()) {
			root = (TreeNode) treeRootsIter.next();
			try {
				Configuration rootCfg = createRootCfg(root.getName(),
						cfgHandler);
				createSubCfg(root, rootCfg);
				cfgHandler.commit();
			} catch (ConfigurationException ce) {
				cfgHandler.rollback();
				throw ce;
			} finally {
				cfgHandler.closeAllConfigurations();
			}
		}
	}

	/**
	 * Creates the DC tree structures, if is not already created.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if <code>Set</code> or <code>ConfigurationHandler</code> are
	 *             null.
	 */
	public static void createCfgTree(ConfigurationHandler cfgHanler)
			throws ConfigurationException, NullPointerException {
		final CfgBuilder repoCfgBuilder = RepositoryComponentsFactory
				.getInstance().getCfgBuilder();
		final CfgBuilder deplStorageCfgBuilder = DeploymentDataStorageFactory
				.getInstance().getCfgBuilder();
		final CfgBuilder undeplStorageCfgBuilder = UndeploymentDataStorageFactory
				.getInstance().getCfgBuilder();
		final CfgBuilder dbLockCfgBuilder = DCLockManagerFactory.getInstance()
				.getCfgBuilder();
		final CfgBuilder sessionIdCfgBuilder = SessionIDFactory.getInstance()
				.getCfgBuilder();
		final CfgBuilder migrationCfgBuilder = MigrationFactory.getInstance()
				.getCfgBuilder();

		final TreeNode treeRoots[] = new TreeNode[] { repoCfgBuilder.getTree(),
				deplStorageCfgBuilder.getTree(),
				undeplStorageCfgBuilder.getTree(), dbLockCfgBuilder.getTree(),
				sessionIdCfgBuilder.getTree(), migrationCfgBuilder.getTree() };
		createCfgTree(treeRoots, cfgHanler);

	}

	private static void createSubCfg(TreeNode node, Configuration cfg)
			throws ConfigurationException, NullPointerException {
		if (node.getParentPath() != null && !node.getParentPath().equals("")) {
			cfg = createSubCfg(node.getName(), cfg);
		}

		final Iterator leavesIter = node.getLeaves().values().iterator();
		TreeNode leaf = null;
		while (leavesIter.hasNext()) {
			leaf = (TreeNode) leavesIter.next();
			createSubCfg(leaf, cfg);
		}
		createEntries(node, cfg);
	}

	private static void createEntries(TreeNode node, Configuration cfg)
			throws NameAlreadyExistsException, NoWriteAccessException,
			InvalidValueException, ConfigurationException {
		Map entries = node.getEntries();
		if (entries == null || entries.size() == 0) {
			return;
		}
		Map.Entry nextEntry;
		String key;
		Object value;
		String[] entryNames = cfg.getAllConfigEntryNames();
		Arrays.sort(entryNames);
		for (Iterator iter = entries.entrySet().iterator(); iter.hasNext();) {
			nextEntry = (Map.Entry) iter.next();
			key = (String) nextEntry.getKey();
			if (Arrays.binarySearch(entryNames, key) < 0) {
				value = nextEntry.getValue();
				cfg.addConfigEntry(key, value);
			}
		}
	}

	/**
	 * Returns <code>Configuration</code> with name <code>String</code> and
	 * white access, which is sub configuration of the give one.
	 * 
	 * @param subName
	 *            <code>String</code>
	 * @param parentCfg
	 *            <code>Configuration</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if the given <code>String</code> or
	 *             <code>Configuration</code> are null.
	 */
	public static Configuration createSubCfg(String subName,
			Configuration parentCfg) throws ConfigurationException,
			NullPointerException {
		return createSubCfg(subName, parentCfg,
				Configuration.CONFIG_TYPE_STANDARD);
	}

	/**
	 * Returns <code>Configuration</code> with name <code>String</code> and
	 * white access, which is sub configuration of the give one with required
	 * type.
	 * 
	 * @param subName
	 *            <code>String</code>
	 * @param parentCfg
	 *            <code>Configuration</code>
	 * @param type
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if the given <code>String</code> or
	 *             <code>Configuration</code> are null.
	 */
	public static Configuration createSubCfg(String subName,
			Configuration parentCfg, int type) throws ConfigurationException,
			NullPointerException {
		if (!parentCfg.existsSubConfiguration(subName)) {
			return parentCfg.createSubConfiguration(subName, type);
		} else {
			return parentCfg.getSubConfiguration(subName);
		}
	}

	/**
	 * Returns <code>Configuration</code> with name <code>String</code> and
	 * white access, which is root configuration for the give
	 * <code>ConfigurationHandler</code>.
	 * 
	 * @param rootName
	 *            <code>String</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if the given <code>String</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 */
	public static Configuration createRootCfg(String rootName,
			ConfigurationHandler cfgHandler) throws ConfigurationException,
			NullPointerException {
		String allRoots[] = cfgHandler.getAllRootNames();
		if (allRoots != null) {
			Arrays.sort(allRoots);
		}
		if (Arrays.binarySearch(allRoots, rootName) < 0) {
			return cfgHandler.createRootConfiguration(rootName);
		} else {
			return openCfgWrite(cfgHandler, rootName);
		}
	}

	/**
	 * Returns opened <code>Configuration</code> for read access.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if the given <code>ConfigurationHandler</code> or
	 *             <code>String</code> are null.
	 */
	public static Configuration openCfgRead(ConfigurationHandler cfgHandler,
			String fullCfgPath) throws ConfigurationException,
			NullPointerException {
		return cfgHandler.openConfiguration(fullCfgPath,
				ConfigurationHandler.READ_ACCESS);
	}

	/**
	 * Returns opened <code>Configuration</code> for write access.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if the given <code>ConfigurationHandler</code> or
	 *             <code>String</code> are null.
	 */
	public static Configuration openCfgWrite(ConfigurationHandler cfgHandler,
			String fullCfgPath) throws ConfigurationException,
			NullPointerException {
		return cfgHandler.openConfiguration(fullCfgPath,
				ConfigurationHandler.WRITE_ACCESS);
	}

	/**
	 * Returns opened shared <code>Configuration</code> for write access.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 * @throws NullPointerException
	 *             if the given <code>ConfigurationHandler</code> or
	 *             <code>String</code> are null.
	 */
	public static Configuration openSharedCfgWrite(ConfigurationHandler cfgHandler,
			String fullCfgPath) throws ConfigurationException,
			NullPointerException {
		return cfgHandler.openConfiguration(fullCfgPath,
				ConfigurationHandler.WRITE_ACCESS, true);
	}

	
	/**
	 * Returns <code>Configuration</code> opened with write access.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 */
	public static Configuration openSubCfg(ConfigurationHandler cfgHandler,
			String fullCfgPath) throws ConfigurationException {
		try {
			return cfgHandler.createSubConfiguration(fullCfgPath);
		} catch (NameAlreadyExistsException e) {
			return CfgUtils.openCfgWrite(cfgHandler, fullCfgPath);
		}
	}

	/**
	 * Returns <code>Configuration</code> opened with write access. This
	 * <code>Configuration</code> is with type <code>int</code>
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @param type
	 *            <code>Configuration</code> type
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 */
	public static Configuration openSubCfg(ConfigurationHandler cfgHandler,
			String fullCfgPath, int type) throws ConfigurationException {
		try {
			return cfgHandler.createSubConfiguration(fullCfgPath, type);
		} catch (NameAlreadyExistsException e) {
			return CfgUtils.openCfgWrite(cfgHandler, fullCfgPath);
		}
	}

	/**
	 * Recreates <code>Configuration</code> opened with write access.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param fullCfgPath
	 *            <code>String</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 */
	public static Configuration recreateSubCfg(ConfigurationHandler cfgHandler,
			String fullCfgPath) throws ConfigurationException {
		try {
			return cfgHandler.createSubConfiguration(fullCfgPath);
		} catch (NameAlreadyExistsException e) {
			final Configuration cfg = openCfgWrite(cfgHandler, fullCfgPath);
			deleteAllCfgData(cfg);
			return cfg;
		}
	}

	/**
	 * Deletes all ConfigEntries, Files and SubConfigurations.
	 * 
	 * @param cfg
	 *            <code>Configuration</code>
	 * @return <code>Configuration</code>
	 * @throws ConfigurationException
	 *             if an error occurs.
	 */
	public static Configuration deleteAllCfgData(Configuration cfg)
			throws ConfigurationException {
		cfg.deleteAllConfigEntries();
		cfg.deleteAllFiles();
		cfg.deleteAllSubConfigurations();
		return cfg;
	}

	public static void setDBDate(Configuration sdaCfg, String dateLabel,
			String description) throws SystemDataSourceBuildingException,
			SQLException, ConfigurationException {
		Connection conn = null;
		try {
			conn = getDBConnection();
			final String dateString = DBUtils.getFormatedDateFromDB(conn,
					description);

			if (sdaCfg.existsConfigEntry(dateLabel)) {
				sdaCfg.modifyConfigEntry(dateLabel, dateString);
			} else {
				sdaCfg.addConfigEntry(dateLabel, dateString);
			}
		} finally {
			closeDBConnection(conn);
		}
	}

	public static void setDBDate(Configuration sdaCfg, String dateLabel,
			Connection conn, String description) throws SQLException,
			ConfigurationException {
		final String dateString = DBUtils.getFormatedDateFromDB(conn,
				description);

		if (sdaCfg.existsConfigEntry(dateLabel)) {
			sdaCfg.modifyConfigEntry(dateLabel, dateString);
		} else {
			sdaCfg.addConfigEntry(dateLabel, dateString);
		}
	}

	private static Connection getDBConnection() throws SQLException,
			SystemDataSourceBuildingException {
		return DBPoolSystemDataSourceBuilder.getInstance()
				.buildSystemDataSource().getConnection();
	}

	private static void closeDBConnection(Connection connection)
			throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public static String getCfgEntry(Configuration cfg, String entryName,
			String defaultValue) throws ConfigurationException {
		try {
			final String entryValue = (String) cfg.getConfigEntry(entryName);
			if (entryValue != null && !entryValue.trim().equals("")) {
				return entryValue;
			}

			return defaultValue;
		} catch (NameNotFoundException nnfe) {
			return defaultValue;
		}
	}

	public static void closeConfiguration(Configuration cfg)
			throws ConfigurationException {
		if (cfg != null) {
			cfg.close();
		}
	}


	public static void createSerializedObject(Object object, Configuration cfg)
			throws ConfigurationException, Exception {
		ByteArrayInputStream baiStream = null;
		try {
			baiStream = new ByteArrayInputStream(ObjectSerializer
					.getByteArray(object));
			cfg.updateFileAsStream(_SER, baiStream, true);
		} finally {
			close(baiStream);
		}
	}

	public static Object getDeserializedObject(Configuration cfg)
			throws ConfigurationException, Exception {
		if (cfg == null || !cfg.existsFile(_SER)) {
			return null;
		}

		InputStream iStream = null;
		try {
			iStream = cfg.getFile(_SER);
			return ObjectSerializer.getObject(iStream);
		} finally {
			close(iStream);
		}
	}

	private static void close(InputStream iStream) {
		try {
			if (iStream != null) {
				iStream.close();
			}
		} catch (IOException ioe) {// $JL-EXC$
			DCLog.logInfo(location, "ASJ.dpl_dc.006314",
					"Problem occurred while closing stream: [{0}]",
					new Object[] { ioe });
		}
	}
}
