package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
final class MethodText extends Method {

  MethodText() {

  }

  void startDocument(String omit, String version, String encoding, String standalone) throws OutputException {

  }

  void endDocument() throws OutputException {

  }

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {

  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {

  }

  void startElement1(boolean isEmpty) throws OutputException {

  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {

  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {

  }

  void startCDATA() throws OutputException {

  }

  void endCDATA() throws OutputException {

  }

  void characters(char[] ch, int start, int length) throws OutputException {
    encoder.out(ch, start, length);
  }

  void comment(char[] ch, int start, int length) throws OutputException {

  }

  void internalEntityDecl(String name, String value) throws OutputException {

  }

  void startDTD(String name, String Id, String systemId) throws OutputException {

  }

  void endDTD() throws OutputException {

  }

  void onDTDEntity(Entity ent) throws OutputException {

  }

}

