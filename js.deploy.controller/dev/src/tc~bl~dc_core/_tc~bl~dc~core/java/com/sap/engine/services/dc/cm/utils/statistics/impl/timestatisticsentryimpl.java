/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Jul 24, 2006
 */
package com.sap.engine.services.dc.cm.utils.statistics.impl;

import java.util.ArrayList;
import java.util.List;

import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jul 24, 2006</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public class TimeStatisticsEntryImpl implements TimeStatisticsEntry {
	private static final long serialVersionUID = -6854888382257638223L;

	private static final long FINISH_TIME_UNKNOWN = -1L;
	private final String name;
	private final long startTime;
	private long finishTime = FINISH_TIME_UNKNOWN;
	private List timeStatisticEntries;
	private final int entryType;

	TimeStatisticsEntryImpl(String name, int entryType) {
		this(name, entryType, System.currentTimeMillis(), FINISH_TIME_UNKNOWN);
	}

	TimeStatisticsEntryImpl(String name, int entryType, long startTime,
			long finishTime) {
		this.name = name;
		this.startTime = startTime;
		this.entryType = entryType;
		this.finishTime = finishTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.statistics.TimeStatistics#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.statistics.TimeStatistics#getStartTime
	 * ()
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.statistics.TimeStatistics#getFinishTime
	 * ()
	 */
	public long getFinishTime() {
		return this.finishTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.statistics.TimeStatistics#getDuration
	 * ()
	 */
	public long getDuration() {
		return (this.finishTime == FINISH_TIME_UNKNOWN) ? FINISH_TIME_UNKNOWN
				: this.finishTime - this.startTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.statistics.TimeStatistics#getChildren
	 * ()
	 */
	public com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[] getTimeStatisticEntries() {
		if (this.timeStatisticEntries != null) {
			TimeStatisticsEntry[] tmpChildren = new TimeStatisticsEntry[this.timeStatisticEntries
					.size()];
			this.timeStatisticEntries.toArray(tmpChildren);
			return tmpChildren;
		} else {
			return null;
		}
	}

	public long finish() {
		if (this.finishTime != FINISH_TIME_UNKNOWN) {
			throw new IllegalStateException(
					"[ERROR CODE DPL.DC.3420] The time statistics for '"
							+ this.name + "' have been already finished.");
		}
		this.finishTime = System.currentTimeMillis();
		return getDuration();
	}

	public TimeStatisticsEntry addTimeStatisticsEntry(
			TimeStatisticsEntry childNode) {
		if (this.timeStatisticEntries == null) {
			this.timeStatisticEntries = new ArrayList();
		}
		boolean ret = this.timeStatisticEntries.add(childNode);
		return ret ? childNode : null;
	}

	public String toString() {
		return new StringBuffer(256).append(this.name).append("(").append(
				getEntryType()).append(") :").append(getDuration()).append(
				" ms.").toString();
	}

	public int getEntryType() {
		return this.entryType;
	}

	public TimeStatisticsEntry removeTimeStatisticsEntry(
			TimeStatisticsEntry timeStatEntry) {
		return this.timeStatisticEntries.remove(timeStatEntry) ? timeStatEntry
				: null;
	}

}
