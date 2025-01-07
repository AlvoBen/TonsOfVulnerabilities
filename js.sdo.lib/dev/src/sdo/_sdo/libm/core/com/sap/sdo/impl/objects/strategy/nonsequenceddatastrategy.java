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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class NonSequencedDataStrategy extends AbstractDefaultDataStrategy implements Serializable{

    private static final long serialVersionUID = -2631285978138535610L;

    public NonSequencedDataStrategy(Type pType) {
        super(pType);
    }

    public PropValue<?> createPropValue(int pPropIndex, Property pProperty) {
        Property property = pProperty!=null?pProperty:getPropertyByIndex(pPropIndex);
        if (property.isMany()) {
            PropValue<?> propValue = new NonSequencedPropMultiValue(this, property);
            setPropValue(pPropIndex, propValue);
            return propValue;
        }
        return new NonSequencedPropSingleValue(this, property);
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
    
    @Override
    protected DataStrategy copySequencedDataStrategy(Type pOldType, Type pNewType) {
        SequenceOfValueNodes dataStrategy = new SequenceOfValueNodes(pNewType);
        dataStrategy.setDataObject(getDataObject());
        List<SdoProperty> oldTypeProperties = pOldType.getProperties();
        int oldTypePropertiesSize = oldTypeProperties.size();
        for (int i = 0; i < oldTypePropertiesSize; i++) {
            Object propValueOrValue = getPropValue(i);
            SdoProperty property;
            if (propValueOrValue instanceof PropValue) {
                final PropValue propValue = ((PropValue)propValueOrValue);
                if (propValue.isSet()) {
                    property = propValue.getProperty();
                    if (property.isXmlElement()) {
                        if (propValue.isMany()) {
                            for (Object value: (List)propValue.getValue()) {
                                dataStrategy.addToSequenceWithoutCheck(property, value);
                            }
                        } else {
                            dataStrategy.addToSequenceWithoutCheck(property, propValue.getValue());
                        }
                    } else {
                        dataStrategy.setPropertyWithoutCheck(property, propValue.getValue());
                    }
                }
            } else {
                if (propValueOrValue != UNSET) {
                    property = oldTypeProperties.get(i);
                    if (property.isXmlElement()) {
                        dataStrategy.addToSequenceWithoutCheck(property, propValueOrValue);
                    } else {
                        dataStrategy.setPropertyWithoutCheck(property, propValueOrValue);
                    }
                }
            }
        }
        List<Property> openProperties = getOpenProperties();
        for (int i = oldTypePropertiesSize; i < getPropertiesSize(); i++) {
            Object propValue = getPropValue(i);
            SdoProperty property;
            Object value;
            if (propValue instanceof PropValue) {
                property = ((PropValue)propValue).getProperty();
                value = ((PropValue)propValue).getValue();
            } else {
                value = propValue;
                property = (SdoProperty)openProperties.get(i - oldTypePropertiesSize);
            }
            Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
            if (newProperty != property) {
                PropValue newPropValue = dataStrategy.getPropValue(newProperty, true);
                value = getReplacedValue(value, property, newProperty, newPropValue, null);
                property = (SdoProperty)newProperty;
            }
            if (property.isXmlElement()) {
                if (property.isMany()) {
                    for (Object singleValue: (List)value) {
                        dataStrategy.addToSequenceWithoutCheck(property, singleValue);
                    }
                } else {
                    dataStrategy.addToSequenceWithoutCheck(property, value);
                }
            } else {
                dataStrategy.setPropertyWithoutCheck(property, value);
            }
        }
        if (_changeState != null) {
            throw new UnsupportedOperationException("sequenced type " + pNewType.getName()
                + " can not refine non-sequenced type " + pOldType.getName());
        }
        return dataStrategy;
    }

    @Override
    protected DataStrategy copyDataStrategy(Map<Property, Property> replacedOpenProperties) {
        final Type type = getDataObject().getType();
        NonSequencedDataStrategy dataStrategy;
        if (type.isOpen()) {
            dataStrategy = new OpenNonSequencedDataStrategy(type);
        } else {
            dataStrategy = new NonSequencedDataStrategy(type);
        }
        dataStrategy.setReadOnlyMode(isReadOnlyMode());
        dataStrategy.setDataObject(getDataObject());
        List typeProperties = type.getProperties();
        int typePropertiesSize = typeProperties.size();
        for (int i = 0; i < typePropertiesSize; i++) {
            Object propValueOrValue = getPropValue(i);
            Property property;
            if (propValueOrValue instanceof PropValue) {
                final PropValue propValue = ((PropValue)propValueOrValue);
                if (propValue.isSet()) {
                    property = propValue.getProperty();
                    dataStrategy.setPropertyWithoutCheck(property, propValue.getValue());
                }
            } else {
                if (propValueOrValue != UNSET) {
                    property = (Property)typeProperties.get(i);
                    dataStrategy.setPropertyWithoutCheck(property, propValueOrValue);
                }
            }
        }
        List<Property> openProperties = getOpenProperties();
        for (int i = typePropertiesSize; i < getPropertiesSize(); i++) {
            Object propValue = getPropValue(i);
            Property property;
            Object value;
            if (propValue instanceof PropValue) {
                property = ((PropValue)propValue).getProperty();
                value = ((PropValue)propValue).getValue();
            } else {
                value = propValue;
                property = openProperties.get(i - typePropertiesSize);
            }
            Property replacementProp = replacedOpenProperties.get(property);
            if (replacementProp != null) {
                PropValue newPropValue = dataStrategy.getPropValue(replacementProp, true);
                value = getReplacedValue(value, property, replacementProp, newPropValue, null);
                property = replacementProp;
            }
            dataStrategy.setPropertyWithoutCheck(property, value);
        }
        if (_changeState != null) {
            dataStrategy.setChangeStateWithoutCheck(_changeState.getState());
            Map<Property, Object> oldPropertyValues = _changeState.getOldNonSequencedPropertyValues();
            if (oldPropertyValues != null) {
                Set<Entry<Property, Object>> oldEntries = oldPropertyValues.entrySet();
                for (Entry<Property, Object> oldEntry: oldEntries) {
                    Property property = oldEntry.getKey();
                    Object value = oldEntry.getValue();
                    if (property.isOpenContent()) {
                        Property replacementProp = replacedOpenProperties.get(property);
                        if (replacementProp != null) {
                            value = getReplacedValue(value, property, replacementProp, null, getDataObject());
                            property = replacementProp;
                        }
                    }
                    dataStrategy.setOldPropertyWithoutCheck(property, value);
                }
            }
            PropValue containmentPropValue = _changeState.getOldContainmentPropValue();
            if (containmentPropValue != null) {
                dataStrategy.setOldContainerWithoutCheck(containmentPropValue.getDataObject(), containmentPropValue.getProperty());
            }
        }
        return dataStrategy;
    }
    
    @Override
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
        if (!pOpenProperties.isEmpty()) {
            throw new UnsupportedOperationException("setOpenProperties");
        }
    }

    public List<Property> getOpenProperties() {
        return null;
    }
    
}
