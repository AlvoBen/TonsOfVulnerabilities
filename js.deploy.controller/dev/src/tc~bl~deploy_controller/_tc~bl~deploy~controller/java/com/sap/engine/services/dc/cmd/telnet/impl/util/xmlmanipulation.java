/*
 * Created on 2004-9-23
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.dc.cmd.telnet.impl.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * 
 */
public class XMLManipulation {
	private static SAXParserFactory factory = SAXParserFactory.newInstance();

	public static void doIt(InputSource inputSource,
			ContentHandler contentHandler, boolean validating)
			throws ParserConfigurationException, IOException, SAXException {

		factory.setValidating(validating);
		SAXParser saxParser = factory.newSAXParser();
		// get XML Reader
		XMLReader xmlReader = saxParser.getXMLReader();
		// xmlReader.setErrorHandler( new InternalErrorHandler(cmdLog) );
		// Set the ContentHandler of the XMLReader.
		xmlReader.setContentHandler(contentHandler);
		// Tell the XMLReader to parse the XML document.
		xmlReader.parse(inputSource);
	}
}
