/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.types.builtin;

import com.sap.sdo.api.util.URINamePair;
/**
 * Built-in type representing a superclass for all complex data objects.
 */
public class OpenType extends MetaDataType<Object> {
	
	private static final long serialVersionUID = 2201577876394399378L;
	private static OpenType _instance = new OpenType();
	/**
	 * Return the singleton instance of this type (all "defined" types are singletons).
	 * @return
	 */
	public static OpenType getInstance() {
		return _instance;
	}
	private OpenType() {
        _instance = this;
		setUNP(URINamePair.OPEN);
		setOpen(true);
		setSequenced(false);
		setAbstract(false);
	}
    
    public Object readResolve() {
		return getInstance();
	}
}
