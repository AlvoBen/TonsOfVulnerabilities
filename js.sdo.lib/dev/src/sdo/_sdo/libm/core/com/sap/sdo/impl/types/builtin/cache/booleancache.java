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
 * The BooleanCache takes advantage of the implementation detail that
 * {@link Boolean#valueOf(boolean)} returns cached values.
 * @author D042807
 *
 */
public class BooleanCache implements ValueCache<Boolean> {

    private static final BooleanCache _instance = new BooleanCache();
    
    private BooleanCache() {
    }
    
    /**
     * Returns a singleton instance of the cache that in not specialized to a
     * single property.
     * @return The singleton BooleanCache.
     */
    public static BooleanCache getInstance() {
        return _instance;
    }

    public Boolean getCachedValue(Boolean pValue) {
        return Boolean.valueOf(pValue.booleanValue());
    }

}
