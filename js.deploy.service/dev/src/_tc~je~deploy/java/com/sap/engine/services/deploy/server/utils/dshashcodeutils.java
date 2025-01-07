/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class DSHashCodeUtils {

	public static int hashCode(Object obj) {
		return (obj != null ? obj.hashCode() : 0);
	}

}
