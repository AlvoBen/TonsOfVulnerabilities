package com.tssap.dtr.client.lib.protocol.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Helper class providing formatted XML serialization. May serialize directly
 * to an output stream or to a string.<br/>
 */
public final class XMLWriter {

	/**
	 * Namespace used for serialization of XML schema based documents.
	 */
	public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	
	private OutputStream out;
	private StringBuffer sb;
	private String rootTag;
	private int tabLevel = 0;
	private char[] indent;
	private boolean wrapAttributes;
	private static final int MAX_INDENT_LEVEL = 32;
	
	
	/**
	 * Helper class representing attributes of XML tags.
	 */
	public static class Attribute {
		/** The name of the attribute */
		public String name;
		/** The value of the attribute */
		public String value;

		/** Creates a new attribute */
		public Attribute(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
	
	/**
	 * Creates a new XML writer based on a string buffer.
	 */
	public XMLWriter() {
		sb = new StringBuffer();
		indent = new char[MAX_INDENT_LEVEL + 1];
		Arrays.fill(indent, '\t');
	}

	/**
	 * Creates a new XML writer for the given output stream.
	 * @param out  an output stream
	 */
	public XMLWriter(OutputStream out) {
		this();
		this.out = out;
	}
	
	/**
	 * Determines whether line breaks should be inserted between
	 * the item in an attribute list.
	 * @param enable  if true, attribute lists are split into multiple lines
	 */
	public void wrapAttributeLists(boolean enable) {
		wrapAttributes = enable;
	}
	
	/**
	 * Writes the "&lt;?xml&gt" header of the document. The encoding
	 * is always selected as "UTF-8".
	 */
	public void beginDocument() {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
	}

	/**
	 * Writes the "&lt;?xml&gt" header of the document and creates
	 * a "&lt;!DOCTYPE&gt;" tag with the given <code>publicID</code>
	 * and <code>dtdLocation</code>. If one of the parameters is
	 * null, the DOCTYPE tag is omitted.
	 */
	public void beginDocument(String publicID, String dtdLocation) {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		if (publicID != null && dtdLocation != null) {
			sb.append("<!DOCTYPE ").append(rootTag);
			sb.append(" PUBLIC \"").append(publicID).append("\"");
			sb.append(" \"").append(dtdLocation).append("\"");
			sb.append(">\r\n");
		}
	}
	
	/**
	 * Writes the end of the document, i.e. the closing tag.
	 */
	public void endDocument() {
		undent();
		sb.append("</").append(rootTag).append(">");
	}	
	
	/**
	 * Writes the root tag of the document.
	 * @param tagName  the name of the root tag.
	 */
	public void writeRootTag(String tagName) {
		this.rootTag = tagName;		
		sb.append("<").append(rootTag).append(">\r\n");
		indent();
	}	
	
	/**
	 * Writes the root tag of the document with the given list of attributes.
	 * @param tagName  the name of the root tag.
	 * @param attributes  a list of attributes
	 */
	public void writeRootTag(String tagName, Attribute[] attributes) {
		this.rootTag = tagName;	
		sb.append("<").append(rootTag);	
		if (attributes != null) {
			writeAttributes(attributes);					
		}
		sb.append(">\r\n");
		indent();
	}	
	
	/**
	 * Writes the root tag of the document with the given list of attributes
	 * and appends a namespace and schema location attribute.
	 * @param tagName  the name of the root tag.
	 * @param attributes  a list of attributes
	 * @param namespaceURI  the namespace of the root tag
	 * @param schemaLocation  the URI where the XML schema for the document can be found
	 */	
	public void writeRootTag(String tagName, String namespaceURI, String schemaLocation, Attribute[] attributes) {
		this.rootTag = tagName;	
		sb.append("<").append(rootTag);	
		if (attributes != null) {
			writeAttributes(attributes);					
		}		
		if (namespaceURI != null && schemaLocation != null) {
			sb.append("\r\n\txmlns=\"").append(namespaceURI);
			sb.append("\"\r\n\txmlns:xsi=\"").append(XSD_NAMESPACE).append("\"\r\n\t");
			sb.append("xsi:schemaLocation=\"").append(namespaceURI).append(" ");
			sb.append(schemaLocation).append("\"");
		}
		sb.append(">\r\n");
		indent();
	}

	/**
	 * Writes an open tag.
	 * @param tagName  the name of the tag.
	 */
	public void beginTag(String tagName) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName).append(">\r\n");
		indent();
	}

	/**
	 * Writes an open tag with attributes.
	 * @param tagName  the name of the tag.
	 * @param attributes  a list of attributes.
	 */
	public void beginTag(String tagName, Attribute[] attributes) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName);
		if (attributes != null) {
			writeAttributes(attributes);
		}
		sb.append(">\r\n");
		indent();
	}

	/**
	 * Writes a closing tag.
	 * @param tagName  the name of the tag.
	 */
	public void endTag(String tagName) {
		undent();
		sb.append(indent, 0, tabLevel);
		sb.append("</").append(tagName).append(">\r\n");
	}

	/**
	 * Write a tag with the given simple value.
	 * @param tagName  the name of the tag.
	 * @param value  the value of the tag.
	 */
	public void writeTag(String tagName, String value) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName);
		if (value!=null) {		
			sb.append(">");
			sb.append(encodeXml(value));
			sb.append("</").append(tagName).append(">\r\n");
		} else {
			sb.append("/>\r\n");			
		}
	}
	
	/**
	 * Write a tag with the given simple value and attributes
	 * @param tagName  the name of the tag.
	 * @param value  the value of the tag.
	 * @param attributes  a list of attributes
	 */
	public void writeTag(String tagName, String value, Attribute[] attributes) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName);
		if (attributes != null) {
			writeAttributes(attributes);
		}
		if (value!=null) {
			sb.append(">");
			sb.append(encodeXml(value));
			sb.append("</").append(tagName).append(">\r\n");			
		} else {
			sb.append("/>\r\n");			
		}
	}
	
	/**
	 * Write a tag for the given integer.
	 * @param tagName  the name of the tag.
	 * @param value  the value of the tag.
	 */
	public void writeTag(String tagName, int value) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName).append(">");
		sb.append(Integer.toString(value));
		sb.append("</").append(tagName).append(">\r\n");
	}

	/**
	 * Write a tag for the given long.
	 * @param tagName  the name of the tag.
	 * @param value  the value of the tag.
	 */
	public void writeTag(String tagName, long value) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName).append(">");
		sb.append(Long.toString(value));
		sb.append("</").append(tagName).append(">\r\n");
	}

	/**
	 * Write an empty tag.
	 * @param tagName  the name of the tag.
	 */
	public void writeEmptyTag(String tagName) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName).append("/>\r\n");
	}

	/**
	 * Write an empty tag with attributes
	 * @param tagName  the name of the tag.
	 * @param attributes  a list of attributes.
	 */
	public void writeEmptyTag(String tagName, Attribute[] attributes) {
		sb.append(indent, 0, tabLevel);
		sb.append("<").append(tagName);
		if (attributes != null) {
			writeAttributes(attributes);
		}
		sb.append("/>\r\n");
	}
	
	/**
	 * Write the given string.
	 * @param content  the string to write
	 */
	public void write(String content) {
		sb.append(content);
	}

	/**
	 * Serialize the content of the writer to the defined output stream.
	 * @throws IOException  if an i/o exception occurs
	 */
	public void serialize() throws IOException {
		if (out != null && sb.length() > 0) {
			try {
				out.write(sb.toString().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				throw new IllegalStateException("Java runtime does not support UTF-8 encoded strings");
			}
			sb.setLength(0);
		}
	}

	/**
	 * Closes the output stream. Any pending content of the writer is
	 * serialized and flushed first.
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (out != null) {
			serialize();
			out.flush();
			out.close();
		}
	}

	/**
	 * Returns the content of the writer as string.
	 * @return the content of the writer.
	 */
	public String toString() {
		return sb.toString();
	}

	/**
	 * Encodes the given text in XML compatible format by replacing
	 * certain characters with predefined XML entities.
	 * @param plainText  the string to encode
	 * @return  the encoded string
	 */
	public static String encodeXml(String plainText) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < plainText.length(); i++) {
			switch (plainText.charAt(i)) {
				case '&' :
					buffer.append("&amp;");
					continue;
				case '<' :
					buffer.append("&lt;");
					continue;
				case '>' :
					buffer.append("&gt;");
					continue;
				case '\'' :
					buffer.append("&apos;");
					continue;
				case '"' :
					buffer.append("&quot;");
					continue;
				default :
					buffer.append(plainText.charAt(i));
			}
		}
		return buffer.toString();
	}


	private void indent() {
		if (tabLevel < MAX_INDENT_LEVEL) {
			++tabLevel;
		}
	}

	private void undent() {
		if (tabLevel > 0) {
			--tabLevel;
		}
	}

	private void writeAttributes(Attribute[] attributes) {
		int attrCount = attributes.length;
		Attribute a;
		for (int i = 0; i < attrCount; i++) {
			a = attributes[i];
			if (a != null && a.name != null && a.name.length()>0) {
				if (wrapAttributes  &&  tabLevel < MAX_INDENT_LEVEL) {
					sb.append("\r\n");
					sb.append(indent, 0, tabLevel+1);
				}				
				sb.append(" ").append(a.name);
				sb.append("=\"");
				sb.append(encodeXml(a.value)).append("\"");
			}
		}
	}

}