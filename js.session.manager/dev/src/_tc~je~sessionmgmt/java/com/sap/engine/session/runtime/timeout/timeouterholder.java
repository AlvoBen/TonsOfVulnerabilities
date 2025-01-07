/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime.timeout;

/**
 * Used to hold Timeouters so we don't take care of concurrency changes when killing and starting new timeouters
 *
 * @author Nikolai Neichev
 */
public class TimeouterHolder {

	private Timeouter timeouter;


	public Timeouter getTimeouter() {
		return timeouter;
	}

	public void setTimeouter(Timeouter timeouter) {
		this.timeouter = timeouter;
	}
}
