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

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.SettingImpl;

import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.ChangeSummary.Setting;

public class ReadOnlySequence implements Sequence, Serializable {

    private static final long serialVersionUID = -6125267974282441102L;

    private final List<Setting> _sequence;

    public ReadOnlySequence(List<Setting> pSequence) {
        _sequence = pSequence;
    }
    
    public ReadOnlySequence(Sequence pSequence) {
        _sequence = new ArrayList<Setting>(pSequence.size());
        for (int i = 0; i < pSequence.size(); i++) {
            _sequence.add(new SettingImpl(pSequence.getProperty(i), pSequence.getValue(i)));
        }
    }

    public void add(int pIndex, int propertyIndex, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public boolean add(int propertyIndex, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void add(int pIndex, Property property, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void add(int pIndex, String propertyName, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void add(int pIndex, String pText) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void addText(int pIndex, String pText) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public boolean add(Property property, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public boolean add(String propertyName, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void add(String pText) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void addText(String pText) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public Property getProperty(int pIndex) {
        return _sequence.get(pIndex).getProperty();
    }

    public Object getValue(int pIndex) {
        return wrapValue(_sequence.get(pIndex).getValue());
    }

    public void move(int pToIndex, int pFromIndex) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public void remove(int pIndex) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public Object setValue(int pIndex, Object pValue) {
        throw new UnsupportedOperationException("Sequence is read only");
    }

    public int size() {
        return _sequence.size();
    }
    
    public List<Setting> getSequenceAsList() {
        return _sequence;
    }
    
    public Object getOldValue(Property pProperty) {
        if (pProperty.isMany()) {
            return getOldMultiValue(pProperty);
        }
        return getOldSingleValue(pProperty);
    }
    
    public List<Object> getOldMultiValue(Property pProperty) {
        List<Object> oldList = new ArrayList<Object>(size());
        for (Setting oldSetting: _sequence) {
            if (oldSetting.getProperty() == pProperty) {
                oldList.add(oldSetting.getValue());
            }
        }
        return oldList;
    }

    public Object getOldSingleValue(Property pProperty) {
        for (Setting oldSetting: _sequence) {
            if (oldSetting.getProperty() == pProperty) {
                return oldSetting.getValue();
            }
        }
        return UNSET;
    }
      private Object wrapValue(Object pValue) {
        Object result = pValue;
        if (result instanceof DataObjectDecorator) {
            result = ((DataObjectDecorator)result).getInstance().getOldStateFacade();
        }
        return result;
    }

    @Override
    public String toString() {
        return _sequence.toString();
    }

}
