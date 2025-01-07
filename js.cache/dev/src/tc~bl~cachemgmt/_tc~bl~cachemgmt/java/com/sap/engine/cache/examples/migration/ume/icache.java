/*
 * Created on 2004.8.17
 *
 */
package com.sap.engine.cache.examples.migration.ume;

/**
 * @author petio-p
 *
 */
public interface ICache {

  /**
   *  get the cache entry associated with the given key
   *
   *@param  key  an object suitable as a HashMap key (i.e. which overrides
   *      java.lang.Object.hashCode()
   *@return      the cached object
   */
  public Object get(Object key);

  /**
   *  invalidate the cache entry associated with the given key locally.
   *  Caches wich are distributed over several JVMs or boxes will not 
   *  invalidate the cache entry associated with the given key.
   *
   *@param  key  an object suitable as a HashMap key (i.e. which overrides
   *      java.lang.Object.hashCode()
   *@return      true if the object was found and invalidated in the local cache
   */
  public boolean invalidateLocally (Object key);
    
  /**
   *  invalidate all cache entries locally.
   *  Caches wich are distributed over several JVMs or boxes will not 
   *  invalidate their cache entries.
   *
   *@return true if the local cache was not emptry and all entries are invalidated
   */
  public boolean invalidateLocally();

  /**
   *  invalidate the cache entry associated with the given key.
   *  Caches wich are distributed over several JVMs or boxes will also 
   *  invalidate the cache entry associated with the given key.
   *
   *@param  key  an object suitable as a HashMap key (i.e. which overrides
   *      java.lang.Object.hashCode()
   *@return      true if the object was found and invalidated in the local cache
   */
  public boolean invalidate (Object key);
    
  /**
   *  invalidate all cache entries.
   *  Caches wich are distributed over several JVMs or boxes will also 
   *  invalidate their cache entries.
   *
   *@return true if the local cache was not emptry and all entries are invalidated
   */
  public boolean invalidate();

  /**
   *  Resets all counters for this cache.
   *
   *@return true if all counters are reset
   */
  public boolean clearStatistics();

  /**
   *  puts the cache entry associated with the given key and the specified max. life time
   *  into the cache.
   *
   *@param  key  an object suitable as a HashMap key (i.e. which overrides
   *      java.lang.Object.hashCode()
   *@param  entry the cache entry associated with the key
   *@param  lifetime the max. life time for the given cache entry in seconds
   */
  public void put ( Object key, Object entry, int lifetime );

  /**
   *  puts the cache entry associated with the given key into the cache.
   *  Note: the max. life time is the default life time for this cache.
   *
   *@param  key  an object suitable as a HashMap key (i.e. which overrides
   *      java.lang.Object.hashCode()
   *@param  entry the cache entry associated with the key
   */
  public void put ( Object key, Object entry );

  /**
   *  initializes the cache instance with the given parameters, and has to be called before any other method
   *  on a newly created cache instance is called.
   *
   *@param  initialSize  the initial size of the cache.
   *@param  maxLifeTimeInSeconds  the default max. life time for all cache entries which are put into the
   *        cache without a max. life time specified
   *@param  owner  the unique id of the cache owner (this key is used to lookup the instances in distributed
   *        environments, e.g. USER_CACHE, GROUP_CACHE, ACL_CACHE etc.)
   *@param  useNotification  specifies whether the cache is sending notifications in distributed environments.
   */
  public void initialize (int initialSize, int maxLifeTimeInSeconds, String owner, boolean useNotification);
    
  /**
   *  gets the number of hits
   *
   *@return the number of hits
   */
  public long getHitCount();

  /**
   *  gets the number of currently existing cache entries
   *
   *@return the number of cache entries
   */
  public long getLoadCount();

  /**
   *  gets the number of missed accesses.
   *  Note: this method is only for convenience, because this number is the same as
   *        getReadCounter()-getHitCounter()
   *
   *@return the number of missed accesses
   */
  public long getMissCount();
    
  /**
   *  gets the number of get requests
   *
   *@return the number of get requests
   */
  public long getReadCount();
    
  /**
   *  gets the number of put requests
   *
   *@return the number of put requests
   */
  public long getWriteCount();
    
  /**
   *  gets the max. number of entries which can be stored in the cache.
   *
   *@return the max. number of entries
   */
  public int getMaxSize();
    
  /**
   *  forces a cleanup of outdated cache entries to get accurate statistics
   */
  public void cleanup();

}
