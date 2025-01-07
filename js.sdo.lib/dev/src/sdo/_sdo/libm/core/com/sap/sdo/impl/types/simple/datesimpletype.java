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

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConstants;

import com.sap.sdo.api.util.URINamePair;

public class DateSimpleType extends AbstractDTSimpleType<Date>
{
    private static final long serialVersionUID = -2910444363685185873L;

	DateSimpleType() {
        super(URINamePair.DATE,Date.class,true);
    }

    public Date convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
		if (data instanceof Date) {
            return (Date) data;
        }
        if (data instanceof Long) {
            return new Date(((Long)data).longValue());
        }
        if (data instanceof Calendar) {
            return ((Calendar) data).getTime();
        }
        if (data instanceof String) {
            return convertToDate((String)data);
        }
        return convertFromWrapperOrEx(data);
	}
    
    public <T> T convertToJavaClass(Date data, Class<T> targetType) {
        if (data==null) {
            return null;
        }
        if (targetType==Date.class) {
            return (T)data;
        }
        if (targetType==String.class) {
            return (T)toString(data);
        }
        if (targetType==Long.class) {
            return (T)new Long(data.getTime());
        }
        if (targetType==java.sql.Date.class) {
            return (T)new java.sql.Date(data.getTime());
        }
        return convertToWrapperOrEx(data, targetType);
    }

    @Override
    public Date copy(Date o, boolean shallow) {
        Date t = new Date(o.getTime());
        return t;
    }

}
