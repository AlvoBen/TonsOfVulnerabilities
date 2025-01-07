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

import com.sap.sdo.api.util.URINamePair;

public class DecimalSimpleType extends JavaSimpleType<BigDecimal>
{
    private static final long serialVersionUID = -767319018822905637L;

    DecimalSimpleType() {
		super(URINamePair.DECIMAL,BigDecimal.class);
	}

	public BigDecimal convertFromJavaClass(Object data) {
		if (data==null) {
            return null;
        }
		if (data instanceof BigDecimal) {
            return (BigDecimal) data;
        }
        if (data instanceof Number) {
            return convertToNumberType(BigDecimal.class, (Number)data);
        }
        if (data instanceof String) {
            return new BigDecimal(((String)data).trim());
        }
        return convertFromWrapperOrEx(data);
	}

    public <T> T convertToJavaClass(BigDecimal data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==BigDecimal.class) {
            return (T)data;
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
    public BigDecimal copy(BigDecimal o, boolean shallow) {
        // TODO there must be better ways
        return new BigDecimal(o.toPlainString());
    }

}
