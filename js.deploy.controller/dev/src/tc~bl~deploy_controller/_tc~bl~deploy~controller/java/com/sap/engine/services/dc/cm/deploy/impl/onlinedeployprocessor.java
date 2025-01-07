package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.DeliveryType;
import com.sap.engine.services.dc.gd.GDFactory;
import com.sap.engine.services.dc.gd.GenericDelivery;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.lcm.LCMException;
import com.sap.engine.services.dc.lcm.LifeCycleManager;
import com.sap.engine.services.dc.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.dc.util.logging.DCLogResourceAccessor;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class OnlineDeployProcessor extends AbstractDeployProcessor {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private final LifeCycleManager lcm;
	private final LifeCycleManagerStartVisitor lcmsv;

	protected OnlineDeployProcessor() {
		this.lcm = LifeCycleManagerFactory.getInstance()
				.createLifeCycleManager();
		this.lcmsv = new LifeCycleManagerStartVisitor(this.lcm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.AbstractDeployProcessor#deploy
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void deploy(CompositeDeploymentItem compositeDeploymentItem,
			DeploymentData deploymentData) throws DeploymentException {

		if (!DeploymentStatus.ADMITTED.equals(compositeDeploymentItem.getDeploymentStatus())) {
			return;
		}

		Sca sca = compositeDeploymentItem.getSca();
		if (sca.getOrigSdaIds().size() == 0) {
			this.performDelivery(compositeDeploymentItem, deploymentData);
			if (!DeploymentStatus.DELIVERED.equals(compositeDeploymentItem
					.getDeploymentStatus())
					&& !DeploymentStatus.WARNING.equals(compositeDeploymentItem
							.getDeploymentStatus())) {
				return;
			}					
			this.notifyObservers(compositeDeploymentItem, deploymentData);
			if (compositeDeploymentItem.getDeploymentStatus().equals(
					DeploymentStatus.DELIVERED)) {
				compositeDeploymentItem.setDeploymentStatus(DeploymentStatus.SUCCESS);
			}					
		}else{
			Sca repoSca = (Sca)RepositoryContainer.getDeploymentsContainer().getDeployment(sca.getId());
			if(!sca.equals(repoSca)){
				final Collection deploymentItems = compositeDeploymentItem.getDeploymentItems();
				for (Iterator iter = deploymentItems.iterator(); iter.hasNext();) {
					final DeploymentItem dplItem = (DeploymentItem) iter.next();
					if (!(DeploymentStatus.ALREADY_DEPLOYED.equals(dplItem.getDeploymentStatus()) || 
							VersionStatus.SAME.equals(dplItem.getVersionStatus()))){
						sca.removeSdaId((SdaId) dplItem.getBatchItemId().getSduId());
					}
				}
				if(!sca.getSdaIds().isEmpty()){
					this.performDelivery(compositeDeploymentItem, deploymentData);
					if (!DeploymentStatus.DELIVERED.equals(compositeDeploymentItem
							.getDeploymentStatus())
							&& !DeploymentStatus.WARNING.equals(compositeDeploymentItem
									.getDeploymentStatus())) {
						return;
					}					
					this.notifyObservers(compositeDeploymentItem, deploymentData);
					if (compositeDeploymentItem.getDeploymentStatus().equals(
							DeploymentStatus.DELIVERED)) {
						compositeDeploymentItem.setDeploymentStatus(DeploymentStatus.SUCCESS);
					}					
				}else{
					compositeDeploymentItem
						.addDescription("No deployment item of the composite one was deployed.");
				}
			}else{
				sca.getSdaIds().clear();
				sca.getSdaIds().addAll(repoSca.getSdaIds());
				this.performDelivery(compositeDeploymentItem, deploymentData);
				if (!DeploymentStatus.DELIVERED.equals(compositeDeploymentItem
						.getDeploymentStatus())
						&& !DeploymentStatus.WARNING.equals(compositeDeploymentItem
								.getDeploymentStatus())) {
					return;
				}			
				if (compositeDeploymentItem.getDeploymentStatus().equals(
						DeploymentStatus.DELIVERED)) {
					compositeDeploymentItem.setDeploymentStatus(DeploymentStatus.SUCCESS);
				}									
			}
		}

	}

	protected void performStart(DeploymentItem deploymentItem,
			DeploymentData deploymentData) throws DeploymentException {
		final String tagName = "Start:" + deploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, this.lcmsv.getClass());
		try {

			// this.lcmsv is not thread safe because it keeps the exception
			// as a member so we have to synchronize in order to avoid threading
			// issues
			synchronized (this.lcmsv) {
				this.lcmsv.visit(deploymentItem);
				this.lcmsv.checkException();
			}

		} catch (LCMException lcme) {

			deploymentItem.setDeploymentStatus(DeploymentStatus.ABORTED);
			deploymentItem.addDescription(lcme);

			final String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.001080",
							"An error occurred while starting the deployment item [{0}]",
							new Object[] { deploymentItem });
			DCLog.logErrorThrowable(location, null, errMsg, lcme);
			final ErrorStrategy errorStrategy = deploymentData
					.getDeploymentErrorStrategy();
			if (errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
				throw new DeploymentException(errMsg, lcme);
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
	}

	protected void performStop(DeploymentItem deploymentItem,
			DeploymentData deploymentData) throws DeploymentException {
		deploymentItem.startTimeStatEntry("Stop",
				TimeStatisticsEntry.ENTRY_TYPE_STOP);
		final String tagName = "Stop:" + deploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, this.lcm.getClass());
		try {
			this.lcm.stop(deploymentItem.getSda().getName(), deploymentItem
					.getSda().getVendor());
		} catch (LCMException lcme) {
			deploymentItem.setDeploymentStatus(DeploymentStatus.ABORTED);
			deploymentItem.addDescription(lcme);

			final String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.001081",
							"An error occurred while stopping the deployment item [{0}]",
							new Object[] { deploymentItem });
			DCLog.logErrorThrowable(location, null, errMsg, lcme);
			final ErrorStrategy errorStrategy = deploymentData
					.getDeploymentErrorStrategy();
			if (errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
				throw new DeploymentException(errMsg, lcme);
			}
		} finally {
			Accounting.endMeasure(tagName);
			deploymentItem.finishTimeStatEntry();
		}
	}

	protected void performDelivery(DeploymentItem deploymentItem,
			DeploymentData deploymentData) throws DeploymentException {
		deploymentItem.startTimeStatEntry("Perform delivery",
				TimeStatisticsEntry.ENTRY_TYPE_DELIVERY);
		final String tagName = "Perform delivery:" + deploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, OnlineDeployProcessor.class);
		try {
			long begin = System.currentTimeMillis();
			if (location.beDebug()) {
				traceDebug(location, "Going to deliver: [{0}]",
						new Object[] { deploymentItem.getSdu().getId() });
			}
			GenericDelivery gd = getGenericDelivery(deploymentData);
			try {
				gd.deploy(deploymentItem);
			} finally {
				if (location.bePath()) {
					tracePath(location,
							"After Delivery: [{0}] ,Delivery time: [{1}] ms.",
							new Object[] { deploymentItem,
									(System.currentTimeMillis() - begin) });
				}
			}
			if (!DeploymentStatus.WARNING.equals(deploymentItem
					.getDeploymentStatus())) {
				deploymentItem.setDeploymentStatus(DeploymentStatus.DELIVERED);
			}

		} catch (DeliveryException de) {
			deploymentItem.setDeploymentStatus(DeploymentStatus.ABORTED);
			deploymentItem.addDescription(de);

			String errMsg = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.001084",
							"An error occurred while deploying the deployment item [{0}].",
							new Object[] { deploymentItem.getBatchItemId() });

			DCLog.logErrorThrowable(location,null, errMsg, de);
			final ErrorStrategy errorStrategy = deploymentData
					.getDeploymentErrorStrategy();
			if (errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
				errMsg = DCLog
						.buildExceptionMessage(
								"ASJ.dpl_dc.001085",
								"An error occurred while deploying the deployment item [{0}].{1}",
								new Object[] { deploymentItem.getBatchItemId(),
										Constants.EOL });
				throw new DeploymentException(errMsg, de);
			}
		} finally {
			Accounting.endMeasure(tagName);
			deploymentItem.finishTimeStatEntry();
		}
	}

	private void performDelivery(
			CompositeDeploymentItem compositeDeploymentItem,
			DeploymentData deploymentData) throws DeploymentException {
		try {
			final GenericDelivery gd = getGenericDelivery(deploymentData);
			if (location.bePath()) {
				tracePath(location,
						"Delivering: [{0}]",
						new Object[] { compositeDeploymentItem.getSdu().getId() });
			}
			gd.deploy(compositeDeploymentItem);

			logInfo(location, "ASJ.dpl_dc.001087",
					"Component has been delivered: [{0}]",
					new Object[] { compositeDeploymentItem });

			if (!DeploymentStatus.WARNING.equals(compositeDeploymentItem
					.getDeploymentStatus())) {
				compositeDeploymentItem
						.setDeploymentStatus(DeploymentStatus.DELIVERED);
			}
		} catch (DeliveryException de) {
			compositeDeploymentItem
					.setDeploymentStatus(DeploymentStatus.ABORTED);
			compositeDeploymentItem.addDescription(de);

			DCLog
					.logError(location,
							"ASJ.dpl_dc.001088",
							"An error occurred while deploying the composite deployment item [{0}].",
							new Object[] { compositeDeploymentItem });

			final ErrorStrategy errorStrategy = deploymentData
					.getDeploymentErrorStrategy();
			if (ErrorStrategy.ON_ERROR_STOP.equals(errorStrategy)) {
				DeploymentException dex = new DeploymentException(
										DCLogResourceAccessor
										.getInstance()
										.getMessageText(
												DCLogConstants.DEPLOY_ERROR_WHILE_DEPLOY_COMPOSITE_ITEM,
												new Object[] { compositeDeploymentItem }),
						de);
				dex.setMessageID("ASJ.dpl_dc.003079");
				throw dex;
			}
		}
	}

	private GenericDelivery getGenericDelivery(DeploymentData deploymentData)
			throws DeliveryException, RollingDeliveryException {
		GenericDelivery gd;
		if (deploymentData.getDeployWorkflowStrategy().equals(
				DeployWorkflowStrategy.ROLLING)) {
			gd = GDFactory.getInstance().createGenericDelivery(
					DeliveryType.ROLLING);
		} else {
			gd = GDFactory.getInstance().createGenericDelivery(
					DeliveryType.NORMAL);
		}
		return gd;
	}

}
