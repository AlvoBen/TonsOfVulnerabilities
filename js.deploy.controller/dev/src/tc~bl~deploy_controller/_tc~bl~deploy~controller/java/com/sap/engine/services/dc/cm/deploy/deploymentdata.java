package com.sap.engine.services.dc.cm.deploy;

import java.util.Collection;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.util.lock.LockData;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-9
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface DeploymentData {

	public Collection getSortedDeploymentBatchItem();

	public DeploymentBatch getDeploymentBatch();

	public String getSessionId();

	public Collection getDeploymentObservers();

	public void addDeploymentObserver(DeploymentObserver observer);

	public ErrorStrategy getDeploymentErrorStrategy();

	public DeployWorkflowStrategy getDeployWorkflowStrategy();

	public DeployParallelismStrategy getDeployParallelismStrategy();

	public LifeCycleDeployStrategy getLifeCycleDeployStrategy();

	public void clear();

	public DeployListenersList getDeployListenersList();

	public String getUserUniqueId();

	public String getCallerHost();

	public void setDescription(String aDescription);

	public String getDescription();

	public boolean getTimeStatsEnabled();

	public Set<InstanceData> getInstancesData();

	public void addInstanceData(InstanceData instanceData);

	public void removeInstanceData(InstanceData instanceData);

	public LockData getLockData();

	public String getOsUserName();

	public String getOsUserPass();

	public DataMeasurements getMeasurements();
}
