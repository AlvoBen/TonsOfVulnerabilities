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

import java.util.Date;

import com.sap.sdo.api.util.URINamePair;

public class LongObjectSimpleType extends JavaSimpleType<Long>
{
    private static final long serialVersionUID = 4928495488665373403L;

    LongObjectSimpleType() {
		this(URINamePair.LONGOBJECT,Long.class);
	}

    LongObjectSimpleType(URINamePair unp, Class<Long> clz) {
        super(unp, clz);
    }

    @Override
    public Long convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }
        if (data instanceof Long) {
            return (Long)data;
        }
        if (data instanceof String) {
            // take advantage of the cache in Long
            return Long.valueOf(Long.parseLong(((String)data).trim()));
        }
        if (data instanceof Number) {
            return convertToLong(((Number)data));
        }
        if (data instanceof Date) {
            return ((Date)data).getTime();
        }
        return convertFromWrapperOrEx(data);
    }

    @Override
    public <T> T convertToJavaClass(Long data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType == Long.class) {
            return (T)data;
        }
        if (targetType == String.class) {
            return (T)data.toString();
        }
        if (targetType == Date.class) {
            return (T)new Date(data);
        }
        if (Number.class.isAssignableFrom(targetType)) {
            return convertToNumberType(targetType, data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public JavaSimpleType<Long> getNillableType() {
        return LONGOBJECT;
    }
    
    @Override
    public JavaSimpleType<?> getInternalBaseType() {
        return INTEGER;
    }
    
}
