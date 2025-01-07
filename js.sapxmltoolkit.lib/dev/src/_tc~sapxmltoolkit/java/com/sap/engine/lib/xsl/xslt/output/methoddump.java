package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
final class MethodDump extends Method {

  MethodDump() {

  }

  void endCDATA() throws OutputException {
    encoder.out("DUMP METHOD endCDATA()").outln();
  }

  void startDocument(String omit, String version, String encoding, String isStandalone) throws OutputException {
    encoder.out("DUMP METHOD startDocument(").out(nonNull(omit)).out(", ").out(nonNull(version)).out(", ").out(nonNull(encoding)).out(", ").out(nonNull(isStandalone)).out(")").outln();
  }

  void startElement1(boolean isEmpty) throws OutputException {
    encoder.out("DUMP METHOD startElement1()").outln();
  }

  void startCDATA() throws OutputException {
    encoder.out("DUMP METHOD startCDATA()").outln();
  }

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {
    encoder.out("DUMP METHOD startElement0(").out(nonNull(uri)).out(", ").out(nonNull(localName)).out(", ").out(nonNull(qName)).out(")").outln();
  }

  void comment(char[] parm1, int parm2, int parm3) throws OutputException {
    encoder.out("DUMP METHOD comment(...) -> \"").out(new String(parm1, parm2, parm2 + parm3)).out("\"").outln();
  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {
    encoder.out("DUMP METHOD processingInstruction(").out(nonNull(target)).out(", ").out(nonNull(data)).out(")").outln();
  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {
    encoder.out("DUMP METHOD endElement(").out(nonNull(uri)).out(", ").out(nonNull(localName)).out(", ").out(nonNull(qName)).out(")").outln();
  }

  void characters(char[] parm1, int parm2, int parm3) throws OutputException {
    encoder.out("DUMP METHOD characters(...) -> \"").out(new String(parm1, parm2, parm2 + parm3)).out("\"").outln();
  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
    encoder.out("DUMP METHOD attribute(").out(nonNull(uri)).out(", ").out(nonNull(localName)).out(", ").out(nonNull(qName)).out(", ").out(nonNull(value)).out(")").outln();
  }

  void endDocument() throws OutputException {
    encoder.out("DUMP METHOD endDocument()").outln();
  }

  void startDTD(String name, String publicId, String systemId) throws OutputException {
    encoder.out("DUMP METHOD startDTD(").out(name).out(", ").out(publicId).out(", ").out(systemId).out(")").outln();
  }

  void endDTD() throws OutputException {
    encoder.out("DUMP METHOD endDTD()").outln();
  }

  private String nonNull(String s) {
    return (s == null) ? "" : s;
  }

  private CharArray nonNull(CharArray s) {
    return (s == null) ? CharArray.EMPTY : s;
  }

  void onDTDEntity(Entity ent) throws OutputException {

  }

}

