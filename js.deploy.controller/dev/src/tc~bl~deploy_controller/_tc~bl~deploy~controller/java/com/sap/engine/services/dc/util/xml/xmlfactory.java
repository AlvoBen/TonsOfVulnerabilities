package com.sap.engine.services.dc.util.xml;

import java.io.InputStream;

import org.w3c.dom.Document;

public abstract class XMLFactory {

	public final String SCHEMA_LANGUAGE_URL = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public final String SCHEMA_SOURCE_URL = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	public final String XML_SCHEMA_URL = "http://www.w3.org/2001/XMLSchema";

	private static XMLFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.util.xml.impl.XMLFactoryImpl";

	protected XMLFactory() {
	}

	public static synchronized XMLFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static XMLFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (XMLFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "[ERROR CODE DPL.DC.X] An error occurred while creating an instance of "
					+ "class XMLFactory: " + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Document createDocument(final String xmlFilePath)
			throws Exception;

	public abstract Document createDocument(final String xmlFilePath,
			final String xsdFilePath) throws Exception;

	public abstract Document createDocument(final InputStream xmlInsputStream)
			throws Exception;

	public abstract Document createDocument(final InputStream xmlInsputStream,
			final InputStream xsdInsputStream) throws Exception;

}
