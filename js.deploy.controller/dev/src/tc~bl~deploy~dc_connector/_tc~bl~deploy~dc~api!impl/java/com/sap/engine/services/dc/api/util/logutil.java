package com.sap.engine.services.dc.api.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.model.Dependency;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.api.util.DAConstants;

public class LogUtil {

	public static String createScaInfo(Sca sca) {

		StringBuffer buffer = new StringBuffer(100);

		buffer.append("name '").append(sca.getName()).append("', vendor '")
				.append(sca.getVendor()).append("', location '").append(
						sca.getLocation()).append("', version '").append(
						sca.getVersion()).append("'");

		return buffer.toString();
	}

	public static String createSdaInfo(Sda sda) {

		StringBuffer buffer = new StringBuffer(100);
		buffer.append("name '").append(sda.getName()).append("', vendor '")
				.append(sda.getVendor()).append("', location '").append(
						sda.getLocation()).append("', version '").append(
						sda.getVersion()).append("', software type (").append(
						sda.getSoftwareType()).append(
						"), csn component '" + sda.getCsnComponent()).append(
						"', dependencies :[");
		Set deps = sda.getDependencies();
		if (deps == null || deps.isEmpty()) {
			buffer.append("none");
		} else {
			Dependency nextDep;
			boolean addComma = false;
			for (Iterator iter = deps.iterator(); iter.hasNext();) {
				nextDep = (Dependency) iter.next();
				if (addComma) {
					buffer.append(", ");
				}
				buffer.append("( name '").append(nextDep.getName()).append(
						"', vendor '").append(nextDep.getVendor())
						.append("') ");
				addComma = true;
			}
		}
		buffer.append("]");
		return buffer.toString();
	}

	public static String dumpTimeStatistics(String indent, String prefix,
			DeployItem deployItem) {
		StringBuffer buffer = new StringBuffer();

		TimeStatisticsEntry[] tsEntries = deployItem.getTimeStatisticEntries();
		if (tsEntries != null && tsEntries.length > 0) {
			buffer.append(indent).append(prefix).append(
					"Time statistics( ms ):").append(DAConstants.EOL);
			for (int i = 0; i < tsEntries.length; i++) {
				dumpTimeStatistics(buffer, indent + DAConstants.SINGLE_INDENT
						+ (i + 1) + ".", tsEntries[i]);
			}
		}
		return buffer.toString();
	}

	private static void dumpTimeStatistics(StringBuffer buffer, String inFront,
			TimeStatisticsEntry tse) {
		buffer.append(inFront).append(tse.getName()).append(" : ").append(
				tse.getDuration()).append(DAConstants.EOL);
		TimeStatisticsEntry[] children = tse.getTimeStatisticEntries();
		if (children != null && children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				dumpTimeStatistics(buffer, DAConstants.SINGLE_INDENT + inFront
						+ (i + 1) + ".", children[i]);
			}
		}
	}

	/**
	 * Convert the given stack trace to a string
	 * 
	 * @param traceElements
	 * @param upperDelimiter
	 *            - pass a string here if you want to have some of the first
	 *            element omiited until the string representaion of the next
	 *            trace element contains the delimiter. Pass null if you do not
	 *            want to have any trace elements omitted
	 * 
	 * @return a string representing the stack trace that you passed in
	 */
	public static String stackTraceToString(StackTraceElement[] traceElements,
			String upperDelimiter) {

		if (traceElements == null || traceElements.length == 0) {
			return "";
		}

		StringWriter result = new StringWriter();
		PrintWriter pw = new PrintWriter(result);

		boolean delimiterReached = false;
		for (int i = 0; i < traceElements.length; i++) {

			String elementString = traceElements[i].toString();

			// try to skip the first N elements until you reach the point where
			// the user
			// called java.lang.Thread.getStackTrace and use all the elements
			// that follow
			if (upperDelimiter != null && !delimiterReached) {
				if (elementString.indexOf(upperDelimiter) >= 0) {
					delimiterReached = true;
				} else {
					continue;
				}

			}

			pw.println("\tat " + elementString);
		}

		return result.toString();

	}

	/**
	 * 
	 * Convert the given stack trace to a string
	 * 
	 * @param traceElements
	 * @return a string representing the stack trace that you passed in
	 */
	public static String stackTraceToString(StackTraceElement[] traceElements) {
		return stackTraceToString(traceElements, null);
	}
}
