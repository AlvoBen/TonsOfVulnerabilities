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

import com.sap.engine.lib.descriptors5.webjsptld.TldTaglibType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

import javax.xml.namespace.QName;
import java.io.InputStream;

import org.xml.sax.SAXNotRecognizedException;

/**
 * @author Violeta Georgieva
 * @version 7.10
 */
public class WebJspTLDProcessor5 extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WebJspTLDProcessor5 singleton;
  private static final String[] schema_sources;

  static {
    schema_sources = new String[]{"web-jsptaglibrary_2_1.xsd"};
    singleton = new WebJspTLDProcessor5();
  }

  public static WebJspTLDProcessor5 getInstance() {
    return singleton;
  }//end of getInstance()

  private WebJspTLDProcessor5() {
    super(schema_sources);

    rootClass = TldTaglibType.class;
    rootElement = new QName("http://java.sun.com/xml/ns/javaee", "taglib");

    marshaller = new XMLMarshaller();
    InputStream typesXMLStream = WebJspTLDProcessor5.class.getResourceAsStream("/com/sap/engine/lib/descriptors5/webjsptld/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WebJspTLDProcessor5.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }
    try {
      setNamespaceReplacement("http://java.sun.com/xml/ns/j2ee", "http://java.sun.com/xml/ns/javaee");
    } catch (Exception e) {
      throw new AssertionError(e);
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
