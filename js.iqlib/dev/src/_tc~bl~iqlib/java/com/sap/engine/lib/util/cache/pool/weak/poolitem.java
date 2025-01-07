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

package com.sap.engine.lib.util.cache.pool.weak;

import java.lang.ref.WeakReference;

/**
 * This is an item in the weak pool
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class PoolItem extends WeakReference {

  protected PoolItem prev;
  protected PoolItem next;

  /**
   * Constructor
   */
  public PoolItem(Object obj) {
    super(obj, WeakPool.queue);
  }


}