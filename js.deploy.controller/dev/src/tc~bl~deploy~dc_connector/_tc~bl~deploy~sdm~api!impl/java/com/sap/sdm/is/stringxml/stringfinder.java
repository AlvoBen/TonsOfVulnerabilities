package com.sap.sdm.is.stringxml;

import com.sap.sdm.util.xml.XMLFilter;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public final class StringFinder {

	/**
	 * 
	 * @param name
	 *            tag name
	 * @param input
	 *            XML string in which the search is done
	 * @return StringPos object containing the first position on which tag
	 *         defined by its <param>name</param> is found; also the position on
	 *         which this tag is closed
	 * @throws Exception
	 */
	public static StringPos findXMLElem(String name, String input)
			throws Exception {
		return findXMLElemInt(name, input, false);
	}

	/**
	 * 
	 * @param name
	 *            tag name
	 * @param input
	 *            XML string in which the search is done
	 * @return StringPos object containing the first position on which tag
	 *         defined by its <code>name</code> is found; also the position on
	 *         which this tag is closed. Note: the method
	 *         <link>findXMLElem(String, String)</link> Finds only tag with such
	 *         <code>name</code> that doesn't have any corresponding attributes
	 * @see findXMLElem(String, String)
	 * @throws Exception
	 */
	public static StringPos findXMLElemOpen(String name, String input)
			throws Exception {
		return findXMLElemInt(name, input, true);
	}

	/**
	 * 
	 * @param elemName
	 *            tag name
	 * @param input
	 *            XML string in which the search is done
	 * @param attrName
	 *            the name of the attribute
	 * @return the value of attribute with <param>attrName</param>. Return
	 *         attribute is filtered from special XML character consequences
	 * @throws Exception
	 */
	public static String findAttrForXMLElem(String elemName, String input,
			String attrName) throws Exception {
		checkNameInput(elemName, input);
		if (attrName == null) {
			throw new NullPointerException(
					"Cannot find an Attribute with name null");
		}
		StringPos elemPos = findXMLElemInt(elemName, input, true);
		if (elemPos == null) {
			return null;
		}

		int elemEndIndex = input.indexOf(">", elemPos.beginPos);
		if (elemEndIndex == -1) {
			throw new Exception("No closing > found for " + elemName);
		}
		String attrString = input.substring(elemPos.beginPos, elemEndIndex);

		StringBuffer attrBuffer = new StringBuffer(attrName);
		attrBuffer.append("=\"");
		int attrBeginIndex = attrString.indexOf(attrBuffer.toString(), elemName
				.length());
		if (attrBeginIndex == -1) {
			// attribute not found for Elem
			return null;
		}
		int attrEndIndex = attrString.indexOf("\"", attrBeginIndex + 2
				+ attrName.length());
		if (attrBeginIndex + 2 + attrName.length() == -1) {
			throw new Exception("No closing \" found for attribute " + attrName);
		}
		String result = attrString.substring(attrBeginIndex + 2
				+ attrName.length(), attrEndIndex);
		return XMLFilter.checkAndFilterInput(result);
	}

	/**
	 * 
	 * @param name
	 *            tag name
	 * @param input
	 *            XML string in which the search is done
	 * @return the only difference with <link>findXMLElem(String, String)</link>
	 *         is that this method doesn't throw an exception in case that XML
	 *         contains no occurrence of tag - returns null in such case
	 * @throws Exception
	 */
	public static StringPos findOptXMLElem(String name, String input)
			throws Exception {
		checkNameInput(name, input);
		String startTag = createStartTag(name, false);
		String endTag = createEndTag(name);
		int beginIndex = input.indexOf(startTag);
		if (beginIndex == -1) {
			return null;
		}
		int endIndex = input.indexOf(endTag);
		if (endIndex == -1) {
			throw new Exception("No closing " + endTag + " found in XML data");
		}
		return new StringPos(beginIndex, endIndex);
	}

	/**
	 * 
	 * @param input
	 *            XML string
	 * @return name of the first XML element inside <param>input</param> XML
	 *         never mind whether this element has no attributes
	 */
	public static String getNameOfNextXMLElement(String input) {

		String result = null;

		if (input == null) {
			return null;
		}
		int nextElemStart = input.indexOf("<");
		if (nextElemStart == -1) {
			return null;
		}
		int nextElemEnd = input.indexOf(">", nextElemStart + 1);
		if (nextElemEnd == -1) {
			return null;
		}
		String nextElemName = input.substring(nextElemStart + 1, nextElemEnd);
		if (nextElemName.indexOf(" ") != -1) {
			int resultIndex = nextElemName.indexOf(" ");
			result = nextElemName.substring(0, resultIndex);
		} else {
			result = nextElemName;
		}
		return result;
	}

	/**
	 * 
	 * @param input
	 *            XML string
	 * @return the name of the root XML element(not the standard xml open tag
	 *         itself)
	 */
	public static String getNameOfTopXMLElement(String input) {

		String result = null;

		if (input == null) {
			return null;
		}
		int indexHeaderStart = input.indexOf("<?");
		if (indexHeaderStart == -1) {
			return null;
		}
		int indexHeaderEnd = input.indexOf("?>", indexHeaderStart + 2);
		if (indexHeaderEnd == -1) {
			return null;
		}
		int topElemStart = input.indexOf("<", indexHeaderEnd + 2);
		if (topElemStart == -1) {
			return null;
		}
		int topElemEnd = input.indexOf(">", topElemStart + 1);
		if (topElemEnd == -1) {
			return null;
		}
		String topElemString = input.substring(topElemStart + 1, topElemEnd);
		if (topElemString.indexOf(" ") != -1) {
			int resultIndex = topElemString.indexOf(" ");
			result = topElemString.substring(0, resultIndex);
		} else {
			result = topElemString;
		}
		return result;
	}

	private static StringPos findXMLElemInt(String name, String input,
			boolean open) throws Exception {
		checkNameInput(name, input);
		String startTag = createStartTag(name, open);
		String endTag = createEndTag(name);
		int beginIndex = input.indexOf(startTag);
		if (beginIndex == -1) {
			throw new Exception("No opening " + startTag + " found in XML data");
		}
		int endIndex = input.indexOf(endTag);
		if (endIndex == -1) {
			throw new Exception("No closing " + endTag + " found in XML data");
		}
		return new StringPos(beginIndex, endIndex);
	}

	private static void checkNameInput(String name, String input)
			throws Exception {
		if (name == null) {
			throw new NullPointerException(
					"Cannot find an Element with name null");
		}
		if (input == null) {
			throw new NullPointerException(
					"Cannot find an Element within input null");
		}
	}

	private static String createStartTag(String name, boolean open) {
		StringBuffer startTag = new StringBuffer("<");
		startTag.append(name);
		if (!open) {
			startTag.append(">");
		}
		return startTag.toString();
	}

	private static String createEndTag(String name) {
		StringBuffer endTag = new StringBuffer("</");
		endTag.append(name);
		endTag.append(">");
		return endTag.toString();
	}
}
