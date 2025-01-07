/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.InternalAttributeList;

/**
 * Generic XML Token Reader interface.
 * XML is parser incremental token by token. And diffrent states are reported for different tokens.
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public interface XMLTokenReader {
  /* The after birth state of a XMLReader. Not initialized.*/
  public static final int NOT_INITIAL = -1;
  /* The initial state of a XMLReader. Parsing not started just initialized.*/
  public static final int INITIAL = 0;
  /* The state denoting the start tag of an element.*/
  public static final int STARTELEMENT = 1;
  /* The state denoting the end tag of an element.*/
  public static final int ENDELEMENT = 2;
  /* The state denoting the character content of an element.*/
  public static final int CHARS = 3;
  /* The state denoting a processing instruction.*/
  public static final int PI = 4;
  /* The state denoting that the end of the document has been reached.*/
  public static final int COMMENT = 5;
  /* The state denoting that the end of the document has been reached.*/
  public static final int EOF = 6;
  /* The state indicating a DTD declaration is being parsed */
  public static final int DTD = 7;

  
  /**
   * Starts parsing process and positions to the first token in XML.
   * @return the state of the parser in first token.
   */
  public int begin() throws ParserException;


  /**
   * Stops parsing process and ends parsing.
   */
  public void end() throws ParserException;


  /**
   * Inits input of the XML Tokenizer. Call before begin.
   */
  public void init(InputStream input);

  /**
   * Inits input of the XML Tokenizer. Call before begin.
   */  
  public void init(InputSource input);

  /**
   * Inits input of the XML Tokenizer. Call before begin.
   */
  public void init(Reader reader);

  /**
   * Forces the parser to make a forward step in the parsing process.
   * @return the next state of the parser
   */
  public int next() throws ParserException;


  /**
   * Returns the URI mapped to this prefix in current parsing node.
   * If prefix mapping is not available return null.
   * @return the URI mapped to this prefix in current element.
   */
  public String getPrefixMapping(String prefix);


  /**
   * Returns value of current node.
   * @return in character nodes returns char content, comments and PI, otherwise NULL
   */
  public String getValue();


  /**
   *  Consistent only in the START state.
   *  @return the attributes of the current element.
   */
  public Attributes getAttributes();
  
  public InternalAttributeList getInternalAttributeList();


  /**
   *  Consistent only in the START and END states.
   *  @return the current URI.
   */
  public String getURI();


  /**
   *  Consistent only in the START and END states.
   *  @return the current local name.
   */
  public String getLocalName();


  /**
   *  Consistent only in the START and END states.
   *  @return the current qualified name.
   */
  public String getQName();


  /**
   *  Consistent only in the START and END states.
   *  @return the current URI as CharArray.
   */
  public CharArray getURICharArray();


  /**
   *  Consistent only in the START and END states.
   *  @return the current local name as CharArray.
   */
  public CharArray getLocalNameCharArray();


  /**
   *  Consistent only in the START and END states.
   *  @return the current qualified name as CharArray.
   */
  public CharArray getQNameCharArray();


  /**
   * Returns value of current node as CharArray.
   * @return in character nodes returns char content, comments and PI, otherwise NULL
   */
  public CharArray getValueCharArray();


  /**
   * Returns current parser state.
   * @return the current parser state.
   */
  public int getState();


  /**
   * Returns true if current token is only white space.
   * @return true if token content is whitespace.
   */
  public boolean isWhitespace();


  public org.w3c.dom.Element getDOMRepresentation(org.w3c.dom.Document doc) throws ParserException;

  /**
   * Moves to the next element start tag.
   * Returns STARTELEMENT if startelement is reached or EOF if parsing is finished.
   */
  public int moveToNextElementStart() throws ParserException;
  
  /**
   * Passes characters. Stops if elemed start or end is met.
   */
  public void passChars() throws ParserException;

  public CharArray getValuePassCharsCA() throws ParserException;
  
  public String getValuePassChars() throws ParserException;

  /**
   * Returns parent element of the current node or null if it is in root node.
   * @return
   */
  public String getParentElement();
  
  public int getCurrentLevel();

  public void setEntityResolver(EntityResolver entityResolver) throws ParserException ;
  
  /**
   * Continue parsing from the current event and  
   * writes the data into <code>writer</code>.
   * Valid reader states, in which this method could be invoked, are STARTELEMENT and CHARS.
   * When reader state is STARTELEMENT, only the data of this element is parsed and written in the writer.
   * When reader state is CHARS, only the chars are parsed and written in the writer.
   * 
   * @param writer in this writer all remaing events are written.
   * 
   * @exception ParserException if an exception occurs during parsing
   * @exception IOException if an I/O occurs during writing.
   */
  public void writeTo(Writer writer) throws ParserException, IOException;
  
  /**
   * Returns Map containing currently available prefixes, where
   * the key is the prefix and the value is the current namespace bound to this prefix.
   * The default namespace prefix is represented by "" value. 
   */
  public Map<String, String> getNamespaceMappings();
  
  /**
   * Returns the prefixes declared on the last start element. 
   */
  public List<String> getPrefixesOnLastStartElement();
  
  /**
   * Returns list of prefix definitions which are running out of scope at end element. One prefix definition
   * is an array with size 2, where [0] is the prefix and [1] is the namespace.
   */
  public List<String[]> getEndedPrefixMappings();
  
  /**
   * Sets XMLTokenReader configuration property.
   * @param key
   * @param value
   */
  public void setProperty(String key, Object value);
  
}

