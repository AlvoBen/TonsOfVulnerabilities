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
import java.util.AbstractList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public abstract class AbstractPropMultiValue extends AbstractList<Object> implements PropValue<List<Object>>, Serializable, RandomAccess {

    private static final long serialVersionUID = -1612634060351583194L;
    protected final AbstractDataStrategy _dataStrategy;
    private final SdoProperty _property;
    
    protected AbstractPropMultiValue(AbstractDataStrategy pDataStrategy, Property property) {
        _dataStrategy = pDataStrategy;
        _property = (SdoProperty)property;
    }
    
    public SdoProperty getProperty() {
        return _property;
    }

    public List<Object> getValue() {
        return this;
    }

    public boolean isSet() {
        return !isEmpty();
    }
    
    @Override
    public Object set(int pIndex, Object pElement) {
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(this, false);
        return set(pIndex, propertyChangeContext.checkAndNormalizeValue(pElement), propertyChangeContext);
    }

    protected abstract Object set(int pIndex, Object pElement, PropertyChangeContext pChangeContext);

    public void add(int pIndex, Object pElement) {
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(this, false);
        add(pIndex, propertyChangeContext.checkAndNormalizeValue(pElement), propertyChangeContext);
    }

    protected abstract void add(int pIndex, Object pElement, PropertyChangeContext pChangeContext);
    
    @Override
    public boolean addAll(Collection<? extends Object> pC) {
        return addAll(size(), pC);
    }

    @Override
    public boolean addAll(int pIndex, Collection<? extends Object> pC) {
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(this, false);
        return addAll(pIndex, propertyChangeContext.checkAndNormalizeValues(pC), propertyChangeContext);
    }

    public boolean addAll(int pIndex, Collection<? extends Object> pC, PropertyChangeContext pChangeContext) {
        boolean modified = false;
        Iterator<? extends Object> e = pC.iterator();
        while (e.hasNext()) {
            add(pIndex++, e.next(), pChangeContext);
            modified = true;
        }
        return modified;
    }

    public Object remove(int pIndex) {
        return remove(pIndex, new PropertyChangeContext(this, false));
    }

    protected abstract Object remove(int pIndex, PropertyChangeContext pChangeContext);
    
    @Override
    public void clear() {
        clear(new PropertyChangeContext(this, false));
    }

    public void clear(PropertyChangeContext pChangeContext) {
        while (!isEmpty()) {
            remove(size() - 1, pChangeContext);
        }
    }

    public void unset() {
        clear(new PropertyChangeContext(this, false));
    }

    public <C> C getConvertedValue(Class<C> pClass) {
        if ((pClass == List.class) || (pClass == Object.class)) {
            return (C)getValue();
        }
        int size = size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return getConvertedValue(0, pClass);
        }
        throw new ClassCastException("Can not convert list of size " + size + " to " + pClass.getName());
    }

    public <C> C getConvertedValue(int pIndex, Class<C> pClass) {
        return AbstractDataStrategy.convertValue(get(pIndex), getProperty(), pClass);
    }

    public GenericDataObject getDataObject() {
        return _dataStrategy.getDataObject();
    }

    public boolean isMany() {
        return true;
    }

    public void internAttachOpposite(DataObjectDecorator pDataObject) {
        add(size(), pDataObject, new PropertyChangeContext(this, true));
    }

    public void internDetachOpposite(DataObjectDecorator pDataObject) {
        int index = indexOf(pDataObject);
        // may be DataObject is already removed by containment constraints
        if (index >= 0) {
            remove(index, new PropertyChangeContext(this, true));
        }
    }

    public void checkSaveOldValue() {
        if (!_dataStrategy.isInitialScope() || isModified()) {
            return;
        }
        saveOldValue();
    }
    
    public boolean isReadOnly() {
        return _dataStrategy.isReadOnly(getProperty());
    }

    public int getIndexByPropertyValue(String propName, Object pValue) {
        if (getProperty().getType().isDataType()) {
            return -1;
        }

        Map<Object, Integer> indexMap = null;
        Type propType = getProperty().getType();
        SdoProperty namedProp = (SdoProperty)propType.getProperty(propName);
        if (namedProp != null) {
            DataObject containingObject = getDataObject();
            // If it is a type propety
            // Look up the index map
            Map<Object, Integer>[] indexMaps = getProperty().getIndexMaps(containingObject);
            Object convertedValue = AbstractDataStrategy.convert(namedProp, pValue);
            if (indexMaps != null) {
                indexMap = indexMaps[namedProp.getIndex(propType)];
                if (indexMap != null) {
                    return indexMap.get(convertedValue);
                }
            }
            // Check the conditions for an index map
            if (namedProp.isReadOnly() || getProperty().isContainment() || 
                ((getProperty().getOpposite() != null) && !getProperty().getOpposite().isMany())) {
                if (indexMaps == null) {
                    indexMaps = getProperty().createIndexMaps(containingObject);
                }
                // Build an index map
                List dataObjects = getValue();
                indexMap = new HashMap<Object, Integer>(dataObjects.size() * 4 / 3);
                indexMaps[namedProp.getIndex(propType)] = indexMap;
                for (int i = 0; i < dataObjects.size(); i++) {
                    DataObject dataObject = (DataObject)dataObjects.get(i);
                    Object value = dataObject.get(namedProp);
                    if (!indexMap.containsKey(value)) {
                        indexMap.put(value, Integer.valueOf(i));
                    }
                }
                return indexMap.get(convertedValue);
            }
            // Linear search
            List dataObjects = getValue();
            for (int i = 0; i < dataObjects.size(); i++) {
                DataObject dataObject = (DataObject)dataObjects.get(i);            
                if (convertedValue.equals(dataObject.get(namedProp))) {
                    return i;
                }
            }
            return -1;
        }
        // If it is an open property
        List dataObjects = getValue();
        Object convertedValue = null;
        for (int i = 0; i < dataObjects.size(); i++) {
            DataObject dataObject = (DataObject)dataObjects.get(i);            
            namedProp = (SdoProperty)dataObject.getInstanceProperty(propName);
            if (namedProp != null) {
                if (convertedValue == null) {
                    convertedValue = AbstractDataStrategy.convert(namedProp, pValue);
                } else {
                    // this should be faster, because in most cases it is the same
                    // conversion and so no convert is necessary
                    try {
                        convertedValue = AbstractDataStrategy.convert(namedProp, convertedValue);
                    } catch (RuntimeException e) {
                        // fall back: convert the incomming value (should be a String)
                        convertedValue = AbstractDataStrategy.convert(namedProp, pValue);
                    }
                }
                if (convertedValue.equals(dataObject.get(namedProp))) {
                    return i;
                }                
            }
        }
        return -1;
    }
    
    protected abstract void saveOldValue();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List<?> other = (List<?>) o;
        int size = size();
        if (size != other.size()) {
            return false;
        }
        if (size == 0) {
            return true;
        }
        Iterator<?> it2 = other.iterator();
        for (int i = 0; i < size; i++) {
            Object o1 = get(i);
            Object o2 = it2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
            return false;
        }
        return true;
    }
    
}
