/*
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
 * A hash function.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.0
 */
public class LongHashHolderImpl implements LongHashHolder {
  
  static final long serialVersionUID = -8309896164661725845L;
  /**
   * This function must return positive number.<p>
   *
   * @param key a long.
   * @return the hash value.
   */
  public int hash(long key) {
    return (int) (key & 0x7fffffff);
  }

}

