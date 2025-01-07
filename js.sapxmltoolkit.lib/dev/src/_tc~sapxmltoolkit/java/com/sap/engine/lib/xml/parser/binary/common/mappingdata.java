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
public class MappingData {
  public CharArray prefix;
  public CharArray uri;

  public MappingData() {
    prefix = CharArray.EMPTY;
    uri = CharArray.EMPTY;
  }

  public MappingData(CharArray prefix, CharArray uri) {
    this.prefix = prefix;
    this.uri = uri;
  }

  public boolean equals(Object obj) {
    boolean result = false;

    if (obj instanceof MappingData) {
      if (prefix.equals(((MappingData)obj).prefix) && uri.equals(((MappingData)obj).uri)) {
        result = true;
      }
    }

    return result;
  }

  public int hashCode() {
    return prefix.hashCode() + uri.hashCode();
  }
}
