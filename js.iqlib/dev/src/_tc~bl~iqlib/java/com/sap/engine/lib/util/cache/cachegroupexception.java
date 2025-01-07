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
 * If you stop the CacheGroup thread and perform any operation with
 * the cache then a CacheGroupException will be thrown.
 * The work of the Memory Sensitive Cache was disturbed, without
 * the thread the cache will be overfilled with SoftValues which will
 * not be removed and soon the whole cache system will be lumbered
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class CacheGroupException extends RuntimeException {

  public CacheGroupException() {
    super("Performed cache operation with stopped CacehGroup thread.");
  }

}