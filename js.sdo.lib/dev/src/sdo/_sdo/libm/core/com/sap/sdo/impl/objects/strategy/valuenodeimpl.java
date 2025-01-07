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
package com.sap.sdo.impl.objects.strategy;

import java.io.Serializable;

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;

import commonj.sdo.Property;

/**
 * A ValueNodeImpl represents a single value of a many-valued property in a sequence. 
 */
public class ValueNodeImpl implements ValueNode, Serializable
{
    private static final long serialVersionUID = 6949917781103626260L;

    private Object _value;

    private final PropValue _propValue;

    public ValueNodeImpl(PropValue pPropValue) {
        _propValue = pPropValue;
    }

    public Object setNodeValue(Object pValue, PropertyChangeContext pChangeContext) {
        Object oldValue = this._value;
        _value = pChangeContext.replaceAndNormalizeValue(oldValue, pValue);
        return oldValue;
    }
    
    public Object getNodeValue() {
        if (_value instanceof GenericDataObject) {
            return ((GenericDataObject)_value).getFacade();
        }
        return _value;
    }

    public void setWithoutCheck(Object pValue) {
        _value = pValue;
    }

    public Property getProperty() {
        return _propValue.getProperty();
    }

    public PropValue getPropValue() {
        return _propValue;
    }
}
