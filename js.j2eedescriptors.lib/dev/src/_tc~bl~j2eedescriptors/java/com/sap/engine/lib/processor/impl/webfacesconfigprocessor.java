/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.processor.impl;

import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;
import com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigType;
import com.sap.engine.lib.processor.SchemaProcessor;

import javax.xml.namespace.QName;
import java.io.InputStream;

/**
 * @author Violeta Georgieva
 * @version 7.10
 */
public class WebFacesConfigProcessor extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WebFacesConfigProcessor singleton;
  private static final String[] schema_sources;

  static {
    schema_sources = new String[]{"web-facesconfig_1_2.xsd"};
    singleton = new WebFacesConfigProcessor();
  }

  public static WebFacesConfigProcessor getInstance() {
    return singleton;
  }//end of getInstance()

  private WebFacesConfigProcessor() {
    super(schema_sources);

    rootClass = FacesConfigType.class;
    rootElement = new QName("http://java.sun.com/xml/ns/javaee", "faces-config");

    marshaller = new XMLMarshaller();
    InputStream typesXMLStream = WebFacesConfigProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors5/webfacesconfig/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WebFacesConfigProcessor.class.getClassLoader());
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
