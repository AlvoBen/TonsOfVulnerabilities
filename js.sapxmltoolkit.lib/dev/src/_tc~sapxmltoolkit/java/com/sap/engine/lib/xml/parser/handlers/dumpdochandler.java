package com.sap.engine.lib.xml.parser.handlers;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import java.io.*;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Nov 27, 2001, 2:36:09 PM
 */
public final class DumpDocHandler extends EmptyDocHandler {

  private LogWriter logWriter;

  public DumpDocHandler() {
    this(LogWriter.getSystemLogWriter()); //$JL-SYS_OUT_ERR$
  }

  public DumpDocHandler(PrintStream out) {
    this(LogWriter.newLogWriter(out));
  }

  public DumpDocHandler(OutputStream out) {
    this(LogWriter.newLogWriter(out));
  }
  
  private DumpDocHandler(LogWriter logWriter) {
    this.logWriter = logWriter;
  }

  public void onXMLDecl(String v, String e, String s) {
    logWriter.println("DUMP DocHandler.onXMLDecl(version='" + v + "', encoding='" + e + "', standalone='" + s + "')");
  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) {
    logWriter.println("DUMP DocHandler.startElementStart(uri='" + uri + "', local='" + localName + "', qname='" + qname + "')");
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
    logWriter.println("DUMP DocHandler.addAttribute(uri='" + uri + "', prefix='" + prefix + "', local='" + localName + "', qname='" + qname + "', value='" + value + "')");
  }

  public void startElementEnd(boolean isEmpty) throws Exception {
    logWriter.println("DUMP DocHandler.startElementEnd(isEmpty=" + isEmpty + ")");
  }

  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    logWriter.println("DUMP DocHandler.endElement(isEmpty=" + isEmpty + ")");
  }

  public void startDocument() {
    logWriter.println("DUMP DocHandler.startDocument()");
  }

  public void endDocument() {
    logWriter.println("DUMP DocHandler.endDocument()");
  }

  public void charData(CharArray data, boolean bDisableOutputEscaping) {
    logWriter.println("DUMP DocHandler.charData(data='" + data + "', bDisableOutputEscaping=" + bDisableOutputEscaping + ")");
  }

  public void onPI(CharArray target, CharArray data) {
    logWriter.println("DUMP DocHandler.onPI(target='" + target + "', data='" + data + "')");
  }

  public void onComment(CharArray text) {
    logWriter.println("DUMP DocHandler.onComment(text='" + text + "')");
  }

  public void onCDSect(CharArray text) {
    logWriter.println("DUMP DocHandler.onCDSect(text='" + text + "')");
  }

  public void onDTDElement(CharArray name, CharArray model) {
    logWriter.println("DUMP DocHandler.onDTDElement(name='" + name + "', model='" + model + "')");
  }

  public void onDTDAttListStart(CharArray name) {
    logWriter.println("DUMP DocHandler.onDTDAttListStart(name='" + name + "')");
  }

  public void onDTDAttListItem(CharArray name, CharArray attName, String type, String defDecl, CharArray crValue, String note) {
    logWriter.println("DUMP DocHandler.onDTDAttListItem(name='" + name + "', attName='" + attName + "', type='" + type + "', defDecl='" + defDecl + "', crValue='" + crValue + "', note='" + note + "')");
  }

  public void onDTDAttListEnd() {
    logWriter.println("DUMP DocHandler.onDTDAttListEnd()");
  }

  public void onDTDEntity(Entity ent) throws Exception {
    logWriter.println("DUMP DocHandler.onDTDEntity(...)");
  }

  public void onDTDUnparsedEntity(String name, String pub, String sys, String note, boolean pe) {
    logWriter.println("DUMP DocHandler.onDTDUnparsedEntity(...)");
  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) {
    logWriter.println("DUMP DocHandler.onDTDNotation(...)");
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) {
    logWriter.println("DUMP DocHandler.startDTD(...)");
  }

  public void endDTD() {
    logWriter.println("DUMP DocHandler.endDTD(...)");
  }

  public void onContentReference(Reference ref) {
    logWriter.println("DUMP DocHandler.onContentReference(...)");
  }

  public void startPrefixMapping(CharArray prefix, CharArray uri) {
    logWriter.println("DUMP DocHandler.startPrefixMapping(prefix='" + prefix + "', uri='" + uri + "')");
  }

  public void endPrefixMapping(CharArray prefix) {
    logWriter.println("DUMP DocHandler.endPrefixMapping(prefix='" + prefix + "')");
  }

  public void onWarning(String warning) throws Exception {
    LogWriter.getSystemLogWriter().println("DUMP DocHandler.onWarning(warning='" + warning + "')"); //$JL-SYS_OUT_ERR$
  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {

  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.parser.DocHandler#onEndContentEntity(com.sap.engine.lib.xml.parser.helpers.CharArray)
   */
  public void onEndContentEntity(CharArray name) throws Exception {
    logWriter.println("DUMP DocHandler.onEndContentEntity(" + name + ")");
  }

  /* (non-Javadoc)
   * @see com.sap.engine.lib.xml.parser.DocHandler#onStartContentEntity(com.sap.engine.lib.xml.parser.helpers.CharArray, boolean)
   */
  public void onStartContentEntity(CharArray name, boolean isExpanding)
    throws Exception {
    logWriter.println("DUMP DocHandler.onStartContentEntity(" + name + ", isExpanding= " + isExpanding + ")");
  }

}

