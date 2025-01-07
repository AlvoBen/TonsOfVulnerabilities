/*
 * Created on 2005-5-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.processor.SchemaProcessorFactory;
import com.sap.engine.lib.descriptors.ws04clientsdd.*;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WS04ClientDDProcessor extends SchemaProcessor {
  
  private static Class rootClass;
  public static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WS04ClientDDProcessor singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"ws-clients-deployment-descriptor.xsd"};
    rootElement = new QName("http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor", "ws-clients-deployment-descriptor");
    singleton = new WS04ClientDDProcessor();
  }


  public static WS04ClientDDProcessor getInstance() {
    return singleton;
  }


  private WS04ClientDDProcessor() {
    super(schema_sources);
    rootClass = WSClientDeploymentDescriptor.class;

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ws04clientsdd/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WS04ClientDDProcessor.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }*/
    
    switchOffValidation();
    marshaller.setIgnoreXsiType(true);
    setStreamingDeserialization(true);
    
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
  
  public static void main(String[]args) throws Exception {
    System.out.println("0");
    SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WS04CLIENTDD).parse("e:/temp/ws-clients-deployment-descriptor_1.xml");
    System.out.println("1");
    SchemaProcessorFactory.getProcessor(SchemaProcessorFactory.WS04WSDD).parse("e:/temp/ws-deployment-descriptor.xml");
    System.out.println("2");
    
    
  }
}
