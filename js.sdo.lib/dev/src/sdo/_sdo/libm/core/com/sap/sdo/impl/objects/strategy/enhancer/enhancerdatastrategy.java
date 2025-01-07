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

import java.util.ArrayList;
import java.util.List;

import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.strategy.NonSequencedPropMultiValue;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class EnhancerDataStrategy extends AbstractPojoDataStrategy {
    
    private static final long serialVersionUID = -2758708126069385621L;
    public EnhancerDataStrategy() {
    }
    public EnhancerDataStrategy(GenericDataObject dataObject) {
		setDataObject(dataObject);
	}
    public DataObject createDataObject(Property property, Type type) {
        DataObject dO = getHelperContext().getDataFactory().create(type);
        if (property.isMany()) {
            ((List<Object>)getPropValue(property, true).getValue()).add(dO);
        } else {
            getDataObject().set(property, dO);
        }
        return dO;
    }

    public PropValue<?> getPropValue(int pIndex, Property pProperty, boolean pCreate) {
        return createPropValue(pIndex, pProperty);
    }

    @Override
    public void addOpenProperty(Property pProperty) {
        throw new UnsupportedOperationException("addOpenProperty");
    }

    @Override
    public void reactivateOpenPropValue(PropValue<?> pPropValue) {
        throw new UnsupportedOperationException("reactivateOpenPropValue");
    }

    @Override
    public PropValue<?> createPropValue(int pPropIndex, Property pProperty) {
        SdoProperty property = (SdoProperty)(pProperty!=null?pProperty:getPropertyByIndex(pPropIndex));
        if (property.isMany()) {
            Object pojoValue = getPojoValue(property);
            if (pojoValue instanceof PropValue) {
                return (PropValue<?>)pojoValue;
            }
            NonSequencedPropMultiValue propValue = new NonSequencedPropMultiValue(this, property);
            if (pojoValue instanceof List) {
                propValue.setWithoutCheck((List<Object>)pojoValue);
            }
            setPojoValue(property, propValue.getValue());
            return propValue;
        }
        return new EnhancerPropSingleValue(this, property);
    }

    @Override
    public void removeOpenPropValue(int pPropertyIndex) {
        throw new UnsupportedOperationException("removeOpenPropValue");
    }

    public DataStrategy refineType(Type pOldType, Type pNewType) {
        return this;
    }
	@Override
	protected String getSetterString() {
		return "_set";
	}
    @Override
    public List<Property> getOpenProperties() {
        return null;
    }
    @Override
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
        throw new UnsupportedOperationException("setOpenProperties");
    }
}
