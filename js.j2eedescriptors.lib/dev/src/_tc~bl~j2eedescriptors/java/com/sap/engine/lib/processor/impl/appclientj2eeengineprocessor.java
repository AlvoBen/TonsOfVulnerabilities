/*
 * Created on 2005-3-23
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.appclientj2eeengine.AppclientJ2EeEngine;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author georgi-g
 */
public class AppclientJ2EEEngineProcessor extends SchemaProcessor {

	private static Class rootClass;
	private static QName rootElement;
	private static XMLMarshaller marshaller;

	private static final AppclientJ2EEEngineProcessor singleton;
	private static final String[] schema_sources;

	static {
		schema_sources = new String[] {"appclient-j2ee-engine.xsd"};
		singleton = new AppclientJ2EEEngineProcessor();
	}
	
	public static AppclientJ2EEEngineProcessor getInstance() {
		return singleton;
	}


	private AppclientJ2EEEngineProcessor() {
		super(schema_sources);
		rootClass = AppclientJ2EeEngine.class;
		rootElement = new QName(null, "appclient-j2ee-engine");
        // The processor uses type from the 
		marshaller = new XMLMarshaller();
        marshaller.init(getBaseMarshaller());
        /*
		InputStream typesXMLStream = AppclientJ2EEEngineProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/appclientj2eeengine/frm/types.xml");
		try {
		  marshaller.init(typesXMLStream, AppclientJ2EEEngineProcessor.class.getClassLoader());
		} catch (TypeMappingException tme) {
			throw new AssertionError(tme);
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
