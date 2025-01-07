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
import java.util.Set;

import com.sap.engine.services.deploy.server.utils.DSConstants;

/**
 * Describes the status of the rolling patch per instance.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class InstanceDescriptor implements Serializable {

	private static final long serialVersionUID = 8729757813473401646L;

	private final Set<ServerDescriptor> serverDescriptors;
	private final DSRollingStatus dsRollingStatus;
	private final int instanceID;

	private final int hashCode;
	private final StringBuilder toString;

	/**
	 * Constructs <code>InstanceDescriptor</code> based on given server set of
	 * server descriptors
	 * 
	 * @param serverDescriptors
	 */
	public InstanceDescriptor(Set<ServerDescriptor> serverDescriptors) {
		this.serverDescriptors = serverDescriptors;
		this.dsRollingStatus = evaluateDSRollingStatus();
		this.instanceID = evaluateInstanceID();

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToStsring();
	}

	/**
	 * Gets the server descriptors per this instance descriptor
	 * 
	 * @return a set of <code>ServerDescriptor</code>s.
	 */
	public Set<ServerDescriptor> getServerDescriptors() {
		return serverDescriptors;
	}

	/**
	 * Gets the <code>DSRollingStatus</code> per this Instance.
	 * 
	 * @return the rolling status
	 */
	public DSRollingStatus getDSRollingStatus() {
		return dsRollingStatus;
	}

	/**
	 * Gets the Instance ID
	 * 
	 * @return Instance ID as int
	 */
	public int getInstanceID() {
		return instanceID;
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

		final InstanceDescriptor otherInstanceDescriptor = (InstanceDescriptor) obj;
		if (!this.getServerDescriptors().equals(
				otherInstanceDescriptor.getServerDescriptors())) {
			return false;
		}
		if (!this.getDSRollingStatus().equals(
				otherInstanceDescriptor.getDSRollingStatus())) {
			return false;
		}
		if (this.getInstanceID() != otherInstanceDescriptor.getInstanceID()) {
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

		int result = offset + getServerDescriptors().hashCode();
		result += result * multiplier + getDSRollingStatus().hashCode();
		result += result * multiplier + getInstanceID();

		return result;
	}

	private StringBuilder evaluateToStsring() {
		final StringBuilder sb = new StringBuilder();
		sb.append("ServerDescriptors = ");
		sb.append(getServerDescriptors());
		sb.append(DSConstants.EOL);
		sb.append("DSRollingStatus = ");
		sb.append(getDSRollingStatus());
		sb.append(DSConstants.EOL);
		sb.append("InstanceID = ");
		sb.append(getInstanceID());
		sb.append(DSConstants.EOL);
		return sb;
	}

	private DSRollingStatus evaluateDSRollingStatus() {
		DSRollingStatus dsRollingStatus = null;
		for (ServerDescriptor serverDescriptor : serverDescriptors) {
			if (dsRollingStatus == null
					|| dsRollingStatus.compareTo(serverDescriptor
							.getDSRollingStatus()) < 0) {
				dsRollingStatus = serverDescriptor.getDSRollingStatus();
			}
		}
		return dsRollingStatus;
	}

	public int evaluateInstanceID() {
		int instanceID = -1;
		for (ServerDescriptor serverDescriptor : serverDescriptors) {
			if (instanceID == -1) {
				instanceID = serverDescriptor.getInstanceID();
			} else if (instanceID != serverDescriptor.getInstanceID()) {
				throw new IllegalStateException(
						"ASJ.dpl_ds.006221 The group identifiers in the server descriptors for this instance are not equal.");
			}
		}
		return instanceID;
	}

	// ************************** PRIVATE **************************//

}
