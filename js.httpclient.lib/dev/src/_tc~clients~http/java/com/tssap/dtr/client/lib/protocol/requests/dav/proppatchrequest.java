package com.tssap.dtr.client.lib.protocol.requests.dav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.Element;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.*;
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This request class implements the DeltaV "PROPPATCH" request
 * that allows to set, change and remove properties of resources.
 */
public class ProppatchRequest extends MultiStatusRequest {

	/** The list of Item elements to be send in the proppatch request */
	private ArrayList properties;
	/** Table of namespaces used by this request */
	private HashMap namespaces;

	/** internal class to store the proppatch operations */
	private class Action {
		public boolean set;
		public String name;
		public Object value;
		public Action(boolean set, String name) {
			this.set = set;
			this.name = name;
		}		
		public Action(boolean set, String name, String value) {
			this.set = set;
			this.name = name;
			if (value != null) {
				this.value = Encoder.encodeXml(value);
			}
		}
		public Action(boolean set, String name, Element value) {
			this.set = set;
			this.name = name;
			this.value = value;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (value == null || value=="") {
				sb.append("<").append(name).append("/>");
			} else {				
				sb.append("<").append(name).append(">");
				sb.append(value.toString());
				sb.append("</").append(name).append(">");
			} 
			return sb.toString();
		}
	}

	/**
	 * Creates a PROPPATCH request for the specified resource.
	 * @param path  the path of a resource, e.g. a file, collection, activity,
	 * working resource etc.
	 */
	public ProppatchRequest(String path) {
		super("PROPPATCH", path);
	}

	/**
	 * Prepares the request object for reuse.
	 * Calls XMLRequest.clear(). Removes property and namespace definitions.
	 */
	public void clear() {
		super.clear();
		if (properties != null) {
			properties.clear();
		}
		if (namespaces != null) {
			namespaces.clear();
		}
	}

	/**
	 * Sets a new simple value of a property.
	 * @param propertyName   the name of the property to set including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param propertyValue  the new simple value of the property.
	 */
	public void addPropertySet(String propertyName, String propertyValue) {
		if (properties == null) {
			properties = new ArrayList();
		}
		properties.add(new Action(true, propertyName, propertyValue));
	}

	/**
	 * Sets a new simple value of a property.
	 * @param propertyName   the name of the property to set including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param propertyValue  the new simple value of the property.
	 * @param namespaceURI  the namespace the property belongs to (given as
	 * an URI).
	 */
	public void addPropertySet(String propertyName, String propertyValue, String namespaceURI) {
		int n = propertyName.indexOf(':');
		if (n > 0) {
			addNamespace(propertyName.substring(0, n), namespaceURI);
		}
		addPropertySet(propertyName, propertyValue);
	}

	/**
	 * Sets a new structured value of a property. The name and new value of
	 * the property to set are retrieved from the Element instance.
	 * @param property  an Element instance describing a property and
	 * the new value of that property.
	 *
	 */
	public void addPropertySet(Element property) {
		if (property.getNamespaceURI() != null) {
			addNamespace(property.getNamespacePrefix(), property.getNamespaceURI());
		}
		if (properties == null) {
			properties = new ArrayList();
		}		
		properties.add(new Action(true, property.getQualifiedName(), property));
	}
	

	/**
	 * Removes a property from the resource.
	 * @param propertyName   the name of the property to set including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 */
	public void addPropertyRemove(String propertyName) {
		if (properties == null) {
			properties = new ArrayList();
		}
		properties.add(new Action(false, propertyName));
	}

	/**
	 * Removes a property from the resource.
	 * @param propertyName   the name of the property to set including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param propertyValue  the new simple value of the property.
	 * @param namespaceURI  the namespace the property belongs to (given as
	 * an URI).
	 */
	public void addPropertyRemove(String propertyName, String namespaceURI) {
		int n = propertyName.indexOf(':');
		if (n > 0) {
			addNamespace(propertyName.substring(0, n), namespaceURI);
		}
		addPropertyRemove(propertyName);
	}

	/**
	 * Adds a namespaces to the request specified by a namespaces prefix
	 * and the URI that defines that namespace. The namespace is "DAV:"
	 * predefined and must not be set with this method.
	 * @param prefix  a namespace prefix.
	 * @param namespaceURI  the URI associated with the namespace.
	 */
	public void addNamespace(String prefix, String namespaceURI) {
		if (namespaces == null) {
			namespaces = new HashMap();
		}
		if (prefix.endsWith(":")) {
			namespaces.put(prefix.substring(0, prefix.length()-1), namespaceURI);			
		} else {
			namespaces.put(prefix, namespaceURI);
		}
	}

	/**
	 * Returns the number of resource elements stored in this entity.
	 * @return The number of resources.
	 */
	public int countResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.countResources() : 0;
	}

	/**
	 * Returns the resource specified by index. The resources are provided
	 * in the order they occured in the multistatus response.
	 * @return The resource element that corresponds to the i-th <DAV:response>
	 * entry in the multistatus response, or null if no resource was
	 * reveived or the index is out of bounds.
	 */
	public ResourceElement getResource(int i) {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.getResource(i) : null;
	}

	/**
	 * Returns an enumeration of ResourceElement objects that were retrieved
	 * from the multistatus response. Each element contains the URL of
	 * the corresponding resource, the set of retrieved properties and optionally
	 * a human readable description of the response state.
	 */
	public Iterator getResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null)? entity.getResources() : null;
	}
	

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this PROPPATCH request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<propertyupdate").append(DAV.DEFAULT_XMLNS);
		if (namespaces != null) {
			Iterator keys = namespaces.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				body.append(" xmlns:").append(key).append("=\"").append(
					namespaces.get(key)).append(
					"\"");
			}
		}
		body.append(">");

		if (properties != null && properties.size() > 0) {
			boolean tagOpen = false;
			boolean setTagOpen = false;
			for (int i = 0; i < properties.size(); ++i) {
				Action action = (Action) properties.get(i);
				if (action.set) {
					if (tagOpen && !setTagOpen) {
						body.append("</prop></remove>");
						tagOpen = false;
					}
					if (!tagOpen) {
						body.append("<set><prop>");
						tagOpen = true;
						setTagOpen = true;
					}
					body.append(action.toString());
				} else {
					if (tagOpen && setTagOpen) {
						body.append("</prop></set>");
						tagOpen = false;
					}
					if (!tagOpen) {
						body.append("<remove><prop>");
						tagOpen = true;
						setTagOpen = false;
					}
					body.append("<").append(action.name).append("/>");
				}
			}
			if (setTagOpen) {
				body.append("</prop></set>");
			} else {
				body.append("</prop></remove>");
			}
		}
		body.append("</propertyupdate>");
		setRequestEntity(body);
		return body;
	}
}
