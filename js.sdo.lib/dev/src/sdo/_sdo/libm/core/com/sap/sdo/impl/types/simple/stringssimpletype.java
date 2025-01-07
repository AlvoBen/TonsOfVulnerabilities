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

public class StringsSimpleType extends ListSimpleType
{
    private static final long serialVersionUID = -6827001526192045390L;

    StringsSimpleType() {
        super(STRING, URINamePair.STRINGS, STRING.getHelperContext());
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.simple.ListSimpleType#isAnonymous()
     */
    @Override
    public boolean isLocal() {
        return false;
    }

}
