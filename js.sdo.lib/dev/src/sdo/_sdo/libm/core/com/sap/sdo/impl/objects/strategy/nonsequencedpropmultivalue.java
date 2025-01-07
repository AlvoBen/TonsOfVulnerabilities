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
import java.util.Collection;
import java.util.List;

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.util.ArrayListBehavior;
import com.sap.sdo.impl.util.ArrayListContainer;

import commonj.sdo.Property;

public class NonSequencedPropMultiValue extends AbstractNonSequencedPropMultiValue implements ArrayListContainer<Object>, Serializable{

    private static final long serialVersionUID = 7067089479427146787L;
    private Object[] _values = null;
    private int _size = 0;
    
    public NonSequencedPropMultiValue(AbstractDataStrategy pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }

    public int size() {
        return _size;
    }

    public void setSize(int pSize) {
        _size = pSize;
        
    }

    public Object[] getArray() {
        return _values;
    }

    public void setArray(Object[] pArray) {
        _values = pArray;
    }

    public Object[] createArray(int pLength) {
        return new Object[pLength];
    }

    public void increaseModCount() {
        modCount++;
    }

    @Override
    public Object get(int pIndex) {
        Object ret = ArrayListBehavior.get(this, pIndex);
        if (ret instanceof GenericDataObject) {
            return ((GenericDataObject)ret).getFacade();
        }
        return ret;
    }

    @Override
    protected void internAdd(Object pValue) {
        ArrayListBehavior.add(this, _size, pValue);
    }

    @Override
    protected void internAdd(int pIndex, Object pValue) {
        ArrayListBehavior.add(this, pIndex, pValue);
    }

    @Override
    protected void internSet(int pIndex, Object pValue) {
        ArrayListBehavior.set(this, pIndex, pValue);
    }

    @Override
    protected void internClear() {
        ArrayListBehavior.clear(this);
    }
    
    @Override
    public boolean addAll(int pIndex, Collection<? extends Object> pC, PropertyChangeContext pChangeContext) {
        ArrayListBehavior.ensureCapacity(this, _size + pC.size());
        return super.addAll(pIndex, pC, pChangeContext);
    }

    @Override
    protected void internRemove(int pIndex) {
        ArrayListBehavior.remove(this, pIndex);
    }

    @Override
    public void setWithoutCheck(List<Object> pValues) {
        ArrayListBehavior.clear(this);
        ArrayListBehavior.ensureCapacity(this, pValues.size());
        for (Object value: pValues) {
            addWithoutCheck(value);
        }
    }

    public void trimMemory() {
        ArrayListBehavior.trimToSize(this);
    }
    
}
