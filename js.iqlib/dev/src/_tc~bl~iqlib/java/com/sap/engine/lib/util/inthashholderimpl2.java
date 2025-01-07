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
 * This class uses Jenkins' 96 bit Mix Function.<p>
 *
 * @author unknown
 * @version 1.0
 */
public class IntHashHolderImpl2 implements IntHashHolder {

  static final long serialVersionUID = 7671557872794132906L;
  private int ai = 0;
  private int bi = 0;

  /**
   * Constructor.<p>
   *
   * @param a any random interger value.
   * @param b any random interger value.
   */
  public IntHashHolderImpl2(int a, int b) {
    ai = a;
    bi = b;
  }

  /**
   * This function must return positive number.<p>
   *
   * @param key an int.
   * @return hash value.
   */
  public int hash(int key) {
    int a = ai;
    int b = bi;
    a = a - b;
    a = a - key;
    a = a ^ (key >> 13);
    b = b - key;
    b = b - a;
    b = b ^ (a << 8);
    key = key - a;
    key = key - b;
    key = key ^ (b >> 13);
    a = a - b;
    a = a - key;
    a = a ^ (key >> 12);
    b = b - key;
    b = b - a;
    b = b ^ (a << 16);
    key = key - a;
    key = key - b;
    key = key ^ (b >> 5);
    key = key - a;
    key = key - b;
    key = key ^ (b >> 3);
    b = b - key;
    b = b - a;
    b = b ^ (a << 10);
    key = key - a;
    key = key - b;
    key = key ^ (b >> 15);
    key = key & 0x7fffffff;
    return key;
  }

  /**
   * Gets the parameters which are used to initialized this class.<p>
   *
   * @return the parameters which are used to initialized this class.
   */
  public int[] getTheParameters() {
    int[] params = new int[2];
    params[0] = ai;
    params[1] = bi;
    return params;
  }

}

