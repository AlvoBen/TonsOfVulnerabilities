package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.*;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
final class IndenterImpl0 extends Indenter {

  private static final int INDENT_STEP = 2;
  private static final int INDENT_INITIAL = 0;
  private int indent;
  private int last = LAST_TEXT;
  private static final int LAST_TEXT = 0;
  private static final int LAST_MARKUP = 1;
  private boolean isInCDATA;

  void startDocument(String omit, String version, String encoding, String isStandalone) throws OutputException {
    indent = INDENT_INITIAL;
    indent = 0;
    last = LAST_MARKUP;
    owner.getMethod().startDocument(omit, version, encoding, isStandalone);
    encoder.outln();
    isInCDATA = false;
  }

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {
    markupStarts();
    owner.getMethod().startElement0(uri, localName, qName);
  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
    owner.getMethod().attribute(uri, localName, qName, value);
  }

  void startElement1(boolean isEmpty) throws OutputException {
    owner.getMethod().startElement1(isEmpty);
    indent += INDENT_STEP;
  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {
    indent -= INDENT_STEP;
    markupStarts();
    owner.getMethod().endElement(uri, localName, qName, isEmpty);
  }

  void characters(char[] ch, int start, int length) throws OutputException {
    if (isInCDATA || ((owner.getMethod() instanceof MethodHTML) && ((MethodHTML) owner.getMethod()).isInScriptSection > 0)) {
      owner.getMethod().characters(ch, start, length);
      return;
    }

    while ((length > 0) && (Symbols.isWhitespace(ch[start + length - 1]))) {
      length--;
    }

    while ((length > 0) && Symbols.isWhitespace(ch[start])) {
      start++;
      length--;
    }

    if (length <= 0) {
      return;
    }

    textStarts();
    owner.getMethod().characters(ch, start, length);
  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {
    specialMarkupStarts();
    owner.getMethod().processingInstruction(target, data);
  }

  void comment(char[] ch, int start, int length) throws OutputException {
    markupStarts();
    owner.getMethod().comment(ch, start, length);
  }

  void endDocument() throws OutputException {
    owner.getMethod().endDocument();
    encoder.outln();
  }

  void startDTD(String name, String publicId, String systemId) throws OutputException {
    markupStarts();
    owner.getMethod().startDTD(name, publicId, systemId);
    indent += INDENT_STEP;
  }

  void endDTD() throws OutputException {
    owner.getMethod().endDTD();
    indent -= INDENT_STEP;
    markupStarts();
  }

  void startCDATA() throws OutputException {
    owner.getMethod().startCDATA();
    isInCDATA = true;
  }

  void endCDATA() throws OutputException {
    owner.getMethod().endCDATA();
    isInCDATA = false;
  }

  private void textStarts() throws OutputException {
    if (last == LAST_MARKUP) {
      encoder.outln();
      encoder.outSpace(indent);
      last = LAST_TEXT;
      return;
    }
  }

  private void markupStarts() throws OutputException {
    encoder.outln();
    encoder.outSpace(indent);
    last = LAST_MARKUP;
  }

  private void specialMarkupStarts() throws OutputException {
    int indentSave = indent;
    indent = 0;
    markupStarts();
    indent = indentSave;
  }

}

