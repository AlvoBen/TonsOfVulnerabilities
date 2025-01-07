/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.impl;


/**
 * @author hristo-s
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TaskSecurityData {
	
	private final String owner;
	private final String runAsUser;
	
	public TaskSecurityData(String owner, String runAsUser) {
		this.owner = owner;
		this.runAsUser = runAsUser;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String runAsUser() {
		return runAsUser;
	}

}
