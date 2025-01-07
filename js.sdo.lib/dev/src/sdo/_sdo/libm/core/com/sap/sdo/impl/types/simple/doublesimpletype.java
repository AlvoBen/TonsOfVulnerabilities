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

import com.sap.sdo.api.util.URINamePair;

public class DoubleSimpleType extends DoubleObjectSimpleType
{
    private static final long serialVersionUID = 7002713798309684378L;

    DoubleSimpleType() {
        super(URINamePair.DOUBLE, double.class);
    }

    @Override
    public Double getDefaultValue() {
        return JavaSimpleType.DEFAULT_DOUBLE;
    }
}
