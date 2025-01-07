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
import com.sap.engine.lib.descriptors.ejb.EjbJarType;


/**
 *
 *
 * @author 	Viktoriya Ivanova
 */

public class EjbProcessor extends SchemaProcessor {

  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;
 
  private static final EjbProcessor singleton;
  private static final String[] schema_sources;
  
  	public static EjbProcessor getInstance() {
  		return singleton;
  	}

  	static {
  		schema_sources = new String[] {"ejb-jar_2_1.xsd"};
  		singleton = new EjbProcessor();
  	}
  	

	private EjbProcessor() {
		super(schema_sources);
		rootClass = EjbJarType.class;
		rootElement = new QName("http://java.sun.com/xml/ns/j2ee", "ejb-jar");

		marshaller = new XMLMarshaller();
        marshaller.init(getBaseMarshaller());
        /*
    InputStream typesXMLStream = EjbProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/ejb/frm/types.xml");
		try {
			marshaller.init(typesXMLStream, EjbProcessor.class.getClassLoader());
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
