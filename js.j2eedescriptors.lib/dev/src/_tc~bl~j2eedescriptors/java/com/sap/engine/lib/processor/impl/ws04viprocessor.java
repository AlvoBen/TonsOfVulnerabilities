/*
 * Created on 2005-5-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.ws04vi.VirtualInterfaceState;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WS04VIProcessor extends SchemaProcessor {
  
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WS04VIProcessor singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"VirtualInterface.xsd"};
    singleton = new WS04VIProcessor();
  }


  public static WS04VIProcessor getInstance() {
    return singleton;
  }


  private WS04VIProcessor() {
    super(schema_sources);
    rootClass = VirtualInterfaceState.class;
    
    //TODO change
    rootElement = new QName("http://xml.sap.com/2002/10/metamodel/vi", "VirtualInterface");

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ws04vi/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, WS04VIProcessor.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }*/
    switchOffValidation();
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
