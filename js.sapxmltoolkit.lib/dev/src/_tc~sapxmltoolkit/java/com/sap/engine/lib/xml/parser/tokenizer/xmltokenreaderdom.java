package com.sap.engine.lib.xml.parser.tokenizer;

import java.util.Hashtable;
import org.w3c.dom.Element;

public interface XMLTokenReaderDOM extends XMLTokenReader {
  
  public void init(Element element);
  
  public void init(Element element,Hashtable prefixes);
  
}
