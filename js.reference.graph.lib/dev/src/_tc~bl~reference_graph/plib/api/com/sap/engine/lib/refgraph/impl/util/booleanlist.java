/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph.impl.util;


/**
 *@author Luchesar Cekov
 */
public class BooleanList {
  private static final int DEFAULT_CAPACITY = 20;
  private int RESIZE_STEP = 10;
  boolean[] a;
  int n;

  public BooleanList() {
    a = new boolean[DEFAULT_CAPACITY];
    n = 0;
  }

  private void resize() {
    int capacity1 = a.length + RESIZE_STEP;
    RESIZE_STEP *= 2;
    boolean[] a1 = new boolean[capacity1];
    System.arraycopy(a, 0, a1, 0, a.length);
    a = a1;
  }

  public void add(boolean x) {
    if (a.length == n) {
      resize();
    }

    a[n] = x;
    n++;
  }

  public boolean removeLast() {
    n--;
    return a[n];
  }

  public int size() {
    return n;
  }

  public boolean get(int index) {
    if (index > n - 1) {
      throw new IllegalArgumentException("size" + n + " index" + index + " index should be at most size-1");
    }
    return a[index];
  }
}
