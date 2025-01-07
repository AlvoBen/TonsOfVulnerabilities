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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sap.sdo.impl.objects.DataStrategy.State;
import com.sap.sdo.impl.util.ArrayListBehavior;
import com.sap.sdo.impl.util.ArrayListContainer;

import commonj.sdo.Property;

/**
 * a list for use to model multi-value properties
 * of data objects. This list only relates via sequence
 * and _property to the actual data object content, so that
 * it is safe against concurrent means of modification on
 * the respective data object.
 *  
 * @author hb
 *
 */
public class SequencedPropMultiValue extends AbstractPropMultiValue implements SequencedPropValue<List<Object>>,ArrayListContainer<ValueNode> , Serializable
{
    private static final long serialVersionUID = -1612634060351583194L;
    private ValueNode[] _valueNodes;
    private int _size = 0;
    
    protected SequencedPropMultiValue(SequenceOfValueNodes pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }
    
    public int size() {
        return _size;
    }

    public void setSize(int pSize) {
        _size = pSize;
        
    }

    public ValueNode[] getArray() {
        return _valueNodes;
    }

    public void increaseModCount() {
        modCount++;
    }

    public void setArray(ValueNode[] pArray) {
        _valueNodes = pArray;
    }

    public ValueNode[] createArray(int pLength) {
        return new ValueNode[pLength];
    }

    public Object get(int index) {
        return getValueNode(index).getNodeValue();
    }

    public ValueNode getValueNode(int index) {
        return ArrayListBehavior.get(this, index);
    }

    protected Object remove(int index, PropertyChangeContext pChangeContext) {
        ValueNode valueNode = ArrayListBehavior.get(this, index);
        Object result = valueNode.getNodeValue();
        internRemove(index, pChangeContext);
        ((SequenceOfValueNodes)_dataStrategy).internRemove(valueNode, index);
        return result;
    }
    
    private void internRemove(int index, PropertyChangeContext pChangeContext) {
        ValueNode removedValueNode = ArrayListBehavior.get(this, index);
        removedValueNode.setNodeValue(null, pChangeContext);
        ArrayListBehavior.remove(this, index);
        if (isEmpty()) {
            _dataStrategy.internUnset(getProperty(), true);
        }
    }

    public void internClearBySequence() {
        ArrayListBehavior.clear(this);
    }

    public void internRemoveBySequence(ValueNode pValueNode) {
        internRemove(indexOfValueNode(pValueNode), new PropertyChangeContext(this, false));
    }
    
    private int indexOfValueNode(ValueNode pValueNode) {
        for (int i = 0; i < _size; i++) {
            if (_valueNodes[i] == pValueNode) {
                return i;
            }
        }
        return -1;
    }

    protected void add(int index, Object element, PropertyChangeContext pChangeContext) {
        if (isEmpty()) {
            //ensure that it exists
            _dataStrategy.reactivatePropValue(this);
        }
        ValueNode newValueNode = new ValueNodeImpl(this);
        newValueNode.setNodeValue(element, pChangeContext);
        final SequenceOfValueNodes sequenceOfValueNodes = ((SequenceOfValueNodes)_dataStrategy);
        if (index == size()) {
            sequenceOfValueNodes.internAdd(newValueNode);
        } else if (index == 0){
            sequenceOfValueNodes.internInsertBefore(getValueNode(0), 0, newValueNode);
        } else {
            sequenceOfValueNodes.internInsertAfter(getValueNode(index - 1), index - 1, newValueNode);
        }
        internAdd(index, newValueNode);
    }

    public void internAddBySequence(ValueNode pValueNode) {
        internAdd(size(), pValueNode);
    }

    void internAdd(int index, ValueNode pValueNode) {
        ArrayListBehavior.add(this, index, pValueNode);
    }
    
    void internInsertAfterBySequence(ValueNode pCurrentValueNode, ValueNode pNewValueNode) {
        int index;
        if (pCurrentValueNode == null) {
            index = 0;
        } else {
            index = indexOfValueNode(pCurrentValueNode) + 1;
        }
        internAdd(index, pNewValueNode);
    }

    @Override
    public boolean addAll(int pIndex, Collection<? extends Object> pC, PropertyChangeContext pChangeContext) {
        ArrayListBehavior.ensureCapacity(this, _size + pC.size());
        return super.addAll(pIndex, pC, pChangeContext);
    }

    @Override
    public Object set(int index, Object element, PropertyChangeContext pPropertyChangeContext) {
        return getValueNode(index).setNodeValue(element, pPropertyChangeContext);
    }

    public void setValue(final Object pValue) {
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
        for (int i = 0; i < size(); i++) {
            set(i, null, propertyChangeContext);
        }
        int index = 0;
        Iterator it = newList.iterator();
        while (it.hasNext() && index < size()) {
            Object newValue = it.next();
            set(index, newValue, propertyChangeContext);
            index++;
        }
        while (index < size()) {
            remove(index, propertyChangeContext);
        }
        while (it.hasNext()) {
            Object newValue = it.next();
            add(size(), newValue, propertyChangeContext);
        }
    }

    public List<Object> getOldValue() {
        ReadOnlySequence oldSquence = _dataStrategy.getOldSavedSequence();
        if (oldSquence == null) {
            return getValue();
        }
        return oldSquence.getOldMultiValue(getProperty());
    }

    public boolean isModified() {
        if (_dataStrategy.getOldSavedSequence() == null) {
            return false;
        }
        List<Object> oldValue = getOldValue();
        if (oldValue == null) {
            return isSet();
        }
        return !oldValue.equals(getValue());
    }

    @Override
    protected void saveOldValue() {
        _dataStrategy.setChangeState(State.MODIFIED, false);
    }

    public void trimMemory() {
        ArrayListBehavior.trimToSize(this);
    }
    
}
