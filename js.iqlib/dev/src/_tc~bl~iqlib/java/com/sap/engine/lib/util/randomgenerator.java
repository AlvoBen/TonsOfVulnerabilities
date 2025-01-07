/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util;

/**
 * FAST random number generator
 *
 * @author Vasil Popovski
 * @version 1.0
 */
public class RandomGenerator {

  private static final int A = 48271;
  private static final int M = 2147483647;
  private static final int Q = M / A;
  private static final int R = M % A;
  private int state;

  public RandomGenerator() {
    this((int) (System.currentTimeMillis() % M));
  }

  public RandomGenerator(int initialValue) {
    if (initialValue < 0) {
      initialValue += M;
    }

    state = initialValue;

    if (state == 0) {
      state = 1;
    }
  }

  public int nextInt() {
    int tmp = A * (state % Q) - R * (state / Q);

    if (tmp >= 0) {
      state = tmp;
    } else {
      state = tmp + M;
    }

    return state;
  }

}

