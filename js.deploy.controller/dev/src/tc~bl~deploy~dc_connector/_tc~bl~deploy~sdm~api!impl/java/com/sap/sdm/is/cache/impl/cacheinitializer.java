package com.sap.sdm.is.cache.impl;

import com.sap.sdm.is.cache.CacheFactory;

/**
 * @author Java Change Management Sep 23, 2003
 */
public final class CacheInitializer {
	private CacheInitializer() {
	}

	public static void init() {
		CacheFactory.setInstance(CacheFactoryImpl.INSTANCE);
	}
}
