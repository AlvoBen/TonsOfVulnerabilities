package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;
import java.util.Collection;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;

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
public interface UndeployResult extends Serializable {

	public UndeployResultStatus getUndeployStatus();

	public Collection getUndeployItems();

	public Collection getOrderedUndeployItems();

	public String getDescription();

	public String toString();
	
	public DMeasurement getMeasurement();

}
