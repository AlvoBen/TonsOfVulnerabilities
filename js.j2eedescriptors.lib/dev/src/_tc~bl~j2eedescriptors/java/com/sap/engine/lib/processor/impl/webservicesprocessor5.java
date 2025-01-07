package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.webservices.WebservicesType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 *
 *
 * @author  Vladimir Savchenko
 */

 public class WebServicesProcessor5 extends SchemaProcessor {
  
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WebServicesProcessor5 singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"javaee_web_services_1_2.xsd"};
    singleton = new WebServicesProcessor5();
  }


  public static WebServicesProcessor5 getInstance() {
    return singleton;
  }


  private WebServicesProcessor5() {
    super(schema_sources);
    rootClass = com.sap.engine.lib.descriptors5.webservices.WebservicesType.class;
    rootElement = new QName("http://java.sun.com/xml/ns/javaee", "webservices");

    marshaller = new XMLMarshaller();
    //marshaller.init(getBaseMarshaller());
    
    InputStream typesXMLStream = WebServicesProcessor5.class.getResourceAsStream("/com/sap/engine/lib/descriptors5/webservices/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WebServicesProcessor5.class.getClassLoader());
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
