/*
 * Created on 2005-3-23
 *
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors.appclient.ApplicationClientType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author georgi-g
 */
public class AppclientProcessor extends SchemaProcessor {

	private static Class rootClass;
	private static QName rootElement;
	private static XMLMarshaller marshaller;

	private static final AppclientProcessor singleton;
	private static final String[] schema_sources;
	
	static {
		schema_sources = new String[] {"application-client_1_4.xsd"};
		singleton = new AppclientProcessor();
	}


	public static AppclientProcessor getInstance() {
		return singleton;
	}


	private AppclientProcessor() {
		super(schema_sources);
		rootClass = ApplicationClientType.class;
		rootElement = new QName("http://java.sun.com/xml/ns/j2ee", "application-client");
		marshaller = new XMLMarshaller();
        marshaller.init(getBaseMarshaller());
        /*
		InputStream typesXMLStream = AppclientProcessor.class.getResourceAsStream("/com/sap/engine/lib/descriptors/appclient/frm/types.xml");
		try {
			marshaller.init(typesXMLStream, AppclientProcessor.class.getClassLoader());
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
