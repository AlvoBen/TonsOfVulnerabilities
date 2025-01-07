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

public class SortUtils {
  public static void sort(String[][] all) {
    String res[][] = (String[][]) all.clone();
    mergeSort(res, all, 0, all.length);
    res = (String[][]) all.clone();
    resort(res, all);
  }

  public static int[] sort(int[] sortedQs) {
    for (int i = 0; i < sortedQs.length - 1; i++) {
      for (int j = (i + 1); j < sortedQs.length; j++) {
        int temp1 = sortedQs[i];
        int temp2 = sortedQs[j];
        if (temp1 < temp2) {
          sortedQs[j] = temp1;
          sortedQs[i] = temp2;
        }
      }
    }
    return sortedQs;
  }

  // ------------------------> private <------------------------

  private static void mergeSort(String src[][], String dest[][], int low, int high) {
    int length = high - low;

    if (length < 7) {
      for (int i = low; i < high; i++) {
        for (int j = i; j > low && (Integer.valueOf(dest[j - 1][0]).intValue() > Integer.valueOf(dest[j][0]).intValue()); j--) {
          swap(dest, j, j - 1);
        }
      }

      return;
    }

    int mid = (low + high) / 2;
    mergeSort(dest, src, low, mid);
    mergeSort(dest, src, mid, high);

    if (Integer.valueOf(src[mid - 1][0]).intValue() <= Integer.valueOf(src[mid][0]).intValue()) {
      System.arraycopy(src, low, dest, low, length);
      return;
    }

    for (int i = low, p = low, q = mid; i < high; i++) {
      if (q >= high || p < mid && (Integer.valueOf(src[p][0]).intValue() <= Integer.valueOf(src[q][0]).intValue())) {
        dest[i] = src[p++];
      } else {
        dest[i] = src[q++];
      }
    }
  }

  private static void resort(String src[][], String dst[][]) {
    int offset = 0;
    int i = 0;

    while (i < src.length) {
      if (new Integer(src[i][0]).intValue() == 0) {
        break;
      }

      i++;
      offset++;
    }

    for (i = offset; i < src.length; i++) {
      dst[i - offset] = src[i];
    }

    int off = src.length - offset;

    for (i = 0; i < offset; i++) {
      dst[i + off] = src[i];
    }
  }

  /**
   * Swaps x[a] with x[b].
   */
  private static void swap(String x[][], int a, int b) {
    String[] t = x[a];
    x[a] = x[b];
    x[b] = t;
  }
}
