package com.tssap.dtr.client.lib.protocol.requests.dav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.tssap.dtr.client.lib.protocol.DAV;
import com.tssap.dtr.client.lib.protocol.Header;
import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.entities.MultiStatusEntity;
import com.tssap.dtr.client.lib.protocol.entities.ResourceElement;
import com.tssap.dtr.client.lib.protocol.entities.StringEntity;
import com.tssap.dtr.client.lib.protocol.requests.*;

/**
 * This request class implements the DeltaV "PROPFIND" request
 * that allows to retrieve properties of resources.
 */
public class PropfindRequest extends MultiStatusRequest {

	/** The list of property names to be send in the profind request*/
	private ArrayList properties;
	/** Table of namespaces used by this request */
	private HashMap namespaces;
	/** Controls whether the request send a set of <prop> element,
	 *  <allprop> or <propname> */
	private PropfindOption option = PropfindOption.PROPERTIES;
	/** The depth header of this request. */
	private Depth depth = Depth.DEPTH_INFINITY;
	/** The label of a version to which this request should be applied.*/
	private String label;

	/**
	 * Creates a PROPFIND request for the specified resource.
	 * The default namespace for properties is set to "DAV:".
	 * @param path  the path of a resource, e.g. a file, collection, activity,
	 * working resource etc.
	 */
	public PropfindRequest(String path) {
		super("PROPFIND", path);
	}

	/**
	 * Creates a PROPFIND request for the specified resource.
	 * The default namespace for properties is set to "DAV:".
	 * @param path  the path of a resource, e.g. a file, collection, activity,
	 * working resource etc.
	 * @param depth  determines for collections if the request should be
	 * applied only to the collection itself (Depth.DEPTH_0), the internal
	 * members of the collection (Depth.DEPTH_1), or to any members of the
	 * collection hierarchy (Depth.INFINITY). Default is Depth.DEPTH_INFINITY.
	 * Adds a "Depth" header to the request.
	 */
	public PropfindRequest(String path, Depth depth) {
		this(path);
		setDepth(depth);
	}

	/**
	 * Creates a PROPFIND request for specified resource and properties.
	 * @param path  the path of a resource, e.g. a file, collection, activity,
	 * working resource etc.
	 * @param depth  determines for collections if the request should be
	 * applied only to the collection itself (Depth.DEPTH_0), the internal
	 * members of the collection (Depth.DEPTH_1), or to any members of the
	 * collection hierarchy (Depth.INFINITY). Default is Depth.DEPTH_INFINITY.
	 * Adds a "Depth" header to the request.
	 * @param propertyNames  an array of property names with namespace
	 * prefixes (despite for properties that belong to the default namespace "DAV:").
	 */
	public PropfindRequest(String path, Depth depth, String[] propertyNames) {
		this(path, depth);
		properties = new ArrayList();
		for (int i = 0; i < propertyNames.length; ++i) {
			properties.add(propertyNames[i]);
		}
	}

	/**
	 * Prepares the request object for reuse.
	 * Calls XMLRequest.clear(). Removes property and namespace definitions
	 * and sets the attributes option, depth and label to their default values.
	 */
	public void clear() {
		super.clear();
		if (properties != null) {
			properties.clear();
		}
		if (namespaces != null) {
			namespaces.clear();
		}
		option = PropfindOption.PROPERTIES;
		depth = Depth.DEPTH_INFINITY;
		label = null;
	}

	/**
	 * Sets the depth for this request.
	 * @param depth  determines for collections if the request should be
	 * applied only to the collection itself (Depth.DEPTH_0), the internal
	 * members of the collection (Depth.DEPTH_1), or to any members of the
	 * collection hierarchy (Depth.INFINITY).
	 * Default is Depth.DEPTH_INFINITY. Adds a "Depth" header to the request.
	 */
	public void setDepth(Depth depth) {
		if (depth.equals(Depth.DEPTH_0)) {
			setHeader(Header.DAV.DEPTH, "0");
			this.depth = Depth.DEPTH_0;
		} else if (depth.equals(Depth.DEPTH_1)) {
			setHeader(Header.DAV.DEPTH, "1");
			this.depth = Depth.DEPTH_1;
		} else if (depth.equals(Depth.DEPTH_INFINITY)) {
			setHeader(Header.DAV.DEPTH, "infinity");
			this.depth = Depth.DEPTH_INFINITY;
		}
	}

	/**
	 * Sets the mode of operation, i.e. whether the request should ask
	 * for certain properties, all properties or only the property names.
	 * If option is set to PropfindOption.ALL_PROPERTIES or
	 * PropfindOption.PROPERTY_NAMES any previously recorded property sets
	 * are removed.
	 * @param option  a value of the PropfindOption enumerator, i.e. either
	 * PROPERTIES, ALL_PROPERTIES or PROPERTY_NAMES;
	 */
	public void setOption(PropfindOption option) {
		this.option = option;
		if (option != PropfindOption.PROPERTIES && properties != null) {
			properties.clear();
			properties = null;
		}
	}

	/**
	 * Retrieves properties of a certain version of a resource matching
	 * the given label.
	 * @param label  a version label.
	 */
	public void setApplyToLabel(String label) {
		this.label = label;
		if (label != null) {
			setHeader(Header.DAV.LABEL, label);
		}
	}

	/**
	 * Adds a single property element to the request. Sets the option parameter
	 * to PropfindOption.PROPERTIES.
	 * @param propertyName   the name of the property to retrieve including a namespace
	 * prefix (despite the property belongs to the default namespace "DAV:").
	 */
	public void addPropertyGet(String propertyName) {
		if (properties == null) {
			properties = new ArrayList();
		}
		properties.add(propertyName);
		option = PropfindOption.PROPERTIES;
	}

	/**
	 * Adds a single property element to the request and specifies the namespace
	 * URI used by this element. Sets the option parameter
	 * to PropfindOption.PROPERTIES.
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
		option = PropfindOption.PROPERTIES;
	}

	/**
	 * Adds a set of properties to the request. Sets the option parameter
	 * to PropfindOption.PROPERTIES.
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
		option = PropfindOption.PROPERTIES;
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
	 * @return A request entity for this PROPFIND request.
	 */
	public IRequestEntity prepareRequestEntity() {
		StringEntity body = null;
		body = new StringEntity("text/xml", "UTF-8");
		body.append("<?xml version=\"1.0\" encoding=\"").append(ENCODING).append("\"?>");
		body.append("<propfind").append(DAV.DEFAULT_XMLNS);
		if (namespaces != null && namespaces.size() > 0) {
			Iterator keys = namespaces.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				body.append(" xmlns:").append(key).append("=\"").append(namespaces.get(key)).append("\"");
			}
		}
		body.append(">");
		if (option.equals(PropfindOption.ALL_PROPERTIES)) {
			body.append("<allprop/>");
		} else if (option.equals(PropfindOption.PROPERTY_NAMES)) {
			body.append("<propname/>");
		} else {
			if (properties != null && properties.size() > 0) {
				body.append("<prop>");
				for (int i = 0; i < properties.size(); ++i) {
					body.append("<").append(properties.get(i)).append("/>");
				}
				body.append("</prop>");
			}
		}
		body.append("</propfind>");
		setRequestEntity(body);
		return body;
	}

}
