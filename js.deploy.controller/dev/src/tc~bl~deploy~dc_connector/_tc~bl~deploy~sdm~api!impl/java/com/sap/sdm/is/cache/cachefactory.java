package com.sap.sdm.is.cache;

/**
 * A <code>Cache</code> factory.
 * 
 * @author Java Change Management Sep 22, 2003
 */
public abstract class CacheFactory {
	private static CacheFactory instance;

	public static void setInstance(CacheFactory instance) {
		CacheFactory.instance = instance;
	}

	public static CacheFactory getInstance() {
		return instance;
	}

	/**
	 * Creates a new <code>Cache</code>. Cached associations will be removed
	 * according to the least recently used strategy: After the cache size has
	 * exceeded <code>maxSize</code> associations, the associations that have
	 * been least recently used (stored or retrieved) will be removed. Note that
	 * it is not specified how many associations will actually be removed.
	 * 
	 * <p>
	 * The created <code>Cache</code> is thread-safe.
	 * </p>
	 * 
	 * @param maxSize
	 *            specifies the maximum size of the cache before its removal
	 *            strategy is applied; must be greater than zero
	 * @return a new <code>Cache</code> object
	 * @throws IllegalArgumentException
	 *             if maxSize is less than or equal to zero
	 */
	public abstract Cache newLruCache(int maxSize);
}
