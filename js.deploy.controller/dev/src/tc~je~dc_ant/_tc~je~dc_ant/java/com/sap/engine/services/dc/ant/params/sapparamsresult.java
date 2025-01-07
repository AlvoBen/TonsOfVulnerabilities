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
package com.sap.engine.services.dc.ant.params;

/**
 * 
 * This class represents the result of an operation for parameters
 * add/remove/update om the engine.
 * 
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public class SAPParamsResult {

	private int niParamsAdded;
	private int niParamsUpdated;
	private int niParamsRemoved;

	public SAPParamsResult(int added, int updated, int removed) {
		this.niParamsAdded = added;
		this.niParamsUpdated = updated;
		this.niParamsRemoved = removed;
	}

	public void setParamsAdded(int addedParamsNum) {
		this.niParamsAdded = addedParamsNum;
	}

	public int getParamsAdded() {
		return this.niParamsAdded;
	}

	public void setParamsUpdated(int updatedParamsNum) {
		this.niParamsUpdated = updatedParamsNum;
	}

	public int getParamsUpdated() {
		return this.niParamsUpdated;
	}

	public void setParamsRemoved(int removedParamsNum) {
		this.niParamsRemoved = removedParamsNum;
	}

	public int getParamsRemoved() {
		return this.niParamsRemoved;
	}
}
