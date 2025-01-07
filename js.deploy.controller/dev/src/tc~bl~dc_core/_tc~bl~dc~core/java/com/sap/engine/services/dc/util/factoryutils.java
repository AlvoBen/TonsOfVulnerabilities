/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Jul 24, 2006
 */
package com.sap.engine.services.dc.util;

public class FactoryUtils {
	public static Object getFactoryInstance(Class returnType,
			String factoryImplClassName) {
		try {
			final Class classFactory = Class.forName(factoryImplClassName);
			Object ret = classFactory.newInstance();
			return ret;
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DC.3430] An error occurred while creating an instance of "
					+ "class "
					+ returnType.getName()
					+ "! "
					+ Constants.EOL
					+ e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}
}
