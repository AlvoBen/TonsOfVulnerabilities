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

import java.lang.ref.SoftReference;


/**
 * This {@link ValueCache} caches values in an array. The index is calculated
 * by the hashCode of the value. The HashCache has an initial size increases
 * automaticly to a maximum size if necessary. The size is always a power of two.
 * The internal array is referenced by a {@link SoftReference}, so in case of
 * low memory the cache will be cleaned up.
 * @author D042807
 *
 */
public class HashCache<T> implements ValueCache<T> {
    
    private SoftReference<T[]> _cache;
    
    /** The cache hits to calculate if a resize is necessary. */
    private long _hit;

    /** The cache misses (multiplied by 2) to calculate if a resize is necessary. */
    private long _misX2;

    /** The initial cache size. */
    private final int _initialSize;

    /** The maximum cache size. */
    private final int _maxSize;
    
    /**
     * Creates a HashCache with an intitial size that can be increased to a
     * maximum size.
     * @param pInitialSize The initial size of the cache.
     * @param pMaxSize The maximum size of the cache.
     */
    public HashCache(final int pInitialSize, final int pMaxSize) {
        int initialSize = 1;
        while (initialSize < pInitialSize) {
            initialSize <<= 1;
        }
        _initialSize = initialSize;
        _maxSize = pMaxSize;
    }

    public T getCachedValue(T pValue) {
        int hashCode = pValue.hashCode();
        int index;
        T[] cache = null;
        if (_cache != null) {
            cache = _cache.get();
        }
        if (cache == null) {
            _hit = 0;
            _misX2 = 0;
            cache = (T[])new Object[_initialSize];
            _cache = new SoftReference<T[]>(cache);
            index = hashCode & (_initialSize - 1);
        } else {
            int cacheSize = cache.length;
            index = hashCode & (cacheSize - 1);
            T cacheValue = cache[index];
            if (pValue.equals(cacheValue)) {
                _hit++;
                return cacheValue;
            }
            if ((cacheValue != null) && (_maxSize > cacheSize)) {
                _misX2 = _misX2 + 2;
                if ((_misX2 >= cacheSize) && (_misX2 >= _hit)) {
                    cache = resizeCache(cache);
                    index = hashCode & (cache.length - 1);
                }
            }
        }
        cache[index] = pValue;
        return pValue;
    }
    
    /**
     * Doubles the cache size. All old entries will be inserted in the new
     * array. Resets hit and mis counters.
     */
    private T[] resizeCache(T[] pOldCache) {
        T[] newCache = (T[])new Object[2*pOldCache.length];
        int mask = newCache.length - 1;
        for (T object: pOldCache) {
            if (object != null) {
                newCache[object.hashCode() & mask] = object;
            }
        }
        _hit = 0;
        _misX2 = 0;
        _cache = new SoftReference<T[]>(newCache);
        return newCache;
    }

}
