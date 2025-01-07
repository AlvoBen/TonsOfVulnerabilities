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

import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.tc.logging.Location;

/**
 * @author Luchesar Cekov
 */
public class OperationTimeStat extends FixedTimeStatisticNode {
	
	private static final Location location = 
		Location.getLocation(OperationTimeStat.class);

	private long startTime;
	private long endTime;
	private long cpuStartTime = -1;
	private long cpuEndTime = -1;

	public OperationTimeStat(final String aOpearationName,
			final String applicationName, final long aStartTime,
			final long aEndTime) {

		super(aOpearationName, applicationName);
		startTime = aStartTime;
		endTime = aEndTime;
	}

	public OperationTimeStat(final String aOpearationName,
			final String applicationName, final long aStartTime,
			final long aEndTime, final long aCpuStartTime,
			final long aCpuEndTime) {

		this(aOpearationName, applicationName, aStartTime, aEndTime);
		this.cpuStartTime = aCpuStartTime;
		this.cpuEndTime = aCpuEndTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getDuration() {
		if (startTime == -1 || endTime == -1) {
			DSLog
					.traceDebug(
							location, 
							"Time statistic: Operation node [{0}] has start time [{1}] and end time [{2}]. The information is insufficient to calculate the duration.",
							sNodePath,
							startTime, endTime);
			return -1;
		}
		return endTime - startTime;
	}

	public long getCpuStartTime() {
		return cpuStartTime;
	}

	public long getCpuEndTime() {
		return cpuEndTime;
	}

	/**
	 * Retrieves the CPU duration in milliseconds.
	 */
	public float getCpuDuration() {
		if (cpuStartTime == -1 || cpuEndTime == -1) {
			DSLog
					.traceDebug(
							location, 
							"Time statistic: Operation node [{0}] has CPU start time [{1}] and CPU end time [{2}]. The information is insufficient to calculate the cpu duration.",
							sNodePath,
							cpuStartTime, cpuEndTime);
			return -1;
		}
		return SystemTime.calculateTimeStampDeltaInMicros(cpuStartTime,
				cpuEndTime) / 1000f;
	}
}
