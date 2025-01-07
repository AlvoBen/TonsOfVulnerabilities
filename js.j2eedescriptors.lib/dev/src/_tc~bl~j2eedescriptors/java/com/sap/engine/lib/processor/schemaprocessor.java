/*
 * $Id$
 *
 * Copyright (c) 200x by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.processor;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.engine.lib.xml.parser.DOMParser;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.parser.JAXPProperties;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;



/**
 * The base schema processing tool for the j2ee deployment descriptors.
 * @author 	Chavdar Baikov
 */

public abstract class SchemaProcessor {


	private DOMParser parser;
	private InputSource[] sources;
  private boolean streamingDeserialization = false;
  private static XMLMarshaller j2ee1_4_marshaller;
  
  static {
    // Static initialization of the j2ee 1.4 marshaller
    if (j2ee1_4_marshaller == null) {
      j2ee1_4_marshaller = new XMLMarshaller();
      InputStream typesXMLStream = SchemaProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/types.xml");
      try {
        j2ee1_4_marshaller.init(typesXMLStream, SchemaProcessor.class.getClassLoader());
      } catch (TypeMappingException ex) {
        throw new AssertionError(ex);
      }
    }          
  }
  /*
  private static synchronized void initJ2EE_Marshaller() {
    if (j2ee1_4_marshaller == null) {
      j2ee1_4_marshaller = new XMLMarshaller();
      InputStream typesXMLStream = SchemaProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/types.xml");
      try {
        j2ee1_4_marshaller.init(typesXMLStream, SchemaProcessor.class.getClassLoader());
        System.out.println("J2EE marshaller was initialized !");
      } catch (TypeMappingException ex) {
        System.out.println("J2EE marshaller was not initialized !");
        ex.printStackTrace(System.out);
        j2ee1_4_marshaller = null;
        throw new AssertionError(ex);
      }
    }      
  }*/
  
  
/*
	public SchemaProcessor() {
		try {
		  
		  parser = new DOMParser();
			EntityResolver resolver = new StandardEntityResolver();
			parser.setFeature(Features.FEATURE_NAMESPACES, true);
			parser.setFeature(Features.APACHE_DYNAMIC_VALIDATION, true);
			parser.setFeature(Features.FEATURE_VALIDATION, true);
			parser.setFeature(Features.FEATURE_READ_DTD, false);
			parser.setFeature(Features.FEATURE_TRIM_WHITESPACES, true);
			parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE, Constants.SCHEMA_LANGUAGE);
			parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_SOURCE, StandardEntityResolver.getJ2EESchemas());
			parser.setEntityResolver(resolver);
		} catch (Exception e) {
			throw new AssertionError();
		}
	}
*/
   
   /**
    * Default constructor uses common xml marshaller for all j2ee 1.4 xmls.
    */
	public SchemaProcessor(String[] sources) {
		try {
			init(sources, new SchemaEntityResolver());
		} catch (Exception e) {
			throw new AssertionError(e);
		}
      //initJ2EE_Marshaller();
	}

    /**
     * 
     * @param sources
     * @param resolver
     * @throws SAXNotRecognizedException
     * @throws IOException
     * @throws SAXException
     * @throws Exception
     */
	public SchemaProcessor(String[] sources, EntityResolver resolver) throws SAXNotRecognizedException, IOException, SAXException, Exception {
		init(sources, resolver);
      //initJ2EE_Marshaller();
	}
		

	private final void init(String[] sources, EntityResolver resolver) throws SAXNotRecognizedException, IOException, SAXException, Exception {
		initSources(sources, resolver);	
		parser = new DOMParser();      
		parser.setFeature(Features.FEATURE_NAMESPACES, true);
		parser.setFeature(Features.APACHE_DYNAMIC_VALIDATION, true);
		parser.setFeature(Features.FEATURE_VALIDATION, true);
		parser.setFeature(Features.FEATURE_READ_DTD, false);
		parser.setFeature(Features.FEATURE_TRIM_WHITESPACES, true);
		parser.setFeature(Features.FEATURE_XSD_DOC_VALIDATION, false);
		parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE, Constants.SCHEMA_LANGUAGE);
		parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_SOURCE, this.sources);
		parser.setEntityResolver( resolver);
	}
	
	private final void initSources(String[] sources, EntityResolver resolver) throws SAXException, IOException {
		this.sources = new InputSource[sources.length];
		for (int i = 0; i < this.sources.length; i++) {
			this.sources[i] = resolver.resolveEntity(null, sources[i]);       
			//assert this.sources[i] != null;
		}
	}
	
	public synchronized void switchOffValidation() {
		try {
			parser.setFeature(Features.FEATURE_VALIDATION, false);
			parser.setFeature(Features.FEATURE_BACKWARDS_COMPATIBILITY_MODE, true);
            setStreamingDeserialization(true);
            getMarshaller().setStringTrim(true);
            getMarshaller().setProperty(Features.FEATURE_READ_DTD, Boolean.FALSE);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	public synchronized void switchOnValidation() {
		try {
			parser.setFeature(Features.FEATURE_VALIDATION, true);
			parser.setFeature(Features.FEATURE_BACKWARDS_COMPATIBILITY_MODE, false);
            setStreamingDeserialization(false);
            getMarshaller().setStringTrim(true);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

  public synchronized void setFeature(String feature, boolean enable) throws SAXException {
    parser.setFeature(feature, enable);
  }

  public synchronized void setProperty(String key, Object value) throws SAXException {
    parser.setProperty(key, value);
  }

  public synchronized void setErrorHandler(ErrorHandler handler) {
    parser.setErrorHandler(handler);
  }

  public synchronized Object parse(InputStream xmlStream) throws SAXException, IOException {
    try {
      if (isStreamingDeserialization()) {
        return getMarshaller().unmarshal(getRootClass(), getRootElement(), xmlStream);
      } else {
        Document doc = parser.parse(xmlStream);
        StandardDOMParser.trimWhiteSpaces(doc);
        return getMarshaller().unmarshal(getRootClass(), doc.getDocumentElement());
      }
    } finally {
      xmlStream.close();
    }
  }

  public synchronized Object parse(Document doc) throws UnmarshalException {
    StandardDOMParser.trimWhiteSpaces(doc);
    return getMarshaller().unmarshal(getRootClass(), doc.getDocumentElement());
  }

  public synchronized Object parse(String path) throws FileNotFoundException, SAXException, IOException {
    return parse(new FileInputStream(path));
  }

	public void build(Object objTree, OutputStream xmlStream) throws RemoteException {
  	getMarshaller().marshal(objTree, getRootElement(), xmlStream);
	}
  
  public void build(Object objTree, String file) throws RemoteException, FileNotFoundException, IOException {
    FileOutputStream out = new FileOutputStream(file);
    try {
      getMarshaller().marshal(objTree, getRootElement(), out);
    } finally {
      out.close();
    }
  }  

  /**
   * Returns base j2ee marshaller that supports all types defined in j2ee 1.4
   * @return
   */
  public XMLMarshaller getBaseMarshaller() {
    if (j2ee1_4_marshaller == null) {
      throw new RuntimeException("Base J2EE marshaller was not initialized ! Some initialization problem occured !"); 
    }
    return j2ee1_4_marshaller;
  }

	public abstract XMLMarshaller getMarshaller();


	public abstract Class getRootClass();

  public abstract QName getRootElement();

  /**
   * @return Returns the streamingValidation.
   */
  public boolean isStreamingDeserialization() {
    return streamingDeserialization;
  }
  /**
   * @param streamingValidation The streamingValidation to set.
   */
  public void setStreamingDeserialization(boolean streamingValidation) {
    this.streamingDeserialization = streamingValidation;
  }
  
  public void setNamespaceReplacement(String originalNS, String replacedNS) throws Exception {
    parser.setProperty(JAXPProperties.PROPERTY_REPLACE_NAMESPACE, new String[]{originalNS, replacedNS});
    getMarshaller().setProperty(JAXPProperties.PROPERTY_REPLACE_NAMESPACE, new String[]{originalNS, replacedNS});
  }
}
