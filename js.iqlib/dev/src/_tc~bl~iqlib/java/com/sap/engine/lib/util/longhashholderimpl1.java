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
 * Example hash function, which uses multiplication with odd constants and left shifts.<p>
 *
 * @author unknown
 * @version 1.0
 */
public class LongHashHolderImpl1 implements LongHashHolder {
  
  static final long serialVersionUID = -5656361264285043032L;
  private static final long c1 = 0x6e5ea73858134343L;
  private static final long c2 = 0xb34e8f99a2ec9ef5L;

  /**
   * This function must return positive number.<p>
   *
   * @param key a long.
   * @return the hash value.
   */
  public int hash(long key) {
    key ^= ((c1 ^ key) >>> 32);
    key *= c1;
    key ^= ((c2 ^ key) >>> 31);
    key *= c2;
    key ^= ((c1 ^ key) >>> 32);
    return (int) (key & 0x7fffffff);
  }

}

