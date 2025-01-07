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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import commonj.sdo.Property;

/**
 * Inherit from this class to implement a non-sequenced multi-value property.
 * @author D042807
 */
public abstract class AbstractNonSequencedPropMultiValue extends AbstractPropMultiValue implements Serializable{

    private static final long serialVersionUID = 1904887265104219331L;

    protected AbstractNonSequencedPropMultiValue(AbstractDataStrategy pDataStrategy, Property property) {
        super(pDataStrategy, property);
    }

    public void setValue(Object pValue) {
        if (pValue == null) {
            unset();
            return;
        }
        if (this.equals(pValue)) {
            return;
        }
        PropertyChangeContext propertyChangeContext = new PropertyChangeContext(this, false);
        final Collection<Object> values;
        if (pValue instanceof Collection) {
            values = (Collection<Object>)pValue;
        } else {
            values = Collections.singletonList(pValue);
        }
        List<Object> newList = propertyChangeContext.checkAndNormalizeValues(values);
        clear(propertyChangeContext);
        addAll(size(), newList, propertyChangeContext);
    }

    @Override
    protected void add(int pIndex, Object pElement, PropertyChangeContext pChangeContext) {
        if (isEmpty()) {
            //ensure that it exists
            _dataStrategy.reactivatePropValue(this);
        }
        pChangeContext.checkSaveOldValue();
        internAdd(pIndex, null);
        try {
            set(pIndex, pElement, pChangeContext);
        } catch (IllegalArgumentException e) {
            internRemove(pIndex);
            throw e;
        }
    }

    @Override
    protected Object remove(int pIndex, PropertyChangeContext pChangeContext) {
        Object oldValue = set(pIndex, null, pChangeContext);
        internRemove(pIndex);
        if (isEmpty()) {
            _dataStrategy.internUnset(getProperty(), true);
        }
        return oldValue;
    }

    @Override
    public Object set(int pIndex, Object pElement, PropertyChangeContext pChangeContext) {
        Object oldValue = get(pIndex);
        Object newValue = pChangeContext.replaceAndNormalizeValue(oldValue, pElement);
        internSet(pIndex, newValue);
        return oldValue;
    }

    public List<Object> getOldValue() {
        List<Object> oldValue = (List<Object>)_dataStrategy.getOldSavedValue(getProperty());
        if (oldValue == null) {
            oldValue = new ArrayList<Object>(this);
        }
        return Collections.unmodifiableList(oldValue);
    }
    
    public boolean isModified() {
        return _dataStrategy.getOldSavedValue(getProperty()) != null;
    }

    @Override
    protected void saveOldValue() {
        _dataStrategy.saveValue(getProperty(), new ArrayList<Object>(this));
    }

    public void setWithoutCheck(List<Object> pValues) {
        internClear();
        for (Object value: pValues) {
            addWithoutCheck(value);
        }
    }
    
    public void addWithoutCheck(Object pValue) {        
        internAdd(AbstractDataStrategy.convert(getProperty(), pValue));
    }
    
    /**
     * Add a value to the intern data structure.
     * @param pValue A DataObject or simple data.
     * @see List#add(E)
     */
    protected abstract void internAdd(Object pValue);
    
    /**
     * Add a value to the intern data structure.
     * @param pIndex The index.
     * @param pValue A DataObject or simple data.
     * @see List#add(int, E)
     */
    protected abstract void internAdd(int pIndex, Object pValue);
    
    /**
     * Set a value to the intern data structure at an index.
     * @param pValue A DataObject or simple data.
     * @see List#set(int, E)
     */
    protected abstract void internSet(int pIndex, Object pValue);
    
    /**
     * Clears the multi-valued property.
     * @see List#clear()
     */
    protected abstract void internClear();

    /**
     * Removes a value to the intern data structure.
     * @param pIndex The index.
     * @see List#remove(int)
     */
    protected abstract void internRemove(int pIndex);    
    
    
}
