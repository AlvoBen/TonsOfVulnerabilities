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

import com.sap.engine.services.dc.api.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.api.dscr.InstanceStatus;
import com.sap.engine.services.dc.api.dscr.TestInfo;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * <code>InstanceDescriptor</code> implementation.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
final class InstanceDescriptorImpl implements InstanceDescriptor {

	private final Set serverDescriptors;
	private final InstanceStatus instanceStatus;
	private final int instanceID;
	private final TestInfo testInfo;
	private final String description;

	private final int hashCode;
	private final StringBuffer toString;

	InstanceDescriptorImpl(Set serverDescriptors,
			InstanceStatus instanceStatus, int instanceID, TestInfo testInfo,
			String description) {
		this.serverDescriptors = serverDescriptors;
		this.instanceStatus = instanceStatus;
		this.instanceID = instanceID;
		this.testInfo = testInfo;
		this.description = description;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToString();
	}

	public Set getServerDescriptors() {
		return serverDescriptors;
	}

	public InstanceStatus getInstanceStatus() {
		return instanceStatus;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public TestInfo getTestInfo() {
		return testInfo;
	}

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

		final InstanceDescriptor otherInstanceDescriptor = (InstanceDescriptor) obj;
		/*
		 * if ( !this.getServerDescriptors().equals(otherInstanceDescriptor.
		 * getServerDescriptors() )) { return false; } if ( !
		 * this.getInstanceStatus
		 * ().equals(otherInstanceDescriptor.getInstanceStatus() )) { return
		 * false; }
		 */
		if (this.getInstanceID() != otherInstanceDescriptor.getInstanceID()) {
			return false;
		}
		/*
		 * if ( !
		 * this.getTestInfo().equals(otherInstanceDescriptor.getTestInfo() )) {
		 * return false; } if ( !
		 * this.getDescription().equals(otherInstanceDescriptor
		 * .getDescription()) ) { return false; }
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
		/*
		 * final int multiplier = 59; int result = offset +
		 * getServerDescriptors().hashCode(); result += resultmultiplier +
		 * getInstanceStatus().hashCode(); result += resultmultiplier +
		 * getInstanceID(); result += resultmultiplier +
		 * getTestInfo().hashCode(); result += resultmultiplier +
		 * getDescription().hashCode();
		 */

		int result = offset + getInstanceID();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(DAConstants.EOL_INDENT_INDENT_INDENT);
		sb.append("ServerDescriptors = ");
		sb.append(getServerDescriptors());
		sb.append(DAConstants.EOL_INDENT_INDENT_INDENT);
		sb.append("InstanceStatus = ");
		sb.append(getInstanceStatus());
		sb.append(DAConstants.EOL_INDENT_INDENT_INDENT);
		sb.append("InstanceID = ");
		sb.append(getInstanceID());
		sb.append(DAConstants.EOL_INDENT_INDENT_INDENT);
		sb.append("TestInfo = ");
		sb.append(getTestInfo());
		sb.append(DAConstants.EOL_INDENT_INDENT_INDENT);
		sb.append("Description = ");
		sb.append(getDescription());
		return sb;
	}

	// ************************** PRIVATE **************************//

}
