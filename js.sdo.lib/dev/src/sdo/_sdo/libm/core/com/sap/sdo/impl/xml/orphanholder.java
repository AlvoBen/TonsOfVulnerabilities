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
package com.sap.sdo.impl.xml;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.DataObject;

public class OrphanHolder {
    private final GenericDataObject _dataObject;
    private final SdoProperty _property;

    private volatile int _hashCode = 0;

    /**
     * @param pDataObject
     * @param pProperty
     */
    public OrphanHolder(DataObject pDataObject, SdoProperty pProperty) {
        super();
        _dataObject = ((DataObjectDecorator)pDataObject).getInstance();
        _property = pProperty;
    }

    /**
     * @return the dataObject
     */
    public GenericDataObject getDataObject() {
        return _dataObject;
    }

    /**
     * @return the property
     */
    public SdoProperty getProperty() {
        return _property;
    }

    @Override
    public boolean equals(Object pObj) {
        if (this == pObj) {
            return true;
        }
        if (!(pObj instanceof OrphanHolder)) {
            return false;
        }
        OrphanHolder orphanHolder = (OrphanHolder)pObj;
        return _dataObject == orphanHolder._dataObject && _property == orphanHolder._property;
    }

    @Override
    public int hashCode() {
        if (_hashCode == 0) {
            int result = 17;
            result = 37*result + _dataObject.hashCode();
            result = 37*result + _property.hashCode();
            _hashCode = result;
        }
        return _hashCode;
    }
}