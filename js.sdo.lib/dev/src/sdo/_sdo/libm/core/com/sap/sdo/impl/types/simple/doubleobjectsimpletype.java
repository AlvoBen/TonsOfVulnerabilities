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

public class DoubleObjectSimpleType extends JavaSimpleType<Double>
{
    private static final long serialVersionUID = -374125154144585432L;

    DoubleObjectSimpleType() {
		this(URINamePair.DOUBLEOBJECT,Double.class);
	}

    DoubleObjectSimpleType(URINamePair unp, Class<Double> clz) {
        super(unp, clz);
    }

    public Double convertFromJavaClass(Object data) {
        if (data==null) {
            return getDefaultValue();
        }        
        if (data instanceof Double) {
            return (Double)data;
        }
        if (data instanceof String) {
            return Double.valueOf(((String)data).trim());
        }
        if (data instanceof Number) {
            return convertToDouble(((Number)data));
        }
        return convertFromWrapperOrEx(data);
    }

    public <T> T convertToJavaClass(Double data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==Double.class) {
            return (T)data;
        }
        if (targetType==String.class) {
            return (T)data.toString();
        }
        if (Number.class.isAssignableFrom(targetType)) {
            return convertToNumberType(targetType,data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

    public JavaSimpleType<Double> getNillableType() {
        return DOUBLEOBJECT;
    }
}
