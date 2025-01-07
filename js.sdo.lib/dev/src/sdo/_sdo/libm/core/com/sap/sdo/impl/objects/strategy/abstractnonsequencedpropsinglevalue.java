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

import commonj.sdo.Property;

/**
 * Inherit from this class to implement a non-sequenced single-value property.
 * @author D042807
 */
public abstract class AbstractNonSequencedPropSingleValue extends AbstractPropSingleValue implements Serializable {

    private static final long serialVersionUID = 8788215995732193587L;

    protected AbstractNonSequencedPropSingleValue(AbstractDataStrategy pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }

    protected void setValue(Object pValue, PropertyChangeContext pChangeContext) {
        Object oldValue = internGetValue();
        Object newValue = pChangeContext.replaceAndNormalizeValue(oldValue, pValue);
        internSetValue(newValue);
    }
    
    protected void unset(PropertyChangeContext pChangeContext) {
        if (isSet()) {
            internRemove(pChangeContext);
        }
    }

    public Object getOldValue() {
        Object oldValue = _dataStrategy.getOldSavedValue(getProperty());
        if (oldValue == null) {
            oldValue = getValue();
        } else if (UNSET == oldValue) {
            oldValue = getProperty().getDefault();
        }
        return oldValue;
    }

    public boolean isModified() {
        return _dataStrategy.getOldSavedValue(getProperty()) != null;
    }

    @Override
    protected void saveOldValue() {
        Object oldValue;
        if (isSet()) {
            oldValue = getValue();
        } else {
            oldValue = UNSET;
        }
        _dataStrategy.saveValue(getProperty(), oldValue);
    }

    public void setWithoutCheck(Object pValue) {
        Object value = pValue;
        if (UNSET == pValue) {
            _dataStrategy.internUnset(getProperty(), false);
        } else {
            value = AbstractDataStrategy.convert(getProperty(), pValue);
            
        }
        internSetValue(value);
    }
    
    public Object getValue() {
        Object value = internGetValue();
        if (UNSET == value) {
            return getProperty().getDefault();
        }
        return value;
    }

    /**
     * Get the value from the intern data structure.
     * @return A DataObject or simple data.
     */
    protected abstract Object internGetValue();

    /**
     * Set the value to the intern data structure.
     * @param pValue A DataObject or simple data.
     */
    protected abstract void internSetValue(Object pValue);

}
