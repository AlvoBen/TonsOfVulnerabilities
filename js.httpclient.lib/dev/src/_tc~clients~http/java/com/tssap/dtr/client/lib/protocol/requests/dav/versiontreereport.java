package com.tssap.dtr.client.lib.protocol.requests.dav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.*;

/**
 * This request class implements the DeltaV "Version Tree" report.
 */
public class VersionTreeReport extends MultiStatusRequest {

	/** The top level property definition */
	private ExpandPropertyDef top;
	
	/** The list of property names to be send in the profind request*/
	private ArrayList properties;
	
	/** Table of namespaces used by this request */
	private HashMap namespaces;

	/**
	 * Creates a Version Tree report for a given version or VCR. If <code>path</code>
	 * specifies a VCR the report is directed to the checked-in version of that
	 * VCR.
	 * @param path  the path of a version or VCR.
	 */
	public VersionTreeReport(String path) {
		super("REPORT", path);
		top = new ExpandPropertyDef("");		
	}

	/**
	 * Adds a single property element to the request. The property is reported
	 * for each version in the version tree.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 */
	public void addPropertyGet(String propertyName) {
		if (properties == null) {
			properties = new ArrayList();
		}
		properties.add(propertyName);
	}

	/**
	 * Adds a single property element to the request and specifies the namespace
	 * URI used by this element. The property is reported
	 * for each version in the version tree.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param namespaceURI  the namespace the property belongs to (given as
	 * an URI).
	 */
	public void addPropertyGet(String propertyName, String namespaceURI) {
		int n = propertyName.indexOf(':');
		if (n > 0) {
			addNamespace(propertyName.substring(0, n), namespaceURI);
		}
		addPropertyGet(propertyName);
	}

	/**
	 * Adds a set of properties to the request. The properties are reported
	 * for each version in the version tree.
	 * @param propertyNames  an array of property names with namespace
	 * prefixes (despite for properties that belong to the default namespace "DAV:").
	 */
	public void addPropertyGet(String[] propertyNames) {
		if (properties == null) {
			properties = new ArrayList();
		}
		for (int i = 0; i < propertyNames.length; ++i) {
			properties.add(propertyNames[i]);
		}
	}
	
	/**
	 * Adds another property tag with specified name attribute to the
	 * given parent tag. If parent is null the tag is append to the
	 * root tag.<p>
	 * Note: property expansion is a non-standard extension to the DeltaV protocol.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param parent  the parent tag to which this tag belongs.
	 * @return A PropertyDef object that represents the new tag. This reference
	 * can be used to append children to that tag.
	 */
	public ExpandPropertyDef addPropertyExpand(String propertyName, ExpandPropertyDef parent) {
		ExpandPropertyDef property = new ExpandPropertyDef(propertyName);
		addPropertyExpand(property, parent);
		return property;
	}	

	/**
	 * Adds another property tag with specified name attribute to the
	 * given parent tag. If parent is null the tag is append to the
	 * root tag.<p>
	 * Note: property expansion is a non-standard extension to the DeltaV protocol.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 * @param namespaceURI   the namespace the property belongs to (given as
	 * an URI).
	 * @param parent   the parent tag to which this tag belongs.
	 * @return A PropertyDef object that represents the new tag. This reference
	 * can be used to append children to that tag.
	 */
	public ExpandPropertyDef addPropertyExpand(String propertyName, String namespaceURI, ExpandPropertyDef parent) {
		ExpandPropertyDef property = new ExpandPropertyDef(propertyName, namespaceURI);
		addPropertyExpand(property, parent);
		return property;
	}

	/**
	 * Adds the specified property definition to the
	 * given parent tag. If parent is null the tag is append to the
	 * root tag.<p>
	 * Note: property expansion is a non-standard extension to the DeltaV protocol.
	 * @param property  the property to add.
	 * @param parent  the parent tag to which this tag belongs.
	 */
	public void addPropertyExpand(ExpandPropertyDef property, ExpandPropertyDef parent) {
		if (parent != null) {
			parent.addChild(property);
		} else {
			top.addChild(property);
		}
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
			namespaces.put(prefix.substring(0, prefix.length() - 1), namespaceURI);
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
		return (entity != null) ? entity.countResources() : 0;
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
		return (entity != null) ? entity.getResource(i) : null;
	}

	/**
	 * Returns an enumeration of ResourceElement objects that were retrieved
	 * from the multistatus response. Each element contains the URL of
	 * the corresponding resource, the set of retrieved properties and optionally
	 * a human readable description of the response state.
	 */
	public Iterator getResources() {
		MultiStatusEntity entity = MultiStatusEntity.valueOf(getResponse().getEntity());
		return (entity != null) ? entity.getResources() : null;
	}

	/**
	 * Prepares the request entity.
	 * This method is called during execution of this request. Do not call
	 * this method directly.
	 * @return A request entity for this report.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<version-tree").append(DAV.DEFAULT_XMLNS);
		if (namespaces != null) {
			Iterator keys = namespaces.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				body.append(" xmlns:").append(key).append("=\"").append(namespaces.get(key)).append("\"");
			}
		}

		if (properties != null || top.firstChild != null) {
			body.append(">");
			if (properties != null) {
				body.append("<prop>");
				for (int i = 0; i < properties.size(); ++i) {
					body.append("<").append(properties.get(i)).append("/>");
				}
				body.append("</prop>");
			}
			if (top.firstChild != null) {
				body.append("<expand-property>");
				for (ExpandPropertyDef child = top.firstChild; child != null; child = child.next) {
					appendChildren(body, child);
				}        
				body.append("</expand-property>");        
			}
			body.append("</version-tree>");
		} else {
			body.append("/>");
		}	

		
		setRequestEntity(body);
		return body;
	}

	/**
	 * Write the tag structure of the given property definition to the
	 * string entity.
	 */
	private void appendChildren(StringEntity body, ExpandPropertyDef property) {
		body.append("<property name=\"").append(property.name).append("\"");
		if (property.namespaceURI != null) {
			body.append(" namespace=\"").append(property.namespaceURI).append("\"");
		}

		ExpandPropertyDef child = property.firstChild;
		if (child == null) {
			body.append("/>");
		} else {
			body.append(">");
			while (child != null) {
				appendChildren(body, child);
				child = child.next;
			}
			body.append("</property>");
		}
	}

}
