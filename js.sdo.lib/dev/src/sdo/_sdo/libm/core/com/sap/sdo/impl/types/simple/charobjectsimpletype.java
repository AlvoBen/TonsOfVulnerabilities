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

public class CharObjectSimpleType extends JavaSimpleType<Character>
{
    private static final long serialVersionUID = -2411392181243444352L;

    CharObjectSimpleType() {
		this(URINamePair.CHARACTEROBJECT,Character.class);
	}

    CharObjectSimpleType(URINamePair unp, Class<Character> clz) {
        super(unp, clz);
    }

    @Override
    public Character convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Character) {
            return ((Character) data);
        }
        if (data instanceof String) {
            String s = (String)data;
            if (s.length() > 1) {
                s = s.trim();
                if (s.length() > 1) {
                    throw new ClassCastException("Can not convert from " + data.getClass().getName() +
                        " to " + getInstanceClass().getName());
                }
            }
            if (s.length() == 0) {
                return getDefaultValue();
            }
            return Character.valueOf(s.charAt(0));
        }
        return convertFromWrapperOrEx(data);
    }

    @Override
    public <T> T convertToJavaClass(Character data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==Character.class) {
            return (T)data;
        }
        if (targetType==String.class) {
            if (data.charValue() == (char)0) {
                return (T)"";
            }
            return (T)data.toString();
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public JavaSimpleType<Character> getNillableType() {
        return CHARACTEROBJECT;
    }
    
    @Override
    public JavaSimpleType<?> getInternalBaseType() {
        return STRING;
    }
    
}
