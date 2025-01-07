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

import static com.sap.sdo.impl.objects.GenericDataObject.UNSET;

import java.io.Serializable;

import com.sap.sdo.impl.objects.GenericDataObject;

import commonj.sdo.Property;

/**
 * In contrast to the other {@link com.sap.sdo.impl.objects.PropValue} instances
 * the NonSequencedPropSingleValue is an temporary object that is created if it
 * is needed. These objects are not stored in the
 * {@link AbstractDataStrategy#_propValues}-list. Instead of the
 * NonSequencedPropSingleValue the value of the Property is stored at this place.
 * A NonSequencedPropSingleValue can be seen as a temporary container for the
 * property and a pointer to its value.
 * @author D042807
 *
 */
public class NonSequencedPropSingleValue extends AbstractNonSequencedPropSingleValue implements Serializable {

    private static final long serialVersionUID = -4419707553910790395L;
    
    NonSequencedPropSingleValue(AbstractDefaultDataStrategy pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }

    @Override
    protected void internUnset() {
        internSetValue(UNSET);
    }

    public boolean isSet() {        
        return UNSET != internGetValue();
    }

    protected void internSetValue(Object pValue) {
        int propIndex = getPropertyIndex();
        ((AbstractDefaultDataStrategy)_dataStrategy).setPropValue(propIndex, pValue);
    }

    protected Object internGetValue() {
        int propIndex = getPropertyIndex();
        Object value;
        if (propIndex < 0) {
            value = UNSET;
        } else {
            value = ((AbstractDefaultDataStrategy)_dataStrategy).getPropValue(propIndex);
            if (value instanceof GenericDataObject) {
            	value = ((GenericDataObject)value).getFacade();
            }
        }
        return value;
    }

    private int getPropertyIndex() {
        int propIndex = getProperty().getIndex(getDataObject().getType());
        if (propIndex < 0) {
            propIndex = _dataStrategy.indexOfOpenProperty(getProperty());
        }
        return propIndex;
    }

}
