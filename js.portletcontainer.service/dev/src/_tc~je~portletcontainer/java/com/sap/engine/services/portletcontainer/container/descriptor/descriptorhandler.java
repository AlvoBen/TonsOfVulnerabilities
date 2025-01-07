/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.container.descriptor;

import java.io.CharArrayWriter;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class DescriptorHandler extends DefaultHandler {
  
  private String displayName = null;
  private CharArrayWriter text = new CharArrayWriter ();
  private boolean isFound = false;
  
  public void startDocument() throws SAXException {
  }

  public void endDocument() throws SAXException {
  }
  
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (qName.equals("display-name")) {
      isFound = true;
      text.reset();
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("display-name")) {
      displayName = getText();
      isFound = false;
    }
  }
  
  private String getText() {
    return text.toString().trim();
  }
  
  public void characters(char[] ch, int start, int length) {
    if (isFound) {
      text.write(ch,start,length);
    }
  }
  
  public String getDisplayName() {
    return displayName;
  }
}
