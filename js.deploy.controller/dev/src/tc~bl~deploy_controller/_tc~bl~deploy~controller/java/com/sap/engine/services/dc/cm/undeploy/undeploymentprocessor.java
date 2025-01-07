package com.sap.engine.services.dc.cm.undeploy;

import com.sap.engine.services.dc.cm.validate.UndeployValidationBatchResult;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface UndeploymentProcessor {

	public UndeployResult process(UndeploymentBatch undeploymentBatch)
			throws UndeploymentException;

	public UndeployValidationBatchResult validate(
			UndeploymentBatch undeploymentBatch) throws UndeploymentException;
}
