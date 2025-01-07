package com.sap.sdm.is.stringxml.impl;

import java.util.Stack;

import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.util.xml.XMLFilter;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
class StringXMLizerImpl implements StringXMLizer {

	private final static String TAGSTART = "<";
	private final static String TAGEND = ">";
	private final static String ENDTAGSTART = "</";
	private final static String XMLHEADER = "<?xml version=\"1.0\"?>";
	private final static String XMLCDATABEGIN = "<![CDATA[";
	private final static String XMLCDATAEND = "]]>";

	private String rootElemName = null;
	private String currentElemName = null;

	private StringBuffer buffer = null;

	private Stack startedElems = null;

	StringXMLizerImpl(String rootElemName) {
		this(rootElemName, new String[0], new String[0]);
	}

	StringXMLizerImpl(String rootElemName, String rootAttrName,
			String rootAttrValue) {
		this(rootElemName, new String[] { rootAttrName },
				new String[] { rootAttrValue });
	}

	StringXMLizerImpl(String rootElemName, String[] rootAttrNames,
			String[] rootAttrValues) {
		checkElemName(rootElemName);
		checkAttrNames(rootAttrNames);
		checkAttrValues(rootAttrValues);
		checkLength(rootAttrNames, rootAttrValues);
		this.rootElemName = rootElemName;
		this.currentElemName = rootElemName;
		buffer = new StringBuffer(XMLHEADER);
		startedElems = new Stack();
		buffer.append(TAGSTART);
		buffer.append(rootElemName);
		for (int i = 0; i < rootAttrNames.length; i++) {
			buffer.append(" ");
			buffer.append(rootAttrNames[i]);
			buffer.append("=\"");
			buffer.append(XMLFilter.checkAndFilterOutput(rootAttrValues[i]));
			buffer.append("\"");
		}
		buffer.append(TAGEND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.StringXMLizer#addElemWithContent
	 * (java.lang.String, java.lang.String)
	 */
	public void addElemWithContent(String name, String content) {

		this.addElemWithAttrsAndContent(name, new String[0], new String[0],
				content);
	}

	public void addElemWithContentCDATA(String name, String content) {
		StringBuffer dummy = new StringBuffer(XMLCDATABEGIN);
		dummy.append(content);
		dummy.append(XMLCDATAEND);
		this.addElemWithAttrsAndContent(name, new String[0], new String[0],
				dummy.toString());
	}

	public void addElemWithAttrAndContent(String name, String attrName,
			String attrValue, String content) {
		this.addElemWithAttrsAndContent(name, new String[] { attrName },
				new String[] { attrValue }, content);
	}

	public void addElemWithAttrsAndContent(String name, String[] attrNames,
			String[] attrValues, String content) {
		checkElemName(name);
		checkAttrNames(attrNames);
		checkAttrValues(attrValues);
		checkLength(attrNames, attrValues);
		buffer.append(TAGSTART);
		buffer.append(name);
		for (int i = 0; i < attrNames.length; i++) {
			buffer.append(" ");
			buffer.append(attrNames[i]);
			buffer.append("=\"");
			buffer.append(XMLFilter.checkAndFilterOutput(attrValues[i]));
			buffer.append("\"");
		}
		buffer.append(TAGEND);
		if (content != null) {
			buffer.append(XMLFilter.checkAndFilterOutput(content));
		}
		buffer.append(ENDTAGSTART);
		buffer.append(name);
		buffer.append(TAGEND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.StringXMLizer#startElem(java.lang
	 * .String)
	 */
	public void startElem(String name) {
		this.startElemWithAttrs(name, new String[0], new String[0]);
	}

	public void startElemWithAttr(String name, String attrName, String attrValue) {
		this.startElemWithAttrs(name, new String[] { attrName },
				new String[] { attrValue });
	}

	public void startElemWithAttrs(String name, String[] attrNames,
			String[] attrValues) {
		checkElemName(name);
		checkAttrNames(attrNames);
		checkAttrValues(attrValues);
		checkLength(attrNames, attrValues);
		buffer.append(TAGSTART);
		buffer.append(name);
		for (int i = 0; i < attrNames.length; i++) {
			buffer.append(" ");
			buffer.append(attrNames[i]);
			buffer.append("=\"");
			buffer.append(XMLFilter.checkAndFilterOutput(attrValues[i]));
			buffer.append("\"");
		}
		buffer.append(TAGEND);
		startedElems.push(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.StringXMLizer#endElem(java.lang.
	 * String)
	 */
	public void endCurrentElem() {
		if (startedElems.empty()) {
			throw new RuntimeException("No more Element to end");
		}
		String currentElem = (String) startedElems.pop();
		buffer.append(ENDTAGSTART);
		buffer.append(currentElem);
		buffer.append(TAGEND);
	}

	public void endRootElem() {
		if (!startedElems.empty()) {
			throw new RuntimeException("Not all Elements ended");
		}
		buffer.append(ENDTAGSTART);
		buffer.append(this.rootElemName);
		buffer.append(TAGEND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.client.StringXMLizer#getString()
	 */
	public String getString() {
		return buffer.toString();
	}

	private void checkElemName(String elemName) {
		if (elemName == null) {
			throw new NullPointerException("Element name null is not allowed");
		}
	}

	private void checkAttrName(String attrName) {
		if (attrName == null) {
			throw new NullPointerException("Attribute name null is not allowed");
		}
	}

	private void checkAttrValue(String attrValue) {
		if (attrValue == null) {
			throw new NullPointerException(
					"Attribute value null is not allowed");
		}
	}

	private void checkAttrNames(String[] attrNames) {
		if (attrNames == null) {
			throw new NullPointerException(
					"Attribute names array is null - not allowed!");
		}
		for (int i = 0; i < attrNames.length; i++) {
			checkAttrName(attrNames[i]);
		}
	}

	private void checkAttrValues(String[] attrValues) {
		if (attrValues == null) {
			throw new NullPointerException(
					"Attribute values array is null - not allowed!");
		}
		for (int i = 0; i < attrValues.length; i++) {
			checkAttrValue(attrValues[i]);
		}
	}

	private void checkLength(String[] arr1, String[] arr2) {
		if (arr1.length != arr2.length) {
			throw new RuntimeException(
					"Arrays have different length - not allowed!");
		}
	}

}
