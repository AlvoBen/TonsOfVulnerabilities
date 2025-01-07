/*
 * $Id$
 *
 * Copyright (c) 200x by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.processor.impl;


import java.io.*;
import javax.xml.namespace.*;

import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.descriptors.jaxrpcmapping.JavaWsdlMappingType;
import com.sap.engine.lib.descriptors.web.WebAppType;


/**
 *
 *
 * @author  Vladimir Savchenko
 */

public class JaxRpcMappingProcessor extends SchemaProcessor {
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final JaxRpcMappingProcessor singleton;
  private static final String[] schema_sources;
  
  static {
    schema_sources = new String[] {"j2ee_jaxrpc_mapping_1_1.xsd"};
    singleton = new JaxRpcMappingProcessor();
  }


  public static JaxRpcMappingProcessor getInstance() {
    return singleton;
  }


  private JaxRpcMappingProcessor() {
    super(schema_sources);
    rootClass = JavaWsdlMappingType.class;
    rootElement = new QName("http://java.sun.com/xml/ns/j2ee", "java-wsdl-mapping");

    marshaller = new XMLMarshaller();
    marshaller.init(getBaseMarshaller());
    /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/jaxrpcmapping/frm/types.xml");
    try {
      marshaller.init(typesXMLStream, JaxRpcMappingProcessor.class.getClassLoader());
    } catch (TypeMappingException ex) {
      throw new AssertionError(ex);
    }*/
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
