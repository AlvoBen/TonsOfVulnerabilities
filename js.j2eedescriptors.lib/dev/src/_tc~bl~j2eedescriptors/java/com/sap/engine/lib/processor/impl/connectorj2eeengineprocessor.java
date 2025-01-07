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

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.connectorj2eeengine.ConnectorType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;


/**
 * @author   Viktoriya Ivanova
 * @author  Jan Sievers
 */

public class ConnectorJ2EEEngineProcessor extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static QName schemaName;
  private static XMLMarshaller marshaller;
 
  private static final ConnectorJ2EEEngineProcessor singleton;
  private static final String[] schema_sources;

  private static final String SAP_XML_TYPES = "/com/sap/engine/lib/descriptors/connectorj2eeengine/frm/types.xml";
  protected static final String CONNECTOR_MAIN_TAG = "connector";

  public static ConnectorJ2EEEngineProcessor getInstance() {
    return singleton;
  }

  static {
    schema_sources = new String[] {"connector-j2ee-engine.xsd"};
    singleton = new ConnectorJ2EEEngineProcessor();
  }
    

  private ConnectorJ2EEEngineProcessor() {
    super(schema_sources);
    try {
      setFeature(Features.APACHE_DYNAMIC_VALIDATION, false);
    } catch(Exception exc) {
      throw new AssertionError(exc);
    }


    rootClass = ConnectorType.class;
    rootElement = new QName(null, CONNECTOR_MAIN_TAG);
    //there is no target name space
    schemaName = new QName("connectorType");//TODO use it in case of marshaling

    marshaller = new XMLMarshaller();
    InputStream typesXMLStream = ConnectorJ2EEEngineProcessor.class.getResourceAsStream(SAP_XML_TYPES);
    try {
      marshaller.init(typesXMLStream, ConnectorJ2EEEngineProcessor.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }
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


}
