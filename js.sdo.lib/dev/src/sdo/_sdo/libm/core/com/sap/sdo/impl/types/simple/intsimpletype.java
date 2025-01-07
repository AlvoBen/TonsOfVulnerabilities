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

public class IntSimpleType extends IntObjectSimpleType
{
    private static final long serialVersionUID = -4900449428509253403L;

    IntSimpleType() {
        super(URINamePair.INT, int.class);
    }

    @Override
    public Integer getDefaultValue() {
        return JavaSimpleType.DEFAULT_INT;
    }
}
