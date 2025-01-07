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
 * This is a basic implementation of ObjectCreator interface.
 *
 * Note: This implementation DOES NOT pretend to be logically right in any case
 *
 * @author Iliyan Nenov, ilian.nenov@sap.com
 * @version SAP J2EE Engine 6.30
 */
public class ObjectCreatorBaseImpl implements ObjectCreator {

  /**
   * Basic implementation. DOES NOT pretend to be logivcally right in any case
   */
  public Object createObjectByKey(Object key) {
    return null;
  }

  /**
   * Basic implementation. DOES NOT pretend to be logivcally right in any case
   */
  public Object createKeyByObject(Object object) {
    return null;
  }

}