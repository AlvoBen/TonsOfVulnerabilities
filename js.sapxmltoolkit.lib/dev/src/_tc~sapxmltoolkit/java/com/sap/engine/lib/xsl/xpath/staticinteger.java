/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public final class StaticInteger {

  /**
   * The smallest value of type <tt>int</tt>. The constant
   * value of this field is <tt>-2147483648</tt>.
   */
  public static final int MIN_VALUE = 0x80000000;
  /**
   * The largest value of type <tt>int</tt>. The constant
   * value of this field is <tt>2147483647</tt>.
   */
  public static final int MAX_VALUE = 0x7fffffff;
  /**
   * All possible chars for representing a number as a String
   */
  static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  /**
   * Array of chars to lookup the char for the digit in the tenth's
   * place for a two digit, base ten number.  The char can be got by
   * using the number as the index.
   */
  private static final char[] radixTenTenths = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
  /**
   * Array of chars to lookup the char for the digit in the unit's
   * place for a two digit, base ten number.  The char can be got by
   * using the number as the index.
   */
  private static final char[] radixTenUnits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
  public char[] intbuf = new char[12];
  
  public static StaticInteger st = null;

  public static synchronized CharArray intToCharArraySync(int i, CharArray res) {
    if (st==null) {
      st = new StaticInteger();
    }
    return st.intToCharArray(i, res);
  }
  
  public CharArray intToCharArray(int i, CharArray res) {
    res.clear();
    res.assertDataLen(12);
    char[] buf = intbuf;
    boolean negative = (i < 0);
    int charPos = 12;

    if (i == Integer.MIN_VALUE) {
      res.set("-2147483648");
      return res;
    }

    if (negative) {
      i = -i;
    }

    do {
      int digit = i % 100;
      buf[--charPos] = radixTenUnits[digit];
      buf[--charPos] = radixTenTenths[digit];
      i = i / 100;
    } while (i != 0);

    if (buf[charPos] == '0') {
      charPos++;
    }

    if (negative) {
      buf[--charPos] = '-';
    }

    System.arraycopy(buf, charPos, res.getData(), 0, 12 - charPos);
    res.setSize(12 - charPos);
    return res;
    //return new String(buf , charPos , (12 - charPos));
  }

}

