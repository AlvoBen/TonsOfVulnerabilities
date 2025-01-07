package com.sap.engine.lib.deploy.sda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.deploy.sda.constants.Constants;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Represents the application-j2ee-engine.xml
 * 
 * @author Radoslav Popov
 */
public class RuntimeDependencyFile extends XmlFile implements Constants {

	private static final Location location = Location
			.getLocation(RuntimeDependencyFile.class);

	private ArrayList<RuntimeDependency> runtimeDependencies;
	private String xmlFileDir;

	private static final String ROOT_ELEMENT_NAME = "application-j2ee-engine";
	private static final String REFERENCE_ELEMENT_NAME = "reference";
	private static final String REFERENCE_TARGET_ELEMENT_NAME = "reference-target";

	private static final String REFERENCE_TYPE_ATTRIBUTE = "reference-type";
	private static final String TARGET_TYPE_ATTRIBUTE = "target-type";
	private static final String VENDOR_ATTRIBUTE = "provider-name";

	public RuntimeDependencyFile(
			ArrayList<RuntimeDependency> runtimeDependencies, String xmlFileDir) {
		this.runtimeDependencies = runtimeDependencies;
		this.xmlFileDir = xmlFileDir;
	}

	/**
	 * Sets the new references to the application-j2ee-engine.xml.
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws DeployLibException
	 */
	public void create() throws ParserConfigurationException, IOException,
			TransformerException, SAXException, DeployLibException {
		File xmlFile = new File(xmlFileDir + File.separator
				+ ADD_APPLICATION_XML);
		if (xmlFile.isFile()) {
			updateXmlFile(xmlFile);
		} else {
			buildXmlFile();
		}
	}

	/**
	 * Updates the application-j2ee-engine.xml with the new runtime references.
	 * The old one should be kept.
	 * 
	 * @param xmlFile
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws DeployLibException
	 */
	private void updateXmlFile(File xmlFile) throws SAXException, IOException,
			TransformerException, DeployLibException {

		Document doc = (new StandardDOMParser()).parse(new InputSource(xmlFile
				.getAbsolutePath()));

		Element root = doc.getDocumentElement();

		NodeList referenceNodes = root.getElementsByTagName("reference");

		HashMap<RuntimeDependency, Node> existingDependencies = new HashMap<RuntimeDependency, Node>(
				referenceNodes.getLength());

		for (int i = 0; i < referenceNodes.getLength(); i++) {

			Node existingReferenceNode = referenceNodes.item(i);

			String referenceType = existingReferenceNode.getAttributes()
					.getNamedItem("reference-type").getNodeValue();

			Node referenceTargetNode = existingReferenceNode.getChildNodes()
					.item(0);
			while (Node.TEXT_NODE == referenceTargetNode.getNodeType()) {
				referenceTargetNode = referenceTargetNode.getNextSibling();
			}

			String softwareType = referenceTargetNode.getAttributes()
					.getNamedItem("target-type").getNodeValue();
			String name = referenceTargetNode.getFirstChild().getNodeValue();
			String vendor = referenceTargetNode.getAttributes().getNamedItem(
					"provider-name").getNodeValue();

			RuntimeDependency existingRuntimeDependency = new RuntimeDependency(
					softwareType, referenceType, vendor, name);

			existingDependencies.put(existingRuntimeDependency,
					existingReferenceNode);

		}

		outer: for (RuntimeDependency dependency : this.runtimeDependencies) {

			boolean replaceChild = false;
			Node existingReferenceNode = null;

			Iterator<RuntimeDependency> iterator = existingDependencies
					.keySet().iterator();

			while (iterator.hasNext()) {

				RuntimeDependency existingDependency = iterator.next();

				if (dependency.equals(existingDependency)) {
					iterator.remove();
					continue outer;
				} else if (dependency.getName().equals(
						existingDependency.getName())
						&& dependency.getVendor().equals(
								existingDependency.getVendor())) {
					replaceChild = true;
					existingReferenceNode = existingDependencies
							.get(existingDependency);
					break;
				}

			}

			Element referenceEl = createReferenceElement(doc, dependency);

			if (replaceChild) {
				root.replaceChild(referenceEl, existingReferenceNode);
			} else {
				root.appendChild(referenceEl);
			}

		}

		save(xmlFileDir + File.separator + ADD_APPLICATION_XML, root);

	}

	/**
	 * Creates a new application-j2ee-engine.xml and sets the runtime references
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 */
	private void buildXmlFile() throws ParserConfigurationException,
			IOException, TransformerException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element root = doc.createElement(ROOT_ELEMENT_NAME);
			doc.appendChild(root);

			for (RuntimeDependency dependency : this.runtimeDependencies) {

				Element referenceEl = createReferenceElement(doc, dependency);

				root.appendChild(referenceEl);

			}

			save(xmlFileDir + File.separator + ADD_APPLICATION_XML, root);

		} catch (ParserConfigurationException pce) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR, "Could not create "
					+ ADD_APPLICATION_XML + " file due to " + pce.getMessage(),
					pce);
			throw pce;
		}

	}

	/**
	 * Creates an org.w3c.dom.Element object which represents the reference tag
	 * in the application-j2ee-engine xml
	 * 
	 * @param doc
	 *            the Document object of the xml
	 * @param dependency
	 *            the RuntimeDependency object from which the element should be
	 *            created
	 * @return referenceEl
	 */
	private Element createReferenceElement(Document doc,
			RuntimeDependency dependency) {

		Element referenceEl = doc.createElement(REFERENCE_ELEMENT_NAME);
		referenceEl.setAttribute(REFERENCE_TYPE_ATTRIBUTE, dependency
				.getReferenceType());

		Element referenceTargetEl = doc
				.createElement(REFERENCE_TARGET_ELEMENT_NAME);
		referenceTargetEl
				.setAttribute(VENDOR_ATTRIBUTE, dependency.getVendor());
		referenceTargetEl.setAttribute(TARGET_TYPE_ATTRIBUTE, dependency
				.getSoftwareType());

		Text text = doc.createTextNode(dependency.getName());
		referenceTargetEl.appendChild(text);

		referenceEl.appendChild(referenceTargetEl);
		return referenceEl;

	}

}
