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
 * Thomas Wang's 64 bit Mix Function.<p>
 *
 * @author unknown
 * @version 1.0
 */
public class LongHashHolderImpl0 implements LongHashHolder {

  static final long serialVersionUID = -960058991873949882L;
  /**
   * This function must return positive number.<p>
   *
   * @param key a long.
   * @return the hash value.
   */
  public int hash(long key) {
    key ^= ((~key) >>> 31);
    key += (key << 28);
    key ^= (key >>> 21);
    key += (key << 3);
    key ^= ((~key) >>> 5);
    key += (key << 13);
    key ^= (key >>> 27);
    key += (key << 32);
    return (int) (key & 0x7fffffff);
  }

}

