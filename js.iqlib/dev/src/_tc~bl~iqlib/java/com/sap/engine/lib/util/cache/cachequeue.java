/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.util.cache;

/**
 * Interface for cache queue. Here are the operations which could
 * be performed with a CacheQueue instantion.
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public interface CacheQueue {

  /**
   * Updates node item in the cache queue
   *
   * @param   node  the node to be updated
   */
  public void update(CacheQueueItem node);

  /**
   * Adds a note to the queue
   *
   * @param   node  the node to be added
   * @return  replaced node or null
   */
  public CacheQueueItem add(CacheQueueItem node);

  /**
   * Removes a node from the queue
   *
   * @param   node  the node to be removed
   * @return  removed node
   */
  public CacheQueueItem remove(CacheQueueItem node);

  /**
   * Removes the last element in the queue
   *
   * @return removed element
   */
  public CacheQueueItem removeLast();

  /**
   * @return the low size boundary of this queue
   */
  public int getMinSize();

  /**
   * @return the high size boundary of this queue
   */
  public int getMaxSize();

  /**
   * @return the size of this queue
   */
  public int getSize();

  /**
   * Prints this queue
   */
  public void print();

  /**
   * Empties this queue
   */
  public void clear();


}