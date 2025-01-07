/*
 * Created on 2005-5-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.ws04wsrt.WSRuntimeDescriptor;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WS04WSRTProcessor extends SchemaProcessor {
  
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WS04WSRTProcessor singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"ws-runtime-descriptor.xsd"};
    singleton = new WS04WSRTProcessor();
  }


  public static WS04WSRTProcessor getInstance() {
    return singleton;
  }


  private WS04WSRTProcessor() {
    super(schema_sources);
    rootClass = WSRuntimeDescriptor.class;
    
    //TODO change
    rootElement = new QName("http://www.sap.com/webas/630/soap/java/descriptors/ws-runtime-descriptor", "ws-runtime-descriptor");

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ws04wsrt/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WS04WSRTProcessor.class.getClassLoader());
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
