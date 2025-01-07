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

public class ByteObjectSimpleType extends JavaSimpleType<Byte>
{
    private static final long serialVersionUID = 3410632095681026802L;

    ByteObjectSimpleType() {
		this(URINamePair.BYTEOBJECT,Byte.class);
	}

    ByteObjectSimpleType(URINamePair unp, Class<Byte> clz) {
        super(unp, clz);
    }

    @Override
    public Byte convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Byte) {
            return ((Byte) data);
        }
        if (data instanceof String) {
            // take advantage of the cache in Byte
            return Byte.valueOf(Byte.parseByte((String)data));
        }
        if (data instanceof Number) {
            return convertToByte((Number)data);
        }
        return convertFromWrapperOrEx(data);
    }

    @Override
    public <T> T convertToJavaClass(Byte data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==Byte.class) {
            return (T)data;
        }
        if (targetType==String.class) {
            return (T)String.valueOf(data);
        }
        if (Number.class.isAssignableFrom(targetType)) {
            return convertToNumberType(targetType, data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public JavaSimpleType<Byte> getNillableType() {
        return BYTEOBJECT;
    }
    
    @Override
    public JavaSimpleType<?> getInternalBaseType() {
        return SHORTOBJECT;
    }
    
}
