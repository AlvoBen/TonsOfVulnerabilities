package com.tssap.dtr.client.lib.protocol.entities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tssap.dtr.client.lib.protocol.HTTPException;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.IRequestStream;
import com.tssap.dtr.client.lib.protocol.IResponse;
import com.tssap.dtr.client.lib.protocol.IResponseEntity;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This class implements an HTTP entity based on a XML DOM document.
 * EXPERIMENTAL. NOT YET COMPLETED.
 */
public class XMLDOMEntity extends ResponseEntityBase implements IRequestEntity, IResponseEntity {
	
	private Document document;
	private int standalone = -1;

	/**
	 * Creates a new DOM entity for the given document. Selects "UTF-8" as
	 * default encoding and "text/xml" as content type.
	 * @param document  an XML document.
	 */
	public XMLDOMEntity(Document document) {
		this(document, "UTF-8");
	}

	/**
	 * Creates a new DOM entity for the given document. Uses the given
	 * encoding for serialization (e.g. "UTF-8"). Selects "text/xml"
	 * as content type.
	 * @param document  an XML document.
	 * @param contentCharset  the encoding to use.
	 */
	public XMLDOMEntity(Document document, String contentCharset) {
		super("text/xml");
		this.contentCharset = contentCharset;
		this.document = document;
	}	
	
	/**
	 * Determines whether a "standalone" attribute should be written to
	 * the XML header declaration. By default, no such attribute is written.
	 * By calling this method you may specify that either the value "yes" 
	 * or "no" is written, respectively.<br/>
	 * The value "yes" indicates that the document is "self-contained", i.e.
	 * it is not necessary for the parser to search for external entity
	 * declarations. The value "no" indicates that there are no such
	 * external entities.
	 * @param standalone if true, the attribute <code>standalone="yes"</code>
	 * is added to the XML header, otherwise <code>standalone="no"</code>.
	 */
	public void setStandalone(boolean standalone) {
		if (standalone) {
			this.standalone = 1;
		} else {
			this.standalone = 0;
		}
	}
		
	/**
	 * Creates a new DOM entity from the specified response.
	 * @param response the response from which this entity is initialized.
	 * @throws HTTPException - if the response is malformed, invalid or incomplete.
	 */
	public XMLDOMEntity(IResponse response, DocumentBuilder builder) throws HTTPException {
		super(response);
		try {
			read(response.getStream(), builder);
		} catch (Exception ex) {
			document = null;
			throw new HTTPException("Failed to parse response body.", ex);
		}
	}	
	
	
	/**
	 * The entity type string for XMLDOMEntity.
	 */
	public static final String ENTITY_TYPE = "XMLDOMEntity";

	/**
	 * Checks whether the given response entity is a XMLDOMEntity.
	 * @return true, if the entity is a XMLDOMEntity.
	 */
	public static boolean isXMLDOMEntity(IResponseEntity entity) {
		return ENTITY_TYPE.equals(entity.getEntityType());
	}

	/**
	 * Returns the given response entity as XMLDOMEntity.
	 * @return the entity casted to XMLDOMEntity, or null
	 * if the entity cannot be converted to XMLDOMEntity
	 */
	public static XMLDOMEntity valueOf(IResponseEntity entity) {
		return (isXMLDOMEntity(entity)) ? ((XMLDOMEntity)entity) : null;
	}

	/**
	 * Returns the type of this entity.
	 * @return "XMLDOMEntity".
	 * @see IResponseEntity#getEntityType()
	 */
	public String getEntityType() {
		return ENTITY_TYPE;
	}	
	
	
	/**
	 * Returns the XML document attached to this entity.
	 * @return  the XML document.
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Writes the content of this entity to the specified stream.
	 * @param destination - the stream to which the content is written.
	 * @throws IOException - if an i/o error occurs
	 * @see IRequestEntity#write(IRequestStream)
	 */
	public void write(IRequestStream destination) throws IOException {
		if (contentCharset == null) {
			contentCharset = "UTF-8";
		}
		Writer writer = new OutputStreamWriter(destination.asStream(), contentCharset);
		serializeNode(document, writer);
		writer.flush();
	}
	
	
	/**
	 * Prepares the entity for a repetition of a request.
	 * This method does nothing.
	 * @see IRequestEntity#reset()
	 */
	public void reset() {
		// nothing to do
	}

	/**
	 * Checks whether this entity supports request repetition.
	 * @return always true.
	 */
	public boolean supportsReset() {
		return true;
	}


	/**
	* Reads the content of this entity from the specified stream.
	* @param source  the stream from which the content is read.
	* @throws IOException  if an i/o error occurs.
	 */
	public void read(InputStream source, DocumentBuilder builder) throws IOException, SAXException {
		if (source != null) {
			document = builder.parse( source );
		}
	}
	
		
	private void serializeNode(Node node, Writer writer) throws IOException {
		if (node == null) {
			return;	
		}
		
		int nodeType = node.getNodeType();
		switch (nodeType) {
			case Node.DOCUMENT_NODE :
				writer.write("<?xml version=\"1.0\" encoding=\"");
				writer.write(contentCharset);
				if (standalone>=0) {
					writer.write(" standalone=");
					if (standalone==0) {
						writer.write("\"no\"");
					} else {
						writer.write("\"yes\"");
					}
				}
				writer.write("\"?>");
				
						 
				Node child = node.getFirstChild();
				while( child != null) {
					serializeNode(child, writer);
					child = child.getNextSibling();
				}
				break;
					
			case Node.PROCESSING_INSTRUCTION_NODE :
				writer.write("<?");
				writer.write(node.getNodeName());

				String value = node.getNodeValue();
				if (value != null && value.length() > 0) {
					writer.write(' ');
					writer.write(value);
				}
				writer.write("?>");
				break;
				
			case Node.DOCUMENT_TYPE_NODE :
				DocumentType docTypeNode = (DocumentType)node;
				writer.write("<!DOCTYPE ");
				writer.write(docTypeNode.getName());
				
				String systemID = docTypeNode.getSystemId();
				String publicID  = docTypeNode.getPublicId();
				if (publicID != null) {
					writer.write(" PUBLIC \"");
					writer.write(publicID);
					writer.write("\"");
				}
				if (systemID != null) {
					if (publicID == null) {
						writer.write(" SYSTEM");
					}
					writer.write(" \"");
					writer.write(systemID);
					writer.write("\"");
				}
				String internalSubset = docTypeNode.getInternalSubset();
				if (internalSubset != null) {
//					writer.write(" [");
//					//TODO
//					writer.write("]");
				}
				writer.write(">");
				break;

			case Node.COMMENT_NODE :
				writer.write("<!--");
				writer.write(node.getNodeValue());
				writer.write("-->");
				break;
				
			case Node.ELEMENT_NODE :
				writer.write("<");
				writer.write(node.getNodeName());
				NamedNodeMap attributes = node.getAttributes();
				if (attributes != null) {
					for (int i=0; i<attributes.getLength(); ++i) {
						Attr attribute = (Attr)attributes.item(i);
						writer.write(" ");
						writer.write(attribute.getNodeName());
						writer.write("=\"");
						writer.write(Encoder.encodeXml(attribute.getValue()));
						writer.write("\"");
					}					
				}
				NodeList children = node.getChildNodes();
				if (children.getLength()>0) {
					writer.write(">");
					for (int i = 0; i<children.getLength(); ++i) {
						serializeNode(children.item(i), writer);
					}
					writer.write("</");
					writer.write(node.getNodeName());
					writer.write(">");
				} else {
					writer.write("/>");
				}							
				break;				
				
			case Node.TEXT_NODE :
				writer.write(Encoder.encodeXml(node.getNodeValue()));
				break;
						
			case Node.ENTITY_REFERENCE_NODE :
				writer.write('&');
				writer.write(node.getNodeName());
				writer.write(';');
				break;

			case Node.CDATA_SECTION_NODE :
				writer.write("<![CDATA[");
				writer.write(node.getNodeValue());
				writer.write("]]>");
				break;	
				
			default:
				break;
		}		
	}	

}
