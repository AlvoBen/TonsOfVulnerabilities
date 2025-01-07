package com.sap.engine.services.dc.repo.migration.impl;

import com.sap.engine.services.dc.repo.migration.MigrationData;
import com.sap.engine.services.dc.repo.migration.MigrationFactory;
import com.sap.engine.services.dc.repo.migration.MigrationManager;
import com.sap.engine.services.dc.util.structure.tree.CfgBuilder;

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
public final class MigrationFactoryImpl extends MigrationFactory {

	public MigrationFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repo.migration.MigrationFactory#
	 * createMigrationData(int, java.lang.String)
	 */
	public MigrationData createMigrationData(int version, String description) {
		return new MigrationDataImpl(version, description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repo.migration.MigrationFactory#
	 * createDefaultMigrationData()
	 */
	public MigrationData createDefaultMigrationData() {
		return new MigrationDataImpl(MigrationConstants.DEFAULT_VERSION,
				MigrationConstants.DEFAULT_DESCRIPTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repo.migration.MigrationFactory#
	 * createMigrationManager()
	 */
	public MigrationManager createMigrationManager() {
		return new MigrationManagerImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.migration.MigrationFactory#getCfgBuilder
	 * ()
	 */
	public CfgBuilder getCfgBuilder() {
		return MigrationCfgBuilder.getInstance();
	}

}
