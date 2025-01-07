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

public class LongSimpleType extends LongObjectSimpleType
{
    private static final long serialVersionUID = 2326526353883541529L;

    LongSimpleType() {
        super(URINamePair.LONG, long.class);
    }

    @Override
    public Long getDefaultValue() {
        return JavaSimpleType.DEFAULT_LONG;
    }
}
