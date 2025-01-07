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

public class BooleanSimpleType extends BooleanObjectSimpleType
{
    private static final long serialVersionUID = -8415479084989409223L;
    
    BooleanSimpleType() {
        super(URINamePair.BOOLEAN,boolean.class);
    }

    @Override
    public Boolean getDefaultValue() {
        return JavaSimpleType.DEFAULT_BOOLEAN;
    }
}
