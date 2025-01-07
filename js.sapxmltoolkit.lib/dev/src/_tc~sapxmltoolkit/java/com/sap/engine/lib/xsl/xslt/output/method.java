package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;

/**
 * An abstract class whose implementations handle SAX-like events,
 * sending them to the Encoder in an appropriate way.
 * Derived classes: MethodXML, MethodHTML, MethodText.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
abstract class Method {

  protected DocHandlerSerializer owner = null;
  protected Encoder encoder = null;

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

  abstract void onDTDEntity(Entity ent) throws OutputException;

  /*
   void startElement0(String uri, CharArray localName, CharArray qName) throws OutputException {
   startElement0(uri, localName.getString(), qName.getString());
   }
   void attribute(String uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
   attribute(uri, localName.getString(), qName.getString(), value.getString());
   }
   void attribute(Attribute a) throws OutputException {
   attribute(a.crUri.getString(), a.crLocalName, a.crRawName, a.crValue);
   }
   void endElement(String uri, CharArray localName, CharArray qName) throws OutputException {
   endElement(uri, localName.getString(), qName.getString());
   }
   void processingInstruction(CharArray target, CharArray data) throws OutputException {
   processingInstruction(target.getString(), data.getString());
   }
   void startDTD(CharArray name, CharArray publicId, CharArray systemId) throws OutputException {
   startDTD(name.getString(), publicId.getString(), systemId.getString());
   }
   */
  void setOwner(DocHandlerSerializer owner) {
    this.owner = owner;
    encoder = owner.getEncoder();
  }

  DocHandlerSerializer getOwner() {
    return owner;
  }

}

