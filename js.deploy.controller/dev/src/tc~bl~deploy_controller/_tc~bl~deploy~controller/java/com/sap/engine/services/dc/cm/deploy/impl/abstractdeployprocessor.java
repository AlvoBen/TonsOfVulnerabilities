package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.PerformanceUtil.isBoostPerformanceDisabled;
import static com.sap.engine.services.dc.util.ThreadUtil.popTask;
import static com.sap.engine.services.dc.util.ThreadUtil.pushTask;
import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeployListenersList;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentObserver;
import com.sap.engine.services.dc.cm.deploy.DeploymentObserverException;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description: The class should be thread safe.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class AbstractDeployProcessor {

	private final static String DEPLOY = "deploy";

	/**
	 * This collection is not thread safe but it is OK for now because it is
	 * used only during the operation after the restart which is single threaded
	 * 
	 * */
	private final Collection<DeployProcessorObserver> deployProcessorObservers;

	private  final Location location =  DCLog.getLocation(this.getClass());
	
	protected AbstractDeployProcessor() {
		this.deployProcessorObservers = new HashSet<DeployProcessorObserver>();
	}

	void addDeployProcessorObserver(DeployProcessorObserver observer) {
		this.deployProcessorObservers.add(observer);
	}

	void removeDeployProcessorObserver(DeployProcessorObserver observer) {
		this.deployProcessorObservers.remove(observer);
	}

	Collection getDeployProcessorObservers() {
		return this.deployProcessorObservers;
	}

	void deploy(DeploymentBatchItem deplBatchItem,
			DeploymentData deploymentData,
			DeployListenersList deployListenersList)
			throws DeploymentException {
		final String tagName = "deploy:" + deplBatchItem.getSdu().getId();
		Accounting.beginMeasure(tagName, AbstractDeployProcessor.class);
		try {
			deplBatchItem.startTimeStatEntry("deployment",
					TimeStatisticsEntry.ENTRY_TYPE_DEPLOY);

			RepositoryContainer.getCsnContainer().add(deplBatchItem.getSdu());

			long beginTime = System.currentTimeMillis();


			if (location.bePath()) {
				tracePath(location, 
					"+++++++ Deploying [{0}] +++++++",
					new Object[] { deplBatchItem.getBatchItemId() });
			}

			notifyForDeploymentTriggered(deplBatchItem, deployListenersList);

			// the helper is used as a visitor in order to dispatch to the right
			// method depending on the runtime
			// type of the deployment item
			final DeployProcessorHelper dplProcessorHelper = new DeployProcessorHelper(
					this, deploymentData);

			try {
				// actual deployment goes here
				deplBatchItem.accept(dplProcessorHelper);

				notifyDeployProcessorObservers(deplBatchItem, deploymentData);
				dplProcessorHelper.checkException();

			} finally {
				RepositoryContainer.getCsnContainer().remove(
						deplBatchItem.getSdu());

				notifyForDeploymentPerformed(deplBatchItem,
						deployListenersList, beginTime);
			}
		} finally {
			Accounting.endMeasure(tagName);
			deplBatchItem.finishTimeStatEntry();// always finish
		}
	}

	/**
	 * 
	 * This method notifies the registered deployment listeners for the event
	 * deployment performed. Subclasses should override this method if they want
	 * to customize the behavior
	 * 
	 * 
	 * @param deplBatchItem
	 * @param deployListenersList
	 * @param beginTime
	 */
	protected void notifyForDeploymentPerformed(
			DeploymentBatchItem deplBatchItem,
			DeployListenersList deployListenersList, long beginTime) {
		deplBatchItem.startTimeStatEntry("Notify:Event performed",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Notify for performed deployment:" + deplBatchItem.getSdu().getId();
		Accounting.beginMeasure(tagName, AbstractDeployProcessor.class);
		try {
			DeployListenersNotifier.getInstance().deploymentPerformed(
					deplBatchItem, deployListenersList, true);
		} finally {
			Accounting.endMeasure(tagName);
			deplBatchItem.finishTimeStatEntry();
		}
		
		logInfo(location, 
				"ASJ.dpl_dc.001013",
				"+++++++ Deployment of item [{0}] finished with [{1}] for [{2}] ms +++++++",
				new Object[] {
						deplBatchItem.getBatchItemId(),
						deplBatchItem.getDeploymentStatus(),
						String.valueOf(System.currentTimeMillis()
								- beginTime) });
		
	}

	private void notifyForDeploymentTriggered(
			DeploymentBatchItem deplBatchItem,
			DeployListenersList deployListenersList) {

		deplBatchItem.startTimeStatEntry("Notify:Event triggered",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Notify for triggered deployment:" + deplBatchItem.getSdu().getId();
		Accounting.beginMeasure(tagName, AbstractDeployProcessor.class);
		try {
			DeployListenersNotifier.getInstance().deploymentPerformed(
					deplBatchItem, deployListenersList, false);
		} finally {
			Accounting.endMeasure(tagName);
			deplBatchItem.finishTimeStatEntry();
		}
	}

	private void notifyDeployProcessorObservers(
			DeploymentBatchItem deplBatchItem, DeploymentData deploymentData) {
		deplBatchItem.startTimeStatEntry("Notify Observers",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Notify Observers:" + deplBatchItem.getSdu().getId();
		Accounting.beginMeasure(tagName, AbstractDeployProcessor.class);
		try {
			for (Iterator iter = this.deployProcessorObservers.iterator(); iter
					.hasNext();) {
				DeployProcessorObserver observer = (DeployProcessorObserver) iter
						.next();
				observer.deployPerformed(deplBatchItem, deploymentData);
			}
		} catch (Exception e) {//TODO consumed - is that ok?

			DCLog.logErrorThrowable(location, e);

		} finally {
			Accounting.endMeasure(tagName);
			deplBatchItem.finishTimeStatEntry();
		}
	}

	protected abstract void deploy(DeploymentItem deplItem,
			DeploymentData deploymentData) throws DeploymentException;

	protected abstract void deploy(
			CompositeDeploymentItem compositeDeploymentItem,
			DeploymentData deploymentData) throws DeploymentException;

	protected void notifyObservers(DeploymentBatchItem deploymentBatchItem,
			DeploymentData deploymentData) throws DeploymentException {
		deploymentBatchItem.startTimeStatEntry("Notify Observers",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Notify Observers:" + deploymentBatchItem.getSdu().getId();
		Accounting.beginMeasure(tagName, AbstractDeployProcessor.class);
		try {
			for (Iterator iter = deploymentData.getDeploymentObservers()
					.iterator(); iter.hasNext();) {
				final DeploymentObserver observer = (DeploymentObserver) iter
						.next();
				try {
					observer.deployPerformed(deploymentBatchItem, deploymentData.getSortedDeploymentBatchItem());
					if (location.bePath()) {
						tracePath(location, 
								"Observer [{0}] has been notified. Component:[{1}].",
								new Object[] { observer,
										deploymentBatchItem.getSdu().getId() });
					}
				} catch (DeploymentObserverException doe) {
					// TODO: DECIDE WHAT TO DO:
					// IN CASE OF CompVers OBSERVER IF AN ERROR OCCURED SHOULD
					// WE UNDEPLOY THE
					// CURRENTLY DEPLOYED SDU?

					final String errMsg = DCLog
							.buildExceptionMessage(
									"ASJ.dpl_dc.006504",
									"An error occurred while notifying the registered deployment observer [{0}] after the component [{1}] has been deployed.",
									new Object[] { observer,
											deploymentBatchItem });
					DCLog.logErrorThrowable(location, null, errMsg, doe);
					throw new DeploymentException(errMsg, doe);
				}
			}
		} finally {
			Accounting.endMeasure(tagName);
			deploymentBatchItem.finishTimeStatEntry();
		}
	}

	private static class DeployProcessorHelper implements
			DeploymentBatchItemVisitor {

		private final AbstractDeployProcessor deployProcessor;
		private final DeploymentData deploymentData;
		private DeploymentException deploymentException;

		private DeployProcessorHelper(AbstractDeployProcessor _deployProcessor,
				DeploymentData _deploymentData) {
			this.deployProcessor = _deployProcessor;
			this.deploymentData = _deploymentData;
		}

		void checkException() throws DeploymentException {
			if (this.deploymentException != null) {
				throw this.deploymentException;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(DeploymentItem deploymentItem) {
			this.deploymentException = null;

			try {
				if (isBoostPerformanceDisabled()) {
					pushTask(DEPLOY, deploymentItem.getSdu().getName(),
							deploymentItem.getSdu().getVendor());
				}

				this.deployProcessor
						.deploy(deploymentItem, this.deploymentData);
			} catch (DeploymentException de) {
				this.deploymentException = de;
			} finally {
				if (isBoostPerformanceDisabled()) {
					popTask();
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(CompositeDeploymentItem compositeDeploymentItem) {
			this.deploymentException = null;

			try {
				if (isBoostPerformanceDisabled()) {
					pushTask(DEPLOY,
							compositeDeploymentItem.getSdu().getName(),
							compositeDeploymentItem.getSdu().getVendor());
				}

				this.deployProcessor.deploy(compositeDeploymentItem,
						this.deploymentData);
			} catch (DeploymentException de) {
				this.deploymentException = de;
			} finally {
				if (isBoostPerformanceDisabled()) {
					popTask();
				}
			}
		}

	}

}
