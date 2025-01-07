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
 * This interface must be implemented by each class which is using
 * CacheManager in order to define what object will be returned when
 * get(key) or get(object) operation fails to find anything. The only way
 * to create CacheManager is to pass an implementation of ObjectCreator interface
 *
 * There is a basic implementation of this interface the class ObjectCreatorBaseImpl
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */

public interface ObjectCreator {

  /**
   * This method must be implemented in order to define what
   * object will be returned if get(key) fails to find anything.
   *
   * @param key to a object which will not be found in the cache
   *
   * @return Some object according to the user's logic
   */
  public Object createObjectByKey(Object key);

  /**
   * This method must be implemented in order to define what
   * key object will be returned if get(object) fails to find anything.
   *
   * @param object which will not be found in the cache, so there is no valid key
   * for this object in this key
   *
   * @return Some key according to the user's logic
   */
  public Object createKeyByObject(Object object);
}