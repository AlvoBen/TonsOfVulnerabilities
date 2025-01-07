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

import java.io.IOException;
import java.io.InputStream;

import com.sap.engine.services.dc.util.FactoryUtils;

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

public abstract class TimeStatisticsFactory {
	private static TimeStatisticsFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.utils.statistics.impl.TimeStatisticsFactoryImpl";

	public static TimeStatisticsFactory getInstance() {
		if (INSTANCE == null) {
			synchronized (TimeStatisticsFactory.class) {
				if (INSTANCE == null) {// double check
					INSTANCE = (TimeStatisticsFactory) FactoryUtils
							.getFactoryInstance(TimeStatisticsFactory.class,
									FACTORY_IMPL);
				}
			}
		}
		return INSTANCE;
	}

	public abstract TimeStatisticsEntry createTimeStatisticEntry(String name,
			int entryType);

	/**
	 * Serializes all the entries in the array in one bunch
	 * 
	 * @param timeStatEntries
	 * @return
	 */
	public abstract InputStream serializeTimeStatisticsAsStream(
			TimeStatisticsEntry[] timeStatEntries);

	/**
	 * Serializes the entire entry subtree
	 * 
	 * @param timeStatEntry
	 *            entry which should be serialized
	 * @return
	 */
	public abstract String serializeTimeStatisticEntry(
			TimeStatisticsEntry timeStatEntry);

	/**
	 * Deserializes time statistic entries from stream.
	 * 
	 * @param iStream
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public abstract TimeStatisticsEntry deserializeTimeStatisticsFromStream(
			InputStream iStream) throws NumberFormatException, IOException;

	/**
	 * Deserialize entry from serialized string.
	 * 
	 * @param src
	 * @return
	 * @throws IOException
	 */
	public abstract TimeStatisticsEntry deserializeTimeStatisticEntry(String src)
			throws IOException;

}