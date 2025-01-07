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

package com.sap.engine.services.deploy.server.utils.cfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationState;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatistic;
import com.sap.engine.services.deploy.container.migration.utils.CMigrationStatus;
import com.sap.engine.services.deploy.container.migration.utils.CMigratorResult;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.tc.logging.Location;

/**
 * This class provides utility methods for handling of the migration statistic
 * configuration.
 * 
 * @author Todor Stoitsev
 */
public class MigrationConfigUtils {
	
	private static final Location location = 
		Location.getLocation(MigrationConfigUtils.class);

	private static final String CFG_ONLINE_MIGRATION = "online_migration";
	private static final String SEPARATOR = "/";

	// constant sub configuration names for migration statistic configuration
	private static final String CFG_MIGRATOR_NAMES_SUB_CFG = "cMigNames";
	private static final String CFG_CURRENT_NAME_SUB_CFG = "active";

	// entries for migration statistic configuration
	private static final String CFG_CURRENT_IDX_ENTRY = "index";
	private static final String CFG_DESC_ENTRY = "description";
	private static final String CFG_STATUS_ENTRY = "status";
	private static final String CFG_MIGRATION_STATE_ENTRY = "cMigState";
	private static final String CFG_DURATION_ENTRY = "duration";

	// online migration configuration path
	private static final String ONLINE_MIG_CFG_PATH = DeployConstants.ROOT_CFG_DEPLOY_SERVICE
			+ SEPARATOR + CFG_ONLINE_MIGRATION;

	private static final int NUM_OF_BACK_STORE = 2;

	/**
	 * Retrieves the online migration configuration in read access mode.
	 * 
	 * @param handler
	 * @return - the configuration of the online migration in read access mode
	 * @throws ConfigurationException
	 */
	private static Configuration getOrCreateOnlineMigrationConfigInReadMode(
			ConfigurationHandler handler) throws ConfigurationException {
		// check required config structure and create it if missing
		Configuration deployServiceConfig = null;
		Configuration onlineMigConfigRead = null;
		Configuration onlineMigConfigWrite = null;
		try {
			onlineMigConfigRead = handler.openConfiguration(
					ONLINE_MIG_CFG_PATH, ConfigurationHandler.READ_ACCESS);
		} catch (NameNotFoundException ce) {
			try {
				onlineMigConfigWrite = handler
						.createSubConfiguration(ONLINE_MIG_CFG_PATH);
			} catch (NameNotFoundException nnfe) {
				deployServiceConfig = handler
						.createSubConfiguration(DeployConstants.ROOT_CFG_DEPLOY_SERVICE);
				onlineMigConfigWrite = deployServiceConfig
						.createSubConfiguration(CFG_ONLINE_MIGRATION);
			}
		}
		// if write access created
		if (onlineMigConfigWrite != null) {
			// create the required configuration nodes
			handler.commit();
			if (deployServiceConfig != null)
				handler.closeConfiguration(deployServiceConfig);
			else
				handler.closeConfiguration(onlineMigConfigWrite);
			// open th configuration in read access
			onlineMigConfigRead = handler.openConfiguration(
					ONLINE_MIG_CFG_PATH, ConfigurationHandler.READ_ACCESS);
		}
		return onlineMigConfigRead;
	}

	/**
	 * Store the given migration statistics in the DB.
	 * 
	 * @param cMigrationStatistic
	 * @param handler
	 * @throws DeploymentException
	 */
	public static void storeMigrationStatistic(
			CMigrationStatistic cMigrationStatistic,
			ConfigurationHandler handler, String prefix)
			throws DeploymentException {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"[{0}] will persist the container migration statistic in DB.",
								prefix);
			}
			// get the online migration configuration in read access mode
			Configuration onlineMigConfig = getOrCreateOnlineMigrationConfigInReadMode(handler);
			// retrieve the information about the active persistence location
			// up to <NUM_OF_BACK_STORE> steps back are stored for debugging
			// purposes
			Object[] activeIdxInfo = getOrCreateActiveIndex(handler,
					onlineMigConfig);
			int niCurrIdx = ((Integer) activeIdxInfo[0]).intValue();
			boolean bActiveIdxNodeCreated = ((Boolean) activeIdxInfo[1])
					.booleanValue();
			Configuration currentIdxConfig = (Configuration) activeIdxInfo[2];
			// maximum reached
			if (niCurrIdx == NUM_OF_BACK_STORE - 1) {
				niCurrIdx = 0;
			}
			// if not initially created increment and overwrite properties
			else if (!bActiveIdxNodeCreated) {
				niCurrIdx++;
			}
			// create the statistics configuration
			Configuration idxNodeConfiguration = storeStatisticConfigInIdxNode(
					cMigrationStatistic, handler, onlineMigConfig, niCurrIdx);
			// update the active index
			if (!bActiveIdxNodeCreated) {
				updateCurrentIdxConfig(currentIdxConfig, niCurrIdx);
			}
			// commit the changes
			handler.commit();
		} catch (ConfigurationException e) {
			rollbackHandler(handler, e, prefix);
		} catch (IOException e) {
			rollbackHandler(handler, e, prefix);
		} finally {
			ConfigUtils.closeAllConfigurations(handler, prefix);
		}
	}

	private static void rollbackHandler(ConfigurationHandler handler,
			Throwable th, String prefix) throws DeploymentException {
		final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "persisting container migration statistics in ["
						+ prefix + "]" }, th);
		sde.setMessageID("ASJ.dpl_ds.005082");
		DSLog.logErrorThrowable(location, sde);
		ConfigUtils.rollbackHandler(handler, prefix);
		throw sde;
	}

	/**
	 * Opens the current active index configuration in the DB in write access.
	 * The configuration is created if missing.
	 * 
	 * @param handler
	 * @param base
	 * @return
	 * @throws ConfigurationException
	 */
	private static Object[] getOrCreateActiveIndex(
			ConfigurationHandler handler, Configuration parent)
			throws ConfigurationException {
		int niRetIdx = -1;
		boolean bIdxNodeCreated = false;
		Configuration currentIdxConfig = null;
		// parent is opened in read access mode;
		if (parent.existsSubConfiguration(CFG_CURRENT_NAME_SUB_CFG)) {
			currentIdxConfig = handler.openConfiguration(parent.getPath()
					+ SEPARATOR + CFG_CURRENT_NAME_SUB_CFG,
					ConfigurationHandler.WRITE_ACCESS);
			niRetIdx = ((Integer) currentIdxConfig
					.getConfigEntry(CFG_CURRENT_IDX_ENTRY)).intValue();
		}
		// no configuration for the current index available
		// so create one and set current index to '0'
		else {
			currentIdxConfig = handler.createSubConfiguration(parent.getPath()
					+ SEPARATOR + CFG_CURRENT_NAME_SUB_CFG);
			currentIdxConfig.addConfigEntry(CFG_CURRENT_IDX_ENTRY, new Integer(
					0));
			bIdxNodeCreated = true;
			niRetIdx = 0;
		}
		return new Object[] { new Integer(niRetIdx),
				new Boolean(bIdxNodeCreated), currentIdxConfig };
	}

	/**
	 * Retrieves the current active index entry value from DB.
	 * 
	 * @param base
	 * @return
	 * @throws ConfigurationException
	 */
	private static int getActiveIndex(Configuration base)
			throws ConfigurationException {
		int niRetIdx = -1;
		boolean bIdxNodeCreated = false;
		Configuration currentIdxConfig = null;
		if (base.existsSubConfiguration(CFG_CURRENT_NAME_SUB_CFG)) {
			currentIdxConfig = base
					.getSubConfiguration(CFG_CURRENT_NAME_SUB_CFG);
			niRetIdx = ((Integer) currentIdxConfig
					.getConfigEntry(CFG_CURRENT_IDX_ENTRY)).intValue();
		}
		return niRetIdx;
	}

	/**
	 * Updates the current active index in DB.
	 * 
	 * @param currentIdxConfig
	 * @param index
	 * @throws ConfigurationException
	 */
	private static void updateCurrentIdxConfig(Configuration currentIdxConfig,
			int index) throws ConfigurationException {
		// precauton
		if (currentIdxConfig == null) {
			System.out.println("Error current index config is <null>!");// $JL-
																		// SYS_OUT_ERR$
			return;
		}
		currentIdxConfig.modifyConfigEntry(CFG_CURRENT_IDX_ENTRY, new Integer(
				index));
	}

	/**
	 * Create the statistics configuration.
	 * 
	 * @param base
	 * @param niIdx
	 * @throws ConfigurationException
	 *             , IOException
	 */
	private static Configuration storeStatisticConfigInIdxNode(
			CMigrationStatistic cMigrationStatistic,
			ConfigurationHandler handler, Configuration base, int niIdx)
			throws ConfigurationException, IOException {
		String sIdxNodeName = String.valueOf(niIdx);
		Configuration idxNodeConfig = null;
		// parent is opened in read access mode;
		// check if such config node already exists and overwrite it if so
		if (base.existsSubConfiguration(sIdxNodeName)) {
			idxNodeConfig = handler.openConfiguration(base.getPath()
					+ SEPARATOR + sIdxNodeName,
					ConfigurationHandler.WRITE_ACCESS);
			idxNodeConfig.deleteAllConfigEntries();
			idxNodeConfig.deleteAllSubConfigurations();
			storeIndexNodeSubTree(cMigrationStatistic, idxNodeConfig);
		}
		// no such configuration exists - create new one
		else {
			idxNodeConfig = handler.createSubConfiguration(base + SEPARATOR
					+ String.valueOf(niIdx));
			storeIndexNodeSubTree(cMigrationStatistic, idxNodeConfig);
		}
		return idxNodeConfig;
	}

	/**
	 * Creates the configuration sub tree of the index node for history storage.
	 * The content created in the method represents the pure migration
	 * statistics data.
	 * 
	 * @param config
	 * @throws ConfigurationException
	 *             , IOException
	 */
	private static void storeIndexNodeSubTree(
			CMigrationStatistic cMigrationStatistic, Configuration config)
			throws ConfigurationException, IOException {
		config.addConfigEntry(CFG_DURATION_ENTRY, cMigrationStatistic
				.getDuration());
		config.addConfigEntry(CFG_MIGRATION_STATE_ENTRY, cMigrationStatistic
				.getCMigrationState().getName());
		CMigratorResult[] cMigratorResults = cMigrationStatistic
				.getCMigratorResults();
		for (int i = 0; i < cMigratorResults.length; i++) {
			CMigratorResult cMigratorResult = cMigratorResults[i];
			// container migrator name
			String sCMigratorName = cMigratorResult.getCMigratorName();

			CMigrationStatus[] cMigrationStatuses = cMigratorResult
					.getCMigrationStatuses();
			for (int j = 0; j < cMigrationStatuses.length; j++) {
				CMigrationStatus cMigrationStatus = cMigrationStatuses[j];
				String sAppName = cMigrationStatus.getAppName();
				ApplicationName appName = new ApplicationName(sAppName);
				// provider name
				String sProvider = appName.getProvider();
				Configuration config2 = null;
				// crate provider node if it does not exist
				if (!config.existsSubConfiguration(sProvider)) {
					config2 = config.createSubConfiguration(sProvider);
				} else {
					config2 = config.getSubConfiguration(sProvider);
				}

				// application name
				sAppName = appName.getName();
				if (!config2.existsSubConfiguration(sAppName)) {
					config2 = config2.createSubConfiguration(sAppName);
				} else {
					config2 = config2.getSubConfiguration(sAppName);
				}
				if (!config2.existsConfigEntry(CFG_STATUS_ENTRY)) {
					config2.addConfigEntry(CFG_STATUS_ENTRY,
							getHumanReadableMigrationStatus(cMigrationStatus));
				}
				if (!config2.existsFile(CFG_DESC_ENTRY)) {
					createDescriptionEntry(config2, cMigrationStatus);
				}
				// add a migrator sub configuration per application
				Configuration config1 = null;
				if (!config2.existsSubConfiguration(CFG_MIGRATOR_NAMES_SUB_CFG)) {
					config1 = config2
							.createSubConfiguration(CFG_MIGRATOR_NAMES_SUB_CFG);
				} else {
					config1 = config2
							.getSubConfiguration(CFG_MIGRATOR_NAMES_SUB_CFG);
				}
				// migrator name is unique, it should not exist
				config1 = config1.createSubConfiguration(sCMigratorName);
			}
		}
	}

	/**
	 * Creates the descritption entry in the specified configuration.
	 * Description is stored in a file.
	 * 
	 * @param config
	 * @param cMigStatus
	 * @throws ConfigurationException
	 *             , IOException
	 */
	private static void createDescriptionEntry(Configuration config,
			CMigrationStatus cMigStatus) throws ConfigurationException,
			IOException {
		String sDescription = cMigStatus.getDescription();

		ByteArrayInputStream defBytes = new ByteArrayInputStream(
				serializeString(sDescription));
		if (config.existsFile(CFG_DESC_ENTRY)) {
			config.updateFileAsStream(CFG_DESC_ENTRY, defBytes);
		} else {
			config.addFileAsStream(CFG_DESC_ENTRY, defBytes);
		}
	}

	/**
	 * Serializes a string and writes it to a DB file entry.
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	private static byte[] serializeString(String s) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(s.length());
		ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
		objOut.writeObject(s);
		objOut.flush();
		byte[] result = byteOut.toByteArray();
		objOut.close();
		byteOut.close();
		return result;
	}

	/**
	 * Reads a serialized string from file entry.
	 * 
	 * @param entryName
	 * @param config
	 * @return
	 * @throws ConfigurationException
	 */
	private static String readStringFromFileEntry(String entryName,
			Configuration config) throws ConfigurationException {
		try {
			InputStream in = config.getFile(entryName);
			ObjectInputStream objIn = new ObjectInputStream(in);
			String result = (String) objIn.readObject();
			objIn.close();
			return result;
		} catch (ConfigurationException ce) {
			throw ce;
		} catch (Exception e) {
			throw new ConfigurationException(e,
					ConfigurationException.UNDEFINED_ERROR,
					new Object[] { "Configuration: " + config.getPath()
							+ ", PropertyEntry: " + entryName });
		}
	}

	/**
	 * Retrieves a human readable status from a given <code>byte</code>
	 * constant.
	 * 
	 * @param cMigStatus
	 * @return
	 */
	public static String getHumanReadableMigrationStatus(
			CMigrationStatus cMigStatus) {
		switch (cMigStatus.getStatus()) {
		case CMigrationStatus.FAILED:
			return "FAILED";
		case CMigrationStatus.PASSED:
			return "PASSED";
		default:
			return "UNKNOWN";
		}
	}

	/**
	 * Retrieves a migration statu <code>byte</code> value form a given stored
	 * string.
	 * 
	 * @param sHumanReadableStatus
	 * @return
	 */
	private static byte getMigrationStatus(String sHumanReadableStatus) {
		if (sHumanReadableStatus == null)
			return CMigrationStatus.UNKNOWN;

		if (sHumanReadableStatus.equals("FAILED"))
			return CMigrationStatus.FAILED;
		if (sHumanReadableStatus.equals("PASSED"))
			return CMigrationStatus.PASSED;

		return CMigrationStatus.UNKNOWN;
	}

	/**
	 * Reads the complete migration statistic from the active index
	 * configuration.
	 * 
	 * @param handler
	 * @return
	 * @throws ConfigurationException
	 */
	public static CMigrationStatistic readCMigrationStatistic(
			ConfigurationHandler handler) throws ConfigurationException {
		// open base configuration node - "deploy_service/offline_migration"
		Configuration base = null;
		try {
			base = handler.openConfiguration(ONLINE_MIG_CFG_PATH,
					ConfigurationHandler.READ_ACCESS);
		} catch (NameNotFoundException e) {
			// no migration was performed
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"MigrationConfigUtils#readCMigrationStatistic: No configuration [{0}] exists!",
								ONLINE_MIG_CFG_PATH);
			}
			return null;
		}
		// get the active index value from the active sub configuration node
		int niCurrIdx = getActiveIndex(base);
		if (niCurrIdx == -1) {
			// eventually throw exceptions here
			DSLog.traceError(
							location,
							"ASJ.dpl_ds.002007",
							"MigrationConfigUtils#readCMigrationStatistic: Error - No current active index specified!");
			// close the connection the opened configurations
			handler.closeConfiguration(base);
			return null;
		}
		Configuration currentIdxConfig = null;
		String sIdxNodeName = String.valueOf(niCurrIdx);
		// check if a sub configuration node with name = current index exists
		// to get the actual migration configuration from there
		if (base.existsSubConfiguration(sIdxNodeName)) {
			currentIdxConfig = base.getSubConfiguration(sIdxNodeName);
		}
		// no such configuration exists - no further read possible
		else {
			// eventually throw exceptions here
			DSLog.traceError(
							location,
							"ASJ.dpl_ds.002008",
							"MigrationConfigUtils#readCMigrationStatistic: Error - No current active index configuration node found in parent!");
			// close the connection the opened configurations
			handler.closeConfiguration(base);
			return null;
		}
		// read the statistics configuration
		CMigrationStatistic cMigrationStatistic = readIndexNodeSubTree(currentIdxConfig);
		// close the connection the opened configurations
		handler.closeConfiguration(base);
		return cMigrationStatistic;
	}

	/**
	 * Reads the active index node sub tree and retrieves the statistic stored
	 * there.
	 * 
	 * @param config
	 * @return
	 * @throws ConfigurationException
	 */
	private static CMigrationStatistic readIndexNodeSubTree(Configuration config)
			throws ConfigurationException {
		Hashtable htCMigratorResults = new Hashtable();

		// duration
		Long duration = (Long) config.getConfigEntry(CFG_DURATION_ENTRY);
		// migration state
		String sMigrationStateName = (String) config
				.getConfigEntry(CFG_MIGRATION_STATE_ENTRY);
		CMigrationState cMigrationState = CMigrationState
				.getStatusByName(sMigrationStateName);
		// providers at this level
		String[] sProviderNames = config.getAllSubConfigurationNames();
		for (int i = 0; i < sProviderNames.length; i++) {
			Configuration providerConfig = config
					.getSubConfiguration(sProviderNames[i]);

			String[] sAppNames = providerConfig.getAllSubConfigurationNames();
			for (int j = 0; j < sAppNames.length; j++) {
				Configuration appConfig = providerConfig
						.getSubConfiguration(sAppNames[j]);
				String sFullApplicationName = sProviderNames[i] + "/"
						+ sAppNames[j];
				String sMigrationStatus = (String) appConfig
						.getConfigEntry(CFG_STATUS_ENTRY);
				byte btStat = getMigrationStatus(sMigrationStatus);
				String sDescription = readStringFromFileEntry(CFG_DESC_ENTRY,
						appConfig);
				// store this one for migrator result
				CMigrationStatus cMigrationStatus = new CMigrationStatus(
						sFullApplicationName, btStat, sDescription);

				Configuration cMigratorNames = appConfig
						.getSubConfiguration(CFG_MIGRATOR_NAMES_SUB_CFG);

				String[] sMigratorNames = cMigratorNames
						.getAllSubConfigurationNames();
				// migrator result migrator names
				for (int k = 0; k < sMigratorNames.length; k++) {
					String sMigratorName = sMigratorNames[k];
					Object oMigratorData = htCMigratorResults
							.get(sMigratorName);
					// migrator not handled yet
					if (oMigratorData == null) {
						Vector vCMigrationStatuses = new Vector();
						vCMigrationStatuses.add(cMigrationStatus);
						htCMigratorResults.put(sMigratorName,
								vCMigrationStatuses);
					} else {
						Vector vCMigrationStatuses = (Vector) oMigratorData;
						vCMigrationStatuses.add(cMigrationStatus);
					}
				}// end for migrator names per application
			}// end for application names
		}// end provider names

		Iterator keySetIter = htCMigratorResults.keySet().iterator();

		int count = 0;
		CMigratorResult[] cMigratorResults = new CMigratorResult[htCMigratorResults
				.size()];
		while (keySetIter.hasNext()) {
			String sMigratorName = (String) keySetIter.next();
			Vector vCMigrationStatuses = (Vector) htCMigratorResults
					.get(sMigratorName);
			CMigrationStatus[] cMigrationStatuses = new CMigrationStatus[vCMigrationStatuses
					.size()];
			for (int i = 0; i < cMigrationStatuses.length; i++) {
				cMigrationStatuses[i] = (CMigrationStatus) vCMigrationStatuses
						.elementAt(i);
			}
			cMigratorResults[count] = new CMigratorResult(sMigratorName,
					cMigrationStatuses);
			count++;
		}

		return new CMigrationStatistic(cMigrationState, cMigratorResults,
				duration);
	}

	/**
	 * Reads the application migration status.
	 * 
	 * @param sFullAppName
	 * @param handler
	 * @return
	 * @throws ConfigurationException
	 */
	public static CMigrationStatus readAppMigrationStatus(String sFullAppName,
			ConfigurationHandler handler) throws ConfigurationException {
		CMigrationStatus cMigrationStatus = null;
		// open base configuration node - "deploy_service/offline_migration"
		Configuration base = null;
		try {
			base = handler.openConfiguration(ONLINE_MIG_CFG_PATH,
					ConfigurationHandler.READ_ACCESS);
		} catch (NameNotFoundException e) {
			// no migration was performed
			// DSLog.traceDebug(
			// "MigrationConfigUtils#readAppMigrationStatus: No configuration \'"
			// , ONLINE_MIG_CFG_PATH, "\' exists!");
			return null;
		}
		// get the active index value from the active sub configuration node
		int niCurrIdx = getActiveIndex(base);
		if (niCurrIdx == -1) {
			// eventually throw exceptions here
			DSLog.traceError(
							location, 
							"ASJ.dpl_ds.002009",
							"MigrationConfigUtils#readAppMigrationStatus: Error - No current active index specified while checking [{0}]!",
							sFullAppName);
			// close the connection the opened configurations
			handler.closeConfiguration(base);
			return null;
		}
		Configuration currentIdxNodeConfig = null;
		// check if a sub configuration node with name = current index exists
		// to get the actual migration configuration from there
		String sIdxNodeName = String.valueOf(niCurrIdx);
		if (base.existsSubConfiguration(sIdxNodeName)) {
			currentIdxNodeConfig = base.getSubConfiguration(sIdxNodeName);
		}
		// no such configuration exists - no further read possible
		else {
			// eventually throw exceptions here
			DSLog.traceError(
							location,
							"ASJ.dpl_ds.002010",
							"MigrationConfigUtils#readAppMigrationStatus: Error - No current active index node configuration found in parent while checking [{0}!",
							sFullAppName);
			// close the connection the opened configurations
			handler.closeConfiguration(base);
			return null;
		}

		// get the application migration data
		ApplicationName appName = new ApplicationName(sFullAppName);
		String sProviderName = appName.getProvider();
		String sAppName = appName.getName();
		// providers at this level
		if (currentIdxNodeConfig.existsSubConfiguration(sProviderName)) {
			Configuration providerConfig = currentIdxNodeConfig
					.getSubConfiguration(sProviderName);
			if (providerConfig.existsSubConfiguration(sAppName)) {
				Configuration appConfig = providerConfig
						.getSubConfiguration(sAppName);
				String sMigrationStatus = (String) appConfig
						.getConfigEntry(CFG_STATUS_ENTRY);
				byte btStat = getMigrationStatus(sMigrationStatus);
				String sDescription = readStringFromFileEntry(CFG_DESC_ENTRY,
						appConfig);
				// store this one for migrator result
				cMigrationStatus = new CMigrationStatus(sFullAppName, btStat,
						sDescription);
			} else {
				// no exceptions here because application may in deed not exist
				// DSLog.traceDebug(
				// "MigrationConfigUtils#readAppMigrationStatus: Debug - No application node found while checking \'"
				// , sFullAppName, "\'!");
			}
		}
		// close the connection the opened configurations
		handler.closeConfiguration(base);
		return cMigrationStatus;
	}
}
