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

public class CharSimpleType extends CharObjectSimpleType
{
    private static final long serialVersionUID = -5408216283408471834L;

    CharSimpleType() {
		super(URINamePair.CHARACTER,char.class);
	}

    @Override
    public Character getDefaultValue() {
        return JavaSimpleType.DEFAULT_CHAR;
    }
}
