/* 
 * Created on Feb 21, 2007
 */
package com.sap.engine.services.dc.api.cmd.report.impl;

import com.sap.engine.services.dc.api.cmd.report.DeployReporter;
import com.sap.engine.services.dc.api.cmd.report.ReporterFactory;

/**
 * Title: | Description: |
 * 
 * Copyright (c) 2007, SAP-AG Date: Feb 21, 2007
 * 
 * @author Daniel Hristov
 * @version 1.0
 */

public class ReporterFactoryImpl extends ReporterFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.cmd.resultprocessor.ResultProcessorFactory
	 * #createDeployResultProcessor()
	 */
	@Override
	public DeployReporter createDeployReporter() {
		// TODO Auto-generated method stub
		return new DeployReporterImpl();
	}

}
