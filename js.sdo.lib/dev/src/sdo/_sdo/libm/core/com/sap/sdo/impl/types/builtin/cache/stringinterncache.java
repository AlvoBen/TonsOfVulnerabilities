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
 * This {@link ValueCache} uses the {@link String#intern()} pool. It is
 * implemented as singleton.
 * WARNING: Use this cache only for meta-data and only for Strings that are
 * interned anyway!
 * @author D042807
 *
 */
public class StringInternCache implements ValueCache<String> {

    private static final StringInternCache _instance = new StringInternCache();
    
    private StringInternCache() {
    }
    
    /**
     * Returns the single instance.
     * @return the single instance.
     */
    public static StringInternCache getInstance() {
        return _instance;
    }

    public String getCachedValue(String pValue) {
        return pValue.intern();
    }

}
