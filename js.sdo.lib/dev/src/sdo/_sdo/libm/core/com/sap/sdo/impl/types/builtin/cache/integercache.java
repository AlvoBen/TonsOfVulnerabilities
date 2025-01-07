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
 * The IntegerCache takes advantage of the implementation detail that
 * {@link Integer#valueOf(int)} returns cached values in the range of
 * -128 to 127. Values out of that rage are delegated to a further
 * {@link ValueCache} implementation. This can be a {@link HashCache} or
 * {@link NoCache}.
 * @author D042807
 *
 */
public class IntegerCache implements ValueCache<Integer> {

    private final ValueCache<Integer> _delegate;
    
    private static final IntegerCache _noHashCache = new IntegerCache();

    /**
     * Creates an IntegerCache that delegates to a {@link HashCache}.
     * @param pInitialSize The initial size of the cache.
     * @param pMaxSize The maximum size of the cache.
     */
    public IntegerCache(int pInitialSize, int pMaxSize) {
        _delegate = new HashCache<Integer>(pInitialSize, pMaxSize);
    }
    
    /**
     * Creates an IntegerCache that delegates to a {@link NoCache}.
     */
    private IntegerCache() {
        _delegate = NoCache.getInstance();
    }

    /**
     * Returns a singleton instance of the cache that in not specialized to a
     * single property. Only values between -128 and 127 are cached.
     * @return The singleton IntegerCache without HashCache.
     */
    public static IntegerCache getInstance() {
        return _noHashCache;
    }

    public Integer getCachedValue(Integer pValue) {
        int value = pValue.intValue();
        if ((value >= -128) && (value <= 127)) {
            return Integer.valueOf(value);
        }
        return _delegate.getCachedValue(pValue);
    }
    
    
}
