package com.sap.engine.lib.xml.parser.handlers;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import java.util.*;

/**
 * Class description, -
 *
 * @author Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version 1.00
 */
public class EmptyDocHandler implements DocHandler {

  public static final boolean DEBUG = true;
  boolean wroteDTDKv = false;

  public void D(String s) {
    if (DEBUG) {
      LogWriter.getSystemLogWriter().println(s); //$JL-SYS_OUT_ERR$
    }
  }

  public void P(String s) {
    LogWriter.getSystemLogWriter().print(s); //$JL-SYS_OUT_ERR$
  }

  public String mq(String str) {
    //if (str.length() == 0) return "";
    if (str.indexOf("\"") != -1) {
      return "\'" + str + "\'";
    } else {
      return "\"" + str + "\"";
    }
  }

  public void onXMLDecl(String v, String e, String s) throws Exception {

  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {

  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {

  }

  public void startElementEnd(boolean isEmpty) throws Exception {

  }

  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {

  }

  //  public void callEmptyElementTag(CharArray QName, CharArray uri, CharArray prefix, String localName, Vector attList) {
  //  }
  //  public void startElement(CharArray QName, CharArray uri, CharArray prefix, String localName, Vector attList) throws Exception {
  //  }
  //  public void endElement(CharArray QName, CharArray uri, CharArray prefix, String localName) throws Exception {
  //  }
  //  
  //  public void callEmptyElementTag(CharArray QName, Vector attList) throws Exception {
  //  }
  //  
  //  public void startElement(CharArray QName, Vector attList) throws Exception {
  //  }
  //  
  //  public void endElement(CharArray sName) throws Exception {
  //  }
  public void startDocument() throws Exception {

  }

  public void endDocument() throws Exception {

  }

  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception {

  }

  public void onPI(CharArray target, CharArray data) throws Exception {

  }

  public void onComment(CharArray text) throws Exception {

  }

  public void onCDSect(CharArray text) throws Exception {

  }

  public void onDTDElement(CharArray name, CharArray model) throws Exception {

  }

  public void onDTDAttListStart(CharArray name) throws Exception {

  }

  public void onDTDAttListItem(CharArray name, CharArray attName, String type, String defDecl, CharArray vAttValue, String note) throws Exception {

  }

  public void onDTDAttListEnd() throws Exception {

  }

  public void onDTDEntity(Entity ent) throws Exception {

  }

  public void onDTDEntity(String name, String pub, String sys, Vector vEntityValue, boolean pe, String note) throws Exception {

  }

  public void onDTDUnparsedEntity(String name, String pub, String sys, String note, boolean pe) throws Exception {

  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception {

  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {

  }

  public void endDTD() throws Exception {

  }

  public void onContentReference(Reference ref) throws Exception {

  }

  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception {

  }

  public void endPrefixMapping(CharArray prefix) throws Exception {

  }

  public void onWarning(String warning) throws Exception {

  }

  public void onStartContentEntity(CharArray name, boolean isExpanding) throws Exception {

  }

  public void onEndContentEntity(CharArray name) throws Exception {

  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {

  }

}

