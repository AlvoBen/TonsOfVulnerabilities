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

import java.sql.Time;
import java.util.Date;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sap.sdo.api.util.URINamePair;

public class TimeSimpleType extends AbstractDTSimpleType<String>
{
    TimeSimpleType() {
        super(URINamePair.TIME,String.class,true);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.simple.AbstractDTSimpleType#convertFromJavaClass(java.lang.Object)
     */
    @Override
    public String convertFromJavaClass(Object pData) {
        if (pData instanceof Time) {
            return super.convertFromJavaClass(new Date(((Time)pData).getTime()));
        }
        return super.convertFromJavaClass(pData);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.simple.AbstractDTSimpleType#convertToJavaClass(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> T convertToJavaClass(String pData, Class<T> pTargetType) {
        if (pTargetType==Time.class) {
            return (T)new Time(super.convertToJavaClass(pData, Date.class).getTime());
        }
        return super.convertToJavaClass(pData, pTargetType);
    }

    private static final long serialVersionUID = 233975897536996177L;

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.simple.AbstractDTSimpleType#pruneCalendar(javax.xml.datatype.XMLGregorianCalendar)
     */
    @Override
    protected void pruneCalendar(XMLGregorianCalendar pXmlG) {
        pXmlG.setDay(DatatypeConstants.FIELD_UNDEFINED);
        pXmlG.setMonth(DatatypeConstants.FIELD_UNDEFINED);
        pXmlG.setYear(DatatypeConstants.FIELD_UNDEFINED);
    }

}
