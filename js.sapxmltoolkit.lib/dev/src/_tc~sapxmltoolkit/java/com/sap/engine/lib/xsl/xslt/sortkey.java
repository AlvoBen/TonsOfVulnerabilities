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

public final class SortKey {

  protected CharArray keyStr = null;
  protected double keyNum;
  protected int order;
  protected int caseOrder;
  protected int dataType;
  protected String lang;

  public CharArray getTextKey() {
    return keyStr;
  }

  public double getNumKey() {
    return keyNum;
  }

  public int getOrder() {
    return order;
  }

  public int getDataType() {
    return dataType;
  }

  public int getCaseOrder() {
    return caseOrder;
  }

  public SortKey() {

  }

  public SortKey reuse(CharArray sortKey, int order, int caseOrder, String lang, int dataType) {
    init(sortKey, order, caseOrder, lang, dataType);
    return this;
  }

  public SortKey reuse(double keyNum, int order, int caseOrder, String lang, int dataType) {
    init(keyNum, order, caseOrder, lang, dataType);
    return this;
  }

  public SortKey(CharArray sortKey, int order, int caseOrder, String lang, int dataType) {
    init(sortKey, order, caseOrder, lang, dataType);
  }

  public SortKey(double keyNum, int order, int caseOrder, String lang, int dataType) {
    init(keyNum, order, caseOrder, lang, dataType);
  }

  public void init(CharArray sortKey, int order, int caseOrder, String lang, int dataType) {
    if (this.keyStr == null) {
      this.keyStr = sortKey.copy();
    } else {
      this.keyStr.set(sortKey);
    }

    //LogWriter.getSystemLogWriter().println(sortKey + " -------" + this.keyStr);
    this.order = order;
    this.caseOrder = caseOrder;
    this.lang = lang;
    this.dataType = dataType;
  }

  public void init(double keyNum, int order, int caseOrder, String lang, int dataType) {
    this.keyNum = keyNum;
    this.order = order;
    this.caseOrder = caseOrder;
    this.lang = lang;
    this.dataType = dataType;
  }

}

