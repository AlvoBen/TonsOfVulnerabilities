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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sap.engine.lib.deploy.sda.constants.Constants;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.engine.lib.deploy.sda.propertiesholder.PropertiesHolder;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class AttributesDeterminant implements Constants {
	private static final Location location = Location
			.getLocation(AttributesDeterminant.class);
	private static String defaultVendor = "JavaEE";
	private static String defaultLocation = "Deployment Manager";
	private static String defaultCounter = "1";

	private String fileName = null;
	private String workDir = null;

	public AttributesDeterminant(String file, String dir) {
		initDefaultValues();
		Logger.trace(location, Severity.DEBUG,
				"Determining SDA attributes for " + file);
		fileName = file;
		workDir = dir;
	}

	private static synchronized void initDefaultValues() {

		String attrFile = PropertiesHolder
				.getProperty(PropertiesHolder.SDA_ATTRIBUTES);

		if (attrFile != null && !attrFile.equals("")) {
			File propsFile = new File(attrFile);

			if (!propsFile.isFile() || !propsFile.canRead()) {
				Logger.log(location, Severity.WARNING,
						"Could not find or read file " + attrFile);
				Logger.log(location, Severity.INFO,
						"Default SDA attributes will be used");
			} else {
				try {
					Properties props = new Properties();
					FileInputStream inStream = new FileInputStream(propsFile);
					props.load(inStream);

					// only these attributes can be default for all SDA files
					defaultVendor = props.getProperty(VENDOR);
					Logger.trace(location, Severity.DEBUG,
							"Default vendor changed to " + defaultVendor);
					defaultLocation = props.getProperty(LOCATION);
					Logger.trace(location, Severity.DEBUG,
							"Default location changed to " + defaultLocation);
					defaultCounter = props.getProperty(COUNTER);
					Logger.trace(location, Severity.DEBUG,
							"Default counter changed to " + defaultCounter);
				} catch (IOException ioe) {// $JL-EXC$
					Logger.log(location, Severity.ERROR,
							"Could not load properties due to "
									+ ioe.getMessage());
					Logger.log(location, Severity.INFO,
							"Default SDA attributes will be used");
				}
			}
		}

	}

	public SoftwareType determineType() {
		SoftwareType type = null;

		if (fileName.toLowerCase(Locale.ENGLISH).endsWith(EAR)) {
			Logger.trace(location, Severity.DEBUG, "File name ends with .ear");
			type = SoftwareType.J2EE;
		} else if (fileName.toLowerCase(Locale.ENGLISH).endsWith(JAR)
				|| fileName.toLowerCase(Locale.ENGLISH).endsWith(WAR)) {
			type = SoftwareType.SINGLE_MODULE;
		} else if (fileName.toLowerCase(Locale.ENGLISH).endsWith(RAR)) {
			if (new File(workDir + sep + META_INF + sep + RA_XML).isFile()) {
				type = SoftwareType.SINGLE_MODULE;
				Logger.trace(location, Severity.DEBUG,
						"RAR file contains META-INF/ra.xml");
			}
		}

		if (type == null) {
			type = SoftwareType.J2EE;
			Logger
					.trace(location, Severity.DEBUG,
							"Determination rules cannot be applied - setting default type: J2EE");
		}

		return type;
	}

	public SoftwareSubType determineSubType(SoftwareType type) {
		SoftwareSubType subType = null;

		if (SoftwareType.SINGLE_MODULE.equals(type)) {
			if (fileName.toLowerCase(Locale.ENGLISH).endsWith(JAR)) {
				Logger.trace(location, Severity.DEBUG,
						"File name ends with .jar");
				subType = SoftwareSubType.JAR;
			} else if (fileName.toLowerCase(Locale.ENGLISH).endsWith(WAR)) {
				Logger.trace(location, Severity.DEBUG,
						"File name ends with .war");
				subType = SoftwareSubType.WAR;
			} else if (fileName.toLowerCase(Locale.ENGLISH).endsWith(RAR)) {
				Logger.trace(location, Severity.DEBUG,
						"File name ends with .rar");

				if (new File(workDir + sep + META_INF + sep + RA_XML).isFile()) {
					subType = SoftwareSubType.RAR;
					Logger.trace(location, Severity.DEBUG,
							"RAR file contains META-INF/ra.xml");
				}

			}
		}

		return subType;
	}

	/**
	 * Returns the node value of the first child of the first tag found by given
	 * tag name in a given xml file name
	 * 
	 * @param xmlFilename
	 *            the xml to be read
	 * @param tagName
	 *            the tag name that have to be searched
	 * @return String the node value
	 * @throws SAXException
	 *             on failure to read the xml document
	 * @throws IOException
	 *             on failure to initialize the parser
	 * 
	 * [todo] Revisit the behaviour when exception is thrown in the methods that
	 * call getXMLNodeFirstChildValue
	 */
	private String getXMLNodeFirstChildValue(String xmlFilename, String tagName)
			throws SAXException, IOException {

		FileInputStream fis = new FileInputStream(xmlFilename);
		try {
			Document xmlDoc = new StandardDOMParser().parse(fis);

			Element el = xmlDoc.getDocumentElement();
			NodeList nodes = el.getElementsByTagName(tagName);
			Node node = null;

			if (nodes != null && nodes.getLength() > 0 && nodes.item(0) != null) {
				node = nodes.item(0).getFirstChild();

				if (node != null) {
					return node.getNodeValue();
				}
			}

			return null;
		} finally {
			if (null != fis) {
				fis.close();
			}
		}

	}

	public String determineName(SoftwareType fileType) throws SAXException,
			IOException {
		String name = null;

		if (fileType.equals(SoftwareType.SINGLE_MODULE)) {
			// stand-alone module -> what name?
			name = fileName.substring(0, fileName.lastIndexOf('.'));
		} else if (fileType.equals(SoftwareType.J2EE)) {
			String applicationXML = workDir + sep + META_INF + sep
					+ APPLICATION_XML;
			File xml = new File(applicationXML);

			// check if application.xml exists
			if (xml.isFile() && xml.canRead()) {
				try {
					name = this.getXMLNodeFirstChildValue(applicationXML,
							DISPLAY_NAME);
				} catch (SAXException saxe) {// $JL-EXC$
					Logger.logThrowable(location, Severity.WARNING,
							"Error occurred while reading " + applicationXML
									+ "\n " + saxe.getMessage(), saxe);
					throw saxe;
				} catch (IOException ioe) {// $JL-EXC$
					Logger.logThrowable(location, Severity.WARNING,
							"Error occurred while reading " + applicationXML,
							ioe);
					throw ioe;
				}
			}

			if (name == null || name.equals("")) {
				 if (fileName.contains(".")){
					 name = fileName.substring(0, fileName.lastIndexOf('.'));
				 } else name = fileName;
			}
		} else if (fileType.equals(SoftwareType.LIBRARY)
				|| fileType.equals(SoftwareType.PRIMARY_LIBRARY)
				|| fileType.equals(SoftwareType.PRIMARY_SERVICE)
				|| fileType.equals(SoftwareType.PRIMARY_INTERFACE)) {

			String providerXML = workDir + sep + SERVER + sep + PROVIDER_XML;
			File xml = new File(providerXML);

			if (!xml.isFile() || !xml.canRead()) {
				providerXML = workDir + sep + DISPATCHER + sep + PROVIDER_XML;
				xml = new File(providerXML);
			}

			if (xml.isFile() && xml.canRead()) {
				try {
					name = this.getXMLNodeFirstChildValue(providerXML,
							COMPONENT_NAME);
				} catch (SAXException saxe) {// $JL-EXC$
					Logger
							.logThrowable(location, Severity.WARNING,
									"Error occurred while reading "
											+ providerXML, saxe);
					throw saxe;
				} catch (IOException ioe) {// $JL-EXC$
					Logger.logThrowable(location, Severity.WARNING,
							"Error occurred while reading " + providerXML, ioe);
					throw ioe;
				}
			}

			if (name == null || name.equals("")) {
				name = fileName.substring(0, fileName.lastIndexOf('.'));
			}
		}

		return name;
	}

	public String determineVendor(SoftwareType fileType) throws SAXException,
			IOException {
		String vendor = null;

		if (fileType.equals(SoftwareType.J2EE)) {
			String applicationXML = workDir + sep + META_INF + sep
					+ ADD_APPLICATION_XML;
			File xml = new File(applicationXML);

			// check if application-j2ee-engine.xml exists
			if (xml.isFile() && xml.canRead()) {
				try {
					vendor = this.getXMLNodeFirstChildValue(applicationXML,
							PROVIDER_NAME);
				} catch (SAXException saxe) {// $JL-EXC$
					Logger.logThrowable(location, Severity.WARNING,
							"Error occurred while reading " + applicationXML
									+ "\n " + saxe.getMessage(), saxe);
					throw saxe;
				} catch (IOException ioe) {// $JL-EXC$
					Logger.logThrowable(location, Severity.WARNING,
							"Error occurred while reading " + applicationXML,
							ioe);
					throw ioe;
				}
			}
		} else if (fileType.equals(SoftwareType.LIBRARY)
				|| fileType.equals(SoftwareType.PRIMARY_LIBRARY)
				|| fileType.equals(SoftwareType.PRIMARY_SERVICE)
				|| fileType.equals(SoftwareType.PRIMARY_INTERFACE)) {

			String providerXML = workDir + sep + SERVER + sep + PROVIDER_XML;
			File xml = new File(providerXML);

			if (!xml.isFile() || !xml.canRead()) {
				providerXML = workDir + sep + DISPATCHER + sep + PROVIDER_XML;
				xml = new File(providerXML);
			}

			if (xml.isFile() && xml.canRead()) {
				try {
					vendor = this.getXMLNodeFirstChildValue(providerXML,
							PROVIDER_NAME);
				} catch (SAXException saxe) {// $JL-EXC$
					Logger
							.logThrowable(location, Severity.WARNING,
									"Error occurred while reading "
											+ providerXML, saxe);
					throw saxe;
				} catch (IOException ioe) {// $JL-EXC$
					Logger.logThrowable(location, Severity.WARNING,
							"Error occurred while reading " + providerXML, ioe);
					throw ioe;
				}
			}
		}

		if (vendor == null || vendor.equals("")) {
			vendor = defaultVendor;
		}

		return vendor;
	}

	public String determineLocation() {
		return defaultLocation;
	}

	public String determineCounter() {
		return defaultCounter;
	}

}