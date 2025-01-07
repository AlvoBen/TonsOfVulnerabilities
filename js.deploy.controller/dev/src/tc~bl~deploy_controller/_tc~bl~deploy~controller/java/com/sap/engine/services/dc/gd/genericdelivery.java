package com.sap.engine.services.dc.gd;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface GenericDelivery {

	public void deploy(DeploymentItem deploymentItem) throws DeliveryException,
			RollingDeliveryException;

	public void deploy(CompositeDeploymentItem compositeDeploymentItem)
			throws DeliveryException, RollingDeliveryException;

	public void undeploy(UndeployItem undeployItem) throws DeliveryException,
			RollingDeliveryException;

	public void undeploy(ScaUndeployItem undeployItem) throws DeliveryException,
			RollingDeliveryException;
	
}
