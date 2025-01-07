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

import com.sap.engine.lib.descriptors.web.WebAppType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

import javax.xml.namespace.QName;
import java.io.InputStream;

/**
 * @author Viktoriya Ivanova
 */

public class WebProcessor extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WebProcessor singleton;
  private static final String[] schema_sources;

  static {
    schema_sources = new String[]{"web-app_2_4.xsd"};
    singleton = new WebProcessor();
  }

  public static WebProcessor getInstance() {
    return singleton;
  }//end of getInstance()

  private WebProcessor() {
    super(schema_sources);
    rootClass = WebAppType.class;
    rootElement = new QName("http://java.sun.com/xml/ns/j2ee", "web-app");

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = WebProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/web/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WebProcessor.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }*/
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
