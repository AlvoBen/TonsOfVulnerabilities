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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;

public class StringSimpleType extends JavaSimpleType<String>
{
    private static final long serialVersionUID = -8590252221151373413L;

    StringSimpleType() {
        this(URINamePair.STRING);
    }

    StringSimpleType(URINamePair unp) {
		super(unp, String.class);
	}

	public String convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
        if (data instanceof byte[]) {
            byte[] bytes = (byte[])data;
            StringBuilder buf = new StringBuilder();
            for (int i=0; i<bytes.length; ++i) {
                String hex = Integer.toHexString(bytes[i] & 0xFF).toUpperCase(Locale.ENGLISH);
                if (hex.length() < 2) {
                    buf.append('0');
                }
                buf.append(hex);
            }
            return buf.toString();
        }
        if (data instanceof Date) {
            return JavaSimpleType.DATE.toString((Date)data);
        }
        if (data instanceof DataObject) {
            return convertFromWrapperOrEx(data);
        }
        return data.toString();
    }

    public <T> T convertToJavaClass(String data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==String.class) {
            return (T)data;
        }
        if (targetType==Date.class) {
            return (T)JavaSimpleType.DATE.convertFromJavaClass(data);
        }
        if (targetType==Boolean.class) {
            return (T)Boolean.valueOf(data);
        }
        if (targetType==Byte.class) {
            return (T)Byte.valueOf(data);
        }
        if (targetType==byte[].class) {
            byte[] bytes = new byte[data.length() / 2];
            for (int i = 0, j = 0; i < bytes.length; ++i, ++j) {
                bytes[i] = 
                    (byte)(Character.digit(data.charAt(j), 16) << 4
                        | Character.digit(data.charAt(++j), 16));
            }
            return (T)bytes;
        }
        if (targetType==Double.class) {
            return (T)Double.valueOf(data);
        }
        if (targetType==Float.class) {
            return (T)Float.valueOf(data);
        }
        if (targetType==Integer.class) {
            return (T)Integer.valueOf(data);
        }
        if (targetType==Long.class) {
            return (T)Long.valueOf(data);
        }
        if (targetType==Short.class) {
            return (T)Short.valueOf(data);
        }
        if (targetType==BigDecimal.class) {
            return (T)new BigDecimal(data);
        }
        if (targetType==BigInteger.class) {
            return (T)new BigInteger(data);
        }
        if (targetType==Character.class) {
            return (T)new Character(data.charAt(0));
        }
        return convertToWrapperOrEx(data, targetType);
    }

}
