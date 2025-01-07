/*
 * Created on 2005-5-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.ws04clientsrt.WSClientsRuntimeDescriptor;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WS04ClientRTProcessor extends SchemaProcessor {
  
  private static Class rootClass;
  public static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WS04ClientRTProcessor singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"ws-clients-runtime-descriptor.xsd"};
    rootElement = new QName("http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-runtime-descriptor", "ws-clients-runtime-descriptor");
    singleton = new WS04ClientRTProcessor();
  }


  public static WS04ClientRTProcessor getInstance() {
    return singleton;
  }


  private WS04ClientRTProcessor() {
    super(schema_sources);
    rootClass = WSClientsRuntimeDescriptor.class;
    

    marshaller = new XMLMarshaller();   
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ws04clientsrt/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WS04ClientRTProcessor.class.getClassLoader());
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
}
