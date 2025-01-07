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
import com.sap.engine.lib.descriptors.persistent.PersistentEjbMap;


/**
 *
 *
 * @author 	Viktoriya Ivanova
 */

public class PersistentProcessor extends SchemaProcessor {
  private static Class rootClass;
  private static QName rootElement;
  private static XMLMarshaller marshaller;

  private static final PersistentProcessor singleton;
  private final static String[] schema_sources;
  
  static {
  	schema_sources = new String[] {"persistent.xsd"};
  	singleton = new PersistentProcessor();
  }

  public static PersistentProcessor getInstance() {
  	return singleton;
  }

	private PersistentProcessor() {
		super(schema_sources);
		rootClass = PersistentEjbMap.class;
		rootElement = new QName(null, "persistent-ejb-map");
		marshaller = new XMLMarshaller();
        marshaller.init(getBaseMarshaller());
        /*
    InputStream typesXMLStream = PersistentProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/persistent/frm/types.xml");
		try {
			marshaller.init(typesXMLStream, PersistentProcessor.class.getClassLoader());
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
