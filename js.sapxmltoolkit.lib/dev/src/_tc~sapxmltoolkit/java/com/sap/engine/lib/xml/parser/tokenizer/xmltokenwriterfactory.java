package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Factory class for XMLTokenWriter instances.
 * @author I024072
 *
 */
public class XMLTokenWriterFactory {
  
  private static XMLTokenWriterFactory factory;
  
  private XMLTokenWriterFactory(){
    
  }
  
  /**
   * Get singleton instance of the factory.
   * @return
   */
  public static synchronized XMLTokenWriterFactory getInstance(){
    if (factory == null){
      factory = new XMLTokenWriterFactory();
    }
            
    return factory;
  }
  
  
  /**
   * Create instance of XMLTokenWriter
   * @return
   */
  public XMLTokenWriter createWriter() {
    return new XMLTokenWriterImpl();
  }
  
  /**
   * Create instance of XMLTokenWriter
   * @return
   */
  public XMLTokenWriter createWriter(final OutputStream output) throws IOException {
    XMLTokenWriter result = new XMLTokenWriterImpl();
    result.init(output);
    return result;
  }
  
  /**
   * Create instance of XMLTokenWriter
   * @return
   */
  public XMLTokenWriter createWriter(final OutputStream output, final Hashtable<?,?> defaultPrefixes) throws IOException {
    XMLTokenWriter result = new XMLTokenWriterImpl();
    result.init(output,defaultPrefixes);
    return result;        
  }
  
  /**
   * Create instance of XMLTokenWriter
   * @return
   */
  public XMLTokenWriter createWriter(final OutputStream output,final String encoding) throws IOException {
    XMLTokenWriter result = new XMLTokenWriterImpl();
    result.init(output,encoding);
    return result;            
  }
  
  /**
   * Create instance of XMLTokenWriter
   * @return
   */
  public XMLTokenWriter createWriter(final OutputStream output,final String encoding,final Hashtable<?,?> defaultPrefixes) throws IOException {
    XMLTokenWriter result = new XMLTokenWriterImpl();
    result.init(output,encoding,defaultPrefixes);
    return result;                
  }
  
  

  /**
   * 
   * @deprecated use createWriter()
   */
  public static XMLTokenWriter newInstance() {
    XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
              
    return factory.createWriter();
  }
  
  /**
   * 
   * @deprecated use createWriter()
   */
  public static XMLTokenWriter newInstance(final OutputStream output) throws IOException {
    XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
    
    return factory.createWriter(output);
  }
  
  
  /**
   * 
   * @deprecated use createWriter()
   */
  public static XMLTokenWriter newInstance(final OutputStream output, final Hashtable<?,?> defaultPrefixes) throws IOException {
    XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
    
    return factory.createWriter(output, defaultPrefixes);        
  }
  
  /**
   * 
   * @deprecated use createWriter()
   */
  public static XMLTokenWriter newInstance(final OutputStream output,final String encoding) throws IOException {
    XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
    
    return factory.createWriter(output, encoding);            
  }
  
  /**
   * 
   * @deprecated use createWriter()
   */
  public static XMLTokenWriter newInstance(final OutputStream output,final String encoding,final Hashtable<?,?> defaultPrefixes) throws IOException {
    XMLTokenWriterFactory factory = XMLTokenWriterFactory.getInstance();
    
    return factory.createWriter(output, encoding, defaultPrefixes);                
  }  
}
