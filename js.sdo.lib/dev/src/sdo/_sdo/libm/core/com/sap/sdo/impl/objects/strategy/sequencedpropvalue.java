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

import java.io.Serializable;

import com.sap.sdo.impl.objects.PropValue;

/**
 * This interface defines further methods for a sequenced {@link PropValue} to
 * maintain the value of the {@link commonj.sdo.Property} if the 
 * {@link commonj.sdo.Sequence} was changed.
 * @author D042807
 *
 * @param <T> Object for single valued properties and List&lt;Object&gt; for multi-valued properties.
 */
public interface SequencedPropValue<T> extends PropValue<T>, Serializable {

    /**
     * This method is called, if the value is added to the 
     * {@link commonj.sdo.Sequence} and has to be added also to the
     * {@link commonj.sdo.Property}.
     * @param pValueNode The new value node.
     */
    void internAddBySequence(ValueNode pValueNode);

    /**
     * This method is called, if the value is removed from the 
     * {@link commonj.sdo.Sequence} and has to be removed from the
     * {@link commonj.sdo.Property} too.
     * @param pValueNode The removed value node.
     */
    void internRemoveBySequence(ValueNode pValueNode);
    
    /**
     * This method is called, if the {@link commonj.sdo.Sequence} is cleared and
     * the {@link commonj.sdo.Property} has to be cleared too.
     * @param pValueNode The removed value node.
     */
    void internClearBySequence();
    
}
