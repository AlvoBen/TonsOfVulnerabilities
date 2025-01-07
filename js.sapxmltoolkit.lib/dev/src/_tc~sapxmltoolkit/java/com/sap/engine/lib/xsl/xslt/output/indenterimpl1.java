package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * XSLT spec:
 *
 * If the indent attribute has the value yes, then the xml output method may
 * output whitespace in addition to the whitespace in the result tree (possibly
 * based on whitespace stripped from either the source document or the stylesheet)
 * in order to indent the result nicely; if the indent attribute has the value
 * no, it should not output any additional whitespace. The default value is no.
 * The xml output method should use an algorithm to output additional whitespace
 * that ensures that the result if whitespace were to be stripped from the output
 * using the process described in [3.4 Whitespace Stripping] with the set of
 * whitespace-preserving elements consisting of just xsl:text would be the same
 * when additional whitespace is output as when additional whitespace is not output.
 *
 * NOTE: It is usually not safe to use indent="yes" with document types that
 * include element types with mixed content.
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
final class IndenterImpl1 extends Indenter {

  private static final boolean SPLIT_LONG_LINES = false;
  private static final int INDENT_STEP = 2;
  private static final int INDENT_INITIAL = 0;
  private int indent;
  private static final int MAX_LENGTH_OF_LINE = Integer.MAX_VALUE;
  private int last = LAST_TEXT;
  private static final int LAST_TEXT = 0;
  private static final int LAST_MARKUP = 1;
  private CharArray buffer = new CharArray();
//  private boolean bufferShouldBeEscaped;
  private boolean isInCDATA;
  private boolean isInContentReference;
  private boolean allowUnindentedClosingTag = false;
  private boolean lastTagWasOpening = false;
  private boolean hadFirstElement = false;

  void startDocument(String omit, String version, String encoding, String isStandalone) throws OutputException {
    indent = INDENT_INITIAL;
    indent = 0;
    last = LAST_MARKUP;
    owner.getMethod().startDocument(omit, version, encoding, isStandalone);
    //encoder.outln();
    isInCDATA = false;
    isInContentReference = false;
    hadFirstElement = false;
    buffer.clear();
  }

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {
    emptyBuffer();

    if (hadFirstElement) {
      markupStarts();
    } else {
      markupStartsFirstEl();
      hadFirstElement = true;
    }

    owner.getMethod().startElement0(uri, localName, qName);
  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
    owner.getMethod().attribute(uri, localName, qName, value);
  }

  void startElement1(boolean isEmpty) throws OutputException {
    owner.getMethod().startElement1(isEmpty);
    indent += INDENT_STEP;
    lastTagWasOpening = true;
  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {
    allowUnindentedClosingTag = true;
    emptyBuffer(); // unindent may be denied here
    indent -= INDENT_STEP;
    allowUnindentedClosingTag = allowUnindentedClosingTag && lastTagWasOpening;

    if (!allowUnindentedClosingTag && !isEmpty) {
      markupStarts();
    }

    allowUnindentedClosingTag = false;
    owner.getMethod().endElement(uri, localName, qName, isEmpty);
    lastTagWasOpening = false;
  }

  void characters(char[] ch, int start, int length) throws OutputException {
    if (isInContentReference) {
      return;
    }

//    String br = new String(ch, start, length);
//    if (br.equals("<BR>")) {
//      LogWriter.getSystemLogWriter().println("IndenterImpl1.characters()> BR");
//    }

    if (encoder.isEscapingEnabled()) {
     Method method = owner.getMethod();
     if (method instanceof MethodXML) {
       buffer.appendEscapedNo13(ch, start, length);
     } else if ((method instanceof MethodHTML) && ((MethodHTML) method).isInScriptSection == 0) {
       buffer.appendBasicEscaped(ch, start, length);
     } else {
       buffer.append(ch, start, length);       
     }
    } else {
      buffer.append(ch, start, length);
    }
  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {
    emptyBuffer();
    specialMarkupStarts();
    owner.getMethod().processingInstruction(target, data);
  }

  void comment(char[] ch, int start, int length) throws OutputException {
    emptyBuffer();
    markupStarts();
    owner.getMethod().comment(ch, start, length);
  }

  void endDocument() throws OutputException {
    emptyBuffer();
    owner.getMethod().endDocument();
    encoder.outln();
  }

  void startDTD(String name, String publicId, String systemId) throws OutputException {
    emptyBuffer();
    markupStarts();
    owner.getMethod().startDTD(name, publicId, systemId);
    indent += INDENT_STEP;
  }

  void endDTD() throws OutputException {
    emptyBuffer();
    markupStarts();
    owner.getMethod().endDTD();
    indent -= INDENT_STEP;
  }

  void startCDATA() throws OutputException {
    emptyBuffer();
    owner.getMethod().startCDATA();
    isInCDATA = true;
  }

  void endCDATA() throws OutputException {
    emptyBuffer();
    owner.getMethod().endCDATA();
    isInCDATA = false;
  }

  protected void markupStarts() throws OutputException {
    emptyBuffer();
    encoder.outln();
    encoder.outSpace(indent);
    last = LAST_MARKUP;
    allowUnindentedClosingTag = false;
  }

  protected void markupStartsFirstEl() throws OutputException {
    emptyBuffer();
    last = LAST_MARKUP;
    allowUnindentedClosingTag = false;
  }

  private void specialMarkupStarts() throws OutputException {
    emptyBuffer();
    int indentSave = indent;
    indent = 0;
    markupStarts();
    indent = indentSave;
  }

  private void emptyBuffer() throws OutputException {
    //    if (SPLIT_LONG_LINES) {
    //      buffer.normalizeSpace();
    //    } else {
    buffer.trimNo13();
    //buffer.trim();
    //    }
    //LogWriter.getSystemLogWriter().println("IndenterImpl1.emptybuffer. Buffer is: '" + buffer +"'");
    int lBuffer = buffer.length();

    if (lBuffer == 0) {
      allowUnindentedClosingTag = false;
      return;
    }

    boolean escapingSaver = encoder.isEscapingEnabled();
    encoder.setEscaping(false);
    int l = MAX_LENGTH_OF_LINE - indent;

    if ((lBuffer < l) && allowUnindentedClosingTag) {
      //owner.getMethod().characters("[4]".toCharArray(), 0, 3);
      owner.getMethod().characters(buffer.getData(), buffer.getOffset(), buffer.length());
      //owner.getMethod().characters("[5]".toCharArray(), 0, 3);
      allowUnindentedClosingTag = true;
    } else {
      if (SPLIT_LONG_LINES) {
        int i = 0;
        int j = l;
        char[] bufferData = buffer.getData();
        int bufferOffset = buffer.getOffset();

        while (true) {
          encoder.outln();
          encoder.outSpace(indent);

          while ((j < lBuffer) && (bufferData[bufferOffset + j - 1] != ' ')) {
            j++;
          }

          if (j >= lBuffer) {
            //owner.getMethod().characters("[0]".toCharArray(), 0, 3);
            owner.getMethod().characters(bufferData, bufferOffset + i, lBuffer - i);
            //owner.getMethod().characters("[1]".toCharArray(), 0, 3);
            break;
          }

          //owner.getMethod().characters("[2]".toCharArray(), 0, 3);
          owner.getMethod().characters(buffer.getData(), bufferOffset + i, j - i);
          //owner.getMethod().characters("[3]".toCharArray(), 0, 3);
          i = j;
          j += l;
        }

        allowUnindentedClosingTag = false;
      } else {
        encoder.outln();
        encoder.outSpace(indent);
        //owner.getMethod().characters("[6]".toCharArray(), 0, 3);
        owner.getMethod().characters(buffer.getData(), buffer.getOffset(), buffer.length());
        //owner.getMethod().characters("[7]".toCharArray(), 0, 3);
        allowUnindentedClosingTag = false;
      }
    }

    buffer.clear();
    encoder.setEscaping(escapingSaver); 
  }

  void startContentReference(CharArray name) throws OutputException {
    emptyBuffer();
    encoder.out("&");
    encoder.out(name);
    encoder.out(";");
    isInContentReference = true;
  }

  void endContentReference(CharArray name) throws OutputException {
    isInContentReference = false;
  }

//  private static CharArray trimWhitespaceLeft(CharArray ca) {
//    char[] data = ca.getData();
//    int offset = ca.getOffset();
//    int length = ca.length();
//    int end = offset + length;
//    int newOffset = offset;
//
//    while (newOffset < end) {
//      char ch = data[newOffset];
//
//      if ((ch == ' ') || (ch == '\r') || (ch == '\n') || (ch == '\t')) {
//        newOffset++;
//        continue;
//      }
//
//      break;
//    }
//
//    if (offset != newOffset) {
//      ca.set(data, newOffset, length - newOffset + offset);
//    }
//
//    return ca;
//  }

  private static CharArray trimWhitespaceRight(CharArray ca) {
    char[] data = ca.getData();
    int offset = ca.getOffset();
    int length = ca.length();
    int end = offset + length;
    int newEndM1 = end - 1;

    while (newEndM1 >= offset) {
      char ch = data[newEndM1];

      if ((ch == ' ') || (ch == '\r') || (ch == '\n') || (ch == '\t')) {
        newEndM1--;
        continue;
      }

      break;
    }

    if (end != newEndM1 + 1) {
      ca.set(data, offset, newEndM1 + 1 - offset);
    }

    return ca;
  }

  public static void main(String[] args) throws Exception {
    LogWriter.getSystemLogWriter().println("[" + trimWhitespaceRight(new CharArray(" \tsadf\n\r\t")) + "]");
  }

}

