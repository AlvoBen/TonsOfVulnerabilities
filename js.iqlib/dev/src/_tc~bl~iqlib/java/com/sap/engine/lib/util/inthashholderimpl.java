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
public class IntHashHolderImpl implements IntHashHolder {

  static final long serialVersionUID = -8971792488161235503L;
  /**
   * This function must return positive number.<p>
   *
   * @param key an int.
   * @return hash value.
   */
  public int hash(int key) {
    return key & 0x7fffffff;
  }

}

