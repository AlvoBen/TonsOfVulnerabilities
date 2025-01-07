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
package com.sap.engine.services.dc.api.undeploy.impl;

import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-4
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public abstract class UndeployItemImpl implements UndeployItem {
	private String name;
	private String vendor;
	private String version;
	private String location;
	private UndeployItemStatus undeployItemStatus = UndeployItemStatus.INITIAL;
	private String description = "";
	private String toString = "";
	private boolean generateToString = true;

	protected UndeployItemImpl(String name, String vendor) {
		this(name, vendor, null, null);
	}

	protected UndeployItemImpl(String name, String vendor, String location,
			String version) {
		this.name = name;
		this.vendor = vendor;
		this.location = location;
		this.version = version;
		this.generateToString = true;
	}

	public String getName() {
		return this.name;
	}

	public String getVendor() {
		return this.vendor;
	}

	public String getLocation() {
		return this.location;
	}

	public String getVersion() {
		return this.version;
	}

	public UndeployItemStatus getUndeployItemStatus() {
		return this.undeployItemStatus;
	}

	public void setLocation(String location) {
		this.location = location;
		this.generateToString = true;
	}

	public void setVersion(String version) {
		this.version = version;
		this.generateToString = true;
	}

	public void setUndeployItemStatus(UndeployItemStatus undeployItemStatus) {
		this.undeployItemStatus = undeployItemStatus;
		this.generateToString = true;
	}

	public String toString() {
		if (this.generateToString) {
			this.toString = "[name=" + this.name + DAConstants.EOL_INDENT
					+ ",vendor=" + this.vendor + DAConstants.EOL_INDENT
					+ ",location=" + this.location + DAConstants.EOL_INDENT
					+ ",version=" + this.version + DAConstants.EOL_INDENT
					+ ",undeployItemStatus=" + this.undeployItemStatus
					+ DAConstants.EOL_INDENT + "]";
			this.generateToString = false;
		}
		return this.toString;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
		this.generateToString = false;
	}
}