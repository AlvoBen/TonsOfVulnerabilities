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

public class Boolean01SimpleType extends BooleanObjectSimpleType
{
    private static final long serialVersionUID = -8415479084989409223L;
    
    Boolean01SimpleType() {
        getResultMap(Boolean.TRUE).put(String.class, "1");
        getResultMap(Boolean.FALSE).put(String.class, "0");
    }

}
