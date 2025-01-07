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
 * Describes the cluster statuses and the next steps, which had to be performed.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The enum will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public final class ClusterStatus {

	/**
	 * &quot;PRODUCTIVE_BUT_NEED_VALIDATION&quot; state, which requires commit
	 * or rollback to be invoked.
	 * 
	 * @deprecated
	 */
	public static final ClusterStatus PRODUCTIVE_BUT_NEED_VALIDATION = new ClusterStatus(
			new Integer(0), "PRODUCTIVE_BUT_NEED_VALIDATION");
	/**
	 * &quot;PRODUCTIVE_AND_COMMITED&quot; state, which means that the update
	 * was successfully committed in the cluster.
	 * 
	 * @deprecated
	 */
	public static final ClusterStatus PRODUCTIVE_AND_COMMITED = new ClusterStatus(
			new Integer(1), "PRODUCTIVE_AND_COMMITED");
	/**
	 * &quot;PRODUCTIVE_AND_ROLLED_BACK&quot; state, which means that the update
	 * was successfully rolled back in the cluster.
	 * 
	 * @deprecated
	 */
	public static final ClusterStatus PRODUCTIVE_AND_ROLLED_BACK = new ClusterStatus(
			new Integer(2), "PRODUCTIVE_AND_ROLLED_BACK");
	/**
	 * &quot;PRODUCTIVE_BUT_NEED_ROLL_BACK&quot; state, which requires rollback
	 * to be invoked.
	 * 
	 * @deprecated
	 */
	public static final ClusterStatus PRODUCTIVE_BUT_NEED_ROLL_BACK = new ClusterStatus(
			new Integer(3), "PRODUCTIVE_BUT_NEED_ROLL_BACK");

	private final Integer id;
	private final String name;

	private ClusterStatus(Integer id, String name) {
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

		if (!(obj instanceof ClusterStatus)) {
			return false;
		}

		final ClusterStatus other = (ClusterStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
