package com.sap.engine.lib.xml.parser.tokenizer;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public class XMLToSAXReader {
  
  private XMLTokenReader xmlTokenReader;
  private ContentHandler contentHandler;
  private Hashtable charArrayToStringMap;
  
  public XMLToSAXReader() {
    charArrayToStringMap = new Hashtable();
  }
  
  public void setXMLTokenReader(XMLTokenReader xmlTokenReader) {
    this.xmlTokenReader = xmlTokenReader;
  }
  
  public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }
  
  public void read() throws Exception {
    start();
    int state = -1;
    while((state = xmlTokenReader.getState()) != XMLTokenReader.EOF) {
      if(state == XMLTokenReader.CHARS) {
        readChars();
      } else {
        if(state == XMLTokenReader.ENDELEMENT) {
          readEndElement();
        } else if(state == XMLTokenReader.STARTELEMENT) {
          readStartElement();
        }
        xmlTokenReader.next();
      }
    }
    end();
  }
  
  private void end() throws Exception {
    xmlTokenReader.end();
    contentHandler.endDocument();
    charArrayToStringMap.clear();
  }
  
  private void start() throws Exception {
    xmlTokenReader.begin();
    contentHandler.startDocument();
  }
  
  private void readChars() throws Exception {
    CharArray chars = xmlTokenReader.getValuePassCharsCA();
    contentHandler.characters(chars.getData(), chars.getOffset(), chars.getSize());
  }
  
  private void readStartElement() throws SAXException {
    CharArray uri = xmlTokenReader.getURICharArray();
    CharArray localName = xmlTokenReader.getLocalNameCharArray();
    CharArray qName = xmlTokenReader.getQNameCharArray();
    Attributes attribs = xmlTokenReader.getAttributes();
    contentHandler.startElement(determineString(uri), determineString(localName), determineString(qName), attribs);
  }
  
  private void readEndElement() throws SAXException {
    CharArray uri = xmlTokenReader.getURICharArray();
    CharArray localName = xmlTokenReader.getLocalNameCharArray();
    CharArray qName = xmlTokenReader.getQNameCharArray();
    contentHandler.endElement(determineString(uri), determineString(localName), determineString(qName));
  }
  
  private String determineString(CharArray charArray) {
    String strValue = (String)(charArrayToStringMap.get(charArray));
    if(strValue == null) {
      strValue = charArray.getStringFast();
      charArrayToStringMap.put(charArray, strValue);
    }
    return(strValue);
  }
  
}
