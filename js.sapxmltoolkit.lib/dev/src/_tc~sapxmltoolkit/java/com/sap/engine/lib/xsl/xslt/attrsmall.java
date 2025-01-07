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
package com.sap.engine.lib.xsl.xslt;

/**
 *
 <!ELEMENT xsl:copy-of EMPTY>
 <!ATTLIST xsl:copy-of select %expr; #REQUIRED>
 *
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 * @version 0.0.1
 *
 *
 * First Edition: 17.01.2001
 *
 */
import com.sap.engine.lib.xml.parser.helpers.CharArray;

public final class AttrSmall {

  public CharArray value = null;
  public String name = null;

  public CharArray getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

  public AttrSmall(String name, CharArray value) {
    this.name = name;
    this.value = value.copy();
  }

}

