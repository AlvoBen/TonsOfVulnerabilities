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
 * This class uses Jenkins' 32 bit Mix Function.<p>
 *
 * @author unknown
 * @version 1.0
 */
public class IntHashHolderImpl0 implements IntHashHolder {

  static final long serialVersionUID = -720707995428611946L;
  /**
   * This function must return positive number.<p>
   *
   * @param key an int.
   * @return hash value.
   */
  public int hash(int key) {
    key += (key << 12);
    key ^= (key >> 22);
    key += (key << 4);
    key ^= (key >> 9);
    key += (key << 10);
    key ^= (key >> 2);
    key += (key << 7);
    key ^= (key >> 12);
    return key;
  }

}

