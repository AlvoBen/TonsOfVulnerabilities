package com.sap.engine.lib.xml.parser.tokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XMLTokenWriter to DOM tree.
 * @author I024072
 *
 */
public interface XMLTokenWriterDOM extends XMLTokenWriter {
  
  /**
   * Initialize XMLTokenWriter to output into DOMSource. If the DOMSource is empty it will create a new
   * document node and output into this document..
   * @param domSource
   * @throws ParserConfigurationException
   */
  public void init(DOMSource domSource) throws ParserConfigurationException;
  
  /**
   * Initialize XMLTokenWriter to output into DOM Document. 
   * @param doc
   */
  public void init(Document doc);
  
  /**
   * Initialize XMLTokenWriter to output into DOM Element.
   * @param mainElement
   */
  public void init(Element mainElement); 
  
}
