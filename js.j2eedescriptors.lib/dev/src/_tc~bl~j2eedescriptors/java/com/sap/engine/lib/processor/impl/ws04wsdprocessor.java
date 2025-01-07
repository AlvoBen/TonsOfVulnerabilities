/*
 * Created on 2005-5-5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.ws04wsd.WebServiceDefinitionState;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WS04WSDProcessor extends SchemaProcessor {
  
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final WS04WSDProcessor singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"WebServiceDefinition.xsd"};
    singleton = new WS04WSDProcessor();
  }


  public static WS04WSDProcessor getInstance() {
    return singleton;
  }


  private WS04WSDProcessor() {
    super(schema_sources);
    rootClass = WebServiceDefinitionState.class;
    
    //TODO change
    rootElement = new QName("http://xml.sap.com/2002/10/metamodel/wsd", "WebServiceDefinition");

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ws04wsd/frm/types.xml");
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
