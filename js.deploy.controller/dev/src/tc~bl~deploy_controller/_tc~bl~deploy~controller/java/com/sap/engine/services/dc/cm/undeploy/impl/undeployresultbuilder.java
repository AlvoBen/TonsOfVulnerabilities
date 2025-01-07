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
package com.sap.engine.services.dc.cm.undeploy.impl;

import com.sap.engine.services.dc.cm.undeploy.UndeployResult;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentData;
import com.sap.engine.services.dc.cm.utils.measurement.MeasurementUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class UndeployResultBuilder {

	private static final UndeployResultBuilder INSTANCE = new UndeployResultBuilder();

	private UndeployResultBuilder() {
	}

	static UndeployResultBuilder getInstance() {
		return INSTANCE;
	}

	UndeployResult build(final UndeploymentData udData) {
		final UndeploymentBatch undeploymentBatch = udData
				.getUndeploymentBatch();
		return new UndeployResultImpl(undeploymentBatch.getUndeployItems(),
				undeploymentBatch.getOrderedUndeployItems(), MeasurementUtils
						.build(udData.getMeasurements(), udData.getSessionId()));
	}

}
