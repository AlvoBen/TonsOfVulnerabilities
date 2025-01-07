/*==============================================================================
    File:         ElementConfiguration.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache;

/**
 * The <code>ElementConfiguration</code> class constitutes an administrative 
 * interface to access cache region element configuration.
 * 
 * @author Petev, Petio, Michael Wintergerst
 */
public interface ElementConfiguration extends ElementConfigurationInfo {

  /**
   * Default setting for the time-to-live value.
   */
  public static final long DEFAULT_TIME_TO_LIVE = -1;
    
  /**
   * Default setting for the absolute eviction time.
   */
  public static final long DEFAULT_ABS_EVICTION_TIME = -1; 
    
  /**
   * Sets the time to live value for the objects in a cache region or a 
   * group.
   * <p>
   * The time to live value indicates the time span the objects will be kept
   * after putting them in the cache before nominating them for eviction. 
   * <p> 
   * A negative value indicates that no time to live value is desired.
   *
   * @param interval the time in milliseconds that objects will be kept 
   *        after putting them in the cache before nominating them for
   *        eviction
   */
  public void setTimeToLive(long interval);

  /**
   * Sets the absolute time of eviction for objects in a cache region or
   * a cache group.
   * <p>
   * A negative value indicates that no absolute eviction time is desired.
   *
   * @param time the moment in time measured in milliseconds the objects
   *        will be evicted
   * 
   * @throws IllegalArgumentException if the time <code>value</code> is set
   *         in the past
   */
  public void setAbsEvictionTime(long time);
    
  /**
   * Sets the eviction period the absolute eviction time should be 
   * associated with.
   * <p>
   * The <code>period</code> parameter indicates that the eviction time
   * should be updated automatically if elapsed. That means newly inserted
   * objects will be associated with the updated eviction time determined by
   * the current eviction time plus the the specified period. Objects
   * associated with the old eviction time and which are still in the cache
   * are not affected by the new eviction time.
   * <p>
   * If the <code>period</code> parameter is set to <code>null</code>, 
   * the absolute eviction time will not be automatically updated.
   * 
   * @param period the eviction period the absolute eviction time should 
   *        be associated with
   */
  public void setEvictionPeriod(EvictionPeriod period);
    
  /**
   * Sets the absolute time of eviction for objects in a cache region or
   * a cache group.
   * <p>
   * A negative value indicates that no absolute eviction time is desired.
   * <p>
   * The <code>period</code> parameter indicates that the eviction time
   * should be updated automatically if elapsed. That means newly inserted
   * objects will be associated with the updated eviction time determined by
   * the current eviction time plus the the specified period. Objects
   * associated with the old eviction time and which are still in the cache
   * are not affected by the new eviction time.
   * <p>
   * If the <code>period</code> parameter is set to <code>null</code>, 
   * the absolute eviction time will not be automatically updated.
   *
   * @param time the moment in time measured in milliseconds the objects
   *        will be evicted
   * @param period the eviction period the absolute eviction time should 
   *        be associated with
   * 
   * @throws IllegalArgumentException if the <code>time</code> value is set
   *         in the past
   */
  public void setAbsEvictionTime(long time, EvictionPeriod period);

  /**
   * Sets the absolute time of eviction for objects in a cache region or
   * a cache group.
   * <p>
   * A negative value indicates that no absolute eviction time is desired.
   * <p>
   * The <code>day</code>, <code>hour</code> and <code>min</code> parameter 
   * indicates that the eviction time should be updated automatically
   * if elapsed. That means newly inserted objects will be associated with 
   * the updated eviction time determined by the current eviction time plus
   * the the specified period. Objects associated with the old eviction time
   * and which are still in the cache are not affected by the new eviction
   * time.
   *
   * @param time the moment in time measured in milliseconds the objects
   *        will be evicted
   * 
   * @throws IllegalArgumentException if the <code>time</code> value is set
   *         in the past or one of parameters <code>day</code>, 
   *         <code>hour</code> and <code>min</code> is set to a negative 
   *         value 
   */
  public void setAbsEvictionTime(long time, int day, int hour, int min);

}