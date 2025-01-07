package com.sap.sdm.is.cache;

/**
 * A caching mechanism. The functionality of a cache is similar to a map.
 * However, in contrast to a map the stored key-value associations are removed
 * by the cache, not by the user of the cache. The user of a cache has no means
 * to remove key-value associations him/herself.
 * 
 * <p>
 * Different implementations of <code>Cache</code> use different removal
 * strategies. An implementation of this interface must indicate which strategy
 * it uses and whether it is thread-safe or not (for example in a factory
 * method).
 * </p>
 * 
 * @author Java Change Management Sep 22, 2003
 */
public interface Cache {

	/**
	 * Associates the specified value with the specified key in this
	 * <code>Cache</code>. If the cache previously contained an association for
	 * this key, the old value is replaced by the specified value.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return previous value associated with specified key, or
	 *         <code>null</code> if there was no association for
	 *         <code>key</code>
	 */
	public Object put(Object key, Object value);

	/**
	 * Returns the value to which this <code>Cache</code> associates the
	 * specified key. Returns <code>null</code> if the cache contains no
	 * association for this key, or if the specified key is associated to
	 * <code>null</code>.
	 * 
	 * @param key
	 *            key whose associated value is to be returned
	 * @return the value to which this cache associates the specified key, or
	 *         <code>null</code> if the cache contains no association for this
	 *         key or if the cache has removed the association
	 */
	public Object get(Object key);
}
