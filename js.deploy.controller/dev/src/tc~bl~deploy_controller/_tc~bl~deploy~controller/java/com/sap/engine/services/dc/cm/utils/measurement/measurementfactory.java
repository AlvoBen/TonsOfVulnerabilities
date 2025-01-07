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
package com.sap.engine.services.dc.cm.utils.measurement;

import java.util.Set;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: Apr 4, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public abstract class MeasurementFactory {
	private static MeasurementFactory INSTANCE = createFactory();
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.utils.measurement.impl.MeasurementFactoryImpl";

	public  static MeasurementFactory getInstance(){
		return INSTANCE;
	}

	private static MeasurementFactory createFactory() {
		try {
			return (MeasurementFactory) Class.forName(FACTORY_IMPL)
					.newInstance();
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DC.0000] An error occurred while creating an instance of "
					+ "class MeasurementFactoryImpl! " + Constants.EOL + e.getMessage();
			throw new RuntimeException(errMsg);
		}
	}
	
	public abstract void addChild(DMeasurement parentMeasurement,
			DMeasurement childMeasurement);

	public abstract DMeasurement createMeasurement(final String tagName, final String dcName,
			Set<DStatistic> statistics, Boolean hasNewThreadStarted);
	
	public abstract DStatistic createStatistic(
			DStatisticType type, Long value);

}
