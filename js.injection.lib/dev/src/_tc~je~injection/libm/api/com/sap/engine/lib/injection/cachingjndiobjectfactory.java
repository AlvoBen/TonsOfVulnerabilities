/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.injection;

/**
 * 
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public class CachingJNDIObjectFactory extends JNDIObjectFactory {

	private Object cachedValue;

	public CachingJNDIObjectFactory(String jndiName) {
		this(jndiName, null);
	}

	public CachingJNDIObjectFactory(String jndiName, Object obj) {
		super(jndiName);
		cachedValue = obj;
	}

	/**
	 * Returns the cached object instance. If it's <code>null</code> it will be looked up in the JNDI namespace.
	 *
	 * @return the cached object instance. If it's <code>null</code> it will be looked up in the JNDI namespace.
	 */
	public Object getObject() throws Exception {
		if (cachedValue == null) {
			cachedValue = super.getObject();
		}
		return cachedValue;
	}

}
