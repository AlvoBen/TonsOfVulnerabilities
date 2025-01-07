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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.DataObjectType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.UndecidedType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;

public abstract class AbstractDefaultDataStrategy extends AbstractDataStrategy {

    private static final long serialVersionUID = -6489089338433304244L;
    private Object[] _propValues;

    public AbstractDefaultDataStrategy(Type pType) {
        super();
        _propValues = new Object[pType.getProperties().size()];
        Arrays.fill(_propValues, UNSET);
    }

    @Override
    public int getPropertiesSize() {
        return _propValues.length;
    }

    protected Object getPropValue(int pIndex) {
        return _propValues[pIndex];
    }

    protected void setPropValue(int pIndex, Object pValue) {
        _propValues[pIndex] = pValue;
    }

    protected void addPropValue(Object pValue) {
        Object[] newPropValues = new Object[_propValues.length + 1];
        System.arraycopy(_propValues, 0 , newPropValues, 0, _propValues.length);
        newPropValues[_propValues.length] = pValue;
        _propValues = newPropValues;
    }

    protected Object removePropValue(int pIndex) {
        Object removed = _propValues[pIndex];
        Object[] newPropValues = new Object[_propValues.length - 1];
        System.arraycopy(_propValues, 0 , newPropValues, 0, pIndex);
        System.arraycopy(_propValues, pIndex + 1 , newPropValues, pIndex, newPropValues.length - pIndex);
        _propValues = newPropValues;
        return removed;
    }

    public DataStrategy refineType(Type pOldType, Type pNewType) {
        if (pNewType.isSequenced()) {
            return copySequencedDataStrategy(pOldType, pNewType);
        }
        return copyNonSequencedDataStrategy(pOldType, pNewType);
    }

    protected DataStrategy copyNonSequencedDataStrategy(Type pOldType, Type pNewType) {
        NonSequencedDataStrategy dataStrategy;
        if (pNewType.isOpen()) {
            dataStrategy = new OpenNonSequencedDataStrategy(pNewType);
        } else {
            dataStrategy = new NonSequencedDataStrategy(pNewType);
        }
        dataStrategy.setDataObject(getDataObject());
        List<Property> oldTypeProperties = pOldType.getProperties();
        int oldTypePropertiesSize = oldTypeProperties.size();
        for (int i = 0; i < oldTypePropertiesSize; i++) {
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
                    property = oldTypeProperties.get(i);
                    dataStrategy.setPropertyWithoutCheck(property, propValueOrValue);
                }
            }
        }
        List<Property> openProperties = getOpenProperties();
        for (int i = oldTypePropertiesSize; i < getPropertiesSize(); i++) {
            Object propValue = getPropValue(i);
            Property property;
            Object value;
            if (propValue instanceof PropValue) {
                property = ((PropValue)propValue).getProperty();
                value = ((PropValue)propValue).getValue();
            } else {
                value = propValue;
                property = openProperties.get(i - oldTypePropertiesSize);
            }
            Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
            if (newProperty != property) {
                PropValue newPropValue = dataStrategy.getPropValue(newProperty, true);
                value = getReplacedValue(value, property, newProperty, newPropValue, null);
                property = newProperty;
            }
            dataStrategy.setPropertyWithoutCheck(property, value);
        }
        if (_changeState != null) {
            dataStrategy.setChangeStateWithoutCheck(_changeState.getState());
            Map<Property, Object> oldNonSequencedPropertyValues = _changeState.getOldNonSequencedPropertyValues();
            if (oldNonSequencedPropertyValues != null) {
                Set<Entry<Property, Object>> oldEntries = oldNonSequencedPropertyValues.entrySet();
                for (Entry<Property, Object> oldEntry: oldEntries) {
                    Property property = oldEntry.getKey();
                    Object value = oldEntry.getValue();
                    if (property.isOpenContent()) {
                        Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
                        if (newProperty != property) {
                            value = dataStrategy.getReplacedValue(value, property, newProperty, null, getDataObject());
                            property = newProperty;
                        }
                    }
                    dataStrategy.setOldPropertyWithoutCheck(property, value);
                }
            }
            Sequence oldSequence = _changeState.getOldSequence();
            if (oldSequence != null) {
                for (int i = 0; i < oldSequence.size(); i++) {
                    Property property = oldSequence.getProperty(i);
                    if (property != null) {
                        Object singleValue = oldSequence.getValue(i);
                        if (property.isOpenContent()) {
                            Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
                            if (newProperty != property) {
                                singleValue = getReplacedSingleValue(singleValue, property, newProperty, null, getDataObject());
                                property = newProperty;
                            }
                        }
                        if (property.isMany()) {
                            dataStrategy.addToOldPropertyWithoutCheck(property, singleValue);
                        } else {
                            dataStrategy.setOldPropertyWithoutCheck(property, singleValue);
                        }
                    }
                }
            }
            PropValue containmentPropValue = _changeState.getOldContainmentPropValue();
            if (containmentPropValue != null) {
                dataStrategy.setOldContainerWithoutCheck(containmentPropValue.getDataObject(), containmentPropValue.getProperty());
            }
        }
        return dataStrategy;
    }

    protected DataStrategy copySequencedDataStrategy(Type pOldType, Type pNewType) {
        throw new UnsupportedOperationException("sequenced type " + pNewType.getName()
            + " can not refine non-sequenced type " + pOldType.getName());
    }

    protected DataStrategy copyDataStrategy(Map<Property, Property> replacedOpenProperties) {
        return this;
    }

    protected Object getReplacedValue(Object pValue, Property pOldProperty, Property pNewProperty, PropValue pNewContainmentPropValue, DataObject pOldContainer) {
        if (!pOldProperty.isMany()) {
            return getReplacedSingleValue(pValue, pOldProperty, pNewProperty, pNewContainmentPropValue, pOldContainer);
        }
        List oldValue = (List)pValue;
        if (pNewProperty.isMany()) {
            List newValue = new ArrayList(oldValue.size());
            for (Object singleOldValue: oldValue) {
                newValue.add(getReplacedSingleValue(singleOldValue, pOldProperty, pNewProperty, pNewContainmentPropValue, pOldContainer));
            }
            return newValue;
        }
        Object singleOldValue = UNSET;
        if (oldValue.size() == 1) {
            singleOldValue = oldValue.get(0);
        }
        return getReplacedSingleValue(singleOldValue, pOldProperty, pNewProperty, pNewContainmentPropValue, pOldContainer);
    }

    protected Object getReplacedSingleValue(Object pValue, Property pOldProperty, Property pNewProperty, PropValue pNewContainmentPropValue, DataObject pOldContainer) {
        if (pValue instanceof DataObjectDecorator) {
            DataObjectDecorator valueDo = (DataObjectDecorator)pValue;
            if (pNewProperty.getType().isDataType()) {
                if (valueDo.getType() == UndecidedType.getInstance()) {
                    if (valueDo.getSequence().size() == 1) {
                        Object value = valueDo.getSequence().getValue(0);
                        return ((SdoType)pNewProperty.getType()).convertFromJavaClass(value);
                    }
                    return null;
                }
                SdoProperty valueProperty = TypeHelperImpl.getSimpleContentProperty(valueDo);
                if (valueProperty != null) {
                    return valueDo.get(valueProperty);
                }
            } else if (pNewProperty.isContainment()) {
                if (pNewContainmentPropValue != null) {
                    valueDo.getInstance().setContainerWithoutCheck(pNewContainmentPropValue);
                    if (pOldContainer == null) {
                        PropValue oldContainmentPropValue = valueDo.getInstance().getOldContainmentPropValue();
                        if ((oldContainmentPropValue != null) && oldContainmentPropValue.getProperty().equals(pOldProperty)) {
                            AbstractDataStrategy dataStrategy = (AbstractDataStrategy)valueDo.getInstance().getDataStrategy();
                            dataStrategy.getOrCreateChangeState().setOldContainerWithoutCheck(pNewContainmentPropValue);
                        }
                    }
                }
                if (pOldContainer != null) {
                    valueDo.getInstance().setOldContainerWithoutCheck(pOldContainer, pNewProperty);
                }
            }
        }
        return pValue;
    }

    protected Property getTypePropertyForOpenContentProperty(Property property, Type pNewType) {
        if (property.getContainingType() == null) {
            // This means it's an open but not a global Property
            SdoProperty prop = (SdoProperty)property;
            String uri = prop.getUri();
            if (uri == null) {
                uri = "";
            }
            Property newProperty =
                ((SdoType<?>)pNewType).getPropertyFromXmlName(uri, prop.getXmlName(), prop.isXmlElement());
            if (newProperty != null) {
                return newProperty;
            }
        }
        return property;
    }

    @Override
    public void addOpenProperty(Property pProperty) {
        addPropValue(UNSET);
    }

    @Override
    public void reactivateOpenPropValue(PropValue<?> pPropValue) {
        addPropValue(pPropValue);
    }

    public PropValue<?> getPropValue(int pPropIndex, Property pProperty, boolean pCreate) {
        if (pProperty != null && !((SdoProperty)pProperty).defined()) {
//            getHelperContext().getTypeHelper().defineOpenContentProperty(null, (DataObject)pProperty);
            throw new IllegalArgumentException("Property " + pProperty.getName() + " is not defined by TypeHelper");
        }
        Object object = getPropValue(pPropIndex);
        if (object instanceof PropValue<?>) {
            return (PropValue<?>)object;
        }
        if (UNSET == object) {
            if (pCreate) {
                return createPropValue(pPropIndex, pProperty);
            }
            return null;
        }
        Property property = pProperty!=null?pProperty:getPropertyByIndex(pPropIndex);
        //for containment Properties the PropValue already exists as containment PropValue
        if (property.isContainment() && object instanceof DataObjectDecorator) {
            DataObjectDecorator dataObject = (DataObjectDecorator)object;
            final PropValue<?> containmentPropValue = dataObject.getInstance().getContainmentPropValue();
            //containmentPropValue could be null if it is an unfinished DataObject
            if (containmentPropValue != null) {
                return containmentPropValue;
            }
        }
        // create the PropValue on demand
        return new NonSequencedPropSingleValue(this, property);
    }

    @Override
    public void removeOpenPropValue(int pPropertyIndex) {
        removePropValue(pPropertyIndex);
    }

    @Override
    public DataStrategy simplifyOpenContent() {
        Map<Property, Property> replacedOpenProperties = getReplacedOpenProperties();
        if (replacedOpenProperties.isEmpty()) {
            return this;
        }
        return copyDataStrategy(replacedOpenProperties);
    }

    private Map<Property, Property> getReplacedOpenProperties() {
        final Type type = getDataObject().getType();
        if (!type.isOpen()) {
            return Collections.emptyMap();
        }
        Map<Property, Property> replacedProperties = new HashMap<Property, Property>();
        int offset = type.getProperties().size();
        Set<Property> oldOpenProperties = new HashSet<Property>(getOldOpenProperties());
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            for (int i = 0; i < openProperties.size(); i++) {
                Property openProperty = openProperties.get(i);
                if (openProperty.isMany() && (openProperty.getType() == DataObjectType.getInstance())) {
                    List<DataObject> objects = (List<DataObject>)getPropValue(offset + i);
                    List<DataObject> oldObjects = null;
                    if (oldOpenProperties.remove(openProperty)) {
                        oldObjects = (List<DataObject>)getOldSavedValue(openProperty);
                    }
                    if (oldObjects == null) {
                        oldObjects = Collections.emptyList();
                    }
                    Property replacementProperty = getReplacementProperty(openProperty, objects, oldObjects);
                    if (replacementProperty != null) {
                        replacedProperties.put(openProperty, replacementProperty);
                    }
                }
            }
        }
        for (Property openProperty: oldOpenProperties) {
            if (openProperty.isMany() && (openProperty.getType() == DataObjectType.getInstance())) {
                List<DataObject> objects = Collections.emptyList();
                List<DataObject> oldObjects = (List<DataObject>)getOldSavedValue(openProperty);
                Property replacementProperty = getReplacementProperty(openProperty, objects, oldObjects);
                if (replacementProperty != null) {
                    replacedProperties.put(openProperty, replacementProperty);
                }
            }
        }

        return replacedProperties;
    }

    Property getReplacementProperty(Property pUndecidedProperty, List<DataObject> pObjects, List<DataObject> pOldObjects) {
        final DataObject undecidedPropObj = (DataObject)pUndecidedProperty;
        boolean singleValue = (pObjects.size() <= 1) && (pOldObjects.size() <= 1) && undecidedPropObj.getBoolean(PropertyType.getManyUnknownProperty());
        List<DataObject> allObjects = new ArrayList<DataObject>(pObjects);
        allObjects.addAll(pOldObjects);
        boolean simpleContent = true;
        Type simpleType = null;
        for (DataObject dataObject: allObjects) {
            if (dataObject == null) {
                simpleContent = false;
                continue;
            }
            if (dataObject.getType() == UndecidedType.getInstance()) {
                if (dataObject.getInstanceProperties().size() > 0) {
                    simpleContent = false;
                    break;
                }
                Sequence sequence = dataObject.getSequence();
                if (sequence.size() == 0) {
                    continue;
                }
                if ((sequence.size() == 1) && (sequence.getProperty(0) == null)) {
                    simpleType = getCommonType(simpleType, JavaSimpleType.STRING);
                    continue;
                }
            }
            SdoProperty valueProperty = TypeHelperImpl.getSimpleContentProperty(dataObject);
            if (valueProperty != null) {
                    simpleType = getCommonType(simpleType, valueProperty.getType());
                    continue;
            }
            simpleContent = false;
            break;
        }
        if (!simpleContent && !singleValue) {
            return null;
        }
        DataObject propObj = getHelperContext().getDataFactory().create(PropertyType.getInstance());
        propObj.setString(PropertyType.NAME, pUndecidedProperty.getName());
        String aliasNames = undecidedPropObj.getString(PropertyType.ALIAS_NAME);
        if (aliasNames != null) {
            propObj.setString(PropertyType.ALIAS_NAME, aliasNames);
        }
        String xmlName = undecidedPropObj.getString(PropertyType.getXmlNameProperty());
        if (xmlName != null) {
            propObj.setString(PropertyType.getXmlNameProperty(), xmlName);
        }
        propObj.setBoolean(PropertyType.MANY, !singleValue);
        propObj.setBoolean(PropertyType.CONTAINMENT, !simpleContent);
        propObj.setBoolean(PropertyType.getXmlElementProperty(), true);
        Type type;
        if (simpleContent) {
            type = (simpleType == null)? JavaSimpleType.STRING: simpleType;
        } else {
            type = pUndecidedProperty.getType();
        }
        propObj.set(PropertyType.TYPE, type);
        String ref = undecidedPropObj.getString(PropertyType.getReferenceProperty());
        if (ref != null) {
            propObj.setString(PropertyType.getReferenceProperty(), ref);
        }
        return getHelperContext().getTypeHelper().defineOpenContentProperty(null, propObj);
    }

    private Type getCommonType(Type pCommonType, Type pCurrentType) {
        if (pCommonType == null) {
            return pCurrentType;
        }
        return ((TypeHelperImpl)getHelperContext().getTypeHelper()).getCommonType((SdoType)pCommonType, (SdoType)pCurrentType);
    }

    @Override
    public void trimMemory() {
        super.trimMemory();
        for(Object value: _propValues) {
            if (value instanceof PropValue) {
                ((PropValue)value).trimMemory();
            }
        }
    }

}
