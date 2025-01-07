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

import com.sap.engine.services.httpserver.lib.exceptions.HttpNumberFormatException;
import com.sap.engine.services.httpserver.lib.exceptions.HttpIllegalArgumentException;

/*
 * Defines some methods for ASCII characters
 *
 * @author Galin Galchev
 * @version 4.0
 */
public class Ascii {
  private static final byte[] toUpper = new byte[256];
  private static final byte[] toLower = new byte[256];
  private static final boolean[] isAlpha = new boolean[256];
  private static final boolean[] isUpper = new boolean[256];
  private static final boolean[] isLower = new boolean[256];
  private static final boolean[] isWhite = new boolean[256];
  private static final boolean[] isDigit = new boolean[256];

  private static final byte[] zero = "0".getBytes();

  static {
    for (int i = 0; i < 256; i++) {
      toUpper[i] = (byte) i;
      toLower[i] = (byte) i;
    }

    for (int j = 97; j <= 122; j++) {
      int k = (j + 65) - 97;
      toUpper[j] = (byte) k;
      toLower[k] = (byte) j;
      isAlpha[j] = true;
      isAlpha[k] = true;
      isLower[j] = true;
      isUpper[k] = true;
    }

    isWhite[32] = true;
    isWhite[9] = true;
    isWhite[13] = true;
    isWhite[10] = true;
    isWhite[12] = true;
    isWhite[8] = true;

    for (int l = 48; l <= 57; l++) {
      isDigit[l] = true;
    }
  }

  private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  private static final byte[] digitBytes = new String(digits).getBytes();

  /**
   * Converts to upper case
   *
   * @param   i  character
   * @return  upper case  character
   */
  public static int toUpper(int i) {
    return toUpper[i & 0xff] & 0xff;
  }

  /**
   * Converts to lower case
   *
   * @param   i character
   * @return  lower case character
   */
  public static int toLower(int i) {
    return toLower[i & 0xff] & 0xff;
  }

  /**
   * Checks is given character alfa
   *
   * @param   i
   * @return  true if the character is alfa, false if not
   */
  public static boolean isAlpha(int i) {
    return isAlpha[i & 0xff];
  }

  /**
   * Checks is given character upper case alfa
   *
   * @param   i
   * @return  true if the character is upper case, false if not
   */
  public static boolean isUpper(int i) {
    return isUpper[i & 0xff];
  }

  /**
   * Checks is given character lower case alfa
   *
   * @param   i
   * @return  true if the character is lower case, false if not
   */
  public static boolean isLower(int i) {
    return isLower[i & 0xff];
  }

  /**
   * Checks is given character is white space
   *
   * @param   i
   * @return  true if the character is white space, false if not
   */
  public static boolean isWhite(int i) {
    return isWhite[i & 0xff];
  }

  /**
   * Checks is given character is digit
   *
   * @param   i
   * @return  true if the character is digit, false if not
   */
  public static boolean isDigit(int i) {
    return isDigit[i & 0xff];
  }

  public static byte[] getBytes(String s) {
    char[] ch = s.toCharArray();
    byte toBytes[] = new byte[ch.length];
    for (int i = 0; i < ch.length; i++) {
      toBytes[i] = (byte) ch[i];
    }
    return toBytes;
  }

  /**
   * Parses this to int
   *
   * @param   abyte0
   * @param   i
   * @param   j
   * @return
   * @exception   java.lang.NumberFormatException
   */
  public static int asciiArrToInt(byte abyte0[], int i, int j) throws NumberFormatException {
    byte byte0 = -1;
    if (abyte0 == null || j <= 0 || !isDigit(byte0 = abyte0[i++])) {
      throw new HttpNumberFormatException(HttpNumberFormatException.PARAMETERS_DO_NOT_SPECIFY_INTEGER);
    }
    int result;
    for (result = byte0 - 48; --j > 0; result = (result * 10 + byte0) - 48) {
      if (!isDigit(byte0 = abyte0[i++])) {
        throw new HttpNumberFormatException(HttpNumberFormatException.SYMBOL_IS_NOT_INTEGER, new Object[]{byte0 + ""});
      }
    }
    return result;
  }

  public static int asciiArrToIntNoException(byte abyte0[], int i, int j) {
    byte byte0;
    if (abyte0 == null || j <= 0 || !isDigit(byte0 = abyte0[i++])) {
      return -1;
    }
    int k;
    for (k = byte0 - 48; --j > 0; k = (k * 10 + byte0) - 48) {
      if (!isDigit(byte0 = abyte0[i++])) {
        return -1;
      }
    }
    return k;
  }

  public static long asciiArrToLongNoException(byte abyte0[], int i, int j) {
    byte byte0;
    if (abyte0 == null || j <= 0 || !isDigit(byte0 = abyte0[i++])) {
      return -1;
    }
    long k;
    for (k = byte0 - 48; --j > 0; k = (k * 10 + byte0) - 48) {
      if (!isDigit(byte0 = abyte0[i++])) {
        return -1;
      }
    }
    return k;
  }

  public static byte[] intToAsciiArr(int integer) {
    if (integer == 0) {
			return zero;
		}
    int length = 1;
    int temp = integer / 10;

    while (temp != 0) {
      temp /= 10;
      length++;
    }

    byte[] newNum = new byte[length];

    for (int i = length - 1; i >= 0; i--) {
      newNum[i] = (byte) (integer % 10 + 48);
      integer /= 10;
    }

    return newNum;
  }

  public static byte[] hexIntToAsciiArr(int i) {
    byte[] buf = new byte[10];
    int charPos = 10;
    int mask = (1 << 4) - 1;

    do {
      buf[--charPos] = digitBytes[i & mask];
      i >>>= 4;
    } while (i != 0);

    byte[] cutBuf = new byte[10 - charPos];
    System.arraycopy(buf, charPos, cutBuf, 0, cutBuf.length);
    return cutBuf;
  }

  public static int asciiArrHexToInt(byte[] buf, int off, int len) {
    int res = 0;
    int coef = 1;
    for (int i = len - 1; i >= 0; i--) {
      int toInt = ((int)buf[off + i]) & 0xff;
      if (toInt > 96 && toInt < 103) {
        res += ((toInt - 87) * coef);
      } else if (toInt > 64 && toInt < 71) {
        res += ((toInt - 55) * coef);
      } else if (toInt > 47 && toInt < 58) {
        res += ((toInt - 48) * coef);
      } else {
        throw new HttpIllegalArgumentException(HttpIllegalArgumentException.INCORRECT_HEXADECIMAL_DIGIT,
            new Object[]{new String(buf, off, len)});
      }
      coef *= 16;
    }
    return res;
  }

  public static byte makeHexDigit(byte[] input, int inputOff) {
    return (byte) ((convertHexDigit(input[inputOff + 1]) << 4) + convertHexDigit(input[inputOff + 2]));
  }

  private static byte convertHexDigit(byte b) {
    if ((b >= '0') && (b <= '9')) {
      return (byte) (b - '0');
    }
    if ((b >= 'a') && (b <= 'f')) {
      return (byte) (b - 'a' + 10);
    }
    if ((b >= 'A') && (b <= 'F')) {
      return (byte) (b - 'A' + 10);
    }
    return 0;
  }
}

