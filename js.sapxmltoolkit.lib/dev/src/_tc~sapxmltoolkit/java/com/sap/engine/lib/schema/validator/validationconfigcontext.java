/*
 * Created on 2005-11-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.schema.validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

import com.sap.engine.lib.schema.components.Schema;
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.XMLParser;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ValidationConfigContext {
  
  private XMLParser parser;
  private boolean dynamicValidationFeature;
  private DocHandler docHandler;
  private Schema schema;
  private ErrorHandler errorHandler;
  
  public DocHandler getDocHandler() {
    return(docHandler);
  }
  
  public void setDocHandler(DocHandler docHandler) {
    this.docHandler = docHandler;
  }
  
  public ErrorHandler getErrorHandler() {
    return(errorHandler);
  }
  
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
  
  public Schema getSchema() {
    return(schema);
  }
  
  public void setSchema(Schema schema) {
    this.schema = schema;
  }
  
  public boolean getDynamicValidationFeature() {
    return(dynamicValidationFeature);
  }
  
  public void setDynamicValidationFeature(boolean dynamicValidationFeature) {
    this.dynamicValidationFeature = dynamicValidationFeature;
  }
  
  public XMLParser getParser() {
    return(parser);
  }
  
  public void setParser(XMLParser parser) {
    this.parser = parser;
  }
  
  public Locator getLocator() {
    return((Locator)parser);
  }
}