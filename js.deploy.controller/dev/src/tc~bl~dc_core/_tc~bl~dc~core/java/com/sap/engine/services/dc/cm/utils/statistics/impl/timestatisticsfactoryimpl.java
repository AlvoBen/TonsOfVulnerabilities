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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsFactory;

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

public class TimeStatisticsFactoryImpl extends TimeStatisticsFactory {
	private final static char SEPARATOR = '|';
	private final static String ENCODING = "US-ASCII";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsFactory#
	 * createTimeStatistics(java.lang.String)
	 */
	public TimeStatisticsEntry createTimeStatisticEntry(String name,
			int entryType) {
		return new TimeStatisticsEntryImpl(name, entryType);
	}

	// ///////////////////////// D E S E R I A L I Z A T I O N

	public TimeStatisticsEntry deserializeTimeStatisticsFromStream(
			InputStream iStream) throws NumberFormatException, IOException {
		if (iStream == null) {
			return null;
		}
		InputStreamReader isReader = null;
		BufferedReader bufferedReader = null;
		try {
			isReader = new InputStreamReader(iStream, ENCODING);
			bufferedReader = new BufferedReader(isReader);
			TimeStatisticsEntry timeStatEntry = internalDeserializeFromStream(bufferedReader);
			return timeStatEntry;
		} finally {
			// always close the streams
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (isReader != null) {
					isReader.close();
				}
				if (iStream != null) {
					iStream.close();
				}
			} catch (IOException e) {
				// $JL-EXC$
			}
		}
	}

	public TimeStatisticsEntry deserializeTimeStatisticEntry(String src)
			throws IOException {
		StringReader reader = new StringReader(src);
		BufferedReader bufferedReader = new BufferedReader(reader);

		TimeStatisticsEntry timeStatEntry = internalDeserializeFromStream(bufferedReader);

		bufferedReader.close();
		reader.close();
		return timeStatEntry;
	}

	private TimeStatisticsEntry internalDeserializeFromStream(
			BufferedReader bufferedReader) throws NumberFormatException,
			IOException {
		TimeStatisticsEntryImpl timeStatEntry = new TimeStatisticsEntryImpl("",
				TimeStatisticsEntry.ENTRY_TYPE_GLOBAL);
		Stack stack = new Stack();
		stack.push(timeStatEntry);
		String line;
		int lastIndent = -1;
		int delta;
		while ((line = bufferedReader.readLine()) != null) {

			int pos1 = line.indexOf(SEPARATOR);
			int childIndent = Integer.parseInt(line.substring(0, pos1), 10);

			delta = childIndent - lastIndent;

			TimeStatisticsEntryImpl newEntry = deserializeSingleEntry(line
					.substring(pos1 + 1));
			if (delta > 0) {
				TimeStatisticsEntry parent = (TimeStatisticsEntry) stack.peek();
				parent.addTimeStatisticsEntry(newEntry);
			} else if (delta == 0) {
				stack.pop();
				TimeStatisticsEntry parent = (TimeStatisticsEntry) stack.peek();
				parent.addTimeStatisticsEntry(newEntry);
			} else {
				TimeStatisticsEntry parent = null;
				for (int i = 0; i < Math.abs(delta) + 2
						&& parent != timeStatEntry; i++) {
					parent = (TimeStatisticsEntry) stack.pop();
				}
				parent = parent == null ? timeStatEntry : parent;

				parent.addTimeStatisticsEntry(newEntry);
				stack.push(parent);
			}

			stack.push(newEntry);
			lastIndent = childIndent;

		}

		return timeStatEntry;
	}

	private static TimeStatisticsEntryImpl deserializeSingleEntry(String line) {
		int entryType;
		long startTime;
		long duration;
		String sName;
		int pos2 = line.indexOf(SEPARATOR);
		entryType = Integer.parseInt(line.substring(0, pos2), 10);
		int pos3 = line.indexOf(',', pos2 + 1);
		startTime = Long.parseLong(line.substring(pos2 + 1, pos3), 10);
		int pos4 = line.indexOf(SEPARATOR, pos3 + 1);
		duration = Long.parseLong(line.substring(pos3 + 1, pos4), 10);
		sName = line.substring(pos4 + 1);
		TimeStatisticsEntryImpl timeStatEntry = new TimeStatisticsEntryImpl(
				sName, entryType, startTime, startTime + duration);
		return timeStatEntry;
	}

	// ///////////////////////// S E R I A L I Z A T I O N
	public InputStream serializeTimeStatisticsAsStream(
			TimeStatisticsEntry[] timeStatEntries) {
		StringBuffer buffer = new StringBuffer(512);
		if (timeStatEntries != null) {
			for (int i = 0; i < timeStatEntries.length; i++) {
				buffer.append(serializeTimeStatisticEntry(timeStatEntries[i]));
			}
		}
		byte[] serialized;
		try {
			serialized = buffer.toString().getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			serialized = buffer.toString().getBytes();
		}
		final ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		return bais;
	}

	/**
	 * Serializes the entire entry subtree
	 * 
	 * @return
	 */
	public String serializeTimeStatisticEntry(TimeStatisticsEntry timeStatEntry) {
		StringBuffer buffer = new StringBuffer(512);
		internalSerialize(0, timeStatEntry, buffer);
		return buffer.toString();
	}

	private static void internalSerialize(
			int indent,
			com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry timeStatEntry,
			StringBuffer buffer) {
		buffer.append(indent).append(SEPARATOR).append(
				timeStatEntry.getEntryType()).append(SEPARATOR).append(
				timeStatEntry.getStartTime()).append(',').append(
				timeStatEntry.getDuration()).append(SEPARATOR).append(
				timeStatEntry.getName()).append("\r\n");
		com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[] children = timeStatEntry
				.getTimeStatisticEntries();
		if (children != null && children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				internalSerialize(indent + 1, children[i], buffer);
			}
		}
	}

}
