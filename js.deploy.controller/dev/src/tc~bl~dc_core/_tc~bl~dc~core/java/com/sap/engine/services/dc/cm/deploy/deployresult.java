package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.Collection;

import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-8
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public interface DeployResult extends Serializable {

	DeployResultStatus getDeployStatus();

	Collection getSortedDeploymentBatchItems();

	Collection getDeploymentItems();

	String getDescription();

	public String toString();
	
	DMeasurement getMeasurement();

}
