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
package com.sap.engine.lib.xsl.xpath;

/**
 * @author Nick Nickolov, e-mail nick_nickolov@abv.bg
 * @version 0.0.1
 */
public final class IntStack {

  private static final int DEFAULT_CAPACITY = 20;
  private int RESIZE_STEP = 10;
  int[] a;
  int n;

  IntStack() {
    a = new int[DEFAULT_CAPACITY];
    n = 0;
  }

  IntStack(int initialCapacity) {
    a = new int[initialCapacity];
    n = 0;
  }

  private void resize() {
    int capacity1 = a.length + RESIZE_STEP;
    RESIZE_STEP *= 2;
    int[] a1 = new int[capacity1];
    System.arraycopy(a, 0, a1, 0, a.length);
    a = a1;
  }

  void push(int x) {
    if (a.length == n) {
      resize();
    }

    a[n] = x;
    n++;
  }

  int pop() {
    n--;
    return a[n];
  }

  int top() {
    return a[n - 1];
  }

  int peek() {
    return a[n - 1];
  }

  int subTop() {
    return a[n - 2];
  }

  void clear() {
    n = 0;
  }

}

