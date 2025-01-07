package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.parser.helpers.*;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
final class IndenterEmpty extends Indenter {

  IndenterEmpty() {

  }

  void startDocument(String omit, String version, String encoding, String isStandalone) throws OutputException {
    owner.getMethod().startDocument(omit, version, encoding, isStandalone);
  }

  void endDocument() throws OutputException {
    owner.getMethod().endDocument();
  }

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {
    owner.getMethod().startElement0(uri, localName, qName);
  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
    owner.getMethod().attribute(uri, localName, qName, value);
  }

  void startElement1(boolean isEmpty) throws OutputException {
    owner.getMethod().startElement1(isEmpty);
  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {
    owner.getMethod().endElement(uri, localName, qName, isEmpty);
  }

  void characters(char[] ch, int start, int length) throws OutputException {
    owner.getMethod().characters(ch, start, length);
  }

  void startCDATA() throws OutputException {
    owner.getMethod().startCDATA();
  }

  void endCDATA() throws OutputException {
    owner.getMethod().endCDATA();
  }

  void comment(char[] ch, int start, int length) throws OutputException {
    owner.getMethod().comment(ch, start, length);
  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {
    owner.getMethod().processingInstruction(target, data);
  }

  void startDTD(String name, String publicId, String systemId) throws OutputException {
    owner.getMethod().startDTD(name, publicId, systemId);
  }

  void endDTD() throws OutputException {
    owner.getMethod().endDTD();
  }

}

