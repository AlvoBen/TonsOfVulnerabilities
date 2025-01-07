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
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.Duration;

import com.sap.sdo.api.util.URINamePair;

public class DurationSimpleType extends JavaSimpleType<String>
{
    private static final long serialVersionUID = 2247807823017218014L;

    DurationSimpleType() {
        super(URINamePair.DURATION,String.class);
    }
    
    public static Date convertToDate(String duration) {
        Calendar c = convertToCalendar(duration, null);
        return c.getTime();
    }

    public static Calendar convertToCalendar(String duration, Locale locale) {
        try {
            Duration d = AbstractDTSimpleType.DATATYPE_FACTORY.newDuration(duration);
            final Calendar c;
            if (locale != null) {
                c = Calendar.getInstance(AbstractDTSimpleType.TIME_ZONE_UTC, locale);
            } else {
                c = Calendar.getInstance(AbstractDTSimpleType.TIME_ZONE_UTC);
            }
            c.clear();
            d.addTo(c);
            return c;
        } catch (RuntimeException ex) { //$JL-EXC$
            throw new IllegalArgumentException("not a valid duration: \""+duration+"\"", ex);
        }
    }

    public String convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }        
        if (data instanceof Date) {
            try {
                Duration d = AbstractDTSimpleType.DATATYPE_FACTORY.newDuration(((Date)data).getTime());
                return d.toString();
            } catch (RuntimeException ex) { //$JL-EXC$
                throw new IllegalArgumentException("not a valid duration: \""+data+"\"", ex);
            }
        }
    	if (data instanceof String) {
            try {
                Duration d = AbstractDTSimpleType.DATATYPE_FACTORY.newDuration((String)data);
                return d.toString();
            } catch (RuntimeException ex) { //$JL-EXC$
                throw new IllegalArgumentException("not a valid duration: \""+data+"\"", ex);
            }
        }
        return convertFromWrapperOrEx(data);
    }
    
    public <T> T convertToJavaClass(String data, Class<T> targetType) {
        if (data==null) {
            return null;
        }        
        if (targetType==String.class) {
            return (T)data;
        }
        if (targetType == Date.class) {
            return (T)convertToDate(data);
        }
        return convertToWrapperOrEx(data, targetType);
    }

}
