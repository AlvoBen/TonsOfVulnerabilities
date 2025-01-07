/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.api.helper.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.sap.sdo.api.helper.SapDataHelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class SapDataHelperMock implements SapDataHelper {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#getHelperContext(commonj.sdo.DataObject)
     */
    public HelperContext getHelperContext(DataObject pDataObject) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#project(commonj.sdo.DataObject)
     */
    public Object project(DataObject pDataObject) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.helper.SapDataHelper#setReadOnlyMode(commonj.sdo.DataObject, boolean)
     */
    public void setReadOnlyMode(DataObject pDataObject, boolean pActivated) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#convert(commonj.sdo.Type, java.lang.Object)
     */
    public Object convert(Type pType, Object pValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#convert(commonj.sdo.Property, java.lang.Object)
     */
    public Object convert(Property property, Object pValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toCalendar(java.lang.String)
     */
    public Calendar toCalendar(String pDateString) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toCalendar(java.lang.String, java.util.Locale)
     */
    public Calendar toCalendar(String pDateString, Locale pLocale) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDate(java.lang.String)
     */
    public Date toDate(String pDateString) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDateTime(java.util.Date)
     */
    public String toDateTime(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDateTime(java.util.Calendar)
     */
    public String toDateTime(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDay(java.util.Date)
     */
    public String toDay(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDay(java.util.Calendar)
     */
    public String toDay(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDuration(java.util.Date)
     */
    public String toDuration(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toDuration(java.util.Calendar)
     */
    public String toDuration(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonth(java.util.Date)
     */
    public String toMonth(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonth(java.util.Calendar)
     */
    public String toMonth(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonthDay(java.util.Date)
     */
    public String toMonthDay(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toMonthDay(java.util.Calendar)
     */
    public String toMonthDay(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toTime(java.util.Date)
     */
    public String toTime(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toTime(java.util.Calendar)
     */
    public String toTime(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYear(java.util.Date)
     */
    public String toYear(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYear(java.util.Calendar)
     */
    public String toYear(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonth(java.util.Date)
     */
    public String toYearMonth(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonth(java.util.Calendar)
     */
    public String toYearMonth(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonthDay(java.util.Date)
     */
    public String toYearMonthDay(Date pDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see commonj.sdo.helper.DataHelper#toYearMonthDay(java.util.Calendar)
     */
    public String toYearMonthDay(Calendar pCalendar) {
        // TODO Auto-generated method stub
        return null;
    }

}
