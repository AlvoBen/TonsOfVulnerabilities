/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.zdm.utils;

import java.io.Serializable;

import com.sap.engine.services.deploy.server.utils.DSConstants;

/**
 * Describes the status of the rolling patch per server node.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class ServerDescriptor implements Serializable {

	private static final long serialVersionUID = 6829221375135895597L;

	private final DSRollingStatus dsRollingStatus;
	private final int clusterID;
	private final int instanceID;
	private final String description;

	private final int hashCode;
	private final StringBuilder toString;

	/**
	 * Constructs <code>ServerDescriptor</code> object with given
	 * DSRollingStatus, cluster ID, instance ID and description.
	 * 
	 * @param dsRollingStatus
	 *            the rolling status
	 * @param clusterID
	 *            id of the given cluster
	 * @param instanceID
	 *            id of the given instance
	 * @param description
	 *            a description as string
	 */
	public ServerDescriptor(DSRollingStatus dsRollingStatus, int clusterID,
			int instanceID, String description) {
		this.dsRollingStatus = dsRollingStatus;
		this.clusterID = clusterID;
		this.instanceID = instanceID;
		this.description = description;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToStsring();
	}

	/**
	 * Gets the DSRollingStatus per this server node.
	 * 
	 * @return the rolling status
	 */
	public DSRollingStatus getDSRollingStatus() {
		return dsRollingStatus;
	}

	/**
	 * Gets the cluster ID per this server node.
	 * 
	 * @return cluster ID as int
	 */
	public int getClusterID() {
		return clusterID;
	}

	/**
	 * Gets the instance ID per this server node.
	 * 
	 * @return instance ID as int
	 */
	public int getInstanceID() {
		return instanceID;
	}

	/**
	 * Gets description for this server node.
	 * 
	 * @return description as string
	 */
	public String getDescription() {
		return description;
	}

	// ************************** OBJECT **************************//

	public int hashCode() {
		return hashCode;
	}

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

		final ServerDescriptor otherServerDescriptor = (ServerDescriptor) obj;
		if (!this.getDSRollingStatus().equals(
				otherServerDescriptor.getDSRollingStatus())) {
			return false;
		}
		if (this.getClusterID() != otherServerDescriptor.getClusterID()) {
			return false;
		}
		if (this.getInstanceID() != otherServerDescriptor.getInstanceID()) {
			return false;
		}
		if (!this.getDescription().equals(
				otherServerDescriptor.getDescription())) {
			return false;
		}

		return true;
	}

	public String toString() {
		return toString.toString();
	}

	// ************************** OBJECT **************************//

	// ************************** PRIVATE **************************//

	public int evaluateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getDSRollingStatus().hashCode();
		result += result * multiplier + getClusterID();
		result += result * multiplier + getInstanceID();
		result += result * multiplier + getDescription().hashCode();

		return result;
	}

	private StringBuilder evaluateToStsring() {
		final StringBuilder sb = new StringBuilder();
		sb.append("DSRollingStatus = ");
		sb.append(getDSRollingStatus());
		sb.append(DSConstants.EOL);
		sb.append("ClusterID = ");
		sb.append(getClusterID());
		sb.append(DSConstants.EOL);
		sb.append("InstanceID = ");
		sb.append(getInstanceID());
		sb.append(DSConstants.EOL);
		sb.append("Description = ");
		sb.append(getDescription());
		sb.append(DSConstants.EOL);
		return sb;
	}

	// ************************** PRIVATE **************************//

}
