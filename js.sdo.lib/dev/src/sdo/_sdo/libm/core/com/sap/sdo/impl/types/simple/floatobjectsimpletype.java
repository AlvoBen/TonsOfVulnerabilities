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
package com.sap.sdo.impl.types.simple;

import com.sap.sdo.api.util.URINamePair;

public class FloatObjectSimpleType extends JavaSimpleType<Float>
{
    private static final long serialVersionUID = -5418522059273236596L;

    FloatObjectSimpleType() {
		this(URINamePair.FLOATOBJECT,Float.class);
	}

    FloatObjectSimpleType(URINamePair unp, Class<Float> clz) {
        super(unp, clz);
    }
    
    public Float convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Float) {
            return (Float)data;
        }
        if (data instanceof String) {
            return Float.valueOf(((String)data).trim());
        }
        if (data instanceof Number) {
            return convertToFloat(((Number)data));
        }
        return convertFromWrapperOrEx(data);
    }

    public <T> T convertToJavaClass(Float data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==Float.class) {
            return (T)data;
        }
        if (targetType==String.class) {
            return (T)data.toString();
        }
        if (Number.class.isAssignableFrom(targetType)) {
            return convertToNumberType(targetType, data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    public JavaSimpleType<Float> getNillableType() {
        return FLOATOBJECT;
    }
}
