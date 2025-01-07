/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util;

/**
 * This class uses Wang's 32 bit Mix Function.<p>
 *
 * @author unknown
 * @version 1.0
 */
public class IntHashHolderImpl1 implements IntHashHolder {

  static final long serialVersionUID = 4823649086773337932L;
  /**
   * This function must return positive number.<p>
   *
   * @param key an int.
   * @return hash value.
   */
  public int hash(int key) {
    key += ~(key << 15);
    key ^= (key >>> 10);
    key += (key << 3);
    key ^= (key >>> 6);
    key += ~(key << 11);
    key ^= (key >>> 16);
    key = key & 0x7fffffff;
    return key;
  }

}

