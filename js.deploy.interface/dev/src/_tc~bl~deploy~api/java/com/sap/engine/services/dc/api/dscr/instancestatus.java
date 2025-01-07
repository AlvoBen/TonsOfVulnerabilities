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
package com.sap.engine.services.dc.api.dscr;

/**
 * Describes the instance statuses and their mapping to the
 * <code>ClusterStatus</code>
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The enum will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public final class InstanceStatus {

	// ************************** ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION
	// **************************//
	/**
	 * &quot;PRODUCTIVE_BUT_NOT_UPDATED&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus PRODUCTIVE_BUT_NOT_UPDATED = new InstanceStatus(
			new Integer(0), "PRODUCTIVE_BUT_NOT_UPDATED");
	/**
	 * &quot;NOT_PRODUCTIVE_AND_NEED_VALIDATION&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus NOT_PRODUCTIVE_AND_NEED_VALIDATION = new InstanceStatus(
			new Integer(1), "NOT_PRODUCTIVE_AND_NEED_VALIDATION");
	// ************************** ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION
	// **************************//

	// ************************** ClusterStatus.PRODUCTIVE_AND_COMMITED
	// **************************//
	/**
	 * &quot;PRODUCTIVE_AND_COMMITTED&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus PRODUCTIVE_AND_COMMITTED = new InstanceStatus(
			new Integer(2), "PRODUCTIVE_AND_COMMITTED");
	// ************************** ClusterStatus.PRODUCTIVE_AND_COMMITED
	// **************************//

	// ************************** ClusterStatus.PRODUCTIVE_AND_ROLLED_BACK
	// **************************//
	/**
	 * &quot;PRODUCTIVE_AND_ROLLED_BACK&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus PRODUCTIVE_AND_ROLLED_BACK = new InstanceStatus(
			new Integer(3), "PRODUCTIVE_AND_ROLLED_BACK");
	// ************************** ClusterStatus.PRODUCTIVE_AND_ROLLED_BACK
	// **************************//

	// ************************** ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK
	// **************************//
	/**
	 * &quot;NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE = new InstanceStatus(
			new Integer(4), "NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE");
	/**
	 * &quot;NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT = new InstanceStatus(
			new Integer(5), "NOT_PRODUCTIVE_AND_FAILED_TO_COMMIT");
	/**
	 * &quot;NOT_PRODUCTIVE_AND_FAILED_TO_ROLL_BACK&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus NOT_PRODUCTIVE_AND_FAILED_TO_ROLL_BACK = new InstanceStatus(
			new Integer(6), "NOT_PRODUCTIVE_AND_FAILED_TO_ROLL_BACK");
	// ************************** ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK
	// **************************//

	// ************************** Internal statuses
	// ****************************************************//
	/**
	 * &quot;NOT_PRODUCTIVE_AND_UPDATED&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus NOT_PRODUCTIVE_AND_UPDATED = new InstanceStatus(
			new Integer(7), "NOT_PRODUCTIVE_AND_UPDATED");

	/**
	 * &quot;NOT_PRODUCTIVE_AND_NOT_UPDATED&quot; status
	 * 
	 * @deprecated
	 */
	public static final InstanceStatus NOT_PRODUCTIVE_AND_NOT_UPDATED = new InstanceStatus(
			new Integer(8), "NOT_PRODUCTIVE_AND_NOT_UPDATED");

	// ************************** Internal statuses
	// ****************************************************//

	private final Integer id;
	private final String name;

	private InstanceStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof InstanceStatus)) {
			return false;
		}

		final InstanceStatus other = (InstanceStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
