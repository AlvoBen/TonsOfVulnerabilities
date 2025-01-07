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
package com.sap.engine.services.dc.ant.undeploy;

/**
 * 
 * This class represents undeployment result delivered to the output of the
 * undeployment task.
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPUndeployResult {

	/**
	 * There was an error on deploy.
	 */
	static final SAPUndeployResult ERROR = new SAPUndeployResult(
			new Integer(0), "Error");

	/**
	 * Undeploy transaction passed successfully.
	 */
	static final SAPUndeployResult SUCCESS = new SAPUndeployResult(new Integer(
			1), "Success");

	/**
	 * Undeploy transaction passed with some warnings. It is better to check
	 * distinct <code>UndeployItemStatus</code> in order to determine which
	 * items are processed with problems.
	 */
	static final SAPUndeployResult WARNING = new SAPUndeployResult(new Integer(
			2), "Warning");

	private final Integer id;
	private final String name;

	private SAPUndeployResult(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	String getName() {
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

		if (!(obj instanceof SAPUndeployResult)) {
			return false;
		}

		SAPUndeployResult other = (SAPUndeployResult) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
