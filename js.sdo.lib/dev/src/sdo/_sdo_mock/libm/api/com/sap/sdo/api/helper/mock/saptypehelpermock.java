/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.api.helper.mock;

import java.util.List;
import java.util.Set;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapTypeHelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class SapTypeHelperMock implements SapTypeHelper {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapTypeHelper#createInterfaceGenerator(java.lang.String)
     */
    public InterfaceGenerator createInterfaceGenerator(String pRootPath) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#define(commonj.sdo.DataObject)
     */
    public Type define(DataObject pType) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#define(java.util.List)
     */
    public List define(List pTypes) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#defineOpenContentProperty(java.lang.String, commonj.sdo.DataObject)
     */
    public Property defineOpenContentProperty(String pUri, DataObject property) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#getOpenContentProperty(java.lang.String, java.lang.String)
     */
    public Property getOpenContentProperty(String pUri, String propertyName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#getType(java.lang.String, java.lang.String)
     */
    public Type getType(String pUri, String pTypeName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.TypeHelper#getType(java.lang.Class)
     */
    public Type getType(Class pInterfaceClass) {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeTypesAndProperties(Set<Type> pTypes, Set<Property> pProperties) {
        // TODO Auto-generated method stub
    }

}
