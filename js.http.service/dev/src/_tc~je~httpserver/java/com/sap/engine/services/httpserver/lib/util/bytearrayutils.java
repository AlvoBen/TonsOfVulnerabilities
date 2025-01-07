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

public class ByteArrayUtils {
  public static byte[] append(byte first[], byte second[]) {
    if (first == null || first.length == 0) {
      return second;
    }
    if (second == null || second.length == 0) {
      return first;
    }
    byte[] newByteArr = new byte[first.length + second.length];
    System.arraycopy(first, 0, newByteArr, 0, first.length);
    System.arraycopy(second, 0, newByteArr, first.length, second.length);
    return newByteArr;
  }

  public static boolean startsWith(byte[] arr, byte[] prefix) {
    if (arr == null) {
      return false;
    }
    return startsWith(arr, 0, arr.length, prefix);
  }

  public static boolean startsWith(byte[] arr, int off, int len, byte[] prefix) {
    if (arr == null || prefix == null) {
      return false;
    }
    if (prefix.length > len) {
      return false;
    }
    for (int i = 1; i < prefix.length; i++) {
      if (prefix[i] != arr[off + i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean startsWithIgnoreCase(byte[] arr, byte[] prefix) {
    if (arr == null) {
      return false;
    }
    return startsWithIgnoreCase(arr, 0, arr.length, prefix);
  }

  public static boolean startsWithIgnoreCase(byte[] arr, int off, int len, byte[] prefix) {
    if (arr == null || prefix == null) {
      return false;
    }
    if (prefix.length > len) {
      return false;
    }
    for (int i = 1; i < prefix.length; i++) {
      if (Ascii.toLower(prefix[i]) != Ascii.toLower(arr[off + i])) {
        return false;
      }
    }
    return true;
  }

  public static boolean endsWith(byte[] arr, byte[] toend) {
    if (arr == null || toend == null) {
      return false;
    }
    if (toend.length > arr.length) {
      return false;
    }
    int toendLen = toend.length;
    int arrLen = arr.length;
    for (int i = 1; i < toendLen + 1; i++) {
      if (toend[toendLen - i] != arr[arrLen - i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean endsWithIgnoreCase(byte[] arr, byte[] toend) {
    if (arr == null || toend == null) {
      return false;
    }
    if (toend.length > arr.length) {
      return false;
    }
    int toendLen = toend.length;
    int arrLen = arr.length;
    for (int i = 1; i < toendLen + 1; i++) {
      if ((Ascii.toLower(toend[toendLen - i]) != Ascii.toLower(arr[arrLen - i]))) {
        return false;
      }
    }
    return true;
  }

  public static boolean equalsBytes(byte abyte0[], byte abyte1[]) {
    if (abyte0 == null || abyte1 == null || abyte0.length != abyte1.length) {
      return false;
    }
    int i = -1;
    while (++i < abyte0.length) {
      if (abyte0[i] != abyte1[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equalsBytes(byte abyte0[], int off, int len, byte abyte1[]) {
    if (abyte0 == null || abyte1 == null || len != abyte1.length) {
      return false;
    }
    int i = -1;
    while (++i < len) {
      if (abyte0[off + i] != abyte1[i]) {
        return false;
      }
    }
    return true;
  }

  public static boolean equalsIgnoreCase(byte[] first, byte[] sec) {
    int i = -1;

    if (first == null || sec == null || (i = first.length) != sec.length) {
      return false;
    }

    while (--i >= 0) {
      if (Ascii.toLower(first[i]) != Ascii.toLower(sec[i])) {
        return false;
      }
    }

    return true;
  }

  public static boolean equalsIgnoreCaseAndTrim(byte[] first, byte[] sec) {
    if (first == null || sec == null) {
      return false;
    }
    int i = 0;
    while (i < first.length && Ascii.isWhite(first[i])) {
      i++;
    }
    int off = i;
    if (first.length - off <= 0 || first.length < sec.length) {
      return false;
    }
    for (i = 0; i < sec.length; i++) {
      if (Ascii.toLower(first[off + i]) != Ascii.toLower(sec[i])) {
        return false;
      }
    }
    if (i == sec.length) {
      return true;
    }
    for (; i < first.length; i++) {
      if (!Ascii.isWhite(first[i])) {
        return false;
      }
    }
    return true;
  }

  public static boolean equalsIgnoreCase(byte[] first, int off, int len, byte[] sec) {
    int i = -1;
    if (first == null || sec == null || (i = len) != sec.length) {
      return false;
    }
    while (--i >= 0) {
      if (Ascii.toLower(first[off + i]) != Ascii.toLower(sec[i])) {
        return false;
      }
    }
    return true;
  }

  public static boolean equalsIgnoreCaseAndTrim(byte[] first, int off, int len, byte[] sec) {
    if (first == null || sec == null) {
      return false;
    }
    int i = 0;
    while (i < len && Ascii.isWhite(first[off + i])) {
      i++;
    }
    off += i;
    len -= i;
    if (len <= 0 || len < sec.length) {
      return false;
    }
    for (i = 0; i < sec.length; i++) {
      if (Ascii.toLower(first[off + i]) != Ascii.toLower(sec[i])) {
        return false;
      }
    }
    if (i == sec.length) {
      return true;
    }
    for (; i < len; i++) {
      if (!Ascii.isWhite(first[off + i])) {
        return false;
      }
    }
    return true;
  }

  public static int indexOf(byte[] array, byte toSearch) {
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

  public static int indexOf(byte[] array, int off, int len, byte toSearch) {
    if (array == null) {
      return -1;
    }
    for (int k = off; k < off + len; k++) {
      if (array[k] == toSearch) {
        return k - off;
      }
    }
    return -1;
  }

  public static int indexOf(byte[] array, byte[] toSearch) {
    if (array == null || toSearch == null) {
      return -1;
    }

    int arrayLen = array.length;
    int toSearchLen = toSearch.length;

    if (arrayLen < toSearchLen) {
      return -1;
    }

    for (int k = 0; k < arrayLen - toSearchLen + 1; k++) {
      if (toSearch[0] == array[k]) {
        int j = 1;

        for (; j < toSearch.length; j++) {
          if (toSearch[j] != array[k + j]) {
            break;
          }
        }

        if (j == toSearchLen) {
          return k;
        }
      }
    }

    return -1;
  }

  public static int indexOf(byte[] array, int off, int len, byte[] toSearch) {
    if (array == null || toSearch == null || toSearch.length > len) {
      return -1;
    }
    int toSearchLen = toSearch.length;
    for (int k = 0; k < len - toSearchLen + 1; k++) {
      if (toSearch[0] == array[off + k]) {
        int j = 1;
        for (; j < toSearch.length; j++) {
          if (toSearch[j] != array[off + k + j]) {
            break;
          }
        }
        if (j == toSearchLen) {
          return k;
        }
      }
    }
    return -1;
  }

  public static int indexOf(byte[] array, byte toSearch, int fromIndex) {
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
  
	public static int indexOfIgnoreCase(byte[] array, int offset, int length, byte[] toSearch) {
		if (array == null || toSearch == null) {
			return -1;
		}

		int arrayLen = length;
		int toSearchLen = toSearch.length;

		if (arrayLen < toSearchLen) {
			return -1;
		}

		for (int k = offset; k < arrayLen - toSearchLen + 1; k++) {
			if (Ascii.toLower(toSearch[0]) == Ascii.toLower(array[k])) {
				int j = 1;

				for (; j < toSearch.length; j++) {
					if (Ascii.toLower(toSearch[j]) != Ascii.toLower(array[k + j])) {
						break;
					}
				}

				if (j == toSearchLen) {
					return k - offset;
				}
			}
		}

		return -1;
	}
	
  public static int indexOfIgnoreCase(byte[] array, byte[] toSearch) {
    if (array == null || toSearch == null) {
      return -1;
    }

    int arrayLen = array.length;
    int toSearchLen = toSearch.length;

    if (arrayLen < toSearchLen) {
      return -1;
    }

    for (int k = 0; k < arrayLen - toSearchLen + 1; k++) {
      if (Ascii.toLower(toSearch[0]) == Ascii.toLower(array[k])) {
        int j = 1;

        for (; j < toSearch.length; j++) {
          if (Ascii.toLower(toSearch[j]) != Ascii.toLower(array[k + j])) {
            break;
          }
        }

        if (j == toSearchLen) {
          return k;
        }
      }
    }

    return -1;
  }

  public static int lastIndexOf(byte[] arr, int len, byte toSearch) {
    if (arr == null) {
      return -1;
    }
    for (int i = len - 1; i >= 0; i--) {
      if (arr[i] == toSearch) {
        return i;
      }
    }
    return -1;
  }

  public static byte[] trim(byte[] array) {
    if (array == null) {
      return null;
    }

    int st = 0;
    int len = array.length;
    int all = len;

    while ((st < len) && (array[st] <= 32)) {
      st++;
    }

    while ((st < len) && (array[len - 1] <= 32)) {
      len--;
    }

    if ((st > 0) || (len < all)) {
      byte[] newArray = new byte[len - st];
      System.arraycopy(array, st, newArray, 0, newArray.length);
      return newArray;
    }

    return array;
  }

  public static void replace(byte[] array, byte oldByte, byte newByte) {
    if (array == null) {
      return;
    }
    for (int i = 0; i < array.length; i++) {
      if (array[i] == oldByte) {
        array[i] = newByte;
      }
    }
  }

  public static int bytesToInt(byte b1, byte b2) {
    return ((int) b1 & 0xFF) | (((int) b2 & 0xFF) << 8);
  }
}
