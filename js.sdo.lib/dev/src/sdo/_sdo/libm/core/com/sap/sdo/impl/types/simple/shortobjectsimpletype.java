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

public class ShortObjectSimpleType extends JavaSimpleType<Short>
{
    private static final long serialVersionUID = -1808076169123853764L;

    ShortObjectSimpleType() {
		this(URINamePair.SHORTOBJECT,Short.class);
	}

    ShortObjectSimpleType(URINamePair unp, Class<Short> clz) {
        super(unp, clz);
    }

    @Override
    public Short convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Short) {
            return (Short)data;
        }
        if (data instanceof String) {
            // take advantage of the cache in Short
            return Short.valueOf(Short.parseShort(((String)data).trim()));
        }
        if (data instanceof Number) {
            return convertToShort(((Number)data));
        }
        return convertFromWrapperOrEx(data);
    }

    @Override
    public <T> T convertToJavaClass(Short data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType == Short.class) {
            return (T)data;
        } 
        if (targetType == String.class) {
            return (T)data.toString();
        }
        if (Number.class.isAssignableFrom(targetType)) {
            return convertToNumberType(targetType, data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public JavaSimpleType<Short> getNillableType() {
        return SHORTOBJECT;
    }

    @Override
    public JavaSimpleType<?> getInternalBaseType() {
        return INTOBJECT;
    }
    
}
