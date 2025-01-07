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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.SapHelperProviderImpl;
import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.SettingImpl;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.impl.util.ArrayListBehavior;
import com.sap.sdo.impl.util.ArrayListContainer;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;

public class SequenceOfValueNodes extends AbstractDefaultDataStrategy
implements Sequence, ArrayListContainer<Object>, Serializable
{
    private static final long serialVersionUID = -1042076412819898139L;
    private static final SdoProperty TEXT_PROPERTY = (SdoProperty)SapHelperProviderImpl.getCoreContext().getTypeHelper()
        .getOpenContentProperty(URINamePair.PROP_SDO_TEXT.getURI(), URINamePair.PROP_SDO_TEXT.getName());
    private Object[] _sequence = null;
    private int _size = 0;
    protected ArrayList<Property> _openProperties;

    public SequenceOfValueNodes(Type pType) {
        super(pType);
    }

    public ValueNode getValueNode(int index) {
        Object valueNode = ArrayListBehavior.get(this, index);
        return valueNode instanceof ValueNode?(ValueNode)valueNode:null;
    }

    public void add(int index, String text) {
        addText(index, text);
    }

    public void addText(int index, String text) {
        add(index, (Property)null, text);
    }

    public void add(String text) {
        addText(text);
    }

    public void addText(String text) {
        add((Property)null, text);
    }

    public void move(int toIndex, int fromIndex) {
        if (toIndex==fromIndex) {
            return;
        }
        //TODO test bounds
        ValueNode valueNode = getValueNode(fromIndex);
        if (valueNode != null && valueNode.getProperty().isMany()) {
            Object value = getValue(fromIndex);
            remove(fromIndex);
            add(toIndex, valueNode.getProperty(), value);
        } else {
            // optimization for single value properties to avoid attach and detache
            setChangeState(State.MODIFIED, true);
            Object value = internRemove(fromIndex);
            internAdd(toIndex, value);
        }
    }

    public void remove(int index) {
        ValueNode valueNode = getValueNode(index);
        if (valueNode != null) {
            SequencedPropValue<?> propValue = (SequencedPropValue<?>)valueNode.getPropValue();
            propValue.internRemoveBySequence(valueNode);
        } else {
            setChangeState(State.MODIFIED, true);            
        }
        internRemove(index);
    }
    
    public void add(int index, Property pProperty, Object value) {
        if ((index < 0 ) || (index > size())) {
            throw new IndexOutOfBoundsException("index: " + index + " not in [0.." + size() + ']');
        }
        if (pProperty == null) {
            setChangeState(State.MODIFIED, true);
            internAdd(index, convertText(value));
            return;
        }
        SequencedPropValue<?> propValue = getPropValue(pProperty);
        ValueNode valueNode = getNewValueNode(propValue);
        if (!propValue.isMany() && propValue.isSet()) {
            throw new IllegalStateException("Single valued property " + pProperty.getName() + " is already set");
        }
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(propValue, false);
        valueNode.setNodeValue(propertyChangeContext.checkAndNormalizeValue(value), propertyChangeContext);
        if (propValue.isMany()) {
            SequencedPropMultiValue propMultiValue = (SequencedPropMultiValue)propValue;
            ValueNode leftValueNode = findNextLeftValueNodeInPropNode(index, propMultiValue);
            propMultiValue.internInsertAfterBySequence(leftValueNode, valueNode);
        }
        internAdd(index, valueNode);
    }

	private SequencedPropValue<?> getPropValue(Property property) {
        if (!((SdoProperty)property).isXmlElement()) {
            throw new IllegalArgumentException("Property " + property.getName() + " can not be found in the sequence because it represents an attribute");
        }
        return (SequencedPropValue<?>)getPropValue(property, true);
	}

    public void add(int index, int propertyIndex, Object value) {
        Property property = getPropertyByIndex(propertyIndex);
        if (property==null) {
            throw new IllegalArgumentException("there is no property of index "+propertyIndex+" on "+getDataObject());
        }
        add(index,property,value);
    }

    public void add(int index, String propertyName, Object value) {
        Property property = getPropertyByNameOrAlias(propertyName);
        if (property==null) {
            if (getDataObject().getType().isOpen()) {
                getDataObject().createOpenProperty(propertyName,
                    Collections.singletonList(value), null);
                move(index, size()-1);
                return;
            }
            throw new IllegalArgumentException("there is no property of name "+propertyName+" on "+getDataObject());
        }
        add(index,property,value);
    }

    public boolean add(Property property, Object value) {
        if (property == null) {
            setChangeState(State.MODIFIED, true);
            internAdd(convertText(value));
            return true;
        }
        SequencedPropValue<?> propValue = getPropValue(property);
        ValueNode valueNode = getNewValueNode(propValue);
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(propValue, false);
        valueNode.setNodeValue(propertyChangeContext.checkAndNormalizeValue(value), propertyChangeContext);
        propValue.internAddBySequence(valueNode);
        internAdd(valueNode);
        return true;
    }
    
    private Object convertText(Object value) {
        String text = JavaSimpleType.STRING.convertFromJavaClass(value);
        return TEXT_PROPERTY.getCachedValue(text);
    }

    public boolean add(int propertyIndex, Object value) {
        Property property = getPropertyByIndex(propertyIndex);
        if (property==null) {
            throw new IllegalArgumentException("there is no property of index "+propertyIndex+" on "+getDataObject());
        }
        return add(property,value);
    }

    public boolean add(String propertyName, Object value) {
        Property property = getPropertyByNameOrAlias(propertyName);
        if (property==null) {
            if (getDataObject().getType().isOpen()) {
                getDataObject().createOpenProperty(propertyName,
                    Collections.singletonList(value), null);
                return true;
            }
            throw new IllegalArgumentException("there is no property of name "+propertyName+" on "+getDataObject());
        }
        return add(property,value);
    }

    public Object setValue(int index, Object value) {
        ValueNode valueNode = getValueNode(index);
        if (valueNode != null) {
            PropertyChangeContext propertyChangeContext = new PropertyChangeContext(valueNode.getPropValue(), false);
            return valueNode.setNodeValue(propertyChangeContext.checkAndNormalizeValue(value), propertyChangeContext);
        }
        setChangeState(State.MODIFIED, true);
        return ArrayListBehavior.set(this, index, convertText(value));
    }

    public Object getValue(int index) {
        Object valueNode = ArrayListBehavior.get(this, index);
        if (valueNode instanceof String) {
            return valueNode;
        }
        return ((ValueNode)valueNode).getNodeValue();
    }

    public Property getProperty(int index) {
        Object valueNode = ArrayListBehavior.get(this, index);
        if (valueNode instanceof String) {
            return null;
        }
        return ((ValueNode)valueNode).getProperty();
    }

    public int size() {
        return _size;
    }
    
    public void setSize(int pSize) {
        _size = pSize;
    }

    public Object[] getArray() {
        return _sequence;
    }

    public void setArray(Object[] pArray) {
        _sequence = pArray;
    }

    public Object[] createArray(int pLength) {
        return new Object[pLength];
    }

    public void increaseModCount() {
        // mod count is not needed
    }

    public DataObject createDataObject(Property property, Type type) {
        DataObject dO = getHelperContext().getDataFactory().create(type);
        if (property.isMany()) {
            add(property, dO);
        } else {
            getDataObject().set(property, dO);
        }
        return dO;
    }

    @Override
    public void asString(StringBuilder b) {
        b.append('{');
        for (int i = 0; i < _size; i++) {
            Property property = getProperty(i);
            if (property != null) {
                b.append(property.getName());
                b.append(':');
                Object v = getValue(i);
                valueAsString(b, property, v);
                if (i < _size - 1) {
                    b.append("; ");
                }
            }
        }
        b.append('}');
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        asString(b);
        return b.toString();
    }

    public void internRemove(ValueNode pValueNode, int pStartSearchIndex) {
        int index = internIndexOf(pValueNode, pStartSearchIndex);
        internRemove(index);
    }
    
    public Object internRemove(int pIndex) {
        return ArrayListBehavior.remove(this, pIndex);
    }

    public void internInsertBefore(ValueNode currentValueNode, int pStartSearchIndex, ValueNode pNewValueNode) {
        int index = internIndexOf(currentValueNode, pStartSearchIndex);
        internAdd(index, pNewValueNode);
    }
    
    public void internInsertAfter(ValueNode currentValueNode, int pStartSearchIndex, ValueNode pNewValueNode) {
        int index = internIndexOf(currentValueNode, pStartSearchIndex) + 1;
        internAdd(index, pNewValueNode);
    }
    
    void internAdd(int pIndex, Object pValueNode) {
        ArrayListBehavior.add(this, pIndex, pValueNode);
    }
    
    void internAdd(Object pValueNode) {
        ArrayListBehavior.add(this, _size, pValueNode);
    }

    int internIndexOf(ValueNode pValueNode, int pStartSearchIndex) {
        for (int i = pStartSearchIndex; i < _size; i++) {
            if (_sequence[i] == pValueNode) {
                return i;
            }
        }
        return -1;
    }
    
    ValueNode findNextLeftValueNodeInPropNode(int pIndexInSequence, SequencedPropMultiValue pPropMultiValue) {
        if (pPropMultiValue.isEmpty()) {
            return null;
        }
        Property property = pPropMultiValue.getProperty();
        for (int i = pIndexInSequence -1; i >= 0; i--) {
            ValueNode valueNode = getValueNode(i);
            if (valueNode != null && property.equals(valueNode.getProperty())) {
                return valueNode;
            }
        }
        return null;
    }

    @Override
    public Sequence getSequence() {
        return this;
    }

    public PropValue<?> createPropValue(int pPropIndex, Property pProperty) {
        SdoProperty property = (SdoProperty)(pProperty!=null?pProperty:getPropertyByIndex(pPropIndex));
        PropValue<?> propValue;
        if (property.isMany()) {
            propValue = new SequencedPropMultiValue(this, property);
            setPropValue(pPropIndex, propValue);
        } else {
            if (property.isXmlElement()) {
                propValue = new SequencedPropSingleValue(this, property);
                setPropValue(pPropIndex, propValue);
            } else {
                propValue = new NonSequencedPropSingleValue(this, property);
            }
        }
        return propValue;
    }

    @Override
    public List<Property> getOldChangedProperties() {
        List<Property> changedProperties = new ArrayList<Property>(getOldChangedNonSequencedProperties());
        for (Property property: getOldAndNewInstanceProperties()) {
            if (((SdoProperty)property).isXmlElement()) {
                PropValue<?> propValue =
                    getPropValue(property, !property.isOpenContent());
                if (propValue == null) {
                    if (UNSET != getOldValue(property)) {
                        changedProperties.add(property);
                    }
                } else {
                    if (propValue.isModified()) {
                        changedProperties.add(property);
                    }
                }
            }
        }
        Collections.sort(changedProperties, new PropertyComperator(getOpenProperties(), getOldOpenProperties()));
        return changedProperties;
    }

    private List<Property> getOldAndNewInstanceProperties() {
        List<Property> properties = new ArrayList<Property>(getDataObject().getType().getProperties());
        List<Property> oldOpenProperties = getOldOpenProperties();
        properties.addAll(oldOpenProperties);
        List<Property> openProperties = getOpenProperties();
        if (openProperties != null) {
            for (Property property: openProperties) {
                if (!oldOpenProperties.contains(property)) {
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    @Override
    protected List<Setting> calculateOldSequence() {
        List<Setting> oldSequence = new ArrayList<Setting>(_size);
        for (int i = 0; i < _size; i++) {
            Property property;
            Object singleValue;
            if (_sequence[i] instanceof String) {
                property = null;
                singleValue = _sequence[i];
            } else {
                ValueNode valueNode = (ValueNode)_sequence[i];
                property = valueNode.getProperty();
                singleValue = valueNode.getNodeValue();
            }
            oldSequence.add(new SettingImpl(property, singleValue));
        }
        return oldSequence;
    }
    
    @Override
    public void setSequenceWithoutCheck(List<Setting> pSettings) {
        ArrayListBehavior.clear(this);
        ArrayListBehavior.ensureCapacity(this, pSettings.size());
        List<PropValue<?>> propValues = getPropValues(false);
        for (PropValue<?> propValue: propValues) {
            SdoProperty property = propValue.getProperty();
            if (property.isXmlElement()) {
                ((SequencedPropValue<?>)propValue).internClearBySequence();
            }
        }
        for (Setting setting: pSettings) {
            addToSequenceWithoutCheck(setting.getProperty(), setting.getValue());
        }
    }
    
    @Override
    public PropValue<List<Object>> addToPropertyWithoutCheck(Property pProperty, Object pValue) {
        throw new UnsupportedOperationException("Not supported for sequenced DataObjects");
    }

    @Override
    public PropValue<?> addToSequenceWithoutCheck(Property pProperty, Object pValue) {
        if (pProperty == null) {
            internAdd(convertText(pValue));
            return null;
        }
        SequencedPropValue<?> propValue = getPropValue(pProperty);
        ValueNode valueNode = getNewValueNode(propValue);
        valueNode.setWithoutCheck(AbstractDataStrategy.convert(propValue.getProperty(), pValue));
        propValue.internAddBySequence(valueNode);
        internAdd(valueNode);
        return propValue;
    }

    private ValueNode getNewValueNode(SequencedPropValue<?> pPropValue) {
        if (pPropValue.isMany()) {
            return new ValueNodeImpl(pPropValue);
        }
        SequencedPropSingleValue propValueNode = (SequencedPropSingleValue)pPropValue;
        if (propValueNode.isSet()) {
            throw new IllegalStateException("Single valued property " + propValueNode.getProperty().getName() + " is already set");
        }
        return propValueNode;
    }

    @Override
    public void addToOldSequenceWithoutCheck(Property pProperty, Object pValue) {
        getOrCreateChangeState().addToOldSequenceWithoutCheck(pProperty, pValue);
    }

    @Override
    public void setOldSequenceWithoutCheck(List<Setting> pSettings) {
        getOrCreateChangeState().setOldSequenceWithoutCheck(pSettings);
    }
    
    @Override
    public Object getOldSavedValue(Property pProperty) {
        if (!((SdoProperty)pProperty).isXmlElement()) {
            return super.getOldSavedValue(pProperty);
        }
        if ((_changeState == null) || (_changeState.getOldSequence() == null)) {
            return null;
        }
        return _changeState.getOldSequence().getOldValue(pProperty);
    }
    
    @Override
    protected DataStrategy copySequencedDataStrategy(Type pOldType, Type pNewType) {
        SequenceOfValueNodes dataStrategy = new SequenceOfValueNodes(pNewType);
        dataStrategy.setDataObject(getDataObject());
        for (int i = 0; i < _size; i++) {
            Property property;
            Object singleValue;
            if (_sequence[i] instanceof String) {
                property = null;
                singleValue = _sequence[i];
            } else {
                ValueNode valueNode = (ValueNode)_sequence[i];
                property = valueNode.getProperty();
                singleValue = valueNode.getNodeValue();
                if (property.isOpenContent()) {
                    Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
                    if (newProperty != property) {
                        PropValue newPropValue = dataStrategy.getPropValue(newProperty, true);
                        singleValue = getReplacedSingleValue(singleValue, property, newProperty, newPropValue, null);
                        property = newProperty;
                    }
                }
            }
            dataStrategy.addToSequenceWithoutCheck(property, singleValue);
        }
        List<SdoProperty> oldTypeProperties = pOldType.getProperties();
        int oldTypePropertiesSize = oldTypeProperties.size();
        for (int i = 0; i < oldTypePropertiesSize; i++) {
            Object propValueOrValue = getPropValue(i);
            if (propValueOrValue instanceof PropValue) {
                final PropValue propValue = ((PropValue)propValueOrValue);
                if (propValue.isSet()) {
                    SdoProperty property = propValue.getProperty();
                    if (!property.isXmlElement()) {
                        dataStrategy.setPropertyWithoutCheck(property, propValue.getValue());
                    }
                }
                
            } else {
                if (propValueOrValue != UNSET) {
                    SdoProperty property = oldTypeProperties.get(i);
                    if (!property.isXmlElement()) {
                        dataStrategy.setPropertyWithoutCheck(property, propValueOrValue);
                    }
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
            if (!((SdoProperty)property).isXmlElement()) {
                Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
                if (newProperty != property) {
                    PropValue newPropValue = dataStrategy.getPropValue(newProperty, true);
                    value = getReplacedValue(value, property, newProperty, newPropValue, null);
                    property = newProperty;
                }
                dataStrategy.setPropertyWithoutCheck(property, value);
            }
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
                            value = getReplacedValue(value, property, newProperty, null, getDataObject());
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
                    Object singleValue = oldSequence.getValue(i);
                    if (property == null) {
                        if (((SdoType)pNewType).isMixedContent()) {
                            dataStrategy.addToOldSequenceWithoutCheck(null, singleValue);
                        }
                    } else {
                        if (property.isOpenContent()) {
                            Property newProperty = getTypePropertyForOpenContentProperty(property, pNewType);
                            if (newProperty != property) {
                                singleValue = getReplacedSingleValue(singleValue, property, newProperty, null, getDataObject());
                                property = newProperty;
                            }
                        }
                        dataStrategy.addToOldSequenceWithoutCheck(property, singleValue);
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

    @Override
    protected DataStrategy copyDataStrategy(Map<Property, Property> replacedOpenProperties) {
        final Type type = getDataObject().getType();
        SequenceOfValueNodes dataStrategy = new SequenceOfValueNodes(type);
        dataStrategy.setReadOnlyMode(isReadOnlyMode());
        dataStrategy.setDataObject(getDataObject());
        for (int i = 0; i < _size; i++) {
            Property property;
            Object singleValue;
            if (_sequence[i] instanceof String) {
                property = null;
                singleValue = _sequence[i];
            } else {
                ValueNode valueNode = (ValueNode)_sequence[i];
                property = valueNode.getProperty();
                singleValue = valueNode.getNodeValue();
                if (property.isOpenContent()) {
                    Property replacementProp = replacedOpenProperties.get(property);
                    if (replacementProp != null) {
                        PropValue newPropValue = dataStrategy.getPropValue(replacementProp, true);
                        singleValue = getReplacedSingleValue(singleValue, property, replacementProp, newPropValue, null);
                        property = replacementProp;
                    }
                }
            }
            dataStrategy.addToSequenceWithoutCheck(property, singleValue);
        }
        List<SdoProperty> typeProperties = type.getProperties();
        int typePropertiesSize = typeProperties.size();
        for (int i = 0; i < typePropertiesSize; i++) {
            Object propValueOrValue = getPropValue(i);
            if (propValueOrValue instanceof PropValue) {
                final PropValue propValue = ((PropValue)propValueOrValue);
                if (propValue.isSet()) {
                    SdoProperty property = propValue.getProperty();
                    if (!property.isXmlElement()) {
                        dataStrategy.setPropertyWithoutCheck(property, propValue.getValue());
                    }
                }
                
            } else {
                if (propValueOrValue != UNSET) {
                    SdoProperty property = typeProperties.get(i);
                    if (!property.isXmlElement()) {
                        dataStrategy.setPropertyWithoutCheck(property, propValueOrValue);
                    }
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
            if (!((SdoProperty)property).isXmlElement()) {
                dataStrategy.setPropertyWithoutCheck(property, value);
            }
        }
        if (_changeState != null) {
            dataStrategy.setChangeStateWithoutCheck(_changeState.getState());
            Map<Property, Object> oldNonSequencedPropertyValues = _changeState.getOldNonSequencedPropertyValues();
            if (oldNonSequencedPropertyValues != null) {
                Set<Entry<Property, Object>> oldEntries = oldNonSequencedPropertyValues.entrySet();
                for (Entry<Property, Object> oldEntry: oldEntries) {
                    Property property = oldEntry.getKey();
                    Object value = oldEntry.getValue();
                    dataStrategy.setOldPropertyWithoutCheck(property, value);
                }
            }
            Sequence oldSequence = _changeState.getOldSequence();
            if (oldSequence != null) {
                for (int i = 0; i < oldSequence.size(); i++) {
                    Property property = oldSequence.getProperty(i);
                    Object singleValue = oldSequence.getValue(i);
                    if (property.isOpenContent()) {
                        Property replacementProp = replacedOpenProperties.get(property);
                        if (replacementProp != null) {
                            singleValue = getReplacedSingleValue(singleValue, property, replacementProp, null, getDataObject());
                            property = replacementProp;
                        }
                    }
                    dataStrategy.addToOldSequenceWithoutCheck(property, singleValue);
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
    public boolean getXsiNil() {
        Boolean xsiNil = getXsiNilIntern();
        if (xsiNil == null) {
            xsiNil = false;
            if (size() == 0) {
                List<SdoProperty> typeProperties = getDataObject().getType().getProperties();
                for (int i = 0; i < typeProperties.size(); i++) {
                    if (typeProperties.get(i).isXmlElement()) {
                        xsiNil = true;
                        break;
                    }
                }
            }
            setXsiNilWithoutCheck(xsiNil);
        }
        return xsiNil;
    }

    @Override
    public void setXsiNil(boolean pXsiNil) {
        if (pXsiNil && !Boolean.TRUE.equals(getXsiNilIntern())) {
            while (size() > 0) {
                remove(0);
            }
        }
        setXsiNilWithoutCheck(pXsiNil);
    }

    @Override
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
        _openProperties = pOpenProperties;
    }

    @Override
    public List<Property> getOpenProperties() {
        return _openProperties;
    }

    @Override
    public void trimMemory() {
        super.trimMemory();
        ArrayListBehavior.trimToSize(this);
        if (_openProperties != null) {
            _openProperties.trimToSize();
        }
    }
    
}
