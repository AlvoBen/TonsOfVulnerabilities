package com.sap.engine.lib.xml.util;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.*;
import com.sap.engine.lib.xml.dom.DOMImplementationImpl;
import javax.xml.transform.dom.DOMResult;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class SAXToDOMHandler extends DefaultHandler implements ContentHandler, DTDHandler, ErrorHandler, DeclHandler, LexicalHandler {

  private Document document = null;
  private Node root = null;
  private Node current = null;
  private DOMResult domResult = null;
  private boolean isInCDATASection = false;

  public SAXToDOMHandler() {

  }

  public SAXToDOMHandler(Node node) {
    setRoot(node);
  }

  public SAXToDOMHandler(DOMResult domResult) {
    this.domResult = domResult;
    setRoot(domResult.getNode());
  }

  public void setRoot(Node node) {
    root = node;
  }

  public Node getRoot() {
    return root;
  }

  /* org.xml.sax.ContentHandler */
  public void startDocument() throws SAXException {
    if (root == null) {
      document = new DOMImplementationImpl().createDocument(null, null, null);
      root = document;
    } else {
      if (root instanceof Document) {
        document = (Document) root;
      } else {
        document = root.getOwnerDocument();
      }
    }

    if (document == null) {
      throw new SAXException("Unable to convert SAX to DOM, root Node has no owner Document.");
    }

    current = root;
    isInCDATASection = false;
  }

  public void endDocument() throws SAXException {
    if (domResult != null) {
      domResult.setNode(root);
    }
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    Element x = document.createElementNS(uri, qName);

    for (int i = 0; i < atts.getLength(); i++) {
      String uri2 = atts.getURI(i);
      if (atts.getQName(i).startsWith("xmlns")) {
        uri2 = NS.XMLNS;
      }      
      x.setAttributeNS(uri2, atts.getQName(i), atts.getValue(i));
    } 

    current.appendChild(x);
    current = x;
  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    current = current.getParentNode();
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    Node x = (isInCDATASection) ? document.createCDATASection(new String(ch, start, length)) : document.createTextNode(new String(ch, start, length));
    current.appendChild(x);
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    characters(ch, start, length);
  }

  public void processingInstruction(String target, String data) throws SAXException {
    ProcessingInstruction x = document.createProcessingInstruction(target, data);
    current.appendChild(x);
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    // Do nothing
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    // Do nothing
  }

  public void setDocumentLocator(Locator locator) {
    // Do nothing
  }

  public void skippedEntity(String name) throws SAXException {
    //xxx
  }

  /* org.xml.sax.DTDHandler */
  public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    //xxx
  }

  public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
    //xxx
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
  public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
    //xxx
  }

  public void elementDecl(String name, String model) throws SAXException {
    //xxx
  }

  public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
    //xxx
  }

  public void internalEntityDecl(String name, String value) throws SAXException {
    //xxx
  }

  /* org.xml.sax.ext.LexicalHandler */
  public void comment(char[] ch, int start, int length) throws SAXException {
    Comment x = document.createComment(new String(ch, start, length));
    current.appendChild(x);
  }

  public void startDTD(String name, String publicId, String systemId) throws SAXException {
    //xxx
  }

  public void endDTD() throws SAXException {
    //xxx
  }

  public void startCDATA() throws SAXException {
    isInCDATASection = true;
  }

  public void endCDATA() throws SAXException {
    isInCDATASection = false;
  }

  public void startEntity(String name) throws SAXException {
    //xxx
  }

  public void endEntity(String name) throws SAXException {
    //xxx
  }

}

