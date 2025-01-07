package com.sap.engine.services.dc.gd.impl;

import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.GenericDelivery;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.Utils;
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
abstract class InitialGenericDeliveryImpl implements GenericDelivery {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.gd.GenericDelivery#deploy(com.sap.engine.services
	 * .dc.cm.deploy.DeploymentItem)
	 */
	public void deploy(DeploymentItem deploymentItem) throws DeliveryException,
			RollingDeliveryException {
		if (deploymentItem == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003304 The specified deployment item could not be null.");
		}

		SoftwareType swtType = deploymentItem.getSda().getSoftwareType();
		final SoftwareTypeService softwareTypeService = (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());

		// TODO take the software types out of generic delivery
		if (softwareTypeService.getApplicationSoftwareTypes().contains(swtType)
				|| softwareTypeService.getSoftwareTypesByAttribute(
						SoftwareTypeService.APPLICATION_DELIVERY).contains(
						swtType)) {
			Deployer appDeployer = getApplicationDeployer();

			appDeployer.performDeployment(deploymentItem);

		} else if (softwareTypeService.getSoftwareTypesByAttribute(

		SoftwareTypeService.LIBRARY_DELIVERY).contains(swtType)) {
			Deployer libraryDeployer = getLibraryDeployer();

			libraryDeployer.performDeployment(deploymentItem);

		} else if (Utils.getOnlineDeploymentOfCoreComponents()) {

			// This is a prototype for online deployment of core components

			Deployer coreDeployer = CoreDeployer.getInstance();
			coreDeployer.performDeployment(deploymentItem);

		} else {

			throw new DeliveryException(
					DCExceptionConstants.SW_TYPE_UNRECOGNIZED,
					new String[] { swtType.toString() });
		}
	}

	abstract protected Deployer getLibraryDeployer() throws DeliveryException,
			RollingDeliveryException;

	abstract protected Deployer getApplicationDeployer()
			throws DeliveryException, RollingDeliveryException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.gd.GenericDelivery#deploy(com.sap.engine.services
	 * .dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void deploy(CompositeDeploymentItem compositeDeploymentItem)
			throws DeliveryException, RollingDeliveryException {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.gd.GenericDelivery#undeploy(com.sap.engine
	 * .services.dc.cm.undeploy.UndeployItem)
	 */
	public void undeploy(UndeployItem undeployItem) throws DeliveryException,
			RollingDeliveryException {
		if (undeployItem == null) {
			return;
		}

		final SoftwareTypeService softwareTypeService = (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());
		SoftwareType swtType = undeployItem.getSda().getSoftwareType();
		if (softwareTypeService.getApplicationSoftwareTypes().contains(swtType)
				|| softwareTypeService.getSoftwareTypesByAttribute(
						SoftwareTypeService.APPLICATION_DELIVERY).contains(
						swtType)) {
			Deployer appDeployer = getApplicationDeployer();

			appDeployer.performUndeployment(undeployItem);
		} else if (softwareTypeService.getSoftwareTypesByAttribute(
				SoftwareTypeService.LIBRARY_DELIVERY).contains(swtType)) {
			Deployer libraryDeployer = getLibraryDeployer();
			libraryDeployer.performUndeployment(undeployItem);

		} else if (Utils.getOnlineDeploymentOfCoreComponents()) {

			// This is a prototype for online deployment of core components

			Deployer coreDeployer = CoreDeployer.getInstance();
			coreDeployer.performUndeployment(undeployItem);

		} else {
			throw new DeliveryException(
					DCExceptionConstants.SW_TYPE_UNRECOGNIZED,
					new String[] { swtType.toString() });
		}

	}

	public void undeploy(ScaUndeployItem undeployItem) throws DeliveryException, 
		RollingDeliveryException{
		
	}

}
