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
import com.sap.sdo.impl.context.SapHelperProviderImpl;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class DataHelperDelegator implements SapDataHelper {
    private static final DataHelper INSTANCE = new DataHelperDelegator();

    /**
     * 
     */
    private DataHelperDelegator() {
        super();
    }
    
    public static DataHelper getInstance() {
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toCalendar(java.lang.String, java.util.Locale)
     */
    public Calendar toCalendar(String pDateString, Locale pLocale) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toCalendar(pDateString, pLocale);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toCalendar(java.lang.String)
     */
    public Calendar toCalendar(String pDateString) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toCalendar(pDateString);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDate(java.lang.String)
     */
    public Date toDate(String pDateString) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDate(pDateString);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDateTime(java.util.Calendar)
     */
    public String toDateTime(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDateTime(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDateTime(java.util.Date)
     */
    public String toDateTime(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDateTime(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDay(java.util.Calendar)
     */
    public String toDay(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDay(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDay(java.util.Date)
     */
    public String toDay(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDay(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDuration(java.util.Calendar)
     */
    public String toDuration(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDuration(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDuration(java.util.Date)
     */
    public String toDuration(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toDuration(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonth(java.util.Calendar)
     */
    public String toMonth(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toMonth(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonth(java.util.Date)
     */
    public String toMonth(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toMonth(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonthDay(java.util.Calendar)
     */
    public String toMonthDay(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toMonthDay(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonthDay(java.util.Date)
     */
    public String toMonthDay(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toMonthDay(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toTime(java.util.Calendar)
     */
    public String toTime(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toTime(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toTime(java.util.Date)
     */
    public String toTime(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toTime(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYear(java.util.Calendar)
     */
    public String toYear(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toYear(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYear(java.util.Date)
     */
    public String toYear(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toYear(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonth(java.util.Calendar)
     */
    public String toYearMonth(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toYearMonth(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonth(java.util.Date)
     */
    public String toYearMonth(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toYearMonth(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonthDay(java.util.Calendar)
     */
    public String toYearMonthDay(Calendar pCalendar) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toYearMonthDay(pCalendar);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonthDay(java.util.Date)
     */
    public String toYearMonthDay(Date pDate) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().toYearMonthDay(pDate);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#convert(commonj.sdo.Type, java.lang.Object)
     */
    public Object convert(Type pType, Object pValue) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().convert(pType, pValue);
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#convert(commonj.sdo.Property, java.lang.Object)
     */
    public Object convert(Property property, Object pValue) {
        return SapHelperProviderImpl.getDefaultContext().getDataHelper().convert(property, pValue);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#setReadOnlyMode(commonj.sdo.DataObject, boolean)
     */
    public void setReadOnlyMode(DataObject pDataObject, boolean pActivated) {
        ((SapDataHelper)SapHelperProviderImpl.getDefaultContext().getDataHelper()).setReadOnlyMode(pDataObject, pActivated);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#getHelperContext(commonj.sdo.DataObject)
     */
    public HelperContext getHelperContext(DataObject pDataObject) {
        return ((SapDataHelper)SapHelperProviderImpl.getDefaultContext().getDataHelper()).getHelperContext(pDataObject);
    }

    public Object project(DataObject pDataObject) {
        return ((SapDataHelper)SapHelperProviderImpl.getDefaultContext().getDataHelper()).project(pDataObject);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" delegate: ");
        buf.append(SapHelperProviderImpl.getDefaultContext().getDataHelper());
        return buf.toString();
    }

}
