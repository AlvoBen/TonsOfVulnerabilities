package com.sap.engine.services.dc.gd.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.rmi.RemoteException;
import javax.naming.NamingException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.DeployServiceExt;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.tc.logging.Location;

/**
 * @author Pavel Genevski
 * @version 7.11
 */
class CoreDeployer extends Deployer {
	
	private Location location = getLocation(this.getClass());

	private static CoreDeployer INSTANCE;
	private final DeployServiceExt deployServiceExtension;

	static synchronized CoreDeployer getInstance() throws DeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new CoreDeployer();
		}

		return INSTANCE;
	}

	private CoreDeployer() throws DeliveryException {
		try {
			this.deployServiceExtension = ServiceConfigurer.getInstance()
					.getDeployServiceExt();
		} catch (NamingException nex) {
			throw new DeliveryException(DCExceptionConstants.GET_DS, nex);
		}
	}

	void performDeployment(DeploymentItem deploymentItem)
			throws DeliveryException {

		deploymentItem.startTimeStatEntry("Core Deployer",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		try {

			try {
				long beginTime = System.currentTimeMillis();


				if (deploymentItem.getSda().getSoftwareType().getName()
						.equalsIgnoreCase("primary-service")) {

					if (location.bePath()) {
						tracePath(location, 
								"Invoking Deploy Service's deployService operation ...");
					}
					this.deployServiceExtension.deployService(deploymentItem
							.getSduFilePath());
					if (location.bePath()) {
						tracePath(location, 
								"Deploy Service's deployService operation has finished. Time: [{0}] ms.",
								new Object[] { String.valueOf(System
										.currentTimeMillis()
										- beginTime) });
					}
				} else if (deploymentItem.getSda().getSoftwareType().getName()
						.equalsIgnoreCase("primary-interface")) {

					if (location.bePath()) {
						tracePath(location, 
								"Invoking Deploy Service's deployInterface operation ...");
					}
					this.deployServiceExtension.deployInterface(deploymentItem
							.getSduFilePath());
					if (location.bePath()) {
						tracePath(location, 
								"Deploy Service's deployInterface operation has finished. Time: [{0}] ms.",
								new Object[] { String.valueOf(System
										.currentTimeMillis()
										- beginTime) });
					}

				} else if (deploymentItem.getSda().getSoftwareType().getName()
						.equalsIgnoreCase("primary-library")) {

					if (location.bePath()) {
						tracePath(location, 
								"Invoking Deploy Service's deployLibrary operation ...");
					}
					// TODO assuming that the primary libraries can be deployed
					// as non primary ones
					this.deployServiceExtension.deployLibrary(deploymentItem
							.getSduFilePath());
					if (location.bePath()) {
						tracePath(location, 
								"Deploy Service's deployLibrary operation has finished. Time: [{0}] ms.",
								new Object[] { String.valueOf(System
										.currentTimeMillis()
										- beginTime) });
					}
				} else {
					throw new IllegalStateException(
							"The only core components allowed for online delivery are primary interfaces, libraries and services");
				}


			} catch (WarningException we) {
				deploymentItem.setDeploymentStatus(DeploymentStatus.WARNING);
				deploymentItem.addDescription("Warnings:\n"
						+ this.concatStrings(we.getWarnings()));
			} catch (RemoteException re) {
				throw new DeliveryException(DCExceptionConstants.DEPLOY,
						new String[] { deploymentItem.getSda().getId()
								.toString() }, re);
			} catch (Exception e) {
				throw new DeliveryException(DCExceptionConstants.DEPLOY,
						new String[] { deploymentItem.getSda().getId()
								.toString() }, e);
			}
		} finally {
			deploymentItem.finishTimeStatEntry();
		}
	}

	void performUndeployment(UndeployItem item) throws DeliveryException {

		final String dcName = item.getSda().getName();
		final String dcVendor = item.getSda().getVendor();

		try {
			long beginTime = System.currentTimeMillis();

			try {

				if (item.getSda().getSoftwareType().getName().equalsIgnoreCase(
						"primary-service")) {

					if (location.bePath()) {
						tracePath(location, 
								"Invoking Deploy Service's removeService operation ...");
					}
					
					this.deployServiceExtension.removeService(dcVendor, dcName);
					
					if (location.bePath()) {
						tracePath(location, 
								"Deploy Service's removeService operation has finished. Time: [{0}] ms.",
								new Object[] { new Long(beginTime - System.currentTimeMillis()) });
					}

				} else if (item.getSda().getSoftwareType().getName()
						.equalsIgnoreCase("primary-interface")) {

					if (location.bePath()) {
						tracePath(location, 
								"Invoking Deploy Service's removeInterface operation ...");
					}
					
					this.deployServiceExtension.removeInterface(dcVendor,
							dcName);
					
					if (location.bePath()) {
						tracePath(location, 
								"Deploy Service's removeInterface operation has finished. Time: [{0}] ms.",
								new Object[] { new Long(beginTime - System.currentTimeMillis()) });
					}

				} else if (item.getSda().getSoftwareType().getName()
						.equalsIgnoreCase("primary-library")) {

					if (location.bePath()) {
						tracePath(location, 
								"Invoking Deploy Service's removeLibrary operation ...");
					}
					
					// TODO assuming that the primary libraries can be deployed
					// as non primary ones
					this.deployServiceExtension.removeLibrary(dcName);
					
					if (location.bePath()) {
						tracePath(location, 
								"Deploy Service's removeLibrary operation has finished. Time: [{0}] ms.",
								new Object[] { new Long(beginTime - System.currentTimeMillis()) });
					}

				} else {
					throw new IllegalStateException(
							"The only core components allowed for online undeployoment are primary interfaces, libraries and services");
				}

			} catch (ComponentNotDeployedException cnfe) {
				item.setUndeployItemStatus(UndeployItemStatus.WARNING);
				item
						.setDescription("Warnings:\n"
								+ "\tThe component is removed from the repository despite it was not deployed."
								+ "\n\tReason:" + cnfe.getLocalizedMessage());
			}
		} catch (WarningException we) {

			item.setUndeployItemStatus(UndeployItemStatus.WARNING);
			item.setDescription("Warnings:\n"
					+ this.concatStrings(we.getWarnings()));

		} catch (RemoteException rex) {

			if (rex.getMessage() != null) {
				item.setDescription("Error: \n" + rex.getMessage());
			}

			throw new DeliveryException(DCExceptionConstants.REMOVE,
					new String[] { item.toString() }, rex);

		} catch (Exception ex) {
			if (ex.getMessage() != null) {
				item.setDescription("Error: \n" + ex.getMessage());
			}

			throw new DeliveryException(DCExceptionConstants.REMOVE,
					new String[] { item.toString() }, ex);
		}
	}

}
