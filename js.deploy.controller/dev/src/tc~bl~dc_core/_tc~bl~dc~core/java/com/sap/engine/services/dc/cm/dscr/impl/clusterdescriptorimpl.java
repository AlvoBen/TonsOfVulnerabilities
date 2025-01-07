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
package com.sap.engine.services.dc.cm.dscr.impl;

import java.util.Set;

import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.dscr.ClusterStatus;
import com.sap.engine.services.dc.cm.dscr.RollingInfo;
import com.sap.engine.services.dc.util.Constants;

/**
 * <code>ClusterDescriptor</code> implementation.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * 
 */
final class ClusterDescriptorImpl implements ClusterDescriptor {

	private static final long serialVersionUID = 7632443016953945218L;

	private final Set instanceDescriptors;
	private final ClusterStatus clusterStatus;
	private final RollingInfo rollingInfo;

	private final int hashCode;
	private final StringBuffer toString;

	ClusterDescriptorImpl(Set instanceDescriptors, ClusterStatus clusterStatus,
			RollingInfo rollingInfo) {
		this.instanceDescriptors = instanceDescriptors;
		this.clusterStatus = clusterStatus;
		this.rollingInfo = rollingInfo;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToString();
	}

	public Set getInstanceDescriptors() {
		return instanceDescriptors;
	}

	public ClusterStatus getClusterStatus() {
		return clusterStatus;
	}

	public RollingInfo getRollingInfo() {
		return rollingInfo;
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
		/*
		 * if ( !
		 * this.getRollingInfo().equals(otherClusterDescriptor.getRollingInfo())
		 * ) { return false; }
		 */

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
		// result += result*multiplier + getRollingInfo().hashCode();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(Constants.EOL_TAB);
		sb.append("InstanceDescriptors = ");
		sb.append(getInstanceDescriptors());
		sb.append(Constants.EOL_TAB);
		sb.append("ClusterStatus = ");
		sb.append(getClusterStatus());
		sb.append(Constants.EOL_TAB);
		sb.append("RollingInfo = ");
		sb.append(getRollingInfo());
		return sb;
	}

	// ************************** PRIVATE **************************//

}
