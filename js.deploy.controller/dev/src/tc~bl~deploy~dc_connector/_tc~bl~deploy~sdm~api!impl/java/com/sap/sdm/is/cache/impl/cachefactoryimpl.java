package com.sap.sdm.is.cache.impl;

import com.sap.sdm.is.cache.Cache;
import com.sap.sdm.is.cache.CacheFactory;
import com.sap.sdm.util.log.Logger;

/**
 * @author Java Change Management Sep 23, 2003
 */
final class CacheFactoryImpl extends CacheFactory {
	private final static Logger log = Logger.getLogger();

	final static CacheFactory INSTANCE = new CacheFactoryImpl();

	private CacheFactoryImpl() {
	}

	public Cache newLruCache(int maxSize) {
		if (maxSize <= 0) {
			String errText = "Parameter maxSize has invalid value " + maxSize;
			log.fatal(errText);

			throw new IllegalArgumentException(errText);
		}

		return new LruCache(maxSize);
	}

}
