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
 * The ByteCache takes advantage of the implementation detail that
 * {@link Byte#valueOf(byte)} returns cached values.
 * @author D042807
 *
 */
public class ByteCache implements ValueCache<Byte> {

    private static final ByteCache _instance = new ByteCache();
    
    private ByteCache() {
    }
    
    /**
     * Returns a singleton instance of the cache that in not specialized to a
     * single property.
     * @return The singleton ByteCache.
     */
    public static ByteCache getInstance() {
        return _instance;
    }

    public Byte getCachedValue(Byte pValue) {
        return Byte.valueOf(pValue.byteValue());
    }

}
