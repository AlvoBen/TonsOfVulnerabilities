/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.timestat;

/**
 *@author Luchesar Cekov
 */
public class DeployOperationTimeStat extends OperationTimeStat implements
		ITimeStatConstants {

	public DeployOperationTimeStat(String aOpearationName, long aStartTime,
			long aEndTime) {
		super(aOpearationName, null, aStartTime, aEndTime);
	}

	public DeployOperationTimeStat(String aOpearationName, long aStartTime,
			long aEndTime, long aCpuStartTime, long aCpuEndTime) {
		super(aOpearationName, null, aStartTime, aEndTime, aCpuStartTime,
				aCpuEndTime);
	}
}
