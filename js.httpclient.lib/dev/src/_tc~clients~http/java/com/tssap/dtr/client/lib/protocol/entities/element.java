package com.tssap.dtr.client.lib.protocol.entities;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This class represents a child element of a structured DAV property.
 * <p>The Element class allows to build simple DOM-like trees by adding children
 * to Element instances.
 * Note, currently properties with "mixed" values, i.e. that consist of
 * text fragments with interspersed tags, are not supported.</p>
 */
public class Element {

	/** The name of this element  */
	private String name;

	/** The namespace prefix of this element*/
	private String namespacePrefix;

	/** The namespace uri of this element */
	private String namespaceURI;

	/** The string representation of this element. */
	private Object value;

	/** The next element in a list of childs */
	private Element next;

	/** The first child of this element */
	private Element firstChild;

	/** The last child of this element */
	private Element lastChild;

	/** The parent of this element */
	private Element parent;

	/** The number of children */
	private int count = 0;

	/**
	  * Creates a new element with given name and namespace URI.
	  * If the qualifiedName has a namespace prefix it is extracted and stored
	  * separately from the name.
	  * @param qualifiedName the name of the new Element.
	  * @param namespaceURI the namespace to which the name of the new element
	  * belongs.
	  */
	public Element(String qualifiedName, String namespaceURI) {
		this((Element) null, qualifiedName, namespaceURI);
	}

	/**
	  * Creates a new element with given name, namespace URI and (simple) value.
	  * If the qualifiedName has a namespace prefix it is extracted and stored
	  * separately from the name.
	  * @param qualifiedName the name of the new Element.
	  * @param namespaceURI the namespace to which the name of the new element
	  * belongs.
	  * @param value the initial value of the new element.
	  */
	public Element(String qualifiedName, String namespaceURI, String value) {
		this((Element) null, qualifiedName, namespaceURI);
		setValue(value);
	}

	/**
	  * Creates a new element with given parent, name and namespace URI.
	  * If the qualifiedName has a namespace prefix it is extracted and stored
	  * separately from the name.
	  * @param parent the parent element of the new element. If null, this
	  * element is a top-level element.
	  * @param qualifiedName the name of the new Element.
	  * @param namespaceURI the namespace to which the name of the new element
	  * belongs.
	  */
	public Element(Element parent, String qualifiedName, String namespaceURI) {
		this.parent = parent;
		int n = qualifiedName.indexOf(':');
		if (n > 0) {
			this.name = qualifiedName.substring(n + 1);
			this.namespacePrefix = qualifiedName.substring(0, n);
		} else {
			this.name = qualifiedName;
			this.namespacePrefix = null;
		}
		this.namespaceURI = namespaceURI;
	}

	/**
	 * Returns the simple name of this element (without namespace prefix)
	 * @return The simple name of this element.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the qualified name of this element, i.e. namespace_prefix:name
	 * @return The qualified name of this element.
	 */
	public String getQualifiedName() {
		return (namespacePrefix != null) ? namespacePrefix + ":" + name : name;
	}

	/**
	 * Returns the namespace prefix of this element.
	 * @return A namespace prefix (without trailing colon).
	 */
	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	/**
	 * Returns the namespace URI of this element.
	 * @return A namespace URI.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * Returns the xmlns attribute for this element in the format
	 * xmlns:prefix=\"uri\", ready for usage in a XML tag
	 * @return A xmlns namespace attribute.
	 */
	public String getNamespaceAttribute() {
		StringBuffer s = null;
		if (namespaceURI != null) {
			s = new StringBuffer("xmlns");
			if (namespacePrefix != null) {
				s.append(":").append(namespacePrefix);
			}
			s.append("=\"").append(namespaceURI).append("\"");
		}
		return s.toString();
	}

	/**
	 * Returns the value of this element. 
	 * If the element has children the value is returned as XML fragment
	 * (@see Element#toString).
	 * @return The value of this element.
	 */
	public String getValue() {
		return (value != null) ? value.toString() : null;
	}

	/**
	 * Sets the (simple) value of this element. If the element has children
	 * these children are removed.
	 * @param value the new value of this element.
	 */
	public void setValue(String value) {
		removeChildren();
		this.value = value;
	}

	/**
	 * Returns the parent of this element.
	 * @return The parent of this element.
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * Returns the successor of this element.
	 * @return The successor of this element.
	 */
	public Element next() {
		return next;
	}

	/**
	 * Returns the number of children of this element.
	 * @return The number of children of this element.
	 */
	public int countChildren() {
		return count;
	}

	/**
	 * Adds a new child to this element.
	 * @param child  the child element to add.
	 * @return A reference to the new Element
	 */
	public Element addChild(Element child) {
		if (lastChild != null) {
			lastChild.next = child;
			lastChild = lastChild.next;
		} else {
			firstChild = child;
			lastChild = firstChild;
			value = this;
		}
		++count;
		return lastChild;
	}

	/**
	 * Adds a new child to this element with specified name and namespace URI.
	 * @param childName the name of the new child. Namespace prefix if present
	 * is extracted.
	 * @param namespaceURI the namespace URI to which this element belongs.
	 * @return A reference to the new Element
	 */
	public Element addChild(String childName, String namespaceURI) {
		if (lastChild != null) {
			lastChild.next = new Element(this, childName, namespaceURI);
			lastChild = lastChild.next;
		} else {
			firstChild = new Element(this, childName, namespaceURI);
			lastChild = firstChild;
			value = this;
		}
		++count;
		return lastChild;
	}

	/**
	 * Adds a new child to this element with specified name, value and namespace URI.
	 * @param childName the name of the new child. Namespace prefix if present
	 * is extracted.
	 * @param value the string value of this Element.
	 * @param namespaceURI the namespace URI to which this element belongs.
	 * @return A reference to the new Element
	 */
	public Element addChild(String childName, String value, String namespaceURI) {
		addChild(childName, namespaceURI);
		lastChild.setValue(value);
		return lastChild;
	}

	/**
	 * Returns the first child of this element.
	 * @return The first child of this element.
	 */
	public Element firstChild() {
		return firstChild;
	}

	/**
	 * Returns the last child of this element.
	 * @return The last child of this element.
	 */
	public Element lastChild() {
		return lastChild;
	}

	/**
	 * Returns the child specified by its name.
	 * If no child
	 * with this name exists (or this element has no childs) the method
	 * returns null. Note, this method ignores namespaces and namespace
	 * prefixes.
	 * @param childName the name of the child.
	 * @return The child corresponding to the given name, or null.
	 */
	public Element getChild(String childName) {
		Element child = firstChild;
		int n = childName.indexOf(':');
		String simpleName = (n > 0) ? childName.substring(n + 1) : childName;
		while (child != null) {
			if (simpleName.equals(child.name)) {
				break;
			}
			child = child.next;
		}
		return child;
	}

	/**
	 * Returns the child specified by its name.
	 * If no child
	 * with this name exists (or this element has no childs) the method
	 * returns null. Note, this method checks if the child's namespace
	 * matches the specified namespace URI, but namespace prefixes are
	 * ignored.
	 * @param childName the name of the child.
	 * @param namespaceURI the namespace URI of the child.
	 * @return The child corresponding to the given name, or null.
	 */
	public Element getChild(String childName, String namespaceURI) {
		Element child = firstChild;
		int n = childName.indexOf(':');
		String simpleName = (n > 0) ? childName.substring(n + 1) : childName;
		while (child != null) {
			if (simpleName.equals(child.name) && namespaceURI.equals(child.namespaceURI)) {
				break;
			}
			child = child.next;
		}
		return child;
	}

	/**
	 * Returns the value of a child of this element specified by its name.
	 * If the childs's value is not a simple string this method
	 * returns a XML fragment. Note, this method ignores namespaces and namespace
	 * prefixes.
	 * @param childName the name of the child.
	 * @return The string value of the specified child, or null if noch such child
	 * exists.
	 */
	public String getChildValue(String childName) {
		Element child = getChild(childName);
		return (child != null) ? child.getValue() : null;
	}

	/**
	 * Returns the value of a child of this element specified by its name.
	 * If the childs's value is not a simple string this method
	 * returns a XML fragment. Note, this method checks if the child's namespace
	 * matches the specified namespace URI, but namespace prefixes are
	 * ignored.
	 * @param childName the name of the child.
	 * @param namespaceURI the namespace URI of the child.
	 * @return The string value of the specified child, or null if no such
	 * child exists.
	 */
	public String getChildValue(String childName, String namespaceURI) {
		Element child = getChild(childName, namespaceURI);
		return (child != null) ? child.getValue() : null;
	}

	/**
	 * Returns the children of this element as an iterator of
	 * Element objects.
	 * @return An enumeration of Element instances representing the children
	 * of this Element.
	 */
	public Iterator getChildren() {
		return new Iterator() {
			private Element child = firstChild;
			public boolean hasNext() {
				return (child != null);
			}
			public Object next() {
				if (child == null) {
					throw new NoSuchElementException();
				}
				Element result = child;
				child = child.next;
				return result;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns the values of all children of this element as an array of strings.
	 * If the value of a child is not a simple string this method returns a XML fragment.
	 * This method is useful to access list- or set-like properties, e.g.
	 * &lt;DAV:activity-version-set&gt; that consists of a list of &lt;DAV:href&gt;
	 * elements.
	 * @return A string array with the values of the children of this element. If
	 * the element has no children the array has length zero.
	 */
	public String[] getChildValues() {
		String[] values = null;
		Element child = firstChild;
		if (child != null) {
			values = new String[count];
			for (int i = 0; i < count; ++i) {
				values[i] = child.getValue();
				child = child.next;
			}
		}
		return values;
	}

	/**
	 * Removes all children of this element.
	 */
	public void removeChildren() {
		Element child = firstChild;
		Element next;
		while (child != null) {
			next = child.next;
			child.next = null;
			child = next;
			next = null;
		}
		firstChild = null;
		lastChild = null;
		value = null;
	}

	/**
	 * Converts the value of this element to a valid XML fragment 
	 * representing the inner structure of this element. If the element
	 * has no children, the simple value of the element is returned.
	 * Note, this method encodes the result according
	 * to XML encoding rules (i.e. the symbols '<', '>' and '&' are replaced
	 * by "&lt;", "&gt;" and "&amp" respectively). 
	 * This method calls getQualifiedName() on each child to determine
	 * the tag names of that child. 
	 * @return The string representation of this Element.
	 */
	public String toString() {
		if (firstChild == null) {
			return (value!=null)? Encoder.encodeXml((String)value) : null;
		}

		StringBuffer buf = new StringBuffer();
		Element child = firstChild;
		String tagName;
		while (child != null) {
			tagName = child.getQualifiedName();
			buf.append('<').append(tagName);
			String s = child.toString();
			if (s != null) {
				buf.append('>').append(s);
				buf.append("</").append(tagName).append('>');
			} else {
				buf.append("/>");
			}
			child = child.next;
		}
		return buf.toString();
	}

}
