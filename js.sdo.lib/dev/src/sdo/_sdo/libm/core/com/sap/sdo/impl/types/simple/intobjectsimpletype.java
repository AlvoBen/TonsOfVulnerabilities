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

public class IntObjectSimpleType extends JavaSimpleType<Integer>
{
    private static final long serialVersionUID = -7051235372382755461L;

    IntObjectSimpleType() {
		this(URINamePair.INTOBJECT,Integer.class);
	}

    IntObjectSimpleType(URINamePair unp, Class<Integer> clz) {
        super(unp, clz);
    }

    @Override
    public Integer convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Integer) {
            return (Integer)data;
        }
        if (data instanceof String) {
            // take advantage of the cache in Integer
            return Integer.valueOf(Integer.parseInt(((String)data).trim()));
        }
        if (data instanceof Number) {
            return convertToInteger(((Number)data));
        }
        return convertFromWrapperOrEx(data);
    }

    @Override
    public <T> T convertToJavaClass(Integer data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==String.class) {
            return (T)data.toString();
        }
        if (targetType==Integer.class) {
            return (T)data;
        }
        if (Number.class.isAssignableFrom(targetType)) {
            return convertToNumberType(targetType, data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public JavaSimpleType<Integer> getNillableType() {
        return INTOBJECT;
    }

    @Override
    public JavaSimpleType<?> getInternalBaseType() {
        return LONGOBJECT;
    }
    
}
