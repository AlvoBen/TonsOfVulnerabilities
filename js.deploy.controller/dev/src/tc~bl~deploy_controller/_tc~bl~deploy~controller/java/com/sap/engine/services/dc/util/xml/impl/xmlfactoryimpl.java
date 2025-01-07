package com.sap.engine.services.dc.util.xml.impl;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sap.engine.services.dc.util.xml.XMLFactory;

public class XMLFactoryImpl extends XMLFactory {

	@Override
	public Document createDocument(final String xmlFilePath) throws Exception {
		return this.createDocument(xmlFilePath, null);
	}

	@Override
	public Document createDocument(final String xmlFilePath,
			final String xsdFilePath) throws Exception {
		if (xmlFilePath == null) {
			return null;
		}

		final DocumentBuilder docBuilder = getDocumentBuilder(xmlFilePath,
				xsdFilePath);
		return docBuilder.parse(xmlFilePath);
	}

	@Override
	public Document createDocument(final InputStream xmlInsputStream)
			throws Exception {
		return this.createDocument(xmlInsputStream, null);
	}

	@Override
	public Document createDocument(final InputStream xmlInsputStream,
			final InputStream xsdInsputStream) throws Exception {
		if (xmlInsputStream == null) {
			return null;
		}

		final DocumentBuilder docBuilder = getDocumentBuilder(xmlInsputStream,
				xsdInsputStream);
		return docBuilder.parse(xmlInsputStream);
	}

	private DocumentBuilder getDocumentBuilder(final Object xml,
			final Object xsd) throws ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		if (xsd != null) {
			docBuilderFactory.setValidating(true);
			docBuilderFactory.setAttribute(SCHEMA_LANGUAGE_URL, XML_SCHEMA_URL);
			docBuilderFactory.setAttribute(SCHEMA_SOURCE_URL, xsd);
		}

		final DocumentBuilder documentBuilder = docBuilderFactory
				.newDocumentBuilder();
		documentBuilder.setErrorHandler(new ErrorHandler() {

			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}

			public void fatalError(SAXParseException exception)
					throws SAXException {
				throw exception;
			}

			public void warning(SAXParseException exception)
					throws SAXException {
				throw exception;
			}
		});

		return documentBuilder;
	}
}
