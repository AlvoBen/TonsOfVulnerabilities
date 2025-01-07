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

import org.w3c.dom.Element;

import com.sap.engine.lib.xsl.xpath.ETObject;

public final class XSLKey {

  private ETObject etMatch = null;
  private ETObject etUse = null;
  private String name = null;
  private String match = null;
  private String use = null;

  public XSLKey(XSLStylesheet owner, Element el) throws XSLException {
    el.normalize();

    match = el.getAttribute("match");

    // explicitly added - the most common case
    if (match.indexOf("/") < 0) {
      match = "//".concat(match);
    }

    name = el.getAttribute("name");
    use = el.getAttribute("use");
    etMatch = owner.etBuilder.process(match);
    etUse = owner.etBuilder.process(use);
  }

  public ETObject getETMatch() {
    return etMatch;
  }

  public ETObject getETUse() {
    return etUse;
  }

  public String getName() {
    return name;
  }

  public String getMatch() {
    return match;
  }

  public String getUse() {
    return use;
  }

}

