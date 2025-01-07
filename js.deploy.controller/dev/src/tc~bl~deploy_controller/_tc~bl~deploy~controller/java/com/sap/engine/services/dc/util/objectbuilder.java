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
package com.sap.engine.services.dc.util;

import java.util.ArrayList;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class ObjectBuilder {

	private ObjectBuilder() {
	}

	/**
	 * Creates <code>ArrayList</code>, which is initialized with
	 * <code>int</code> null elements.
	 * 
	 * @param size
	 * @return <code>ArrayList</code>
	 */
	public static ArrayList createArrayList(int size) {
		final ArrayList result = new ArrayList();
		for (int i = 0; i < size; i++) {
			result.add(i, null);
		}
		return result;
	}

}
