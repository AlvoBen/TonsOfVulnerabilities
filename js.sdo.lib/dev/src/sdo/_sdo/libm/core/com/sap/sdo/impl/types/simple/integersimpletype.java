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

import java.math.BigInteger;

import com.sap.sdo.api.util.URINamePair;

public class IntegerSimpleType extends JavaSimpleType<BigInteger>
{
    private static final long serialVersionUID = 1954247302047014301L;

    IntegerSimpleType() {
        super(URINamePair.INTEGER, BigInteger.class);
    }

    @Override
    public BigInteger convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
        if (data instanceof BigInteger) {
            return (BigInteger) data;
        }
        if (data instanceof byte[]) {
            return new BigInteger((byte[])data);
        }
        if (data instanceof Number) {
            return convertToBigInteger(((Number)data));
        }
        if (data instanceof String) {
            return new BigInteger(((String)data).trim());
        }
        return convertFromWrapperOrEx(data);
    }

    @Override
    public <T> T convertToJavaClass(BigInteger data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==BigInteger.class) {
            return (T)data;
        }
        if (targetType==byte[].class) {
            return (T)data.toByteArray();
        }
        if (targetType==String.class) {
            return (T)data.toString();
        }
        if (Number.class.isAssignableFrom(targetType) && targetType!=Short.class && targetType!=Byte.class) {
            return convertToNumberType(targetType, data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public BigInteger copy(BigInteger o, boolean shallow) {
        return new BigInteger(o.toByteArray());
    }
    
    @Override
    public JavaSimpleType<?> getInternalBaseType() {
        return DECIMAL;
    }
    
}
