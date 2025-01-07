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
package com.sap.sdo.impl.objects.strategy;

import com.sap.sdo.impl.objects.PropValue;

import commonj.sdo.Property;

/**
 * A ValueNode represents a single value in a sequence. 
 */
public interface ValueNode
{
    /**
     * Sets the value of this ValueNode.
     * @param pValue The new value.
     * @param pChangeContext The PropertyChangeContext.
     * @return The old value.
     */
    Object setNodeValue(Object pValue, PropertyChangeContext pChangeContext);
    
    /**
     * The single value in the {@link commonj.sdo.Sequence}.
     * @return The value of this ValueNode.
     */
    Object getNodeValue();

    /**
     * Sets the value of this ValueNode without any checks or manipulations.
     * @param pValue The new value.
     */
    void setWithoutCheck(Object pValue);

    /**
     * Returns the Property that is assigned to this ValueNode.
     * @return The Property.
     */
    Property getProperty();

    /**
     * Returns the PropValue that is assigned to this ValueNode.
     * @return The PropValue.
     */
    PropValue getPropValue();
}
