package com.sap.engine.services.dc.repo.migration;

import com.sap.engine.services.dc.util.Constants;
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
public abstract class MigrationFactory {

	private static MigrationFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.repo.migration.impl.MigrationFactoryImpl";

	protected MigrationFactory() {
	}

	public static synchronized MigrationFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}

		return INSTANCE;
	}

	private static MigrationFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (MigrationFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003363 An error occurred while creating an instance of "
					+ "class MigrationFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract MigrationData createMigrationData(int version,
			String description);

	public abstract MigrationData createDefaultMigrationData();

	public abstract MigrationManager createMigrationManager();

	public abstract CfgBuilder getCfgBuilder();

}
