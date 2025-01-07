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
package com.sap.engine.services.deploy.zdm;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.server.DTransaction;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.TransactionStatistics;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.zdm.utils.DSRollingStatus;
import com.sap.engine.services.deploy.zdm.utils.InstanceDescriptor;
import com.sap.engine.services.deploy.zdm.utils.ServerDescriptor;

/**
 * Describes how the state of the instance was changed as a result from calling
 * methods of <code>DSRollingPatch interface</code>. It provides also a static
 * method to form DSRollingResult based on transaction statistic, current server
 * ID and current group ID. Based on the result the method caller is responsible
 * to decide is the new state of the instance acceptable or not.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 */
public class DSRollingResult implements Serializable {

	private static final long serialVersionUID = 7163861498237698692L;

	private final ApplicationName applicationName;
	private final InstanceDescriptor instanceDescriptor;

	private final int hashCode;
	private final StringBuilder toString;

	/**
	 * Constructs DSRollingResult based on application's name and instance
	 * descriptor
	 * 
	 * @param applicationName
	 * @param instanceDescriptor
	 */
	public DSRollingResult(ApplicationName applicationName,
			InstanceDescriptor instanceDescriptor) {
		this.applicationName = applicationName;
		this.instanceDescriptor = instanceDescriptor;

		this.hashCode = evaluateHashCode();
		this.toString = evaluateToStsring();
	}

	/**
	 * Getter method for application's name
	 * 
	 * @return applicationName
	 */
	public ApplicationName getApplicationName() {
		return applicationName;
	}

	/**
	 * Getter method for instance descriptor
	 * 
	 * @return instanceDescriptor
	 */
	public InstanceDescriptor getInstanceDescriptor() {
		return instanceDescriptor;
	}

	/**
	 * Creates DSRollingResult based on transaction statistic, current server ID
	 * and current group ID.
	 * 
	 * @param transactionStatistics
	 *            array of transaction statistics, i.e. errors, warnings,
	 *            answers per cluster Id
	 * @param currentGroupID
	 *            current group ID (ID on instance level)
	 * @param currentServerID
	 *            server ID (server node)
	 * @return DSRollingResult
	 */
	public static DSRollingResult createDSRollingResult(
		final DTransaction tx, int currentGroupID,
		int currentServerID) {
		final Set<ServerDescriptor> serverDescriptors = 
			new HashSet<ServerDescriptor>();
		final TransactionStatistics txStatistics[] = tx.getStatistics();
		for (int i = 0; i < txStatistics.length; i++) {
			// serverDescriptors
			serverDescriptors.add(createServerDescriptor(
				txStatistics[i], currentGroupID, currentServerID));
		}

		final InstanceDescriptor instanceDescriptor = new InstanceDescriptor(
				serverDescriptors);
		return new DSRollingResult(
			new ApplicationName(tx.getModuleID()), instanceDescriptor);
	}

	/**
	 * Creates server descriptor based on transaction statistic, current server
	 * ID and current group ID.
	 * 
	 * @param trStat
	 *            transaction statistics, i.e. errors, warnings, answers per
	 *            cluster ID
	 * @param currentGroupID
	 *            current group ID (ID on instance level)
	 * @param currentServerID
	 *            server ID (server node)
	 * @return ServerDescriptor
	 */
	private static ServerDescriptor createServerDescriptor(
			TransactionStatistics trStat, int currentGroupID,
			int currentServerID) {
		// DSRollingStatus
		DSRollingStatus dsRollingStatus = DSRollingStatus.SUCCESS;
		if (trStat.getWarnings() != null && trStat.getWarnings().length > 0) {
			dsRollingStatus = DSRollingStatus.WARNING;
		}
		if (trStat.getErrors() != null && trStat.getErrors().length > 0) {
			if (trStat.getClusterID() == currentServerID) {
				dsRollingStatus = DSRollingStatus.ERROR;
			} else {
				dsRollingStatus = DSRollingStatus.WARNING;
			}
		}
		// Description
		final String warningsAndErrors[] = DUtils.concatArrays(trStat
				.getWarnings(), trStat.getErrors());
		final String description = CAConvertor.toString(warningsAndErrors, "");

		return new ServerDescriptor(dsRollingStatus, trStat.getClusterID(),
				currentGroupID, description);
	}

	// ************************** OBJECT **************************//

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final DSRollingResult otherDSRollingResult = (DSRollingResult) obj;
		if (!this.getApplicationName().equals(
				otherDSRollingResult.getApplicationName())) {
			return false;
		}
		if (!this.getInstanceDescriptor().equals(
				otherDSRollingResult.getInstanceDescriptor())) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return toString.toString();
	}

	// ************************** OBJECT **************************//

	// ************************** PRIVATE **************************//

	public int evaluateHashCode() {
		final int offset = 17;
		final int multiplier = 59;

		int result = offset + getApplicationName().hashCode();
		result += result * multiplier + getInstanceDescriptor().hashCode();

		return result;
	}

	private StringBuilder evaluateToStsring() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getApplicationName());
		sb.append(getInstanceDescriptor());
		return sb;
	}

	// ************************** PRIVATE **************************//

}
