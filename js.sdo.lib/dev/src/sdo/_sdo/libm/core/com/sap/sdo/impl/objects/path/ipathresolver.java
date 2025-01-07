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
package com.sap.sdo.impl.objects.path;

import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;

/**
 * Interface to access all necessary information
 * from endpoint of a path navigation.
 * 
 * @author D042774
 *
 */
public interface IPathResolver {
    /**
     * Container object that contains the property that was accessed through xpath.
     * 
     * @return data object
     */
    GenericDataObject getDataObject();
    
    /**
     * Property and Value that were accessed through xpath.
     * 
     * @return propValue as result of xpath navigation.
     */
    PropValue<?> getPropValue();
    
    /**
     * If property ist multi-valued returns index of value.
     * 
     * @return index of value if property is multi-valued, otherwise returns -1.
     */
    int getIndex();
    
    /**
     * Checks if property is not multi-valued.
     * @return true if property isn't multi-valued, otherwise false.
     */
    boolean isPlain();
}
