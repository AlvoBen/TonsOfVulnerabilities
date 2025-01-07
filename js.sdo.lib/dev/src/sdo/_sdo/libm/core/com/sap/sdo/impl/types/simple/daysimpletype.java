﻿/*
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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sap.sdo.api.util.URINamePair;

public class DaySimpleType extends AbstractDTSimpleType<String>
{
    private static final long serialVersionUID = 9171536101555620953L;

    DaySimpleType() {
        super(URINamePair.DAY,String.class,false);
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.simple.AbstractDTSimpleType#pruneCalendar(javax.xml.datatype.XMLGregorianCalendar)
     */
    @Override
    protected void pruneCalendar(XMLGregorianCalendar pXmlG) {
        pXmlG.setHour(DatatypeConstants.FIELD_UNDEFINED);
        pXmlG.setMinute(DatatypeConstants.FIELD_UNDEFINED);
        pXmlG.setSecond(DatatypeConstants.FIELD_UNDEFINED);
        pXmlG.setMonth(DatatypeConstants.FIELD_UNDEFINED);
        pXmlG.setYear(DatatypeConstants.FIELD_UNDEFINED);
    }

}
