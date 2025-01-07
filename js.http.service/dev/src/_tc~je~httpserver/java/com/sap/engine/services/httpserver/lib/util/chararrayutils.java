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
package com.sap.engine.services.httpserver.lib.util;

public class CharArrayUtils {
  public static int indexOf(char[] array, char toSearch) {
    if (array == null) {
      return -1;
    }
    for (int k = 0; k < array.length; k++) {
      if (array[k] == toSearch) {
        return k;
      }
    }
    return -1;
  }

  public static int indexOf(char[] array, char toSearch, int fromIndex) {
    if (array == null || fromIndex >= array.length) {
      return -1;
    }
    if (fromIndex < 0) {
      fromIndex = 0;
    }
    for (int k = fromIndex; k < array.length; k++) {
      if (toSearch == array[k]) {
        return k;
      }
    }
    return -1;
  }

  public static int lastIndexOf(char[] arr, char toSearch) {
    if (arr == null) {
      return -1;
    }
    for (int i = arr.length - 1; i >= 0; i--) {
      if (arr[i] == toSearch) {
        return i;
      }
    }
    return -1;
  }
}
