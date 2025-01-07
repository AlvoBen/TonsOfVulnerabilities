package com.sap.engine.services.dc.repo.migration;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;

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
public interface MigrationManager {

	public void persistMigrationData(MigrationData migrationData,
			ConfigurationHandler cfgHandler) throws MigrationException;

	public MigrationData loadMigrationData(ConfigurationHandler cfgHandler)
			throws MigrationException;

}
