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
package com.sap.engine.services.dc.api.dscr.impl;

import java.util.Set;

import com.sap.engine.services.dc.api.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.api.dscr.ClusterStatus;
import com.sap.engine.services.dc.api.dscr.ServerDescriptor;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * <code>ClusterDescriptor</code> implementation.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
final class ClusterDescriptorImpl implements ClusterDescriptor {

	private final Set instanceDescriptors;
	private final ClusterStatus clusterStatus;

	private final int hashCode;
	private final StringBuffer toString;

	ClusterDescriptorImpl(Set instanceDescriptors, ClusterStatus clusterStatus) {
		this.instanceDescriptors = instanceDescriptors;
		this.clusterStatus = clusterStatus;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToString();
	}

	public Set getInstanceDescriptors() {
		return instanceDescriptors;
	}

	public ClusterStatus getClusterStatus() {
		return clusterStatus;
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

		final ClusterDescriptor otherClusterDescriptor = (ClusterDescriptor) obj;
		if (!this.getInstanceDescriptors().equals(
				otherClusterDescriptor.getInstanceDescriptors())) {
			return false;
		}
		if (!this.getClusterStatus().equals(
				otherClusterDescriptor.getClusterStatus())) {
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

		int result = offset + getInstanceDescriptors().hashCode();
		result += result * multiplier + getClusterStatus().hashCode();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(DAConstants.EOL_INDENT_INDENT);
		sb.append("InstanceDescriptors = ");
		sb.append(getInstanceDescriptors());
		sb.append(DAConstants.EOL_INDENT_INDENT);
		sb.append("ClusterStatus = ");
		sb.append(getClusterStatus());
		return sb;
	}

	// ************************** PRIVATE **************************//

}
