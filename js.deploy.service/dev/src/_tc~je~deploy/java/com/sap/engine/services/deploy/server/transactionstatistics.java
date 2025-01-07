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
package com.sap.engine.services.deploy.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.server.utils.DSConstants;

/**
 * This class contains the results of the execution of an deployment 
 * transaction on a given server node. Every transaction has its corresponding
 * statistics for every server node in the cluster.
 * @author Rumiana Angelova
 * @version 6.30
 */
public class TransactionStatistics implements Serializable {
	private static final long serialVersionUID = 1L;

	private final int serverID;
	private final Set<String> errors;
	private final Set<String> warnings;

	/**
	 * The constructor.
	 * @param serverId the ID of the server, where the transaction is executed.
	 * Must be a valid server ID.
	 */
	public TransactionStatistics(final int serverId) {
		assert serverId > 0;
		this.serverID = serverId;
		errors = new HashSet<String>();
		warnings = new HashSet<String>();
	}

	/**
	 * @return the errors occurred during the execution of the corresponding 
	 * transaction. Not null but can be empty array if no errors occurred. 
	 */
	public String[] getErrors() {
		return errors.toArray(new String[errors.size()]);
	}

	/**
	 * Add an error to the error set.
	 * @param error the error to be added.
	 */
	public void addError(String error) {
		if (error != null) {
			errors.add(error);
		}
	}

	/**
	 * @return the warnings occurred during the execution of the corresponding 
	 * transaction. Not null but can be empty array if no warnings occurred.
	 */
	public String[] getWarnings() {
		return warnings.toArray(new String[warnings.size()]);
	}

	/**
	 * Add a warning to the warning set.
	 * @param war the warning to be added.
	 */
	public void addWarning(String war) {
		if (war != null) {
			warnings.add(war);
		}
	}

	/**
	 * @return <tt>true</tt> if there are no errors.
	 */
	public boolean isOkResult() {
		return errors.isEmpty();
	}

	/**
	 * @return the ID of the server where the corresponding transaction was 
	 * executed.
	 */
	public int getClusterID() {
		return this.serverID;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("serverID=").append(serverID).append(DSConstants.EOL_TAB)
			.append("warnings=").append(CAConvertor.toString(warnings, ""))
			.append(DSConstants.EOL_TAB)
			.append("errors=").append(CAConvertor.toString(errors, ""))
			.append(CAConstants.EOL);
		return sb.toString();
	}
}