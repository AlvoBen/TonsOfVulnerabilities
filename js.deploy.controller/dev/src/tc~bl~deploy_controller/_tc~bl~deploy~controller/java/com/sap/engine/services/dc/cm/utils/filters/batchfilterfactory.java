package com.sap.engine.services.dc.cm.utils.filters;

import java.util.Set;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class BatchFilterFactory {

	private static BatchFilterFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.utils.filters.impl.BatchFilterFactoryImpl";

	protected BatchFilterFactory() {
	}

	public static synchronized BatchFilterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static BatchFilterFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (BatchFilterFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003251 An error occurred while creating an instance of "
					+ "class BatchFilterFactory! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract BatchFilter createSoftwareTypeBatchFilter(
			String softwareType);

	public abstract BatchFilter createSoftwareTypeBatchFilter(
			String softwareType, String softwareSubType);

}
