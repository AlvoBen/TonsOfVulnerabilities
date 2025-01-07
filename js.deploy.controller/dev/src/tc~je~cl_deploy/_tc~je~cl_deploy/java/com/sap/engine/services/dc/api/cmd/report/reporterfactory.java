/* 
 * Created on Feb 21, 2007
 */
package com.sap.engine.services.dc.api.cmd.report;

/**
 * Title: Java installation team Description:
 * 
 * Copyright (c) 2007, SAP-AG Date: Feb 21, 2007
 * 
 * @author Daniel Hristov
 * @version 1.0
 */

public abstract class ReporterFactory {
	private static ReporterFactory instance;

	private static final String FACTORY_IMPLEMENTATION_CLASS = "com.sap.engine.services.dc.api.cmd.report.impl.ReporterFactoryImpl";

	public static synchronized ReporterFactory getInstance() {
		if (instance == null) {
			try {
				Class clazz = Class.forName(FACTORY_IMPLEMENTATION_CLASS);
				instance = (ReporterFactory) clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(
						"Failed to instantiate ResultProcessorFactory! "
								+ e.getMessage(), e);
			}
		}
		return instance;
	}

	public abstract DeployReporter createDeployReporter();
}
