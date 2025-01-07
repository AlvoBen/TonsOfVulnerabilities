/*
 * Copyright (c) 2006 by SAP Labs Bulgaria.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.parser.binary.common;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Vladimir Videlov
 * @version 7.10
 */
public class Constants {
  public static final int NO_NAMESPACE_ID = 1;
  public static final int START_ID_VALUE = 2;

  // ascii codes for "BXML"
  public static final byte[] BXML_DECL = new byte[] { 0x42, 0x58, 0x4D, 0x4C };
  public static final CharArray BXML_VERSION = new CharArray("0.7");
  public static final CharArray BXML_ENCODING = new CharArray("UTF-8");

  // headers
  public static final CharArray BXML_HEADER_VERSION = new CharArray("VER");
  public static final CharArray BXML_HEADER_ENCODING = new CharArray("ENC");
}
