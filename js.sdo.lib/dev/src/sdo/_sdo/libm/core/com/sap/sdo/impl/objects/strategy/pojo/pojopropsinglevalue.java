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
package com.sap.sdo.impl.objects.strategy.pojo;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.strategy.AbstractNonSequencedPropSingleValue;

import commonj.sdo.Property;

public class PojoPropSingleValue extends AbstractNonSequencedPropSingleValue {
    private static final long serialVersionUID = 752027153751109133L;

    public PojoPropSingleValue(PojoDataStrategy pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }

    @Override
    protected Object internGetValue() {
        Object pojoValue = ((PojoDataStrategy)_dataStrategy).getPojoValue(getProperty());
        if (getProperty().getType().isDataType()) {
            return pojoValue;
        }
        return ((PojoDataStrategy)_dataStrategy).getDataObject(pojoValue, this);
    }

    @Override
    protected void internSetValue(Object pValue) {
        ((PojoDataStrategy)_dataStrategy).setPojoValue(getProperty(), unwrapPojo(pValue));
    }

    @Override
    protected void internUnset() {
        Object defaultValue = getProperty().getDefault();
        ((PojoDataStrategy)_dataStrategy).setPojoValue(getProperty(), defaultValue);
    }

    public boolean isSet() {
        Object pojoValue = ((PojoDataStrategy)_dataStrategy).getPojoValue(getProperty());
        Object defaultValue = getProperty().getDefault();
        if (defaultValue == null) {
            return pojoValue != null;
        }
        return !defaultValue.equals(pojoValue);
    }

    private Object unwrapPojo(Object pValue) {
        if (pValue == null) {
            return null;
        }
        if (getProperty().getType().isDataType()) {
            return pValue;
        }
        final GenericDataObject dataObject = ((DataObjectDecorator)pValue).getInstance();
        PojoDataStrategy dataStrategy = (PojoDataStrategy)dataObject.getDataStrategy();
        return dataStrategy.getPojo();
    }
}
