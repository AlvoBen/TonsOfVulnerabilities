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

/**
 * @author Vladimir Videlov
 * @version 7.10
 */
public class EntryTypes {
  // XML Infoset types
  public static final byte Element = '<';
  public static final byte EndElement = '>';
  public static final byte Attribute = '@';
  public static final byte Comment = '!';
  public static final byte String = '+';
  public static final byte PI = '?';

  // Value-Types
  public static final byte EText = 'T';
  public static final byte AText = 'A';
  public static final byte Binary = 'B';
  public static final byte Ref = 'R';

  // Reserved types
  public static final byte CharArray = 'C';
  public static final byte Header = '?';
  public static final byte DeclNamespace = ':';
  public static final byte OpenNamespace = '*';
  public static final byte Undefined = -1;
}
