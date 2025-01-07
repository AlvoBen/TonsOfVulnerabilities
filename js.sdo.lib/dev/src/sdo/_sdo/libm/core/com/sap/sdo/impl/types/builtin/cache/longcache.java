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
 * The LongCache takes advantage of the implementation detail that
 * {@link Long#valueOf(long)} returns cached values in the range of
 * -128 to 127. Values out of that rage are delegated to a further
 * {@link ValueCache} implementation. This can be a {@link HashCache} or
 * {@link NoCache}.
 * @author D042807
 *
 */
public class LongCache implements ValueCache<Long> {

    private final ValueCache<Long> _delegate;
    
    private static final LongCache _noHashCache = new LongCache();

    /**
     * Creates a LongCache that delegates to a {@link HashCache}.
     * @param pInitialSize The initial size of the cache.
     * @param pMaxSize The maximum size of the cache.
     */
    public LongCache(int pInitialSize, int pMaxSize) {
        _delegate = new HashCache<Long>(pInitialSize, pMaxSize);
    }
    
    /**
     * Creates a LongCache that delegates to a {@link NoCache}.
     */
    private LongCache() {
        _delegate = NoCache.getInstance();
    }

    /**
     * Returns a singleton instance of the cache that in not specialized to a
     * single property. Only values between -128 and 127 are cached.
     * @return The singleton LongCache without HashCache.
     */
    public static LongCache getInstance() {
        return _noHashCache;
    }

    public Long getCachedValue(Long pValue) {
        long value = pValue.longValue();
        if ((value >= -128) && (value <= 127)) {
            return Long.valueOf(value);
        }
        return _delegate.getCachedValue(pValue);
    }
    
    
}
