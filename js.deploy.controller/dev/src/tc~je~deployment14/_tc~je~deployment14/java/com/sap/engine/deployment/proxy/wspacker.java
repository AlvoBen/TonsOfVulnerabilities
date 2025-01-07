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
package com.sap.engine.deployment.proxy;

import java.util.Properties;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.jar.JarUtils;
import com.sap.engine.deployment.Constants;
import com.sap.engine.deployment.Logger;

/**
 * @author Mariela Todorova
 */
public class WSPacker implements Constants {
	private static final Location location = Location
			.getLocation(WSPacker.class);
	private File archive = null;
	private Properties props = null;
	private String workDir = null;
	private String appName = null;
	private String result = null;

	public WSPacker(File arch, Properties properties, String work) throws ParserConfigurationException, TransformerException, IOException {
		Logger.trace(location, Severity.INFO, "Webservices pack for "
				+ arch.getAbsoluteFile() + " with properties " + properties);
		archive = arch;
		props = properties;
		workDir = work + sep + WS_PACK;
		appName = props.getProperty(ROOT_MODULE_NAME);

		if (appName == null || appName.equals("")) {
			appName = "webservicesApp";
		} else {
			appName = appName.substring(0, appName.indexOf(JAR));
		}

		Logger.trace(location, Severity.DEBUG, "Application name " + appName);
		result = work + sep + appName + EAR;
		pack();
		changeProps();
	}

	private void pack() throws ParserConfigurationException, TransformerException, IOException {
		try {
			FileUtils.copy(archive, workDir + sep + archive.getName());
			File manifest = new File(workDir + sep + META_INF, MANIFEST);
			manifest.getParentFile().mkdirs();
			manifest.createNewFile();
			buildApplicationXML();
			String ref = props.getProperty(REFERENCE);

			if (ref != null && !ref.equals("")) {
				buildAdditionalAppXML();
			}

			JarUtils utils = new JarUtils();
			utils.setCompressMethod(ZipEntry.DEFLATED);
			utils.makeJarFromDir(result, workDir);
		} catch (IOException ioe) {// $JL-EXC$
			Logger
					.logThrowable(
							location,
							Severity.ERROR,
							"Could not pack ear file from standalone EJB jar for webservices ",
							new String[] { archive.getAbsolutePath() }, ioe);
			throw ioe;
		}
	}

	// create application.xml
	private void buildApplicationXML() throws ParserConfigurationException, TransformerException {
		Logger.trace(location, Severity.PATH, "Creating application.xml");

		try {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fact.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElementNS(
					"http://java.sun.com/xml/ns/j2ee", "application");
			root.setAttribute("version", "1.4");
			root
					.setAttributeNS(
							"http://www.w3.org/2001/XMLSchema-instance",
							"xsi:schemaLocation",
							"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/application_1_4.xsd");
			// create elements
			doc.appendChild(root);
			createTextNode(doc, root, "http://java.sun.com/xml/ns/j2ee",
					"description", "Application description");
			createTextNode(doc, root, "http://java.sun.com/xml/ns/j2ee",
					DISPLAY_NAME, appName);
			Element mod = createElement(doc, root,
					"http://java.sun.com/xml/ns/j2ee", "module");
			createTextNode(doc, mod, "http://java.sun.com/xml/ns/j2ee", "ejb",
					archive.getName());
			// create file & save it
			File xml = new File(workDir + sep + META_INF, APPLICATION_XML);
			xml.getParentFile().mkdirs();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(root), new StreamResult(xml));
		} catch (ParserConfigurationException pce) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR,
					"Could not create application.xml", pce);
			throw pce;
		} catch (TransformerException te) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR,
					"Could not create application.xml", te);
			throw te;
		}
	}

	// create application-j2ee-engine.xml
	private void buildAdditionalAppXML() throws ParserConfigurationException, TransformerException {
		Logger.trace(location, Severity.PATH,
				"Creating application-j2ee-engine.xml");
		try {
			String ref = props.getProperty(REFERENCE);
			String target = ref.substring(0, ref.indexOf(" "));
			String targetType = ref.substring(ref.indexOf(" ") + 1, ref
					.lastIndexOf(" "));
			String refType = ref.substring(ref.lastIndexOf(" ") + 1);
			// build xml
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fact.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("application-j2ee-engine");
			doc.appendChild(root);
			Element reference = createElement(doc, root, null, "reference");
			createAttribute(doc, reference, "reference-type", refType);
			Element refTarget = createTextNode(doc, reference, null,
					"reference-target", target);
			createAttribute(doc, refTarget, "target-type", targetType);
			// create file & save it
			File xml = new File(workDir + sep + META_INF, ADD_APPLICATION_XML);
			xml.getParentFile().mkdirs();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(root), new StreamResult(xml));
		} catch (ParserConfigurationException pce) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR,
					"Could not create application-j2ee-engine.xml", pce);
			throw pce;
		} catch (TransformerException te) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR,
					"Could not create application-j2ee-engine.xml", te);
			throw te;
		}
	}

	private Element createElement(Document doc, Element parent, String ns,
			String tag) {
		Element temp = null;

		if (ns == null) {
			temp = doc.createElement(tag);
		} else {
			temp = doc.createElementNS(ns, tag);
		}

		parent.appendChild(temp);
		return temp;
	}

	private Element createTextNode(Document doc, Element parent, String ns,
			String tag, String value) {
		Element temp = null;

		if (ns == null) {
			temp = doc.createElement(tag);
		} else {
			temp = doc.createElementNS(ns, tag);
		}

		temp.appendChild(doc.createTextNode(value));
		parent.appendChild(temp);
		return temp;
	}

	private void createAttribute(Document doc, Element owner, String name,
			String value) {
		Attr attr = doc.createAttribute(name);
		attr.setValue(value);
		owner.setAttributeNode(attr);
	}

	private void changeProps() {
		Logger.trace(location, Severity.PATH, "Changing properties");
		props.setProperty(STAND_ALONE, FALSE);
		props.setProperty(ROOT_MODULE_NAME, appName);
		props.remove(REFERENCE);
		props.remove(WEBSERVICES_PACK);
	}

	public String getResultArchive() {
		return result;
	}

}
