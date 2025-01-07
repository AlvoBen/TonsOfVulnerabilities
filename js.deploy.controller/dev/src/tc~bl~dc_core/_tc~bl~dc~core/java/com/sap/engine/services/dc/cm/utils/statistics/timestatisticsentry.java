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
package com.sap.engine.services.dc.cm.utils.statistics;

/**
 * This interface provides common methods for handling time statistics in the
 * deploy controller. It extends the client functionality by adding mutators for
 * the time statistics.
 */
public interface TimeStatisticsEntry extends
		com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry {

	/**
	 * Adds <code>TimeStatistics</code> child to the statistics tree.
	 * 
	 * @param childNode
	 * @return
	 */
	public TimeStatisticsEntry addTimeStatisticsEntry(
			TimeStatisticsEntry childNode);

	/**
	 * For internal purpose. Marks the step as finished in order to calculate
	 * the delay. The client should not invoke this method.
	 * 
	 * @return delay in milliseconds
	 */
	public long finish();

	/**
	 * removes given argument from the list of children
	 * 
	 * @param timeStatEntry
	 *            entry to be removed
	 * @return argument if it has been successfully removed. 'null' if the
	 *         argument is not child of the node
	 */
	public TimeStatisticsEntry removeTimeStatisticsEntry(
			TimeStatisticsEntry timeStatEntry);

}
