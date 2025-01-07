package com.sap.engine.services.dc.repo.migration.impl;

import java.util.Properties;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.migration.MigrationData;
import com.sap.engine.services.dc.repo.migration.MigrationException;
import com.sap.engine.services.dc.repo.migration.MigrationFactory;
import com.sap.engine.services.dc.repo.migration.MigrationManager;
import com.sap.engine.services.dc.util.CfgUtils;

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
final class MigrationManagerImpl implements MigrationManager {

	MigrationManagerImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repo.migration.MigrationManager#
	 * persistMigrationData
	 * (com.sap.engine.services.dc.repo.migration.MigrationData,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public void persistMigrationData(MigrationData migrationData,
			ConfigurationHandler cfgHandler) throws MigrationException {
		paramCheck(migrationData, "persistMigrationData", "migrationData");
		paramCheck(cfgHandler, "persistMigrationData", "cfgHandler");

		final MigrationDataLocation migDataLocation = MigrationDataLocationBuilder
				.getInstance().build(migrationData);

		persist(migDataLocation, cfgHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.migration.MigrationManager#loadMigrationData
	 * (com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public MigrationData loadMigrationData(ConfigurationHandler cfgHandler)
			throws MigrationException {
		final MigrationDataLocation migDataLocation = MigrationDataLocationBuilder
				.getInstance().build();

		return load(migDataLocation, cfgHandler);
	}

	private MigrationData load(MigrationDataLocation migDataLocation,
			ConfigurationHandler cfgHandler) throws MigrationException {

		Configuration migCfg = null;
		try {
			final PropertySheet propSheet;
			try {
				migCfg = CfgUtils.openCfgRead(cfgHandler, migDataLocation
						.getLocation());

				propSheet = getPropertyEntries(migCfg,
						LocationConstants.MIGRATION_PROPS);
			} catch (NameNotFoundException nnfe) {
				return MigrationFactory.getInstance()
						.createDefaultMigrationData();
			}

			final String versionStr = propSheet.getPropertyEntry(
					MigrationConstants.MIGRATION_VERSION).getValue().toString();
			final String description = propSheet.getPropertyEntry(
					MigrationConstants.MIGRATION_DESCRIPTION).getValue()
					.toString();

			return MigrationFactory.getInstance().createMigrationData(
					Integer.parseInt(versionStr), description);
		} catch (ConfigurationException ce) {
			throw new MigrationException(
					"ASJ.dpl_dc.003359 An error occurred while loading the migration "
							+ " data for the migration location '"
							+ migDataLocation.getLocation() + "'", ce);
		} finally {
			closeCfg(cfgHandler, migCfg);
		}
	}

	private PropertySheet getPropertyEntries(Configuration migCfg,
			String propSheetName) throws ConfigurationException {
		final Configuration migPropsCfg = migCfg
				.getSubConfiguration(propSheetName);

		return migPropsCfg.getPropertySheetInterface();
	}

	private void persist(MigrationDataLocation migDataLocation,
			ConfigurationHandler cfgHandler) throws MigrationException {
		Configuration migCfg = null;
		try {
			migCfg = CfgUtils.openSubCfg(cfgHandler, migDataLocation
					.getLocation());
			final Configuration propsCfg = CfgUtils.createSubCfg(
					LocationConstants.MIGRATION_PROPS, migCfg,
					Configuration.CONFIG_TYPE_PROPERTYSHEET);
			final PropertySheet migPropsSheet = propsCfg
					.getPropertySheetInterface();
			migPropsSheet.deleteAllPropertyEntries();
			final Properties migProps = getMigProps(migDataLocation);
			migPropsSheet.createPropertyEntries(migProps);

			cfgHandler.commit();
		} catch (ConfigurationException ce) {
			throw new MigrationException(
					"ASJ.dpl_dc.003360 An error occurred while persisting the migration "
							+ " data for the migration location '"
							+ migDataLocation.getLocation() + "'", ce);
		} finally {
			closeCfg(cfgHandler, migCfg);
		}
	}

	private Properties getMigProps(MigrationDataLocation migDataLocation) {
		final Properties migProps = new Properties();
		migProps.put(MigrationConstants.MIGRATION_VERSION, String
				.valueOf(migDataLocation.getMigrationData().getVersion()));
		migProps.put(MigrationConstants.MIGRATION_DESCRIPTION, String
				.valueOf(migDataLocation.getMigrationData().getDescription()));

		return migProps;
	}

	private void paramCheck(Object obj, String methodName, String paramName) {
		if (obj != null) {
			return;
		}

		final String errText = "ASJ.dpl_dc.003361 "
				+ this.getClass().getName() + "." + methodName
				+ "(...): parameter '" + paramName + "' is null.";

		throw new NullPointerException(errText);
	}

	private void closeCfg(ConfigurationHandler cfgHandler, Configuration cfg)
			throws MigrationException {
		if (cfg == null) {
			return;
		}

		try {
			cfgHandler.closeConfiguration(cfg);
		} catch (ConfigurationException ce) {
			throw new MigrationException(
					"ASJ.dpl_dc.003362 The configuration '"
							+ cfg.getPath() + "' could not be closed.", ce);
		}
	}

}
