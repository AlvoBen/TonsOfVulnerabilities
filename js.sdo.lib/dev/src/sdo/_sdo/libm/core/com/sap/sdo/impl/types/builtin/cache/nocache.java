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
 * This {@link ValueCache} does not cache anything. It is implemented as
 * singleton.
 * @author D042807
 *
 */
public class NoCache<T> implements ValueCache<T> {

    private static final NoCache _instance = new NoCache();
    
    private NoCache() {
    }
    
    /**
     * Returns the single instance.
     * @return the single instance.
     */
    public static <T> NoCache<T> getInstance() {
        return _instance;
    }

    /**
     * Returns the same value without any caching.
     * @param pValue The value, never null.
     * @return pValue.
     */
    public T getCachedValue(T pValue) {
        return pValue;
    }

}
