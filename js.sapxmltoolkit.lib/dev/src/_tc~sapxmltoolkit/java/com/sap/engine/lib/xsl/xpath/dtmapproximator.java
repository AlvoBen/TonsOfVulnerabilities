package com.sap.engine.lib.xsl.xpath;

import java.io.*;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.parser.helpers.*;
import com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler;

/**
 * The purpose of this class is to provide an estimate for the size of the
 * <tt>DTM</tt> table, which should be built for a particular xml file.
 * Thus resizing of the table is avoided, which saves space and time.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class DTMApproximator extends EmptyDocHandler {

  private XMLParser p;
  private int total;

  public DTMApproximator() throws XPathException {
    try {
      p = new XMLParser();
    } catch (Exception e) {
      //      LogWriter.getSystemLogWriter().println("DUMP BEGIN");
      //      _.printStackTrace();
      //      LogWriter.getSystemLogWriter().println("DUMP END");
      throw new XPathException("Could not initialize DTMApproximator", e);
    }
  }

  public DTMApproximator(XMLParser p0) {
    if (p0 == null) {
      throw new IllegalArgumentException();
    }

    p = p0;
  }

  public int approximate(InputStream in) throws XPathException {
    return approximate(in, ":main:");
  }
  
  public int approximate(InputStream in, String systemID) throws XPathException {
    total = 0;
    try {
      p.parse(in, systemID, this);
    } catch (Exception e) {
      //      LogWriter.getSystemLogWriter().println("DUMP BEGIN");
      //      _.printStackTrace();
      //      LogWriter.getSystemLogWriter().println("DUMP END");
      throw new XPathException("Could not load source data.", e);
    }
    return total;
  }

  // DocHandler methods
  public void onXMLDecl(String version, String encoding, String ssdecl) throws Exception {

  }

  public void startElementStart(String uri, CharArray localName, CharArray qname) throws Exception {
    total++;
  }

  public void addAttribute(String uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    total++;
  }

  public void startElementEnd(boolean isEmpty) throws Exception {

  }

  public void endElement(String uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {

  }

  public void startDocument() throws Exception {
    total = 1;
  }

  public void endDocument() throws Exception {

  }

  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception {
    total++;
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    total++;
  }

  public void onComment(CharArray text) throws Exception {
    total++;
  }

  public void startPrefixMapping(String prefix, String uri) throws Exception {
    total++;
  }

}

