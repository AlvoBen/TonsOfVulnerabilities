package com.sap.engine.lib.xml.parser.tokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLTokenWriterDOMFactory {
  
  public static XMLTokenWriterDOM newInstance(final Document document) {
    return new XMLDOMTokenWriter(document);
  }
  
  public static XMLTokenWriterDOM newInstance(final DOMSource domSource) throws ParserConfigurationException {
    return new XMLDOMTokenWriter(domSource);
  }
  
  public static XMLTokenWriterDOM newInstace(final Element element) {
    return new XMLDOMTokenWriter(element);
  }  
  
}
