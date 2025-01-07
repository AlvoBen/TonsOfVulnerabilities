/*
 * Created on 2004-12-21
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.xml.parser.tokenizer;

import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sap.engine.lib.xml.util.NS;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ElementTokenWriter {
	
	private Hashtable prefixToNamespaceMapping;
	private Hashtable namespaceToPrefixMapping;
	private Document doc;
	private Element element;
	private ElementTokenWriter currentElementTokenWriter; 
	private int enterCounter;
	
	protected ElementTokenWriter(Document doc, Element element, Hashtable prefixToNamespaceMapping, Hashtable namespaceToPrefixMapping) {
		this.doc = doc;
		this.element = element;
		//this.prefixToNamespaceMapping = prefixToNamespaceMapping == null ? new Hashtable() : new Hashtable(prefixToNamespaceMapping);
		//this.namespaceToPrefixMapping = namespaceToPrefixMapping == null ? new Hashtable() : new Hashtable(namespaceToPrefixMapping);
		this.prefixToNamespaceMapping = prefixToNamespaceMapping == null ? new Hashtable() : (Hashtable) prefixToNamespaceMapping.clone();
		this.namespaceToPrefixMapping = namespaceToPrefixMapping == null ? new Hashtable() : (Hashtable) namespaceToPrefixMapping.clone();
		enterCounter = 0;
	}
	
	protected boolean isLeaved() {
		return(element != null && enterCounter == 0);
	}
		
	protected void appendNamespaces(Hashtable prefixToNamespaceMapping) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.appendNamespaces(prefixToNamespaceMapping);
		} else {
			this.prefixToNamespaceMapping.putAll(prefixToNamespaceMapping);
			Enumeration prefixesEnum = prefixToNamespaceMapping.keys();
			while(prefixesEnum.hasMoreElements()) {
				Object prefix = prefixesEnum.nextElement();
				Object namespace = prefixToNamespaceMapping.get(prefix);
				namespaceToPrefixMapping.put(namespace, prefix); 
			}
		}
	}
	
	private String determinePrefix(String namespace) {
		return((String)(namespaceToPrefixMapping.get(namespace)));
	}
	
	private String determineNamespace(String prefix) {
		return((String)(prefixToNamespaceMapping.get(prefix)));
	}
	
	private String createQName(String namespace, String localName) {
		String prefix = determinePrefix(namespace);
		if(prefix == null) {
			if(namespace.equals("")) {
				Object defaultNamespace = prefixToNamespaceMapping.get("");
				if(defaultNamespace == null) {
					prefix = "";
				}
			} else {
        prefix = "ns" + a++;
        prefixToNamespaceMapping.put(prefix, namespace);
        namespaceToPrefixMapping.put(namespace, prefix);
      }
		}
		if(prefix == null) {
			throw new IllegalArgumentException("No prefix is mapped to the namespace '" + namespace + "'.");
		}
		return(prefix.equals("") ? localName : prefix + ":" + localName);
	}
  
	protected void enter(String namespace, String localName) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.enter(namespace, localName);
		} else if(element == null) {
			String elementNamespace = processNamespace(namespace);
			element = doc.createElementNS(elementNamespace, createQName(elementNamespace, localName));
			enterCounter++;
		} else {
			currentElementTokenWriter = new ElementTokenWriter(doc, null, prefixToNamespaceMapping, namespaceToPrefixMapping);
			currentElementTokenWriter.enter(namespace, localName);
			element.appendChild(currentElementTokenWriter.getElement());
			enterCounter++;
		}
	}
	
	private String processNamespace(String namespace) {
		return(namespace == null ? "" : namespace);
	}
	
	protected Element getElement() {
		return(element);
	}

	protected void leave() {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.leave();
			if(currentElementTokenWriter.isLeaved()) {
				enterCounter--;
				currentElementTokenWriter = null;
			}
		} else {
			enterCounter--;
		}
	}

	protected String getPrefixForNamespace(String namespace) {
		if(currentElementTokenWriter != null) {
			return(currentElementTokenWriter.getPrefixForNamespace(namespace));
		}
		return(determinePrefix(namespace));
	}

	protected void setPrefixForNamespace(String prefix, String namespace) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.setPrefixForNamespace(prefix, namespace);
		} else {
			prefixToNamespaceMapping.put(prefix, namespace);
			namespaceToPrefixMapping.put(namespace, prefix);
		}
	}
  
  static int a = 0;
	protected void writeAttribute(String namespace, String name, String value) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.writeAttribute(namespace, name, value);
		} else {
			if(element == null) {
				throw new IllegalStateException("Illegal state is detected for operation 'writeAttribute'. Parent element is not created. 'Enter' operation should be performed.");
			}
			String attribNamespace = processNamespace(namespace);

			Attr attrib = doc.createAttributeNS(attribNamespace, createQName(attribNamespace, name));
			attrib.setNodeValue(value);
			element.setAttributeNode(attrib);
		}
	}


	protected void writeContent(String content) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.writeContent(content);
		} else {
			if(element == null) {
				throw new IllegalStateException("Illegal state is detected for operation 'writeContent'. Parent element is not created. 'Enter' operation should be performed.");
			}
			Text textNode = doc.createTextNode(content);
			element.appendChild(textNode);
		}
	}

	protected void writeContentCData(char[] chars) {
		writeContentCData(chars, 0, chars.length);
	}

	protected void writeContentCData(char[] chars, int offset, int count) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.writeContentCData(chars, offset, count);
		} else {
			if(element == null) {
				throw new IllegalStateException("Illegal state is detected for operation 'writeContentCData'. Parent element is not created. 'Enter' operation should be performed.");
			}
			CDATASection cdataSection = doc.createCDATASection(new String(chars, offset, count));
			element.appendChild(cdataSection);
		}
	}
	
	protected void writeComment(String comment) {
		if(currentElementTokenWriter != null) {
			currentElementTokenWriter.writeComment(comment);
		} else {
			if(element == null) {
				throw new IllegalStateException("Illegal state is detected for operation 'writeComment'. Parent element is not created. 'Enter' operation should be performed.");
			}
			Comment commentNode = doc.createComment(comment);
			element.appendChild(commentNode);
		}
	}

	protected void writeXmlAttribute(String name, String value) {
		setPrefixForNamespace("xmlns", NS.XMLNS);
		writeAttribute(NS.XMLNS, name, value);
	}
}
