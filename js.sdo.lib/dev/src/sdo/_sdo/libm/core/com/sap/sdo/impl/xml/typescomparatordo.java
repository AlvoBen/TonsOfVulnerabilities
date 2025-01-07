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
package com.sap.sdo.impl.xml;

import java.util.Comparator;

import com.sap.sdo.impl.types.builtin.TypeType;

import commonj.sdo.DataObject;

public class TypesComparatorDO implements Comparator<DataObject> {
    
    public static TypesComparatorDO INSTANCE = new TypesComparatorDO();

    public int compare(DataObject pType1, DataObject pType2) {
        if (pType1 == pType2) {
            return 0;
        }
        int result = pType1.getString(TypeType.URI).compareTo(pType2.getString(TypeType.URI));
        if (result == 0) {
            result = pType1.getString(TypeType.NAME).compareTo(pType2.getString(TypeType.NAME));
        }
        return result;
    }
    
}