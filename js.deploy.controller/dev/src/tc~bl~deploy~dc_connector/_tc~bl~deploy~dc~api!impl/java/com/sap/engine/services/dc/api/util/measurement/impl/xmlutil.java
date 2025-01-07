package com.sap.engine.services.dc.api.util.measurement.impl;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;
import com.sap.engine.services.dc.api.util.measurement.DAStatistic;

class XmlUtil {
	private static final String MEASUREMENT_TAG = "Measurement";
	private static final String DC_NAME_ATT = "dcName";
	private static final String TAG_NAME_ATT = "tagName";
	private static final String HAS_NEW_THREAD_STARTED_ATT = "hasNewThreadStarted";
	
	private static final String YES = "yes";
	private static final String INDENT_NUMBER = "indent-number";
	private static final Integer INDENT_SIZE = new Integer(4);

	private static final String TRUE = Boolean.TRUE.toString();

	private XmlUtil() {

	}

	static Document toDocument(final DAMeasurement measurement) {
		try {
			if (measurement == null) {
				return null;
			}

			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(true);

			final DocumentBuilder documentBuilder = docBuilderFactory
					.newDocumentBuilder();
			final Document resultDocument = documentBuilder.newDocument();

			attachNodeToDoc(measurement, resultDocument);

			return resultDocument;
		} catch (final ParserConfigurationException e) {
			throw new IllegalStateException("Could ont crate document: " + e.getMessage());
		}		
	}
	
	private static Node createNode(final Node parentNode, final DAMeasurement chilDAMeasurement,
			final Document document) {
		// crate tag for this measurement
		final Node result = document.createElement(MEASUREMENT_TAG);
		// create attribute for this measurement
		final NamedNodeMap tagAttributes = result.getAttributes();
		// has custom added child
		if (Boolean.TRUE.equals(chilDAMeasurement.hasNewThreadStarted())) {
			final Attr hasCustomAddedChildAttr = document
					.createAttribute(HAS_NEW_THREAD_STARTED_ATT);
			hasCustomAddedChildAttr.setValue(TRUE);
			tagAttributes.setNamedItem(hasCustomAddedChildAttr);
		}

		// tagName
		final String tagName = chilDAMeasurement.getTagName();
		final Attr tagNameAttr = document.createAttribute(TAG_NAME_ATT);
		tagNameAttr.setValue(tagName);
		tagAttributes.setNamedItem(tagNameAttr);
		// dc name attribute
		final String dcName = chilDAMeasurement.getDcName();
		final Attr dcNameAttr = document.createAttribute(DC_NAME_ATT);
		dcNameAttr.setValue(dcName);
		tagAttributes.setNamedItem(dcNameAttr);
		// append statistic entries as attributes
		Iterator statisticsIterator = chilDAMeasurement.getStatistics().iterator();
		while ( statisticsIterator.hasNext()) {
			DAStatistic statistic = (DAStatistic) statisticsIterator.next();
			final Attr statisticEntryAttr = document.createAttribute(statistic
					.getType().toString());
			statisticEntryAttr.setValue(statistic.getValue().toString());
			tagAttributes.setNamedItem(statisticEntryAttr);
		}
		
		
		if ( parentNode != null ) {
			parentNode.appendChild(result);			
		}
		
		
		return result;
	}
	

	private static Node attachNodeToDoc(final DAMeasurement DAMeasurement,
			final Document document) {
		if (DAMeasurement == null || document == null) {
			return null;
		}
		
		// crate tag for this measurement
		//final Node result = document.createElement(MEASUREMENT_TAG);
		
		final LinkedList fifoStack = new LinkedList();
		Node result = null;
		boolean isResult = false;
		fifoStack.add(new LinkMesurement(null, DAMeasurement));
		while ( fifoStack.size() > 0 ) {
			final LinkMesurement linkMeasurement = (LinkMesurement) fifoStack.removeFirst();
			final Node parentNode = createNode(linkMeasurement.getParentNode(), linkMeasurement.getDAMeasurement(), document);
			if (!isResult) {
				isResult = true;
				result = parentNode;
			}
			final DAMeasurement measurement = linkMeasurement.getDAMeasurement();
			final Iterator childrenIterator = measurement.getChildrenMeasurments().iterator();
			while(childrenIterator.hasNext()) {
				DAMeasurement chilDAMeasurement = (DAMeasurement) childrenIterator.next();
				fifoStack.add(new LinkMesurement(parentNode, chilDAMeasurement));				
			}
		}
		
		document.appendChild(result);
		
		return result;		
	}
	
	static String toDocumentAsString(final DAMeasurement measurement) {
		if (measurement == null) {
			return null;
		}

		final Document document = XmlUtil.toDocument(measurement);

		return toString(document);
	}

	private static String toString(final Document document) {
		try {
			if (document == null) {
				return null;
			}

			final TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute(INDENT_NUMBER, INDENT_SIZE);

			final Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, YES);

			final StreamResult result = new StreamResult(new StringWriter());
			final DOMSource source = new DOMSource(document);
			transformer.transform(source, result);

			return result.getWriter().toString();
		} catch (final TransformerFactoryConfigurationError e) {
			throw new IllegalStateException(
					"Error occurred while transforming document to string: " + e.getMessage());
		} catch (final TransformerException e) {
			throw new IllegalStateException(
					"Error occurred while transforming document to string: " + e.getMessage());
		}		
	}
	
	
	private static class LinkMesurement {
		private final DAMeasurement daMeasurement; 
		private final Node parentNode;
		
		LinkMesurement(Node parentNode, DAMeasurement daMeasurement ){
			this.daMeasurement = daMeasurement;
			this.parentNode = parentNode;
		}

		public DAMeasurement getDAMeasurement() {
			return daMeasurement;
		}

		public Node getParentNode() {
			return parentNode;
		}
	}


}
