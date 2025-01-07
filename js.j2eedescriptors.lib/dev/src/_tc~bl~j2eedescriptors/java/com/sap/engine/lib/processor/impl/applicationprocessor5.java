package com.sap.engine.lib.processor.impl;

import java.io.*;
import javax.xml.namespace.*;

import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.descriptors5.application.ApplicationType;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;


public class ApplicationProcessor5 extends SchemaProcessor {
	private static Class rootClass;
 	private static QName rootElement;
 	private static XMLMarshaller marshaller;

	private static final ApplicationProcessor5 singleton;
	private static final String[] schema_sources;

	static {
		schema_sources = new String[] {"application_5.xsd"};
		singleton = new ApplicationProcessor5();
	}

	public static ApplicationProcessor5 getInstance() {
		return singleton;
	}

	private ApplicationProcessor5() {
		super(schema_sources);
		rootClass = ApplicationType.class;
		rootElement = new QName("http://java.sun.com/xml/ns/javaee", "application");

		marshaller = new XMLMarshaller();
		InputStream typesXMLStream = ApplicationProcessor5.class.getResourceAsStream("/com/sap/engine/lib/descriptors5/application/frm/types.xml");
		
		try {
			marshaller.init(typesXMLStream, ApplicationProcessor5.class.getClassLoader());
		} catch (TypeMappingException ex) {
			throw new AssertionError(ex);
		}

		try {
			setNamespaceReplacement("http://java.sun.com/xml/ns/j2ee", "http://java.sun.com/xml/ns/javaee");
		} catch (Exception e) {
			throw new AssertionError(e);
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
