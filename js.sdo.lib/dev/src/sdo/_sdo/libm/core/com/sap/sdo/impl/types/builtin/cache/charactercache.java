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
 * The CharacterCache takes advantage of the implementation detail that
 * {@link Character#valueOf(char)} returns cached values in the range of
 * 0 to 127. Values out of that rage are delegated to a further
 * {@link ValueCache} implementation. This can be a {@link HashCache} or
 * {@link NoCache}.
 * @author D042807
 *
 */
public class CharacterCache implements ValueCache<Character> {

    private final ValueCache<Character> _delegate;
    
    private static final CharacterCache _noHashCache = new CharacterCache();

    /**
     * Creates a CharacterCache that delegates to a {@link HashCache}.
     * @param pInitialSize The initial size of the cache.
     * @param pMaxSize The maximum size of the cache.
     */
    public CharacterCache(int pInitialSize, int pMaxSize) {
        _delegate = new HashCache<Character>(pInitialSize, pMaxSize);
    }
    
    /**
     * Creates a CharacterCache that delegates to a {@link NoCache}.
     */
    private CharacterCache() {
        _delegate = NoCache.getInstance();
    }

    /**
     * Returns a singleton instance of the cache that in not specialized to a
     * single property. Only values between 0 and 127 are cached.
     * @return The singleton CharacterCache without HashCache.
     */
    public static CharacterCache getInstance() {
        return _noHashCache;
    }

    public Character getCachedValue(Character pValue) {
        char value = pValue.charValue();
        if (value <= 127) {
            return Character.valueOf(value);
        }
        return _delegate.getCachedValue(pValue);
    }
    
    
}
