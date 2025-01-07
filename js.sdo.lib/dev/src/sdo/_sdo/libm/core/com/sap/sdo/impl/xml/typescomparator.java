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

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.Type;

public class TypesComparator implements Comparator<Type> {
    
    public static TypesComparator INSTANCE = new TypesComparator();

    public int compare(Type pType1, Type pType2) {
        if (pType1 == pType2) {
            return 0;
        }
        URINamePair unp1 = ((SdoType)pType1).getQName();
        URINamePair unp2 = ((SdoType)pType2).getQName();
        return unp1.compareTo(unp2);
    }
    
}