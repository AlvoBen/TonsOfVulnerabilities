package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.InputSource;
import com.sap.engine.lib.xml.parser.ParserException;
import com.sap.engine.lib.xml.parser.handlers.INamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;



/**
 * Factory responsible for creating instances of the XMLTokenReader.
 * @author chavdar baikov, angel tcholtchev
 *
 */
public class XMLTokenReaderFactory {
  
  private static XMLTokenReaderFactory factory;
    
  private XMLTokenReaderFactory(){  
  }
  
  
  /**
   * 
   * @return Singleton instance of the factory.
   */
  public static synchronized XMLTokenReaderFactory getInstance(){
    if (factory == null){
      factory = new XMLTokenReaderFactory();
    }    
    return factory;
  }
  
  /**
   * Creates new default XMLTokenReader instance.
   * 
   */
  public XMLTokenReader createReader() {
    return new XMLTokenReaderImpl();
  }

  /**
   * Creates new default XMLTokenReader instance with predefined namespaces.
   * 
   */
  public XMLTokenReader createReader(Map<?,?> envMap) throws ParserException {
    XMLTokenReaderImpl reader = new XMLTokenReaderImpl();
    
    // Run begin to init the parser and the NS handler
    reader.begin();
    
    addEnvelopePrefixesInReader(reader.getNamespaceHandler(), envMap);
    
    return reader;
  }
  
  /**
   * Creates new XMLTokenReader that loads the passed input stream. 
   * 
   */
  public XMLTokenReader createReader(final InputStream input) {
    return new XMLTokenReaderImpl(input);
  }

  /**
   * Creates new XMLTokenReader instance  that loads the passed input stream
   * and predefines namespaces.
   * 
   */
  public XMLTokenReader createReader(final InputStream input, Map envMap) throws ParserException {
    XMLTokenReaderImpl reader = new XMLTokenReaderImpl(input);
    
    // Run begin to init the parser and the NS handler
    reader.begin();
    
    addEnvelopePrefixesInReader(reader.getNamespaceHandler(), envMap);
    
    return reader;
  }
    
  /**
   * Creates new XMLTokenReader instance  that loads the passed input stream
   * and predefines namespaces.
   * 
   */
  public XMLTokenReader createReader(final InputSource input) {
    return new XMLTokenReaderImpl(input);    
  }
  
  /**
   * Creates new XMLTokenReader instance  that loads the passed input stream
   * and predefines namespaces.
   * 
   */
  public XMLTokenReader createReader(final InputSource input, Map envMap) throws ParserException {
    XMLTokenReaderImpl reader = new XMLTokenReaderImpl(input);
    
    // Run begin to init the parser and the NS handler
    reader.begin();
    
    addEnvelopePrefixesInReader(reader.getNamespaceHandler(), envMap);
    
    return reader;    
  }

  /**
   * Creates new XMLTokenReader instance  that loads the passed input stream
   * and predefines namespaces.
   * 
   */  
  public XMLTokenReader createReader(final Reader reader) {
    return new XMLTokenReaderImpl(reader);
  }
  
  /**
   * Creates new XMLTokenReader instance  that loads the passed input stream
   * and predefines namespaces.
   * 
   */
  public XMLTokenReader createReader(final Reader reader, Map envMap) throws ParserException {
    XMLTokenReaderImpl newReader = new XMLTokenReaderImpl(reader);
    
    // Run begin to init the parser and the NS handler
    newReader.begin();
    
    addEnvelopePrefixesInReader(newReader.getNamespaceHandler(), envMap);
    
    return newReader;    
  }

  
  
  /**
   * Adds the prefixes from <code>envMap</code> into <code>nsHandler</code>.
   */
  private void addEnvelopePrefixesInReader(INamespaceHandler nsHandler, Map<?,?> envMap) {
    if (envMap == null){
      return;
    }
        
    //keys are prefix namespaces
    String prefNS, pref;
    for(Iterator<?> it = envMap.keySet().iterator(); it.hasNext();){ 
      prefNS = (String) it.next();
      pref = (String) envMap.get(prefNS);
      nsHandler.add(new CharArray(pref), new CharArray(prefNS));
    }
  }
  
  
  
  /**
   * Creates new default XMLTokenReader instance.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance() {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
                
    return factory.createReader();
  }
  
  
  /**
   * Creates new default XMLTokenReader instance.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance(Map<?,?> envMap) throws ParserException {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(envMap);
  }
  
  
  /**
   * Creates new XMLTokenReader that loads the stream.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance(final InputStream input) {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(input);
  }
  
  /**
   * Creates new XMLTokenReader that loads the stream.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance(final InputStream input, Map envMap) throws ParserException {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(input, envMap);
  }
  
  /**
   * Creates new XMLTokenReader.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance(final InputSource input) {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(input);    
  }
 
  /**
   * Creates new XMLTokenReader.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance(final InputSource input, Map envMap) throws ParserException {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(input, envMap);    
  }
  
  /**
   * Creates new XMLTokenReader.
   * 
   * @deprecated use the factory createReader() method 
   */
  public static XMLTokenReader newInstance(final Reader reader) {
    XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(reader);
  }
  
  /**
   * Creates new XMLTokenReader.
   * 
   * @deprecated use the factory createReader() method 
   */
  public XMLTokenReader newInstance(final Reader reader, Map envMap) throws ParserException {
 XMLTokenReaderFactory factory = XMLTokenReaderFactory.getInstance();
    
    return factory.createReader(reader, envMap);
  }
    
}
