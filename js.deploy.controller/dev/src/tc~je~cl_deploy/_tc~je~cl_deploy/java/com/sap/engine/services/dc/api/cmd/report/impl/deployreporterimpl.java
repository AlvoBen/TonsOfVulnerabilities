/* 
 * Created on Feb 21, 2007
 */
package com.sap.engine.services.dc.api.cmd.report.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sap.engine.services.dc.api.cmd.report.DeployReporter;
import com.sap.engine.services.dc.api.cmd.report.ReporterException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployItemStatus;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;
import com.sap.engine.services.dc.api.event.DeploymentEvent;
import com.sap.engine.services.dc.api.event.DeploymentListener;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry;

/**
 * Title: | Description: |
 * 
 * Copyright (c) 2007, SAP-AG Date: Feb 21, 2007
 * 
 * @author Daniel Hristov
 * @version 1.0
 */

public class DeployReporterImpl implements DeployReporter, DeploymentListener {

	private static final String ELEMENT_NAME_DOCROOT = "deploy_results";

	private static final String ELEMENT_NAME_DEPLOY_BATCH = "deploy_batch";

	private static final String ELEMENT_NAME_DEPLOY_ITEM = "deploy_item";

	private static final String ELEMENT_NAME_SCA_ITEM = "Sca";

	private static final String ELEMENT_NAME_SDA_ITEM = "Sda";

	private static final String ELEMENT_NAME_TIME_ENTRY = "time_entry";

	private static final String DEPLOY_BATCH_STATUS = "status";

	private static final String DEPLOY_BATCH_STATUS_DESCRIPTION = "status_description";

	private static final String DEPLOY_BATCH_DEPLOYABLES_ID = "components_id";

	private static final String DEPLOY_ITEM_NAME = "name";

	private static final String DEPLOY_ITEM_VENDOR = "vendor";

	private static final String DEPLOY_ITEM_VERSION = "version";

	private static final String DEPLOY_ITEM_STATUS = "status";

	private static final String DEPLOY_ITEM_DESCRIPTION = "description";

	private static final String DEPLOY_ITEM_FILE = "file";

	private static final String TIME_STAT_TYPE_STRING = "type";

	private static final String TIME_STAT_START_TIME = "start_time";

	private static final String TIME_STAT_FINISH_TIME = "finish_time";

	private static final String TIME_STAT_DURATION = "duration";

	private static final String REPORT_FILE_NAME = "DeployStatus.xml";

	public void deploymentPerformed(DeploymentEvent event) {
		/* execute action on deployment performed */
		return;
	}

	public DeploymentListener getDeploymentListener() {
		return this;
	}

	public void processDeployItems(DeployItem[] items,
			DeployResultStatus status, String statusDescription)
			throws ReporterException {
		Document document = loadDocument();

		Element batchElement = createBatchElement(document);
		writeBatchStatus(status, statusDescription, batchElement);
		getRootElement(document).appendChild(batchElement);

		addItemsToBatch(items, document, batchElement);

		String listID = getItemListID(items);

		batchElement.setAttribute(DEPLOY_BATCH_DEPLOYABLES_ID, listID);

		storeDocument(document);
	}

	/**
	 * Adds the passed items and their one level deep contained items as
	 * subelements of the passed batchElement
	 * 
	 * @param toplevelItems
	 * @param document
	 * @param batchElement
	 */
	private void addItemsToBatch(DeployItem[] toplevelItems, Document document,
			Element batchElement) {
		/* add the results from deployment */
		for (DeployItem toplevelDeployItem : toplevelItems) {
			Element toplevelItemElement = addItemElement(document,
					batchElement, toplevelDeployItem);

			DeployItem[] containedItems = toplevelDeployItem
					.getContainedDeployItems();
			if (containedItems != null) {
				for (DeployItem item : containedItems) {
					addItemElement(document, toplevelItemElement, item);
				}
			}
		}
	}

	/**
	 * Returns string id that is kept the same for a given list of components
	 * 
	 * @param items
	 * @return
	 */
	private String getItemListID(DeployItem[] items) {
		List<String> idList = new ArrayList<String>();
		for (DeployItem item : items) {
			String componentName = "Unknown";
			String vendor = "sap.com";

			if (item.getSdu() != null) {
				componentName = item.getSdu().getName();
				vendor = item.getSdu().getVendor();
			} else {
				if (item.getArchive() != null) {
					componentName = item.getArchive().getAbsolutePath();
				}
			}

			idList.add(componentName + "/" + vendor + ";");
		}
		String[] idArray = idList.toArray(new String[] {});
		Arrays.sort(idArray);
		StringBuffer itemsIDBuffer = new StringBuffer();

		for (String string : idArray) {
			itemsIDBuffer.append(string);
		}

		return itemsIDBuffer.toString();
	}

	public void processDeployResult(DeployResult result)
			throws ReporterException {
		Document document = loadDocument();

		Element batchElement = createBatchElement(document);
		writeBatchStatus(result.getDeployResultStatus(), result
				.getDescription(), batchElement);
		getRootElement(document).appendChild(batchElement);
		addItemsToBatch(result.getSortedDeploymentItems(), document,
				batchElement);
		String listID = getItemListID(result.getDeploymentItems());
		batchElement.setAttribute(DEPLOY_BATCH_DEPLOYABLES_ID, listID);
		storeDocument(document);
	}

	private Element addItemElement(Document doc, Element parent,
			DeployItem deployItem) {
		String elementName = ELEMENT_NAME_DEPLOY_ITEM;
		if (deployItem.getSdu() instanceof Sca) {
			elementName = ELEMENT_NAME_SCA_ITEM;
		} else if (deployItem.getSdu() instanceof Sda) {
			elementName = ELEMENT_NAME_SDA_ITEM;
		}

		Element itemElement = createDeployItemElement(doc, elementName);
		writeItemStatus(deployItem, itemElement);
		parent.appendChild(itemElement);

		TimeStatisticsEntry[] timeStatisticEntries = deployItem
				.getTimeStatisticEntries();
		if (timeStatisticEntries != null) {
			for (TimeStatisticsEntry timeStatisticsEntry : timeStatisticEntries) {
				Element timeElement = doc
						.createElement(ELEMENT_NAME_TIME_ENTRY);
				timeElement.setAttribute(TIME_STAT_TYPE_STRING,
						timeStatisticsEntry.getName());
				timeElement.setAttribute(TIME_STAT_DURATION, new Long(
						timeStatisticsEntry.getDuration()).toString());
				timeElement.setAttribute(TIME_STAT_START_TIME, new Long(
						timeStatisticsEntry.getStartTime()).toString());
				timeElement.setAttribute(TIME_STAT_FINISH_TIME, new Long(
						timeStatisticsEntry.getFinishTime()).toString());
				itemElement.appendChild(timeElement);
			}
		}

		return itemElement;
	}

	private Element getRootElement(Document document) {
		Element retval = document.getDocumentElement();
		if (retval == null) {
			retval = document.createElement(ELEMENT_NAME_DOCROOT);
			document.appendChild(retval);
		}
		return retval;
	}

	private Element createBatchElement(Document document) {
		Element batchElement = document
				.createElement(ELEMENT_NAME_DEPLOY_BATCH);
		return batchElement;
	}

	private Element createDeployItemElement(Document document,
			String elementName) {
		Element deployItemElement = document.createElement(elementName);
		return deployItemElement;
	}

	private void writeBatchStatus(DeployResultStatus resultStatus,
			String resultDescription, Element element) {
		DeployResultStatus status = resultStatus == null ? DeployResultStatus.UNKNOWN
				: resultStatus;
		element.setAttribute(DEPLOY_BATCH_STATUS, status.getName());
		if (status != null) {
			element.setAttribute(DEPLOY_BATCH_STATUS_DESCRIPTION,
					resultDescription);
		}
	}

	private void writeItemStatus(DeployItem deployItem, Element element) {
		Sdu sdu = deployItem.getSdu();

		String componentName = "Unknown";
		String vendor = "sap.com";
		String version = "0.0.0.0";

		if (sdu != null) {
			componentName = sdu.getName();
			vendor = sdu.getVendor();
			version = sdu.getVersion().getVersionAsString();
		} else {
			if (deployItem.getArchive() != null) {
				componentName = deployItem.getArchive().getAbsolutePath();
			}
		}

		String archPath = "";
		if (deployItem.getArchive() != null) {
			archPath = deployItem.getArchive().getPath();
		}

		element.setAttribute(DEPLOY_ITEM_NAME, componentName);
		element.setAttribute(DEPLOY_ITEM_VENDOR, vendor);
		element.setAttribute(DEPLOY_ITEM_VERSION, version);
		element.setAttribute(DEPLOY_ITEM_FILE, archPath);
		DeployItemStatus status = deployItem.getDeployItemStatus();
		element.setAttribute(DEPLOY_ITEM_STATUS, status.getName());
		element.setAttribute(DEPLOY_ITEM_DESCRIPTION, deployItem
				.getDescription());

	}

	private Document loadDocument() throws ReporterException {
		File sourceFile = new File(REPORT_FILE_NAME);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ReporterException("Could not create document builder", e);
		}

		Document document = null;

		if (sourceFile.exists()) {
			try {
				document = builder.parse(sourceFile);
			} catch (SAXException e) {
				throw new ReporterException(
						"Caught SAXException while parsing " + REPORT_FILE_NAME,
						e);
			} catch (IOException e) {
				throw new ReporterException("Caught IOException while parsing "
						+ REPORT_FILE_NAME, e);
			}
		} else {
			document = builder.newDocument();
		}

		return document;
	}

	private void storeDocument(Document document) throws ReporterException {
		File targetFile = new File(REPORT_FILE_NAME);
		DOMSource dsrc = new DOMSource(document);
		StreamResult result = new StreamResult(targetFile);

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new ReporterException(
					"Failed to create Transformer instance", e);
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			transformer.transform(dsrc, result);
		} catch (TransformerException e) {
			throw new ReporterException(
					"Failed to transform document to target file", e);
		}
	}
}
