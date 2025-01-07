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
import java.util.Locale;

import com.sap.sdo.api.util.URINamePair;

public class BytesSimpleType extends JavaSimpleType<byte[]>
{
    private static final long serialVersionUID = 268937658216475569L;

    BytesSimpleType() {
        super(URINamePair.BYTES,byte[].class);
    }

	public byte[] convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
		if (data instanceof byte[]) {
            return (byte[]) data;
        }
		if (data instanceof BigInteger) {
            return ((BigInteger)data).toByteArray();
        }
        if (data instanceof String) {
            String str = (String)data;
            byte[] bytes = new byte[str.length() / 2];
            for (int i = 0, j = 0; i < bytes.length; ++i, ++j) {
                bytes[i] = 
                    (byte)(Character.digit(str.charAt(j), 16) << 4
                        | Character.digit(str.charAt(++j), 16));
            }
            return bytes;
        }
        return convertFromWrapperOrEx(data);
	}

    public <T> T convertToJavaClass(byte[] data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==byte[].class) {
            return (T)data;
        }
        if (targetType==BigInteger.class) {
            return (T)new BigInteger(data);
        }
        if (targetType==String.class) {
            if (data != null) {
                StringBuilder buf = new StringBuilder();
                for (int i=0; i<data.length; ++i) {
                    String hex = Integer.toHexString((int)data[i] & 0xFF).toUpperCase(Locale.ENGLISH);
                    if (hex.length() < 2) {
                        buf.append('0');
                    }
                    buf.append(hex);
                }
                return (T)buf.toString();
            }
        }
        return convertToWrapperOrEx(data, targetType);
    }
    
    @Override
    public byte[] copy(byte[] o, boolean shallow) {
        int l = o.length;
        byte[] t = new byte[o.length];
        System.arraycopy(o,0,t,0,l);
        return t;
    }

}
