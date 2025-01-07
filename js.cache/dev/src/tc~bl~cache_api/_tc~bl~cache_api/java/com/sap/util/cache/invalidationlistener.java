/*==============================================================================
    File:         InvalidationListener.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache;

/**
 * An interface implemented by the cache user which can be registered with the
 * cache region to receive events of changes in the cache.
 * <p>
 * <b>
 * Note that it depends on the concrete implementation whether invalidation listeners 
 * are supported.
 * </b>
 * 
 * @see com.sap.util.cache.CacheFeatureSet
 * 
 * @author Petio Petev, Michael Wintergerst
 */
public interface InvalidationListener {

    public static final byte EVENT_INVALIDATION = 0;

    public static final byte EVENT_REMOVAL = 1;

    public static final byte EVENT_MODIFICATION = 2;

    public static final byte EVENT_ATT_CHANGE = 3;

    // Added by the request of SLD. Only the customly implemented 
    // InvalidationListener of the user will process the event.
    public static final byte EVENT_PUT = 5; 

    public static final byte EVENT_SEMANTIC_INVALIDATION = 10;

    public static final byte EVENT_SEMANTIC_REMOVAL = 11;

    /**
     * The method is invoked when an event about changes in the cache region is 
     * due.
     *
     * @param key The cached object key of the object that has been changed
     * @param event The type of the invalidation, can be
     *              <code>InvalidationListener.EVENT_INVALIDATION</code> - if
     *              an explicit invalidation was done; 
     *              <code>InvalidationListener.EVENT_REMOVAL</code> - if 
     *              invalidation due to remove invokation was done;
     *              <code>InvalidationListener.EVENT_MODIFICATION</code> - if
     *              invalidation due to successive put invokation was done
     */
    public void invalidate(String key, byte event);
    
    /**
     * The method is invoked when an event about changes in the cache region
     * is due. This method is called only when the storage plugin supports
     * transportation of objects.
     *
     * @param key The cached object key of the object that has been changed
     * @param cachedObject The value of the cached object that has been changed
     */
    public void invalidate(String key, Object cachedObject);
}