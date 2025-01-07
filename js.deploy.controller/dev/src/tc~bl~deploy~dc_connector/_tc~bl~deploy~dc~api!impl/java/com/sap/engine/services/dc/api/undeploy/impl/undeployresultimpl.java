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
import com.sap.engine.services.dc.api.undeploy.UndeployResult;
import com.sap.engine.services.dc.api.undeploy.UndeployResultStatus;
import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-8
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class UndeployResultImpl implements UndeployResult {
	private final UndeployResultStatus undeployResultStatus;
	private final UndeployItem[] undeployItems;
	private final UndeployItem[] orderedItems;
	private final String description;
	private String toString = null;
	private final DAMeasurement measurement;

	UndeployResultImpl(UndeployResultStatus undeployResultStatus,
			UndeployItem[] undeployItems, UndeployItem[] orderedItems,
			String description, DAMeasurement measurement) {
		this.orderedItems = orderedItems;
		this.undeployResultStatus = undeployResultStatus;
		this.undeployItems = undeployItems;
		this.description = description;
		this.measurement = measurement;
	}

	public UndeployResultStatus getUndeployStatus() {
		return this.undeployResultStatus;
	}

	public String getDescription() {
		return this.description;
	}
	
	public DAMeasurement getMeasurement() {
		return this.measurement;
	}

	public UndeployItem[] getUndeployItems() {
		return this.undeployItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.undeploy.UndeployResult#
	 * getOrderedUndeployItems()
	 */
	public UndeployItem[] getOrderedUndeployItems() {
		return this.orderedItems;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "UndeployResultImpl[undeployResultStatus="
					+ this.undeployResultStatus + ",description="
					+ this.description + "', measurement=" + this.measurement + "]";
		}
		return this.toString;
	}

}