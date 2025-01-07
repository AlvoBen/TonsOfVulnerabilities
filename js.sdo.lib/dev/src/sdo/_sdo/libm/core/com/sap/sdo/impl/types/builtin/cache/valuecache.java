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
package com.sap.sdo.impl.types.builtin.cache;

/**
 * The aim of a ValueCache is to save space by reducing the number of instances.
 * It caches frequently used values so that equal values can be replaced by the
 * cached values.
 * @author D042807
 *
 */
public interface ValueCache<T> {

    /**
     * Replaces the value by an equal cached value to save space by reducing the
     * number of instances.
     * @param value The value, never null.
     * @return The value or an equal cached value.
     */
    T getCachedValue(T value);
    
}
