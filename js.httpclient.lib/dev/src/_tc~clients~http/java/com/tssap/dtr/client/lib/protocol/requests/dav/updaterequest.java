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
import com.tssap.dtr.client.lib.protocol.util.Encoder;

/**
 * This request class implements the DeltaV "UPDATE" request.
 */
public class UpdateRequest extends MultiStatusRequest {

	/** The list of property names to be send in the profind request*/
	private ArrayList properties;
	/** Table of namespaces used by this request */
	private HashMap namespaces;
	/** The version (or maybe the label of a version) to update to*/
	private String updateSource;
	/** if true, interpret updateSource as label of a version*/
	private boolean applyToLabel = false;

	/**
	 * Creates a UPDATE request for the specified resource.
	 * @param path  the path of the checked-in VCR to update.
	 * @param updateSource  the path of a version the VCR should be updated to.
	 */
	public UpdateRequest(String path, String updateSource) {
		super("UPDATE", path);
		this.updateSource = updateSource;
	}

	/**
	 * Determines that the update source given in the constructor
	 * should be interpreted as label of a version.
	 */
	public void applyToLabel() {
		this.applyToLabel = true;
	}

	/**
	 * Adds a single property element to the request. The property is
	 * retrieved from the updated resource.
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
	 * URI used by this element. The property is retrieved from the updated resource.
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
	 * Adds a set of properties to the request. The properties are retrieved
	 * from the updated resource.
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
	 * @return A request entity for this UPDATE request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<update").append(DAV.DEFAULT_XMLNS);
		if (properties != null) {
			if (namespaces != null) {
				Iterator keys = namespaces.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					body.append(" xmlns:").append(key).append("=\"").append(namespaces.get(key)).append("\"");
				}
			}
			body.append(">");
		}
		String tagName = (applyToLabel) ? "label-name" : "version";
		body.append("<").append(tagName).append("><href>");
		body.append(Encoder.encodeXml(updateSource));
		body.append("</href></").append(tagName).append(">");
		if (properties != null) {
			body.append("<prop>");
			for (int i = 0; i < properties.size(); ++i) {
				body.append("<").append(properties.get(i)).append("/>");
			}
			body.append("</prop>");
		}
		body.append("</update>");
		setRequestEntity(body);
		return body;
	}

}
