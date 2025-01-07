/*
 * Created on Oct 17, 2004
 *
 */
package com.sap.engine.services.dc.api.deploy.impl;

import java.io.File;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployItemStatus;
import com.sap.engine.services.dc.api.deploy.DeployItemVersionStatus;
import com.sap.engine.services.dc.api.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.util.DAConstants;
import com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry;

/**
 * Deploy Item implementation
 * 
 * @author Georgi Danov
 * @author Boris Savov
 */
public final class DeployItemImpl implements DeployItem {
	private final File sduFile;
	private Sdu sdu;
	private DeployItemVersionStatus versionStatus;
	private String description = "";
	private DeployItemStatus deployItemStatus = DeployItemStatus.INITIAL;
	private ClusterDescriptor clusterDescriptor;
	private String toString = "";
	private boolean generateToString = false;
	private DeployItem[] deployItems = null;// contained SDAs
	private TimeStatisticsEntry[] timeStatisticEntries;

	DeployItemImpl(String archiveLocation) {

		// check the input
		if (archiveLocation == null) {
			throw new IllegalArgumentException(
					"Archive location can not be null.");
		}

		File file = new File(archiveLocation);

		if (!file.exists()) {
			throw new IllegalArgumentException("Archive location '"
					+ archiveLocation + "' does not exist.");
		}

		if (!file.isFile()) {
			throw new IllegalArgumentException("Archive location '"
					+ archiveLocation + "' is not a file.");
		}

		if (!file.canRead()) {
			throw new IllegalArgumentException("Archive location '"
					+ archiveLocation + "' can not be read.");
		}

		this.generateToString = true;
		this.sduFile = file;
	}

	DeployItemImpl(Sdu sdu, String calculatedScaFilePath) {

		// check the input
		// if (sdu == null) {
		// throw new
		// IllegalArgumentException("Input parameter Sdu can not be null.");
		// }
		if (calculatedScaFilePath == null) {
			throw new IllegalArgumentException(
					"Input parameter calculated file path can not be null.");
		}

		this.sduFile = new File(calculatedScaFilePath);
		setSdu(sdu);
	}

	public File getArchive() {
		return this.sduFile;
	}

	public void setSdu(Sdu sdu) {
		this.sdu = sdu;
		this.generateToString = true;
	}

	public Sdu getSdu() {
		return this.sdu;
	}

	void setDeployItemStatus(DeployItemStatus deployItemStatus) {
		this.deployItemStatus = deployItemStatus;
		this.generateToString = true;
	}

	public DeployItemStatus getDeployItemStatus() {
		return this.deployItemStatus;
	}

	void setDescription(String description) {
		this.description = description;
		this.generateToString = true;
	}

	public String getDescription() {
		return this.description;
	}

	void setClusterDescriptor(ClusterDescriptor clusterDescriptor) {
		this.clusterDescriptor = clusterDescriptor;
		this.generateToString = true;
	}

	public ClusterDescriptor getClusterDescriptor() {
		return clusterDescriptor;
	}

	void setVersionStatus(DeployItemVersionStatus versionStatus) {
		this.versionStatus = versionStatus;
		this.generateToString = true;
	}

	public DeployItemVersionStatus getVersionStatus() {
		return this.versionStatus;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeployItemImpl)) {
			return false;
		}

		DeployItemImpl otherDeploymentItem = (DeployItemImpl) obj;
		if (!this.sduFile.equals(otherDeploymentItem.sduFile)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.sduFile.hashCode();
	}

	public String toString() {
		if (this.generateToString) {
			this.toString = "DeployItem[file=" + getArchive().getAbsolutePath()
					+ DAConstants.EOL_INDENT + ",deployItemStatus="
					+ getDeployItemStatus() + DAConstants.EOL_INDENT
					+ ",versionStatus=" + getVersionStatus()
					+ DAConstants.EOL_INDENT + ",description="
					+ getDescription() + DAConstants.EOL_INDENT + "sdu="
					+ getSdu() + DAConstants.EOL_INDENT + "clusterDescriptor="
					+ getClusterDescriptor() + DAConstants.EOL_INDENT + "]";
			this.generateToString = false;
		}
		return this.toString;
	}

	void setContainedDeployItems(DeployItem[] _deployItems) {
		this.deployItems = _deployItems;
		this.generateToString = true;
	}

	public DeployItem[] getContainedDeployItems() {
		return this.deployItems;
	}

	/**
	 * The method sets the time statistics obtained for the deployment
	 * operation.
	 * 
	 * @param timeStat
	 */
	public void setTimeStatistics(TimeStatisticsEntry[] timeStat) {
		this.timeStatisticEntries = timeStat;
	}

	/**
	 * This is an accessor method for the deployment time statistics.
	 */
	public TimeStatisticsEntry[] getTimeStatisticEntries() {
		return this.timeStatisticEntries;
	}
}