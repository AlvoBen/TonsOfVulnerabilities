package com.sap.engine.lib.xml.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;

import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class SAXToDocHandler extends DefaultHandler implements ContentHandler, DTDHandler, ErrorHandler, DeclHandler, LexicalHandler {

  private DocHandler docHandler;
  private CharArray[] ca = new CharArray[10];
  private NamespaceSupport namespaceSupport = new NamespaceSupport();
  private boolean isInCDATASection;

  public SAXToDocHandler() {
    initCA();
  }

  public SAXToDocHandler(DocHandler docHandler) {
    initCA();
    setDocHandler(docHandler);
  }

  public void setDocHandler(DocHandler docHandler) {
    this.docHandler = docHandler;
  }

  private void initCA() {
    for (int i = 0; i < ca.length; i++) {
      ca[i] = new CharArray();
    } 
  }

  /* org.xml.sax.ContentHandler */
  public void startDocument() throws SAXException {
    try {
      docHandler.startDocument();
      namespaceSupport.reset();
      isInCDATASection = false;
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void endDocument() throws SAXException {
    try {
      docHandler.endDocument();
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    try {
      ca[0].set(qName);
      ca[1].set(localName);
      ca[2].set(uri);
      docHandler.startElementStart(ca[2], 
      /*localName*/
      ca[1], 
      /*qName*/
      ca[0]);

      for (int i = 0; i < atts.getLength(); i++) {
        String attQName = atts.getQName(i);
        int a = attQName.indexOf(':');
        String pref = (a == -1) ? "" : attQName.substring(0, a);
        ca[2].set(pref);
        ca[6].set(atts.getURI(i));
        docHandler.addAttribute(ca[6], ca[2], ca[3].set(atts.getLocalName(i)), ca[4].set(atts.getQName(i)), atts.getType(i), ca[5].set(atts.getValue(i)));
      } 

      docHandler.startElementEnd(false);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    try {
      ca[0].set(qName);
      ca[1].set(localName);
      ca[2].set(uri);
      docHandler.endElement(ca[2], ca[1], ca[0], false);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    try {
      ca[0].set(ch);
      ca[1].set(ca[0], start, start + length);

      if (isInCDATASection) {
        docHandler.onCDSect(ca[1]);
      } else {
        docHandler.charData(ca[1], true);
      }
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    try {
      characters(ch, start, length);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void processingInstruction(String target, String data) throws SAXException {
    try {
      ca[0].set(target);
      ca[1].set(data);
      docHandler.onPI(ca[0], ca[1]);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    try {
      namespaceSupport.pushContext();
      namespaceSupport.declarePrefix(prefix, uri);
      docHandler.startPrefixMapping(new CharArray(prefix), new CharArray(uri));
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    try {
      namespaceSupport.popContext();
      docHandler.endPrefixMapping(new CharArray(prefix));
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void setDocumentLocator(Locator locator) {

  }

  public void skippedEntity(String name) throws SAXException {

  }

  /* org.xml.sax.DTDHandler */
  public void notationDecl(String name, String publicId, String systemId) throws SAXException {

  }

  public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {

  }

  /* org.xml.sax.ErrorHandler */
  public void warning(SAXParseException exception) throws SAXException {

  }

  public void error(SAXParseException exception) throws SAXException {

  }

  public void fatalError(SAXParseException exception) throws SAXException {
    throw exception;
  }

  /* org.xml.sax.ext.DeclHandler */
  public void elementDecl(String name, String model) throws SAXException {

  }

  public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {

  }

  public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {

  }

  public void internalEntityDecl(String name, String value) throws SAXException {

  }

  /* org.xml.sax.ext.LexicalHandler */
  public void comment(char[] ch, int start, int length) throws SAXException {
    try {
      ca[0].set(ch);
      ca[1].set(ca[0], start, start + length);
      docHandler.onComment(ca[1]);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void startDTD(String name, String publicId, String systemId) throws SAXException {
    try {
      ca[0].set(name);
      ca[1].set(publicId);
      ca[2].set(systemId);
      docHandler.startDTD(ca[0], ca[1], ca[2]);
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void endDTD() throws SAXException {
    try {
      docHandler.endDTD();
    } catch (Exception e) {
      throw new NestedSAXParseException(e);
    }
  }

  public void startCDATA() throws SAXException {
    isInCDATASection = true;
  }

  public void endCDATA() throws SAXException {
    isInCDATASection = false;
  }

  public void startEntity(String name) throws SAXException {

  }

  public void endEntity(String name) throws SAXException {

  }

}

