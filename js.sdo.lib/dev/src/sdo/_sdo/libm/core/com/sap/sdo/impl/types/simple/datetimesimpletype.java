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

import javax.xml.datatype.DatatypeConstants;

import com.sap.sdo.api.util.URINamePair;

public class DateTimeSimpleType extends AbstractDTSimpleType<String>
{
    private static final long serialVersionUID = 3276784422664159189L;

    DateTimeSimpleType() {
        super(URINamePair.DATETIME,String.class,true);
    }


}
