package com.sap.engine.lib.xml.parser.tokenizer;

import java.util.Hashtable;
import org.w3c.dom.Element;

public class XMLTokenReaderDOMFactory {
  
  public static XMLTokenReaderDOM newInstance(final Element element) {
    return new XMLDOMTokenReader(element);
  }
  
  public static XMLTokenReaderDOM newInstance(final Element element , final Hashtable prefixes) {
    return new XMLDOMTokenReader(element,prefixes);
  }
  
  
}
