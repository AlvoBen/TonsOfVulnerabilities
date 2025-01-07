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
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sap.sdo.api.util.URINamePair;

/**
 * base class for date oriented simple types
 */
public abstract class AbstractDTSimpleType<S> extends JavaSimpleType<S>
{
    public static final boolean JAVA_5;
    public static final DatatypeFactory DATATYPE_FACTORY;
    public static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");

    private boolean _timeZone;

    static {
        try {
            DATATYPE_FACTORY = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            IllegalArgumentException iae = new IllegalArgumentException("datetime conversion error");
            iae.initCause(e);
            throw iae;
        }
        String javaVersion = System.getProperty("java.version");
        JAVA_5 = javaVersion != null && javaVersion.startsWith("1.5");
    }

    AbstractDTSimpleType(URINamePair unp, Class<S> clz, boolean timeZone) {
        super(unp,clz);
        _timeZone = timeZone;
    }

    public static Date convertToDate(String date) {
        Calendar c = convertToCalendar(date, null);
        if (!c.isSet(Calendar.ZONE_OFFSET)) {
            c.setTimeZone(TIME_ZONE_UTC);
        }
        return c.getTime();
    }

    public static Calendar convertToCalendar(String date, Locale locale) {
        String input = date.trim();
        // http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#gMonth defined as --MM--
        if (JAVA_5 && input != null && input.length() >= 4 && input.length() <= 10
                && input.startsWith("--") && input.charAt(2) != '-'
                && !(input.length() == 7 && input.charAt(4) == '-' && input.substring(5).matches("[0-9]*"))
                && !(input.length() > 7 && input.charAt(4) == '-' && input.charAt(7) != ':' && input.substring(5).matches("[0-9]*"))) {
            input = fixFormatOfMonth(input);
        }
        XMLGregorianCalendar xmlG = createXmlGregorianCalendar(input, false);
        Calendar c = xmlG.toGregorianCalendar(null, locale, null);
        if (xmlG.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            c.clear(Calendar.ZONE_OFFSET);
        }
        return c;
    }

    private static String fixFormatOfMonth(String s) {
        final String input;
        StringBuilder buf = new StringBuilder(s);
        buf.insert(4, "--");
        input = buf.toString();
        return input;
    }

    private static Date calculateDate(String input, boolean fixMonth) {
        XMLGregorianCalendar xmlG = createXmlGregorianCalendar(input, fixMonth);
        if (xmlG.getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            xmlG.setTimezone(0);
        }
        return xmlG.toGregorianCalendar().getTime();
    }

    /**
     * @param input
     * @return
     * @throws DatatypeConfigurationException
     */
    private static XMLGregorianCalendar createXmlGregorianCalendar(String s, boolean fixMonth) {
        String input = null;
        // http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#gMonth defined as --MM--
        if (fixMonth) {
            if (JAVA_5) {
                if (s.length() >= 4 && s.startsWith("--") && s.charAt(2)!='-' && s.lastIndexOf("--")==0) {
                    input = fixFormatOfMonth(s);
                }
            } else if (s.length()>=6 && s.charAt(4)=='-' && s.charAt(5)=='-') {
                input = s.substring(0,4) + s.substring(6);
            }
            if (input == null) {
                input = s;
            }
        } else {
            input = s;
        }
        try {
            return DATATYPE_FACTORY.newXMLGregorianCalendar(input);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("invalid date string: "+s, ex);
        }
    }

    private Date parse(String input) {
        try {
            // _timeZone = _timeZone || (xmlG.getTimezone() != DatatypeConstants.FIELD_UNDEFINED);
            return calculateDate(input, URINamePair.MONTH.equals(getQName()));
        } catch (RuntimeException ex) { //$JL-EXC$
            throw new IllegalArgumentException("invalid date string: "+input, ex);
        }
    }

    public String toString(Date d) {
        GregorianCalendar gC = new GregorianCalendar();
        gC.clear();
        gC.setGregorianChange(new Date(Long.MIN_VALUE));
        gC.setTimeZone(TIME_ZONE_UTC);
        gC.setTime(d);
        return toString(gC, _timeZone && gC.isSet(Calendar.ZONE_OFFSET));
    }

    /**
     * @param gC
     * @return
     */
    public String toString(Calendar c) {
        if (!(c instanceof GregorianCalendar)) {
            return toString(c.getTime());
        }
        return toString(c, c.isSet(Calendar.ZONE_OFFSET));
    }

    private String toString(Calendar c, boolean keepTimeZone) {
        boolean milliSec = c.isSet(Calendar.MILLISECOND);
        XMLGregorianCalendar xmlG =
            DATATYPE_FACTORY.newXMLGregorianCalendar((GregorianCalendar)c); //.normalize();
        if (!keepTimeZone) {
            xmlG.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        }
        if (!milliSec) {
            xmlG.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        }
        pruneCalendar(xmlG);
        return toString(xmlG);
    }

    /**
     * @param xmlG
     */
    protected void pruneCalendar(XMLGregorianCalendar xmlG) {
        // default behavior doesn't prune
    }

    /**
     * @param xmlG
     * @return
     */
    private String toString(XMLGregorianCalendar xmlG) {
        String result = xmlG.toXMLFormat();
        // http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#gMonth defined as --MM--
        if (URINamePair.MONTH.equals(getQName())) {
            int index = result.indexOf("--", 3);
            if (index != -1) {
                StringBuilder buf = new StringBuilder(result.length() - 2);
                buf.append(result, 0, index);
                buf.append(result, index+2, result.length());
                result = buf.toString();
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * This is the default implementation where it is assumed that the data
     * parameter is of type String. Override this method if data has another
     * type!
     * @param S In this implementation it is assumed that S stands for String.
     */
    @Override
    public <T> T convertToJavaClass(S data, Class<T> targetType) {
        if (data==null) {
            return null;
        }
        if (targetType==String.class) {
            return (T)data;
        }
        if (targetType == Date.class) {
            return (T)parse((String)data);
        }
        if (targetType == java.sql.Date.class) {
            return (T)new java.sql.Date(parse((String)data).getTime());
        }
        return convertToWrapperOrEx(data, targetType);
    }

    /**
     * {@inheritDoc}
     * This is the default implementation where it is assumed that the result
     * is of type String. Override this method if the result has another
     * type!
     * @param S In this implementation it is assumed that S stands for String.
     */
    @Override
    public S convertFromJavaClass(Object data) {
        if (data==null) {
            return null;
        }
        if (data instanceof String) {
            XMLGregorianCalendar calendar =
                createXmlGregorianCalendar(
                    (String)data,
                    URINamePair.MONTH.equals(getQName()));
            pruneCalendar(calendar);
            try {
                return (S)toString(calendar);
            } catch (RuntimeException ex) { //$JL-EXC$
                throw new IllegalArgumentException("invalid "+getQName()+" string: "+data, ex);
            }
        }
        if (data instanceof Date) {
            return (S)toString((Date)data);
        }
        if (data instanceof Calendar) {
            return (S)toString(((Calendar)data).getTime());
        }
        return convertFromWrapperOrEx(data);
    }

}
