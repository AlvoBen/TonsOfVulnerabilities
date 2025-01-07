package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXToXMLWriter extends DefaultHandler {
  
  private XMLTokenWriter xmlTokenWriter;
  
  public void init(XMLTokenWriter xmlTokenWriter) {
    this.xmlTokenWriter = xmlTokenWriter;
  }

  public void endDocument() throws SAXException {
    try {
      xmlTokenWriter.flush();
    } catch(IOException ioExc) {
      throw new SAXException(ioExc);
    }
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    try {
      xmlTokenWriter.setPrefixForNamespace(prefix, uri);
    } catch(IOException ioExc) {
      throw new SAXException(ioExc);
    }
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    try {
      xmlTokenWriter.enter(uri, localName);
      for(int i = 0; i < atts.getLength(); i++) {
        xmlTokenWriter.writeAttribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
      }
    } catch(IOException ioExc) {
      throw new SAXException(ioExc);
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    try {
      xmlTokenWriter.leave();
    } catch(IOException ioExc) {
      throw new SAXException(ioExc);
    }
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    try {
      xmlTokenWriter.writeContent(new String(ch, start, length));
    } catch(IOException ioExc) {
      throw new SAXException(ioExc);
    }
  }
}
