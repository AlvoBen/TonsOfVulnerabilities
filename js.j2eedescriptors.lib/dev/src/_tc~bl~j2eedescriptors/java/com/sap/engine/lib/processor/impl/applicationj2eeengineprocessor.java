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
import com.sap.engine.lib.descriptors.applicationj2eeengine.ApplicationJ2EeEngine;


/**
 *
 *
 * @author 	Viktoriya Ivanova
 */

public class ApplicationJ2EEEngineProcessor extends SchemaProcessor {

	private static Class rootClass;
	private static QName rootElement;
	private static XMLMarshaller marshaller;

	private static final ApplicationJ2EEEngineProcessor singleton;
	private static final String[] schema_sources;

	static {
		schema_sources = new String[] {"application-j2ee-engine.xsd"};
		singleton = new ApplicationJ2EEEngineProcessor();
	}
	
	public static ApplicationJ2EEEngineProcessor getInstance() {
		return singleton;
	}


	private ApplicationJ2EEEngineProcessor() {
		super(schema_sources);
		rootClass = ApplicationJ2EeEngine.class;
		rootElement = new QName(null, "application-j2ee-engine");

		marshaller = new XMLMarshaller();        
    InputStream typesXMLStream = ApplicationJ2EEEngineProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/applicationj2eeengine/frm/types.xml");
		try {
		  marshaller.init(typesXMLStream, ApplicationJ2EEEngineProcessor.class.getClassLoader());
		} catch (TypeMappingException tme) {
			throw new AssertionError(tme);
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
