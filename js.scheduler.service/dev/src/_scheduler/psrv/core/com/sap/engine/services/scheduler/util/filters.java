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
package com.sap.engine.services.scheduler.util;

import com.sap.scheduler.api.Filter;

public class Filters {
	public static Filter filterOut(Filter[] filters, long time) {
		for (int i = 0; i < filters.length; i++) {
			if (filters[i].filterOut(time)) {
				return filters[i];
			}
		}
		return null;
	}
}
