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
package com.sap.sdo.impl.objects.strategy.enhancer;

import com.sap.sdo.impl.objects.strategy.AbstractNonSequencedPropSingleValue;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.Property;

public class EnhancerPropSingleValue extends AbstractNonSequencedPropSingleValue {
    private static final long serialVersionUID = 752027153751109133L;

    public EnhancerPropSingleValue(AbstractPojoDataStrategy pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }

    @Override
    protected Object internGetValue() {
        SdoType type = (SdoType)getProperty().getType();
        Object pojoValue = ((AbstractPojoDataStrategy)_dataStrategy).getPojoValue(getProperty());
        return type.convertFromJavaClass(pojoValue);
    }

    @Override
    protected void internSetValue(Object pValue) {
        ((AbstractPojoDataStrategy)_dataStrategy).setPojoValue(getProperty(), pValue);
    }

    @Override
    protected void internUnset() {
        Object defaultValue = getProperty().getDefault();
        ((AbstractPojoDataStrategy)_dataStrategy).setPojoValue(getProperty(), defaultValue);
    }

    public boolean isSet() {
        Object pojoValue = ((AbstractPojoDataStrategy)_dataStrategy).getPojoValue(getProperty());
        Object defaultValue = getProperty().getDefault();
        if (defaultValue == null) {
            return pojoValue != null;
        }
        return !defaultValue.equals(pojoValue);
    }
}
