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
package com.sap.sdo.impl.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.sap.sdo.api.helper.SapDataHelper;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.simple.DateSimpleType;
import com.sap.sdo.impl.types.simple.DurationSimpleType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;

public class DataHelperImpl implements SapDataHelper
{
    private final HelperContext _helperContext;
    
    private DataHelperImpl(HelperContext pHelperContext) {
        _helperContext = pHelperContext;
    }

    public static DataHelper getInstance(HelperContext pHelperContext) {
        // to avoid illegal instances
        DataHelper dataHelper = pHelperContext.getDataHelper();
        if (dataHelper != null) {
            return dataHelper;
        }
        return new DataHelperImpl(pHelperContext);
    }
    
	public Date toDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        if (dateString.startsWith("P") || dateString.startsWith("-P")) {
            return DurationSimpleType.convertToDate(dateString);
        }
        return DateSimpleType.convertToDate(dateString);
    }

    public Calendar toCalendar(String dateString) {
        return toCalendar(dateString, null);
    }

    public Calendar toCalendar(String dateString, Locale locale) {
        if (dateString != null && (dateString.startsWith("P") || dateString.startsWith("-P"))) {
            return DurationSimpleType.convertToCalendar(dateString, locale);
        } else {
            return DateSimpleType.convertToCalendar(dateString, locale);
        }
    }

    public String toDateTime(Date date) {
        return JavaSimpleType.DATETIME.toString(date);
    }

    public String toDuration(Date date) {
        return JavaSimpleType.DURATION.convertFromJavaClass(date);
    }

    public String toTime(Date date) {
        return JavaSimpleType.TIME.toString(date);
    }

    public String toDay(Date date) {
        return JavaSimpleType.DAY.toString(date);
    }

    public String toMonth(Date date) {
        return JavaSimpleType.MONTH.toString(date);
    }

    public String toMonthDay(Date date) {
        return JavaSimpleType.MONTHDAY.toString(date);
    }

    public String toYear(Date date) {
        return JavaSimpleType.YEAR.toString(date);
    }

    public String toYearMonth(Date date) {
        return JavaSimpleType.YEARMONTH.toString(date);
    }

    public String toYearMonthDay(Date date) {
        return JavaSimpleType.YEARMONTHDAY.toString(date);
    }

    public String toDateTime(Calendar calendar) {
        return JavaSimpleType.DATETIME.toString(calendar);
    }

    public String toDuration(Calendar calendar) {
        return toDuration(calendar.getTime());
    }

    public String toTime(Calendar calendar) {
        return JavaSimpleType.TIME.toString(calendar);
    }

    public String toDay(Calendar calendar) {
        return JavaSimpleType.DAY.toString(calendar);
    }

    public String toMonth(Calendar calendar) {
        return JavaSimpleType.MONTH.toString(calendar);
    }

    public String toMonthDay(Calendar calendar) {
        return JavaSimpleType.MONTHDAY.toString(calendar);
    }

    public String toYear(Calendar calendar) {
        return JavaSimpleType.YEAR.toString(calendar);
    }

    public String toYearMonth(Calendar calendar) {
        return JavaSimpleType.YEARMONTH.toString(calendar);
    }

    public String toYearMonthDay(Calendar calendar) {
        return JavaSimpleType.YEARMONTHDAY.toString(calendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#convert(commonj.sdo.Type, java.lang.Object)
     */
    public Object convert(Type pType, Object pValue) {
        return ((SdoType)pType).convertFromJavaClass(pValue);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#convert(commonj.sdo.Property, java.lang.Object)
     */
    public Object convert(Property pProperty, Object pValue) {
        SdoProperty property = (SdoProperty)pProperty;
        Object value = ((SdoType)property.getType()).convertFromJavaClass(pValue);
        return property.getCachedValue(value);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#setReadOnlyMode(commonj.sdo.DataObject, boolean)
     */
    public void setReadOnlyMode(DataObject pDataObject, boolean pActivated) {
        ((DataObjectDecorator)pDataObject).getInstance().setReadOnlyMode(pActivated);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#getHelperContext(commonj.sdo.DataObject)
     */
    public HelperContext getHelperContext(DataObject pDataObject) {
        return ((DataObjectDecorator)pDataObject).getInstance().getHelperContext();
    }

    public Object project(DataObject pDataObject) {
        try {
            GenericDataObject gdo = ((DataObjectDecorator)pDataObject).getInstance();
            return gdo.project();
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("DataObject does not support projection "
                + pDataObject, cce);
        }
    }

}
