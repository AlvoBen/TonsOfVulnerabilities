/*
 * Created on 2004-12-21
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLDOMTokenWriter implements XMLTokenWriterDOM {
	
	private ElementTokenWriter elementTokenWriter;
	private Document doc;
	private Element mainElement;
  
  public XMLDOMTokenWriter(DOMSource domSource) throws ParserConfigurationException {
  	init(domSource);
  }
  
	public XMLDOMTokenWriter(Document doc) {
		init(doc);
	}
	
	public XMLDOMTokenWriter(Element mainElement) {
		init(mainElement);
	}
  
	public void init(DOMSource domSource) throws ParserConfigurationException {
		Node node = domSource.getNode();
		if(node == null) {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			domSource.setNode(doc);
			init(doc);
		} else if(node instanceof Document) {
			init((Document)node);
		} else if(node instanceof Element) {
			init((Element)node);
		}
	}
	
	public void init(Document doc) {
		this.doc = doc;
		init(doc.getDocumentElement());
	}
	
	public void init(Element mainElement) {
		if(mainElement != null) {
			doc = mainElement.getOwnerDocument();
		}
		this.mainElement = mainElement;
		elementTokenWriter = new ElementTokenWriter(doc, mainElement, null, null);
	} 
  
	public void appendNamespaces(Hashtable prefixToNamespaceMapping) {
		elementTokenWriter.appendNamespaces(prefixToNamespaceMapping);
	}
  
	public void enter(String namespace, String localName) throws IOException {
		elementTokenWriter.enter(namespace, localName);
	}

	public void leave() throws IOException, IllegalStateException {
		elementTokenWriter.leave();
		if(elementTokenWriter.isLeaved() && mainElement == null) {
			doc.appendChild(elementTokenWriter.getElement());
		}
	}

	public void flush() throws IOException {
	}

	public String getPrefixForNamespace(String namespace) throws IOException, IllegalStateException {
		return(elementTokenWriter.getPrefixForNamespace(namespace));
	}

	public void setPrefixForNamespace(String prefix, String namespace) throws IOException, IllegalStateException {
		elementTokenWriter.setPrefixForNamespace(prefix, namespace);
	}

	public void writeAttribute(String namespace, String name, String value) throws IOException, IllegalStateException {
		elementTokenWriter.writeAttribute(namespace, name, value);
	}

	public void writeContent(String content) throws IOException {
		elementTokenWriter.writeContent(content);
	}

	public void writeContentCData(char[] chars) throws IOException {
		elementTokenWriter.writeContentCData(chars);
	}

	public void writeContentCData(char[] chars, int offset, int count) throws IOException {
		elementTokenWriter.writeContentCData(chars, offset, count);
	}
	
	public void writeContentCDataDirect(char[] chars) throws IOException {
    writeContent(new String(chars));
	}

	public void writeContentCDataDirect(char[] chars, int offset, int count) throws IOException {
    writeContent(new String(chars, offset, count));
	}

	public void writeComment(String comment) throws IOException {
		elementTokenWriter.writeComment(comment);
	}

	public void writeXmlAttribute(String name, String value) throws IOException, IllegalStateException {
		elementTokenWriter.writeXmlAttribute(name, value);
	}
  
	public void setAttributeHandler(AttributeHandler handler) {
	}
	
	public void init(OutputStream output) throws IOException {
	}
  
	public void init(OutputStream output, String encoding) throws IOException {
	}
  
	public void init(OutputStream output, String encoding, Hashtable defaultPrefixes) throws IOException {
	}
	
	public void init(OutputStream output, Hashtable defaultPrefixes) throws IOException {
	}

  public void close() throws IOException {    
  }

  public void writeInitial() throws IOException {
  }

  public void setProperty(String key, Object value) {
    // TODO Auto-generated method stub
    
  }
  
  
}
