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

import java.util.Collection;
import java.util.Set;

import com.sap.engine.services.deploy.ear.common.EqualUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class DSEqualUtils extends EqualUtils {

	public static boolean equals(Collection c1, Collection c2) {
		if (c1 == c2) {
			return true;
		}

		if (c1 == null && c2 != null) {
			return false;
		}

		if (c1 != null && c2 == null) {
			return false;
		}

		if (c1.size() != c2.size()) {
			return false;
		}

		return c1.containsAll(c2);
	}

}
