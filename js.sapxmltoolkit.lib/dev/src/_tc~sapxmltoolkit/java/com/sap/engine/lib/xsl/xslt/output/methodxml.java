package com.sap.engine.lib.xsl.xslt.output;

import java.util.*;
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
final class MethodXML extends Method {

  private boolean isInCDATASection;
  private HashSet cDataSectionElements;
  private boolean cDataSectionElementsEmpty;
  private boolean hasDTDEntity;

  MethodXML() {

  }

  void startDocument(String omit, String version, String encoding, String standalone) throws OutputException {
    if (encoding.equalsIgnoreCase("utf-16")) {
      encoder.outDirect((char)0xFF);
      encoder.outDirect((char)0xFE);
    } else if (encoding.equalsIgnoreCase("utf-16be")) {
      encoder.outDirect((char)0xFE);
      encoder.outDirect((char)0xFF);
    }  
 
    if (!Tools.isYes(omit)) {
      if (Tools.notEmpty(version) && (!version.equals("1.0"))) {
        version = "1.0";
      }
      encoder.out("<?xml version=\"1.0\"");

      if (Tools.notEmpty(encoding)) {
        encoder.out(" encoding=\"");
        encoder.out(owner.getEncoder().getEncodingName());
        encoder.out('\"');
      }

      encoder.out("?>\n");
    }

    isInCDATASection = false;
    cDataSectionElements = owner.getCDataSectionElements();
    cDataSectionElementsEmpty = cDataSectionElements.isEmpty();
  }

  void endDocument() throws OutputException {

  }

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {
    encoder.out('<');
    encoder.out(qName);

    if ((!cDataSectionElementsEmpty) && cDataSectionElements.contains(qName.getString())) {
      isInCDATASection = true;
    }
  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
    encoder.out(' ');
    encoder.out(qName);
    encoder.out("=\"");
    encoder.outAttribute(value, '\"');
    encoder.out('\"');
  }

  void startElement1(boolean isEmpty) throws OutputException {
    if (isEmpty) {
      encoder.out("/>");
      isInCDATASection = false;
    } else {
      encoder.out('>');
    }
  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {
    if (!isEmpty) {
      encoder.out("</").out(qName).out('>');

      if ((!cDataSectionElementsEmpty) && cDataSectionElements.contains(qName.getString())) {
        isInCDATASection = false;
      }
    }
  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {
    if (data.length() == 0) {
      encoder.out("<?").out(target).out("?>");
    } else {
      encoder.out("<?");
      encoder.out(target);
      encoder.out(' ');
      encoder.out(data);
      encoder.out("?>");
    }
  }

  void startCDATA() throws OutputException {
    isInCDATASection = true;
  }

  void endCDATA() throws OutputException {
    isInCDATASection = false;
  }

  void startDTD(String name, String publicId, String systemId) throws OutputException {
    encoder.out("<!DOCTYPE ").out(name);

    if (Tools.notEmpty(publicId)) {
      encoder.out(" PUBLIC '").out(publicId).out("' '").out(systemId).out('\'');
    } else if (Tools.notEmpty(systemId)) {
      encoder.out(" SYSTEM '").out(systemId).out('\'');
    }

    hasDTDEntity = false;
    //encoder.out(" [\n");
  }

  void endDTD() throws OutputException {
    if (hasDTDEntity) {
    encoder.out("]>").outln();
    } else {
      encoder.out(">").outln();
    }
  }

  void characters(char[] ch, int start, int length) throws OutputException {
    if (isInCDATASection) {
      encoder.outCDATA(ch, start, length);
    } else {
      //if (!Tools.isWhitespace(ch, start, length)) {
      encoder.outEscaped(ch, start, length);
      //}
    }
  }

  void comment(char[] ch, int start, int length) throws OutputException {
    encoder.out("<!--").out(ch, start, length).out("-->");
  }

  void onDTDEntity(Entity ent) throws OutputException {
    if (!hasDTDEntity) {
      encoder.out(" [\n");
      hasDTDEntity = true;
    }
    encoder.out("<!ENTITY ").out(ent.getName());

    if (ent.isInternal()) {
      try {
        encoder.out(" \"").out(ent.getValue()).out("\">\n");
      } catch (Exception e) {
        throw new OutputException(e);

      }
    } else {
      if (ent.getPub().length() > 0) {
        encoder.out(" PUBLIC '").out(ent.getPub()).out("' '").out(ent.getSys()).out('\'');
      } else if (ent.getSys().length() > 0) {
        encoder.out(" SYSTEM '").out(ent.getSys()).out('\'');
      }

      encoder.out(">\n");
    }
  }

}

