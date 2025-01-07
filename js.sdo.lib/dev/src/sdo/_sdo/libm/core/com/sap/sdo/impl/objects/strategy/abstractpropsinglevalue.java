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

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public abstract class AbstractPropSingleValue implements PropValue<Object>, Serializable {

    private static final long serialVersionUID = -6678966934013117889L;
    private final SdoProperty _property;
    protected final AbstractDataStrategy _dataStrategy;

    AbstractPropSingleValue(AbstractDataStrategy pDataStrategy, Property pProperty) {
        _property = (SdoProperty)pProperty;
        _dataStrategy = pDataStrategy;
    }

    public SdoProperty getProperty() {
        return _property;
    }

    public boolean remove(Object pValue) {
        if (isSet() && (getValue() == pValue)) {
            unset();
            return true;
        }
        return false;
    }

    public void setValue(Object pValue) {
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(this, false);
        Object normalizedValue = propertyChangeContext.checkAndNormalizeValue(pValue);
        if ((normalizedValue == null) && !getProperty().isNullable()) {
            unset(propertyChangeContext);
        } else {
            setValue(normalizedValue, propertyChangeContext);
        }
    }
    
    protected abstract void setValue(Object pValue, PropertyChangeContext pChangeContext);
    
    public void unset() {
        unset(new PropertyChangeContext(this, false));
    }

    protected abstract void unset(PropertyChangeContext pChangeContext);

    public Object set(int pIndex, Object pValue) {
        if (pIndex != 0) {
            throw new IllegalArgumentException(
                "Index of single value properties must be 0 but is " + pIndex);
        }
        setValue(pValue);
        return null;
    }

    public void internRemove(PropertyChangeContext pChangeContext) {
        setValue(UNSET, pChangeContext);
        internUnset();
        _dataStrategy.internUnset(getProperty(), true);
    }
    
    protected abstract void internUnset();
    
    public <C> C getConvertedValue(Class<C> pClass) {
        return AbstractDataStrategy.convertValue(getValue(), getProperty(), pClass);
    }

    public <C> C getConvertedValue(int pIndex, Class<C> pClass) {
        if (pIndex != 0) {
            throw new IllegalArgumentException(
                "Index of single value properties must be 0 but is " + pIndex);
        }
        return getConvertedValue(pClass);
    }

    public GenericDataObject getDataObject() {
        return _dataStrategy.getDataObject();
    }

    public boolean isMany() {
        return false;
    }

    public void internAttachOpposite(DataObjectDecorator pDataObject) {
        PropertyChangeContext changeContext = new PropertyChangeContext(this, true);
        if (!pDataObject.equals(getValue())) {
            setValue(null, changeContext);
        }
        setValue(pDataObject, changeContext);
        
    }

    public void internDetachOpposite(DataObjectDecorator pDataObject) {
        if (pDataObject.equals(getValue())) {
            if (pDataObject.getInstance().isLogging() && !isModified()) {
                //pDataObject is the old parent, but this PropValue is already detached
                saveOldValue();
            }
            unset(new PropertyChangeContext(this, true));
        }
    }

    public void checkSaveOldValue() {
        if (!_dataStrategy.isInitialScope()|| isModified()) {
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
        DataObject dataObject = (DataObject)getValue();
        if (dataObject == null) {
            return -1;
        }
        SdoProperty property = (SdoProperty)dataObject.getInstanceProperty(propName);
        Object value = AbstractDataStrategy.convert(property, pValue);
        if (value.equals(dataObject.get(property))) {
            return 0;
        }
        return -1;
    }

    public void invalidateIndexMaps() {
    }

    public void invalidateIndexMap(SdoProperty property) {
    }

    protected abstract void saveOldValue();

    public void trimMemory() {
    }
    
}
