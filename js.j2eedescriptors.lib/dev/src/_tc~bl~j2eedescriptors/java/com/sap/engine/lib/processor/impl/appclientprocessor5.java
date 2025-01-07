/*
 * Created on 2005-3-23
 *
 */
package com.sap.engine.lib.processor.impl;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sap.engine.lib.descriptors5.appclient.ApplicationClientType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLMarshaller;
import com.sap.engine.services.webservices.jaxrpc.exceptions.TypeMappingException;

/**
 * @author i031222
 */
public class AppclientProcessor5 extends SchemaProcessor {

	private static Class rootClass;
	private static QName rootElement;
	private static XMLMarshaller marshaller;

	private static final AppclientProcessor5 singleton;
	private static final String[] schema_sources;
	
	static {
		schema_sources = new String[] {"application-client_5.xsd"};
		singleton = new AppclientProcessor5();
	}


	public static AppclientProcessor5 getInstance() {
		return singleton;
	}

	private AppclientProcessor5() {
		super(schema_sources);
		rootClass = ApplicationClientType.class;
		rootElement = new QName("http://java.sun.com/xml/ns/javaee", "application-client");
		marshaller = new XMLMarshaller();
//        marshaller.init(getBaseMarshaller());
        
        InputStream typesXMLStream = AppclientProcessor5.class.getResourceAsStream("/com/sap/engine/lib/descriptors5/appclient/frm/types.xml");
        try {
          marshaller.init(typesXMLStream, AppclientProcessor5.class.getClassLoader());
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
