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
package com.sap.sdo.impl.objects;

import static com.sap.sdo.impl.objects.GenericDataObject.UNSET;

import java.io.Serializable;
import java.util.Collections;

import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.Property;

public class SettingImpl implements Setting, Serializable {

    private static final long serialVersionUID = 6651884236539704110L;
    private final Property _property;
    private final Object _value;
    
    public SettingImpl(Property pProperty, Object pValue) {
        _property = pProperty;
        _value = pValue;
    }
    
    public Property getProperty() {
        return _property;
    }

    public Object getValue() {
        if (_property==null || _property.isMany() || isSet()) {
            return _value;
        }
        return _property.getDefault();
    }

    public boolean isSet() {
        return (UNSET != _value) && !Collections.emptyList().equals(_value);
    }

    @Override
    public String toString() {
        return _property.getName() + ':' + _value;
    }

}
