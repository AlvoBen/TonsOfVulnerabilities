package com.sap.engine.services.dc.event.msg.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.event.msg.MessageEventDeploymentBatchItem;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.Constants;

final class MessageEventDeploymentBatchItemImpl implements
		MessageEventDeploymentBatchItem {

	private static final long serialVersionUID = 3441389152188672908L;

	private static final String errorMsg = "[ERROR CODE DPL.DC.3454] Method is not supported for message event item.";

	private final BatchItemId batchItemId;
	private final ClusterDescriptor clusterDescriptor;
	private final DeploymentStatus deploymentStatus;
	private final String description;
	private final Sdu oldSdu;
	private final Map properties;
	private final Sdu sdu;
	private final String sduFilePath;
	private final VersionStatus versionStatus;
	private final com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[] timeStatisticsEntries;
	private String toString;

	MessageEventDeploymentBatchItemImpl(DeploymentBatchItem deployItem) {

		this.batchItemId = deployItem.getBatchItemId();
		this.clusterDescriptor = deployItem.getClusterDescriptor();
		this.deploymentStatus = deployItem.getDeploymentStatus();
		this.description = deployItem.getDescription();
		this.oldSdu = deployItem.getOldSdu();
		this.properties = deployItem.getProperties();
		this.sdu = deployItem.getSdu();
		this.sduFilePath = deployItem.getSduFilePath();
		this.versionStatus = deployItem.getVersionStatus();
		this.timeStatisticsEntries = deployItem.getTimeStatisticEntries();
	}

	public void addDescription(String description) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void addDescription(Throwable throwable) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void deserializeTimeStatisticsFromStream(InputStream is)
			throws NumberFormatException, IOException {
		throw new UnsupportedOperationException(errorMsg);
	}

	public TimeStatisticsEntry finishTimeStatEntry() {
		throw new UnsupportedOperationException(errorMsg);
	}

	public BatchItemId getBatchItemId() {
		return this.batchItemId;
	}

	public ClusterDescriptor getClusterDescriptor() {
		return this.clusterDescriptor;
	}

	public DeploymentStatus getDeploymentStatus() {
		return this.deploymentStatus;
	}

	public String getDescription() {
		return this.description;
	}

	public Sdu getOldSdu() {
		return this.oldSdu;
	}

	public Map getProperties() {
		return this.properties;
	}

	public Sdu getSdu() {
		return this.sdu;
	}

	public String getSduFilePath() {
		return this.sduFilePath;
	}

	public com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[] getTimeStatisticEntries() {
		return this.timeStatisticsEntries;
	}

	public VersionStatus getVersionStatus() {
		return this.versionStatus;
	}

	public InputStream serializeTimeStatisticsAsStream() {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setClusterDescriptor(ClusterDescriptor arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setDeploymentStatus(DeploymentStatus arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setDescription(String arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setOldSdu(Sdu arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setProperties(Map arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void setVersionStatus(VersionStatus arg0) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public TimeStatisticsEntry startTimeStatEntry(String entryName,
			int entryType) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public void accept(DeploymentBatchItemVisitor visitor) {
		throw new UnsupportedOperationException(errorMsg);
	}

	public String toString() {

		if (this.toString == null) {
			buildToString();
		}
		return this.toString;
	}

	private void buildToString() {

		final StringBuffer sbTosTring = new StringBuffer();
		sbTosTring.append("sdu id: ").append(getBatchItemId().toString())
				.append(Constants.EOL).append("sdu file path: ").append(
						getSduFilePath()).append(Constants.EOL).append(
						"version status: ").append(getVersionStatus()).append(
						Constants.EOL).append("deployment status: ").append(
						getDeploymentStatus()).append(Constants.EOL);

		if (this.description != null && this.description.length() > 0) {
			sbTosTring.append("description: ").append(this.description);
		}

		this.toString = sbTosTring.toString();

	}
}
