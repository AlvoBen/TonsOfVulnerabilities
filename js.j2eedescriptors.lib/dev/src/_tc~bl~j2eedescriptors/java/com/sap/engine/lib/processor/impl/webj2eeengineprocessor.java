/*
 * Copyright (c) 2004-2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.processor.impl;

import com.sap.engine.lib.descriptors.webj2eeengine.WebJ2EeEngineType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

import javax.xml.namespace.QName;
import java.io.InputStream;

/**
 * @author Viktoriya Ivanova
 */

public class WebJ2EEEngineProcessor extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WebJ2EEEngineProcessor singleton;
  private static final String[] schema_sources;

  static {
    schema_sources = new String[]{"web-j2ee-engine.xsd"};
    singleton = new WebJ2EEEngineProcessor();
  }

  public static WebJ2EEEngineProcessor getInstance() {
    return singleton;
  }//end of getInstance()

  private WebJ2EEEngineProcessor() {
    super(schema_sources);
    rootClass = WebJ2EeEngineType.class;
    rootElement = new QName(null, "web-j2ee-engine");

    marshaller = new XMLMarshaller();
    InputStream typesXMLStream = WebJ2EEEngineProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/webj2eeengine/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WebJ2EEEngineProcessor.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }
  }//end of constructor

  public XMLMarshaller getMarshaller() {
    return marshaller;
  }//end of getMarshaller()

  public Class getRootClass() {
    return rootClass;
  }//end of getRootClass()

  public QName getRootElement() {
    return rootElement;
  }//end of getRootElement()

}//end of class
