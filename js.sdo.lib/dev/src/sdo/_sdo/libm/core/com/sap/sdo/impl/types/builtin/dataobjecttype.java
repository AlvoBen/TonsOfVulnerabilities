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

import commonj.sdo.DataObject;
/**
 * Built-in type representing a superclass for all complex data objects.
 */
public class DataObjectType extends MetaDataType<DataObject> {
	
	private static final long serialVersionUID = 2201577876394399378L;
	private static DataObjectType _instance = new DataObjectType();;
	/**
	 * Return the singleton instance of this type (all "defined" types are singletons).
	 * @return
	 */
	public static DataObjectType getInstance() {
		return _instance;
	}
	private DataObjectType() {
        _instance = this;
		setUNP(URINamePair.DATAOBJECT);
		setOpen(false);
		setSequenced(false);
		setAbstract(true);
        setInstanceClass(DataObject.class);
	}
	public Object readResolve() {
		return getInstance();
	}
	/** without wrapper object
    public Object convertFromJavaClass(final Object data) {
    	return data;
	}
	***/
}
