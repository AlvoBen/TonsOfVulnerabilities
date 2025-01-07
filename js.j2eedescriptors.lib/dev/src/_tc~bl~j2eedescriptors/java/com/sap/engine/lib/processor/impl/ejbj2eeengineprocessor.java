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
import com.sap.engine.lib.descriptors.ejbj2eeengine.EjbJ2EeEngine;

/**
 *
 *
 * @author 	Viktoriya Ivanova
 */

public class EjbJ2EEEngineProcessor extends SchemaProcessor {
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final EjbJ2EEEngineProcessor singleton;;
  private static final String[] schema_sources;
  
  static {
  	schema_sources = new String[] {"ejb-j2ee-engine.xsd"};
  	singleton = new EjbJ2EEEngineProcessor();
  }

  public static EjbJ2EEEngineProcessor getInstance() {
    return singleton;
  }


	private EjbJ2EEEngineProcessor() {
		super(schema_sources);
		rootClass = EjbJ2EeEngine.class;
		rootElement = new QName(null, "ejb-j2ee-engine");

		marshaller = new XMLMarshaller();
        
    InputStream typesXMLStream = EjbJ2EEEngineProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ejbj2eeengine/frm/types.xml");
		try {
			marshaller.init(typesXMLStream, EjbJ2EEEngineProcessor.class.getClassLoader());
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
