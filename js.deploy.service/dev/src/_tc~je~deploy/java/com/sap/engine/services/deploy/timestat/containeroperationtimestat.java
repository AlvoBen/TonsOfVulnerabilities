/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.timestat;

public class ContainerOperationTimeStat extends OperationTimeStat {
	public ContainerOperationTimeStat(final String aOpearationName,
			final String applicationName, final long aStartTime,
			final long aEndTime) {
		super(aOpearationName, applicationName, aStartTime, aEndTime);
	}

	public ContainerOperationTimeStat(final String aOpearationName,
			final String applicationName, final long aStartTime,
			final long aEndTime, final long aCpuStartTime,
			final long aCpuEndTime) {
		super(aOpearationName, applicationName, aStartTime, aEndTime,
				aCpuStartTime, aCpuEndTime);
	}
}
