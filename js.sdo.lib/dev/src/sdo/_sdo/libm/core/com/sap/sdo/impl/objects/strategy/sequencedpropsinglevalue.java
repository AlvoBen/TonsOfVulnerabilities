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

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.DataStrategy.State;

import commonj.sdo.Property;

public class SequencedPropSingleValue extends AbstractPropSingleValue implements SequencedPropValue<Object>, ValueNode, Serializable {

    private static final long serialVersionUID = 6952778943419597346L;
    private Object _value = UNSET;

    SequencedPropSingleValue(SequenceOfValueNodes pDataStrategy, Property pProperty) {
        super(pDataStrategy, pProperty);
    }

    public Object getValue() {
        if (!isSet()) {
            return getProperty().getDefault();
        }
        return getNodeValue();
    }

    public Object getNodeValue() {
        if (_value instanceof GenericDataObject) {
            return ((GenericDataObject)_value).getFacade();
        }
        return _value;
    }

    public Object getOldValue() {
        ReadOnlySequence oldSquence = _dataStrategy.getOldSavedSequence();
        if (oldSquence == null) {
            return getValue();
        }
        return oldSquence.getOldSingleValue(getProperty());
    }
    
    public boolean isModified() {
        if (_dataStrategy.getOldSavedSequence() == null) {
            return false;
        }
        Object oldValue = getOldValue();
        if (UNSET == oldValue) {
            return isSet();
        }
        return !oldValue.equals(getValue());
    }
    
    @Override
    protected void saveOldValue() {
        _dataStrategy.setChangeState(State.MODIFIED, false);
    }

    protected void setValue(Object pValue, PropertyChangeContext pChangeContext) {
        if (!isSet()) {
            _value = pChangeContext.replaceAndNormalizeValue(null, pValue);
            ((SequenceOfValueNodes)_dataStrategy).internAdd(this);
            return;
        }
        Object oldValue = _value;
        _value = pChangeContext.replaceAndNormalizeValue(oldValue, pValue);
    }
    
    public Object setNodeValue(Object pValue, PropertyChangeContext pChangeContext) {
        if (!isSet()) {
            _value = pChangeContext.replaceAndNormalizeValue(null, pValue);
            return null;
        }
        Object oldValue = _value;
        _value = pChangeContext.replaceAndNormalizeValue(oldValue, pValue);
        return oldValue;
    }

    protected void unset(PropertyChangeContext pChangeContext) {
        if (isSet()) {
            ((SequenceOfValueNodes)_dataStrategy).internRemove(this, 0);
            internRemove(pChangeContext);
        }
    }

    @Override
    protected void internUnset() {
        _value = UNSET;
        
    }

    public boolean isSet() {
        return _value != UNSET;
    }
    
    public void internAddBySequence(ValueNode pValueNode) {
//      pValueNode must be this!
    }

    public void internRemoveBySequence(ValueNode pValueNode) {
//      pValueNode must be this!
        internRemove(new PropertyChangeContext(this, false));
    }
    
    public void internClearBySequence() {
        internUnset();
    }

    public PropValue<?> getPropValue() {
        return this;
    }


    public void setWithoutCheck(Object pValue) {
        _value = pValue;
    }
    
    
}
