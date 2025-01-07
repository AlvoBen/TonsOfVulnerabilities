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

import com.sap.engine.services.dc.cm.dscr.ItemStatus;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.util.Constants;

/**
 * <code>ServerDescriptor</code> implementation.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
final class ServerDescriptorImpl implements ServerDescriptor {

	private static final long serialVersionUID = 6324862264330895324L;

	private final ItemStatus itemStatus;
	private final int clusterID;
	private final int instanceID;
	private final String description;

	private final int hashCode;
	private final StringBuffer toString;

	ServerDescriptorImpl(ItemStatus itemStatus, int clusterID, int instanceID,
			String description) {
		this.itemStatus = itemStatus;
		this.clusterID = clusterID;
		this.instanceID = instanceID;
		this.description = description;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToString();
	}

	public ItemStatus getItemStatus() {
		return itemStatus;
	}

	public int getClusterID() {
		return clusterID;
	}

	public int getInstanceID() {
		return instanceID;
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

		final ServerDescriptor otherServerDescriptor = (ServerDescriptor) obj;
		if (this.getClusterID() != otherServerDescriptor.getClusterID()) {
			return false;
		}
		if (this.getInstanceID() != otherServerDescriptor.getInstanceID()) {
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

		int result = offset + getClusterID();
		result += result * multiplier + getInstanceID();

		return result;
	}

	private StringBuffer evaluateToString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(Constants.EOL_TAB_TAB_TAB);
		sb.append("itemStatus=");
		sb.append(getItemStatus());
		sb.append(",clusterID=");
		sb.append(getClusterID());
		sb.append(",instanceID=");
		sb.append(getInstanceID());
		sb.append(",description=");
		sb.append(getDescription());
		return sb;
	}

	// ************************** PRIVATE **************************//

}
