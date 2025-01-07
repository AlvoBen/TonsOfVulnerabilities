package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.GenericDelivery;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-18
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
class RollingGenericDeliveryImpl extends InitialGenericDeliveryImpl {

	RollingGenericDeliveryImpl() {
	}

	final protected Deployer getLibraryDeployer() throws DeliveryException,
			RollingDeliveryException {
		return RollingLibraryDeployer.getInstance();
	}

	final protected Deployer getApplicationDeployer() throws DeliveryException,
			RollingDeliveryException {
		return RollingApplicationDeployer.getInstance();
	}

}
