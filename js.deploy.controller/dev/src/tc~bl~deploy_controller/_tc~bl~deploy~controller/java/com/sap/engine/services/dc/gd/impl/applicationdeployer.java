package com.sap.engine.services.dc.gd.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.NamingException;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.tc.logging.Location;

/**
 * @author Rumiana Angelova
 * @version 7.0
 * 
 */
class ApplicationDeployer extends InitialApplicationDeployer {
	
	private Location location = getLocation(this.getClass());

	private static ApplicationDeployer INSTANCE;

	private final DeployService deployService;

	static synchronized ApplicationDeployer getInstance()
			throws DeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new ApplicationDeployer();
		}

		return INSTANCE;
	}

	private ApplicationDeployer() throws DeliveryException {
		try {
			this.deployService = ServiceConfigurer.getInstance()
					.getDeployService();
		} catch (NamingException nex) {
			throw new DeliveryException(DCExceptionConstants.GET_DS, nex);
		}
	}

	final protected void update(DeploymentItem deploymentItem,
			boolean isStandAlone, String newSdaPath,
			Properties deploymentProperties) throws DeliveryException {
		final String tagName = "update: " + deploymentItem.getBatchItemId();
		Accounting.beginMeasure(tagName, DeployService.class);
		try {
			if (isStandAlone) {
				if (location.bePath()) {
					tracePath(location, "Invoking Deploy Service's update operation ...");
				}
				deployService.update(newSdaPath, null, SUPPORTEDTRANSPORTS,
						deploymentProperties);
				if (location.bePath()) {
					tracePath(location, "Deploy Service's update operation has finished");
				}
			} else {
				if (location.bePath()) {
					tracePath(location, "Invoking Deploy Service's update operation ...");
				}
				deployService.update(newSdaPath, deploymentProperties);
				if (location.bePath()) {
					tracePath(location, "Deploy Service's update operation has finished");
				}
			}
		} catch (WarningException wex) {
			if (ServiceConfigurer.getInstance().isDSWarningSuppressed() == false) {
				String csnComponent = deploymentItem.getSdu().getCsnComponent();
				if (csnComponent.equals("")) {
					csnComponent = "not available";
				}
				deploymentItem.setDeploymentStatus(DeploymentStatus.WARNING);
				final String warnings = this.concatStrings(wex.getWarnings()) + "\r\n CSN component is " + csnComponent;
				deploymentItem.addDescription(warnings);
				if (location.beWarning()) {
					traceWarning(
							location,
							"ASJ.dpl_dc.005101",
							"Deploy Service's update operation has finished with warnings. CSN component is [{0}]. \r\n [{1}]",
							new String[] { csnComponent, warnings });
				}
			}
		} catch (RemoteException rex) {
			throw new DeliveryException(DCExceptionConstants.UPDATE,
					new String[] { deploymentItem.toString() }, rex);
		} catch (Exception ex) {
			throw new DeliveryException(DCExceptionConstants.UPDATE,
					new String[] { deploymentItem.toString() }, ex);
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	final protected void deploy(DeploymentItem deploymentItem,
			boolean isStandAlone, String newSdaPath,
			Properties deploymentProperties) throws DeliveryException {
		final String tagName = "deploy: " + deploymentItem.getBatchItemId();
		Accounting.beginMeasure(tagName, DeployService.class);
		try {
			if (isStandAlone) {
				if (location.bePath()) {
					tracePath(location, "Invoking Deploy Service's deploy operation ...");
				}
				deployService.deploy(newSdaPath, null, SUPPORTEDTRANSPORTS,
						deploymentProperties);
				if (location.bePath()) {
					tracePath(location, "Deploy Service's deploy operation has finished");
				}

			} else {
				if (location.bePath()) {
					tracePath(location, "Invoking Deploy Service's deploy operation ...");
				}
				deployService.deploy(newSdaPath, SUPPORTEDTRANSPORTS,
						deploymentProperties);
				if (location.bePath()) {
					tracePath(location, "Deploy Service's deploy operation has finished");
				}
			}
		} catch (WarningException wex) {
			if (ServiceConfigurer.getInstance().isDSWarningSuppressed() == false) {
				String csnComponent = deploymentItem.getSdu().getCsnComponent();
				if (csnComponent.equals("")) {
					csnComponent = "not available";
				}
				deploymentItem.setDeploymentStatus(DeploymentStatus.WARNING);
				final String warnings = this.concatStrings(wex.getWarnings()) + "\r\n CSN component is " + csnComponent;
				deploymentItem.addDescription(warnings);
				if (location.beWarning()) {
					traceWarning(
							location,
							"ASJ.dpl_dc.005102",
							"Deploy Service's deploy operation has finished with warnings. CSN component is [{0}]. \r\n [{1}]", 
							new String[] { csnComponent, warnings });
				}
			}
		} catch (RemoteException rex) {
			throw new DeliveryException(
					DCExceptionConstants.DEPLOY,
					new String[] { deploymentItem.getSda().getId().toString() },
					rex);
		} catch (Exception ex) {
			throw new DeliveryException(
					DCExceptionConstants.DEPLOY,
					new String[] { deploymentItem.getSda().getId().toString() },
					ex);
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	void performUndeployment(UndeployItem undeployItem)
			throws DeliveryException {
		// init the development component in order to extract
		// its name and vendor used by the J2EE engine

		final String dcName = undeployItem.getName();
		final String dcVendor = undeployItem.getVendor();
		final String tagName = "remove: " + undeployItem.getId();
		Accounting.beginMeasure(tagName, DeployService.class);
		try {
			if (location.bePath()) {
				tracePath(location, 
						"Invoking Deploy Service's remove operation ...");
			}
			try {
				this.deployService.remove(dcVendor, dcName);
			} catch (ComponentNotDeployedException cnfe) {
				undeployItem.setUndeployItemStatus(UndeployItemStatus.WARNING);
				undeployItem
						.setDescription("Warnings:\n"
								+ "\tThe component is removed from the repository despite it was not deployed."
								+ "\n\tReason:" + cnfe.getLocalizedMessage());
			}
			if (location.bePath()) {
				tracePath(location, 
						"Deploy Service's remove operation has finished");
			}
		} catch (WarningException wex) {
			undeployItem.setUndeployItemStatus(UndeployItemStatus.WARNING);
			undeployItem.setDescription("Warnings:\n"
					+ this.concatStrings(wex.getWarnings()));
		} catch (RemoteException ex) {
			if (ex.getMessage() != null) {
				undeployItem.setDescription("Error:\n" + ex.getMessage());
			}

			throw new DeliveryException(DCExceptionConstants.REMOVE,
					new String[] { undeployItem.toString() }, ex);
		} catch (Exception ex) {
			if (ex.getMessage() != null) {
				undeployItem.setDescription("Error:\n" + ex.getMessage());
			}

			throw new DeliveryException(DCExceptionConstants.REMOVE,
					new String[] { undeployItem.toString() }, ex);
		} finally {
			Accounting.endMeasure(tagName);
		}

	}

} // class EngineJ2EE620OnlineDeployer

