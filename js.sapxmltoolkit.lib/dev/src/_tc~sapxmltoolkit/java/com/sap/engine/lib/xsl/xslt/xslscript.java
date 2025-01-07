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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XSLScript {

  private String data = null;
  private String implements_prefix = null;
  private String language = null;
  private String src = null;
  private String archive = null;

  public XSLScript(Element el) throws XSLException {
    el.normalize();
    NodeList nl = el.getChildNodes();

    if (nl.getLength() > 0 && nl.item(0).getNodeType() == Node.TEXT_NODE) {
      data = ((Text) nl.item(0)).getData();
    }

    implements_prefix = el.getAttribute("implements-prefix");
    language = el.getAttribute("language");
    src = el.getAttribute("src");
    archive = el.getAttribute("archive");
  }

  public String getPrefix() {
    return implements_prefix;
  }

  public String getLanguage() {
    return language;
  }

  public String getSrc() {
    return src;
  }

  public String getArchive() {
    return archive;
  }

  public String getData() {
    return data;
  }

}

