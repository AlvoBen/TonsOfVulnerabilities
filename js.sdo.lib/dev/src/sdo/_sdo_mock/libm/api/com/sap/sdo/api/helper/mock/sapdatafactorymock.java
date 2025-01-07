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

import com.sap.sdo.api.helper.SapDataFactory;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class SapDataFactoryMock implements SapDataFactory {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataFactory#cast(java.lang.Object)
     */
    public DataObject cast(Object pojo) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataFactory#project(commonj.sdo.DataObject)
     */
    public DataObject project(DataObject pDataObject) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataFactory#create(java.lang.String, java.lang.String)
     */
    public DataObject create(String pUri, String pTypeName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataFactory#create(java.lang.Class)
     */
    public DataObject create(Class pInterfaceClass) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataFactory#create(commonj.sdo.Type)
     */
    public DataObject create(Type pType) {
        // TODO Auto-generated method stub
        return null;
    }

}
