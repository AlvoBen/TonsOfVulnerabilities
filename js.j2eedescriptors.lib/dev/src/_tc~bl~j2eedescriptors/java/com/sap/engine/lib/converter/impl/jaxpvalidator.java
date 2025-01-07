/*
 * Copyright (c) 2006 by SAP AG, Walldorf. http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 * 
 * $Id$
 */

package com.sap.engine.lib.converter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sap.engine.lib.converter.AbstractConverter;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.FileNameExceptionPair;
import com.sap.engine.lib.converter.ParserWrapper;
import com.sap.engine.lib.converter.util.XercesUtil;
import com.sap.tc.logging.Severity;

/**
 * Validator implementation of top of J2SE 5.0 validation API (JAXP 1.3), which provides
 * schema pre-parsing & caching which speeds-up the validation process.
 * 
 * Keeps a map of all JEE schemas in memory.
 *  
 * @author Petar Zhechev
 * @version 1.1, 2007-7-05 
 */
public class JAXPValidator extends com.sap.engine.lib.converter.Validator implements ErrorHandler {
  
  private XMLToolkitValidator legacyValidator;
  
  private SchemaFactory schemaFactory;
  
  private ParserWrapper nonValidatingParser;
  
  private String currentFileName;
  
  private List<FileNameExceptionPair> fileExcPairList = new ArrayList<FileNameExceptionPair>();  
  
  private Map<String,Validator> validators;
  
  private static final Map<String, String[]> schemas = new HashMap<String, String[]>();
  
  static {
    schemas.put(ApplicationConverter.APPLICATION_FILENAME, new String[] {"application_1_4.xsd", "com/sap/engine/lib/schema/application_1_4.xsd",
                                                                         "application_5.xsd", "com/sap/engine/lib/schema/application_5.xsd"});
    schemas.put(ApplicationConverter.APPLICATION_J2EE_FILENAME, new String[] {"application-j2ee-engine.xsd", "com/sap/engine/lib/schema/application-j2ee-engine.xsd"});
    schemas.put(ConnectorConverter.CONNECTOR_FILENAME, new String[] {"connector_1_5.xsd", "com/sap/engine/lib/schema/connector_1_5.xsd"});
    schemas.put(ConnectorConverter.CONNECTOR_J2EE_FILENAME, new String[] {"connector-j2ee-engine.xsd", "com/sap/engine/lib/schema/connector-j2ee-engine.xsd"});
    schemas.put(EJBConverter.EJBJAR_FILENAME, new String[] {"ejb-jar_2_1.xsd", "com/sap/engine/lib/schema/ejb-jar_2_1.xsd",
                                                            "ejb-jar_3_0.xsd", "com/sap/engine/lib/schema/ejb-jar_3_0.xsd"});
    schemas.put(EJBConverter.EJBJ2EE_FILENAME, new String[] {"ejb-j2ee-engine.xsd", "com/sap/engine/lib/schema/ejb-j2ee-engine.xsd"});
    schemas.put(EJBConverter.PERSISTENT_FILENAME, new String[] {"persistent.xsd", "com/sap/engine/lib/schema/persistent.xsd"});
    schemas.put(TldConverter.TLD_FILENAME, new String[] {"web-jsptaglibrary_2_0.xsd", "com/sap/engine/lib/schema/web-jsptaglibrary_2_0.xsd",
                                                         "web-jsptaglibrary_2_1.xsd", "com/sap/engine/lib/schema/web-jsptaglibrary_2_1.xsd"});
    schemas.put(WebConverter.WEB_FILENAME, new String[] {"web-app_2_4.xsd", "com/sap/engine/lib/schema/web-app_2_4.xsd",
                                                         "web-app_2_5.xsd", "com/sap/engine/lib/schema/web-app_2_5.xsd"});
    schemas.put(WebConverter.WEBJ2EE_FILENAME, new String[] {"web-j2ee-engine.xsd", "com/sap/engine/lib/schema/web-j2ee-engine.xsd"});    
  }
  
  public JAXPValidator() {
    validators = new HashMap<String, Validator>();
    schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    resolver = new MultiResolver();
    schemaFactory.setResourceResolver(resolver);    
    legacyValidator = new XMLToolkitValidator();
    legacyValidator.setEntityResolver(resolver);
    try {
      nonValidatingParser = new ParserWrapper(XercesUtil.getDocumentBuilder(XercesUtil.NON_VALIDATING));
    } catch (ParserConfigurationException e) {
      AbstractConverter.LOCATION.catching(e);
      throw new RuntimeException(e);
    }
  }
  
  
  
  /* (non-Javadoc)
   * @see com.sap.engine.lib.converter.Validator#validate(javax.xml.transform.Source, java.lang.String, boolean)
   */
  public synchronized DOMResult validate(Source source, String descriptorName, boolean forgiving) throws ConversionException {
    long st = System.currentTimeMillis();
    this.currentFileName = descriptorName;
    DOMSource domSource = null;
    Source validatedSource = null;
    
    if (source instanceof DOMSource) {
      domSource = (DOMSource) source;
      try {
        validatedSource = XercesUtil.serialize(domSource);
      } catch (SAXException e) {
        throw new RuntimeException(e);
      }
    } else if (source instanceof StreamSource) {
      long s = System.currentTimeMillis();
      StreamSource streamSource = (StreamSource) source;
      
      InputSource inputSource = XercesUtil.toInputSource((streamSource.getInputStream()));
      inputSource.setSystemId(streamSource.getSystemId());
      domSource = new DOMSource(nonValidatingParser.parse(inputSource, descriptorName));
      domSource.setSystemId(inputSource.getSystemId());
      // workaround for the bug from CTS for UndeclaredPrefix
      try {
        source = XercesUtil.serialize(domSource);
      } catch (SAXException e) {
        throw new RuntimeException(e);
      }
      validatedSource = source;
      AbstractConverter.LOCATION.debugT("JAXPValidator - parsing with non-validating parser took: " + (System.currentTimeMillis() - s) + " " + descriptorName);
    } else {
      throw new IllegalArgumentException(source.getClass().getName());
    }
    
    boolean hasSchemaLocation = XercesUtil.hasSchemaLocation((Document) domSource.getNode());
    
    if (hasSchemaLocation) {
      // REVISE workaround for ejb-jar-j2ee.xml validation, it is not possible to create one Schema object for the two schemas 
      Validator validator = EJBConverter.EJBJ2EE_FILENAME.equals(descriptorName) ? getSchemaValidator(XercesUtil.getSchemaLocation((Document) domSource.getNode())) : getValidator(descriptorName);
      if (validator == null) {
        if (AbstractConverter.LOCATION.beDebug()) {
          AbstractConverter.LOCATION.debugT(String.format("Failed to create validator for descriptor %s", descriptorName));
        }        
        throw new IllegalArgumentException("Unsupported Java EE descriptor " + descriptorName);
      }    	
      validator.setErrorHandler(this);
      try {
        long s = System.currentTimeMillis();
        validator.validate(validatedSource);
        AbstractConverter.LOCATION.debugT("JAXPValidator - validation using validator took: " + (System.currentTimeMillis() - s) + " " + descriptorName);
      } catch (SAXException e) {
        // $JL-EXC$ handled by the ErrorHandler
      } catch (IOException e) {
        AbstractConverter.throwConversionException(e, descriptorName);
      } finally {
        validator.reset();
      }
    } else {
      // either DTD or nothing
      if (!forgiving) {
        // we have to do DTD validation in this case
        long s = System.currentTimeMillis();
        domSource.setNode(legacyValidator.validate(domSource, descriptorName, forgiving).getNode());
        AbstractConverter.LOCATION.debugT("JAXPValidator - validation using legacy parser took: " + (System.currentTimeMillis() - s) + " " + descriptorName);
      }
    }
    throwConversionExcIfNeeded();
    AbstractConverter.LOCATION.debugT("JAXPValidator - overall validation took: " + (System.currentTimeMillis() - st) + " " + descriptorName);
    return new DOMResult(domSource.getNode());
  }
  
  private Validator getSchemaValidator(String schemaLocation) {
    String[] schemaLocations = schemaLocation.split("\\s");
    schemaLocation = schemaLocations[schemaLocations.length - 1];
  	if (validators.containsKey(schemaLocation)) {
  		return validators.get(schemaLocation);
  	}
  	try {
    	InputSource schemaSource = resolver.resolveEntity(null, schemaLocation);
    	if (schemaSource != null) {
      	Schema schema = schemaFactory.newSchema(new StreamSource(schemaSource.getByteStream(), schemaLocation));
      	Validator validator = schema.newValidator();
        validators.put(schemaLocation, validator);
      	return validator;
    	}
  	} catch (SAXException e) {
  	  //$JL-EXC$	
      e.printStackTrace();
  	} catch (IOException e) {
  	  //$JL-EXC$		
  	}
  	return null;
 }
  
  private Validator getValidator(String descriptorName) {
    Validator validator = (Validator) validators.get(descriptorName);
    if (validator != null) {
      return validator;
    }
    long s = System.currentTimeMillis();
    Schema descriptorSchema = null;
    ClassLoader loader = JAXPValidator.class.getClassLoader();
    String[] descriptorSchemas = (String[]) schemas.get(descriptorName);
    if (descriptorSchemas == null) {
      throw new IllegalArgumentException("Unsupported Java EE descriptor " + descriptorName);
    }
    ArrayList<Source> schemaSources = new ArrayList<Source>(3);
    for (int i = 0; i < descriptorSchemas.length; i=i+2) {
      String systemId = descriptorSchemas[i];
      String location = descriptorSchemas[i+1];
      InputStream schemaStream =  loader.getResourceAsStream(location);
      if (schemaStream == null) {
        if (AbstractConverter.LOCATION.beDebug()) {
          AbstractConverter.LOCATION.debugT("Failed to load schema " + systemId + " from " + location + ". Will try to resolve it from entity resolver " + resolver);
        }
        InputSource inputSource = null;
        try {
          inputSource = resolver.resolveEntity(null, systemId);
        } catch (SAXException e) {
          //$JL-EXC$
        } catch (IOException e) {
          //$JL-EXC$
        }
        if (inputSource != null) {
          schemaStream = inputSource.getByteStream();
        }
      }
      if (schemaStream != null) {
        schemaSources.add(new StreamSource(schemaStream, systemId));
      } else {
        if (AbstractConverter.LOCATION.beDebug()) {
          AbstractConverter.LOCATION.debugT("Failed to load schema " + systemId + " with entity resolver " + resolver);
        }
      }
            
    }
    try {
      descriptorSchema = schemaFactory.newSchema((Source[]) schemaSources.toArray(new Source[0]));
    } catch (SAXException e) {
      /// bad schemas, it shouldn't happen, but make it visible
      AbstractConverter.LOCATION.catching(e);
      throw new RuntimeException(e);          
    }
      
    if (descriptorSchema == null) {
      throw new IllegalArgumentException("Unsupported Java EE descriptor " + descriptorName);
    }
    Validator descriptorValidator = descriptorSchema.newValidator();    
    descriptorValidator.setErrorHandler(this);    
    descriptorValidator.setResourceResolver(resolver);
    validators.put(descriptorName, descriptorValidator);
    AbstractConverter.LOCATION.debugT("JLinEE init schema: " + (System.currentTimeMillis() - s) + descriptorName);
    return descriptorValidator;
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.lib.converter.Validator#setEntityResolver(org.xml.sax.EntityResolver)
   */
  public void setEntityResolver(EntityResolver entityResolver) {
    if (resolver.addResolver(entityResolver)) {
      // new resolver has been added, the validators have to be re-created
      validators.clear();
    } 
  }
  

  // Error Handler 
  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
   */
  public void error(SAXParseException exception) throws SAXException {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "SAX errorhandler registering error", exception);
    addToExceptionList(currentFileName, exception);
    // have to re-throw because parser will return null Document otherwise
    throw exception;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
   */
  public void fatalError(SAXParseException exception) throws SAXException {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "SAX errorhandler registering fatal error", exception);
    addToExceptionList(currentFileName, exception);
    // have to re-throw because parser will return null Document otherwise
    throw exception;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
   */
  public void warning(SAXParseException exception) throws SAXException {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "SAX errorhandler registering warning", exception);
    addToExceptionList(currentFileName, exception,
        FileNameExceptionPair.SEVERITY_WARNING);
    // don't re-throw because parsing could still succeed if we have warnings
    // only
  }

  private void addToExceptionList(String fileName, Throwable t) {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "adding exception to conversion exception list", t);
    fileExcPairList.add(new FileNameExceptionPair(t, fileName));
  }

  private void addToExceptionList(String fileName, Throwable t, int severity) {
    AbstractConverter.LOCATION.traceThrowableT(Severity.DEBUG,
        "adding exception to conversion exception list", t);
    fileExcPairList.add(new FileNameExceptionPair(t, fileName, severity));
  }

  private void throwConversionExcIfNeeded() throws ConversionException {
    if (fileExcPairList.size() == 0) {
      return;
    }
    ConversionException ce = new ConversionException(
        (FileNameExceptionPair[]) fileExcPairList
            .toArray(new FileNameExceptionPair[0]));
    fileExcPairList.clear();
    AbstractConverter.LOCATION.throwing("throwing conversion exception", ce);
    throw ce;
  }
}