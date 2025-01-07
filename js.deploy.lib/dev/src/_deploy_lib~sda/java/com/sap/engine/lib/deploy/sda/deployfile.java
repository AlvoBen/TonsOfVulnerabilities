/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.deploy.sda;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class DeployFile extends XmlFile {
	private static final Location location = Location
			.getLocation(DeployFile.class);
	private static final String SDA = "SDA";
	private static final String STYPE = "SoftwareType";
	private static final String EDD = "engine-deployment-descriptor";
	private static final String VERSION = "version";
	private static final String VN = "2.0";

	private SoftwareType type = null;
	private Document doc = null;
	private Element rootEl = null;

	public DeployFile(SoftwareType sType) {
		Logger.trace(location, Severity.INFO,
				"Creating deploy file for software type " + sType);
		type = sType;
	}

	private void build() throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			rootEl = doc.createElement(SDA);
			doc.appendChild(rootEl);
			createTextNode(rootEl, STYPE, type.getValue());
			Element el = createElement(rootEl, EDD);
			el.setAttribute(VERSION, VN);
		} catch (ParserConfigurationException pce) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR,
					"Could not create deploy file due to " + pce.getMessage(),
					pce);
			throw pce;
		}
	}

	private Element createElement(Element parent, String tag) {
		Element temp = doc.createElement(tag);
		parent.appendChild(temp);
		return temp;
	}

	private void createTextNode(Element parent, String tag, String value) {
		Element temp = doc.createElement(tag);
		temp.appendChild(doc.createTextNode(value));
		parent.appendChild(temp);
	}

	public void make(String file) throws ParserConfigurationException,
			IOException, TransformerException {
		build();
		save(file, rootEl);
	}

}
