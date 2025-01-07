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
package com.sap.sdo.impl.types;

import java.util.List;
import java.util.Set;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;

/**
 * @author D042774
 *
 */
public class TypeHelperDelegator implements SapTypeHelper {
    private static final TypeHelper INSTANCE = new TypeHelperDelegator();

    /**
     * 
     */
    private TypeHelperDelegator() {
        super();
    }
    
    public static TypeHelper getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#getType(java.lang.String, java.lang.String)
     */
    public Type getType(String pUri, String pTypeName) {
        return SapHelperProviderImpl.getDefaultContext().getTypeHelper().getType(pUri, pTypeName);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#getType(java.lang.Class)
     */
    public Type getType(Class pInterfaceClass) {
        return SapHelperProviderImpl.getDefaultContext().getTypeHelper().getType(pInterfaceClass);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#define(commonj.sdo.DataObject)
     */
    public Type define(DataObject pType) {
        return SapHelperProviderImpl.getDefaultContext().getTypeHelper().define(pType);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#define(java.util.List)
     */
    public List define(List pTypes) {
        return SapHelperProviderImpl.getDefaultContext().getTypeHelper().define(pTypes);
    }

    public Property defineOpenContentProperty(String uri, DataObject property) {
        return SapHelperProviderImpl.getDefaultContext().getTypeHelper().defineOpenContentProperty(uri, property);
    }
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" delegate: ");
        buf.append(SapHelperProviderImpl.getDefaultContext().getTypeHelper());
        return buf.toString();
    }

	public Property getOpenContentProperty(String uri, String propertyName) {
		return SapHelperProviderImpl.getDefaultContext().getTypeHelper().getOpenContentProperty(uri, propertyName);
	}

    public InterfaceGenerator createInterfaceGenerator(String pRootPath) {
        return ((SapTypeHelper)SapHelperProviderImpl.getDefaultContext().getTypeHelper()).createInterfaceGenerator(pRootPath);
    }
    
    public void removeTypesAndProperties(Set<Type> pTypes, Set<Property> pProperties) {
        ((SapTypeHelper)SapHelperProviderImpl.getDefaultContext().getTypeHelper()).removeTypesAndProperties(pTypes, pProperties);
    }

}
