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
package com.sap.sdo.impl.objects;

import commonj.sdo.DataObject;

/**
 * This interface is what we actually create proxies for (in the typed
 * case), so that, from a data object proxy we can still get to the 
 * backing generic data object implementation.
 *
 */
public interface DataObjectDecorator extends DataObject
{
    /**
     * Returns the GenericDataObject behind the facade.
     * @return The GenericDataObject behind the facade.
     */
    GenericDataObject getInstance();
    
    InternalDataObjectModifier getInternalModifier();
}
