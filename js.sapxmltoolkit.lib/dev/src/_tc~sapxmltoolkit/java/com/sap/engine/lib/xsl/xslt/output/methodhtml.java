package com.sap.engine.lib.xsl.xslt.output;

import java.util.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Chavdar Baykov
 * @version      1.0
 */
final class MethodHTML extends Method {

  /*
   * Warning !!! Be careful when changing constants - the hashset's initial
   * size is set for performance !
   */
  //mem
  public static HashSet HTML_TAGS = new HashSet(128);
  public static HashSet HTML_EMPTYTAGS = new HashSet(16);
  public static HashSet HTML_SCRIPTTAGS = new HashSet(4);
  public static HashSet HTML_BOOLEANATTR = new HashSet(16);
  public static HashSet HTML_URIATTR = new HashSet(16);
  private boolean isInCDATASection;
  protected int isInScriptSection = 0;
  private boolean isHTMLTag;
  private String encoding;
  private boolean isHeadTag;
  private HashSet cDataSectionElements;
  private CharArray lastUri = null;
  private boolean bMustBeEmpty = false;
  protected static boolean staticInit = false;
  private boolean hasDTDEntity;

  protected MethodHTML() {
    synchronized (MethodHTML.class) {
      if (!staticInit) {
        staticInit = true;
        HTML_EMPTYTAGS.add(new CharArray("AREA"));
        HTML_EMPTYTAGS.add(new CharArray("BASE"));
        HTML_EMPTYTAGS.add(new CharArray("BASEFONT"));
        HTML_EMPTYTAGS.add(new CharArray("BR"));
        HTML_EMPTYTAGS.add(new CharArray("COL"));
        HTML_EMPTYTAGS.add(new CharArray("FRAME"));
        HTML_EMPTYTAGS.add(new CharArray("HR"));
        HTML_EMPTYTAGS.add(new CharArray("IMG"));
        HTML_EMPTYTAGS.add(new CharArray("INPUT"));
        HTML_EMPTYTAGS.add(new CharArray("ISINDEX"));
        HTML_EMPTYTAGS.add(new CharArray("LINK"));
        HTML_EMPTYTAGS.add(new CharArray("META"));
        HTML_EMPTYTAGS.add(new CharArray("PARAM"));
        HTML_SCRIPTTAGS.add(new CharArray("SCRIPT"));
        HTML_SCRIPTTAGS.add(new CharArray("STYLE"));
        HTML_BOOLEANATTR.add(new CharArray("CHECKED"));
        HTML_BOOLEANATTR.add(new CharArray("COMPACT"));
        HTML_BOOLEANATTR.add(new CharArray("DECLARE"));
        HTML_BOOLEANATTR.add(new CharArray("DEFER"));
        HTML_BOOLEANATTR.add(new CharArray("DISABLED"));
        HTML_BOOLEANATTR.add(new CharArray("ISMAP"));
        HTML_BOOLEANATTR.add(new CharArray("MULTIPLE"));
        HTML_BOOLEANATTR.add(new CharArray("NOHREF"));
        HTML_BOOLEANATTR.add(new CharArray("NORESIZE"));
        HTML_BOOLEANATTR.add(new CharArray("NOSHADE"));
        HTML_BOOLEANATTR.add(new CharArray("NOWRAP"));
        HTML_BOOLEANATTR.add(new CharArray("READONLY"));
        HTML_BOOLEANATTR.add(new CharArray("SELECTED"));
        HTML_URIATTR.add(new CharArray("ACTION"));
        HTML_URIATTR.add(new CharArray("BACKGROUND"));
        HTML_URIATTR.add(new CharArray("CITE"));
        HTML_URIATTR.add(new CharArray("CLASSID"));
        HTML_URIATTR.add(new CharArray("CODEBASE"));
        HTML_URIATTR.add(new CharArray("DATA"));
        HTML_URIATTR.add(new CharArray("HREF"));
        HTML_URIATTR.add(new CharArray("LONGDESC"));
        HTML_URIATTR.add(new CharArray("PROFILE"));
        HTML_URIATTR.add(new CharArray("SRC"));
        HTML_URIATTR.add(new CharArray("USEMAP"));
        HTML_TAGS.add(new CharArray("!DOCTYPE"));
        HTML_TAGS.add(new CharArray("A"));
        HTML_TAGS.add(new CharArray("ABBR"));
        HTML_TAGS.add(new CharArray("ACRONYM"));
        HTML_TAGS.add(new CharArray("ADDRESS"));
        HTML_TAGS.add(new CharArray("APPLET"));
        HTML_TAGS.add(new CharArray("AREA"));
        HTML_TAGS.add(new CharArray("B"));
        HTML_TAGS.add(new CharArray("BASE"));
        HTML_TAGS.add(new CharArray("BASEFONT"));
        HTML_TAGS.add(new CharArray("BGSOUND"));
        HTML_TAGS.add(new CharArray("BDO"));
        HTML_TAGS.add(new CharArray("BIG"));
        HTML_TAGS.add(new CharArray("BLINK"));
        HTML_TAGS.add(new CharArray("BLOCKQUOTE"));
        HTML_TAGS.add(new CharArray("BODY"));
        HTML_TAGS.add(new CharArray("BR"));
        HTML_TAGS.add(new CharArray("BUTTON"));
        HTML_TAGS.add(new CharArray("CAPTION"));
        HTML_TAGS.add(new CharArray("CENTER"));
        HTML_TAGS.add(new CharArray("CITE"));
        HTML_TAGS.add(new CharArray("CODE"));
        HTML_TAGS.add(new CharArray("COL"));
        HTML_TAGS.add(new CharArray("COLGROUP"));
        HTML_TAGS.add(new CharArray("COMMENT"));
        HTML_TAGS.add(new CharArray("DD"));
        HTML_TAGS.add(new CharArray("DEL"));
        HTML_TAGS.add(new CharArray("DFN"));
        HTML_TAGS.add(new CharArray("DIR"));
        HTML_TAGS.add(new CharArray("DIV"));
        HTML_TAGS.add(new CharArray("DL"));
        HTML_TAGS.add(new CharArray("DT"));
        HTML_TAGS.add(new CharArray("EM"));
        HTML_TAGS.add(new CharArray("EMBED"));
        HTML_TAGS.add(new CharArray("FIELDSET"));
        HTML_TAGS.add(new CharArray("FONT"));
        HTML_TAGS.add(new CharArray("FORM"));
        HTML_TAGS.add(new CharArray("FRAME"));
        HTML_TAGS.add(new CharArray("FRAMESET"));
        HTML_TAGS.add(new CharArray("H"));
        HTML_TAGS.add(new CharArray("H1"));
        HTML_TAGS.add(new CharArray("H2"));
        HTML_TAGS.add(new CharArray("H3"));
        HTML_TAGS.add(new CharArray("H4"));
        HTML_TAGS.add(new CharArray("H5"));
        HTML_TAGS.add(new CharArray("H6"));
        HTML_TAGS.add(new CharArray("HEAD"));
        HTML_TAGS.add(new CharArray("HR"));
        HTML_TAGS.add(new CharArray("HTML"));
        HTML_TAGS.add(new CharArray("I"));
        HTML_TAGS.add(new CharArray("IFRAME"));
        HTML_TAGS.add(new CharArray("IMG"));
        HTML_TAGS.add(new CharArray("INPUT"));
        HTML_TAGS.add(new CharArray("INS"));
        HTML_TAGS.add(new CharArray("ISINDEX"));
        HTML_TAGS.add(new CharArray("KBD"));
        HTML_TAGS.add(new CharArray("LABEL"));
        HTML_TAGS.add(new CharArray("LEGEND"));
        HTML_TAGS.add(new CharArray("LI"));
        HTML_TAGS.add(new CharArray("LINK"));
        HTML_TAGS.add(new CharArray("LISTING"));
        HTML_TAGS.add(new CharArray("MAP"));
        HTML_TAGS.add(new CharArray("MARQUEE"));
        HTML_TAGS.add(new CharArray("MENU"));
        HTML_TAGS.add(new CharArray("META"));
        HTML_TAGS.add(new CharArray("MULTICOL"));
        HTML_TAGS.add(new CharArray("NEXTID"));
        HTML_TAGS.add(new CharArray("NOBR"));
        HTML_TAGS.add(new CharArray("NOFRAMES"));
        HTML_TAGS.add(new CharArray("NOSCRIPT"));
        HTML_TAGS.add(new CharArray("OBJECT"));
        HTML_TAGS.add(new CharArray("OL"));
        HTML_TAGS.add(new CharArray("OPTGROUP"));
        HTML_TAGS.add(new CharArray("OPTION"));
        HTML_TAGS.add(new CharArray("P"));
        HTML_TAGS.add(new CharArray("PARAM"));
        HTML_TAGS.add(new CharArray("PLAINTEXT"));
        HTML_TAGS.add(new CharArray("PRE"));
        HTML_TAGS.add(new CharArray("Q"));
        HTML_TAGS.add(new CharArray("S"));
        HTML_TAGS.add(new CharArray("SAMP"));
        HTML_TAGS.add(new CharArray("SCRIPT"));
        HTML_TAGS.add(new CharArray("SELECT"));
        HTML_TAGS.add(new CharArray("SERVER"));
        HTML_TAGS.add(new CharArray("SMALL"));
        HTML_TAGS.add(new CharArray("SOUND"));
        HTML_TAGS.add(new CharArray("SPACER"));
        HTML_TAGS.add(new CharArray("SPAN"));
        HTML_TAGS.add(new CharArray("STRIKE"));
        HTML_TAGS.add(new CharArray("STRONG"));
        HTML_TAGS.add(new CharArray("STYLE"));
        HTML_TAGS.add(new CharArray("SUB"));
        HTML_TAGS.add(new CharArray("SUP"));
        HTML_TAGS.add(new CharArray("TABLE"));
        HTML_TAGS.add(new CharArray("TBODY"));
        HTML_TAGS.add(new CharArray("TD"));
        HTML_TAGS.add(new CharArray("TEXTAREA"));
        HTML_TAGS.add(new CharArray("TEXTFLOW"));
        HTML_TAGS.add(new CharArray("TFOOT"));
        HTML_TAGS.add(new CharArray("TH"));
        HTML_TAGS.add(new CharArray("THEAD"));
        HTML_TAGS.add(new CharArray("TITLE"));
        HTML_TAGS.add(new CharArray("TR"));
        HTML_TAGS.add(new CharArray("TT"));
        HTML_TAGS.add(new CharArray("U"));
        HTML_TAGS.add(new CharArray("UL"));
        HTML_TAGS.add(new CharArray("VAR"));
        HTML_TAGS.add(new CharArray("WBR"));
        HTML_TAGS.add(new CharArray("XMP"));
      }
    }
  }

  void startDocument(String omit, String version, String encoding, String isStandalone) throws OutputException {
    this.encoding = encoding.toLowerCase(Locale.ENGLISH);
    isInCDATASection = false;
    cDataSectionElements = owner.getCDataSectionElements();
  }

  void endDocument() throws OutputException {

  }

  private CharArray caSe0 = new CharArray();

  void startElement0(CharArray uri, CharArray localName, CharArray qName) throws OutputException {
    //    String localName = localNameCA.getString();
    //    String qName = qNameCA.getString();
    lastUri = uri;

    if ((Tools.notEmpty(uri) == false) && (HTML_TAGS.contains(qName.toUpperCase(caSe0)))) {
      //If this is HTML tag
      isHTMLTag = true;
      encoder.out('<');
      encoder.out(qName);

      if (cDataSectionElements.contains(qName.toUpperCase(caSe0))) {
        isInCDATASection = true;
      }

      //      LogWriter.getSystemLogWriter().println("Checking if scipt: " + qName);
      if (HTML_SCRIPTTAGS.contains(qName.toUpperCase(caSe0))) {
        isInScriptSection++;
      }

      //      LogWriter.getSystemLogWriter().println("Checking if scipt: xxx:" + isInScriptSection);
      if (qName.equalsIgnoreCase("HEAD")) {
        isHeadTag = true;
      } else {
        isHeadTag = false;
      }
    } else {
      // If not a HTML tag or URI!=NULL
      isHTMLTag = false;
      encoder.out('<');
      encoder.out(qName);

      if (cDataSectionElements.contains(qName.toUpperCase(caSe0))) {
        isInCDATASection = true;
      }
    }

    if ((Tools.notEmpty(lastUri) == false) && (HTML_EMPTYTAGS.contains(qName.toUpperCase(caSe0)))) {
      bMustBeEmpty = true;
    } else {
      bMustBeEmpty = false;
    }
  }

  void attribute(CharArray uri, CharArray localName, CharArray qName, CharArray value) throws OutputException {
    boolean showEscaped = true; // Permanent flag indicates escaping
    boolean hasDoubleQuotes = value.indexOf('\"') != -1;

    if (isHTMLTag == false) {
      // This is not HTML tag - doing default processing
      encoder.out(' ');
      encoder.out(qName);
      encoder.out("=");
      encoder.out(hasDoubleQuotes ? '\'' : '\"');
      encoder.outEscaped(value);
      encoder.out(hasDoubleQuotes ? '\'' : '\"');
    } else {
      encoder.out(' ');
      encoder.out(qName);
      boolean htmlbooleanattr = HTML_BOOLEANATTR.contains(qName.toUpperCase(caSe0));
      boolean uriattr = HTML_URIATTR.contains(qName.toUpperCase(caSe0));

      if (!htmlbooleanattr) {
        //        LogWriter.getSystemLogWriter().println(uriattr);
        // If this is not boolean attribute
        encoder.out("=");
        char delimiter = hasDoubleQuotes ? '\'' : '\"'; 
        encoder.out(delimiter);
        encoder.outHTMLAttribute(value, uriattr, delimiter);
        encoder.out(delimiter);
      }
    }
  }

  void startElement1(boolean isEmpty) throws OutputException {
    //    if (isEmpty) {
    //      if (bMustBeEmpty || isHeadTag) { // If this is Empty HTML tag
    //        encoder.out('>');
    //      } else {
    //        encoder.out("/>");
    //      }
    //    } else {
    encoder.out('>');

    //    }
    if (isHTMLTag && isHeadTag) {
      if (Tools.notEmpty(encoding) == false) {
        encoding = "utf-8";
      }
      encoder.out("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
      encoder.out(encoding);
      encoder.out("\">");
      //encoder.out("</head>");
      //isHeadTag = false;
    }
  }

  void endElement(CharArray uri, CharArray localName, CharArray qName, boolean isEmpty) throws OutputException {
    //    LogWriter.getSystemLogWriter().println("MethodHTML.endElement: " + qName);
    if (isHeadTag) {
      encoder.out("</").out(qName).out(">");
    } else if (isEmpty) {
      if (!bMustBeEmpty) {
        encoder.out("</").out(qName).out(">");
      }
    } else {
      if ((Tools.notEmpty(uri) == false) && (HTML_EMPTYTAGS.contains(qName.toUpperCase(caSe0)))) {
        // If this is Empty HTML tag
        return;
      }

      encoder.out("</");
      encoder.out(qName);
      encoder.out('>');
    }

    if (cDataSectionElements.contains(qName.toUpperCase(caSe0))) {
      isInCDATASection = false;
    }

    // Checks if this is SCRIPT or STYLE tag
    if (Tools.notEmpty(uri) == false && HTML_SCRIPTTAGS.contains(qName.toUpperCase(caSe0))) {
      //      LogWriter.getSystemLogWriter().println("Checking if scipt: scrupt out:" + isInScriptSection);
      isInScriptSection--;
    }
  }

  void startCDATA() throws OutputException {
    isInCDATASection = true;
  }

  void processingInstruction(CharArray target, CharArray data) throws OutputException {
    encoder.out("<?");
    encoder.out(target);
    encoder.out(' ');
    encoder.out(data);
    encoder.out(">");
  }

  void endCDATA() throws OutputException {
    isInCDATASection = false;
  }

  void startDTD(String name, String publicId, String systemId) throws OutputException {
    encoder.out("<!DOCTYPE ").out(name);

    if (Tools.notEmpty(publicId)) {
      encoder.out(" PUBLIC '").out(publicId).out('\'');
      if (Tools.notEmpty(systemId)) {
        encoder.out(" '").out(systemId).out('\'');
      }
    } else if (Tools.notEmpty(systemId)) {
      encoder.out(" SYSTEM '").out(systemId).out('\'');
    }

    hasDTDEntity = false;
//    encoder.out(" [\n");
  }

  void endDTD() throws OutputException {
    if (hasDTDEntity) {
      encoder.out("]>").outln();
    } else {
      encoder.out(">").outln();
    }
  }

  void characters(char[] ch, int start, int length) throws OutputException {
    //        LogWriter.getSystemLogWriter().println("Method HTML:" + new String(ch, start, length));
    if (isInCDATASection) {
      encoder.outCDATA(ch, start, length);
    } else {
      if (isInScriptSection != 0) {
        encoder.out(ch, start, length);
      } else {
        encoder.outEscaped(ch, start, length);
        //encoder.out(ch, start, length); //xxx
      }
    }
  }

  void comment(char[] ch, int start, int length) throws OutputException {
    //    LogWriter.getSystemLogWriter().println("----" + isInScriptSection + "  Outputtting section : "+ new String(ch, start, length));
    encoder.out("<!--").out(ch, start, length).out("-->").flush();
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
        //$JL-EXC$
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

