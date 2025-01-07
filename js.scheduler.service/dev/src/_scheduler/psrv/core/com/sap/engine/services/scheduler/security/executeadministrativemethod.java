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
package com.sap.engine.services.scheduler.security;

import com.sap.security.api.permissions.NamePermission;


public class ExecuteAdministrativeMethod extends NamePermission {

	public ExecuteAdministrativeMethod(String name) {
		super(name);
		
	}
	
	public ExecuteAdministrativeMethod(String name, String action) {
		super(name, action);
	}
	
}
