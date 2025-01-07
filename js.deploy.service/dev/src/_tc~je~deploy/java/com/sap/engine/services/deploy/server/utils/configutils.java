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

package com.sap.engine.services.deploy.server.utils;

import java.io.File;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.DerivedConfiguration;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.frame.core.configuration.admin.ConfigurationHandlerExtension;
import com.sap.engine.lib.io.hash.HashUtils;
import com.sap.engine.lib.io.hash.Index;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.tc.logging.Location;

/**
 * Utils class for configuration operations.
 * 
 * @author Anton Georgiev
 * @version 7.00
 */
public class ConfigUtils {
	
	private static final Location location = 
		Location.getLocation(ConfigUtils.class);

	public static ConfigurationHandler getConfigurationHandler(
		ConfigurationHandlerFactory factory, String action)
		throws ServerDeploymentException {
		if (factory == null) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_AVAILABLE_CONFIG_MANAGER_ON_PRINCIPLE,
				action);
			sde.setMessageID("ASJ.dpl_ds.005057");
			throw sde;
		}
		try {
			return factory.getConfigurationHandler();
		} catch (ConfigurationException ce) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_GET_HANDLER_ON_PRINCIPLE,
				new String[] { action }, ce);
			sde.setMessageID("ASJ.dpl_ds.005056");
			throw sde;
		}
	}

	public static ConfigurationHandler rollbackHandler(
			ConfigurationHandler handler, String action)
			throws ServerDeploymentException {
		if (handler == null) {
			return null;
		}
		try {
			handler.rollback();
		} catch (ConfigurationException ce) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "rollback of configuration handler in "
							+ action }, ce);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		} finally {
			closeAllConfigurations(handler, action);
		}
		return null;
	}

	public static Configuration createRootConfiguration(
			ConfigurationHandler handler, String configName, String action)
			throws ServerDeploymentException {
		Configuration config = null;
		try {
			config = handler.createRootConfiguration(configName);
		} catch (ConfigurationException ce1) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_CREATE_CONFIGURATION,
					new String[] { configName + " in " + action + ".\nReason: "
							+ ce1.toString() }, ce1);
			sde.setMessageID("ASJ.dpl_ds.005058");
			sde.setDcNameForObjectCaller(handler);
			throw sde;
		}
		return config;
	}

	public static ConfigurationHandler commitHandler(
		ConfigurationHandler handler, String action)
		throws ServerDeploymentException {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(location, "Will commit [{0}] in [{1}].", 
					handler, action);
			}
			handler.commit();
		} catch (ConfigurationException cex) {
			
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_COMMIT_HANDLER_ON_PRINCIPLE,
				new String[] { action + ".\nReason: " + cex.toString() },
				cex);
			sde.setMessageID("ASJ.dpl_ds.005060");
			sde.setDcNameForObjectCaller(handler);
			throw sde;
		} finally {
			closeAllConfigurations(handler, action);
		}
		return null;
	}

	public static void closeAllConfigurations(ConfigurationHandler handler,
		String action) throws ServerDeploymentException {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
					"Will close all configurations from [{0}] in [{1}].",
					handler, action);
			}
			handler.closeAllConfigurations();
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { action + ".\nReason: " + cex.toString() },
				cex);
			sde.setMessageID("ASJ.dpl_ds.005082");
			sde.setDcNameForObjectCaller(handler);
			throw sde;
		}
	}

	public static void closeConfiguration(ConfigurationHandler handler,
		Configuration cfg, String action) throws ServerDeploymentException {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
					"Will close [{0}] configuration from [{1}] in [{2}].",
					cfg.getPath(), handler, action);
			}
			handler.closeConfiguration(cfg);
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { action + ".\nReason: " + cex.toString() },
				cex);
			sde.setMessageID("ASJ.dpl_ds.005082");
			sde.setDcNameForObjectCaller(handler);
			throw sde;
		}
	}

	public static Configuration openConfiguration(ConfigurationHandler handler,
			String configName, int access, String action)
			throws ServerDeploymentException {
		Configuration config = null;
		final String accessAsString = convertAccess(access);
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
					"Will open [{0}] configuration with [{1}] access from [{2}] in [{3}].",
					configName, accessAsString, handler, action);
			}
			config = handler.openConfiguration(configName, access);
		} catch (ConfigurationException ce) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION_ON_PRINCIPLE,
				new String[] { configName + " in " + action, accessAsString },
				ce);
			sde.setMessageID("ASJ.dpl_ds.005062");
			throw sde;
		}
		return config;
	}

	public static Configuration openPossibleDerivedConfiguration(
			ConfigurationHandler handler, String configName, int access,
			String action) throws ServerDeploymentException {
		Configuration config = null;
		final String accessAsString = convertAccess(access);
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
					"Will open [{0}] possible derived configuration with [{1}] access from [{2}] in [{3}].",
					configName, accessAsString, handler, action);
			}
			config = ((ConfigurationHandlerExtension) handler)
				.openPossibleDerivedConfigurationExtension(configName,
							access);
		} catch (ConfigurationException ce) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_OPEN_CONFIGURATION_ON_PRINCIPLE,
				new String[] { configName + " in " + action, accessAsString },
				ce);
			sde.setMessageID("ASJ.dpl_ds.005062");
			throw sde;
		}
		return config;
	}

	private static String convertAccess(int access) {
		switch (access) {
		case ConfigurationHandler.READ_ACCESS: {
			return "read";
		}
		case ConfigurationHandler.WRITE_ACCESS: {
			return "write";
		}
		default: {
			return "unknown";
		}
		}
	}

	public static void deleteConfiguration(ConfigurationHandler handler,
			String fullCfgName, String action) throws ServerDeploymentException {
		try {
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
					"Will delete [{0}] configuration from [{1}] in [{2}].",
					fullCfgName, handler, action);
			}
			try {
				final Configuration cfgForDelete = handler.openConfiguration(
					fullCfgName, ConfigurationHandler.WRITE_ACCESS);
				if (cfgForDelete != null) {
					cfgForDelete.deleteConfiguration();
				}
			} catch (NameNotFoundException nnfe) {
				// ignore it.
			}
		} catch (ConfigurationException deleteCe) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_DELETE_CFG, new String[] {
					fullCfgName, action }, deleteCe);
			sde.setMessageID("ASJ.dpl_ds.005105");
			sde.setDcNameForObjectCaller(handler);
			throw sde;
		}
	}

	public static Configuration deleteAllSubConfiguration(Configuration cfg)
		throws ConfigurationException {
		cfg.deleteAllConfigEntries();
		cfg.deleteAllFiles();
		cfg.deleteAllSubConfigurations();
		return cfg;
	}

	public static Configuration recreateSubConfiguration(Configuration parent,
		String subCfgName, int type) throws ConfigurationException {
		deleteSubConfiguration(parent, subCfgName);
		return parent.createSubConfiguration(subCfgName, type);
	}

	public static void deleteSubConfiguration(Configuration parent,
		String subCfgName) throws ConfigurationException {
		if (parent.existsSubConfiguration(subCfgName)) {
			parent.deleteConfiguration(subCfgName);
		}
	}

	public static void deleteConfigEntry(Configuration parent,
		String configEntry) throws ConfigurationException {
		if (parent.existsConfigEntry(configEntry)) {
			parent.deleteConfigEntry(configEntry);
		}
	}

	public static void deleteLocalConfigEntry(Configuration parent,
		String configEntry) throws ConfigurationException {
		if (parent instanceof DerivedConfiguration) {
			if (parent.existsConfigEntry(configEntry)) {
				final DerivedConfiguration dParent = (DerivedConfiguration) parent;
				if (dParent.isLocalConfigEntry(configEntry)) {
					dParent.deleteConfigEntry(configEntry);
				}
			}
		} else {
			deleteConfigEntry(parent, configEntry);
		}
	}

	public static void deleteFile(Configuration parent, String file)
		throws ConfigurationException {
		if (parent.existsFile(file)) {
			parent.deleteFile(file);
		}
	}

	public static Properties getPropsFromSubConfiguration(Configuration cfg,
		String psName) throws ConfigurationException {
		final PropertySheet ps = getExistsPropertySheet(cfg, psName);
		return ps != null ? ps.getProperties() : null;
	}

	public static PropertySheet getExistsPropertySheet(Configuration cfg,
		String psName) throws ConfigurationException {
		final Configuration psCfg = getExistsSubConfiguration(cfg, psName);
		return psCfg != null ? psCfg.getPropertySheetInterface() : null;
	}

	public static Configuration getOrCreateSubConfiguration(Configuration cfg,
		String subName, int type) throws ConfigurationException {
		return cfg.existsSubConfiguration(subName) ?
			cfg.getSubConfiguration(subName) :
			cfg.createSubConfiguration(subName, type);
	}

	public static Configuration getExistsSubConfiguration(Configuration cfg,
		String subName) throws ConfigurationException {
		if (cfg.existsSubConfiguration(subName)) {
			return cfg.getSubConfiguration(subName);
		}
		return null;
	}

	public static Object getExistingConfigEntry(Configuration cfg,
		String entryName) throws ConfigurationException {
		if (cfg.existsConfigEntry(entryName)) {
			return cfg.getConfigEntry(entryName);
		}
		return null;
	}

	public static void createPropertyEntry(PropertySheet ps, String key,
		Object value) throws ConfigurationException {
		if (value == null) {
			return;
		}
		ps.createPropertyEntry(key, value, value.getClass().getName());
	}

	public static void createPropertyEntry(PropertySheet ps, String key,
		Object value, String description) throws ConfigurationException {
		if (value == null) {
			return;
		}
		PropertyEntry entry = ps.createPropertyEntry(
			key, value, value.getClass().getName());
		entry.setShortDescription(description);
	}

	public static void updatePropertyEntry(PropertySheet ps, String key,
		Object value) throws ConfigurationException {
		if (ps.existsPropertyEntry(key)) {
			ps.deletePropertyEntry(key);
		}
		if (value == null) {
			return;
		}
		ps.createPropertyEntry(key, value, value.getClass().getName());
	}

	public static void updatePropertyEntry(PropertySheet ps, String key,
		Object value, String description) throws ConfigurationException {
		if (ps.existsPropertyEntry(key)) {
			ps.deletePropertyEntry(key);
		}
		if (value == null) {
			return;
		}
		PropertyEntry entry = ps.createPropertyEntry(
			key, value, value.getClass().getName());
		entry.setShortDescription(description);
	}

	public static Configuration getOrCreateSubCnfiguration(
			Configuration parent, String childs) throws ConfigurationException {
		if (childs == null || childs.equals("")) {
			return parent;
		}
		childs = clearCfgName(childs);
		int index = childs.indexOf("/");
		if (index == -1) {
			return getOrCreateSubConfiguration(parent, childs,
					Configuration.CONFIG_TYPE_STANDARD);
		} else {
			final String start = childs.substring(0, index);
			final String rest = childs.substring(index + 1);
			final Configuration subCfg = getOrCreateSubConfiguration(parent,
					start, Configuration.CONFIG_TYPE_STANDARD);
			return getOrCreateSubCnfiguration(subCfg, rest);
		}
	}

	public static Configuration getSubCnfiguration(Configuration parent,
			String childs) throws ConfigurationException {
		if (childs == null || childs.equals("")) {
			return parent;
		}
		childs = clearCfgName(childs);
		int index = childs.indexOf("/");
		if (index == -1) {
			return parent.getSubConfiguration(childs);
		} else {
			final String start = childs.substring(0, index);
			final String rest = childs.substring(index + 1);
			final Configuration subCfg = parent.getSubConfiguration(start);
			return getSubCnfiguration(subCfg, rest);
		}
	}

	private static String clearCfgName(String source) {
		final char del = '/';
		String result = source.replace(File.separatorChar, del);
		if (result.startsWith(del + "")) {
			result = result.substring(1);
		}
		if (result.endsWith(del + "")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public static Configuration getSubConfiguration(
		ConfigurationHandler handler, String root, String provider,
		String appName) throws ConfigurationException {
		final String cfgPath = root + "/" + provider + "/" + appName;
		try {
			return handler.openConfiguration(cfgPath,
					ConfigurationHandler.WRITE_ACCESS);
		} catch (NameNotFoundException ce) {
			try {
				return handler.createSubConfiguration(cfgPath);
			} catch (NameNotFoundException nnfe) {
				Configuration providerConfig = null;
				try {
					providerConfig = handler.createSubConfiguration(root + "/"
							+ provider);
				} catch (NameNotFoundException nnfEx) {
					providerConfig = handler.createRootConfiguration(root)
							.createSubConfiguration(provider);
				}
				return providerConfig.createSubConfiguration(appName);
			}
		}
	}

	/**
	 * Returns application index, which will be stored to
	 * apps\<vemdor>\<name>\version.bin for comparing it with the file system
	 * index.
	 * 
	 * @param config
	 * @param cfgFactory
	 * @return
	 * @throws ServerDeploymentException
	 */
	public static Index getIndexDB(Configuration config,
			ConfigurationHandlerFactory cfgFactory)
			throws ServerDeploymentException {
		try {
			final int type = config.getConfigurationType();
			if ((type & Configuration.CONFIG_TYPE_INDEXED) != 0) {
				return config.getIndex();
			} else {
				// it is not indexed, because this application is
				// deployed before AS Java to be updated to 7.11
				// to improve download time in this case we create
				// this index using last modification time stamp
				// of application configuration

				return HashUtils.createIndex(""
						+ cfgFactory.getHierarchyModificationTS(config
								.getPath()));
			}
		} catch (ConfigurationException cEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "get configuration index from "
							+ config.getPath() }, cEx);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}
	}

}
