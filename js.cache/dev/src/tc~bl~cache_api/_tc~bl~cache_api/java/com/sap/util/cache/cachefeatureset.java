/*==============================================================================
    File:         CacheFeatureSet.java
    Created:      09.08.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/08/12 $
==============================================================================*/
package com.sap.util.cache;

/**
 * The <code>CacheFeatureSet</code> interface encapsulates implementation
 * information about optional cache features. 
 * <br>
 * The caching infrastructure marks some features as optional. That means a
 * corresponding implementation is free to support these features or to omit
 * them. Via this interface the cache user is able to request whether an 
 * implementation supports an optional feature or not.
 * 
 * @author Petio Petev, Michael Wintergerst
 * @version $Revision: #1 $
 */
public interface CacheFeatureSet {

    /**
     * Identifier for invalidation listener feature.
     */    
    public static final String INVALIDATION_LISTENER = 
            "InvalidationListenerSupported";
    
    /**
     * Identifer for direct object invalidation feature.
     */
    public static final String DIRECT_OBJECT_INVALIDATION = 
            "DirectObjectInvalidationSupported";
    
    /**
     * Identifier for client dependent caching feature.
     */
    public static final String CLIENT = 
            "ClientSupported";
        
    /**
     * Checks whether an implementation supports the concepts of invalidation
     * listeners.
     *  
     * @return <code>true</code> if the implementation supports invalidation 
     *         listener; otherwise <code>false</code> is returned
     */
    public boolean isInvalidationListenerSupported();
    
    /**
     * Checks whether an implemenation supports the direct object invalidation
     * mode. 
     * 
     * @return <code>true</code> if the implementation supports a direct 
     *         object invalidation mode; otherwise <code>false</code> is
     *         is returned
     */
    public boolean isDirectObjectInvalidationSupported();
    
    /**
     * Checks whether the implementation supports client dependent caching.
     * 
     * @return <code>true</code> if client dependent caching caching is 
     *         supported; otherwise <code>false</code> is returned
     */
    public boolean isClientSupported();
    
    /**
     * Checks whether the specified feature is supported.
     * 
     * @param feature cache feature to be checked for support
     * 
     * @return <code>true</code> if the feature is supported; otherwise
     *         <code>false</code> is returned
     * 
     * @throws NullPointerException if the <code>feature</code> parameter is 
     *                              set to <code>null</code>
     */
    public boolean isSupported(String feature);
}