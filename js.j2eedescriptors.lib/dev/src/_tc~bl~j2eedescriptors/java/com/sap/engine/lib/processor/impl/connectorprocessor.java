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

package com.sap.engine.lib.processor.impl;


import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Hashtable;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.connector.ConnectorType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.xml.parser.JAXPProperties;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;
import org.xml.sax.SAXException;


/**
 * @author  Viktoriya Ivanova
 * @author  Jan Sievers
 * @author  Peter Matov
 */

public class ConnectorProcessor extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;
 
  private static final ConnectorProcessor singleton;
  private static final String[] schema_sources;

  private static final String RA_XML_TARGET_NAME_SPACE = "http://java.sun.com/xml/ns/j2ee";
  private static final String RA_XML_TYPES = "/com/sap/engine/lib/descriptors/connector/frm/types.xml";
  protected static final String CONNECTOR_MAIN_TAG = "connector";

    public static ConnectorProcessor getInstance() {
      return singleton;
    }

    static {
      schema_sources = new String[] {"connector_1_5.xsd"};
      singleton = new ConnectorProcessor();
    }
    

  private ConnectorProcessor() {
    super(schema_sources);

    try {
      setFeature(Features.APACHE_DYNAMIC_VALIDATION, false);
      setNameSpaceMapping();
    } catch(Exception exc) {
      throw new AssertionError(exc);
    }

    rootClass = ConnectorType.class;
    rootElement = new QName(RA_XML_TARGET_NAME_SPACE, CONNECTOR_MAIN_TAG);

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = ConnectorProcessor.class.getResourceAsStream(RA_XML_TYPES);
    try {
      marshaller.init(typesXMLStream, ConnectorProcessor.class.getClassLoader());
    } catch (TypeMappingException exc) {
      throw new AssertionError(exc);
    }*/
  }

  public synchronized Object parse(InputStream xmlStream) throws SAXException, IOException {
    setNameSpaceMapping();
    return super.parse(xmlStream);
  }

  public synchronized Object parse(String path) throws FileNotFoundException, SAXException, IOException {
    setNameSpaceMapping();
    return super.parse(path);
  }

  public XMLMarshaller getMarshaller() {
    return marshaller;
  }


  public Class getRootClass() {
    return rootClass;
  }


  public QName getRootElement() {
    return rootElement;
  }

  private void setNameSpaceMapping() throws SAXException {
    Hashtable nameSpacesMapping = new Hashtable();
    nameSpacesMapping.put("<<<>>>", RA_XML_TARGET_NAME_SPACE);
    setProperty(JAXPProperties.PROPERTY_ADD_NSMAPPINGS, nameSpacesMapping);
  }

}
