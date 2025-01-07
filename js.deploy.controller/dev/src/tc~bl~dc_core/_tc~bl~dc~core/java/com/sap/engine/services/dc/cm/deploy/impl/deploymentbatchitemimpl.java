package com.sap.engine.services.dc.cm.deploy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemObserver;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsFactory;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.StringUtils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class DeploymentBatchItemImpl implements DeploymentBatchItem {

	private static final long serialVersionUID = 4054352746262624218L;

	private final BatchItemId batchItemId;
	private final Sdu sdu;
	private final String sduFilePath;

	private DeploymentStatus deploymentStatus = DeploymentStatus.INITIAL;
	private VersionStatus versionStatus = VersionStatus.NOT_RESOLVED;
	private String description = "";
	private Sdu oldSdu;
	private Map propsMap = new HashMap(0);// $JL-SER$
	private int descrCount = 0;
	private TimeStatisticsEntry timeStatistics;
	private final transient Stack timeStatStack;

	private ClusterDescriptor clusterDescriptor;

	private transient final Set deploymentBatchItemObserverSet = new HashSet();

	private DeploymentBatchItemImpl(Sdu sdu, BatchItemId batchItemId,
			String sduFilePath, boolean enableTimeStats) {
		this.sdu = sdu;
		this.sduFilePath = sduFilePath;
		this.batchItemId = batchItemId;
		if (enableTimeStats) {
			this.timeStatStack = new Stack();
			setTimeStatWrapper(TimeStatisticsFactory.getInstance()
					.createTimeStatisticEntry("",
							TimeStatisticsEntry.ENTRY_TYPE_GLOBAL));
		} else {
			this.timeStatStack = null;
		}
	}

	DeploymentBatchItemImpl(BatchItemId batchItemId, String sduFilePath,
			boolean enableTimeStats) {
		this(null, batchItemId, sduFilePath, enableTimeStats);
		/*
		 * this.sdu = null; this.sduFilePath = sduFilePath; this.batchItemId =
		 * batchItemId;
		 */
	}

	DeploymentBatchItemImpl(Sdu sdu, String sduFilePath, boolean enableTimeStats) {
		this(sdu, new BatchItemIdImpl(sdu.getId()), sduFilePath,
				enableTimeStats);
		/*
		 * this.sdu = sdu; this.sduFilePath = sduFilePath; this.batchItemId =
		 * new BatchItemIdImpl( sdu.getId() );
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#getBatchItemId()
	 */
	public BatchItemId getBatchItemId() {
		return this.batchItemId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#getSdu()
	 */
	public Sdu getSdu() {
		return this.sdu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#getSduFilePath()
	 */
	public String getSduFilePath() {
		return this.sduFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#
	 * setDeploymentItemVersionStatus
	 * (com.sap.engine.services.dc.cm.deploy.VersionStatus)
	 */
	public void setVersionStatus(VersionStatus versionStatus) {
		this.versionStatus = versionStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#
	 * getDeploymentItemVersionStatus()
	 */
	public VersionStatus getVersionStatus() {
		return this.versionStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#
	 * getDeploymentItemStatus()
	 */
	public DeploymentStatus getDeploymentStatus() {
		return this.deploymentStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#
	 * setDeploymentItemStatus
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemStatus)
	 */
	public void setDeploymentStatus(DeploymentStatus deploymentStatus) {

		DeploymentStatus oldStatus = this.deploymentStatus;
		this.deploymentStatus = deploymentStatus;

		if (deploymentBatchItemObserverSet == null) {
			throw new IllegalStateException(
					"Remote invocation of the method is forbidden");
		} else {
			Iterator observers = deploymentBatchItemObserverSet.iterator();
			while (observers.hasNext()) {
				((DeploymentBatchItemObserver) observers.next()).statusChanged(
						this, oldStatus, deploymentStatus);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		if (description == null
				|| (description = description.trim()).length() == 0) {
			return;
		}
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#addDescription
	 * (java.lang.String)
	 */
	public void addDescription(String description) {
		if (description == null
				|| (description = description.trim()).length() == 0) {
			return;
		}
		StringBuffer buffer = new StringBuffer(128);
		if (this.description != null && this.description.trim().length() > 0) {
			buffer.append(this.description);
		}
		buffer.append(Constants.EOL_TAB_TAB).append(++this.descrCount).append(
				". ").append(description);
		this.description = buffer.toString();
	}

	public void addDescription(Throwable th) {
	String desc = StringUtils.getCauseMessage(th);
    if (desc != null) {
      addDescription(desc);
	}
	}

	public ClusterDescriptor getClusterDescriptor() {
		return clusterDescriptor;
	}

	public void setClusterDescriptor(ClusterDescriptor clusterDescriptor) {
		this.clusterDescriptor = clusterDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#getOldSdu()
	 */
	public Sdu getOldSdu() {
		return this.oldSdu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#setOldSdu(com
	 * .sap.engine.services.dc.repo.Sdu)
	 */
	public void setOldSdu(Sdu sdu) {
		this.oldSdu = sdu;
	}

	public Map getProperties() {
		return this.propsMap;
	}

	public void setProperties(Map propsMap) {
		this.propsMap.clear();
		this.propsMap.putAll(propsMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem#accept(com.sap
	 * .engine.services.dc.cm.deploy.DeploymentBatchItemVisitor)
	 */
	public abstract void accept(DeploymentBatchItemVisitor visitor);

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		DeploymentBatchItem otherDeploymentItem = (DeploymentBatchItem) obj;

		if (!this.getBatchItemId().equals(otherDeploymentItem.getBatchItemId())) {
			return false;
		}

		if (!this.getSduFilePath().equals(otherDeploymentItem.getSduFilePath())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		final int offset = 17;
		final int multiplier = 59;
		int result = offset + this.getBatchItemId().hashCode();

		result = result * multiplier + this.getSduFilePath().hashCode();

		return result;
	}

	public String toString() {
		final StringBuffer sbTosTring = new StringBuffer();
		sbTosTring.append("sdu id: [").append(getBatchItemId().toString())
				.append("]").append(Constants.EOL).append("sdu file path: [")
				.append(getSduFilePath()).append("]").append(Constants.EOL)
				.append("version status: [").append(getVersionStatus()).append(
						"]").append(Constants.EOL).append(
						"deployment status: [").append(getDeploymentStatus())
				.append("]").append(Constants.EOL).append("description: [")
				.append(getDescription()).append("]").append(Constants.EOL);

		ClusterDescriptor descriptor = getClusterDescriptor();
		if (descriptor != null) {
			sbTosTring.append("clusterDescriptor: [").append(descriptor)
					.append("]").append(Constants.EOL);
		}

		return sbTosTring.toString();
	}

	public com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[] getTimeStatisticEntries() {
		return (com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[]) (this.timeStatistics != null ? this.timeStatistics
				.getTimeStatisticEntries()
				: null);
	}

	public void deserializeTimeStatisticsFromStream(InputStream iStream)
			throws NumberFormatException, IOException {
		if (!getTimeStatsEnabled()) {
			return;
		}
		setTimeStatWrapper(TimeStatisticsFactory.getInstance()
				.deserializeTimeStatisticsFromStream(iStream));
	}

	public InputStream serializeTimeStatisticsAsStream() {
		if (!getTimeStatsEnabled()) {
			return null;
		}
		return TimeStatisticsFactory.getInstance()
				.serializeTimeStatisticsAsStream(
						(TimeStatisticsEntry[]) getTimeStatisticEntries());
	}

	public TimeStatisticsEntry startTimeStatEntry(String entryName,
			int entryType) {
		if (!getTimeStatsEnabled()) {
			return null;
		}

		TimeStatisticsEntry entry = TimeStatisticsFactory.getInstance()
				.createTimeStatisticEntry(entryName, entryType);
		((TimeStatisticsEntry) this.timeStatStack.peek())
				.addTimeStatisticsEntry(entry);
		this.timeStatStack.push(entry);
		return entry;
	}

	public TimeStatisticsEntry finishTimeStatEntry() {
		if (!getTimeStatsEnabled()) {
			return null;
		}
		TimeStatisticsEntry ret = (TimeStatisticsEntry) this.timeStatStack
				.pop();
		if (ret.finish() == 0) {
			TimeStatisticsEntry parent = ((TimeStatisticsEntry) this.timeStatStack
					.peek());
			if (parent != null) {
				parent.removeTimeStatisticsEntry(ret);
			}
		}
		return ret;
	}

	private void setTimeStatWrapper(TimeStatisticsEntry wrapper) {
		this.timeStatStack.clear();
		if (wrapper != null) {
			this.timeStatistics = wrapper;
			this.timeStatStack.push(wrapper);
		}
	}

	private boolean getTimeStatsEnabled() {
		return (this.timeStatistics != null && this.timeStatStack != null);
	}

	public void addDeploymentBatchItemObserver(
			DeploymentBatchItemObserver observer) {
		deploymentBatchItemObserverSet.add(observer);
	}

	public void removeDeploymentBatchItemObserver(
			DeploymentBatchItemObserver observer) {
		deploymentBatchItemObserverSet.remove(observer);
	}

}
