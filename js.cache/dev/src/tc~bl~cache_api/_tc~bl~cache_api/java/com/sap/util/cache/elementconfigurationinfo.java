/*==============================================================================
    File:         ElementConfigurationInfo.java
    Created:      20.07.2004

    $Author: d039261 $
    $Revision: #1 $
    $Date: 2004/07/29 $
==============================================================================*/
package com.sap.util.cache;

/**
 * The <code>ElementConfigurationInfo</code> interface constitutes a 
 * read-only interface to the cache region element configuration.
 * 
 * @author Petio Petev, Michael Wintergerst
 */

public interface ElementConfigurationInfo {

  /**
   * Gets the time to live value configured for the specific objects in a
   * cache region or a group.
   * <p>
   * The time to live value indicates the time span the objects will be kept
   * after putting them in the cache before nominating them for eviction.
   *
   * @return the time in milliseconds that objects will be kept after putting
   *         them in the cache before nominating them for eviction; if the 
   *         value is not set, <code>-1</code> is returned 
   */
  public long getTimeToLive();

  /**
   * Returns the absolute time of eviction for objects in the cache region
   * or group.
   *
   * @return the moment in time measured in milliseconds the objects will
   *         be evicted; if the value is not set, <code>-1</code> is 
   *         returned
   */
  public long getAbsEvictionTime();
    
  /**
   * Returns the eviction period associated with the absolute eviction time.
   * 
   * @return the eviction period associated with the absolute evicition time
   *         or <code>null</code> if not set
   */
  public EvictionPeriod getEvictionPeriod();

}