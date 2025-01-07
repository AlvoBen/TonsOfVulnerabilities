package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.Collection;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeployListenersList;
import com.sap.engine.services.dc.cm.undeploy.UndeployParallelismStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentData;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class UndeploymentDataImpl implements UndeploymentData {

	private final Collection sortedUndeploymentBatchItems;
	private final UndeploymentBatch undeploymentBatch;
	private final String sessionId;
	private final Collection undeploymentObservers;
	private final ErrorStrategy undeploymentErrorStrategy;
	private final UndeploymentStrategy undeploymentStrategy;
	private final UndeployWorkflowStrategy workflowStrategy;
	private final UndeployParallelismStrategy undeployParallelismStrategy;
	private final UndeployListenersList undeployListenersList;
	private final String userUniqueId;
	private final String callerHost;
	private final DataMeasurements measurements;

	UndeploymentDataImpl(Collection sortedUndeploymentBatchItems,
			UndeploymentBatch undeploymentBatch, String sessionId,
			Collection undeploymentObservers,
			ErrorStrategy undeploymentErrorStrategy,
			UndeploymentStrategy undeploymentStrategy,
			UndeployWorkflowStrategy workflowStrategy,
			UndeployParallelismStrategy undeployParallelismStrategy,
			UndeployListenersList undeployListenersList,
			DataMeasurements measurements, String userUniqueId,
			String callerHost) {
		this.sortedUndeploymentBatchItems = sortedUndeploymentBatchItems;
		this.undeploymentBatch = undeploymentBatch;
		this.sessionId = sessionId;
		this.undeploymentObservers = undeploymentObservers;
		this.undeploymentErrorStrategy = undeploymentErrorStrategy;
		this.undeploymentStrategy = undeploymentStrategy;
		this.workflowStrategy = workflowStrategy;
		this.undeployParallelismStrategy = undeployParallelismStrategy;
		this.undeployListenersList = undeployListenersList;
		this.measurements = (measurements == null) ? new DataMeasurements()
				: measurements;
		this.userUniqueId = userUniqueId;
		this.callerHost = callerHost;
	}

	public DataMeasurements getMeasurements() {
		return measurements;
	}

	public String getUserUniqueId() {
		return this.userUniqueId;
	}

	public String getCallerHost() {
		return this.callerHost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * getSortedDeploymentBatchItem()
	 */
	public Collection getSortedUndeploymentBatchItem() {
		return this.sortedUndeploymentBatchItems;
	}

	public void setSortedUndeploymentBatchItem(Collection sortedUndeplBatchItems) {
		this.sortedUndeploymentBatchItems.clear();

		this.sortedUndeploymentBatchItems.addAll(sortedUndeplBatchItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeploymentData#getDeploymentBatch
	 * ()
	 */
	public UndeploymentBatch getUndeploymentBatch() {
		return this.undeploymentBatch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeploymentData#getSessionId()
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * getUndeploymentObservers()
	 */
	public Collection getUndeploymentObservers() {
		return this.undeploymentObservers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * addUndeploymentObserver
	 * (com.sap.engine.services.dc.cm.undeploy.UndeploymentObserver)
	 */
	public void addUndeploymentObserver(UndeploymentObserver observer) {
		this.undeploymentObservers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * getUndeploymentErrorStrategy()
	 */
	public ErrorStrategy getUndeploymentErrorStrategy() {
		return this.undeploymentErrorStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * getUndeploymentStrategy()
	 */
	public UndeploymentStrategy getUndeploymentStrategy() {
		return this.undeploymentStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * getUndeployWorkflowStrategy()
	 */
	public UndeployWorkflowStrategy getUndeployWorkflowStrategy() {
		return this.workflowStrategy;
	}

	public UndeployParallelismStrategy getUndeployParallelismStrategy() {
		return this.undeployParallelismStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeploymentData#clear()
	 */
	public void clear() {
		this.sortedUndeploymentBatchItems.clear();
		this.undeploymentBatch.clear();
		this.undeploymentObservers.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.undeploy.UndeploymentData#
	 * getUndeployListenersList()
	 */
	public UndeployListenersList getUndeployListenersList() {
		return this.undeployListenersList;
	}

}
