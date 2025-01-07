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
 * Describes the item statuses.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The enum will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public final class ItemStatus {

	/**
	 * &quot;SUCCESS&quot; state
	 * 
	 * @deprecated
	 */
	public static final ItemStatus SUCCESS = new ItemStatus(new Integer(0),
			"SUCCESS");
	/**
	 * &quot;WARNING&quot; state
	 * 
	 * @deprecated
	 */
	public static final ItemStatus WARNING = new ItemStatus(new Integer(1),
			"WARNING");
	/**
	 * &quot;ABORTED&quot; state
	 * 
	 * @deprecated
	 */
	public static final ItemStatus ABORTED = new ItemStatus(new Integer(2),
			"ABORTED");

	private final Integer id;
	private final String name;

	private ItemStatus(Integer id, String name) {
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

		if (!(obj instanceof ItemStatus)) {
			return false;
		}

		final ItemStatus other = (ItemStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
