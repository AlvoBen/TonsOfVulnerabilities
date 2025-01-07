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
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.ChangeSummary;
/**
 * Built-in type representing the {commonj.sdo}ChangeSummary interface.  This is the type
 * given to properties that will be serialized as a change summary.  Note that we this type
 * is not instanciated to create a change summary.  The current version of SAP's SDO implementation
 * does not store change information in a seperate data structure, rather associating it
 * with the objects being logged.
 */
public class ChangeSummaryType extends MetaDataType<ChangeSummary> {
	
	private static final long serialVersionUID = 2201577876394399378L;
	private static ChangeSummaryType _instance = new ChangeSummaryType();
	/**
	 * Typo-proof string representing the create property.
	 */
	public static final String CREATE = "create";
	/**
	 * Typo-proof string representing the delete property.
	 */
	public static final String DELETE = "delete";
	/**
	 * Typo-proof string representing the logging property.
	 */
	public static final String LOGGING = "logging";
	/**
	 * Return the singleton instance of this type (all "defined" types are singletons).
	 * @return
	 */
	public static ChangeSummaryType getInstance() {
		return _instance;
	}
	@Override
    public ChangeSummary convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
    	if (data instanceof ChangeSummary) {
    		return (ChangeSummary)data;
    	}
        throw new ClassCastException("Can not convert from " + data.getClass().getName() +
            " to " + ChangeSummary.class.getName());
	}
	@Override
	public <T> T convertToJavaClass(ChangeSummary data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
		if (ChangeSummary.class.isAssignableFrom(targetType)) {
			return (T)data;
		}
        // only for toString()
        if (targetType == String.class) {
            return (T)"change summary";
        }
        throw new ClassCastException("Can not convert from " + ChangeSummary.class.getName() +
            " to " + targetType.getName());

	}
	private ChangeSummaryType() {
        _instance = this;
		setUNP(URINamePair.CHANGESUMMARY_TYPE);
		SdoProperty[] propsa = new SdoProperty[] {
			new MetaDataPropertyLogicFacade(new MetaDataProperty(CREATE,JavaSimpleType.STRING,this,false,false,true,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(DELETE,JavaSimpleType.STRING,this,false,false,true,false)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(LOGGING,JavaSimpleType.BOOLEAN,this,false,false,true,false))
		};
		setDeclaredProperties(propsa);
		setOpen(true);
		setSequenced(false);
        setDataType(true);
        setInstanceClass(ChangeSummary.class);
	}
	public Object readResolve() {
		return getInstance();
	}
}
