package com.sap.engine.lib.xml.parser.tokenizer;

import java.util.Hashtable;
import org.w3c.dom.Element;

/**
 * 
 * @author I024072
 * @deprecated Please use the official interface {@link XMLTokenReaderDOM} and {@link XMLTokenReaderDOMFactory} to operate with such reader.
 */
public class XMLDOMTokenReaderNew extends XMLDOMTokenReader {
  
  public XMLDOMTokenReaderNew(Element element) {
    super(element);
  }
  
  public XMLDOMTokenReaderNew(Element element, Hashtable prefixes) {
    super(element,prefixes);
  }
  
}
