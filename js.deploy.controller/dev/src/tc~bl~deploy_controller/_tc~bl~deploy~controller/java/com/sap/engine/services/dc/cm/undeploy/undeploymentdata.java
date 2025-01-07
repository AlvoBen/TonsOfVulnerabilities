package com.sap.engine.services.dc.cm.undeploy;

import java.util.Collection;

import com.sap.engine.services.dc.cm.ErrorStrategy;
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
public interface UndeploymentData {

	public Collection getSortedUndeploymentBatchItem();

	public void setSortedUndeploymentBatchItem(
			Collection sortedUndeploymentBatchItems);

	public UndeploymentBatch getUndeploymentBatch();

	public String getSessionId();

	public Collection getUndeploymentObservers();

	public void addUndeploymentObserver(UndeploymentObserver observer);

	public ErrorStrategy getUndeploymentErrorStrategy();

	public UndeploymentStrategy getUndeploymentStrategy();

	public UndeployWorkflowStrategy getUndeployWorkflowStrategy();

	public UndeployParallelismStrategy getUndeployParallelismStrategy();

	public void clear();

	public UndeployListenersList getUndeployListenersList();

	public String getUserUniqueId();
	
	public String getCallerHost();
	
	public DataMeasurements getMeasurements();
}
