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

import javax.naming.*;

/**
 * 
 * @author  Vladimir Pavlov, vladimir.pavlov@sap.com
 * @version 7.10
 */
public class JNDIObjectFactory implements ObjectFactory {

	private final String jndiName;

	public JNDIObjectFactory(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * Returns a new object looked up in the JNDI namespace using the <code>jndiName</code> path string
	 *
	 * @return a new object looked up in the JNDI namespace using the <code>jndiName</code> path string
	 */
	public Object getObject() throws Exception {
		return new InitialContext().lookup(jndiName);
	}

}
