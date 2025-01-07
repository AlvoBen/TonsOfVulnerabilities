package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.parser.helpers.*;

/**
 * Title:
 * Description:  An abstract class whose derived classes' methods are invoked
 *               by the owner DocHandlerSerializer object immediately before and after
 *               invoking the respective methods of the Method object.
 *               Derived classes: IndenterEmpty, IndenterImpl0.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
abstract class Indenter {

  protected DocHandlerSerializer owner = null;
  protected Encoder encoder = null;

  Indenter() {

  }

  abstract void startDocument(String omit, String version, String encoding, String isStandalone) throws OutputException;

  abstract void endDocument() throws OutputException;

  abstract void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException;

  abstract void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException;

  abstract void startElement1(boolean isEmpty) throws OutputException;

  abstract void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException;

  abstract void characters(char[] ch, int start, int length) throws OutputException;

  abstract void startCDATA() throws OutputException;

  abstract void endCDATA() throws OutputException;

  abstract void comment(char[] ch, int start, int length) throws OutputException;

  abstract void processingInstruction(CharArray target, CharArray data) throws OutputException;

  abstract void startDTD(String name, String publicId, String systemId) throws OutputException;

  abstract void endDTD() throws OutputException;

  void startContentReference(CharArray name) throws OutputException {

  }

  void endContentReference(CharArray name) throws OutputException {

  }

  void setOwner(DocHandlerSerializer owner) {
    this.owner = owner;
    encoder = owner.getEncoder();
  }

  DocHandlerSerializer getOwner() {
    return owner;
  }

}

